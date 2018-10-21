package playground.onnene.routeGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

import org.apache.log4j.Logger;
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
import org.matsim.up.utils.Header;

import playground.onnene.ga.ProblemUtils;

public class RouteSetGen {
	
	static int numLines = ProblemUtils.numberOfLines();
	private static final Logger log = Logger.getLogger(RouteSetGen.class);

	public static void main(String[] args) throws IOException {
		
		Header.printHeader(RouteSetGen.class, args);
		RouteSetGen rsg = new RouteSetGen();
	
		List<List<String>> temp = rsg.getFeasibleRoutesByStratifiedSampling();
		
		int numOfSplit = temp.size()/numLines;
		
		log.info(rsg.splitPopulationIntoIndividuals(temp, numOfSplit));
		
		Header.printFooter();
			
	}

		
	public <T> List<List<T>> splitPopulationIntoIndividuals(List<T> list, int size) throws NullPointerException, IllegalArgumentException, IOException {
		
		
	    if (list == null) {
	        throw new NullPointerException("The list parameter is null.");
	    }

	    if (size <= 0) {
	        throw new IllegalArgumentException("The size parameter must be more than 0.");
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
	    
		FileWriter Routesfile = new FileWriter("./input/routeGenInput/feasibleRoutes.txt");		
		BufferedWriter outStream = new BufferedWriter(Routesfile);
		
        outStream.write(result.toString());  
		outStream.flush();
    	outStream.close();
    
    	log.info("Generatiion complete...");

	    return result;
	}
	
	
	public List<List<String>> getFeasibleRoutesByStratifiedSampling() throws IOException {
		
		log.info("Generating Feasible Routes...");
		
		Graph<CustomVertex, CustomEdge> graph;
		List<List<String>> vertLst = new ArrayList<>();
		List<List<List<String>>> strata = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader("./input/routeGenInput/46_myciti_station.csv"))) {

			graph = getGraphFromGraphMLFile();
			
			String line = "";
			br.readLine(); // skip the header line
			while ((line = br.readLine()) != null) {

				String[] od = line.split(",");

				CustomVertex origin = new CustomVertex(od[0]);
				CustomVertex destination = new CustomVertex(od[1]);
				
				List<GraphPath<CustomVertex, CustomEdge>> totalPaths = new ArrayList<>();
				List<GraphPath<CustomVertex, CustomEdge>> twoPaths = getShortestAndAlternativePathsForVertexPairs(graph, origin, destination);
				
				totalPaths.addAll(twoPaths);
	
					 List<List<String>> vertLst1 = new ArrayList<>();
					 					 
						for (int i = 0 ; i < totalPaths.size(); i++) {
													
							List<CustomVertex> tempLst = null;
							List<String> vertStrLst = null;
				
							vertStrLst = new ArrayList<>(totalPaths.get(i).getVertexList().size());
							tempLst = totalPaths.get(i).getVertexList();
									
								for (CustomVertex cv: tempLst) {
									
									String vertexString = cv.getId().toString();
	
									vertexString = vertexString.replaceAll("\\D+", "");
									
									vertStrLst.add(vertexString);
									
								}
								
								vertLst1.add(vertStrLst);
						}
						
						strata.add(vertLst1);

			}
			
			List<Integer> check = new ArrayList<Integer>();
			
			for (int z = 0; z<strata.size(); z++) {
				
				check.add(strata.get(z).size());
								
			}
						
			int sizeOfLongestList = Collections.max(check);

			Random r = new Random();
			List<List<String>> temp = null;
			
			for (int i = 0; i<sizeOfLongestList; i++) {
						
				for (int y = 0; y<strata.size(); y++) {
					
					temp = strata.get(y);
					//System.out.println(temp.size());
					vertLst.add(RouteSetGen.choice(temp, r));
			
				}
										
			}
			

		} catch (IOException | ImportException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int remainder = vertLst.size() % numLines;
		int origLen = vertLst.size();		
		int temp = origLen - remainder;
		
		vertLst = vertLst.subList(0, temp);

		return vertLst;
		
	}
	
	
	public static <E> E choice(Collection<? extends E> coll, Random rand) {
	    if (coll.size() == 0) {
	        return null; // or throw IAE, if you prefer
	    }

	    int index = rand.nextInt(coll.size());
	    if (coll instanceof List) { // Optimisation
	        return ((List<? extends E>) coll).get(index);
	    } else {
	        Iterator<? extends E> iter = coll.iterator();
	        for (int i = 0; i < index; i++) {
	            iter.next();
	        }
	        return iter.next();
	    }
	}
	
	
	
	public List<List<String>> getFeasibleRoutesByRandomSampling() throws IOException {
		
		Graph<CustomVertex, CustomEdge> graph;
		List<GraphPath<CustomVertex, CustomEdge>> totalPaths = new ArrayList<>();
		List<List<String>> vertLst = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader("./input/routeGenInput/46_myciti_station.csv"))) {
			graph = getGraphFromGraphMLFile();

			int counter = 0;
			String line = "";
			br.readLine(); // skip the header line
			while ((line = br.readLine()) != null) {

				counter++;

				String[] od = line.split(",");

				CustomVertex origin = new CustomVertex(od[0]);
				CustomVertex destination = new CustomVertex(od[1]);
				
				List<GraphPath<CustomVertex, CustomEdge>> twoPaths = getShortestAndAlternativePathsForVertexPairs(graph, origin, destination);
				totalPaths.addAll(twoPaths);
							
			}
			
			for (int i = 0; i < totalPaths.size(); i++) {
				
				List<CustomVertex> tempLst = null;
				List<String> vertStrLst = null;
				vertStrLst = new ArrayList<>(totalPaths.get(i).getVertexList().size());
			
				tempLst = totalPaths.get(i).getVertexList();
					
					for (CustomVertex cv: tempLst) {
						
						String vertexString = cv.getId().toString();

						vertexString = vertexString.replaceAll("\\D+", "");
						vertStrLst.add(vertexString);
						
					}
					
					vertLst.add(vertStrLst);
		
			}
			

		} catch (IOException | ImportException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int remainder = vertLst.size() % numLines;
		int origLen = vertLst.size();
		int temp = origLen - remainder;
	
		vertLst = vertLst.subList(0, temp);
		
		return vertLst;
		
	}
	

	public static List<GraphPath<CustomVertex, CustomEdge>> getShortestAndAlternativePathsForVertexPairs(
			Graph<CustomVertex, CustomEdge> graph, CustomVertex startVertex, CustomVertex endVertex) throws Exception {
		
		List<GraphPath<CustomVertex, CustomEdge>> twoPaths = new ArrayList<>();
		GraphPath<CustomVertex, CustomEdge> shortestPath = null;
		GraphPath<CustomVertex, CustomEdge> alternativeShortestPath = null;
		List<GraphPath<CustomVertex, CustomEdge>> kShortestPaths = getKShortestPaths(graph, startVertex, endVertex);
		List<GraphPath<CustomVertex, CustomEdge>> listOfPaths = null;
		
		listOfPaths = getAlternativeShortestPath(kShortestPaths);
		
		if (kShortestPaths.size() >= 1) {
			shortestPath = kShortestPaths.get(0);
			twoPaths.add(shortestPath);
		}
		
		for (int k = 0; k < listOfPaths.size(); k++) {
			
			if (listOfPaths.size() >= 2) {
				
				alternativeShortestPath = listOfPaths.get(k);
				
				if (alternativeShortestPath != null)
					twoPaths.add(alternativeShortestPath);
				
			}
		}

		return twoPaths;
	}

	public static List<GraphPath<CustomVertex, CustomEdge>> getAlternativeShortestPath(
			List<GraphPath<CustomVertex, CustomEdge>> kShortestPaths) throws Exception {
		
		GraphPath<CustomVertex, CustomEdge> shortestPath = kShortestPaths.get(0);
		List<GraphPath<CustomVertex, CustomEdge>> listOfPaths = new ArrayList<>();

		GraphPath<CustomVertex, CustomEdge> graphPath = null;
		for (int i = 1; i < kShortestPaths.size(); i++) {
			
			graphPath = kShortestPaths.get(i);
			
			if (isPathDissimilar(shortestPath, graphPath)) {
												
				if (isPathReasonablyLonger(shortestPath, graphPath)) {
					
					listOfPaths.add(graphPath);	
				}
			}
		}

		return listOfPaths;

	}

	public static boolean isPathReasonablyLonger(GraphPath<CustomVertex, CustomEdge> shortestPath,
			GraphPath<CustomVertex, CustomEdge> graphPath) {
		double shortestPathInKm = shortestPath.getWeight();
		double graphPathInKm = graphPath.getWeight();
		
		return graphPathInKm <= (1.2 * shortestPathInKm);
	}

	public static boolean isPathDissimilar(GraphPath<CustomVertex, CustomEdge> shortestPath,
			GraphPath<CustomVertex, CustomEdge> graphPath) {
		List<CustomVertex> vertexList1 = shortestPath.getVertexList();
		List<CustomVertex> vertexList2 = graphPath.getVertexList();

		int minNumberOfDissimlarVertices = (int) Math.ceil(vertexList1.size() * 0.5);
		int currentNumberOfDissimilarVertices = 0;
		for (CustomVertex vertex : vertexList2) {
						
			if (!vertexList1.contains(vertex)) {

				currentNumberOfDissimilarVertices++;
				
				if (currentNumberOfDissimilarVertices >= minNumberOfDissimlarVertices)
					return true;
				
			}
		}

		return false;
	}

	public static Graph<CustomVertex, CustomEdge> getGraphFromGraphMLFile() throws IOException, ImportException {

		//String graphStr = new String(Files.readAllBytes(Paths.get(DirectoryConfig.GRAPH_FILE.getAbsolutePath())));
		String graphStr = new String(Files.readAllBytes(Paths.get(new File("./input/routeGenInput/NetworkGraph.graphml").getAbsolutePath())));
		Graph<CustomVertex, CustomEdge> graph = new DirectedWeightedPseudograph<>(CustomEdge.class);
		GraphImporter<CustomVertex, CustomEdge> importer = createImporter();
		importer.importGraph(graph, new StringReader(graphStr));
		return graph;
	}


	public static List<GraphPath<CustomVertex, CustomEdge>> getKShortestPaths(
			Graph<CustomVertex, CustomEdge> directedGraph, CustomVertex startVertex, CustomVertex endVertex) {
		KShortestPaths<CustomVertex, CustomEdge> kShortestPaths = new KShortestPaths<>(directedGraph, 1500);
		List<GraphPath<CustomVertex, CustomEdge>> paths = kShortestPaths.getPaths(startVertex, endVertex);

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
			sb.append("(").append(this.id).append(")");
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



}
