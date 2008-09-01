package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.FileIndexTable;

public class ConcreteIndexerTests extends AbstractRodinDBTests {

	public ConcreteIndexerTests(String name) {
		super(name);
	}

	private IIndexer indexer = new ConcreteIndexer();
	private IRodinFile file;

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
		assertTrue("Should be able to index an IRodinFile", result);
	}

	// TODO add more specialized tests
	public void testIndex() throws Exception {
		NamedElement element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName);

		final RodinIndex rodinIndex = new RodinIndex();
		IndexingFacade index = new IndexingFacade(rodinIndex,
				new FileIndexTable());
		indexer.index(file, index);

		IndexTestsUtil.assertDescriptor(rodinIndex, element,
				IndexTestsUtil.defaultName, 6);
	}

}
