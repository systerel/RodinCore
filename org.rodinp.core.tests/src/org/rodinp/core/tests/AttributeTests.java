/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * Tests about attributes of Rodin internal elements.
 * 
 * @author Laurent Voisin
 */

// TODO finish tests: test for all error conditions
//TODO finish tests: test for generated deltas

public class AttributeTests extends ModifyingResourceTests {
	
	public AttributeTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
	}
	
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	static final Set<String> knownAttributeNames =
		new HashSet<String>(Arrays.asList(new String[] {
			"fBool",
			"fInt",
			"fHandle",
			"fString",
		}));
	
	private void assertErrorCode(RodinDBException exception, int failureCode) {
		final IRodinDBStatus status = exception.getRodinDBStatus();
		assertEquals("Status should be an error",
				IRodinDBStatus.ERROR,
				status.getSeverity());
		assertEquals("Unexpected status code", 
				failureCode, 
				status.getCode());
	}
	
	private void assertAttributeNames(IInternalElement element,
			String... expectedNames) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		String[] actualNames = element.getAttributeNames(null);
		Set<String> expected = new HashSet<String>(Arrays.asList(expectedNames));
		Set<String> actual = new HashSet<String>(Arrays.asList(actualNames));
		assertEquals("Unexpected attribute names", expected, actual);
		for (String name: actualNames) {
			assertTrue("Returned name shoud exist", element.hasAttribute(name, null));
		}
		Set<String> inexistent = new HashSet<String>(knownAttributeNames);
		inexistent.removeAll(actual);
		for (String name: inexistent) {
			assertFalse("Unreturned name shoud not exist",
					element.hasAttribute(name, null));
		}
	}
	
	private void assertBooleanValue(IInternalElement element, String name,
			boolean expected) throws RodinDBException {
		
		assertTrue("Element should exist", element.exists());
		assertEquals("Unexpected attribute value", expected,
				element.getBooleanAttribute(name, null));
	}
	
	private void assertHandleValue(IInternalElement element, String name,
			IRodinElement expected) throws RodinDBException {
		
		assertTrue("Element should exist", element.exists());
		assertEquals("Unexpected attribute value", expected,
				element.getHandleAttribute(name, null));
	}
	
	private void assertIntegerValue(IInternalElement element, String name,
			int expected) throws RodinDBException {
		
		assertTrue("Element should exist", element.exists());
		assertEquals("Unexpected attribute value", expected,
				element.getIntegerAttribute(name, null));
	}
	
	private void assertStringValue(IInternalElement element, String name,
			String expected) throws RodinDBException {
		
		assertTrue("Element should exist", element.exists());
		assertEquals("Unexpected attribute value", expected,
				element.getStringAttribute(name, null));
	}
	
//	private void assertNoAttribute(IInternalElement element, String name)
//			throws RodinDBException {
//		
//		assertTrue("Element should exist", element.exists());
//		try {
//			if ("fBool".equals(name)) {
//				element.getBooleanAttribute(name, null);
//			}
//			if ("fHandle".equals(name)) {
//				element.getHandleAttribute(name, null);
//			}
//			if ("fInt".equals(name)) {
//				element.getIntegerAttribute(name, null);
//			}
//			if ("fString".equals(name)) {
//				element.getStringAttribute(name, null);
//			}
//			fail("Should have raised an exception");
//		} catch (RodinDBException e) {
//			assertErrorFor(e, IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST,
//					element);
//			// TODO check string when accessor is there.
//		}
//	}
	
	
	void setBoolAttrPositive(IInternalElement element, String name,
			boolean newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setBooleanAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getBooleanAttribute(name, null));
	}

	void setHandleAttrPositive(IInternalElement element, String name,
			IRodinElement newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setHandleAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getHandleAttribute(name, null));
	}

	void setIntAttrPositive(IInternalElement element, String name,
			int newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setIntegerAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getIntegerAttribute(name, null));
	}

	void setStringAttrPositive(IInternalElement element, String name,
			String newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setStringAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getStringAttribute(name, null));
	}

	void setBoolAttrNegative(IInternalElement element, String name,
			boolean newValue, int failureCode) throws RodinDBException {
		try {
			element.setBooleanAttribute(name, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	void setHandleAttrNegative(IInternalElement element, String name,
			IRodinElement newValue, int failureCode) throws RodinDBException {
		try {
			element.setHandleAttribute(name, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	void setIntAttrNegative(IInternalElement element, String name,
			int newValue, int failureCode) throws RodinDBException {
		try {
			element.setIntegerAttribute(name, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	void setStringAttrNegative(IInternalElement element, String name,
			String newValue, int failureCode) throws RodinDBException {
		try {
			element.setStringAttribute(name, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}
	
	/**
	 * Ensures that the getAttributeNames() and hasAttribute() methods works
	 * properly on internal elements.
	 */
	public void testGetAttributeNames() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null); 
		assertAttributeNames(e1);
		
		setBoolAttrPositive(e1, "fBool", true);
		assertAttributeNames(e1, "fBool");

		setHandleAttrPositive(e1, "fHandle", rf);
		assertAttributeNames(e1, "fBool", "fHandle");
		
		setIntAttrPositive(e1, "fInt", -55);
		assertAttributeNames(e1, "fBool", "fHandle", "fInt");

		setStringAttrPositive(e1, "fString", "bar");
		assertAttributeNames(e1, "fBool", "fHandle", "fInt", "fString");
	}

	/**
	 * Ensures that attempting to set an attribute on an inexistent element
	 * raises an INVALID_ATTRIBUTE_KIND error.
	 */
	public void testSetAttributeNoElement() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = getNamedElement(rf, "foo"); 
		final int code = IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
		assertFalse("Element should not exist", e1.exists());

		setBoolAttrNegative(e1, "fBool", true, code);
		setHandleAttrNegative(e1, "fHandle", rf, code);
		setIntAttrNegative(e1, "fInt", -55, code);
		setStringAttrNegative(e1, "fString", "bar", code);
	}

	/**
	 * Ensures that attempting to set an attribute with the wrong kind raises
	 * an INVALID_ATTRIBUTE_KIND error.
	 */
	public void testSetAttributeWrongKind() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		final int code = IRodinDBStatusConstants.INVALID_ATTRIBUTE_KIND;
		
		assertAttributeNames(e1);

		setBoolAttrNegative(e1, "fHandle", true, code);
		setBoolAttrNegative(e1, "fInt",    true, code);
		setBoolAttrNegative(e1, "fString", true, code);
		assertAttributeNames(e1);

		setHandleAttrNegative(e1, "fBool",   rf, code);
		setHandleAttrNegative(e1, "fInt",    rf, code);
		setHandleAttrNegative(e1, "fString", rf, code);
		assertAttributeNames(e1);

		setIntAttrNegative(e1, "fBool",   256, code);
		setIntAttrNegative(e1, "fHandle", 256, code);
		setIntAttrNegative(e1, "fString", 256, code);
		assertAttributeNames(e1);
		
		setStringAttrNegative(e1, "fBool",   "bar", code);
		setStringAttrNegative(e1, "fHandle", "bar", code);
		setStringAttrNegative(e1, "fInt",    "bar", code);
		assertAttributeNames(e1);
	}

	
	public void testSnapshotAttributeReadOnly() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		final int code = IRodinDBStatusConstants.READ_ONLY;
		rf.save(null, false);

		final InternalElement snapshot = e1.getSnapshot();
		setBoolAttrNegative(snapshot, "fBool", true, code);
		setHandleAttrNegative(snapshot, "fHandle", rf, code);
		setIntAttrNegative(snapshot, "fInt", -55, code);
		setStringAttrNegative(snapshot, "fString", "bar", code);
	}

	public void testAttributePersistent() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		setBoolAttrPositive(e1, "fBool", true);
		setHandleAttrPositive(e1, "fHandle", rf);
		setIntAttrPositive(e1, "fInt", -55);
		setStringAttrPositive(e1, "fString", "bar");
		rf.save(null, false);

		final InternalElement snapshot = e1.getSnapshot();
		assertBooleanValue(snapshot, "fBool", true);
		assertHandleValue(snapshot, "fHandle", rf);
		assertIntegerValue(snapshot, "fInt", -55);
		assertStringValue(snapshot, "fString", "bar");

		assertBooleanValue(e1, "fBool", true);
		assertHandleValue(e1, "fHandle", rf);
		assertIntegerValue(e1, "fInt", -55);
		assertStringValue(e1, "fString", "bar");
	}

}
