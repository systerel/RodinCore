/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.explorer.tests.contentProviders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.EventContentProvider;

/**
 * 
 *
 */
public class EventContentProviderTest extends ExplorerTest {

	private EventContentProvider contentProvider;
	protected static IMachineRoot m0;
	protected static IElementNode node;
	protected static IEvent event1;
	protected static IEvent event2;
	protected static IEvent event3;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new EventContentProvider();
		
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);

		ModelController.processProject(rodinProject);
		
		node = ModelController.getMachine(m0).event_node;
		assertNotNull("the node should be created successfully ", node);

		event1 = createEvent(m0, "event1");
		event2 = createEvent(m0, "event2");
		event3 = createEvent(m0, "event3");
		assertArray(m0.getEvents(), event1, event2, event3);
		
	}
	
	@Test
	public void getChildren() throws RodinDBException {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(m0), node);
		//get the events for the node input
		assertArray(contentProvider.getChildren(node), event1, event2, event3);
	}

	@Test
	public void getParent() throws RodinDBException {
		ModelController.getMachine(m0).processChildren();
		// get the parent of the events
		assertEquals(contentProvider.getParent(event1),  node);
		// get the parent of the intermediary node
		assertEquals(contentProvider.getParent(node),  m0);
	}

	@Test
	public void hasChildren() {
		//the intermediary node has 3 children (the 3 events)
		assertTrue(contentProvider.hasChildren(node));
		//the context has 1 child (the intermediary node)
		assertTrue(contentProvider.hasChildren(m0));
	}

	@Test
	public void getElements() {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(m0), node);
		//get the events for the node input
		assertArray(contentProvider.getChildren(node), event1, event2, event3);
	}

}
