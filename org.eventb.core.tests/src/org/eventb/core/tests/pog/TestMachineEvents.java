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
public class TestMachineEvents extends BasicPOTest {
	
	/**
	 * simple case of invariant preservation
	 */
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
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+1∈0‥4");
		
	}
	
	/**
	 * invariant preservation with local variable
	 */
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
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "L1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "L1∈0‥4");
		
	}
	
	/**
	 * invariant preservation with non-determinitic assignment
	 */
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
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "L1", "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "L1⊆ℕ", "V1'∈L1");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈0‥4");
		
	}
	
	/**
	 * no PO for invariants outside frame
	 */
	public void testEvents_04() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1", "V2");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈0‥4", "V2∈{TRUE}"));
		addEvent(mac, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1⊆ℕ"), 
				makeSList("A1"), makeSList("V1:∈L1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("L1", powIntType);
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
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
		
		runBuilder();
		
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
	
	/**
	 * invariant preservation for simultaneous assignment containing local variables
	 */
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
		
		runBuilder();
		
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

	/**
	 * two invariant preservation POs
	 */
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
		
		runBuilder();
		
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
	
	/**
	 * two invariant preservation POs whith differently typed local variables
	 */
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
		
		runBuilder();
		
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
	
	/**
	 * invariant preservation: context in hypothesis
	 */
	public void testEvents_09() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S1"));
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"));
		
		con.save(null, true);
		
		runBuilder();
		
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
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1", "C1", "S1");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "C1∈S1", "1∈ℕ");
		sequentHasGoal(sequent, typeEnvironment, "V1+1∈0‥4");
		
	}
	
	/**
	 * no PO for NOT generated trivial invariants
	 */
	public void testEvents_10() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "V1", "V2", "V3");
		addInvariants(mac, 
				makeSList("I1", "I2", "I3", "I4", "I5", "I6"), 
				makeSList("V1>0", "V2∈BOOL", "V1∈ℤ", "V1∈V3", "V3⊆V3", "V3⊆ℤ"));
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
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
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
		noSequent(po, "fvt/I6/INV");	
		
		sequent = getSequent(po, "fvt/I4/INV");
		sequentHasIdentifiers(sequent, "V3'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1>0", "V2∈BOOL", "V1∈ℤ", "V1∈V3", "V3⊆V3", "V3⊆ℤ");
		sequentHasGoal(sequent, typeEnvironment, "V1∈ℤ");
		
		sequent = getSequent(po, "fvt/I5/INV");
		sequentHasIdentifiers(sequent, "V3'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1>0", "V2∈BOOL", "V1∈ℤ", "V1∈V3", "V3⊆V3", "V3⊆ℤ");
		sequentHasGoal(sequent, typeEnvironment, "ℤ⊆ℤ");
	}
	
	/**
	 * An event with a local variable and whose first guards produces a
	 * well-definedness PO.  That PO should declare the local variable, although
	 * no predicate involving that variable occurs in hypothesis. 
	 */
	public void testEvents_11() throws Exception {

		final String i1 = "x ∈ {0}";
		final String g1 = "v = min({0})";
		
		IMachineFile mac = createMachine("mac");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("i1"), makeSList(i1));
		addEvent(mac, "evt1", 
				makeSList("v"), 
				makeSList("g1"), makeSList(g1), 
				makeSList("a1"), makeSList("x ≔ x + v"));
		mac.save(null, true);
	
		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPOFile po = mac.getPOFile();
		containsIdentifiers(po, "x");
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt1/g1/WD");
		sequentHasIdentifiers(sequent, "v");
		sequentHasHypotheses(sequent, typeEnvironment, i1);
		sequentHasNotHypotheses(sequent, typeEnvironment, g1);
		sequentHasGoal(sequent, typeEnvironment, "{0}≠∅ ∧ (∃b·∀x·x∈{0} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/i1/INV");
		sequentHasIdentifiers(sequent, "v");
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1);
		sequentHasGoal(sequent, typeEnvironment, "x+v ∈ {0}");
	}

	/**
	 * An event with two guards that produce a well-definedness PO.
	 */
	public void testEvents_12() throws Exception {
		
		final String i1 = "x ∈ {0,1}";
		final String g1 = "x = min({0})";
		final String g2 = "x = min({0,1})";
		
		IMachineFile mac = createMachine("mac");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("i1"), makeSList(i1));
		addEvent(mac, "evt1", 
				makeSList(), 
				makeSList("g1", "g2"), makeSList(g1, g2), 
				makeSList("a1"), makeSList("x ≔ x + 1"));
		mac.save(null, true);
	
		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPOFile po = mac.getPOFile();
		containsIdentifiers(po, "x");
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt1/g1/WD");
		sequentHasIdentifiers(sequent);
		sequentHasHypotheses(sequent, typeEnvironment, i1);
		sequentHasNotHypotheses(sequent, typeEnvironment, g1, g2);
		sequentHasGoal(sequent, typeEnvironment, "{0}≠∅ ∧ (∃b·∀x·x∈{0} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/g2/WD");
		sequentHasIdentifiers(sequent);
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1);
		sequentHasNotHypotheses(sequent, typeEnvironment, g2);
		sequentHasGoal(sequent, typeEnvironment,
				"{0,1}≠∅ ∧ (∃b·∀x·x∈{0,1} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/i1/INV");
		sequentHasIdentifiers(sequent);
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1, g2);
		sequentHasGoal(sequent, typeEnvironment, "x+1 ∈ {0,1}");
	}

	/**
	 * An event with three guards, the last two producing a well-definedness PO.
	 */
	public void testEvents_13() throws Exception {
		
		final String i1 = "x ∈ {0,1}";
		final String g1 = "x ∈ ℤ";
		final String g2 = "x = min({0})";
		final String g3 = "x = min({0,1})";
		
		IMachineFile mac = createMachine("mac");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("i1"), makeSList(i1));
		addEvent(mac, "evt1", 
				makeSList(), 
				makeSList("g1", "g2", "g3"), makeSList(g1, g2, g3), 
				makeSList("a1"), makeSList("x ≔ x + 1"));
		mac.save(null, true);
	
		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPOFile po = mac.getPOFile();
		containsIdentifiers(po, "x");
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt1/g2/WD");
		sequentHasIdentifiers(sequent);
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1);
		sequentHasNotHypotheses(sequent, typeEnvironment, g2, g3);
		sequentHasGoal(sequent, typeEnvironment, "{0}≠∅ ∧ (∃b·∀x·x∈{0} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/g3/WD");
		sequentHasIdentifiers(sequent);
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1, g2);
		sequentHasNotHypotheses(sequent, typeEnvironment, g3);
		sequentHasGoal(sequent, typeEnvironment,
				"{0,1}≠∅ ∧ (∃b·∀x·x∈{0,1} ⇒ b≤x)");
		
		sequent = getSequent(po, "evt1/i1/INV");
		sequentHasIdentifiers(sequent);
		sequentHasHypotheses(sequent, typeEnvironment, i1, g1, g2, g3);
		sequentHasGoal(sequent, typeEnvironment, "x+1 ∈ {0,1}");
	}

}
