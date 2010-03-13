/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp;

import java.util.concurrent.CancellationException;

import org.eventb.pp.IPPMonitor;

/**
 * Utility class for easily checking for cancellation in a call of PP. To use it
 * just create an instance with the factory method
 * {@link #newChecker(IPPMonitor)} and regularly call the {@link #check()}
 * method. In case of cancellation, this latter call will throw a
 * {@link CancellationException}.
 * 
 * @author Laurent Voisin
 */
public class CancellationChecker {

	public static CancellationChecker newChecker(IPPMonitor monitor) {
		if (monitor == null) {
			return new CancellationChecker(null) {
				@Override
				public void check() {
					// do nothing as no monitor is present.
				}
			};
		}
		return new CancellationChecker(monitor);
	}

	private final IPPMonitor monitor;

	protected CancellationChecker(IPPMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * Checks for cancellation. Throws a {@link CancellationException} in case
	 * of cancellation of this call has been detected.
	 * 
	 * @throws CancellationException
	 */
	public void check() {
		if (monitor.isCanceled()) {
			throw new CancellationException();
		}
	}

}
