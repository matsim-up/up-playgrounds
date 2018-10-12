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

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.matsim.up.utils.Header;

/**
 * @author jwjoubert
 *
 */
public class TryParallel {
	final private static Logger LOG = Logger.getLogger(TryParallel.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(TryParallel.class, args);
		
		String pathToRelease = args[0];
		String pathToOutput = args[1];
		int threadsPerRun = Integer.parseInt(args[2]);
		int runsPerBlock = Integer.parseInt(args[3]);
		
		ExecutorService executor = Executors.newFixedThreadPool(threadsPerRun*runsPerBlock);
//		List<Future<Integer>> jobs = new ArrayList<>();
		Map<Integer, Future<Integer>> jobs = new TreeMap<>();
		
		for(int i = 0; i < 5 ; i++) {
			TryParallelCallable tpc = new TryParallelCallable(pathToRelease, pathToOutput, i);
			Future<Integer> job = executor.submit(tpc);
			jobs.put(i, job);
		}
		executor.shutdown();
		while(!executor.isTerminated()) {
		}
		
		for(Integer i : jobs.keySet()) {
			Future<Integer> job = jobs.get(i);
			try {
				int status = job.get();
				LOG.info("Job " + i + "; exit status: " + status);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		
		Header.printFooter();
	}

}
