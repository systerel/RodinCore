/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.tests.model.MementoTests.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
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
		IRodinElement restored = RodinCore.create(actual);
		assertEquals("Unexpected restored element", element, restored);
	}

	public void setUp() throws Exception {
		super.setUp();

		this.createRodinProject("P");
	}

	public void tearDown() throws Exception {
		this.deleteProject("P");
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
		IRodinElement element = RodinCore.create("/P/");
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
	 * Tests that a project with special chararcters in its name can be
	 * persisted and restored using its memento. (regression test for JDT bug
	 * 47815 Refactoring doesn't work with some project names [refactoring])
	 */
	public void testProjectMemento2() {
		IRodinProject project = getRodinProject("P |!#");
		assertMemento("/P \\|\\!\\#", project);
	}

	/**
	 * Tests that a bogus memento cannot be restored.
	 */
	public void testRestoreBogusMemento() {
		IRodinElement restored = RodinCore.create("bogus");
		assertNull("should not be able to restore a bogus memento", restored);
	}

	/**
	 * Tests that a memento containing an unknown internal type doesn't raise a
	 * NullPointerException.  Regression test for bug 1529854.
	 */
	public void testRestoreWrongInternalType() {
		String bogusType = "org.rodinp.core.tests.bogus";
		IRodinElement restored = RodinCore.create(
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
		IRodinElement restored = RodinCore.create(
				"/P/X.bogus"
		);
		assertNull("should not be able to restore a bogus memento", restored);
	}

	/**
	 * Tests that a named top-level internal element can be persisted and
	 * restored using its memento.
	 */
	public void testNamedTopMemento() {
		final String type = NamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");
		NamedElement ne = (NamedElement) rf.getInternalElement(type, "foo");
		assertMemento("/P/X.test|" + type + "#foo", ne);

		ne = (NamedElement) rf.getInternalElement(type, "foo", 3);
		assertMemento("/P/X.test|" + type + "#foo!3", ne);
	}

	/**
	 * Tests that an unnamed top-level internal element can be persisted and
	 * restored using its memento.
	 */
	public void testUnnamedTopMemento() {
		final String type = UnnamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");
		UnnamedElement ue = (UnnamedElement) rf.getInternalElement(type, "");
		assertMemento("/P/X.test|" + type + "#", ue);

		ue = (UnnamedElement) rf.getInternalElement(type, "", 2);
		assertMemento("/P/X.test|" + type + "#!2", ue);
	}

	/**
	 * Tests that a named non top-level internal element can be persisted and
	 * restored using its memento.
	 */
	public void testNamedNonTopMemento() {
		final String nType = NamedElement.ELEMENT_TYPE;
		final String uType = UnnamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");

		IInternalElement top = rf.getInternalElement(nType, "foo");
		String prefix = "/P/X.test|" + nType + "#foo";
		IInternalElement ne = top.getInternalElement(nType, "bar");
		assertMemento(prefix + "|" + nType + "#bar", ne);
		ne = top.getInternalElement(nType, "bar", 2);
		assertMemento(prefix + "|" + nType + "#bar!2", ne);
		
		top = rf.getInternalElement(uType, "");
		prefix = "/P/X.test|" + uType + "#";
		ne = top.getInternalElement(nType, "bar");
		assertMemento(prefix + "|" + nType + "#bar", ne);
		ne = top.getInternalElement(nType, "bar", 2);
		assertMemento(prefix + "|" + nType + "#bar!2", ne);
	}

	/**
	 * Tests that an unnamed non top-level internal element can be persisted and
	 * restored using its memento.
	 */
	public void testUnnamedNonTopMemento() {
		final String nType = NamedElement.ELEMENT_TYPE;
		final String uType = UnnamedElement.ELEMENT_TYPE;
		IRodinFile rf = getRodinFile("/P/X.test");

		IInternalElement top = rf.getInternalElement(nType, "foo");
		String prefix = "/P/X.test|" + nType + "#foo";
		IInternalElement ue = top.getInternalElement(uType, "");
		assertMemento(prefix + "|" + uType + "#", ue);
		ue = top.getInternalElement(uType, "", 2);
		assertMemento(prefix + "|" + uType + "#!2", ue);
		
		top = rf.getInternalElement(uType, "");
		prefix = "/P/X.test|" + uType + "#";
		ue = top.getInternalElement(uType, "");
		assertMemento(prefix + "|" + uType + "#", ue);
		ue = top.getInternalElement(uType, "", 2);
		assertMemento(prefix + "|" + uType + "#!2", ue);
	}
	
}
