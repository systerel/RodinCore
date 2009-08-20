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
package org.eventb.internal.eventBKeyboard;

public class KeyboardUtils {

	public static boolean TEXT_DEBUG = false;
	
	public static boolean MATH_DEBUG = false;
	
	public static void debugText(String str) {
		if (TEXT_DEBUG) System.out.println(str);
	}
	
	public static void debugMath(String str) {
		if (MATH_DEBUG) System.out.println(str);
	}
	
}
