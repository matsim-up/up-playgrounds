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
package playground.onnene.transitScheduleMaker;

import org.apache.log4j.Logger;
import org.matsim.up.utils.Header;

import playground.onnene.exampleCode.UnzipUtility;

/**
 * @author Onnene
 *
 */
public class UnzipUtilityRunner {
	
	private static final Logger log = Logger.getLogger(UnzipUtilityRunner.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Header.printHeader(UnzipUtilityRunner.class, args);
		
		String zipFilePath = "C:/Users/NNNOB/Documents/GitHub/up-playgrounds/onnene/input/matsimInput/release.zip";
        String destDirectory = "C:/Users/NNNOB/Documents/GitHub/up-playgrounds/onnene/input/matsimInput/new/";
        UnzipUtility unzipper = new UnzipUtility();
        try {
            unzipper.unzip(zipFilePath, destDirectory);
        } catch (Exception ex) {
            // some errors occurred
            ex.printStackTrace();
        }
        
        Header.printFooter();

	}

}
