/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added checks of generated deltas
 *     Systerel - separation of file and root element
 *******************************************************************************/

package org.rodinp.core.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.RodinTestRoot;

/**
 * Tests about attributes of Rodin internal elements.
 * 
 * @author Laurent Voisin
 */

// TODO finish tests: test for all error conditions

public class AttributeTests extends ModifyingResourceTests {
	
	public AttributeTests(String name) {
		super(name);
	}
	
	private IRodinFile rodinFile;
	private RodinTestRoot root;
	private NamedElement ne;

	protected void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		rodinFile = createRodinFile("P/X.test");
		root = (RodinTestRoot) rodinFile.getRoot();
		ne = createNEPositive(root, "foo", null);
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
	
	void removeAttrPositive(IAttributedElement element, IAttributeType type)
			throws RodinDBException {

		assertExists("Element should exist", element);
		final boolean attributeExists = element.hasAttribute(type);
		try {
			startDeltas();
			element.removeAttribute(type, null);
			if (attributeExists) {
				assertAttributeDelta(element);
			} else {
				assertNoDelta(element);
			}
		} finally {
			stopDeltas();
		}

		assertFalse("Attribute should have been removed", element
				.hasAttribute(type));
	}

	private void assertNoDelta(IAttributedElement element) {
		final IRodinElementDelta delta = getDeltaFor(element, true);
		assertNull("No delta should have been generated", delta);
	}

	private void assertAttributeDelta(IAttributedElement element) {
		final IRodinElementDelta delta = getDeltaFor(element, true);
		assertNotNull("No delta", delta);
		assertEquals("Wrong delta kind", IRodinElementDelta.CHANGED,
				delta.getKind());
		assertEquals("Wrong delta flag",
				IRodinElementDelta.F_ATTRIBUTE, delta.getFlags());
	}

	void removeAttrNegative(IAttributedElement element, IAttributeType type,
			int failureCode) throws RodinDBException {
		
		try {
			startDeltas();
			element.removeAttribute(type, null);
			fail("Removing the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
			assertNoDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setBoolAttrPositive(IAttributedElement element,
			IAttributeType.Boolean type, boolean newValue)
			throws RodinDBException {
		assertExists("Element should exist", element);
		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			assertTrue("Attribute should have been created", element
					.hasAttribute(type));
			assertEquals("New value should have been set", newValue, element
					.getAttributeValue(type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setHandleAttrPositive(IAttributedElement element,
			IAttributeType.Handle type, IRodinElement newValue)
			throws RodinDBException {
		assertExists("Element should exist", element);
		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			assertTrue("Attribute should have been created", element
					.hasAttribute(type));
			assertEquals("New value should have been set", newValue, element
					.getAttributeValue(type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setIntAttrPositive(IAttributedElement element,
			IAttributeType.Integer type, int newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			assertTrue("Attribute should have been created", element
					.hasAttribute(type));
			assertEquals("New value should have been set", newValue, element
					.getAttributeValue(type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setLongAttrPositive(IAttributedElement element,
			IAttributeType.Long type, long newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			assertTrue("Attribute should have been created", element
					.hasAttribute(type));
			assertEquals("New value should have been set", newValue, element
					.getAttributeValue(type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setStringAttrPositive(IAttributedElement element,
			IAttributeType.String type, String newValue)
			throws RodinDBException {
		assertExists("Element should exist", element);
		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			assertTrue("Attribute should have been created", element
					.hasAttribute(type));
			assertEquals("New value should have been set", newValue, element
					.getAttributeValue(type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setBoolAttrNegative(IAttributedElement element,
			IAttributeType.Boolean type, boolean newValue, int failureCode)
			throws RodinDBException {

		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
			assertNoDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setHandleAttrNegative(IAttributedElement element,
			IAttributeType.Handle type, IRodinElement newValue, int failureCode)
			throws RodinDBException {

		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
			assertNoDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setIntAttrNegative(IAttributedElement element,
			IAttributeType.Integer type, int newValue, int failureCode)
			throws RodinDBException {

		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
			assertNoDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setLongAttrNegative(IAttributedElement element,
			IAttributeType.Long type, long newValue, int failureCode)
			throws RodinDBException {

		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
			assertNoDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setStringAttrNegative(IAttributedElement element,
			IAttributeType.String type, String newValue, int failureCode)
			throws RodinDBException {

		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			fail("Setting the attribute should have failed");
		} catch (RodinDBException e) {
			assertErrorCode(e, failureCode);
			assertNoDelta(element);
		} finally {
			stopDeltas();
		}
	}
	
	private void assertGetAttributeNames(IInternalElement ie)
			throws CoreException {

		assertAttributeNames(ie);
		
		setBoolAttrPositive(ie, fBool, true);
		assertAttributeNames(ie, fBool);

		setHandleAttrPositive(ie, fHandle, ie);
		assertAttributeNames(ie, fBool, fHandle);
		
		setIntAttrPositive(ie, fInt, -55);
		assertAttributeNames(ie, fBool, fHandle, fInt);

		setLongAttrPositive(ie, fLong, 12345678901L);
		assertAttributeNames(ie, fBool, fHandle, fInt, fLong);

		setStringAttrPositive(ie, fString, "bar");
		assertAttributeNames(ie, fBool, fHandle, fInt, fLong, fString);
		
		removeAttrPositive(ie, fLong);
		assertAttributeNames(ie, fBool, fHandle, fInt, fString);
	}
	
	/**
	 * Ensures that the getAttributeTypes() and hasAttribute() methods works
	 * properly on Rodin files.
	 */
	public void testGetAttributeNamesFile() throws CoreException {
		assertGetAttributeNames(root);
	}

	/**
	 * Ensures that the getAttributeTypes() and hasAttribute() methods works
	 * properly on internal elements.
	 */
	public void testGetAttributeNamesInternal() throws CoreException {
		assertGetAttributeNames(ne);
	}

	private void assertRemoveAttribute(IInternalElement ie)
			throws CoreException {

		setBoolAttrPositive(ie, fBool, true);
		removeAttrPositive(ie, fBool);

		// Can be done twice
		removeAttrPositive(ie, fBool);
	}

	/**
	 * Ensures that the removeAttribute() method works properly on Rodin files.
	 */
	public void testRemoveAttributeFile() throws CoreException {
		assertRemoveAttribute(root);
	}

	/**
	 * Ensures that the removeAttribute() method works properly on internal
	 * elements.
	 */
	public void testRemoveAttributeInternal() throws CoreException {
		assertRemoveAttribute(ne);
	}

	private void assertRemoveAttributeNoElement(IInternalElement ie)
			throws CoreException {
		assertFalse(ie.exists());

		final int code = IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
		assertNotExists("Element should not exist", ie);

		removeAttrNegative(ie, fBool, code);
	}

	/**
	 * Ensures that attempting to remove an attribute on an inexistent Rodin
	 * file raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testRemoveAttributeNoElementFile() throws CoreException {
		rodinFile.delete(false, null);
		assertRemoveAttributeNoElement(root);
	}

	/**
	 * Ensures that attempting to remove an attribute on an inexistent internal
	 * element raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testRemoveAttributeNoElementInternal() throws CoreException {
		ne.delete(false, null);
		assertRemoveAttributeNoElement(ne);
	}

	private void assertSetAttributeNoElement(IInternalElement ie)
			throws CoreException {

		final int code = IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
		assertNotExists("Element should not exist", ie);

		setBoolAttrNegative(ie, fBool, true, code);
		setHandleAttrNegative(ie, fHandle, ie, code);
		setIntAttrNegative(ie, fInt, -55, code);
		setLongAttrNegative(ie, fLong, 12345678901L, code);
		setStringAttrNegative(ie, fString, "bar", code);
	}

	/**
	 * Ensures that attempting to set an attribute on an inexistent Rodin file
	 * raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testSetAttributeNoElementRoot() throws CoreException {
		rodinFile.delete(false, null);
		assertSetAttributeNoElement(root);
	}

	/**
	 * Ensures that attempting to set an attribute on an inexistent internal
	 * element raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	public void testSetAttributeNoElementInternal() throws CoreException {
		ne.delete(false, null);
		assertSetAttributeNoElement(ne);
	}

	private void assertSnapshotAttributeReadOnly(IInternalElement ie)
			throws CoreException {

		final int code = IRodinDBStatusConstants.READ_ONLY;
		rodinFile.save(null, false);

		final IInternalElement snapshot = (IInternalElement) ie.getSnapshot();
		removeAttrNegative(snapshot, fBool, code);
		setBoolAttrNegative(snapshot, fBool, true, code);
		setHandleAttrNegative(snapshot, fHandle, rodinFile, code);
		setIntAttrNegative(snapshot, fInt, -55, code);
		setLongAttrNegative(snapshot, fLong, 12345678901L, code);
		setStringAttrNegative(snapshot, fString, "bar", code);
	}

	public void testSnapshotAttributeReadOnlyRoot() throws CoreException {
		assertSnapshotAttributeReadOnly(root);
	}

	public void testSnapshotAttributeReadOnlyInternal() throws CoreException {
		assertSnapshotAttributeReadOnly(ne);
	}

	
	private void assertAttributePersistent(IInternalElement ie)
			throws CoreException {

		setBoolAttrPositive(ie, fBool, true);
		setHandleAttrPositive(ie, fHandle, rodinFile);
		setIntAttrPositive(ie, fInt, -55);
		setLongAttrPositive(ie, fLong, 12345678901L);
		setStringAttrPositive(ie, fString, "bar");

		rodinFile.save(null, false);

		final IInternalElement snapshot = ie.getSnapshot();
		assertBooleanValue(snapshot, fBool, true);
		assertHandleValue(snapshot, fHandle, rodinFile);
		assertIntegerValue(snapshot, fInt, -55);
		assertLongValue(snapshot, fLong, 12345678901L);
		assertStringValue(snapshot, fString, "bar");

		assertBooleanValue(ie, fBool, true);
		assertHandleValue(ie, fHandle, rodinFile);
		assertIntegerValue(ie, fInt, -55);
		assertLongValue(ie, fLong, 12345678901L);
		assertStringValue(ie, fString, "bar");
	}
	
	public void testAttributePersistentFile() throws CoreException {
		assertAttributePersistent(root);
	}

	public void testAttributePersistentInternal() throws CoreException {
		assertAttributePersistent(ne);
	}

}
