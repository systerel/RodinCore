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

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.core.tests.util.IndexTestsUtil;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.tables.NameTable;

public class NameTableTests extends IndexTests {

	public NameTableTests(String name) {
		super(name);
	}

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file = createRodinFile(project, "nameTable.test");
		element1 = createNamedElement(file, "elt1");
		element2 = createNamedElement(file, "elt2");
		declN1E1 = new Declaration(element1, name1);
		declN1E2 = new Declaration(element2, name1);
		declN2E2 = new Declaration(element2, name2);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testPutGetOneElement() throws Exception {
		table.add(declN1E1);

		final IDeclaration[] expectedResult = makeArray(declN1E1);
		final IDeclaration[] elements = table.getDeclarations(name1);

		IndexTestsUtil.assertSameElements(expectedResult, elements, "declarations");
	}

	public void testPutGetSeveralSameName() throws Exception {
		table.add(declN1E1);
		table.add(declN1E2);

		final IDeclaration[] expectedResult = makeArray(declN1E1,
				declN1E2);
		final IDeclaration[] elements = table.getDeclarations(name1);

		assertSameElements(expectedResult, elements, "declarations");
	}

	public void testPutGetVariousNames() throws Exception {
		table.add(declN1E1);
		table.add(declN2E2);

		final IDeclaration[] expectedResult1 = makeArray(declN1E1);
		final IDeclaration[] expectedResult2 = makeArray(declN2E2);
		final IDeclaration[] elements1 = table.getDeclarations(name1);
		final IDeclaration[] elements2 = table.getDeclarations(name2);

		assertSameElements(expectedResult1, elements1, "declarations");
		assertSameElements(expectedResult2, elements2, "declarations");
	}

	public void testRemove() throws Exception {
		table.add(declN1E1);
		table.add(declN1E2);
		table.remove(declN1E1);

		final IDeclaration[] expectedResult = makeArray(declN1E2);
		final IDeclaration[] elements = table.getDeclarations(name1);

		assertSameElements(expectedResult, elements, "declarations");
	}

	public void testClear() throws Exception {
		table.add(declN1E1);
		table.add(declN1E2);

		table.clear();

		final IDeclaration[] elements = table.getDeclarations(name1);

		assertIsEmpty(elements);
	}

}
