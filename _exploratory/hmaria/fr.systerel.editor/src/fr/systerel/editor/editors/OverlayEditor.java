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

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 *
 */
public class OverlayEditor implements KeyListener, VerifyListener, IAnnotationModelListener, IAnnotationModelListenerExtension{
	private StyledText editorText;
	private DocumentMapper mapper;
	private StyledText parent;
	private ProjectionViewer viewer;
	private boolean ctrl = false;
	private boolean enter = false;
	private Interval interval;
	private static final int DEFAULT_WIDTH = 300;

	public OverlayEditor(StyledText parent, DocumentMapper mapper, ProjectionViewer viewer) {
		this.viewer = viewer;
		this.mapper = mapper;
		this.parent = parent;
		
		createEditorText();
	}


	private void createEditorText() {
		editorText = new StyledText(parent, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		editorText.setFont(parent.getFont());
		Point oldsize = parent.getSize();
		parent.pack();
		parent.setSize(oldsize);
		editorText.setVisible(false);
		editorText.addKeyListener(this);
		editorText.addVerifyListener(this);
	}

	
	public void showAtOffset(int offset){
		if (!editorText.isVisible()) {
			interval = mapper.findEditableInterval(viewer.widgetOffset2ModelOffset(offset));
			String text = "test";
			if (interval != null) {
				int start = viewer.modelOffset2WidgetOffset(interval.getOffset());
				text = parent.getText(start, start +interval.getLength()-1);
				Point location = new Point (parent.getLocationAtOffset(start).x, parent.getLocationAtOffset(start).y -2);
				Point endPoint = new Point(findMaxWidth(start, start +interval.getLength()), parent.getLocationAtOffset(start +interval.getLength()).y);
				Point size = new Point(endPoint.x - location.x, endPoint.y - (location.y  +2) +parent.getLineHeight());
				
				int width = Math.max(size.x +5 +editorText.getVerticalBar().getSize().x, DEFAULT_WIDTH);
				int height = size.y +4;
				editorText.setSize(width, height);
				editorText.setText(text);
				editorText.setFont(parent.getFont());
				editorText.setLocation(location);
				editorText.setVisible(true);
				editorText.setFocus();
			}
			
		}
	}
	
	public void setToLocation(int y) {
		editorText.setLocation(editorText.getLocation().x, y-2);
	}

	public void abortEditing() {
		ctrl = false;
		enter = false;
		editorText.setVisible(false);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.character == SWT.ESC) {
			abortEditing();
		}
		if (e.character == '\r'){
			enter = true;
		}
	
		if (e.character == '\0'){
			ctrl= true;
		}
		if (enter && ctrl) {
			ctrl = false;
			enter = false;
			//TODO: add the changes to the database;
			System.out.println("changes in " +interval.getElement());
			addChangeToDatabase();
			editorText.setVisible(false);
		}
		
		
	}


	@Override
	public void keyReleased(KeyEvent e) {

		if (e.character == '\r'){
			enter = false;
		}
	
		if (e.character == '\0'){
			ctrl= false;
		}
		
	}
	
	public void addChangeToDatabase() {
		IRodinElement element = interval.getElement();
		String contentType= interval.getContentType();
		String text = removeWhiteSpaces(editorText.getText());
		if (element instanceof IIdentifierElement && contentType.equals(RodinConfiguration.IDENTIFIER_TYPE)) {
			try {
				((IIdentifierElement) element).setIdentifierString(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (element instanceof ILabeledElement && contentType.equals(RodinConfiguration.IDENTIFIER_TYPE)) {
			try {
				((ILabeledElement) element).setLabel(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (element instanceof IPredicateElement && contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
			try {
				((IPredicateElement) element).setPredicateString(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (element instanceof IAssignmentElement && contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
			try {
				((IAssignmentElement) element).setAssignmentString(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (element instanceof ICommentedElement && contentType.equals(RodinConfiguration.COMMENT_TYPE)) {
			try {
				((ICommentedElement) element).setComment(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Removes whitespaces at beginning and end of a text.
	 * 
	 * @return the text with the whitespaces removed
	 */
	public String removeWhiteSpaces(String text) {
		
		int first_pos = -1;
		int last_pos = -1;
		int i = 0;
		for (char ch : text.toCharArray()) {
			if (first_pos == -1 && !isWhitespace(ch)) {
				first_pos = i;
			}
			if (last_pos == -1 && isWhitespace(ch)){
				last_pos = i;
			}
			if (last_pos != -1 && !isWhitespace(ch)){
				last_pos = -1;
			}
			i++;
		}
		first_pos = Math.max(first_pos, 0);
		last_pos = (last_pos == -1 ) ? (text.length()) : last_pos;
		return (text.substring(first_pos, last_pos));
	}
	
	protected boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}

	
	/**
	 * finds the maximum with of the the text in the parent widget within a
	 * given range.
	 * 
	 * @param start
	 * @param end
	 * @return the maximum with in pixels.
	 */
	protected int findMaxWidth(int start, int end){
		int firstLine = parent.getLineAtOffset(start);
		int lastLine = parent.getLineAtOffset(end);
		int max = 0;
		for (int i = firstLine; i <= lastLine; i++) {
			max = Math.max(parent.getLocationAtOffset(parent.getOffsetAtLine(i+1)-1).x, max);
		}
		
		return max;
	}


	@Override
	public void verifyText(VerifyEvent e) {
		if (e.text != null && e.text.length() > 0) {
			if (e.text.charAt(0) == ('\r') ){
				if (ctrl) {
					e.doit = false;
				}
			}
		}
		
	}


	@Override
	public void modelChanged(IAnnotationModel model) {
		// do nothing
	}


	@Override
	public void modelChanged(AnnotationModelEvent event) {
		// react to folding of the editor
		if (event.getChangedAnnotations().length >0 && editorText.isVisible()) {
			//adjust the location of the editor
			if (viewer.modelOffset2WidgetOffset(interval.getOffset()) > 0) {
				setToLocation(parent.getLocationAtOffset(viewer.modelOffset2WidgetOffset(interval.getOffset())).y);
			} else {
				// if the interval that is currently being edited, is hidden from view
				// abort the editing
				abortEditing();
			}
		}
	}
	
}