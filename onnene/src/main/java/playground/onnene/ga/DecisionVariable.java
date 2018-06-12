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
package playground.onnene.ga;

import org.json.JSONObject;
import org.moeaframework.core.Variable;

/**
 * This class creates the GA's decision variable in the form of 
 *  a JSON format of a MATSim TransitSchedule files.
 *  
 * @author Onnene
 *
 */
public class DecisionVariable implements Variable{
	
	private static final long serialVersionUID = 1L;
    private JSONObject transitSchedule; 

    public DecisionVariable() {
    	
    	super();
    		
    }
    
    public DecisionVariable(JSONObject transitSchedule) {
    	
		this();
		this.transitSchedule = transitSchedule;
    }
    
    public JSONObject getTransitSchedule() {
        return transitSchedule;
    }

    public void setTransitSchedule(JSONObject transitSchedule) {
        this.transitSchedule = transitSchedule;
    }

    @Override
    public Variable copy() {
    	
        DecisionVariable v = new DecisionVariable();
        v.setTransitSchedule(new JSONObject(this.getTransitSchedule().toString()));
        return v;
    }

    @Override
    public void randomize() {
    	
        transitSchedule = ProblemUtils.getRandomTransitSchedule();
        
    }

    
	
	
	
}
