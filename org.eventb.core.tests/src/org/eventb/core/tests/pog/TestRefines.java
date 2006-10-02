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
public class TestRefines extends BasicTest {
	
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
		
		runSC(abs);
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
		addEventRefines(event, "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
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
		
		runSC(abs);
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
		addEventRefines(event, "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
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
		
		runSC(abs);
		
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
		
		runSC(mac);
		
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
		
		runSC(abs);
		
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
		
		runSC(mac);
		
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

//		sequent = getSequent(po, "evt/I2/INV");
//		
//		sequentHasIdentifiers(sequent, "V1'", "V2'");
//		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1", "V1'≥V2'");
//		sequentHasGoal(sequent, typeEnvironment, "V2+2≥V1'");
//		
//		sequent = getSequent(po, "evt/A1/SIM");
//		
//		sequentHasIdentifiers(sequent, "V1'", "V2'");
//		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1", "V1'≥V2'");
//		sequentHasGoal(sequent, typeEnvironment, "V1'∈ℕ");
		
	}

}
