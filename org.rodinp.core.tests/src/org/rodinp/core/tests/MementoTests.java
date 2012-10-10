/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.core.tests.model.MementoTests
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - removed occurrence count
 *     Systerel - separation of file and root element
 *     Systerel - added tests for internal elements named "|"
 *******************************************************************************/
package org.rodinp.core.tests;

import org.eclipse.core.runtime.IPath;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.RodinTestRoot;
import org.rodinp.core.tests.util.Util;

public class MementoTests extends ModifyingResourceTests {
	public MementoTests(String name) {
		super(name);
	}

	protected static void assertMemento(String expected, IRodinElement element) {
		String actual = element.getHandleIdentifier();
		if (!expected.equals(actual)) {
			System.out.print(Util.displayString(actual, 2));
			System.out.println(",");
		}
		assertEquals("Unexpected memento for " + element, expected, actual);
		IRodinElement restored = RodinCore.valueOf(actual);
		assertEquals("Unexpected restored element", element, restored);
	}

	private static String expectedRootMemento(IInternalElement root) {
		final IRodinFile rf = (IRodinFile) root.getParent();
		
		final IPath filePath = rf.getPath();
		String rootName = filePath.removeFileExtension().lastSegment();
		return filePath + "|" + root.getElementType().getId() + "#" + rootName;
	}

	public void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
	}

	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}


	/**
	 * Tests that a Rodin file can be persisted and restored using its
	 * memento.
	 */
	public void testRodinFileMemento() {
		IRodinFile rf = getRodinFile("/P/X.test");
		assertMemento("/P/X.test", rf);

		rf = getRodinFile("/P/Y.test");
		assertMemento("/P/Y.test", rf);
	}


	/**
	 * Ensures that a Rodin element is returned for an invalid memento.
	 * (regression test for JDT bug 81762 [model] AIOOB in breakpoints view)
	 */
	public void testInvalidMemento() {
		IRodinElement element = RodinCore.valueOf("/P/");
		assertElementEquals("Unexpected element", "P", element);
	}


	/**
	 * Tests that a project can be persisted and restored using its memento.
	 */
	public void testProjectMemento() {
		IRodinProject project = getRodinProject("P");
		assertMemento("/P", project);
	}

	/**
	 * Tests that a project with special chararacters in its name can be
	 * persisted and restored using its memento. (regression test for JDT bug
	 * 47815 Refactoring doesn't work with some project names [refactoring])
	 */
	public void testProjectMemento2() {
		IRodinProject project = getRodinProject("P |#");
		assertMemento("/P \\|\\#", project);
	}

	/**
	 * Tests that a bogus memento cannot be restored.
	 */
	public void testRestoreBogusMemento() {
		IRodinElement restored = RodinCore.valueOf("bogus");
		assertNull("should not be able to restore a bogus memento", restored);
	}

	/**
	 * Tests that a memento containing an unknown internal type doesn't raise a
	 * NullPointerException.  Regression test for bug 1529854.
	 */
	public void testRestoreWrongInternalType() {
		String bogusType = PLUGIN_ID + ".bogus";
		IRodinElement restored = RodinCore.valueOf(
				"/P/X.test|"
				+ bogusType
				+ "#foo"
		);
		assertNull("should not be able to restore a bogus memento", restored);
	}

	/**
	 * Tests that a memento containing an unknown Rodin file type doesn't raise a
	 * NullPointerException.  Regression test for bug 1529854.
	 */
	public void testRestoreWrongFileType() {
		IRodinElement restored = RodinCore.valueOf(
				"/P/X.bogus"
		);
		assertNull("should not be able to restore a bogus memento", restored);
	}

	/**
	 * Tests that a top-level internal element can be persisted and
	 * restored using its memento.
	 */
	public void testRootMemento() {
		IRodinFile rf = getRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		assertMemento(expectedRootMemento(root), root);
	}

	/**
	 * Tests that a top-level internal element can be persisted and
	 * restored using its memento.
	 */
	public void testTopMemento() {
		final IInternalElementType<NamedElement> type = NamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		NamedElement ne = root.getInternalElement(type, "foo");
		assertMemento(expectedRootMemento(root) + "|" + type.getId() + "#foo", ne);
		
		// Element with empty name
		ne = root.getInternalElement(type, "");
		assertMemento(expectedRootMemento(root) + "|" + type.getId() + "#", ne);
	}

	/**
	 * Tests that a top-level internal element named "|" can be persisted and
	 * restored using its memento.
	 * Refers to bug #2961115
	 */
	public void testTopMementoPipe() {
		final IInternalElementType<NamedElement> type = NamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		
		// Root is named "|"
		NamedElement ne = root.getInternalElement(type, "|");
		final String prefix = expectedRootMemento(root) + "|" + type.getId();
		assertMemento(prefix + "#\\|", ne);
		
		// Root is named "||"
		ne = root.getInternalElement(type, "||");
		assertMemento(prefix + "#\\|\\|", ne);
		
		// Root is named "\|"
		ne = root.getInternalElement(type, "\\|");
		assertMemento(prefix + "#\\\\\\|", ne);
		
		// Root is named "\#|"
		ne = root.getInternalElement(type, "\\#|");
		assertMemento(prefix + "#\\\\\\#\\|", ne);
	}

	/**
	 * Tests that a top-level internal element named "#" can be persisted and
	 * restored using its memento.
	 */
	public void testMementoHash() {
		final IInternalElementType<NamedElement> type = NamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		NamedElement ne = root.getInternalElement(type, "#");
		String expectedNEMemento = expectedRootMemento(root) + "|"
				+ type.getId() + "#\\#";
		assertMemento(expectedNEMemento, ne);

		NamedElement ne2 = ne.getInternalElement(type, "bar");
		assertMemento(expectedNEMemento + "|" + type.getId() + "#bar", ne2);
	}

	/**
	 * Tests that a non top-level internal element can be persisted and
	 * restored using its memento.
	 */
	public void testNonTopMemento() {
		final IInternalElementType<NamedElement> nType = NamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");

		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = root.getInternalElement(nType, "foo");
		String prefix = expectedRootMemento(root) + "|" + nType.getId() + "#foo";
		IInternalElement ne = top.getInternalElement(nType, "bar");
		assertMemento(prefix + "|" + nType.getId() + "#bar", ne);
		
		
		// Element with empty name
		ne = top.getInternalElement(nType, "");
		assertMemento(prefix + "|" + nType.getId() + "#", ne);

		// Top with empty name
		top = root.getInternalElement(nType, "");
		prefix = expectedRootMemento(root) + "|" + nType.getId() + "#";
		ne = top.getInternalElement(nType, "bar");
		assertMemento(prefix + "|" + nType.getId() + "#bar", ne);

		// Top and child with empty name
		ne = top.getInternalElement(nType, "");
		assertMemento(prefix + "|" + nType.getId() + "#", ne);
	}
	
	/**
	 * Tests that a non top-level internal element named "|" can be persisted
	 * and restored using its memento.
	 * Refers to bug #2961115
	 */
	public void testNonTopMementoPipe() {
		final IInternalElementType<NamedElement> nType = NamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");

		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = root.getInternalElement(nType, "foo");
		String prefix = expectedRootMemento(root) + "|" + nType.getId() + "#foo";
		IInternalElement ne = top.getInternalElement(nType, "|");
		assertMemento(prefix + "|" + nType.getId() + "#\\|", ne);

		// Top with empty name, child named "|"
		top = root.getInternalElement(nType, "");
		prefix = expectedRootMemento(root) + "|" + nType.getId() + "#";
		ne = top.getInternalElement(nType, "|");
		assertMemento(prefix + "|" + nType.getId() + "#\\|", ne);

		// Top empty name, child  "\#|"
		ne = top.getInternalElement(nType, "\\#|");
		assertMemento(prefix + "|" + nType.getId() + "#\\\\\\#\\|", ne);
	}

	public void testNullMemento() {
		assertNull(RodinCore.valueOf((String) null));
	}
	
	public void testPartialMemento() {
		final String typeId = NamedElement.ELEMENT_TYPE.getId();
		final IRodinFile rf = getRodinFile("/P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final String prefix = expectedRootMemento(root);
		assertEquals(RodinCore.valueOf(prefix + "|"), root);
		assertEquals(RodinCore.valueOf(prefix + "/"), root);
		assertEquals(RodinCore.valueOf(prefix + "|" + typeId), root);
		assertEquals(RodinCore.valueOf(prefix + "|" + typeId + "|"), root);
	}

}
