package org.rodinp.internal.core.index.tables.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class FileTableTests extends AbstractRodinDBTests {

	private static final FileTable table = new FileTable();
	private static NamedElement element;
	private static NamedElement element2;
	private static IRodinFile file;
	private static IRodinFile file2;

	public FileTableTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = rodinProject.getRodinFile("filetable.test");
		file.create(false, null);
		file2 = rodinProject.getRodinFile("filetable2.test");
		file2.create(false, null);
		element = IndexTestsUtil.createNamedElement(file, "elem");
		element2 = IndexTestsUtil.createNamedElement(file2, "elem2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testGetElementsPresent() throws Exception {
		table.addElement(element, file);
		final IInternalElement[] expectedResult = new IInternalElement[] { element };

		final IInternalElement[] elements = table.getElements(file);

		IndexTestsUtil.assertSameElements(expectedResult, elements);
	}

	public void testGetElementsFileAbsent() throws Exception {
		table.addElement(element, file);

		final IInternalElement[] elements = table.getElements(file2);

		IndexTestsUtil.assertIsEmpty(elements);
	}

	public void testAddElement() throws Exception {
		table.addElement(element, file);

		final IInternalElement[] expectedResult = new IInternalElement[] { element };
		final IInternalElement[] elements = table.getElements(file);

		IndexTestsUtil.assertSameElements(expectedResult, elements);
	}

	public void testAlienElement() throws Exception {
		try {
			table.addElement(element, file2);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("adding an alien element to a FileTable should raise IllegalArgumentException");
	}

	public void testRemoveElements() throws Exception {
		table.addElement(element, file);
		table.addElement(element2, file2);
		table.removeElements(file);

		final IInternalElement[] elements = table.getElements(file);
		final IInternalElement[] expectedResult2 = new IInternalElement[] { element2 };
		final IInternalElement[] elements2 = table.getElements(file2);

		IndexTestsUtil.assertIsEmpty(elements);
		IndexTestsUtil.assertSameElements(expectedResult2, elements2);
	}

	public void testRemoveElementsFileAbsent() throws Exception {
		table.addElement(element, file);
		table.removeElements(file2);

		final IInternalElement[] expectedResult = new IInternalElement[] { element };
		final IInternalElement[] elements = table.getElements(file);
		final IInternalElement[] elements2 = table.getElements(file2);

		IndexTestsUtil.assertSameElements(expectedResult, elements);
		IndexTestsUtil.assertIsEmpty(elements2);
	}

	public void testClear() throws Exception {
		table.addElement(element, file);
		table.addElement(element2, file2);
		table.clear();

		final IInternalElement[] elements = table.getElements(file);
		final IInternalElement[] elements2 = table.getElements(file2);

		IndexTestsUtil.assertIsEmpty(elements);
		IndexTestsUtil.assertIsEmpty(elements2);
	}

}
