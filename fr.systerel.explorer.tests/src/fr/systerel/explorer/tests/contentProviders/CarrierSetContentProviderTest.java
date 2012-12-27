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

import org.eventb.core.ICarrierSet;
import org.eventb.core.IContextRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.CarrierSetContentProvider;

/**
 * 
 *
 */
public class CarrierSetContentProviderTest extends ExplorerTest {

	private static CarrierSetContentProvider contentProvider;
	protected static IContextRoot c0;
	protected static IElementNode node;
	protected static ICarrierSet cset1;
	protected static ICarrierSet cset2;
	protected static ICarrierSet cset3;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new CarrierSetContentProvider();
		
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		ModelController.processProject(rodinProject);
		
		node = ModelController.getContext(c0).carrierset_node;
		assertNotNull("the node should be created successfully ", node);

		cset1 = createCarrierSet(c0, "cset1");
		cset2 = createCarrierSet(c0, "cset2");
		cset3 = createCarrierSet(c0, "cset3");
		assertArray(c0.getCarrierSets(), cset1, cset2, cset3);
		
	}
	
	@Test
	public void getChildren() throws RodinDBException {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(c0), node);
		//get the carrier sets for the node input
		assertArray(contentProvider.getChildren(node), cset1, cset2, cset3);
	}

	@Test
	public void getParent() throws RodinDBException {
		// get the parent of the carrier sets
		assertEquals(contentProvider.getParent(cset1),  node);
		// get the parent of the intermediary node
		assertEquals(contentProvider.getParent(node),  c0);
	}

	@Test
	public void hasChildren() {
		//the intermediary node has 3 children (the 3 carrier sets)
		assertTrue(contentProvider.hasChildren(node));
		//the context has 1 child (the intermediary node)
		assertTrue(contentProvider.hasChildren(c0));
	}

	@Test
	public void getElements() {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(c0), node);
		//get the carrier sets for the node input
		assertArray(contentProvider.getChildren(node), cset1, cset2, cset3);
	}

}
