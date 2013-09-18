/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.core;

/**
 * Symbol translation utilities.
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class KeyboardUtils {

	private KeyboardUtils() {
		// no instance
	}
	
	/**
	 * Log the given message.
	 * 
	 * @param message
	 * 				the message to log
	 */
	public static void debug(String message) {
		System.out.println(message);
	}

	/**
	 * DO NOT USE THIS METHOD
	 * 
	 * Testing if a character is a text character.
	 * 
	 * This method is used by the UI part of the Rodin Keyboard implementation.
	 * Client shall not use this method.
	 * 
	 * @param c
	 *            a character
	 * @return <code>true</code> if the character is one of the text characters
	 *         (i.e. 'A' to 'Z', 'a' to 'z', etc.), <code>false</code> otherwise
	 */
	public static boolean isTextCharacter(char c) {
		if (c <= 'Z' && c >= 'A')
			return true;
		if (c <= 'z' && c >= 'a')
			return true;
		if (c <= '9' && c >= '0')
			return true;
		if (c == '_')
			return true;
		return false;
	}

}
