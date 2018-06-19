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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements our simulation based optimisation problem
 * 
 * @author Onnene
 *
 */
public class SimulationBasedTransitOptimizationProblem extends AbstractProblem {
	
	
private Logger LOGGER = Logger.getLogger(SimulationBasedTransitOptimizationProblem.class.getName());
    
    private static FileOutputStream FOS;
    public static  int currentMOEAEvaluationNumber = 0;
    
    public static int callsToEvaluate = 0;
    
    static {
        try {
            FOS = new FileOutputStream(new File(DirectoryConfig.TRANSIT_PROBLEM_LOG_FILE_PATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
     
    public SimulationBasedTransitOptimizationProblem() throws FileNotFoundException {
        super(1, 2);
    }


    @Override
    public void evaluate(Solution solution) {
    	
    	//System.out.println(callsToEvaluate++);

    	DecisionVariable var = (DecisionVariable) solution.getVariable(0);

        
        //System.out.println("Number of MOEA Evaluations is " + currentMOEAEvaluationNumber++);
        System.out.println("Number of MOEA Evaluations is " + callsToEvaluate++);
        
        JSONObject Jvar = var.getTransitSchedule();
        
        String tScheduleFile = DirectoryConfig.TRANSIT_SCHEDULE_FILE;
        
        
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
        

        String matsimOutputFolderPath = DirectoryConfig.MATSIM_OUTPUT_FOLDER + callsToEvaluate++ + "\\";

        runMatsim(DirectoryConfig.CONFIG_FILE, matsimOutputFolderPath);
        
        try {
        	
            double[] objectives = processOutputFiles(matsimOutputFolderPath);
            
            //System.out.print(Arrays.toString(objectives));
            
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
        config.controler().setLastIteration(DirectoryConfig.MATSIM_ITERATION_NUMBER);       
        config.controler().setOutputDirectory(matsimOutputDirectory);       
        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

        Scenario scenario = ScenarioUtils.loadScenario(config);
        Controler controler = new Controler(scenario);
        controler.run();
    }
    
    

    @SuppressWarnings("unused")
	private void printObjectives(Objectives objectives) {
    	
    
        System.out.println("\n=========================");
        System.out.println("Total time objective: " + objectives.objectives[0]);
        System.out.println("Second objective: " + objectives.objectives[1]);
        System.out.println("=========================");
        
        try {
            FOS.write("\n=========================".getBytes());
            FOS.write(("Total time objective: " + objectives.objectives[0]).getBytes());
            FOS.write(("Second objective: " + objectives.objectives[1]).getBytes());
            FOS.write("=========================".getBytes());
            FOS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	private double[] processOutputFiles(String outputFolderPath) throws Exception {
				
		//String eventsFile = args[0];
		//String userScoreOutputFile = args[1];
		//String networkFile = args[2];
		//String operatorScoreOutputFile = args[3];	
		
		//String iterationFolderPath = outputFolderPath + iterationNumber + "ITERS\\;
		String eventsFile = outputFolderPath + "output_events.xml.gz";
		String userScoreOutputFile = DirectoryConfig.USER_SCORING_FUNCTION_FILE;
		String operatorScoreOutputFile = DirectoryConfig.OPERATOR_SCORING_FUNCTION_FILE;
		String networkFile = DirectoryConfig.TRANSIT_NETWORK_FILE;
		
		//Score Function 2
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());	
//		MatsimNetworkReader mnr = new MatsimNetworkReader(scenario.getNetwork());
//		//MatsimNetworkReader mnr1 = new MatsimNetworkReader(scenario.getNetwork());
		
//		mnr.readFile(networkFile);
		
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
		
		//Score Function 1
		EventsManager manager = new EventsManagerImpl();		
		manager.addHandler(new NetworkUserScoringFunction(userScoreOutputFile));	
		manager.addHandler(new NetworkOperatorScoringFunction(operatorScoreOutputFile, scenario.getNetwork()));
		
		new MatsimEventsReader(manager).readFile(eventsFile);
		//manager.addHandler(new CarTravelDistanceEvaluator(scenario.getNetwork()));
		//manager.addHandler(new PtTravelDistanceCalculator(scenario.getNetwork()));
		
//		EventsManager eventsManager = EventsUtils.createEventsManager();
//		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());		
//		new MatsimNetworkReader(scenario.getNetwork()).readFile("input/network.xml");
		
//		PtTravelDistanceCalculator PtTravelDistanceEvaluator = new PtTravelDistanceCalculator(scenario.getNetwork());
//		
//		System.out.println(Arrays.toString(PtTravelDistanceEvaluator.getDistanceDistribution()));
//		
		
		
		//NetworkUserScoringFunction nuc = new NetworkUserScoringFunction(output);
		//List<Double> totalVehicleTime = NetworkUserScoringFunction.getUsersTimeList();
		
		//double[] obj = {totalTimeObjective.doubleValue(), secondObjective.doubleValue()};
		
//		double aaa = NetworkUserScoringFunction.getUserScore();
//		
//		
//		double bbb = NetworkOperatorScoringFunction.getOperatorScore();
		
		double userScore = NetworkUserScoringFunction.getUserScore();
		double opScore = NetworkOperatorScoringFunction.getOperatorScore();
		
		double[] obj = {userScore, opScore};
		
//		double sum = 0.0;
//		
//		for(Double tvt: totalVehicleTime) {
//			
//			sum += tvt;
//		}
//		System.out.println("user score is " + aaa + " minutes");
//		System.out.println("operator score is " + bbb + " rands");
		
		System.out.println(Arrays.toString(obj));
	    	
	    	    	
		return obj;
  	  	
	    }

    private double[] processOutputFiles1(String outputFolderPath, int iterationNumber) throws Exception {
    	
        String iterationFolderPath = outputFolderPath + "ITERS\\it." + iterationNumber + "\\";

        File eventsZipFile = new File(iterationFolderPath + iterationNumber + ".events.xml.gz");
        File eventsFile = new File(iterationFolderPath + iterationNumber + ".events.xml");
        if (!eventsZipFile.exists()) {
            throw new Exception(eventsZipFile.getAbsolutePath() + " doesn't exists");
        }
        if (eventsFile.exists())
            eventsFile.delete();
        eventsFile.createNewFile();

        File linkStatsZipFile = new File(iterationFolderPath + iterationNumber + ".linkstats.txt.gz");
        File linkStatsFile = new File(iterationFolderPath + iterationNumber + ".linkstats.txt");
        if (!linkStatsZipFile.exists()) {
            throw new Exception(linkStatsZipFile.getAbsolutePath() + " doesn't exists");
        }
        if (linkStatsFile.exists())
            linkStatsFile.delete();
        linkStatsFile.createNewFile();
        
        gUnzip(eventsZipFile, eventsFile);
        gUnzip(linkStatsZipFile, linkStatsFile);

        Map<String, List<Event>> personToEventsMap = processEventsXmlFile(eventsFile, true);
        Map<String, Link> linksMap = processLinkStatsFile(linkStatsFile);
        
        // Calculate total time objective
        BigDecimal totalTimeObjective = getTotalTimeObjective(personToEventsMap, linksMap);
        
        // Calculate second objective
        BigDecimal secondObjective = getsecondObjective(eventsFile, linksMap);
        
        System.out.println("\n=========================");
        System.out.println("Total time objective: " + totalTimeObjective);
        
        System.out.println("\n=========================");
        System.out.println("Second objective: " + secondObjective);
      
        double[] obj = {totalTimeObjective.doubleValue(), secondObjective.doubleValue()};
        
        for (int i = 0; i < obj.length; i++) {
        	
        	System.out.println("obj" + i + obj[i]);
        	
        }
         
        return obj;
    }

    private BigDecimal getsecondObjective(File eventsFile, Map<String, Link> linksMap) throws ParserConfigurationException, SAXException, IOException {
        Map<String, List<Event>> personToEventsMap = processEventsXmlFile(eventsFile, false);
        
        List<Event> events = new ArrayList<>();
        for (List<Event> list : personToEventsMap.values()) {
            events.addAll(list);
        }
        
        BigDecimal nVehicles = new BigDecimal(events.size());
        System.out.println("nVehicles: " + nVehicles);
        
        // Calculate duration
        BigDecimal smallestTime = new BigDecimal(0);
        BigDecimal biggestTime = new BigDecimal(0);
        for (Event event : events) {
            if (event.time.compareTo(smallestTime) == -1)
                smallestTime = event.time;
            
            if (event.time.compareTo(biggestTime) == +1)
                biggestTime = event.time;
        }
        BigDecimal durationInHr = biggestTime.subtract(smallestTime).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
        System.out.println("Duration (in Hours): " + durationInHr);
        
        // Calculate total link length
        BigDecimal totalLinkLength = new BigDecimal(0);
        Link link;
        for (Event event : events) {
            link = linksMap.get(event.link);
            totalLinkLength = totalLinkLength.add(link.length);
        }
        totalLinkLength = totalLinkLength.divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
        System.out.println("Total link length (in km): " + totalLinkLength);
        
        // Calculate objective
        BigDecimal secondObjective = (nVehicles.divide(durationInHr, 2, RoundingMode.HALF_UP)).multiply(totalLinkLength);
        
        return secondObjective;
    }

    private BigDecimal getTotalTimeObjective(Map<String, List<Event>> personToEventsMap, Map<String, Link> linksMap) {
        BigDecimal totalTimeObjectiveMin = new BigDecimal(0);
        BigDecimal totalTimeObjectiveHr = new BigDecimal(0);
        for (String person : personToEventsMap.keySet()) {
            
            List<Event> events = personToEventsMap.get(person);
            
            BigDecimal time = new BigDecimal(0);
            for (Event event : events) {
                Link link = linksMap.get(event.link);
                time = time.add(link.avgAvg10_11);
                totalTimeObjectiveMin = totalTimeObjectiveMin.add(link.avgAvg10_11);
            }
            
        }
        System.out.println("Total time for all passengers in minutes is " + totalTimeObjectiveMin);
        
        totalTimeObjectiveHr = totalTimeObjectiveMin.divide(new BigDecimal(3600.0), 2, RoundingMode.HALF_UP);
        System.out.println("Total time for all passengers in hours is " + totalTimeObjectiveHr);
        
        return totalTimeObjectiveHr;
    }
    

    public void gUnzip(File gZipFile, File outputFile) {

        byte[] buffer = new byte[1024];

        try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(gZipFile));
            FileOutputStream out = new FileOutputStream(outputFile);) {

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Map<String, List<Event>> processEventsXmlFile(File xmlFile, boolean totalTimeObjective) throws ParserConfigurationException, SAXException, IOException {
        
        Map<String, List<Event>> events = new HashMap<>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("event");
            
            System.out.println("Nodelist size: " + nodeList.getLength());
            
            //now XML is loaded as Document in memory, lets convert it to Object List
            Node eventNode;
            for (int i = 0; i < nodeList.getLength(); i++) {
                
                eventNode = nodeList.item(i);
                
                Event event;
                if (totalTimeObjective)
                    event = getEventsForTotalTimeObjective(eventNode);
                else
                    event = getEventsForSecondObjective(eventNode);
                
                if (event != null) {
                    List<Event> list = events.get(event.person);
                    if (list == null) {
                        list = new ArrayList<>();
                        events.put(event.person, list);
                    }
                    list.add(event);
                }
            }
            
            
        return events;
    }
    
    private Map<String, Link> processLinkStatsFile(File linkStatsFile) throws Exception {
        Map<String, Link> linksMap = new HashMap<>();
        
        try(BufferedReader br = new BufferedReader(new FileReader(linkStatsFile))) {

            String line;
            String[] columns;
            
            // process header line
            line = br.readLine();
            columns = line.split("\\s+");

            while ((line = br.readLine()) != null) {
                columns = line.split("\\s+");
                
                Link link = new Link();
                link.linkId = columns[0];
                link.avgAvg10_11 = new BigDecimal(columns[113]); // TRAVELTIME10-11avg column
                link.length = new BigDecimal(columns[3]); // link length
                linksMap.put(link.linkId, link);
                
            }
        }
        
        return linksMap;
    }
    
    private static Event getEventsForTotalTimeObjective(Node node) {
        //XMLReaderDOM domReader = new XMLReaderDOM();
        
        Event event = null;
      
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            
            Element element = (Element) node;
            String type = element.getAttribute("type");
            String legmode = element.getAttribute("legMode");
            if ("departure".equalsIgnoreCase(type) && "pt".equalsIgnoreCase(legmode)) {
                
                event = new Event();
                event.person = element.getAttribute("person");
                event.link = element.getAttribute("link");
            }

        }

        return event;
    }
    
    private static Event getEventsForSecondObjective(Node node) {
        //XMLReaderDOM domReader = new XMLReaderDOM();
        
        Event event = null;
        
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            
            Element element = (Element) node;
            String type = element.getAttribute("type");

            if ("vehicle enters traffic".equalsIgnoreCase(type) || "vehicle leaves traffic".equalsIgnoreCase(type)) {
                
                event = new Event();
                //event.type = type;
                event.link = element.getAttribute("link");
                //event.vehicles = element.getAttribute("vehicle");
                event.time = new BigDecimal(element.getAttribute("time"));
            }

        }

        return event;
    }


    private static class Event {
        //String type;
        String person;
        String link;

        //String vehicles;
        BigDecimal time;
        
    }
    
    private static class Link {
        String linkId;
        BigDecimal avgAvg10_11;
        BigDecimal length;
    }
    
    private static class Objectives {
        double[] objectives;
    }
	
	

}
