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

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.Header;

import playground.onnene.ga.NetworkOperatorScoringFunction;
import playground.onnene.ga.NetworkUserScoringFunction;

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
		String userScoreOutputFile = args[1];
		String networkFile = args[2];
		String operatorScoreOutputFile = args[3];	
		
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());			
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
		
		EventsManager manager = new EventsManagerImpl();
		NetworkUserScoringFunction userFunction = new NetworkUserScoringFunction(userScoreOutputFile);
		manager.addHandler(userFunction);	
		NetworkOperatorScoringFunction operatorFunction = new NetworkOperatorScoringFunction(operatorScoreOutputFile, scenario.getNetwork());
		manager.addHandler(operatorFunction);
		
		
		new MatsimEventsReader(manager).readFile(eventsFile);
		
		double aaa = userFunction.getUserScore();		
		double bbb = operatorFunction.getOperatorScore();

		System.out.println("user score is " + aaa + " minutes");
		System.out.println("operator score is " + bbb + " rands");

		Header.printFooter();
	}

}
