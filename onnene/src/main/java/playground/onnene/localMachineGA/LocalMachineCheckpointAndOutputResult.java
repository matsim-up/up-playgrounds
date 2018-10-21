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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.moeaframework.algorithm.Checkpoints;
import org.moeaframework.analysis.sensitivity.ResultEntry;
import org.moeaframework.analysis.sensitivity.ResultFileWriter;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;

/**
 * @author Onnene
 *
 */
public class LocalMachineCheckpointAndOutputResult extends Checkpoints{
	
		private static final Logger log = Logger.getLogger(LocalMachineCheckpointAndOutputResult.class);
	
	    private final ResultFileWriter writer;
		
		public LocalMachineCheckpointAndOutputResult(Algorithm algorithm, File stateFile, File outputFile,
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
