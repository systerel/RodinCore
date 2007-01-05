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
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

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

	@SuppressWarnings("unchecked")
	private static final IInternalParent internGetSimilarElement(
			IInternalParent element, IRodinFile newFile) {
		if (element instanceof RodinFile) {
			return newFile;
		}
		final IInternalParent parent = (IInternalParent) element.getParent();
		final IInternalParent newParent = 
			internGetSimilarElement(parent, newFile);
		final IInternalElementType type = 
			(IInternalElementType) element.getElementType();
		final String name = element.getElementName();
		return newParent.getInternalElement(type, name);
	}

	/**
	 * Returns a handle to the an internal element which has the same relative
	 * path as the given one, but relative to the given file.
	 * 
	 * @param element
	 *            source element
	 * @param newFile
	 *            file in which to construct the new handle
	 * @return the element in the given file and with the same relative path
	 *         inside its file as the given element
	 */
	public static final IInternalElement getSimilarElement(
			IInternalElement element, IRodinFile newFile) {
		
		return (IInternalElement) internGetSimilarElement(element, newFile);
	}
}
