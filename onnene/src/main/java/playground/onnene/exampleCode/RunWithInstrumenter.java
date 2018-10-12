package playground.onnene.exampleCode;
import java.io.File;
import java.io.IOException;
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

import playground.onnene.ga.GA_OperatorProvider;
import playground.onnene.ga.GA_ProblemProvider;

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
public class RunWithInstrumenter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
    	Problem problem = ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimizationProblem");
    	OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
		String[] algorithms = { "NSGAII" };
		//File refSet = new File("./input/ProblemReferenceSet/problemRefSet.txt");
		
		Instrumenter instrumenter = new Instrumenter();
				instrumenter.withProblem(problem);
				//instrumenter.withReferenceSet(refSet);
				instrumenter.attachApproximationSetCollector();
				instrumenter.attachElapsedTimeCollector();
				//instrumenter.withFrequency(10);
				//instrumenter.attachGenerationalDistanceCollector();
				//instrumenter.attachHypervolumeCollector();
		
		Executor executor = new Executor()
				.withSameProblemAs(instrumenter)
				.withProperty("operator", "MyCrossover+MyMutation")
				.withProperty("MyCrossover.Rate", 0.75)
	            .withProperty("MyMutation.Rate", 0.25)
	            .withProperty("populationSize", 100)
				.withMaxEvaluations(500)
				.withInstrumenter(instrumenter);
		
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

		for (String algorithm : algorithms) {
			referenceSet.addAll(executor.withAlgorithm(algorithm).run());
			results.put(algorithm, instrumenter.getLastAccumulator());
			
		}
		
		// Calculate the performance metrics using the reference set
		
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < referenceSet.size(); i++) {
			
			sb.append(referenceSet.get(i));
			
			System.out.println(sb.toString());
			
		}
		//StringBuilder resultStr = sb.append(referenceSet.toString());
		
		QualityIndicator qi = new QualityIndicator(problem, referenceSet);
		
		for (String algorithm : algorithms) {
			Accumulator accumulator = results.get(algorithm);
			System.out.print(accumulator.toCSV());
			try {
				accumulator.saveCSV(new File("./output/optimisationResults/runtimeResults.csv"));
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			for (int i = 0; i < accumulator.size("NFE"); i++) {

				List<Solution> approximationSet = (List<Solution>)accumulator.get("Approximation Set", i);
				qi.calculate(new NondominatedPopulation(approximationSet));
				
				System.out.print("    ");
				System.out.print(accumulator.get("NFE", i));
				System.out.print(" ");
				System.out.println("hypervolume");
				System.out.print(qi.getHypervolume());
				System.out.println();
				System.out.println("GD");
				System.out.print(qi.getGenerationalDistance());
				
			}
			
			System.out.println();
		}
		
//		private String encode(Variable variable) throws IOException {
//			StringBuilder sb = new StringBuilder();
//			
//			if (variable instanceof RealVariable) {
//				RealVariable rv = (RealVariable)variable;
//				sb.append(rv.getValue());
//			} else if (variable instanceof BinaryVariable) {
//				BinaryVariable bv = (BinaryVariable)variable;
//				
//				for (int i=0; i<bv.getNumberOfBits(); i++) {
//					sb.append(bv.get(i) ? "1" : "0");
//				}
//			} else if (variable instanceof Permutation) {
//				Permutation p = (Permutation)variable;
//
//				for (int i=0; i<p.size(); i++) {
//					if (i > 0) {
//						sb.append(',');
//					}
//					
//					sb.append(p.get(i));
//				}
//			} else {
//				throw new IOException("type not supported");
//			}
//			
//			return sb.toString();
//		}
	}

}
