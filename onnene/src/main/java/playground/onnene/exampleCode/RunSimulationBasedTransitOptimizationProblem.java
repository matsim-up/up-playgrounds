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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.spi.OperatorFactory;
import org.xml.sax.SAXException;

import playground.onnene.ga.DecisionVariable;
import playground.onnene.ga.DirectoryConfig;
import playground.onnene.ga.GA_OperatorProvider;
import playground.onnene.ga.ProblemUtils;
import playground.onnene.ga.SimulationBasedTransitOptimizationProblem;
import playground.onnene.transitScheduleMaker.TransitSchedule;


/**
 * This class runs the optimisation problem 
 * 
 * @author Onnene
 *
 */
public class RunSimulationBasedTransitOptimizationProblem {
	
    //private static final Logger LOGGER = Logger.getLogger(RunSimulationBasedTransitOptimizationProblem.class);
    private static final int MAX_MOEA_EVALUATIONS = 100;
    //public static int callsToEvaluate = 0;
    
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
    
    private String encode(Variable variable, String resultFilePath) throws IOException {
    	
    	int index = 1;
    	
    	//TransitSchedule TS = new TransitSchedule();
    	
		StringBuilder sb = new StringBuilder();
		
		if (variable instanceof  DecisionVariable) {
			
			DecisionVariable obi = (DecisionVariable) variable;
			
			//sb.append(obi.getTransitSchedule());
			
			String paretoResultName = DirectoryConfig.LOG_FOLDER_PATH + "paretoSolution" + index++ + ".xml";
			
			try {
				TransitSchedule.string2Dom(TransitSchedule.JSON2XML(obi.getTransitSchedule().toString()), paretoResultName);
			} catch (JSONException | SAXException | ParserConfigurationException | TransformerException e) {
				
				e.printStackTrace();
			}
								
		} 
		
		else {
			
			throw new IOException("type not supported");
		}
		
		 //System.out.print(sb.length());
		
		return sb.toString();
	}
    
    
    private void decodeResult(Variable variable, String resultFilePath) throws IOException {
        //StringBuilder sb = new StringBuilder();
    	
    	File resultFolder = new File(resultFilePath);
        
        int index = 1;
       
        if (variable instanceof  DecisionVariable) {
               
               DecisionVariable varObj = (DecisionVariable) variable;
                             
               //return varObj.getTransitSchedule();
               
               
               if (resultFolder.listFiles() != null){
                   
	                   for (File file: resultFolder.listFiles()) {
	                         
	                         if (file.isFile()) {
	                                
	                                file.delete();
	                         }
	                   }
	                   
	                   String resultFileName = "pareto_solution" + index++ + ".xml";             
	                   String pareto_result_folder = DirectoryConfig.RESULTS_FILE + resultFileName;              
	                   ProblemUtils.getXMLFromJSONDecisionVar(varObj.getTransitSchedule(), pareto_result_folder);
               }
               
               //ProblemUtils.getXMLFromJSONDecisionVar(solution.getVariable(0))
               
        }
        
        else {
               
               throw new IOException("type not supported");
        }
        
                    
    }

    
    public static void main(String[] args) throws Exception {
    	
//    	File checkpointFile = new File(DirectoryConfig.CHECKPOINT_FILE);
//    	long start = System.currentTimeMillis();
//    	
//    	if (checkpointFile.exists()) {
//    		
//    		System.out.println("Checkpoint file exists, will resume from prior run!");
//    	}

        
    	//ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
    	//Problem problem = ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimizationProblem");
    	//Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
        //Problem problem = new SimulationBasedTransitOptimizationProblem();
        
        OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());
       
//        String[] algorithms = { "NSGAII", "GDE3" };
//        
//        Instrumenter instrumenter = new Instrumenter()
//				.withProblem(problem)
//				.attachApproximationSetCollector();
//		
//        Executor executor = new Executor()
//				.withProblem(problem)
//				.withMaxEvaluations(10000)
//				.withInstrumenter(instrumenter);
//		
//		  // Store the data and compute the reference set
// 		Map<String, Accumulator> results = new HashMap<String, Accumulator>();
// 		NondominatedPopulation referenceSet = new NondominatedPopulation();
// 		
// 		for (String algorithm : algorithms) {
// 			referenceSet.addAll(executor.withAlgorithm(algorithm).run());
// 			results.put(algorithm, instrumenter.getLastAccumulator());
// 		}
// 		
// 		// Calculate the performance metrics using the reference set
// 		QualityIndicator qi = new QualityIndicator(problem, referenceSet);
// 		
// 		for (String algorithm : algorithms) {
// 			Accumulator accumulator = results.get(algorithm);
// 			
// 			System.out.println(algorithm);
// 			
// 			for (int i = 0; i < accumulator.size("NFE"); i++) {
// 				List<Solution> approximationSet = (List<Solution>) accumulator.get("Approximation Set", i);
// 				qi.calculate(new NondominatedPopulation(approximationSet));
// 				
// 				System.out.print("    ");
// 				System.out.print(accumulator.get("NFE", i));
// 				System.out.print(" ");
// 				System.out.print(qi.getHypervolume());
// 				System.out.println();
// 			}
// 			
// 			System.out.println();
// 		}
    
        
        //PRNG.setSeed(2255);
        
        Instrumenter instrumenter = new Instrumenter();
        	
            instrumenter.withProblemClass(SimulationBasedTransitOptimizationProblem.class);
        	instrumenter.withFrequency(1);
        	instrumenter.attachElapsedTimeCollector();
//        	instrumenter.withReferenceSet(null);
//        	instrumenter.attachGenerationalDistanceCollector();
//        	instrumenter.attachHypervolumeCollector();
//        	instrumenter.attachPopulationSizeCollector();
  
//        	
        	int run = 1;
        	int threads = 2;
        	boolean plot = false;
        	
        	PRNG.setSeed(run * 20180620);
        	
		NondominatedPopulation result = new Executor()
            //.withProblemClass(SimulationBasedTransitOptimizationProblem.class)
        		.distributeOn(threads)
        	.withSameProblemAs(instrumenter)
            .withAlgorithm("NSGAII")
            .withProperty("operator", "MyCrossover+MyMutation")
            .withProperty("MyCrossover.Rate", 0.75)
            .withProperty("MyMutation.Rate", 0.25)
            .withProperty("populationSize", 15)
            //.withCheckpointFrequency(1)
            .withMaxEvaluations(MAX_MOEA_EVALUATIONS)
            //.withCheckpointFile(checkpointFile)           
            .withInstrumenter(instrumenter)
            .run();
        
        
        
        //Accumulator acc = new Accumulator();
        Accumulator acc = instrumenter.getLastAccumulator();
                
        if(plot) {
        	new Plot()
        	.add("NSGAII", result)
        	.setXLabel("User-Cost")
        	.setYLabel("Operator-Cost")
        	//.add(acc)
        	.show();
        	
        	new Plot()
        	.add(acc)
        	.show();
        }
              
        System.out.println(acc.toCSV());
     
        StringBuilder sb = new StringBuilder();
        
        RunSimulationBasedTransitOptimizationProblem rsbtop = new RunSimulationBasedTransitOptimizationProblem();
         
        System.out.println("Size of Pareto front is:" + " " + sb.append(result.size()));
        System.out.println("Evaluate called " + SimulationBasedTransitOptimizationProblem.callsToEvaluate + " times...");
        
        /*File resultFolder = new File(DirectoryConfig.RESULTS_FILE);
        
        int index = 0;
        */
        
        for (Solution solution : result) {
        	
        	//rsbtop.encode(solution.getVariable(0));
        	//System.out.print(rsbtop.encode(solution.getVariable(0)));
        	
        	//String xml = XML.toString(rsbtop.encode(solution.getVariable(0)));
        	
        	//rsbtop.encode(solution.getVariable(0), DirectoryConfig.LOG_FOLDER_PATH);
        	
        	rsbtop.decodeResult(solution.getVariable(0), DirectoryConfig.RESULTS_FILE);
        	
        	
        	/*if (resultFolder.listFiles() != null){
                
                for (File file: resultFolder.listFiles()) {
                      
                      
                      if (file.isFile()) {
                             
                             file.delete();
                      }
                }
                
                String resultFileName = "pareto_solution" + index++ + 1 + ".xml";             
                String pareto_result_folder = DirectoryConfig.RESULTS_FILE + resultFileName;              
                ProblemUtils.getXMLFromJSONDecisionVar(rsbtop.decode(solution.getVariable(0)), pareto_result_folder);
         }*/
        	
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
        FOS.write(String.format("\nEvaluate is called %d times...", SimulationBasedTransitOptimizationProblem.callsToEvaluate).getBytes());
        FOS.write(String.format("\nDuration: %02d:%02d:%02d", hours, minutes, seconds).getBytes());
               
        
        //System.out.println("Elapsed time: " + (System.currentTimeMillis() - start)/1000 + "s");
    }
	

}
