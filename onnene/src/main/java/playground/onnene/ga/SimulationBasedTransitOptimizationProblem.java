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

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.Header;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

import playground.onnene.exampleCode.RunScoringFunctions;

/**
 * This class implements our simulation based optimisation problem
 * 
 * @author Onnene
 *
 */
public class SimulationBasedTransitOptimizationProblem extends AbstractProblem {
	
	private Logger LOGGER = Logger.getLogger(SimulationBasedTransitOptimizationProblem.class.getName());   
    private static FileOutputStream FOS;
    public static int callsToEvaluate = 0;
    
    static {
        try {
        	FOS = new FileOutputStream(new File("./output/logs/transit_problem_log.txt"));
          
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
     
    public SimulationBasedTransitOptimizationProblem() throws FileNotFoundException {
        super(1, 2);
    }


    @Override
    public void evaluate(Solution solution) {
    
    	DecisionVariable var = (DecisionVariable) solution.getVariable(0);
        System.out.println("Number of MOEA Evaluations is " + callsToEvaluate++);        
        JSONObject Jvar = var.getTransitSchedule();
        String tScheduleFile = "./input/matsimInput/transitSchedule.xml";
        
        try {
        	
			ProblemUtils.getXMLFromJSONDecisionVar(Jvar, tScheduleFile);
			FOS.write("\nMOEA evaluate(...) function called".getBytes());
			
			} catch (IOException e2) {
				
				e2.printStackTrace();
			}
        
        try {
        	
			FOS.write("\nMOEA evaluate(...) function called".getBytes());
			
			} catch (IOException e1) {
				
				e1.printStackTrace();
			
			}
            
        LOGGER.debug("\nMOEA evaluate(...) function called".getBytes());
        
        String matsimOutputFolderPath = "./output/matsimOutput/"+ callsToEvaluate + "\\";
   
        runMatsim("./input/matsimInput/config.xml", matsimOutputFolderPath);
        try {
        	
            double[] objectives = processScoreFiles(matsimOutputFolderPath);
            
            for (int i = 0; i < objectives.length; i++) {
            
            	solution.setObjective(i, objectives[i]);
          }
                       
        } catch (Exception e) {
        	
            try {
                FOS.write(("\nError while processing outputFiles " + e.getMessage()).getBytes());
                FOS.flush();

            } catch (IOException e1) {
               e1.printStackTrace();
            }
            System.err.println(e.getMessage());

        }

    }
    

    @Override
    public Solution newSolution() {
    	
    	Solution solution = new Solution(1, 2);   	
        solution.setVariable(0, new DecisionVariable());      
        return solution;
    }
    
    public void runMatsim(String configFile, String matsimOutputDirectory) {

        try {
            FOS.write("\nrunMatsim(...) function called".getBytes());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        
        Config config = ConfigUtils.loadConfig(configFile);
        config.controler().setLastIteration(RunWithOwnSeed.MATSIM_ITERATION_NUMBER);       
        config.controler().setOutputDirectory(matsimOutputDirectory);       
        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setWriteEventsInterval(50);
       

        Scenario scenario = ScenarioUtils.loadScenario(config);
        Controler controler = new Controler(scenario);
        
        /*Code stub to implement SBB Raptor router
         * get back to it when the dependency is resolved
         * */
//        controler.addOverridingModule(new AbstractModule() {
//			@Override
//			public void install() {
//				// To use the deterministic pt simulation:
//				install(new SBBQSimModule());
//
//				// To use the fast pt router:
//				install(new SwissRailRaptorModule());
//			}
//		});
        
        controler.run();
    }
       
	private double[] processScoreFiles(String outputFolderPath) throws Exception {
		
		String eventsFile = outputFolderPath + "output_events.xml.gz";
		String userScoreOutputFile = "./output/ScoringFunctionResults/user_score.csv";
		String operatorScoreOutputFile = "./output/ScoringFunctionResults/operator_score.csv";
		String networkFile = "./input/matsimInput/transitNetwork.xml";
		
		String[] str = {eventsFile, userScoreOutputFile, operatorScoreOutputFile, networkFile};
		
		Header.printHeader(RunScoringFunctions.class, str);
		
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());	
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
		
		EventsManager manager = new EventsManagerImpl();		
		manager.addHandler(new NetworkUserScoringFunction(userScoreOutputFile));	
		manager.addHandler(new NetworkOperatorScoringFunction(operatorScoreOutputFile, scenario.getNetwork()));
		new MatsimEventsReader(manager).readFile(eventsFile);

		double[] obj = {NetworkUserScoringFunction.getUserScore(), NetworkOperatorScoringFunction.getOperatorScore()};
	    	
		Header.printFooter();
		
		return obj;
  	  	
	    }
	

}
