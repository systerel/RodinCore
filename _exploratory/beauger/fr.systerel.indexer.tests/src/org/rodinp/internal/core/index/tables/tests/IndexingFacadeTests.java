package org.rodinp.internal.core.index.tables.tests;

import java.util.Arrays;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.FileIndexTable;
import org.rodinp.internal.core.index.tests.FakeIndexer;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class IndexingFacadeTests extends AbstractRodinDBTests {

	public IndexingFacadeTests(String name) {
		super(name);
	}

	private static final IIndexer indexer = new FakeIndexer();
	private static IRodinFile file;
	private static NamedElement namedElement;
	private static NamedElement namedElement2;
	private static IInternalElement[] expectedElements;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = rodinProject.getRodinFile("concInd.test");
		file.create(false, null);
		namedElement = IndexTestsUtil.createNamedElement(file, "elt1");
		namedElement2 = IndexTestsUtil.createNamedElement(file, "elt2");
		expectedElements = new NamedElement[] { namedElement, namedElement2 };
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	private static void assertFileTable(IInternalElement[] expectedElts,
			IInternalElement[] actualElts) {

		assertEquals("bad number of elements", expectedElts.length,
				actualElts.length);
		List<IInternalElement> expectedList = Arrays.asList(actualElts);
		List<IInternalElement> actualList = Arrays.asList(actualElts);
		assertTrue("missing elements\nexpected: " + expectedList
				+ "\nbut was: " + actualList, actualList
				.containsAll(expectedList));
	}

	public void testFileTable() throws Exception {
		final RodinIndex rodinIndex = new RodinIndex();
		final FileIndexTable fileIndexTable = new FileIndexTable();
		IndexingFacade index = new IndexingFacade(rodinIndex, fileIndexTable);
		
		indexer.index(file, index);
		IInternalElement[] actualElts = fileIndexTable.getElements(file);
		
		assertFileTable(expectedElements, actualElts);
	}

	// TODO add more tests
	// FIXME review all test files to improve code quality and test completeness
}
