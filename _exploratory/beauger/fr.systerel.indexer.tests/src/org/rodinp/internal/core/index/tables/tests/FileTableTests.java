package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertIsEmpty;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertSameElements;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.makeIIEArray;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tables.FileTable;

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
		file = createRodinFile(rodinProject, "filetable.test");
		file2 = createRodinFile(rodinProject, "filetable2.test");
		element = createNamedElement(file, "elem");
		element2 = createNamedElement(file2, "elem2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		table.clear();
		super.tearDown();
	}

	public void testGetElementsPresent() throws Exception {
		table.add(element, file);
		final IInternalElement[] expectedResult = makeIIEArray(element);

		final IInternalElement[] elements = table.get(file);

		assertSameElements(expectedResult, elements);
	}

	public void testGetElementsFileAbsent() throws Exception {
		table.add(element, file);

		final IInternalElement[] elements = table.get(file2);

		assertIsEmpty(elements);
	}

	public void testAddElement() throws Exception {
		table.add(element, file);

		final IInternalElement[] expectedResult = makeIIEArray(element);
		final IInternalElement[] elements = table.get(file);

		assertSameElements(expectedResult, elements);
	}

	public void testAlienElement() throws Exception {
		try {
			table.add(element, file2);
		} catch (Exception e) {
			fail("adding an alien element to a FileTable should not raise any Exception");
		}
	}

	public void testRemoveElements() throws Exception {
		table.add(element, file);
		table.add(element2, file2);
		table.remove(file);

		final IInternalElement[] elements = table.get(file);
		final IInternalElement[] expectedResult2 = makeIIEArray(element2);
		final IInternalElement[] elements2 = table.get(file2);

		assertIsEmpty(elements);
		assertSameElements(expectedResult2, elements2);
	}

	public void testRemoveElementsFileAbsent() throws Exception {
		table.add(element, file);
		table.remove(file2);

		final IInternalElement[] expectedResult = makeIIEArray(element);
		final IInternalElement[] elements = table.get(file);
		final IInternalElement[] elements2 = table.get(file2);

		assertSameElements(expectedResult, elements);
		assertIsEmpty(elements2);
	}

	public void testClear() throws Exception {
		table.add(element, file);
		table.add(element2, file2);
		table.clear();

		final IInternalElement[] elements = table.get(file);
		final IInternalElement[] elements2 = table.get(file2);

		assertIsEmpty(elements);
		assertIsEmpty(elements2);
	}

}
