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

import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.AxiomContentProvider;

/**
 * 
 *
 */
public class AxiomContentProviderTest extends ExplorerTest {

	private static AxiomContentProvider contentProvider;
	protected static IContextRoot c0;
	protected static IElementNode node;
	protected static IAxiom axiom1;
	protected static IAxiom axiom2;
	protected static IAxiom axiom3;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new AxiomContentProvider();
		
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		ModelController.processProject(rodinProject);
		
		node = ModelController.getContext(c0).axiom_node;
		assertNotNull("the node should be created successfully ", node);

		axiom1 = createAxiom(c0, "axiom1");
		axiom2 = createAxiom(c0, "axiom2");
		axiom3 = createAxiom(c0, "axiom3");
		assertArray(c0.getAxioms(), axiom1, axiom2, axiom3);
		
	}
	
	/**
	 * Test method for {@link fr.systerel.internal.explorer.navigator.contentProviders.AxiomContentProvider#getChildren(java.lang.Object)}.
	 * @throws RodinDBException 
	 */
	@Test
	public void getChildren() throws RodinDBException {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(c0), node);
		//get the axioms for the node input
		assertArray(contentProvider.getChildren(node), axiom1, axiom2, axiom3);
	}

	/**
	 * Test method for {@link fr.systerel.internal.explorer.navigator.contentProviders.AxiomContentProvider#getParent(java.lang.Object)}.
	 * @throws RodinDBException 
	 */
	@Test
	public void getParent() throws RodinDBException {
		ModelController.getContext(c0).processChildren();
		// get the parent of the axioms
		assertEquals(node, contentProvider.getParent(axiom1));
		// get the parent of the intermediary node
		assertEquals(c0, contentProvider.getParent(node));
	}

	/**
	 * Test method for {@link fr.systerel.internal.explorer.navigator.contentProviders.AxiomContentProvider#hasChildren(java.lang.Object)}.
	 */
	@Test
	public void hasChildren() {
		//the intermediary node has 3 children (the 3 axioms)
		assertTrue(contentProvider.hasChildren(node));
		//the context has 1 child (the intermediary node)
		assertTrue(contentProvider.hasChildren(c0));
	}

	/**
	 * Test method for {@link fr.systerel.internal.explorer.navigator.contentProviders.AxiomContentProvider#getElements(java.lang.Object)}.
	 */
	@Test
	public void getElements() {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(c0), node);
		//get the axioms for the node input
		assertArray(contentProvider.getChildren(node), axiom1, axiom2, axiom3);
	}

}
