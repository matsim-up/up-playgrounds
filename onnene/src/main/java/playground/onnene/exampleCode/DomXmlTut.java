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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Amamifechukwuka
 *
 */
public class DomXmlTut {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document transitSchedule = builder.parse("C:\\Users\\NNNOB\\Documents\\GitHub\\input\\matsim_input\\transitSchedule.xml");
			
			NodeList transitLines = transitSchedule.getElementsByTagName("transitLine");
			
			for (int i = 0; i < transitLines.getLength(); i++) {
				
				Node tL = transitLines.item(i);
				
				if (tL.getNodeType() == Node.ELEMENT_NODE) {
					
					Element transitLine = (Element) tL;
					String transitLineId = transitLine.getAttribute("id");
					System.out.println(transitLineId);
					
					NodeList transitRoutes = transitLine.getChildNodes();
					//System.out.println(transitRoutes);
					int counter = 0;
					for (int j = 0; j < transitRoutes.getLength(); j++) {
						
						Node tR = transitRoutes.item(j);
						
						if (tR.getNodeType() == Node.ELEMENT_NODE){
							
							Element transitRoute = (Element) tR;
							String transitRouteId = transitRoute.getAttribute("id");
							System.out.println(transitRouteId);					
							NodeList transitRouteChildren = transitRoute.getChildNodes();
							
							
							for (int k = 0; k <transitRouteChildren.getLength(); k++) {
								
								Node trc = transitRouteChildren.item(k);
								
								if (trc.getNodeType() == Node.ELEMENT_NODE) {
									
									Element transitRouteChild = (Element) trc;
									String transitRouteChildId = transitRouteChild.getNodeName();
					
									
									System.out.println(transitRouteChildId);
									
									
									
									if (!(transitRouteChildId == "transportMode")) {
										
										//transitRouteChild.getAttributeNode("stop");
										
										NodeList routeProfiles = transitRouteChild.getChildNodes();
										//NodeList routeProfiles = transitRouteChild.getElementsByTagName("routeProfile");
										
										
										
										for (int l = 0; l < routeProfiles.getLength(); l++) {
										
											Node rP = routeProfiles.item(l);
											
											//System.out.println(rP.getNodeName());
											
											if (rP.getNodeType() == Node.ELEMENT_NODE) {
												
												Element routeProfile = (Element) rP;
												
												//System.out.println(routeProfile.getNodeName())
												//if (routeProfile.getNodeName() == "")
												
												//String rpId = routeProfile.getNodeName();
												//String rpA = routeProfile.getAttribute("refId");
												//routeProfile.getNodeName();
//												if (routeProfile.getNodeName() == "") {
//													
//												}
												//System.out.println(routeProfile.getAttribute("refId"));
												
												String rpId = routeProfile.getNodeName();
												
												String rpAT = routeProfile.getAttribute("refID");
												
												System.out.println(rpId);
												System.out.print(rpAT);
												
												
												NodeList transitRouteChildren1 = transitRoute.getChildNodes();
												
												if (rP.getNodeType() == Node.ATTRIBUTE_NODE) {
													
													
													//System.out.print(transitRouteChildren1.item());
													
												}
												//System.out.println(rpA);
												
											}
											
										}
										
									}
									
									
								}
								
								
								
							}
							
							System.out.println();
							
//							counter +=1;
//							
//							System.out.println(counter);
						}
					}
					
					
					
					
					//System.out.println(id);
					
				}
					
					
				
				//System.out.println(transitLines.item(i).getNodeName().getBytes());
				
			}
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
