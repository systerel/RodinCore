/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.sc.state;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eventb.internal.core.sc.symbolTable.StackedCollection;
import org.junit.Test;

/**
 * Unit tests for class {@link StackedCollection}. The class should behave like
 * a concatenation of the two collections, therefore we check that this is
 * actually the case using an equivalent model.
 * 
 * @author Laurent Voisin
 */
public class StackedCollectionTests {

	/**
	 * Ensures correct implementation in presence of empty collections.
	 */
	@Test
	public void empty() {
		assertValid(collection(), collection());
		assertValid(collection(), collection("1"));
		assertValid(collection("1"), collection());
	}

	/**
	 * Ensures correct implementation in presence of non-empty collections.
	 */
	@Test
	public void notEmpty() {
		assertValid(collection("1"), collection("2"));
		assertValid(collection("1", "2"), collection("3"));
		assertValid(collection("1"), collection("2", "3"));
	}

	/**
	 * Ensures that the StackedCollection implementation corresponds to its
	 * equivalent model.
	 * 
	 * @param sup
	 *            the first collection
	 * @param inf
	 *            the second collection
	 */
	private <T> void assertValid(Collection<T> sup, Collection<T> inf) {
		final StackedCollection<T> instance;
		instance = new StackedCollection<T>(sup, inf);

		final List<T> model = new ArrayList<T>();
		model.addAll(sup);
		model.addAll(inf);

		assertEquals(model.size(), instance.size());

		// The new ArrayList exercises the iterator.
		assertEquals(model, new ArrayList<T>(instance));
	}

	private Collection<String> collection(String... strings) {
		return Arrays.asList(strings);
	}

}
