package playground.onnene.transitScheduleMaker;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


 
/**
 * A class that extracts some helper files for creating the MATSim transitSchedule.xml file
 * 
 * @author Onnene
 *
 */
public class ExtractTransitScheduleHelperFiles {
		

    public void getMatsimStops(String xmlFile, String outputFilePath) throws ParserConfigurationException, SAXException, IOException{
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        FileMakerUtils fileMaker = new FileMakerUtils();
       
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);
        //Document document = documentBuilder.parse("C:\\Users\\NNNOB\\Documents\\GitHub\\Hyperion\\Tschedule\\transitSchedule.xml");
        
        NodeList transitScheduleList = document.getElementsByTagName("transitSchedule"); 
        fileMaker.openFile(outputFilePath + "transitStopList");
        
        for(int i = 0; i<transitScheduleList.getLength(); i++){
             Node transitScheduleNode = transitScheduleList.item(i);

             if(transitScheduleNode.getNodeType() == Node.ELEMENT_NODE){
                 Element transitScheduleElement = (Element) transitScheduleNode;
                 NodeList transitScheduleElementList = transitScheduleElement.getChildNodes();

                 for(int j = 0; j<transitScheduleElementList.getLength(); j++){
                     Node transitScheduleElementNode = transitScheduleElementList.item(j);

                     if (transitScheduleElementNode.getNodeType() == Node.ELEMENT_NODE){
                        Element transitElement = (Element) transitScheduleElementNode;

                        if (transitElement.getTagName().equals("transitStops")){

                            NodeList transitStopList = transitElement.getChildNodes();

                            for (int k = 0; k<transitStopList.getLength(); k++){
                                Node transitStopNode = transitStopList.item(k);

                                if(transitStopNode.getNodeType() == Node.ELEMENT_NODE){
                                    Element transitStopElement = (Element) transitStopNode;
                                    
                                    if(k == transitStopList.getLength()-1){
                                        fileMaker.addRecord(transitStopElement.getAttribute("id")+", "
                                            +transitStopElement.getAttribute("x")+", "
                                            +transitStopElement.getAttribute("y")+", "
                                            +transitStopElement.getAttribute("linkRefId")+", "
                                            +transitStopElement.getAttribute("name")+", "
                                            +transitStopElement.getAttribute("isBlocking")+"\n ");
                                    }
                                    else{
                                        fileMaker.addRecord(transitStopElement.getAttribute("id")+", "
                                            +transitStopElement.getAttribute("x")+", "
                                            +transitStopElement.getAttribute("y")+", "
                                            +transitStopElement.getAttribute("linkRefId")+", "
                                            +transitStopElement.getAttribute("name")+", "
                                            +transitStopElement.getAttribute("isBlocking")+"\n");
                                    }                                  
                                }
                            }
                        }
                        
                    }

                }
                fileMaker.closeFile();
                System.out.println("MATSim transit stops file written");
            }
        }  
    }
    
    public void getMatsimLines(String xmlFile, String outputFilePath) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        FileMakerUtils fileMaker = new FileMakerUtils();
       
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);

        NodeList transitScheduleList = document.getElementsByTagName("transitSchedule"); 
        fileMaker.openFile(outputFilePath + "transitLineList");
              
        for(int i = 0; i<transitScheduleList.getLength(); i++){
             Node transitScheduleNode = transitScheduleList.item(i);

             if(transitScheduleNode.getNodeType() == Node.ELEMENT_NODE){
                 Element transitScheduleElement = (Element) transitScheduleNode;
                 NodeList transitScheduleElementList = transitScheduleElement.getChildNodes();

                 for(int j = 0; j<transitScheduleElementList.getLength(); j++){
                     Node transitScheduleElementNode = transitScheduleElementList.item(j);

                     if (transitScheduleElementNode.getNodeType() == Node.ELEMENT_NODE){
                        Element transitElement = (Element) transitScheduleElementNode;

                        if(transitElement.getTagName().equals("transitLine")){                           

                            NodeList transitLineList = transitElement.getChildNodes();
                           
                            int counter = 0;
                            for(int k = 0; k<transitLineList.getLength(); k++){
                                Node transitLineNode = transitLineList.item(k);
                                                                 
                                if(transitLineNode.getNodeType() == Node.ELEMENT_NODE){
                                    counter++;
                                    //Element transitLineElement = (Element) transitLineNode;
                                   
                                }  
                            }
                            fileMaker.addRecord(transitElement.getAttribute("id")+","+counter+"\n");

                        }
                    }

                }
                fileMaker.closeFile();
                System.out.println("MATSim transit lines file written");
            }
        }            
        
        
    }
    
}
