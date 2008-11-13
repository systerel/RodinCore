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

import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 *
 */
public class OverlayEditor implements KeyListener {
	private StyledText editorText;
	private DocumentMapper mapper;
	private StyledText parent;
	private ProjectionViewer viewer;
	private boolean ctrl = false;
	private boolean enter = false;
	private Interval interval;

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
	}

	
	public void setToOffset(int offset){
		if (!editorText.isVisible()) {
			interval = mapper.findEditableInterval(viewer.widgetOffset2ModelOffset(parent.getCaretOffset()));
			String text = "test";
			if (interval != null) {
				int start = viewer.modelOffset2WidgetOffset(interval.getOffset());
				text = parent.getText(start, start +interval.getLength());
				Point location = new Point (parent.getLocationAtOffset(start).x, parent.getLocationAtOffset(start).y);
				Point endPoint = parent.getLocationAtOffset(start +interval.getLength());
				Point size = new Point(endPoint.x - location.x +4, endPoint.y - location.y +parent.getLineHeight() +4);

				editorText.setSize(size.x +5 +editorText.getVerticalBar().getSize().x, size.y);
				editorText.setText(text);
				editorText.setFont(parent.getFont());
				editorText.setLocation(location);
				editorText.setVisible(true);
				editorText.setFocus();
			}
			
		}
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if (e.character == SWT.ESC) {
			ctrl = false;
			enter = false;
			editorText.setVisible(false);
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
	}
	
	/**
	 * Removes whitespaces at beginning and end of a text.
	 * @return
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
		return (text.substring(first_pos, last_pos));
	}
	
	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
	
	
	
}
