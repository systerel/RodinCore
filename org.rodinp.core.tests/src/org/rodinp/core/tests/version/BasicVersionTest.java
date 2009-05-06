/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *     ETH Zurich - initial API and implementation
 *     Systerel - added test for invalid version
 *     Systerel - separation of file and root element
 *     Systerel - added attribute modification
 *     Systerel - added test for name changing
 *******************************************************************************/
package org.rodinp.core.tests.version;

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.rodinp.core.IRodinDBStatusConstants.FUTURE_VERSION;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_VERSION_NUMBER;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IConversionResult;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.IConversionResult.IEntry;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.core.tests.version.db.IVersionEA;
import org.rodinp.core.tests.version.db.IVersionEB;
import org.rodinp.core.tests.version.db.IVersionEC;
import org.rodinp.core.tests.version.db.IVersionRootF;
import org.rodinp.core.tests.version.db.Modifier;
import org.rodinp.core.tests.version.db.VersionAttributes;

/**
 * @author Stefan Hallerstede
 *
 */
public class BasicVersionTest extends ModifyingResourceTests {

	public BasicVersionTest(String name) {
		super(name);
	}
	
	private static IInternalElement[] getElements(IRodinProject project,
			String fileName,
			IInternalElementType<? extends IInternalElement> type, int size)
			throws RodinDBException {
		
		IRodinFile f = project.getRodinFile(fileName);
		
		IInternalElement[] elements=f.getRoot().getChildrenOfType(type);
		
		assertEquals("should have elements of type " + type, size, elements.length);
		
		return elements;
	}
	
	private static void assertAttribute(
			IInternalElement element, 
			IAttributeType.String attr, 
			String string) throws Exception {
		assertTrue("Attribute not present " + attr.getId(), element.hasAttribute(attr));
		final String value = element.getAttributeValue(attr);
		assertEquals("Attribute has wrong value " + attr.getId(), string, value);
	}

	private static void assertName(String expectedName, IInternalElement element) {
		final String actual = element.getElementName();
		assertEquals("Unexpected element name", expectedName, actual);
	}

	private static void convertProjectWithSuccess(IRodinProject project, int size)
	throws RodinDBException {
		IConversionResult result = RodinCore.convert(project, true, null);
		
		IEntry[] entries = getEntries(result, size);
		
		for (int i=0; i<entries.length; i++)
			assertTrue("error when transforming " + entries[i].getFile().getElementName(), 
					entries[i].success());
		
		result.accept(true, false, null);
	}

	private static IEntry[] getEntries(IConversionResult result, int size) {
		IEntry[] entries = result.getEntries();
		
		assertEquals("wrong number of entries in result", size, entries.length);
		return entries;
	}
	
	private static byte[] getContents(IRodinFile file) throws Exception {
		InputStream s = file.getResource().getContents();
		int size = s.available();
		byte[] contents = new byte[size];
		int read = s.read(contents);
		s.close();
		assert read == size;
		return contents;
	}
	
	private static class ByteArrayWrapper {
		private final byte[] array;

		public ByteArrayWrapper(byte[] array) {
			super();
			this.array = array;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(array);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ByteArrayWrapper other = (ByteArrayWrapper) obj;
			if (!Arrays.equals(array, other.array))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			StringBuffer bb = new StringBuffer(array.length);
			for (int i=0; i<array.length; i++)
				bb.append(array[i]);
			return bb.toString();
		}
		
	}

	private static void convertProjectFailsFor(IRodinProject project, int size,
			String... files) throws Exception {
		IConversionResult result = RodinCore.convert(project, true, null);
		
		IEntry[] entries = getEntries(result, size);
		
		ByteArrayWrapper[] c = new ByteArrayWrapper[files.length];
		for (int i=0; i<files.length; i++) {
			c[i] = new ByteArrayWrapper(getContents(project.getRodinFile(files[i])));
		}
		
		for (int i=0; i<entries.length; i++)
			for (String name : files)
				if (entries[i].getFile().getElementName().equals(name))
					assertFalse("tarnsformation should have failed " + entries[i].getFile().getElementName(), 
					entries[i].success());
		
		result.accept(true, false, null);
		
		for (int i=0; i<files.length; i++) {
			ByteArrayWrapper b = new ByteArrayWrapper(getContents(project.getRodinFile(files[i])));
			assertEquals("File contents should not have changed " + files[i], c[i], b);
		}
		
	}

	private static IRodinProject fetchProject(String name) throws Exception {
		importProject(name);
		
		IRodinProject qProject = getRodinProject(name);
		return qProject;
	}
	
	private static void assertRodinDBExceptionRaised(IRodinFile rf,
			int severity, int code) {
		try {
			rf.getChildren();
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			final IRodinDBStatus status = (IRodinDBStatus) e.getStatus();
			assertEquals(severity, status.getSeverity());
			assertEquals(code, status.getCode());
			if (rf != null) {
				final IRodinElement[] elements = status.getElements();
				assertEquals(1, elements.length);
				assertEquals(rf, elements[0]);
			}
		}
	}

	private static void setFileContents(final IRodinFile rodinFile,
			final String contents) throws Exception {
		rodinFile.create(false, null);
		final IFile file = rodinFile.getResource();
		final byte[] bytes = contents.getBytes("UTF-8");
		final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		file.setContents(is, false, false, null);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
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
		
		assertAttribute(elements[0], VersionAttributes.StringAttr, "Hello");
	}
	
	/**
	 * add an attribute and load fixed Rodin file
	 */
	public void test_03_AddAttribute() throws Exception {
		
		IRodinProject project = fetchProject("V03");
		
		convertProjectWithSuccess(project, 1);
		
		IInternalElement[] elements = getElements(project, "ff.tvc", IVersionEA.ELEMENT_TYPE, 2);
		
		// new attribute in elements[0] added
		
		assertAttribute(elements[0], VersionAttributes.StringAttr, "Hello");
		
		// existing attributes are not modified
		
		assertAttribute(elements[1], VersionAttributes.StringAttr, "Byebye");
	}
	
	private void checkV04(IRodinProject project) throws Exception {
		IInternalElement[] elements = getElements(project, "ff.tvd", IVersionEC.ELEMENT_TYPE, 6);
		
		String[] names = new String[] { "ax", "ay", "bx", "by", "bz", "cx" };
		String[] attrs = new String[] { "A", "A", "B", "B", "B", "C" };
		
		for (int i=0; i< 6; i++) {
			IInternalElement element = elements[i];
			assertEquals("wrong name", names[i], element.getElementName());
			assertAttribute(element, VersionAttributes.StringAttr, attrs[i]);
		}
	}

	/**
	 * sort elements, rename elements, add attributes and load fixed Rodin file
	 */
	public void test_04_SortRenameElementAddAttribute() throws Exception {
		
		IRodinProject project = fetchProject("V04");
		
		convertProjectWithSuccess(project, 1);
		
		checkV04(project);
	}

	/**
	 * When a new Rodin file is created, the correct current version must be assigned to it.
	 */
	public void test_05_createAndOpenFileWithVersion() throws Exception {
		
		try {
			IRodinProject project = createRodinProject("P");
			IRodinFile file = project.getRodinFile("f.tvc");
			file.create(true, null);
			file.open(null);
		} catch (RodinDBException e) {
			fail("Could not access new file.");
		}
	}
	
	/**
	 * When necessary, a sequence of conversions must be carried out.
	 * (This is a variant of test_04)
	 */
	public void test_06_ConversionSequence() throws Exception {
		
		IRodinProject project = fetchProject("V04a");
		
		convertProjectWithSuccess(project, 1);
		
		checkV04(project);
	}
	
	/**
	 * If the version number of a file is higher than the current version number,
	 * conversion fails. The file must not be modified (even if the conversion result is accepted).
	 */
	public void test_07_CannotConvertFromFutureVersion() throws Exception {
		
		IRodinProject project = fetchProject("V02a");
		IRodinFile rf = project.getRodinFile("ff.tvb");
		assertRodinDBExceptionRaised(rf, ERROR, FUTURE_VERSION);
		convertProjectFailsFor(project, 1, rf.getElementName());
	}
	
	/**
	 * Check whether a version number is created if there is none initially
	 * and an attribute is added to the root node
	 */
	public void test_08_AddAttributeToRootNode() throws Exception {
		
		IRodinProject project = fetchProject("V05");
		
		convertProjectWithSuccess(project, 1);
		
		getElements(project, "ff.tve", IVersionEA.ELEMENT_TYPE, 0);

	}
	
	/**
	 * Basic function test for a source conversion:
	 * + creation of version number
	 * + change of version number
	 * + example of a conversion that is neither simple nor sorted
	 */
	public void test_09_SourceConversion() throws Exception {
		
		IRodinProject project = fetchProject("V06");
		
		convertProjectWithSuccess(project, 3);
		
		// proper version number "1" -changed or created
		project.getRodinFile("ff.tvf").getChildren();
		project.getRodinFile("gg.tvf").getChildren();
		
		// a more complicated conversion:
		IRodinFile file = project.getRodinFile("hh.tvf");
		IVersionRootF root = (IVersionRootF) file.getRoot();
		IVersionEA[] eas = root.getChildrenOfType(IVersionEA.ELEMENT_TYPE);
		assertEquals("one ea element", 1, eas.length);
		assertEquals("attribute of ea ok","Hello", 
				eas[0].getAttributeValue(VersionAttributes.StringAttr));
		assertFalse("", eas[0].hasAttribute(VersionAttributes.StringAttrX));
		IVersionEC[] ecs = eas[0].getChildrenOfType(IVersionEC.ELEMENT_TYPE);
		assertEquals("one ec element", 1, ecs.length);
		assertEquals("attribute of ec ok","x", 
				ecs[0].getAttributeValue(VersionAttributes.StringAttr));
		IVersionEB[] ebs = eas[0].getChildrenOfType(IVersionEB.ELEMENT_TYPE);
		assertEquals("no eb element in ea", 0, ebs.length);
		IVersionEB[] eds = root.getChildrenOfType(IVersionEB.ELEMENT_TYPE);
		assertEquals("one eb element on root level", 1, eds.length);
		assertEquals("eb has proper name", "a", eds[0].getElementName());
	}

	/**
	 * Test for a file containing an invalid version number.
	 */
	public void test_10_InvalidVersion() throws Exception {
		final String contents = //
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.rodinp.core.tests.versionFileF "
				+ "    version=\"not a number\">"
				+ "</org.rodinp.core.tests.versionFileF>";

		final IRodinFile rf = createRodinProject("P").getRodinFile("ff.tvf");
		setFileContents(rf, contents);
		assertRodinDBExceptionRaised(rf, ERROR, INVALID_VERSION_NUMBER);
	}

	/**
	 * Test of the modification of the contents of an attribute.
	 */
	public void test_11_ModifyAttribute()throws Exception {

		IRodinProject project = fetchProject("V07");

		convertProjectWithSuccess(project, 1);

		final IInternalElement[] elementsEA = getElements(project, "ff.tvg", IVersionEA.ELEMENT_TYPE, 2);
		final IInternalElement[] elementsEB = getElements(project, "ff.tvg", IVersionEB.ELEMENT_TYPE, 1);
		final IInternalElement[] elementsEC = getElements(project, "ff.tvg", IVersionEC.ELEMENT_TYPE, 1);
		final IVersionEC[] elementsEBEC = elementsEB[0].getChildrenOfType(IVersionEC.ELEMENT_TYPE);
		
		final Modifier modifier = new Modifier();
		assertAttribute(elementsEA[0], VersionAttributes.StringAttr, modifier.getNewValue("at EA"));
		assertAttribute(elementsEA[1], VersionAttributes.StringAttr, modifier.getNewValue("at EA"));
		assertAttribute(elementsEC[0], VersionAttributes.StringAttr, modifier.getNewValue("at EC"));
		assertAttribute(elementsEBEC[0], VersionAttributes.StringAttr, modifier.getNewValue("at EB EC"));
	}
	
	/**
	 * Test of the modification of the name of an element.
	 */
	public void test_12_ChangeName() throws Exception {
		final IRodinProject project = fetchProject("V08");

		convertProjectWithSuccess(project, 1);

		final IInternalElement[] elementsEA = getElements(project, "ff.tvh", IVersionEA.ELEMENT_TYPE, 2);
		final IInternalElement[] elementsEC = getElements(project, "ff.tvh", IVersionEC.ELEMENT_TYPE, 1);

		assertName("xx_a1_yy", elementsEA[0]);
		assertName("xx_a2_yy", elementsEA[1]);
		assertName("newName", elementsEC[0]);
	}

}

