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
package playground.onnene.exampleCode;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import au.com.bytecode.opencsv.CSVReader;

  
/**
 * @author Amamifechukwuka
 *
 */
public class testingPopulation {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		String startFile = args[0]; // csv input
        String outFile = args[1]; //"./outData.xml"; // xml output
        //int counter = 0;

        try {
            CSVReader reader = new CSVReader(new FileReader(startFile));
            String[] line = null;

            String[] header = reader.readNext();

            List out = new ArrayList();

            while((line = reader.readNext())!=null){
            	//counter++;
            	
            	//System.out.println(line[0] + " " + line[1] + " " + line[2]);
            	
                List<String[]> item = new ArrayList<String[]>();
                    //for (int i = 0; i < header.length; i++) {
                    String[] keyVal = new String[2];
                    
                    String string = header[2];
                    
                    System.out.println(string);
                    String val = line[2];
//                    keyVal[0] = string;
                    keyVal[1] = val;
                    item.add(keyVal);
                //}
                out.add(item);
            }

            XStream xstream = new XStream();

            xstream.toXML(out, new FileWriter(outFile,false));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("end");

	}

}
