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
package playground.onnene.localMachineGA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.moeaframework.core.Variable;

/**
 * This class creates the GA's decision variable in the form of 
 *  a JSON format of a MATSim TransitSchedule files.
 *  
 * @author Onnene
 *
 */
public class LocalMachineDecisionVariable implements Variable{
	
	private static final long serialVersionUID = 1L;
    private JSONObject transitSchedule; 

    public LocalMachineDecisionVariable() {
    	
    	super();
    		
    }
    
    public LocalMachineDecisionVariable(JSONObject transitSchedule) {
    	
		this();
		this.transitSchedule = transitSchedule;
    }
    
    /* Added to make the decision variable serializable for use in the checkpoint file */
    private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.getTransitSchedule().toString());
	}

    /* Added to make the decision variable serializable for use in the checkpoint file */
	private void readObject(ObjectInputStream in) throws IOException  {
		try {
			this.setTransitSchedule(new JSONObject((String)in.readObject()));
		} catch (JSONException | ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
    
    public JSONObject getTransitSchedule() {
        return transitSchedule;
    }

    public void setTransitSchedule(JSONObject transitSchedule) {
        this.transitSchedule = transitSchedule;
    }

    @Override
    public Variable copy() {
    	
        LocalMachineDecisionVariable v = new LocalMachineDecisionVariable(); 
        if (this.getTransitSchedule() != null) {
        	v.setTransitSchedule(new JSONObject(this.getTransitSchedule().toString())); 
        }
        return v;
    }

    @Override
    public void randomize() {
        transitSchedule = new ProblemUtils().getRandomTransitSchedule();
    }
	
}
