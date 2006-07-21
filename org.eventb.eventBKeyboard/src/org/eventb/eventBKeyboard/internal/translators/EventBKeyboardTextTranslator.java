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
 *         The translator for text symbols
 */
public class EventBKeyboardTextTranslator implements IEventBKeyboardTranslator {

	/**
	 * Testing if a character is a text character
	 * 
	 * @param c:
	 *            a character
	 * @return true if the character is one of the text characters (i.e. 'A' to
	 *         'Z', 'a' to 'z', etc.) false otherwise
	 */
	private boolean isTextCharacter(char c) {
		if (c <= 'Z' && c >= 'A')
			return true;
		if (c <= 'z' && c >= 'a')
			return true;
		if (c <= '9' && c >= '0')
			return true;
		if (c == '_')
			return true;
		return false;
	}

	/**
	 * Attempting to find and translate a text combo. The combo is translated if
	 * there are white space at the beginning and the end of the combo.
	 * 
	 * @param widget:
	 *            The Text Widget
	 * @param text:
	 *            The actual string contains in the "widget"
	 * @param currentPos
	 *            Current position in the string "text"
	 * 
	 * @return true if there is a string which is translated into mathematical
	 *         expression false otherwise
	 */

	private static HashMap<String, Collection<Symbol>> symbols = null;

	private static int maxSize = 0;

	/**
	 * Translate the content of the text widget. Because of the "space", it
	 * needs be translated twice.
	 * <p>
	 * 
	 * @see org.eventb.eventBKeyboard.IEventBKeyboardTranslator#translate(org.eclipse.swt.widgets.Text)
	 */
	public void translate(Text widget) {
		if (symbols == null) {
			TextSymbols textSymbol = new TextSymbols();
			symbols = textSymbol.getSymbols();
			maxSize = textSymbol.getMaxSize();
		}
		String text = widget.getText();
		translate(widget, 0, text.length());
	}

	private void translate(Text widget, int beginIndex, int endIndex) {
		KeyboardUtils.debugText("***************************************");
		KeyboardUtils.debugText("Begin: " + beginIndex);
		KeyboardUtils.debugText("End: " + endIndex);
		if (beginIndex == endIndex)
			return;
		String text = widget.getText();
		int currentPos = widget.getCaretPosition();
		String subString = text.substring(beginIndex, endIndex);

		KeyboardUtils.debugText("Process: \"" + text + "\"");
		KeyboardUtils.debugText("Pos: " + currentPos);
		KeyboardUtils.debugText("Substring: \"" + subString + "\"");

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

						if (index != 0) {
							if (isTextCharacter(text.charAt(index - 1))) {
								break;
							}
						}

						if (realIndex + test.length() != endIndex) {
							if (isTextCharacter(text.charAt(realIndex
									+ test.length()))) {
								break;
							}
						} else {
							if (endIndex == text.length()) {
								break;
							}
						}

						widget.setSelection(realIndex, realIndex
								+ test.length());
						KeyboardUtils.debugText("Replace at pos " + realIndex
								+ " from \"" + test + "\" by \"" + result
								+ "\"");
						widget.insert(result);

						if (currentPos <= realIndex) { // Translate after
							// current pos
							widget.setSelection(currentPos);
						}
						// Transate before current pos
						else if (realIndex + test.length() < currentPos)
							widget.setSelection(currentPos - test.length()
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
