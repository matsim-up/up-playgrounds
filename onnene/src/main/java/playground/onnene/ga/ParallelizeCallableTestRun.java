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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.moeaframework.core.NondominatedPopulation;

/**
 * @author Onnene
 *
 */
public class ParallelizeCallableTestRun {
	
	final private static Logger LOG = Logger.getLogger(ParallelizeCallableTestRun.class);
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//Header.printHeader(ExampleParallel.class, args);
		
		//int numberOfThreads = Integer.parseInt(args[0]);
		
		int numberOfThreads = Runtime.getRuntime().availableProcessors();
		System.out.println(numberOfThreads);
		//runnableExample(numberOfThreads);
		callableExample(numberOfThreads);
		
		//Header.printFooter();
	}
	
	/**
	 * Execute an example to show how to use a {@link Runnable} class.
	 * @param numberOfThreads
	 * @throws Exception 
	 */
	private static void callableExample(int numberOfThreads) throws Exception {
		/* Set up the multithreaded infrastructure. */
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		List<Future<NondominatedPopulation>> offSpringList =  new ArrayList<>();
		List<NondominatedPopulation> offSpringList1 =  new ArrayList<>();
		//Callable<List<NondominatedPopulation>> oo = new ArrayList<Callable<List<NondominatedPopulation>>>();	//List<Future<Integer>> jobsCallable = new ArrayList<>();
		
		
		//testRun tr = new testRun();
		
		//executor.submit(testRun.runSimulation());
		
//		CallableTestRun job = new CallableTestRun();	
//		Future<NondominatedPopulation> result = executor.submit(job);
//		offSpringList.add(result);
		
			
		
		
//		for(int i = 1; i <= 2; i++) {
//			CallableTestRun job = new CallableTestRun();		
//			Future<NondominatedPopulation> result = executor.submit(job);		
//			offSpringList.add(result);
//			Thread.sleep(3000);	
//		}		
		
		
		
//		 List<Callable<NondominatedPopulation>> callables = new ArrayList<Callable<NondominatedPopulation>>();
//		        for(int i=0; i<=5; i++) {
//		        	CallableTestRun job = new CallableTestRun();
//		            //callables.add(new CallableTestRun());
//		        	callables.add(job);
//		            Thread.sleep(3000);
//		        }
//		        try {
//		            List<Future<NondominatedPopulation>> results = executor.invokeAll(callables);
//		            for(Future<NondominatedPopulation> result: results) {
//		            	
//		                System.out.println("Got result of thread #" + result.get());
//		                Thread.sleep(3000);
//		            }
//		        } catch (InterruptedException ex) {
//		            ex.printStackTrace();
//		        } finally {
//		        	executor.shutdownNow();
//		        }
//		
//		
		executor.shutdown();
		while(!executor.isTerminated()) {
		}
			
//			NondominatedPopulation result = (NondominatedPopulation) executor.submit(job);
//			offSpringList.add(result);
			//ctr.call();
			
			 
			
//			ExampleParallelCallable job = new ExampleParallelCallable(i);
//			Future<Integer> result = executor.submit(job);
			//jobsCallable.add(result);
		
		
		
		/* Consolidate the output */
		
		
		//int bigTotal = 0;
		//NondominatedPopulation pop = null;
//		for(Future<NondominatedPopulation> offSpring : offSpringList) {
//			
//			NondominatedPopulation pop = offSpring.get();
//			//System.out.println(pop);
//			offSpringList1.add(pop);
//			//offSpringList1.add((NondominatedPopulation) offSpring);
//		}
//		CallableTestRun.processResults(offSpringList1);
		//LOG.info("Total from Callable: " + bigTotal);
		
		LOG.info("Run is complete");
	}

}
