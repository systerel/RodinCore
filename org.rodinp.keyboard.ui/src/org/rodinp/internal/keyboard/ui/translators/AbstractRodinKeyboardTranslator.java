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
package org.rodinp.internal.keyboard.ui.translators;

import static org.rodinp.keyboard.core.KeyboardUtils.debug;
import static org.rodinp.keyboard.core.KeyboardUtils.isTextCharacter;

import java.util.Collection;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.rodinp.keyboard.core.ISymbol;

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
			int subStringEndIndex) {
		if (debug) {
			debug("***************************************");
			debug("Begin: " + beginIndex);
			debug("End: " + subStringEndIndex);
		}
		if (beginIndex == subStringEndIndex)
			return;

		int i = 0;
		for (i = symbolComputer.getMaxSymbolSize(); i > 0; i--) {

			String text = widget.getText();
			int subStringEnd = Math.min(subStringEndIndex, text.length());
			String subString = text.substring(beginIndex, subStringEnd);
			int currentPos = widget.getCaretOffset();
			int indexInSubstring = 0;

			if (debug) {
				debug("Process: \"" + text + "\"");
				debug("Pos: " + currentPos);
				debug("Substring: \"" + subString + "\"");
			}

			final Collection<ISymbol> collection = symbolComputer.getSymbols(i);
			if (collection != null) {
				for (ISymbol symbol : collection) {

					final String combo = symbol.getCombo();
					final String result = symbol.getTranslation();

					int index = subString.indexOf(combo);

					while (index != -1) {

						indexInSubstring = beginIndex + index;

						// particular treatment for the text translators: do
						// not translate symbols which could be substrings of
						// identifiers
 						if (textTranslator && //
								isNoTextTranslation(text, combo,
										indexInSubstring, subStringEnd)) {
							index = subString.indexOf(combo, index + 1);
							continue;
						}

						widget.setSelection(indexInSubstring, indexInSubstring
								+ combo.length());
						if (debug)
							debug("Replace at pos " + indexInSubstring
									+ " from \"" + combo + "\" by \"" + result
									+ "\"");
						widget.insert(result);

						text = widget.getText();
						subStringEnd = Math.min(subStringEndIndex, text.length());
						subString = text.substring(beginIndex, subStringEnd);
						index = subString.indexOf(combo, indexInSubstring+1);
					}
				}
			}
		}
	}
	
	private boolean isNoTextTranslation(String text, String test,
			int indexInString, int stringEndIndex) {
		// if the previous character is a text character then ignore and
		// continue (similar to identical translation).
		if (indexInString != 0) {
			if (isTextCharacter(text.charAt(indexInString - 1))) {
				return true;
			}
		}
		// if the next character is a text character or the end then ignore and
		// continue (similar to identical translation).
		if (indexInString + test.length() != stringEndIndex) {
			if (isTextCharacter(text.charAt(indexInString + test.length()))) {
				return true;
			}
		} else {
			if (stringEndIndex == text.length()) {
				return true;
			}
		}
		return false;
	}

}
