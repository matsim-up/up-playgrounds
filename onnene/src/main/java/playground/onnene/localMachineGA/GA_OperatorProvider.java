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
package playground.onnene.localMachineGA;

import java.util.Properties;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.spi.OperatorProvider;
import org.moeaframework.util.TypedProperties;

/**
 * This is a utility class for the GA, it specifies the 
 * variation operators to be used and their probabilities
 * 
 * @author Onnene
 *
 */
public class GA_OperatorProvider extends OperatorProvider {
	
	    @Override
	    public String getMutationHint(Problem problem) {
	        return "MyMutation";
	    }

	    @Override
	    public String getVariationHint(Problem problem) {
	        return "MyCrossOver+MyMutation";
	    }

	    @Override
	    public Variation getVariation(String name, Properties properties, Problem problem) {
	        
	        TypedProperties typedProperties = new TypedProperties(properties);

	        if (name.equalsIgnoreCase("MyCrossover")) {
	            return new Crossover(typedProperties.getDouble("MyCrossover.Rate", 0.75));
	        } else if (name.equalsIgnoreCase("MyMutation")) {
	            return new Mutation(typedProperties.getDouble("MyMutation.Rate", 0.25));
	        } else {

	        // No match, return null
	        
	        	return null;
	        }
	    }

}
