/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.keyboard.ui;

import org.rodinp.keyboard.core.KeyboardUtils;

public class KeyboardCoreUtils {
	
	public static boolean DEBUG = false;

	public static boolean TEXT_DEBUG = false;
	
	public static boolean MATH_DEBUG = false;
	
	public static void debug(String str) {
		KeyboardUtils.debug(str);
	}

	/**
	 * Testing if a character is a text character
	 * 
	 * @param c
	 *            a character
	 * @return true if the character is one of the text characters (i.e. 'A' to
	 *         'Z', 'a' to 'z', etc.) false otherwise
	 */
	public static boolean isTextCharacter(char c) {
		return KeyboardUtils.isTextCharacter(c);
	}
	
	/**
	 * 
	 * @param length
	 * @return
	 */
	public static String generateKey(int length) {
		return KeyboardUtils.generateKey(length);
	}
	
}
