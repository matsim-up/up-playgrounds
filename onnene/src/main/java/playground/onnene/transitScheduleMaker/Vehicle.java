/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playground.onnene.transitScheduleMaker;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import playground.onnene.ga.DirectoryConfig;



/**
 * Class that builds the transitVehicle.xml file
 * 
 * 
 * @author Onnene
 *
 */
class Vehicle {
    public  Document d2;
    public  Element e;
    //private boolean flag = false;
    
    public void defineVehicle() throws FileNotFoundException, IOException, TransformerException, ParserConfigurationException, SAXException  {
      
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        d2 = db.newDocument();
      	
      	e = d2.createElement("vehicleDefinitions");
        e.setAttribute("xmlns", "http://www.matsim.org/files/dtd");
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xsi:schemaLocation", "http://www.matsim.org/files/dtd http://www.matsim.org/files/dtd/vehicleDefinitions_v1.0.xsd");
      
      	Element e0 = d2.createElement("vehicleType");
      	e0.setAttribute("id", "defaultTransitVehicleType");
      	
      	Element e00 = d2.createElement("capacity");
      	
      	Element e000 = d2.createElement("seats");
      	e000.setAttribute("persons", "101");

      	Element e001 = d2.createElement("standingRoom");
      	e001.setAttribute("persons", "0");

      	e0.appendChild(e00);
      	e00.appendChild(e000);
      	e00.appendChild(e001);
      
      	Element e01 = d2.createElement("length");
      	e01.setAttribute("meter", "7.5");
      	e0.appendChild(e01);
      	Element e02 = d2.createElement("width");
      	e02.setAttribute("meter","1.0");
      	e0.appendChild(e02);
      	Element e03 = d2.createElement("accessTime");
      	e03.setAttribute("secondsPerPerson", "1.0");
      	e0.appendChild(e03);
      	Element e04 = d2.createElement("egressTime");
      	e04.setAttribute("secondsPerPerson", "1.0");
      	e0.appendChild(e04);
     	Element e05 = d2.createElement("doorOperation");
      	e05.setAttribute("mode", "serial");
      	e0.appendChild(e05);
      	Element e06 = d2.createElement("passengerCarEquivalents");
      	e06.setAttribute("pce", "1.0");
      	e0.appendChild(e06);
      	e.appendChild(e0);
        
    }
    public void addVehicle(String id) throws TransformerException{
        //System.out.println("ff"+id);
        Element e10 = d2.createElement("vehicle");
        e10.setAttribute("id", id);
        e10.setAttribute("type", "defaultTransitVehicleType");
        e.appendChild(e10); 
    }
    
    public void output() throws TransformerConfigurationException, TransformerException{
        d2.appendChild(e);
        DOMSource doms = new DOMSource(d2);
        //String fileName = "C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\output\\SBO_input\\common\\transitVehicles" + ".xml";
        String fileName = DirectoryConfig.TRANSIT_VEHICLES_FILE;
        StreamResult sr = new StreamResult(new File(fileName));
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(doms, sr);
        System.out.println("DONE!!!");
    }
}
