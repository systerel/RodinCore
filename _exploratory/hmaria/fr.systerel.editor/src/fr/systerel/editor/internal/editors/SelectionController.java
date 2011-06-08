/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.Selections.MultipleSelection;
import fr.systerel.editor.internal.editors.Selections.SelectionEffect;

/**
 * Controls the selection in the RodinEditor and decides when it should be
 * editable. It is not possible to have just parts of the text editable. So the
 * whole styled text widget is set to editable or not according to the current
 * carret position. Since this class directly accesses the styled text widget,
 * coordinates may be in need of transformation, since the editor implements
 * folding and the <code>DocumentMapper</code> works with model coordinates and
 * not widget coordinates.
 */
public class SelectionController implements MouseListener, VerifyListener,
		VerifyKeyListener, TraverseListener {

	// TODO tracing
	public static boolean DEBUG;
	

	private StyledText styledText;
	private DocumentMapper mapper;
	private ProjectionViewer viewer;
	private OverlayEditor overlayEditor;
	private final MultipleSelection selection;

	public SelectionController(StyledText styledText, DocumentMapper mapper,
			ProjectionViewer viewer, OverlayEditor overlayEditor) {
		super();
		this.styledText = styledText;
		this.viewer = viewer;
		this.mapper = mapper;
		this.overlayEditor = overlayEditor;
		selection = new MultipleSelection(new SelectionEffect(styledText));
	}

	public boolean hasSelectedElements() {
		return !selection.isEmpty();
	}
	
	/**
	 * @return the selectedElement
	 */
	public ILElement[] getSelectedElements() {
		return selection.getElements();
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		if (DEBUG)
			System.out.println("double click " + e);
		final int offset = getOffset(e);
		if (offset < 0) return;
		toggleSelection(offset);
	}
	
	public Position toggleSelection(int offset) {
		// select the enclosing element
		final EditorElement editElem = mapper.findItemContaining(offset);
		if (editElem == null) return null;
		return toggleSelection(editElem);
	}

	public Position toggleSelection(ILElement element) {
		final EditorElement editElem = mapper.findEditorElement(element);
		if (editElem == null) return null;
		return toggleSelection(editElem);
	}
	
	private Position toggleSelection(EditorElement editElem) {
		final ILElement element = editElem.getLightElement();
		if (element.isImplicit()) return null;
		final Point enclosingRange = mapper.getEnclosingRange(editElem);
		if (enclosingRange == null) return null;
		
		final int start = enclosingRange.x;
		final int length = enclosingRange.y - start + 1;
		final Position position = new Position(start, length);
		// TODO position is only useful if element is not selected
		selection.toggle(element, position);
		//styledText.setSelection(start);
		if (DEBUG)
			System.out.println("selected " + element.getElement() + " in "
					+ enclosingRange);
		return position;
	}

	public ILElement getSelectionAt(int offset) {
		return selection.getSelectionAt(offset);
	}
	
	public boolean isSelected(ILElement element) {
		return selection.contains(element); // FIXME or contains a parent ?
	}

	private int getModelCaretOffset() {
		return viewer.widgetOffset2ModelOffset(styledText
				.getCaretOffset());
	}
	
	private int getOffset(MouseEvent e) {
		return getOffset(new Point(e.x, e.y));
	}

	public int getOffset(Point p) {
		try {
			return styledText.getOffsetAtLocation(p);
		} catch (IllegalArgumentException exc) {
			// not over a character
			final int lineIndex = styledText.getLineIndex(p.y);
			final int offset;
			if (lineIndex < styledText.getLineCount() - 1) {
				offset = styledText.getOffsetAtLine(lineIndex + 1);
				return offset - 1;
			}
			return styledText.getOffsetAtLine(lineIndex);
		}
	}

	public void mouseDown(MouseEvent e) {
		if (DEBUG) System.out.println("mouse down " + e);
		
		final int offset = getOffset(e);
		if (offset < 0 ) return;
		if ((e.stateMask & SWT.MOD1) != 0) {
			toggleSelection(offset);
		} else if (selection.contains(offset)) {
			final boolean dragging = styledText.dragDetect(e);
			if (!dragging && ((e.button & SWT.BUTTON2) != 0)) {
				resetSelection(offset);
			}
		} else {
			resetSelection(offset);
		}
	}

	private void resetSelection(int offset) {
		selection.clear();
		styledText.setSelection(offset);
	}
	
	public void selectItems(ILElement[] selected) {
		selection.clear();
		for (ILElement e : selected) {
			final EditorElement editorElem = mapper.findEditorElement(e);
			if (editorElem == null)
				continue;
			final ILElement element = editorElem.getLightElement();
			if (element.isImplicit())
				return;
			final Point enclosingRange = mapper.getEnclosingRange(editorElem);
			if (enclosingRange == null)
				return;

			final int start = enclosingRange.x;
			final int length = enclosingRange.y - start + 1;
			final Position position = new Position(start, length);
			selection.add(element, position);
			// styledText.setSelection(start);
			if (DEBUG)
				System.out.println("selected " + element.getElement() + " in "
						+ enclosingRange);
		}
	}

	public void resetSelectionNoEffect(int offset) {
		selection.clearNoEffect();
		styledText.setSelection(offset);
	}

	public void mouseUp(MouseEvent e) {
		if (DEBUG)
			System.out.println("mouse up " + e);

		// no selection
		if (viewer.getSelectedRange().y == 0) {
			overlayEditor.showAtOffset(styledText.getCaretOffset());
		}
	}

	public void verifyText(VerifyEvent e) {
		int start = viewer.widgetOffset2ModelOffset(e.start);

		Interval editable = mapper.findEditableInterval(start);
		// if there is no editable interval in the region, cancel.
		// this includes cases where there is a deletion right before an
		// editable interval
		if (editable == null) {
			// System.out.println("no editable here");
			e.doit = false;
			return;
		}
		// do not delete after the editable has ended
		int end = viewer.widgetOffset2ModelOffset(e.end);
		if (editable.getLastIndex() < end
				&& e.text.length() == 0) {
			// System.out.println("can not delete after editable has ended");
			e.doit = false;
			return;
		}
	}

	public void verifyKey(VerifyEvent event) {
		if (event.character == SWT.ESC) {
			overlayEditor.abortEditing();
		}
		if (event.character == SWT.CR) {
			overlayEditor.showAtOffset(styledText.getCaretOffset());
		}
	}

	public void keyTraversed(TraverseEvent e) {
		if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
			goToNextEditRegion();
			e.doit = false;
		}
		if (e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
			goToPreviousEditRegion();
			e.doit = false;
		}
	}

	public void goToNextEditRegion() {
		int offset = getModelCaretOffset();
		Interval next = mapper.findEditableIntervalAfter(offset);
		int new_offset = viewer.modelOffset2WidgetOffset(next.getOffset());
		// TODO: check if folding regions need to be expanded.
		styledText.setSelection(new_offset);
	}

	public void goToPreviousEditRegion() {
		int offset = getModelCaretOffset();
		Interval next = mapper.findEditableIntervalBefore(offset);
		int new_offset = viewer.modelOffset2WidgetOffset(next.getOffset());
		// TODO: check if folding regions need to be expanded.
		styledText.setSelection(new_offset);
	}

}
