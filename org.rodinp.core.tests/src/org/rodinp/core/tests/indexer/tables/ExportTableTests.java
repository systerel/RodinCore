/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
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

import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.tables.ExportTable;

public class ExportTableTests extends IndexTests {


	public ExportTableTests(String name) {
		super(name);
	}

	private static final ExportTable table = new ExportTable();
	private static final Set<IDeclaration> emptyExport = new HashSet<IDeclaration>();
	private static NamedElement elt1F1;
	private static NamedElement elt2F1;
	private static NamedElement elt1F2;
	private static IDeclaration declElt1F1Name1F1;
	private static IDeclaration declElt2F1Name2F1;
	private static IDeclaration declElt1F2Name1F2;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static final String name1F1 = "name1F1";
	private static final String name2F1 = "name2F1";
	private static final String name1F2 = "name1F2";

	private void assertEmptyExports(Set<IDeclaration> map) {
		assertExports(emptyExport, map);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file1 = createRodinFile(rodinProject, "exp1.test");
		file2 = createRodinFile(rodinProject, "exp2.test");
		elt1F1 = createNamedElement(file1, "elem1F1");
		elt2F1 = createNamedElement(file1, "elem2F1");
		elt1F2 = createNamedElement(file2, "elem1F2");
		declElt1F1Name1F1 = new Declaration(elt1F1, name1F1);
		declElt2F1Name2F1 = new Declaration(elt2F1, name2F1);
		declElt1F2Name1F2 = new Declaration(elt1F2, name1F2);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testAddGet() throws Exception {
		Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(new Declaration(elt1F1, name1F1));

		table.add(file1, declElt1F1Name1F1);
		final Set<IDeclaration> actual = table.get(file1);

		assertExports(expected, actual);
	}

	public void testAddGetSeveral() throws Exception {
		Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(new Declaration(elt1F1, name1F1));
		expected.add(new Declaration(elt2F1, name2F1));

		table.add(file1, declElt1F1Name1F1);
		table.add(file1, declElt2F1Name2F1);
		final Set<IDeclaration> actual = table.get(file1);

		assertExports(expected, actual);
	}

	public void testAddGetVariousFiles() throws Exception {
		Set<IDeclaration> expected1 = new HashSet<IDeclaration>();
		expected1.add(new Declaration(elt1F1, name1F1));
		Set<IDeclaration> expected2 = new HashSet<IDeclaration>();
		expected2.add(new Declaration(elt1F2, name1F2));

		table.add(file1, declElt1F1Name1F1);
		table.add(file2, declElt1F2Name1F2);

		final Set<IDeclaration> actual1 = table.get(file1);
		final Set<IDeclaration> actual2 = table.get(file2);

		assertExports(expected1, actual1);
		assertExports(expected2, actual2);
	}

	public void testGetUnknownFile() throws Exception {
		final Set<IDeclaration> map = table.get(file1);

		assertEmptyExports(map);
	}

	public void testRemove() throws Exception {
		Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(new Declaration(elt1F1, name1F1));

		table.add(file1, declElt1F1Name1F1);
		table.add(file2, declElt1F2Name1F2);

		table.remove(file2);

		final Set<IDeclaration> actual1 = table.get(file1);
		final Set<IDeclaration> actual2 = table.get(file2);

		assertExports(expected, actual1);
		assertEmptyExports(actual2);
	}

	public void testClear() throws Exception {

		table.add(file1, declElt1F1Name1F1);
		table.add(file2, declElt1F2Name1F2);

		table.clear();

		final Set<IDeclaration> actual1 = table.get(file1);
		final Set<IDeclaration> actual2 = table.get(file2);

		assertEmptyExports(actual1);
		assertEmptyExports(actual2);
	}

}
