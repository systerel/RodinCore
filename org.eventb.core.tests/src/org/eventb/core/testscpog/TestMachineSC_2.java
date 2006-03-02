/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.ISCAxiomSet;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachine;
import org.eventb.core.ISCTheoremSet;
import org.eventb.core.ISCVariable;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class TestMachineSC_2 extends BuilderTest {

	/**
	 * Test method for name clashes of variables
	 */
	public void testVariables1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2", "V2", "V3"));
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList("V1∈ℕ", "V2∈ℕ", "V3∈ℕ"));
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCVariable[] variables = scMachine.getSCVariables();
		
		assertTrue("2 variables", variables.length == 2);
		
		Set<String> set = new HashSet<String>(5);
		set.add(variables[0].getElementName());
		set.add(variables[1].getElementName());
		
		assertTrue("names correspond", set.contains("V1") && set.contains("V3"));
	}
	
	/**
	 * Test method for typing of variables
	 */
	public void testVariables2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2", "V3"));
		addInvariants(rodinFile, makeList("I1", "I3"), makeList("V1∈ℕ", "V3∈ℕ"));
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCVariable[] variables = scMachine.getSCVariables();
		
		assertTrue("2 variables", variables.length == 2);
		
		Set<String> set = new HashSet<String>(5);
		set.add(variables[0].getElementName());
		set.add(variables[1].getElementName());
		
		assertTrue("names correspond", set.contains("V1") && set.contains("V3"));
	}
	
	private ISCContext makeContext1() throws RodinDBException {
		ISCContext context = (ISCContext) rodinProject.createRodinFile("ctx.bcc", true, null);
		addSCCarrierSets(context, makeList("S1"), makeList("ℙ(S1)"));
		addSCConstants(context, makeList("C1"), makeList("S1"));
		context.save(null, true);
		return context;
	}
	
	/**
	 * Test method for name clashes of variables with carrier sets in seen context
	 */
	public void testVariables3() throws Exception {
		makeContext1();
		IMachine rodinFile = createMachine("one");
		addSees(rodinFile, "ctx");
		addVariables(rodinFile, makeList("S1", "V2", "V3"));
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList("S1∈ℕ", "V2∈ℕ", "V3∈ℕ"));
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IVariable[] variables = scMachine.getSCVariables();
		
		assertTrue("2 variables", variables.length == 2);
		
		Set<String> set = new HashSet<String>(5);
		set.add(variables[0].getElementName());
		set.add(variables[1].getElementName());
		
		assertTrue("names correspond", set.contains("V2") && set.contains("V3"));
	}
	
	/**
	 * Test method for name clashes of variables with constants in seen context
	 */
	public void testVariables4() throws Exception {
		makeContext1();
		IMachine rodinFile = createMachine("one");
		addSees(rodinFile, "ctx");
		addVariables(rodinFile, makeList("C1", "V2", "V3"));
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList("C1∈ℕ", "V2∈ℕ", "V3∈ℕ"));
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IVariable[] variables = scMachine.getSCVariables();
		
		assertTrue("2 variables", variables.length == 2);
		
		Set<String> set = new HashSet<String>(5);
		set.add(variables[0].getElementName());
		set.add(variables[1].getElementName());
		
		assertTrue("names correspond", set.contains("V2") && set.contains("V3"));
	}
	
	/**
	 * Test method for non-name clashes of variables and carrier sets and constants in seen context
	 */
	public void testVariables5() throws Exception {
		makeContext1();
		IMachine rodinFile = createMachine("one");
		addSees(rodinFile, "ctx");
		addVariables(rodinFile, makeList("V1", "V2", "V3"));
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList("V1∈ℕ", "V2∈ℕ", "V3∈ℕ"));
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IVariable[] variables = scMachine.getSCVariables();
		
		assertTrue("3 variables", variables.length == 3);
		
		Set<String> set = new HashSet<String>(5);
		set.add(variables[0].getElementName());
		set.add(variables[1].getElementName());
		set.add(variables[2].getElementName());
		
		assertTrue("names correspond", set.contains("V1") && set.contains("V2") && set.contains("V3"));
		
		set.clear();
		
		ISCCarrierSet[] carrierSets = scMachine.getSCCarrierSets();
		
		assertTrue("1 carrier set", carrierSets.length == 1 && carrierSets[0].getElementName().equals("S1"));
		
		ISCConstant[] constants = scMachine.getSCConstants();
		
		assertTrue("1 constant", constants.length == 1 && constants[0].getElementName().equals("C1"));
	}
	
	/**
	 * Test method for non-name names of variables
	 */
	public void testVariables6() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList(""));
		addInvariants(rodinFile, makeList("I1"), makeList("⊤"));
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCVariable[] variables = scMachine.getSCVariables();
		
		assertTrue("variables", variables.length == 0);
	}
	
	/**
	 * Test method for non-name clashes of invariants and (machine) theorems
	 */
	public void testInvariants1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList("⊤", "⊤", "⊤"));
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IInvariant[] invariants = scMachine.getInvariants();
		
		assertTrue("3 invariants", invariants.length == 3);
		
		Set<String> set = new HashSet<String>(5);
		set.add(invariants[0].getElementName());
		set.add(invariants[1].getElementName());
		set.add(invariants[2].getElementName());
		
		assertTrue("names correspond", set.contains("I1") && set.contains("I2") && set.contains("I3"));
	}
	
	/**
	 * Test method for name clashes of invariants and (machine) theorems
	 */
	public void testInvariants2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addInvariants(rodinFile, makeList("I1", "I2", "I2"), makeList("⊤", "⊤", "⊤"));
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IInvariant[] invariants = scMachine.getInvariants();
		
		assertTrue("1 invariant", invariants.length == 1 && invariants[0].getElementName().equals("I1"));
	}

	/**
	 * Test method for non-name clashes of invariants and (machine) theorems
	 */
	public void testTheorems1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addTheorems(rodinFile, makeList("T1", "T2", "T3"), makeList("⊤", "⊤", "⊤"), null);
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ITheorem[] theorems = scMachine.getTheorems();
		
		assertTrue("3 theorems", theorems.length == 3);
		
		Set<String> set = new HashSet<String>(5);
		set.add(theorems[0].getElementName());
		set.add(theorems[1].getElementName());
		set.add(theorems[2].getElementName());
		
		assertTrue("names correspond", set.contains("T1") && set.contains("T2") && set.contains("T3"));
	}
	
	/**
	 * Test method for name clashes of invariants and (machine) theorems
	 */
	public void testTheorems2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addTheorems(rodinFile, makeList("T1", "T2", "T2"), makeList("⊤", "⊤", "⊤"), null);
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ITheorem[] theorems = scMachine.getTheorems();
		
		assertTrue("1 theorem", theorems.length == 1 && theorems[0].getElementName().equals("T1"));
	}
	
	/**
	 * Test method for name clashes of invariants and (machine) theorems
	 */
	public void testInvariantsAndTheorems1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addInvariants(rodinFile, makeList("I1", "I2", "T3"), makeList("⊤", "⊤", "⊤"));
		addTheorems(rodinFile, makeList("T1", "T2", "T3"), makeList("⊤", "⊤", "⊤"), null);
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ITheorem[] theorems = scMachine.getTheorems();
		
		assertTrue("2 theorems", theorems.length == 2);
		
		Set<String> set = new HashSet<String>(5);
		set.add(theorems[0].getElementName());
		set.add(theorems[1].getElementName());
		
		assertTrue("theorem names correspond", set.contains("T1") && set.contains("T2"));
		
		IInvariant[] invariants = scMachine.getInvariants();
		
		assertTrue("2 invariants", invariants.length == 2);
		
		set.clear();
		set.add(invariants[0].getElementName());
		set.add(invariants[1].getElementName());
		
		assertTrue("invariant names correspond", set.contains("I1") && set.contains("I2"));
	}
		
	/**
	 * Test method for non-name clashes of events
	 */
	public void testEvents1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", makeList(), makeList(), makeList(), makeList());
		addEvent(rodinFile, "E2", makeList(), makeList(), makeList(), makeList());
		addEvent(rodinFile, "E3", makeList(), makeList(), makeList(), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("3 events", events.length == 3);
		
		Set<String> set = new HashSet<String>(5);
		set.add(events[0].getElementName());
		set.add(events[1].getElementName());
		set.add(events[2].getElementName());
		
		assertTrue("names correspond", set.contains("E1") && set.contains("E2") && set.contains("E3"));
	}
	
	/**
	 * Test method for name clashes of events
	 */
	public void testEvents2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", makeList(), makeList(), makeList(), makeList());
		addEvent(rodinFile, "E2", makeList(), makeList(), makeList(), makeList());
		addEvent(rodinFile, "E2", makeList(), makeList(), makeList(), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("1 event", events.length == 1 && events[0].getElementName().equals("E1"));
	}
	
	/**
	 * Test method for name clashes of invariants and (machine) theorems and events
	 */
	public void testInvariantsAndTheoremsAndEvents1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addInvariants(rodinFile, makeList("I1", "I2", "T3"), makeList("⊤", "⊤", "⊤"));
		addTheorems(rodinFile, makeList("T1", "T2", "T3"), makeList("⊤", "⊤", "⊤"), null);
		addEvent(rodinFile, "I1", makeList(), makeList(), makeList(), makeList());
		addEvent(rodinFile, "T2", makeList(), makeList(), makeList(), makeList());
		addEvent(rodinFile, "E3", makeList(), makeList(), makeList(), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ITheorem[] theorems = scMachine.getTheorems();
		
		assertTrue("1 theorem", theorems.length == 1 && theorems[0].getElementName().equals("T1"));
				
		IInvariant[] invariants = scMachine.getInvariants();
		
		assertTrue("1 invariant", invariants.length == 1 && invariants[0].getElementName().equals("I2"));
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("1 event", events.length == 1 && events[0].getElementName().equals("E3"));
	}
	
	/**
	 * Test method for non-name clashes of local variables
	 */
	public void testLocalVariables1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", 
				makeList("L1", "L2", "L3", "L4"), 
				makeList("G1"), makeList("L1∈ℕ∧L2∈ℕ∧L3∈ℕ∧L4∈ℕ"), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCEvent[] events = scMachine.getSCEvents();
		
		assertTrue("1 event", events.length == 1);
		
		ISCVariable[] localVariables = events[0].getSCVariables();
		
		assertTrue("4 local variables", localVariables.length == 4);
		
		Set<String> set = new HashSet<String>(5);
		set.add(localVariables[0].getElementName());
		set.add(localVariables[1].getElementName());
		set.add(localVariables[2].getElementName());
		set.add(localVariables[3].getElementName());
		
		assertTrue("names correspond", set.contains("L1") && set.contains("L2") && set.contains("L3") && set.contains("L4"));
	}
	
	/**
	 * Test method for name clashes of local variables
	 */
	public void testLocalVariables2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", 
				makeList("L1", "L2", "L3", "L3"), 
				makeList("G1"), makeList("L1∈ℕ∧L2∈ℕ"), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCEvent[] events = scMachine.getSCEvents();
		
		assertTrue("1 event", events.length == 1);
		
		ISCVariable[] localVariables = events[0].getSCVariables();
		
		assertTrue("2 local variables", localVariables.length == 2);
		
		Set<String> set = new HashSet<String>(5);
		set.add(localVariables[0].getElementName());
		set.add(localVariables[1].getElementName());
		
		assertTrue("names correspond", set.contains("L1") && set.contains("L2"));
	}
	
	/**
	 * Test method for name clashes of local variables and variables
	 */
	public void testLocalVariables3() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "E1", 
				makeList("L1", "V2", "L3"), 
				makeList("G1", "G2", "G3"), makeList("L1∈ℕ", "V2∈ℕ", "L3∈ℕ"), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCVariable[] variables = scMachine.getSCVariables();
		
		assertTrue("2 variables", variables.length == 2);
		
		Set<String> set = new HashSet<String>(5);
		set.add(variables[0].getElementName());
		set.add(variables[1].getElementName());
		
		assertTrue("names correspond", set.contains("V1") && set.contains("V2"));
		
		ISCEvent[] events = scMachine.getSCEvents();
		
		assertTrue("1 event", events.length == 1);
		
		ISCVariable[] localVariables = events[0].getSCVariables();
		
		assertTrue("2 local variables", localVariables.length == 2);
		
		set.clear();
		set.add(localVariables[0].getElementName());
		set.add(localVariables[1].getElementName());
		
		assertTrue("names correspond", set.contains("L1") && set.contains("L3"));
	}
	
	/**
	 * Test method for name clashes of local variables and variables and carrier sets and constants
	 */
	public void testLocalVariables4() throws Exception {
		makeContext1();
		IMachine rodinFile = createMachine("one");
		addSees(rodinFile, "ctx");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "E1", 
				makeList("L1", "V2", "L3", "S1", "C1"), 
				makeList("G1", "G2", "G3", "G4", "G5"), makeList("L1∈ℕ", "V2∈ℕ", "L3∈ℕ", "S1∈ℕ", "C1∈ℕ"), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCCarrierSet[] carrierSets = scMachine.getSCCarrierSets();
		
		assertTrue("1 carrier set", carrierSets.length == 1 && carrierSets[0].getElementName().equals("S1"));
		
		ISCConstant[] constants = scMachine.getSCConstants();
		
		assertTrue("1 carrier set", constants.length == 1 && constants[0].getElementName().equals("C1"));
		
		ISCVariable[] variables = scMachine.getSCVariables();
		
		assertEquals("2 variables", 2, variables.length);
		
		Set<String> set = new HashSet<String>(5);
		set.add(variables[0].getElementName());
		set.add(variables[1].getElementName());
		
		assertTrue("names correspond", set.contains("V1") && set.contains("V2"));
		
		ISCEvent[] events = scMachine.getSCEvents();
		
		assertEquals("1 event", 1, events.length);
		
		ISCVariable[] localVariables = events[0].getSCVariables();
		
		assertEquals("2 local variables", 2, localVariables.length);
		
		set.clear();
		set.add(localVariables[0].getElementName());
		set.add(localVariables[1].getElementName());
		
		assertTrue("names correspond", set.contains("L1") && set.contains("L3"));
	}
	
	/**
	 * Test method for non-empty names of local variables
	 */
	public void testLocalVariables5() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", 
				makeList(""), 
				makeList("G1"), makeList("⊤"), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCEvent[] events = scMachine.getSCEvents();
		
		assertTrue("1 event", events.length == 1);
		
		ISCVariable[] localVariables = events[0].getSCVariables();
		
		assertTrue("local variables", localVariables.length == 0);
		
	}
	
	/**
	 * Test method for non-name clashes of guards
	 */
	public void testGuards1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", 
				makeList("L1", "L2", "L3", "L4"), 
				makeList("G1", "G2", "G3", "G4"), makeList("L1∈ℕ", "L2∈ℕ", "L3∈ℕ", "L4∈ℕ"), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("1 event", events.length == 1);
		
		IGuard[] guards = events[0].getGuards();
		
		assertTrue("4 guards", guards.length == 4);
		
		Set<String> set = new HashSet<String>(5);
		set.add(guards[0].getElementName());
		set.add(guards[1].getElementName());
		set.add(guards[2].getElementName());
		set.add(guards[3].getElementName());
		
		assertTrue("names correspond", set.contains("G1") && set.contains("G2") && set.contains("G3") && set.contains("G4"));

	}
	
	/**
	 * Test method for name clashes of guards
	 */
	public void testGuards2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", 
				makeList("L1", "L2", "L3", "L4"), 
				makeList("G1", "G2", "G2", "G4"), makeList("L1∈ℕ", "L2∈ℕ", "L3∈ℕ", "L4∈ℕ"), makeList());
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("1 event", events.length == 1);
		
		IGuard[] guards = events[0].getGuards();
		
		assertTrue("2 guards", guards.length == 2);
		
		Set<String> set = new HashSet<String>(5);
		set.add(guards[0].getElementName());
		set.add(guards[1].getElementName());
		
		assertTrue("names correspond", set.contains("G1") && set.contains("G4"));

	}
	
	private ISCContext makeContext2() throws RodinDBException {
		ISCContext context = makeContext1();
		addAxioms(context, makeList("A1", "A2"), makeList("∀x·x>0", "⊤"), null);
		addTheorems(context, makeList("T1", "T2"), makeList("⊤", "⊤"), null);
		context.save(null, true);
		return context;
	}
	
	private Set<String> setof(IRodinElement[] elements) {
		HashSet<String> set = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(IRodinElement element : elements) {
			set.add(element.getElementName());
		}
		return set;
	}
	
	/**
	 * Test method for name clashes of guards
	 */
	public void testContextAxiomsAndTheorems1() throws Exception {
		makeContext2();
		IMachine rodinFile = createMachine("one");
		addSees(rodinFile, "ctx");
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("⊤", "⊤"));
		addTheorems(rodinFile, makeList("T1", "T2"), makeList("⊤", "⊤"), null);
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		Set<String> set;
		
		set = setof(scMachine.getInvariants());
		
		assertTrue("2 invariants", set.size() == 2 & set.contains("I1") && set.contains("I2"));
		
		set = setof(scMachine.getTheorems());
		
		assertTrue("2 theorems", set.size() == 2 & set.contains("T1") && set.contains("T2"));
		
		ISCAxiomSet[] axiomSets = scMachine.getAxiomSets();
		
		assertTrue("1 axiom set", axiomSets.length == 1);
		
		set = setof(axiomSets[0].getAxioms());
		
		assertTrue("2 axioms", set.size() == 2 & set.contains("A1") && set.contains("A2"));
		
		ISCTheoremSet[] theoremSets = scMachine.getTheoremSets();
		
		assertTrue("1 theorem set", theoremSets.length == 1);
		
		set = setof(theoremSets[0].getTheorems());
		
		assertTrue("2 theorems", set.size() == 2 & set.contains("T1") && set.contains("T2"));
	}

	/**
	 * Test method for typing of variables
	 */
	public void testTyping1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2=ℕ+1"));
		
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
	
		assertTrue("one invariant", scMachine.getInvariants().length == 1 && scMachine.getInvariants()[0].getElementName().equals("I1"));
		assertTrue("one variable", scMachine.getSCVariables().length == 1 && scMachine.getSCVariables()[0].getElementName().equals("V1"));
	}

	/**
	 * Test method for typing of local variables
	 */
	public void testTyping2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", 
				makeList("L1", "L2"), 
				makeList("G1", "G2"), makeList("L1∈ℕ", "L2=ℕ+1"), makeList());
	
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCEvent[] events = scMachine.getSCEvents();
		
		assertTrue("one event", events.length == 1);
	
		assertTrue("one guard", events[0].getGuards().length == 1 && events[0].getGuards()[0].getElementName().equals("G1"));
		assertTrue("one local variable", events[0].getSCVariables().length == 1 && events[0].getSCVariables()[0].getElementName().equals("L1"));
	}
	
	/**
	 * Test method for typing of local variables across events
	 */
	public void testTyping3() throws Exception {
		IMachine rodinFile = createMachine("one");
		addEvent(rodinFile, "E1", 
				makeList("L1", "L2"), 
				makeList("G1", "G2"), makeList("L1∈ℕ", "L2=1"), makeList());
		addEvent(rodinFile, "E2", 
				makeList("L1", "L2"), 
				makeList("G1", "G2"), makeList("L1∈BOOL", "L2=L1+1"), makeList());

		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCEvent[] events = scMachine.getSCEvents();
		
		assertTrue("two events", events.length == 2);
		
		ISCEvent e0 = (events[0].getElementName().equals("E1")) ? events[0] : events[1];
		ISCEvent e1 = (events[0].getElementName().equals("E2")) ? events[0] : events[1];
	
		assertTrue("two guards", e0.getGuards().length == 2);
		assertTrue("two local variables", e0.getSCVariables().length == 2);
		
		assertTrue("one guard", e1.getGuards().length == 1 && e1.getGuards()[0].getElementName().equals("G1"));
		assertTrue("one local variable", e1.getSCVariables().length == 1 && e1.getSCVariables()[0].getElementName().equals("L1"));
	}
	
	/**
	 * Test method for free identifiers of actions
	 */
	public void testActions1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "E1", 
				makeList(), 
				makeList(), makeList(), 
				makeList("V1≔V2", "V1:∈ℕ", "V1:∣V1'=V1+1"));
		
		String a0 = FormulaFactory.getDefault().parseAssignment("V1≔V2").getParsedAssignment().toString();
		String a1 = FormulaFactory.getDefault().parseAssignment("V1:∈ℕ").getParsedAssignment().toString();
		String a2 = FormulaFactory.getDefault().parseAssignment("V1:∣V1'=V1+1").getParsedAssignment().toString();
	
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("one event", events.length == 1);
		
		IAction[] actions = events[0].getActions();

		assertTrue("three actions", actions.length == 3);
		
		assertEquals("action 0", a0, actions[0].getContents());
		assertEquals("action 1", a1, actions[1].getContents());
		assertEquals("action 2", a2, actions[2].getContents());
	}
	
	/**
	 * Test method for free identifiers of actions
	 */
	public void testActions2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "E1", 
				makeList(), 
				makeList(), makeList(), 
				makeList("V3≔V2", "V1:∈ℕ", "V1:∣V1'=V4+1"));
	
		String a1 = FormulaFactory.getDefault().parseAssignment("V1:∈ℕ").getParsedAssignment().toString();
	
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("one event", events.length == 1);
	
		IAction[] actions = events[0].getActions();

		assertTrue("one action", actions.length == 1);
		
		assertEquals("action 0", a1, actions[0].getContents());

	}
	
	/**
	 * Test method for free identifiers of actions
	 */
	public void testActions3() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "E1", 
				makeList("L1", "L2"), 
				makeList("G1", "G2"), makeList("L1∈ℕ", "L2=ℕ+1"), 
				makeList("L1≔V2", "V1:∈ℕ", "V1:∣V1'=L1+1"));
	
		String a1 = FormulaFactory.getDefault().parseAssignment("V1:∈ℕ").getParsedAssignment().toString();
		String a2 = FormulaFactory.getDefault().parseAssignment("V1:∣V1'=L1+1").getParsedAssignment().toString();
	
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("one event", events.length == 1);
	
		IAction[] actions = events[0].getActions();

		assertTrue("two actions", actions.length == 2);

		assertEquals("action 0", a1, actions[0].getContents());
		assertEquals("action 1", a2, actions[1].getContents());
	}
	
	/**
	 * Test method for faulty initialisation
	 */
	public void testInitialisation1() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "INITIALISATION", 
				makeList("L1", "L2"), 
				makeList("G1", "G2"), makeList("L1∈ℕ", "L2=1"), 
				makeList("V1≔1", "V1:∣V1'=1"));
	
		/*String a1 =*/ FormulaFactory.getDefault().parseAssignment("V1:∈ℕ").getParsedAssignment().toString();
		/*String a2 =*/ FormulaFactory.getDefault().parseAssignment("V1:∣V1'=L1+1").getParsedAssignment().toString();
	
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("no event", events.length == 0);
		
	}
	
	/**
	 * Test method for correct initialisation with faulty action
	 */
	public void testInitialisation2() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "INITIALISATION", 
				makeList(), 
				makeList(), makeList(), 
				makeList("V2≔V1", "V1:∣V1'=1"));
	
		/*String a1 =*/ FormulaFactory.getDefault().parseAssignment("V1:∈ℕ").getParsedAssignment().toString();
		/*String a2 =*/ FormulaFactory.getDefault().parseAssignment("V1:∣V1'=L1+1").getParsedAssignment().toString();
	
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("one event", events.length == 1);
		
	}

	/**
	 * Test method for correct initialisation action
	 */
	public void testInitialisation3() throws Exception {
		IMachine rodinFile = createMachine("one");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "INITIALISATION", 
				makeList(), 
				makeList(), makeList(), 
				makeList("V2≔1", "V1:∣V1'=1"));
	
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		IEvent[] events = scMachine.getEvents();
		
		assertTrue("one event", events.length == 1);
		
		assertTrue("two actions", events[0].getActions().length == 2);
		
	}
	
	private ISCContext makeContext3() throws RodinDBException {
		ISCContext context = (ISCContext) rodinProject.createRodinFile("ctx.bcc", true, null);
		addSCConstants(context, makeList("C1", "C2"), makeList("ℤ", "ℙ(ℤ)"));
//		TestUtil.addIdentifiers(context, TestUtil.makeList("C1", "C2"), TestUtil.makeList("ℤ", "ℙ(ℤ)"));
		context.save(null, true);
		return context;
	}
	

	/**
	 * Test method for correct initialisation action
	 */
	public void testInitialisation4() throws Exception {
		makeContext3();
		IMachine rodinFile = createMachine("one");
		addSees(rodinFile, "ctx");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2∈ℕ"));
		addEvent(rodinFile, "INITIALISATION", 
				makeList(), 
				makeList(), makeList(), 
				makeList("V2≔C1", "V1:∈C2"));
	
		rodinFile.save(null, true);
		
		ISCMachine scMachine = runSC(rodinFile);
		
		ISCEvent[] events = scMachine.getSCEvents();
		
		assertTrue("one event", events.length == 1);
		
		assertTrue("two actions", events[0].getActions().length == 2);
		
	}


}
