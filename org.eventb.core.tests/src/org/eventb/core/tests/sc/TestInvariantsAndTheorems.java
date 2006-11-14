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
public class TestInvariantsAndTheorems extends BasicTest {
	
	public void testInvariantsAndTheorems_00_createInvariant() throws Exception {
		IMachineFile mac = createMachine("mac");

		addInvariants(mac, makeSList("I1"), makeSList("ℕ≠∅"));
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, emptyEnv, makeSList("I1"), makeSList("ℕ≠∅"));
		
	}
	
	public void testInvariantsAndTheorems_01_twoInvariantsLabelConflict() throws Exception {
		IMachineFile mac = createMachine("mac");

		addInvariants(mac, makeSList("I1"), makeSList("ℕ≠∅"));
		addInvariants(mac, makeSList("I1"), makeSList("ℕ=∅"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, emptyEnv, makeSList("I1"), makeSList("ℕ≠∅"));
		
	}
	
	public void testInvariantsAndTheorems_02_invariantTypeConflict() throws Exception {
		IMachineFile mac = createMachine("mac");

		addInvariants(mac, makeSList("I1"), makeSList("ℕ≠BOOL"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, emptyEnv, makeSList(), makeSList());
		
	}
	
	public void testInvariantsAndTheorems_03_invariantWithVariableOK() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈1‥0"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, emptyEnv, makeSList("I1"), makeSList("V1∈1‥0"));
		
	}
	
	public void testInvariantsAndTheorems_04_invariantUndeclaredVariable() throws Exception {
		IMachineFile mac = createMachine("mac");

		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, emptyEnv, makeSList(), makeSList());
		
	}
	
	public void testInvariantsAndTheorems_05_carrierSetType() throws Exception {
		IContextFile con =  createContext("con");
		addCarrierSets(con, "S1");
	
		con.save(null, true);
		
		runSC(con);

		IMachineFile mac = createMachine("mac");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈ℕ∪S1", "V1∈S1"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, typeEnvironment, makeSList("I2"), makeSList("V1∈S1"));
		
	}
	
	public void testInvariantsAndTheorems_06_partialTyping() throws Exception {
		IContextFile con =  createContext("con");
		addCarrierSets(con, "S1");
	
		con.save(null, true);
		
		runSC(con);

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("V1", factory.makeGivenType("S1"));
		
		IMachineFile mac = createMachine("mac");
		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1", "I2", "I3", "I4"), makeSList("V1=V1", "V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, typeEnvironment, makeSList("I2", "I3", "I4"), makeSList("V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"));
		
	}
	
	public void testInvariantsAndTheorems_07_createTheorem() throws Exception {
		IMachineFile mac = createMachine("mac");

		addTheorems(mac, makeSList("T1"), makeSList("ℕ≠∅"));
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		
	}
	
	public void testInvariantsAndTheorems_08_twoTheorems() throws Exception {
		IMachineFile mac = createMachine("mac");

		addTheorems(mac, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"));
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsTheorems(file, emptyEnv, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"));
		
	}
	
	public void testInvariantsAndTheorems_09_twoTheoremsLabelConflict() throws Exception {
		IMachineFile mac = createMachine("mac");

		addTheorems(mac, makeSList("T1"), makeSList("ℕ≠∅"));
		addTheorems(mac, makeSList("T1"), makeSList("ℕ=∅"));
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		
	}
	
	public void testInvariantsAndTheorems_10_invariantTheoremLabelConflict() throws Exception {
		IMachineFile mac = createMachine("mac");

		addInvariants(mac, makeSList("T1"), makeSList("ℕ≠∅"));
		addTheorems(mac, makeSList("T1"), makeSList("ℕ=∅"));
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		containsTheorems(file, emptyEnv, makeSList(), makeSList());
		
	}

}
