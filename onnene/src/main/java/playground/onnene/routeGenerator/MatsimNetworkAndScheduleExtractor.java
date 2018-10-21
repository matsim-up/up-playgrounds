package playground.onnene.routeGenerator;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;


/**
 * This class is used to extract the details of the MyCiTi 
 * routes, links and nodes.
 * 
 * @author Onnene
 *
 */
public class MatsimNetworkAndScheduleExtractor {
	
	private static final Logger log = Logger.getLogger(MatsimNetworkAndScheduleExtractor.class);

	public void RouteExtractor(String routeStyleSheet, String transitScheduleFile, String outputDirectory) throws Exception{

        File stylesheet = new File(routeStyleSheet);
        File xmlSource = new File(transitScheduleFile);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlSource);

        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        Source source = new DOMSource(document);
        Result outputTarget = new StreamResult(new File(outputDirectory + "myciti_routes.csv"));
        transformer.transform(source, outputTarget);

        log.info("Route extraction finished");

	}
	
	
	public void stopFacilityExtractor(String stopFacilityStyleSheet, String transitScheduleFile, String outputDirectory) throws Exception{

        File stylesheet = new File(stopFacilityStyleSheet);
        File xmlSource = new File(transitScheduleFile);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlSource);

        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        Source source = new DOMSource(document);
        Result outputTarget = new StreamResult(new File(outputDirectory + "myciti_stopFacilities.csv"));
        transformer.transform(source, outputTarget);

        log.info("Stop facility extraction finished");

	}
	
	
	public void NodeExtractor(String nodeStyleSheet, String transitNetworkFile, String outputDirectory) throws Exception{
       
        File stylesheet = new File(nodeStyleSheet);
        File xmlSource = new File(transitNetworkFile);
       
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlSource);

        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        Source source = new DOMSource(document);
        Result outputTarget = new StreamResult(new File(outputDirectory + "myciti_node.csv"));
    
        transformer.transform(source, outputTarget);
        
        
        log.info("Node extraction finished");
        
	}
	
	
	public void LinkExtractor(String linkStyleSheet, String transitNetworkFile, String outputDirectory) throws Exception{
	
       
        File stylesheet = new File(linkStyleSheet);
        File xmlSource = new File(transitNetworkFile);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlSource);

        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        Source source = new DOMSource(document);
        Result outputTarget = new StreamResult(new File(outputDirectory + "myciti_links.csv"));
        transformer.transform(source, outputTarget);
        
        log.info("Link extraction finished");

        
	}

}
