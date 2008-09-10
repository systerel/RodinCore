package org.rodinp.internal.core.index.tables.tests;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class ExportTableTests extends AbstractRodinDBTests {

	public ExportTableTests(String name) {
		super(name);
	}

	private static final ExportTable table = new ExportTable();
	private static NamedElement element1F1;
	private static NamedElement element2F1;
	private static NamedElement element1F2;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static final String name1F1 = "name1F1";
	private static final String name2F1 = "name2F1";
	private static final String name1F2 = "name1F2";

	private void assertMap(Map<IInternalElement, String> expected,
			final Map<IInternalElement, String> actual) {
		assertEquals("the map does not have the expected size",
				expected.size(), actual.size());
		assertTrue("the map does not contain all expected elements", actual
				.keySet().containsAll(expected.keySet()));
		for (IInternalElement elem : actual.keySet()) {
			assertEquals(
					"unexpected name for element1F1 " + elem.getElementName(),
					expected.get(elem), actual.get(elem));
		}
	}

	private void assertEmptyMap(Map<IInternalElement, String> map) {
		assertNotNull("the map should not be null", map);
		assertTrue("the map should be empty", map.isEmpty());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file1 = IndexTestsUtil.createRodinFile(rodinProject, "exp1.test");
		file2 = IndexTestsUtil.createRodinFile(rodinProject, "exp2.test");
		element1F1 = IndexTestsUtil.createNamedElement(file1, "elem1F1");
		element2F1 = IndexTestsUtil.createNamedElement(file1, "elem2F1");
		element1F2 = IndexTestsUtil.createNamedElement(file2, "elem1F2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	
	public void testPutGet() throws Exception {
		Map<IInternalElement, String> expected = new HashMap<IInternalElement, String>();
		expected.put(element1F1, name1F1);

		table.put(file1, expected);
		final Map<IInternalElement, String> actual = table.get(file1);

		assertMap(expected, actual);
	}

	public void testPutGetSeveral() throws Exception {
		Map<IInternalElement, String> expected = new HashMap<IInternalElement, String>();
		expected.put(element1F1, name1F1);
		expected.put(element2F1, name2F1);

		table.put(file1, expected);
		final Map<IInternalElement, String> actual = table.get(file1);

		assertMap(expected, actual);
	}

	public void testPutGetVariousFiles() throws Exception {
		Map<IInternalElement, String> expected1 = new HashMap<IInternalElement, String>();
		expected1.put(element1F1, name1F1);
		Map<IInternalElement, String> expected2 = new HashMap<IInternalElement, String>();
		expected2.put(element1F2, name1F2);

		table.put(file1, expected1);
		table.put(file2, expected2);
		final Map<IInternalElement, String> actual1 = table.get(file1);
		final Map<IInternalElement, String> actual2 = table.get(file2);

		assertMap(expected1, actual1);
		assertMap(expected2, actual2);
	}
	
	public void testGetUnknownFile() throws Exception {
		final Map<IInternalElement, String> map = table.get(file1);
		
		assertEmptyMap(map);
	}

	public void testRemove() throws Exception {
		Map<IInternalElement, String> expected = new HashMap<IInternalElement, String>();
		expected.put(element1F1, name1F1);
		Map<IInternalElement, String> elements2 = new HashMap<IInternalElement, String>();
		elements2.put(element1F2, name1F2);

		table.put(file1, expected);
		table.put(file2, elements2);
		
		table.remove(file2);
		
		final Map<IInternalElement, String> actual1 = table.get(file1);
		final Map<IInternalElement, String> actual2 = table.get(file2);

		assertMap(expected, actual1);
		assertEmptyMap(actual2);
	}

	public void testClear() throws Exception {
		Map<IInternalElement, String> expected = new HashMap<IInternalElement, String>();
		expected.put(element1F1, name1F1);
		Map<IInternalElement, String> elements2 = new HashMap<IInternalElement, String>();
		elements2.put(element1F2, name1F2);
		
		table.clear();
		
		final Map<IInternalElement, String> actual1 = table.get(file1);
		final Map<IInternalElement, String> actual2 = table.get(file2);

		assertEmptyMap(actual1);
		assertEmptyMap(actual2);
	}

}
