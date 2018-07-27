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

import java.util.concurrent.Callable;

/**
 * Example implementation class for {@link Callable}.
 * 
 * @author jwjoubert
 */
public class ExampleParallelCallable implements Callable<Integer> {

	private final int run;
	private int total = 0;
	
	
	public ExampleParallelCallable(int run) {
		this.run = run;
	}

	
	/**
	 * Class to basically just run some code. In this example we just run 
	 * through a loop and increment some total value;
	 */
	@Override
	public Integer call() throws Exception {
		for(int i = 0; i < this.run*100; i++) {
			total += 1;
		}
		return total;
	}

}
