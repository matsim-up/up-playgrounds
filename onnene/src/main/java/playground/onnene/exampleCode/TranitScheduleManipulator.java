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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitScheduleFactory;
import org.matsim.pt.transitSchedule.api.TransitScheduleWriter;
import org.matsim.vehicles.Vehicle;
  
/**
 * @author Amamifechukwuka
 *
 */
public class TranitScheduleManipulator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
			Scenario scenario = ScenarioUtils.loadScenario(ConfigUtils.loadConfig("C:\\Users\\NNNOB\\Documents\\GitHub\\input\\matsim_input\\config.xml"));

			Id<TransitLine> lineNo = Id.create("1001", TransitLine.class);
			TransitLine line1 = scenario.getTransitSchedule().getTransitLines().get(lineNo);
			Id<TransitRoute> routeNo = Id.create("1001_0", TransitRoute.class);
			TransitRoute route1 = line1.getRoutes().get(routeNo);
			System.out.println(route1.getRoute());
			TransitScheduleFactory transitScheduleFactory = scenario.getTransitSchedule().getFactory();
			TransitRoute route1a = transitScheduleFactory.createTransitRoute(Id.create("1a",TransitRoute.class), route1.getRoute(), route1.getStops(), route1.getTransportMode());
			Departure lastDeparture = null;
			int i = 0;
			for (Departure currentDeparture : route1.getDepartures().values()){
				double newDepartureTime = currentDeparture.getDepartureTime()-i*5*60;
				Departure newDeparture = transitScheduleFactory.createDeparture(Id.create("1_"+newDepartureTime, Departure.class), newDepartureTime);
				newDeparture.setVehicleId(currentDeparture.getVehicleId());
				route1a.addDeparture(newDeparture);
				lastDeparture = newDeparture;
				System.out.println(lastDeparture);
				i++;
			}
			Id<Vehicle> lastTransitVehicle = lastDeparture.getVehicleId();
			
			
			for (double j = lastDeparture.getDepartureTime()+15*60; j < 22*3600; j = j+15*60){
				Id<Departure> departureId = Id.create("1_"+j, Departure.class);
				Departure nextDeparture = transitScheduleFactory.createDeparture(departureId, j);
				Id<Vehicle> nextTransitVehicle =getNextTransitVehicle(lastTransitVehicle);
				lastTransitVehicle = nextTransitVehicle;
				nextDeparture.setVehicleId(nextTransitVehicle);
				route1a.addDeparture(nextDeparture);
				System.out.println(lastDeparture);
				lastDeparture = nextDeparture;
			}
			
			line1.removeRoute(route1);
			line1.addRoute(route1a);
			
			new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile("C:\\Users\\NNNOB\\Documents\\GitHub\\input\\matsim_input\\newschedule.xml");
			
			
		}
		private static Id<Vehicle> getNextTransitVehicle(Id<Vehicle> currentTransitVehicle){
			String[] vehicle = currentTransitVehicle.toString().split("_");
			String line = vehicle[0];
			String lastVehicleNo = vehicle[1];
			int lastVehicle = Integer.parseInt(lastVehicleNo);
			lastVehicle++;
			if (lastVehicle == 5) {
				lastVehicle = 0;
			}
			Id<Vehicle> nextVehicle = Id.create(line+"_"+lastVehicle, Vehicle.class);
			return nextVehicle;

	}

}
