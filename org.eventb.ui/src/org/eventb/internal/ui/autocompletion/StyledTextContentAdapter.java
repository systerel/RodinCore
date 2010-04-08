/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.autocompletion;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * An {@link IControlContentAdapter} for StyledText controls. Based on
 * {@link TextContentAdapter}
 */
public class StyledTextContentAdapter implements IControlContentAdapter {
	public String getControlContents(Control control) {
		return ((StyledText) control).getText();
	}

	public int getCursorPosition(Control control) {
		return ((StyledText) control).getCaretOffset();
	}

	public Rectangle getInsertionBounds(Control control) {
		StyledText text = (StyledText) control;
		Point caretOrigin = text.getCaret().getLocation();

		return new Rectangle(caretOrigin.x + text.getClientArea().x,
				caretOrigin.y + text.getClientArea().y, 1, text.getLineHeight());
	}

	public void insertControlContents(Control control, String contents,
			int cursorPosition) {
		final StyledText text = (StyledText) control;
		final int offset = text.getCaretOffset();
		text.insert(contents);
		text.setCaretOffset(offset + contents.length());
	}

	/*
	 * cursorPosition is ignored. The cursor is placed at the end of inserted
	 * text
	 */
	public void setControlContents(Control control, String contents,
			int cursorPosition) {
		final StyledText text = ((StyledText) control);
		text.setText(contents);
		text.setCaretOffset(cursorPosition);
	}

	public void setCursorPosition(Control control, int index) {
		((StyledText) control).setCaretOffset(index);
	}

}
