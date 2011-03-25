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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import fr.systerel.editor.documentModel.EditorItem;

public class ElementButton implements PaintListener {

	private Button button;
	private EditorItem editorElement;
	private StyledText parent;
	private ProjectionViewer viewer;

	public Button getButton() {
		return button;
	}

	public ElementButton(StyledText parent, EditorItem editorElement,
			ProjectionViewer viewer) {
		this.editorElement = editorElement;
		this.parent = parent;
		this.viewer = viewer;

		createButton();
		reposition();
		parent.addPaintListener(this);
		editorElement.setButton(this);
	}

	private void createButton() {
		button = new Button(parent, SWT.CHECK);
		final int lineHeight = parent.getLineHeight();
		button.setSize(lineHeight, lineHeight);
		Image image = getDefaultImage();
		button.setImage(image);
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				selectElement();

			}

		});
	}

	public EditorItem getEditorElement() {
		return editorElement;
	}

	public void selectElement() {
		int modeloffset = editorElement.getOffset();
		int modellength = editorElement.getLength();
		viewer.setSelectedRange(modeloffset, modellength);
	}

	/**
	 * Positions the button according to the position of its editorElement.
	 */
	public void reposition() {
		int offset = viewer.modelOffset2WidgetOffset(editorElement.getOffset());
		if (offset > -1) {
			int line = parent.getLineAtOffset(offset);
			Point location = parent.getLocationAtOffset(parent
					.getOffsetAtLine(line));
			button.setLocation(location);
			button.setVisible(true);
		} else {
			button.setVisible(false);
		}

	}

	/**
	 * Returns the default title image.
	 * 
	 * @return the default image
	 */
	protected Image getDefaultImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_NEW_WIZARD);
	}

	public void paintControl(PaintEvent e) {
		reposition();

	}

	public void dispose() {
		button.dispose();
		parent.removePaintListener(this);

	}

}
