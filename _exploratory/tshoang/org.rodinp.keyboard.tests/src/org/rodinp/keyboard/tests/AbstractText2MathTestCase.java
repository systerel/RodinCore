/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.rodinp.keyboard.tests;

import junit.framework.TestCase;

import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         This class contains some simple test cases for Text2EventBMath
 *         translator. This tests the translation on all the symbols separately.
 */
public class AbstractText2MathTestCase extends TestCase {

	protected void testTranslator(String message, String input, String expected) {
		RodinKeyboardPlugin kbrdPlugin = RodinKeyboardPlugin.getDefault();
		assertEquals("1. " + message, expected, kbrdPlugin.translate(input));
		assertEquals("2. " + message, expected + " ", kbrdPlugin
				.translate(input + " "));
		assertEquals("3. " + message, " " + expected, kbrdPlugin.translate(" "
				+ input));
		assertEquals("4. " + message, " " + expected + " ", kbrdPlugin
				.translate(" " + input + " "));
	}

	protected void testTranslator(String input, String expected) {
		testTranslator(input, input, expected);
	}

	protected void testTranslator(String ... tests) {
		for (int i = 0; i + 1 < tests.length; i += 2) {
			String input = tests[i];
			String expected = tests[i+1];
			testTranslator(input, expected);
		}
	}
}
