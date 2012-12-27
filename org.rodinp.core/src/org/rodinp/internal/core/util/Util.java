/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.util.Util
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

	/**
	 * Logs an error message to the Rodin core plug-in log.
	 * 
	 * @param exc
	 *            a low-level exception, or <code>null</code> if not
	 *            applicable
	 * @param message
	 *            a message describing the error
	 */
	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			Throwable nestedException = ((RodinDBException)exc).getException();
			if (nestedException != null) { 
				exc = nestedException;
			}
		}
		IStatus status= new Status(
			IStatus.ERROR, 
			RodinCore.PLUGIN_ID, 
			IStatus.ERROR, 
			message, 
			exc); 
		RodinCore.getPlugin().getLog().log(status);
	}

	private static boolean isSpaceChar(int codePoint) {
		return Character.isWhitespace(codePoint)
				|| Character.isSpaceChar(codePoint) || codePoint == '\u00A0'
				|| codePoint == '\u2007' || codePoint == '\u202F';
	}

	/**
	 * Trims leading and trailing Unicode and Java space characters from the
	 * given string.
	 * <p>
	 * Space characters are detected using both
	 * {@link Character#isSpaceChar(int)} and
	 * {@link Character#isWhitespace(int)}.
	 * </p>
	 * 
	 * @param string
	 *            a string to trim
	 * @return the given string trimmed of space characters
	 */
	public static String trimSpaceChars(String string) {
		final int len = string.length();
		int first = 0;
		int last = len;

		while ((first < len) && isSpaceChar(string.codePointAt(first))) {
			first = string.offsetByCodePoints(first, 1);
		}
		while ((first < last) && isSpaceChar(string.codePointBefore(last))) {
			last = string.offsetByCodePoints(last, -1);
		}
		if (first == 0 && last == len) {
			return string;
		}
		return string.substring(first, last);
	}

}
