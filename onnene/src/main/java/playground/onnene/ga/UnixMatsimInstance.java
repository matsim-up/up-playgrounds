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


import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.Header;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

/**
 * Class to execute a single MATSim run in its own virtual machine.
 * 
 * @author jwjoubert
 */
public class UnixMatsimInstance{
	
	private static final Logger log = Logger.getLogger(UnixMatsimInstance.class);
	private static String configFile;
	private static String output;
	private static long seed;
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		Header.printHeader(UnixMatsimInstance.class, args);
		
		configFile = args[0];
		output = args[1];
		seed = Long.parseLong(args[2]);
		runInstance();
		
		Header.printFooter();
	}
	
	private static void runInstance() {
		Config config = ConfigUtils.createConfig();
		ConfigUtils.loadConfig(config, configFile);
				
		config.global().setRandomSeed(seed);
		config.global().setNumberOfThreads(2);
		config.controler().setLastIteration(RunSimulationBasedTransitOptimisation.MATSIM_ITERATION_NUMBER);       
		config.controler().setOutputDirectory(output);       
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		
		config.plans().setInputFile("./plans.xml");
		config.parallelEventHandling().setNumberOfThreads(2);
		config.qsim().setNumberOfThreads(2);
		config.controler().setWriteEventsInterval(RunSimulationBasedTransitOptimisation.MATSIM_ITERATION_NUMBER); //FIXME
		config.network().setInputFile("./network.xml");
		config.transit().setVehiclesFile("./transitVehicles.xml");
		config.transit().setTransitScheduleFile("./transitSchedule.xml");

		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				// To use the deterministic pt simulation:
				//install(new SBBQSimModule());

				// To use the fast pt router:
				install(new SwissRailRaptorModule());
			}
		});

		controler.run();
	}
}
