/*******************************************************************************
 * Copyright (c) 2013, 2022 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.core;

import org.rodinp.internal.keyboard.core.symbols.SymbolRegistry;
import org.rodinp.internal.keyboard.core.translators.Text2MathTranslator;

/**
 * Facade to core translation methods.
 * 
 * @author Thomas Muller
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class RodinKeyboardCore {
	
	private RodinKeyboardCore() {
		// no instance
	}

	/**
	 * Returns the default symbol registry.
	 * 
	 * @return the default symbol registry
	 */
	public static ISymbolRegistry getSymbolRegistry() {
		return SymbolRegistry.getDefault();
	}

	/**
	 * Returns the given string where symbols have been translated.
	 * 
	 * @param text
	 *            the text to translate symbols from
	 * @return the text where symbols (both mathematical and textual) have been
	 *         translated
	 */
	public static String translate(String text) {
		return Text2MathTranslator.translate(text, -1);
	}

	/**
	 * Returns the given string where symbols have been translated.
	 *
	 * If the caret position is not available (or should be ignored),
	 * any out of bounds value can be provided (e.g., -1).
	 * 
	 * @param text
	 *            the text to translate symbols from
	 * @param caretOffset
	 *            the offset of the caret in the text
	 * @return the text where symbols (both mathematical and textual) have been
	 *         translated
	 * @since 2.3
	 */
	public static String translate(String text, int caretOffset) {
		return Text2MathTranslator.translate(text, caretOffset);
	}

}
