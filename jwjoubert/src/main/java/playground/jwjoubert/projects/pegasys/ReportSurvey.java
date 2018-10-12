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
package playground.jwjoubert.projects.pegasys;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.Header;

/**
 * Class to just read in the Cape Town travel survey and report the number of
 * travellers and basic population statistics.
 * 
 * @author jwjoubert
 */
public class ReportSurvey {
	final private static Logger LOG = Logger.getLogger(ReportSurvey.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(ReportSurvey.class, args);
		String survey = args[0];
		
		Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new PopulationReader(sc).readFile(survey);
		int traveler = 0;
		for(Person person : sc.getPopulation().getPersons().values()) {
			Plan plan = person.getSelectedPlan();
			if(plan.getPlanElements().size() > 1) {
				traveler++;
			}
		}
		
		LOG.info("Some statistics:");
		LOG.info(String.format("      Total number of people: %d", sc.getPopulation().getPersons().size()));
		LOG.info(String.format("   Total number of travelers: %d", traveler));
		LOG.info(String.format("        Percentage travelers: %.3f%%", ((double)traveler)/((double)sc.getPopulation().getPersons().size())*100));
		
		Header.printFooter();
	}

}
