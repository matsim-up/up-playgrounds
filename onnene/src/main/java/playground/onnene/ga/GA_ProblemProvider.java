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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;

/**
 * @author Onnene
 *
 */
public class GA_ProblemProvider extends ProblemProvider {

	@Override
	public Problem getProblem(String name) {
		
		if (name.equalsIgnoreCase("SimulationBasedTransitOptimizationProblem")) {
			
						try {
					return new SimulationBasedTransitOptimizationProblem();
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
				}				
		}
		
		else {
			
			return null;						
		}
		
		return null;		
	}
	
	

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		
		if (name.equalsIgnoreCase("SimulationBasedTransitOptimizationProblem")){
			
			try {
				return new NondominatedPopulation(PopulationIO.readObjectives(new File("./input/ProblemReferenceSet/problemRefSet.txt")));
			} catch (IOException e) {
				throw new FrameworkException(e);
			}
			
		}
		else {
			
			return null;
		}
	}
		

}
