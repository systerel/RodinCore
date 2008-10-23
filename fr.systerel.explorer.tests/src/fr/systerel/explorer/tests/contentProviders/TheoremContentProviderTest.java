/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
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

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ITheorem;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.navigator.IElementNode;
import fr.systerel.explorer.navigator.contentProviders.TheoremContentProvider;
import fr.systerel.explorer.tests.ExplorerTest;

/**
 * 
 *
 */
public class TheoremContentProviderTest extends ExplorerTest {

	private static TheoremContentProvider contentProvider;
	protected static IContextRoot c0;
	protected static IMachineRoot m0;
	protected static IElementNode node;
	protected static IElementNode node2;
	protected static ITheorem theorem1;
	protected static ITheorem theorem2;
	protected static ITheorem theorem3;
	protected static ITheorem theorem4;
	protected static ITheorem theorem5;
	protected static ITheorem theorem6;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new TheoremContentProvider();
		
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);
		
		ModelController.processProject(rodinProject);
		
		node = ModelController.getContext(c0).theorem_node;
		assertNotNull("the node should be created successfully ", node);
		node2 = ModelController.getMachine(m0).theorem_node;
		assertNotNull("the node should be created successfully ", node2);

		theorem1 = createTheorem(c0, "theorem1");
		theorem2 = createTheorem(c0, "theorem2");
		theorem3 = createTheorem(c0, "theorem3");
		assertArray(c0.getTheorems(), theorem1, theorem2, theorem3);

		theorem4 = createTheorem(m0, "theorem4");
		theorem5 = createTheorem(m0, "theorem5");
		theorem6 = createTheorem(m0, "theorem6");
		assertArray(m0.getTheorems(), theorem4, theorem5, theorem6);
		
	}
	
	@Test
	public void getChildrenContext() throws RodinDBException {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(c0), node);
		//get the theorems for the node input
		assertArray(contentProvider.getChildren(node), theorem1, theorem2, theorem3);
	}

	@Test
	public void getChildrenMachine() throws RodinDBException {
		// get the intermediary node for machine input
		assertArray(contentProvider.getChildren(m0), node2);
		//get the theorem for the node input
		assertArray(contentProvider.getChildren(node2), theorem4, theorem5, theorem6);
	}
	
	@Test
	public void getParentContext() throws RodinDBException {
		// get the parent of the theorems
		assertEquals(contentProvider.getParent(theorem1),  node);
		// get the parent of the intermediary node
		assertEquals(contentProvider.getParent(node),  c0);
	}

	@Test
	public void getParentMachine() throws RodinDBException {
		// get the parent of the theorems
		assertEquals(contentProvider.getParent(theorem4),  node2);
		// get the parent of the intermediary node
		assertEquals(contentProvider.getParent(node2),  m0);
	}
	
	@Test
	public void hasChildrenContext() {
		//the intermediary node has 3 children (the 3 theorems)
		assertTrue(contentProvider.hasChildren(node));
		//the context has 1 child (the intermediary node)
		assertTrue(contentProvider.hasChildren(c0));
	}

	@Test
	public void hasChildrenMachine() {
		//the intermediary node has 3 children (the 3 theorems)
		assertTrue(contentProvider.hasChildren(node2));
		//the machine has 1 child (the intermediary node)
		assertTrue(contentProvider.hasChildren(m0));
	}
	
	@Test
	public void getElementsContext() {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(c0), node);
		//get the theorems for the node input
		assertArray(contentProvider.getChildren(node), theorem1, theorem2, theorem3);
	}

	@Test
	public void getElementsMachine() {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(m0), node2);
		//get the theorems for the node input
		assertArray(contentProvider.getChildren(node2), theorem4, theorem5, theorem6);
	}
	
	
}
