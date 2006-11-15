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
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
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
	
	public static final IAttributeType.Boolean fBool = 
		RodinCore.getBooleanAttrType("org.rodinp.core.tests.fBool");
	public static final IAttributeType.Handle fHandle =
		RodinCore.getHandleAttrType("org.rodinp.core.tests.fHandle");
	public static final IAttributeType.Integer fInt =
		RodinCore.getIntegerAttrType("org.rodinp.core.tests.fInt");
	public static final IAttributeType.Long fLong = 
		RodinCore.getLongAttrType("org.rodinp.core.tests.fLong");
	public static final IAttributeType.String fString = 
		RodinCore.getStringAttrType("org.rodinp.core.tests.fString");
	
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

	static final Set<IAttributeType> knownAttributeTypes =
		new HashSet<IAttributeType>(Arrays.asList(new IAttributeType[] {
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
			IAttributeType... expectedTypes) throws RodinDBException {
		
		assertExists("Element should exist", element);
		IAttributeType[] actualTypes = element.getAttributeTypes(null);
		Set<IAttributeType> expected =
			new HashSet<IAttributeType>(Arrays.asList(expectedTypes));
		Set<IAttributeType> actual =
			new HashSet<IAttributeType>(Arrays.asList(actualTypes));
		assertEquals("Unexpected attribute types", expected, actual);
		for (IAttributeType name: actualTypes) {
			assertTrue("Returned type should exist", element.hasAttribute(name, null));
		}
		Set<IAttributeType> inexistent =
			new HashSet<IAttributeType>(knownAttributeTypes);
		inexistent.removeAll(actual);
		for (IAttributeType type: inexistent) {
			assertFalse("Unreturned name shoud not exist",
					element.hasAttribute(type, null));
		}
	}
	
	static void assertBooleanValue(IInternalElement element,
			IAttributeType.Boolean type, boolean expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type, null));
	}
	
	static void assertHandleValue(IInternalElement element,
			IAttributeType.Handle type, IRodinElement expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type, null));
	}
	
	static void assertIntegerValue(IInternalElement element,
			IAttributeType.Integer type, int expected) throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type, null));
	}
	
	static void assertLongValue(IInternalElement element,
			IAttributeType.Long type, long expected) throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type, null));
	}
	
	static void assertStringValue(IInternalElement element,
			IAttributeType.String type, String expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type, null));
	}
	
//	private void assertNoAttribute(IInternalElement element, IAttributeType type)
//			throws RodinDBException {
//		
//		assertExists("Element should exist", element);
//		try {
//			if (type == fBool) {
//				element.getBooleanAttribute(type, null);
//			}
//			if (type == fHandle) {
//				element.getHandleAttribute(type, null);
//			}
//			if (type == fInt) {
//				element.getIntegerAttribute(type, null);
//			}
//			if (type == fLong) {
//				element.getLongAttribute(type, null);
//			}
//			if (type == fString) {
//				element.getStringAttribute(type, null);
//			}
//			fail("Should have raised an exception");
//		} catch (RodinDBException e) {
//			assertErrorFor(e, IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST,
//					element);
//			// TODO check string when accessor is there.
//		}
//	}
	
	
	static void removeAttrPositive(IInternalElement element, IAttributeType type)
			throws RodinDBException {

		assertExists("Element should exist", element);
		element.removeAttribute(type, null);
		assertFalse("Attribute should have been removed", 
				element.hasAttribute(type, null));
	}

	static void removeAttrNegative(IInternalElement element, IAttributeType type,
			int failureCode) throws RodinDBException {
		
		try {
			element.removeAttribute(type, null);
			fail("Removing the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
		}
	}

	static void setBoolAttrPositive(IInternalElement element, IAttributeType.Boolean type,
			boolean newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type, null));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type, null));
	}

	static void setHandleAttrPositive(IInternalElement element, IAttributeType.Handle type,
			IRodinElement newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type, null));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type, null));
	}

	static void setIntAttrPositive(IInternalElement element, IAttributeType.Integer type,
			int newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type, null));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type, null));
	}

	static void setLongAttrPositive(IInternalElement element, IAttributeType.Long type,
			long newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type, null));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type, null));
	}

	static void setStringAttrPositive(IInternalElement element, IAttributeType.String type,
			String newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type, null));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type, null));
	}

	static void setBoolAttrNegative(IInternalElement element,
			IAttributeType.Boolean type, boolean newValue, int failureCode)
			throws RodinDBException {

		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setHandleAttrNegative(IInternalElement element,
			IAttributeType.Handle type, IRodinElement newValue, int failureCode)
			throws RodinDBException {

		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setIntAttrNegative(IInternalElement element,
			IAttributeType.Integer type, int newValue, int failureCode)
			throws RodinDBException {
		
		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setLongAttrNegative(IInternalElement element,
			IAttributeType.Long type, long newValue, int failureCode)
			throws RodinDBException {
		
		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}
	
	static void setStringAttrNegative(IInternalElement element,
			IAttributeType.String type, String newValue, int failureCode)
			throws RodinDBException {
		
		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}
	
	/**
	 * Ensures that the getAttributeTypes() and hasAttribute() methods works
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
		assertNotExists("Element should not exist", e1);

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
		assertNotExists("Element should not exist", e1);

		setBoolAttrNegative(e1, fBool, true, code);
		setHandleAttrNegative(e1, fHandle, rf, code);
		setIntAttrNegative(e1, fInt, -55, code);
		setLongAttrNegative(e1, fLong, 12345678901L, code);
		setStringAttrNegative(e1, fString, "bar", code);
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
