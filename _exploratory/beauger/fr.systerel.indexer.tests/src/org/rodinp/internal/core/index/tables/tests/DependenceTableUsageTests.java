package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertExports;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertIsEmpty;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertLength;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.tables.DependenceTable;
import org.rodinp.internal.core.index.tables.ExportTable;

public class DependenceTableUsageTests extends AbstractRodinDBTests {

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static IRodinFile file3;
	private static NamedElement eltF2;
	private static final String eltF2Name = "eltF2Name";
	private static final String eltF2Name2 = "eltF2Name2";
	private static final ExportTable f2ExportsElt2 = new ExportTable();
	private static final ExportTable emptyExports = new ExportTable();
	private static final DependenceTable f1DepsOnf2 = new DependenceTable();

	private static final IndexManager manager = IndexManager.getDefault();

	public DependenceTableUsageTests(String name) {
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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file1 = createRodinFile(project, "DepTable1.test");
		file2 = createRodinFile(project, "DepTable2.test");
		file3 = createRodinFile(project, "DepTable3.test");
		eltF2 = createNamedElement(file2, "eltF2");
		f2ExportsElt2.add(file2, eltF2, eltF2Name);
		f1DepsOnf2.put(file1, new IRodinFile[] { file2 });

	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		f2ExportsElt2.clear();
		super.tearDown();
	}

	public void testIndexingOrder() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, f2ExportsElt2);

		// files to index are presented in the reverse order
		// of the required indexing order
		// (file2 should be indexed before file1)
		IRodinFile[] toIndex = new IRodinFile[] { file1, file2 };
		IRodinFile[] expectedOrder = new IRodinFile[] { file2, file1 };

		RodinIndexer.register(indexer, file1.getElementType());
		manager.scheduleIndexing(toIndex);
		manager.clearIndexers();

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testCycle() throws Exception {
		DependenceTable cycle = new DependenceTable();
		// cycle: file1 -> file2 -> file1
		cycle.put(file1, new IRodinFile[] { file2 });
		cycle.put(file2, new IRodinFile[] { file1 });

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(cycle,
				f2ExportsElt2);

		IRodinFile[] toIndex = new IRodinFile[] { file1, file2 };

		RodinIndexer.register(indexer, file1.getElementType());

		try {
			manager.scheduleIndexing(toIndex);
		} catch (IllegalStateException e) {
			assertTrue("Good exception but bad message", e.getMessage()
					.contains("cycle"));
			return;
		}
		fail("A cycle in dependencies should raise IllegalStateException");
	}

	public void testReindexDependents() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer, file1.getElementType());

		// file1 must already be known by the manager to be taken into account
		// when resolving dependencies
		manager.scheduleIndexing(file1);
		indexer.clearOrder();

		// only file2 is requested to index, but file1 should also be indexed
		// again, after file2, as it depends on file2 and file2 has exports
		// FIXME should work only when exports change
		IRodinFile[] toIndex = new IRodinFile[] { file2 };
		IRodinFile[] expectedOrder = new IRodinFile[] { file2, file1 };

		manager.scheduleIndexing(toIndex);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testNoExports() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, emptyExports);
		RodinIndexer.register(indexer, file1.getElementType());

		// file1 must already be known by the manager to be taken into account
		// when resolving dependencies and export changes
		manager.scheduleIndexing(file1);
		indexer.clearOrder();

		// file2 is requested to index, but file1 should not be indexed
		// again, as it depends on file2 but file2 has no exports
		IRodinFile[] toIndex = new IRodinFile[] { file2 };
		IRodinFile[] expectedOrder = new IRodinFile[] { file2 };

		manager.scheduleIndexing(toIndex);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testExportsUnchanged() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer, file1.getElementType());

		IRodinFile[] toIndex = new IRodinFile[] { file1, file2 };

		// file1 and file2 must already be known by the manager to be taken into
		// account when resolving dependencies and export changes
		manager.scheduleIndexing(toIndex);
		indexer.clearOrder();

		// file2 is requested to index, but file1 should not be indexed
		// again, even if it depends on file2, because file2 exports are
		// unchanged
		IRodinFile[] expectedOrder = new IRodinFile[] { file2 };

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);

	}

	public void testNameChangesOnly() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer, file1.getElementType());

		IRodinFile[] toIndex = new IRodinFile[] { file1, file2 };

		// file1 and file2 must already be known by the manager to be taken
		// into account when resolving dependencies and export changes
		manager.scheduleIndexing(toIndex);

		manager.clearIndexers();
		final ExportTable f2ExportsElt2Name2 = new ExportTable();
		f2ExportsElt2Name2.add(file2, eltF2, eltF2Name2);
		final FakeDependenceIndexer indexerNewName = new FakeDependenceIndexer(
				f1DepsOnf2, f2ExportsElt2Name2);
		RodinIndexer.register(indexerNewName, file1.getElementType());

		// file2 is requested to index, but file1 should not be indexed
		// again, even if it depends on file2 and file2 exports are
		// changed, because it concerns only names
		IRodinFile[] expectedOrder = new IRodinFile[] { file2 };

		manager.scheduleIndexing(file2);

		final IRodinFile[] actualOrder = indexerNewName.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testFileRemoved() throws Exception {
		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer, file1.getElementType());

		manager.scheduleIndexing(file1);

		file1.delete(true, null);

		try {
			manager.scheduleIndexing(file1);
		} catch (Exception e) {
			fail("Exception thrown when trying to index a deleted file: "
					+ e.getLocalizedMessage());
		}
		final DependenceTable dependenceTable = manager
				.getDependenceTable(project);
		assertIsEmpty(dependenceTable.get(file1));
	}

	public void testSerialExports() throws Exception {
		final DependenceTable f1dF2dF3 = new DependenceTable();
		f1dF2dF3.put(file1, new IRodinFile[] { file2 });
		f1dF2dF3.put(file2, new IRodinFile[] { file3 });

		final NamedElement elt3 = createNamedElement(file3, "elt3");
		final String elt3Name = "elt3Name";

		final ExportTable f1f2f3expElt3 = new ExportTable();
		f1f2f3expElt3.add(file3, elt3, elt3Name);
		f1f2f3expElt3.add(file2, elt3, elt3Name);
		f1f2f3expElt3.add(file1, elt3, elt3Name);

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1dF2dF3, f1f2f3expElt3);
		RodinIndexer.register(indexer, file1.getElementType());

		IRodinFile[] toIndex = new IRodinFile[] { file1, file2, file3 };


		try {
			manager.scheduleIndexing(toIndex);
		} catch (Exception e) {
			fail("Re-exporting elements should not raise an exception.");
		}

		final ExportTable exportTable = manager.getExportTable(project);
		assertExports(f1f2f3expElt3.get(file3), exportTable.get(file3));
		assertExports(f1f2f3expElt3.get(file2), exportTable.get(file2));
		assertExports(f1f2f3expElt3.get(file1), exportTable.get(file1));
	}

}
