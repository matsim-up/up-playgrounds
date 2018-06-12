package playground.onnene.exampleCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.GraphMLImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;

public class JGraphExample {

    private static final String SOURCE_SINK_PAIR_FILE_PATH = "E:\\workspace\\codementor\\Obi\\SolveOptimizationProblem\\input\\output_matrix_with_flow.csv";
    private static final File GRAPH_FILE = new File("E:\\workspace\\codementor\\Obi\\SolveOptimizationProblem\\input\\SAMPLE_OUT1.graphml");
    
    public static void main(String[] args) {

        Graph<CustomVertex, CustomEdge> graph;
        
        try (BufferedReader br = new BufferedReader(new FileReader(SOURCE_SINK_PAIR_FILE_PATH))) {
            
            graph = getGraphFromGraphMLFile();

            List<GraphPath<CustomVertex, CustomEdge>> totalPaths = new ArrayList<>();
            int counter = 0;
            String line = "";
            br.readLine(); // skip the header line
            while ((line = br.readLine()) != null) {

                counter++;
                
                String[] od = line.split(",");
                
                CustomVertex origin = new CustomVertex(od[0]);
                CustomVertex destination = new CustomVertex(od[1]);
//                CustomVertex origin = new CustomVertex("MyCiTi_332", -48792.9894897794, -3717982.169309352);
//                CustomVertex destination = new CustomVertex("MyCiTi_307");

                System.out.print("Pair: " + counter + " [" + origin + "," + destination + "]");
                
                List<GraphPath<CustomVertex, CustomEdge>> twoPaths = getShortestAndAlternativePathsForVertexPairs(graph, origin, destination);
                totalPaths.addAll(twoPaths);
                
//                System.out.println(" -> " + twoPaths.size());
                
//                if (counter == 10)
//                    break;
            }
            
//            System.out.println("Total paths size: " + totalPaths.size());
            
        } catch (IOException | ImportException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<GraphPath<CustomVertex, CustomEdge>> getShortestAndAlternativePathsForVertexPairs(Graph<CustomVertex, CustomEdge> graph, CustomVertex startVertex, CustomVertex endVertex) throws Exception {
        List<GraphPath<CustomVertex, CustomEdge>> twoPaths = new ArrayList<>();
        
        GraphPath<CustomVertex, CustomEdge> shortestPath = null;
        GraphPath<CustomVertex, CustomEdge> alternativeShortestPath = null;
        
//            getShortestPath(graph, startVertex, endVertex);
        List<GraphPath<CustomVertex, CustomEdge>> kShortestPaths = getKShortestPaths(graph, startVertex, endVertex, 12);
        if (kShortestPaths.size() >= 1) {
            shortestPath = kShortestPaths.get(0);
            twoPaths.add(shortestPath);
        }
        if (kShortestPaths.size() >= 2) {
            alternativeShortestPath = getAlternativeShortestPath(kShortestPaths);
            if (alternativeShortestPath != null)
                twoPaths.add(alternativeShortestPath);
        }

        
//      System.out.println("Shortest Path: " + shortestPath);
//      System.out.println("Alternative Shortest Path: " + alternativeShortestPath);

        return twoPaths;
    }

    private static GraphPath<CustomVertex, CustomEdge> getAlternativeShortestPath(List<GraphPath<CustomVertex, CustomEdge>> kShortestPaths) throws Exception {
        GraphPath<CustomVertex, CustomEdge> shortestPath = kShortestPaths.get(0);
        
        GraphPath<CustomVertex, CustomEdge> graphPath;
        for (int i = 1; i < kShortestPaths.size(); i++) {
            graphPath = kShortestPaths.get(i);
            if (isPathDissimilar(shortestPath, graphPath)) {
                if (isPathReasonablyLonger(shortestPath, graphPath)) {
                    return graphPath;
                }
            }
        }
        
//        throw new Exception("No alternative path found for shortest path: " + shortestPath);
        return null;
    }

    private static boolean isPathReasonablyLonger(GraphPath<CustomVertex, CustomEdge> shortestPath, GraphPath<CustomVertex, CustomEdge> graphPath) {
        double shortestPathInKm = shortestPath.getWeight();
        double graphPathInKm = graphPath.getWeight();
//        System.out.println("Shortest path in km: " + shortestPathInKm);
//        System.out.println("1.5 times: " + 1.5 * shortestPathInKm);
//        System.out.println("current in km: " + graphPathInKm);
        return graphPathInKm <= (1.5 * shortestPathInKm);
    }

    private static boolean isPathDissimilar(GraphPath<CustomVertex, CustomEdge> shortestPath, GraphPath<CustomVertex, CustomEdge> graphPath) {
        List<CustomVertex> vertexList1 = shortestPath.getVertexList();
        List<CustomVertex> vertexList2 = graphPath.getVertexList();
        
        int minNumberOfDissimlarVertices = (int) Math.ceil(vertexList1.size() * .7);
        
//        System.out.println("nVertices in shortest path: " + vertexList1.size());
//        System.out.println("minNumberOfDissimlarVertices: " + minNumberOfDissimlarVertices);
        
        int currentNumberOfDissimilarVertices = 0;
        for (CustomVertex vertex : vertexList2) {
            if (!vertexList1.contains(vertex)) {
                currentNumberOfDissimilarVertices++;
                if (currentNumberOfDissimilarVertices >= minNumberOfDissimlarVertices)
                    return true;
            }
        }
        
//        System.out.println("nDissimilar vertices: " + currentNumberOfDissimilarVertices);
        return false;
    }

    private static Graph<CustomVertex, CustomEdge> getGraphFromGraphMLFile() throws IOException, ImportException {
        
        String graphStr = new String(Files.readAllBytes(Paths.get(GRAPH_FILE.getAbsolutePath())));

        Graph<CustomVertex, CustomEdge> graph = new DirectedWeightedPseudograph<>(CustomEdge.class);
        GraphImporter<CustomVertex, CustomEdge> importer = createImporter();
        importer.importGraph(graph, new StringReader(graphStr));
        return graph;
    }

    private static void getShortestPath(Graph<CustomVertex, CustomEdge> directedGraph, CustomVertex startVertex, CustomVertex endVertex) {
        DijkstraShortestPath<CustomVertex, CustomEdge> dijkstraShortestPath = new DijkstraShortestPath(directedGraph);
        GraphPath<CustomVertex, CustomEdge> path = dijkstraShortestPath.getPath(startVertex, endVertex);

        System.out.println("Shortest path:");
        if (path != null)
            printPath(path);
        else
            System.out.println("No path exists between source and sink");
    }

    private static void printPath(GraphPath<CustomVertex, CustomEdge> path) {
        List<CustomVertex> vertices = path.getVertexList();
        System.out.println("Total weight: " + path.getWeight());
        System.out.println(vertices);
//            for (CustomVertex customVertex : vertices) {
//                System.out.print(customVertex.getId());
//                System.out.print("  " + customVertex.getX());
//                System.out.println("  " + customVertex.getY());
//            }
//        for (CustomEdge edge : path.getEdgeList()) {
//          System.out.print(edge.getSource());
//          System.out.print("  " + edge.getTarget());
//          System.out.println("  " + edge.getWeight());
//        }
    }

    private static List<GraphPath<CustomVertex, CustomEdge>> getKShortestPaths(Graph<CustomVertex, CustomEdge> directedGraph, CustomVertex startVertex, CustomVertex endVertex, int k) {
        KShortestPaths<CustomVertex, CustomEdge> kShortestPaths = new KShortestPaths<>(directedGraph, k, 2000);
        List<GraphPath<CustomVertex, CustomEdge>> paths = kShortestPaths.getPaths(startVertex, endVertex);

//        System.out.println("KShortest paths:" + paths.size());
//        for (GraphPath<CustomVertex, CustomEdge> graphPath : paths)
//            printPath(graphPath);

        return paths;
    }

    private static GraphMLImporter<CustomVertex, CustomEdge> createImporter() {
        /*
         * Create vertex provider.
         *
         * The importer reads vertices and calls a vertex provider to create them. The provider
         * receives as input the unique id of each vertex and any additional attributes from the
         * input stream.
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
         * The importer reads edges from the input stream and calls an edge provider to create them.
         * The provider receives as input the source and target vertex of the edge, an edge label
         * (which can be null) and a set of edge attributes all read from the input stream.
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
            if(obj instanceof CustomVertex){
                final CustomVertex other = (CustomVertex) obj;
                return Objects.equals(this.id, other.id);
            } else{
                return false;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("(")
                .append(this.id)
//                .append(this.x)
//                .append(this.y)
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
    // Graph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
    // g.addVertex("v1");
    // g.addVertex("v2");
    // g.addEdge("v1", "v2");
    // }
    //
    // private static DirectedGraph<String, DefaultEdge> getSampleDirectedGraph() {
    // DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
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
