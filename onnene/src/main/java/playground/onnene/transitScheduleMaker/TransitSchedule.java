package playground.onnene.transitScheduleMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import playground.onnene.ga.DirectoryConfig;


/**
 * Class that converts the generated routes into MATSim transitSchedule.xml files
 * 
 * @author Onnene
 *
 */
public class TransitSchedule {
	
    
    public String createTransitScheduleXML(Collection<String> f2, int index, int numberOfLines, String outputFile) throws IOException, FileNotFoundException, TransformerException, ParserConfigurationException, SAXException{
       
        //creating xml file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();

        //root element
        Element e = d.createElement("transitSchedule");

        //transit stop
        Element e0 = d.createElement("transitStops");
        e.appendChild(e0);
        
        // Create vehicle xml
        Vehicle v = new Vehicle();
        v.defineVehicle();
        
        //text files used throughout code
        File f0 = new File(DirectoryConfig.SCHEDULE_STOPS_HELPER_FILE);
        File f1 = new File(DirectoryConfig.SCHEDULE_LINES_HELPER_FILE);

        //transit stop object and stop facility called
        TransitStops tsObject = new TransitStops (d, e0, f0);
        tsObject.stopFacility();

        //s contains routeId, s0 contains routes
        Scanner s = new Scanner(new FileInputStream(f1));
 
        //counting variables
        int transitsize = 0, vref = 0, id = 0;

        //departure hours
        int [] hours = {4,4,4,4,3,3,3,3,3,3,4,4,4,2,2,2};

        Iterator<String> iterator = f2.iterator();

        while (transitsize < numberOfLines){
        
            id = 0;
            int flag = 0, forward = 0, reverse = 0;

            //transit line object
            TransitLine tlObject = new TransitLine(d, e);

            String[] a = s.nextLine().split(",");
            tlObject.transitLine(a[0]);


            int routenum = Integer.parseInt(a[1]);
            String routes = iterator.next();

            System.out.println(routes);
             
            
            for(int i = 0; i<routenum; i++){
                if(i<Math.round((double)routenum/2)){
                    forward++;
                }
                else{
                    reverse++;
                }
            }
            int trip = (int) Math.round((double)(hours.length/forward));
               // System.out.println(trip+"****");

                //for uneven route sizes
            int left = hours.length - trip * forward;
            int min = 0, max = 0;
            
            for(int i = 0; i<forward ; i++){

                //forward trip
                flag = 1;

                //tansit route which takes the routeID and i
                tlObject.transitRoutes(a[0], i);
                //returns bus
                tlObject.transportMode();
                //takes in route, number of routes, i to determine if first or last element in textfile
                tlObject.routeProfile(routes, transitsize, routenum, flag, numberOfLines);
                
                
                //takes in the transitStop file and flag
                tlObject.route(f0, flag);

                //departure
                Element e2 = tlObject.departure();
                
                trip = (int) Math.round((double)(hours.length/forward));
                max+=trip;
                //increments value of trip for uneven route sizes
                if(left>0){
                    trip++;
                    max++;
                    left--;
                }

                //min and max being parameters of the hours divided by trip
                

                //max value returns to array size if its greater than array size
                if(max>hours.length){
                    max = hours.length;
                }

                //stores the hours in a temporary array size of the trip
                int [] tempArr = new int[trip];
                int incr = 0;
                for (int j = min; j<max; j++){
                    tempArr[incr] = hours[j];
                    incr++;
                 }     

                //initial time being at 05:00:00
                int hrs = min+4;
                int mins = 0;

                for (int k = 0; k<trip; k++){
                    //generates the hours and minutes according to departure rate
                    int shiftcounter = 0;
                    while(shiftcounter<tempArr[k]){

                        mins = (60/tempArr[k])*shiftcounter;
                        if(mins == 0){
                            hrs++;
                        }

                        //mins = (r.nextInt((3)+1)+mins-3); 
                        if(mins<0){
                            //change values
                            mins += 60;
                            hrs--;
                            //checks if values are less than 10
                            String minstr = (mins< 10) ? ("0"+mins) : (""+mins);
                            String hrstr = (hrs< 10) ? ("0"+hrs) : (""+hrs);

                            //creating elements to xml
                            Element e3 = d.createElement("departure");
                            e2.appendChild(e3);
                            e3.setAttribute("id", a[0]+"000"+id);
                            e3.setAttribute("vehicleRefId", ""+"tr_"+vref);
                            v.addVehicle("tr_"+vref);
                            e3.setAttribute("departureTime", hrstr+":"+minstr+":00");
                            vref++;
                            id++;
                            shiftcounter++;
                            //next hour
                            hrs++;
                        }else{
                            //checks if values are less than 10
                            String minstr = (mins< 10) ? ("0"+mins) : (""+mins);
                            String hrstr = (hrs< 10) ? ("0"+hrs) : (""+hrs);

                            //creating elements to xml
                            Element e3 = d.createElement("departure");
                            e2.appendChild(e3);
                            e3.setAttribute("id", a[0]+"000"+id);
                            e3.setAttribute("vehicleRefId", ""+"tr_"+vref);
                            v.addVehicle("tr_"+vref);
                            e3.setAttribute("departureTime", hrstr+":"+minstr+":00");
                            vref++;
                            id++;
                            shiftcounter++;
                        }

                    }
                    min = max;
                }
                
            }
            
           trip = (int) Math.round((double)(hours.length/reverse));
                //System.out.println(trip+"@@@");
           //System.out.println(routes);
                //for uneven route sizes
            left = hours.length - trip*reverse;
            int min2 = 0;
            int max2 = 0;
            
            for(int i = 0; i<reverse ; i++){
                //reverse trip
                flag = 2;

                //tansit route which takes the routeID and i
                tlObject.transitRoutes(a[0], i+forward);
                //returns bus
                tlObject.transportMode();
                //takes in route, number of routes, i to determine if first or last element in textfile
                tlObject.routeProfile(routes, transitsize, routenum, flag, numberOfLines);
                //takes in the transitStop file and flag
                tlObject.route(f0, flag);

                //departure
                Element e2 = tlObject.departure();
                
                
                trip = (int) Math.round((double)(hours.length/reverse));
                max2 +=trip;
                //increments value of trip for uneven route sizes
                if(left>0){
                    trip++;
                    max2 ++;
                    left--;
                }
                //max value returns to array size if its greater than array size
                if(max2>hours.length){
                    max2 = hours.length;
                }

                //stores the hours in a temporary array size of the trip
                int [] tempArr = new int[trip];
                int incr = 0;
                for (int j = min2; j<max2; j++){
                    tempArr[incr] = hours[j];
                    incr++;
                 }     

                //initial time being at 05:00:00
                int hrs = min2+4;
                int mins = 0;

                for (int k = 0; k<trip; k++){

                    //generates the hours and minutes according to departure rate
                    int shiftcounter = 0;
                    while(shiftcounter<tempArr[k]){

                        mins = (60/tempArr[k])*shiftcounter;
                        if(mins == 0){
                            hrs++;
                        }

                        //mins = (r.nextInt((3)+1)+mins-3); 
                        if(mins<0){
                            //change values
                            mins += 60;
                            hrs--;
                            //checks if values are less than 10
                            String minstr = (mins< 10) ? ("0"+mins) : (""+mins);
                            String hrstr = (hrs< 10) ? ("0"+hrs) : (""+hrs);

                            //creating elements to xml
                            Element e3 = d.createElement("departure");
                            e2.appendChild(e3);
                            e3.setAttribute("id", a[0]+"000"+id);
                            e3.setAttribute("vehicleRefId", ""+"tr_"+vref);
                            v.addVehicle( "tr_"+vref);
                            e3.setAttribute("departureTime", hrstr+":"+minstr+":00");
                            vref++;
                            id++;
                            shiftcounter++;
                            //next hour
                            hrs++;
                        }else{
                            //checks if values are less than 10
                            String minstr = (mins< 10) ? ("0"+mins) : (""+mins);
                            String hrstr = (hrs< 10) ? ("0"+hrs) : (""+hrs);

                            //creating elements to xml
                            Element e3 = d.createElement("departure");
                            e2.appendChild(e3);
                            e3.setAttribute("id", a[0]+"000"+id);
                            e3.setAttribute("vehicleRefId", ""+"tr_"+vref);
                            v.addVehicle("tr_"+vref);
                            e3.setAttribute("departureTime", hrstr+":"+minstr+":00");
                            vref++;
                            id++;
                            shiftcounter++;
                        }

                    }
                    min2 = max2;
                }
            }
            transitsize++;        }
        //appends route element to document
        d.appendChild(e);          
        
      s.close();
      return generateXmlFile(d, v, index, outputFile);
    }

	/**
	 * @param d
	 * @param v
	 * @return
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	private String generateXmlFile(Document d, Vehicle v, int numFiles, String outputFile)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		//generating the xml file

        DOMSource doms = new DOMSource(d);
        StreamResult sr = new StreamResult(new File(outputFile));
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
             
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMImplementation di = d.getImplementation(); 
        DocumentType dt = di.createDocumentType("doctype","transitSchedule","http://www.matsim.org/files/dtd/transitSchedule_v1.dtd");
        t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dt.getSystemId());
        t.transform(doms, sr);
        
        System.out.println("DONE!!!");
        
        //write vehicles files
        v.output();
        
        //Prettyprint the XML
        Writer out = new StringWriter();    
        Transformer tf1 = TransformerFactory.newInstance().newTransformer();
        tf1.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf1.setOutputProperty(OutputKeys.INDENT, "yes");
        tf1.transform(doms, new StreamResult(out));
        
        String xmlString = out.toString();
        
        return xmlString;
	}
    
    public JSONObject XML2JSON(String xmlFile){
    
    	JSONObject xmlJSONObj = null;
    	try {
    		
		  xmlJSONObj = XML.toJSONObject(xmlFile);
		  
		} catch (JSONException e) {

			e.printStackTrace();
			
		}
    	
    	
    	return xmlJSONObj;
    }
    
    public static String JSON2XML(String JsonFile) throws JSONException {
    	
    	JSONObject json = new JSONObject(JsonFile);
    	String xml = XML.toString(json);
    	
    	return xml;
    }
    
    
    public static void string2Dom(String xmlSource) 
            throws SAXException, ParserConfigurationException, IOException, TransformerException {
        // Parse the given input
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlSource)));

        // Write the parsed document to an xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        DOMImplementation di = doc.getImplementation(); 
        DocumentType dt = di.createDocumentType("doctype","transitSchedule","http://www.matsim.org/files/dtd/transitSchedule_v1.dtd");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dt.getSystemId());
        
        StreamResult result =  new StreamResult(new File("my-file.xml"));
        
        transformer.transform(source, result);
    }
    
    
    
  }

