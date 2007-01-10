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
public class TestMachineHints extends GenericHintTest<IMachineFile> {
	
	/**
	 * guard well-definedness hints
	 */
	public void testMachineHints_00() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I"), makeSList("x∈0‥4"));
		addEvent(mac, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x÷x", "x÷x=0"), 
				makeSList(), makeSList());
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "x");
		
		IPOSequent sequent = getSequent(po, "evt/G/WD");
		
		sequentHasIdentifiers(sequent, "y");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
		
		sequent = getSequent(po, "evt/H/WD");
		
		sequentHasIdentifiers(sequent, "y");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x÷x");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
	}
		
	/**
	 * action well-definedness and feasilibility hints
	 */
	public void testMachineHints_01() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "x", "z");
		addInvariants(mac, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL"));
		addEvent(mac, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList("S", "T"), makeSList("x≔x÷y", "z:∣z'≠z"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);
		typeEnvironment.addName("z", boolType);
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "x", "z");
		
		IPOSequent sequent = getSequent(po, "evt/S/WD");
		
		sequentHasIdentifiers(sequent, "y", "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x", "x=0");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4", "z∈BOOL");
		
		sequent = getSequent(po, "evt/T/FIS");
		
		sequentHasIdentifiers(sequent, "y", "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x", "x=0");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4", "z∈BOOL");
	}
	
	/**
	 * invariant preservation hints
	 */
	public void testMachineHints_02() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, "x", "z");
		addInvariants(mac, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL∖{TRUE}"));
		addEvent(mac, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList("S", "T"), makeSList("x≔y", "z:∣z'≠z"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);
		typeEnvironment.addName("z", boolType);
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "x", "z");
		
		IPOSequent sequent = getSequent(po, "evt/I/INV");
		
		sequentHasIdentifiers(sequent, "y", "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x", "x=0", "x∈0‥4");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "z∈BOOL∖{TRUE}");
		
		sequent = getSequent(po, "evt/J/INV");
		
		sequentHasIdentifiers(sequent, "y", "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x", "x=0", "z∈BOOL∖{TRUE}");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
	}
	
	/**
	 * guard strengthening hints in refinement
	 */
	public void testMachineHints_03() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "x", "z");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL∖{TRUE}"));
		addEvent(abs, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList(), makeSList());
		
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "x", "z");
		IEvent event = addEvent(mac, "evt", 
				makeSList("yc"), 
				makeSList("GC", "HC"), makeSList("yc>1", "x=1"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("y"), makeSList("yc=y"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);
		typeEnvironment.addName("z", boolType);
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "x", "z");
		
		IPOSequent sequent = getSequent(po, "evt/G/GRD");
		
		sequentHasIdentifiers(sequent, "y", "yc");
		sequentHasSelectionHints(sequent, typeEnvironment, "yc>1", "x=1", "yc=y");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4", "z∈BOOL∖{TRUE}");
		
		sequent = getSequent(po, "evt/H/GRD");
		
		sequentHasIdentifiers(sequent, "y", "yc");
		sequentHasSelectionHints(sequent, typeEnvironment, "yc>1", "x=1");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4", "z∈BOOL∖{TRUE}", "yc=y");
	}
	
	/**
	 * simulation hints in refinement
	 */
	public void testMachineHints_04() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "x", "z");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL∖{TRUE}"));
		addEvent(abs, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList("S", "T"), makeSList("x≔y", "z:∣z'≠z"));
		
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");

		addVariables(mac, "x", "zc");
		addInvariants(mac, makeSList("JC"), makeSList("zc∈BOOL∖{TRUE}"));
		IEvent event = addEvent(mac, "evt", 
				makeSList("yc"), 
				makeSList("GC", "HC"), makeSList("yc>1", "x=1"), 
				makeSList("SC", "TC"), makeSList("x≔1", "zc≔TRUE"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("y", "z'"), makeSList("yc=y", "z'≠zc'"));
	
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);
		typeEnvironment.addName("yc", intType);
		typeEnvironment.addName("z", boolType);
		typeEnvironment.addName("zc", boolType);
		typeEnvironment.addName("z'", boolType);
		typeEnvironment.addName("zc'", boolType);
	
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "x", "z", "zc");
		
		IPOSequent sequent = getSequent(po, "evt/S/SIM");
		
		sequentHasIdentifiers(sequent, "y", "yc", "x'", "z'", "zc'");
		sequentHasSelectionHints(sequent, typeEnvironment, 
				"yc>1", "x=1", "yc=y");
		sequentHasNotSelectionHints(sequent, typeEnvironment, 
				"x∈0‥4", "z∈BOOL∖{TRUE}", "zc∈BOOL∖{TRUE}", "z'≠zc'");
		
		sequent = getSequent(po, "evt/T/SIM");
		
		sequentHasIdentifiers(sequent, "y", "yc", "x'", "z'", "zc'");
		sequentHasSelectionHints(sequent, typeEnvironment, 
				"yc>1", "x=1", "z'≠zc'");
		sequentHasNotSelectionHints(sequent, typeEnvironment, 
				"x∈0‥4", "z∈BOOL∖{TRUE}", "zc∈BOOL∖{TRUE}", "yc=y");
	}
	
	/**
	 * invariant preservation hints in refinement
	 */
	public void testMachineHints_05() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "x", "z");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL∖{TRUE}"));
		addEvent(abs, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList("S", "T"), makeSList("x≔y", "z:∣z'≠z"));
		
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");

		addVariables(mac, "x", "zc");
		addInvariants(mac, makeSList("JC", "JD"), makeSList("zc∈BOOL∖{TRUE}", "z≠zc"));
		IEvent event = addEvent(mac, "evt", 
				makeSList("yc"), 
				makeSList("GC", "HC"), makeSList("yc>1", "x=1"), 
				makeSList("SC", "TC"), makeSList("x≔1", "zc :∣ TRUE≠zc'"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("y", "z'"), makeSList("yc=y", "z'≠zc'"));

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);
		typeEnvironment.addName("yc", intType);
		typeEnvironment.addName("z", boolType);
		typeEnvironment.addName("zc", boolType);
		typeEnvironment.addName("z'", boolType);
		typeEnvironment.addName("zc'", boolType);
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "x", "z", "zc");
		
		IPOSequent sequent = getSequent(po, "evt/JC/INV");
		
		sequentHasIdentifiers(sequent, "y", "yc", "x'", "z'", "zc'");
		sequentHasSelectionHints(sequent, typeEnvironment, 
				"yc>1", "x=1", "TRUE≠zc'", "zc∈BOOL∖{TRUE}");
		sequentHasNotSelectionHints(sequent, typeEnvironment, 
				"x∈0‥4", "z∈BOOL∖{TRUE}", "yc=y", "z≠zc");
		
		sequent = getSequent(po, "evt/JD/INV");
		
		sequentHasIdentifiers(sequent, "y", "yc", "x'", "z'", "zc'");
		sequentHasSelectionHints(sequent, typeEnvironment, 
				"yc>1", "x=1", "z'≠zc'", "TRUE≠zc'", "z≠zc");
		sequentHasNotSelectionHints(sequent, typeEnvironment, 
				"x∈0‥4", "z∈BOOL∖{TRUE}", "zc∈BOOL∖{TRUE}", "yc=y");
	}

}
