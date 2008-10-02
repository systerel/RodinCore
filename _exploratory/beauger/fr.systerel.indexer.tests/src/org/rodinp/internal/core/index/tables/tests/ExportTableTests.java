package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertExports;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tests.IndexTests;

public class ExportTableTests extends IndexTests {

	public ExportTableTests(String name) {
		super(name, true);
	}

	private static final ExportTable table = new ExportTable();
	private static final Set<IDeclaration> emptyExport = new HashSet<IDeclaration>();
	private static NamedElement element1F1;
	private static NamedElement element2F1;
	private static NamedElement element1F2;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static final String name1F1 = "name1F1";
	private static final String name2F1 = "name2F1";
	private static final String name1F2 = "name1F2";

	private void assertEmptyExports(Set<IDeclaration> map) {
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
		Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(new Declaration(element1F1, name1F1));

		table.add(file1, element1F1, name1F1);
		final Set<IDeclaration> actual = table.get(file1);

		assertExports(expected, actual);
	}

	public void testAddGetSeveral() throws Exception {
		Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(new Declaration(element1F1, name1F1));
		expected.add(new Declaration(element2F1, name2F1));

		table.add(file1, element1F1, name1F1);
		table.add(file1, element2F1, name2F1);
		final Set<IDeclaration> actual = table.get(file1);

		assertExports(expected, actual);
	}

	public void testAddGetVariousFiles() throws Exception {
		Set<IDeclaration> expected1 = new HashSet<IDeclaration>();
		expected1.add(new Declaration(element1F1, name1F1));
		Set<IDeclaration> expected2 = new HashSet<IDeclaration>();
		expected2.add(new Declaration(element1F2, name1F2));

		table.add(file1, element1F1, name1F1);
		table.add(file2, element1F2, name1F2);

		final Set<IDeclaration> actual1 = table.get(file1);
		final Set<IDeclaration> actual2 = table.get(file2);

		assertExports(expected1, actual1);
		assertExports(expected2, actual2);
	}

	public void testGetUnknownFile() throws Exception {
		final Set<IDeclaration> map = table.get(file1);

		assertEmptyExports(map);
	}

	public void testRemove() throws Exception {
		Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(new Declaration(element1F1, name1F1));

		table.add(file1, element1F1, name1F1);
		table.add(file2, element1F2, name1F2);

		table.remove(file2);

		final Set<IDeclaration> actual1 = table.get(file1);
		final Set<IDeclaration> actual2 = table.get(file2);

		assertExports(expected, actual1);
		assertEmptyExports(actual2);
	}

	public void testClear() throws Exception {

		table.add(file1, element1F1, name1F1);
		table.add(file2, element1F2, name1F2);

		table.clear();

		final Set<IDeclaration> actual1 = table.get(file1);
		final Set<IDeclaration> actual2 = table.get(file2);

		assertEmptyExports(actual1);
		assertEmptyExports(actual2);
	}

//	public void testContains() throws Exception {
//		table.add(file1, element1F1, name1F1);
//
//		final boolean contains = table.contains(file1, element1F1);
//
//		assertTrue("The ExportTable should contain " + element1F1, contains);
//	}
//
//	public void testContainsNot() throws Exception {
//		final boolean contains = table.contains(file1, element1F1);
//
//		assertFalse("TheExportTable should not contain " + element1F1, contains);
//	}
}
