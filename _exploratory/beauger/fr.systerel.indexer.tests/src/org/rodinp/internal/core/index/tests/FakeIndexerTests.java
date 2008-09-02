package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.FileIndexTable;

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
		file = rodinProject.getRodinFile("concInd.test");
		file.create(false, null);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testCanIndex() throws Exception {
		boolean result;

		result = indexer.canIndex(file);
		assertTrue("FakeIndexer Should be able to index an IRodinFile", result);
	}

	public void testIndex() throws Exception {
		NamedElement element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName);

		final RodinIndex rodinIndex = new RodinIndex();
		IndexingFacade index = new IndexingFacade(rodinIndex,
				new FileIndexTable());
		indexer.index(file, index);

		IndexTestsUtil.assertDescriptor(rodinIndex.getDescriptor(element),
				element, IndexTestsUtil.defaultName, 6);
	}

}
