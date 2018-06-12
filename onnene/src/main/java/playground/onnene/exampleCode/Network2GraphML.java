package playground.onnene.exampleCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.GraphMLImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;
import org.w3c.dom.Document;

import com.opencsv.CSVReader;

public class Network2GraphML {
	
	
	@Override
	public String toString() {
		return "Network2GraphML [getClass()=" + getClass() +  ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}


	// Number of vertices
    private static final int SIZE = 6;

    private static Random generator = new Random(17);

    /**
     * Color
     */
    enum Color
    {
        BLACK("black"),
        WHITE("white");

        private final String value;

        private Color(String value)
        {
            this.value = value;
        }

        public String toString()
        {
            return value;
        }

    }

    
     //A custom graph vertex.
   
    static class CustomVertex
    {
        private String id;
        private Color color;

        public CustomVertex(String id)
        {
            this(id, null);
        }

        public CustomVertex(String id, Color color)
        {
            this.id = id;
            this.color = color;
        }

        @Override
        public int hashCode()
        {
            return (id == null) ? 0 : id.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CustomVertex other = (CustomVertex) obj;
            if (id == null) {
                return other.id == null;
            } else {
                return id.equals(other.id);
            }
        }

        public Color getColor()
        {
            return color;
        }

        public void setColor(Color color)
        {
            this.color = color;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("(").append(id);
            if (color != null) {
                sb.append(",").append(color);
            }
            sb.append(")");
            return sb.toString();
        }
    }
    
    
    /**
     * Create exporter
     */
//    private static GraphExporter<CustomVertex, DefaultWeightedEdge> createExporter()
//    {
//        // create GraphML exporter
//        GraphMLExporter<CustomVertex, DefaultWeightedEdge> exporter =
//            new GraphMLExporter<>((v) -> v.id, null, new IntegerComponentNameProvider<>(), null);
//
//        // set to export the internal edge weights
//        exporter.setExportEdgeWeights(true);
//
//        // register additional color attribute for vertices
//        exporter.registerAttribute("color", AttributeCategory.NODE, AttributeType.STRING);
//
//        // register additional name attribute for vertices and edges
//        exporter.registerAttribute("name", AttributeCategory.ALL, AttributeType.STRING);
//
//        // register provider of vertex attributes
//        exporter.setVertexAttributeProvider(v -> {
//            Map<String, Attribute> m = new HashMap<>();
//            if (v.getColor() != null) {
//                m.put("color", DefaultAttribute.createAttribute(v.getColor().toString()));
//            }
//            m.put("name", DefaultAttribute.createAttribute("node-" + v.id));
//            return m;
//        });
//
//        // register provider of edge attributes
//        exporter.setEdgeAttributeProvider(e -> {
//            Map<String, Attribute> m = new HashMap<>();
//            m.put("name", DefaultAttribute.createAttribute(e.toString()));
//            return m;
//        });
//
//        return exporter;
//    }
    
    
	/**
     * Create importer
     */
    private static GraphImporter<CustomVertex, DefaultWeightedEdge> createImporter()
    {
        // create vertex provider
        VertexProvider<CustomVertex> vertexProvider = new VertexProvider<CustomVertex>()
        {
            @Override
            public CustomVertex buildVertex(String id, Map<String, Attribute> attributes)
            {
                CustomVertex cv = new CustomVertex(id);

                // read color from attributes
                String color = attributes.get("color").getValue();
                if (color != null) {
                    switch (color) {
                    case "black":
                        cv.setColor(Color.BLACK);
                        break;
                    case "white":
                        cv.setColor(Color.WHITE);
                        break;
                    default:
                        // ignore not supported color
                    }
                }
                return cv;
            }
        };

        // create edge provider
        EdgeProvider<CustomVertex, DefaultWeightedEdge> edgeProvider =
            (from, to, label, attributes) -> new DefaultWeightedEdge();

        // create GraphML importer
        GraphMLImporter<CustomVertex, DefaultWeightedEdge> importer =
            new GraphMLImporter<>(vertexProvider, edgeProvider);

        return importer;
    }
    
    
    private static CustomVertex[] csvReader(){
        
	    //String OdFlows = ".\\output_matrix_with_flow.csv";
	    String OdFlows = "C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\output_matrix_with_flow.csv";
	    String line = "";
	    String line1 = "";
	    String cvsSplitBy = ",";
	    int iteration = 0;
	    int Counter = 0;
	    CustomVertex cv = null;
	    CustomVertex cv1 = null;
	    String ar[] = new String[2];
	    CustomVertex ar1[] = new CustomVertex[2];
	    
//	    Scanner s = new Scanner(new File(OdFlows));
//	    ArrayList<String> list = new ArrayList<String>();
//	    while (s.hasNext()){
//	        list.add(s.next());
//	    }
//	    
//	    System.out.println(list);
//	    
//	    s.close();
//	    
    
	    
	    try { 
	        BufferedReader br = new BufferedReader(new FileReader(OdFlows)); 
	        
	        //String text = br.readLine();
	        
	        //System.out.println(text);
	        
	        
	    	  
	        while ((line = br.readLine()) != null) {
	        	//String lineWithoutSpaces = line.replaceAll("\\s+","");
	        	//line = line.trim();
	        	
	        	
	        	
	        	if(iteration < 1) {
	                iteration++;  
	                continue;
	                
	            }
	        
	        	
	        	Counter += 1;
	
	            // use comma as separator
	            String[] od = line.split(cvsSplitBy);
	            
	            
	            
	            cv = new CustomVertex(od[0]);
	            cv1 = new CustomVertex(od[1]);
	            
	            
	            
	            
	            ar1[0]= cv;
                ar1[1] = cv1;
                
                
                System.out.println(ar1);
	            
	            //System.out.println(Arrays.toString(od));
	            
//	            List<String> strings = new ArrayList<String>();
//	            
//	            strings.add(od[0]);
//	            strings.add(od[1]);
//	            
//	            System.out.println(strings);
//	            
	            
//	            for (int i :  Arrays.toString(od)) { 
//	            		
//	            		System.out.println(strings.add(Arrays.toString(od).valueOf(i)));
//	            
//	            }
	            //String[] lst = new String[Arrays.toString(od)];
	            
	            //List<String>
	            
	            //lst.(Arrays.toString(od));
	            
	            //System.out.println(lst);
	            	
	            //System.out.println(Counter + " " + "" +od[0] + " " + od[1] + " " +  od[2]);
	            
	            //g.addVertex(stops[0].replaceAll("\\s+",""));
	        }
	        
	        
	        
	            
	        } catch (IOException e) {
	        	
	        	e.printStackTrace();
	        }
	        	
	    
	    
	    return ar1;

    }
    
    
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
			//csvReader();
			
			
			System.out.println("Start of programme");
			//"C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder"
		    File xmlSource = new File("C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\transitNetwork.xml");
		    File stylesheet = new File("C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\graph.xsl");
	        //File stylesheet = new File(".\\graph.xsl");
	        
	        
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document document = builder.parse(xmlSource);

	        System.out.println("mid of programme");
	        StreamSource stylesource = new StreamSource(stylesheet);
	        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
	        Source source = new DOMSource(document);
	        File grahpFile = new File("C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\SAMPLEOUT1.graphml");
	        Result outputTarget = new StreamResult(grahpFile);
	        transformer.transform(source, outputTarget);
	        System.out.println(outputTarget);
	        
	        String content = new String(Files.readAllBytes(Paths.get(grahpFile.getAbsolutePath())));
	        //System.out.println(content);
	        
	        int counter2 = 0;
	        
	        
	        CSVReader reada = new CSVReader(new FileReader("C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\output_matrix_with_flow_1.csv"), ',');
	        String[] nextLine;
	        
	        reada.readNext();
	        List<String[]> myEntries = reada.readAll();
            System.out.println("myEntries" + " " + myEntries.size());
            
           
            
//	        for (String[] lines : myEntries) {
//	        	
//	        	System.out.println("myEntries" + " " + lines.getClass().getName().);
//	        	
//	        }
	        
	        
	        String OdFlows = "C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\output_matrix_with_flow.csv";
		    String line = "";
		    String line1 = "";
		    String cvsSplitBy = ",";
		    int iteration = 0;
		    int Counter = 0;
		    CustomVertex origin = null;
		    CustomVertex destination = null;
	    	
	        try {
	            // import it back
	            System.out.println("-- Importing graph back from GraphML");
	                    
//	            Graph<CustomVertex, DefaultWeightedEdge> graph2 =
//	                new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
//	            GraphImporter<CustomVertex, DefaultWeightedEdge> importer1 = createImporter();
//	            importer1.importGraph(graph2, new StringReader(content));
	            
	            Graph<CustomVertex, DefaultWeightedEdge> graph2 =
		                new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		            GraphImporter<CustomVertex, DefaultWeightedEdge> importer1 = createImporter();
		            importer1.importGraph(graph2, new StringReader(content));
		            
	            DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph2);
	            KShortestPaths<CustomVertex, DefaultWeightedEdge> k = new KShortestPaths(graph2, 100);
	            
	            
	            	            
	            
//	            ShortestPathAlgorithm path = new ShortestPathAlgorithm(graph2);
	            
//                List<DefaultWeightedEdge> shortestPath = ShortestPathAlgorithm.SingleSourcePaths(graph2);  
               
                //ShortestPathAlgorithm spath = new ShortestPathAlgorithm(graph2);
                
        
//                List<DefaultWeightedEdge> path = DijkstraShortestPath.findPathBetween(graph2, start, end);

                               
                ShortestPathAlgorithm.SingleSourcePaths <CustomVertex,DefaultWeightedEdge> paths;
                
                CustomVertex cv = new CustomVertex(("MyCiTi_180"));
                CustomVertex cv1 = new CustomVertex(("MyCiTi_301"));     
                
//                System.out.println(Graphs.neighborListOf(graph2, cv1));
//                System.out.println(Graphs.neighborListOf(graph2, cv));
                
                
                
                //List<CustomVertex> adj;
                		
                //neighborListOf(Graph<CustomVertex,DefaultWeightedEdge> graph2, CustomVertex cv); 
                		
	            //System.out.println(graph2.getEdgeWeight(e));
	            List<DefaultWeightedEdge> path; 
	            
	            //List<CustomVertex> adj = graph2.neighborListOf(cv)
	            
	            List<GraphPath<CustomVertex, DefaultWeightedEdge>> lst1 = k.getPaths(cv, cv1);
	            
	            
	            //System.out.println(lst1);
	            //System.out.println(lst1.get(91));
	            //System.out.println(lst1.get(91).getVertexList());
	            //System.out.println(lst1.get(91).getWeight());
	            //System.out.println(lst1.get(91).);
	            
	            
	            
//	           for (int ee = 0; ee < lst1.size(); ee++) {
//	        	   
//	        	   
//	        	   System.out.println(lst1.get(ee));
//	        	   
//	           }
	            
	            
	            
	            
	            //for (CustomVertex item : graph2.vertexSet()){
	            	
	            	
	            	
	            	//System.out.println(item);
	            	//System.out.println("The type for vertex: " + item + " " + "is" + " " + item.getClass().getName());
//	            	System.out.println(graph2.containsVertex(item));
	            
	            	//System.out.println(dijkstraShortestPath.getPath(item, cv));
	            	
	            	//System.out.println(dijkstraShortestPath.getPath(item, cv).getLength());
	            	
	            	//System.out.println(dijkstraShortestPath.getPath(cv, item).getWeight());
	            	
	            	
	            	
	            	//System.out.println(dijkstraShortestPath.getPath(item, cv));
	            	//System.out.println(k.getPaths(item, cv));
	            	
	            	//List<GraphPath<CustomVertex, DefaultWeightedEdge>> lst = k.getPaths(item, cv);
//	            	GraphPath lst = dijkstraShortestPath.getPath(item, cv);
//	            	
	            	//System.out.println(lst);
	            	//System.out.println((lst.get(0).toString()));
	            	
//	            	for (int i = 0; i < lst.size(); i++) {
//	            		
//	            		System.out.println(lst.get(i));
//	            		
//	            		System.out.println(lst.get(i).getWeight());
//	            		
//	            			            		
//	            	}
	            	
	            	//for (item1 : dijkstraShortestPath.getPaths(item))
	            	
	            	//String content1 = new String(Files.readAllBytes(Paths.get(grahpFile.getAbsolutePath())));
	            //}
	            
//	            for (DefaultWeightedEdge item2 : graph2.edgeSet()){
//	            	//System.out.println("The type for vertex: " + item + " " + "is" + " " + item.getClass().getName());
//	            	
//	            	//System.out.println(item2.toString());
//	            	//System.out.println(graph2.containsEdge(item2));
//	            	
//	            	System.out.println(item2);
//	            	
//	            	System.out.println(graph2.getEdgeWeight(item2));
//	            	
//	            
//	            	//System.out.println(dijkstraShortestPath.findpathbetween(("MyCiTi_400")));
//	            	
//	            	//DijkstraShortestPath.findPathBetween(graph2, item2[0], item2[1]);
//
//	            	//for (item1 : dijkstraShortestPath.getPaths(item))
//	            	
//	            	//String content1 = new String(Files.readAllBytes(Paths.get(grahpFile.getAbsolutePath())));
//	            }
	            //System.out.println(graph2);
	            
	            // graph2.contains("MyCiTi_197");
	            //System.out.println("List of vertices" + "" + graph2.vertexSet());
	            //System.out.println("List of edges" + "" + graph2.edgeSet());
	            //System.out.println(graph2.vertexSet().contains(("MyCiTi_400")));
	            
//	            System.out.println(graph2.vertexSet().contains(("MyCiTi_400")));
//	            graph2.containsVertex("MyCiTi_197");
	            
//	            @Test
//	            public void whenGetDijkstraShortestPath_thenGetNotNullPath() {
//	                DijkstraShortestPath dijkstraShortestPath 
//	                  = new DijkstraShortestPath(directedGraph);
//	                List<String> shortestPath = dijkstraShortestPath
//	                  .getPath("v1","v4").getVertexList();
//	              
//	                assertNotNull(shortestPath);
//	            }
	            
	           
	            
//	            //ShortestPathAlgorithm.SingleSourcePaths<V, E>
//	            List<DefaultWeightedEdge> path = DijkstraShortestPath.getPath("MyCiTi_400");
//	            System.out.println(path); // prints [(7 : 9), (9 : 3), (3 : 2)]
	            
	            
//	            
	            BufferedReader br = new BufferedReader(new FileReader(OdFlows));
		        while ((line = br.readLine()) != null) {
		        	
		        	if(iteration < 1) {
		                iteration++;  
		                continue;
		                
		            }
		        		        	
		        	Counter += 1;
		        	
		        	String[] od = line.split(cvsSplitBy);
		                        
             
		        	origin = new CustomVertex(od[0]);
//	            	System.out.println(origin);
		            destination = new CustomVertex(od[1]);
//		            System.out.println(destination);
		            
		            
		            		            
		            //for (int p = 0; p < od.length; p++) {
		            	
		            			           		            	
	            	List<GraphPath<CustomVertex, DefaultWeightedEdge>> lst = k.getPaths(origin, destination);
	            			            
//		            System.out.println(od.length);
			        System.out.println(lst.get(0));
			        System.out.println(lst.get(0).getWeight());
			        
			        System.out.println(lst.get(10));
			        System.out.println(lst.get(10).getWeight());
			      
//			        System.out.println(lst.get(91));
//			        System.out.println(lst.get(91).getWeight());
		            int LengthOfPath = lst.size();
//		            System.out.println(LengthOfPath);
		            
		            
		            
	            	for (int i = 0; i < LengthOfPath; i++) {
	            		
//	            		System.out.println(i);
//	            		
//	            		System.out.println(lst.get(i).getLength());
	            		List<CustomVertex> vertList = lst.get(i).getVertexList();
	            		//System.out.println(vertList);
	            		
	            		
	            		
	            		List<DefaultWeightedEdge> edges = lst.get(i).getEdgeList();
	            		//System.out.println(edges);
//	            		System.out.println("");
//			            		System.out.println("Shortest Path");
//			            		System.out.println(lst.get(i));
//			            		System.out.println("");
//			            		System.out.println(lst.get(i).getVertexList());
//			            		System.out.println("");
//			            		System.out.println(lst.get(i).getEdgeList());
	            		
//		            		for (int j = 0; j < lst.get(i).getLength(); j++) {
//		            			
//		            			System.out.println("Shortest Path Vertices");
//		       
//		            			System.out.println(lst.get(i).getVertexList());
//		            			
//		            			List<customClasses.Network2GraphML.CustomVertex> vertList = lst.get(i).getVertexList();
//			            			
//			            			System.out.println("");
//			            			
//			            			System.out.println("Shortest Path Edges");
//			            			
//			            			System.out.println(lst.get(i).getEdgeList());
	            			
	            	    //for (CustomVertex cvt : vertList) {}
	            			
            			for (int f = 0; f < vertList.size(); f++) {
            				
            				CustomVertex cvt = vertList.get(f);
            				
            				//System.out.println(cvt);
            				
            				//System.out.println(vertList.get(f));
            				List<CustomVertex>  neighborList = Graphs.neighborListOf(graph2, cvt);
//            				System.out.println("List of neighbors" + "" + neighborList);
            				    				
            				for(int z = 0; z < neighborList.size(); z++) {
            					
//            					System.out.println((""));
//            					System.out.println(("neighbor pairs =" + cvt + "" + neighborList.get(z)));
            					//System.out.println((""));

            						for (int row = 0; row<myEntries.size(); row++) {
            							
            							
                		            	
                		            	//System.out.println((myEntries.get(row)));
                		            	
                		            	CustomVertex entryOD = new CustomVertex((myEntries.get(row)[0].trim()));
                		            	
                		            	//System.out.println(entryOD.getClass().getTypeName());
                		            	
                		            	
                		            	CustomVertex entryOD1 = new CustomVertex((myEntries.get(row)[1].trim()));
                		            	
//                		            	System.out.println(("entry ODS are" + entryOD + "" + entryOD1));
                		            	
                		            	
//                		            	System.out.println((myEntries.get(row)[0].trim()));
//                		            	System.out.println((myEntries.get(row)[1].trim()));
//                		            	System.out.println(( "demand=" +""+ myEntries.get(row)[2].trim()));
                		            	
                		            	
                		                       		            
                		            	if (entryOD.equals(cvt) && entryOD1.equals(neighborList.get(z)) ) {
                		            		
//                    							System.out.println("travel demand" + "" + myEntries.get(row)[2].trim());
                		            	
//                		            	for (int column = 0; column<myEntries.get(row).length; column++) {
//                		            		
//                		            		System.out.println((myEntries.get(row)[0].trim()));
//                		            		
//                		            		//System.out.println((myEntries.get(row)[column].trim()));
//                		            		
//                		            	}
                		            }
                					
            					}
            					
            					
          					
            				}
//            				System.out.println(Graphs.neighborListOf(graph2, cvt));
            			}
			            
	            		//}
//            				System.out.println("");
	            		
//		            		System.out.println(lst.get(i));
//		           
//		            		
//		            		System.out.println(lst.get(i).getWeight());
            		
            			            		
            	}
		            //}
		            	
		      		        
	        }	
		        	
	        }catch ( ImportException e) {
	            System.err.println("Error: " + e.getMessage());
	            System.exit(-1);
	        }
	        
	   	   	        
	       /* String xml1 = new XMLDocument(source1).toString();
	        System.out.println(xml1);*/
	        
	        System.out.println("end of programme");
	        	       
	        
	}



}
