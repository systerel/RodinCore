/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.explorer.tests.contentProviders;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.InvariantContentProvider;

/**
 * 
 *
 */
public class InvariantContentProviderTest extends ExplorerTest {

	private InvariantContentProvider contentProvider;
	protected static IMachineRoot m0;
	protected static IElementNode node;
	protected static IInvariant invariant1;
	protected static IInvariant  invariant2;
	protected static IInvariant  invariant3;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new InvariantContentProvider();
		
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);

		ModelController.processProject(rodinProject);
		
		node = ModelController.getMachine(m0).invariant_node;
		assertNotNull("the node should be created successfully ", node);

		invariant1 = createInvariant(m0, "invariant1");
		invariant2 = createInvariant(m0, "invariant2");
		invariant3 = createInvariant(m0, "invariant3");
		assertArray(m0.getInvariants(), invariant1, invariant2, invariant3);
		
	}
	
	@Test
	public void getChildren() throws RodinDBException {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(m0), node);
		//get the invariants for the node input
		assertArray(contentProvider.getChildren(node), invariant1, invariant2, invariant3);
	}

	@Test
	public void getParent() throws RodinDBException {
		ModelController.getMachine(m0).processChildren();
		// get the parent of the invariants
		assertEquals(contentProvider.getParent(invariant1),  node);
		// get the parent of the intermediary node
		assertEquals(contentProvider.getParent(node),  m0);
	}

	@Test
	public void hasChildren() {
		//the intermediary node has 3 children (the 3 invariants)
		assertTrue(contentProvider.hasChildren(node));
		//the context has 1 child (the intermediary node)
		assertTrue(contentProvider.hasChildren(m0));
	}

	@Test
	public void getElements() {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(m0), node);
		//get the invariants for the node input
		assertArray(contentProvider.getChildren(node), invariant1, invariant2, invariant3);
	}

}
