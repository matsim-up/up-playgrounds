/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2018 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
  
/**
 * 
 */
package playground.onnene.localMachineGA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.utils.Header;
import org.moeaframework.analysis.sensitivity.ResultFileEvaluator;
import org.moeaframework.analysis.sensitivity.ResultFileMerger;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.TypedProperties;

import playground.onnene.ga.DecisionVariable;
import playground.onnene.ga.GA_OperatorProvider;
import playground.onnene.ga.ProblemUtils;

/**
 * @author Onnene
 *
 */
public class LocalMachineProblemRunner {
	
	private static final Logger log = Logger.getLogger(LocalMachineProblemRunner.class);
    private static final int MAX_NFE = 3;
    public static int callsToEvaluate = 0;   
	private static final int CHECKPOINT_FREQ = 3;
	public static final int MATSIM_ITERATION_NUMBER = 10; // FIXME was 10
    //private static final int MAX_NFE = 30;
    //private static BufferedWriter SEED_FILE;
    private static FileOutputStream SEED_FILE, REFSET_TXT, REFSET_PF, MOEA_LOG;
    static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");   
    static Date startTime = new Date();
    
    
    /**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {	
		Header.printHeader(LocalMachineRunner.class, args);

		//int numThreads = Integer.parseInt(args[0]);
		long seed_base = Long.parseLong(args[0]);
		int numberOfRuns = Integer.parseInt(args[1]);

		processResults(runSimulation(seed_base, numberOfRuns));
		log.info("Finished!");
		Header.printFooter();
		
	}
	
	
	 private void decodeResult(Variable variable, String resultFilePath, String algorithmNameFolder,  int folderNum, int fileNum) throws IOException {
			if (variable instanceof  DecisionVariable) {
				DecisionVariable varObj = (DecisionVariable) variable;         
				//Path algorithmFolder = Files.createDirectories(Paths.get(algorithmNameFolder)); 				
				String resultFileName = "Solution" + fileNum + ".xml";                   
				String innerFolderStr = resultFilePath + algorithmNameFolder +  folderNum + File.separator;             
				Path innerFolder = Files.createDirectories(Paths.get(innerFolderStr));               
				String paretoResultFolderPath = innerFolder + File.separator + resultFileName;
				ProblemUtils.getXMLFromJSONDecisionVar(varObj.getTransitSchedule(), paretoResultFolderPath);
			}
			else {
				throw new IOException("type not supported");
			}        
		}
	
	/**
	 * Creates all the necessary output folders and sets up the log files.
	 */
	private static void setupOutput() {
		File outputFolder = new File("./output/");
		if(outputFolder.exists()) {
			log.warn("The output folder exists and will be deleted.");
			try {
				FileUtils.deleteDirectory(outputFolder);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot delete the old output folder.");
			}
		}
		
		/* Set up output folders. */
		new File("./output/logs/").mkdirs();
		new File("./output/matsimOutput/").mkdirs();
		new File("./output/optimisationResults/").mkdirs();
		new File("./output/problemReferenceSet/").mkdirs();

		BufferedWriter bwSeed = IOUtils.getBufferedWriter("./output/logs/seeds.txt");
		BufferedWriter bwMoea = IOUtils.getBufferedWriter("./output/logs/run_moea_log.txt");
		BufferedWriter bwRef = IOUtils.getBufferedWriter("./output/problemReferenceSet/referenceSet.txt");
		
		try {
			bwSeed.write("Run\tSeed\n");
			bwMoea.write("Run\tPareto\tObj1\tOb2\n");
			bwRef.write("Run\tPareto\tObj1\tOb2\n");
			/* No header to the PF format file. */
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot write to seed file");
		} finally {
			try {
				bwSeed.close();
				bwMoea.close();
				bwRef.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close seed file");
			}
		}
		
	}
	
	
	public static List<List<NondominatedPopulation>> runSimulation(long seedBase, int numberOfRuns) throws Exception {
		
		
		setupOutput();
    	
    	Files.createDirectories(Paths.get("./input/output/logs/"));
		//Files.createDirectories(Paths.get("./input/output/optimisationResults/"));
    	Files.createDirectories(Paths.get("./input/output/problemReferenceSet/"));
    	Files.createDirectories(Paths.get("./input/output/checkpoint/"));
    	Files.createDirectories(Paths.get("./input/output/indicator/"));
    	//Files.createDirectories(Paths.get("./input/output/matsimOutput/"));
    	
    	MOEA_LOG = new FileOutputStream(new File("./input/output/logs/run_moea_log.txt"));
		MOEA_LOG.write(String.format("Run\tPareto\tObjective1\tObjective2\n").getBytes());
		REFSET_TXT = new FileOutputStream(new File("./input/output/problemReferenceSet/refSet.txt"), true);
		REFSET_PF = new FileOutputStream(new File("./input/output/problemReferenceSet/refSet.pf"), true);
//		SEED_FILE = new FileOutputStream(new File("./input/output/seed/seeds.txt"));
//		SEED_FILE.write(String.format("Run\tSeed\n").getBytes());
		

    	List<List<NondominatedPopulation>> allResultsLst = new ArrayList<>();   	
    	List<NondominatedPopulation> allResults = new ArrayList<>();
   
    	List<List<Long>> allSeedsLst = new ArrayList<>();
		List<Long> allSeeds = new ArrayList<>();
		
		// Step 1 - Run the algorithm(s).  If running multiple algorithms, save to separate files.
		//Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		ProblemFactory.getInstance().addProvider(new LocalMachineGA_ProblemProvider());
		Problem problem = ProblemFactory.getInstance().getProblem("LocalMachineSimulationBasedTransitOptimisationProblem");
//		
		
		TypedProperties properties = new TypedProperties();
		properties.setString("operator", "MyCrossover+MyMutation");
		properties.setDouble("MyCrossover.Rate", 0.75);
		properties.setDouble("MyMutation.Rate", 0.25);
		properties.setInt("populationSize", 3);
		
		
		String[] algorithmNames = new String[] { "NSGAII", "NSGAIII" };
		//String[] algorithmNames = new String[] { "NSGAII", GDE3 };
		List<File> outputFiles = new ArrayList<File>();
		//List<List<File>> outputFilesLst = new ArrayList<>();
		
		
		for (int i = 0; i < algorithmNames.length; i++) {
			
			String algorithmName = algorithmNames[i];
			
			System.out.println("Evaluating " + algorithmName + "...");
			
			OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
			Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, properties.getProperties(), problem);

			//File checkpointFile = new File("./output/SBO_input/" + "checkpoint_" + algorithmName + ".chkpt");
			File checkpointFile = new File("./input/output/checkpoint/" + "checkpoint_" + algorithmName + ".dat");
			//File outputFile = new File("./output/SBO_input/" + "output_" + algorithmName + ".set"); TODO
			File outputFile = new File("./input/output/problemReferenceSet/" + algorithmName + "output_" + ".set");
			//File outputFile = new File("./input/output/problemReferenceSet/" + algorithmName + "output_" + i + ".set");
			
			//File checkpointFile = new File("./output/SBO_input/" + "checkpoint_" + algorithmName + ".dat");
			
			
			if (checkpointFile.exists()) {
				log.info("Using checkpoint file for " + algorithmName + "!");
			}
			
			//TODO - Do not delete this but get back to it when you have a standard reference set
			/*File indicators = new File("./output/.../" + "indicator_" + algorithmName + ".txt");
			    LocalMachineCheckpointAndOutputResult wrapper = new LocalMachineCheckpointAndOutputResult(algorithm, checkpointFile, indicators, 100);*/
			
			//LocalMachineCheckpointAndOutputResult wrapper = new LocalMachineCheckpointAndOutputResult(algorithm, checkpointFile, outputFile, 100); TODO
			LocalMachineCheckpointAndOutputResult wrapper = new LocalMachineCheckpointAndOutputResult(algorithm, checkpointFile, outputFile, CHECKPOINT_FREQ);
			
			for(int run = 0; run < numberOfRuns; run++) {
				
				long seed = seedBase+((long)run);
				
				log.info("Running population " + run + " (using seed "+ seedBase + ")... ");
		
				PRNG.setSeed(seed);
				
				
				//TODO - Do not delete this but get back to it when you have a standard reference set
				
				/*Checkpoints checkpoint = new Checkpoints(algorithm, checkpointFile, 3);
								
				QualityIndicator qi = new QualityIndicator(problem, referenceSet);
				
				try (FileOutputStream fos = new FileOutputStream(indicators, true)) {
					
					FileInputStream fis = new FileInputStream(indicators);
					
					int b = fis.read();
					
					if (b == -1) {
					
						fos.write(String.format("NFE,HV,GD,AEI,IGD,S,MPE\n").getBytes());
					
					} else {	
						
					}
					
					fis.close(); */
				
				while (wrapper.getNumberOfEvaluations() < MAX_NFE) {
					
					wrapper.step();					
					
					//TODO - Get after obtaining standard reference set
					
					/*checkpoint.step(); 
					fos.write(String.format(checkpoint.getNumberOfEvaluations() + ","
							+ qi.getHypervolume() + ","
							+ qi.getGenerationalDistance() + ","
							+ qi.getAdditiveEpsilonIndicator() + ","
							+ qi.getInvertedGenerationalDistance() + ","
							+ qi.getSpacing() + ","
							+ qi.getMaximumParetoFrontError() + ","					
							+ System.lineSeparator()).getBytes());*/
				}
				
			//TODO}
				
				NondominatedPopulation finalResult = algorithm.getResult();
				
				allResults.add(finalResult);
				allSeeds.add(seed);
					
				outputFiles.add(outputFile);
				
			}
			allResultsLst.add(allResults);		
			allSeedsLst.add(allSeeds);
			//outputFilesLst.add(outputFiles);
		}
		
		//computeRefSet(problem, outputFilesLst);
		computeRefSet(problem, outputFiles);
		
		/* Write all the seeds to file (for record). */
		for (int i = 0; i < allSeedsLst.size(); i++) {
			
			String seedFile = "./input/output/seed/seeds_" + i + ".txt";
			
			SEED_FILE = new FileOutputStream(new File(seedFile));
			SEED_FILE.write(String.format("Run\tSeed\n").getBytes());
			
			List<Long> currentSeedLst = allSeedsLst.get(i);
			
			for (int j = 0; j < currentSeedLst.size(); j++) {
				
				SEED_FILE.write(String.format("%d\t%d\n", j+1, currentSeedLst.get(j)).getBytes());
			}
		}
		
		return allResultsLst;
		
	}
	
	//public static void computeRefSet(Problem problem, List<List<File>> outputFilesLst) throws Exception {
	public static void computeRefSet(Problem problem, List<File> outputFiles) throws Exception {
			
			//for (int i = 0; i < outputFilesLst.size(); i++) {
				
				//List<List<File>> outputFiles = outputFilesLst.get(i);
				//List<File> outputFiles = outputFilesLst.get(i);
	
				// Step 2 - Compute the reference set.
				log.info("Computing reference set...");
				File referenceSetFile = new File("./input/output/problemReferenceSet/output_refset.ref");
				//File referenceSetFileTxt = new File("./input/output/problemReferenceSet/output_refset.txt");
				
				ResultFileMerger.main(
						ArrayUtils.addAll(
							new String[] {
								"--dimension", Integer.toString(problem.getNumberOfObjectives()),
								"--output", referenceSetFile.getAbsolutePath()
							},
							outputFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()).toArray(new String[0])));
				
				// Step 3 - Evaluate the metrics.
				for (File outputFile : outputFiles) {
					log.info("Calculating metrics for " + outputFile + "...");
					
					ResultFileEvaluator.main(new String[] {
							"--dimension", Integer.toString(problem.getNumberOfObjectives()),
							"--input", outputFile.getAbsolutePath(),
							"--output", new File(outputFile.getParentFile(), outputFile.getName().replace(".set", ".metrics")).getAbsolutePath(),
							"--reference", referenceSetFile.getAbsolutePath(),
							"--force"
					});
				}
			
		//}
	
	}
		
		
	/**
	 * @param allResults List of results obtained from runSimulation()
	 * @throws IOException
	 */
	public static void processResults(List<List<NondominatedPopulation>> allResultsLst) throws IOException{
			
			String resultFolder = "./input/output/optimisationResults/";
			LocalMachineProblemRunner lmpr = new LocalMachineProblemRunner();
			
			//int AlgorithmIdx = 0;			
			
			//String algorithmResultFolder = "algorithm_";
			
			for (int resultNo = 0; resultNo < allResultsLst.size(); resultNo++) {
				
				String algorithmResultFolder = "algorithm_" + resultNo + File.separator;
				
				
				List<NondominatedPopulation> allResults = allResultsLst.get(resultNo);
					
					/* Process the results after running the algorithm. */
					
			
					//LocalMachineRunSimulationBasedTransitOptimisationProblem lmrsbtop = new LocalMachineRunSimulationBasedTransitOptimisationProblem();
					
			//		MOEA_LOG = new FileOutputStream(new File("./input/output/logs/run_moea_log.txt"));
			//		MOEA_LOG.write(String.format("Run\tPareto\tObjective1\tObjective2\n").getBytes());
			//		REFSET_TXT = new FileOutputStream(new File("./input/output/logs/refSet.txt"));
			//		REFSET_PF = new FileOutputStream(new File("./input/output/logs/refSet.pf"));
					
					
					int folderIdx = 0;
					//FileUtils.deleteDirectory(new File(resultFolder));
					//FileUtils.cleanDirectory(new File(resultFolder));
				
					//List<NondominatedPopulation> runResult = allResults;
					for(int run = 0; run < allResults.size(); run++) {
						
						NondominatedPopulation runResult = allResults.get(run);
			
						folderIdx++;
						int fileIdx = 0;
						log.info("Size of Pareto front for run " + (run+1) + " is:" + " " + runResult.size());
			
						for(int solution = 0; solution < runResult.size(); solution++) {
							
							Solution runSolution = runResult.get(solution);
			
							fileIdx++;
							//rsbtop.decodeResult(solution.getVariable(0), DirectoryConfig.RESULTS_FILE, folderIdx, fileIdx);
							lmpr.decodeResult(runSolution.getVariable(0), resultFolder, algorithmResultFolder, folderIdx, fileIdx);
							//MOEA_LOG_FILE  = new FileOutputStream(new File("./output/logs/run_moea_log.txt"), true);
							log.info(String.format("%.4f\t%.4f", runSolution.getObjective(0), runSolution.getObjective(1)));
							
							MOEA_LOG.write(String.format("%d\t%d\t%.4f\t%.4f\n", run+1, solution+1, runSolution.getObjective(0), runSolution.getObjective(1)).getBytes());
							REFSET_TXT.write(String.format("%d\t%d\t%.4f\t%.4f\n", run+1, solution+1, runSolution.getObjective(0), runSolution.getObjective(1)).getBytes());
							REFSET_PF.write(String.format("%.4f\t%.4f\n", runSolution.getObjective(0), runSolution.getObjective(1)).getBytes());
										
						}
			
					}
					
					File matsimOutput = new File("./input/output/matsimOutput/");
					for(File file: matsimOutput.listFiles()) 
						if (file.isDirectory()) {
							for(File f: file.listFiles()) 					
									if (!f.getName().equals("ensembleRuns.txt")) 
										f.delete();
						}		
			    }
		
	}
		
	
	
	
//	public static void main (String[] args) throws Exception {
//		
//		// Step 1 - Run the algorithm(s).  If running multiple algorithms, save to separate files.
//		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
////		ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
////		Problem problem = ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimisationProblem");
////		
//		
//		TypedProperties properties = new TypedProperties();
//		properties.setInt("populationSize", 10);
////		properties.setString("operator", "MyCrossover+MyMutation");
////		properties.setDouble("MyCrossover.Rate", 0.75);
////		properties.setDouble("MyMutation.Rate", 0.25);
//		//properties.setInt("populationSize", 3);
//		
//		int maxNFE = 1000000;
//		String[] algorithmNames = new String[] { "GDE3" };
//		//String[] algorithmNames = new String[] { "NSGAII", GDE3 };
//		List<File> outputFiles = new ArrayList<File>();
//		
//		for (String algorithmName : algorithmNames) {
//			System.out.println("Evaluating " + algorithmName + "...");
//			
//			//OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
//			Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", properties.getProperties(), problem);
//
//			//File checkpointFile = new File("./output/SBO_input/" + "checkpoint_" + algorithmName + ".chkpt");
//			File checkpointFile = new File("./output/SBO_input/" + "checkpoint_" + algorithmName + ".dat");
//			File outputFile = new File("./output/SBO_input/" + "output_" + algorithmName + ".set");
//			
//			if (checkpointFile.exists()) {
//				System.out.println("Using checkpoint file for " + algorithmName + "!");
//			}
//			
//			LocalMachineCheckpointAndOutputResult wrapper = new LocalMachineCheckpointAndOutputResult(algorithm, checkpointFile, outputFile, 100);
//	
//			while (wrapper.getNumberOfEvaluations() < maxNFE) {
//				wrapper.step();
//			}
//			
//			
//			
//			outputFiles.add(outputFile);
//		}
//		
//		// Step 2 - Compute the reference set.
//		System.out.println("Computing reference set...");
//		File referenceSetFile = new File("./output/SBO_input/output_refset.ref");
//		File referenceSetFileTxt = new File("./output/SBO_input/output_refset.txt");
//		
//		ResultFileMerger.main(
//				ArrayUtils.addAll(
//					new String[] {
//						"--dimension", Integer.toString(problem.getNumberOfObjectives()),
//						"--output", referenceSetFile.getAbsolutePath()
//					},
//					outputFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()).toArray(new String[0])));
//		
//		// Step 3 - Evaluate the metrics.
//		for (File outputFile : outputFiles) {
//			System.out.println("Calculating metrics for " + outputFile + "...");
//			
//			ResultFileEvaluator.main(new String[] {
//					"--dimension", Integer.toString(problem.getNumberOfObjectives()),
//					"--input", outputFile.getAbsolutePath(),
//					"--output", new File(outputFile.getParentFile(), outputFile.getName().replace(".set", ".metrics")).getAbsolutePath(),
//					"--reference", referenceSetFile.getAbsolutePath(),
//					"--force"
//			});
//		}
//
//		System.out.println("Finished!");
//		
//	}
	
	
	

}
