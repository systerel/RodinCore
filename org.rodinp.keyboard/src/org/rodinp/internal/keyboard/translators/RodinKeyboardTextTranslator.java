/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.keyboard.translators;

import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.rodinp.internal.keyboard.KeyboardUtils;

/**
 * @author htson
 *         <p>
 *         The translator for text symbols
 */
public class RodinKeyboardTextTranslator implements IRodinKeyboardTranslator {

	private static Map<String, Collection<Symbol>> symbols = null;

	private static int maxSize = 0;

	/**
	 * Translate the content of the text widget. Because of the "space", it
	 * needs be translated twice.
	 * <p>
	 * 
	 * @see IRodinKeyboardTranslator#translate(org.eclipse.swt.widgets.Text)
	 */
	public void translate(Widget widget) {
		SymbolRegistry registry = SymbolRegistry.getDefault();
		symbols = registry.getTextSymbols();
		maxSize = registry.getMaxTextSymbolSize();
		if (widget instanceof Text) {
			String text = ((Text) widget).getText();
			translate((Text) widget, 0, text.length());
		}
		if (widget instanceof StyledText) {
			String text = ((StyledText) widget).getText();
			translate((StyledText) widget, 0, text.length());
		}
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
			key = Symbols.generateKey(i);

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
							if (KeyboardUtils.isTextCharacter(text.charAt(realIndex - 1))) {
								result = test;
								translated = true;
								break;
							}
						}

						// If the next character is a text character or the end
						// then ignore and continue (similar to identical
						// translation).
						if (realIndex + test.length() != endIndex) {
							if (KeyboardUtils.isTextCharacter(text.charAt(realIndex
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

	private void translate(StyledText widget, int beginIndex, int endIndex) {
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
			key = Symbols.generateKey(i);

			Collection<Symbol> collection = symbols.get(key);
			if (collection != null) {
				for (Symbol symbol : collection) {
					test = symbol.getCombo();
					int index = subString.indexOf(test);

					while (index != -1) {
						result = symbol.getTranslation();

						realIndex = beginIndex + index;

						if (index != 0) {
							if (KeyboardUtils.isTextCharacter(subString.charAt(index - 1))) {
								index = subString.indexOf(test, index + 1);
								continue;
							}
						}

						if (realIndex + test.length() != endIndex) {
							if (KeyboardUtils.isTextCharacter(text.charAt(realIndex
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
