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

import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.Header;

/**
 * This class is used to run the user and operator scoring function
 * 
 * @author Onnene
 *
 */
public class RunScoringFunctions {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Header.printHeader(RunScoringFunctions.class, args);
		
		String eventsFile = args[0];
		String output = args[1];
		String networkFile = args[2];
		String operationsOutput = args[3];	
		
		//Score Function 2
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());	
//		MatsimNetworkReader mnr = new MatsimNetworkReader(scenario.getNetwork());
//		//MatsimNetworkReader mnr1 = new MatsimNetworkReader(scenario.getNetwork());
		
//		mnr.readFile(networkFile);
		
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
		
		//Score Function 1
		EventsManager manager = new EventsManagerImpl();		
		manager.addHandler(new NetworkUserScoringFunction(output));	
		manager.addHandler(new NetworkOperatorScoringFunction(operationsOutput, scenario.getNetwork()));
		
		new MatsimEventsReader(manager).readFile(eventsFile);
		//manager.addHandler(new CarTravelDistanceEvaluator(scenario.getNetwork()));
		//manager.addHandler(new PtTravelDistanceCalculator(scenario.getNetwork()));
		
//		EventsManager eventsManager = EventsUtils.createEventsManager();
//		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());		
//		new MatsimNetworkReader(scenario.getNetwork()).readFile("input/network.xml");
		
//		PtTravelDistanceCalculator PtTravelDistanceEvaluator = new PtTravelDistanceCalculator(scenario.getNetwork());
//		
//		System.out.println(Arrays.toString(PtTravelDistanceEvaluator.getDistanceDistribution()));
//		
		
		//NetworkUserScoringFunction nuc = new NetworkUserScoringFunction(output);
		List<Double> totalVehicleTime = NetworkUserScoringFunction.getTotalVehicleTime();
		
		double sum = 0.0;
		
		for(Double tvt: totalVehicleTime) {
			
			sum += tvt;
		}
		System.out.println(sum);
		System.out.println(NetworkUserScoringFunction.getTotalVehicleTime());
		
		
		Header.printFooter();
	}

}
