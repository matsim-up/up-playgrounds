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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Onnene
 *
 */
public class Utility {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String targetFile = "./output/optimisationResults/1/indicators.txt";
		//String targetFile = "C:\\Users\\NNNOB\\Documents\\GitHub\\up-playgrounds\\onnene\\output\\optimisationResults\\1\\indicators.txt";
		String toUpdate = " ";
		String updated = "NFE    HV   GD    AEI    IGD    S    MPE";
		
		BufferedReader file = new BufferedReader(new FileReader(targetFile ));
		String line;
		StringBuilder input = new StringBuilder();
		int count = 0;

		while ((line = file.readLine()) != null) {
			
			System.out.println(line);
			//System.out.println(updated);
		    if (count == 0) {
		        line = line.replace(toUpdate, updated);
		        
		    }
		    
		    input.append(line).append('\n');
		    ++count;
		}
		
		
		
		file.close();

	}

}
