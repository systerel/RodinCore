/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.adapters.dboperations;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Gateway for updating the EMF model.
 * <p>
 * The EMF model is not intrinsically thread-safe, therefore we need to make
 * sure that all updates are performed sequentially. To update the EMF models,
 * clients must create an {@link ElementOperation} and pass it to
 * {@link #submit(ElementOperation)}.
 * </p>
 * <p>
 * We use an executor service with a simple thread to ensure sequentiality of
 * operations.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class OperationProcessor {

	public static boolean DEBUG = false;

	// The executor that actually runs the operations
	private static final ExecutorService exec = newSingleThreadExecutor();

	// A task that does nothing, just used for waiting.
	private static final Runnable noop = new Runnable() {
		@Override
		public void run() {
			// Do nothing
		}
	};

	/**
	 * Ensures that processing can start.
	 */
	public static void start() {
		// Implementation note: We indeed start the executor service at
		// class-loading, this makes this class immutable.
		if (DEBUG) {
			debug("starting");
		}
	}

	/**
	 * Submit a modifying operation to be performed asynchronously.
	 * 
	 * @param op
	 *            some modifying operation
	 */
	public static void submit(ElementOperation op) {
		if (DEBUG) {
			debug("submitting " + op);
		}
		exec.submit(op);
	}

	/**
	 * Blocks until all already submitted operations have been performed.
	 * <p>
	 * If additional operations have been submitted after this method is called,
	 * this method might return before they have been performed.
	 * </p>
	 */
	public static void waitUpToDate() {
		if (DEBUG) {
			debug("waiting for up to date");
		}
		final Future<?> future = exec.submit(noop);
		try {
			// Await for termination
			future.get();
		} catch (InterruptedException exc) {
			Thread.currentThread().interrupt();
		} catch (CancellationException e) {
			// Ignore
		} catch (ExecutionException e) {
			// Ignore
		}
	}

	/**
	 * Stop the processing thread. Don't bother terminating any work, as all
	 * clients have already stopped.
	 */
	public static void stop() {
		if (DEBUG) {
			debug("shutting down");
		}
		exec.shutdownNow();
	}

	private OperationProcessor() {
		// Do not instantiate
	}

	private static void debug(String message) {
		System.out.println("Rodin EMF OperationProcessor: " + message);
	}
}