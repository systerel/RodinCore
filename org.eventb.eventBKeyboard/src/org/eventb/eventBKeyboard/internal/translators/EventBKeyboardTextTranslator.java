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

	HashMap<String, Collection<Symbol>> symbols = null;

	int maxSize = 0;

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

	void translate(Text widget, int beginIndex, int endIndex) {
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

						// If the previous character is a text character then
						// ignore and continue (similar to identical
						// translation).
						if (realIndex != 0) {
							if (Utils.isTextCharacter(text.charAt(realIndex - 1))) {
								result = test;
								translated = true;
								break;
							}
						}

						// If the next character is a text character or the end
						// then ignore and continue (similar to identical
						// translation).
						if (realIndex + test.length() != endIndex) {
							if (Utils.isTextCharacter(text.charAt(realIndex
									+ test.length()))) {
								result = test;
								translated = true;
								break;
							}
						} else {
							if (endIndex == text.length()) {
								result = test;
								translated = true;
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
						// Translate before current pos
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

		// No translation occurred, exit.
		if (i == 0)
			return;

		// Otherwise, keep translating the head and last of the string
		else {
			translate(widget, realIndex + result.length(), endIndex
					- test.length() + result.length());
			translate(widget, beginIndex, realIndex);
		}
		return;
	}

}
