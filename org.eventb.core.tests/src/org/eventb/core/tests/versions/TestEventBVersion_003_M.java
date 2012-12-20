/*******************************************************************************
 * Copyright (c) 2008 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.versions;

import static org.junit.Assert.assertEquals;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.junit.Test;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEventBVersion_003_M extends EventBVersionTest {
	
	/**
	 * machines of version 2 are updated to machines of version 3;
	 * inherited events become extended events
	 */
	@Test
	public void testVersion_00_inheritedEvents() throws Exception {
		String contents =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.machineFile version=\"2\" org.eventb.core.configuration=\"org.eventb.core.fwd\">" +
			"<org.eventb.core.event name=\"x1\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"false\" org.eventb.core.label=\"INITIALISATION\"/>" +
			"<org.eventb.core.event name=\"x2\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"true\" org.eventb.core.label=\"e\">" +
			"<org.eventb.core.parameter name=\"x3\" org.eventb.core.identifier=\"a\"/>" +
			"<org.eventb.core.guard name=\"x4\" org.eventb.core.label=\"G\" org.eventb.core.predicate=\"a∈ℤ\"/>" +
			"</org.eventb.core.event>" +
			"<org.eventb.core.event name=\"y2\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"false\" org.eventb.core.label=\"f\">" +
			"<org.eventb.core.parameter name=\"y3\" org.eventb.core.identifier=\"a\"/>" +
			"<org.eventb.core.guard name=\"y4\" org.eventb.core.label=\"G\" org.eventb.core.predicate=\"a∈ℤ\"/>" +
			"</org.eventb.core.event>" +
			"<org.eventb.core.event name=\"z1\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"true\" org.eventb.core.label=\"g\"/>" +
			"</org.eventb.core.machineFile>";
		String name = "mac.bum";
		createFile(name, contents);
		
		IRodinFile file = rodinProject.getRodinFile(name);
		IMachineRoot root = (IMachineRoot) file.getRoot();

		IEvent[] events = root.getEvents();
		assertEquals("4 events expected", 4, events.length);
		
		String[] labels = new String[] {IEvent.INITIALISATION, "e", "f", "g"};
		boolean[] ext = new boolean[] {false, true, false, true};
		String[] refines = new String[] {null, "e", null, "g"};
		
		for (int i=0; i<4; i++) {
			assertEquals("exptected " + labels[i], labels[i], events[i].getLabel());
			assertEquals("should" + (ext[i] ? "" : "not") + "be extended", ext[i],
					events[i].isExtended());
			IRefinesEvent[] refinesEvents = events[i].getRefinesClauses();
			if (refines[i] == null) {
				assertEquals("no refines clause expected", 0, refinesEvents.length);
			} else {
				assertEquals("1 refines clause expected", 1, refinesEvents.length);
				assertEquals("should refine " + refines[i], refines[i], refinesEvents[0].getAbstractEventLabel());
			}
		}
	
		assertEquals("should not have parameters", 0, events[1].getParameters().length);
		assertEquals("should not have guards", 0, events[1].getGuards().length);

		assertEquals("should have 1 parameter", 1, events[2].getParameters().length);
		assertEquals("should have 1 guard", 1, events[2].getGuards().length);
		
	}
	
	/**
	 * machines of version 2 are updated to machines of version 3;
	 * inherited initialisations become extended initialisations with out refines clauses
	 */
	@Test
	public void testVersion_01_inheritedInitialisation() throws Exception {
		String contents =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.machineFile version=\"2\" org.eventb.core.configuration=\"org.eventb.core.fwd\">" +
			"<org.eventb.core.event name=\"x1\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"true\" org.eventb.core.label=\"INITIALISATION\"/>" +
			"</org.eventb.core.machineFile>";
		String name = "mac.bum";
		createFile(name, contents);
		
		IRodinFile file = rodinProject.getRodinFile(name);
		IMachineRoot root = (IMachineRoot) file.getRoot();
		
		IEvent[] events = root.getEvents();
		assertEquals("1 event expected", 1, events.length);

		IRefinesEvent[] refinesEvents = events[0].getRefinesClauses();
		assertEquals("no refines clause expected", 0, refinesEvents.length);
	}

}
