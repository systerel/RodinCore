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
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tests.IndexTests;

public class NameTableTests extends IndexTests {

	public NameTableTests(String name) {
		super(name, true);
	}

	private static final NameTable table = new NameTable();
	private static IRodinProject project;
	private static IRodinFile file;
	private static NamedElement element1;
	private static NamedElement element2;
	private static final String name1 = "name1";
	private static final String name2 = "name2";

	@Override
	protected void setUp() throws Exception {
		project = createRodinProject("P");
		file = createRodinFile(project, "nameTable.test");
		element1 = createNamedElement(file, "elt1");
		element2 = createNamedElement(file, "elt2");

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testPutGetOneElement() throws Exception {
		table.put(name1, element1);

		final IInternalElement[] expectedResult = makeIIEArray(element1);
		final IInternalElement[] elements = table.getElements(name1);

		assertSameElements(expectedResult, elements);
	}

	public void testPutGetSeveralSameName() throws Exception {
		table.put(name1, element1);
		table.put(name1, element2);

		final IInternalElement[] expectedResult = makeIIEArray(element1,
				element2);
		final IInternalElement[] elements = table.getElements(name1);

		assertSameElements(expectedResult, elements);
	}

	public void testPutGetVariousNames() throws Exception {
		table.put(name1, element1);
		table.put(name2, element2);

		final IInternalElement[] expectedResult1 = makeIIEArray(element1);
		final IInternalElement[] expectedResult2 = makeIIEArray(element2);
		final IInternalElement[] elements1 = table.getElements(name1);
		final IInternalElement[] elements2 = table.getElements(name2);

		assertSameElements(expectedResult1, elements1);
		assertSameElements(expectedResult2, elements2);
	}

	public void testRemove() throws Exception {
		table.put(name1, element1);
		table.put(name1, element2);

		table.remove(name1, element1);

		final IInternalElement[] expectedResult = makeIIEArray(element2);
		final IInternalElement[] elements = table.getElements(name1);

		assertSameElements(expectedResult, elements);
	}

	public void testClear() throws Exception {
		table.put(name1, element1);
		table.put(name1, element2);

		table.clear();

		final IInternalElement[] elements = table.getElements(name1);

		assertIsEmpty(elements);
	}

}
