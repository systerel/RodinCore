/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eventb.core.IContext;
import org.eventb.core.ISCContext;
import org.eventb.internal.core.protosc.SCCore;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class TestContextSC_2 extends TestCase {

	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	IRodinProject rodinProject;
	
	protected void setUp() throws Exception {
		super.setUp();
		RodinCore.create(workspace.getRoot()).open(null);  // TODO temporary kludge
		IProject project = workspace.getRoot().getProject("testsc");
		project.create(null);
		project.open(null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(description, null);
		rodinProject = RodinCore.create(project);
		rodinProject.open(null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		rodinProject.getProject().delete(true, true, null);
	}

	/**
	 * Test method for name clashes of carrier sets and constants
	 */
	public void testCarrierSetandConstant1() throws Exception {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.buc", true, null);
		TestUtil.addCarrierSets(rodinFile, TestUtil.makeList("S1", "S1"));
		TestUtil.addConstants(rodinFile, TestUtil.makeList("C1", "C1"));
		TestUtil.addAxioms(rodinFile, TestUtil.makeList("A1"), TestUtil.makeList("C1∈ℕ∧C2∈ℕ"), null);
		
		rodinFile.save(null, true);
		
		ISCContext scContext = (ISCContext) rodinProject.createRodinFile("one.bcc", true, null);
		
		scContext.open(null);
		
		SCCore.runContextSC((IContext) rodinFile, scContext);
		
		scContext.save(null, true);
		
		assertTrue("empty carrier set", scContext.getCarrierSets().length == 0);
		assertTrue("empty constant", scContext.getConstants().length == 0);
	}

	/**
	 * Test method for name clashes of carrier sets and constants
	 */
	public void testCarrierSetandConstant2() throws Exception {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.buc", true, null);
		TestUtil.addCarrierSets(rodinFile, TestUtil.makeList("S1"));
		TestUtil.addConstants(rodinFile, TestUtil.makeList("S1"));
		
		rodinFile.save(null, true);
		
		ISCContext scContext = (ISCContext) rodinProject.createRodinFile("one.bcc", true, null);
		
		scContext.open(null);
		
		SCCore.runContextSC((IContext) rodinFile, scContext);
		
		scContext.save(null, true);
		
		assertTrue("empty carrier set", scContext.getCarrierSets().length == 0);
		assertTrue("empty constant", scContext.getConstants().length == 0);
	}

	/**
	 * Test method for name clashes of carrier sets and constants
	 */
	public void testCarrierSetandConstant3() throws Exception {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.buc", true, null);
		TestUtil.addCarrierSets(rodinFile, TestUtil.makeList("S1", "S2"));
		TestUtil.addConstants(rodinFile, TestUtil.makeList("C1", "S1"));
		TestUtil.addAxioms(rodinFile, TestUtil.makeList("A1"), TestUtil.makeList("C1∈ℕ"), null);

		rodinFile.save(null, true);
		
		ISCContext scContext = (ISCContext) rodinProject.createRodinFile("one.bcc", true, null);
		
		scContext.open(null);
		
		SCCore.runContextSC((IContext) rodinFile, scContext);
		
		scContext.save(null, true);
		
		assertTrue("one carrier set", scContext.getSCCarrierSets().length == 1 && scContext.getSCCarrierSets()[0].getElementName().equals("S2"));
		assertTrue("one constant", scContext.getSCConstants().length == 1 && scContext.getSCConstants()[0].getElementName().equals("C1"));
	}

	/**
	 * Test method for name clashes of theorems and axioms
	 */
	public void testTheoremsAndAxioms1() throws Exception {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.buc", true, null);
		TestUtil.addAxioms(rodinFile, TestUtil.makeList("A1", "A1"), TestUtil.makeList("⊤", "⊤"), null);
		TestUtil.addTheorems(rodinFile, TestUtil.makeList("T1", "T1"), TestUtil.makeList("⊤", "⊤"), null);
		
		rodinFile.save(null, true);
		
		ISCContext scContext = (ISCContext) rodinProject.createRodinFile("one.bcc", true, null);
		
		scContext.open(null);
		
		SCCore.runContextSC((IContext) rodinFile, scContext);
		
		scContext.save(null, true);
		
		assertTrue("no axioms", scContext.getAxioms().length == 0);
		assertTrue("no theorems", scContext.getTheorems().length == 0);
	}

	/**
	 * Test method for name clashes of theorems and axioms
	 */
	public void testTheoremsAndAxioms2() throws Exception {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.buc", true, null);
		TestUtil.addAxioms(rodinFile, TestUtil.makeList("A1", "A2"), TestUtil.makeList("⊤", "⊤"), null);
		TestUtil.addTheorems(rodinFile, TestUtil.makeList("T1", "A1"), TestUtil.makeList("⊤", "⊤"), null);
		
		rodinFile.save(null, true);
		
		ISCContext scContext = (ISCContext) rodinProject.createRodinFile("one.bcc", true, null);
		
		scContext.open(null);
		
		SCCore.runContextSC((IContext) rodinFile, scContext);
		
		scContext.save(null, true);
		
		assertTrue("one axiom", scContext.getAxioms().length == 1 && scContext.getAxioms()[0].getElementName().equals("A2"));
		assertTrue("one theorem", scContext.getTheorems().length == 1 && scContext.getTheorems()[0].getElementName().equals("T1"));
	}

	/**
	 * Test method for name clashes of theorems and axioms
	 */
	public void testTyping1() throws Exception {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.buc", true, null);
		TestUtil.addConstants(rodinFile, TestUtil.makeList("C1", "C2"));
		TestUtil.addAxioms(rodinFile, TestUtil.makeList("A1", "A2"), TestUtil.makeList("C1∈ℕ", "C2={C1,ℕ}"), null);
		
		rodinFile.save(null, true);
		
		ISCContext scContext = (ISCContext) rodinProject.createRodinFile("one.bcc", true, null);
		
		scContext.open(null);
		
		SCCore.runContextSC((IContext) rodinFile, scContext);
		
		scContext.save(null, true);
		
		assertTrue("one axiom", scContext.getAxioms().length == 1 && scContext.getAxioms()[0].getElementName().equals("A1"));
		assertTrue("one constant", scContext.getSCConstants().length == 1 && scContext.getSCConstants()[0].getElementName().equals("C1"));
	}
}
