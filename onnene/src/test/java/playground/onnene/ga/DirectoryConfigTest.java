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

import org.junit.Assert;
import org.junit.Test;

import playground.onnene.exampleCode.DirectoryConfig;

public class DirectoryConfigTest {

	@Test
	public void testGetParentDirectory() {
		String parentDefault = DirectoryConfig.getParentDirectory();
		Assert.assertTrue("Wrong default parent folder", parentDefault.equalsIgnoreCase("C:\\Users\\NNNOB\\Documents\\GitHub\\SBO_input\\"));
		//Assert.assertTrue("Wrong default parent folder", parentDefault.equalsIgnoreCase("./input/"));
		
	}

	@Test
	public void testSetParentDirectory() {
		String parentDefault = DirectoryConfig.getParentDirectory();
		DirectoryConfig.setParentDirectory("/home/dummy/");
		String changedParent = DirectoryConfig.getParentDirectory();
		Assert.assertFalse("Wrong default parent folder", changedParent.equalsIgnoreCase(parentDefault));
		Assert.assertTrue("Wrong default parent folder", changedParent.equalsIgnoreCase("/home/dummy/"));
	}
	
}
