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

import java.io.File;

/**
 * Collection of file directories used in the program
 * 
 * @author Onnene
 *
 */
public class DirectoryConfig {
	
	    
		// MATSim Iterations
	    public static final int MATSIM_ITERATION_NUMBER = 10;
	    
	    // Parent Directory
	    private static final String PARENT_DIRECTORY = "C:\\Users\\NNNOB\\Documents\\GitHub\\SBO_input\\";
	    
	    // MATSim input files 
	    public static final String CONFIG_FILE = PARENT_DIRECTORY + "common\\config.xml";
	    public static final String TRANSIT_SCHEDULE_FILE = PARENT_DIRECTORY + "common\\transitSchedule.xml";
	    public static final String TRANSIT_VEHICLES_FILE = PARENT_DIRECTORY + "common\\transitVehicles.xml";
	    
	    // MATSim output directory
	    public static final String MATSIM_OUTPUT_FOLDER = PARENT_DIRECTORY + "output\\";
	    
	    //AFC data directory
	    public static final String AFC_DATA_FOLDER = PARENT_DIRECTORY + "AFC_data\\";
	    	    
	    // MOEA input files
	    public static final String INITIAL_POPULATION_DIRECTORY = PARENT_DIRECTORY + "initial_pop\\";
	    
	    // Log Files Directory
	    public static final String LOG_FOLDER_PATH = PARENT_DIRECTORY + "logs\\";
	    public static final String RUN_MOEA_LOG_FILE_PATH = LOG_FOLDER_PATH + "run_moea_log.txt";
	    public static final String TRANSIT_PROBLEM_LOG_FILE_PATH = LOG_FOLDER_PATH + "transit_problem_log.txt";

	    // Route Generation Utility Files 
	    public static final String OD_NODES_FILE = PARENT_DIRECTORY + "route_gen_input\\46_myciti_station.csv";
	    //public static final String OD_NODES_FILE = PARENT_DIRECTORY + "route_gen_input\\output_matrix_2018.csv";
	    public static final String FEASIBLE_ROUTES_FILE = PARENT_DIRECTORY + "route_gen_input\\feasibleRoutes.txt";
	    public static final String ROUTES_STYLESHEET_FILE = PARENT_DIRECTORY + "route_gen_input\\route_style.xsl";
	    public static final String NODES_STYLESHEET_FILE = PARENT_DIRECTORY + "route_gen_input\\node_style.xsl";
	    public static final String STOP_FACILITY_STYLESHEET_FILE = PARENT_DIRECTORY + "route_gen_input\\stopFacility_style.xsl";
	    public static final String LINKS_STYLESHEET_FILE = PARENT_DIRECTORY + "route_gen_input\\link_style.xsl";
	    public static final String GRAPH_STYLESHEET_FILE = PARENT_DIRECTORY + "route_gen_input\\graph_style.xsl";
	    public static final String GRAPH_FILE_PATH = PARENT_DIRECTORY + "route_gen_input\\NetworkGraph.graphml";
	    public static final File   GRAPH_FILE = new File(GRAPH_FILE_PATH);
	    public static final String NETWORK_GRAPH_OUPUT_DIRECTORY = PARENT_DIRECTORY + "route_gen_input\\";    
	    
	    
	    // Transit Schedule creator helper files directory
	    public static final String SCHEDULE_STOPS_AND_LINES_HELPER_FILES_PATH = PARENT_DIRECTORY + "transitScheduleMakerHelperFiles\\";
	    public static final String SCHEDULE_LINES_HELPER_FILE = PARENT_DIRECTORY + "transitScheduleMakerHelperFiles\\transitLineList.txt";
	    public static final String SCHEDULE_STOPS_HELPER_FILE = PARENT_DIRECTORY + "transitScheduleMakerHelperFiles\\transitStopList.txt";
	    
	    // GTFS directory
	    public static final String GTFS_FEED = PARENT_DIRECTORY + "gtfsInputs\\31Mar2018_MyCiTi_gtfs.txt";
	    public static final String COMPRESSED_GTFS_GZIP_DIRECTORY = PARENT_DIRECTORY + "gtfsInputs\\gtfs_output\\";
	    public static final String COMPRESSED_GTFS_GZIP_TRANSIT_SCHEDULE_FILE = PARENT_DIRECTORY + "gtfsInputs\\gtfs_output\\transitSchedule.xml.gz";
	    public static final String COMPRESSED_GTFS_GZIP_TRANSIT_NETWORK_FILE = PARENT_DIRECTORY + "gtfsInputs\\gtfs_output\\transitNetwork.xml.gz";
	    public static final String COMPRESSED_GTFS_GZIP_TRANSIT_VEHICLE_FILE = PARENT_DIRECTORY + "gtfsInputs\\gtfs_output\\transitVehicles.xml.gz";
	    public static final String DECOMPRESSED_GTFS_XML_DIRECTORY = PARENT_DIRECTORY + "gtfsInputs\\gtfs_output\\";
	    public static final String DECOMPRESSED_TRANSIT_SCHEDULE_XML_FILE = PARENT_DIRECTORY + "gtfsInputs\\gtfs_output\\transitSchedule.xml";
	    public static final String DECOMPRESSED_TRANSIT_NETWORK_XML_FILE = PARENT_DIRECTORY + "gtfsInputs\\gtfs_output\\transitNetwork.xml";
	    public static final String DECOMPRESSED_TRANSIT_VEHICLE_XML_FILE = PARENT_DIRECTORY + "gtfsInputs\\gtfs_output\\transitVehicles.xml";

}
