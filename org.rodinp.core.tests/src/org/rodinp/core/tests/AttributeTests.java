/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added checks of generated deltas
 *     Systerel - separation of file and root element
 *     Systerel - added tests for extension point processing
 *     Systerel - generic attribute manipulation
 *******************************************************************************/
package org.rodinp.core.tests;

import static java.util.Arrays.asList;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_ATTRIBUTE_TYPE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.NamedElement2;
import org.rodinp.core.tests.basis.RodinTestRoot;
import org.rodinp.internal.core.AttributeType;

/**
 * Tests about attributes of Rodin internal elements.
 * 
 * @author Laurent Voisin
 */

// TODO finish tests: test for all error conditions

public class AttributeTests extends ModifyingResourceTests {
	
	private IRodinFile rodinFile;
	private RodinTestRoot root;
	private NamedElement ne;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		rodinFile = createRodinFile("P/X.test");
		root = (RodinTestRoot) rodinFile.getRoot();
		ne = createNEPositive(root, "foo", null);
	}
	
	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	private static final <T> Set<T> mSet(T... members) {
		return new HashSet<T>(Arrays.asList(members));
	}

	static final Set<IAttributeType> knownAttributeTypes = mSet(fBool, fInt,
			fHandle, fLong, fString);
	
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
		final IAttributeType[] actualTypes = element.getAttributeTypes();
		final Set<IAttributeType> expected = mSet(expectedTypes);
		final Set<IAttributeType> actual = mSet(actualTypes);
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
	
	private static void assertAttributeValues(IInternalElement element,
			IAttributeValue... expected) throws RodinDBException {
		final IAttributeValue[] actual = element.getAttributeValues();
		final Set<IAttributeValue> eSet = mSet(expected);
		final Set<IAttributeValue> aSet = mSet(actual);
		assertEquals(eSet, aSet);
	}

	static void assertBooleanValue(IInternalElement element,
			IAttributeType.Boolean type, boolean expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void assertHandleValue(IInternalElement element,
			IAttributeType.Handle type, IRodinElement expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void assertIntegerValue(IInternalElement element,
			IAttributeType.Integer type, int expected) throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void assertLongValue(IInternalElement element,
			IAttributeType.Long type, long expected) throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	static void assertStringValue(IInternalElement element,
			IAttributeType.String type, String expected)
			throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals("Unexpected attribute value", expected,
				element.getAttributeValue(type));
	}
	
	void removeAttrPositive(IInternalElement element, IAttributeType type)
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

	private void assertNoDelta(IInternalElement element) {
		final IRodinElementDelta delta = getDeltaFor(element, true);
		assertNull("No delta should have been generated", delta);
	}

	private void assertAttributeDelta(IInternalElement element) {
		final IRodinElementDelta delta = getDeltaFor(element, true);
		assertNotNull("No delta", delta);
		assertEquals("Wrong delta kind", IRodinElementDelta.CHANGED,
				delta.getKind());
		assertEquals("Wrong delta flag",
				IRodinElementDelta.F_ATTRIBUTE, delta.getFlags());
	}

	void removeAttrNegative(IInternalElement element, IAttributeType type,
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

	void setBoolAttrPositive(IInternalElement element,
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
			assertEquals("Wrong generic attribute", type.makeValue(newValue),
					element.getAttributeValue((IAttributeType) type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setHandleAttrPositive(IInternalElement element,
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
			assertEquals("Wrong generic attribute", type.makeValue(newValue),
					element.getAttributeValue((IAttributeType) type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setIntAttrPositive(IInternalElement element,
			IAttributeType.Integer type, int newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			assertTrue("Attribute should have been created", element
					.hasAttribute(type));
			assertEquals("New value should have been set", newValue, element
					.getAttributeValue(type));
			assertEquals("Wrong generic attribute", type.makeValue(newValue),
					element.getAttributeValue((IAttributeType) type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setLongAttrPositive(IInternalElement element,
			IAttributeType.Long type, long newValue) throws RodinDBException {
		assertExists("Element should exist", element);
		try {
			startDeltas();
			element.setAttributeValue(type, newValue, null);
			assertTrue("Attribute should have been created", element
					.hasAttribute(type));
			assertEquals("New value should have been set", newValue, element
					.getAttributeValue(type));
			assertEquals("Wrong generic attribute", type.makeValue(newValue),
					element.getAttributeValue((IAttributeType) type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setStringAttrPositive(IInternalElement element,
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
			assertEquals("Wrong generic attribute", type.makeValue(newValue),
					element.getAttributeValue((IAttributeType) type));
			assertAttributeDelta(element);
		} finally {
			stopDeltas();
		}
	}

	void setBoolAttrNegative(IInternalElement element,
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

	void setHandleAttrNegative(IInternalElement element,
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

	void setIntAttrNegative(IInternalElement element,
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

	void setLongAttrNegative(IInternalElement element,
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

	void setStringAttrNegative(IInternalElement element,
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
		assertAttributeValues(ie);

		final IAttributeValue vBool = fBool.makeValue(true);
		setBoolAttrPositive(ie, fBool, true);
		assertAttributeNames(ie, fBool);
		assertAttributeValues(ie, vBool);

		final IAttributeValue vHandle = fHandle.makeValue(ie);
		setHandleAttrPositive(ie, fHandle, ie);
		assertAttributeNames(ie, fBool, fHandle);
		assertAttributeValues(ie, vBool, vHandle);

		final IAttributeValue vInt = fInt.makeValue((-55));
		setIntAttrPositive(ie, fInt, -55);
		assertAttributeNames(ie, fBool, fHandle, fInt);
		assertAttributeValues(ie, vBool, vHandle, vInt);

		final IAttributeValue vLong = fLong.makeValue(12345678901L);
		setLongAttrPositive(ie, fLong, 12345678901L);
		assertAttributeNames(ie, fBool, fHandle, fInt, fLong);
		assertAttributeValues(ie, vBool, vHandle, vInt, vLong);

		final IAttributeValue vString = fString.makeValue("bar");
		setStringAttrPositive(ie, fString, "bar");
		assertAttributeNames(ie, fBool, fHandle, fInt, fLong, fString);
		assertAttributeValues(ie, vBool, vHandle, vInt, vLong, vString);

		removeAttrPositive(ie, fLong);
		assertAttributeNames(ie, fBool, fHandle, fInt, fString);
		assertAttributeValues(ie, vBool, vHandle, vInt, vString);
	}
	
	/**
	 * Ensures that the getAttributeTypes() and hasAttribute() methods works
	 * properly on Rodin files.
	 */
	@Test
	public void testGetAttributeNamesFile() throws CoreException {
		assertGetAttributeNames(root);
	}

	/**
	 * Ensures that the getAttributeTypes() and hasAttribute() methods works
	 * properly on internal elements.
	 */
	@Test
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
	@Test
	public void testRemoveAttributeFile() throws CoreException {
		assertRemoveAttribute(root);
	}

	/**
	 * Ensures that the removeAttribute() method works properly on internal
	 * elements.
	 */
	@Test
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
	@Test
	public void testRemoveAttributeNoElementFile() throws CoreException {
		rodinFile.delete(false, null);
		assertRemoveAttributeNoElement(root);
	}

	/**
	 * Ensures that attempting to remove an attribute on an inexistent internal
	 * element raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	@Test
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
	@Test
	public void testSetAttributeNoElementRoot() throws CoreException {
		rodinFile.delete(false, null);
		assertSetAttributeNoElement(root);
	}

	/**
	 * Ensures that attempting to set an attribute on an inexistent internal
	 * element raises an ELEMENT_DOES_NOT_EXIST error.
	 */
	@Test
	public void testSetAttributeNoElementInternal() throws CoreException {
		ne.delete(false, null);
		assertSetAttributeNoElement(ne);
	}

	private void assertSnapshotAttributeReadOnly(IInternalElement ie)
			throws CoreException {

		final int code = IRodinDBStatusConstants.READ_ONLY;
		rodinFile.save(null, false);

		final IInternalElement snapshot = ie.getSnapshot();
		removeAttrNegative(snapshot, fBool, code);
		setBoolAttrNegative(snapshot, fBool, true, code);
		setHandleAttrNegative(snapshot, fHandle, rodinFile, code);
		setIntAttrNegative(snapshot, fInt, -55, code);
		setLongAttrNegative(snapshot, fLong, 12345678901L, code);
		setStringAttrNegative(snapshot, fString, "bar", code);
	}

	@Test
	public void testSnapshotAttributeReadOnlyRoot() throws CoreException {
		assertSnapshotAttributeReadOnly(root);
	}

	@Test
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
	
	@Test
	public void testAttributePersistentFile() throws CoreException {
		assertAttributePersistent(root);
	}

	@Test
	public void testAttributePersistentInternal() throws CoreException {
		assertAttributePersistent(ne);
	}
	
	private void assertIllegalArgument(Runnable runnable) {
		try {
			runnable.run();
			fail("should have raised an exception");
		} catch (IllegalArgumentException e) {
			// success
		}
	}

	@Test
	public void testInvalidAttrType() {
		assertIllegalArgument(new Runnable() {
			public void run() {
				RodinCore.getAttributeType("invalid");
			}
		});
	}
	
	@Test
	public void testWrongAttrType() {
		assertIllegalArgument(new Runnable() {
			public void run() {
				RodinCore.getBooleanAttrType(fHandle.getId());
			}
		});
		assertIllegalArgument(new Runnable() {
			public void run() {
				RodinCore.getHandleAttrType(fInt.getId());
			}
		});
		assertIllegalArgument(new Runnable() {
			public void run() {
				RodinCore.getIntegerAttrType(fLong.getId());
			}
		});
		assertIllegalArgument(new Runnable() {
			public void run() {
				RodinCore.getLongAttrType(fString.getId());
			}
		});
		assertIllegalArgument(new Runnable() {
			public void run() {
				RodinCore.getStringAttrType(fBool.getId());
			}
		});
	}

	@Test
	public void testIdWithDotInAttributeTypeDeclaration() throws Exception {
		assertNull(AttributeType.valueOf(new MockConfigurationElement( //
				"id", "foo.bar", //
				"name", "Foo Bar", //
				"kind", "long" //
		)));
	}

	@Test
	public void testIdWithSpaceInAttributeTypeDeclaration() throws Exception {
		assertNull(AttributeType.valueOf(new MockConfigurationElement( //
				"id", "foo bar", //
				"name", "Foo Bar", //
				"kind", "long" //
		)));
	}

	@Test
	public void testInvalidKindInAttributeTypeDeclaration() throws Exception {
		assertNull(AttributeType.valueOf(new MockConfigurationElement( //
				"id", "foo", //
				"name", "Foo", //
				"kind", "inexistent" //
		)));
	}

	@Test
	public void testMissingIdInAttributeTypeDeclaration() throws Exception {
		assertNull(AttributeType.valueOf(new MockConfigurationElement( //
				"name", "Foo", //
				"kind", "long" //
		)));
	}

	@Test
	public void testMissingNameInAttributeTypeDeclaration() throws Exception {
		assertNull(AttributeType.valueOf(new MockConfigurationElement( //
				"id", "foo", //
				"kind", "long" //
		)));
	}

	@Test
	public void testMissingKindInAttributeTypeDeclaration() throws Exception {
		assertNull(AttributeType.valueOf(new MockConfigurationElement( //
				"id", "foo", //
				"name", "Foo" //
		)));
	}

	/**
	 * Ensures that generic attribute values can be set (tested only for
	 * boolean).
	 */
	@Test
	public void testGenericSetAttribute() throws RodinDBException {
		final IAttributeValue v1 = fBool.makeValue(true);
		final IAttributeValue v2 = fBool.makeValue(true);

		assertAttributeValues(root);

		root.setAttributeValue(v1, null);
		assertAttributeValues(root, v1);

		root.setAttributeValue(v2, null);
		assertAttributeValues(root, v2);
	}

	/**
	 * Ensures that an attribute of invalid type for a given root element cannot
	 * be set.
	 */
	@Test
	public void testSetInvalidAttributeRoot() throws CoreException {
		final IInternalElement r = createRodinFile("P/X.test2").getRoot();
		final IAttributeValue v = fBool.makeValue(true);
		assertSetAttributeInvalidType(r, v);
	}

	/**
	 * Ensures that an attribute of invalid type for a given non-root element
	 * cannot be set.
	 */
	@Test
	public void testSetInvalidAttributeInternal() throws CoreException {
		final IInternalElement r = createRodinFile("P/X.test2").getRoot();
		final NamedElement2 ne2 = createNE2Positive(r, "foo", null);
		final IAttributeValue v = fBool.makeValue(true);
		assertSetAttributeInvalidType(ne2, v);
	}

	private void assertSetAttributeInvalidType(IInternalElement owner,
			IAttributeValue value) {
		try {
			owner.setAttributeValue(value, null);
			fail("Should have raised a RodinDBException");
		} catch (RodinDBException e) {
			final IRodinDBStatus status = e.getRodinDBStatus();
			assertEquals(ERROR, status.getSeverity());
			assertEquals(INVALID_ATTRIBUTE_TYPE, status.getCode());
			assertEquals(asList(owner), asList(status.getElements()));
		}
	}

}
