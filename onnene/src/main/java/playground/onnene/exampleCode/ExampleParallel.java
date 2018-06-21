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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.matsim.up.utils.Header;

/**
 * Class to demonstrate two possible implementations of parallelization. The 
 * main difference is that {@link Runnable} only does something, and if you
 * want to get something back, you have to create methods, while a 
 * {@link Callable} gives something back (which you have to define at the
 * outset).
 * 
 * @author jwjoubert
 */
public class ExampleParallel {
	final private static Logger LOG = Logger.getLogger(ExampleParallel.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(ExampleParallel.class, args);
		
		runnableExample(4);
		callableExample(4);
		
		Header.printFooter();
	}

	
	/**
	 * Execute an example to show how to use a {@link Runnable} class.
	 * @param numberOfThreads
	 */
	private static void runnableExample(int numberOfThreads) {
		/* Set up the multithreaded infrastructure. */
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		List<ExampleParallelRunnable> jobsRunnable = new ArrayList<>();
		for(int i = 1; i <= 4; i++) {
			ExampleParallelRunnable job = new ExampleParallelRunnable(i);
			executor.submit(job);
			
			/* If I want to get something from the Runnable class, I have to
			 * keep a copy of it in the list. If I don't need it, i.e. it
			 * just DO SOMETHING, then I don't need to keep it. */
			jobsRunnable.add(job);
		}
		executor.shutdown();
		while(!executor.isTerminated()) {
		}
		
		/* Consolidate the output */
		int bigTotal = 0;
		for(ExampleParallelRunnable job : jobsRunnable) {
			bigTotal += job.getTotal();
		}
		LOG.info("Total from Runnable: " + bigTotal);
	}

	
	/**
	 * Execute an example to show how to use a {@link Runnable} class.
	 * @param numberOfThreads
	 */
	private static void callableExample(int numberOfThreads) {
		/* Set up the multithreaded infrastructure. */
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		List<Future<Integer>> jobsCallable = new ArrayList<>();
		for(int i = 1; i <= 4; i++) {
			ExampleParallelCallable job = new ExampleParallelCallable(i);
			Future<Integer> result = executor.submit(job);
			jobsCallable.add(result);
		}
		executor.shutdown();
		while(!executor.isTerminated()) {
		}
		
		/* Consolidate the output */
		int bigTotal = 0;
		for(Future<Integer> job : jobsCallable) {
			try {
				bigTotal += job.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException("Woopsie!! Something went wrong!!");
			} catch (ExecutionException e) {
				e.printStackTrace();
				throw new RuntimeException("Woopsie!! Something went wrong!!");
			}
		}
		LOG.info("Total from Callable: " + bigTotal);
	}
	
}
