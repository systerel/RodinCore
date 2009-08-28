/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - handling of LaTeX symbols
 ******************************************************************************/

package org.eventb.eventBKeyboard.internal.translators;

import org.eclipse.swt.custom.StyledText;
import org.eventb.eventBKeyboard.IEventBStyledTextTranslator;
import org.rodinp.internal.keyboard.translators.RodinKeyboardTextTranslator;

public class EventBStyledTextTextTranslator implements
		IEventBStyledTextTranslator {

	private final RodinKeyboardTextTranslator translator = new RodinKeyboardTextTranslator();

	public void translate(StyledText widget) {
		translator.translate(widget);
	}

}
