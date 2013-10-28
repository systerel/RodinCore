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
package org.eventb.internal.ui.prover;

import static org.eclipse.swt.SWT.CURSOR_ARROW;
import static org.eclipse.swt.SWT.CURSOR_HAND;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * An adapter to set appropriate cursors depending on the control being hovered.
 */
public class CursorModifier extends MouseTrackAdapter {

	public final static Cursor HAND_CURSOR = new Cursor(Display.getDefault(),
			CURSOR_HAND);
	public final static Cursor ARROW_CURSOR = new Cursor(Display.getDefault(),
			CURSOR_ARROW);

	private final Control parent;
	private final Cursor defaultCursor;

	public CursorModifier(Control parent) {
		this.parent = parent;
		this.defaultCursor = parent.getCursor();
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		parent.setCursor(ARROW_CURSOR);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		parent.setCursor(defaultCursor);
	}

}