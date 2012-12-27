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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IContextRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.navigator.contentProviders.ContextContentProvider;

/**
 * 
 *
 */
public class ContextContentProviderTest extends ExplorerTest {

	private static ContextContentProvider contentProvider;
	protected static IContextRoot c0;
	protected static IContextRoot c1;
	protected static IContextRoot c2;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new ContextContentProvider();
		
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);
		c1 = createContext("c1");
		assertNotNull("c1 should be created successfully ", c1);
		c2 = createContext("c2");
		assertNotNull("c2 should be created successfully ", c2);
		
		
		
	}
	
	@Test
	public void getChildren() throws RodinDBException {
		Object[] children = contentProvider.getChildren(rodinProject.getProject());
		assertArray(children, c0, c1, c2);
		assertProcessed(rodinProject);
	}

	@Test
	public void getParent() throws RodinDBException {
		assertEquals(contentProvider.getParent(c0), rodinProject);
		assertEquals(contentProvider.getParent(c1), rodinProject);
		assertEquals(contentProvider.getParent(c2), rodinProject);
	}

	@Test
	public void hasChildren() {
		assertTrue(contentProvider.hasChildren(rodinProject.getProject()));
	}

	@Test
	public void getElements() {
		Object[] elements = contentProvider.getElements(rodinProject.getProject());
		assertArray(elements, c0, c1, c2);
		assertProcessed(rodinProject);
	}

}
