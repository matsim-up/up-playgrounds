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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.XML;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.spi.OperatorFactory;


/**
 * This class runs the optimisation problem 
 * 
 * @author Onnene
 *
 */
public class RunSimulationBasedTransitOptimizationProblem {
	
    //private static final Logger LOGGER = Logger.getLogger(RunSimulationBasedTransitOptimizationProblem.class);
    private static final int MAX_MOEA_EVALUATIONS = 2;
    public static int callsToEvaluate = 0;
    
    private static FileOutputStream FOS;
    static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");   
    static Date startTime = new Date();
    
    static {
        try {
            FOS = new FileOutputStream(new File(DirectoryConfig.RUN_MOEA_LOG_FILE_PATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private String encode(Variable variable) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		if (variable instanceof  DecisionVariable) {
			
			DecisionVariable obi = (DecisionVariable) variable;
			
			sb.append(obi.getTransitSchedule());
			
		} 
		
		else {
			
			throw new IOException("type not supported");
		}
		
		 //System.out.print(sb.length());
		
		return sb.toString();
	}
    
    public static void main(String[] args) throws Exception {

        OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
        
        //PRNG.setSeed(2255);
        
        Instrumenter instrumenter = new Instrumenter();
        	
            instrumenter.withProblemClass(SimulationBasedTransitOptimizationProblem.class);
        	instrumenter.withFrequency(1);
        	instrumenter.attachElapsedTimeCollector();
//        	instrumenter.withReferenceSet(null);
//        	instrumenter.attachGenerationalDistanceCollector();
//        	instrumenter.attachHypervolumeCollector();
//        	instrumenter.attachPopulationSizeCollector();
  
        	
        NondominatedPopulation result = new Executor()
            //.withProblemClass(SimulationBasedTransitOptimizationProblem.class)
        	.withSameProblemAs(instrumenter)
            .withAlgorithm("NSGAII")
            .withProperty("operator", "MyCrossover+MyMutation")
            .withProperty("MyCrossover.Rate", 0.75)
            .withProperty("MyMutation.Rate", 0.25)
            .withProperty("populationSize", 2)
            .withMaxEvaluations(MAX_MOEA_EVALUATIONS)
            .withInstrumenter(instrumenter)
            .run();
        
        //Accumulator acc = new Accumulator();
        Accumulator acc = instrumenter.getLastAccumulator();
        
        
        
        new Plot()
            .add("NSGAII", result)
            .setXLabel("User-Cost")
            .setYLabel("Operator-Cost")
            //.add(acc)
            .show();
        
        new Plot()
        .add(acc)
        .show();
        
        System.out.println(acc.toCSV());
     
        StringBuilder sb = new StringBuilder();
        
        RunSimulationBasedTransitOptimizationProblem rsbtop = new RunSimulationBasedTransitOptimizationProblem();
         
        System.out.println("Size of Pareto front is:" + " " + sb.append(result.size()));
        System.out.println("Evaluate called " + SimulationBasedTransitOptimizationProblem.callsToEvaluate + " times...");
        
        
        for (Solution solution : result) {
        	
        	rsbtop.encode(solution.getVariable(0));
        	//System.out.print(rsbtop.encode(solution.getVariable(0)));
        	
        	String xml = XML.toString(rsbtop.encode(solution.getVariable(0)));
        	
//        	try (PrintWriter out = new PrintWriter("filename.xml")) {
//        		//System.out.println(xml);
//        	}
        	
        	//FOS.write(String.format(xml));
        	
        	//System.out.print(solution.getVariable(0));
        	//System.out.println();
        	//sb.append(solution.toString());
        	System.out.println();
            System.out.format("%.4f      %.4f%n", solution.getObjective(0), solution.getObjective(1));
            FOS.write(String.format("\n%.4f      %.4f%n", solution.getObjective(0), solution.getObjective(1)).getBytes());
            FOS.write("\nObjective1  Objective2%n".getBytes());
            
        }
              
        Date endTime = new Date();
        
        long timeDiff = endTime.getTime() - startTime.getTime();
      
        //long end = System.currentTimeMillis();
       
        int durationInMilliseconds = (int) (timeDiff);
       
        int seconds = (int) (durationInMilliseconds / 1000) % 60 ;
        int minutes = (int) ((durationInMilliseconds / (1000*60)) % 60);
        int hours   = (int) ((durationInMilliseconds / (1000*60*60)) % 24);
       
        System.out.println("hours: " + hours + " " + "minutes: " + minutes + " " +  "seconds: " + seconds);
                    
        FOS.write("\nend time is:".getBytes());
        FOS.write(endTime.toString().getBytes());
        FOS.write(String.format("\nEvaluate is called %d  times...", SimulationBasedTransitOptimizationProblem.callsToEvaluate).getBytes());
        FOS.write(String.format("\nDuration: %02d:%02d:%02d", hours, minutes, seconds).getBytes());
               
    }
	

}
