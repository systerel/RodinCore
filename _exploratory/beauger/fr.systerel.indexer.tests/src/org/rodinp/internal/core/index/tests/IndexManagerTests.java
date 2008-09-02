package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;

public class IndexManagerTests extends AbstractRodinDBTests {

	private IIndexer indexer = new FakeIndexer();
	private IRodinProject project;
	private IRodinFile file;
	private final IndexManager manager = IndexManager.getDefault();

	public IndexManagerTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file = project.getRodinFile("indMan.test");
		file.create(true, null);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testScheduleIndexing() throws Exception {
		final NamedElement element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName);

		RodinIndexer.register(indexer);
		manager.scheduleIndexing(file);
		RodinIndexer.deregister(indexer);

		final IRodinIndex index = manager.getIndex(project);
		final IDescriptor desc = index.getDescriptor(element);

		IndexTestsUtil.assertDescriptor(desc, element,
				IndexTestsUtil.defaultName, 6);
	}

	public void testScheduleSeveralIndexing() throws Exception {
		// TODO test several calls to scheduleIndexing with same file

		final String elementName = IndexTestsUtil.defaultName;
		final NamedElement element = IndexTestsUtil.createNamedElement(file,
				elementName);

		final String element2Name = IndexTestsUtil.defaultName + "2";
		final NamedElement element2 = new NamedElement(element2Name, file);

		IRodinIndex index;

		RodinIndexer.register(indexer);

		// first indexing with element, without element2
		manager.scheduleIndexing(file);

		index = manager.getIndex(project);
		final IDescriptor descElement = index.getDescriptor(element);

		IndexTestsUtil.assertDescriptor(descElement, element, elementName, 6);
		IndexTestsUtil.assertNoSuchDescriptor(index, element2);

		element.delete(true, null);
		element2.create(null, null);

		// second indexing with element2, without element		
		manager.scheduleIndexing(file);

		index = manager.getIndex(project);
		final IDescriptor descElement2 = index.getDescriptor(element2);

		IndexTestsUtil.assertNoSuchDescriptor(index, element);
		IndexTestsUtil.assertDescriptor(descElement2, element2, element2Name, 6);

		RodinIndexer.deregister(indexer);

	}
}
