/*******************************************************************************
 * Copyright (c) 2009, 2012 Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Universitaet Duesseldorf - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.versions;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Tests for theorems almost everywhere, i.e., theorems and axioms are kept in one list
 * 
 * @author Stefan Hallerstede
 *
 */
public class TestEventBVersion_003_C extends EventBVersionTest {
	
	/**
	 * contexts of version 2 are updated to contexts of version 3
	 * order is preserved
	 */
	@Test
	public void testVersion_01_context() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"2\">" +
			"<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"4&lt;3\"/>" +
			"<org.eventb.core.axiom name=\"internal_element2\" org.eventb.core.label=\"axm2\" org.eventb.core.predicate=\"∀x·x=1\"/>" +
			"<org.eventb.core.theorem name=\"internal_element1\" org.eventb.core.label=\"thm1\" org.eventb.core.predicate=\"TRUE=FALSE\"/>" +
			"<org.eventb.core.theorem name=\"internal_element2\" org.eventb.core.label=\"thm2\" org.eventb.core.predicate=\"∃p·p∈∅∩BOOL\"/>" +
			"</org.eventb.core.contextFile>";

		runTest(contents);
		
	}

	/**
	 * contexts of version 2 are updated to contexts of version 3
	 * order is corrected
	 */
	@Test
	public void testVersion_02_context() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"2\">" +
			"<org.eventb.core.theorem name=\"internal_element1\" org.eventb.core.label=\"thm1\" org.eventb.core.predicate=\"TRUE=FALSE\"/>" +
			"<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"4&lt;3\"/>" +
			"<org.eventb.core.theorem name=\"internal_element2\" org.eventb.core.label=\"thm2\" org.eventb.core.predicate=\"∃p·p∈∅∩BOOL\"/>" +
			"<org.eventb.core.axiom name=\"internal_element2\" org.eventb.core.label=\"axm2\" org.eventb.core.predicate=\"∀x·x=1\"/>" +
			"</org.eventb.core.contextFile>";

		runTest(contents);
		
	}

	private void runTest(String contents) throws CoreException,
			RodinDBException {
		String name = "ctx.buc";
		createFile(name, contents);
		
		IRodinFile file = rodinProject.getRodinFile(name);
		IContextRoot root = (IContextRoot)file.getRoot();
		IAxiom[] axioms = root.getAxioms();
		
		String[] labels = new String[] { "axm1", "axm2", "thm1", "thm2" };
		String[] preds = new String[] { "4<3", "∀x·x=1", "TRUE=FALSE", "∃p·p∈∅∩BOOL" };
		boolean[] thm = new boolean[] { false, false, true, true };
		
		for (int i=0; i<4; i++) {
			assertEquals("wrong label", labels[i], axioms[i].getLabel());
			assertEquals("wrong formula", preds[i], axioms[i].getPredicateString());
			assertEquals("wrong status", thm[i], axioms[i].isTheorem());
		}
	}

}
