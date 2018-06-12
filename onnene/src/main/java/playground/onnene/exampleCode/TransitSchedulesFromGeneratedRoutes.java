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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.io.ImportException;

import playground.onnene.exampleCode.RouteSetGen.CustomEdge;
import playground.onnene.exampleCode.RouteSetGen.CustomVertex;
  
/**
 * @author Amamifechukwuka
 *
 */
public class TransitSchedulesFromGeneratedRoutes {
	
	private static final String SOURCE_SINK_PAIR_FILE_PATH = "C:\\Users\\NNNOB\\Documents\\GitHub\\input\\routeGen_Input\\output_matrix_with_flow.csv";
	private static final File GRAPH_FILE = new File(
			"C:\\Users\\NNNOB\\Documents\\GitHub\\input\\routeGen_Input\\graphStyleSheet.graphml");
	
	public static List GeneratedRoutes() {
		
		List<GraphPath<CustomVertex, CustomEdge>> totalPaths = new ArrayList<>();
		
		RouteSetGen rsg = new RouteSetGen();

		Graph<CustomVertex, CustomEdge> graph;
		
		List vertLst = new ArrayList();

		try (BufferedReader br = new BufferedReader(new FileReader(SOURCE_SINK_PAIR_FILE_PATH))) {

			graph = RouteSetGen.getGraphFromGraphMLFile();

			
			int counter = 0;
			String line = "";
			br.readLine(); // skip the header line
			while ((line = br.readLine()) != null) {

				counter++;

				String[] od = line.split(",");

				CustomVertex origin = new CustomVertex(od[0]);
				CustomVertex destination = new CustomVertex(od[1]);
				// CustomVertex origin = new CustomVertex("MyCiTi_332", -48792.9894897794,
				// -3717982.169309352);
				// CustomVertex destination = new CustomVertex("MyCiTi_307");

				//System.out.print("Pair: " + counter + " [" + origin + "," + destination + "]");
				
				
				List<GraphPath<CustomVertex, CustomEdge>> twoPaths = RouteSetGen.getShortestAndAlternativePathsForVertexPairs(graph,
						origin, destination);
				totalPaths.addAll(twoPaths);
				
				

				//System.out.println(" -> " + twoPaths.size());
				
				//GraphPath<CustomVertex, CustomEdge> routes = twoPaths.get(0);
				
				//routes.getVertexList();
				
				
				
				for (int i = 0 ; i < twoPaths.size(); i++) {
					
					twoPaths.get(i).getVertexList();
					
					List<String> vertStrLst = new ArrayList<>(twoPaths.get(i).getVertexList().size());
					
					//System.out.println(arrlst);
					
					List<CustomVertex> tempLst = twoPaths.get(i).getVertexList();
					
//					for (int j = 0; j < tempLst.size(); j++  ) {
						
//						System.out.print(tempLst.get(j));
						
						for (CustomVertex cv: tempLst) {
							
							String verterxString = cv.getId().toString();
							
							verterxString = verterxString.replaceAll("\\D+", "");
							
							vertStrLst.add(verterxString);
							
							//System.out.print("->" + cv.toString());
//							
//							
						}
//						
//					}
					
					//System.out.println(" -> " + twoPaths.get(i).getVertexList());
					
					//arrlst.add((twoPaths.get(i).getVertexList()));
					vertLst.add(vertStrLst);
					
					//System.out.println(vertLst);
					
					
					
					
				}
				
				
				//System.out.println(Collections.addAll((Arrays.asList(twoPaths.get(i).getVertexList()))));
				
				// if (counter == 10)
				// break;
				
				
				
			}

			//System.out.println(vertLst);
			// System.out.println("Total paths size: " + totalPaths.size());

		} catch (IOException | ImportException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(" -> " + totalPaths);
		
		//System.out.println(vertLst);
		return vertLst;
		
		//return vertLst.iterator();
	}




}
