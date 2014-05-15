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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.basis.RodinTestRoot;

/**
 * Unit tests for the IRodinProject interface
 * 
 * @author Laurent Voisin
 */
public class TestRodinProject extends ModifyingResourceTests {

	private static final String PROJECT_NAME = "P";

	private static <T> void assertSetEquality(Set<T> expected, T[] actual) {
		Set<T> actualSet = new HashSet<T>(Arrays.asList(actual));
		assertEquals(expected, actualSet);
	}

	private IRodinProject rodinProject;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject(PROJECT_NAME);
	}

	@After
	public void tearDown() throws Exception {
		deleteProject(PROJECT_NAME);
		super.tearDown();
	}

	/**
	 * Ensures correctness of project name.
	 */
	@Test
	public void testProjectName() throws Exception {
		assertEquals(PROJECT_NAME, rodinProject.getElementName());
	}

	/**
	 * Ensures correctness of project type.
	 */
	@Test
	public void testProjectType() throws Exception {
		assertEquals(IRodinProject.ELEMENT_TYPE, rodinProject.getElementType());
	}

	/**
	 * Ensures correctness of underlying resource.
	 */
	@Test
	public void testProject() throws Exception {
		final IProject project = getWorkspaceRoot().getProject(PROJECT_NAME);
		assertEquals(project, rodinProject.getProject());
		assertEquals(project, rodinProject.getResource());
		assertEquals(project, rodinProject.getUnderlyingResource());
	}

	/**
	 * Ensures that non-Rodin files and folders are recognized as such, while
	 * Rodin files are not.
	 */
	@Test
	public void testNonRodinResources() throws Exception {
		final Set<IResource> expected = new HashSet<IResource>();
		expected.add(getFile("/P/.project"));
		expected.add(createFile("/P/foo", "foo"));
		expected.add(createFolder("/P/bar"));
		createRodinFile("/P/X.test");

		assertSetEquality(expected, rodinProject.getNonRodinResources());
	}

	/**
	 * Ensures that Rodin file handles are correctly built.
	 */
	@Test
	public void testRodinFile() throws Exception {
		IFile file = getFile("/P/X.test");
		assertEquals(file, rodinProject.getRodinFile("X.test").getResource());
	}

	/**
	 * Ensures that Rodin files contained in a project are correctly returned.
	 */
	@Test
	public void testRodinFiles() throws Exception {
		final Set<IRodinFile> expected = new HashSet<IRodinFile>();
		expected.add(createRodinFile("/P/X.test"));
		expected.add(createRodinFile("/P/Y.test"));
		createFile("/P/foo", "foo");
		createFolder("/P/bar");

		assertSetEquality(expected, rodinProject.getRodinFiles());
	}

	/**
	 * Ensures that file roots of a given type are correctly retrieved.
	 */
	@Test
	public void testRootElementsOfType() throws Exception {
		final Set<RodinTestRoot> expected = new HashSet<RodinTestRoot>();
		expected.add((RodinTestRoot) createRodinFile("/P/X.test").getRoot());
		expected.add((RodinTestRoot) createRodinFile("/P/Y.test").getRoot());
		createRodinFile("/P/A.test2");

		final IInternalElementType<RodinTestRoot> type = RodinTestRoot.ELEMENT_TYPE;
		assertSetEquality(expected, rodinProject.getRootElementsOfType(type));
	}

}
