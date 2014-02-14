/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
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
package org.rodinp.internal.keyboard.ui.translators;

import static org.rodinp.keyboard.core.KeyboardUtils.debug;
import static org.rodinp.keyboard.core.KeyboardUtils.isTextCharacter;

import java.util.Collection;

import org.rodinp.keyboard.core.ISymbol;
import org.rodinp.keyboard.ui.RodinKeyboardUIPlugin;

/**
 * @author htson
 */
public class LegacyRodinKeyboardTranslator extends AbstractRodinKeyboardTranslator {
	
	public LegacyRodinKeyboardTranslator(boolean textTranslator,
			boolean debug, SymbolComputer symbolComputer) {
		super(textTranslator, debug, symbolComputer);
	}
	
	@Override
	public void translate(AbstractWidgetWrapper widget, int beginIndex,
			int endIndex) {
		if (debug) {
			debug("***************************************");
			debug("Begin: " + beginIndex);
			debug("End: " + endIndex);
		}
		if (beginIndex == endIndex)
			return;
		int currentPos = widget.getCaretOffset();
		final String text = widget.getText();
		final String subString = text.substring(beginIndex, endIndex);

		if (debug) {
			debug("Process: \"" + text + "\"");
			debug("Pos: " + currentPos);
			debug("Substring: \"" + subString + "\"");
		}
		int realIndex = 0;
		String combo = null;
		String result = null;

		int i = 0;
		for (i = symbolComputer.getMaxSymbolSize(); i > 0; i--) {
			boolean translated = false;

			final Collection<ISymbol> collection = symbolComputer.getSymbols(i);
			if (collection != null) {
				for (ISymbol symbol : collection) {
					combo = symbol.getCombo();
					int index = subString.indexOf(combo);
					// particular treatment for the text translators: do
					// not translate symbols which could be substrings of
					// identifiers
					if (textTranslator) {
						while (index != -1) {
							realIndex = beginIndex + index;
							if (isNoTextTranslation(text, combo, realIndex,
									endIndex)) {
								index = subString.indexOf(combo, index + 1);
								continue;
							}
							break;
						}
					}

					if (index != -1) {
						result = symbol.getTranslation();
						realIndex = beginIndex + index;
						widget.setSelection(realIndex,
								realIndex + combo.length());
						if (debug)
							debug("Replace at pos "
									+ realIndex + " from \"" + combo
									+ "\" by \"" + result + "\"");
						widget.insert(result);

						if (currentPos <= realIndex) { // Translate after
							// current pos
							widget.setSelection(currentPos);
						}
						// Transate before current pos
						else if (realIndex + combo.length() < currentPos)
							widget.setSelection(currentPos - combo.length()
									+ result.length());
						// Translate within the current pos
						else {
							widget.setSelection(realIndex + result.length());
						}
						translated = true;
						break;
					}
				}
				if (translated)
					break;
			}

		}

		// no translation occurred, exit.
		if (i == 0)
			return;

		// otherwise, keep translating the head and last of the string
		else {
			translate(widget, realIndex + result.length(),
					endIndex - combo.length() + result.length());
			translate(widget, beginIndex, realIndex);
		}
		return;
	}
	
	private boolean isNoTextTranslation(String text, String test,
			int realIndex, int endIndex) {
		// if the previous character is a text character then ignore and
		// continue (similar to identical translation).
		if (realIndex != 0) {
			if (isTextCharacter(text.charAt(realIndex - 1))) {
				return true;
			}
		}
		// if the next character is a text character or the end then ignore and
		// continue (similar to identical translation).
		if (realIndex + test.length() != endIndex) {
			if (isTextCharacter(text.charAt(realIndex + test.length()))) {
				return true;
			}
		} else {
			if (endIndex == text.length()) {
				return true;
			}
		}
		return false;
	}

	public static class LegacyMathTranslator extends
			LegacyRodinKeyboardTranslator {

		public LegacyMathTranslator() {
			super(false, RodinKeyboardUIPlugin.MATH_DEBUG,
					new SymbolComputer.MathSymbolComputer());
		}

	}

	public static class LegacyTextTranslator extends
			LegacyRodinKeyboardTranslator {

		public LegacyTextTranslator() {
			super(true, RodinKeyboardUIPlugin.TEXT_DEBUG,
					new SymbolComputer.TextSymbolComputer());
		}

	}

}
