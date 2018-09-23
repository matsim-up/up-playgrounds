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
package playground.onnene.ga;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.utils.Header;
import org.moeaframework.algorithm.Checkpoints;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.TypedProperties;

/**
 * @author Onnene
 *
 */
public class LocalMachineRunner {
	
	private static final Logger LOG = Logger.getLogger(LocalMachineRunner.class);
    private static final int MAX_NFE = 3;
    public static int callsToEvaluate = 0;
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
		LOG.info("Finished!");
		Header.printFooter();
		
	}
	
	 private void decodeResult(Variable variable, String resultFilePath, int folderNum, int fileNum) throws IOException {
			if (variable instanceof  DecisionVariable) {
				DecisionVariable varObj = (DecisionVariable) variable;               
				String resultFileName = "Solution" + fileNum + ".xml";                   
				String innerFolderStr = resultFilePath + folderNum + File.separator;             
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
			LOG.warn("The output folder exists and will be deleted.");
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
    
//    private String encode(Variable variable) throws IOException {
//		StringBuilder sb = new StringBuilder();
//		
//		if (variable instanceof  DecisionVariable) {
//			
//			DecisionVariable obi = (DecisionVariable) variable;
//			
//			sb.append(obi.getTransitSchedule());
//			
//		} 
//		
//		else {
//			
//			throw new IOException("type not supported");
//		}
//		
//		 //System.out.print(sb.length());
//		
//		return sb.toString();
//	}

//    public static void main(String[] args) throws IOException {
//    	
//    	
//		
//		Header.printHeader(InjectedPop.class, args);
//    	
//    }
	
	/**
	 * @param seed_base the seed_base used for each run of the algorithm
	 * @param numberOfRuns the number of times the algorithm is called
	 * @return 
	 * @throws Exception 
	 */   	
    public static List<NondominatedPopulation> runSimulation(long seedBase, int numberOfRuns) throws Exception {
    	
    	setupOutput();
    	
    	Files.createDirectories(Paths.get("./input/output/logs/"));
		Files.createDirectories(Paths.get("./input/output/optimisationResults/"));
    	Files.createDirectories(Paths.get("./input/output/problemReferenceSet/"));
    	Files.createDirectories(Paths.get("./input/output/checkpoint/"));
    	Files.createDirectories(Paths.get("./input/output/indicator/"));
    	Files.createDirectories(Paths.get("./input/output/matsimOutput/"));
    	
    	MOEA_LOG = new FileOutputStream(new File("./input/output/logs/run_moea_log.txt"));
		MOEA_LOG.write(String.format("Run\tPareto\tObjective1\tObjective2\n").getBytes());
		REFSET_TXT = new FileOutputStream(new File("./input/output/problemReferenceSet/refSet.txt"), true);
		REFSET_PF = new FileOutputStream(new File("./input/output/problemReferenceSet/refSet.pf"), true);
		SEED_FILE = new FileOutputStream(new File("./input/output/seed/seeds.txt"));
		SEED_FILE.write(String.format("Run\tSeed\n").getBytes());
//    	
//    	bwSeed.write("Run\tSeed\n");
//		bwMoea.write("Run\tPareto\tObj1\tOb2\n");
//		bwRef.write("Run\tPareto\tObj1\tOb2\n");
//    	
//    	new File("./input/output/logs/").mkdirs();
//		//new File("./input/output/matsimOutput/").mkdirs();
//		new File("./input/output/optimisationResults/").mkdirs();
//		new File("./input/output/problemReferenceSet/").mkdirs();
//		new File("./input/output/checkpoint/").mkdirs();
//		new File("./input/output/indicator/").mkdirs();	
//		
		//InjectedPop ijp = new InjectedPop();
		
		//final Logger LOG = Logger.getLogger(InjectedPop.class);
    	
    	List<NondominatedPopulation> allResults = new ArrayList<>();
		List<Long> lstOfSeeds = new ArrayList<>();
		
		
		//File matsimOutputFolder = new File(DirectoryConfig.MATSIM_OUTPUT_FOLDER);
		
		//FileUtils.delete(matsimOutputFolder);
		
		//FileUtils.cleanDirectory(matsimOutputFolder);
		
		//OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
		//Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", properties.getProperties(), problem);
	    		
		//Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		ProblemFactory.getInstance().addProvider(new LocalMachineGA_ProblemProvider());
		Problem problem = ProblemFactory.getInstance().getProblem("LocalMachineSimulationBasedTransitOptimisationProblem");
		
		//TypedProperties prop = new TypedProperties(new );
		
		//Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", new Properties(), problem);
		TypedProperties properties = new TypedProperties();
		properties.setString("operator", "MyCrossover+MyMutation");
		properties.setDouble("MyCrossover.Rate", 0.75);
		properties.setDouble("MyMutation.Rate", 0.25);
		properties.setInt("populationSize", 3);
		
		
		OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", properties.getProperties(), problem);
		
		//Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", new Properties(), problem);
		
		//NondominatedPopulation referenceSet = new NondominatedPopulation(PopulationIO.readObjectives(new File("./output/SBO_input/pf/DTLZ2.2D.pf")));
		NondominatedPopulation referenceSet = new NondominatedPopulation(PopulationIO.readObjectives(new File("./input/problemReferenceSet/problemRefSet.txt")));
		QualityIndicator qi = new QualityIndicator(problem, referenceSet);
		
		File checkpointFile = new File("./input/output/checkpoint/checkpointFile.dat");
		File indicators = new File("./input/output/indicator/indicatorFile.txt");
		//FOS = new FileOutputStream(new File("./output/SBO_input/injectedOutput.txt"));
		//File = seed new File("./output/SBO_input/injectedOutput.txt");
		
		if (checkpointFile.exists()) { 
			LOG.info("Using checkpoint file!");
		}
		
		/* Run the Algorithm. */
		for(int run = 0; run < numberOfRuns; run++) {
			long seed = seedBase+((long)run);
			
			LOG.info("Running population " + run + " (using seed "+ seedBase + ")... ");
		
		//algorithm.getResult();
		PRNG.setSeed(seed);
		Checkpoints checkpoint = new Checkpoints(algorithm, checkpointFile, 3);
		//try (FileWriter writer = new FileWriter(outputFile, true)) {
		
		
		try (FileOutputStream fos = new FileOutputStream(indicators, true)) {
			
			FileInputStream fis = new FileInputStream(indicators);
			
			int b = fis.read();
			
			if (b == -1) {
			
				fos.write(String.format("NFE,HV,GD,AEI,IGD,S,MPE\n").getBytes());
			
			} else {	
				
			}
			
			fis.close();
						
			while (checkpoint.getNumberOfEvaluations() < MAX_NFE) {
				checkpoint.step();
				//algorithm.step();
				qi.calculate(checkpoint.getResult());
	
				//qi.calculate(algorithm.getResult());
				
				fos.write(String.format(checkpoint.getNumberOfEvaluations() + ","
						+ qi.getHypervolume() + ","
						+ qi.getGenerationalDistance() + ","
						+ qi.getAdditiveEpsilonIndicator() + ","
						+ qi.getInvertedGenerationalDistance() + ","
						+ qi.getSpacing() + ","
						+ qi.getMaximumParetoFrontError() + ","					
						+ System.lineSeparator()).getBytes());
				}
		}
		
		NondominatedPopulation finalResult = algorithm.getResult();
		
		allResults.add(finalResult);
		lstOfSeeds.add(seed);
		
		}
		
		/* Write all the seeds to file (for record). */
		
		for (int i = 0; i < lstOfSeeds.size(); i++) {
			SEED_FILE.write(String.format("%d\t%d\n", i+1, lstOfSeeds.get(i)).getBytes());
		}
		
		
		return allResults;
		
    }
		
		/**
		 * @param allResults List of results obtained from runSimulation()
		 * @throws IOException
		 */
		public static void processResults(List<NondominatedPopulation> allResults) throws IOException{
		
		String ResultFolder = "./input/output/optimisationResults/";
		
		/* Process the results after running the algorithm. */
		LocalMachineRunner lmr = new LocalMachineRunner();

		//LocalMachineRunSimulationBasedTransitOptimisationProblem lmrsbtop = new LocalMachineRunSimulationBasedTransitOptimisationProblem();
		
//		MOEA_LOG = new FileOutputStream(new File("./input/output/logs/run_moea_log.txt"));
//		MOEA_LOG.write(String.format("Run\tPareto\tObjective1\tObjective2\n").getBytes());
//		REFSET_TXT = new FileOutputStream(new File("./input/output/logs/refSet.txt"));
//		REFSET_PF = new FileOutputStream(new File("./input/output/logs/refSet.pf"));
		
		int folderIdx = 0;
		FileUtils.deleteDirectory(new File(ResultFolder));

		for(int run = 0; run < allResults.size(); run++) {
			NondominatedPopulation runResult = allResults.get(run);

			folderIdx++;
			int fileIdx = 0;
			LOG.info("Size of Pareto front for run " + (run+1) + " is:" + " " + runResult.size());

			for(int solution = 0; solution < runResult.size(); solution++) {
				Solution runSolution = runResult.get(solution);

				fileIdx++;
				//rsbtop.decodeResult(solution.getVariable(0), DirectoryConfig.RESULTS_FILE, folderIdx, fileIdx);
				lmr.decodeResult(runSolution.getVariable(0), ResultFolder, folderIdx, fileIdx);
				//MOEA_LOG_FILE  = new FileOutputStream(new File("./output/logs/run_moea_log.txt"), true);
				LOG.info(String.format("%.4f\t%.4f", runSolution.getObjective(0), runSolution.getObjective(1)));
				
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
