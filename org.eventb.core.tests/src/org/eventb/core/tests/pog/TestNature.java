/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.eventb.core.pog.IPOGNature;
import org.eventb.internal.core.pog.POGNatureFactory;
import org.junit.Test;

/**
 * @author A. Gilles
 * 
 */
public class TestNature extends EventBPOTest {

	private static final POGNatureFactory NATURE_FACTORY = POGNatureFactory
			.getInstance();

	// verify that the description is correctly retrieved
	@Test
	public void testGetDescription() throws Exception {
		final String testGetDescription = "Test get description";
		final IPOGNature nature = NATURE_FACTORY.getNature(testGetDescription);
		assertEquals("Unexpected description", testGetDescription, nature
				.getDescription());
	}

	// verify the uniqueness of natures for a given description
	@Test
	public void testSameNature() throws Exception {
		final String testSameNature1 = new String("Test same nature");
		final String testSameNature2 = new String("Test same nature");
		final IPOGNature nature1 = NATURE_FACTORY.getNature(testSameNature1);
		final IPOGNature nature2 = NATURE_FACTORY.getNature(testSameNature2);
		assertSame("Expected same natures", nature1, nature2);
	}

	// verify the distinction of natures with different descriptions 
	@Test
	public void testNotSameNature() throws Exception {
		final String testNotSameNature1 = new String("Test not same nature 1");
		final String testNotSameNature2 = new String("Test not same nature 2");
		final IPOGNature nature1 = NATURE_FACTORY.getNature(testNotSameNature1);
		final IPOGNature nature2 = NATURE_FACTORY.getNature(testNotSameNature2);
		assertNotSame("Expected not same natures", nature1, nature2);
	}

}
