/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.searchhypothesis;

import java.util.StringTokenizer;

/**
 * @author htson
 *         <p>
 *         This is an utility class for supporting the Searched Hypothesis View.
 */
public class SearchHypothesisUtils {

	// The Debug flag. This is set by the main activator class of this UI
	// plugin. Client should not try to reset this flag.
	public static boolean DEBUG = false;

	// The default prefix for debugging messages.
	public final static String DEBUG_PREFIX = "*** SearchHypothesis *** "; // $NON-NLS-1$

	/**
	 * Print the debug message with the prefix for cache hypothesis.
	 * 
	 * @param message
	 *            the debug message
	 */
	public static void debug(String message) {
		StringTokenizer tokenizer = new StringTokenizer(message, "\n");
		while (tokenizer.hasMoreTokens()) {
			System.out.println(DEBUG_PREFIX + tokenizer.nextToken());
		}
	}

}
