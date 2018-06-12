package playground.onnene.routeGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.GraphMLImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;

import playground.onnene.ga.DirectoryConfig;
import playground.onnene.transitScheduleMaker.FileMakerUtils;

public class RouteSetGen {

//	private static final String SOURCE_SINK_PAIR_FILE_PATH = "C:\\Users\\NNNOB\\Documents\\GitHub\\input\\routeGen_Input\\output_matrix_with_flow3.csv";
//	private static final File GRAPH_FILE = new File("C:\\Users\\NNNOB\\Documents\\GitHub\\input\\routeGen_Input\\graphStyleSheet.graphml");

	public static void main(String[] args) throws IOException {
		
		int numLines = FileMakerUtils.count(DirectoryConfig.SCHEDULE_LINES_HELPER_FILE);
		//System.out.print(numLines);
		//System.out.println();
				
		//int lenOfIndividual = getPoolOfFeasibleRoutes2().size() / numLines;
		
		//System.out.print(lenOfIndividual);
		//System.out.println();
		
		try {
			
			List temp = getPoolOfFeasibleRoutes2();
			int numOfSplit = temp.size()/numLines;
			
			System.out.print(splitPopulationIntoIndividuals(temp, numOfSplit));
		} catch (NullPointerException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Graph<CustomVertex, CustomEdge> graph;
//		
//		List parents = new ArrayList();
//
//		try (BufferedReader br = new BufferedReader(new FileReader(SOURCE_SINK_PAIR_FILE_PATH))) {
//
//			graph = getGraphFromGraphMLFile();
//
//			List<GraphPath<CustomVertex, CustomEdge>> totalPaths = new ArrayList<>();
//			
//			
//			int counter = 0;
//			String line = "";
//			br.readLine(); // skip the header line
//			while ((line = br.readLine()) != null) {
//
//				counter++;
//
//				String[] od = line.split(",");
//
//				CustomVertex origin = new CustomVertex(od[0]);
//				CustomVertex destination = new CustomVertex(od[1]);
//				// CustomVertex origin = new CustomVertex("MyCiTi_332", -48792.9894897794,
//				// -3717982.169309352);
//				// CustomVertex destination = new CustomVertex("MyCiTi_307");
//
//				System.out.print("Pair: " + counter + " [" + origin + "," + destination + "]" + "\n");
//
//				List<GraphPath<CustomVertex, CustomEdge>> twoPaths = getShortestAndAlternativePathsForVertexPairs(graph,
//						origin, destination);
//				totalPaths.addAll(twoPaths);
//				
//				System.out.println(" -> " + twoPaths);
//				System.out.println();
//				// System.out.println(" -> " + twoPaths.size());
//
//				// if (counter == 10)
//				// break;
//			}
//			    System.out.println("Total paths size: " + totalPaths);
//			    System.out.println("Total paths size: " + totalPaths.size());
//
//		} catch (IOException | ImportException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
		
	public static <T> List<List<T>> splitPopulationIntoIndividuals(List<T> list, int size) throws NullPointerException, IllegalArgumentException, IOException {
		
		
	    if (list == null) {
	        throw new NullPointerException("The list parameter is null.");
	    }

	    if (size <= 0) {
	        throw new IllegalArgumentException(
	                "The size parameter must be more than 0.");
	    }

	    List<List<T>> result = new ArrayList<List<T>>(size);

	    for (int i = 0; i < size; i++) {
	        result.add(new ArrayList<T>());
	    }

	    int index = 0;

	    for (T t : list) {
	        result.get(index).add(t);
	        index = (index + 1) % size;
	    }
	    
	    // System.out.println("size of list is " + index);
	   	    
	    
		FileWriter Routesfile = new FileWriter(DirectoryConfig.FEASIBLE_ROUTES_FILE);
		BufferedWriter outStream = new BufferedWriter(Routesfile);
		
//		int lst2Size =  lst2.size();
//		for (int k = 0; k < lst2Size; k++)
            outStream.write(result.toString());  
			outStream.flush();
        	outStream.close();
	    
	    
	    // System.out.println(result.size());

	    return result;
	}
	
	public static List getPoolOfFeasibleRoutes2() throws IOException {
		
		Graph<CustomVertex, CustomEdge> graph;
		
		List<List<String>> vertLst = new ArrayList<>();
		//List lst2 = new ArrayList();
		
		//List vertLst1 = new ArrayList();
		 
		List<List<List<String>>> lol = new ArrayList<>();
		int numLines = FileMakerUtils.count(DirectoryConfig.SCHEDULE_LINES_HELPER_FILE);
				
		//List parents = new ArrayList();

		try (BufferedReader br = new BufferedReader(new FileReader(DirectoryConfig.OD_NODES_FILE))) {

			graph = getGraphFromGraphMLFile();
			

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
				//System.out.println();
				//System.out.print("Pair: " + counter + " [" + origin + "," + destination + "]" + "\n");
				//System.out.println();
				List<GraphPath<CustomVertex, CustomEdge>> totalPaths = new ArrayList<>();
				List<GraphPath<CustomVertex, CustomEdge>> twoPaths = getShortestAndAlternativePathsForVertexPairs(graph, origin, destination);
				
				totalPaths.addAll(twoPaths);
								
				 //System.out.println(" -> " + twoPaths);
				 //System.out.println();
//				 System.out.println(" -> " + twoPaths.size());
				 
				 //System.out.println(" -> " + totalPaths);

				// if (counter == 10)*****************************
				// break;
				 
				 
				 
				 //lol.add(twoPaths);
				 
				
				 
				 //System.out.println(" -> " + lol );
				 
				 
				 
//				    System.out.println("Total paths size: " + totalPaths);
//				    System.out.println("Total paths size: " + totalPaths.size());
				
				
//    			for (int i = 0 ; i < totalPaths.size(); i++) {
//									
//					List<CustomVertex> tempLst = null;
//					
//					List<String> vertStrLst = null;
//					
//					//totalPaths.get(i).getVertexList();
//					
//					vertStrLst = new ArrayList<>(totalPaths.get(i).getVertexList().size());
//					
//					//System.out.println(arrlst);
//					
//					//System.out.println(" -> " + twoPaths);
//					
//					tempLst = totalPaths.get(i).getVertexList();
//					
//					//System.out.println(tempLst);
//					
//					vertLst1.add(tempLst);
//
//				}
				 
				 //System.out.println(" total Paths -> " + totalPaths);
				 //System.out.println();
					
					
					//System.out.print("vertex list of lists" + vertLst1);
					 List<List<String>> vertLst1 = new ArrayList<>();
					 
					 
						for (int i = 0 ; i < totalPaths.size(); i++) {
													
							List<CustomVertex> tempLst = null;
							List<String> vertStrLst = null;
							
							//totalPaths.get(i).getVertexList();
							
							vertStrLst = new ArrayList<>(totalPaths.get(i).getVertexList().size());
							
							//System.out.println(arrlst);
							
							//System.out.println(" -> " + twoPaths);
							
							tempLst = totalPaths.get(i).getVertexList();
							
							//System.out.println(tempLst);
							
//							for (int j = 0; j < tempLst.size(); j++  ) {
								
//								System.out.print(tempLst.get(j));
								
								for (CustomVertex cv: tempLst) {
									
									String vertexString = cv.getId().toString();
									
									//System.out.print(vertexString);
									
									vertexString = vertexString.replaceAll("\\D+", "");
									
									vertStrLst.add(vertexString);
									
								}
								
								
								vertLst1.add(vertStrLst);
								
								
								
								//System.out.print(vertLst1.get(vertLst1.size()-1));
									
						
						 //System.out.println();
						//System.out.println();
						
						}
						
						lol.add(vertLst1);
						
						
						//System.out.print(vertLst1);
						//System.out.println();
												

			}
			
			//List<List<String>> lll = null;
			
			
			//System.out.print(lol);
			
			List<Integer> check = new ArrayList<Integer>();
			
			for (int z = 0; z<lol.size(); z++) {
				
				check.add(lol.get(z).size());
								
			}
						
			int sizeOfLongestList = Collections.max(check);
			
			
			 //= new ArrayList<>();
			Random r = new Random();
			List<List<String>> temp = null;
			
			for (int i = 0; i<sizeOfLongestList; i++) {
				
				
				for (int y = 0; y<lol.size(); y++) {
					//int randomNum = r.nextInt();
					
					
					temp = lol.get(y);
					//System.out.println(RouteSetGen.choice(temp, r));
					vertLst.add(RouteSetGen.choice(temp, r));
					//System.out.println();
					//System.out.print(ooo);
							
					//vertLst.add(PRNG.nextItem(ooo));	
					
					//System.out.println();
				}
				 
				
			}
			
			
			//System.out.print(vertLst);

			//System.out.print("vertex list of lists" + vertLst1);
			
//			for (int i = 0; i < 2; i++) {
//				List lst1 = new ArrayList();
//		
//				for (int j = 0; j < 47; j++) {
//					
//					   //System.out.print(PRNG.nextItem(lst) + "\n");
//					   
//					   lst1.add(vertLst.get(j));
//	
//				   }
//				
//				lst2.add(lst1);
//				
//				System.out.print(lst2.size());
//			}
			 

//			FileWriter Routesfile = new FileWriter("C:\\Users\\NNNOB\\Documents\\GitHub\\input\\routeGen_Input\\FeasibleRoutes.txt");
//			BufferedWriter outStream = new BufferedWriter(Routesfile);
//			
//			int lst2Size =  lst2.size();
//			for (int k = 0; k < lst2Size; k++)
//                outStream.write(lst2.get(k).toString());  
//				outStream.flush();
//            	outStream.close();
            // System.out.println("Data saved.");
//			String jsonPrettyPrintString = vertLst.toString();
//			
//			Routesfile.write(jsonPrettyPrintString );

		} catch (IOException | ImportException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			
//		System.out.println("Total paths:" + vertLst);
		//System.out.println("Length of total paths list: " + vertLst.size());
//		System.out.println();
		
		int remainder = vertLst.size() % numLines;
		int origLen = vertLst.size();		
		int temp = origLen - remainder;
		
//		System.out.print(origLen - remainder);
//		
//	    System.out.println("rem" + " " + remainder);
//	    System.out.println("origLen" + " " +origLen);
//		System.out.println(vertLst.get(93));
//		System.out.println(vertLst.get(94));
//		System.out.println(vertLst.get(95));
		//System.out.println(vertLst.subList(0, temp));
		
		//System.out.println("vertLst.subList(0, 94)" + vertLst.subList(0, 94));
		
		//System.out.println("vertLst.subList(0, 95)" + vertLst.subList(0, 95));
		
		//vertLst = vertLst.subList(0, 95); 
		
		vertLst = vertLst.subList(0, temp);
		//System.out.println(vertLst.size());
		//return lst2;
		
		return vertLst;
		
	}
	
	
	public static <E> E choice(Collection<? extends E> coll, Random rand) {
	    if (coll.size() == 0) {
	        return null; // or throw IAE, if you prefer
	    }

	    int index = rand.nextInt(coll.size());
	    if (coll instanceof List) { // optimization
	        return ((List<? extends E>) coll).get(index);
	    } else {
	        Iterator<? extends E> iter = coll.iterator();
	        for (int i = 0; i < index; i++) {
	            iter.next();
	        }
	        return iter.next();
	    }
	}
	
	
	
	public static List getPoolOfFeasibleRoutes() throws IOException {
		
		Graph<CustomVertex, CustomEdge> graph;
		List<GraphPath<CustomVertex, CustomEdge>> totalPaths = new ArrayList<>();
		List vertLst = new ArrayList();
		List lst2 = new ArrayList();
		
		int numLines = FileMakerUtils.count(DirectoryConfig.SCHEDULE_LINES_HELPER_FILE);
				
		//List parents = new ArrayList();

		try (BufferedReader br = new BufferedReader(new FileReader(DirectoryConfig.OD_NODES_FILE))) {

			graph = getGraphFromGraphMLFile();

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

				System.out.print("Pair: " + counter + " [" + origin + "," + destination + "]" + "\n");
				
				List<GraphPath<CustomVertex, CustomEdge>> twoPaths = getShortestAndAlternativePathsForVertexPairs(graph, origin, destination);
				totalPaths.addAll(twoPaths);
				
				 //System.out.println(" -> " + twoPaths);
//				 System.out.println();
//				 System.out.println(" -> " + twoPaths.size());

				// if (counter == 10)*****************************
				// break;
				
								
			}
//			    System.out.println("Total paths size: " + totalPaths);
//			    System.out.println("Total paths size: " + totalPaths.size());
////			    
			
			for (int i = 0; i < totalPaths.size(); i++) {
				
				List<CustomVertex> tempLst = null;
				List<String> vertStrLst = null;
				
				//totalPaths.get(i).getVertexList();
				
				vertStrLst = new ArrayList<>(totalPaths.get(i).getVertexList().size());
				
				//System.out.println(arrlst);
				
				//System.out.println(" -> " + twoPaths);
				
				tempLst = totalPaths.get(i).getVertexList();
				
//				for (int j = 0; j < tempLst.size(); j++  ) {
					
//					System.out.print(tempLst.get(j));
					
					for (CustomVertex cv: tempLst) {
						
						String vertexString = cv.getId().toString();
						
						//System.out.print(vertexString);
						
						vertexString = vertexString.replaceAll("\\D+", "");
						
						vertStrLst.add(vertexString);
						
					}
					
					vertLst.add(vertStrLst);
					
					//System.out.print(vertLst);
					
					
			}
			
			
//			for (int i = 0; i < 2; i++) {
//				List lst1 = new ArrayList();
//		
//				for (int j = 0; j < 47; j++) {
//					
//					   //System.out.print(PRNG.nextItem(lst) + "\n");
//					   
//					   lst1.add(vertLst.get(j));
//	
//				   }
//				
//				lst2.add(lst1);
//				
//				System.out.print(lst2.size());
//			}
			 

//			FileWriter Routesfile = new FileWriter("C:\\Users\\NNNOB\\Documents\\GitHub\\input\\routeGen_Input\\FeasibleRoutes.txt");
//			BufferedWriter outStream = new BufferedWriter(Routesfile);
//			
//			int lst2Size =  lst2.size();
//			for (int k = 0; k < lst2Size; k++)
//                outStream.write(lst2.get(k).toString());  
//				outStream.flush();
//            	outStream.close();
            // System.out.println("Data saved.");
//			String jsonPrettyPrintString = vertLst.toString();
//			
//			Routesfile.write(jsonPrettyPrintString );

		} catch (IOException | ImportException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
//		System.out.println("Total paths:" + vertLst);
		//System.out.println("Length of total paths list: " + vertLst.size());
//		System.out.println();
		
		int remainder = vertLst.size() % numLines;
		int origLen = vertLst.size();
		
		int temp = origLen - remainder;
		
//		System.out.print(origLen - remainder);
//		
//	    System.out.println("rem" + " " + remainder);
//	    System.out.println("origLen" + " " +origLen);
//		System.out.println(vertLst.get(93));
//		System.out.println(vertLst.get(94));
//		System.out.println(vertLst.get(95));
		//System.out.println(vertLst.subList(0, temp));
		
		//System.out.println("vertLst.subList(0, 94)" + vertLst.subList(0, 94));		
		//System.out.println("vertLst.subList(0, 95)" + vertLst.subList(0, 95));		
		//vertLst = vertLst.subList(0, 95); 
		
		vertLst = vertLst.subList(0, temp);
		//System.out.println(vertLst.size());
		//return lst2;
		
		return vertLst;
		
	}
	

	public static List<GraphPath<CustomVertex, CustomEdge>> getShortestAndAlternativePathsForVertexPairs(
			Graph<CustomVertex, CustomEdge> graph, CustomVertex startVertex, CustomVertex endVertex) throws Exception {
		
		List<GraphPath<CustomVertex, CustomEdge>> twoPaths = new ArrayList<>();
		GraphPath<CustomVertex, CustomEdge> shortestPath = null;
		GraphPath<CustomVertex, CustomEdge> alternativeShortestPath = null;

		// getShortestPath(graph, startVertex, endVertex);
		List<GraphPath<CustomVertex, CustomEdge>> kShortestPaths = getKShortestPaths(graph, startVertex, endVertex);
		
		//System.out.println("size of k Shortest Path:" + kShortestPaths.size());
		//System.out.println(kShortestPaths);
		
		List<GraphPath<CustomVertex, CustomEdge>> listOfPaths = null;
		
		listOfPaths = getAlternativeShortestPath(kShortestPaths);
		
		//System.out.println("size of all listOfPaths:" + listOfPaths.size());
		
		if (kShortestPaths.size() >= 1) {
			shortestPath = kShortestPaths.get(0);
			twoPaths.add(shortestPath);
		}
		
		for (int k = 0; k < listOfPaths.size(); k++) {
			
			if (listOfPaths.size() >= 2) {
				
				alternativeShortestPath = listOfPaths.get(k);
				
				//System.out.println("size of k Shortest Path:" + kShortestPaths.size());
				
				if (alternativeShortestPath != null)
					twoPaths.add(alternativeShortestPath);
				
				//System.out.println("size of alt Shortest Path:" + twoPaths.size());
			}
		}
		//System.out.println("size of alt Shortest Path:" + twoPaths);
		//System.out.println("size of all Shortest Path:" + twoPaths.size());

//		System.out.println("Shortest Path: " + shortestPath.getVertexList());
//		 System.out.println("Alternative Shortest Path: " + alternativeShortestPath);

		return twoPaths;
	}

	public static List<GraphPath<CustomVertex, CustomEdge>> getAlternativeShortestPath(
			List<GraphPath<CustomVertex, CustomEdge>> kShortestPaths) throws Exception {
		
		//System.out.println(kShortestPaths);
		GraphPath<CustomVertex, CustomEdge> shortestPath = kShortestPaths.get(0);
		
		//System.out.println("size of kshortests" + kShortestPaths.size());
		
		List<GraphPath<CustomVertex, CustomEdge>> listOfPaths = new ArrayList<>();

		GraphPath<CustomVertex, CustomEdge> graphPath = null;
		for (int i = 1; i < kShortestPaths.size(); i++) {
			
			graphPath = kShortestPaths.get(i);
			//System.out.println("graphPath" + graphPath);
			
			//System.out.println(i);
			
			if (isPathDissimilar(shortestPath, graphPath)) {
												
				if (isPathReasonablyLonger(shortestPath, graphPath)) {
					
					listOfPaths.add(graphPath);	
					//return graphPath;
				}
					
			//return graphPath;
			}
		}

		// throw new Exception("No alternative path found for shortest path: " +
		// shortestPath);
		//System.out.println("type of graphPath" + graphPath.getClass());
//		System.out.println("type of graphPath" + graphPath.getClass());
//		System.out.println("type of shortest Path" + shortestPath.getClass());
//		System.out.println("feasible paths" + listOfPaths);
//		System.out.println("length of feasible paths" + listOfPaths.size());
		//return graphPath;
		return listOfPaths;
		
		//return null;
	}

	public static boolean isPathReasonablyLonger(GraphPath<CustomVertex, CustomEdge> shortestPath,
			GraphPath<CustomVertex, CustomEdge> graphPath) {
		double shortestPathInKm = shortestPath.getWeight();
		double graphPathInKm = graphPath.getWeight();
		//System.out.println("Shortest path in km: " + shortestPathInKm);
		// System.out.println("1.5 times: " + 1.5 * shortestPathInKm);
		//System.out.println("current in km: " + graphPathInKm);
		
		return graphPathInKm <= (1.2 * shortestPathInKm);
	}

	public static boolean isPathDissimilar(GraphPath<CustomVertex, CustomEdge> shortestPath,
			GraphPath<CustomVertex, CustomEdge> graphPath) {
		List<CustomVertex> vertexList1 = shortestPath.getVertexList();
		List<CustomVertex> vertexList2 = graphPath.getVertexList();

		int minNumberOfDissimlarVertices = (int) Math.ceil(vertexList1.size() * 0.5);
				
		//System.out.println("number of vertices: " + vertexList1.size());		
		//System.out.println("number of vertices: " + vertexList2.size());		
		//System.out.println("number of vertices: " + graphPath.getWeight());		
		//System.out.println("minNumberOfDissimlarVertices: " + minNumberOfDissimlarVertices);

		// System.out.println("nVertices in shortest path: " + vertexList1.size());
		// System.out.println("minNumberOfDissimlarVertices: " +
		// minNumberOfDissimlarVertices);

		int currentNumberOfDissimilarVertices = 0;
		for (CustomVertex vertex : vertexList2) {
						
			if (!vertexList1.contains(vertex)) {
				
				//System.out.println("does not contain the vertex ");
				
				//System.out.println("currentNumberOfDissimilarVertices: " + currentNumberOfDissimilarVertices);
				currentNumberOfDissimilarVertices++;
				
				if (currentNumberOfDissimilarVertices >= minNumberOfDissimlarVertices)
					return true;
				
			}
			
			
			
			//System.out.println("currentNumberOfDissimilarVertices: " + currentNumberOfDissimilarVertices);
		}

		// System.out.println("nDissimilar vertices: " +
		// currentNumberOfDissimilarVertices);
		return false;
	}

	public static Graph<CustomVertex, CustomEdge> getGraphFromGraphMLFile() throws IOException, ImportException {

		String graphStr = new String(Files.readAllBytes(Paths.get(DirectoryConfig.GRAPH_FILE.getAbsolutePath())));

		Graph<CustomVertex, CustomEdge> graph = new DirectedWeightedPseudograph<>(CustomEdge.class);
		GraphImporter<CustomVertex, CustomEdge> importer = createImporter();
		importer.importGraph(graph, new StringReader(graphStr));
		return graph;
	}

//	public static void getShortestPath(Graph<CustomVertex, CustomEdge> directedGraph, CustomVertex startVertex,
//			CustomVertex endVertex) {
//		DijkstraShortestPath<CustomVertex, CustomEdge> dijkstraShortestPath = new DijkstraShortestPath(directedGraph);
//		GraphPath<CustomVertex, CustomEdge> path = dijkstraShortestPath.getPath(startVertex, endVertex);
//
//		//System.out.println("Shortest path:");
//		if (path != null)
//			printPath(path);
//		else
//			System.out.println("No path exists between source and sink");
//	}

//	public static void printPath(GraphPath<CustomVertex, CustomEdge> path) {
//		List<CustomVertex> vertices = path.getVertexList();
//		System.out.println("Total weight: " + path.getWeight());
//		System.out.println(vertices);
		// for (CustomVertex customVertex : vertices) {
		// System.out.print(customVertex.getId());
		// System.out.print(" " + customVertex.getX());
		// System.out.println(" " + customVertex.getY());
		// }
		// for (CustomEdge edge : path.getEdgeList()) {
		// System.out.print(edge.getSource());
		// System.out.print(" " + edge.getTarget());
		// System.out.println(" " + edge.getWeight());
		// }
//	}

	public static List<GraphPath<CustomVertex, CustomEdge>> getKShortestPaths(
			Graph<CustomVertex, CustomEdge> directedGraph, CustomVertex startVertex, CustomVertex endVertex) {
		KShortestPaths<CustomVertex, CustomEdge> kShortestPaths = new KShortestPaths<>(directedGraph, 5);
		List<GraphPath<CustomVertex, CustomEdge>> paths = kShortestPaths.getPaths(startVertex, endVertex);

		// System.out.print("Pair: " + " [" + startVertex + "," + endVertex + "]" + "\n");
		//System.out.println("KShortest paths:" + paths.size());
		//System.out.println("->route" + paths);
//		System.out.println();
//		 for (GraphPath<CustomVertex, CustomEdge> graphPath : paths)
//		 System.out.println(graphPath.getWeight());

		return paths;
	}

	public static GraphMLImporter<CustomVertex, CustomEdge> createImporter() {
		/*
		 * Create vertex provider.
		 *
		 * The importer reads vertices and calls a vertex provider to create them. The
		 * provider receives as input the unique id of each vertex and any additional
		 * attributes from the input stream.
		 */
		VertexProvider<CustomVertex> vertexProvider = (id, attributes) -> {

			double x = 0;
			double y = 0;
			if (attributes.containsKey("x"))
				x = Double.parseDouble(attributes.get("x").getValue());
			if (attributes.containsKey("y"))
				y = Double.parseDouble(attributes.get("y").getValue());

			return new CustomVertex(id, x, y);
		};

		/*
		 * Create edge provider.
		 *
		 * The importer reads edges from the input stream and calls an edge provider to
		 * create them. The provider receives as input the source and target vertex of
		 * the edge, an edge label (which can be null) and a set of edge attributes all
		 * read from the input stream.
		 */
		EdgeProvider<CustomVertex, CustomEdge> edgeProvider = (from, to, label, attributes) -> {
			CustomEdge CustomEdge = new CustomEdge();
			return CustomEdge;
		};

		/*
		 * Create the graph importer with a vertex and an edge provider.
		 */
		GraphMLImporter<CustomVertex, CustomEdge> importer = new GraphMLImporter<>(vertexProvider, edgeProvider);

		return importer;
	}

	static class CustomVertex {

		private String id;
		double x;
		double y;

		public CustomVertex(String id, double x, double y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}

		public CustomVertex(String string) {
			this(string, 0, 0);
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getX() {
			return x;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getY() {
			return y;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.id);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CustomVertex) {
				final CustomVertex other = (CustomVertex) obj;
				return Objects.equals(this.id, other.id);
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("(").append(this.id)
					// .append(this.x)
					// .append(this.y)
					.append(")");
			return sb.toString();
		}
	}

	static class CustomEdge extends DefaultWeightedEdge {
		private static final long serialVersionUID = 1L;

		@Override
		public Object getSource() {
			return super.getSource();
		}

		@Override
		public Object getTarget() {
			return super.getTarget();
		}

		@Override
		public double getWeight() {
			return super.getWeight();
		}
	}

	// public static void main(String[] args) {
	// getSampleGraph();
	// DirectedGraph<String, DefaultEdge> directedGraph = getSampleDirectedGraph();
	// String startVertex = "v8";
	// String endVertex = "v4";
	// printShortestPath(directedGraph, startVertex, endVertex);
	// getKShortestPaths(directedGraph, startVertex, endVertex);
	// }
	// private static void getSampleGraph() {
	// Graph<String, DefaultEdge> g = new SimpleGraph<String,
	// DefaultEdge>(DefaultEdge.class);
	// g.addVertex("v1");
	// g.addVertex("v2");
	// g.addEdge("v1", "v2");
	// }
	//
	// private static DirectedGraph<String, DefaultEdge> getSampleDirectedGraph() {
	// DirectedGraph<String, DefaultEdge> directedGraph = new
	// DefaultDirectedGraph<>(DefaultEdge.class);
	// directedGraph.addVertex("v1");
	// directedGraph.addVertex("v2");
	// directedGraph.addVertex("v3");
	// directedGraph.addVertex("v4");
	// directedGraph.addVertex("v5");
	// directedGraph.addVertex("v6");
	// directedGraph.addVertex("v7");
	// directedGraph.addVertex("v8");
	// directedGraph.addVertex("v9");
	//
	// directedGraph.addEdge("v1", "v2");
	// directedGraph.addEdge("v2", "v4");
	// directedGraph.addEdge("v3", "v1");
	// directedGraph.addEdge("v4", "v3");
	// directedGraph.addEdge("v5", "v4");
	// directedGraph.addEdge("v5", "v6");
	// directedGraph.addEdge("v6", "v7");
	// directedGraph.addEdge("v7", "v5");
	// directedGraph.addEdge("v8", "v5");
	// directedGraph.addEdge("v9", "v8");
	//
	// directedGraph.addEdge("v8", "v4");
	// return directedGraph;
	// }

}
