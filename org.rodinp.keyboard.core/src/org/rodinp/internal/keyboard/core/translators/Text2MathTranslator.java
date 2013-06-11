/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - supported contribution through extension and at runtime
 *******************************************************************************/
package org.rodinp.internal.keyboard.core.translators;

import java.util.Collection;

import org.rodinp.internal.keyboard.core.symbols.SymbolRegistry;
import org.rodinp.keyboard.core.ISymbol;
import org.rodinp.keyboard.core.ISymbolRegistry;
import org.rodinp.keyboard.core.KeyboardUtils;

/**
 * @author htson
 *         <p>
 *         This class provides a translation of a string into Event-B
 *         mathematical language.
 */
public class Text2MathTranslator {

	/**
	 * Translate the input string into Event-B Mathematical Language.
	 * <p>
	 * 
	 * @param str
	 *            input string
	 * @return a string corresponds to the input in Event-B Mathematical
	 *         Language
	 */
	public static String translate(String str) {
		final ISymbolRegistry registry = SymbolRegistry.getDefault();
		// Math
		String test = null;
		int i = 0;
		for (i = registry.getMaxMathSymbolSize(); i > 0; i--) {
			final Collection<ISymbol> collection = registry.getMathSymbols(i);
			if (collection != null) {
				for (ISymbol symbol : collection) {
					test = symbol.getCombo();
					int index = str.indexOf(test);
					if (index != -1) {
						return translate(str.substring(0, index))
								+ symbol.getTranslation()
								+ translate(str.substring(index
										+ test.length()));
					}
				}
			}
		}

		// Text

		for (i = registry.getMaxTextSymbolSize(); i > 0; i--) {
			final Collection<ISymbol> collection = registry.getTextSymbols(i);
			if (collection != null) {
				for (ISymbol symbol : collection) {
					String combo = symbol.getCombo();
					int index = comboIndex(str, combo);
					if (index == 0) {
						return symbol.getTranslation()
								+ translate(str.substring(combo
										.length()));
					} else if (index != -1) {
						return translate(str.substring(0, index))
								+ symbol.getTranslation()
								+ translate(str.substring(index
										+ combo.length()));
					}

				}
			}
		}

		// for (int i = 0; i < textCombo.length; i++) {
		// String test = " " + textCombo[i] + " ";
		// int index = (" " + str + " ").indexOf(test);
		// if (index == 0) {
		// return textComboTranslation[i]
		// + translate(str.substring(textCombo[i].length()));
		// } else if (index != -1) {
		// return translate(str.substring(0, index))
		// + textComboTranslation[i]
		// + translate(str
		// .substring(index + textCombo[i].length()));
		// }
		// }

		return str;
	}

	private static int comboIndex(String str, String combo) {
		int index = str.indexOf(combo);
		if (index == -1)
			return -1;
		if (index == 0) {
			int lastIndex = index + combo.length();
			if (lastIndex < str.length()) {
				char c = str.charAt(lastIndex);
				if (KeyboardUtils.isTextCharacter(c)) {
					return -1;
				}
			}
			return index;
		} else {
			char c = str.charAt(index - 1);
			if (KeyboardUtils.isTextCharacter(c)) {
				return -1;
			}
			int lastIndex = index + combo.length();
			if (lastIndex < str.length()) {
				c = str.charAt(lastIndex);
				if (KeyboardUtils.isTextCharacter(c)) {
					return -1;
				}
			}
			return index;
		}
	}

}
