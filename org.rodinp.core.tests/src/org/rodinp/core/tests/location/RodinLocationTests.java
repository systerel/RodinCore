/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.location;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IAttributeSubstringLocation;
import org.rodinp.core.location.IInternalLocation;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;

public class RodinLocationTests extends AbstractRodinDBTests {

	public static final IAttributeType.String TEST_ATTR_TYPE = RodinCore
			.getStringAttrType(PLUGIN_ID + ".testAttributeType");


	public RodinLocationTests(String name) {
		super(name);
	}

	private static final int defaultStart = 1;
	private static final int defaultEnd = 3;

	private IRodinProject project;
	private IRodinFile file;
	private IInternalElement locElement;

	public static void assertLocation(IInternalLocation loc, IInternalElement element) {
		assertEquals("unexpected element in location", element, loc
				.getElement());
	}

	public static void assertLocation(IInternalLocation loc,
			IInternalElement element, IAttributeType attributeType) {
		assertLocation(loc, element);
		assertTrue(loc instanceof IAttributeLocation);
		final IAttributeLocation aLoc = (IAttributeLocation) loc;
		assertEquals("unexpected attribute type in location", attributeType,
				aLoc.getAttributeType());
	}

	public static void assertLocation(IInternalLocation loc,
			IInternalElement element, IAttributeType.String attributeType,
			int start, int end) {
		assertLocation(loc, element, attributeType);
		assertTrue(loc instanceof IAttributeSubstringLocation);
		final IAttributeSubstringLocation aLoc = (IAttributeSubstringLocation) loc;
		assertEquals("unexpected start position in location", start, aLoc
				.getCharStart());
		assertEquals("unexpected end position in location", end, aLoc
				.getCharEnd());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = getRodinProject("P");
		file = project.getRodinFile("rodLoc.test");
		locElement = file.getRoot().getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
	}

	public void testConstructor() throws Exception {
		IInternalLocation loc = RodinCore.getInternalLocation(locElement,
				TEST_ATTR_TYPE, defaultStart, defaultEnd);
		assertLocation(loc, locElement, TEST_ATTR_TYPE, defaultStart, defaultEnd);
	}

	public void testNullElement() throws Exception {
		try {
			RodinCore.getInternalLocation(null);
			fail("expected NullPointerException");
		} catch (NullPointerException e) {
			// Pass
		}
	}

	public void testFileElement() throws Exception {
		final IInternalElement root = file.getRoot();
		IInternalLocation loc = RodinCore.getInternalLocation(root);
		assertLocation(loc, root);
	}

	public void testInternalElement() throws Exception {
		IInternalLocation loc = RodinCore.getInternalLocation(locElement);
		assertLocation(loc, locElement);
	}

	public void testAttribute() throws Exception {
		IInternalLocation loc = RodinCore
				.getInternalLocation(locElement, TEST_ATTR_TYPE);
		assertLocation(loc, locElement, TEST_ATTR_TYPE);
	}

	public void testAttributeSubstring() throws Exception {
		IInternalLocation loc = RodinCore.getInternalLocation(locElement,
				TEST_ATTR_TYPE, defaultStart, defaultEnd);
		assertLocation(loc, locElement, TEST_ATTR_TYPE, defaultStart, defaultEnd);
	}

	public void testNullAttribute() throws Exception {
		try {
			RodinCore.getInternalLocation(locElement, null, defaultStart,
					defaultEnd);
			fail("expected NullPointerException");
		} catch (NullPointerException e) {
			// Pass
		}
	}

	public void testInvalidStart() throws Exception {
		try {
			RodinCore.getInternalLocation(locElement, TEST_ATTR_TYPE, -1, 0);
			fail("expected NullPointerException");
		} catch (IllegalArgumentException e) {
			// Pass
		}
	}

	public void testInvalidEnd() throws Exception {
		try {
			RodinCore.getInternalLocation(locElement, TEST_ATTR_TYPE, 0, -1);
			fail("expected NullPointerException");
		} catch (IllegalArgumentException e) {
			// Pass
		}
	}

	public void testEmptySubstring() throws Exception {
		try {
			RodinCore.getInternalLocation(locElement, TEST_ATTR_TYPE, 0, 0);
			fail("expected NullPointerException");
		} catch (IllegalArgumentException e) {
			// Pass
		}
	}

	/**
	 * Ensures that locations of different nature are never equal.
	 */
	public void testDiffers() throws Exception {
		final IInternalLocation eLoc = RodinCore.getInternalLocation(locElement);
		final IInternalLocation aLoc = RodinCore.getInternalLocation(locElement,
				TEST_ATTR_TYPE);
		final IInternalLocation sLoc = RodinCore.getInternalLocation(locElement,
				TEST_ATTR_TYPE, defaultStart, defaultEnd);

		assertFalse(eLoc.equals(aLoc));
		assertFalse(eLoc.equals(sLoc));
		assertFalse(aLoc.equals(eLoc));
		assertFalse(aLoc.equals(sLoc));
		assertFalse(sLoc.equals(eLoc));
		assertFalse(sLoc.equals(aLoc));
	}

	public void testEqualsElement() throws Exception {
		final IInternalLocation loc1 = RodinCore.getInternalLocation(locElement);
		final IInternalLocation loc2 = RodinCore.getInternalLocation(locElement);
		assertEquals(loc1, loc2);
	}

	public void testEqualsAttribute() throws Exception {
		final IInternalLocation loc1 = RodinCore.getInternalLocation(locElement,
				TEST_ATTR_TYPE);
		final IInternalLocation loc2 = RodinCore.getInternalLocation(locElement,
				TEST_ATTR_TYPE);
		assertEquals(loc1, loc2);
	}

	public void testEqualsSubstring() throws Exception {
		final IInternalLocation loc1 = RodinCore.getInternalLocation(locElement,
				TEST_ATTR_TYPE, defaultStart, defaultEnd);
		final IInternalLocation loc2 = RodinCore.getInternalLocation(locElement,
				TEST_ATTR_TYPE, defaultStart, defaultEnd);
		assertEquals(loc1, loc2);
	}

}
