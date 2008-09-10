package org.rodinp.internal.core.index.tables.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class NameTableTests extends AbstractRodinDBTests {

	public NameTableTests(String name) {
		super(name);
	}

	private static final NameTable table = new NameTable();
	private static IRodinProject project;
	private static IRodinFile file;
	private static NamedElement element1;
	private static NamedElement element2;
	private static final String name1 = "name1";
	private static final String name2 = "name2";

	@Override
	protected void setUp() throws Exception {
		project = createRodinProject("P");
		file = IndexTestsUtil.createRodinFile(project, "nameTable.test");
		element1 = IndexTestsUtil.createNamedElement(file, "elt1");
		element2 = IndexTestsUtil.createNamedElement(file, "elt2");

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testPutGetOneElement() throws Exception {
		table.put(name1, element1);

		final IInternalElement[] expectedResult = new IInternalElement[] { element1 };
		final IInternalElement[] elements = table.getElements(name1);

		IndexTestsUtil.assertSameElements(expectedResult, elements);
	}

	public void testPutGetSeveralSameName() throws Exception {
		table.put(name1, element1);
		table.put(name1, element2);

		final IInternalElement[] expectedResult = new IInternalElement[] {
				element1, element2 };
		final IInternalElement[] elements = table.getElements(name1);

		IndexTestsUtil.assertSameElements(expectedResult, elements);
	}

	public void testPutGetVariousNames() throws Exception {
		table.put(name1, element1);
		table.put(name2, element2);

		final IInternalElement[] expectedResult1 = new IInternalElement[] { element1 };
		final IInternalElement[] expectedResult2 = new IInternalElement[] { element2 };
		final IInternalElement[] elements1 = table.getElements(name1);
		final IInternalElement[] elements2 = table.getElements(name2);

		IndexTestsUtil.assertSameElements(expectedResult1, elements1);
		IndexTestsUtil.assertSameElements(expectedResult2, elements2);
	}

	public void testRemove() throws Exception {
		table.put(name1, element1);
		table.put(name1, element2);

		table.remove(name1, element1);

		final IInternalElement[] expectedResult = new IInternalElement[] { element2 };
		final IInternalElement[] elements = table.getElements(name1);

		IndexTestsUtil.assertSameElements(expectedResult, elements);
	}

	public void testClear() throws Exception {
		table.put(name1, element1);
		table.put(name1, element2);

		table.clear();

		final IInternalElement[] elements = table.getElements(name1);

		IndexTestsUtil.assertIsEmpty(elements);
	}
	
}
