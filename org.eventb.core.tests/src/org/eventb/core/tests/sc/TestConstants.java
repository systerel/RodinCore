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
public class TestConstants extends BasicTest {

	public void testConstants_0() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, makeSList("C1"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsConstants(file);
		
	}

	public void testConstants_1() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℤ"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsConstants(file, "C1");
		
		numberOfAxioms(file, 1);

	}

	public void testConstants_2() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C2∈ℤ"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsConstants(file);
		
		numberOfAxioms(file, 0);
	}
	
	public void testConstants_3() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, makeSList("C1"));
		addCarrierSets(con, makeSList("S1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈S1"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsCarrierSets(file, "S1");
		
		containsConstants(file, "C1");
		
		numberOfAxioms(file, 1);
	}
	
	public void testConstants_4() throws Exception {
		IContextFile abs1 = createContext("abs1");
		addConstants(abs1, makeSList("C1"));
		addAxioms(abs1, makeSList("A1"), makeSList("C1∈ℕ"));
		
		abs1.save(null, true);
		
		runSC(abs1);
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs1", "abs1");

		addConstants(con, makeSList("C2"));
		addAxioms(con, makeSList("A1"), makeSList("C2∈ℕ"));
		
		con.save(null, true);
		
		runSC(con);

		ISCContextFile file = con.getSCContextFile();
		
		containsConstants(file, "C2");
	
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

	}
	
	public void testConstants_5() throws Exception {
		IContextFile abs1 = createContext("abs1");
		addConstants(abs1, makeSList("C1"));
		addAxioms(abs1, makeSList("A1"), makeSList("C1∈ℕ"));
		
		abs1.save(null, true);
		
		runSC(abs1);
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs1", "abs1");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));
		
		con.save(null, true);
		
		runSC(con);

		ISCContextFile file = con.getSCContextFile();
		
		containsConstants(file);
	
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

	}

}
