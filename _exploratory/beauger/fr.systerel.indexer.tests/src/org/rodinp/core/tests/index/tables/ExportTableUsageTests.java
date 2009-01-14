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
package org.rodinp.core.tests.index.tables;

import static org.rodinp.core.tests.index.IndexTestsUtil.*;

import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.index.IndexTests;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

public class ExportTableUsageTests extends IndexTests {

	private static final ExportTable exportTable = new ExportTable();
	private static IRodinProject rodinProject;
	private static IRodinFile file;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static IDeclaration declElt1Name1;
	private static IDeclaration declElt2Name2;
	private static final RodinIndex rodinIndex = new RodinIndex();

	private static final IndexManager manager = IndexManager.getDefault();
	private static final String name1 = "elt1Name";
	private static final String name2 = "elt2Name";

	public ExportTableUsageTests(String name) {
		super(name, true);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "expInd.test");
		elt1 = createNamedElement(file, "elt1");
		elt2 = createNamedElement(file, "elt2");
		declElt1Name1 = new Declaration(elt1, name1);
		declElt2Name2 = new Declaration(elt2, name2);
		exportTable.add(file, declElt1Name1);
		exportTable.add(file, declElt2Name2);
		final IDeclaration declElt1 = new Declaration(elt1, name1);
		final IDeclaration declElt2 = new Declaration(elt2, name2);
		makeDescAndDefaultOcc(rodinIndex, declElt1, file.getRoot());
		makeDescAndDefaultOcc(rodinIndex, declElt2, file.getRoot());

	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		rodinIndex.clear();
		exportTable.clear();
		manager.clear();
		super.tearDown();
	}

	public void testExportTableUpdatingFilling() throws Exception {
		manager.clearIndexers();
		final FakeExportIndexer indexer = new FakeExportIndexer(rodinIndex, exportTable);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		manager.scheduleIndexing(file);
		
		final Set<IDeclaration> expected = indexer.getExports(file);
		final Set<IDeclaration> actual = manager.getExportTable(
				rodinProject).get(file);
		
		assertExports(expected, actual);
	}

	public void testExportTableRenaming() throws Exception {
		// index file
		manager.scheduleIndexing(file);
		
		// change exports
		exportTable.get(file).add(new Declaration(elt1, "expRenName1"));
		manager.clearIndexers();
		final FakeExportIndexer indexer = new FakeExportIndexer(rodinIndex, exportTable);
		manager.addIndexer(indexer, TEST_FILE_TYPE);
		
		// then index again file
		manager.scheduleIndexing(file);
		
		// verify renaming
		final Set<IDeclaration> expected = indexer.getExports(file);
		final Set<IDeclaration> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}

	public void testExportTableRemoving() throws Exception {
		// index file
		manager.scheduleIndexing(file);

		// change exports
		exportTable.remove(file);
		exportTable.add(file, declElt1Name1);
		manager.clearIndexers();
		final FakeExportIndexer indexer = new FakeExportIndexer(rodinIndex, exportTable);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		// then index again file
		manager.scheduleIndexing(file);

		// verify removing
		final Set<IDeclaration> expected = indexer.getExports(file);
		final Set<IDeclaration> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}

	public void testExportTableAdding() throws Exception {
		// index file
		manager.scheduleIndexing(file);

		// change exports
		NamedElement eltAdd = createNamedElement(file, "eltAdd");
		final String eltAddName = "eltAddName";
		final IDeclaration declEltAdd = new Declaration(eltAdd, eltAddName);
		exportTable.add(file, declEltAdd);
		makeDescAndDefaultOcc(rodinIndex, declEltAdd, file.getRoot());
		manager.clearIndexers();
		final FakeExportIndexer indexer = new FakeExportIndexer(rodinIndex, exportTable);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		// then index again file
		manager.scheduleIndexing(file);

		// verify adding
		final Set<IDeclaration> expected = indexer.getExports(file);
		final Set<IDeclaration> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}
}
