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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.charts.XYLineChart;

/**
 * @author Amamifechukwuka
 *
 */
public class MySimpleScoringTxtFunction2 implements LinkEnterEventHandler, ScoringFunction {

	// Path p1 = Paths
	// .get("src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\transitNetwork_final\\output");

	private double[] volumeLink52;

	public MySimpleScoringTxtFunction2() {
		reset(0);
	}

	public double getTravelTime(int slot) {
		return this.volumeLink52[slot];
	}

	private int getSlot(double time) {
		return (int) time / 3600;
	}

	@Override
	public void reset(int iteration) {
		this.volumeLink52 = new double[24];
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		if (event.getLinkId().equals(Id.create("MyCiTi_52", Link.class))) {
			this.volumeLink52[getSlot(event.getTime())]++;
		}
	}

	public void writeChart(String filename) {
		double[] hours = new double[24];
		for (double i = 0.0; i < 24.0; i++) {
			hours[(int) i] = i;
		}
		XYLineChart chart = new XYLineChart("Traffic link MyCiTi_52", "hour", "departures");
		chart.addSeries("times", hours, this.volumeLink52);
		chart.saveAsPng(filename, 800, 600);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.matsim.onnene.exampleCode.ScoringFunction#getLastIterationOutput(java.
	 * lang.String)
	 */
	@Override
	public double getLastIterationOutput(String args) {
		// TODO Auto-generated method stub

		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// System.out.println("hello");
		MySimpleScoringTxtFunction2 mstf = new MySimpleScoringTxtFunction2();

		System.out.println(mstf.getLastIterationOutput(
				"C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\transitNetwork_final\\output\\ITERS\\it.50\\50.events.xml"));
		
		

		// System.out.println(mstf.getTravelTime(5));
		// mstf.writeChart(
		// "C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\exampleCode\\outputFolder\\transitNetwork_final\\output\\ITERS\\it.50\\hia.png");
		// ;

		// EventsManager eventsManager = EventsUtils.createEventsManager();

		// MatsimEventsReader reader = new MatsimEventsReader(eventsManager);

		// LinkEnterEvent linkEvtHndl = new LinkEnterEvent(50.0, "MyCiTi_52");

		// eventsManager.addHandler((EventHandler) linkEvtHndl);

	}

}
