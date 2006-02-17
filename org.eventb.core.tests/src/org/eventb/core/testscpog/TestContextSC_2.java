/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import org.eventb.core.IContext;
import org.eventb.core.ISCContext;

public class TestContextSC_2 extends BuilderTest {

	/**
	 * Test method for name clashes of carrier sets and constants
	 */
	public void testCarrierSetandConstant1() throws Exception {
		IContext rodinFile = createContext("one");
		addCarrierSets(rodinFile, makeList("S1", "S1"));
		addConstants(rodinFile, makeList("C1", "C1"));
		addAxioms(rodinFile, makeList("A1"), makeList("C1∈ℕ∧C2∈ℕ"), null);
		rodinFile.save(null, true);
		
		ISCContext scContext = runSC(rodinFile);
		
		assertTrue("empty carrier set", scContext.getCarrierSets().length == 0);
		assertTrue("empty constant", scContext.getConstants().length == 0);
	}

	/**
	 * Test method for name clashes of carrier sets and constants
	 */
	public void testCarrierSetandConstant2() throws Exception {
		IContext rodinFile = createContext("one");
		addCarrierSets(rodinFile, makeList("S1"));
		addConstants(rodinFile, makeList("S1"));
		rodinFile.save(null, true);
		
		ISCContext scContext = runSC(rodinFile);
		
		assertTrue("empty carrier set", scContext.getCarrierSets().length == 0);
		assertTrue("empty constant", scContext.getConstants().length == 0);
	}

	/**
	 * Test method for name clashes of carrier sets and constants
	 */
	public void testCarrierSetandConstant3() throws Exception {
		IContext rodinFile = createContext("one");
		addCarrierSets(rodinFile, makeList("S1", "S2"));
		addConstants(rodinFile, makeList("C1", "S1"));
		addAxioms(rodinFile, makeList("A1"), makeList("C1∈ℕ"), null);
		rodinFile.save(null, true);
		
		ISCContext scContext = runSC(rodinFile);
		
		assertTrue("one carrier set", scContext.getSCCarrierSets().length == 1 && scContext.getSCCarrierSets()[0].getElementName().equals("S2"));
		assertTrue("one constant", scContext.getSCConstants().length == 1 && scContext.getSCConstants()[0].getElementName().equals("C1"));
	}

	/**
	 * Test method for name clashes of theorems and axioms
	 */
	public void testTheoremsAndAxioms1() throws Exception {
		IContext rodinFile = createContext("one");
		addAxioms(rodinFile, makeList("A1", "A1"), makeList("⊤", "⊤"), null);
		addTheorems(rodinFile, makeList("T1", "T1"), makeList("⊤", "⊤"), null);
		rodinFile.save(null, true);
		
		ISCContext scContext = runSC(rodinFile);
		
		assertTrue("no axioms", scContext.getAxioms().length == 0);
		assertTrue("no theorems", scContext.getTheorems().length == 0);
	}

	/**
	 * Test method for name clashes of theorems and axioms
	 */
	public void testTheoremsAndAxioms2() throws Exception {
		IContext rodinFile = createContext("one");
		addAxioms(rodinFile, makeList("A1", "A2"), makeList("⊤", "⊤"), null);
		addTheorems(rodinFile, makeList("T1", "A1"), makeList("⊤", "⊤"), null);
		rodinFile.save(null, true);
		
		ISCContext scContext = runSC(rodinFile);
		
		assertTrue("one axiom", scContext.getAxioms().length == 1 && scContext.getAxioms()[0].getElementName().equals("A2"));
		assertTrue("one theorem", scContext.getTheorems().length == 1 && scContext.getTheorems()[0].getElementName().equals("T1"));
	}

	/**
	 * Test method for name clashes of theorems and axioms
	 */
	public void testTyping1() throws Exception {
		IContext rodinFile = createContext("one");
		addConstants(rodinFile, makeList("C1", "C2"));
		addAxioms(rodinFile, makeList("A1", "A2"), makeList("C1∈ℕ", "C2={C1,ℕ}"), null);
		rodinFile.save(null, true);
		
		ISCContext scContext = runSC(rodinFile);
		
		assertTrue("one axiom", scContext.getAxioms().length == 1 && scContext.getAxioms()[0].getElementName().equals("A1"));
		assertTrue("one constant", scContext.getSCConstants().length == 1 && scContext.getSCConstants()[0].getElementName().equals("C1"));
	}
}
