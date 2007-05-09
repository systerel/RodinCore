/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
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
public class TestExtendsContext extends BasicSCTest {

	/**
	 * a carrier set should be copied into internal contexts
	 */
	public void testFetchCarrierSet_01_createCarrierSet() throws Exception {
		IContextFile abs = createContext("abs");
		addCarrierSets(abs, makeSList("S"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs");
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		extendsContexts(file, "abs");
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S");
		
		containsMarkers(con, false);
	}

	/**
	 * two carrier sets should be copied into internal contexts
	 */
	public void testFetchCarrierSet_02_twoCarrierSets() throws Exception {
		IContextFile abs = createContext("abs");
		addCarrierSets(abs, makeSList("S1", "S2"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs");
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1", "S2");
		
		containsMarkers(con, false);
	}
	
	/**
	 * internal contexts that would introduce name conflicts must be removed
	 */
	public void testFetchCarrierSet_03_extendsConflict() throws Exception {
		IContextFile abs1 = createContext("abs1");
		addCarrierSets(abs1, makeSList("S11", "S12"));
		
		abs1.save(null, true);
		
		runBuilder();
		
		IContextFile abs2 = createContext("abs2");
		addCarrierSets(abs2, makeSList("S11", "S22"));
		
		abs2.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs1");
		addContextExtends(con, "abs2");
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		extendsContexts(file);
		getInternalContexts(file, 0);
		
		hasMarker(con.getExtendsClauses()[0]);
		hasMarker(con.getExtendsClauses()[1]);
		
	}

	/**
	 * carrier sets from different contexts should be copied into corresponding internal contexts
	 */
	public void testFetchCarrierSet_04_extendsNoConflict() throws Exception {
		IContextFile abs1 = createContext("abs1");
		addCarrierSets(abs1, makeSList("S11", "S12"));
		
		abs1.save(null, true);
		
		runBuilder();
		
		IContextFile abs2 = createContext("abs2");
		addCarrierSets(abs2, makeSList("S21", "S22"));
		
		abs2.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs1");
		addContextExtends(con, "abs2");
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();

		extendsContexts(file, "abs1", "abs2");
		containsContexts(file, "abs1", "abs2");
		
		ISCInternalContext[] contexts = getInternalContexts(file, 2);
		
		containsCarrierSets(contexts[0], "S11", "S12");

		containsCarrierSets(contexts[1], "S21", "S22");
		
		containsMarkers(con, false);
	}
	
	/**
	 * internal contexts that would introduce name conflicts must be removed
	 * but contexts untouched should be kept.
	 */
	public void testFetchCarrierSet_05_extendsPartialConflict() throws Exception {
		IContextFile abs1 = createContext("abs1");
		addCarrierSets(abs1, makeSList("S11", "S12"));
		
		abs1.save(null, true);
		
		runBuilder();
		
		IContextFile abs2 = createContext("abs2");
		addCarrierSets(abs2, makeSList("S11", "S22"));
		
		abs2.save(null, true);
		
		IContextFile abs3 = createContext("abs3");
		addCarrierSets(abs3, makeSList("S31", "S32"));
		
		abs3.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs1");
		addContextExtends(con, "abs2");
		addContextExtends(con, "abs3");
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		extendsContexts(file, "abs3");
		containsContexts(file, "abs3");

		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S31", "S32");
		
		hasMarker(con.getExtendsClauses()[0]);
		hasMarker(con.getExtendsClauses()[1]);
	}
	
	/**
	 * Contexts should be included transitively.
	 */
	public void testExtendsContext_06_transitive() throws Exception {
		IContextFile abs1 = createContext("abs1");
		addCarrierSets(abs1, makeSList("S1"));
		abs1.save(null, true);
		
		IContextFile abs2 = createContext("abs2");
		addContextExtends(abs2, "abs1");
		addCarrierSets(abs2, makeSList("S2"));
		abs2.save(null, true);
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs2");
		con.save(null, true);

		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		extendsContexts(file, "abs2");
		containsContexts(file, "abs1", "abs2");
		
		containsMarkers(con, false);
	}

	/**
	 * Contexts extended transitively by means of a extended contexts
	 * should not be extended directly by the context.
	 * (This should be a warning not an error)
	 */
	public void testExtendsContext_07_redundant() throws Exception {
		IContextFile cab = createContext("cab");
		
		cab.save(null, true);
		
		IContextFile cco = createContext("cco");
		addContextExtends(cco, "cab");
		IContextFile con = createContext("con");
		addContextExtends(con, "cco");
		addContextExtends(con, "cab");

		cco.save(null, true);
		con.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getExtendsClauses()[1]);
	}

	/**
	 * A context should not be extended directly more than once by the same context.
	 * (This should be a warning not an error)
	 */
	public void testExtendsContext_08_redundant() throws Exception {
		IContextFile cco = createContext("cco");
		IContextFile con = createContext("con");
		addContextExtends(con, "cco");
		addContextExtends(con, "cco");

		cco.save(null, true);
		con.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getExtendsClauses()[1]);
	}

}
