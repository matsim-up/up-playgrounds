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
package playground.jwjoubert.projects.bnChain;

import org.matsim.up.utils.Header;

/**
 * A class to prepare the City of Cape Town travel survey's data into a format
 * that can be used as input to a Bayesian Network (BN) procedure in R.
 * 
 * @author jwjoubert
 */
public class BnDataPrepper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(BnDataPrepper.class, args);
		
		Header.printFooter();
	}

}
