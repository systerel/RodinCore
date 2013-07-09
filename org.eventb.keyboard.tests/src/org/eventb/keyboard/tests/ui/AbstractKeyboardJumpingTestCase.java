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
package org.eventb.keyboard.tests.ui;

import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Before;
import org.rodinp.keyboard.ui.tests.KeyboardUITestContext;
import org.rodinp.keyboard.ui.tests.Text2MathUITranslationTester;

/**
 * An abstract base class to check the translation of LaTeX symbols in a
 * {@link Text} widget, and capable of checking the dynamic translation of
 * symbols.
 */
public class AbstractKeyboardJumpingTestCase {

	protected KeyboardUITestContext context = new KeyboardUITestContext();

	@Before
	public void setUp() throws Exception {
		context.setUp();
	}

	@After
	public void tearDown() throws Exception {
		context.tearDown();
	}

	protected Text2MathUITranslationTester getTranslatorTester() {
		return context.getTranslationTester();
	}

	/**
	 * Dynamically checks the symbol translation when multiple additional insertions are
	 * done at given positions in the base translated string of a targeted widget.
	 * 
	 * @param input
	 *            the base input string to start the test with
	 * @param insertionIndexes
	 *            the position in the translated input where further insertion
	 *            shall happen
	 * @param nextInsertion
	 *            the string contents to be inserted in the base translated
	 *            input string at the <code>insertionIndex</code> position
	 * @param expectedTranslation
	 *            the final string in the widget after further insertion, and
	 *            translation occurred.
	 */
	protected void testJumping(String input, int[] insertionIndex,
			String[] nextInsertion, String expectedTranslation) {
		final Text2MathUITranslationTester translator = getTranslatorTester();
		final Text widget = context.getWidget();
		widget.setText("");
		for (int i = 0; i < input.length(); i++)
			translator.insert("" + input.charAt(i));
		for (int i = 0; i < insertionIndex.length; i++) {
			widget.setSelection(insertionIndex[i]);
			translator.insert(nextInsertion[i]);
		}
		translator.compare("SHWTInvariant ", expectedTranslation);
	}


	/**
	 * Dynamically checks the symbol translation when an additional insertion is
	 * done at a given position in the base translated string of a targeted widget.
	 * 
	 * @param input
	 *            the base input string to start the test with
	 * @param insertionIndex
	 *            the position in the translated input where further insertion
	 *            shall happen
	 * @param nextInsertion
	 *            the string contents to be inserted in the base translated
	 *            input string at the <code>insertionIndex</code> position
	 * @param expectedTranslation
	 *            the final string in the widget after further insertion, and
	 *            translation occurred.
	 */
	protected void testJumping(String input, int insertionIndex,
			String nextInsertion, String expectedTranslation) {
		testJumping(input, new int[] { insertionIndex },
				new String[] { nextInsertion }, expectedTranslation);
	}

}