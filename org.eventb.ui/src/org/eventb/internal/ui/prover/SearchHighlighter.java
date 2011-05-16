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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eventb.internal.ui.EventBSharedColor;

/**
 * Class able to display occurences of a substring in proverUI views.
 * 
 * @author "Thomas Muller"
 */
public class SearchHighlighter {

	protected static final Color BGCOLOR = EventBSharedColor.getColor(new RGB(85,
			255, 185));

	private static class HighlightData {

		private final StyledText text;
		private final HighlightListener hListener;
		private final SearchHighlighter highlighter;
		private StyleRange[] originalRanges;
		private List<Integer> matchingOffsets;
		private int currentMatching = -1;

		public HighlightData(StyledText text, SearchHighlighter highlighter) {
			this.text = text;
			this.hListener = new HighlightListener(this);
			this.highlighter = highlighter;
			this.matchingOffsets = new ArrayList<Integer>();
		}
		
		public void addHiglightListener() {
			if (text == null || text.isDisposed())
				return;
			text.addMouseListener(hListener);
			text.addKeyListener(hListener);
		}

		public void removeHighlightListener() {
			if (text == null || text.isDisposed())
				return;
			text.removeMouseListener(hListener);
			text.addKeyListener(hListener);
		}
		
		protected void rememberRanges() {
			if (originalRanges == null && text != null && !text.isDisposed())
				originalRanges = text.getStyleRanges();
		}
		
		protected void updateSelection() {
			if (text != null && !text.isDisposed())
				highlighter.setToSearch(text.getSelectionText());
		}

		protected void restoreOriginalRanges() {
			if (validRanges() && text != null && !text.isDisposed())
				text.setStyleRanges(originalRanges);
		}
		
		protected void refreshHighlight() {
			highlighter.refreshHighlight();
		}
		
		protected boolean isSelection() {
			return text.getSelectionCount() > 0;
		}
		
		private boolean validRanges() {
			return originalRanges != null && originalRanges.length > 0;
		}
		
		public void highlightMatchings(String toSearch, Color bgColor) {
			matchingOffsets = getSearchMatchingOffsets(toSearch);
			for (int m : matchingOffsets) {
				final int length = toSearch.length();
				final StyleRange[] styleRanges = text.getStyleRanges(m, length,
						true);
				text.setStyleRange(new StyleRange(m, length, null, bgColor));
				for (StyleRange r : styleRanges) {
					r.background = bgColor;
					text.setStyleRange(r);
				}
			}
		}
		
		private void revealMatching(int matchingOffset) {
			text.setCaretOffset(matchingOffset);
		}
		
		public void revealNextMatching() {
			if (!matchingOffsets.contains(currentMatching)) {
				final int caretOffset = text.getCaretOffset();
				currentMatching = searchNextMatching(caretOffset);
			} else {
				final int index = matchingOffsets.indexOf(currentMatching);
				if (index < 0 || index > matchingOffsets.size())
					return;
				currentMatching = matchingOffsets.listIterator(index).next();
			}
			if (currentMatching != -1) {
				revealMatching(currentMatching);
				currentMatching++;
			}
		}

		public void revealPreviousMatching() {
			if (!matchingOffsets.contains(currentMatching)) {
				final int caretOffset = text.getCaretOffset();
				currentMatching = searchPreviousMatching(caretOffset);
			} else {
				final int index = matchingOffsets.indexOf(currentMatching);
				if (index < 0 || index >= matchingOffsets.size())
					return;
				if (index == 0)
					currentMatching = matchingOffsets.get(index);
				else
					currentMatching = matchingOffsets.listIterator(index)
							.previous();
			}
			if (currentMatching != -1) {
				revealMatching(currentMatching);
			}
		}

		private int searchNextMatching(int caretOffset) {
			for (int i : matchingOffsets) {
				if (i > caretOffset) {
					return i;
				}
			}
			return -1;
		}
		
		private int searchPreviousMatching(int caretOffset) {
			for (int i = matchingOffsets.size()-1; i > 0; i--){
				final Integer loc = matchingOffsets.get(i);
				if (loc < caretOffset){
					return loc;
				}
			}
			return -1;
		}

		public List<Integer> getSearchMatchingOffsets(String toLight) {
			if (text.isDisposed())
				return Collections.emptyList();
			final String contents = text.getText();
			final List<Integer> collected = new ArrayList<Integer>();
			collectMatchingOffsets(collected, 0, toLight, contents);
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

		public StyledText getText() {
			return text;
		}

	}

	private static class HighlightListener implements MouseListener,
			KeyListener {

		private final HighlightData data;

		public HighlightListener(HighlightData data) {
			this.data = data;
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// Nothing to do
		}

		@Override
		public void mouseDown(MouseEvent e) {
			// Nothing to do
		}

		@Override
		public void mouseUp(MouseEvent e) {
			data.updateSelection();
			if (data.isSelection()){
				data.refreshHighlight();				
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// Nothing to do
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			data.updateSelection();
			if (data.isSelection()){
				data.refreshHighlight();				
			}
		}

	}

	private static SearchHighlighter INSTANCE;

	private Set<HighlightData> toHighlight;

	private String toSearch;
	
	private boolean highlight;

	private SearchHighlighter() {
		// private constructor : SINGLETON
		toHighlight = new LinkedHashSet<HighlightData>();
		highlight = false;
		setToSearch("");
	}

	public static SearchHighlighter newHighlighter() {
		if (INSTANCE == null)
			INSTANCE = new SearchHighlighter();
		return INSTANCE;
	}

	public void highlight(StyledText text) {
		final HighlightData data = new HighlightData(text, this);
		data.addHiglightListener();
		toHighlight.add(data);
	}
	
	public void removeHighlight(StyledText text) {
		HighlightData toRemove = null;
		for (HighlightData data : toHighlight) {
			if (data.getText() == text) {
				toRemove = data;
				break;
			}
		}
		if (toRemove != null) {
			toRemove.removeHighlightListener();
			toHighlight.remove(toRemove);
		}
	}

	public void highlightPattern(String pattern) {
		removeHightlight(true);
		setToSearch(pattern);
		refreshHighlight();
	}
	
	public void setToSearch(String toSearchStr) {
		toSearch = toSearchStr;
	}
	
	public String getToSearch() {
		return toSearch;
	}

	public void refreshHighlight() {
		if (!highlight) {
			removeHightlight(false);
			return;
		}
		for (HighlightData d : toHighlight) {
			final StyledText text = d.getText();
			if (text != null && !text.isDisposed() && text.isFocusControl()) {
				final String selectionText = text.getSelectionText();
				if (!selectionText.isEmpty()) {
					setToSearch(selectionText);
					break;
				}
			}
		}
		for (HighlightData d : toHighlight) {
			d.rememberRanges();
			d.restoreOriginalRanges();
		}
		if (toSearch.isEmpty())
			return;
		for (HighlightData d : toHighlight) {
			d.highlightMatchings(toSearch, BGCOLOR);
		}
	}

	public void removeHightlight(boolean removeSelection) {
		setToSearch("");
		for (HighlightData d : toHighlight) {
			d.rememberRanges();
			d.restoreOriginalRanges();
			if (removeSelection)
				removeSelection(d);
		}
	}

	private void removeSelection(HighlightData d) {
		final StyledText text = d.getText();
		if (text == null || text.isDisposed())
			return;
		if (text.isFocusControl()) {
			final int caretOffset = text.getCaretOffset();
			text.setSelection(caretOffset);
		}
	}
	
	public void traverseNext() {
		for (HighlightData d : toHighlight) {
			final StyledText text = d.getText();
			if (!text.isDisposed() && text.isFocusControl()) {
				d.revealNextMatching();
				break;
			}
		}
	}

	public void traversePrevious() {
		for (HighlightData d : toHighlight) {
			final StyledText text = d.getText();
			if (!text.isDisposed() && text.isFocusControl()) {
				d.revealPreviousMatching();
				break;
			}
		}
	}

	public void activateHighlight(boolean active) {
		highlight = active;
		refreshHighlight();
	}
	
	public boolean isHighlightActivated() {
		return highlight;
	}
	
}
