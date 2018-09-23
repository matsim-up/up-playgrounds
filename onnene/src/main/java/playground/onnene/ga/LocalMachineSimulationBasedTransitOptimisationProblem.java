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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.utils.FileUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

/**
 * This class is a version of the simulation based 
 * @author Onnene
 *
 */
public class LocalMachineSimulationBasedTransitOptimisationProblem extends AbstractProblem{
	
	/*TODO The following should be set once we have a good idea of what they need to be. */ 
	final private static int SIMULATIONS_PER_EVALUATION = 1;
	final private static int SIMULATIONS_PER_BLOCK = 1;
	final private static int THREADS_PER_SIMULATION = 6;
	final private ConsolidateMechanism mech = ConsolidateMechanism.mean;

	/* Other variables. */
	final private Logger log = Logger.getLogger(LocalMachineSimulationBasedTransitOptimisationProblem.class.getName());  
	final private long seedBase = 20180920l;
	private static AtomicInteger overallRunNumber = new AtomicInteger(0);


	public static synchronized int getOverallRunNumber() {
		return overallRunNumber.get();
	}


	//	public SimulationBasedTransitOptimizationProblem(String inputFolder, String outputFolder, int runNumber) throws IOException, InterruptedException {
	public LocalMachineSimulationBasedTransitOptimisationProblem(){
		super(1, 2);
	}


	@Override
	public void evaluate(Solution solution) {
		DecisionVariable var = (DecisionVariable) solution.getVariable(0);
		JSONObject Jvar = var.getTransitSchedule();

		final int runNumber = overallRunNumber.getAndIncrement();
		String inputFolder = "./input/";
		String outputFolder = "./output/matsimOutput/" + runNumber + File.separator;
		final String folder = outputFolder;
		new File(outputFolder).mkdirs();
		log.info("Starting evaluate() call... " + folder);
		log.info("Output folder created. This run: " + runNumber + "; overall: " + (runNumber+1));

		/* Copy all the necessary input files, JAR included */
		log.info("Copying all required input files...");
		try {
			File configIn = new File(inputFolder + "matsimInput/config.xml");
			if(!configIn.exists()) { throw new IOException("Cannot find " + configIn.getAbsolutePath()); }
			FileUtils.copyFile(configIn, new File(outputFolder + "config.xml"));

			File networkIn = new File(inputFolder + "matsimInput/network.xml");
			if(!networkIn.exists()) { throw new IOException("Cannot find " + networkIn.getAbsolutePath()); }
			FileUtils.copyFile(networkIn, new File(outputFolder + "network.xml"));

			File plansIn = new File(inputFolder + "matsimInput/plans.xml");
			if(!plansIn.exists()) { throw new IOException("Cannot find " + plansIn.getAbsolutePath()); }
			FileUtils.copyFile(plansIn, new File(outputFolder + "plans.xml"));

			File transitVehiclesIn = new File(inputFolder + "matsimInput/transitVehicles.xml");
			if(!transitVehiclesIn.exists()) { throw new IOException("Cannot find " + transitVehiclesIn.getAbsolutePath()); }
			FileUtils.copyFile(transitVehiclesIn, new File(outputFolder + "transitVehicles.xml"));

			//    	File transitScheduleIn = new File(inputFolder + "matsimInput/transitSchedule.xml");
			//    	if(!transitScheduleIn.exists()) { throw new IOException("Cannot find " + transitScheduleIn.getAbsolutePath()); }
			//    	FileUtils.copyFile(transitScheduleIn, new File(outputFolder + "transitSchedule.xml"));

//			File release = new File(inputFolder + "matsimInput/release.zip");
//			if(!release.exists()) { throw new IOException("Cannot find " + release.getAbsolutePath()); }
//			FileUtils.copyFile(release, new File(outputFolder + "release.zip"));
		} catch(Exception e) {
			throw new RuntimeException("Cannot copy input file.");
		}
		log.info("Done copying input files.");

		/* Translate schedule into input file for simulation */ 
		try {
			ProblemUtils.getXMLFromJSONDecisionVar(Jvar, folder + "transitSchedule.xml");
		} catch (IOException e3) {
			e3.printStackTrace();
			throw new RuntimeException("Cannot convert JSON solution to MATSim transitSchedule.xml");
		}

		/* Set up the parallel MATSim evaluation infrastructure. */
		List<Future<Double[]>> jobs = new ArrayList<Future<Double[]>>();
		int totalSimulations = 0;
		int simulationsInCurrentBlock = 0;
		while(totalSimulations < SIMULATIONS_PER_EVALUATION) {
			ExecutorService executor = Executors.newFixedThreadPool(THREADS_PER_SIMULATION*SIMULATIONS_PER_BLOCK);

			while(simulationsInCurrentBlock < SIMULATIONS_PER_BLOCK && totalSimulations < SIMULATIONS_PER_EVALUATION) {
				LocalMachineMatsimInstanceCallable mic = new LocalMachineMatsimInstanceCallable(folder, totalSimulations, seedBase);
				Future<Double[]> job = executor.submit(mic);
				jobs.add(job);

				simulationsInCurrentBlock++;
				totalSimulations++;
			}
			executor.shutdown();
			while(!executor.isTerminated()) {
			}
		}
		/* Consolidate the MATSim runs into single solution. */
		double[] evaluatedSolution = consolidateMatsimRuns(outputFolder, jobs, solution.getNumberOfObjectives());

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			solution.setObjective(i, evaluatedSolution[i]);
		}
		
		/* Clean up. Leave only the consolidation result output. */
		FileUtils.delete(new File(folder + "config.xml"));
		FileUtils.delete(new File(folder + "network.xml"));
		FileUtils.delete(new File(folder + "plans.xml"));
		//FileUtils.delete(new File(folder + "release.zip"));
		FileUtils.delete(new File(folder + "transitSchedule.xml"));
		FileUtils.delete(new File(folder + "transitVehicles.xml"));
		
		log.info("Completed evaluate() call on folder " + folder);
	}


	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 2);   	
		solution.setVariable(0, new DecisionVariable());
		return solution;
	}

	private double[] consolidateMatsimRuns(String folder, List<Future<Double[]>> listOfJobs, int objectives) {
		double[] result = new double[objectives];
		
		//log.info("folder: " + folder);
		//String matsimEvalNum = new File(folder).getParentFile().getName();
		String ensembleFilename = folder + "ensembleRuns.txt";
		//String ensembleFilename = "./input/output/matsimOutput/" + "ensembleRuns.txt";
		//BufferedWriter bw = IOUtils.getBufferedWriter(ensembleFilename);
		BufferedWriter bw = IOUtils.getBufferedWriter(ensembleFilename);
		try {
			bw.write("Run\tObj1\tObj2\n");
			
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Could not write ensemble runs result.");
		} finally {
			try {
				bw.close();
				
			} catch (IOException e) {
				e.printStackTrace();
				log.error("Could not write ensemble runs result.");
			}
		}
		
		
		List<String> report = new ArrayList<>();

		/* Consolidate. */
		switch (mech) {
		case mean:
			List<Double[]> listOfResults = new ArrayList<>();
			for(Future<Double[]> job : listOfJobs) {
				try {
					listOfResults.add(job.get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
					throw new RuntimeException("Cannot get multi-MATSim-run results.");
				}
			}
			
			double[] sum = new double[objectives];

			int run = 0;
			for(Double[] da : listOfResults) {
				for(int obj = 0; obj < objectives; obj++) {
					double oldSum = sum[obj];
					sum[obj] = oldSum + da[obj];
					
				}
				/* Report result */
				String reportLine = String.format("%d\t%.4f\t%.4f\n", run++, da[0], da[1]);
				report.add(reportLine);
			}
			
			for(int obj = 0; obj < objectives; obj++) {
				result[obj] = sum[obj] / ((double) listOfJobs.size());
			}
			String reportLine = String.format("Mean\t%.4f\t%.4f\n", result[0], result[1]);
			report.add(reportLine);
			break;
		default:
			throw new RuntimeException("Cannot consolidate multiple MATSim runs using mechanism '" + mech.toString() + "'");
		}

		bw = IOUtils.getAppendingBufferedWriter(ensembleFilename);
		try {
			for(String reportLine : report) {
				bw.write(reportLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Could not write ensemble runs result.");
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				log.error("Could not write ensemble runs result.");
			}
		}
		
		try {
			
			//Paths.get(folder);
			//Paths.get("./input/output/matsimOutput/");
			
			File ensembleIn = new File(folder);
			File ensembleOut = new File("./input/output/matsimOutput/");
			
			//File currentFolder = new File (ensembleOut.getAbsolutePath() + File.separator + ensembleIn.getName());
			
//			if (currentFolder.exists()){
//				//FileUtils.delete(ensembleIn);
//				org.apache.commons.io.FileUtils.cleanDirectory(currentFolder);
//			} 
			
			
			org.apache.commons.io.FileUtils.copyDirectoryToDirectory(ensembleIn, ensembleOut);
//			for(File file: ensembleOut.listFiles()) 
//				if (file.isDirectory()) {
//					for(File f: file.listFiles()) 
//						if (!f.getName().equals("ensembleRuns.txt")) 
//							f.delete();
//		    
//				}
			//FileUtils.copyDirectoryStructure(ensembleIn, new File("./input/output/matsimOutput/"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return result;
	}

	/**
	 * Initially (21/8/2018|) we thought that 'best' and 'worst' might also be
	 * possible consolidation mechanisms, but this will add some complications.
	 * For example, what if the first ensemble run produces the 'best' value for
	 * objective 1, but a very bad one for the other. Similarly, the third 
	 * ensemble run might produce the 'best' value for objective two, but pretty
	 * bad for objective one.
	 *
	 * @author jwjoubert
	 */
	private enum ConsolidateMechanism{
		mean, median, best, worst;
	}
	

}
