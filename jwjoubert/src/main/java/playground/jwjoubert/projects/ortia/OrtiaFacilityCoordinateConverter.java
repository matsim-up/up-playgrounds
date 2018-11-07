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
package playground.jwjoubert.projects.ortia;

import java.io.BufferedWriter;
import java.io.IOException;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.MatsimFacilitiesReader;
import org.matsim.up.utils.Header;

/**
 * Converts the facility file's coordinates to WGS84.
 * 
 * @author jwjoubert
 */
public class OrtiaFacilityCoordinateConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(OrtiaFacilityCoordinateConverter.class, args);
		
		String facilities = args[0];
		String output = args[1];
		
		/*FIXME This may have to be fixed if the facilities file does not yet 
		 * contain the CRS, i.e. new version. */
		CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.HARTEBEESTHOEK94_LO29, TransformationFactory.WGS84);
		Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
//		new MatsimFacilitiesReader(TransformationFactory.WGS84, sc).readFile(facilities);
		
		BufferedWriter bw = IOUtils.getBufferedWriter(output);
		try {
			bw.write("id,lon,lat");
			bw.newLine();
			
			for(ActivityFacility facility : sc.getActivityFacilities().getFacilities().values()) {
				bw.write(String.format("%s,%.8f,%.8f\n", 
						facility.getId().toString(),
						facility.getCoord().getX(),
						facility.getCoord().getY()));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot write to " + output);
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close " + output);
			}
		}
		
		Header.printFooter();
	}

}
