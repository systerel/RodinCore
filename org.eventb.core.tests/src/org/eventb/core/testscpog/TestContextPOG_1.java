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
import org.eventb.core.ISCContext;
import org.eventb.internal.core.protopog.POGCore;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TestContextPOG_1 extends TestCase {

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
		ISCContext context = createContextOne();
		
		/*IFile poFile0 =*/ workspace.getRoot().getFile(context.getResource().getFullPath().removeFileExtension().addFileExtension("bpo"));
		/*String poName =*/ context.getPath().removeFileExtension().addFileExtension("bpo").toString();
		
		IPOFile poFile = (IPOFile) rodinProject.createRodinFile("one.bpo", true, null);

		poFile.open(null);
		
		POGCore.runContextPOG(context, poFile);
		
		poFile.save(null, true);

	}

	private ISCContext createContextOne() throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.bcc", true, null);
		TestUtil.addOldAxioms(rodinFile, "AXIOMS");
		TestUtil.addOldTheorems(rodinFile, "THEOREMS");
//		TestUtil.addIdentifiers(rodinFile,
//				TestUtil.makeList("S1", "S2", "C1", "C2", "C3", "F1"),
//				TestUtil.makeList("ℙ(S1)", "ℙ(S2)", "S1", "ℙ(S1∗S2)", "S2", "ℕ"));
		TestUtil.addSCCarrierSets(rodinFile, TestUtil.makeList("S1", "S2"), TestUtil.makeList("ℙ(S1)", "ℙ(S2)"));
		TestUtil.addSCConstants(rodinFile, TestUtil.makeList("C1", "F1", "C2", "C3"), TestUtil.makeList("S1", "ℙ(S1×S2)", "S2", "ℤ"));
		TestUtil.addAxioms(rodinFile, 
				TestUtil.makeList("A1", "A2", "A3", "A4"), 
				TestUtil.makeList("C1∈S1", "F1∈S1↔S2", "C2∈F1[{C1}]", "C3=1"), null);
		TestUtil.addTheorems(rodinFile, 
				TestUtil.makeList("T1"), 
				TestUtil.makeList("C3>0 ⇒ (∃ x · x ∈ ran(F1))"), null);
		rodinFile.save(null, true);
		return (ISCContext) rodinFile;
	}
}
