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
package org.rodinp.keyboard.core;

import org.rodinp.internal.keyboard.core.symbols.SymbolRegistry;
import org.rodinp.internal.keyboard.core.translators.Text2MathTranslator;

/**
 * Facade to core translation methods.
 * 
 * @author Thomas Muller
 */
public class RodinKeyboardCore {

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
		return Text2MathTranslator.translate(text);
	}

}
