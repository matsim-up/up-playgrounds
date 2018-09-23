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
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.FileUtils;

/**
 * @author Onnene
 *
 */
public class LocalMachineMatsimInstanceCallable implements Callable<Double[]>{
	
	final private Logger log = Logger.getLogger(LocalMachineMatsimInstanceCallable.class);
	final private File folder;
	final private File innerOutputFolder;
	final private long seed;

	public LocalMachineMatsimInstanceCallable(String parentFolder, int run, long seedBase) {
		parentFolder += parentFolder.endsWith(File.separator) ? "" : File.separator;
		folder = new File(parentFolder + "output_" + run + File.separator);
		boolean created = folder.mkdirs();
		if(!created) {
			throw new RuntimeException("Could not create the MATSim run folder " + folder.getAbsolutePath());
		}
		
		innerOutputFolder = new File(folder.getAbsolutePath() + File.separator + "output" + File.separator );
		innerOutputFolder.mkdirs();
		if(!created) {
			throw new RuntimeException("Could not create the MATSim run inneroutput folder " + innerOutputFolder.getAbsolutePath());
		}
		
		seed = seedBase*run;
	}

	
	@Override
	public Double[] call() throws Exception {
		/* Copy the necessary input files. */
		File parentFolder = folder.getParentFile();
		try {
			File configIn = new File(parentFolder.getAbsolutePath() + "/config.xml");
			if(!configIn.exists()) { throw new IOException("Cannot find " + configIn.getAbsolutePath()); }
			FileUtils.copyFile(configIn, new File(folder.getAbsolutePath() + "/config.xml"));
			
			File networkIn = new File(parentFolder.getAbsolutePath() + "/network.xml");
			if(!networkIn.exists()) { throw new IOException("Cannot find " + networkIn.getAbsolutePath()); }
			FileUtils.copyFile(networkIn, new File(folder.getAbsolutePath() + "/network.xml"));
			
			File plansIn = new File(parentFolder.getAbsolutePath() + "/plans.xml");
			if(!plansIn.exists()) { throw new IOException("Cannot find " + plansIn.getAbsolutePath()); }
			FileUtils.copyFile(plansIn, new File(folder.getAbsolutePath() + "/plans.xml"));
			
			File transitVehiclesIn = new File(parentFolder.getAbsolutePath() + "/transitVehicles.xml");
			if(!transitVehiclesIn.exists()) { throw new IOException("Cannot find " + transitVehiclesIn.getAbsolutePath()); }
			FileUtils.copyFile(transitVehiclesIn, new File(folder.getAbsolutePath() + "/transitVehicles.xml"));
			
			File transitScheduleIn = new File(parentFolder.getAbsolutePath() + "/transitSchedule.xml");
			if(!transitScheduleIn.exists()) { throw new IOException("Cannot find " + transitScheduleIn.getAbsolutePath()); }
			FileUtils.copyFile(transitScheduleIn, new File(folder.getAbsolutePath() + "/transitSchedule.xml"));

		} catch(Exception e) {
			throw new RuntimeException("Cannot copy input file for MATSim run in " + folder.getAbsolutePath());
		}
		
		//LocalMachineMatsimInstance.runInstance(folder + File.separator + "config.xml", folder.getAbsolutePath(), seed);
		
		
//		Config config = ConfigUtils.createConfig();
//		ConfigUtils.loadConfig(config, folder.getAbsolutePath() + "./config.xml");
//		config.global().setRandomSeed(seed);
//		config.global().setNumberOfThreads(4);
//		config.controler().setLastIteration(LocalMachineRunSimulationBasedTransitOptimisationProblem.MATSIM_ITERATION_NUMBER);       
//		config.controler().setOutputDirectory(innerOutput.getAbsolutePath());       
//		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
//		
//		config.plans().setInputFile(folder.getAbsolutePath() + "./plans.xml");
//		//config.parallelEventHandling().setNumberOfThreads(1);
//		config.qsim().setNumberOfThreads(6);
//		config.controler().setWriteEventsInterval(1); //FIXME
//		config.network().setInputFile(folder.getAbsolutePath() + "./network.xml");
//		config.transit().setVehiclesFile(folder.getAbsolutePath() + "./transitVehicles.xml");
//		config.transit().setTransitScheduleFile( folder.getAbsolutePath() + "./transitSchedule.xml");
//
//		Scenario scenario = ScenarioUtils.loadScenario(config);
//		Controler controler = new Controler(scenario);
//		
//		controler.run();


		/* Execute the MATSim run */
		LocalMachineMatsimInstance.runInstance(folder.getAbsolutePath(), innerOutputFolder.getAbsolutePath(), seed);
		
		/* Interpret the output events. */
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());	
		new MatsimNetworkReader(scenario.getNetwork()).readFile(innerOutputFolder + File.separator + "output_network.xml.gz");

		EventsManager manager = new EventsManagerImpl();	

		NetworkUserScoringFunction userFunction = new NetworkUserScoringFunction(innerOutputFolder + File.separator + "user_score.csv");
		manager.addHandler(userFunction);
		NetworkOperatorScoringFunction operatorFunction = new NetworkOperatorScoringFunction(innerOutputFolder + File.separator + "operator_score.csv", scenario.getNetwork());
		manager.addHandler(operatorFunction);
		new MatsimEventsReader(manager).readFile(innerOutputFolder + File.separator + "output_events.xml.gz");

		Double[] scoringArray = {userFunction.getUserScore(), operatorFunction.getOperatorScore()};
		
		
		/* Clean up. */
		
		FileUtils.delete(folder);
		
//		for(File file: folder.listFiles()) 
//	    if (!file.getName().endsWith("txt")) 
//	        file.delete();
		
//		try {
//			//File ensembleIn = new File(folder.getParent());
//			File ensembleIn = new File("./output/matsimOutput/");
//			File ensembleOut = new File("./input/output/matsimOutput/");
//						
//			//if(!ensembleIn.exists()) { throw new IOException("Cannot find " + ensembleIn.getAbsolutePath()); }
//			org.apache.commons.io.FileUtils.copyDirectoryToDirectory(ensembleIn, ensembleOut);
////			for(File file: ensembleOut.listFiles()) 
////			    if (!file.getName().endsWith("txt")) 
////			        file.delete();
//			//FileUtils.copyDirectoryStructure(ensembleIn, new File("./input/output/matsimOutput/"));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		

		
		return scoringArray;
	}
	

}
