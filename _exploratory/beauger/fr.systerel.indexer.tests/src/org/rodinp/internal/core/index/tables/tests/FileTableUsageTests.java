package org.rodinp.internal.core.index.tables.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tests.FakeIndexer;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class FileTableUsageTests extends AbstractRodinDBTests {

	public FileTableUsageTests(String name) {
		super(name);
	}

	private static final boolean DEBUG = false;
	
	private static final IIndexer indexer = new FakeIndexer();
	private static IRodinFile file;
	private static NamedElement namedElement;
	private static NamedElement namedElement2;
	private static IInternalElement[] fileElements;
	private static final IndexManager manager = IndexManager.getDefault();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = rodinProject.getRodinFile("concInd.test");
		file.create(false, null);
		namedElement = IndexTestsUtil.createNamedElement(file, "elt1");
		namedElement2 = IndexTestsUtil.createNamedElement(file, "elt2");
		fileElements = new NamedElement[] { namedElement, namedElement2 };
		RodinIndexer.register(indexer);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		RodinIndexer.deregister(indexer);
		super.tearDown();
	}

	private void assertFileTable(IRodinFile rodinFile, IInternalElement[] expectedElements,
			String message) {
	
		final FileTable fileTable = manager.getFileTable(rodinFile
				.getRodinProject());
		IInternalElement[] actualElements = fileTable.getElements(rodinFile);

		if (DEBUG) {
			System.out.println(getName() + message);
			System.out.println(fileTable.toString());
		}
		IndexTestsUtil.assertSameElements(expectedElements, actualElements);
	}

	public void testFileTableFilling() throws Exception {
		manager.scheduleIndexing(file);
		assertFileTable(file, fileElements, "");
	}

	public void testFileTableUpdating() throws Exception {

		// first indexing with namedElement and namedElement2
		manager.scheduleIndexing(file);
		assertFileTable(file, fileElements, "\nBefore");

		// deleting some file contents
		namedElement.delete(true, null);
		IInternalElement[] fileElementsAfter = new NamedElement[] { namedElement2 };

		// second indexing with namedElement2 only
		manager.scheduleIndexing(file);
		assertFileTable(file, fileElementsAfter, "\nAfter");

	}

}
