/* *********************************************************************** *
 * project: org.matsim.*
 * ExampleQuadTree.java
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
package playground.onnene.exampleCode;

import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.matsim.up.utils.Header;

/**
 * Demonstrate the use of a {@link QuadTree}.
 * 
 * @author jwjoubert
 */
public class ExampleQuadTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(ExampleQuadTree.class, args);
		
		String transitFile = args[0];

		/* First parse the transit schedule file into a MATSim scenario. */
		Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new TransitScheduleReader(sc).readFile(transitFile);
		buildQuadTreeFromTransitPoints(sc);
		
		Header.printFooter();
	}
	
	
	public static void buildQuadTreeFromTransitPoints(Scenario sc) {
		
		double xMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
		
		/* Determine the extent of QuadTree. */
		Map<Id<TransitStopFacility>, TransitStopFacility> facilitiesMap = sc.getTransitSchedule().getFacilities();
		for(TransitStopFacility facility : facilitiesMap.values()) {
			Coord c = facility.getCoord();
			System.out.println(c);
			xMin = Math.min(xMin, c.getX());
			xMax = Math.max(xMax, c.getX());
			yMin = Math.min(yMin, c.getY());
			yMax = Math.max(yMin, c.getY());
		
		}
		
//		System.out.println("xMin" + xMin);
//		System.out.println("yMin" + yMin);
//		System.out.println("xMax" + xMax);		
//		System.out.println("yMax" + yMax);
		
		/* Create the QuadTree and add transit stop facilities. */
		QuadTree<Id<TransitStopFacility>> qt = new QuadTree<>(xMin, yMin, xMax, yMax);
		for(TransitStopFacility facility : facilitiesMap.values()) {
			qt.put(	facility.getCoord().getX(), 
					facility.getCoord().getY(), 
					facility.getId());
		}
		
		/* Now you can find any value inside the QuadTree. */
		
	}

}
