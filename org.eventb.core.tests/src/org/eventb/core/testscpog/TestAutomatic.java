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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContext;
import org.eventb.core.IMachine;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TestAutomatic extends TestCase {
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	IRodinProject rodinProject;
	
	protected void setUp() throws Exception {
		super.setUp();
		RodinCore.create(workspace.getRoot()).open(null);  // TODO temporary kludge
		IProject project = workspace.getRoot().getProject("P");
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
	
	protected void runBuilder() throws CoreException {
		rodinProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
	private IContext createContextOne() throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.buc", true, null);
		TestUtil.addCarrierSets(rodinFile, TestUtil.makeList("S1", "S2"));
		TestUtil.addConstants(rodinFile, TestUtil.makeList("C1", "C2", "C3", "F1"));
		TestUtil.addAxioms(rodinFile, TestUtil.makeList("A1", "A2", "A3", "A4"), TestUtil.makeList("C1∈S1", "F1∈S1↔S2", "C2∈F1[{C1}]", "C3=1"), null);
		TestUtil.addTheorems(rodinFile, TestUtil.makeList("T1"), TestUtil.makeList("C3>0 ⇒ (∃ x · x ∈ ran(F1))"), null);
		rodinFile.save(null, true);
		return (IContext) rodinFile;
	}

	public final void testContext1() throws Exception {
		createContextOne();
		runBuilder();
	}
	
	private IMachine createMachineOne(boolean sees) throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.bum", true, null);
		if(sees)
			TestUtil.addSees(rodinFile, "two");
		TestUtil.addVariables(rodinFile, TestUtil.makeList("V1", "V2"));
		TestUtil.addInvariants(rodinFile, TestUtil.makeList("I1", "I2"), TestUtil.makeList("V1∈ℕ", "V2>V1"));
		TestUtil.addTheorems(rodinFile, TestUtil.makeList("T2"), TestUtil.makeList("1 = V2+V1"), null);
		TestUtil.addEvent(rodinFile, "E1", 
				TestUtil.makeList("L1"), 
				TestUtil.makeList("G1", "G2"), 
				TestUtil.makeList("V1=V2", "L1>V1"), 
				TestUtil.makeList("V1≔1", "V2≔V1+L1"));
		rodinFile.save(null, true);
		return (IMachine) rodinFile;
	}

	private IContext createContextTwo() throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("two.buc", true, null);
		TestUtil.addCarrierSets(rodinFile, TestUtil.makeList("S1", "S2"));
		TestUtil.addConstants(rodinFile, TestUtil.makeList("C1", "C2", "C3", "F1"));
		TestUtil.addAxioms(rodinFile, TestUtil.makeList("A1", "A2", "A3", "A4"), TestUtil.makeList("C1∈S1", "F1∈S1↔S2", "C2∈F1[{C1}]", "C3=1"), null);
		TestUtil.addTheorems(rodinFile, TestUtil.makeList("T1"), TestUtil.makeList("C3>0 ⇒ (∃ x · x ∈ ran(F1))"), null);
		rodinFile.save(null, true);
		return (IContext) rodinFile;
	}
	public final void testMachine1() throws Exception {
		createMachineOne(true);
		createContextTwo();
		runBuilder();
	}
	
}
