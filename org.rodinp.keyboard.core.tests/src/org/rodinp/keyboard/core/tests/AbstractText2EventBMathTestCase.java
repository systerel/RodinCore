/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.core.tests;

/**
 * Abstract base class for the keyboard translation tests.
 * <p>
 * The defaut translator tester checks the text translator (see
 * {@link Text2MathTranslationTester}. The method <code>getTranslatorTester()</code> is
 * meant to be overriden by clients to chck other kinds of translators (e.g.
 * Keyboard UI translators).
 * </p>
 */
public abstract class AbstractText2EventBMathTestCase {

	/**
	 * Checks the translation of <code>input</code> using the translator tester
	 * provided by the <code>getTranslatorTester()</code> method by comparing it
	 * to <code>expectedOutput</code>.
	 * 
	 * @param testName
	 *            the name of the test
	 * @param expectedOutput
	 *            the expected string that should match the translated input
	 * @param input
	 *            the input string to be translated
	 */
	public void testTranslation(String message, String expectedOutput,
			String input) {
		final IKeyboardTranslationTester tester = getTranslatorTester();
		tester.doTest(message, expectedOutput, input);
	}

	/**
	 * Pairwise checks the translations using the translator tester provided by
	 * the <code>getTranslatorTester()</code> method.
	 * <p>
	 * The first string of each pair is the expected translation, and the second
	 * string is the string to be translated.
	 * </p>
	 * 
	 * @param strings
	 *            pairs of expected string and string to be translated to be
	 *            compared
	 */
	public void testTranslation(String... strings) {
		final IKeyboardTranslationTester tester = getTranslatorTester();
		tester.doTest(strings);
	}

	/**
	 * Returns a tester for the basic textual translator.
	 * 
	 * Clients shall override this method to test other kinds of translators
	 * (e.g. UI translators).
	 * 
	 * @return a tester for the basic textual translator
	 */
	protected IKeyboardTranslationTester getTranslatorTester() {
		return new Text2MathTranslationTester();
	}

}