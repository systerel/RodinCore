package org.rodinp.internal.core.index.tables.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tables.FileIndexTable;
import org.rodinp.internal.core.index.tests.ConcreteIndexer;
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

	private void assertSize(final IInternalElement[] elements, int size) {
		assertEquals("incorrect number of elements", size, elements.length);
	}

//	private void assertElementPresent(FileIndexTable t, IRodinFile f, NamedElement elt) {
//		final IInternalElement[] elements = t.getElements(f);
//		assertNotNull("elements set should not be null", elements);
//		assertTrue("element is not present: " + elt.readableName(),
//				Arrays.asList(elements).contains(elt));
//	}
	
//	private void assertElementAbsent(FileIndexTable t, IRodinFile f, NamedElement elt) {
//		final IInternalElement[] elements = t.getElements(f);
//		if (elements != null) {
//			assertFalse("removed element is still present", Arrays.asList(
//					elements).contains(elt));
//		}
//	}

	private void assertIsEmpty(final IInternalElement[] elements) {
		assertSize(elements, 0);
	}
	
	private void assertFirstElement(final IInternalElement[] elements, IInternalElement elem) {
		assertEquals("bad element", elem, elements[0]);
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
		final IInternalElement[] elements = table.getElements(file);
		assertSize(elements, 1);
		assertFirstElement(elements, element);
	}

	public void testGetElementsFileAbsent() throws Exception {
		table.addElement(element, file);
		final IInternalElement[] elements = table.getElements(file2);
		assertIsEmpty(elements);
	}

	public void testAddElement() throws Exception {
		table.addElement(element, file);
		final IInternalElement[] elements = table.getElements(file);
		assertSize(elements, 1);
		assertFirstElement(elements, element);
	}

	public void testRemoveElements() throws Exception {
		table.addElement(element, file);
		table.addElement(element2, file2);
		table.removeElements(file);
		final IInternalElement[] elements = table.getElements(file);
		final IInternalElement[] elements2 = table.getElements(file2);
		assertIsEmpty(elements);
		assertSize(elements2, 1);
		assertFirstElement(elements2, element2);
	}

	public void testRemoveElementsFileAbsent() throws Exception {
		table.addElement(element, file);
		table.removeElements(file2);
		final IInternalElement[] elements = table.getElements(file);
		final IInternalElement[] elements2 = table.getElements(file2);
		assertSize(elements, 1);
		assertFirstElement(elements, element);
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
