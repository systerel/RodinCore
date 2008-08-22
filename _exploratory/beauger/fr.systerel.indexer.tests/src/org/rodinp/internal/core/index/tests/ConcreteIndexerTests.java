package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.RodinIndex;

public class ConcreteIndexerTests extends ModifyingResourceTests {

	public ConcreteIndexerTests(String name) {
		super(name);
	}

	private IIndexer indexer = new ConcreteIndexer();
	private IRodinFile file;

	protected void setUp() throws Exception {
		super.setUp();
		IndexTestsUtil.createRodinProject("P");
		file = IndexTestsUtil.createRodinFile("P/concInd.test");
	}

	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testCanIndex() throws Exception {
		boolean result;

		result = indexer.canIndex(file);
		assertTrue("Should be able to index an IRodinFile", result);
	}

	public void testIndex() throws Exception {
		file.create(true, null);
		NamedElement element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultNamedElementName);
		element.create(null, null);

		RodinIndex index = new RodinIndex();
		indexer.index(file, index);

		final String id = IndexTestsUtil.elementUniqueId(element);

		assertDescriptor(index, id, 6);
	}

	private void assertDescriptor(RodinIndex index, final String id,
			final int expectedLength) {
		final IDescriptor descriptor = index.getDescriptor(id);
		assertNotNull("expected descriptor not found", descriptor);

		final int refsLength = descriptor.getOccurrences().length;
		assertEquals("Did not index correctly", expectedLength, refsLength);
	}

}
