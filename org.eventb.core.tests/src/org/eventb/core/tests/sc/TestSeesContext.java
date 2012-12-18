/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestSeesContext extends BasicSCTestWithFwdConfig {
	
	/*
	 * Seen contexts are copied into internal contexts
	 */
	@Test
	public void testSeesContext_00() throws Exception {
		IContextRoot con = createContext("ctx");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("S1=ℙ(S1); V2=ℙ(ℤ)",
				factory);

		addCarrierSets(con, makeSList("S1"));
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"), false, false);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1=C1"), false);
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		seesContexts(file, "ctx");
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		containsConstants(contexts[0], "C1");
		
		containsAxioms(contexts[0], typeEnvironment, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"), false, false);
		
		containsVariables(file, "V1");
		
		typeEnvironment.addName("V1", factory.makeGivenType("S1"));
		
		containsInvariants(file, typeEnvironment, makeSList("I1"), makeSList("V1=C1"), false);

		containsMarkers(mac, false);
	}

	/**
	 * Ensures that a context seen only indirectly occurs as an internal
	 * context, but doesn't occur in a sees clause.
	 */
	@Test
	public void testSeesContext_01() throws Exception {
		IContextRoot con1 = createContext("con1");
		saveRodinFileOf(con1);
		
		IContextRoot con2 = createContext("con2");
		addContextExtends(con2, "con1");
		saveRodinFileOf(con2);
		
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		addMachineSees(mac, "con2");
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		seesContexts(file, "con2");
		containsContexts(file, "con1", "con2");

		containsMarkers(mac, false);
	}

	/**
	 * Ensures that a context seen through the abstraction is repaired and
	 * occurs both as an internal context and in a sees clause.
	 */
	@Test
	public void testSeesContext_02() throws Exception {
		IContextRoot con = createContext("ctx");
		saveRodinFileOf(con);
		
		IMachineRoot abs = createMachine("abs");
		addMachineSees(abs, "ctx");
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		seesContexts(file, "ctx");
		containsContexts(file, "ctx");
		
		hasMarker(mac.getRefinesClauses()[0]);
	}

	/**
	 * Ensures that a context seen through the abstraction (and where it's
	 * indirectly seen by the abstraction) is repaired and occurs 
	 * as an internal context and but not in a sees clause.
	 */
	@Test
	public void testSeesContext_03() throws Exception {
		IContextRoot con1 = createContext("con1");
		saveRodinFileOf(con1);
		
		IContextRoot con2 = createContext("con2");
		addContextExtends(con2, "con1");
		saveRodinFileOf(con2);
		
		IMachineRoot abs = createMachine("abs");
		addMachineSees(abs, "con2");
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		seesContexts(file, "con2");
		containsContexts(file, "con1", "con2");
	}

	/**
	 * Ensures that a context seen both directly and through the abstraction is
	 * not duplicated and occurs as an internal context.
	 */
	@Test
	public void testSeesContext_04() throws Exception {
		IContextRoot acon = createContext("acon");
		saveRodinFileOf(acon);
		
		IContextRoot ccon = createContext("ccon");
		addContextExtends(ccon, "acon");
		saveRodinFileOf(ccon);
		
		IMachineRoot abs = createMachine("abs");
		addMachineSees(abs, "acon");
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "acon");
		addMachineSees(mac, "ccon");
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		seesContexts(file, "acon", "ccon");
		containsContexts(file, "acon", "ccon");
	}

	/**
	 * Contexts seen transitively by means of a seen contexts
	 * should not be seen directly by the machine.
	 * (This should be a warning not an error)
	 */
	@Test
	public void testSeesContext_05() throws Exception {
		IContextRoot cab = createContext("cab");
		
		saveRodinFileOf(cab);
		
		IContextRoot cco = createContext("cco");
		addContextExtends(cco, "cab");
		IMachineRoot con = createMachine("cnc");
		addMachineSees(con, "cco");
		addMachineSees(con, "cab");

		saveRodinFileOf(cco);
		saveRodinFileOf(con);
		
		runBuilder();
		
		hasMarker(con.getSeesClauses()[1]);
	}

	/**
	 * A context should not be seen directly more than once by the same machine.
	 * (This should be a warning not an error)
	 */
	@Test
	public void testSeesContext_06() throws Exception {
		IContextRoot cco = createContext("cco");
		IMachineRoot con = createMachine("cnc");
		addMachineSees(con, "cco");
		addMachineSees(con, "cco");

		saveRodinFileOf(cco);
		saveRodinFileOf(con);
		
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
	@Test
	public void testSeesContext_07() throws Exception {
		IContextRoot cab = createContext("cab");
		
		saveRodinFileOf(cab);
		
		IContextRoot cco = createContext("cco");
		addContextExtends(cco, "cab");
		IMachineRoot con = createMachine("cnc");
		addMachineSees(con, "cab");
		addMachineSees(con, "cco");
		addMachineSees(con, "cab");
		addMachineSees(con, "cab");

		saveRodinFileOf(cco);
		saveRodinFileOf(con);
		
		runBuilder();
		
		hasMarker(con.getSeesClauses()[0]);
		hasNotMarker(con.getSeesClauses()[1]);
		hasMarker(con.getSeesClauses()[2]);
		hasMarker(con.getSeesClauses()[3]);
	}

	/*
	 * Seen context not saved!
	 */
	@Test
	public void testSeesContext_08() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1"));
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈S1"), false);
				
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);

		saveRodinFileOf(mac);
		
		runBuilder();
		
		containsMarkers(con, true);
		containsMarkers(mac, true);
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		seesContexts(file);
		
		containsVariables(file, "V1");
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V1=ℤ;",
				factory);
		
		containsInvariants(file, typeEnvironment, makeSList("I1"), makeSList("V1∈ℕ"), false);

		hasMarker(mac.getSeesClauses()[0], EventBAttributes.TARGET_ATTRIBUTE);
	}

}
