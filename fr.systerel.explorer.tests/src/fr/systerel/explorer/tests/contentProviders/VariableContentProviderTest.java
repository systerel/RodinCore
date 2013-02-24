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

import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.VariableContentProvider;

/**
 * 
 *
 */
public class VariableContentProviderTest extends ExplorerTest {

	private static VariableContentProvider contentProvider;
	protected static IMachineRoot m0;
	protected static IElementNode node;
	protected static IVariable variable1;
	protected static IVariable variable2;
	protected static IVariable variable3;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new VariableContentProvider();
		
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);

		ModelController.processProject(rodinProject);
		
		node = ModelController.getMachine(m0).variable_node;
		assertNotNull("the node should be created successfully ", node);

		variable1 = createVariable(m0, "variable1");
		variable2 = createVariable(m0, "variable2");
		variable3 = createVariable(m0, "variable3");
		assertArray(m0.getVariables(), variable1, variable2, variable3);
		
	}
	
	@Test
	public void getChildren() throws RodinDBException {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(m0), node);
		//get the variables for the node input
		assertArray(contentProvider.getChildren(node), variable1, variable2, variable3);
	}

	@Test
	public void getParent() throws RodinDBException {
		// get the parent of the axioms
		assertEquals(contentProvider.getParent(variable1),  node);
		// get the parent of the intermediary node
		assertEquals(contentProvider.getParent(node),  m0);
	}

	@Test
	public void hasChildren() {
		//the intermediary node has 3 children (the 3 variables)
		assertEquals(contentProvider.hasChildren(node), true);
		//the context has 1 child (the intermediary node)
		assertEquals(contentProvider.hasChildren(m0), true);
	}

	@Test
	public void getElements() {
		// get the intermediary node for context input
		assertArray(contentProvider.getChildren(m0), node);
		//get the variables for the node input
		assertArray(contentProvider.getChildren(node), variable1, variable2, variable3);
	}

}
