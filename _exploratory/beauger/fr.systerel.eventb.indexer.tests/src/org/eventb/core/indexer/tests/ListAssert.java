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
package org.eventb.core.indexer.tests;

import java.util.List;

import junit.framework.TestCase;

/**
 * @author Nicolas Beauger
 *
 */
public class ListAssert {
	
	
	public static <T> void assertSameElements(List<T> expected,
			List<T> actual, String listEltDesc) {
		TestCase.assertEquals(listEltDesc + ": bad size in\n"
				+ ListAssert.makeActExpString(expected, actual), expected.size(), actual
				.size());
		TestCase.assertTrue(listEltDesc + ": incorrect items in\n"
				+ ListAssert.makeActExpString(expected, actual), actual
				.containsAll(expected));
	}

	private static <T> String makeActExpString(List<T> expected, List<T> actual) {
		return ListAssert.makeString("act", actual) + ListAssert.makeString("exp", expected);
	}

	private static <T> String makeString(String listDesc, List<T> list) {
		return listDesc + ": " + list + "\n";
	}

	
	
}
