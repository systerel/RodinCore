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

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.graphics.Point;
import org.eventb.internal.ui.EventBSharedColor;

/**
 * Class able to display occurences of a substring in hypotheses.
 * 
 * @author "Thomas Muller"
 */
public class SearchHighlighter implements FocusListener, KeyListener,
		MouseListener {

	private static final Color GREEN = EventBSharedColor.getSystemColor(SWT.COLOR_GREEN);
	private final StyledText text;
	private StyleRange[] originalRanges;
	
	private SearchHighlighter(StyledText text) {
		this.text = text;
	}
	
	public static SearchHighlighter hightlight(StyledText text) {
		final SearchHighlighter sh = new SearchHighlighter(text);
		text.addFocusListener(sh);
		text.addKeyListener(sh);
		text.addMouseListener(sh);
		return sh;
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
		rememberOriginalRanges();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (text.getSelectionCount() > 0)
			restoreOriginalRanges();
		displayMatchings();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		rememberOriginalRanges();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.character == SWT.ESC) {
			restoreOriginalRanges();
			originalRanges = null;
			return;
		}
		if (text.getSelectionCount() > 0) {
			restoreOriginalRanges();
		}
		displayMatchings();
	}

	@Override
	public void focusLost(FocusEvent e) {
		restoreOriginalRanges();
		originalRanges = null;
	}

	@Override
	public void focusGained(FocusEvent e) {
		rememberOriginalRanges();
	}

	private void rememberOriginalRanges() {
		if (originalRanges == null) {
			originalRanges = text.getStyleRanges(); 			
		}
	}
	
	private void restoreOriginalRanges() {
		if (originalRanges == null)
			return;
		text.setStyleRanges(originalRanges);
	}

	private void displayMatchings() {
		final String selection = text.getSelectionText();
		if (selection.isEmpty())
			return;
		final List<Integer> matchingOffsets = getSearchMatchingOffsets(selection);
		final Point curSel = text.getSelection();
		for (int m : matchingOffsets) {
			if (curSel.x == m)
				continue;
			final StyleRange toSet = new StyleRange(m, selection.length(), null, GREEN); 
			text.setStyleRange(toSet);
		}
	}

	private List<Integer> getSearchMatchingOffsets(String selection) {
		final String contents = text.getText();
		final List<Integer> collected = new ArrayList<Integer>();
		collectMatchingOffsets(collected, 0, selection, contents);
		return collected;
	}
	
	private void collectMatchingOffsets(List<Integer> collected, int begin,
			String searched, String contents) {
		final int index = contents.indexOf(searched, begin);
		if (index == -1) {
			return;
		}
		collected.add(index);
		if (index + 1 < contents.length()) {
			collectMatchingOffsets(collected, index + 1, searched, contents);
		}
	}
	
}
