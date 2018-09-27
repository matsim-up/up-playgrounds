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
package playground.onnene.exampleCode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.utils.Header;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;

import playground.onnene.ga.DecisionVariable;
import playground.onnene.ga.GA_OperatorProvider;
import playground.onnene.ga.GA_ProblemProvider;
import playground.onnene.ga.ProblemUtils;

/**
 * @author Onnene
 *
 */
public class RunSimulationBasedTransitOptimisationProblem {
	final private static Logger LOG = Logger.getLogger(RunSimulationBasedTransitOptimisationProblem.class);
	private static final int MAX_MOEA_EVALUATIONS = 500; //FIXME was 20
	public static final int MATSIM_ITERATION_NUMBER = 50; // FIXME was 10
	private static BufferedWriter SEED_FILE;
	static Calendar cal = Calendar.getInstance();
	static SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");   
	static Date startTime = new Date();


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {	
		Header.printHeader(RunSimulationBasedTransitOptimisationProblem.class, args);

		int numThreads = Integer.parseInt(args[0]);
		long seed_base = Long.parseLong(args[1]);
		int numberOfRuns = Integer.parseInt(args[2]);
		//int startEvaluate = Integer.parseInt(args[3]);

		//runSimulation(numThreads, seed_base, numberOfRuns, startEvaluate);
		runSimulation(numThreads, seed_base, numberOfRuns);
		Header.printFooter();
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


	private static File checkPoint(String checkPointFile) {
		File checkpointFile = new File (checkPointFile);
		if (checkpointFile.exists()) {
			LOG.error("Checkpoint file exists, will resume from prior run!");
		}
		return checkpointFile;
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

	//public static void runSimulation(int numThreads, long seed_base, int numberOfRuns, int startEvaluate) throws Exception {
	public static void runSimulation(int numThreads, long seed_base, int numberOfRuns) throws Exception {
		/* Only delete the output folder if a new run is initiated. */
//		if(startEvaluate == 0) {
//			setupOutput();
//		}
		setupOutput();
		File checkPointFile = checkPoint("./output/logs/checkpoint.dat");
		String ResultFolder = "./output/optimisationResults/";

		List<NondominatedPopulation> allResults = new ArrayList<>();
		List<Long> lstOfSeeds = new ArrayList<>();

		ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
		Problem problem = ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimisationProblem");  

		OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());  
		// String[] algorithms = { "NSGAII" }; 


		Instrumenter instrumenter = new Instrumenter()
				.withProblem(problem)		
				.withFrequency(5)
				.attachApproximationSetCollector()
				.attachElapsedTimeCollector();

		for(int run = 0; run < numberOfRuns; run++) {
			long seed = seed_base*((long)run);

			PRNG.setSeed(seed);
			LOG.info("Running population " + run + " (using seed "+ seed_base + ")... ");
			NondominatedPopulation finalResult = new Executor()
					.withSameProblemAs(instrumenter)
					.withAlgorithm("NSGAII")
					.withProperty("operator", "MyCrossover+MyMutation")
					.withProperty("MyCrossover.Rate", 0.75)
					.withProperty("MyMutation.Rate", 0.25)
					.withProperty("populationSize", 20) // FIXME
					.withMaxEvaluations(MAX_MOEA_EVALUATIONS)  
					//.resetCheckpointFile()
					//					.withCheckpointFile(checkPointFile)		  
					//					.withCheckpointFrequency(5)
					.withInstrumenter(instrumenter)     
					//					.distributeOn(numThreads)
					//					.distributeOnAllCores()            
					.run();

			allResults.add(finalResult);
			lstOfSeeds.add(seed);

			LOG.info("Completed run");
		}

		/* Write all the seeds to file (for record). */
		SEED_FILE = IOUtils.getAppendingBufferedWriter("./output/logs/seeds.txt");
		try {
			for (int i = 0; i < lstOfSeeds.size(); i++) {
				SEED_FILE.write(String.format("%d\t%d\n", i+1, lstOfSeeds.get(i)));
			}
		} finally {
			SEED_FILE.close();
		}

		RunSimulationBasedTransitOptimisationProblem rwos = new RunSimulationBasedTransitOptimisationProblem();

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
				rwos.decodeResult(runSolution.getVariable(0), ResultFolder, folderIdx, fileIdx);
				LOG.info(String.format("%.4f\t%.4f%n", runSolution.getObjective(0), runSolution.getObjective(1)));

				BufferedWriter bw = IOUtils.getAppendingBufferedWriter("./output/logs/run_moea_log.txt");		
				BufferedWriter bwRef = IOUtils.getAppendingBufferedWriter("./output/problemReferenceSet/referenceSet.txt");
				BufferedWriter bwRefPf = IOUtils.getAppendingBufferedWriter("./output/problemReferenceSet/referenceSet.pf");

				try {
					bw.write(String.format("%d\t%d\t%.4f\t%.4f\n", 
							(run+1), 
							(solution+1), 
							runSolution.getObjective(0), 
							runSolution.getObjective(1)));
					bwRef.write(String.format("%d\t%d\t%.4f\t%.4f\n",  
							(run+1), 
							(solution+1), 
							runSolution.getObjective(0), 
							runSolution.getObjective(1)));
					bwRefPf.write(String.format("%.4f\t%.4f\n",  
							runSolution.getObjective(0), 
							runSolution.getObjective(1)));
				} finally {
					bw.close();
					bwRef.close();
					bwRefPf.close();
				}
			}

			Map<String, Accumulator> results = null;

			if (runResult.size() == 1){
				LOG.error("requires at least two solutions");
			} else {

				results = new HashMap<String, Accumulator>();
				results.put("NSGAII", instrumenter.getLastAccumulator());
				QualityIndicator qi = new QualityIndicator(problem, runResult);
				Accumulator accumulator = results.get("NSGAII");

				try {
					accumulator.saveCSV(new File("./output/optimisationResults/runtimeResults.csv"));
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < accumulator.size("NFE"); i++) {
					List<Solution> approximationSet = (List<Solution>)accumulator.get("Approximation Set", i);
					qi.calculate(new NondominatedPopulation(approximationSet));

					/* Write the header, but only the first time. */
					File indicatorFile = new File(ResultFolder+folderIdx + File.separator + "indicators.txt");
					BufferedWriter bwIndicator = null;
					if(!indicatorFile.exists()) {
						bwIndicator = IOUtils.getBufferedWriter(indicatorFile.getAbsolutePath());
						try {
							bwIndicator.write("NFE\tHV\tGD\tAEI\tIGD\tS\tMPE\n");
						} finally {
							bwIndicator.close();
						}
					}
					bwIndicator = IOUtils.getAppendingBufferedWriter(indicatorFile.getAbsolutePath());
					try {
						bwIndicator.write(
								String.format("\n%d\t%.8f\t%.8f\t%10.8f\t%10.8f\t%10.8f\t%10.8f\n", 
										accumulator.get("NFE", i), 
										qi.getHypervolume(), 
										qi.getGenerationalDistance(), 
										qi.getAdditiveEpsilonIndicator(), 
										qi.getInvertedGenerationalDistance(), 
										qi.getSpacing(), 
										qi.getMaximumParetoFrontError())
								);
					} finally {
						bwIndicator.close();
					}

				}
			}
		}

		Date endTime = new Date();        
		long timeDiff = endTime.getTime() - startTime.getTime();   
		int durationInMilliseconds = (int) (timeDiff);

		int seconds = (int) (durationInMilliseconds / 1000) % 60 ;
		int minutes = (int) ((durationInMilliseconds / (1000*60)) % 60);
		int hours   = (int) ((durationInMilliseconds / (1000*60*60)) % 24);

		BufferedWriter bwMOEA = IOUtils.getAppendingBufferedWriter("./output/logs/run_moea_log.txt");
		try {
			bwMOEA.write(String.format("End time: %s\n", endTime.toString()));
			bwMOEA.write(String.format("\nDuration: %02d:%02d:%02d\n", hours, minutes, seconds));
		} finally {
			bwMOEA.close();
		}
	}
}
