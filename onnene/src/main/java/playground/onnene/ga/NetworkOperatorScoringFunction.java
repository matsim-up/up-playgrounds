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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.TransitDriverStartsEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.TransitDriverStartsEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
//import org.matsim.core.mobsim.jdeqsim.Vehicle;
import org.matsim.core.utils.io.IOUtils;
//import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.Vehicle;

/**
 * This class is used to estimate the total cost incurred 
 * by the network operator in the optimization problem.
 * 
 * @author Onnene
 *
 */
public class NetworkOperatorScoringFunction implements VehicleEntersTrafficEventHandler, VehicleLeavesTrafficEventHandler, 

	LinkLeaveEventHandler, TransitDriverStartsEventHandler{
	
	private final Logger log = Logger.getLogger(NetworkOperatorScoringFunction.class);
	private Map<Id<Vehicle>, Double> vehicleMap = new TreeMap<>();
	private Map<Id<Vehicle>, Id<Link>> vehicleLinkMap = new TreeMap<>();
	private Map<Id<Vehicle>, Double> vkt = new TreeMap<>();
	//private Map<Id<Person>,Double> travelledDistance = new HashMap<>(); 
	private Map<Id<Vehicle>,Id<Person>> vehicles2Persons = new HashMap<>();
	//private Map<Id<Person>, Double> personMap = new TreeMap<>();
	private Map<Id<Vehicle>,Id<Person>> vehicleDriver = new HashMap<>();
	private String freqOutput;
	private Network network; 

	
	public NetworkOperatorScoringFunction(String output, Network network) {
		File file = new File(output);
		if (file.exists()) {
			log.warn("The output file " + output + " exists and will be overwritten.");
			file.delete();
		}
		
		this.freqOutput = output;
		this.network = network;
	}
	

	@Override
	public void reset(int iteration) {
		
		vehicleMap = new TreeMap<>();
		vehicleLinkMap = new TreeMap<>();
			
	}
	

	@Override
	public void handleEvent(VehicleEntersTrafficEvent event) {

		if (vehicleMap.containsKey(event.getVehicleId())) {
			log.warn("Oops, vehicle " + event.getVehicleId().toString() + " is already in our map.");
		}
		 
		vehicleMap.put(event.getVehicleId(), event.getTime());
		vehicleLinkMap.put(event.getVehicleId(), event.getLinkId());

	}
	

	@Override
	public void handleEvent(VehicleLeavesTrafficEvent event) {
		
		
		if (!vehicleMap.containsKey(event.getVehicleId())) {
			log.error("Cannot calculate trip duration for " + event.getVehicleId().toString());
			//vehicleMap.put(event.getVehicleId(), event.getTime());
		} else {
			
			 if (event.getVehicleId().toString().startsWith("tr_")) {
				 
				double vehTripStartTime = vehicleMap.get(event.getVehicleId());
//				System.out.println("start time for " + event.getVehicleId() + " is "  + vehTripStartTime);
//				System.out.println("end time for " + event.getVehicleId() + " is " +  event.getTime());
				double vehTripDuration = event.getTime() - vehTripStartTime;
				
								
				BufferedWriter bw = IOUtils.getAppendingBufferedWriter(freqOutput);
				try {
					try {
						if (vehTripDuration != 0) {
							
							bw.write(event.getVehicleId().toString() + "," + vehTripDuration);
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

				/* Remove vehicle from map. */
				vehicleMap.remove(event.getVehicleId());
			}
		}
		
	}



	@Override
	public void handleEvent(LinkLeaveEvent event) {
				
		Double value = 0.0;
		
		Double totalLength = 0.0;
		
		if (this.vehicleLinkMap.containsKey(event.getVehicleId())) {
					
		  double length = this.network.getLinks().get(event.getLinkId()).getLength();
		  value = this.vkt.get(event.getVehicleId());
		  if (value == null) {
			    value = 0.0;
		      }
		 totalLength = this.vkt.put(event.getVehicleId(), length);		
		 //totalLength = this.vkt.put(event.getVehicleId(), value + length);	
		 
		}
		
		
//		if (totalLength != null) {
//			
//			System.out.println(totalLength);
//			
//		}
		
		
		
		
		//System.out.println("travel length for vehicle " + event.getVehicleId() + " is " + this.vkt.get(event.getVehicleId()));
		
	}

//
//	@Override
//	public void handleEvent(LinkEnterEvent event) {
//		
//		if (this.vehicles2Persons.containsKey(event.getVehicleId())){
//			Id<Person> personId = this.vehicles2Persons.get(event.getVehicleId());
//			double distanceSoFarTravelled = this.travelledDistance.get(personId);
//			double length = this.network.getLinks().get(event.getLinkId()).getLength();
//			double newDistanceTravelled = distanceSoFarTravelled+length;
//			this.travelledDistance.put(personId, newDistanceTravelled);
//			}
//		
//	}



	@Override
	public void handleEvent(TransitDriverStartsEvent event) {
		
		if (!vehicleDriver.containsKey(event.getDriverId())){
			vehicles2Persons.put(event.getVehicleId(), event.getDriverId());
		}
		
		//System.out.println(event);
		
	}





}
