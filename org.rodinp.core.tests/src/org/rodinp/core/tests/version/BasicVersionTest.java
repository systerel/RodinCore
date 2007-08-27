/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.version;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IConversionResult;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.IConversionResult.IEntry;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.core.tests.version.db.IVersionEA;
import org.rodinp.core.tests.version.db.IVersionEC;
import org.rodinp.core.tests.version.db.VersionAttributes;

/**
 * @author Stefan Hallerstede
 *
 */
public class BasicVersionTest extends ModifyingResourceTests {

	public BasicVersionTest(String name) {
		super(name);
	}
	
	private IInternalElement[] getElements(IRodinProject project, String fileName,
			IInternalElementType<? extends IInternalElement> type, int size) throws RodinDBException {
		
		IRodinFile f = project.getRodinFile(fileName);
		
		IInternalElement[] elements=f.getChildrenOfType(type);
		
		assertEquals("should have elements of type " + type, size, elements.length);
		
		return elements;
	}
	
	private String getAttribute(
			IInternalElement element, 
			IAttributeType.String attr, 
			String string) throws Exception {
		assertTrue("Attribute not present " + attr.getId(), element.hasAttribute(attr));
		String value = element.getAttributeValue(attr);
		assertEquals("Attribute has wrong value " + attr.getId(), string, value);
		return value;
	}

	private void convertProjectWithSuccess(IRodinProject project, int size)
	throws RodinDBException {
		IConversionResult result = RodinCore.convert(project, true, null);
		
		IEntry[] entries = result.getEntries();
		
		assertEquals("wrong number of entries in result", size, entries.length);
		
		for (int i=0; i<entries.length; i++)
			assertTrue("error when transforming " + entries[i].getFile().getElementName(), 
					entries[i].success());
		
		result.accept(true, false, null);
	}
	
	private IRodinProject fetchProject(String name) throws Exception {
		importProject(name);
		
		IRodinProject qProject = getRodinProject(name);
		return qProject;
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		IWorkspaceRoot workspaceRoot = getWorkspaceRoot();
		for (IProject project : workspaceRoot.getProjects()) {
			project.delete(true, null);
		}
	}
	
	/**
	 * rename broken element name and load fixed Rodin file
	 */
	public void test_01_RenameElement() throws Exception {
		
		IRodinProject project = fetchProject("V01");
		
		convertProjectWithSuccess(project, 2);
		
		// ff does not have a version attribute, i.e., its version is 0
		
		getElements(project, "ff.tva", IVersionEA.ELEMENT_TYPE, 1);
		
		// gg has a version attribute with value 0
		
		getElements(project, "gg.tva", IVersionEA.ELEMENT_TYPE, 1);
	}

	/**
	 * rename broken attribute name and load fixed Rodin file
	 */
	public void test_02_RenameAttribute() throws Exception {
		
		IRodinProject project = fetchProject("V02");
		
		convertProjectWithSuccess(project, 1);
		
		IInternalElement[] elements = getElements(project, "ff.tvb", IVersionEA.ELEMENT_TYPE, 1);
		
		getAttribute(elements[0], VersionAttributes.StringAttr, "Hello");
	}
	
	/**
	 * add an attribute and load fixed Rodin file
	 */
	public void test_03_AddAttribute() throws Exception {
		
		IRodinProject project = fetchProject("V03");
		
		convertProjectWithSuccess(project, 1);
		
		IInternalElement[] elements = getElements(project, "ff.tvc", IVersionEA.ELEMENT_TYPE, 2);
		
		// new attribute in elements[0] added
		
		getAttribute(elements[0], VersionAttributes.StringAttr, "Hello");
		
		// existing attributes are not modified
		
		getAttribute(elements[1], VersionAttributes.StringAttr, "Byebye");
	}
	
	/**
	 * sort elements, rename elements, add attributes and load fixed Rodin file
	 */
	public void test_04_SortRenameElementAddAttribute() throws Exception {
		
		IRodinProject project = fetchProject("V04");
		
		convertProjectWithSuccess(project, 1);
		
		IInternalElement[] elements = getElements(project, "ff.tvd", IVersionEC.ELEMENT_TYPE, 6);
		
		String[] names = new String[] { "ax", "ay", "bx", "by", "bz", "cx" };
		String[] attrs = new String[] { "A", "A", "B", "B", "B", "C" };
		
		for (int i=0; i< 6; i++) {
			IInternalElement element = elements[i];
			assertEquals("wrong name", names[i], element.getElementName());
			getAttribute(element, VersionAttributes.StringAttr, attrs[i]);
		}
	}

}
