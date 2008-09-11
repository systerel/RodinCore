package org.rodinp.internal.core.index.tables.tests;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.tables.DependenceTable;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class DependenceTableUsageTests extends AbstractRodinDBTests {

	private static IRodinProject rodinProject;
	private static IRodinFile file1;
	private static IRodinFile file2;
	// private static IRodinFile file3;
	private static NamedElement eltF2;
	private static final String eltF2Name = "eltF2Name";
	private static final Map<IInternalElement, String> eltF2Map = new HashMap<IInternalElement, String>();
	private static final ExportTable f2ExportsElt2 = new ExportTable();
	private static final DependenceTable f1DepsOnf2 = new DependenceTable();

	private static final IndexManager manager = IndexManager.getDefault();

	public DependenceTableUsageTests(String name) {
		super(name);
	}

	private void assertSameOrder(IRodinFile[] expectedOrder,
			IRodinFile[] actualOrder) {

		final int length = expectedOrder.length;
		IndexTestsUtil.assertLength(actualOrder, length);

		for (int i = 0; i < length; i++) {
			assertEquals("bad order at rank " + (i + 1) + "/" + length,
					expectedOrder[i], actualOrder[i]);
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file1 = IndexTestsUtil.createRodinFile(rodinProject, "DepTable1.test");
		file2 = IndexTestsUtil.createRodinFile(rodinProject, "DepTable2.test");
		eltF2 = IndexTestsUtil.createNamedElement(file2, "eltF2");
		eltF2Map.put(eltF2, eltF2Name);
		f2ExportsElt2.put(file2, eltF2Map);
		f1DepsOnf2.put(file1, new IRodinFile[] { file2 });

		// file3 = IndexTestsUtil.createRodinFile(rodinProject,
		// "DepTable3.test");

	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		f2ExportsElt2.clear();
		eltF2Map.clear();
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

		RodinIndexer.register(indexer);
		manager.scheduleIndexing(toIndex);
		RodinIndexer.deregister(indexer);

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

		RodinIndexer.register(indexer);

		try {
			manager.scheduleIndexing(toIndex);
		} catch (IllegalStateException e) {
			assertTrue("Good exception but bad message", e.getMessage()
					.contains("cycle"));
			RodinIndexer.deregister(indexer);
			return;
		}
		fail("A cycle in dependencies should raise IllegalStateException");
	}

	public void testReindexDependents() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer);

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
		RodinIndexer.deregister(indexer);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testNoExports() throws Exception {
		ExportTable emptyExports = new ExportTable();

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, emptyExports);
		RodinIndexer.register(indexer);

		// file1 must already be known by the manager to be taken into account
		// when resolving dependencies and export changes
		manager.scheduleIndexing(file1);
		indexer.clearOrder();

		// file2 is requested to index, but file1 should not be indexed
		// again, as it depends on file2 but file2 has no exports
		IRodinFile[] toIndex = new IRodinFile[] { file2 };
		IRodinFile[] expectedOrder = new IRodinFile[] { file2 };

		manager.scheduleIndexing(toIndex);
		RodinIndexer.deregister(indexer);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);
	}

	public void testExportsUnchanged() throws Exception {

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				f1DepsOnf2, f2ExportsElt2);
		RodinIndexer.register(indexer);

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
		RodinIndexer.deregister(indexer);

		final IRodinFile[] actualOrder = indexer.getIndexingOrder();

		assertSameOrder(expectedOrder, actualOrder);

	}

	public void testNameChangesOnly() throws Exception {

		// final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
		// f1DepsOnf2, f2ExportsElt2);
		// RodinIndexer.register(indexer);
		//
		// IRodinFile[] toIndex = new IRodinFile[] { file1, file2 };
		//
		// // file1 and file2 must already be known by the manager to be taken
		// into
		// // account when resolving dependencies and export changes
		// manager.scheduleIndexing(toIndex);
		// indexer.clearOrder();
		//
		// TODO modify elt2Name
		// // file2 is requested to index, but file1 should not be indexed
		// // again, even if it depends on file2, because file2 exports are
		// // unchanged
		// IRodinFile[] expectedOrder = new IRodinFile[] { file2 };
		//
		// manager.scheduleIndexing(file2);
		// RodinIndexer.deregister(indexer);
		//
		// final IRodinFile[] actualOrder = indexer.getIndexingOrder();
		//
		// assertSameOrder(expectedOrder, actualOrder);
	}

}
