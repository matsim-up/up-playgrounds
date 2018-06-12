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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jgrapht.io.ImportException;
  
/**
 * @author Amamifechukwuka
 *
 */
public class IndSchXML {
	

	/**
	 * @param args
	 * @return 
	 */
	
	public static  List<List<String>> readTransitLineId() throws ImportException{
		
	
		List<List<String>> lines = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\NNNOB\\Documents\\GitHub\\input\\routeGen_Input\\transitSchedule1.csv"))) {
			
			
			
			//List<GraphPath<CustomVertex, CustomEdge>> totalPaths = new ArrayList<>();
			int counter = 0;
			String line = "";
			br.readLine(); // skip the header line
			while ((line = br.readLine()) != null) {

				counter++;

				String[] od = line.split(",");
				
				lines.add(Arrays.asList(od));
				
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return lines; 

			
			}
		
	
	public static void main(String[] args) throws ImportException, UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		
		RouteSetGen RSG = new RouteSetGen();
		List listOfPaths = RouteSetGen.getPoolOfFeasibleRoutes();		
		//TransitSchedulesFromGeneratedRoutes TSFGR = new TransitSchedulesFromGeneratedRoutes();
		//List listOfPaths = TransitSchedulesFromGeneratedRoutes.GeneratedRoutes();
		
		//System.out.println(listOfPaths.get(0).getClass());
		//System.out.println(listOfPaths);
		
		for (int i = 0; i < listOfPaths.size(); i++) {
			
			ArrayList Al = (ArrayList) listOfPaths.get(i);		
			Iterator it = Al.listIterator();
		
			while (it.hasNext()) {
				
				System.out.println(it.next());
			}
		
		System.out.println();
	}
		
		
		//System.out.print(listOfPaths.getClass());
		
		 Document document = DocumentHelper.createDocument();
		    Element root = document.addElement( "transitSchedule" );
		    
		    
		    Element transitStops = root.addElement("transitStops")
		       .addAttribute("company", "Ferrai");
		  
		    transitStops.addElement("stopFacility")
		       .addAttribute("id", "Ferrari 101")
		       .addAttribute("x", "Ferrari 101")
		       .addAttribute("y", "Ferrari 101")
		       .addAttribute("linkRefId", "Ferrari 101")
		       .addAttribute("name", "Ferrari 101")
		       .addAttribute("isBlocking", "false");
		       //.addText("Ferrari 101");
		
		
		List<List<String>> linesId = readTransitLineId();
		
		//System.out.println("LINE" + linesId);
//		
//		System.out.println(linesId.get(2).get(0));
		
//		
		for (List<String> lstStr: linesId) {
//			
//			for (String str: lstStr) {
				
				//System.out.println(str);
				
				Element transitLine = root.addElement("transitLine")
				   .addAttribute("id", lstStr.get(0));
			
				
				Element transitRoute = transitLine.addElement("transitRoute")
				   .addAttribute("id", "foo");
				   //.addText("Ferrari 203");
				
				transitRoute.addElement("transportMode")
				   .addText("bus");
				
				
//			        transitRoute.addElement("route");
//			        transitRoute.addElement("departures");
				
				Element routeProfile = transitRoute.addElement("routeProfile");
				//transitRoute.addElement("routeProfile");
				
 
				Element stop = routeProfile.addElement("stop")
					.addAttribute("refId", "180")
					.addAttribute("arrivalOffset", "180")
					.addAttribute("departureOffset", "180")
					.addAttribute("awaitDeparture", "180");
				
				Element route = transitRoute.addElement("route");
				
				Element link = route.addElement("link")
					.addAttribute("refId", "180");
				
				
				Element departures = transitRoute.addElement("departures");
				
				Element departure = departures.addElement("departure")
				    .addAttribute("id", "180")
					.addAttribute("departureTime", "180")
					.addAttribute("vehicleRefId", "180");
			
			
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter( System.out, format );
        writer.write( document );

	}

}
