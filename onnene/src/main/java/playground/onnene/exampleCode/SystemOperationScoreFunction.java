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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.vehicles.Vehicle;

import playground.onnene.ga.NetworkOperatorScoringFunction;

/**
 * @author Onnene
 *
 */
public class SystemOperationScoreFunction implements VehicleEntersTrafficEventHandler, VehicleLeavesTrafficEventHandler, 

LinkEnterEventHandler, LinkLeaveEventHandler{
	
	
	private final Logger log = Logger.getLogger(NetworkOperatorScoringFunction.class);
	private Map<Id<Vehicle>, Double> vehicleMap = new TreeMap<>();
	private Map<Id<Link>, Id<Vehicle>> vehicleLinkMap = new TreeMap<>();
	Map<Id<Vehicle>, Double> vkt = new TreeMap<>();
	private String output;
	private Network network; 
	private Link link;
	

	public SystemOperationScoreFunction(String output) {
		File file = new File(output);
		if (file.exists()) {
			log.warn("The output file " + output + " exists and will be overwritten.");
			file.delete();
		}

		this.output = output;
		
	}
	
	
	public SystemOperationScoreFunction(Network network, Link link) {
		
		this.network = network;
		this.link = link;
	}


	@Override
	public void reset(int iteration) {
		this.vehicleMap.clear();

	}
	
	@Override
	public void handleEvent(VehicleLeavesTrafficEvent event) {

		
		if (vehicleMap.containsKey(event.getVehicleId())) {
			log.warn("Oops, vehicle " + event.getVehicleId().toString() + " is already in our map.");
		
		}
		 
		vehicleMap.put(event.getVehicleId(), event.getTime());
		vehicleLinkMap.put(event.getLinkId(), event.getVehicleId());
		
	}
	

	@Override
	public void handleEvent(VehicleEntersTrafficEvent event) {
		
		
		if (!vehicleMap.containsKey(event.getVehicleId())) {
			log.error("Cannot calculate trip duration for " + event.getVehicleId().toString());
		} else {
			
			
			 if (!event.getVehicleId().toString().startsWith("tr")) {
				 
				double tripStartTime = vehicleMap.get(event.getVehicleId());
				double tripDuration = event.getTime() - tripStartTime;
				
				
				BufferedWriter bw = IOUtils.getAppendingBufferedWriter(output);
				try {
					try {
						if (tripDuration != 0) {
						bw.write(event.getVehicleId().toString() + "," + tripDuration);
						bw.newLine();
						}
					} catch (IOException e) {
						e.printStackTrace();
						throw new RuntimeException("Cannot write to output.");
					}
				} finally {
					try {
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
						throw new RuntimeException("Cannot close file.");
					}
				}

				/* Remove person from map. */
				vehicleMap.remove(event.getVehicleId());
			}
		}
		
		
		
	}


	@Override
	public void handleEvent(LinkLeaveEvent event) {

	}


	@Override
	public void handleEvent(LinkEnterEvent event) {
		
		  double length = network.getLinks().get(event.getLinkId()).getLength();
		  Double value = vkt.get(event.getVehicleId());
		  if (value == null) {
		    value = 0.0;
		  }
		  vkt.put(event.getVehicleId(), value + length);
		
		
	}
	
	

}
