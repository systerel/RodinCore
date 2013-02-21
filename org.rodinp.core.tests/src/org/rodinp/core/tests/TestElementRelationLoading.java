/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Acceptance tests of the element relationship checking during file loading.
 * These checks verify the filtering of invalid element types in element
 * hierarchies.
 * 
 * @author Thomas Muller
 */
public class TestElementRelationLoading extends ModifyingResourceTests {

	public TestElementRelationLoading() {
		super(PLUGIN_ID + ".TestElementRelationLoading");
	}

	private IRodinProject rodinProject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("foo");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		rodinProject.getProject().delete(true, true, null);
		rodinProject.getRodinDB().close();
	}

	/**
	 * Ensures that a Rodin file with a correct hierarchy of elements is well
	 * constructed.
	 */
	public void testCreateNormalHierarchy() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<org.rodinp.core.tests.test>\n"
				+ "<org.rodinp.core.tests.namedElement name=\"'\"> \n"
				+ "<org.rodinp.core.tests.namedElement11 name=\"-\"/> \n"
				+ "</org.rodinp.core.tests.namedElement> \n"
				+ "<org.rodinp.core.tests.namedElement name=\"(\" /> \n"
				+ "</org.rodinp.core.tests.test>\n";
		final TypeTreeShape expectedShape = s("test", //
				s("namedElement", s("namedElement11")), s("namedElement") //
		);
		assertSameHierarchy("toto.test", contents, expectedShape);
	}

	/**
	 * Ensures that another kind of Rodin file with a correct hierarchy of
	 * elements is well constructed.
	 */
	public void testCreateNormalHierarchy2() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<org.rodinp.core.tests.test2>\n"
				+ "<org.rodinp.core.tests.namedElement2 name=\"'\"/> \n"
				+ "<org.rodinp.core.tests.namedElement2 name=\"(\"> \n"
				+ "<org.rodinp.core.tests.namedElement22 name=\"-\"/> \n"
				+ "</org.rodinp.core.tests.namedElement2> \n"
				+ "</org.rodinp.core.tests.test2>\n";
		final TypeTreeShape expectedShape = s("test2", //
				s("namedElement2"), s("namedElement2", s("namedElement22")) //
		);
		assertSameHierarchy("toto.test2", contents, expectedShape);
	}

	/**
	 * Ensures that a Rodin file with a invalid child for a given root element
	 * is well constructed : "namedElement" can not be a valid child type of
	 * "test" root element.
	 */
	public void testCreateAbNormalRootChild() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<org.rodinp.core.tests.test>\n"
				+ "<org.rodinp.core.tests.namedElement2 />"
				+ " </org.rodinp.core.tests.test>\n";
		final TypeTreeShape expectedShape = s("test", s("namedElement2"));
		assertSameHierarchy("toto.test", contents, expectedShape);
	}

	/**
	 * Ensures that a Rodin file with an invalid child for a given element is
	 * well constructed : "namedElement22" can not be a valid child type of
	 * "namedElement" root element.
	 */
	public void testCreateAbNormalElementChild() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<org.rodinp.core.tests.test2>\n"
				+ "<org.rodinp.core.tests.namedElement name=\"'\">"
				+ "<org.rodinp.core.tests.namedElement2 name=\"(\" /> \n"
				+ "</org.rodinp.core.tests.namedElement>"
				+ " </org.rodinp.core.tests.test2>\n";
		final TypeTreeShape expectedShape = s("test2", //
				s("namedElement", //
						s("namedElement2")));
		assertSameHierarchy("toto.test2", contents, expectedShape);
	}
	
	private static TypeTreeShape s(String id, TypeTreeShape... children) {
		return new TypeTreeShape(id, children);
	}

	private static TypeTreeShape s(String id) {
		return new TypeTreeShape(id);
	}

	private static class TypeTreeShape {

		private final IInternalElementType<?> elementType;
		private TypeTreeShape[] children;

		public TypeTreeShape(String elementTypeName) {
			this.elementType = RodinCore.getInternalElementType(PLUGIN_ID + "."
					+ elementTypeName);
		}

		public TypeTreeShape(String elementTypeName, TypeTreeShape... children) {
			this(elementTypeName);
			this.children = children;
		}

		public void assertSameTree(IInternalElement element)
				throws RodinDBException {
			assertEquals(elementType, element.getElementType());
			final IRodinElement[] eChildren = element.getChildren();
			if (children != null) {
				assertEquals("Shape's children size is different "
						+ "from internal element's children size : "
						+ element.getElementType().toString(), children.length,
						eChildren.length);
				for (int i = 0; i < children.length; i++) {
					children[i].assertSameTree((IInternalElement) eChildren[i]);
				}
			}
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			this.toString(sb, 1);
			return sb.toString();
		}

		public void toString(StringBuilder sb, int level) {
			sb.append(getLevelChars(level));
			sb.append(elementType.toString());
			sb.append("\n");
			level++;
			if (children != null) {
				for (TypeTreeShape c : children) {
					c.toString(sb, level);
				}
			}
		}

		private String getLevelChars(int level) {
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < level; i++) {
				sb.append("-");
			}
			return sb.toString();
		}

	}

	final void assertSameHierarchy(String filename, String fileContents,
			TypeTreeShape expectedShape) throws Exception {
		// Create one Rodin file handle
		final IRodinFile rodinFile = rodinProject.getRodinFile(filename);
		assertNotExists("File should not exist", rodinFile);
		// Actually create the file
		final IFile file = rodinFile.getResource();
		final byte[] bytes = fileContents.getBytes("UTF-8");
		file.create(new ByteArrayInputStream(bytes), true, null);
		assertExists("File should exist", rodinFile);
		final IInternalElement root = rodinFile.getRoot();
		expectedShape.assertSameTree(root);
		// Then delete the file
		file.delete(true, null);
	}

}
