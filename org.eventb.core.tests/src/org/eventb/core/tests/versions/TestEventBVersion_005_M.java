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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEventBVersion_005_M extends EventBVersionTest {
	
	/**
	 * machines of version 4 are updated to machines of version 5
	 * order is preserved
	 */
	@Test
	public void testVersion_01_machine() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"4\">" +
			"<org.eventb.core.invariant name=\"internal_element1\" org.eventb.core.label=\"inv1\" org.eventb.core.predicate=\"4&lt;3\"/>" +
			"<org.eventb.core.invariant name=\"internal_element2\" org.eventb.core.label=\"inv2\" org.eventb.core.predicate=\"∀x·x=1\"/>" +
			"<org.eventb.core.theorem name=\"internal_element1\" org.eventb.core.label=\"thm1\" org.eventb.core.predicate=\"TRUE=FALSE\"/>" +
			"<org.eventb.core.theorem name=\"internal_element2\" org.eventb.core.label=\"thm2\" org.eventb.core.predicate=\"∃p·p∈∅∩BOOL\"/>" +
			"</org.eventb.core.machineFile>";

		runTest(contents);
		
	}

	/**
	 * machines of version 4 are updated to machines of version 5
	 * order is corrected
	 */
	@Test
	public void testVersion_02_machine() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"4\">" +
			"<org.eventb.core.theorem name=\"internal_element1\" org.eventb.core.label=\"thm1\" org.eventb.core.predicate=\"TRUE=FALSE\"/>" +
			"<org.eventb.core.invariant name=\"internal_element1\" org.eventb.core.label=\"inv1\" org.eventb.core.predicate=\"4&lt;3\"/>" +
			"<org.eventb.core.theorem name=\"internal_element2\" org.eventb.core.label=\"thm2\" org.eventb.core.predicate=\"∃p·p∈∅∩BOOL\"/>" +
			"<org.eventb.core.invariant name=\"internal_element2\" org.eventb.core.label=\"inv2\" org.eventb.core.predicate=\"∀x·x=1\"/>" +
			"</org.eventb.core.machineFile>";

		runTest(contents);
		
	}

	/**
	 * machines of version 4 are updated to machines of version 5
	 * guards take a theorem attribute with false value
	 */
	@Test
	public void testVersion_03_machine() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"4\">"
			+ "<org.eventb.core.event"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.convergence=\"0\""
			+ "		org.eventb.core.extended=\"false\""
			+ "		org.eventb.core.label=\"evt1\">"
			+ "		<org.eventb.core.guard"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.label=\"grd1\""
			+ "				org.eventb.core.predicate=\"prm1 = var1\"/>"
			+ "</org.eventb.core.event>" +
			"</org.eventb.core.machineFile>";

		final String name = "mch.bum";
		createFile(name, contents);
		final IRodinFile file = rodinProject.getRodinFile(name);
		IMachineRoot root = (IMachineRoot)file.getRoot();
		final IEvent event = assertSingleGet(root.getEvents());
		final IGuard guard = assertSingleGet(event.getGuards());
		assertTrue("guards should have a theorem attribute", guard.hasTheorem());
		assertFalse("guards should not be theorems", guard.isTheorem());
	}

	private void runTest(String contents) throws Exception,
			RodinDBException {
		String name = "mch.bum";
		createFile(name, contents);
		
		IRodinFile file = rodinProject.getRodinFile(name);
		IMachineRoot root = (IMachineRoot)file.getRoot();
		IInvariant[] invariants = root.getInvariants();
		
		String[] labels = new String[] { "inv1", "inv2", "thm1", "thm2" };
		String[] preds = new String[] { "4<3", "∀x·x=1", "TRUE=FALSE", "∃p·p∈∅∩BOOL" };
		boolean[] thm = new boolean[] { false, false, true, true };
		
		for (int i=0; i<4; i++) {
			assertEquals("wrong label", labels[i], invariants[i].getLabel());
			assertEquals("wrong formula", preds[i], invariants[i].getPredicateString());
			assertEquals("wrong status", thm[i], invariants[i].isTheorem());
		}
	}

}
