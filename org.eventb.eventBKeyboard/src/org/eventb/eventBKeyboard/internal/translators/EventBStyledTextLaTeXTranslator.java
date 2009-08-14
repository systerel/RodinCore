/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.eventBKeyboard.internal.translators;

import org.eclipse.swt.custom.StyledText;

public class EventBStyledTextLaTeXTranslator extends
		EventBStyledTextTextTranslator {

	@Override
	public void translate(StyledText widget) {
		if (symbols == null) {
			TextSymbols textSymbol = new LaTeXSymbols();
			symbols = textSymbol.getSymbols();
			maxSize = textSymbol.getMaxSize();
		}
		String text = widget.getText();
		translate(widget, 0, text.length());
	}
}
