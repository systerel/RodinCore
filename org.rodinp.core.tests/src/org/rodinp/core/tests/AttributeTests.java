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
	
	public static final String fBool = "org.rodinp.core.tests.fBool";
	public static final String fHandle = "org.rodinp.core.tests.fHandle";
	public static final String fInt = "org.rodinp.core.tests.fInt";
	public static final String fLong = "org.rodinp.core.tests.fLong";
	public static final String fString = "org.rodinp.core.tests.fString";
	
	public AttributeTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
	}
	
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	static final Set<String> knownAttributeNames =
		new HashSet<String>(Arrays.asList(new String[] {
			fBool,
			fInt,
			fHandle,
			fLong,
			fString,
		}));
	
	static void assertErrorCode(RodinDBException exception, int failureCode) {

		final IRodinDBStatus status = exception.getRodinDBStatus();
		assertEquals("Status should be an error",
				IRodinDBStatus.ERROR,
				status.getSeverity());
		assertEquals("Unexpected status code", 
				failureCode, 
				status.getCode());
	}
	
	static void assertAttributeNames(IInternalElement element,
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
	
	static void assertBooleanValue(IInternalElement element, String name,
			boolean expected) throws RodinDBException {
		
		assertTrue("Element should exist", element.exists());
		assertEquals("Unexpected attribute value", expected,
				element.getBooleanAttribute(name, null));
	}
	
	static void assertHandleValue(IInternalElement element, String name,
			IRodinElement expected) throws RodinDBException {
		
		assertTrue("Element should exist", element.exists());
		assertEquals("Unexpected attribute value", expected,
				element.getHandleAttribute(name, null));
	}
	
	static void assertIntegerValue(IInternalElement element, String name,
			int expected) throws RodinDBException {
		
		assertTrue("Element should exist", element.exists());
		assertEquals("Unexpected attribute value", expected,
				element.getIntegerAttribute(name, null));
	}
	
	static void assertLongValue(IInternalElement element, String name,
			long expected) throws RodinDBException {
		
		assertTrue("Element should exist", element.exists());
		assertEquals("Unexpected attribute value", expected,
				element.getLongAttribute(name, null));
	}
	
	static void assertStringValue(IInternalElement element, String name,
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
//			if (fBool.equals(name)) {
//				element.getBooleanAttribute(name, null);
//			}
//			if (fHandle.equals(name)) {
//				element.getHandleAttribute(name, null);
//			}
//			if (fInt.equals(name)) {
//				element.getIntegerAttribute(name, null);
//			}
//			if (fLong.equals(name)) {
//				element.getLongAttribute(name, null);
//			}
//			if (fString.equals(name)) {
//				element.getStringAttribute(name, null);
//			}
//			fail("Should have raised an exception");
//		} catch (RodinDBException e) {
//			assertErrorFor(e, IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST,
//					element);
//			// TODO check string when accessor is there.
//		}
//	}
	
	
	static void removeAttrPositive(IInternalElement element, String name)
			throws RodinDBException {

		assertTrue("Element should exist", element.exists());
		element.removeAttribute(name, null);
		assertFalse("Attribute should have been removed", 
				element.hasAttribute(name, null));
	}

	static void removeAttrNegative(IInternalElement element, String name,
			int failureCode) throws RodinDBException {
		
		try {
			element.removeAttribute(name, null);
			fail("Removing the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
		}
	}

	static void setBoolAttrPositive(IInternalElement element, String name,
			boolean newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setBooleanAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getBooleanAttribute(name, null));
	}

	static void setHandleAttrPositive(IInternalElement element, String name,
			IRodinElement newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setHandleAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getHandleAttribute(name, null));
	}

	static void setIntAttrPositive(IInternalElement element, String name,
			int newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setIntegerAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getIntegerAttribute(name, null));
	}

	static void setLongAttrPositive(IInternalElement element, String name,
			long newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setLongAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getLongAttribute(name, null));
	}

	static void setStringAttrPositive(IInternalElement element, String name,
			String newValue) throws RodinDBException {
		assertTrue("Element should exist", element.exists());
		element.setStringAttribute(name, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(name, null));
		assertEquals("New value should have been set",
				newValue, element.getStringAttribute(name, null));
	}

	static void setBoolAttrNegative(IInternalElement element, String name,
			boolean newValue, int failureCode) throws RodinDBException {
		try {
			element.setBooleanAttribute(name, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setHandleAttrNegative(IInternalElement element, String name,
			IRodinElement newValue, int failureCode) throws RodinDBException {
		try {
			element.setHandleAttribute(name, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setIntAttrNegative(IInternalElement element, String name,
			int newValue, int failureCode) throws RodinDBException {
		try {
			element.setIntegerAttribute(name, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setLongAttrNegative(IInternalElement element, String name,
			long newValue, int failureCode) throws RodinDBException {
		try {
			element.setLongAttribute(name, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}
	
	static void setStringAttrNegative(IInternalElement element, String name,
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
		
		setBoolAttrPositive(e1, fBool, true);
		assertAttributeNames(e1, fBool);

		setHandleAttrPositive(e1, fHandle, rf);
		assertAttributeNames(e1, fBool, fHandle);
		
		setIntAttrPositive(e1, fInt, -55);
		assertAttributeNames(e1, fBool, fHandle, fInt);

		setLongAttrPositive(e1, fLong, 12345678901L);
		assertAttributeNames(e1, fBool, fHandle, fInt, fLong);

		setStringAttrPositive(e1, fString, "bar");
		assertAttributeNames(e1, fBool, fHandle, fInt, fLong, fString);
		
		removeAttrPositive(e1, fLong);
		assertAttributeNames(e1, fBool, fHandle, fInt, fString);
	}

	/**
	 * Ensures that the removeAttribute() method works properly on internal
	 * elements.
	 */
	public void testRemoveAttribute() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null); 
		
		setBoolAttrPositive(e1, fBool, true);
		removeAttrPositive(e1, fBool);

		// Can be done twice
		removeAttrPositive(e1, fBool);
	}

	/**
	 * Ensures that attempting to remove an attribute on an inexistent element
	 * raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testRemoveAttributeNoElement() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = getNamedElement(rf, "foo"); 
		final int code = IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
		assertFalse("Element should not exist", e1.exists());

		removeAttrNegative(e1, fBool, code);
	}

	/**
	 * Ensures that attempting to set an attribute on an inexistent element
	 * raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testSetAttributeNoElement() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = getNamedElement(rf, "foo"); 
		final int code = IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
		assertFalse("Element should not exist", e1.exists());

		setBoolAttrNegative(e1, fBool, true, code);
		setHandleAttrNegative(e1, fHandle, rf, code);
		setIntAttrNegative(e1, fInt, -55, code);
		setLongAttrNegative(e1, fLong, 12345678901L, code);
		setStringAttrNegative(e1, fString, "bar", code);
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

		setBoolAttrNegative(e1, fHandle, true, code);
		setBoolAttrNegative(e1, fInt,    true, code);
		setBoolAttrNegative(e1, fLong,   true, code);
		setBoolAttrNegative(e1, fString, true, code);
		assertAttributeNames(e1);

		setHandleAttrNegative(e1, fBool,   rf, code);
		setHandleAttrNegative(e1, fInt,    rf, code);
		setHandleAttrNegative(e1, fLong,   rf, code);
		setHandleAttrNegative(e1, fString, rf, code);
		assertAttributeNames(e1);

		setIntAttrNegative(e1, fBool,   256, code);
		setIntAttrNegative(e1, fHandle, 256, code);
		setIntAttrNegative(e1, fLong,  256, code);
		setIntAttrNegative(e1, fString, 256, code);
		assertAttributeNames(e1);
		
		setLongAttrNegative(e1, fBool,   12345678901L, code);
		setLongAttrNegative(e1, fHandle, 12345678901L, code);
		setLongAttrNegative(e1, fInt,    12345678901L, code);
		setLongAttrNegative(e1, fString, 12345678901L, code);
		assertAttributeNames(e1);
		
		setStringAttrNegative(e1, fBool,   "bar", code);
		setStringAttrNegative(e1, fHandle, "bar", code);
		setStringAttrNegative(e1, fInt,    "bar", code);
		setStringAttrNegative(e1, fLong,    "bar", code);
		assertAttributeNames(e1);
	}

	
	public void testSnapshotAttributeReadOnly() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		final int code = IRodinDBStatusConstants.READ_ONLY;
		rf.save(null, false);

		final InternalElement snapshot = e1.getSnapshot();
		removeAttrNegative(snapshot, fBool, code);
		setBoolAttrNegative(snapshot, fBool, true, code);
		setHandleAttrNegative(snapshot, fHandle, rf, code);
		setIntAttrNegative(snapshot, fInt, -55, code);
		setLongAttrNegative(snapshot, fLong, 12345678901L, code);
		setStringAttrNegative(snapshot, fString, "bar", code);
	}

	public void testAttributePersistent() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		setBoolAttrPositive(e1, fBool, true);
		setHandleAttrPositive(e1, fHandle, rf);
		setIntAttrPositive(e1, fInt, -55);
		setLongAttrPositive(e1, fLong, 12345678901L);
		setStringAttrPositive(e1, fString, "bar");
		rf.save(null, false);

		final InternalElement snapshot = e1.getSnapshot();
		assertBooleanValue(snapshot, fBool, true);
		assertHandleValue(snapshot, fHandle, rf);
		assertIntegerValue(snapshot, fInt, -55);
		assertLongValue(snapshot, fLong, 12345678901L);
		assertStringValue(snapshot, fString, "bar");

		assertBooleanValue(e1, fBool, true);
		assertHandleValue(e1, fHandle, rf);
		assertIntegerValue(e1, fInt, -55);
		assertLongValue(e1, fLong, 12345678901L);
		assertStringValue(e1, fString, "bar");
	}

}
