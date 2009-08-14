/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - handling of LaTeX symbols
 ******************************************************************************/

package org.eventb.eventBKeyboard;

import java.util.Collection;
import java.util.HashMap;

import org.eventb.eventBKeyboard.internal.translators.AbstractSymbols;
import org.eventb.eventBKeyboard.internal.translators.LaTeXSymbols;
import org.eventb.eventBKeyboard.internal.translators.MathSymbols;
import org.eventb.eventBKeyboard.internal.translators.Symbol;
import org.eventb.eventBKeyboard.internal.translators.TextSymbols;
import org.eventb.eventBKeyboard.internal.translators.Utils;

/**
 * @author htson
 *         <p>
 *         This class provides a translation of a string into Event-B
 *         mathematical language.
 */
public class Text2EventBMathTranslator {

	private static HashMap<String, Collection<Symbol>> mathSymbols = null;

	private static int maxMathSize = 0;

	private static HashMap<String, Collection<Symbol>> textSymbols = null;

	private static int maxSize = 0;

	private static HashMap<String, Collection<Symbol>> latexSymbols = null;

	private static int maxLaTeXSize = 0;

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
		// LaTeX
		if (latexSymbols == null) {
			LaTeXSymbols lsymbol = new LaTeXSymbols();
			latexSymbols = lsymbol.getSymbols();
			maxLaTeXSize = lsymbol.getMaxSize();
		}
		String translation = translateSymbols(str, maxLaTeXSize, latexSymbols);
		if (translation != null)
			return translation;
		
		// Math
		if (mathSymbols == null) {
			MathSymbols ssymbols = new MathSymbols();
			mathSymbols = ssymbols.getSymbols();
			maxMathSize = ssymbols.getMaxSize();
		}
		translation = translateSymbols(str, maxMathSize, mathSymbols);
		if (translation != null)
			return translation;

		// Text
		if (textSymbols == null) {
			TextSymbols tsymbol = new TextSymbols();
			textSymbols = tsymbol.getSymbols();
			maxSize = tsymbol.getMaxSize();
		}
		for (int i = maxSize; i > 0; i--) {
			String key = AbstractSymbols.generateKey(i);

			Collection<Symbol> collection = textSymbols.get(key);
			if (collection != null) {
				for (Symbol symbol : collection) {
					String combo = symbol.getCombo();
					int index = comboIndex(str, combo);
					if (index == 0) {
						return symbol.getTranslation()
								+ translate(str.substring(combo.length()));
					} else if (index != -1) {
						return translate(str.substring(0, index))
								+ symbol.getTranslation()
								+ translate(str.substring(index
										+ combo.length()));
					}

				}
			}
		}

		return str;
	}

	private static String translateSymbols(String str, int size,
			HashMap<String, Collection<Symbol>> symbols) {
		for (int i = size; i > 0; i--) {
			String key = AbstractSymbols.generateKey(i);

			Collection<Symbol> collection = symbols.get(key);
			if (collection != null) {
				for (Symbol symbol : collection) {
					String combo = symbol.getCombo();
					int index = str.indexOf(combo);
					if (index != -1) {
						return translate(str.substring(0, index))
								+ symbol.getTranslation()
								+ translate(str.substring(index
										+ combo.length()));
					}
				}
			}
		}
		return null;
	}

	private static int comboIndex(String str, String combo) {
		int index = str.indexOf(combo);
		if (index == -1)
			return -1;
		if (index == 0) {
			int lastIndex = index + combo.length();
			if (lastIndex < str.length()) {
				char c = str.charAt(lastIndex);
				if (Utils.isTextCharacter(c)) {
					return -1;
				}
			}
			return index;
		} else {
			char c = str.charAt(index - 1);
			if (Utils.isTextCharacter(c)) {
				return -1;
			}
			int lastIndex = index + combo.length();
			if (lastIndex < str.length()) {
				c = str.charAt(lastIndex);
				if (Utils.isTextCharacter(c)) {
					return -1;
				}
			}
			return index;
		}
	}

}
