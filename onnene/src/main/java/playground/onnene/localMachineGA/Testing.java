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
		
//		try {
//			
//			File ensembleToCopy = new File("./output/matsimOutput/0/ensembleRuns0.txt");
//			File ensembleDest = new File("./input/output/NSGAII/matsimOutput/");
//			
//			//File ensembleDest = new File(RunSimulationBasedTransitOptimisation.matsimOutput.toString());
//						
//			if (new File(ensembleDest + File.separator + ensembleToCopy.getName()).exists()) {
//				
//				File[] ff = ensembleDest.listFiles();
//				
//				
//				int newFileNum = Integer.parseInt(ff[ff.length-1].getName().replaceAll("\\D+","")) + 1;
//			
//				//File dest = new File(ensembleToCopy.getAbsolutePath() + "_" + UUID.randomUUID().toString());			
//				
//				///File dest = new File(ensembleToCopy.getAbsolutePath() + "_" + Integer.parseInt(ff[-1].getName().substring(-1)) + 1);
//				
//				ensembleToCopy.renameTo(new File (ensembleDest.getAbsolutePath() + File.separator + "ensembleRuns" + newFileNum + ".txt"));			
//				
//				//Log.info(ensembleToCopy);
//				//org.apache.commons.io.FileUtils.copyDirectoryToDirectory(dest, ensembleDest);
//								
//			} else {
//				
//					
//					//FileUtils.copyFile(ensembleToCopy, ensembleDest);
//				
//					org.apache.commons.io.FileUtils.copyFileToDirectory(ensembleToCopy, ensembleDest);
//			}
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		
		final int totalDemand = 38569;
		
		System.out.println(26372/totalDemand); 
		
		System.out.println((26372)  * 100.0/totalDemand) ;
		System.out.println((float)(26372/totalDemand) * 100.0);

	
	}

}
