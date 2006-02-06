/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.util.Util.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Provides convenient utility methods to other types in this package.
 */
public class Util {

	/**
	 * Combines two hash codes to make a new one.
	 */
	public static int combineHashCodes(int hashCode1, int hashCode2) {
		return hashCode1 * 17 + hashCode2;
	}

	public static void log(Throwable exc, String message) {
		Throwable nestedException;
		if (exc instanceof RodinDBException 
				&& (nestedException = ((RodinDBException)exc).getException()) != null) {
			exc = nestedException;
		}
		IStatus status= new Status(
			IStatus.ERROR, 
			RodinCore.PLUGIN_ID, 
			IStatus.ERROR, 
			message, 
			exc); 
		RodinCore.getPlugin().getLog().log(status);
	}
	
	/**
	 * Trims leading and trailing Unicode space chararacters from the given
	 * string.
	 * <p>
	 * Unicode space characters are detected using
	 * {@link Character#isSpaceChar(int)}.
	 * </p>
	 * 
	 * @param string
	 *            a string to trim
	 * @return the given string trimmed of Unicode space characters
	 */
	public static String trimSpaceChars(String string) {
		
		int length = string.length();
		if (length == 0) return string;
		
		int firstCP = string.codePointAt(0);
		int lastCP = string.codePointBefore(length);
		
		if (! Character.isWhitespace(firstCP) && ! Character.isWhitespace(lastCP)) {
			// Nothing to do.
			return string;
		}
		
		// TODO Implement using Unicode space characters
		// This is only a shortcut, cause I'm in a hurry
		return string.trim();
	}
}
