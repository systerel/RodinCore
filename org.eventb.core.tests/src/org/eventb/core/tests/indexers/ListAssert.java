/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.indexers;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Nicolas Beauger
 * 
 */
public class ListAssert {

	public static <T> void assertSameElements(List<T> expected, List<T> actual,
			String listEltDesc) {
		final String actExpString = ListAssert.makeActExpString(expected,
				actual);
		TestCase.assertEquals(listEltDesc + ": bad size in\n" + actExpString,
				expected.size(), actual.size());

		final boolean containsAll = actual.containsAll(expected);
		TestCase.assertTrue(listEltDesc + ": incorrect items in\n"
				+ actExpString, containsAll);
	}

	public static <T> void assertSameAsArray(List<T> expected,
			T[] actual, String listEltDesc) {
		final List<T> actList = Arrays.asList(actual);
		assertSameElements(expected, actList, listEltDesc);
	}

	private static <T> String makeActExpString(List<T> expected, List<T> actual) {
		return makeString("act", actual) + makeString("exp", expected);
	}

	private static <T> String makeString(String listDesc, List<T> list) {
		return listDesc + ": " + list + "\n";
	}

}
