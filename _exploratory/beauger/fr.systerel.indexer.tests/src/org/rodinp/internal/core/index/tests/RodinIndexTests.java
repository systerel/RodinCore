package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertDescriptor;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertNoSuchDescriptor;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.defaultName;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.tables.RodinIndex;

public class RodinIndexTests extends IndexTests {

	public RodinIndexTests(String name) {
		super(name, true);
	}

	private static IRodinProject project;
	private static IRodinFile file;
	private static NamedElement element;
	private static NamedElement element2;

	private static final RodinIndex index = new RodinIndex();
	private static final String name = "eltName";
	private static final String name2 = "eltName2";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file = createRodinFile(project, "rodinIndex.test");
		element = createNamedElement(file,
				defaultName);
		element2 = IndexTestsUtil.createNamedElement(file,
				defaultName + "2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		index.clear();
		super.tearDown();
	}

	public void testMakeDescriptor() throws Exception {
		final Descriptor descriptor = index.makeDescriptor(element, name);

		assertDescriptor(descriptor, element, name, 0);
	}

	public void testGetDescriptor() throws Exception {
		final Descriptor descriptorMake = index.makeDescriptor(element, name);

		final Descriptor descriptorGet = index.getDescriptor(element);

		assertEquals("descriptors returned by make and get are different",
				descriptorMake, descriptorGet);
	}

	public void testMakeDoubleDescriptor() throws Exception {
		index.makeDescriptor(element, name);

		try {
			index.makeDescriptor(element, name);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testMakeDoubleDescriptorDiffName() throws Exception {
		index.makeDescriptor(element, name);

		try {
			index.makeDescriptor(element, name2);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testRemoveDescriptor() throws Exception {
		index.makeDescriptor(element, name);
		index.removeDescriptor(element);

		assertNoSuchDescriptor(index, element);
	}

	public void testGetDescriptors() throws Exception {
		index.makeDescriptor(element, name);
		index.makeDescriptor(element2, name2);

		final Descriptor[] descriptors = index.getDescriptors();

		assertEquals("bad number of descriptors", 2, descriptors.length);

		Descriptor desc = descriptors[0];
		Descriptor desc2 = descriptors[1];

		if (desc.getElement() == element) {
			assertDescriptor(desc, element, name, 0);
			assertDescriptor(desc2, element2, name2, 0);
		} else {
			assertDescriptor(desc, element2, name2, 0);
			assertDescriptor(desc2, element, name, 0);
		}
	}

	public void testClear() throws Exception {
		index.makeDescriptor(element, name);
		index.makeDescriptor(element2, name2);

		index.clear();

		assertNoSuchDescriptor(index, element);
		assertNoSuchDescriptor(index, element2);
	}

}
