/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

/**
 * Controls the selection in the RodinEditor and decides when it should be
 * editable. It is not possible to have just parts of the text editable. So the
 * whole styled text widget is set to editable or not according to the current
 * carret position.
 */
public class SelectionController implements SelectionListener, KeyListener, MouseListener, VerifyListener {

	private StyledText styledText;
	private DocumentMapper mapper;

	public SelectionController(StyledText styledText, DocumentMapper mapper) {
		super();
		this.styledText = styledText;
		this.mapper = mapper;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Controls the selection in widget
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		
		int offset = styledText.getSelection().x;
		int end = styledText.getSelection().y;
		if (!isValidSelection(offset, end - offset)) {
			correctSelection();
		}
		styledText.setEditable(isEditableRegion(offset));
		
	}
	
	public boolean isValidSelection(int offset, int length) {
		Interval interval = mapper.findEditableInterval(offset);
		//only editable intervals can be selected
		if (interval != null) {
			//selection stays inside the borders of the interval
			if (offset >= interval.getOffset() &&
					(offset +length <= interval.getOffset() +interval.getLength())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Resets the selection
	 */
	public void correctSelection() {
		boolean forward = styledText.getSelection().y == styledText.getCaretOffset();
		if (forward) {
			styledText.setSelection(styledText.getSelection().x,styledText.getSelection().x );
			
		} else {
			styledText.setSelection(styledText.getSelection().y,styledText.getSelection().y );
		}
		
	}
	
	/**
	 * Decides whether a given position should be editable or not.
	 * 
	 * @param offset
	 *            The position to check
	 * @param intervals
	 *            The intervals at that offset.
	 * @return <code>true</code>, if the region is editable,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEditableRegion(int offset) {
		boolean editable = mapper.findEditableInterval(offset) != null;
		return editable;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//do nothing
	}

	
	@Override
	public void keyReleased(KeyEvent e) {
		//setEditable according to the region the caret is in.
		if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN ||
				e.keyCode == SWT.ARROW_RIGHT || e.keyCode == SWT.ARROW_LEFT) {
			Interval interval = mapper.findEditableInterval((styledText.getCaretOffset()));
			boolean editable = interval != null;
			styledText.setEditable(editable) ;
		}
		
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// do nothing
		
	}

	@Override
	public void mouseDown(MouseEvent e) {
		// do nothing
		
	}

	@Override
	public void mouseUp(MouseEvent e) {
		
		int offset = styledText.getSelection().x;
		int end = styledText.getSelection().y;
		//no selection, otherwise the widgedSelected function takes care of it
		//setEditable according to the region the caret is in.
		if (offset == end ) {
			Interval interval = mapper.findEditableInterval((styledText.getCaretOffset()));
			boolean editable = interval != null;
			styledText.setEditable(editable) ;
		}
		
	}

	@Override
	public void verifyText(VerifyEvent e) {
		Interval editable = mapper.findEditableInterval(e.start);
		//if there is no editable interval in the region, cancel.
		//this includes cases where there is a deletion right before an editable interval
		if (editable == null)  {
//			System.out.println("no editable here");
			e.doit = false;
			return;
		}
		//do not delete after the editable has ended
		if (editable.getOffset() +editable.getLength() < e.end && e.text.length() == 0) {
//			System.out.println("can not delete after editable has ended");
			e.doit = false;
			return;
		}
	}
	
}
