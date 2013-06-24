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
package org.rodinp.internal.keyboard.translators;

import org.eclipse.swt.custom.StyledText;

class StyledTextWrapper extends AbstractWidgetWrapper {

	private final StyledText wrappedWidget;

	public StyledTextWrapper(StyledText wrappedWidget) {
		this.wrappedWidget = wrappedWidget;
	}
	
	@Override
	public String getText() {
		return wrappedWidget.getText();
	}

	@Override
	public int getCaretOffset() {
		return wrappedWidget.getCaretOffset();
	}

	@Override
	public void insert(String toInsert) {
		wrappedWidget.insert(toInsert);
	}

	@Override
	public void setSelection(int start) {
		wrappedWidget.setSelection(start);
		
	}

	@Override
	public void setSelection(int start, int end) {
		wrappedWidget.setSelection(start, end);
	}
	
}