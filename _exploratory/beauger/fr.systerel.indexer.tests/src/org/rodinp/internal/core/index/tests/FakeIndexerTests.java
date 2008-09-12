package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexingFacade;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.DependenceTable;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class FakeIndexerTests extends AbstractRodinDBTests {

	public FakeIndexerTests(String name) {
		super(name);
	}

	private static IIndexer indexer = new FakeIndexer();
	private static IRodinFile file;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = IndexTestsUtil.createRodinFile(rodinProject, "concInd.test");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testIndex() throws Exception {
		NamedElement element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName);

		final RodinIndex rodinIndex = new RodinIndex();
		IIndexingFacade index = new IndexingFacade(file, rodinIndex,
				new FileTable(), new NameTable(), new ExportTable(), new DependenceTable());
		indexer.index(file, index);

		IndexTestsUtil.assertDescriptor(rodinIndex.getDescriptor(element),
				element, IndexTestsUtil.defaultName, 6);
	}

}
