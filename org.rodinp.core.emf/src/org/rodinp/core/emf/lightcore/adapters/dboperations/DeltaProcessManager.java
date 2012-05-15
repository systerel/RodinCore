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

import static org.rodinp.core.emf.lightcore.LightCoreUtils.log;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class DeltaProcessManager {

	public static boolean DEBUG = false;
	private static DeltaProcessManager instance = new DeltaProcessManager();
	private static DeltaProcessJob deltaProcessJob;
	
	private DBOperationQueuer queuer;
	private DBOperationQueue queue;

	private volatile boolean deltaProcessingEnabled = true;

	private DeltaProcessManager() {
		queue = new DBOperationQueue();
		queuer = new DBOperationQueuer(queue);
	}

	public static DeltaProcessManager getDefault() {
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
						queue.put(operation);
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
	 */
	public void start(IProgressMonitor daemonMonitor) {
		boolean stop = false;
		do {
			try {
				stop = daemonMonitor.isCanceled();
				if (deltaProcessingEnabled) {
					processCurrentOperations(daemonMonitor);
				}
			} catch (InterruptedException e) {
				stop = true;
				if (DEBUG)
					log(e);
				Thread.currentThread().interrupt();
			}
		} while (!stop);
	}
	
	public static void startDeltaProcess() {
		deltaProcessJob = new DeltaProcessJob("RodinEditor Delta Processor Manager");
		deltaProcessJob.setPriority(Job.INTERACTIVE);
		deltaProcessJob.setSystem(true); // don't show because always running
		deltaProcessJob.schedule();
	}


	private void processCurrentOperations(IProgressMonitor monitor)
			throws InterruptedException {
		final ElementOperation headOperation = queuer.take();
		headOperation.perform();
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
			if (DEBUG) {
				Thread.currentThread().setName(this.getName());
			}
			DeltaProcessManager.getDefault().start(monitor);
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}
		
		public void stop() {
			final Thread thread = getThread();
			if (thread != null) {
				thread.interrupt();
			}
		}
		
	}

}