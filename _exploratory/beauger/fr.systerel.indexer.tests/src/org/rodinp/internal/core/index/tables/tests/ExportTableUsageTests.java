package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertExports;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.ExportTable;

public class ExportTableUsageTests extends AbstractRodinDBTests {

	private static FakeExportIndexer indexer;
	private static final ExportTable exportTable = new ExportTable();
	private static IRodinProject rodinProject;
	private static IRodinFile file;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static final RodinIndex rodinIndex = new RodinIndex();

	private static final IndexManager manager = IndexManager.getDefault();
	private static final String name1 = "elt1Name";
	private static final String name2 = "elt2Name";

	public ExportTableUsageTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "expInd.test");
		elt1 = createNamedElement(file, "elt1");
		elt2 = createNamedElement(file, "elt2");
		exportTable.add(file, elt1);
		exportTable.add(file, elt2);
		rodinIndex.makeDescriptor(elt1, name1);
		rodinIndex.makeDescriptor(elt2, name2);

		indexer = new FakeExportIndexer(rodinIndex, exportTable);
		RodinIndexer.register(indexer, file.getElementType());
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
		manager.scheduleIndexing(file);

		final Set<IInternalElement> expected = indexer.getExports(file);
		final Set<IInternalElement> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}

	public void testExportTableRemoving() throws Exception {
		// index file
		manager.scheduleIndexing(file);

		// change exports
		exportTable.remove(file);
		exportTable.add(file, elt1);
		manager.clearIndexers();
		indexer = new FakeExportIndexer(rodinIndex, exportTable);
		RodinIndexer.register(indexer, file.getElementType());

		// then index again file
		manager.scheduleIndexing(file);

		// verify removing
		final Set<IInternalElement> expected = indexer.getExports(file);
		final Set<IInternalElement> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}

	public void testExportTableAdding() throws Exception {
		// index file
		manager.scheduleIndexing(file);

		// change exports
		NamedElement eltAdd = createNamedElement(file, "eltAdd");
		exportTable.add(file, eltAdd);
		final String eltAddName = "eltAddName";
		rodinIndex.makeDescriptor(eltAdd, eltAddName);
		manager.clearIndexers();
		indexer = new FakeExportIndexer(rodinIndex, exportTable);
		RodinIndexer.register(indexer, file.getElementType());

		// then index again file
		manager.scheduleIndexing(file);

		// verify adding
		final Set<IInternalElement> expected = indexer.getExports(file);
		final Set<IInternalElement> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}
}
