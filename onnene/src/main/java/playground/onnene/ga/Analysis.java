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

import org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * @author Onnene
 *
 */
public class Analysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 OperatorFactory.getInstance().addProvider(new GA_OperatorProvider());   
		 ProblemFactory.getInstance().addProvider(new GA_ProblemProvider());
		 ProblemFactory.getInstance().getProblem("SimulationBasedTransitOptimizationProblem");
		 
		 Settings.PROPERTIES.setString(Settings.KEY_DIAGNOSTIC_TOOL_ALGORITHMS, "NSGAII");
		 
		 Settings.PROPERTIES.setString(Settings.KEY_DIAGNOSTIC_TOOL_PROBLEMS, "SimulationBasedTransitOptimizationProblem");
		 
		 try {
			LaunchDiagnosticTool.main(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
