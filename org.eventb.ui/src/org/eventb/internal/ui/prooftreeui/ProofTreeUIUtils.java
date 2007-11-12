/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prooftreeui;

import org.eventb.internal.ui.UIUtils;

/**
 * @author htson
 *         <p>
 *         Utility class supporting the Proof Tree UI, including debug options.
 */
public class ProofTreeUIUtils {

	// The debug flag which will be set by the main plug-in class on loading.
	public static boolean DEBUG = false;

	// The default prefix for debug messages.
	public final static String DEBUG_PREFIX = "*** ProofTreeUI *** "; //$NON-NLS-1$

	/**
	 * Print the debug message by with the default prefix.
	 * 
	 * @param message
	 *            the debug message
	 */
	public static void debug(String message) {
		UIUtils.printDebugMessage(DEBUG_PREFIX, message);
	}

}
