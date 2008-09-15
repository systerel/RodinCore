package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertDescriptor;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.defaultName;

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
		file = createRodinFile(rodinProject, "concInd.test");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testIndex() throws Exception {
		NamedElement element = createNamedElement(file,
				defaultName);

		final RodinIndex rodinIndex = new RodinIndex();
		IIndexingFacade index = new IndexingFacade(file, rodinIndex,
				new FileTable(), new NameTable(), new ExportTable(), new DependenceTable());
		indexer.index(file, index);

		assertDescriptor(rodinIndex.getDescriptor(element),
				element, defaultName, 6);
	}

}
