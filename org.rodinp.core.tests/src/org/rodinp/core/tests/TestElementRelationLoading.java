/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * Acceptance tests of the element relationship checking during file loading.
 * These checks verify the filtering of invalid element types in element
 * hierarchies.
 * 
 * @author Thomas Muller
 */
public class TestElementRelationLoading extends ModifyingResourceTests {

	private static final String PROJECT_NAME = "P";

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
	 * Ensures that a Rodin file with a correct hierarchy of elements is well
	 * constructed.
	 */
	@Test
	public void testCreateNormalHierarchy() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<org.rodinp.core.tests.test>\n"
				+ "<org.rodinp.core.tests.namedElement name=\"1\"> \n"
				+ "<org.rodinp.core.tests.namedElement name=\"11\"/> \n"
				+ "<org.rodinp.core.tests.namedElement name=\"12\"/> \n"
				+ "</org.rodinp.core.tests.namedElement> \n"
				+ "<org.rodinp.core.tests.namedElement2 name=\"2\" /> \n"
				+ "</org.rodinp.core.tests.test>\n";
		final TypeTreeShape expectedShape = s("test", //
				s("namedElement", s("namedElement"), s("namedElement")),
				s("namedElement2") //
		);
		assertSameHierarchy(expectedShape, "toto.test", contents);
	}

	/**
	 * Ensures that an invalid child for a root element is ignored.
	 */
	@Test
	public void testCreateAbnormalRootChild() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<org.rodinp.core.tests.test2>\n"
				+ "<org.rodinp.core.tests.namedElement />"
				+ " </org.rodinp.core.tests.test2>\n";
		final TypeTreeShape expectedShape = s("test2");
		assertSameHierarchy(expectedShape, "toto.test2", contents);
	}

	/**
	 * Ensures that an invalid child for a non root element is ignored.
	 */
	@Test
	public void testCreateAbnormalElementChild() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<org.rodinp.core.tests.test2>\n"
				+ "<org.rodinp.core.tests.namedElement2 name=\"1\">"
				+ "<org.rodinp.core.tests.namedElement name=\"11\" /> \n"
				+ "</org.rodinp.core.tests.namedElement2>"
				+ " </org.rodinp.core.tests.test2>\n";
		final TypeTreeShape expectedShape = s("test2", s("namedElement2"));
		assertSameHierarchy(expectedShape, "toto.test2", contents);
	}

	private void assertSameHierarchy(TypeTreeShape expectedShape,
			String filename, String fileContents) throws Exception {
		// Check that the file does not exist
		final IRodinFile rodinFile = rodinProject.getRodinFile(filename);
		assertNotExists("File should not exist", rodinFile);

		// Actually create the file with the given contents
		createFile("/P/" + filename, fileContents);
		assertExists("File should exist", rodinFile);

		expectedShape.assertSameTree(rodinFile.getRoot());
	}

	private static TypeTreeShape s(String id, TypeTreeShape... children) {
		return TypeTreeShape.s(PLUGIN_ID, id, children);
	}

}
