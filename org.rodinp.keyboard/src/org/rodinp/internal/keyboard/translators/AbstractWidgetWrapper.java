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
import org.eclipse.swt.widgets.Text;

/**
 * Abstract class to wrap and use indifferently the Text and StyledText widgets
 * which does not have super class in common. This class provides some methods
 * to external users that are mapped to widget methods.
 */
public abstract class AbstractWidgetWrapper {
	
	public abstract String getText();
	
	public abstract int getCaretOffset();
	
	public abstract void insert(String toInsert);
	
	public abstract void setSelection(int start);
	
	public abstract void setSelection(int start, int end);

	public static TextWrapper getTextWidgetWrapper(Text widget) {
		return new TextWrapper(widget);
	}

	public static StyledTextWrapper getTextWidgetWrapper(StyledText widget) {
		return new StyledTextWrapper(widget);
	}
	
}