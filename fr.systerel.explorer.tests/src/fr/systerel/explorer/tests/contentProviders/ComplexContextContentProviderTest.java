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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.navigator.contentProviders.complex.ComplexContextContentProvider;
import fr.systerel.explorer.tests.ExplorerTest;

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
		
		//create some contexts
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);
		c1 = createContext("c1");
		assertNotNull("c1 should be created successfully ", c1);
		c2 = createContext("c2");
		assertNotNull("c2 should be created successfully ", c2);
		c3 = createContext("c3");
		assertNotNull("c3 should be created successfully ", c3);
		
		//create dependencies between the contexts.
		createExtendsContextClause(c1, c0, "extend1");
		assertTrue(c1.getExtendsClause("extend1").exists());
		createExtendsContextClause(c2, c1, "extend2");
		assertTrue(c2.getExtendsClause("extend2").exists());
		createExtendsContextClause(c3, c0, "extend3");
		assertTrue(c3.getExtendsClause("extend3").exists());
		
		//create a machine
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);
		createSeesContextClause(m0, c0, "sees1");
		assertTrue(m0.getSeesClause("sees1").exists());
		
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
		assertEquals(contentProvider.getParent(c1), rodinProject);
		assertEquals(contentProvider.getParent(c2), rodinProject);

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
	
}
