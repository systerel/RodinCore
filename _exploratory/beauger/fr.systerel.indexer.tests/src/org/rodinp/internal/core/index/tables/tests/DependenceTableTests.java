package org.rodinp.internal.core.index.tables.tests;

import java.util.Arrays;
import java.util.List;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.internal.core.index.tables.DependenceTable;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class DependenceTableTests extends AbstractRodinDBTests {

	public DependenceTableTests(String name) {
		super(name);
	}

	private static DependenceTable table;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static IRodinFile file3;
	private static IRodinFile file4;

	private void assertSameFiles(IRodinFile[] expected, IRodinFile[] actual) {

		assertEquals("The list does not have the expected size",
				expected.length, actual.length);

		final List<IRodinFile> expList = Arrays.asList(expected);
		final List<IRodinFile> actList = Arrays.asList(actual);

		assertTrue("Missing files", actList.containsAll(expList));
	}

	private void assertIsEmpty(IRodinFile[] files) {
		assertEquals("Should be empty", 0, files.length);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file1 = IndexTestsUtil.createRodinFile(rodinProject, "dep1.test");
		file2 = IndexTestsUtil.createRodinFile(rodinProject, "dep2.test");
		file3 = IndexTestsUtil.createRodinFile(rodinProject, "dep3.test");
		file4 = IndexTestsUtil.createRodinFile(rodinProject, "dep4.test");
		table = new DependenceTable();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table = null;
		super.tearDown();
	}

	public void testPutGet() throws Exception {
		final IRodinFile[] expected = new IRodinFile[] { file2, file3 };

		table.put(file1, expected);

		final IRodinFile[] actual = table.get(file1);

		assertSameFiles(expected, actual);
	}

	public void testGetNone() throws Exception {
		final IRodinFile[] actual = table.get(file1);

		assertIsEmpty(actual);
	}

	public void testPutGetSeveral() throws Exception {
		final IRodinFile[] expected2 = new IRodinFile[] { file2 };
		final IRodinFile[] expected4 = new IRodinFile[] { file4 };

		table.put(file1, expected2);
		table.put(file3, expected4);

		final IRodinFile[] actual1 = table.get(file1);
		final IRodinFile[] actual3 = table.get(file3);

		assertSameFiles(expected2, actual1);
		assertSameFiles(expected4, actual3);
	}

	public void testPutRedundancy() throws Exception {
		final IRodinFile[] redundant = new IRodinFile[] { file1, file4, file1 };

		try {
			table.put(file2, redundant);
		} catch (IllegalArgumentException e) {
			assertTrue("bad exception message", e.getMessage().contains(
					"Redundancy"));
			return;
		}
		fail("Adding redundant dependencies should raise IllegalArgumentException");
	}

	public void testPutSelfDependent() throws Exception {
		final IRodinFile[] selfDep = new IRodinFile[] { file4, file1 };

		try {
			table.put(file1, selfDep);
		} catch (IllegalArgumentException e) {
			assertTrue("bad exception message", e.getMessage().contains(
					"self"));
			return;
		}
		fail("Adding redundant dependencies should raise IllegalArgumentException");
	}

	public void testClear() throws Exception {
		final IRodinFile[] expected2 = new IRodinFile[] { file2 };
		final IRodinFile[] expected4 = new IRodinFile[] { file4 };

		table.put(file1, expected2);
		table.put(file3, expected4);

		table.clear();

		final IRodinFile[] actual1 = table.get(file1);
		final IRodinFile[] actual3 = table.get(file3);

		assertIsEmpty(actual1);
		assertIsEmpty(actual3);
	}

	public void testGetDependents() throws Exception {
		final IRodinFile[] top = new IRodinFile[] { file1 };
		final IRodinFile[] expected = new IRodinFile[] { file2, file3, file4 };

		table.put(file2, top);
		table.put(file3, top);
		table.put(file4, top);

		final IRodinFile[] actual = table.getDependents(file1);

		assertSameFiles(expected, actual);
	}

	public void testGetDependentsNone() throws Exception {

		final IRodinFile[] actual = table.getDependents(file1);

		assertIsEmpty(actual);
	}

}
