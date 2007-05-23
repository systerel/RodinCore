/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
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
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
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
	
	static void assertAttributeNames(IAttributedElement element,
			IAttributeType... expectedTypes) throws RodinDBException {
		
		assertExists("Element should exist", element);
		IAttributeType[] actualTypes = element.getAttributeTypes();
		Set<IAttributeType> expected =
			new HashSet<IAttributeType>(Arrays.asList(expectedTypes));
		Set<IAttributeType> actual =
			new HashSet<IAttributeType>(Arrays.asList(actualTypes));
		assertEquals("Unexpected attribute types", expected, actual);
		for (IAttributeType name: actualTypes) {
			assertTrue("Returned type should exist", element.hasAttribute(name));
		}
		Set<IAttributeType> inexistent =
			new HashSet<IAttributeType>(knownAttributeTypes);
		inexistent.removeAll(actual);
		for (IAttributeType type: inexistent) {
			assertFalse("Unreturned name shoud not exist",
					element.hasAttribute(type));
		}
	}
	
	static void assertBooleanValue(IAttributedElement element,
			IAttributeType.Boolean type, boolean expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void assertHandleValue(IAttributedElement element,
			IAttributeType.Handle type, IRodinElement expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void assertIntegerValue(IAttributedElement element,
			IAttributeType.Integer type, int expected) throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void assertLongValue(IAttributedElement element,
			IAttributeType.Long type, long expected) throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void assertStringValue(IAttributedElement element,
			IAttributeType.String type, String expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void removeAttrPositive(IAttributedElement element, IAttributeType type)
			throws RodinDBException {

		assertExists("Element should exist", element);
		element.removeAttribute(type, null);
		assertFalse("Attribute should have been removed", 
				element.hasAttribute(type));
	}

	static void removeAttrNegative(IAttributedElement element, IAttributeType type,
			int failureCode) throws RodinDBException {
		
		try {
			element.removeAttribute(type, null);
			fail("Removing the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
		}
	}

	static void setBoolAttrPositive(IAttributedElement element, IAttributeType.Boolean type,
			boolean newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type));
	}

	static void setHandleAttrPositive(IAttributedElement element, IAttributeType.Handle type,
			IRodinElement newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type));
	}

	static void setIntAttrPositive(IAttributedElement element, IAttributeType.Integer type,
			int newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type));
	}

	static void setLongAttrPositive(IAttributedElement element, IAttributeType.Long type,
			long newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type));
	}

	static void setStringAttrPositive(IAttributedElement element, IAttributeType.String type,
			String newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		element.setAttributeValue(type, newValue, null);
		assertTrue("Attribute should have been created", 
				element.hasAttribute(type));
		assertEquals("New value should have been set",
				newValue, element.getAttributeValue(type));
	}

	static void setBoolAttrNegative(IAttributedElement element,
			IAttributeType.Boolean type, boolean newValue, int failureCode)
			throws RodinDBException {

		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setHandleAttrNegative(IAttributedElement element,
			IAttributeType.Handle type, IRodinElement newValue, int failureCode)
			throws RodinDBException {

		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setIntAttrNegative(IAttributedElement element,
			IAttributeType.Integer type, int newValue, int failureCode)
			throws RodinDBException {
		
		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}

	static void setLongAttrNegative(IAttributedElement element,
			IAttributeType.Long type, long newValue, int failureCode)
			throws RodinDBException {
		
		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}
	
	static void setStringAttrNegative(IAttributedElement element,
			IAttributeType.String type, String newValue, int failureCode)
			throws RodinDBException {
		
		try {
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
				assertErrorCode(e, failureCode);
		}
	}
	
	private void testGetAttributeNames(IAttributedElement ae)
			throws CoreException {

		assertAttributeNames(ae);
		
		setBoolAttrPositive(ae, fBool, true);
		assertAttributeNames(ae, fBool);

		setHandleAttrPositive(ae, fHandle, ae);
		assertAttributeNames(ae, fBool, fHandle);
		
		setIntAttrPositive(ae, fInt, -55);
		assertAttributeNames(ae, fBool, fHandle, fInt);

		setLongAttrPositive(ae, fLong, 12345678901L);
		assertAttributeNames(ae, fBool, fHandle, fInt, fLong);

		setStringAttrPositive(ae, fString, "bar");
		assertAttributeNames(ae, fBool, fHandle, fInt, fLong, fString);
		
		removeAttrPositive(ae, fLong);
		assertAttributeNames(ae, fBool, fHandle, fInt, fString);
	}
	
	/**
	 * Ensures that the getAttributeTypes() and hasAttribute() methods works
	 * properly on Rodin files.
	 */
	public void testGetAttributeNamesFile() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		testGetAttributeNames(rf);
	}

	/**
	 * Ensures that the getAttributeTypes() and hasAttribute() methods works
	 * properly on internal elements.
	 */
	public void testGetAttributeNamesInternal() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		testGetAttributeNames(e1);
	}

	private void testRemoveAttribute(IAttributedElement ae)
			throws CoreException {

		setBoolAttrPositive(ae, fBool, true);
		removeAttrPositive(ae, fBool);

		// Can be done twice
		removeAttrPositive(ae, fBool);
	}

	/**
	 * Ensures that the removeAttribute() method works properly on Rodin files.
	 */
	public void testRemoveAttributeFile() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		testRemoveAttribute(rf);
	}

	/**
	 * Ensures that the removeAttribute() method works properly on internal
	 * elements.
	 */
	public void testRemoveAttributeInternal() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		testRemoveAttribute(e1);
	}

	private void testRemoveAttributeNoElement(IAttributedElement ae)
			throws CoreException {

		final int code = IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
		assertNotExists("Element should not exist", ae);

		removeAttrNegative(ae, fBool, code);
	}

	/**
	 * Ensures that attempting to remove an attribute on an inexistent Rodin
	 * file raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testRemoveAttributeNoElementFile() throws CoreException {
		final IRodinFile rf = getRodinFile("P/X.test");
		testRemoveAttributeNoElement(rf);
	}

	/**
	 * Ensures that attempting to remove an attribute on an inexistent internal
	 * element raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testRemoveAttributeNoElementInternal() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = getNamedElement(rf, "foo");
		testRemoveAttributeNoElement(e1);
	}

	private void testSetAttributeNoElement(IAttributedElement ae)
			throws CoreException {

		final int code = IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
		assertNotExists("Element should not exist", ae);

		setBoolAttrNegative(ae, fBool, true, code);
		setHandleAttrNegative(ae, fHandle, ae, code);
		setIntAttrNegative(ae, fInt, -55, code);
		setLongAttrNegative(ae, fLong, 12345678901L, code);
		setStringAttrNegative(ae, fString, "bar", code);
	}

	/**
	 * Ensures that attempting to set an attribute on an inexistent Rodin file
	 * raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testSetAttributeNoElementFile() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = getNamedElement(rf, "foo");
		testSetAttributeNoElement(e1);
	}

	/**
	 * Ensures that attempting to set an attribute on an inexistent internal
	 * element raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testSetAttributeNoElementInternal() throws CoreException {
		final IRodinFile rf = getRodinFile("P/X.test");
		testSetAttributeNoElement(rf);
	}

	private void testSnapshotAttributeReadOnly(IRodinFile rf, IInternalParent ae)
			throws CoreException {

		final int code = IRodinDBStatusConstants.READ_ONLY;
		rf.save(null, false);

		final IInternalParent snapshot = ae.getSnapshot();
		removeAttrNegative(snapshot, fBool, code);
		setBoolAttrNegative(snapshot, fBool, true, code);
		setHandleAttrNegative(snapshot, fHandle, rf, code);
		setIntAttrNegative(snapshot, fInt, -55, code);
		setLongAttrNegative(snapshot, fLong, 12345678901L, code);
		setStringAttrNegative(snapshot, fString, "bar", code);
	}

	public void testSnapshotAttributeReadOnlyFile() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		testSnapshotAttributeReadOnly(rf, rf);
	}

	public void testSnapshotAttributeReadOnlyInternal() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		testSnapshotAttributeReadOnly(rf, e1);
	}

	
	private void testAttributePersistent(IRodinFile rf, IInternalParent ae)
			throws CoreException {

		setBoolAttrPositive(ae, fBool, true);
		setHandleAttrPositive(ae, fHandle, rf);
		setIntAttrPositive(ae, fInt, -55);
		setLongAttrPositive(ae, fLong, 12345678901L);
		setStringAttrPositive(ae, fString, "bar");

		rf.save(null, false);

		final IInternalParent snapshot = ae.getSnapshot();
		assertBooleanValue(snapshot, fBool, true);
		assertHandleValue(snapshot, fHandle, rf);
		assertIntegerValue(snapshot, fInt, -55);
		assertLongValue(snapshot, fLong, 12345678901L);
		assertStringValue(snapshot, fString, "bar");

		assertBooleanValue(ae, fBool, true);
		assertHandleValue(ae, fHandle, rf);
		assertIntegerValue(ae, fInt, -55);
		assertLongValue(ae, fLong, 12345678901L);
		assertStringValue(ae, fString, "bar");
	}
	
	public void testAttributePersistentFile() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		testAttributePersistent(rf, rf);
	}

	public void testAttributePersistentInternal() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final NamedElement e1 = createNEPositive(rf, "foo", null);
		testAttributePersistent(rf, e1);
	}

}
