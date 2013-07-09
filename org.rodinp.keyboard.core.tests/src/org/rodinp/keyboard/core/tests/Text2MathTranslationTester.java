/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - port to JUnit 4
 *     Systerel - extracted to make test cases reusable
 *******************************************************************************/
package org.rodinp.keyboard.core.tests;

import static org.junit.Assert.assertEquals;
import static org.rodinp.keyboard.core.RodinKeyboardCore.translate;

import org.rodinp.keyboard.core.RodinKeyboardCore;

/**
 * This tester class realizes checks on the provided strings using the basic
 * textual translation provided by the {@link RodinKeyboardCore} facade.
 * 
 * @author htson
 */
public class Text2MathTranslationTester implements IKeyboardTranslationTester {

	public void doTest(final String message, final String expected,
			final String input) {
		assertEquals("1. " + message, expected, translate(input));
		assertEquals("2. " + message, expected + " ", translate(input + " "));
		assertEquals("3. " + message, " " + expected, translate(" " + input));
		assertEquals("4. " + message, " " + expected + " ", translate(" "
				+ input + " "));
	}

	@Override
	public void doTest(final String expectedOutput, final String input) {
		doTest(input, expectedOutput, input);
	}

	@Override
	public void doTest(final String... strings) {
		for (int i = 0; i + 1 < strings.length; i += 2) {
			final String expected = strings[i];
			final String input = strings[i + 1];
			doTest(expected, input);
		}
	}

}
