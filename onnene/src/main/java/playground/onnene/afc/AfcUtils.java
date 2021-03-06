/* *********************************************************************** *
 * project: org.matsim.*
 * AfcUtils.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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
package playground.onnene.afc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.matsim.core.utils.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class with various methods to handle and deal with the City of Cape
 * Town's automated fare collection (AFC) data.
 * 
 * @author jwjoubert
 */
public class AfcUtils {

	
	/**
	 * Parses the stops.txt file (from a GTFS feed) and builds a map linking 
	 * stop names to the stop IDs.
	 *  
	 * @param filename
	 * @return
	 */
	public static Map<String, Integer> parseStopIdFromGtfs(String filename){
		Map<String, Integer> stopMap = new TreeMap<>();
		
		BufferedReader br = IOUtils.getBufferedReader(filename);
		
		try{
			String line = br.readLine(); /* Header. */
			while((line = br.readLine()) != null){
				String[] sa = line.split(",");
				int stopId = Integer.parseInt(sa[5]);
				String stopName = sa[6];
				stopMap.put(stopName, stopId);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot read from " + filename);
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close " + filename);
			}
		}
		
		return stopMap;
	}
	
	
	/**
	 * Simplifies the raw data's transaction description to a single letter:
	 * 'B' for boarding; 'A' for alighting; and 'C' for connecting.
	 * @param s
	 * @return
	 */
	public static String getTransactionAbbreviation(String s){
		String abbreviation = null;
		if(s.equalsIgnoreCase("1st boarding")){
			abbreviation = "B";
		} else if(s.equalsIgnoreCase("Alighting")){
			abbreviation = "A";
		} else if(s.equalsIgnoreCase("Connection")){
			abbreviation = "C";
		} else{
			throw new RuntimeException("Don't know what transaction type '" + s + "' is.");
		}
		return abbreviation;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		int counter = 0;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();			
			
			Document transitSchedule = builder.parse("C:\\Users\\NNNOB\\Documents\\GitHub\\up-playgrounds\\onnene\\input\\AFC_data\\April_2016\\20160430_tripChains\\plan.xml");
			
			NodeList transitLines = transitSchedule.getElementsByTagName("transitLine");
			
			System.out.println(transitLines.getLength());
			
			for (int i = 0; i < transitLines.getLength(); i++) {
				
				Node tL = transitLines.item(i);
				
				if (tL.getNodeType() == Node.ELEMENT_NODE) {
					
					Element transitLine = (Element) tL;
					String transitLineId = transitLine.getAttribute("id");
					System.out.println(transitLineId);
					
					NodeList transitRoutes = transitLine.getChildNodes();
					//System.out.println(transitRoutes);
					
					
					
				}
				
			}
					
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	}
					
			
	
	
	/**
	 * The stop IDs form the AFC and GTFS data do not coincide. Some stops can
	 * be resolved by the stop's name alone. If that can not be done, this 
	 * class provides a manual mapping.
	 * 
	 * @param name of the stop as taken from the AFC data.
	 * @return
	 */
	public static int getGtfsStationIdFromName(String name){
		if(name.equalsIgnoreCase("Berg-en-dal")){
			return 295;
		} else if(name.equalsIgnoreCase("Birkenhead")){
			// FIXME Find stop.
			return 493;
		} else if(name.equalsIgnoreCase("Blaauwberg Hospital_")){
			return 13;
		} else if(name.equals("Castle")){
			return 158;
		} else if(name.equalsIgnoreCase("Cput")){
			return 34;
		} else if(name.equalsIgnoreCase("Dartford")){
			// FIXME Find stop;
			return 499;
		} else if(name.equalsIgnoreCase("Johan Heyns North")){
			// FIXME Check stop
			return 322;
		} else if(name.equalsIgnoreCase("Koeberg Power Station")){
			// FIXME Find stop;
			return 495;
		} else if(name.equalsIgnoreCase("Kwezi - Khayelitsha")){
			return 410; // FIXME Fix GTFS.
		} else if(name.equalsIgnoreCase("Maidens Cove")){
			return 271;
		} else if(name.equalsIgnoreCase("Marine Circle")){
			return 93; // FIXME Fix GTFS.
		} else if(name.equalsIgnoreCase("Mitchells Plain (Town Centre)")){
			return 350;
		} else if(name.equalsIgnoreCase("Rontree")){
			return 217; // FIXME Check and fix GTFS.
		} else if(name.equalsIgnoreCase("St Michaels")){
			return 214;
		} else if(name.equalsIgnoreCase("Thibault Square")){
			return 160; // FIXME Check and fix GTFS?
		} else if(name.equalsIgnoreCase("Vuyani Taxi Rank")){
			return 355;
		}
		return 000;
	}

}
