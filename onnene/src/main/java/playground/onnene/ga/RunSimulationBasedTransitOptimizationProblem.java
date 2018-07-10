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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;


/**
 * This class runs the optimisation problem 
 * 
 * @author Onnene
 *
 */
public class RunSimulationBasedTransitOptimizationProblem {
    
    private static final int MAX_MOEA_EVALUATIONS = 3;
    public static final int MATSIM_ITERATION_NUMBER = 10;
    
    private static FileOutputStream FOS;
    static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");   
    static Date startTime = new Date();
    
    static {
        try {
            FOS = new FileOutputStream(new File("./output/logs/run_moea_log.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args) throws Exception {
    	
    	runSimulation("./output/optimisationResults/");
    }
    

    private void decodeResult(Variable variable, String resultFilePath, int folderNum, int fileNum) throws IOException {

        if (variable instanceof  DecisionVariable) {
               
               DecisionVariable varObj = (DecisionVariable) variable;               
               String resultFileName = "Solution" + fileNum + ".xml";                   
               String innerFolderStr = resultFilePath + folderNum + File.separator;             
               Path innerFolder = Files.createDirectories(Paths.get(innerFolderStr));               
               String paretoResultFolderPath = innerFolder + File.separator + resultFileName;
               ProblemUtils.getXMLFromJSONDecisionVar(varObj.getTransitSchedule(), paretoResultFolderPath);
               
        }
        
        else {
               
               throw new IOException("type not supported");
        }        
                    
    }

    public static void runSimulation(String ResultFolder) throws Exception {
    	
    	ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
    	Problem problem = ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimizationProblem");     
        OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());    
        
        Instrumenter instrumenter = new Instrumenter();       	
            instrumenter.withProblem(problem);
        	instrumenter.withFrequency(1);
        	instrumenter.attachElapsedTimeCollector();
        	
		List<NondominatedPopulation> result = new Executor()
        	.withSameProblemAs(instrumenter)
            .withAlgorithm("NSGAII")
            .withProperty("operator", "MyCrossover+MyMutation")
            .withProperty("MyCrossover.Rate", 0.75)
            .withProperty("MyMutation.Rate", 0.25)
            .withProperty("populationSize", 3)
            .withMaxEvaluations(MAX_MOEA_EVALUATIONS)        
            .withInstrumenter(instrumenter)
            .runSeeds(3);

        Accumulator acc = instrumenter.getLastAccumulator();
                         
        System.out.println(acc.toCSV());
       
        RunSimulationBasedTransitOptimizationProblem rsbtop = new RunSimulationBasedTransitOptimizationProblem();
     
        System.out.println("Evaluate called " + SimulationBasedTransitOptimizationProblem.callsToEvaluate + " times...");
        
        int folderIdx = 0;
        
        FileUtils.deleteDirectory(new File(ResultFolder));

        for (NondominatedPopulation pop: result) {
        	
        	folderIdx++;
        	int fileIdx = 0;

       	 	System.out.println("Size of Pareto front is:" + " " + pop.size());
  
        	for (Solution solution : pop) {
        		 
        		fileIdx++;
        		 //rsbtop.decodeResult(solution.getVariable(0), DirectoryConfig.RESULTS_FILE, folderIdx, fileIdx);
             	 rsbtop.decodeResult(solution.getVariable(0), ResultFolder, folderIdx, fileIdx);
             	 
	             System.out.format("%.4f      %.4f%n", solution.getObjective(0), solution.getObjective(1));
	             FOS.write(String.format("\n%.4f      %.4f%n", solution.getObjective(0), solution.getObjective(1)).getBytes());
	             FOS.write("\nObjective1  Objective2%n".getBytes());
	                             
             }
        	
//  	      new Plot()
//  	      .add("NSGAII", pop)
//  	      .setXLabel("User-Cost")
//  	      .setYLabel("Operator-Cost")
//  	      //.add(acc)
//  	      .show();
        	
        	System.out.println();
        }
        
       
        Date endTime = new Date();        
        long timeDiff = endTime.getTime() - startTime.getTime();   
        int durationInMilliseconds = (int) (timeDiff);
       
        int seconds = (int) (durationInMilliseconds / 1000) % 60 ;
        int minutes = (int) ((durationInMilliseconds / (1000*60)) % 60);
        int hours   = (int) ((durationInMilliseconds / (1000*60*60)) % 24);
       
        System.out.println("hours: " + hours + " " + "minutes: " + minutes + " " +  "seconds: " + seconds);
                    
        FOS.write("\nend time is:".getBytes());
        FOS.write(endTime.toString().getBytes());
        FOS.write(String.format("\nEvaluate is called %d times...", SimulationBasedTransitOptimizationProblem.callsToEvaluate).getBytes());
        FOS.write(String.format("\nDuration: %02d:%02d:%02d", hours, minutes, seconds).getBytes());

    }
	

}
