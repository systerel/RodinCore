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

import java.util.Arrays;
import java.util.List;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.core.tests.util.IndexTestsUtil;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.IndexManager;
import org.rodinp.internal.core.indexer.tables.ExportTable;
import org.rodinp.internal.core.indexer.tables.IExportTable;
import org.rodinp.internal.core.indexer.tables.RodinIndex;

public class TotalOrderUsageTests extends IndexTests {

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static IRodinFile file3;
	private static NamedElement eltF2;
	private static IDeclaration declEltF2;
	private static final ExportTable f2ExportsElt2 = new ExportTable();
	private static final IExportTable emptyExports = new ExportTable();
	private static final DependenceTable f1DepsOnf2 = new DependenceTable();
	private static final RodinIndex rodinIndex = new RodinIndex();

	private static final IndexManager manager = IndexManager.getDefault();
	private static final String eltF2Name = "eltF2Name";

	public TotalOrderUsageTests(String name) {
		super(name);
	}

	private void assertSameOrder(IRodinFile[] expectedOrder,
			IRodinFile[] actualOrder) {

		final int length = expectedOrder.length;
		assertLength(actualOrder, length);

		for (int i = 0; i < length; i++) {
			assertEquals("bad order at rank " + (i + 1) + "/" + length,
					expectedOrder[i], actualOrder[i]);
		}
	}

	private void assertAnyOrder(IRodinFile[] expectedFiles,
			IRodinFile[] actualOrder) {

		final int length = expectedFiles.length;
		assertLength(actualOrder, length);

		List<IRodinFile> actualList = Arrays.asList(actualOrder);

		for (IRodinFile file : expectedFiles) {
			assertTrue(file + " was not indexed", actualList.contains(file));
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file1 = createRodinFile(project, "DepTable1.test");
		file2 = createRodinFile(project, "DepTable2.test");
		file3 = createRodinFile(project, "DepTable3.test");
		eltF2 = createNamedElement(file2, "eltF2");
		
		declEltF2 = new Declaration(eltF2, eltF2Name);
		makeDescAndDefaultOcc(rodinIndex, declEltF2, file2.getRoot());
		f2ExportsElt2.add(file2, declEltF2);
		f1DepsOnf2.put(file1, makeArray(file2));

	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		rodinIndex.clear();
		f2ExportsElt2.clear();
		super.tearDown();
	}

	public void testIndexingOrder() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);

		IRodinFile[] expectedOrder = makeArray(file2, file1);

		manager.addIndexer(indexer, TEST_FILE_TYPE);
		// files to index are presented in the reverse order of the required
		// indexing order (file2 should be indexed before file1)
		manager.scheduleIndexing(file1, file2);
		manager.clearIndexers();

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testCycle() throws Exception {
		DependenceTable cycle = new DependenceTable();
		// cycle: file1 -> file2 -> file1
		cycle.put(file1, makeArray(file2));
		cycle.put(file2, makeArray(file1));

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, cycle, f2ExportsElt2);

		manager.addIndexer(indexer, TEST_FILE_TYPE);

		manager.scheduleIndexing(file1, file2);

		final IRodinFile[] expectedFiles = makeArray(file1, file2);
		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertAnyOrder(expectedFiles, actualOrder);
	}

	public void testReindexDependents() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		// file1 must already be known by the manager to be taken into account
		// when resolving dependencies
		manager.scheduleIndexing(file1);
		indexer.clearOrder();

		// only file2 is requested to index, but file1 should also be indexed
		// again, after file2, as it depends on file2, which has exports changes
		// (file2 was never indexed and its exports are not empty).
		IRodinFile[] expectedOrder = makeArray(file2, file1);

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testNoExports() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, emptyExports);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		// file1 must already be known by the manager to be taken into account
		// when resolving dependencies and export changes
		manager.scheduleIndexing(file1);
		indexer.clearOrder();

		// file2 is requested to index, but file1 should not be indexed
		// again, as it depends on file2 but file2 has no exports
		IRodinFile[] expectedOrder = makeArray(file2);

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testExportsUnchanged() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		// file1 and file2 must already be known by the manager to be taken into
		// account when resolving dependencies and export changes
		manager.scheduleIndexing(file1, file2);
		indexer.clearOrder();

		// file2 is requested to index, but file1 should not be indexed
		// again, even if it depends on file2, because file2 exports are
		// unchanged
		IRodinFile[] expectedOrder = makeArray(file2);

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);

	}

	public void testNameChangesOnly() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		// file1 and file2 must already be known by the manager to be taken
		// into account when resolving dependencies and export changes
		manager.scheduleIndexing(file1, file2);

		manager.clearIndexers();
		final ExportTable f2ExportsElt2Name2 = new ExportTable();
		final String eltF2Name2 = "eltF2Name2";
		final IDeclaration declEltF2Name2 = new Declaration(eltF2, eltF2Name2);
		rodinIndex.removeDescriptor(eltF2);
		makeDescAndDefaultOcc(rodinIndex, declEltF2Name2, file2.getRoot());
		f2ExportsElt2Name2.add(file2, declEltF2Name2);
		final FakeDependenceIndexer indexerNewName = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2Name2);
		manager.addIndexer(indexerNewName, TEST_FILE_TYPE);

		// file2 is requested to index, but file1 should be indexed
		// again, because it depends on file2 and file2 exports are
		// changed, even if it concerns only names
		IRodinFile[] expectedOrder = makeArray(file2, file1);

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexerNewName.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testFileRemoved() throws Exception {
		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		manager.scheduleIndexing(file2);

		file2.delete(true, null);

		manager.scheduleIndexing(file2);

		final IDeclaration[] exports = manager.getExports(file2);
		final IDeclaration[] fileDecls = manager.getDeclarations(file2);
		final IDeclaration[] nameDecls = manager.getDeclarations(project,
				eltF2Name);

		assertIsEmpty(exports);
		assertIsEmpty(fileDecls);
		assertIsEmpty(nameDecls);
		assertNotIndexed(manager, eltF2);
	}

	public void testSerialExports() throws Exception {
		final DependenceTable f1dF2dF3 = new DependenceTable();
		f1dF2dF3.put(file1, makeArray(file2));
		f1dF2dF3.put(file2, makeArray(file3));

		final NamedElement elt3 = createNamedElement(file3, "elt3");
		final String elt3Name = "elt3Name";
		final IDeclaration declElt3 = new Declaration(elt3, elt3Name);
		IndexTestsUtil.makeDescAndDefaultOcc(rodinIndex, declElt3, file3
				.getRoot());

		final ExportTable f1f2f3expElt3 = new ExportTable();
		f1f2f3expElt3.add(file3, declElt3);
		f1f2f3expElt3.add(file2, declElt3);
		f1f2f3expElt3.add(file1, declElt3);

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1dF2dF3, f1f2f3expElt3);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		manager.scheduleIndexing(file1, file2, file3);

		final String exportsStr = "exports";
		assertSameElements(f1f2f3expElt3.get(file3), manager.getExports(file3),
				exportsStr);
		assertSameElements(f1f2f3expElt3.get(file2), manager.getExports(file2),
				exportsStr);
		assertSameElements(f1f2f3expElt3.get(file1), manager.getExports(file1),
				exportsStr);
	}

	public void testSeveralIndexing() throws Exception {
		final int indexingCount = 4;

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);

		manager.addIndexer(indexer, TEST_FILE_TYPE);

		for (int i = 1; i <= indexingCount; i++) {
			try {
				manager.scheduleIndexing(file1, file2, file3);
			} catch (Exception e) {
				fail("Several indexing raised exception at indexing i=" + i
						+ "\n" + e);
			}
		}
	}
}
