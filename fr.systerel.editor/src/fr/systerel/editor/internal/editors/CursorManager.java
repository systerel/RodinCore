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
package fr.systerel.editor.internal.editors;

import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;

import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.presentation.RodinConfiguration;

/**
 * Class able to manage the cursors of the editor according to its placement
 * over intervals. The blank space on the left of the elements in the editor
 * provides an arrow original for element selection.
 */
public class CursorManager implements MouseMoveListener {

	private final RodinEditor editor;
	private final ProjectionViewer viewer;
	private final Cursor original;
	private Cursor aCursor;

	public CursorManager(RodinEditor editor, ProjectionViewer viewer) {
		this.editor = editor;
		this.viewer = viewer;
		this.original = viewer.getControl().getCursor();
		initializeArrowCursor();
	}

	private void initializeArrowCursor() {
		this.aCursor = new Cursor(viewer.getControl().getDisplay(),
				SWT.CURSOR_HAND);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		final Point p = new Point(e.x, e.y);
		final StyledText styledText = viewer.getTextWidget();
		try {
			final int offset = styledText.getOffsetAtLocation(p);
			final Interval inter = editor.getDocumentMapper().findInterval(
					offset);
			if (inter == null) {
				return;
			}
			if (inter.getContentType() == RodinConfiguration.LEFT_PRESENTATION_TYPE
					&& styledText.getCursor() != original) {
				styledText.setCursor(aCursor);
				return;
			}
		} catch (IllegalArgumentException ex) {
			// Capture of getOffsetAtLocation() exceptions
			// Nothing to do
		}
		if (styledText.getCursor() == aCursor) {
			styledText.setCursor(original);
		}
	}

}
