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
package org.rodinp.core.tests.indexer.tables;

import static org.rodinp.core.tests.util.IndexTestsUtil.*;

import java.util.Collections;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.tables.FileTable;

public class FileTableTests extends IndexTests {

	private static final String BAD_ELEMENTS = "bad elements in file table";
	private static final FileTable table = new FileTable();
	private static final String nameElement = "nameElement";
	private static final String nameElement2 = "nameElement2";
	private static IDeclaration declElement;
	private static IDeclaration declElement2;
	private static IRodinFile file;
	private static IRodinFile file2;

	public FileTableTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "filetable.test");
		file2 = createRodinFile(rodinProject, "filetable2.test");
		final NamedElement element = createNamedElement(file, "elem");
		final NamedElement element2 = createNamedElement(file2, "elem2");
		declElement = new Declaration(element, nameElement);
		declElement2 = new Declaration(element2, nameElement2);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testGetElementsPresent() throws Exception {
		table.add(file, declElement);
		final Set<IDeclaration> expectedResult = Collections.singleton(declElement);

		final Set<IDeclaration> elements = table.get(file);

		assertEquals(BAD_ELEMENTS, expectedResult, elements);
	}

	public void testGetElementsFileAbsent() throws Exception {
		table.add(file, declElement);

		final Set<IDeclaration> elements = table.get(file2);

		assertIsEmpty(elements);
	}

	public void testAddElement() throws Exception {
		table.add(file, declElement);

		final Set<IDeclaration> expectedResult = Collections.singleton(declElement);
		final Set<IDeclaration> elements = table.get(file);

		assertEquals(BAD_ELEMENTS, expectedResult, elements);
	}

	public void testImportedElement() throws Exception {
		table.add(file2, declElement);
	}

	public void testRemoveElements() throws Exception {
		table.add(file, declElement);
		table.add(file2, declElement2);
		table.remove(file);

		final Set<IDeclaration> elements = table.get(file);
		final Set<IDeclaration> expectedResult2 = Collections.singleton(declElement2);
		final Set<IDeclaration> elements2 = table.get(file2);

		assertIsEmpty(elements);
		assertEquals(BAD_ELEMENTS, expectedResult2, elements2);
	}

	public void testRemoveElementsFileAbsent() throws Exception {
		table.add(file, declElement);
		table.remove(file2);

		final Set<IDeclaration> expectedResult = Collections.singleton(declElement);
		final Set<IDeclaration> elements = table.get(file);
		final Set<IDeclaration> elements2 = table.get(file2);

		assertEquals(BAD_ELEMENTS, expectedResult, elements);
		assertIsEmpty(elements2);
	}

	public void testClear() throws Exception {
		table.add(file, declElement);
		table.add(file2, declElement2);
		table.clear();

		final Set<IDeclaration> elements = table.get(file);
		final Set<IDeclaration> elements2 = table.get(file2);

		assertIsEmpty(elements);
		assertIsEmpty(elements2);
	}

	public void testContains() throws Exception {
		table.add(file, declElement);

		final boolean contains = table.contains(file, declElement);

		assertTrue("FileTable should contain " + declElement, contains);
	}

	public void testContainsNot() throws Exception {
		final boolean contains = table.contains(file, declElement);

		assertFalse("FileTable should not contain " + declElement, contains);

	}
}
