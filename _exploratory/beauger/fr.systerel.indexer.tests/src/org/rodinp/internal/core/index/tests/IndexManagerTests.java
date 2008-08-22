package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.RodinIndex;

public class IndexManagerTests extends ModifyingResourceTests {

	private IIndexer indexer = new ConcreteIndexer();
	private IRodinProject project;
	private IRodinFile file;

	public IndexManagerTests(String name) {
		super(name);
	}

	private void assertDescriptor(RodinIndex index, final String id,
			final int expectedLength) {
		final IDescriptor descriptor = index.getDescriptor(id);
		assertNotNull("expected descriptor not found", descriptor);

		final int refsLength = descriptor.getOccurrences().length;
		assertEquals("Did not index correctly", expectedLength, refsLength);
	}

	protected void setUp() throws Exception {
		super.setUp();
		project = IndexTestsUtil.createRodinProject("P");
		file = IndexTestsUtil.createRodinFile("P/indMan.test");
	}

	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testScheduleIndexing() throws Exception {
		file.create(true, null);
		final NamedElement element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultNamedElementName);
		element.create(null, null);
		final String id = IndexTestsUtil.elementUniqueId(element);

		RodinIndexer.register(indexer);
		IndexManager.getDefault().scheduleIndexing(file);
		RodinIndexer.deregister(indexer);

		final RodinIndex index = IndexManager.getDefault().getIndex(project);

		assertDescriptor(index, id, 6);
	}
}
