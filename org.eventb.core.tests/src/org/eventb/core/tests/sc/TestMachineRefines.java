/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
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

import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineRefines extends BasicSCTestWithFwdConfig {
	
	/*
	 * abstract machine identifiers have precedence over 
	 * seen context identifiers
	 */
	public void testMachineRefines_0() throws Exception {
		IContextRoot con =  createContext("con");
		addCarrierSets(con, "V1");
	
		con.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "con");
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V2∈ℕ"));

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		seesContexts(file);
		
		containsVariables(file, "V1", "V2");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		typeEnvironment.addName("V2", factory.makeIntegerType());
	
		containsInvariants(file, typeEnvironment, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2∈ℕ"));
		
		getInternalContexts(file, 0);

		hasMarker(mac.getSeesClauses()[0]);
		
	}
	
	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; carrier sets are propagated
	 */
	public void testMachineRefines_1() throws Exception {
		IContextRoot con =  createContext("con");
		addCarrierSets(con, "S1");
	
		con.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		
		addMachineSees(abs, "con");

		abs.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
	
		addMachineSees(mac, "con");
		addMachineRefines(mac, "abs");

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");

		containsMarkers(mac.getRodinFile(), false);
	}
	
	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; constants are propagated
	 */
	public void testMachineRefines_2() throws Exception {
		IContextRoot con =  createContext("con");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));
	
		con.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot abs = createMachine("abs");
		
		addMachineSees(abs, "con");

		abs.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "con");
		addMachineRefines(mac, "abs");

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

		containsMarkers(mac.getRodinFile(), false);
	}
	
	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; constants are propagated
	 * when also variables are declared
	 */
	public void testMachineRefines_3() throws Exception {
		IContextRoot con =  createContext("con");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));
	
		con.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot abs = createMachine("abs");
		
		addMachineSees(abs, "con");
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "con");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

		containsMarkers(mac.getRodinFile(), false);
	}
	
	/*
	 * variables are propagated
	 */
	public void testMachineRefines_4() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I2"), makeSList("V2=V1+1"));

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		typeEnvironment.addName("V2", factory.makeIntegerType());
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V1", "V2");
		containsInvariants(file, typeEnvironment, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2=V1+1"));

		containsMarkers(mac.getRodinFile(), false);
	}
	
	/*
	 * abstract machine not saved!
	 */
	public void testMachineRefines_5() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V2∈ℕ"));

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V2");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V2", factory.makeIntegerType());
		
		containsMarkers(abs.getRodinFile(), true);
		containsMarkers(mac.getRodinFile(), true);
	
		containsInvariants(file, typeEnvironment, 
				makeSList("I2"), makeSList("V2∈ℕ"));
		
		hasMarker(mac.getRefinesClauses()[0], EventBAttributes.TARGET_ATTRIBUTE);
		
	}

}
