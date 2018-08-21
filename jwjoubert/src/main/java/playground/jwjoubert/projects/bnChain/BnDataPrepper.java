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
package playground.jwjoubert.projects.bnChain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.households.Household;
import org.matsim.households.HouseholdsReaderV10;
import org.matsim.up.utils.Header;

/**
 * A class to prepare the City of Cape Town travel survey's data into a format
 * that can be used as input to a Bayesian Network (BN) procedure in R.
 * 
 * @author jwjoubert
 */
public class BnDataPrepper {
	final private static Logger LOG = Logger.getLogger(BnDataPrepper.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(BnDataPrepper.class, args);

		String population = args[0];
		String household = args[1];
		String output = args[2];

		Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new PopulationReader(sc).readFile(population);
		new HouseholdsReaderV10(sc.getHouseholds()).readFile(household);

		int single = 0;
		int multiple = 0;

		BufferedWriter bw = IOUtils.getBufferedWriter(output);
		try {
			bw.write("pId,trip,fromType,mode,toType,gender,birth,employment,edu,travelForWork,workFromHome,licCar,licHeavy,licMc,dwelling,hhSize,assetOne,carAccess,carOwned,mcAccess,mcOwned");
			bw.newLine();

			for(Id<Person> pId : sc.getPopulation().getPersons().keySet()) {
				Person person = sc.getPopulation().getPersons().get(pId);
				Id<Household> hId = Id.create(pId.toString().split("_")[0], Household.class);
				Household hh = sc.getHouseholds().getHouseholds().get(hId);
				if(hh == null) {
					throw new RuntimeException("Cannot find household for person " + pId.toString());
				}

				List<PlanElement> list = person.getSelectedPlan().getPlanElements();
				if(list.size() == 1) {
					Activity act = (Activity)list.get(0);
					bw.write(String.format("%s,%d,%s,NA,NA,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", 
							pId.toString(), 1, 
							act.getType(),
							/* Individual */
							person.getAttributes().getAttribute("gender").toString(),
							person.getAttributes().getAttribute("yearOfBirth").toString(),
							person.getAttributes().getAttribute("employment").toString(),
							person.getAttributes().getAttribute("education").toString().replaceAll(",", ""),
							person.getAttributes().getAttribute("travelForWork").toString(),
							person.getAttributes().getAttribute("workFromHome").toString(),
							person.getAttributes().getAttribute("license_car").toString(),
							person.getAttributes().getAttribute("license_heavyVehicle").toString(),
							person.getAttributes().getAttribute("license_motorcycle").toString(),
							/* Household */
							hh.getAttributes().getAttribute("dwellingType").toString(),
							hh.getAttributes().getAttribute("householdSize").toString(),
							hh.getAttributes().getAttribute("assetClassMethod1").toString(),
							hh.getAttributes().getAttribute("numberOfHouseholdCarsAccessTo").toString(),
							hh.getAttributes().getAttribute("numberOfHouseholdCarsOwned").toString(),
							hh.getAttributes().getAttribute("numberOfHouseholdMotorcyclesAccessTo").toString(),
							hh.getAttributes().getAttribute("numberOfHouseholdMotorcyclesOwned").toString()
							));
				} else {
					int counter = 1;
					for(int i = 0; i < list.size()-2; i+=2) {
						Activity from = (Activity) list.get(i);
						Leg leg = (Leg) list.get(i+1);
						Activity to = (Activity) list.get(i+2);
						bw.write(String.format("%s,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", 
								pId.toString(), counter++, 
								from.getType(),
								leg.getMode(),
								to.getType(),
								/* Individual */
								person.getAttributes().getAttribute("gender").toString(),
								person.getAttributes().getAttribute("yearOfBirth").toString(),
								person.getAttributes().getAttribute("employment").toString(),
								person.getAttributes().getAttribute("education").toString().replaceAll(",", ""),
								person.getAttributes().getAttribute("travelForWork").toString(),
								person.getAttributes().getAttribute("workFromHome").toString(),
								person.getAttributes().getAttribute("license_car").toString(),
								person.getAttributes().getAttribute("license_heavyVehicle").toString(),
								person.getAttributes().getAttribute("license_motorcycle").toString(),
								/* Household */
								hh.getAttributes().getAttribute("dwellingType").toString(),
								hh.getAttributes().getAttribute("householdSize").toString(),
								hh.getAttributes().getAttribute("assetClassMethod1").toString(),
								hh.getAttributes().getAttribute("numberOfHouseholdCarsAccessTo").toString(),
								hh.getAttributes().getAttribute("numberOfHouseholdCarsOwned").toString(),
								hh.getAttributes().getAttribute("numberOfHouseholdMotorcyclesAccessTo").toString(),
								hh.getAttributes().getAttribute("numberOfHouseholdMotorcyclesOwned").toString()
								));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot write to " + output);
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close " + output);
			}
		}

		LOG.info("Single: " + single + "; Multiple: " + multiple);
		Header.printFooter();
	}

}
