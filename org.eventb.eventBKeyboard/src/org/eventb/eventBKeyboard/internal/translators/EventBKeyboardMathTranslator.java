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

	private HashMap<String, Collection<Symbol>> symbols = null;

	private int maxSize = 0;

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
						// Translate before current pos
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
