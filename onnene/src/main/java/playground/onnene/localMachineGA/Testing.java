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
package playground.onnene.localMachineGA;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * @author Onnene
 *
 */
public class Testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		/* Copy consolidated result to a folder outside the output folder
		 * where it wont be deleted in subsequent restarts */	
//		try {
//			
//			
//			File ensembleIn = new File("./output/matsimOutput/2");
//			File ensembleOut = new File("./input/output/matsimOutput/");
//			
//			System.out.println(ensembleIn.getCanonicalPath());
//			System.out.println(ensembleIn.getAbsolutePath());
//			System.out.println(ensembleIn.getName());
//			System.out.println(new File(ensembleOut.getAbsolutePath() + ensembleIn.getName() + File.separator).exists());
//			System.out.println(new File(ensembleOut.getAbsolutePath() +   File.separator + ensembleIn.getName()));
//			System.out.println(ensembleOut.getCanonicalPath());
//			System.out.println(ensembleOut.getAbsolutePath());
//			System.out.println(ensembleOut.getName());
//
//			
//			
//			if (new File(ensembleOut.getAbsolutePath() + File.separator + ensembleIn.getName()).exists()) {
//				
//				
//				
//				//org.apache.commons.io.FileUtils.moveDirectory(org.apache.commons.io.FileUtils.getFile(ensembleIn.getAbsoluteFile()), org.apache.commons.io.FileUtils.getFile(ensembleIn.getAbsolutePath() + "_1"));
//				
//				File dest = new File(ensembleIn.getAbsolutePath() + "_1");
//				
//				ensembleIn.renameTo(dest);
//				
//				//org.apache.commons.io.FileUtils.copyDirectoryToDirectory(ensembleIn, ensembleOut);
//				
//				org.apache.commons.io.FileUtils.copyDirectoryToDirectory(dest, ensembleOut);
//				//org.apache.commons.io.FileUtils.copyDirectoryToDirectory(new File(ensembleIn.getAbsolutePath() + "_1"), ensembleOut);
//				
//			}
//			
//			else {
//				
//				org.apache.commons.io.FileUtils.copyDirectoryToDirectory(ensembleIn, ensembleOut);
//			}
//			
//			
//			
//			
////			for(File file: ensembleOut.listFiles()) 
////				if (file.isDirectory()) {
////					for(File f: file.listFiles()) 
////						if (!f.getName().equals("ensembleRuns.txt")) 
////							f.delete();
////		    
////				}
//			//FileUtils.copyDirectoryStructure(ensembleIn, new File("./input/output/matsimOutput/"));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
		
		
		
//		File matsimOutput = new File("./input/output/matsimOutput/");
//		for(File file: matsimOutput.listFiles()) 
//			if (file.isDirectory()) {
//				for(File f: file.listFiles()) 					
//						if (!f.getName().equals("ensembleRuns.txt")) 
//							f.delete();
//				
		System.out.println(UUID.randomUUID().toString());
		
		
		System.out.println(generateRandomStringToken(16));

	}
	
	public static String generateRandomStringToken(int byteLength) {
    	
    	SecureRandom sr = new SecureRandom();
    	byte[] token = new byte[byteLength];
    	sr.nextBytes(token);
    	return new BigInteger(1, token).toString(16);
    }
	
	

}
