/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.tests;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.eventb.internal.ui.utils.ListMultimap;
import org.junit.Test;

/**
 * Unit test for class {@link ListMultimap}.
 * 
 * @author Laurent Voisin
 */
public class TestListMultimap {

	private final ListMultimap<Integer, Boolean> map = new ListMultimap<Integer, Boolean>();

	/**
	 * Ensures that <code>null</code> is returned for a nonexistent key.
	 */
	@Test
	public void getInexistentKey() {
		assertNull(map.get(0));
	}

	/**
	 * Ensures that the appropriate value is returned for a key, and is not
	 * associated with another key.
	 */
	@Test
	public void getKeyOneImage() {
		map.put(0, true);
		assertEquals(asList(true), map.get(0));
		assertNull(map.get(1));
	}

	/**
	 * Ensures that the appropriate values are returned for a key.
	 */
	@Test
	public void getKeyTwoImages() {
		map.put(0, true);
		map.put(0, false);
		assertEquals(asList(true, false), map.get(0));
	}

	/**
	 * Ensures that the appropriate values are returned for a key (with
	 * duplicate values).
	 */
	@Test
	public void getKeyTwiceSameImage() {
		map.put(0, true);
		map.put(0, true);
		assertEquals(asList(true, true), map.get(0));
	}

	/**
	 * Ensures that the appropriate values are returned for a key.
	 */
	@Test
	public void putAll() {
		final List<Boolean> values = asList(true, false);
		map.putAll(0, values);
		assertEquals(values, map.get(0));
	}

}
