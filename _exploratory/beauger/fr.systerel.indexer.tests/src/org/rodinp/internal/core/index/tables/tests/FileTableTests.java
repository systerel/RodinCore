/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertIsEmpty;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertSameElements;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.makeIIEArray;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tests.IndexTests;

public class FileTableTests extends IndexTests {

	private static final FileTable table = new FileTable();
	private static NamedElement element;
	private static NamedElement element2;
	private static IRodinFile file;
	private static IRodinFile file2;

	public FileTableTests(String name) {
		super(name, true);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "filetable.test");
		file2 = createRodinFile(rodinProject, "filetable2.test");
		element = createNamedElement(file, "elem");
		element2 = createNamedElement(file2, "elem2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testGetElementsPresent() throws Exception {
		table.add(file, element);
		final IInternalElement[] expectedResult = makeIIEArray(element);

		final IInternalElement[] elements = table.get(file);

		assertSameElements(expectedResult, elements);
	}

	public void testGetElementsFileAbsent() throws Exception {
		table.add(file, element);

		final IInternalElement[] elements = table.get(file2);

		assertIsEmpty(elements);
	}

	public void testAddElement() throws Exception {
		table.add(file, element);

		final IInternalElement[] expectedResult = makeIIEArray(element);
		final IInternalElement[] elements = table.get(file);

		assertSameElements(expectedResult, elements);
	}

	public void testImportedElement() throws Exception {
		table.add(file2, element);
	}

	public void testRemoveElements() throws Exception {
		table.add(file, element);
		table.add(file2, element2);
		table.remove(file);

		final IInternalElement[] elements = table.get(file);
		final IInternalElement[] expectedResult2 = makeIIEArray(element2);
		final IInternalElement[] elements2 = table.get(file2);

		assertIsEmpty(elements);
		assertSameElements(expectedResult2, elements2);
	}

	public void testRemoveElementsFileAbsent() throws Exception {
		table.add(file, element);
		table.remove(file2);

		final IInternalElement[] expectedResult = makeIIEArray(element);
		final IInternalElement[] elements = table.get(file);
		final IInternalElement[] elements2 = table.get(file2);

		assertSameElements(expectedResult, elements);
		assertIsEmpty(elements2);
	}

	public void testClear() throws Exception {
		table.add(file, element);
		table.add(file2, element2);
		table.clear();

		final IInternalElement[] elements = table.get(file);
		final IInternalElement[] elements2 = table.get(file2);

		assertIsEmpty(elements);
		assertIsEmpty(elements2);
	}

	public void testContains() throws Exception {
		table.add(file, element);

		final boolean contains = table.contains(file, element);

		assertTrue("FileTable should contain " + element, contains);
	}

	public void testContainsNot() throws Exception {
		final boolean contains = table.contains(file, element);

		assertFalse("FileTable should not contain " + element, contains);

	}
}
