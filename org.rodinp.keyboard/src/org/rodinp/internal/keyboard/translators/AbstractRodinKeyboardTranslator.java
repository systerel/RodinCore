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
 *     Systerel - removed duplicated code
 *******************************************************************************/
package org.rodinp.internal.keyboard.translators;

import static org.rodinp.keyboard.core.KeyboardUtils.isTextCharacter;

import java.util.Collection;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.rodinp.keyboard.core.ISymbol;
import org.rodinp.keyboard.core.KeyboardUtils;

/**
 * @author htson
 *         <p>
 *         Abstract class for symbol translation.
 *         </p>
 */
public abstract class AbstractRodinKeyboardTranslator implements IRodinKeyboardTranslator {

	/**
	 * Boolean value telling whether the DEBUG mode is activated or not
	 */
	protected final boolean debug;

	/**
	 * Boolean value telling whether the current translator is a text translator
	 * or not.
	 */
	protected final boolean textTranslator;

	/**
	 * The object which is responsible of computing and resolving the
	 * appropriate symbols depending on whether the translator is a text
	 * translator or a mathematical translator.
	 */
	protected final SymbolComputer symbolComputer;

	public AbstractRodinKeyboardTranslator(boolean textTranslator,
			boolean debug, SymbolComputer symbolComputer) {
		this.debug = debug;
		this.symbolComputer = symbolComputer;
		this.textTranslator = textTranslator;
	}
	
	@Override
	public void translate(Widget widget) {
		if (widget instanceof Text) {
			translate(new TextWrapper((Text) widget));
		}
		if (widget instanceof StyledText) {
			translate(new StyledTextWrapper((StyledText) widget));
		}
	}
	
	private void translate(AbstractWidgetWrapper widget) {
		final String text = widget.getText();
		final int beginIndex = 0;
		final int endIndex = text.length();
		translate(widget, beginIndex, endIndex);
	}
	
	private void translate(AbstractWidgetWrapper widget, int beginIndex,
			int endIndex) {
		if (debug) {
			KeyboardUtils.debug("***************************************");
			KeyboardUtils.debug("Begin: " + beginIndex);
			KeyboardUtils.debug("End: " + endIndex);
		}
		if (beginIndex == endIndex)
			return;
		int currentPos = widget.getCaretOffset();
		final String text = widget.getText();
		final String subString = text.substring(beginIndex, endIndex);

		if (debug) {
			KeyboardUtils.debug("Process: \"" + text + "\"");
			KeyboardUtils.debug("Pos: " + currentPos);
			KeyboardUtils.debug("Substring: \"" + subString + "\"");
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

					if (index != -1) {
						result = symbol.getTranslation();
						realIndex = beginIndex + index;

						// particular treatment for the text translators: do
						// not translate symbols which could be substrings of
						// identifiers
						if (textTranslator && //
								isNoTextTranslation(text, combo, realIndex,
										endIndex)) {
							result = combo;
							break;
						}

						widget.setSelection(realIndex,
								realIndex + combo.length());
						if (debug)
							KeyboardUtils.debug("Replace at pos "
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

}
