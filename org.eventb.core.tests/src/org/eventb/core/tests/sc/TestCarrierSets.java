/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCInternalContext;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestCarrierSets extends BasicSCTest {

	/**
	 * creation of carrier sets
	 */
	public void testCarrierSets_00_createCarrierSet() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S1"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsCarrierSets(file, "S1");
		
	}

	/**
	 * creation of two carrier sets
	 */
	public void testCarrierSets_01_twoCarrierSets() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S1", "S2"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsCarrierSets(file, "S1", "S2");
	}

	/**
	 * name conflict between two carrier sets
	 */
	public void testCarrierSets_02_twoCarrierSetsNameConflict() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S1", "S1"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsCarrierSets(file);
				
	}

	/**
	 * faulty names for carrier sets
	 */
	public void testCarrierSets_03_carrierSetFaultyName() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S>", "k-1", "#"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsCarrierSets(file);
	}

	/**
	 * copying of carrier sets from abstraction
	 */
	public void testCarrierSets_04_carrierSetOfAbstraction() throws Exception {
		IContextFile abs = createContext("abs");
		addCarrierSets(abs, makeSList("S1"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs");
		
		addCarrierSets(con, makeSList("S2"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsCarrierSets(file, "S2");
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
	}

	/**
	 * name conflict with carrier set from abstraction
	 */
	public void testCarrierSets_05_carrierSetOfAbstractionNameConflict() throws Exception {
		IContextFile abs = createContext("abs");
		addCarrierSets(abs, makeSList("S1"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs");
		
		addCarrierSets(con, makeSList("S1"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsCarrierSets(file);
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
	}


}
