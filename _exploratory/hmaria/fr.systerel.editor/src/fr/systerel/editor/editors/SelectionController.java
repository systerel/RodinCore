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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.EditorItem;
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
public class SelectionController implements SelectionListener, MouseListener,
		VerifyListener, VerifyKeyListener, TraverseListener {

	private StyledText styledText;
	private DocumentMapper mapper;
	private ProjectionViewer viewer;
	private OverlayEditor overlayEditor;

	public SelectionController(StyledText styledText, DocumentMapper mapper,
			ProjectionViewer viewer, OverlayEditor overlayEditor) {
		super();
		this.styledText = styledText;
		this.viewer = viewer;
		this.mapper = mapper;
		this.overlayEditor = overlayEditor;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Controls the selection in widget
	 */
	public void widgetSelected(SelectionEvent e) {

		int offset = widget2ModelOffset(styledText.getSelection().x);
		int end = widget2ModelOffset(styledText.getSelection().y);
		if (!isValidSelection(offset, end - offset)) {
			correctSelection();
		}
		styledText.setEditable(isEditableRegion(offset));
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
		// Interval[] intervals = mapper.findIntervals(modelOffset, length);
		// if (intervals.length ==1 ) {
		// return true;
		// }

		EditorItem element = mapper.findEditorElement(modelOffset, length);
		return element != null;
	}

	/**
	 * Resets the selection
	 */
	public void correctSelection() {
		boolean forward = styledText.getSelection().y == styledText
				.getCaretOffset();
		if (forward) {
			styledText.setSelection(styledText.getSelection().x,
					styledText.getSelection().x);

		} else {
			styledText.setSelection(styledText.getSelection().y,
					styledText.getSelection().y);
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
		// do nothing

	}

	public void mouseDown(MouseEvent e) {
		// do nothing

	}

	public void mouseUp(MouseEvent e) {
		// no selection
		if (viewer.getSelectedRange().y == 0) {
			overlayEditor.showAtOffset(styledText.getCaretOffset());
		}
	}

	public void verifyText(VerifyEvent e) {
		int start = widget2ModelOffset(e.start);

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
		int end = widget2ModelOffset(e.end);
		if (editable.getOffset() + editable.getLength() < end
				&& e.text.length() == 0) {
			// System.out.println("can not delete after editable has ended");
			e.doit = false;
			return;
		}
	}

	protected int widget2ModelOffset(int widgetOffset) {
		return viewer.widgetOffset2ModelOffset(widgetOffset);
	}

	protected int model2widgetOffset(int modelOffset) {
		return viewer.modelOffset2WidgetOffset(modelOffset);
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
		int offset = widget2ModelOffset(styledText.getCaretOffset());
		Interval next = mapper.findEditableIntervalAfter(offset);
		int new_offset = model2widgetOffset(next.getOffset());
		// TODO: check if folding regions need to be expanded.
		styledText.setCaretOffset(new_offset);
		styledText.showSelection();
	}

	public void goToPreviousEditRegion() {
		int offset = widget2ModelOffset(styledText.getCaretOffset());
		Interval next = mapper.findEditableIntervalBefore(offset);
		int new_offset = model2widgetOffset(next.getOffset());
		// TODO: check if folding regions need to be expanded.
		styledText.setCaretOffset(new_offset);
		styledText.showSelection();
	}

}
