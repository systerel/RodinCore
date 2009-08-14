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

import org.eclipse.swt.custom.StyledText;
import org.eventb.eventBKeyboard.IEventBStyledTextTranslator;
import org.eventb.internal.eventBKeyboard.KeyboardUtils;

public class EventBStyledTextTextTranslator implements
IEventBStyledTextTranslator {

	HashMap<String, Collection<Symbol>> symbols = null;

	int maxSize = 0;

	public void translate(StyledText widget) {
		if (symbols == null) {
			TextSymbols textSymbol = new TextSymbols();
			symbols = textSymbol.getSymbols();
			maxSize = textSymbol.getMaxSize();
		}
		String text = widget.getText();
		translate(widget, 0, text.length());
	}

	void translate(StyledText widget, int beginIndex, int endIndex) {
		KeyboardUtils.debugText("***************************************");
		KeyboardUtils.debugText("Begin: " + beginIndex);
		KeyboardUtils.debugText("End: " + endIndex);
		if (beginIndex == endIndex)
			return;
		String text = widget.getText();
		int currentPos = widget.getCaretOffset();
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

					while (index != -1) {
						result = symbol.getTranslation();

						realIndex = beginIndex + index;

						if (index != 0) {
							if (Utils.isTextCharacter(subString.charAt(index - 1))) {
								index = subString.indexOf(test, index + 1);
								continue;
							}
						}

						if (realIndex + test.length() != endIndex) {
							if (Utils.isTextCharacter(text.charAt(realIndex
									+ test.length()))) {
								index = subString.indexOf(test, index + 1);
								continue;
							}
						} else {
							if (endIndex == text.length()) {
								index = subString.indexOf(test, index + 1);
								continue;
							}
						}
						break;
					}
					if (index != -1) {

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
