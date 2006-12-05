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
	
	public void testEvents_00() throws Exception {
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
	
	public void testEvents_01() throws Exception {
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
	
	public void testEvents_02() throws Exception {
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
	
	public void testEvents_03() throws Exception {
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
	public void testEvents_04() throws Exception {
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
	public void testEvents_05() throws Exception {
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

}
