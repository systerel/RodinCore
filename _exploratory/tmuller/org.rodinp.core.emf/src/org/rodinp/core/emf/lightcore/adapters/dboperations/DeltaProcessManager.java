/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.adapters.dboperations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class DeltaProcessManager {

	public static boolean DEBUG = false;
	private static DeltaProcessManager instance;
	private static DeltaProcessJob deltaProcessJob;
	
	private DBOperationQueuer queuer;
	private DBOperationQueue queue;

	private final List<ElementOperation> currentOperations;

	private volatile boolean deltaProcessingEnabled = true;

	private DeltaProcessManager() {
		currentOperations = new ArrayList<ElementOperation>();
		queue = new DBOperationQueue();
		queuer = new DBOperationQueuer(queue);
	}

	public static synchronized DeltaProcessManager getDefault() {
		if (instance == null)
			instance = new DeltaProcessManager();
		return instance;
	}

	private static class DBOperationQueuer {

		private final DBOperationQueue queue;

		private final ThreadLocal<Boolean> interrupted = new ThreadLocal<Boolean>();

		public DBOperationQueuer(DBOperationQueue queue) {
			this.queue = queue;
		}

		public void enqueueOperation(ElementOperation operation,
				boolean allowDuplicate) {
			interrupted.set(false);
			try {
				while (true) {
					try {
						queue.put(operation, allowDuplicate);
						return;
					} catch (InterruptedException e) {
						interrupted.set(true);
					}
				}
			} finally {
				if (interrupted.get()) {
					Thread.currentThread().interrupt();
				}
			}
		}

		public ElementOperation take() throws InterruptedException {
			return queue.take();
		}
	}

	public void enqueueOperation(ElementOperation op) {
		queuer.enqueueOperation(op, false);
	}

	/**
	 * Starts the indexing system. It will run until the given progress monitor
	 * is canceled.
	 * 
	 * @param daemonMonitor
	 *            the progress monitor that handles the delta processingF system
	 *            cancellation.
	 * @throws InterruptedException 
	 */
	public void start(IProgressMonitor daemonMonitor) throws InterruptedException {
		boolean stop = false;
		do {
			try {
				stop = daemonMonitor.isCanceled();
				final ElementOperation headOperation = queuer.take();
				currentOperations.add(headOperation);
				queue.drainTo(currentOperations);
				if (deltaProcessingEnabled) {
					processCurrentOperations(daemonMonitor);
				}
			} catch (InterruptedException e) {
				stop = true;
				throw e;
			}
		} while (!stop);
	}
	
	public static void startDeltaProcess() {
		deltaProcessJob = new DeltaProcessJob("Delta Processor Manager");
		deltaProcessJob.setPriority(Job.INTERACTIVE);
		deltaProcessJob.setSystem(true);
		deltaProcessJob.schedule();
	}


	private void processCurrentOperations(IProgressMonitor monitor)
			throws InterruptedException {
		final int maxAttempts = 3;
		final Iterator<ElementOperation> iter = currentOperations.iterator();
		while (iter.hasNext()) {
			final ElementOperation operation = iter.next();
			int attempts = 0;
			boolean success = false;
			do {
				try {
					operation.perform();
					success = true;
				} catch (CancellationException e) {
					attempts++;
				}
			} while (!success && attempts < maxAttempts);
			queue.deltaProcessed();
			iter.remove();
		}
	}
	
	public static void stopDeltaProcess() {
		DeltaProcessManager.getDefault().stop();
		deltaProcessJob.stop();
	}

	public void stop() {
		// avoid processing future operations
		deltaProcessingEnabled = false;
	}
	
	public static class DeltaProcessJob extends Job {

		public DeltaProcessJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				if (DEBUG) {
					Thread.currentThread().setName(this.getName());
				}
				DeltaProcessManager.getDefault().start(monitor);
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return Status.CANCEL_STATUS;
			}
		}
		
		public void stop() {
			final Thread thread = getThread();
			if (thread != null) {
				thread.interrupt();
			}
		}
		
	}

}