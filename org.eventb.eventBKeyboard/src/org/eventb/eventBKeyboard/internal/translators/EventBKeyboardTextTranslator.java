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
 *     Systerel - delegated to org.rodinp.keyboard
 ******************************************************************************/

package org.eventb.eventBKeyboard.internal.translators;

import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.IEventBKeyboardTranslator;
import org.rodinp.internal.keyboard.translators.RodinKeyboardTextTranslator;

/**
 * @author htson
 *         <p>
 *         The translator for text symbols
 */
public class EventBKeyboardTextTranslator implements IEventBKeyboardTranslator {

	private final RodinKeyboardTextTranslator translator = new RodinKeyboardTextTranslator();

	/**
	 * Translate the content of the text widget. Because of the "space", it
	 * needs be translated twice.
	 * <p>
	 * 
	 * @see org.eventb.eventBKeyboard.IEventBKeyboardTranslator#translate(org.eclipse.swt.widgets.Text)
	 */
	public void translate(Text widget) {
		translator.translate(widget);
	}

}
