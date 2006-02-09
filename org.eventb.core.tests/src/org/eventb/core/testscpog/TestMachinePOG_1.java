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
import org.eventb.core.IPOFile;
import org.eventb.core.ISCMachine;
import org.eventb.internal.core.protopog.POGCore;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TestMachinePOG_1 extends TestCase {
	
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

	/*
	 * Test method for 'org.eventb.internal.core.protopog.ContextPOG.run()'
	 */
	public final void testRun() throws Exception {
		ISCMachine machine = createContextOne();
		
		/*IFile poFile0 =*/ workspace.getRoot().getFile(machine.getResource().getFullPath().removeFileExtension().addFileExtension("bpo"));
		/*String poName =*/ machine.getPath().removeFileExtension().addFileExtension("bpo").toString();
		
		IPOFile poFile = (IPOFile) rodinProject.createRodinFile("one.bpo", true, null);

		poFile.open(null);
		
		POGCore.runMachinePOG(machine, poFile);
		
		poFile.save(null, true);

	}

	private ISCMachine createContextOne() throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.bcm", true, null);
		TestUtil.addOldAxioms(rodinFile, "AXIOMS");
		TestUtil.addOldTheorems(rodinFile, "THEOREMS");
		TestUtil.addIdentifiers(rodinFile,
				TestUtil.makeList("V1", "V2"),
				TestUtil.makeList("ℤ", "ℤ"));
		TestUtil.addVariables(rodinFile, TestUtil.makeList("V1", "V2"));
		TestUtil.addInvariants(rodinFile, 
				TestUtil.makeList("I1", "I2", "I3"), 
				TestUtil.makeList("V1∈ℤ", "V2∈ℤ", "V1+V2=0"));
		TestUtil.addTheorems(rodinFile, 
				TestUtil.makeList("T1"), 
				TestUtil.makeList("V1>0 ⇒ (∃ x · x ∈ 0‥V2)"), null);
		TestUtil.addEvent(rodinFile,
				"E1",
				TestUtil.makeList("T1", "T2"),
				TestUtil.makeList("G1", "G2"),
				TestUtil.makeList("T1∈ℕ", "T2∈ℕ"),
				TestUtil.makeList("V1≔1")
		);
		rodinFile.save(null, true);
		return (ISCMachine) rodinFile;
	}

}
