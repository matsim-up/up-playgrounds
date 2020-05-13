/* *********************************************************************** *
 * project: org.matsim.*
 * HullConverter.java                                                                        *
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
package playground.jwjoubert.projects.hullSimilarity;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.MatsimFacilitiesReader;
import org.matsim.up.utils.Header;

/**
 * Class to read the clustered facilities from file and convert their hulls to
 * a flat format that can be visualised in R. It is assumed that the current
 * facility coordinates are in the Hartebeesthoek Lo29 coordinate reference 
 * system.
 * 
 * @author jwjoubert
 */
public class HullConverter {
	final private static Logger LOG = Logger.getLogger(HullConverter.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(HullConverter.class, args);

		String folder = args[0];
		folder += folder.endsWith("/") ? "" : "/";
		String output = args[1];

		int[] years = {2010, 2011, 2012, 2013, 2014};

		CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(
				TransformationFactory.HARTEBEESTHOEK94_LO29, 
				TransformationFactory.WGS84);

		BufferedWriter bw = IOUtils.getBufferedWriter(output);
		/* Write header. */
		try {
			bw.write("year,fId,pId,x,y,cX,cY,lon,lat,cLon,cLat\n");

			/* Process all years... */
			for(int year : years) {
				String filename = folder + "20_20_facilities_" + year + "03.xml.gz";
				LOG.info("Processing " + filename + "...");

				/* PArse the facilities */
				Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
				MatsimFacilitiesReader fr = new MatsimFacilitiesReader(sc);
				fr.putAttributeConverter(Point.class, new org.matsim.up.freight.clustering.HullConverter());
				fr.putAttributeConverter(LineString.class, new org.matsim.up.freight.clustering.HullConverter());
				fr.putAttributeConverter(Polygon.class, new org.matsim.up.freight.clustering.HullConverter());
				fr.readFile(filename);
				LOG.info("Total of " + sc.getActivityFacilities().getFacilities().size() + " facilities found.");

				/* Process the facilities */
				int pId = 0;
				for(ActivityFacility facility : sc.getActivityFacilities().getFacilities().values()) {
					pId = 1;
					
					/* Get and convert the centroid */
					Coord cH94 = facility.getCoord();
					Coord cWGS = ct.transform(cH94);
					
					Polygon hull = null;
					Object o = facility.getAttributes().getAttribute("concaveHull");
					if(o != null & o instanceof Polygon) {
						hull = (Polygon) o;

						/* Only write the hull if it can accurately be 
						 * identified as a Polygon. */
						Coordinate[] ca = hull.getCoordinates();
						for(Coordinate c : ca) {
							Coord coord = ct.transform(CoordUtils.createCoord(c.x, c.y));
							String line = String.format("%d,%d_%s,%d,%.0f,%.0f,%.0f,%.0f,%.8f,%.8f,%.8f,%.8f\n",
									getYearCode(year),
									year,
									facility.getId().toString(),
									pId++,
									c.x, 
									c.y,
									cH94.getX(),
									cH94.getY(),
									coord.getX(),
									coord.getY(),
									cWGS.getX(),
									cWGS.getY());
							bw.write(line);
						}
					}
				}
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
	
	private static int getYearCode(int year) {
		switch (year) {
		case 2010:
			return 1;
		case 2011:
			return 2;
		case 2012:
			return 3;
		case 2013:
			return 4;
		case 2014:
			return 5;
		default:
			break;
		}
		return 0;
	}

}
