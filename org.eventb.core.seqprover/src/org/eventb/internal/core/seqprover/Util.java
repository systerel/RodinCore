/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.seqprover;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.seqprover.SequentProver;

/**
 * Class grouping various utility functions, which are intended for internal use
 * only (never published).
 * 
 * @author Laurent Voisin
 */
public abstract class Util {

	/**
	 * Log an exception to the Eclipse platform.
	 * 
	 * @param exc
	 *            exception to log
	 * @param message
	 *            message giving the context where the exception occurred.
	 */
	public static void log(Throwable exc, String message) {
		IStatus status= new Status(
			IStatus.ERROR, 
			SequentProver.PLUGIN_ID, 
			IStatus.ERROR, 
			message, 
			exc); 
		SequentProver.getDefault().getLog().log(status);
	}

}
