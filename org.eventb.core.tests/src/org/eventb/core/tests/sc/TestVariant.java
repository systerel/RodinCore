/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestVariant extends BasicSCTest {
	
	/**
	 * create an integer variant
	 */
	public void testVariant_00() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariant(mac, "1");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariant(file, emptyEnv, "1");
		
		containsMarkers(mac, false);
	}

	/**
	 * create a set variant
	 */
	public void testVariant_01() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariant(mac, "{TRUE}");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariant(file, emptyEnv, "{TRUE}");
		
		containsMarkers(mac, false);
	}
	
	/**
	 * create an integer variant containing a variable
	 */
	public void testVariant_02() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addVariant(mac, "V1");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		
		containsVariant(file, typeEnvironment, "V1");
		
		containsMarkers(mac, false);
	}
	
	/**
	 * variants must be of type integer or POW(...)
	 */
	public void testVariant_03() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariant(mac, "TRUE");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariant(file, emptyEnv);
		
	}
	
	/**
	 * create an integer variant containing a variable and a constant
	 */
	public void testVariant_04() throws Exception {
		IContextFile con = createContext("con");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));

		con.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addVariant(mac, "V1+C1");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
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
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "V0");
		addInvariants(abs, makeSList("I0"), makeSList("V0∈ℕ"));

		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addVariant(mac, "V1+V0");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file, "V0", "V1");
		containsVariant(file, emptyEnv);
		
	}

}
