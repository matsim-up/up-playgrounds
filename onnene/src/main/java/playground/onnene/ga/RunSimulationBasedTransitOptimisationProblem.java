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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * @author Onnene
 *
 */
public class RunSimulationBasedTransitOptimisationProblem {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {	

		runSimulation(10, 35);
	}
	
	static long start = System.currentTimeMillis();
	
    private static final int MAX_MOEA_EVALUATIONS = 5000;
    public static final int MATSIM_ITERATION_NUMBER = 100;
    private static FileOutputStream MOEA_LOG_FILE, INDICATOR_FILE, REFERENCE_SET_FILE, REFERENCE_SET_FILE_PF_FORMAT, SEED_FILE;
    static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");   
    static Date startTime = new Date();
    //static String ResultFolder = "./output/optimisationResults/";
    
    static {
        try {
            MOEA_LOG_FILE = new FileOutputStream(new File("./output/logs/run_moea_log.txt"));
            REFERENCE_SET_FILE = new FileOutputStream(new File("./output/ProblemReferenceSet/referenceSet.txt"), true);
            REFERENCE_SET_FILE_PF_FORMAT = new FileOutputStream(new File("./output/ProblemReferenceSet/referenceSet.pf"), true);
            SEED_FILE = new FileOutputStream(new File("./output/logs/seeds.txt"), true);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
        
    private static File checkPoint(String checkPointFile) {
    	
    	File checkpointFile = new File (checkPointFile);
    	
    	if (checkpointFile.exists()) {
    		
    		System.out.println("Checkpoint file exists, will resume from prior run!");
    	}
		return checkpointFile;
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

    public static void runSimulation(int numSeeds, int numThreads) throws Exception {
    	
    	File checkPointFile = checkPoint("./output/logs/checkpoint.dat");
    	String ResultFolder = "./output/optimisationResults/";
    	
    	List<NondominatedPopulation> result = new ArrayList<>();
        List<Long> lstOfSeeds = new ArrayList<>();
        Long seed = null; 
    	
    	ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
    	Problem problem = ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimizationProblem");     
        OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());  
        // String[] algorithms = { "NSGAII" }; 
        
       
        Instrumenter instrumenter = new Instrumenter();       	
            instrumenter.withProblem(problem);
        	instrumenter.withFrequency(100);
        	instrumenter.attachApproximationSetCollector();
        	instrumenter.attachElapsedTimeCollector();
        		        		        	
        for (int i=0; i < numSeeds; i++) {
       
        	//seed = (long)(PRNG.nextInt(Integer.MAX_VALUE-1)); 
        	seed = (long)(PRNG.nextInt());       	      	
	        PRNG.setSeed(seed);
			NondominatedPopulation offSpring = new Executor()
	        	.withSameProblemAs(instrumenter)
	            .withAlgorithm("NSGAII")
	            .withProperty("operator", "MyCrossover+MyMutation")
	            .withProperty("MyCrossover.Rate", 0.75)
	            .withProperty("MyMutation.Rate", 0.25)
	            .withProperty("populationSize", 100)
	            .withMaxEvaluations(MAX_MOEA_EVALUATIONS)  
	            //.resetCheckpointFile()
	            .withCheckpointFile(checkPointFile)		  
	            .withCheckpointFrequency(100)
	            .withInstrumenter(instrumenter)     
	            .distributeOn(numThreads)
	            .run();
			result.add(offSpring);
			lstOfSeeds.add(seed);
        }
	      
        for (int i = 0; i < lstOfSeeds.size(); i++) {
        	
        	SEED_FILE.write(String.format("Seed%d: %d ",i+1, lstOfSeeds.get(i)).getBytes());
        	
        }
       
        RunSimulationBasedTransitOptimisationProblem rwos = new RunSimulationBasedTransitOptimisationProblem();
     
        System.out.println("Evaluate called " + SimulationBasedTransitOptimizationProblem.callsToEvaluate + " times...");
        
        int folderIdx = 0;
        
          
        //FileUtils.deleteDirectory(new File("./output/optimisationResults/"));
        FileUtils.deleteDirectory(new File(ResultFolder));
       	       
        for (NondominatedPopulation pop: result) {
        	
        	folderIdx++;
        	int fileIdx = 0;
        	
        	MOEA_LOG_FILE.write("\nObjective1  Objective2".getBytes());
       	 	System.out.println("Size of Pareto front is:" + " " + pop.size());
  
        	for (Solution solution : pop) {
        		 
        		fileIdx++;
        		 //rsbtop.decodeResult(solution.getVariable(0), DirectoryConfig.RESULTS_FILE, folderIdx, fileIdx);
        		 rwos.decodeResult(solution.getVariable(0), ResultFolder, folderIdx, fileIdx);
        		 //MOEA_LOG_FILE  = new FileOutputStream(new File("./output/logs/run_moea_log.txt"), true);
	             System.out.format("%.4f      %.4f%n", solution.getObjective(0), solution.getObjective(1));	            
	             MOEA_LOG_FILE.write(String.format("%.4f      %.4f", solution.getObjective(0), solution.getObjective(1)).getBytes());
	             REFERENCE_SET_FILE.write(String.format("%.4f      %.4f", solution.getObjective(0), solution.getObjective(1)).getBytes());
	             REFERENCE_SET_FILE_PF_FORMAT.write(String.format("%.4f      %.4f", solution.getObjective(0), solution.getObjective(1)).getBytes());
             }
        	
        	Map<String, Accumulator> results = null;
        	
        	if (pop.size() == 1){
	 					 				 
 				 System.out.println("requires at least two solutions");
 				 
 			} else {
        	
        	results = new HashMap<String, Accumulator>();
 				
 			results.put("NSGAII", instrumenter.getLastAccumulator());
 							
 	        QualityIndicator qi = new QualityIndicator(problem, pop);
 	        	        
			Accumulator accumulator = results.get("NSGAII");
			
			//System.out.print(accumulator.toCSV());
			try {
				accumulator.saveCSV(new File("./output/optimisationResults/runtimeResults.csv"));
			} catch (IOException e) {
				
				e.printStackTrace();
			}
 								
			for (int i = 0; i < accumulator.size("NFE"); i++) {
					
 				List<Solution> approximationSet = (List<Solution>)accumulator.get("Approximation Set", i);
 				qi.calculate(new NondominatedPopulation(approximationSet));
 				
 				//FOS = new FileOutputStream(new File("./output/logs/run_moea_log.txt"));
 				System.out.print("    ");
 				System.out.print(accumulator.get("NFE", i));
 				System.out.print(" ");
 				System.out.println();
 		
 				INDICATOR_FILE = new FileOutputStream(new File(ResultFolder+folderIdx + File.separator + "indicators.txt"), true);
 				
 				
 				//INDICATOR_FILE.write("NFE    HV   GD    AEI    IGD    S    MPE".getBytes());	
 				INDICATOR_FILE.write(String.format("\n%d    %.8f    %.8f    %10.8f    %10.8f   %10.8f    %10.8f", accumulator.get("NFE", i), qi.getHypervolume(), qi.getGenerationalDistance(), qi.getAdditiveEpsilonIndicator(), qi.getInvertedGenerationalDistance(), qi.getSpacing(), qi.getMaximumParetoFrontError()).getBytes());
 				//INDICATOR_FILE.write(String.format("%n").getBytes());
 				//INDICATOR_FILE.write(String.format(" ", qi.getHypervolume()).getBytes());
 				//INDICATOR_FILE.write("Generational Distance".getBytes());
 				//INDICATOR_FILE.write(String.format("\nGD%f ", qi.getGenerationalDistance()).getBytes());
 				//INDICATOR_FILE.write("\nAdditive Epsilon Indicator".getBytes());
 				//INDICATOR_FILE.write(String.format("\nAEI%fn", qi.getAdditiveEpsilonIndicator()).getBytes());
 				//qi.getAdditiveEpsilonIndicator();
 			}
			
				
				System.out.println();
 					
			}
 	            	
      
        }
        
        
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start)/1000 + "s") ;
	
        Date endTime = new Date();        
        long timeDiff = endTime.getTime() - startTime.getTime();   
        int durationInMilliseconds = (int) (timeDiff);
       
        int seconds = (int) (durationInMilliseconds / 1000) % 60 ;
        int minutes = (int) ((durationInMilliseconds / (1000*60)) % 60);
        int hours   = (int) ((durationInMilliseconds / (1000*60*60)) % 24);
       
        System.out.println("hours: " + hours + " " + "minutes: " + minutes + " " +  "seconds: " + seconds);
                    
        MOEA_LOG_FILE.write("\nend time is:".getBytes());
        MOEA_LOG_FILE.write(endTime.toString().getBytes());
        MOEA_LOG_FILE.write(String.format("\nEvaluate is called %d times...", SimulationBasedTransitOptimizationProblem.callsToEvaluate).getBytes());
        MOEA_LOG_FILE.write(String.format("\nDuration: %02d:%02d:%02d", hours, minutes, seconds).getBytes());

    }

}
