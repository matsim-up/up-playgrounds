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
import java.io.IOException;
import java.util.Properties;

import org.moeaframework.algorithm.Checkpoints;
import org.moeaframework.analysis.sensitivity.ResultEntry;
import org.moeaframework.analysis.sensitivity.ResultFileWriter;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;

/**
 * This class run several algorithms saving their Pareto sets to a file, 
 * compute a reference set from those files, and calculating the metrics 
 * using that reference set:
 * 
 * @author Onnene
 */
public class CheckpointAndOutputResult extends Checkpoints{

	/**
	 * 
	 */
	
	private final ResultFileWriter writer;
	
	public CheckpointAndOutputResult(Algorithm algorithm, File stateFile, File outputFile,
			int frequency) throws IOException {
		super(algorithm, stateFile, frequency);
		writer = new ResultFileWriter(algorithm.getProblem(), outputFile, true);
	}

	@Override
	public void doAction() {
		// Write the result to the output file
		try {
			Properties resultInfo = new Properties();
			resultInfo.setProperty("NFE", Integer.toString(algorithm.getNumberOfEvaluations()));

			writer.append(new ResultEntry(algorithm.getResult(), resultInfo));
		}
		catch (IOException e) {
			throw new FrameworkException(e);
		}
		
		// Call super to save the checkpoint file
		super.doAction();
	}

	@Override
	public void terminate() {
		super.terminate();
		writer.close();
	}
	
	
	

}
