/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.eventBKeyboard.internal.translators;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.IEventBKeyboardTranslator;
import org.eventb.internal.eventBKeyboard.KeyboardUtils;

/**
 * @author htson
 *         <p>
 *         The translator for mathematical tempSymbols
 */
public class EventBKeyboardMathTranslator implements IEventBKeyboardTranslator {

	// Combos input which is "math".
	// private static final String[] mathCombo = {
	// "<=>", "\u2264>", "<\u21d2",
	// "=>", "&", "!", "#", "/=", "\u00f7=", "<=", ">=", "\u00f7<<:",
	// "<<:", "/\u2282", "\u00f7<:", "<:", "/\u2286", "\u00f7:",
	// "/\u2208", "<<->>", "<<\u2212>>", "\ue100>", "<\ue101", "<<->",
	// "<<\u2212>", "<\u2194", "<->>", "<\u2212>>", "\u2194>", "<->",
	// "<\u2212>", ">->>", ">\u2212>>", "\u21a3>", ">\u21a0", "+->",
	// "+\u2212>", "-\u2212>", "\u2212\u2212>", ">+>", ">->", ">\u2212>",
	// "+>>", "->>", "\u2212>>", "\u2223->", "\u2223\u2212>", "{}",
	// "/\u2216", "\u00f7\\", "\\\u00f7", "\u2216/", "\\", "\u2217*",
	// "*\u2217", "<+", "><", "\u2223|", "|\u2223", "~", "<<|", "<|",
	// "<\u25c1", "\u2223>", "\u2223>>", "\u25b7>", "%", ".\u00b7",
	// "\u00b7.", ".", "-", "*", "/", ":=", "\u2208=", "\u2208:",
	// "\u2208|", ":", "|", "|->" };

	// The translation of the above "math" combos.
	// private static final String[] mathComboTranslation = {
	// "\u21d4", "\u21d4",
	// "\u21d4", "\u21d2", "\u2227", "\u2200", "\u2203", "\u2260",
	// "\u2260", "\u2264", "\u2265", "\u2284", "\u2282", "\u2284",
	// "\u2288", "\u2286", "\u2288", "\u2209", "\u2209", "\ue102",
	// "\ue102", "\ue102", "\ue102", "\ue100", "\ue100", "\ue100",
	// "\ue101", "\ue101", "\ue101", "\u2194", "\u2194", "\u2916",
	// "\u2916", "\u2916", "\u2916", "\u21f8", "\u21f8", "\u2192",
	// "\u2192", "\u2914", "\u21a3", "\u21a3", "\u2900", "\u21a0",
	// "\u21a0", "\u21a6", "\u21a6", "\u2205", "\u2229", "\u2229",
	// "\u222a", "\u222a", "\u2216", "\u00d7", "\u00d7", "\ue103",
	// "\u2297", "\u2225", "\u2225", "\u223c", "\u2a64", "\u25c1",
	// "\u2a64", "\u25b7", "\u2a65", "\u2a65", "\u03bb", "\u2025",
	// "\u2025", "\u00b7", "\u2212", "\u2217", "\u00f7", "\u2254",
	// "\u2254", ":\u2208", ":\u2223", "\u2208", "\u2223" };

	private static HashMap<String, Collection<Symbol>> symbols = null;

	private static int maxSize = 0;

	/**
	 * Attempting to find and translate a mathematical combo. The combo is
	 * translated once it is found in the string.
	 * 
	 * @param widget:
	 *            The Text Widget
	 * @param text:
	 *            The actual string contains in the "widget"
	 * @param currentPos
	 *            Current position in the string "text"
	 * 
	 * @return true if there is a mathematical string which is translated into
	 *         mathematical expression false otherwise
	 */
	public void translate(Text widget) {
		if (symbols == null) {
			MathSymbols mathSymbols = new MathSymbols();
			symbols = mathSymbols.getSymbols();
			maxSize = mathSymbols.getMaxSize();
		}
		String text = widget.getText();
		translate(widget, 0, text.length());
	}

	private void translate(Text widget, int beginIndex, int endIndex) {
		KeyboardUtils.debugMath("***************************************");
		KeyboardUtils.debugMath("Begin: " + beginIndex);
		KeyboardUtils.debugMath("End: " + endIndex);
		if (beginIndex == endIndex) {
			KeyboardUtils.debugMath("Here " + widget.getCaretPosition());
			return;
		}
		String text = widget.getText();
		int currentPos = widget.getCaretPosition();
		String subString = text.substring(beginIndex, endIndex);

		KeyboardUtils.debugMath("Process: \"" + text + "\"");
		KeyboardUtils.debugMath("Pos: " + currentPos);
		KeyboardUtils.debugMath("Substring: \"" + subString + "\"");

		int realIndex = 0;
		String test = null;
		String result = null;
		String key = "";
		int i = 0;
		for (i = maxSize; i > 0; i--) {
			boolean translated = false;
			key = AbstractSymbols.generateKey(i);

			Collection<Symbol> collection = symbols.get(key);
			if (collection != null) {
				for (Symbol symbol : collection) {
					test = symbol.getCombo();
					int index = subString.indexOf(test);

					if (index != -1) {
						result = symbol.getTranslation();

						realIndex = beginIndex + index;

						widget.setSelection(realIndex, realIndex
								+ test.length());
						KeyboardUtils.debugMath("Replace at pos " + realIndex
								+ " from \"" + test + "\" by \"" + result
								+ "\"");
						widget.insert(result);

						if (currentPos <= realIndex) { // Translate after
							// current pos
							widget.setSelection(currentPos);
							KeyboardUtils.debugMath("New pos: " + currentPos);
						}
						// Transate before current pos
						else if (realIndex + test.length() < currentPos) {
							widget.setSelection(currentPos - test.length()
									+ result.length());
							KeyboardUtils.debugMath("New pos: "
									+ (currentPos - test.length() + result
											.length()));
						}
						// Translate within the current pos
						else {
							widget.setSelection(realIndex + result.length());
							KeyboardUtils.debugMath("New pos: "
									+ (realIndex + result.length()));
						}
						translated = true;
						break;
					}
				}
				if (translated)
					break;
			}

		}

		if (i == 0)
			return;

		else {
			translate(widget, realIndex + result.length(), endIndex
					- test.length() + result.length());
			translate(widget, beginIndex, realIndex);
		}
		return;
	}

}
