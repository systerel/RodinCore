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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

public class DoubleClickTextListener extends AbstractDoubleClickListener {

	private final ITextWrapper wrapper;

	public DoubleClickTextListener(Text text) {
		wrapper = new TextWrapper(text);
	}

	@Override
	public ITextWrapper getWraper() {
		return wrapper;
	}

	class TextWrapper implements ITextWrapper {
		private final Text text;

		public TextWrapper(Text text) {
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
