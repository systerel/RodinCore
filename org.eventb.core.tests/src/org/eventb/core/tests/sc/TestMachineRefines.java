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
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineRefines extends BasicSCTestWithFwdConfig {
	
	/*
	 * abstract machine identifiers have precedence over 
	 * seen context identifiers
	 */
	@Test
	public void testMachineRefines_0() throws Exception {
		IContextRoot con =  createContext("ctx");
		addCarrierSets(con, "V1");
	
		saveRodinFileOf(con);
		
		runBuilder();

		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);

		saveRodinFileOf(abs);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V2∈ℕ"), false);

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		seesContexts(file);
		
		containsVariables(file, "V1", "V2");
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V1=ℤ; V2=ℤ",
				factory);
	
		containsInvariants(file, typeEnvironment, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2∈ℕ"), false, false);
		
		getInternalContexts(file, 0);

		hasMarker(mac.getSeesClauses()[0]);
		
	}
	
	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; carrier sets are propagated
	 */
	@Test
	public void testMachineRefines_1() throws Exception {
		IContextRoot con =  createContext("ctx");
		addCarrierSets(con, "S1");
	
		saveRodinFileOf(con);
		
		runBuilder();

		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		
		addMachineSees(abs, "ctx");

		saveRodinFileOf(abs);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
	
		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");

		containsMarkers(mac, false);
	}
	
	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; constants are propagated
	 */
	@Test
	public void testMachineRefines_2() throws Exception {
		IContextRoot con =  createContext("ctx");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"), true);
	
		saveRodinFileOf(con);
		
		runBuilder();

		IMachineRoot abs = createMachine("abs");
		
		addMachineSees(abs, "ctx");
		
		addInitialisation(abs);

		saveRodinFileOf(abs);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

		containsMarkers(mac, false);
	}
	
	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; constants are propagated
	 * when also variables are declared
	 */
	@Test
	public void testMachineRefines_3() throws Exception {
		IContextRoot con =  createContext("ctx");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"), true);
	
		saveRodinFileOf(con);
		
		runBuilder();

		IMachineRoot abs = createMachine("abs");
		
		addMachineSees(abs, "ctx");
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(abs, "V1");

		saveRodinFileOf(abs);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

		containsMarkers(mac, false);
	}
	
	/*
	 * variables are propagated
	 */
	@Test
	public void testMachineRefines_4() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(abs, "V1");

		saveRodinFileOf(abs);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I2"), makeSList("V2=V1+1"), true);
		IEvent ini = addInitialisation(mac, "V2");
		addEventWitness(ini, "V1'", "⊤");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V1=ℤ; V2=ℤ",
				factory);
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V1", "V2");
		containsInvariants(file, typeEnvironment, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2=V1+1"), true, true);

		containsMarkers(mac, false);
	}
	
	/*
	 * abstract machine not saved!
	 */
	@Test
	public void testMachineRefines_5() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);

		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V2∈ℕ"), false);

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V2");
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V2=ℤ",
				factory);
		
		containsMarkers(abs, true);
		containsMarkers(mac, true);
	
		containsInvariants(file, typeEnvironment, 
				makeSList("I2"), makeSList("V2∈ℕ"), false);
		
		hasMarker(mac.getRefinesClauses()[0], EventBAttributes.TARGET_ATTRIBUTE);
		
	}

}
