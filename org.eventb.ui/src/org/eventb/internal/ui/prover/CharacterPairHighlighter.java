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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
public class CharacterPairHighlighter implements MouseListener, KeyListener,
		FocusListener {

	private static final Color GRAY = EventBSharedColor
			.getSystemColor(SWT.COLOR_GRAY);
	private static final int NOT_FOUND = -1;
	private final StyledText text;
	private StyleRange range;
	private StyleRange backupRange;
	private char c;

	private CharacterPairHighlighter(StyledText text) {
		this.text = text;
	}

	public static CharacterPairHighlighter highlight(StyledText text) {
		final CharacterPairHighlighter h = new CharacterPairHighlighter(text);
		text.addMouseListener(h);
		text.addKeyListener(h);
		text.addFocusListener(h);
		return h;
	}
	
	public void remove() {
		if (text != null && !text.isDisposed()) {
			text.removeKeyListener(this);
			text.removeMouseListener(this);
			text.removeFocusListener(this);
		}
	}
		
	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// Nothing to do
	}

	@Override
	public void mouseDown(MouseEvent e) {
		removeRange(range, backupRange, text);
	}

	@Override
	public void mouseUp(MouseEvent e) {
		higlightPairCharacter();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.character != SWT.ESC) {
			removeRange(range, backupRange, text);
			higlightPairCharacter();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Nothing to do
	}

	@Override
	public void focusGained(FocusEvent e) {
		// Nothing to do
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		removeRange(range, backupRange, text);
	}

	private static void removeRange(StyleRange toErase, StyleRange backup,
			StyledText text) {
		final StyleRange eraser;
		if (backup != null) {
			eraser = backup;
		} else if (toErase != null) {
			toErase.font = null;
			toErase.rise = 0;
			toErase.metrics = null;
			toErase.background = null;
			toErase.borderStyle = SWT.NONE;
			toErase.borderColor = null;
			eraser = toErase;
		} else {
			eraser = null;
		}
		if (eraser != null) {
			text.setStyleRange(eraser);
			toErase = null;
		}
	}

	private void higlightPairCharacter() {
		final int carStart = text.getCaretOffset();
		final int textEnd = text.getCharCount() - 1;
		if (carStart < 1 || carStart - 1 > textEnd
				|| text.getSelectionCount() != 0) {
			return;
		}
		// size == 1
		final String selection = text.getText(carStart - 1, carStart - 1);
		c = selection.charAt(0);

		int pairDistance = NOT_FOUND;
		if (carStart < textEnd) {
			pairDistance = getPairedAhead(text.getText(carStart, textEnd),
					c);
		}
		if (pairDistance == NOT_FOUND && carStart > 1) {
			pairDistance = getPairedBehind(text.getText(0, carStart - 2), c);
		}
		if (pairDistance != NOT_FOUND) {
			final int matched = carStart + pairDistance;
			backupRange = text.getStyleRangeAtOffset(matched);
			range = new StyleRange(matched, 1, null, null);
			if (backupRange != null)
				range.foreground = backupRange.foreground;
			range.borderStyle = SWT.BORDER_SOLID;
			range.borderColor = GRAY;
			text.setStyleRange(range);
		}
	}

	private static int getPairedAhead(String text, char c) {
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
		int depth = 0;
		int cnt;
		boolean found = false;
		for (cnt = 0; cnt < text.length(); cnt++) {
			if (depth == 0 && text.charAt(cnt) == toSearch) {
				found = true;
				break;
			}
			if (depth > 0 && text.charAt(cnt) == toSearch) {
				depth--;
			}
			if (text.charAt(cnt) == c) {
				depth++;
			}
		}
		if (depth > 0 || !found) {
			return -1;
		}
		return cnt;
	}

	private static int getPairedBehind(String text, char c) {
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
		int depth = 0;
		int cnt;
		boolean found = false;
		for (cnt = text.length()-1; cnt >= 0; cnt--) {
			if (depth == 0 && text.charAt(cnt) == toSearch) {
				found = true;
				break;
			}
			if (depth > 0 && text.charAt(cnt) == toSearch) {
				depth--;
			}
			if (text.charAt(cnt) == c) {
				depth++;
			}
		}
		if (depth > 0 || !found) {
			return -1;
		}
		return -text.length() + cnt - 1;
	}

}
