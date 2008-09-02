package org.rodinp.internal.core.index.tables.tests;

import java.util.Arrays;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tables.FileIndexTable;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class FileIndexTableTests extends AbstractRodinDBTests {

	private static final FileIndexTable table = new FileIndexTable();
	private static NamedElement element;
	private static NamedElement element2;
	private static IRodinFile file;
	private static IRodinFile file2;

	public FileIndexTableTests(String name) {
		super(name);
	}

	private void assertSize(IInternalElement[] elements, int size) {
		assertEquals("incorrect number of elements", size, elements.length);
	}

	private void assertIsEmpty(IInternalElement[] elements) {
		assertSize(elements, 0);
	}

	private void assertContainsAll(IInternalElement[] expectedElements,
			IInternalElement[] actualElements) {

		for (IInternalElement elem : expectedElements) {
			assertContains(elem, actualElements);
		}
	}

	private void assertSameElements(IInternalElement[] expectedElements,
			IInternalElement[] actualElements) {

		assertContainsAll(expectedElements, actualElements);

		assertSize(actualElements, expectedElements.length);
	}

	private void assertContains(IInternalElement elem,
			IInternalElement[] actualElements) {

		List<IInternalElement> actList = Arrays.asList(actualElements);

		assertTrue("element " + elem.getElementName() + " is not present",
				actList.contains(elem));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = rodinProject.getRodinFile("ref.test");
		file.create(false, null);
		file2 = rodinProject.getRodinFile("ref2.test");
		file2.create(false, null);
		element = IndexTestsUtil.createNamedElement(file, "elem");
		element2 = IndexTestsUtil.createNamedElement(file, "elem2");
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

		assertSameElements(expectedResult, elements);
	}

	public void testGetElementsFileAbsent() throws Exception {
		table.addElement(element, file);

		final IInternalElement[] elements = table.getElements(file2);

		assertIsEmpty(elements);
	}

	public void testAddElement() throws Exception {
		table.addElement(element, file);

		final IInternalElement[] expectedResult = new IInternalElement[] { element };
		final IInternalElement[] elements = table.getElements(file);

		assertSameElements(expectedResult, elements);
	}

	public void testRemoveElements() throws Exception {
		table.addElement(element, file);
		table.addElement(element2, file2);
		table.removeElements(file);

		final IInternalElement[] elements = table.getElements(file);
		final IInternalElement[] expectedResult2 = new IInternalElement[] { element2 };
		final IInternalElement[] elements2 = table.getElements(file2);
	
		assertIsEmpty(elements);
		assertSameElements(expectedResult2, elements2);
	}

	public void testRemoveElementsFileAbsent() throws Exception {
		table.addElement(element, file);
		table.removeElements(file2);

		final IInternalElement[] expectedResult = new IInternalElement[] { element };
		final IInternalElement[] elements = table.getElements(file);
		final IInternalElement[] elements2 = table.getElements(file2);

		assertSameElements(expectedResult, elements);
		assertIsEmpty(elements2);
	}

	public void testClear() throws Exception {
		table.addElement(element, file);
		table.addElement(element2, file2);
		table.clear();
		
		final IInternalElement[] elements = table.getElements(file);
		final IInternalElement[] elements2 = table.getElements(file2);
		
		assertIsEmpty(elements);
		assertIsEmpty(elements2);
	}

}
