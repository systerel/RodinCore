/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.goal;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

/**
 * @author htson
 *         <p>
 */
public class GoalUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** Goal *** ";

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

}
