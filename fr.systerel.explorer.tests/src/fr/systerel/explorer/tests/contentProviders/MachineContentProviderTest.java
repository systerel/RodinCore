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
import static org.junit.Assert.assertTrue;

import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.navigator.contentProviders.MachineContentProvider;

/**
 * 
 *
 */
public class MachineContentProviderTest extends ExplorerTest {

	private static MachineContentProvider contentProvider;
	protected static IMachineRoot m0;
	protected static IMachineRoot m1;
	protected static IMachineRoot m2;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new MachineContentProvider();
		
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);
		m1 = createMachine("m1");
		assertNotNull("m1 should be created successfully ", m1);
		m2 = createMachine("m2");
		assertNotNull("m2 should be created successfully ", m2);
		
		
	}
	
	@Test
	public void getChildren() throws RodinDBException {
		Object[] children = contentProvider.getChildren(rodinProject.getProject());
		assertArray(children, m0, m1, m2);
		assertProcessed(rodinProject);
	}

	@Test
	public void getParent() throws RodinDBException {
		assertEquals(contentProvider.getParent(m0), rodinProject);
		assertEquals(contentProvider.getParent(m1), rodinProject);
		assertEquals(contentProvider.getParent(m2), rodinProject);
	}

	@Test
	public void hasChildren() {
		assertTrue(contentProvider.hasChildren(rodinProject.getProject()));
	}

	@Test
	public void getElements() {
		Object[] elements = contentProvider.getElements(rodinProject.getProject());
		assertArray(elements, m0, m1, m2);
		assertProcessed(rodinProject);
	}

}
