/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEvents extends BasicTest {
	
	public void testEvents_00() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+1∈0‥4");
		
	}
	
	public void testEvents_01() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("V1≔L1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", intType);
		
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "L1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "L1∈0‥4");
		
	}
	
	public void testEvents_03() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1"), makeSList("V1:∈L1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", powIntType);
		
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "L1⊆ℕ", "V1'∈L1");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈0‥4");
		
	}
	
	public void testEvents_04() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1", "V2");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈0‥4", "V2∈BOOL"));
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1"), makeSList("V1:∈L1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", powIntType);
		
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		noSequent(po, "evt/I2/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈BOOL", "L1⊆ℕ", "V1'∈L1");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈0‥4");
		
	}
	
	public void testEvents_05() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1", "V2");
		addInvariants(mac, 
				makeSList("I1", "I2", "I3"), 
				makeSList("V1∈0‥4", "V2∈BOOL", "V2=TRUE ⇒ V1<1"));
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1", "A2"), makeSList("V1:∈L1", "V2 :∈ {TRUE}"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V2", boolType);
		typeEnvironment.addName("L1", powIntType);
		
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, 
				"V1∈0‥4", "V2=TRUE ⇒ V1<1", "V2∈BOOL", "L1⊆ℕ", "V1'∈L1");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈0‥4");
		
		sequent = getSequent(po, "evt/I3/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, 
				"V1∈0‥4", "V2=TRUE ⇒ V1<1", "V2∈BOOL", "L1⊆ℕ", "V1'∈L1", "V2' ∈ {TRUE}");
		sequentHasGoal(sequent, typeEnvironment, "V2'=TRUE ⇒ V1'<1");
		
	}
	
	public void testEvents_06() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V0", "V1", "V2");
		addInvariants(mac, 
				makeSList("I0", "I1", "I2", "I3"), 
				makeSList("V0∈BOOL", "V1∈0‥4", "V2∈BOOL", "V2=V0 ⇒ V1<1"));
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A0", "A1", "A2"), 
				makeSList("V0≔bool(V1≠L1)", "V1≔L1", "V2≔bool(V1=L1)"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V0", boolType);
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V2", boolType);
		typeEnvironment.addName("L1", intType);
		
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V0", "V1", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V0'", "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, 
				"V0∈BOOL", "V1∈0‥4", "V2=V0 ⇒ V1<1", "V2∈BOOL", "L1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "L1∈0‥4");
		
		sequent = getSequent(po, "evt/I3/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V0'", "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, 
				"V0∈BOOL", "V1∈0‥4", "V2=V0 ⇒ V1<1", "V2∈BOOL", "L1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "bool(V1=L1)=bool(V1≠L1)⇒L1<1");
		
	}

	public void testEvents_07() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(mac, "evt1", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		addEvent(mac, "evt2", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
	
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt1/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+1∈0‥4");
		
		sequent = getSequent(po, "evt2/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+2∈0‥4");
		
	}
	
	public void testEvents_08() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(mac, "evt1", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("V1≔L1"));
		addEvent(mac, "evt2", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1"), makeSList("V1 :∈ L1"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", intType);
		
		IPOSequent sequent = getSequent(po, "evt1/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'", "L1");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "L1∈0‥4");
		
		typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", powIntType);
		
		sequent = getSequent(po, "evt2/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'", "L1");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V1'∈L1");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈0‥4");
		
	}
	
	public void testEvents_09() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S1"));
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"));
		
		con.save(null, true);
		
		runSC(con);
		
		IMachineFile mac = createMachine("mac");

		addMachineSees(mac, "con");
		
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("C1", given("S1"));
		typeEnvironment.addName("V1", intType);
		
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1", "C1", "S1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "C1∈S1", "1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "V1+1∈0‥4");
		
	}
	
	public void testEvents_10() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"));
		addEvent(mac, "evt", makeSList(), makeSList(), makeSList(), makeSList("A1"), makeSList("L1≔1"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "L1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1'");
		sequentHasHypotheses(sequent, typeEnvironment, "L1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "1∈ℕ");
		
	}
	
}
