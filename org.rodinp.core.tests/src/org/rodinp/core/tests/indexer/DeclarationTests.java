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
package org.rodinp.core.tests.indexer;

import static org.rodinp.core.tests.util.IndexTestsUtil.*;

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

	public DeclarationTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "desc.test");
		elt1 = createNamedElement(file, "internalName1");
		elt2 = createNamedElement(file, "internalName2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testGetElement() {
		final IDeclaration declaration = new Declaration(elt1, eltName1);
		
		final IInternalElement element = declaration.getElement();
		
		assertEquals("bad element in declaration", elt1, element);
	}

	public void testGetName() {
		final IDeclaration declaration = new Declaration(elt1, eltName1);
		
		final String name = declaration.getName();
		
		assertEquals("bad name in declaration", eltName1, name);
	}
	
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
	
	public void testEquals() throws Exception {
		final IDeclaration decl1 = new Declaration(elt1, eltName1);
		final IDeclaration decl2 = new Declaration(elt1, eltName1);
		
		assertEquals(decl1, decl2);
	}

}
