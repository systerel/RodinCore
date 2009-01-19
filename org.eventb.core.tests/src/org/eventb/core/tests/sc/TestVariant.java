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
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestVariant extends BasicSCTestWithFwdConfig {
	
	/**
	 * create an integer variant
	 */
	public void testVariant_00() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariant(mac, "1");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariant(file, emptyEnv, "1");
		
		containsMarkers(mac, false);
	}

	/**
	 * create a set variant
	 */
	public void testVariant_01() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariant(mac, "{TRUE}");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariant(file, emptyEnv, "{TRUE}");
		
		containsMarkers(mac, false);
	}
	
	/**
	 * create an integer variant containing a variable
	 */
	public void testVariant_02() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addVariant(mac, "V1");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		
		containsVariant(file, typeEnvironment, "V1");
		
		containsMarkers(mac, false);
	}
	
	/**
	 * variants must be of type integer or POW(...)
	 */
	public void testVariant_03() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariant(mac, "TRUE");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariant(file, emptyEnv);
		
		hasMarker(mac.getVariants()[0]);
	}
	
	/**
	 * create an integer variant containing a variable and a constant
	 */
	public void testVariant_04() throws Exception {
		IContextRoot con = createContext("con");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));

		saveRodinFileOf(con);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");
		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addVariant(mac, "V1+C1");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("C1", factory.makeIntegerType());
		typeEnvironment.addName("V1", factory.makeIntegerType());
		
		containsVariant(file, typeEnvironment, "V1+C1");
		
		containsMarkers(mac, false);
	}
	
	/**
	 * variants must not refer to disappearing variables
	 */
	public void testVariant_05() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "V0");
		addInvariants(abs, makeSList("I0"), makeSList("V0∈ℕ"));

		saveRodinFileOf(abs);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addVariant(mac, "V1+V0");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "V0", "V1");
		containsVariant(file, emptyEnv);
		
		hasMarker(mac.getVariants()[0]);
	
	}

}
