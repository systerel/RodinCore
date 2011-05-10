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
package fr.systerel.editor.editors;

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

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.EditorElement;
import fr.systerel.editor.documentModel.Interval;

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
	
	private static class ElementSelection {
		public final ILElement element;
		public final Point range;
		
		public ElementSelection(ILElement element, Point range) {
			this.element = element;
			this.range = range;
		}
		
		public boolean contains(int offset) {
			return range.x <= offset && offset < range.y;
		}
	}
	
	private StyledText styledText;
	private DocumentMapper mapper;
	private ProjectionViewer viewer;
	private OverlayEditor overlayEditor;
	private ElementSelection selection;

	public SelectionController(StyledText styledText, DocumentMapper mapper,
			ProjectionViewer viewer, OverlayEditor overlayEditor) {
		super();
		this.styledText = styledText;
		this.viewer = viewer;
		this.mapper = mapper;
		this.overlayEditor = overlayEditor;
		this.selection = null;
	}

	/**
	 * @return the selectedElement
	 */
	public ILElement getSelectedElement() {
		if (selection == null) return null;
		return selection.element;
	}
	
	/**
	 * Checks if a selection is valid.
	 * 
	 * @param offset
	 *            The offset to check, in model coordinates
	 * @param length
	 * @return <code>true</code> if the selection is valid, <code>false</code>
	 *         otherwise.
	 */
	public boolean isValidSelection(int offset, int length) {
		int modelOffset = viewer.widgetOffset2ModelOffset(offset);
		Interval[] intervals = mapper.findIntervals(modelOffset, length);
		if (intervals.length == 1) {
			return true;
		}
		final EditorElement element = mapper
				.findEditorElement(modelOffset, length);
		return element != null;
	}

	/**
	 * Resets the selection
	 */
	public void correctSelection() {
		final int x = styledText.getSelection().x;
		final int y = styledText.getSelection().y;
		boolean forward = y == styledText.getCaretOffset();
		if (forward) {
			styledText.setSelection(x, x);

		} else {
			styledText.setSelection(y, y);
		}

	}

	/**
	 * Decides whether a given position should be editable or not.
	 * 
	 * @param offset
	 *            The position to check, in model coordinates.
	 * @return <code>true</code>, if the region is editable, <code>false</code>
	 *         otherwise.
	 */
	public boolean isEditableRegion(int offset) {
		boolean editable = mapper.findEditableInterval(offset) != null;
		return editable;
	}

	public void mouseDoubleClick(MouseEvent e) {
		if (DEBUG)
			System.out.println("double click " + e);
		
		// select the enclosing element
		final ILElement element = getEnclosingElement(e);
		if (element == null) return;
		final Point enclosingRange = mapper.getEnclosingRange(element);
		if (enclosingRange == null) return;
		styledText.setSelection(enclosingRange);
		selection = new ElementSelection(element, enclosingRange);
		if (DEBUG)
			System.out.println("selected " + element.getElement() + " in "
					+ enclosingRange);
	}

	private ILElement getEnclosingElement(MouseEvent e) {
		final int offset = getOffset(e);
		if (offset < 0) return null;
		final EditorElement item = mapper.findItemContaining(offset);
		if (item == null) return null;
		final ILElement element = item.getLightElement();
		return element;
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
			final int offset = styledText.getOffsetAtLine(lineIndex);
			return offset;
		}
	}

	public void mouseDown(MouseEvent e) {
		// detect drag
		if (DEBUG) System.out.println("mouse down " + e);
		
		if (selection == null) return;

		final int offset = getOffset(e); 
		if (selection.contains(offset)) {
			// restore selection
			styledText.setSelection(selection.range);
			// fire drag event
			final boolean dragging = styledText.dragDetect(e);
			if (!dragging) {
				selection = null;
				styledText.setSelection(offset);
			}
			if (DEBUG) {
				if (dragging)
					System.out.println("dragging");
				else
					System.out.println("not dragging");
			}
				
		}
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
