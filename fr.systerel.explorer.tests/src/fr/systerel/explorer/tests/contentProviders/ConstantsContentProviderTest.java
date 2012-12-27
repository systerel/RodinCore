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

import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.ConstantsContentProvider;

/**
 * 
 *
 */
public class ConstantsContentProviderTest extends ExplorerTest {

	private static ConstantsContentProvider contentProvider;
	protected static IContextRoot c0;
	protected static IElementNode node;
	protected static IConstant const1;
	protected static IConstant const2;
	protected static IConstant const3;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new ConstantsContentProvider();
		
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		ModelController.processProject(rodinProject);
		
		node = ModelController.getContext(c0).constant_node;
		assertNotNull("the node should be created successfully ", node);

		const1 = createConstant(c0, "const1");
		const2 = createConstant(c0, "const2");
		const3 = createConstant(c0, "const3");
		assertArray(c0.getConstants(), const1, const2, const3);
		
	}
	
	@Test
	public void getChildren() throws RodinDBException {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(c0), node);
		//get the constants for the node input
		assertArray(contentProvider.getChildren(node), const1, const2, const3);
	}

	@Test
	public void getParent() throws RodinDBException {
		// get the parent of the constants
		assertEquals(contentProvider.getParent(const1),  node);
		// get the parent of the intermediary node
		assertEquals(contentProvider.getParent(node),  c0);
	}

	@Test
	public void hasChildren() {
		//the intermediary node has 3 children (the 3 constants)
		assertTrue(contentProvider.hasChildren(node));
		//the context has 1 child (the intermediary node)
		assertTrue(contentProvider.hasChildren(c0));
	}

	@Test
	public void getElements() {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(c0), node);
		//get the constants for the node input
		assertArray(contentProvider.getChildren(node), const1, const2, const3);
	}

}
