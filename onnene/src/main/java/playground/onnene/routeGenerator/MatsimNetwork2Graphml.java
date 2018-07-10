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

import org.w3c.dom.Document;

public class MatsimNetwork2Graphml {

	public void Network2Graphml(String graphStyleSheet, String transitNetworkFile, String graphFileDirectory) throws Exception{

        File xmlSource = new File(transitNetworkFile);
        File stylesheet = new File(graphStyleSheet);
             
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlSource);
        
        
        StreamSource stylesource = new StreamSource(stylesheet);        
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        
        Source source = new DOMSource(document);
        
        
        Result outputTarget = new StreamResult(new File(graphFileDirectory));
        transformer.transform(source, outputTarget);
        
        //String xml = new XMLDocument(source).toString();
        
        System.out.println("Network converted to Graphml format");
    
	}

}
