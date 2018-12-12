/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

/**
 * Allows to check that auto tactics seem to work (see bug #779).
 * 
 * @author Laurent Voisin
 */
public class AutoTacticChecker {

	public static boolean DEBUG = false;

	private static final void trace(String message) {
		if (DEBUG) {
			System.out.println(message);
		}
	}

}
