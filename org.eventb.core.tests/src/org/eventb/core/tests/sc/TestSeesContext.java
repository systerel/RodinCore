/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestSeesContext extends BasicSCTest {
	
	/*
	 * Seen contexts are copied into internal contexts
	 */
	public void testSeesContext_00() throws Exception {
		IContextFile con = createContext("con");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addCarrierSets(con, makeSList("S1"));
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"));
		
		con.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1=C1"));

		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		seesContexts(file, "con");
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		containsConstants(contexts[0], "C1");
		
		containsAxioms(contexts[0], typeEnvironment, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"));
		
		containsVariables(file, "V1");
		
		typeEnvironment.addName("V1", factory.makeGivenType("S1"));
		
		containsInvariants(file, typeEnvironment, makeSList("I1"), makeSList("V1=C1"));

		containsMarkers(mac, false);
	}

	/**
	 * Ensures that a context seen only indirectly occurs as an internal
	 * context, but doesn't occur in a sees clause.
	 */
	public void testSeesContext_01() throws Exception {
		IContextFile con1 = createContext("con1");
		con1.save(null, true);
		
		IContextFile con2 = createContext("con2");
		addContextExtends(con2, "con1");
		con2.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineSees(mac, "con2");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		seesContexts(file, "con2");
		containsContexts(file, "con1", "con2");

		containsMarkers(mac, false);
	}

	/**
	 * Ensures that a context seen through the abstraction is repaired and
	 * occurs both as an internal context and in a sees clause.
	 */
	public void testSeesContext_02() throws Exception {
		IContextFile con = createContext("con");
		con.save(null, true);
		
		IMachineFile abs = createMachine("abs");
		addMachineSees(abs, "con");
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		seesContexts(file, "con");
		containsContexts(file, "con");
		
		hasMarker(mac.getRefinesClauses()[0]);
	}

	/**
	 * Ensures that a context seen through the abstraction (and where it's
	 * indirectly seen by the abstraction) is repaired and occurs 
	 * as an internal context and but not in a sees clause.
	 */
	public void testSeesContext_03() throws Exception {
		IContextFile con1 = createContext("con1");
		con1.save(null, true);
		
		IContextFile con2 = createContext("con2");
		addContextExtends(con2, "con1");
		con2.save(null, true);
		
		IMachineFile abs = createMachine("abs");
		addMachineSees(abs, "con2");
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		seesContexts(file, "con2");
		containsContexts(file, "con1", "con2");
	}

	/**
	 * Ensures that a context seen both directly and through the abstraction is
	 * not duplicated and occurs as an internal context.
	 */
	public void testSeesContext_04() throws Exception {
		IContextFile acon = createContext("acon");
		acon.save(null, true);
		
		IContextFile ccon = createContext("ccon");
		addContextExtends(ccon, "acon");
		ccon.save(null, true);
		
		IMachineFile abs = createMachine("abs");
		addMachineSees(abs, "acon");
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "acon");
		addMachineSees(mac, "ccon");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		seesContexts(file, "acon", "ccon");
		containsContexts(file, "acon", "ccon");
	}

	/**
	 * Contexts seen transitively by means of a seen contexts
	 * should not be seen directly by the machine.
	 * (This should be a warning not an error)
	 */
	public void testSeesContext_05() throws Exception {
		IContextFile cab = createContext("cab");
		
		cab.save(null, true);
		
		IContextFile cco = createContext("cco");
		addContextExtends(cco, "cab");
		IMachineFile con = createMachine("con");
		addMachineSees(con, "cco");
		addMachineSees(con, "cab");

		cco.save(null, true);
		con.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getSeesClauses()[1]);
	}

	/**
	 * A context should not be seen directly more than once by the same machine.
	 * (This should be a warning not an error)
	 */
	public void testSeesContext_06() throws Exception {
		IContextFile cco = createContext("cco");
		IMachineFile con = createMachine("con");
		addMachineSees(con, "cco");
		addMachineSees(con, "cco");

		cco.save(null, true);
		con.save(null, true);
		
		runBuilder();
		
		hasNotMarker(con.getSeesClauses()[0]);
		hasMarker(con.getSeesClauses()[1]);
	}

	/**
	 * Contexts seen transitively by means of a seen contexts
	 * should not be seen directly by the machine. All directly seen
	 * contexts of that name should have a marker.
	 * (This should be a warning not an error)
	 */
	public void testSeesContext_07() throws Exception {
		IContextFile cab = createContext("cab");
		
		cab.save(null, true);
		
		IContextFile cco = createContext("cco");
		addContextExtends(cco, "cab");
		IMachineFile con = createMachine("con");
		addMachineSees(con, "cab");
		addMachineSees(con, "cco");
		addMachineSees(con, "cab");
		addMachineSees(con, "cab");

		cco.save(null, true);
		con.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getSeesClauses()[0]);
		hasNotMarker(con.getSeesClauses()[1]);
		hasMarker(con.getSeesClauses()[2]);
		hasMarker(con.getSeesClauses()[3]);
	}

}
