/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
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
package playground.jwjoubert.projects.vanLaar;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.Counter;
import org.matsim.up.utils.Header;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ConvertFacilities {
	private static final String INPUT = "/Users/jwjoubert/Documents/University/Graduate/Masters/MastersCurrent/Zane van Laar/Git/data/20_20_facilityCsv.csv.gz";
	private static final String OUTPUT = "/Users/jwjoubert/Documents/University/Graduate/Masters/MastersCurrent/Zane van Laar/Git/data/20_20_facilityCsv_output.csv";

//	public static final String CRS_INPUT = TransformationFactory.HARTEBEESTHOEK94_LO29;
	public static final String CRS_INPUT = TransformationFactory.WGS84_SA_Albers;
	public static final String CRS_OUTPUT = TransformationFactory.WGS84;


	public static void main(String[] args) {
		Header.printHeader(ConvertFacilities.class, args);

		/* Read in current facilities. */
		BufferedReader br = IOUtils.getBufferedReader(INPUT);
		BufferedWriter bw = IOUtils.getBufferedWriter(OUTPUT);
		Counter counter = new Counter("  Lines #");

		CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(CRS_INPUT, CRS_OUTPUT);
		try{
			String header = br.readLine();
			bw.write(header);
			bw.newLine();
			counter.incCounter();
			String line;
			while((line = br.readLine()) != null){
				String[] sa = line.split(",");
				String id = sa[0];
				double lon = Double.parseDouble(sa[1]);
				double lat = Double.parseDouble(sa[2]);
				String count = sa[3];

				Coord c = ct.transform(CoordUtils.createCoord(lon, lat));
				bw.write(String.format("%s,%.6f,%.6f,%s\n", id, c.getX(), c.getY(), count));

				counter.incCounter();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		counter.printCounter();

		Header.printFooter();
	}
}
