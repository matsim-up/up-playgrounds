/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
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
package playground.jwjoubert.projects.tum;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.utils.Header;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtractDistanceDistribution {

    private static final String CT_SURVEY = "/Users/jwjoubert/workspace/matsim-data/scenarios/capeTown/survey/2013/surveyOnly/persons.xml.gz";
    private static final double CROW_FLY_FACTOR = 1.0;

    public static void main(String[] args) {
        Header.printHeader(ExtractDistanceDistribution.class, args);

        List<String> list = new ArrayList<>();
        CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, TransformationFactory.HARTEBEESTHOEK94_LO19);

        Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        new PopulationReader(sc).readFile(CT_SURVEY);

        int personId = 1;
        for (Person person : sc.getPopulation().getPersons().values()) {
            Plan plan = person.getSelectedPlan();
            if (plan.getPlanElements().size() > 1) {
                Activity activity = PopulationUtils.getFirstActivity(plan);
                for (int i = 2; i < plan.getPlanElements().size(); i++) {
                    PlanElement pe = plan.getPlanElements().get(i);
                    if (pe instanceof Activity) {
                        Activity thisActivity = (Activity) pe;
                        Coord c1 = ct.transform(activity.getCoord());
                        Coord c2 = ct.transform(thisActivity.getCoord());

                        double dist = CoordUtils.calcEuclideanDistance(c1, c2) * CROW_FLY_FACTOR;
                        list.add(String.format("%d,%.0f", personId, dist));
                        activity = thisActivity;
                    }
                }
            }
            personId++;
        }

        BufferedWriter bw = IOUtils.getBufferedWriter("/Users/jwjoubert/Downloads/distances.csv.gz");
        try{
            bw.write("Id,dist");
            bw.newLine();
            for(String s : list){
                bw.write(s);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Header.printFooter();
    }
}
