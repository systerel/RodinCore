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
import org.eventb.core.IMachine;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCMachine;
import org.eventb.internal.core.protosc.SCCore;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TestMachineSC_1 extends TestCase {

	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	IRodinProject rodinProject;
	
	@Override
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

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		rodinProject.getProject().delete(true, true, null);
	}

	/*
	 * Test method for 'org.eventb.internal.core.protosc.ContextSC.run()'
	 * This test only checks whether the database works correctly
	 * with the context static checker
	 */
	public void testRunWithoutSees() throws Exception {
		IMachine machine = createMachineOne(false);
		
//		IFile scFile = workspace.getRoot().getFile(machine.getResource().getFullPath().removeFileExtension().addFileExtension("bcm"));
//		String scName = machine.getPath().removeFileExtension().addFileExtension("bcm").toString();
		
		ISCMachine scMachine = (ISCMachine) rodinProject.createRodinFile("one.bcm", true, null);
		
//		SCContext scContext = (SCContext) RodinCore.create(scName);
//		((IRodinProject) scContext.getRodinProject()).createRodinFile(scContext.getElementName(), true, null);
		scMachine.open(null);
		
		SCCore.runMachineSC(machine, scMachine);
		
		scMachine.save(null, true);

	}
	
	/*
	 * Test method for 'org.eventb.internal.core.protosc.ContextSC.run()'
	 * This test only checks whether the database works correctly
	 * with the context static checker
	 */
	public void testRunWithSees() throws Exception {
		IContext context = createContextTwo();
		IMachine machine = createMachineOne(true);

//		IFile scFile = workspace.getRoot().getFile(context.getResource().getFullPath().removeFileExtension().addFileExtension("bcc"));
//		String scName = context.getPath().removeFileExtension().addFileExtension("bcc").toString();
		
		ISCContext scContext = (ISCContext) rodinProject.createRodinFile("two.bcc", true, null);
		ISCMachine scMachine = (ISCMachine) rodinProject.createRodinFile("one.bcm", true, null);
	
//		SCContext scContext = (SCContext) RodinCore.create(scName);
//		((IRodinProject) scContext.getRodinProject()).createRodinFile(scContext.getElementName(), true, null);
		scContext.open(null);
		
		SCCore.runContextSC(context, scContext);
		
		scContext.save(null, true);
		
		scMachine.open(null);
		
		SCCore.runMachineSC(machine, scMachine);
		
		scMachine.save(null, true);

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

}
