/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.indexer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.rodinp.core.tests.util.IndexTestsUtil.createNamedElement;
import static org.rodinp.core.tests.util.IndexTestsUtil.createRodinFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.indexer.Declaration;

public class DeclarationTests extends IndexTests {

	private IRodinProject rodinProject;
	private IRodinFile file;
	private NamedElement elt1;
	private NamedElement elt2;

	private static final String eltName1 = "eltName1";
	private static final String eltName2 = "eltName2";

	@Before
	public void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "desc.test");
		elt1 = createNamedElement(file, "internalName1");
		elt2 = createNamedElement(file, "internalName2");
	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	@Test
	public void testGetElement() {
		final IDeclaration declaration = new Declaration(elt1, eltName1);
		
		final IInternalElement element = declaration.getElement();
		
		assertEquals("bad element in declaration", elt1, element);
	}

	@Test
	public void testGetName() {
		final IDeclaration declaration = new Declaration(elt1, eltName1);
		
		final String name = declaration.getName();
		
		assertEquals("bad name in declaration", eltName1, name);
	}
	
	@Test
	public void testDiffers() throws Exception {
		final IDeclaration decl11 = new Declaration(elt1, eltName1);
		final IDeclaration decl12 = new Declaration(elt1, eltName2);
		final IDeclaration decl21 = new Declaration(elt2, eltName1);
		final IDeclaration decl22 = new Declaration(elt2, eltName2);
		
		assertFalse(decl11.equals(decl12));
		assertFalse(decl11.equals(decl21));
		assertFalse(decl11.equals(decl22));
		assertFalse(decl12.equals(decl21));
		assertFalse(decl12.equals(decl22));
		assertFalse(decl21.equals(decl22));
	}
	
	@Test
	public void testEquals() throws Exception {
		final IDeclaration decl1 = new Declaration(elt1, eltName1);
		final IDeclaration decl2 = new Declaration(elt1, eltName1);
		
		assertEquals(decl1, decl2);
	}

}
