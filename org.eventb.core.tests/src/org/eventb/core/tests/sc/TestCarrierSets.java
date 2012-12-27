/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCInternalContext;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestCarrierSets extends BasicSCTestWithFwdConfig {

	/**
	 * creation of carrier sets
	 */
	@Test
	public void testCarrierSets_00_createCarrierSet() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file, "S1");
		
		containsMarkers(con, false);
	}

	/**
	 * creation of two carrier sets
	 */
	@Test
	public void testCarrierSets_01_twoCarrierSets() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1", "S2"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file, "S1", "S2");
		
		containsMarkers(con, false);
	}

	/**
	 * name conflict between two carrier sets
	 */
	@Test
	public void testCarrierSets_02_twoCarrierSetsNameConflict() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1", "S1"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file);
				
		hasMarker(con.getCarrierSets()[0]);
		hasMarker(con.getCarrierSets()[1]);
	}

	/**
	 * faulty names for carrier sets
	 */
	@Test
	public void testCarrierSets_03_carrierSetFaultyName() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S>", "k-1", "#"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file);
		
		hasMarker(con.getCarrierSets()[0]);
		hasMarker(con.getCarrierSets()[1]);
		hasMarker(con.getCarrierSets()[2]);

	}

	/**
	 * copying of carrier sets from abstraction
	 */
	@Test
	public void testCarrierSets_04_carrierSetOfAbstraction() throws Exception {
		IContextRoot abs = createContext("abs");

		addCarrierSets(abs, makeSList("S1"));
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		
		addCarrierSets(con, makeSList("S2"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file, "S2");
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		
		containsMarkers(con, false);
	}

	/**
	 * name conflict with carrier set from abstraction
	 */
	@Test
	public void testCarrierSets_05_carrierSetOfAbstractionNameConflict() throws Exception {
		IContextRoot abs = createContext("abs");
		addCarrierSets(abs, makeSList("S1"));
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		
		addCarrierSets(con, makeSList("S1"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file);
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		
		hasMarker(con.getCarrierSets()[0]);
		hasMarker(con.getExtendsClauses()[0]);
	}


}
