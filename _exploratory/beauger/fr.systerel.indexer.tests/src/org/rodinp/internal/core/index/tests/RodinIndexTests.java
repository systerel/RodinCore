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
		file = project.getRodinFile("rodinIndex.test");
		file.create(true, null);
		element = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName);
		element2 = IndexTestsUtil.createNamedElement(file,
				IndexTestsUtil.defaultName+"2");
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testMakeGetDescriptor() throws Exception {
		index.makeDescriptor(element, name);
		
		final IDescriptor descriptor = index.getDescriptor(element);
		
		IndexTestsUtil.assertDescriptor(descriptor, element, name, 0);
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
	
	// RodinIndex()
	// getDescriptor(Object)
	// getDescriptors()
	// makeDescriptor(IInternalElement, String)
	// removeDescriptor(Object)
	// clear()
	// toString()
	// TODO
}
