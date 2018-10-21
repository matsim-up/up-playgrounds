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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.matsim.up.utils.Header;
import org.xml.sax.SAXException;

/**
 * This class is used to run the transitSchedule helper files extractor class
 * 
 * @author Onnene
 *
 */
public class RunExtractTransitScheduleHelperFiles {
	
	public static final Logger log = Logger.getLogger(RunExtractTransitScheduleHelperFiles.class);

	/**
	 * @param args
	 */	 
    public static void main(String[] args) throws ParserConfigurationException, SAXException {
    	
    	Header.printHeader(RunExtractTransitScheduleHelperFiles.class, args);
	
		String gZipTschedule = "./input/gtfsInputs/gtfsOutput/transitSchedule.xml.gz";
	    String decompressedTscheduleXml = "./input/gtfsInputs/gtfsOutput/transitSchedule.xml";
	    String gZipNetwork = "./input/gtfsInputs/gtfsOutput/transitNetwork.xml.gz";
	    String decompressedNetworkXml = "./input/gtfsInputs/gtfsOutput/transitNetwork.xml";
	    String gZipVehicle = "./input/gtfsInputs/gtfsOutput/transitVehicles.xml.gz";
	    String decompressedVehicleXml = "./input/gtfsInputs/gtfsOutput/transitVehicles.xml";
	    String xmlOutputFilePath = "./input/transitScheduleMakerHelperFiles/";
	    
//	    String gZipTschedule = DirectoryConfig.COMPRESSED_GTFS_GZIP_TRANSIT_SCHEDULE_FILE;
//	    String decompressedTscheduleXml = DirectoryConfig.DECOMPRESSED_TRANSIT_SCHEDULE_XML_FILE;
//	    String gZipNetwork = DirectoryConfig.COMPRESSED_GTFS_GZIP_TRANSIT_NETWORK_FILE;
//	    String decompressedNetworkXml = DirectoryConfig.DECOMPRESSED_TRANSIT_NETWORK_XML_FILE;
//	    String gZipVehicle = DirectoryConfig.COMPRESSED_GTFS_GZIP_TRANSIT_VEHICLE_FILE;
//	    String decompressedVehicleXml = DirectoryConfig.DECOMPRESSED_TRANSIT_VEHICLE_XML_FILE;
//	    String xmlOutputFilePath = DirectoryConfig.SCHEDULE_STOPS_AND_LINES_HELPER_FILES_PATH;

	    FileMakerUtils fmu = new FileMakerUtils();   
	    ExtractTransitScheduleHelperFiles tsd = new ExtractTransitScheduleHelperFiles();
	    
	    try {
	    			    	
	    	fmu.unGunzipFile(gZipTschedule, decompressedTscheduleXml);
	    	fmu.unGunzipFile(gZipNetwork, decompressedNetworkXml);
	    	fmu.unGunzipFile(gZipVehicle, decompressedVehicleXml);
	    	tsd.getMatsimStops(decompressedTscheduleXml, xmlOutputFilePath);
	    	tsd.getMatsimLines(decompressedTscheduleXml, xmlOutputFilePath);	    		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	      
	    Header.printFooter();
    }

}
