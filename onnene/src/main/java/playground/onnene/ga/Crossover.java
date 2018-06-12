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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;

import playground.onnene.transitScheduleMaker.FileMakerUtils;

/**
 * This class performs a one-point crossover operator on the GA's decision variable. 
 * It swaps genetic materials on either side of the crossover point
 * 
 * @author Onnene
 *
 */
public class Crossover implements Variation {
	
	 private double probability;

	    public Crossover(double probability) {
	        this.probability = probability;
	    }

	    @Override
	    public int getArity() {
	        return 2;
	    }

	    @Override
	    public Solution[] evolve(Solution[] parents) {
	        Solution parent1Child = parents[0].copy();
	        Solution parent2Child = parents[1].copy();

	        int numberOfChildren = parent1Child.getNumberOfVariables();

	        for (int i = 0; i < numberOfChildren; i++) {

	            Variable variable1 = parent1Child.getVariable(i);
	            Variable variable2 = parent2Child.getVariable(i);

	            if ((PRNG.nextDouble() <= probability) && (variable1 instanceof DecisionVariable) && (variable2 instanceof DecisionVariable)) {

	                DecisionVariable v1 = (DecisionVariable) variable1;
	                DecisionVariable v2 = (DecisionVariable) variable2;

	                applyCrossover(v1, v2);
	            }

	        }

	        return new Solution[] { parent1Child, parent2Child };
	    }
	    


	    private void applyCrossover(DecisionVariable v1, DecisionVariable v2) {
	        // swap first half of <transitLine> between v1 and v2
	    	
	    	int numLines = 0;
			try {
				numLines = FileMakerUtils.count(DirectoryConfig.SCHEDULE_LINES_HELPER_FILE);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    
	        //int numberOfTransitLinesToSwap = 25;
	        int numberOfTransitLinesToSwap = PRNG.nextInt(1, numLines-1);
	        System.out.println("number of lines" + numberOfTransitLinesToSwap);
	        List<JSONObject> transitLines1 = getTransitLines(v1, numberOfTransitLinesToSwap);
	        List<JSONObject> transitLines2 = getTransitLines(v2, numberOfTransitLinesToSwap);
        
	        replaceTransitLines(v1, transitLines2);
	        replaceTransitLines(v2, transitLines1);
	        
	    }

	    private void replaceTransitLines(DecisionVariable v, List<JSONObject> transitLinesToReplace) {
	        JSONArray tlList = ProblemUtils.getTransitLines(v);
	        
	        JSONObject tlToReplace;
	        for (int i = 0; i < transitLinesToReplace.size(); i++) {
	            tlToReplace = transitLinesToReplace.get(i);
	            tlList.put(i, tlToReplace);
	        }
	    }

	    private List<JSONObject> getTransitLines(DecisionVariable v2, int numberOfTransitLinesToSwap) {

	        JSONArray tlList = ProblemUtils.getTransitLines(v2);
	        
	        List<JSONObject> subList = new ArrayList<JSONObject>();
	        for (int i = 0; i < numberOfTransitLinesToSwap; i++)
	            subList.add(tlList.getJSONObject(i));
	        
	        return subList;
	    }

}
