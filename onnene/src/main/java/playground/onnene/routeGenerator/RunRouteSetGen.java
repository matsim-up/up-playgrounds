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
package playground.onnene.routeGenerator;

import java.util.List;

import playground.onnene.ga.DirectoryConfig;
import playground.onnene.transitScheduleMaker.FileMakerUtils;


/**
 * @author Onnene
 *
 */
public class RunRouteSetGen {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		int numLines = FileMakerUtils.count(DirectoryConfig.SCHEDULE_LINES_HELPER_FILE);
		List temp = RouteSetGen.getPoolOfFeasibleRoutes();
		int numOfSplit = temp.size()/numLines;
		
		String transitNetworkXml = DirectoryConfig.DECOMPRESSED_TRANSIT_NETWORK_XML_FILE;
		String transitScheduleXml = DirectoryConfig.DECOMPRESSED_TRANSIT_SCHEDULE_XML_FILE;
		
		String linkStyle = DirectoryConfig.LINKS_STYLESHEET_FILE;
		String nodeStyle = DirectoryConfig.NODES_STYLESHEET_FILE;
		String routeStyle = DirectoryConfig.ROUTES_STYLESHEET_FILE;
		String stopFacility = DirectoryConfig.STOP_FACILITY_STYLESHEET_FILE;
		String graphStyle = DirectoryConfig.GRAPH_STYLESHEET_FILE;
		String networkGraphml = DirectoryConfig.GRAPH_FILE_PATH;
		String outputFolder = DirectoryConfig.NETWORK_GRAPH_OUPUT_DIRECTORY;
		
		MatsimNetworkAndScheduleExtractor mnse = new MatsimNetworkAndScheduleExtractor();
		MatsimNetwork2Graphml mng = new MatsimNetwork2Graphml();	
		//RouteSetGen rag = new RouteSetGen();	
		

		// Step1: Extract link details from transitNetwork.xml
		mnse.LinkExtractor(linkStyle, transitNetworkXml, outputFolder);
		
		// Step2: Extract node details from transitNetwork.xml
		mnse.NodeExtractor(nodeStyle, transitNetworkXml, outputFolder);
		
		// Step3: Extract route details from transitSchedule.xml
		mnse.RouteExtractor(routeStyle, transitScheduleXml, outputFolder);

		// Step 4: Extract stop facilities details from transitSchedule.xml
		mnse.stopFacilityExtractor(stopFacility, transitScheduleXml, outputFolder);
		
		//Step 5: Create network Graph 
		mng.Network2Graphml(graphStyle, transitNetworkXml, networkGraphml);
		
		//Step 6: Create create feasible Routes		
		RouteSetGen.splitPopulationIntoIndividuals(temp, numOfSplit);
		
	}

}
