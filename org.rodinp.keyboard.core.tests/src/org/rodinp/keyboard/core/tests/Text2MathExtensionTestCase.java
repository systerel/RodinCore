/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.core.tests;

import org.junit.Test;
import org.rodinp.keyboard.core.tests.registry.TestSymbolProvider;

/**
 * Test the individual translation for programmatically contributed symbols.
 */
public class Text2MathExtensionTestCase extends AbstractText2EventBMathTestCase {

	@Test
	public void testAlphaExtensionSymbol() {
		testTranslation("AlphaTest ", "x \u03b1 p", "x alpha p");
	}

	@Test
	public void testBetaExtensionSymbol() {
		testTranslation("BetaTest ", "x \u03b2 p", "x beta p");
	}

	@Test
	public void testRuntimeEpsilonExtensionSymbol() {
		try {
			TestSymbolProvider.addSymbol("epsilon", "ε");
			testTranslation("EpsilonTest ", "x \u03b5 p", "x epsilon p");
		} finally {
			TestSymbolProvider.reset();
		}
	}

	@Test
	public void testCaretOnWord() {
		// On the first letter of the word
		testTranslation("CaretOnWord", "x alpha p", "x alpha p", 2);
		// Somewhere in the middle of the word
		testTranslation("CaretOnWord", "x alpha p", "x alpha p", 4);
		// On the last letter of the word
		testTranslation("CaretOnWord", "x alpha p", "x alpha p", 7);
		// For math symbols: translate immediately
		testTranslation("CaretOnWord", "x ÷ y", "x / y", 3);
	}

	@Test
	public void testCaretOutsideWord() {
		// Before the word
		testTranslation("CaretOnWord", "x \u03b1 p", "x alpha p", 1);
		// After the word
		testTranslation("CaretOnWord", "x \u03b1 p", "x alpha p", 8);
	}

}
