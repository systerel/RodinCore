/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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
package org.rodinp.internal.keyboard;

public class KeyboardUtils {
	
	public static boolean DEBUG = false;

	public static boolean TEXT_DEBUG = false;
	
	public static boolean MATH_DEBUG = false;
	
	public static void debug(String str) {
		System.out.println(str);
	}
	
	public static void debugText(String str) {
		System.out.println(str);
	}
	
	public static void debugMath(String str) {
		System.out.println(str);
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
