/* *********************************************************************** *
 * project: org.matsim.*
 * AfcDailyPopulationBuilder.java
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.Time;
import org.matsim.up.utils.Header;

/**
 * Class to read in the trips extracted by {@link AfcTripChainer} and creating
 * a MATSim {@link Population} from it.
 * 
 * @author jwjoubert
 */
public class AfcDailyPopulationBuilder {
	final private static Logger LOG = Logger.getLogger(AfcDailyPopulationBuilder.class);
	private Scenario sc;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(AfcDailyPopulationBuilder.class, args);
		run(args);		
		Header.printFooter();
	}
	
	public static void run(String[] args) {
		String tripFile = args[0];
		String network = args[1];
		String populationFile = args[2];
		
		AfcDailyPopulationBuilder apb = new AfcDailyPopulationBuilder(network);
		Map<Id<Person>, List<String>> personMap = apb.parseTrips(tripFile);
		apb.buildPopulation(personMap);
		apb.writePopulationToFile(populationFile);
	}
	

	public AfcDailyPopulationBuilder(String network) {
		this.sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new MatsimNetworkReader(this.sc.getNetwork()).readFile(network);
	}

	
	/**
	 * Reads the trips from file and groups the records per person.
	 *  
	 * @param tripFile
	 * @return
	 */
	private Map<Id<Person>, List<String>> parseTrips(String tripFile) {
		BufferedReader br = IOUtils.getBufferedReader(tripFile);
		
		Map<Id<Person>, List<String>> personMap = new HashMap<Id<Person>, List<String>>();
		
		try {
			String line = br.readLine(); /* Header */
			while((line = br.readLine()) != null) {
				String[] sa = line.split(",");
				
				/* Group each person's records together */
				Id<Person> pId = Id.createPersonId(sa[0]); 
				if(!personMap.containsKey(pId)) {
					personMap.put(pId, new ArrayList<String>());
				}
				personMap.get(pId).add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot read from " + tripFile);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close " + tripFile);
			}
		}
		
		LOG.info("Number of unique indviduals: " + personMap.size());
		return personMap;
	}
	
	
	
	public void buildPopulation(Map<Id<Person>, List<String>> map) {
		
		for(Id<Person> pId : map.keySet()) {
			Person person = this.sc.getPopulation().getFactory().createPerson(pId);
			Plan plan = PopulationUtils.createPlan();
			
			int activityIndex = 1;

			List<String> records = map.get(pId);
			/* Start with the first record. */
			String[] sa = records.get(0).split(",");
			String stopIdStart = sa[1];
			Node nodeStart = this.sc.getNetwork().getNodes().get(Id.createNodeId("MyCiTi_" + stopIdStart));
			
			String stopIdEnd = sa[2];
			Node nodeEnd = this.sc.getNetwork().getNodes().get(Id.createNodeId("MyCiTi_" + stopIdEnd));
			double tripStart = Time.parseTime(sa[3]);
			double tripEnd = Time.parseTime(sa[4]);

			/* Add origin activity. */
			if(nodeStart==null) {
				LOG.error("Cannot find node " + stopIdStart);
				continue; /* Ignore this person if they have transactions at unknown stops. */
			}
			Activity actOrigin = PopulationUtils.createActivityFromCoord("a"+activityIndex++, nodeStart.getCoord());
			actOrigin.setEndTime(tripStart);
			plan.addActivity(actOrigin);
			
			Leg leg = PopulationUtils.createLeg("pt");
			plan.addLeg(leg);
			
			/* Add destination activity */
			if(nodeEnd==null) {
				LOG.error("Cannot find node " + stopIdEnd);
				continue; /* Ignore this person if they have transactions at unknown stops. */
			}
			
			Activity actDest = PopulationUtils.createActivityFromCoord("a"+activityIndex++, nodeEnd.getCoord());
			actDest.setStartTime(tripEnd);
			plan.addActivity(actDest);
			
			Activity actPrevious = actDest;
			
			if(records.size() > 1) {
				for(int i = 1; i < records.size(); i++) {
					sa = records.get(i).split(",");
					stopIdStart = sa[1];
					nodeStart = this.sc.getNetwork().getNodes().get(Id.createNodeId("MyCiTi_" + stopIdStart));
					
					stopIdEnd = sa[2];
					nodeEnd = this.sc.getNetwork().getNodes().get(Id.createNodeId("MyCiTi_" + stopIdEnd));
					
					tripStart = Time.parseTime(sa[3]);
					tripEnd = Time.parseTime(sa[4]);
					
					/* Add origin activity. Since this is a follow-up activity, we need to look at the 
					 * previous activity. */
					if(nodeStart==null) {
						LOG.error("Cannot find node " + stopIdStart);
						break; /* Ignore this person if they have transactions at unknown stops. */
					}
					Coord coordThisStart = nodeStart.getCoord();
					if(!coordThisStart.equals(actPrevious.getCoord())) {
						/* Subsequent activity coordinates are not the same. */
						actPrevious.setEndTime(tripStart);
						/*FIXME Add a dummy trip connecting them. */
						Leg dummy = PopulationUtils.createLeg("teleport");
						plan.addLeg(dummy);
						Activity thisOrigin = PopulationUtils.createActivityFromCoord("a"+activityIndex++, nodeStart.getCoord());
						thisOrigin.setStartTime(tripStart);
						thisOrigin.setEndTime(tripStart);
						plan.addActivity(thisOrigin);
						actPrevious = thisOrigin;
					} else{
						actPrevious.setEndTime(tripStart);
					}
					
					Leg thisLeg = PopulationUtils.createLeg("pt");
					plan.addLeg(thisLeg);
					
					/* Add destination activity */
					if(nodeEnd==null) {
						LOG.error("Cannot find node " + stopIdEnd);
						break; /* Ignore this person if they have transactions at unknown stops. */
					}
					
					Activity thisActDest = PopulationUtils.createActivityFromCoord("a"+activityIndex++, nodeEnd.getCoord());
					thisActDest.setStartTime(tripEnd);
					plan.addActivity(thisActDest);
					actPrevious = thisActDest;
				}
			}
			person.addPlan(plan);
			this.sc.getPopulation().addPerson(person);
		}
		
		LOG.info("Number of persons in population: " + this.sc.getPopulation().getPersons().size());
	}
	
	public void writePopulationToFile(String output) {
		new PopulationWriter(this.sc.getPopulation()).write(output);
	}
	
	
	
	

}
