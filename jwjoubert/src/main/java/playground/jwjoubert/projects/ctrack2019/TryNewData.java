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
package playground.jwjoubert.projects.ctrack2019;

import org.apache.log4j.Logger;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.Counter;
import org.matsim.up.acceleration.grid.DigiGrid3D;
import org.matsim.up.acceleration.grid.DigiGrid_XYZ;
import org.matsim.up.utils.DateString;
import org.matsim.up.utils.FileUtils;
import org.matsim.up.utils.Header;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TryNewData {
    private static final Logger LOG = Logger.getLogger(TryNewData.class);
    private static final String FOLDER = "/Users/jwjoubert/Downloads/Wamatha/";
    public static final String CLEAN_FILE_NO_HEADER = FOLDER + "processed/clean.csv";

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TIME = 1;
    private static final int COLUMN_LON = 2;
    private static final int COLUMN_LAT = 3;
    private static final int COLUMN_X = 4;
    private static final int COLUMN_Y = 5;
    private static final int COLUMN_Z = 6;
    private static final int COLUMN_SPEED = 7;
    private static final int COLUMN_SPEED_LIMIT = 8;
    private static final int COLUMN_ROAD_TYPE = 9;
    private static final int COLUMN_GPS_LOCK = 10;

    private static final double DEPTH_START = 1008;
    private static final double DEPTH_END = 1010;
    private static final double DEPTH_STEP = 1;

    private Map<String, Integer> vehicleMap = new TreeMap<>();

    public static void main(String[] args) {
        Header.printHeader(TryNewData.class, args);
        TryNewData tnd = new TryNewData();
        try {
            tnd.cleanupInput();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not complete the run.");
        }
        tnd.run();
        Header.printFooter();
    }

    public void run() {
        DigiGrid_XYZ grid = new DigiGrid_XYZ(20.0);
        grid.setInputHasHeader(false);
        grid.setInputColumns(COLUMN_X, COLUMN_Y, COLUMN_Z);
        List<Double> riskThresholds = new ArrayList<>();
        riskThresholds.add(0.960);
        riskThresholds.add(0.996);
        riskThresholds.add(0.9995);
        riskThresholds.add(1.00);
        grid.setRiskThresholds(riskThresholds);
        grid.setupGrid(CLEAN_FILE_NO_HEADER);

        /* Populate the grid */
        BufferedReader br = IOUtils.getBufferedReader(CLEAN_FILE_NO_HEADER);
        Counter counter = new Counter("  lines # ");
        try {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] sa = line.split(",");
                double x = Double.parseDouble(sa[COLUMN_X]);
                double y = Double.parseDouble(sa[COLUMN_Y]);
                double z = Double.parseDouble(sa[COLUMN_Z]);
                grid.incrementCount(grid.getClosest(x, y, z), 1.0);
                counter.incCounter();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read from " + CLEAN_FILE_NO_HEADER);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot close " + CLEAN_FILE_NO_HEADER);
            }
        }
        counter.printCounter();
        grid.rankGridCells();
        grid.writeCellCountsAndRiskClasses(FOLDER + "processed/");

        grid.setSnapshotsFolder(FOLDER + "processed/");
        grid.setVisualiseOnScreen(false);
        grid.setVisual(DigiGrid3D.Visual.SLICE);
        for (double depth = DEPTH_START; depth <= DEPTH_END; depth += DEPTH_STEP) {
            grid.setSliceDepth(depth);
            grid.visualiseGrid();
        }
    }

    private void cleanupInput() throws IOException {
        LOG.info("Cleaning and combining multiple input files.");

        /* Check and remove cleaned data. */
        if (new File(CLEAN_FILE_NO_HEADER).exists()) {
            LOG.error("Remove the existing clean data file first: " + CLEAN_FILE_NO_HEADER);
            throw new RuntimeException("The clean data set already exists");
        }

        List<File> files = FileUtils.sampleFiles(new File(FOLDER + "Wamatha/"), Integer.MAX_VALUE, FileUtils.getFileFilter("csv.gz"));
//        List<File> files = FileUtils.sampleFiles(new File(FOLDER + "Wamatha/"), 1, FileUtils.getFileFilter("csv.gz"));

        BufferedReader br;
        BufferedWriter bw = IOUtils.getAppendingBufferedWriter(CLEAN_FILE_NO_HEADER);

        Calendar earliest = new DateString();
        earliest.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
        earliest.setTimeInMillis(Long.MAX_VALUE);
        Calendar latest = new DateString();
        latest.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
        latest.setTimeInMillis(Long.MIN_VALUE);

        Counter counter = new Counter("   total lines # ");
        try {
            for (File file : files) {
                LOG.info("   parsing " + file.getName());
                br = IOUtils.getBufferedReader(file.getAbsolutePath());
                String line = br.readLine(); /* Header */
                while ((line = br.readLine()) != null) {
                    String[] sa = line.split(",");
                    String id = sa[COLUMN_ID];
                    vehicleMap.putIfAbsent(id, 0);
                    vehicleMap.put(id, vehicleMap.get(id) + 1);
                    String timeString = sa[COLUMN_TIME];
                    Calendar date = new DateString();
                    date.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
                    date.setTimeInMillis(Long.parseLong(timeString) * 1000);
                    if (date.compareTo(earliest) < 0) {
                        earliest = date;
                    }
                    if (date.compareTo(latest) > 0) {
                        latest = date;
                    }
                    date.setTimeInMillis(Long.parseLong(timeString) * 1000);
                    double lon = Double.parseDouble(sa[COLUMN_LON]);
                    double lat = Double.parseDouble(sa[COLUMN_LAT]);
                    double x = Double.parseDouble(sa[COLUMN_X]);
                    double y = Double.parseDouble(sa[COLUMN_Y]);
                    double z = Double.parseDouble(sa[COLUMN_Z]);
                    double speed = Double.parseDouble(sa[COLUMN_SPEED]);
                    double speedLimit = Double.parseDouble(sa[COLUMN_SPEED_LIMIT]);
                    double roadType = Double.parseDouble(sa[COLUMN_ROAD_TYPE]);
                    int gpsLock = Integer.parseInt(sa[COLUMN_GPS_LOCK]);
                    if (isLineValid(lon, lat, gpsLock) && isVehicleValid(id)) {
                        bw.write(line);
                        bw.newLine();
                        counter.incCounter();
                    }
                }
                br.close();
            }
        } finally {
            bw.close();
        }
        counter.printCounter();
        LOG.info("Done cleaning the data:");
        LOG.info("   earliest date: " + earliest.toString());
        LOG.info("     latest date: " + latest.toString());
        for (String id : vehicleMap.keySet()) {
            LOG.info("   " + id + ": " + vehicleMap.get(id));
        }

        String[] command = {"gzip", CLEAN_FILE_NO_HEADER};
        Process process = new ProcessBuilder().directory(new File(FOLDER + "processed/")).command(command).start();
        while(process.isAlive()){
        }
        if (process.exitValue() != 0){
            LOG.error("Could not zip output file");
        }
    }

    private boolean isVehicleValid(String vehicleId){
//        if(vehicleId.equalsIgnoreCase("CF9E-1C-291B5-05520")){
//        if(vehicleId.equalsIgnoreCase("CF9E-D8-2BBE8-1F6E1")){
//        if(vehicleId.equalsIgnoreCase("CF9E-58-2B6BB-AD7E7")){
        if(vehicleId.equalsIgnoreCase("CF9E-F2-2D6E9-D6524")){
            return true;
        }
        return false;
//        return true;
    }

    private boolean isLineValid(double lon, double lat, int gpsLock) {
        boolean valid = true;
        if (lon == 0.0) {
            valid = false;
        }
        if (lat == 0.0) {
            valid = false;
        }
        if (gpsLock == 0) {
            valid = false;
        }
        return valid;
    }
}
