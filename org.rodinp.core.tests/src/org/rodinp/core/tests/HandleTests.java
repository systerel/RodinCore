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
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.junit.Test;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.tests.basis.RodinTestRoot;

/**
 * @author Laurent Voisin
 * 
 */
public class HandleTests extends AbstractRodinDBTests {

	public static void assertNameParent(IRodinElement element,
			String expectedName, IRodinElement expectedParent) {
		assertEquals(expectedName, element.getElementName());
		if (expectedParent == null) {
			assertNull(element.getParent());
		} else {
			assertEquals(expectedParent, element.getParent());
		}
	}

	public static void assertNameTypeParent(IRodinElement element,
			String expectedName, IElementType<?> expectedType,
			IRodinElement expectedParent) {
		assertNameParent(element, expectedName, expectedParent);
		assertEquals(expectedType, element.getElementType());
	}

	public static void assertNameRootTypeParent(IRodinFile file,
			String expectedName, IElementType<?> expectedType,
			IRodinElement expectedParent) {
		assertNameParent(file, expectedName, expectedParent);
		assertEquals(expectedType, file.getRootElementType());
	}

	@Test
	public void testDBHandle() throws Exception {
		final IRodinDB rodinDB = getRodinDB();
		assertNameTypeParent(rodinDB, "", IRodinDB.ELEMENT_TYPE, null);
		assertNull(RodinCore.valueOf((IWorkspaceRoot) null));
	}

	@Test
	public void testProjectHandle() throws Exception {
		final String projectName = "P";
		final IRodinDB rodinDB = getRodinDB();
		final IRodinProject rodinProject = getRodinProject(projectName);
		assertNameTypeParent(rodinProject, projectName,
				IRodinProject.ELEMENT_TYPE, rodinDB);
		assertNull(RodinCore.valueOf((IProject) null));
	}

	@Test
	public void testFileHandle() throws Exception {
		final String fileName = "foo.test";
		final IRodinProject rodinProject = getRodinProject("P");
		final IRodinFile rodinFile = rodinProject.getRodinFile(fileName);
		assertNameRootTypeParent(rodinFile, fileName, RodinTestRoot.ELEMENT_TYPE,
				rodinProject);
		assertNull(RodinCore.valueOf((IFile) null));
	}

	@Test
	public void testResourceHandle() throws Exception {
		assertNull(RodinCore.valueOf((IResource) null));		
	}

	@Test
	public void testRootHandle() throws Exception {
		final String filePath = "/P/foo.test";
		final String rootName = "foo";
		final IRodinFile rodinFile = getRodinFile(filePath);
		final RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		assertNameTypeParent(root, rootName, RodinTestRoot.ELEMENT_TYPE,
				rodinFile);
	}

}
