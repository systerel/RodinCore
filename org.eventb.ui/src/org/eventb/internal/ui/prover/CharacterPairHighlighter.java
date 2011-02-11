/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eventb.internal.ui.EventBSharedColor;

/**
 * Class that listens for caret moves (mouse or keyboard) and highlights
 * parentheses, brackets etc.
 * 
 * @author "Thomas Muller"
 */
public class CharacterPairHighlighter implements MouseListener, KeyListener {

	private static final Color GRAY = EventBSharedColor.getSystemColor(SWT.COLOR_DARK_GRAY);
	private static final int NOT_FOUND = -1;
	private final StyledText text;
	private StyleRange range;
	private char c;

	public CharacterPairHighlighter(StyledText text) {
		this.text = text;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// IGNORE
	}

	@Override
	public void mouseDown(MouseEvent e) {
		removeRange();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		higlightPairCharacter();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.character != SWT.ESC) {
			removeRange();
			higlightPairCharacter();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.character == SWT.ESC) {
			removeRange();
		}
	}

	private void removeRange() {
		if (range != null) {
			range.font = null;
			range.rise = 0;
			range.metrics = null;
			range.foreground = null;
			range.background = null;
			range.borderStyle = SWT.NONE;
			range.borderColor = null;
			text.setStyleRange(range);
			range = null;
		}
	}

	private void higlightPairCharacter() {
		final int carStart = text.getCaretOffset();
		final int textEnd = text.getCharCount() - 1;
		if (carStart < 2 || carStart > textEnd || text.getSelectionCount() != 0) {
			return;
		}
		// size == 1
		final String selection = text.getText(carStart - 1, carStart - 1);
		c = selection.charAt(0);

		int pairDistance = NOT_FOUND;
		if (carStart < textEnd) {
			pairDistance = getPairedAhead(text.getText(carStart + 1, textEnd),
					c);
		}
		if (pairDistance == NOT_FOUND) {
			pairDistance = getPairedBehind(text.getText(0, carStart - 2), c);
		}
		if (pairDistance != NOT_FOUND) {
			range = new StyleRange(carStart + pairDistance, 1, null,null);
			range.borderStyle = SWT.BORDER_SOLID;
			range.borderColor = GRAY;
			text.setStyleRange(range);
		}
	}

	private static int getPairedAhead(String text, char c) {
		return getPairedAhead(text, c, 0);
	}

	private static int getPairedBehind(String text, char c) {
		return getPairedBehind(text, c, text.length());
	}

	private static int getPairedAhead(String text, char c, int start) {
		final char toSearch;
		switch (c) {
		case '(':
			toSearch = ')';
			break;
		case '[':
			toSearch = ']';
			break;
		case '{':
			toSearch = '}';
			break;
		default:
			return -1;
		}
		if (text.isEmpty() || start >= text.length()) {
			return -1;
		}
		int oNdx = text.indexOf(c, start);
		int cNdx = text.indexOf(toSearch, start);
		if (oNdx < cNdx && oNdx > 0) {
			return getPairedAhead(text, c, cNdx + 1);
		}
		return cNdx + 1;
	}

	private static int getPairedBehind(String text, char c, int end) {
		final char toSearch;
		switch (c) {
		case ')':
			toSearch = '(';
			break;
		case ']':
			toSearch = '[';
			break;
		case '}':
			toSearch = '{';
			break;
		default:
			return -1;
		}
		if (text.isEmpty() || end <= 0) {
			return -1;
		}
		int oNdx = text.lastIndexOf(toSearch, end);
		int cNdx = text.lastIndexOf(c, end);
		if (oNdx < cNdx && cNdx > 0) {
			return getPairedBehind(text, c, oNdx - 1);
		}
		return -text.length() + oNdx - 1;
	}

}
