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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.complex.ComplexContextContentProvider;

/**
 * 
 *
 */
public class ComplexContextContentProviderTest extends ExplorerTest {

	private static ComplexContextContentProvider contentProvider;
	protected static IContextRoot c0;
	protected static IContextRoot c1;
	protected static IContextRoot c2;
	protected static IContextRoot c3;
	protected static IMachineRoot m0;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new ComplexContextContentProvider();
		
		createContexts();
		createContextDependencies();
		setUpMachine();
		
	}

	
	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		ModelController.removeProject(rodinProject);
		
	}
	
	
	@Test
	public void getChildrenProject() throws RodinDBException {
		//check the children of the project
		Object[] children = contentProvider.getChildren(rodinProject.getProject());
		assertArray(children, c0, c1, c2);
	}

	@Test
	public void getChildrenContext() throws RodinDBException {
		ModelController.processProject(rodinProject);
		//check the children of the context c0
		assertArray(contentProvider.getChildren(c0), c3);
	}

	@Test
	public void getChildrenMachine() throws RodinDBException {
		ModelController.processProject(rodinProject);
		//check the children of the machine m0
		assertArray(contentProvider.getChildren(m0), c0);
	}
	
	
	@Test
	public void getParent() throws RodinDBException {
		ModelController.processProject(rodinProject);

		assertEquals(contentProvider.getParent(c0), rodinProject);
		assertEquals(contentProvider.getParent(c1), c0);
		assertEquals(contentProvider.getParent(c2), c1);
		assertEquals(contentProvider.getParent(c3), c0);

	}

	@Test
	public void hasChildrenProject() {
		ModelController.processProject(rodinProject);
		assertTrue(contentProvider.hasChildren(rodinProject.getProject()));
	}

	@Test
	public void hasChildrenContext() {
		ModelController.processProject(rodinProject);
		assertTrue(contentProvider.hasChildren(c0));
		assertFalse(contentProvider.hasChildren(c1));
	}

	@Test
	public void hasChildrenMachine() {
		ModelController.processProject(rodinProject);
		assertTrue(contentProvider.hasChildren(m0));
	}
	
	@Test
	public void getElementsProject() {
		Object[] elements = contentProvider.getElements(rodinProject.getProject());
		assertArray(elements, c0, c1, c2);
	}

	@Test
	public void getElementsContext() {
		ModelController.processProject(rodinProject);
		assertArray(contentProvider.getElements(c0), c3);
	}

	@Test
	public void getElementsMachine() {
		ModelController.processProject(rodinProject);
		assertArray(contentProvider.getElements(m0), c0);
	}

	private void setUpMachine() throws RodinDBException {
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);
		createSeesContextClause(m0, c0);
	}

	private void createContextDependencies() throws RodinDBException {
		createExtendsContextClause(c1, c0);
		createExtendsContextClause(c2, c1);
		createExtendsContextClause(c3, c0);
	}

	private void createContexts() throws RodinDBException {
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);
		c1 = createContext("c1");
		assertNotNull("c1 should be created successfully ", c1);
		c2 = createContext("c2");
		assertNotNull("c2 should be created successfully ", c2);
		c3 = createContext("c3");
		assertNotNull("c3 should be created successfully ", c3);
	}
	
}
