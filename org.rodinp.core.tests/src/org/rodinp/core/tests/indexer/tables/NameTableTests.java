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
package org.rodinp.core.tests.indexer.tables;

import static org.junit.Assert.assertEquals;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertIsEmpty;
import static org.rodinp.core.tests.util.IndexTestsUtil.createNamedElement;
import static org.rodinp.core.tests.util.IndexTestsUtil.createRodinFile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.tables.NameTable;

public class NameTableTests extends IndexTests {

	private static final String BAD_DECLARATIONS = "bad declarations";

	private static final NameTable table = new NameTable();
	private static IRodinProject project;
	private static IRodinFile file;
	private static NamedElement element1;
	private static NamedElement element2;
	private static final String name1 = "name1";
	private static final String name2 = "name2";
	private static IDeclaration declN1E1;
	private static IDeclaration declN1E2;
	private static IDeclaration declN2E2;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file = createRodinFile(project, "nameTable.test");
		element1 = createNamedElement(file, "elt1");
		element2 = createNamedElement(file, "elt2");
		declN1E1 = new Declaration(element1, name1);
		declN1E2 = new Declaration(element2, name1);
		declN2E2 = new Declaration(element2, name2);
	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	@Test
	public void testPutGetOneElement() throws Exception {
		table.add(declN1E1);

		final Set<IDeclaration> expectedResult = Collections.singleton(declN1E1);
		final Set<IDeclaration> elements = table.getDeclarations(name1);

		assertEquals(BAD_DECLARATIONS, expectedResult, elements);
	}

	@Test
	public void testPutGetSeveralSameName() throws Exception {
		table.add(declN1E1);
		table.add(declN1E2);

		final Set<IDeclaration> expectedResult = new HashSet<IDeclaration>();
		Collections.addAll(expectedResult, declN1E1, declN1E2);
		final Set<IDeclaration> elements = table.getDeclarations(name1);

		assertEquals(BAD_DECLARATIONS, expectedResult, elements);
	}

	@Test
	public void testPutGetVariousNames() throws Exception {
		table.add(declN1E1);
		table.add(declN2E2);

		final Set<IDeclaration> expectedResult1 = Collections.singleton(declN1E1);
		final Set<IDeclaration> expectedResult2 = Collections.singleton(declN2E2);
		final Set<IDeclaration> elements1 = table.getDeclarations(name1);
		final Set<IDeclaration> elements2 = table.getDeclarations(name2);

		assertEquals(BAD_DECLARATIONS, expectedResult1, elements1);
		assertEquals(BAD_DECLARATIONS, expectedResult2, elements2);
	}

	@Test
	public void testRemove() throws Exception {
		table.add(declN1E1);
		table.add(declN1E2);
		table.remove(declN1E1);

		final Set<IDeclaration> expectedResult = Collections.singleton(declN1E2);
		final Set<IDeclaration> elements = table.getDeclarations(name1);

		assertEquals(BAD_DECLARATIONS, expectedResult, elements);
	}

	@Test
	public void testClear() throws Exception {
		table.add(declN1E1);
		table.add(declN1E2);

		table.clear();

		final Set<IDeclaration> elements = table.getDeclarations(name1);

		assertIsEmpty(elements);
	}

}
