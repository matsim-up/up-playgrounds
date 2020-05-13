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

package playground.jwjoubert.projects.wlbPop;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.Header;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;

/**
 * Class to read in the old 100% population, sample a given fraction, and then 
 * convert into the new format.
 * 
 * @author jwjoubert
 */
public class SamplePopulationAndUpdate {

	/**
	 * @param args three arguments, in the following order:
	 *             <ol>
	 *             <li>folder where the original population files are; </li>
	 *             <li>the fraction you want from the original population; and</li>
	 *             <li>the path of the new (single) population file with attributes embedded.</li>
	 *             </ol>
	 */
	public static void main(String[] args) {
		Header.printHeader(SamplePopulationAndUpdate.class, args);
		
		String folder = args[0]; 
		folder += folder.endsWith("/") ? "" : "/";
		double fraction = Double.parseDouble(args[1]);
		String populationFile = args[2];
		
		Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		
		new PopulationReader(sc).readFile(folder + "population.xml.gz");
		ObjectAttributes attr = new ObjectAttributes();
		new ObjectAttributesXmlReader(attr).readFile(folder + "populationAttributes.xml.gz");
		
		Scenario scSample = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		MatsimRandom.reset(20181025L);
		for(Id<Person> pid : sc.getPopulation().getPersons().keySet()) {
			double rnd = MatsimRandom.getLocalInstance().nextDouble();
			if(rnd < fraction) {
				Person person = sc.getPopulation().getPersons().get(pid);
				
				/* Distinguish between individuals and commercial vehicles */
				String pidString = pid.toString();
				if(pidString.startsWith("coct_p")) {
					person.getAttributes().putAttribute("age", attr.getAttribute(pid.toString(), "age"));
					person.getAttributes().putAttribute("gender", attr.getAttribute(pid.toString(), "gender"));
					person.getAttributes().putAttribute("householdId", attr.getAttribute(pid.toString(), "householdId"));
					person.getAttributes().putAttribute("population", attr.getAttribute(pid.toString(), "population"));
					person.getAttributes().putAttribute("relationship", attr.getAttribute(pid.toString(), "relationship"));
					person.getAttributes().putAttribute("school", attr.getAttribute(pid.toString(), "school"));
				}
				
				/* Each person and commercial vehicle must have a subpopulation attribute. */
				person.getAttributes().putAttribute("subpopulation", attr.getAttribute(pid.toString(), "subpopulation"));
				
				scSample.getPopulation().addPerson(person);
			}
		}
		
		new PopulationWriter(scSample.getPopulation()).write(populationFile);
		
		Header.printFooter();
	}

}
