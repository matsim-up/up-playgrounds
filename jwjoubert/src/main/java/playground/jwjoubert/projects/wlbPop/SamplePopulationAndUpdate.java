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
package playground.jwjoubert.projects.wlbPop;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.Header;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;

/**
 * Class to read in the old 100% population, sample a given fraction, and then 
 * convert into the new format.
 * 
 * @author jwjoubert
 */
public class SamplePopulationAndUpdate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(SamplePopulationAndUpdate.class, args);
		
		String folder = args[0]; 
		folder += folder.endsWith("/") ? "" : "/";
		double fraction = Double.parseDouble(args[1]);
		String populationFile = args[2];
		
		Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		
		new PopulationReader(sc).readFile(folder + "population.xml.gz");
		new ObjectAttributesXmlReader(sc.getPopulation().getPersonAttributes()).readFile(folder + "populationAttributes.xml.gz");
		
		Scenario scSample = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		MatsimRandom.reset(20181025l);
		for(Id<Person> pid : sc.getPopulation().getPersons().keySet()) {
			double rnd = MatsimRandom.getLocalInstance().nextDouble();
			if(rnd < fraction) {
				Person person = sc.getPopulation().getPersons().get(pid);
				person.getAttributes().putAttribute("age", sc.getPopulation().getPersonAttributes().getAttribute(pid.toString(), "age"));
				person.getAttributes().putAttribute("gender", sc.getPopulation().getPersonAttributes().getAttribute(pid.toString(), "gender"));
				person.getAttributes().putAttribute("householdId", sc.getPopulation().getPersonAttributes().getAttribute(pid.toString(), "householdId"));
				person.getAttributes().putAttribute("population", sc.getPopulation().getPersonAttributes().getAttribute(pid.toString(), "population"));
				person.getAttributes().putAttribute("relationship", sc.getPopulation().getPersonAttributes().getAttribute(pid.toString(), "relationship"));
				person.getAttributes().putAttribute("school", sc.getPopulation().getPersonAttributes().getAttribute(pid.toString(), "school"));
				person.getAttributes().putAttribute("subpopulation", sc.getPopulation().getPersonAttributes().getAttribute(pid.toString(), "subpopulation"));
				
				scSample.getPopulation().addPerson(person);
			}
		}
		
		new PopulationWriter(scSample.getPopulation()).write(populationFile);
		
		Header.printFooter();
	}

}
