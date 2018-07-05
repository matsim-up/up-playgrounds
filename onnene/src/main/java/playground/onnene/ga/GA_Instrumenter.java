package playground.onnene.ga;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;

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
 * @author Onnene
 *
 */
public class GA_Instrumenter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
    	Problem problem = ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimizationProblem");
    	OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
		String[] algorithms = { "NSGAII" };
		
		Instrumenter instrumenter = new Instrumenter();
				instrumenter.withProblem(problem);
				
				//.withProblem(problem)
				instrumenter.attachApproximationSetCollector();
				instrumenter.attachElapsedTimeCollector();
				instrumenter.withFrequency(10);
				//instrumenter.attachHypervolumeCollector();
		
		Executor executor = new Executor()
				//.withSameProblemAs(instrumenter)
				.withProblem(problem)
				.withProperty("operator", "MyCrossover+MyMutation")
				.withProperty("MyCrossover.Rate", 0.75)
	            .withProperty("MyMutation.Rate", 0.25)
	            .withProperty("populationSize", 2)
				.withMaxEvaluations(2)
				.withInstrumenter(instrumenter);
				
		
		 Accumulator acc = instrumenter.getLastAccumulator();
		
//       new Plot()
//       //.add("NSGAII", result)
//       .
//       .setXLabel("User-Cost")
//       .setYLabel("Operator-Cost")
//       //.add(acc)
//       .show();
//   
//   new Plot()
//   .add(acc)
//   .show();
	
		// Store the data and compute the reference set
		Map<String, Accumulator> results = new HashMap<String, Accumulator>();
		NondominatedPopulation referenceSet = new NondominatedPopulation();
//		
//		referenceSet.addAll(executor.withAlgorithm("NSGAII").run());
//		results.put("NSGAII", instrumenter.getLastAccumulator());
//		
		 StringBuilder sb = new StringBuilder();
		
		for (String algorithm : algorithms) {
			referenceSet.addAll(executor.withAlgorithm(algorithm).run());
			results.put(algorithm, instrumenter.getLastAccumulator());
			
		}
		System.out.println();
		System.out.println(sb.append(instrumenter.getLastAccumulator().toString()));
		System.out.println();
		System.out.println(sb.append(instrumenter.getLastAccumulator().keySet()));
		System.out.println();
		System.out.println(sb.append(instrumenter.getLastAccumulator().toCSV()));
		
		//System.out.print(Arrays.toString(results.entrySet().toArray()));
		
		// Calculate the performance metrics using the reference set
		QualityIndicator qi = new QualityIndicator(problem, referenceSet);
		
		for (String algorithm : algorithms) {
			Accumulator accumulator = results.get(algorithm);
			System.out.print(accumulator.keySet());
			
			
			
			for (int i = 0; i < accumulator.size("NFE"); i++) {

				List<Solution> approximationSet = (List<Solution>)accumulator.get("Approximation Set", i);
				qi.calculate(new NondominatedPopulation(approximationSet));
				
				System.out.print("    ");
				System.out.print(accumulator.get("NFE", i));
				System.out.print(" ");
				System.out.print(qi.getHypervolume());
				System.out.println();
			}
			
			System.out.println();
		}
	}

}
