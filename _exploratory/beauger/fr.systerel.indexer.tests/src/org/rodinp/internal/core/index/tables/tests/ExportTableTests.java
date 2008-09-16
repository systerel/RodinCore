package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertExports;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import java.util.HashSet;
import java.util.Set;

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
	private static NamedElement element1F1;
	private static NamedElement element2F1;
	private static NamedElement element1F2;
	private static IRodinFile file1;
	private static IRodinFile file2;

	private void assertEmptyExports(Set<IInternalElement> set) {
		assertNotNull("the export set should not be null", set);
		assertTrue("the export set should be empty", set.isEmpty());
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
		Set<IInternalElement> expected = new HashSet<IInternalElement>();
		expected.add(element1F1);

		table.add(file1, element1F1);
		final Set<IInternalElement> actual = table.get(file1);

		assertExports(expected, actual);
	}

	public void testAddGetSeveral() throws Exception {
		Set<IInternalElement> expected = new HashSet<IInternalElement>();
		expected.add(element1F1);
		expected.add(element2F1);

		table.add(file1, element1F1);
		table.add(file1, element2F1);
		final Set<IInternalElement> actual = table.get(file1);

		assertExports(expected, actual);
	}

	public void testAddGetVariousFiles() throws Exception {
		Set<IInternalElement> expected1 = new HashSet<IInternalElement>();
		expected1.add(element1F1);
		Set<IInternalElement> expected2 = new HashSet<IInternalElement>();
		expected2.add(element1F2);

		table.add(file1, element1F1);
		table.add(file2, element1F2);

		final Set<IInternalElement> actual1 = table.get(file1);
		final Set<IInternalElement> actual2 = table.get(file2);

		assertExports(expected1, actual1);
		assertExports(expected2, actual2);
	}

	public void testGetUnknownFile() throws Exception {
		final Set<IInternalElement> map = table.get(file1);

		assertEmptyExports(map);
	}

	public void testRemove() throws Exception {
		Set<IInternalElement> expected = new HashSet<IInternalElement>();
		expected.add(element1F1);

		table.add(file1, element1F1);
		table.add(file2, element1F2);

		table.remove(file2);

		final Set<IInternalElement> actual1 = table.get(file1);
		final Set<IInternalElement> actual2 = table.get(file2);

		assertExports(expected, actual1);
		assertEmptyExports(actual2);
	}

	public void testClear() throws Exception {

		table.add(file1, element1F1);
		table.add(file2, element1F2);

		table.clear();

		final Set<IInternalElement> actual1 = table.get(file1);
		final Set<IInternalElement> actual2 = table.get(file2);

		assertEmptyExports(actual1);
		assertEmptyExports(actual2);
	}

}
