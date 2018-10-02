/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests;

import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.IVariant.DEFAULT_LABEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for Event-B variants.
 *
 * Verifies that the labels introduced in version 3.4 work as expected.
 * 
 * @author Laurent Voisin
 * @since 3.4
 */
public class VariantTest extends EventBTest {

	private static final String ALT_LABEL = "some-variant";
	private static final String ALT_LABEL2 = "some-other-variant";

	private IMachineRoot mch;
	private IVariant variant;

	/**
	 * Creates a machine with a single unlabeled variant.
	 */
	@Before
	public void setUp() throws Exception {
		mch = createMachine("foo");
		addVariant(mch, "v");
		variant = mch.getVariants()[0];
	}

	/**
	 * Ensures that hasLabel() returns true even when the attribute is absent in the
	 * Rodin database.
	 */
	@Test
	public void testHasLabelAbsent() throws RodinDBException {
		assertLabelAttribute(null);

		assertTrue(variant.hasLabel());
	}

	/**
	 * Ensures that hasLabel() returns true when the attribute is present in the
	 * Rodin database.
	 */
	@Test
	public void testHasLabelPresent() throws RodinDBException {
		variant.setLabel(ALT_LABEL, null);
		assertLabelAttribute(ALT_LABEL);

		assertTrue(variant.hasLabel());
	}

	/**
	 * Ensures that getLabel() returns the default label when the attribute is
	 * absent in the Rodin database.
	 */
	@Test
	public void testGetLabelAbsent() throws RodinDBException {
		assertLabelAttribute(null);
		
		assertEquals(DEFAULT_LABEL, variant.getLabel());
	}

	/**
	 * Ensures that getLabel() returns the label value when the attribute is present
	 * in the Rodin database.
	 */
	@Test
	public void testGetLabelPresent() throws RodinDBException {
		variant.setLabel(ALT_LABEL, null);
		assertLabelAttribute(ALT_LABEL);

		assertEquals(ALT_LABEL, variant.getLabel());
	}

	/**
	 * Ensures that setLabel() does not change anything when the attribute was
	 * absent in the Rodin database and the value is the default one.
	 */
	@Test
	public void testSetLabelAbsentDefault() throws RodinDBException {
		assertLabelAttribute(null);

		variant.setLabel(DEFAULT_LABEL, null);
		assertLabelAttribute(null);
	}

	/**
	 * Ensures that setLabel() creates an attribute when the attribute was absent
	 * and the value is not the default one.
	 */
	@Test
	public void testSetLabelAbsentNonDefault() throws RodinDBException {
		assertLabelAttribute(null);

		variant.setLabel(ALT_LABEL, null);
		assertLabelAttribute(ALT_LABEL);
	}

	/**
	 * Ensures that setLabel() removes the attribute that was present in the Rodin
	 * database when the value is the default one.
	 */
	@Test
	public void testSetLabelPresentDefault() throws RodinDBException {
		variant.setLabel(ALT_LABEL, null);
		assertLabelAttribute(ALT_LABEL);

		variant.setLabel(DEFAULT_LABEL, null);
		assertLabelAttribute(null);
	}

	/**
	 * Ensures that setLabel() updates the attribute that was present in the Rodin
	 * database when the value is not the default one.
	 */
	@Test
	public void testSetLabelPresentNonDefault() throws RodinDBException {
		variant.setLabel(ALT_LABEL, null);
		assertLabelAttribute(ALT_LABEL);

		variant.setLabel(ALT_LABEL2, null);
		assertLabelAttribute(ALT_LABEL2);
	}

	/* Checks directly the value of the label attribute in the Rodin database */
	private void assertLabelAttribute(String expected) throws RodinDBException {
		if (expected == null) {
			assertFalse(variant.hasAttribute(LABEL_ATTRIBUTE));
		} else {
			assertEquals(expected, variant.getAttributeValue(LABEL_ATTRIBUTE));
		}
	}

}
