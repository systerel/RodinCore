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
	public void testSeesContext_0() throws Exception {
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
	public void testSeesContext_1() throws Exception {
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
	public void testSeesContext_2() throws Exception {
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
	}

	/**
	 * Ensures that a context seen through the abstraction (and where it's
	 * indirectly seen by the abstraction) is repaired and occurs 
	 * as an internal context and but not in a sees clause.
	 */
	public void testSeesContext_3() throws Exception {
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

}
