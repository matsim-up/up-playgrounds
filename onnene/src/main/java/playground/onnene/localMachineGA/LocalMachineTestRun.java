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
import java.util.ArrayList;
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

/**
 * This class is used to run the simulation based transit optimisation problem
 * 
 * @author Onnene
 *
 */
public class LocalMachineTestRun {
	
	private static final Logger log = Logger.getLogger(LocalMachineTestRun.class);
    private static final int MAX_NFE = 12;   
	private static final int CHECKPOINT_FREQ = 3;
	private static final int POP_SIZE = 3;
	public static final int MATSIM_ITERATION_NUMBER = 10;
    private static FileOutputStream SEED_FILE, REFSET_TXT, REFSET_PF, MOEA_LOG;
    
    public static Path matsimOutput; 

    /**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {	
		Header.printHeader(LocalMachineTestRun.class, args);

		//int numThreads = Integer.parseInt(args[0]);
		long seedBase = Long.parseLong(args[0]);
		int numberOfRuns = Integer.parseInt(args[1]);

		runSimulation(seedBase, numberOfRuns);
		log.info("Finished!");
		Header.printFooter();
		
	}
	
	
	
	 /**
	  * This method is used to convert from solutions from JSON to XML
	  * 
	 * @param variable decision variable representation of the solution that is to be decoded
	 * @param resultFilePath root directory of the optimisation results
	 * @param algorithmNameFolder specific folder for the result of each specific algorithms
	 * @param folderNum number of the result folder
	 * @param fileNum number of the result file
	 * @throws IOException
	 */
	private void decodeResult(Variable variable, String resultFilePath,  int folderNum, int fileNum) throws IOException {
		
		
		if (variable instanceof LocalMachineDecisionVariable) {
			
			try {
			LocalMachineDecisionVariable varObj = (LocalMachineDecisionVariable) variable;         
			//Path algorithmFolder = Files.createDirectories(Paths.get(resultFilePath)); 				
			String resultFileName = "Solution" + fileNum + ".xml";                   
			String innerFolderStr = resultFilePath + File.separator +  folderNum + File.separator;             
			Path innerFolder = Files.createDirectories(Paths.get(innerFolderStr));               
			String paretoResultFolderPath = innerFolder + File.separator + resultFileName;
			ProblemUtils.getXMLFromJSONDecisionVar(varObj.getTransitSchedule(), paretoResultFolderPath);
			
			}
			
			catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Something is wrong here");
			}
			
		}else {
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
	
	
	/**
	 * This class runs the simulation based transit optimisation problem
	 * 
	 * @param seedBase the constant used to get each run seed - run seed = seedBase * run number
	 * @param numberOfRuns number of times the algorithm is run 
	 * @throws Exception
	 */
	private static void runSimulation(long seedBase, int numberOfRuns) throws Exception {
				
		setupOutput();
    	
    	Files.createDirectories(Paths.get("./input/output/logs/"));
		//Files.createDirectories(Paths.get("./input/output/optimisationResults/"));
//    	Files.createDirectories(Paths.get("./input/output/problemReferenceSet/"));
//    	Files.createDirectories(Paths.get("./input/output/checkpoint/"));
//    	Files.createDirectories(Paths.get("./input/output/indicator/"));
    	//Files.createDirectories(Paths.get("./input/output/matsimOutput/"));
    	
    	MOEA_LOG = new FileOutputStream(new File("./input/output/logs/run_moea_log.txt"));
		MOEA_LOG.write(String.format("Run\tPareto\tObjective1\tObjective2\n").getBytes());
		REFSET_TXT = new FileOutputStream(new File("./input/output/problemReferenceSet/refSet.txt"), true);
		REFSET_PF = new FileOutputStream(new File("./input/output/problemReferenceSet/refSet.pf"), true);
  		
		// Step 1 - Run the algorithm(s).  If running multiple algorithms, save to separate files.
		ProblemFactory.getInstance().addProvider(new LocalMachineGA_ProblemProvider());
		Problem problem = ProblemFactory.getInstance().getProblem("LocalMachineSimulationBasedTransitOptimisationProblem");
		
		
		TypedProperties properties = new TypedProperties();
		properties.setString("operator", "MyCrossover+MyMutation");
		properties.setDouble("MyCrossover.Rate", 0.75);
		properties.setDouble("MyMutation.Rate", 0.25);
		properties.setInt("populationSize", POP_SIZE);
		
		
		String[] algorithmNames = new String[] { "NSGAII"};
		List<File> outputFiles = new ArrayList<File>();
		
			
		
		for (int i = 0; i < algorithmNames.length; i++) {
			
			List<NondominatedPopulation> allResults = new ArrayList<>();
			List<Long> allSeeds = new ArrayList<>();
			String algorithmName = algorithmNames[i];
			log.info("Evaluating " + algorithmName + "...");
			
			Path algorithmOutputFolder = Files.createDirectories(Paths.get("./input/output" + File.separator + algorithmName + File.separator));
			Path checkPointFolder = Files.createDirectories(Paths.get(algorithmOutputFolder.toAbsolutePath() +  File.separator + "checkPoint" + File.separator));
			Path refsetFolder = Files.createDirectories(Paths.get(algorithmOutputFolder.toAbsolutePath() + File.separator +"referenceSet" + File.separator));
			
			matsimOutput = Files.createDirectories(Paths.get(algorithmOutputFolder.toString() + File.separator +"matsimOutput" + File.separator));
		
			OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
			Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, properties.getProperties(), problem);

//			File checkpointFile = new File("./input/output/checkpoint/" + "checkpoint_" + algorithmName + ".dat");
//			File outputFile = new File("./input/output/problemReferenceSet/" + algorithmName + "output_" + ".set");
			
			File checkpointFile = new File(checkPointFolder.toAbsolutePath() + File.separator + "checkpoint_" + algorithmName + ".dat");
			File outputFile = new File(refsetFolder.toAbsolutePath() + File.separator + "approximationset_" + algorithmName + ".set");
			
			if (checkpointFile.exists()) {
				log.info("Using checkpoint file for " + algorithmName + "!");
				
			}
			
			long seed = seedBase + PRNG.nextInt(POP_SIZE);
		
			//LocalMachineCheckpointAndOutputResult wrapper = new LocalMachineCheckpointAndOutputResult(algorithm, checkpointFile, outputFile, CHECKPOINT_FREQ);
			
			LocalMachineCheckpointAndOutputResult wrapper = new LocalMachineCheckpointAndOutputResult(algorithm, checkpointFile, outputFile, CHECKPOINT_FREQ);
			
			for(int run = 0; run < numberOfRuns; run++) {
				
				log.info("Running population " + run + " (using seed "+ seed + ")... ");	
				
				PRNG.setSeed((long)(seed*run));
			
				while (wrapper.getNumberOfEvaluations() < MAX_NFE) {
					
					wrapper.step();					

				}
				
				NondominatedPopulation finalResult = algorithm.getResult();
				allResults.add(finalResult);
				allSeeds.add(seed);
				outputFiles.add(outputFile);
				
			}
		
//			computeRefSet(problem, outputFiles);			
//			writeSeeds(allSeeds, algorithmName);			
//			processResults(allResults, algorithmName);	
			
			computeRefSet(problem, outputFiles, algorithmOutputFolder);			
			writeSeeds(allSeeds, algorithmOutputFolder);			
			processResults(allResults, algorithmOutputFolder);		
			
		}		

	}
	

	
	/**
	 * A method to write the seeds used in each run of the algorithm.
	 * Each algorithm is run n-times and each individual run is seeded.
	 * 
	 * @param seeds the seed for each run that is written to file
	 * @param algorithmSeedDir Directory where the seed file is stored
	 * @throws IOException
	 */
	private static void writeSeeds(List<Long> seeds, Path folder) throws IOException {
		
		/* Write all the seeds to file (for record). */
		
		
	
		//Path seedFolder = Files.createDirectories(Paths.get("./output/SBO_input/seeds/" + algorithmSeedDir + File.separator));
		
		
		//Files.createDirectories(Paths.get(seedFolder+));
		
		
		Path seedFolder = Files.createDirectories(Paths.get(folder + "./seeds/"));
		String seedFile = seedFolder + File.separator + "seed.txt";

		SEED_FILE = new FileOutputStream(new File(seedFile));
		SEED_FILE.write(String.format("Run\tSeed\n").getBytes());

		for (int seed = 0; seed < seeds.size(); seed++) {
			
			SEED_FILE.write(String.format("%d\t%d\n", seed+1, seeds.get(seed)).getBytes());
		}
	}
	
	
	
	/**
	 * Method to compute the metrics of the algorithm run and write them to file.
	 * 
	 * @param problem the optimisation problem to be solved
	 * @param outputFiles output files
	 * @throws Exception
	 */
	private static void computeRefSet(Problem problem, List<File> outputFiles, Path folder) throws Exception {
		
		// Step 1 - Compute the reference set.
		log.info("Computing reference set...");
		File referenceSetFile = new File(folder.normalize() + File.separator + "referenceSet/refset.ref");
		
		ResultFileMerger.main(
				ArrayUtils.addAll(
					new String[] {
						"--dimension", Integer.toString(problem.getNumberOfObjectives()),
						"--output", referenceSetFile.getAbsolutePath()
					},
					outputFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()).toArray(new String[0])));
		
		// Step 2 - Evaluate the metrics.
		for (File outputFile : outputFiles) {
			
			log.info("Calculating metrics for " + outputFile + "...");
		
			
//			File indicator = new File("metrics.set");
//			
//			if (outputFile.renameTo(indicator)) {
//				
//				LOG.info("Rename successful");
//				
//			} else {
//				
//				LOG.info("Rename unsuccessful");
//			}
			
			ResultFileEvaluator.main(new String[] {
					"--dimension", Integer.toString(problem.getNumberOfObjectives()),
					"--input", outputFile.getAbsolutePath(),
					"--output", new File(outputFile.getParentFile(), outputFile.getName().replace(".set", ".metrics")).getAbsolutePath(),
					"--reference", referenceSetFile.getAbsolutePath(),
					"--force"
			});
		}
	}
	
	/**
	 * This method to call @decodeResult() (convert from JSON to XML) on the 
	 * final optimisation results contained in the Pareto front of the 
	 * algorithm and write them to file.	 
	 * 
	 * @param allResults List of results obtained from runSimulation()
	 * @throws IOException
	 */
	private static void processResults(List<NondominatedPopulation> allResults, Path algorithmNameDirectory) throws IOException{
		
		//log.info("is " +algorithmNameDirectory.toAbsolutePath());
		
		String resultFolder =  algorithmNameDirectory.toAbsolutePath() +  File.separator + "optimisationResults" + File.separator;
		
		//File matsimOutputFolder = new File(resultFolder);
		
		//FileUtils.delete(matsimOutputFolder);
		
		if (Files.exists(Paths.get(resultFolder))){
			
			FileUtils.cleanDirectory(new File(resultFolder));
			
		}  else {
			
			Files.createDirectories(Paths.get(resultFolder));
		}
		
		
		LocalMachineTestRun tr = new LocalMachineTestRun();	
				
		int folderIdx = 0;
		
		for(int run = 0; run < allResults.size(); run++) {
			
			NondominatedPopulation runResult = allResults.get(run);

			folderIdx++;
			int fileIdx = 0;
			log.info("Size of Pareto front for run " + (run+1) + " is:" + " " + runResult.size());

			for(int solution = 0; solution < runResult.size(); solution++) {				
				Solution runSolution = runResult.get(solution);

				fileIdx++;
				tr.decodeResult(runSolution.getVariable(0), resultFolder, folderIdx, fileIdx);
				log.info(String.format("%.4f\t%.4f", runSolution.getObjective(0), runSolution.getObjective(1)));
				
				MOEA_LOG.write(String.format("%d\t%d\t%.4f\t%.4f\n", run+1, solution+1, runSolution.getObjective(0), runSolution.getObjective(1)).getBytes());
				REFSET_TXT.write(String.format("%d\t%d\t%.4f\t%.4f\n", run+1, solution+1, runSolution.getObjective(0), runSolution.getObjective(1)).getBytes());
				REFSET_PF.write(String.format("%.4f\t%.4f\n", runSolution.getObjective(0), runSolution.getObjective(1)).getBytes());
							
			}

		}
		
		//cleanMatsimFolder();		
		
	}
	


	/**
	 * Utility method to clean the MATSIM output folder
	 */
	private static void cleanMatsimFolder() {
		
		File matsimOutput = new File("./input/output/matsimOutput/");
		for(File file: matsimOutput.listFiles()) 
			if (file.isDirectory()) {
				for(File f: file.listFiles()) 					
						if (!f.getName().equals("ensembleRuns.txt")) 
							f.delete();
			}
	}
	
	

}
