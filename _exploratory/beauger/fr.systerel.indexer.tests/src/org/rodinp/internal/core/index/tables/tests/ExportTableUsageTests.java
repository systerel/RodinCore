package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.IndexManager;

public class ExportTableUsageTests extends AbstractRodinDBTests {

	private static FakeExportIndexer indexer;
	private static final Map<IInternalElement, String> elements = new HashMap<IInternalElement, String>();
	private static IRodinProject rodinProject;
	private static IRodinFile file;
	private static NamedElement namedElement;
	private static NamedElement namedElement2;
	private static final IndexManager manager = IndexManager.getDefault();

	public ExportTableUsageTests(String name) {
		super(name);
	}

	private void assertExports(Map<IInternalElement, String> expected,
			Map<IInternalElement, String> actual) {
		final Set<IInternalElement> expKeySet = expected.keySet();
		final Set<IInternalElement> actKeySet = actual.keySet();
	
		assertTrue("missing exports", actKeySet.containsAll(expKeySet));
		assertEquals("unexpected exports", expKeySet.size(), actKeySet.size());
	
		for (IInternalElement elem : actKeySet) {
			assertEquals("bad name for element " + elem.getElementName(),
					actual.get(elem), expected.get(elem));
		}
	}

	// TODO avoid all this stuff by overriding runTest and calling a clean method
	// then propagate that way of doing through all test units
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "expInd.test");
		namedElement = createNamedElement(file, "elt1");
		namedElement2 = createNamedElement(file, "elt2");
		elements.put(namedElement, "namedElementName");
		elements.put(namedElement2, "namedElement2Name");
		indexer = new FakeExportIndexer(elements);
		RodinIndexer.register(indexer, file.getElementType());
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		elements.clear();
		manager.clear();
		super.tearDown();
	}
	
	public void testExportTableUpdatingFilling() throws Exception {
		manager.scheduleIndexing(file);
		
		final Map<IInternalElement, String> expected = indexer.getExports();
		final Map<IInternalElement, String> actual = manager.getExportTable(
				rodinProject).get(file);
		
		assertExports(expected, actual);
	}

	public void testExportTableRenaming() throws Exception {
		// index file
		manager.scheduleIndexing(file);
		
		// change exports
		elements.put(namedElement, "expRenName1");
		manager.clearIndexers();
		indexer = new FakeExportIndexer(elements);
		RodinIndexer.register(indexer, file.getElementType());
		
		// then index again file
		manager.scheduleIndexing(file);
		
		// verify renaming
		final Map<IInternalElement, String> expected = indexer.getExports();
		final Map<IInternalElement, String> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}

	public void testExportTableRemoving() throws Exception {
		// index file
		manager.scheduleIndexing(file);
		
		// change exports
		elements.remove(namedElement2);
		manager.clearIndexers();
		indexer = new FakeExportIndexer(elements);
		RodinIndexer.register(indexer, file.getElementType());
		
		// then index again file
		manager.scheduleIndexing(file);
		
		// verify removing
		final Map<IInternalElement, String> expected = indexer.getExports();
		final Map<IInternalElement, String> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}

	public void testExportTableAdding() throws Exception {
		// index file
		manager.scheduleIndexing(file);
		
		// change exports
		NamedElement eltAdd = createNamedElement(file, "eltAdd");
		elements.put(eltAdd, "expAddName1");
		manager.clearIndexers();
		indexer = new FakeExportIndexer(elements);
		RodinIndexer.register(indexer, file.getElementType());
		
		// then index again file
		manager.scheduleIndexing(file);
		
		// verify adding
		final Map<IInternalElement, String> expected = indexer.getExports();
		final Map<IInternalElement, String> actual = manager.getExportTable(
				rodinProject).get(file);

		assertExports(expected, actual);
	}

}
