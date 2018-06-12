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

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;

/**
 * This class performs applies the mutation operator to the GA's decision variable. 
 * It helps to increase variation in the population after crossover has been performed.
 * 
 * @author Onnene
 *
 */

public class Mutation implements Variation {

    private double probability;

    public Mutation(double probability) {
        this.probability = probability;
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public Solution[] evolve(Solution[] parents) {
        Solution result1 = parents[0].copy();

        for (int i = 0; i < result1.getNumberOfVariables(); i++) {
                Variable variable1 = result1.getVariable(i);

                if ((PRNG.nextDouble() <= probability)
                    && (variable1 instanceof DecisionVariable)) {
                        applyMutation((DecisionVariable)variable1);
                }
        }

        return new Solution[] { result1};
    }


    private void applyMutation(DecisionVariable v1) {
        
        int transitLineNumberToReplace = new Random().nextInt(47);

        JSONObject ts2 = ProblemUtils.getRandomTransitSchedule();
        JSONObject transitLine = getTransitLine(ts2, transitLineNumberToReplace);
        replaceTransitLine(v1, transitLine, transitLineNumberToReplace); 
    }
    

    private JSONObject getTransitLine(JSONObject ts, int transitLineNumberToReplace) {
        JSONArray tsList = ProblemUtils.getTransitLines(ts);
        return tsList.getJSONObject(transitLineNumberToReplace);
    }
    

    private void replaceTransitLine(DecisionVariable v1, JSONObject transitLineToReplace, int transitLineNumberToReplace) {
        JSONArray tsList = ProblemUtils.getTransitLines(v1);
        tsList.put(transitLineNumberToReplace, transitLineToReplace);
    }
	
	

}
