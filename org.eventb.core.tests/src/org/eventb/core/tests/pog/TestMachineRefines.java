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
 *     Systerel - mathematical language V2
 *     University of Dusseldorf - added theorem attribute
 *     Systerel - fix bug #3469348: Bug in INITIALISATION FIS PO
 *******************************************************************************/
package org.eventb.core.tests.pog;

import static junit.framework.Assert.assertTrue;
import static org.eventb.core.IEvent.INITIALISATION;
import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineRefines extends EventBPOTest {
	
	/**
	 * rewriting of deterministic action simulation POs
	 */
	@Test
	public void testRefines_00() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ", factory);
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
		addEventRefines(event, "evt");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/A1/SIM");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+2=V1+1");
		
	}
	
	/**
	 * rewriting of action frame simulation POs
	 */
	@Test
	public void testRefines_01() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ", factory);
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
		addEventRefines(event, "evt");
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/V1/EQL");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+2=V1");
		
	}
	
	/**
	 * simulation and invariant preservation using global witnesses
	 */
	@Test
	public void testRefines_02() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ", factory);
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V2∈0‥5", "V2≥V1"), false, false);
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V2≔V2+2"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("V1'"), makeSList("V1'≥V2'"));
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1");
		sequentHasGoal(sequent, typeEnvironment, "V2+2∈0‥5");

		sequent = getSequent(po, "evt/I2/INV");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1", "V1'≥V2+2");
		sequentHasGoal(sequent, typeEnvironment, "V2+2≥V1'");
		
		sequent = getSequent(po, "evt/A1/SIM");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1", "V1'≥V2+2");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈ℕ");
		
	}
	
	/**
	 * simulation and invariant preservation using local witnesses
	 */
	@Test
	public void testRefines_03() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1", "V2");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("V1∈0‥4", "V2≥6"), false, false);
		addEvent(abs, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ∖{0}"), 
				makeSList("A1", "A2"), makeSList("V1≔L1", "V2≔7"));
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1X", "V2");
		addInvariants(mac, makeSList("I3"), makeSList("V1X=V1+1"), false);
		IEvent event = addEvent(mac, "evt", 
				makeSList("L2"), 
				makeSList("L2"), makeSList("L2∈ℕ"), 
				makeSList("A1"), makeSList("V1X≔L2"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("L1"), makeSList("L1=L2−1"));
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ; V2=ℤ; V1X=ℤ; L1=ℤ; L2=ℤ", factory);
		
		containsIdentifiers(po, "V1", "V1X", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I3/INV");
		
		sequentHasIdentifiers(sequent, "L1", "L2", "V1'", "V2'", "V1X'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2≥6", "V1X=V1+1");
		sequentHasGoal(sequent, typeEnvironment, "L2=(L2−1)+1");

	}

	/*
	 * POG attempts to store twice the predicate set "ALLHYP", once for the
	 * well-definedness PO of the guard, and then once at the end of the
	 * machine.
	 */
	@Test
	public void testRefines_04() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("G"), makeSList("0 ≤ min({0})"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po);
		getSequent(po, "evt/G/WD");
	}

	/*
	 * PO filter: the POG should not generate WD POs for guards when these
	 * conditions have already been proved for the abstract event
	 */
	@Test
	public void testRefines_05() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3"), makeSList("x ÷ x > x", "1 ÷ x > 1", "2÷x = x"), 
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3", "G4"), makeSList("5 ÷ (x+x) = −1", "1 ÷ x > 1", "x ÷ x > x", "2÷x = x"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/G1/WD");
		sequentHasGoal(sequent, environment, "(x+x)≠0");
		
		sequent= getSequent(po, "evt/G2/WD");
		sequentHasGoal(sequent, environment, "x≠0");
		
		noSequent(po, "evt/G3/WD");
		
		noSequent(po, "evt/G4/WD");
	}

	/*
	 * PO filter: the POG should not generate guard strengthening PO
	 * for an abstract guard which is syntactically (but normalized) contained
	 * in the concrete guards.
	 */
	@Test
	public void testRefines_06() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2", "GA3"), makeSList("x > x", "1 > 1", "x−1∈ℕ"), 
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3"), makeSList("1 > 1", "x > x", "x = x"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		noSequent(po, "evt/GA1/GRD");
		
		noSequent(po, "evt/GA2/GRD");

		sequent= getSequent(po, "evt/GA3/GRD");
		sequentHasGoal(sequent, environment, "x−1∈ℕ");
	}

	/*
	 * PO filter: inherited events should only produce invariant preservation POs
	 */
	@Test
	public void testRefines_07() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "A");
		addInvariants(abs, makeSList("I"), makeSList("A∈ℕ"), false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2"), makeSList("1 > x", "x−1∈ℕ"), makeBList(false, true),
				makeSList("S"), makeSList("A≔A+1"));
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addInvariants(ref, makeSList("J"), makeSList("A=B"), false);
		IEvent evt = addExtendedEvent(ref, "evt");
		addEventRefines(evt, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"A=ℤ; B=ℤ; x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "A", "B");
		
		IPOSequent sequent;
		
		noSequent(po, "evt/G1/REF");
		noSequent(po, "evt/G2/REF");
		noSequent(po, "evt/G2/THM");
		noSequent(po, "evt/S/SIM");
		
		sequent= getSequent(po, "evt/J/INV");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "1 > x", "x−1∈ℕ");
		sequentHasGoal(sequent, environment, "A+1=B");
	}

	/*
	 * PO filter: do not produce WD and FIS POs for repeated actions
	 */
	@Test
	public void testRefines_08() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"), false, false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x÷x"));
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B", "C");
		addInvariants(ref, makeSList("J"), makeSList("C=B"), false);
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList("SC1", "SC2", "SC3"), makeSList("A :∣ A'>x", "B ≔ x÷x", "C :∈ {1÷x}"));
		addEventRefines(event, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"A=ℤ; B=ℤ; C=ℤ; x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "A", "B", "C");
		
		IPOSequent sequent;
		
		noSequent(po, "evt/SA1/SIM");
		noSequent(po, "evt/SA2/SIM");
		noSequent(po, "evt/SC1/WD");
		noSequent(po, "evt/SC1/FIS");
		noSequent(po, "evt/SC2/WD");
		noSequent(po, "evt/SC2/FIS");
		
		sequent= getSequent(po, "evt/GA/GRD");
		sequentHasExactlyHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x−1∈ℕ");
		
		sequent= getSequent(po, "evt/SC3/WD");
		sequentHasExactlyHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0", "A'>x");
		sequentHasGoal(sequent, environment, "x≠0");
		
		sequent= getSequent(po, "evt/SC3/FIS");
		sequentHasExactlyHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0", "A'>x");
		sequentHasGoal(sequent, environment, "{1÷x}≠∅");
	}	
	
	/*
	 * create event merge POs (simple)
	 */
	@Test
	public void testRefines_09() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList(), makeSList());
		addEvent(abs, "fvt", 
				makeSList("x"), 
				makeSList("HA"), makeSList("x+1∈ℕ"), 
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/MRG");
		sequentHasHypotheses(sequent, environment, "x>0");
		sequentHasGoal(sequent, environment, "x−1∈ℕ ∨ x+1∈ℕ");
	}

	/*
	 * create event merge POs (complicated)
	 */
	@Test
	public void testRefines_10() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"), false, false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "fvt", 
				makeSList("x", "y"), 
				makeSList("HA1", "HA2"), makeSList("x+1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B", "C");
		addInvariants(ref, makeSList("J"), makeSList("C=B"), false);
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList("SC1", "SC2", "SC3"), makeSList("A :∣ A'>x", "B ≔ x+1", "C :∈ {x+1}"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"A=ℤ; B=ℤ; C=ℤ; x=ℤ; y=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "A", "B", "C");
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/MRG");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x−1∈ℕ ∨ (x+1∈ℕ ∧ x=y+y)");
		
		sequent= getSequent(po, "evt/SA2/SIM");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x+1=x");
	}

	/*
	 * filter repeated guards from event merge POs
	 */
	@Test
	public void testRefines_11() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"), false, false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2"), makeSList("x−1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "fvt", 
				makeSList("x", "y"), 
				makeSList("HA1", "HA2"), makeSList("x+1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "gvt", 
				makeSList("x", "y"), 
				makeSList("IA1", "IA2"), makeSList("x=y+y", "A>1"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x", "y"), 
				makeSList("GC"), makeSList("x=y+y"), 
				makeSList("SC1"), makeSList("A :∣ A'>x"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		addEventRefines(event, "gvt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"A=ℤ; B=ℤ; C=ℤ; x=ℤ; y=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "A", "B");
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/MRG");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "x=y+y");
		sequentHasGoal(sequent, environment, "x−1∈ℕ ∨ x+1∈ℕ ∨ A>1");
		
		sequent= getSequent(po, "evt/SA2/SIM");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "x=y+y");
		sequentHasGoal(sequent, environment, "B=x");
	}

	/*
	 * filter event merge POs entirely if one of the disjuncts is true
	 */
	@Test
	public void testRefines_12() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"), false, false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2"), makeSList("x−1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "fvt", 
				makeSList("x", "y"), 
				makeSList("HA1", "HA2"), makeSList("x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x", "y"), 
				makeSList("GC"), makeSList("x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		addEventRefines(event, "gvt");
		saveRodinFileOf(ref);
		runBuilder();
			
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "A", "B");
		
		noSequent(po, "evt/MRG");
		
		noSequent(po, "evt/SA1/SIM");
		noSequent(po, "evt/SA2/SIM");
	}

	/*
	 * PO filter: do not produce any POs in identical refinements
	 */
	@Test
	public void testRefines_13() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"), false, false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA", "HA"), makeSList("x>0", "B÷x>0"), makeBList(false, true), 
				makeSList("SA", "TA"), makeSList("A :∣ A'>x", "B ≔ x÷x"));
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addExtendedEvent(ref, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		IPORoot apo = abs.getPORoot();
		containsIdentifiers(apo, "A", "B");
		
		getSequent(apo, "evt/HA/WD");
		getSequent(apo, "evt/HA/THM");
		getSequent(apo, "evt/SA/FIS");
		getSequent(apo, "evt/TA/WD");
		getSequent(apo, "evt/I1/INV");
		getSequent(apo, "evt/I2/INV");
		
		IPORoot cpo = ref.getPORoot();
		containsIdentifiers(cpo, "A", "B");
		
		// no sequents!
		getExactSequents(cpo);
	}
	
	/*
	 * Generate PO for event action that modifies abstract preserved variable;
	 * two cases
	 * 	(1) the event is new
	 * 	(2) the abstract event is empty
	 * (POs should be identical!)
	 */
	@Test
	public void testRefines_14() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"), false);
		addEvent(abs, "fvt");

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "p");
		addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("p≔TRUE"));
		IEvent fvt = addEvent(ref, "fvt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("p≔TRUE"));
		addEventRefines(fvt, "fvt");
	
		saveRodinFileOf(ref);
		
		runBuilder();
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "p");
		
		getSequent(po, "evt/p/EQL");
		getSequent(po, "fvt/p/EQL");
		
	}
	
	/*
	 * Proper naming in goals with nondeterministic witnesses
	 */
	@Test
	public void testRefines_15() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"), false);
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("p :∣ p'≠p"));

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "q");
		addInvariants(ref, makeSList("J"), makeSList("q∈BOOL"), false);
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("q≔q"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("p'"), makeSList("p'≠q'"));
	
		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"p=BOOL; q=BOOL; p'=BOOL; q'=BOOL", factory);

		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "p", "q");
		
		IPOSequent sequent = getSequent(po, "evt/A/SIM");
		sequentHasIdentifiers(sequent, "p'", "q'");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "q∈BOOL", "p'≠q");
		sequentHasGoal(sequent, environment, "p'≠p");
	}
	
	/*
	 * If the event variable witnesses do not refer to post state variable values,
	 * a more efficient way of POs can be generated:
	 * (1) the concrete non-determistic actions can be removed from the hypothesis of /GRD
	 * (2) and, as a consequence, the abstract non-determistic actions can be put in the
	 * hypothesis of the concrete /FIS.
	 */
	@Test
	public void testRefines_16() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"), false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x≠p"), 
				makeSList("A"), makeSList("p :∣ p'≠x"));

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "p");
		
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("p :∣ p'≠p"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("x"), makeSList("p'=x"));
		
		IEvent fvt = addEvent(ref, "fvt", 
				makeSList("y"), 
				makeSList("H"), makeSList("y≠p"), 
				makeSList("B"), makeSList("p :∣ p'≠y"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x"), makeSList("y=x"));
	
		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"p=BOOL; p'=BOOL; x=BOOL; y=BOOL", factory);

		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "p");
		
		IPOSequent sequent = getSequent(po, "evt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "x");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p'=x", "p'≠p");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "evt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "x");
		sequentHasExactlyHypotheses(sequent, environment, "p∈BOOL");
		sequentHasGoal(sequent, environment, "∃p'·p'≠p");
		
		sequent = getSequent(po, "fvt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "y≠p", "y=x");
		sequentHasNotHypotheses(sequent, environment, "p'≠p");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "fvt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "x", "y");
		sequentHasExactlyHypotheses(sequent, environment, "p∈BOOL", "y≠p", "y=x", "p'≠x");
		sequentHasGoal(sequent, environment, "∃p'·p'≠y");
	}
	
	/*
	 * If the event variable witnesses do not refer to post state variable values of
	 * certain variables, then corresponding before-after predicates do not need to be
	 * added to the hypothesis of /GRD
	 */
	@Test
	public void testRefines_17() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I", "J"), makeSList("p∈BOOL", "q∈BOOL"), false, false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x≠p"), 
				makeSList("A", "B"), makeSList("p :∣ p'≠x", "q :∣ q'≠p"));

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "p", "q");
		
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A", "B"), makeSList("p :∣ p'≠p", "q :∣ q'≠q"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("x"), makeSList("p'=x"));
	
		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"p=BOOL; p'=BOOL; x=BOOL; y=BOOL", factory);

		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "p", "q");
		
		IPOSequent sequent = getSequent(po, "evt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p'=x", "p'≠p");
		sequentHasNotHypotheses(sequent, environment, "q'≠p");
		sequentHasGoal(sequent, environment, "x≠p");
	}
	
	/*
	 * Additional abstract before-after predicates in a /FIS proof obligation must
	 * be correctly rewritten using the witnesses (all witnesses!)
	 */
	@Test
	public void testRefines_18() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"), false);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x≠p"), 
				makeSList("A"), makeSList("p :∣ p'≠x"));

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "q");
		addInvariants(ref, makeSList("J"), makeSList("p∈{q}"), false);
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList("y"), 
				makeSList("H"), makeSList("y∈{q}"), 
				makeSList("B"), makeSList("q :∣ q'≠q"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("x", "p'"), makeSList("y=x", "p'=y"));
		
		IEvent fvt = addEvent(ref, "fvt", 
				makeSList("y"), 
				makeSList("H"), makeSList("y≠q"), 
				makeSList("B"), makeSList("q :∣ q'≠y"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x", "p'"), makeSList("x=y", "p'=y"));
		
		IEvent gvt = addEvent(ref, "gvt", 
				makeSList("y"), 
				makeSList("H"), makeSList("y≠q"), 
				makeSList("B"), makeSList("q :∣ q'≠y"));
		addEventRefines(gvt, "evt");
		addEventWitnesses(gvt, makeSList("x", "p'"), makeSList("y=x", "y=p'"));
		
		IEvent hvt = addEvent(ref, "hvt", 
				makeSList(), 
				makeSList("H"), makeSList("q∈{q}"), 
				makeSList("B"), makeSList("q :∣ q'≠q"));
		addEventRefines(hvt, "evt");
		addEventWitnesses(hvt, makeSList("x", "p'"), makeSList("q'=x", "q'=p'"));

		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"p=BOOL; p'=BOOL; q=BOOL; q'=BOOL; x=BOOL; y=BOOL", factory);

		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "p", "q");
		
		IPOSequent sequent = getSequent(po, "evt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "p∈{q}", "y∈{q}", "y=x");
		sequentHasNotHypotheses(sequent, environment, "p'=y", "y≠x");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "evt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasExactlyHypotheses(sequent, environment, //
				"p∈BOOL", "p∈{q}", "y∈{q}", "y=x", "p'≠x");
		sequentHasNotHypotheses(sequent, environment, "p'=y");
		sequentHasGoal(sequent, environment, "∃q'·q'≠q");
		
		sequent = getSequent(po, "fvt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "y≠q");
		sequentHasGoal(sequent, environment, "y≠p");
		
		sequent = getSequent(po, "fvt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasExactlyHypotheses(sequent, environment, //
				"p∈BOOL", "p∈{q}", "y≠q", "p'≠y");
		sequentHasGoal(sequent, environment, "∃q'·q'≠y");
		
		sequent = getSequent(po, "gvt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "p∈{q}", "y≠q", "y=x");
		sequentHasNotHypotheses(sequent, environment, "y=p'");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "gvt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasExactlyHypotheses(sequent, environment, //
				"p∈BOOL", "p∈{q}", "p∈{q}", "y≠q", "y=x", "p'≠x");
		sequentHasGoal(sequent, environment, "∃q'·q'≠y");
		
		sequent = getSequent(po, "hvt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "q∈{q}", "q'=x", "q'≠q");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "hvt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "q'", "x");
		sequentHasExactlyHypotheses(sequent, environment, //
				"p∈BOOL", "p∈{q}", "q∈{q}");
		sequentHasNotHypotheses(sequent, environment, "q'=x", "q'=p'", "p'≠x");
		sequentHasGoal(sequent, environment, "∃q'·q'≠q");
	}
	
	/*
	 * Disappearing variables in deterministic actions must not be simulated,
	 * but the preserved variables must be simulated. This also holds for
	 * multiple assignments!
	 */
	@Test
	public void testRefines_19() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x", "y");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈ℤ", "y∈ℤ"), false, false);
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x,y ≔ y,x"));

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "y", "z");
		addInvariants(ref, makeSList("K", "L"), makeSList("z∈ℤ", "y≤x"), false, false);
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("y,z ≔ z,y"));
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ; y=ℤ; z=ℤ", factory);

		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "x", "y", "z");
		
		IPOSequent 
		sequent = getSequent(po, "evt/A/SIM");
		sequentHasIdentifiers(sequent, "x'", "y'", "z'");
		sequentHasHypotheses(sequent, environment, "x∈ℤ", "y∈ℤ", "z∈ℤ", "y≤x");
		sequentHasGoal(sequent, environment, "z=x");
		
		sequent = getSequent(po, "evt/L/INV");
		sequentHasIdentifiers(sequent, "x'", "y'", "z'");
		sequentHasHypotheses(sequent, environment, "x∈ℤ", "y∈ℤ", "z∈ℤ", "y≤x");
		sequentHasGoal(sequent, environment, "z≤y");
	}
	
	/*
	 * Check if types of local variables of abstract event are added to type environment
	 */
	@Test
	public void testRefines_20() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x", "y");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈ℤ", "y∈ℤ"), false, false);
		addEvent(abs, "evt", 
				makeSList("a", "b"), 
				makeSList("G", "H"), makeSList("a ∈ ℕ", "b ∈ {a}"), 
				makeSList("A"), makeSList("x,y ≔ a,b"));

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "x", "y");
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList("c"), 
				makeSList("GG"), makeSList("c ∈ ℕ"), 
				makeSList("B"), makeSList("x,y ≔ c,c"));
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ; y=ℤ; a=ℤ; b=ℤ; c=ℤ", factory);

		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "x", "y");
		
		IPOSequent 
		sequent = getSequent(po, "evt/A/SIM");
		sequentHasIdentifiers(sequent, "x'", "y'", "a", "b", "c");
		sequentHasGoal(sequent, environment, "c=a ∧ c=b");
	}
	
	/*
	 * Check if invariant preservation PO is generated for event with
	 * empty list of actions (i.e. the concrete action is skip)
	 */
	@Test
	public void testRefines_21() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"), false);
		addEvent(abs, "evt", 
				makeSList("a"), 
				makeSList("G"), makeSList("a ∈ ℕ"), 
				makeSList("A"), makeSList("x ≔ a"));

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "y");
		addInvariants(ref, makeSList("J"), makeSList("x+y=2"), false);
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("H"), makeSList("y=1"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("a"), makeSList("a=y'"));

		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ; y=ℤ; a=ℤ", factory);

		IPORoot po = ref.getPORoot();
		containsIdentifiers(po, "x", "y");
		
		IPOSequent 
		sequent = getSequent(po, "evt/J/INV");
		sequentHasIdentifiers(sequent, "x'", "a", "y'");
		sequentHasHypotheses(sequent, environment, "y=1", "x+y=2");
		sequentHasGoal(sequent, environment, "y+y=2");
	}
	
	/*
	 * Check that there are no witness-related POs (WFIS, WWD) for an inherited event.
	 */
	@Test
	public void testRefines_22() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"), false);
		addInitialisation(abs, "x");
		addEvent(abs, "evt", 
				makeSList("a"), 
				makeSList("G"), makeSList("a ∈ ℕ"), 
				makeSList("A"), makeSList("x ≔ a"));

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "y");
		addInvariants(ref, makeSList("J"), makeSList("x+y=2"), false);
	
		IEvent ini = addInitialisation(ref, "y");
		addEventWitnesses(ini, makeSList("x'"), makeSList("y'=x'÷1"));
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("H"), makeSList("y=1"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("a"), makeSList("a÷1=y'"));

		saveRodinFileOf(ref);
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "ref");
		addVariables(con, "y");
	
		addExtendedEvent(con, IEvent.INITIALISATION);
		addExtendedEvent(con, "evt");

		saveRodinFileOf(con);
		
		runBuilder();
		
		IPORoot po = ref.getPORoot();
		
		getSequent(po, IEvent.INITIALISATION + "/x'/WFIS");
		getSequent(po, IEvent.INITIALISATION + "/x'/WWD");
		getSequent(po, "evt/a/WFIS");
		getSequent(po, "evt/a/WWD");

		po = con.getPORoot();
		
		noSequent(po, IEvent.INITIALISATION + "/x'/WFIS");
		noSequent(po, IEvent.INITIALISATION + "/x'/WWD");
		noSequent(po, "evt/a/WFIS");
		noSequent(po, "evt/a/WWD");
	}

	/*
	 * Check that there are no witness-related POs (WFIS, WWD) for an inherited event.
	 */
	@Test
	public void testBug_1920752() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addVariables(abs, "v1", "v2");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("v1∈ℤ", "v2∈ℤ"), false, false);
		addInitialisation(abs, "v1", "v2");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1", "A2"), makeSList("v1 :∈ ℤ", "v2 ≔ v1"));
		saveRodinFileOf(abs);
		
		final IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "v3");
		addInvariants(ref, makeSList("J"), makeSList("v3 = v1 ↦ v2"), false);
	
		final IEvent ini = addInitialisation(ref, "v3");
		addEventWitnesses(ini,
				makeSList("v1'", "v2'"),
				makeSList("v1' ∈ dom({v3'})", "v2' ∈ ran({v3'})"));

		final IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("v3 :∈ ℤ×dom({v3})"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("v1'"), makeSList("v1' ∈ dom({v3'})"));
		saveRodinFileOf(ref);
		
		runBuilder();
		
		final IPORoot po = ref.getPORoot();
		final IPOSequent sequent = getSequent(po, "evt/J/INV");
		
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment(
				"v1=ℤ; v1'=ℤ", factory);
		sequentHasGoal(sequent, typenv, "v3' = v1' ↦ v1");
	}

	/**
	 * Test with all possible data refinements together.
	 */
	@Test
	public void testRefines_23() throws Exception {
		
		// Context
		final IContextRoot ctx = createContext("ctx");
		addConstants(ctx, "gp", "fvdd", "fvdn", "fvd", "ga",
				"glue", "ip", "fpwd", "gpwn", "fvnwd", "gvnwn",
				"hvdd", "hvnd", "hvrd", "ia");
		addAxioms(ctx,
				"gp", "gp ⊆ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ",
				"fvdd", "fvdd ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ → ℤ",
				"fvdn", "fvdn ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ → ℤ ",
				"fvd", "fvd ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ → ℤ ",
				"ga", "ga ⊆ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ",
				"glue", "glue ⊆ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ",
				"ip", "ip ⊆ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ",
				"fpwd", "fpwd ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ → ℤ",
				"gpwn", "gpwn ⊆ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ",
				"fvnwd", "fvnwd ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ → ℤ",
				"gvnwn", "gvnwn ⊆ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ",
				"hvdd", "hvdd ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ → ℤ",
				"hvnd", "hvnd ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ → ℤ",
				"hvrd", "hvrd ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ → ℤ",
				"ia", "ia ⊆ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ"
		);
		saveRodinFileOf(ctx);
		
		// Abstract machine
		final IMachineRoot abs = createMachine("abs");
		addMachineSees(abs, ctx.getElementName());

		final String[] absVars = makeSList("vdd", "vdn", "vd", "vnd", "vnn",
				"vnwd", "vnwn");
		final String absVarMaplet = makeMaplet(absVars);
		addVariables(abs, absVars);
		addInvariant(abs, "I", absVarMaplet + " ∈ ℤ×ℤ×ℤ×ℤ×ℤ×ℤ×ℤ", false);
		addInitialisation(abs, absVars);
		
		final String[] absPars = makeSList("p", "pwd", "pwn");
		final String absParMaplet = makeMaplet(absPars);
		final String absFullMaplet = makeMaplet(absParMaplet, absVarMaplet);
		final IEvent absEvt = addEvent(abs, "evt",
				makeSList(absPars),
				makeSList("G"),
				makeSList(absFullMaplet + " ∈ gp"),
				makeSList("vdd", "vdn", "vd", "act"),
				makeSList(
						"vdd ≔ fvdd(" + absFullMaplet + ")",
						"vdn ≔ fvdn(" + absFullMaplet + ")",
						"vd  ≔ fvd (" + absFullMaplet + ")",
						"vnd, vnn, vnwd, vnwn :∣ " + absFullMaplet + " ↦ vnd' ↦ vnn' ↦ vnwd' ↦ vnwn' ∈ ga"
				)
		);
		saveRodinFileOf(abs);
		
		// Concrete machine
		final IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, abs.getElementName());
		addMachineSees(ref, ctx.getElementName());

		final String[] refVars = makeSList("vdd", "vdn", "vnd", "vnn", "vrd",
				"vrn");
		final String[] refPostVars = makePrime(refVars);
		final String refVarMaplet = makeMaplet(refVars);
		final String bothVarMaplet = makeMaplet(refVarMaplet, "vd", "vnwd", "vnwn");
		addVariables(ref, refVars);
		addInvariant(ref, "J", bothVarMaplet + " ∈ glue", false);
		final IEvent refInit = addInitialisation(ref, refVars);
		addEventWitnesses(refInit, "vd'", "⊤", "vnwd'", "⊤", "vnwn'", "⊤");

		final String[] refPars = makeSList("p", "pr");
		final String refParMaplet = makeMaplet(refPars);
		final String refFullMaplet = makeMaplet(refParMaplet, refVarMaplet);
		final IEvent refEvt = addEvent(ref, "evt",
				makeSList(refPars),
				makeSList("H"),
				makeSList(refFullMaplet + " ∈ ip"),
				makeSList("vdd", "vnd", "vrd", "act"),
				makeSList(
						"vdd ≔ hvdd(" + refFullMaplet + ")",
						"vnd ≔ hvnd(" + refFullMaplet + ")",
						"vrd ≔ hvrd (" + refFullMaplet + ")",
						"vdn, vnn, vrn :∣ " + refFullMaplet + " ↦ vdn' ↦ vnn' ↦ vrn' ∈ ia"
				)
		);
		addEventRefines(refEvt, absEvt.getLabel());
		final String bothFullMaplet = makeMaplet(refParMaplet, bothVarMaplet);
		final String bothFullMapletPlusPost = makeMaplet(bothFullMaplet,
				makeMaplet(refPostVars));
		addEventWitnesses(refEvt, 
				"pwd", "pwd = fpwd(" + bothFullMaplet + ")",
				"pwn", "pwn ↦ " + bothFullMaplet + " ∈ gpwn",
				"vnwd'", "vnwd' = fvnwd(" + bothFullMapletPlusPost + ")", 
				"vnwn'", bothFullMapletPlusPost + " ↦ vnwn' ∈ gvnwn"
		);
		saveRodinFileOf(ref);
		
		/////////////
		runBuilder();
		/////////////

		// Populate type environment with axioms
		final ITypeEnvironmentBuilder typenv = factory.makeTypeEnvironment();
		for (IAxiom axiom: ctx.getAxioms()) {
			final String s = axiom.getPredicateString();
			final Predicate p = factory.parsePredicate(s, V2, null)
					.getParsedPredicate();
			final ITypeCheckResult tcResult = p.typeCheck(typenv);
			assertTrue(p.isTypeChecked());
			typenv.addAll(tcResult.getInferredEnvironment());
		}
		
		// Evaluate some post values
		final String refVarMaplet_p = makeMaplet(
				"hvdd(" + refFullMaplet + ")",
				"vdn'",
				"hvnd(" + refFullMaplet + ")",
				"vnn'",
				"hvrd (" + refFullMaplet + ")",
				"vrn'"
		);
		final String absParMaplet_i = makeMaplet(
				"p",
				"fpwd(" + bothFullMaplet + ")",
				"pwn"
		);
		final String absFullMaplet_i = makeMaplet(absParMaplet_i, absVarMaplet);
		final String bothFullMaplet_p = makeMaplet(refParMaplet, bothVarMaplet);
		final String bothFullMapletPlusPost_p = makeMaplet(bothFullMaplet_p,
				refVarMaplet_p);
		final String bothVarMaplet_p = makeMaplet(refVarMaplet_p,
				"fvd (" + absFullMaplet_i + ")",
				"fvnwd(" + bothFullMapletPlusPost_p + ")",
				"vnwn'"
		);
		
		// evt/J/INV
		final IPORoot po = ref.getPORoot();
		final IPOSequent inv = getSequent(po, "evt/J/INV");
		sequentHasGoal(inv, typenv, bothVarMaplet_p + " ∈ glue");

		// evt/act/SIM
		final IPOSequent sim = getSequent(po, "evt/act/SIM");
		final String lhs = makeMaplet(absFullMaplet_i,
				"hvnd(" + refFullMaplet + ")",
				"vnn'",
				"fvnwd(" + bothFullMapletPlusPost_p + ")",
				"vnwn'"
		);
		sequentHasGoal(sim, typenv, lhs + " ∈ ga");
	}

	/*
	 * PO filter: the POG should not generate guard strengthening POs if
	 * abstract guard is a theorem
	 */
	@Test
	public void testRefines_24() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2"), makeSList("x > x", "1 > 2"),
				makeBList(false, true),
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x = 1"), makeBList(false),
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt/GA1/GRD");
		sequentHasGoal(sequent, environment, "x > x");
		
		noSequent(po, "evt/GA2/GRD");
	}

	/*
	 * Guard strengthening in event merge PO with abstract theorems
	 */
	@Test
	public void testRefines_25() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList("GA", "TA"), makeSList("1<2", "2<3"), makeBList(false, true),
				makeSList(), makeSList());
		addEvent(abs, "fvt", 
				makeSList(), 
				makeSList("HA", "UA"), makeSList("3<4", "4<5"), makeBList(false, true),
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("GC", "TC"), makeSList("5<6", "6<7"), makeBList(false, true), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		saveRodinFileOf(ref);
		runBuilder();
		
		ITypeEnvironmentBuilder environment = factory.makeTypeEnvironment();
		
		IPORoot po = ref.getPORoot();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/MRG");
		sequentHasHypotheses(sequent, environment, "5<6", "6<7");
		sequentHasGoal(sequent, environment, "1<2 ∨ 3<4");
	}

	/*
	 * Guard strengthening in event merge PO with all abstract guards being
	 * theorems
	 */
	@Test
	public void testRefines_26() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList("TA"), makeSList("1<2"), makeBList(true), 
				makeSList(), makeSList());
		addEvent(abs, "fvt", 
				makeSList(), 
				makeSList("HA", "UA"), makeSList("3<4", "4<5"), makeBList(false, true),
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("GC", "TC"), makeSList("5<6", "6<7"), makeBList(false, true), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		saveRodinFileOf(ref);
		runBuilder();
		
		IPORoot po = ref.getPORoot();
		noSequent(po, "evt/MRG");
	}
	
	/*
	 * PO filter: the POG should not generate THM POs for guards when these
	 * conditions have already been proved for the abstract event
	 */
	@Test
	public void testRefines_27() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList("G","H"), makeSList("min(ℕ)∈ℕ","0 ≤ min({0})"), 
				makeBList(false,true), 
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("G","H","K"), makeSList("min(ℕ)∈ℕ","0 ≤ min({0})","2 > 1"), 
				makeBList(false,true,false),
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		IPORoot po = ref.getPORoot();
		getExactSequents(po);
	}
	
	/*
	 * PO filter: the POG should generate THM POs for guards when these
	 * conditions have not been proved for the abstract event
	 */
	@Test
	public void testRefines_28() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList("G","H","K"), makeSList("min(ℕ)∈ℕ","0 ≤ min({0})","2 > 1"), 
				makeBList(false,true,true), 
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("G","K"), makeSList("min(ℕ)∈ℕ","2 > 1"), 
				makeBList(false,true),
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		IPORoot po = ref.getPORoot();
		getExactSequents(po, "evt/K/THM");
	}
	
	/*
	 * PO filter: the POG should generate THM POs for guards when these
	 * conditions have not been proved for the abstract event
	 */
	@Test
	public void testRefines_29() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList("G","H"), makeSList("min(ℕ)∈ℕ","0 ≤ min({0})"), 
				makeBList(false,false), 
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("G","H","K"), makeSList("min(ℕ)∈ℕ","0 ≤ min({0})","2 > 1"), 
				makeBList(false,true,false),
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		saveRodinFileOf(ref);
		runBuilder();
		
		IPORoot po = ref.getPORoot();
		getExactSequents(po, "evt/H/THM");
	}

	/*
	 * Regression test for bug #3469348: Bug in INITIALISATION FIS PO. The after
	 * value witness must not be in hypothesis.
	 */
	@Test
	public void testBug3469348() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addVariables(abs, "v");
		addInvariant(abs, "I", "v = 1", false);
		addInitialisation(abs, makeSList("A1"), makeSList("v  :∣ v' = 1"));
		saveRodinFileOf(abs);

		final IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "w");
		addInvariant(ref, "J", "v = −(w∗w)", false);
		final IEvent init = addInitialisation(ref, makeSList("A2"),
				makeSList("w  :∣ w'∗w' = −1"));
		addEventRefines(init, INITIALISATION);
		addEventWitnesses(init, makeSList("v'"), makeSList("v' = −(w'∗w')"));
		saveRodinFileOf(ref);
		runBuilder();

		final ITypeEnvironmentBuilder typenv = mTypeEnvironment(
				"v'=ℤ", factory);

		final IPORoot po = ref.getPORoot();
		final IPOSequent sequent = getSequent(po, INITIALISATION + "/A2/FIS");
		// sequentHasIdentifiers(sequent, "v'");
		sequentHasExactlyHypotheses(sequent, typenv, "v' = 1");
		sequentHasGoal(sequent, typenv, "∃w'·w'∗w' = −1");
	}

	/*
	 * Regression test for bug #3488583: Duplicate PO for action WD. An event
	 * that extends its abstraction without contributing any addition shall not
	 * produce any WD PO.
	 */
	@Test
	public void testBug3488583() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariant(abs, "I", "x ∈ ℤ", true);
		addInitialisation(abs, makeSList("A1"), makeSList("x ≔ 1÷3"));
		addEvent(abs, "evt",//
				makeSList(),//
				makeSList(),//
				makeSList(),//
				makeSList("A1"), makeSList("x ≔ 1÷2"));
		saveRodinFileOf(abs);

		final IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "x");
		addExtendedEvent(ref, INITIALISATION);
		final IEvent refEvt = addExtendedEvent(ref, "fvt");
		addEventRefines(refEvt, "evt");
		saveRodinFileOf(ref);
		runBuilder();

		getExactSequents(abs.getPORoot(), "INITIALISATION/A1/WD", "evt/A1/WD");
		getExactSequents(ref.getPORoot());
	}

}
