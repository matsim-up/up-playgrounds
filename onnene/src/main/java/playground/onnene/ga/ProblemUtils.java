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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.moeaframework.core.PRNG;
import org.xml.sax.SAXException;

import playground.onnene.transitScheduleMaker.FileMakerUtils;
import playground.onnene.transitScheduleMaker.TransitSchedule;



/**
 * Some utility methods used in the GA
 * 
 * @author Onnene
 *
 */
public class ProblemUtils{
	
	private List<File> transitScheduleFiles;
	
	public ProblemUtils() {
		this.transitScheduleFiles = fetchTransitScheduleFilesPath();
	}

	public ProblemUtils(String folder) {
		this.transitScheduleFiles = fetchTransitScheduleFilesPath(folder);
	}
	
	public List<File> getTransitScheduleFiles(){
		return this.transitScheduleFiles;
	}
    
    public JSONObject getRandomTransitSchedule() {
    	File transitScheduleXmlFile = PRNG.nextItem(transitScheduleFiles);
        String transitScheduleXmlStr = convertXMLFileIntoString(transitScheduleXmlFile);
        return XML.toJSONObject(transitScheduleXmlStr);
    }
    
    public List<File> fetchTransitScheduleFilesPath(String folder){
    	
    	List<File> files = new ArrayList<File>();    	
    	File InitialPopDirectory = new File(folder);   	
    	
    	if ((!InitialPopDirectory.exists()) || (!InitialPopDirectory.isDirectory()))
    		throw new RuntimeException("Directory doesn't exists: " + folder);
    	
    	for (File file : InitialPopDirectory.listFiles()) {
    		
    		if (file.isDirectory())
    			continue;
    		
    		if (file.getName().startsWith("transitSchedule"))
    			files.add(file);
    	}
    	
    	return files;    	
    }
    
    
    public List<File> fetchTransitScheduleFilesPath() {
    	return(fetchTransitScheduleFilesPath("./input/initialPop/"));
    }
    
    
	public File selectTransitScheduleXMLFileRandomly() {
		/* For deterministic behaviour, the files must be SORTED first. */
		Collections.sort(this.transitScheduleFiles);
        return transitScheduleFiles.get(PRNG.nextInt(transitScheduleFiles.size()));
    }
    
    
    private String convertXMLFileIntoString(File xmlFile) {
        StringBuilder sb = new StringBuilder();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(xmlFile));
            String line;
            while((line=br.readLine()) != null){
                sb.append(line.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    
    
    public static JSONArray getTransitLines(DecisionVariable v) {
        return getTransitLines(v.getTransitSchedule());
    }
    
       
    public static JSONArray getTransitLines(JSONObject jsonObj) {
        return jsonObj.getJSONObject("transitSchedule").getJSONArray("transitLine");
    }
    
         
    public static void getXMLFromJSONDecisionVar(JSONObject jObject, String outputFile) throws IOException {
    	
    	int index = 0;   	
    	int numLines = FileMakerUtils.count("./input/transitScheduleMakerHelperFiles/transitLineList.txt");
    	List<List<Integer>> routefromJsonLink = new ArrayList<List<Integer>>();
    	Collection<String> collection = new ArrayList<>();
    		
    	TransitSchedule TS = new TransitSchedule(); 
    	
    	JSONArray transitLine = getTransitLines(jObject);
    	
    	for (int i = 0; i < transitLine.length(); i++) {
    		
    		JSONArray jsonStops = transitLine.getJSONObject(i).getJSONArray("transitRoute").getJSONObject(0).getJSONObject("routeProfile").getJSONArray("stop");
    		List<Integer> temp = new ArrayList<Integer>();
    		
        	for (int j = 0; j < jsonStops.length(); j++) {
    		
	    		JSONObject jsonStop = jsonStops.getJSONObject(j);
	    		int stop = jsonStop.getInt("refId");		
	    		temp.add(stop);
	    		
        	}
        	
        	routefromJsonLink.add(temp);  	
       
    	}
    	 	
    	String routesStr = Arrays.toString(routefromJsonLink.toArray()).toString();
	    String[] arr = routesStr.split("],");
	   
    	for (String a: arr){
    		
	    	collection.add(a);
    	}
    	   	
    	try {
    		
    		TS.createTransitScheduleXML(collection, index, numLines, outputFile);
		
		} catch (IOException | TransformerException | ParserConfigurationException | SAXException e1) {

			e1.printStackTrace();
		}
    	
    }
    
   
    
    public static int numberOfLines() {
    	
    	int numlines = 0;
    	
    	try {
    		
    		numlines = FileMakerUtils.count("./input/transitScheduleMakerHelperFiles/transitLineList.txt");
    		
    		} catch (IOException e) {
    			
    			e.printStackTrace();
    		}
    	
		return numlines;
    }
    
    public static String generateRandomStringToken(int byteLength) {
    	
    	SecureRandom sr = new SecureRandom();
    	byte[] token = new byte[byteLength];
    	sr.nextBytes(token);
    	
    	return new BigInteger(1, token).toString(16);
    }
    
}
