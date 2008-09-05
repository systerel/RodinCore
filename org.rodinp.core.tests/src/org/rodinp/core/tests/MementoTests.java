/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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
 *******************************************************************************/
package org.rodinp.core.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.util.Util;

public class MementoTests extends ModifyingResourceTests {
	public MementoTests(String name) {
		super(name);
	}

	protected void assertMemento(String expected, IRodinElement element) {
		String actual = element.getHandleIdentifier();
		if (!expected.equals(actual)) {
			System.out.print(Util.displayString(actual, 2));
			System.out.println(",");
		}
		assertEquals("Unexpected memento for " + element, expected, actual);
		IRodinElement restored = RodinCore.valueOf(actual);
		assertEquals("Unexpected restored element", element, restored);
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
		String bogusType = "org.rodinp.core.tests.bogus";
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
	public void testTopMemento() {
		final IInternalElementType<NamedElement> type = NamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");
		NamedElement ne = rf.getInternalElement(type, "foo");
		assertMemento("/P/X.test|" + type.getId() + "#foo", ne);
		
		// Element with empty name
		ne = rf.getInternalElement(type, "");
		assertMemento("/P/X.test|" + type.getId() + "#", ne);
	}

	/**
	 * Tests that a non top-level internal element can be persisted and
	 * restored using its memento.
	 */
	public void testNonTopMemento() {
		final IInternalElementType<NamedElement> nType = NamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");

		IInternalElement top = rf.getInternalElement(nType, "foo");
		String prefix = "/P/X.test|" + nType.getId() + "#foo";
		IInternalElement ne = top.getInternalElement(nType, "bar");
		assertMemento(prefix + "|" + nType.getId() + "#bar", ne);
		
		
		// Element with empty name
		ne = (NamedElement) top.getInternalElement(nType, "");
		assertMemento(prefix + "|" + nType.getId() + "#", ne);

		// Top with empty name
		top = rf.getInternalElement(nType, "");
		prefix = "/P/X.test|" + nType.getId() + "#";
		ne = top.getInternalElement(nType, "bar");
		assertMemento(prefix + "|" + nType.getId() + "#bar", ne);

		// Top and child with empty name
		ne = top.getInternalElement(nType, "");
		assertMemento(prefix + "|" + nType.getId() + "#", ne);
	}

}
