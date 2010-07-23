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

	private final ITextWrapper wrapper;

	public DoubleClickStyledTextListener(StyledText text) {
		wrapper = new StyledTextWrapper(text);
	}

	@Override
	public ITextWrapper getWraper() {
		return wrapper;
	}

	class StyledTextWrapper implements ITextWrapper {
		private final StyledText text;

		public StyledTextWrapper(StyledText text) {
			this.text = text;
		}

		@Override
		public Point getSelection() {
			return text.getSelection();
		}

		@Override
		public String getText() {
			return text.getText();
		}

		@Override
		public void setSelection(Point p) {
			text.setSelection(p);
		}
	}
}
