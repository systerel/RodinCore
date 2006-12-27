/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineRefines extends BasicPOTest {
	
	/**
	 * rewriting of deterministic action simulation POs
	 */
	public void testRefines_00() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
		addEventRefines(event, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/A1/SIM");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+2=V1+1");
		
	}
	
	/**
	 * rewriting of action frame simulation POs
	 */
	public void testRefines_01() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
		addEventRefines(event, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/V1/EQL");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+2=V1");
		
	}
	
	/**
	 * simulation and invariant preservation using global witnesses
	 */
	public void testRefines_02() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V2∈0‥5", "V2≥V1"));
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V2≔V2+2"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("V1'"), makeSList("V1'≥V2'"));
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1");
		sequentHasGoal(sequent, typeEnvironment, "V2+2∈0‥5");

		sequent = getSequent(po, "evt/I2/INV");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1", "V1'≥V2'");
		sequentHasGoal(sequent, typeEnvironment, "V2+2≥V1'");
		
		sequent = getSequent(po, "evt/A1/SIM");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1", "V1'≥V2'");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈ℕ");
		
	}
	
	/**
	 * simulation and invariant preservation using local witnesses
	 */
	public void testRefines_03() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1", "V2");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("V1∈0‥4", "V2≥6"));
		addEvent(abs, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ∖{0}"), 
				makeSList("A1", "A2"), makeSList("V1≔L1", "V2≔7"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1X", "V2");
		addInvariants(mac, makeSList("I3"), makeSList("V1X=V1+1"));
		IEvent event = addEvent(mac, "evt", 
				makeSList("L2"), 
				makeSList("L2"), makeSList("L2∈ℕ"), 
				makeSList("A1"), makeSList("V1X≔L2"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("L1"), makeSList("L1=L2−1"));
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V2", intType);
		typeEnvironment.addName("V1X", intType);
		typeEnvironment.addName("L1", intType);
		typeEnvironment.addName("L2", intType);
		
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
	public void testRefines_04() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("G"), makeSList("0 ≤ min({0})"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		ref.save(null, true);
		runBuilder();
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po);
		getSequent(po, "evt/G/WD");
	}

	/*
	 * PO filter: the POG should not generate WD POs for guards when these
	 * conditions have already been proved for the abstract event
	 */
	public void testRefines_05() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3"), makeSList("x ÷ x > x", "1 ÷ x > 1", "2÷x = x"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3", "G4"), makeSList("5 ÷ (x+x) = −1", "1 ÷ x > 1", "x ÷ x > x", "2÷x = x"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
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
	 * PO filter: the POG should not generate guard strengthening POs if
	 * all abstract guards are syntactically (but normalised) contained
	 * in the concrete guards
	 */
	public void testRefines_06() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2", "GA3"), makeSList("x > x", "1 > 1", "x−1∈ℕ"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3"), makeSList("1 > 1", "x > x", "x = x"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		noSequent(po, "evt/GA1/REF");
		
		noSequent(po, "evt/GA2/REF");

		sequent= getSequent(po, "evt/GA3/REF");
		sequentHasGoal(sequent, environment, "x−1∈ℕ");
	}

	/*
	 * PO filter: inherited events should only produce invariant preservation POs
	 */
	public void testRefines_07() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A");
		addInvariants(abs, makeSList("I"), makeSList("A∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2"), makeSList("1 > x", "x−1∈ℕ"), 
				makeSList("S"), makeSList("A≔A+1"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addInvariants(ref, makeSList("J"), makeSList("A=B"));
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "A", "B");
		
		IPOSequent sequent;
		
		noSequent(po, "evt/G1/REF");
		noSequent(po, "evt/G2/REF");
		noSequent(po, "evt/S/SIM");
		
		sequent= getSequent(po, "evt/J/INV");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "1 > x", "x−1∈ℕ");
		sequentHasGoal(sequent, environment, "A+1=B");
	}

	/*
	 * PO filter: do not produce WD and FIS POs for repeated actions
	 */
	public void testRefines_08() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x÷x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B", "C");
		addInvariants(ref, makeSList("J"), makeSList("C=B"));
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList("SC1", "SC2", "SC3"), makeSList("A :∣ A'>x", "B ≔ x÷x", "C :∈ {1÷x}"));
		addEventRefines(event, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("C", intType);
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "A", "B", "C");
		
		IPOSequent sequent;
		
		noSequent(po, "evt/SA1/SIM");
		noSequent(po, "evt/SA2/SIM");
		noSequent(po, "evt/SC1/FIS");
		noSequent(po, "evt/SC2/WD");
		
		sequent= getSequent(po, "evt/GA/REF");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x−1∈ℕ");
		
		sequent= getSequent(po, "evt/SC3/WD");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x≠0");
		
		sequent= getSequent(po, "evt/SC3/FIS");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "{1÷x}≠∅");
	}	
	
	/*
	 * create event merge POs (simple)
	 */
	public void testRefines_09() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList(), makeSList());
		addEvent(abs, "fvt", 
				makeSList("x"), 
				makeSList("HA"), makeSList("x+1∈ℕ"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/MRG");
		sequentHasHypotheses(sequent, environment, "x>0");
		sequentHasGoal(sequent, environment, "x−1∈ℕ ∨ x+1∈ℕ");
	}

	/*
	 * create event merge POs (complicated)
	 */
	public void testRefines_10() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "fvt", 
				makeSList("x", "y"), 
				makeSList("HA1", "HA2"), makeSList("x+1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B", "C");
		addInvariants(ref, makeSList("J"), makeSList("C=B"));
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList("SC1", "SC2", "SC3"), makeSList("A :∣ A'>x", "B ≔ x+1", "C :∈ {x+1}"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("C", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);
		
		IPOFile po = ref.getPOFile();
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
	public void testRefines_11() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
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
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x", "y"), 
				makeSList("GC"), makeSList("x=y+y"), 
				makeSList("SC1"), makeSList("A :∣ A'>x"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		addEventRefines(event, "gvt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("C", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);
		
		IPOFile po = ref.getPOFile();
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
	 * filter event merge POs entirely if one of the dijuncts is true
	 */
	public void testRefines_12() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2"), makeSList("x−1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "fvt", 
				makeSList("x", "y"), 
				makeSList("HA1", "HA2"), makeSList("x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x", "y"), 
				makeSList("GC"), makeSList("x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		addEventRefines(event, "gvt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("C", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "A", "B");
		
		noSequent(po, "evt/MRG");
		
		noSequent(po, "evt/SA1/SIM");
		noSequent(po, "evt/SA2/SIM");
	}

	/*
	 * PO filter: do not produce any POs in identical refinements
	 */
	public void testRefines_13() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA", "HA"), makeSList("x>0", "B÷x>0"), 
				makeSList("SA", "TA"), makeSList("A :∣ A'>x", "B ≔ x÷x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		IPOFile apo = abs.getPOFile();
		containsIdentifiers(apo, "A", "B");
		
		getSequent(apo, "evt/HA/WD");
		getSequent(apo, "evt/SA/FIS");
		getSequent(apo, "evt/TA/WD");
		getSequent(apo, "evt/I1/INV");
		getSequent(apo, "evt/I2/INV");
		
		IPOFile cpo = ref.getPOFile();
		containsIdentifiers(cpo, "A", "B");
		
		// no sequents!
		
		getSequents(cpo);
	}
	
	/*
	 * Generate PO for event action that modifies abstract preserved variable;
	 * two cases
	 * 	(1) the event is new
	 * 	(2) the abstract event is empty
	 * (POs should be identical!)
	 */
	public void testRefines_14() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"));
		addEvent(abs, "fvt");

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
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
	
		ref.save(null, true);
		
		runBuilder();
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "p");
		
		getSequent(po, "evt/p/EQL");
		getSequent(po, "fvt/p/EQL");
		
	}

}
