/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.tests;

import org.junit.Test;

/**
 * Test the translation for programmatically contributed symbols.
 */
public class Text2MathExtensionTestCase extends AbstractText2MathTestCase {
	
	@Test
	public void testAlphaExtensionSymbol() {
		String input = "x alpha p";
		String expect = "x \u03b1 p";
		testTranslator("AlphaTest ", input, expect);
	}
	
	@Test
	public void testBetaExtensionSymbol() {
		String input = "x beta p";
		String expect = "x \u03b2 p";
		testTranslator("BetaTest ", input, expect);
	}
	
}
