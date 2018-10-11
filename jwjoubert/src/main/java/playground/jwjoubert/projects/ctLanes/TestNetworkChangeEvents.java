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
package playground.jwjoubert.projects.ctLanes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkChangeEventsParser;
import org.matsim.core.network.io.NetworkChangeEventsWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.misc.Time;
import org.matsim.up.utils.Header;

/**
 * Play around with {@link NetworkChangeEvent}s.
 * 
 * @author jwjoubert
 */
public class TestNetworkChangeEvents {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Header.printHeader(TestNetworkChangeEvents.class, args);
		String equil = args[0];
		
		Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		
		new MatsimNetworkReader(sc.getNetwork()).readFile(equil);
		Network network = sc.getNetwork();
		Collection<NetworkChangeEvent> events = new ArrayList<>();
		
		NetworkChangeEvent nceAddOne = new NetworkChangeEvent(Time.parseTime("05:00:00"));
		nceAddOne.addLink(network.getLinks().get(Id.createLinkId("6")));
		nceAddOne.addLink(network.getLinks().get(Id.createLinkId("15")));
		ChangeValue changeAdd = new ChangeValue(ChangeType.OFFSET_IN_SI_UNITS, -1);
		nceAddOne.setLanesChange(changeAdd);
		events.add(nceAddOne);

		NetworkChangeEvent nceRemoveOne = new NetworkChangeEvent(Time.parseTime("08:00:00"));
		nceRemoveOne.addLink(network.getLinks().get(Id.createLinkId("6")));
		nceRemoveOne.addLink(network.getLinks().get(Id.createLinkId("15")));
		ChangeValue changeRemove = new ChangeValue(ChangeType.OFFSET_IN_SI_UNITS, +1);
		nceRemoveOne.setLanesChange(changeRemove);
		events.add(nceRemoveOne);

		new NetworkChangeEventsWriter().write("/Users/jwjoubert/Downloads/changeEvents.xml", events );
		
		
		/* Now try and read it. */
		List<NetworkChangeEvent> readEvents = new ArrayList<>();
		NetworkChangeEventsParser p = new NetworkChangeEventsParser(network, readEvents);
		p.readFile("/Users/jwjoubert/Downloads/changeEvents.xml");
		
		Header.printFooter();
	}

}
