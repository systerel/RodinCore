package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.RodinIndex;

public class RodinIndexTests extends AbstractRodinDBTests {

	public RodinIndexTests(String name) {
		super(name);
	}

	private static IRodinProject project;
	private static IRodinFile file;
	private static NamedElement element;
	private static NamedElement element2;

	private static final IRodinIndex index = new RodinIndex();
	private static final String name = "eltName";
	private static final String name2 = "eltName2";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file = IndexTestsUtil.createRodinFile(project, "rodinIndex.test");
		element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName);
		element2 = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName + "2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		index.clear();
		super.tearDown();
	}

	public void testMakeDescriptor() throws Exception {
		final IDescriptor descriptor = index.makeDescriptor(element, name);

		IndexTestsUtil.assertDescriptor(descriptor, element, name, 0);
	}

	public void testGetDescriptor() throws Exception {
		final IDescriptor descriptorMake = index.makeDescriptor(element, name);

		final IDescriptor descriptorGet = index.getDescriptor(element);

		assertEquals("descriptors returned by make and get are different",
				descriptorMake, descriptorGet);
	}

	public void testMakeDoubleDescriptor() throws Exception {
		final IDescriptor descriptor1 = index.makeDescriptor(element, name);
		IDescriptor descriptor2 = null;
		try {
			descriptor2 = index.makeDescriptor(element, name);
		} catch (IllegalArgumentException e) {
			fail("2 successive calls to make with same name should not raise an exception "
					+ e.getLocalizedMessage());
		}
		assertEquals(
				"2 successive calls to make with same name should return the same descriptor",
				descriptor2, descriptor1);
	}

	public void testMakeDoubleFaultyDescriptor() throws Exception {
		index.makeDescriptor(element, name);
		
		try{
			index.makeDescriptor(element, name2);
		} catch(IllegalArgumentException e) {
			return;
		}
		fail("2 successive calls to make with different names should raise an exception");
	}

	public void testRemoveDescriptor() throws Exception {
		index.makeDescriptor(element, name);
		index.removeDescriptor(element);

		IndexTestsUtil.assertNoSuchDescriptor(index, element);
	}

	public void testGetDescriptors() throws Exception {
		index.makeDescriptor(element, name);
		index.makeDescriptor(element2, name2);

		final IDescriptor[] descriptors = index.getDescriptors();

		assertEquals("bad number of descriptors", 2, descriptors.length);

		IDescriptor desc = descriptors[0];
		IDescriptor desc2 = descriptors[1];

		if (desc.getElement() == element) {
			IndexTestsUtil.assertDescriptor(desc, element, name, 0);
			IndexTestsUtil.assertDescriptor(desc2, element2, name2, 0);
		} else {
			IndexTestsUtil.assertDescriptor(desc, element2, name2, 0);
			IndexTestsUtil.assertDescriptor(desc2, element, name, 0);
		}
	}

	public void testClear() throws Exception {
		index.makeDescriptor(element, name);
		index.makeDescriptor(element2, name2);

		index.clear();

		IndexTestsUtil.assertNoSuchDescriptor(index, element);
		IndexTestsUtil.assertNoSuchDescriptor(index, element2);
	}

}
