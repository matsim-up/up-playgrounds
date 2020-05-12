/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
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
package playground.jwjoubert.tmp.guice;

import com.google.inject.*;
import playground.jwjoubert.tmp.guiceOther.*;

public class TryGuice {

	public static void main(String[] args) {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(AA.class).to(AAImpl.class);
				bind(BB.class).to(BBImpl.class).in(Singleton.class);
			}
		};

		Module module2 = new AbstractModule() {
			@Override
			protected void configure() {
				bind(AA.class).to(AAImplOther.class);
				bind(BB.class).to(BBImpl.class).in(Singleton.class);
			}
		};

		Injector injector = Guice.createInjector(module, module2);
		AA aa = injector.getInstance( AA.class );
		aa.doA();
	}
}
