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
package playground.jwjoubert.projects.ortia;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.freight.algorithms.complexNetwork.DigicorePathDependentNetworkReader_v2;
import org.matsim.up.freight.algorithms.complexNetwork.PathDependentNetwork;
import org.matsim.up.freight.algorithms.complexNetwork.PathDependentNetwork.PathDependentNode;
import org.matsim.up.freight.containers.DigicoreActivity;
import org.matsim.up.freight.containers.DigicoreChain;
import org.matsim.up.freight.containers.DigicoreFacility;
import org.matsim.up.utils.Header;

/**
 * Class to extract the complex (sub)network for a possible study for the 
 * Airports Company South Africa (ACSA).
 * 
 * @author jwjoubert
 */
public class OrtiaSubnetworkExtractor {
	final private static Logger LOG = Logger.getLogger(OrtiaSubnetworkExtractor.class);
	final private List<Id<Node>> facilities = getOrtiaFacilities();
	private PathDependentNetwork network;
	private final CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(
			TransformationFactory.HARTEBEESTHOEK94_LO29, 
			TransformationFactory.WGS84);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(OrtiaSubnetworkExtractor.class, args);
		run(args);
		Header.printFooter();
	}
	
	/**
	 * Parses the vehicle container and calls the extraction method.
	 * 
	 * @param args needs two arguments and in the following order:
	 * <ol>
	 * 		<li> path to {@link PathDependentNetwork} container file; and
	 * 		<li> path to where the first and second-echelon facilities should
	 * 			 be written to.
	 * </ol>
	 */
	public static void run(String[] args) {
		String networkFile = args[0];
		String output = args[1];
		
		DigicorePathDependentNetworkReader_v2 nr = new DigicorePathDependentNetworkReader_v2();
		nr.readFile(networkFile);
		OrtiaSubnetworkExtractor osn = new OrtiaSubnetworkExtractor();
		osn.network = nr.getPathDependentNetwork();
		osn.extract(output);
	}
	
	public void extract(String outputFile) {
		LOG.info("Extracting first and second echelon network nodes...");
		
		BufferedWriter bw = IOUtils.getBufferedWriter(outputFile);
		try {
			bw.write("echelon,direction,oId,oX,oY,dId,dX,dY");
			bw.newLine();
			
			for(Id<Node> nodeId : facilities) {
				List<String> list = processNode(nodeId);
				for(String entry : list) {
					bw.write(entry);
					bw.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot write to " + outputFile);
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close " + outputFile);
			}
		}
		
		LOG.info("Done extracting echelon facilities.");
	}
	
	private List<String> processNode(Id<Node> nodeId){
		List<String> list = new ArrayList<>();
		
		PathDependentNode node = network.getPathDependentNode(nodeId);
		if(node == null) {
			LOG.error("Could not find node '" + nodeId.toString() + "' in the network. Ignoring it.");
		} else {
			/* Upstream */
			list.addAll(getInLinks(nodeId, 1));
			Collection<Id<Node>> inNodes = network.getConnectedInNodeIds(nodeId);
			for(Id<Node> inId : inNodes) {
				list.addAll(getInLinks(inId, 2));
			}
			
			/* Downstream */
			list.addAll(getOutLinks(nodeId, 1));
			Collection<Id<Node>> outNodes = network.getConnectedOutNodeIds(nodeId);
			for(Id<Node> outId : outNodes) {
				list.addAll(getOutLinks(outId, 2));
			}
		}
		return list;
	}
	
	private List<String> getInLinks(final Id<Node> nodeId, int echelon){
		List<String> list = new ArrayList<>();

		PathDependentNode node = network.getPathDependentNode(nodeId);
		Coord cNode = ct.transform(node.getCoord());
		
		Collection<Id<Node>> inNodes = network.getConnectedInNodeIds(nodeId);
		for(Id<Node> fromId : inNodes) {
			PathDependentNode fromNode = network.getPathDependentNode(fromId);
			Coord cFrom = ct.transform(fromNode.getCoord());
			
			String entry = String.format("%d,in,%s,%.6f,%.6f,%s,%.6f,%.6f",
					echelon,
					fromId.toString(),
					cFrom.getX(), cFrom.getY(),
					nodeId.toString(),
					cNode.getX(), cNode.getY());
			list.add(entry);
		}
		return list;
	}
	
	
	private List<String> getOutLinks(final Id<Node> nodeId, int echelon){
		List<String> list = new ArrayList<>();
		
		PathDependentNode node = network.getPathDependentNode(nodeId);
		Coord cNode = ct.transform(node.getCoord());
		
		Collection<Id<Node>> outNodes = network.getConnectedOutNodeIds(nodeId);
		for(Id<Node> outId : outNodes) {
			PathDependentNode toNode = network.getPathDependentNode(outId);
			Coord cTo = ct.transform(toNode.getCoord());

			String entry = String.format("%d,out,%s,%.6f,%.6f,%s,%.6f,%.6f",
					echelon,
					nodeId.toString(),
					cNode.getX(), cNode.getY(),
					outId.toString(),
					cTo.getX(), cTo.getY());
			list.add(entry);
		}
		return list;
	}
	
	
	
	
	private List<String> processChain(DigicoreChain chain) {
		List<DigicoreActivity> activities = chain.getAllActivities();
		List<String> entries = new ArrayList<>();
		for(int i = 0; i < activities.size(); i++) {
			DigicoreActivity activity = activities.get(i);
			if(isInOrtiaProximity(activity)) {
				/* Backward */
				int steps = Math.min(i, 2);
				for(int step = 1; step <= steps; step++) {
					DigicoreActivity stepActivity = activities.get(i-step);
					String entry = String.format("%d,in,%s,%.0f,%.0f,%s,%.0f,%.0f", 
							step,
							(stepActivity.getFacilityId() == null ? "NA" : stepActivity.getFacilityId().toString()),
							stepActivity.getCoord().getX(), stepActivity.getCoord().getY(),
							activity.getFacilityId().toString(),
							activity.getCoord().getX(), activity.getCoord().getY());
					entries.add(entry);
				}
				
				/* Forward */
				steps = Math.min(2, activities.size()-i-1);
				for(int step = 1; step <= steps; step++) {
					DigicoreActivity stepActivity = activities.get(i+step);
					String entry = String.format("%d,out,%s,%.0f,%.0f,%s,%.0f,%.0f", 
							step,
							activity.getFacilityId().toString(),
							activity.getCoord().getX(), activity.getCoord().getY(),
							(stepActivity.getFacilityId() == null ? "NA" : stepActivity.getFacilityId().toString()),
							stepActivity.getCoord().getX(), stepActivity.getCoord().getY());
					entries.add(entry);
				}
			}
		}
		return entries;
	}

	
	private boolean isInOrtiaProximity(DigicoreActivity activity) {
		boolean result = false;
		if(facilities.contains(activity.getFacilityId())) {
			result = true;
		}
		return result;
	}
	
	/**
	 * Creates a list of {@link DigicoreFacility} {@link Id}s as taken from the
	 * 2014/03 clustered facilities using (20,20) density-based configuration.
	 * @return
	 */
	private List<Id<Node>> getOrtiaFacilities(){
		List<Id<Node>> list = new ArrayList<>();
		
		/* Freight terminals */
		list.add(Id.createNodeId("10019"));
		list.add(Id.createNodeId("10039"));
		list.add(Id.createNodeId("10043"));
		list.add(Id.createNodeId("10065"));
		list.add(Id.createNodeId("10077"));
		list.add(Id.createNodeId("10078"));
		list.add(Id.createNodeId("10112"));
		list.add(Id.createNodeId("10117"));
		
		/* Closer to passenger terminal */
		list.add(Id.createNodeId("10095"));
		list.add(Id.createNodeId("10106"));

		/* Passenger terminal */
		list.add(Id.createNodeId("10020"));
		list.add(Id.createNodeId("10056"));
		list.add(Id.createNodeId("10100"));
		list.add(Id.createNodeId("10102"));
		
		/* Airside south of passenger terminal */
		list.add(Id.createNodeId("10071"));
		list.add(Id.createNodeId("10089"));
		list.add(Id.createNodeId("10099"));
		
		return list;
	}

}
