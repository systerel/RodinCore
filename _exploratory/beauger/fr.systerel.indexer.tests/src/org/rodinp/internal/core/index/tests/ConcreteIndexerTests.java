package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = IndexTestsUtil.createRodinProject("P");
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

	public void testIndex() throws Exception {
		file.create(true, null);
		NamedElement element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultNamedElementName);

		RodinIndex index = new RodinIndex();
		indexer.index(file, index);

		IndexTestsUtil.assertDescriptor(index, element, 6);
	}

}
