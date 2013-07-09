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
 * Interface for translation testers.
 * <p>
 * A translation tester is a class which evaluates the translation of given
 * strings and compares it with other given expected strings.
 * </p>
 * This interface is created to be able to run the same tests for graphical and
 * non-graphical translators.
 */
public interface IKeyboardTranslationTester {

	/**
	 * Translates the given input string and compares the result with expected
	 * output.
	 * 
	 * @param testName
	 *            the name of the test
	 * @param expectedOutput
	 *            the expected string that should match the translated input
	 * @param input
	 *            the input string to be translated
	 */
	void doTest(String testName, String expectedOutput, String input);

	/**
	 * Translates the given input string and compares the result with expected
	 * output.
	 * 
	 * @param expectedOutput
	 *            the expected string that should match the translated input
	 * @param input
	 *            the input string to be translated
	 */
	void doTest(String expectedOutput, String input);

	/**
	 * Translates and compares the given strings pairwise.
	 * <p>
	 * The first string of each pair is the expected translation, and the second
	 * string is the string to be translated.
	 * </p>
	 * 
	 * @param strings
	 *            pairs of expected string and string to be translated to be
	 *            compared
	 */
	void doTest(String... strings);

}
