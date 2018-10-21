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

import playground.onnene.ga.NetworkOperatorScoringFunction;
import playground.onnene.ga.NetworkUserScoringFunction;

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
		

		
		return scoringArray;
	}
	

}
