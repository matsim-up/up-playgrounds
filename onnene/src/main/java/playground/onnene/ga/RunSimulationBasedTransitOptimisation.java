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
import org.apache.log4j.PropertyConfigurator;
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
 * @author Onnene
 *
 */
public class RunSimulationBasedTransitOptimisation {
	
	private static final Logger log = Logger.getLogger(RunSimulationBasedTransitOptimisation.class);
	
    private static final int MAX_NFE = 3500;   
	private static final int POP_SIZE = 70;
	private static final int CHECKPOINT_FREQ = POP_SIZE;
	public static final int MATSIM_ITERATION_NUMBER = 10;
    private static FileOutputStream SEED_FILE, REFSET_TXT, REFSET_PF, MOEA_LOG;
    public static Path matsimOutput; 

    /**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {	
		
		Header.printHeader(RunSimulationBasedTransitOptimisation.class, args);
		
		/*Check memory consumption*/
		log.info(String.format("Memory Usage: %s ", Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
		
		/*Redirect console output to log file with log4j and for windows*/
		if (System.getProperty("os.name").startsWith("Windows")){
            
            PropertyConfigurator.configure("log4j.properties");
           
            
		}

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
	private void decodeResult(Variable variable, String resultFilePath,  int folderNum, int fileNum) throws IOException  {
			
			if (variable instanceof  DecisionVariable) {
				
				DecisionVariable varObj = (DecisionVariable) variable;         
				//Path algorithmFolder = Files.createDirectories(Paths.get(resultFilePath)); 				
				String resultFileName = "Solution" + fileNum + ".xml";                   
				String innerFolderStr = resultFilePath + File.separator +  folderNum + File.separator;             
				Path innerFolder = Files.createDirectories(Paths.get(innerFolderStr));			
				String paretoResultFolderPath = innerFolder + File.separator + resultFileName;
				
				ProblemUtils.getXMLFromJSONDecisionVar(varObj.getTransitSchedule(), paretoResultFolderPath);

			}
			else {
				
				throw new IOException("Type not supported");
				
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
		//new File("./output/logs/").mkdirs();
		new File("./output/matsimOutput/").mkdirs();
		//new File("./output/optimisationResults/").mkdirs();
		//new File("./output/problemReferenceSet/").mkdirs();

//		BufferedWriter bwSeed = IOUtils.getBufferedWriter("./output/logs/seeds.txt");
//		BufferedWriter bwMoea = IOUtils.getBufferedWriter("./output/logs/run_moea_log.txt");
//		BufferedWriter bwRef = IOUtils.getBufferedWriter("./output/problemReferenceSet/referenceSet.txt");
//		
//		try {
//			bwSeed.write("Run\tSeed\n");
//			bwMoea.write("Run\tPareto\tObj1\tOb2\n");
//			bwRef.write("Run\tPareto\tObj1\tOb2\n");
//			/* No header to the PF format file. */
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Cannot write to seed file");
//		} finally {
//			try {
//				bwSeed.close();
//				bwMoea.close();
//				bwRef.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//				throw new RuntimeException("Cannot close seed file");
//			}
//		}
		
		//int excessFiles = matsimOutput.getNameCount() % POP_SIZE;
		
		
	}


	/**
	 * Method to delete extra files ensembleRuns file when a run of the algorithm stops 
	 * without completely evaluating a population.
	 */
	private static void deleteExcessEnsembleRunsFiles() {
		File[] ensembleFiles = new File(matsimOutput.toString()).listFiles();
		ProblemUtils.reverseSortArrayOfFiles(ensembleFiles);
		
		int excessFiles = ensembleFiles.length % POP_SIZE;
		
		for(int i = 0; i < excessFiles; i++) {
			
			ensembleFiles[i].delete();
			
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
	
		log.info(String.format("Memory Usage: %s ", Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
		
    	
    	Files.createDirectories(Paths.get("./input/output/logs/"));
    	
    	MOEA_LOG = new FileOutputStream(new File("./input/output/logs/run_moea_log.txt"));
		MOEA_LOG.write(String.format("Run\tPareto\tObjective1\tObjective2\n").getBytes());
		REFSET_TXT = new FileOutputStream(new File("./input/output/logs/obtainedRefSet.txt"), true);
		REFSET_PF = new FileOutputStream(new File("./input/output/logs/obtainedRefSet.pf"), true);
  		
		// Step 1 - Run the algorithm(s).  If running multiple algorithms, save to separate files.
		ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
		Problem problem = ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimisationProblem");
		
		
		TypedProperties properties = new TypedProperties();
		properties.setString("operator", "MyCrossover+MyMutation");
		properties.setDouble("MyCrossover.Rate", 0.75);
		properties.setDouble("MyMutation.Rate", 0.25);
		//properties.setDoubleArray("weights", new double[] {0.2, 0.8});
		properties.setInt("populationSize", POP_SIZE);
	
		//String[] algorithmNames = new String[] {"NSGA-II","NSGA-III"}; 
		String[] algorithmNames = new String[] {"SPEA2", "DBEA"};
		//String[] algorithmNames = new String[] {"IBEA"};
		//String[] algorithmNames = new String[] {"GA"}
		
		List<File> outputFiles = new ArrayList<File>();
		
		for(int run = 0; run < numberOfRuns; run++) {
				
				for (int i = 0; i < algorithmNames.length; i++) {
					
					List<NondominatedPopulation> allResults = new ArrayList<>();
					List<NondominatedPopulation> currentResults = new ArrayList<>();
					List<Long> allSeeds = new ArrayList<>();
					
					String algorithmName = algorithmNames[i];
					log.info("Evaluating " + algorithmName + "...");
					
					Path algorithmOutputFolder = Files.createDirectories(Paths.get("./input/output" + File.separator + algorithmName + File.separator));
					Path algorithmSeedFolder = Files.createDirectories(Paths.get(algorithmOutputFolder + File.separator + "seed_"+run));
					Path checkPointFolder = Files.createDirectories(Paths.get(algorithmSeedFolder.toString() +  File.separator + "checkPoint" + File.separator));
					Path refsetFolder = Files.createDirectories(Paths.get(algorithmSeedFolder.toString() + File.separator +"referenceSet" + File.separator));
					
					matsimOutput = Files.createDirectories(Paths.get(algorithmSeedFolder.toString() + File.separator +"matsimOutput" + File.separator));
					
					
					OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
					Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, properties.getProperties(), problem);

					File checkpointFile = new File(checkPointFolder.toAbsolutePath() + File.separator + "checkpoint_" + algorithmName + ".dat");
					File outputFile = new File(refsetFolder.toAbsolutePath() + File.separator + "approximationset_" + algorithmName + ".set");
					
					if (checkpointFile.exists()) {
						log.info("Using checkpoint file for " + algorithmName + "!");
												
						deleteExcessEnsembleRunsFiles();
						
					} else {
						
						FileUtils.cleanDirectory(new File(matsimOutput.toString()));
						
					}
					
					long seed_base = seedBase + PRNG.nextInt(1000, 10000);
				
					CheckpointAndOutputResult wrapper = new CheckpointAndOutputResult(algorithm, checkpointFile, outputFile, CHECKPOINT_FREQ);
				
				long seed = seed_base*run;
				
				log.info("Running population " + run + " (using seed "+ seed + ")... ");	
				
				PRNG.setSeed(seed);
				while (wrapper.getNumberOfEvaluations() < MAX_NFE) {
					
					wrapper.step();			
					
					if (MAX_NFE % POP_SIZE == 0 && wrapper.getNumberOfEvaluations() < MAX_NFE) {
						
						wrapper.getResult();
						
						NondominatedPopulation currentResult = algorithm.getResult();
						
						currentResults.add(currentResult);
						processCurrentResults(currentResults, algorithmSeedFolder);
							
					}

					
				}
				
				NondominatedPopulation finalResult = algorithm.getResult();
				allResults.add(finalResult);
				allSeeds.add(seed);
				outputFiles.add(outputFile);
				
								
				computeRefSet(problem, outputFiles, algorithmSeedFolder);			
				writeSeeds(allSeeds, algorithmSeedFolder);			
				processFinalResults(allResults, algorithmSeedFolder);	
				
	
			}
		
			// computeRefSet(problem, outputFiles, algorithmOutputFolder);			
			// writeSeeds(allSeeds, algorithmOutputFolder);			
			// processResults(allResults, algorithmOutputFolder);			
			
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
			
			
			try {
		
			ResultFileEvaluator.main(new String[] {
					
					"--dimension", Integer.toString(problem.getNumberOfObjectives()),
					"--input", outputFile.getAbsolutePath(),
					"--output", new File(outputFile.getParentFile(), outputFile.getName().replace(".set", ".metrics")).getAbsolutePath(),
					"--reference", referenceSetFile.getAbsolutePath(),
					"--force"
				});
	
			
			} catch (Exception e) {
				
				log.error("requires at least two solutions", e);
			}
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
	private static void processFinalResults(List<NondominatedPopulation> allResults, Path algorithmNameDirectory) throws IOException{

		String resultFolder =  algorithmNameDirectory.toAbsolutePath() +  File.separator + "finalOptimisationResults" + File.separator;
		
		if (Files.exists(Paths.get(resultFolder))){
			
			FileUtils.cleanDirectory(new File(resultFolder));
			
		} else {
		
			Files.createDirectories(Paths.get(resultFolder));
		
		}
		
		RunSimulationBasedTransitOptimisation tr = new RunSimulationBasedTransitOptimisation();	
				
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
		
	}
	
	
	/**
	 * This method to call @decodeResult() (convert from JSON to XML) on the 
	 * final optimisation results contained in the Pareto front of the 
	 * algorithm and write them to file.	 
	 * 
	 * @param allResults List of results obtained from runSimulation()
	 * @throws IOException
	 */
	private static void processCurrentResults(List<NondominatedPopulation> currentResults, Path algorithmNameDirectory) throws IOException{

		String resultFolder =  algorithmNameDirectory.toAbsolutePath() +  File.separator + "currentOptimisationResults" + File.separator;
		
		if (Files.exists(Paths.get(resultFolder))){
			
			FileUtils.cleanDirectory(new File(resultFolder));
			
		} else {
		
			Files.createDirectories(Paths.get(resultFolder));
		
		}
		
		RunSimulationBasedTransitOptimisation tr = new RunSimulationBasedTransitOptimisation();	
				
		int folderIdx = 0;
		
		for(int run = 0; run < currentResults.size(); run++) {
			
			NondominatedPopulation runResult = currentResults.get(run);

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
		
	}

}
