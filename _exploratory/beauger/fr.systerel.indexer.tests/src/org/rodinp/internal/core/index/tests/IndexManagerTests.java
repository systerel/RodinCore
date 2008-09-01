package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;

public class IndexManagerTests extends AbstractRodinDBTests {

	private IIndexer indexer = new ConcreteIndexer();
	private IRodinProject project;
	private IRodinFile file;

	public IndexManagerTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file = project.getRodinFile("indMan.test");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testScheduleIndexing() throws Exception {
		file.create(true, null);
		final NamedElement element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName);
		
		RodinIndexer.register(indexer);
		IndexManager.getDefault().scheduleIndexing(file);
		RodinIndexer.deregister(indexer);

		final IRodinIndex index = IndexManager.getDefault().getIndex(project);

		IndexTestsUtil.assertDescriptor(index, element, IndexTestsUtil.defaultName, 6);
	}
}
