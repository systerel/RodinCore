package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class TotalOrderUsageTests extends AbstractRodinDBTests {

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static IRodinFile file3;
	private static NamedElement eltF2;
	private static final ExportTable f2ExportsElt2 = new ExportTable();
	private static final ExportTable emptyExports = new ExportTable();
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
		rodinIndex.makeDescriptor(eltF2, eltF2Name);
		f2ExportsElt2.add(file2, eltF2, eltF2Name);
		f1DepsOnf2.put(file1, makeIRFArray(file2));

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

		IRodinFile[] expectedOrder = makeIRFArray(file2, file1);

		RodinIndexer.register(indexer, file1.getElementType());
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
		cycle.put(file1, makeIRFArray(file2));
		cycle.put(file2, makeIRFArray(file1));

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, cycle, f2ExportsElt2);

		RodinIndexer.register(indexer, file1.getElementType());

		manager.scheduleIndexing(file1, file2);

		final IRodinFile[] expectedFiles = makeIRFArray(file1, file2);
		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertAnyOrder(expectedFiles, actualOrder);
	}

	public void testReindexDependents() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer, file1.getElementType());

		// file1 must already be known by the manager to be taken into account
		// when resolving dependencies
		manager.scheduleIndexing(file1);
		indexer.clearOrder();

		// only file2 is requested to index, but file1 should also be indexed
		// again, after file2, as it depends on file2, which has exports changes
		// (file2 was never indexed and its exports are not empty).
		IRodinFile[] expectedOrder = makeIRFArray(file2, file1);

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testNoExports() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, emptyExports);
		RodinIndexer.register(indexer, file1.getElementType());

		// file1 must already be known by the manager to be taken into account
		// when resolving dependencies and export changes
		manager.scheduleIndexing(file1);
		indexer.clearOrder();

		// file2 is requested to index, but file1 should not be indexed
		// again, as it depends on file2 but file2 has no exports
		IRodinFile[] expectedOrder = makeIRFArray(file2);

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testExportsUnchanged() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer, file1.getElementType());

		// file1 and file2 must already be known by the manager to be taken into
		// account when resolving dependencies and export changes
		manager.scheduleIndexing(file1, file2);
		indexer.clearOrder();

		// file2 is requested to index, but file1 should not be indexed
		// again, even if it depends on file2, because file2 exports are
		// unchanged
		IRodinFile[] expectedOrder = makeIRFArray(file2);

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);

	}

	public void testNameChangesOnly() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer, file1.getElementType());

		// file1 and file2 must already be known by the manager to be taken
		// into account when resolving dependencies and export changes
		manager.scheduleIndexing(file1, file2);

		manager.clearIndexers();
		final ExportTable f2ExportsElt2Name2 = new ExportTable();
		final String eltF2Name2 = "eltF2Name2";
		rodinIndex.removeDescriptor(eltF2);
		rodinIndex.makeDescriptor(eltF2, eltF2Name2);
		f2ExportsElt2Name2.add(file2, eltF2, eltF2Name2);
		final FakeDependenceIndexer indexerNewName = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2Name2);
		RodinIndexer.register(indexerNewName, file1.getElementType());

		// file2 is requested to index, but file1 should be indexed
		// again, because it depends on file2 and file2 exports are
		// changed, even if it concerns only names
		IRodinFile[] expectedOrder = makeIRFArray(file2, file1);

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexerNewName.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testFileRemoved() throws Exception {
		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer, file2.getElementType());

		manager.scheduleIndexing(file2);

		file2.delete(true, null);

		manager.scheduleIndexing(file2);

		final ExportTable exportTable = manager.getExportTable(project);
		final FileTable fileTable = manager.getFileTable(project);
		final NameTable nameTable = manager.getNameTable(project);
		final RodinIndex index = manager.getIndex(project);

		final Map<IInternalElement, String> exports = exportTable.get(file2);
		final IInternalElement[] fileElements = fileTable.get(file2);
		final IInternalElement[] nameElements = nameTable
				.getElements(eltF2Name);

		assertTrue("exports should be empty after file deletion", exports
				.isEmpty());
		assertIsEmpty(fileElements);
		assertIsEmpty(nameElements);
		assertNoSuchDescriptor(index, eltF2);
	}

	public void testSerialExports() throws Exception {
		final DependenceTable f1dF2dF3 = new DependenceTable();
		f1dF2dF3.put(file1, makeIRFArray(file2));
		f1dF2dF3.put(file2, makeIRFArray(file3));

		final NamedElement elt3 = createNamedElement(file3, "elt3");
		final String elt3Name = "elt3Name";
		rodinIndex.makeDescriptor(elt3, elt3Name);

		final ExportTable f1f2f3expElt3 = new ExportTable();
		f1f2f3expElt3.add(file3, elt3, elt3Name);
		f1f2f3expElt3.add(file2, elt3, elt3Name);
		f1f2f3expElt3.add(file1, elt3, elt3Name);

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1dF2dF3, f1f2f3expElt3);
		RodinIndexer.register(indexer, file1.getElementType());

		try {
			manager.scheduleIndexing(file1, file2, file3);
		} catch (Exception e) {
			fail("Re-exporting elements should not raise an exception.");
		}

		final ExportTable exportTable = manager.getExportTable(project);
		assertExports(f1f2f3expElt3.get(file3), exportTable.get(file3));
		assertExports(f1f2f3expElt3.get(file2), exportTable.get(file2));
		assertExports(f1f2f3expElt3.get(file1), exportTable.get(file1));
	}

	public void testSeveralIndexing() throws Exception {
		final int indexingCount = 4;

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f1DepsOnf2, f2ExportsElt2);

		RodinIndexer.register(indexer, file1.getElementType());

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
