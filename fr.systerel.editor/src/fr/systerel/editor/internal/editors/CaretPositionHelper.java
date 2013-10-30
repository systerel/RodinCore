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
package fr.systerel.editor.internal.editors;

import org.eclipse.swt.custom.StyledText;

/**
 * Helper class which registers a caret position as a distance from the styled
 * text end, and further calculates new caret position from this distance.
 */
public class CaretPositionHelper {

	/**
	 * The base styled text
	 */
	private final StyledText editorText;

	private int oldCharCount = 0;
	private int oldCaretPos = 0;
	private int oldLineIndex = 0;
	private int newCharCount = 0;

	public CaretPositionHelper(StyledText editorText) {
		this.editorText = editorText;
	}

	public static CaretPositionHelper getHelper(
			StyledText editorText) {
		return new CaretPositionHelper(editorText);
	}

	/**
	 * Registers the caret position from which to calculate the new one.
	 */
	public void recordCaretPosition() {
		oldCharCount = editorText.getCharCount();
		oldCaretPos = editorText.getCaretOffset();
		oldLineIndex = editorText.getLineAtOffset(oldCaretPos);
	}

	private int getOldPositionToEnd() {
		final int toEnd = Math.min(oldCharCount, oldCharCount - oldCaretPos);
		return Math.max(0, toEnd);
	}

	/**
	 * Returns an offset which keeps the recorded distance to the end of the
	 * styled text component.
	 *
	 * @return a calculated offset which is located at the same distance from
	 *         the current end of the styled text as the recorded location.
	 */
	public int getNewPositionToEnd() {
		newCharCount = editorText.getCharCount();
		return Math.min(newCharCount - getOldPositionToEnd(), newCharCount);
	}

	/**
	 * Returns the old caret position if the styled text contains such position,
	 * or the new character count (size) of the styled otherwise.
	 *
	 * @return the recorded position if the styled text contains such position,
	 *         the styled text character count otherwise.
	 */
	public int getSafeNewPositionToEnd() {
		newCharCount = editorText.getCharCount();
		if (oldCaretPos < newCharCount) {
			final int lineAtOffset = editorText.getLineAtOffset(oldCaretPos);
			if (lineAtOffset != oldLineIndex) {
				return getNewPositionToEnd();
			} else {
				return oldCaretPos;
			}
		}
		return newCharCount;
	}

	/**
	 * Returns the current offset of the line start at <code>lineIndex</code> or
	 * <code>-1</code> if <code>lineIndex</code> is invalid.
	 */
	public int getSafeLineOffset(int lineIndex) {
		if ((lineIndex < 0) || (lineIndex > editorText.getLineCount())) {
			return -1;
		}
		return editorText.getOffsetAtLine(lineIndex);
	}

}