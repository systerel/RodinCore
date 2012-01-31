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
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.pog;

import static org.eventb.core.IEvent.INITIALISATION;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineEvents extends EventBPOTest {
	
	/**
	 * simple case of invariant preservation
	 */
	public void testEvents_00_invariantPreservation() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
				
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+1∈0‥4");
		
	}
	
	/**
	 * invariant preservation with local variable
	 */
	public void testEvents_01_invPresWParameter() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("V1≔L1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", intType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "L1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "L1∈0‥4");
		
	}
	
	/**
	 * invariant preservation with non-deterministic assignment
	 */
	public void testEvents_03_invPresNonDet() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1"), makeSList("V1:∈L1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", powIntType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "L1⊆ℕ", "V1'∈L1");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈0‥4");
		
	}
	
	/**
	 * no PO for invariants outside frame
	 */
	public void testEvents_04_invPresNonFrame() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1", "V2");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈0‥4", "V2∈{TRUE}"), false, false);
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1"), makeSList("V1:∈L1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", powIntType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		noSequent(po, "evt/I2/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈{TRUE}", "L1⊆ℕ", "V1'∈L1");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈0‥4");
		
	}
	
	/**
	 * invariant preservation for simultaneous non-deterministic assignment
	 */
	public void testEvents_05_invPresSimNondetAssgn() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1", "V2");
		addInvariants(mac, 
				makeSList("I1", "I2", "I3"), 
				makeSList("V1∈0‥4", "V2∈BOOL", "V2=TRUE ⇒ V1<1"),
				false, false, false);
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1", "A2"), makeSList("V1:∈L1", "V2 :∈ {TRUE}"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V2", boolType);
		typeEnvironment.addName("L1", powIntType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
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
	
	/**
	 * invariant preservation for simultaneous assignment containing local variables
	 */
	public void testEvents_06_invPresSimAssgnWParam() throws Exception {

		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V0", "V1", "V2");
		addInvariants(mac, 
				makeSList("I0", "I1", "I2", "I3"), 
				makeSList("V0∈BOOL", "V1∈0‥4", "V2∈BOOL", "V2=V0 ⇒ V1<1"),
				false, false, false, false);
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
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
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

	/**
	 * two invariant preservation POs
	 */
	public void testEvents_07_twoInvPresPOs() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
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
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
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
	
	/**
	 * two invariant preservation POs with differently typed local variables
	 */
	public void testEvents_08_invPresParamTyping() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(mac, "evt1", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("V1≔L1"));
		addEvent(mac, "evt2", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1"), makeSList("V1 :∈ L1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
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
	
	/**
	 * invariant preservation: context in hypothesis
	 */
	public void testEvents_09_invPresContextInHyp() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1"));
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"), false, false);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");

		addMachineSees(mac, "ctx");
		
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("C1", given("S1"));
		typeEnvironment.addName("V1", intType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1", "C1", "S1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "C1∈S1", "1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "V1+1∈0‥4");
		
	}
	
	/**
	 * no PO for NOT generated trivial invariants
	 */
	public void testEvents_10_invPresTriv() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1", "V2", "V3");
		addInvariants(mac, 
				makeSList("I1", "I2", "I3", "I4", "I5", "I6"), 
				makeSList("V1>0", "V2∈BOOL", "V1∈ℤ", "V1∈V3", "V3⊆V3", "V3⊆ℤ"),
				false, false, false, false, false, false);
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1", "A2"), makeSList("V1≔V1+1", "V2≔TRUE"));
		addEvent(mac, "fvt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V3≔ℤ"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V2", boolType);
		typeEnvironment.addName("V3", powIntType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1", "V2", "V3");
		
		IPOSequent sequent;
		
		noSequent(po, "evt/I2/INV");
		noSequent(po, "evt/I3/INV");
		noSequent(po, "evt/I5/INV");
		noSequent(po, "evt/I6/INV");
		
		sequent = getSequent(po, "evt/I1/INV");
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1>0", "V2∈BOOL", "V1∈ℤ", "V1∈V3", "V3⊆V3", "V3⊆ℤ");
		sequentHasGoal(sequent, typeEnvironment, "V1+1>0");
		
		sequent = getSequent(po, "evt/I4/INV");
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1>0", "V2∈BOOL", "V1∈ℤ", "V1∈V3", "V3⊆V3", "V3⊆ℤ");
		sequentHasGoal(sequent, typeEnvironment, "V1+1∈V3");
		
		noSequent(po, "fvt/I1/INV");
		noSequent(po, "fvt/I2/INV");
		noSequent(po, "fvt/I3/INV");
		noSequent(po, "fvt/I4/INV");
		noSequent(po, "fvt/I5/INV");
		noSequent(po, "fvt/I6/INV");	
	}
	
	/**
	 * An event with a local variable and whose first guards produces a
	 * well-definedness PO.  That PO should declare the local variable, although
	 * no predicate involving that variable occurs in hypothesis. 
	 */
	public void testEvents_11_paramTypingInWDefPO() throws Exception {

		final String i1 = "x ∈ {0}";
		final String g1 = "v = min({0})";
		
		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("i1"), makeSList(i1), false);
		addEvent(mac, "evt1", 
				makeSList("v"), 
				makeSList("g1"), makeSList(g1), 
				makeSList("a1"), makeSList("x ≔ x + v"));
		saveRodinFileOf(mac);
	
		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPORoot po = mac.getPORoot();
		containsIdentifiers(po, "x");
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt1/g1/WD");
		sequentHasIdentifiers(sequent, "x'", "v");
		sequentHasHypotheses(sequent, typeEnvironment, i1);
		sequentHasNotHypotheses(sequent, typeEnvironment, g1);
		sequentHasGoal(sequent, typeEnvironment, "{0}≠∅ ∧ (∃b·∀x·x∈{0} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/i1/INV");
		sequentHasIdentifiers(sequent, "x'", "v");
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1);
		sequentHasGoal(sequent, typeEnvironment, "x+v ∈ {0}");
	}

	/**
	 * An event with two guards that produce a well-definedness PO.
	 */
	public void testEvents_12_twoWDefPOs() throws Exception {
		
		final String i1 = "x ∈ {0,1}";
		final String g1 = "x = min({0})";
		final String g2 = "x = min({0,1})";
		
		IMachineRoot mac =  createMachine("mac");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("i1"), makeSList(i1), false);
		addEvent(mac, "evt1", 
				makeSList(), 
				makeSList("g1", "g2"), makeSList(g1, g2), 
				makeSList("a1"), makeSList("x ≔ x + 1"));
		saveRodinFileOf(mac);
	
		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPORoot po = mac.getPORoot();
		containsIdentifiers(po, "x");
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt1/g1/WD");
		sequentHasIdentifiers(sequent, "x'");
		sequentHasHypotheses(sequent, typeEnvironment, i1);
		sequentHasNotHypotheses(sequent, typeEnvironment, g1, g2);
		sequentHasGoal(sequent, typeEnvironment, "{0}≠∅ ∧ (∃b·∀x·x∈{0} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/g2/WD");
		sequentHasIdentifiers(sequent, "x'");
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1);
		sequentHasNotHypotheses(sequent, typeEnvironment, g2);
		sequentHasGoal(sequent, typeEnvironment,
				"{0,1}≠∅ ∧ (∃b·∀x·x∈{0,1} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/i1/INV");
		sequentHasIdentifiers(sequent, "x'");
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1, g2);
		sequentHasGoal(sequent, typeEnvironment, "x+1 ∈ {0,1}");
	}

	/**
	 * An event with three guards, the last two producing a well-definedness PO.
	 */
	public void testEvents_13_moreWDefPOs() throws Exception {
		
		final String i1 = "x ∈ {0,1}";
		final String g1 = "x ∈ ℤ";
		final String g2 = "x = min({0})";
		final String g3 = "x = min({0,1})";
		
		IMachineRoot mac =  createMachine("mac");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("i1"), makeSList(i1), false);
		addEvent(mac, "evt1", 
				makeSList(), 
				makeSList("g1", "g2", "g3"), makeSList(g1, g2, g3), 
				makeSList("a1"), makeSList("x ≔ x + 1"));
		saveRodinFileOf(mac);
	
		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPORoot po = mac.getPORoot();
		containsIdentifiers(po, "x");
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt1/g2/WD");
		sequentHasIdentifiers(sequent, "x'");
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1);
		sequentHasNotHypotheses(sequent, typeEnvironment, g2, g3);
		sequentHasGoal(sequent, typeEnvironment, "{0}≠∅ ∧ (∃b·∀x·x∈{0} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/g3/WD");
		sequentHasIdentifiers(sequent, "x'");
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1, g2);
		sequentHasNotHypotheses(sequent, typeEnvironment, g3);
		sequentHasGoal(sequent, typeEnvironment,
				"{0,1}≠∅ ∧ (∃b·∀x·x∈{0,1} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/i1/INV");
		sequentHasIdentifiers(sequent, "x'");
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1, g2, g3);
		sequentHasGoal(sequent, typeEnvironment, "x+1 ∈ {0,1}");
	}
	
	/**
	 * feasibility of nondeterministic event
	 */
	public void testEvents_14_eventFeasibility() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1 :∣ V1' > 0"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/A1/FIS");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasExactlyHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "∃V1'·V1'>0");
		
	}

	/**
	 * Ensures that no PO is generated for derived invariant establishment or
	 * preservation.
	 */
	public void test_15_theoremPO() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V1<4"), false, true);
		addEvent(mac, INITIALISATION, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔0"));
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();

		getSequent(po, INITIALISATION + "/I1/INV");
		noSequent(po, INITIALISATION + "/I2/INV");

		getSequent(po, "evt/I1/INV");
		noSequent(po, "evt/I2/INV");
	}

}
