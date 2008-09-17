package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertExports;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tables.ExportTable;

public class ExportTableTests extends AbstractRodinDBTests {

	public ExportTableTests(String name) {
		super(name);
	}

	private static final ExportTable table = new ExportTable();
	private static final Map<IInternalElement, String> emptyExport = new HashMap<IInternalElement, String>();
	private static NamedElement element1F1;
	private static NamedElement element2F1;
	private static NamedElement element1F2;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static final String name1F1 = "name1F1";
	private static final String name2F1 = "name2F1";
	private static final String name1F2 = "name1F2";

	private void assertEmptyExports(Map<IInternalElement, String> map) {
		assertExports(emptyExport, map);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file1 = createRodinFile(rodinProject, "exp1.test");
		file2 = createRodinFile(rodinProject, "exp2.test");
		element1F1 = createNamedElement(file1, "elem1F1");
		element2F1 = createNamedElement(file1, "elem2F1");
		element1F2 = createNamedElement(file2, "elem1F2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testAddGet() throws Exception {
		Map<IInternalElement, String> expected = new HashMap<IInternalElement, String>();
		expected.put(element1F1, name1F1);

		table.add(file1, element1F1, name1F1);
		final Map<IInternalElement, String> actual = table.get(file1);

		assertExports(expected, actual);
	}

	public void testAddGetSeveral() throws Exception {
		Map<IInternalElement, String> expected = new HashMap<IInternalElement, String>();
		expected.put(element1F1, name1F1);
		expected.put(element2F1, name2F1);

		table.add(file1, element1F1, name1F1);
		table.add(file1, element2F1, name2F1);
		final Map<IInternalElement, String> actual = table.get(file1);

		assertExports(expected, actual);
	}

	public void testAddGetVariousFiles() throws Exception {
		Map<IInternalElement, String> expected1 = new HashMap<IInternalElement, String>();
		expected1.put(element1F1, name1F1);
		Map<IInternalElement, String> expected2 = new HashMap<IInternalElement, String>();
		expected2.put(element1F2, name1F2);

		table.add(file1, element1F1, name1F1);
		table.add(file2, element1F2, name1F2);

		final Map<IInternalElement, String> actual1 = table.get(file1);
		final Map<IInternalElement, String> actual2 = table.get(file2);

		assertExports(expected1, actual1);
		assertExports(expected2, actual2);
	}

	public void testGetUnknownFile() throws Exception {
		final Map<IInternalElement, String> map = table.get(file1);

		assertEmptyExports(map);
	}

	public void testRemove() throws Exception {
		Map<IInternalElement, String> expected = new HashMap<IInternalElement, String>();
		expected.put(element1F1, name1F1);

		table.add(file1, element1F1, name1F1);
		table.add(file2, element1F2, name1F2);

		table.remove(file2);

		final Map<IInternalElement, String> actual1 = table.get(file1);
		final Map<IInternalElement, String> actual2 = table.get(file2);

		assertExports(expected, actual1);
		assertEmptyExports(actual2);
	}

	public void testClear() throws Exception {

		table.add(file1, element1F1, name1F1);
		table.add(file2, element1F2, name1F2);

		table.clear();

		final Map<IInternalElement, String> actual1 = table.get(file1);
		final Map<IInternalElement, String> actual2 = table.get(file2);

		assertEmptyExports(actual1);
		assertEmptyExports(actual2);
	}

	public void testContains() throws Exception {
		table.add(file1, element1F1, name1F1);

		final boolean contains = table.contains(file1, element1F1);

		assertTrue("The ExportTable should contain " + element1F1, contains);
	}

	public void testContainsNot() throws Exception {
		final boolean contains = table.contains(file1, element1F1);

		assertFalse("TheExportTable should not contain " + element1F1, contains);
	}
}
