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

import org.apache.log4j.Logger;
import org.matsim.up.utils.Header;

/**
 * This class is used to run the @RouteSetGen class
 * @author Onnene
 *
 */
public class RunRouteSetGen {
	
	private static final Logger log = Logger.getLogger(RunRouteSetGen.class);

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		Header.printHeader(RunRouteSetGen.class, args);
		
		//int numLines = FileMakerUtils.count("./input/transitScheduleMakerHelperFiles/transitLineList.txt");		
		
		String transitNetworkXml = "./input/gtfsInputs/gtfsOutput/transitNetwork.xml";
		String transitScheduleXml = "./input/gtfsInputs/gtfsOutput/transitSchedule.xml";

				
		String linkStyle = "./input/routeGenInput/link_style.xsl";
		String nodeStyle = "./input/routeGenInput/node_style.xsl";
		String routeStyle = "./input/routeGenInput/route_style.xsl";
		String stopFacility = "./input/routeGenInput/stopFacility_style.xsl";
		String graphStyle = "./input/routeGenInput/graph_style.xsl";
		String networkGraphml = "./input/routeGenInput/NetworkGraph.graphml";
		String outputFolder = "./input/routeGenInput/";
		
				
		MatsimNetworkAndScheduleExtractor mnse = new MatsimNetworkAndScheduleExtractor();
		MatsimNetwork2Graphml mng = new MatsimNetwork2Graphml();	
			

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
		
		RouteSetGen rsg = new RouteSetGen();
		//List<List<String>> temp = rsg.getFeasibleRoutesByStratifiedSampling();
		//int numOfSplit = temp.size()/numLines;
		//rsg.splitPopulationIntoIndividuals(temp, numOfSplit);
		
		Header.printFooter();
		
	}

}
