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
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.AxiomContentProvider;
import fr.systerel.internal.explorer.navigator.contentProviders.InvariantContentProvider;

/**
 * 
 *
 */
public class TheoremContentProviderTest extends ExplorerTest {

	private static InvariantContentProvider invContentProvider;
	private static AxiomContentProvider axmContentProvider;
	protected static IContextRoot c0;
	protected static IMachineRoot m0;
	protected static IElementNode nodeInv;
	protected static IElementNode nodeAxm;
	protected static IAxiom theorem1;
	protected static IAxiom theorem2;
	protected static IAxiom theorem3;
	protected static IInvariant theorem4;
	protected static IInvariant theorem5;
	protected static IInvariant theorem6;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		invContentProvider = new InvariantContentProvider();
		axmContentProvider = new AxiomContentProvider();
		
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);
		
		ModelController.processProject(rodinProject);
		
		createTheoremNodes();

		createContextTheorems();

		createMachineTheorems();
		
	}

	
	@Test
	public void getChildrenContext() throws RodinDBException {
		// get the intermediary node for context input
		assertArray(axmContentProvider.getChildren(c0), nodeAxm);
		//get the theorems for the node input
		assertArray(axmContentProvider.getChildren(nodeAxm), theorem1, theorem2, theorem3);
	}

	@Test
	public void getChildrenMachine() throws RodinDBException {
		// get the intermediary node for machine input
		assertArray(invContentProvider.getChildren(m0), nodeInv);
		//get the theorem for the node input
		assertArray(invContentProvider.getChildren(nodeInv), theorem4, theorem5, theorem6);
	}
	
	@Test
	public void getParentContext() throws RodinDBException {
		ModelController.getContext(c0).processChildren();
		// get the parent of the theorems
		assertEquals(axmContentProvider.getParent(theorem1),  nodeAxm);
		// get the parent of the intermediary node
		assertEquals(axmContentProvider.getParent(nodeAxm),  c0);
	}

	@Test
	public void getParentMachine() throws RodinDBException {
		ModelController.getMachine(m0).processChildren();
		// get the parent of the theorems
		assertEquals(invContentProvider.getParent(theorem4),  nodeInv);
		// get the parent of the intermediary node
		assertEquals(invContentProvider.getParent(nodeInv),  m0);
	}
	
	@Test
	public void hasChildrenContext() {
		//the intermediary node has 3 children (the 3 theorems)
		assertTrue(axmContentProvider.hasChildren(nodeAxm));
		//the context has 1 child (the intermediary node)
		assertTrue(axmContentProvider.hasChildren(c0));
	}

	@Test
	public void hasChildrenMachine() {
		//the intermediary node has 3 children (the 3 theorems)
		assertTrue(invContentProvider.hasChildren(nodeInv));
		//the machine has 1 child (the intermediary node)
		assertTrue(invContentProvider.hasChildren(m0));
	}
	
	@Test
	public void getElementsContext() {
		// get the intermediary node for context input
		assertArray(axmContentProvider.getChildren(c0), nodeAxm);
		//get the theorems for the node input
		assertArray(axmContentProvider.getChildren(nodeAxm), theorem1, theorem2, theorem3);
	}

	@Test
	public void getElementsMachine() {
		// get the intermediary node for context input
		assertArray(invContentProvider.getChildren(m0), nodeInv);
		//get the theorems for the node input
		assertArray(invContentProvider.getChildren(nodeInv), theorem4, theorem5, theorem6);
	}
	
	private void createTheoremNodes() {
		nodeAxm = ModelController.getContext(c0).axiom_node;
		assertNotNull("the node should be created successfully ", nodeAxm);
		nodeInv = ModelController.getMachine(m0).invariant_node;
		assertNotNull("the node should be created successfully ", nodeInv);
	}

	private void createMachineTheorems() throws RodinDBException {
		theorem4 = createInvariantTheorem(m0, "theorem4");
		theorem5 = createInvariantTheorem(m0, "theorem5");
		theorem6 = createInvariantTheorem(m0, "theorem6");
		assertArray(m0.getInvariants(), theorem4, theorem5, theorem6);
	}

	private void createContextTheorems() throws RodinDBException {
		theorem1 = createAxiomTheorem(c0, "theorem1");
		theorem2 = createAxiomTheorem(c0, "theorem2");
		theorem3 = createAxiomTheorem(c0, "theorem3");
		assertArray(c0.getAxioms(), theorem1, theorem2, theorem3);
	}
	
}
