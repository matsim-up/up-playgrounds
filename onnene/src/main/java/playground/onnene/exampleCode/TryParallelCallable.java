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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.matsim.up.utils.FileUtils;

import com.jogamp.common.util.IntObjectHashMap;

/**
 * @author jwjoubert
 *
 */
public class TryParallelCallable implements Callable<Integer> {
	final private Logger log = Logger.getLogger(TryParallelCallable.class);
	final String release;
	final String output;
	final int job;

	public TryParallelCallable(String release, String output, int job) {
		this.release = release;
		this.output = output;
		this.job = job;
	}
	
	
	@Override
	public Integer call() throws Exception {
		/* Create the run-specific folder. */
		File thisOutput = new File(String.format("%s/run_%03d/", output, job));
		if(thisOutput.exists()) {
			log.warn("Output folder '" + thisOutput.getAbsolutePath() + "' exists and will be deleted");
			FileUtils.delete(thisOutput);
		}
		boolean createFolder = thisOutput.mkdirs();
		if(!createFolder) {
			throw new RuntimeException("Cannot create " + thisOutput.getAbsolutePath()); 
		}
		
		/* Copy all the necessary files. */
		File releaseFile = new File(thisOutput.getAbsolutePath() + "/release.zip");
		FileUtils.copyFile(new File(release), releaseFile);
		
		File configFile = new File(thisOutput.getAbsolutePath() + "/config.xml");
		FileUtils.copyFile(new File(this.output + "config.xml"), configFile);
		
		File networkFile = new File(thisOutput.getAbsolutePath() + "/network.xml");
		FileUtils.copyFile(new File(this.output + "network.xml"), networkFile);
		
		File plansFile = new File(thisOutput.getAbsolutePath() + "/plans100.xml");
		FileUtils.copyFile(new File(this.output + "plans100.xml"), plansFile);
		
		/* Unzip the release */
		ProcessBuilder zipBuilder = new ProcessBuilder(
				"unzip", 
				String.format("%s/release.zip", thisOutput.getAbsolutePath()), 
				"-d", 
				String.format("%s", thisOutput.getAbsolutePath()));
		final Process zipProcess = zipBuilder.start();
		int zipExitCode = zipProcess.waitFor();
		if(zipExitCode != 0) {
			log.error("Could not unzip release. Exit status '" + zipExitCode + "'");
		}
		
		/* Do your magic and run the Equil example. */
		ProcessBuilder equilBuilder = new ProcessBuilder(
				"java",
				"-Xmx512m",
				"-cp",
				"matsim-up-0.10.0-SNAPSHOT/matsim-up-0.10.0-SNAPSHOT.jar",
				"org.matsim.run.RunMatsim",
				"config.xml"
				);
		equilBuilder.directory(thisOutput);
		log.info("Builder: " + equilBuilder.command().toString());
		equilBuilder.redirectErrorStream(true);
		final Process equilProcess = equilBuilder.start();
		log.info("Process started...");
		BufferedReader br = new BufferedReader(new InputStreamReader(equilProcess.getInputStream()));
		String line;
		while((line = br.readLine()) != null) {
			log.info("Job " + job + ": " + line);
		}
		int equilExitCode = equilProcess.waitFor();
		log.info("Process ended. Exit status '" + equilExitCode + "'");
		if(equilExitCode != 0) {
			log.error("Could not complete Equil run for job '" + job + "'");
		}

		/* Clean up the output folders */
		FileUtils.delete(new File(thisOutput.getAbsolutePath() + "/release.zip"));
		FileUtils.delete(new File(thisOutput.getAbsolutePath() + "/matsim-up-0.10.0-SNAPSHOT/"));
		FileUtils.delete(new File(thisOutput.getAbsolutePath() + "/output/ITERS/"));
		
		return equilExitCode;
	}

}
