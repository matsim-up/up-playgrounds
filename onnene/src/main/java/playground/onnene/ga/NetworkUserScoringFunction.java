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


package playground.onnene.ga;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;

/**
 * 
 * This class is used to estimates the total cost incurred 
 * by the network user or passenger in the optimization problem.
 * 
 * @author Onnene
 *
 */
public class NetworkUserScoringFunction implements PersonDepartureEventHandler, PersonArrivalEventHandler {

	private final Logger log = Logger.getLogger(NetworkUserScoringFunction.class);
	private Map<Id<Person>, Double> personMap = new TreeMap<>();
	private String output;
	private static List<Double> totalTraveltime = new ArrayList<>();
	private static double totalPersonTripDuration;
	


	public NetworkUserScoringFunction(String output) {
		File file = new File(output);
		if (file.exists()) {
			log.warn("The output file " + output + " exists and will be overwritten.");
			file.delete();
		}

		this.output = output;

	}


	@Override
	public void reset(int iteration) {
		personMap = new TreeMap<>();
		//distanceDistribution = new int[100000];
		totalTraveltime = new ArrayList<>();
	}


	@Override
	public void handleEvent(PersonDepartureEvent event) {
		if (personMap.containsKey(event.getPersonId())) {
			log.warn("Oops, person " + event.getPersonId().toString() + " is already in our map.");
		}
		personMap.put(event.getPersonId(), event.getTime());
		
	}


	@Override
	public void handleEvent(PersonArrivalEvent event) {
		
		//double totalTripDuration = 0.0;
		
		//double[] totalTripDuration = new double[100000];
		
		if (!personMap.containsKey(event.getPersonId())) {
			log.error("Cannot calculate trip duration for " + event.getPersonId().toString());
			
		} else {
			if (!event.getPersonId().toString().startsWith("pt_tr")) {
				double tripStartTime = personMap.get(event.getPersonId());
				double tripDuration = event.getTime() - tripStartTime;
				
				
				BufferedWriter bw = IOUtils.getAppendingBufferedWriter(output);
				try {
					try {
						 
						bw.write(event.getPersonId().toString() + "," + tripDuration);
						bw.newLine();
						//this.distanceDistribution[(int) tripDuration]++;
						totalTraveltime.add(tripDuration);
						
						totalPersonTripDuration += tripDuration;
						
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
				
				//totalTripDuration += tripDuration;

				/* Remove person from map. */
				personMap.remove(event.getPersonId());
			}
			
			
		}
		
		//System.out.println(totalTripDuration);
		//System.out.println(totalTraveltime);
	}
	
		public static List<Double> getUsersTimeList() {
				
			return totalTraveltime;
	}
		
		public static double getUserScore() {
			
			BigDecimal averagetripDur = new BigDecimal(totalPersonTripDuration/(15*60*totalTraveltime.size()));
			BigDecimal userScore = averagetripDur.setScale(4, RoundingMode.CEILING);
			
//			BigDecimal totaltripDur = new BigDecimal(totalPersonTripDuration/(24*60));			
//			BigDecimal userScore = totaltripDur.setScale(4, RoundingMode.CEILING);
//			
						
			return userScore.doubleValue();
		}
	
	
//	
//	public static int getUserScore1(List<Double> userTimeList) {
//		
//		double sum = 0.0;
//		
//		
//		for(Double tvt: userTimeList) {
//			
//			sum += tvt;
//		}
//		
//		System.out.println("another way to calculate total user cost " + totalPersonTripDuration/(24*60));
//		System.out.println("another way to calculate avr user cost " + totalPersonTripDuration/(24*60*userTimeList.size()));
//		
//		int totalTravelTime = (int) (sum/(24*60));
//		
//		int averageTravelTime = (int) (sum/(24*60*userTimeList.size()));
//		
//		System.out.println("first way to calculate total user cost " + totalTravelTime );
//		System.out.println("first way to calculate avr user cost " + averageTravelTime);
//		
//		//System.out.println(totalTraveltime);
//		
//		//System.out.println(Arrays.toString(this.totalTraveltime));
//		
//		return averageTravelTime ;
//	}
		


}
