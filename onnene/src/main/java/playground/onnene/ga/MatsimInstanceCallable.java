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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

import playground.onnene.exampleCode.UnzipUtility;


/**
 * Class that implements parallel MATSim
 * 
 * @author jwjoubert
 *
 */
public class MatsimInstanceCallable implements Callable<Double[]> {
	
	final private Logger log = Logger.getLogger(MatsimInstanceCallable.class);
	final private File folder;
	final private long seed;
	final private File innerOutputFolder;
	
	public MatsimInstanceCallable(String parentFolder, int run, long seedBase) {
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
			
			File release = new File(parentFolder.getAbsolutePath() + "/release.zip");
			if(!release.exists()) { throw new IOException("Cannot find " + release.getAbsolutePath()); }
			FileUtils.copyFile(release, new File(folder.getAbsolutePath() + "/release.zip"));
			
			log.info("folder path is: " + folder.getAbsolutePath());
		} catch(Exception e) {
			throw new RuntimeException("Cannot copy input file for MATSim run in " + folder.getAbsolutePath());
		}

	

		if (System.getProperty("os.name").startsWith("Windows")){
			
			/* Unzip the release */
			UnzipUtility unzipper = new UnzipUtility();
	        try {
	            unzipper.unzip(folder.getAbsolutePath() + File.separator + "release.zip", folder.getAbsolutePath());
	        } catch (Exception ex) {
	            // some errors occurred
	            ex.printStackTrace();
	        }
	        
	        /* Execute the MATSim run */
	        WindowsMatsimInstance.run(folder.getAbsolutePath(), innerOutputFolder.getAbsolutePath(), seed);
	        
	        //System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("/location/to/console.out")), true));
	        
        
		} else {
			
			/* Unzip the release */
			ProcessBuilder zipBuilder = new ProcessBuilder(
					"unzip", 
					String.format("%s%srelease.zip", folder.getAbsolutePath(),File.separator), 
					"-d", 
					String.format("%s", folder.getAbsolutePath()));
			Process zipProcess = null;
			int zipExitCode = 1;
			try {
				zipProcess = zipBuilder.start();
				zipExitCode = zipProcess.waitFor();
			} 
			
			catch (IOException e4) {
				e4.printStackTrace();
			}
			
			catch (InterruptedException e4) {
				e4.printStackTrace();
			}
			
			if(zipExitCode != 0) {
				throw new RuntimeException("Could not unzip release for MATSim run " + folder.getAbsolutePath());
			}
		
		
			/* Execute the MATSim run */
			ProcessBuilder equilBuilder = new ProcessBuilder(
					"java",
					"-Xmx8g",
					"-cp",
					String.format(".%s./onnene-0.10.0-SNAPSHOT/onnene-0.10.0-SNAPSHOT.jar", File.pathSeparator),
					"playground.onnene.ga.UnixMatsimInstance",
					"config.xml",
					String.format("%s", folder.getAbsolutePath()+File.separator+"output"),
					//"output/",
					String.valueOf(seed)
					);
			equilBuilder.directory(folder);
			equilBuilder.redirectErrorStream(true);
			
			Process equilProcess;
			try {
				equilProcess = equilBuilder.start();
			} catch (IOException e3) {
				e3.printStackTrace();
				throw new RuntimeException("Cannot execute MATSim instance");
			}
			log.info("MATSim instance started... " + folder.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(equilProcess.getInputStream()));
			String line;
			try {
				while((line = br.readLine()) != null) {
	//				System.out.println(line);
					/*FIXME Check if this can be written to standard out, or rather formal Logger. */
				}
			} catch (IOException e3) {
				e3.printStackTrace();
				throw new RuntimeException("Cannot reinterpret terminal process terminal output from buffer");
			}
			int equilExitCode;
			try {
				equilExitCode = equilProcess.waitFor();
			} catch (InterruptedException e3) {
				e3.printStackTrace();
				throw new RuntimeException("Terminated while waiting for MATSim instance to complete.");
			}
			log.info("MATSim instance completed: " + folder.getAbsolutePath() + ". Exit status '" + equilExitCode + "'");
			if(equilExitCode != 0) {
				log.error("Could not complete MATSim run " + folder.getAbsolutePath());
			}
		
		}
		
		/* Interpret the output events. */
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());	
		new MatsimNetworkReader(scenario.getNetwork()).readFile(folder + "/output/output_network.xml.gz");

		EventsManager manager = new EventsManagerImpl();	

		NetworkUserScoringFunction userFunction = new NetworkUserScoringFunction(folder + "/output/user_score.csv");
		manager.addHandler(userFunction);
		NetworkOperatorScoringFunction operatorFunction = new NetworkOperatorScoringFunction(folder + "/output/operator_score.csv", scenario.getNetwork());
		manager.addHandler(operatorFunction);
		new MatsimEventsReader(manager).readFile(folder + "/output/output_events.xml.gz");

		Double[] scoringArray = {userFunction.getUserScore(), operatorFunction.getOperatorScore()};
		
		/* Clean up. */
		FileUtils.delete(folder);
		
		return scoringArray;
	}

}
