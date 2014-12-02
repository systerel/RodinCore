/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation.updaters;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * A snapshot of the current editor state in terms of visible elements, caret
 * position, topIndex value, and selected items.
 * 
 * The topIndex reposition doesn't handle half visible lines, thus in such case,
 * the editor might squiggle when setting the retrieved top index from this
 * snapshot.
 */
public class EditorSnapshot {

	private final RodinEditor editor;
	private DocumentMapper mapper;

	private final LinkedList<PositionRecord> visibleElements = new LinkedList<PositionRecord>();
	private final List<IRodinElement> elementsAfter = new LinkedList<IRodinElement>();
	private PositionRecord caretPosition;
	private boolean isCaretVisible;

	private ILElement[] selection;

	public EditorSnapshot(RodinEditor editor) {
		this.editor = editor;
		this.mapper = editor.getDocumentMapper();
	}

	public void record() {
		recordVisibleElementsPositions();
		recordElementsAfter();
		recordCaretPosition();
		selection = editor.getSelectionController().getSelectedElements();
	}

	private void recordVisibleElementsPositions() {
		visibleElements.clear();
		final SourceViewer viewer = editor.getViewer();
		final StyledText text = editor.getStyledText();
		final int firstVisibleOffset = viewer.getTopIndexStartOffset();
		final int firstVisibleLine = viewer.getTopIndex();
		final int lastVisibleOffset = viewer.getBottomIndexEndOffset();
		final Interval[] visibleIntervals = mapper.findIntervalsBetween(
				firstVisibleOffset, lastVisibleOffset);
		for (Interval interval : visibleIntervals) {
			final ILElement interElem = interval.getElement();
			final EditorElement eElem = mapper.findEditorElement(interElem);
			final int elemStartLine = text.getLineAtOffset(eElem.getOffset());
			final int interStartLine = text.getLineAtOffset(interval
					.getOffset());
			final int distanceFromElementStart = Math.max(0, interStartLine
					- elemStartLine);
			final int distanceFromTopLine = Math.max(0, interStartLine
					- firstVisibleLine);
			visibleElements.add(new PositionRecord(interval.getRodinElement(),
					null, distanceFromElementStart, distanceFromTopLine, 0));
		}
	}

	private void recordElementsAfter() {
		elementsAfter.clear();
		final EditorElement[] editorElems = mapper.findEditorElementsBetween(
				editor.getCurrentOffset(), editor.getStyledText()
						.getCharCount());
		for (EditorElement editorElem : editorElems) {
			final IRodinElement rodinElement = editorElem.getRodinElement();
			if (!rodinElement.isRoot()) {
				elementsAfter.add(rodinElement);
			}
		}
	}

	private boolean isOffsetVisible(int offset) {
		final SourceViewer viewer = editor.getViewer();
		final int firstVisibleOffset = viewer.getTopIndexStartOffset();
		final int lastVisibleOffset = viewer.getBottomIndexEndOffset();
		return firstVisibleOffset <= offset && offset <= lastVisibleOffset;
	}

	private void recordCaretPosition() {
		final int caretOffset = editor.getCurrentOffset();
		isCaretVisible = isOffsetVisible(caretOffset);
		final StyledText text = editor.getStyledText();
		final int caretLine = text.getLineAtOffset(caretOffset);
		final int cartetLineOffset = text.getOffsetAtLine(caretLine);
		final Interval interval = mapper.findInterval(caretOffset);
		if (interval == null) {
			return;
		}
		final EditorElement editorElement = mapper.findEditorElement(interval
				.getElement());
		IRodinElement element = null;
		IRodinElement parent = null;
		int elementLine = -1;
		int distanceFromElementStartLine = 0;
		if (editorElement != null) {
			element = editorElement.getRodinElement();
			final ILElement lParent = editorElement.getLightElement()
					.getParent();
			if (lParent != null) {
				parent = lParent.getElement();
			}
			elementLine = text.getLineAtOffset(editorElement.getOffset());
			distanceFromElementStartLine = Math.max(0, caretLine - elementLine);
		}
		final int distanceFromTopLine = Math.max(0,
				caretLine - text.getTopIndex());
		final int distanceFromLineStart = Math.max(0, caretOffset
				- cartetLineOffset);
		caretPosition = new PositionRecord(element, parent,
				distanceFromElementStartLine, distanceFromTopLine,
				distanceFromLineStart);
	}

	public ILElement[] getSelectedItems() {
		return selection;
	}

	private boolean wasCaretVisible() {
		return isCaretVisible;
	}

	public PositionRecord getCaretPosition() {
		return caretPosition;
	}

	/**
	 * Returns the first editable offset of the newly added element or the right
	 * caret position defined by {@link EditorSnapshot#getCaretOffset()}.
	 * 
	 * @param newElement
	 *            the element added or <code>null</code> if none
	 * @return the new caret position
	 */
	public int getCaretOffset(ILElement newElement) {
		if (newElement != null) {
			final EditorElement editorElement = mapper
					.findEditorElement(newElement);
			final Interval firstEditableInterval = mapper
					.findEditableIntervalAfter(editorElement.getOffset());
			if (firstEditableInterval != null) {
				return firstEditableInterval.getOffset();
			}
			return editorElement.getOffset();
		}
		return getCaretOffset();
	}

	/**
	 * Returns the new caret position calculated as such :<br>
	 * <ul>
	 * <li>the element that owned the caret is found and then, the same position
	 * is returned, or</li>
	 * <li>the beginning of the parent element (if it still exists) is returned,
	 * or</li>
	 * <li>the first offset of the following element that is found after is
	 * returned.</li>
	 * </ul>
	 */
	public int getCaretOffset() {
		final EditorElement editorElement = mapper
				.findEditorElement(caretPosition.element);
		final StyledText text = editor.getStyledText();
		if (editorElement != null) {
			final int elementOffset = editorElement.getOffset();
			final int elementStartLine = text.getLineAtOffset(elementOffset);
			return text.getOffsetAtLine(elementStartLine
					+ caretPosition.distanceFromElementStartLine)
					+ caretPosition.distanceFromLineStartOffset;
		} else if (caretPosition.parent != null) {
			final IRodinElement parent = caretPosition.parent;
			final EditorElement parentVisible = mapper
					.findEditorElement(parent);
			if (parentVisible != null) {
				final int parentLine = text.getLineAtOffset(parentVisible
						.getOffset());
				return text.getOffsetAtLine(parentLine);
			}
		}
		for (IRodinElement element : elementsAfter) {
			final EditorElement editorElem = mapper.findEditorElement(element);
			if (editorElem != null) {
				return editorElem.getOffset();
			}
		}
		return Math.max(0, text.getOffsetAtLine(text.getLineCount() - 2));
	}

	/**
	 * <em>IMPORTANT</em><br>
	 * This method must be called after caret offset has been updated.
	 * 
	 * Returns the index of the first line to be displayed in the editor.<br>
	 * There are two cases:
	 * <ol>
	 * <li>the caret was visible, the first element that was visible, starting
	 * from the top, is revealed,</li>
	 * <li>otherwise, then the editor maintains the distance from the top
	 * element.</li>
	 * </ol>
	 * 
	 * @param newElement
	 *            the new element being added or <code>null</code> if none
	 */
	public int getTopIndex(ILElement newElement) {
		final StyledText text = editor.getStyledText();
		final int newCaretOffset = text.getCaretOffset();
		final int caretLine = text.getLineAtOffset(newCaretOffset);
		if (!wasCaretVisible()) {
			for (PositionRecord pos : visibleElements) {
				final EditorElement editorElement = mapper
						.findEditorElement(pos.element);
				if (editorElement != null) {
					final int firstOffset = editorElement.getOffset();
					return text.getLineAtOffset(firstOffset)
							+ pos.distanceFromElementStartLine;
				}
			}
		}
		return Math.max(0, caretLine - caretPosition.distanceFromTopLine);
	}

	/**
	 * Class recording a position relatively to :
	 * <ul>
	 * <li>the element which owns its offset,</li>
	 * <li>the parent element of the element which owns its offset,</li>
	 * <li>the distance from the starting line of this element,</li>
	 * <li>the distance from the top line of the visible area,</li>
	 * <li>the distance from the beginning of the line (for caret offsets).</li>
	 * </ul>
	 */
	public static class PositionRecord {

		public final IRodinElement parent;
		public final IRodinElement element;
		public final int distanceFromElementStartLine;
		public final int distanceFromTopLine;
		public final int distanceFromLineStartOffset;

		public PositionRecord(IRodinElement element, IRodinElement parent,
				int distanceFromElementStartLine, int distanceFromTopLine,
				int distanceFromLineStartOffset) {
			this.element = element;
			this.parent = parent;
			this.distanceFromElementStartLine = distanceFromElementStartLine;
			this.distanceFromTopLine = distanceFromTopLine;
			this.distanceFromLineStartOffset = distanceFromLineStartOffset;
		}

	}

}