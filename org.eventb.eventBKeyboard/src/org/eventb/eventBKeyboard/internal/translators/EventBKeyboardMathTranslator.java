/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 *     Systerel - delegated to org.rodinp.keyboard
 ******************************************************************************/

package org.eventb.eventBKeyboard.internal.translators;

import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.IEventBKeyboardTranslator;
import org.rodinp.internal.keyboard.translators.RodinKeyboardMathTranslator;

/**
 * @author htson
 *         <p>
 *         The translator for mathematical tempSymbols
 */
public class EventBKeyboardMathTranslator implements IEventBKeyboardTranslator {

	private final RodinKeyboardMathTranslator translator = new RodinKeyboardMathTranslator();

	public void translate(Text widget) {

		translator.translate(widget);
	}

}
