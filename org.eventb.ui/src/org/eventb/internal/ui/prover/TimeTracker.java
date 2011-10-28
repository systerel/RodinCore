/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

/**
 * Utility class for tracking time in the prover UI.
 * 
 * @author Laurent Voisin
 */
public class TimeTracker {

	/**
	 * Return a new time tracker instance which will track time for the given
	 * view.
	 * 
	 * @param viewName
	 *            name of the view
	 * @param enabled
	 *            whether the tracker is enabled
	 * @return a new time tracker
	 */
	public static TimeTracker newTracker(String viewName, boolean enabled) {
		if (enabled) {
			return new TimeTracker(viewName);
		} else {
			return new NullTimeTracker();
		}
	}

	/**
	 * Time tracker that does nothing, as it is disabled.
	 */
	public static class NullTimeTracker extends TimeTracker {

		public NullTimeTracker() {
			super(null);
		}

		@Override
		public void start() {
			// Do nothing
		}

		@Override
		public void endSubtask(String msg) {
			// Do nothing
		}

		@Override
		public void endTask(String msg) {
			// Do nothing
		}

	}

	private final String prefix;
	private long start;
	private long lap;
	private String sep;
	private StringBuilder details;

	public TimeTracker(String viewName) {
		this.prefix = "* View " + viewName + ": ";
	}

	/**
	 * Start tracking time.
	 */
	public void start() {
		start = lap = System.nanoTime();
		sep = "";
		details = new StringBuilder();
	}

	/**
	 * Memorize the time elapsed since the last call to {#link {@link #start()}
	 * or this method, whichever occurred most recently.
	 * 
	 * @param msg
	 *            information message to display (currently unused)
	 */
	public void endSubtask(String msg) {
		details.append(sep);
		sep = " + ";
		details.append(elapsed(lap));
		lap = System.nanoTime();
	}

	/**
	 * Print the given message with the time elapsed since the last call to
	 * {#link {@link #start()}, together with all times memorized by
	 * {@link #endSubtask(String)}.
	 * 
	 * @param msg
	 *            message to display
	 */
	public void endTask(String msg) {
		System.out.println(prefix + msg + ": " + elapsed(start) + " ms ("
				+ details + ")");
	}

	/**
	 * Print the given message without timing information.
	 * 
	 * @param msg
	 *            message to display
	 */
	public void trace(String msg) {
		System.out.println(prefix + msg);
	}

	private long elapsed(final long nanoTime) {
		return (System.nanoTime() - nanoTime + 500000) / 1000000;
	}

}
