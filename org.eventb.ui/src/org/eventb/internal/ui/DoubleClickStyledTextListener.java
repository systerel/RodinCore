/*******************************************************************************
* Copyright (c) 2008 Systerel and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Systerel - initial API and implementation
*******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

public class DoubleClickStyledTextListener extends AbstractDoubleClickListener {

	private final ITextWidget widget;

	public DoubleClickStyledTextListener(StyledText text) {
		widget = new StyledWidget(text);
	}

	@Override
	public ITextWidget getWidget() {
		return widget;
	}

	class StyledWidget implements ITextWidget {
		private final StyledText text;

		public StyledWidget(StyledText text) {
			this.text = text;
		}

		public Point getSelection() {
			return text.getSelection();
		}

		public String getText() {
			return text.getText();
		}

		public void setSelection(Point p) {
			text.setSelection(p);
		}
	}
}
