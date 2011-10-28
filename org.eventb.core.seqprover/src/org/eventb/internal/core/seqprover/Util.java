/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - null proof monitor
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.seqprover.IProofMonitor;
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

	/**
	 * Returns a place-holder proof monitor which is never cancelled. This
	 * monitor comes in handy as a replacement for a null pointer.
	 * 
	 * @return a proof monitor
	 */
	public static IProofMonitor getNullProofMonitor() {
		return NULL_PROOF_MONITOR;
	}

	private static final IProofMonitor NULL_PROOF_MONITOR = new NullProofMonitor();

	private static class NullProofMonitor implements IProofMonitor {

		@Override
		public boolean isCanceled() {
			return false;
		}

		@Override
		public void setCanceled(boolean value) {
			// Ignore
		}

		@Override
		public void setTask(String name) {
			// Ignore
		}
	}

}
