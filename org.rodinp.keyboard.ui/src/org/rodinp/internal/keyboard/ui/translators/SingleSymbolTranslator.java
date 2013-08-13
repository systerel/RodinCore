/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.keyboard.ui.translators;

import static org.rodinp.keyboard.core.KeyboardUtils.debug;
import static org.rodinp.keyboard.core.KeyboardUtils.isTextCharacter;

import org.rodinp.keyboard.core.ISymbol;

/**
 * This class realizes the translation of a single symbol within the contents of
 * a widget.
 */
public class SingleSymbolTranslator {

	private final AbstractWidgetWrapper widget;
	private final boolean isTextTranslator;
	private final boolean debug;

	private String combo;
	private String translation;
	
	public SingleSymbolTranslator(AbstractWidgetWrapper widget,
			boolean isTextTranslator, boolean debug) {
		this.widget = widget;
		this.isTextTranslator = isTextTranslator;
		this.debug = debug;
	}
	
	/**
	 * Sets the symbol to be translated.
	 * 
	 * @param symbol
	 * 		the symbol which combo shall be translated
	 */
	public void setSymbol(ISymbol symbol) {
		this.combo = symbol.getCombo();
		this.translation = symbol.getTranslation();
	}

	/**
	 * Translate all occurrences of the current symbol (where possible) in the
	 * text contents of the handled widget.
	 */
	public void translateAllOccurrences() {
		final String text = widget.getText();
		if (debug) {
			debug("Process: \"" + text + "\"");
			debug("Pos: " + widget.getCaretOffset());
		}
		int nextIndex = findOccurrence(0);
		while (nextIndex != -1) {
			final boolean translated = translateOccurrenceAt(nextIndex);
			nextIndex += (translated) ? translation.length() : combo.length();
			nextIndex = findOccurrence(nextIndex);
		}
	}

	/**
	 * Returns the index of the next combo found from the given start index in
	 * the widget text contents or <code>-1</code> if the combo has not been
	 * found.
	 * 
	 * @param start
	 *            the index to start searching a occurrence of combo from
	 * @return the index of the next combo found from the given start index in
	 *         the widget text contents or <code>-1</code> if the combo has not
	 *         been found
	 */
	private int findOccurrence(int start) {
		final String text = widget.getText();
		if (start >= text.length())
			return -1;
		final int index = text.substring(start).indexOf(combo);
		return (index == -1) ? index : start + index;
	}

	private boolean translateOccurrenceAt(int occurrenceIndex) {
		final int currentPos = widget.getCaretOffset();
		// particular treatment for the text translators: do
		// not translate symbols which could be substrings of
		// identifiers
		if (isTextTranslator && !shallBeTranslated(occurrenceIndex)) {
			return false;
		}
		if (debug)
			debug("Replace at pos " + occurrenceIndex + //
					" from \"" + combo + //
					"\" by \"" + translation + "\"");
		// replacement is composed of three phases :
		// - combo is selected [setSelection()]
		// - combo is replaced by an insertion [insert()]
		// - caret in the widget is positioned 
		widget.setSelection(occurrenceIndex, occurrenceIndex + combo.length());
		widget.insert(translation);
		positionCaret(occurrenceIndex, currentPos);
		return true;
	}

	private void positionCaret(int occurrenceIndex, int currentPos) {
		// translate after current pos
		if (currentPos <= occurrenceIndex) {
			widget.setSelection(currentPos);
			return;
		}
		// translate before current pos
		final int comboLength = combo.length();
		if (occurrenceIndex + comboLength < currentPos) {
			widget.setSelection(currentPos - comboLength + translation.length());
			return;
		}
		// translate within the current pos
		widget.setSelection(occurrenceIndex + translation.length());
	}

	private boolean shallBeTranslated(int occurrenceIndex) {
		final String text = widget.getText();
		// if the previous character is a text character then ignore and
		// continue (similar to identical translation).
		if (occurrenceIndex != 0) {
			if (isTextCharacter(text.charAt(occurrenceIndex - 1))) {
				return false;
			}
		}
		// if the next character is a text character or the end then ignore and
		// continue (similar to identical translation).
		if (occurrenceIndex + combo.length() == text.length()) {
			return false;
		}
		if (isTextCharacter(text.charAt(occurrenceIndex + combo.length()))) {
			return false;
		}
		return true;
	}

}
