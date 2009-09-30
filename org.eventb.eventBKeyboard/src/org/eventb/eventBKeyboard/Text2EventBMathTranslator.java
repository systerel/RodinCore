/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - delegated to org.rodinp.keyboard
 *******************************************************************************/
package org.eventb.eventBKeyboard;

import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         This class provides a translation of a string into Event-B
 *         mathematical language.
 *         </p>
 * @deprecated use {@link RodinKeyboardPlugin#translate(String)}
 * @since 2.9
 */
@Deprecated
public class Text2EventBMathTranslator {

	/**
	 * Translate the input string into Event-B Mathematical Language.
	 * <p>
	 * 
	 * @param str
	 *            input string
	 * @return a string corresponds to the input in Event-B Mathematical
	 *         Language
	 * @deprecated use {@link RodinKeyboardPlugin#translate(String)}.
	 */
	@Deprecated
	public static String translate(String str) {
		return RodinKeyboardPlugin.getDefault().translate(str);
	}

}
