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

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.testcases.MatsimTestUtils;
import org.moeaframework.core.PRNG;

public class ProblemUtilsTest {
	@Rule public MatsimTestUtils utils = new MatsimTestUtils();
	final private static Logger LOG = Logger.getLogger(ProblemUtilsTest.class);

	@Test
	public void testConstructor() {
		@SuppressWarnings("unused")
		ProblemUtils pu = null;
		try {
			pu = new ProblemUtils(utils.getClassInputDirectory());
		} catch (Exception e) {
			Assert.fail("Should create ProblemUtils without exception.");
		}
	}
	
	@Test
	public void testFetchTransitScheduleFilesPath() {
		
		ProblemUtils pu = new ProblemUtils(utils.getClassInputDirectory());
		List<File> files = null;
		try {
			files = pu.getTransitScheduleFiles();
		} catch(Exception e){
			Assert.fail("Should read the files without exception.");
		}
		Assert.assertEquals("Wrong number of files.", 3, files.size());
	}
	
	@Test
	public void testSelectTransitScheduleXMLFileRandomly() {
		PRNG.setSeed(12);
		ProblemUtils pu = new ProblemUtils(utils.getClassInputDirectory());
		File f = pu.selectTransitScheduleXMLFileRandomly();
		Assert.assertTrue("Wrong file sampled: " + f.getName(), f.getName().equalsIgnoreCase("transitSchedule1.xml"));
	}

}
