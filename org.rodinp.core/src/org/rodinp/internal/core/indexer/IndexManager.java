/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.internal.core.indexer.IIndexDelta.Kind;
import org.rodinp.internal.core.indexer.persistence.PersistenceManager;
import org.rodinp.internal.core.indexer.persistence.PersistentIndexManager;
import org.rodinp.internal.core.indexer.persistence.PersistentPIM;

public final class IndexManager {

	private static final int BLOCKING_DELAY = 200; // milliseconds

	// For debugging and tracing purposes
	public static boolean DEBUG;
	public static boolean VERBOSE;

	// Must be accessed only by synchronized methods
	private static IndexManager instance;

	private final PerProjectPIM pppim;

	private final IndexerRegistry indexerRegistry;

	private static final int eventMask = ElementChangedEvent.POST_CHANGE;

	private final DeltaQueue queue;

	private final List<IIndexDelta> currentDeltas;

	private final DeltaQueuer listener;
	// private static final int TIME_BEFORE_INDEXING = 2000;

	private volatile boolean indexingEnabled = true;

	private IndexManager() {
		pppim = new PerProjectPIM();
		indexerRegistry = IndexerRegistry.getDefault();
		queue = new DeltaQueue();
		currentDeltas = new ArrayList<IIndexDelta>();
		listener = new DeltaQueuer(queue);
	}

	/**
	 * Returns the singleton instance of the IndexManager.
	 * 
	 * @return the singleton instance of the IndexManager.
	 */
	public static synchronized IndexManager getDefault() {
		if (instance == null) {
			instance = new IndexManager();
		}
		return instance;
	}

	/**
	 * Adds an indexer, associated with the given file type.
	 * <p>
	 * The same indexer may be added for several file types. It will then be
	 * called whenever a file of one of those file types has to be indexed.
	 * </p>
	 * <p>
	 * Conversely, several indexers may be added for the same file type. They
	 * will then all be called each time a file of the given file type has to be
	 * indexed, according to the order they were added in.
	 * </p>
	 * 
	 * @param indexer
	 *            the indexer to add.
	 * @param fileType
	 *            the associated file type.
	 */
	public void addIndexer(IIndexer indexer, IInternalElementType<?> fileType) {
		indexerRegistry.addIndexer(indexer, fileType);
	}

	/**
	 * Clears all associations between indexers and file types. Indexers will
	 * have to be added again if indexing is to be performed anew.
	 */
	public synchronized void clearIndexers() {
		indexerRegistry.clear();
	}

	// for testing purposes only
	public void scheduleIndexing(IRodinFile... files) {
		assert !indexingEnabled;
		for (IRodinFile file : files) {
			final IRodinProject project = file.getRodinProject();
			final ProjectIndexManager pim = fetchPIM(project);
			pim.fileChanged(file, null);
		}

		doIndexing(null);
	}

	/**
	 * Performs the actual indexing of all files currently set to index, as soon
	 * as the indexing lock is obtained. Files are indexed project per project.
	 * If cancellation is requested on the given progress monitor, the method
	 * returns when the indexing of the current project has completed.
	 * 
	 * @param monitor
	 *            the monitor by which cancel requests can be performed.
	 */
	void doIndexing(IProgressMonitor monitor) {
		for (IRodinProject project : pppim.projects()) {
			fetchPIM(project).doIndexing(monitor);
			checkCancel(monitor);
		}
	}

	void checkCancel(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			throw new CancellationException();
		}
	}

	ProjectIndexManager fetchPIM(IRodinProject project) {
		return pppim.getOrCreate(project);
	}
	
	private final FileIndexing indexing = new FileIndexing("File Indexer");
	
	private class FileIndexing extends Job {
		
		private IRodinFile file = null;
		
		public FileIndexing(String name) {
			super(name);
		}

		public IRodinFile getFile() {
			return file;
		}
		
		public void setFile(IRodinFile file) {
			this.file = file;
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (DEBUG) {
				Thread.currentThread().setName(this.getName());
			}
			// avoid file modification during indexing
			final IRodinFile fileToIndex = file;
			try {
				if (fileToIndex == null) {
					doIndexing(monitor);
				} else {
					final IRodinProject project = fileToIndex.getRodinProject();
					final ProjectIndexManager pim = fetchPIM(project);
					pim.fileChanged(fileToIndex, monitor);
					checkCancel(monitor);
					pim.doIndexing(monitor);
					checkCancel(monitor);
				}
				file = null;
				return Status.OK_STATUS;
			} catch (CancellationException e) {
				if (IndexManager.VERBOSE)
					printVerbose("indexing cancelled for file: " + fileToIndex);
				return Status.CANCEL_STATUS;
			} finally {
				if (monitor != null) {
					monitor.done();
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}
			}
		}
	}

	/**
	 * Starts the indexing system. It will run until the given progress monitor
	 * is canceled.
	 * 
	 * @param savedState
	 *            the plugin saved state, or <code>null</code> if none available
	 * @param daemonMonitor
	 *            the progress monitor that handles the indexing system
	 *            cancellation.
	 */
	public void start(ISavedState savedState, IProgressMonitor daemonMonitor) {
		restore(savedState, daemonMonitor);

		indexing.setRule(null); // rules are managed for each file by the FIM
		indexing.setPriority(Job.DECORATE);

		boolean stop = false;
		do {
			try {
				stop = daemonMonitor.isCanceled();

				final IIndexDelta headDelta = queue.take();
				currentDeltas.add(headDelta);
				queue.drainTo(currentDeltas);
				if (indexingEnabled) {
					processCurrentDeltas(daemonMonitor);
					// TODO consider implementing a DeltaIndexingManager
					// or DeltaProcessor to delegate some code
				}
			} catch (InterruptedException e) {
				stop = true;
			}
		} while (!stop);
	}

	public void addListeners() {
		RodinCore.addElementChangedListener(listener, eventMask);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
				IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.PRE_CLOSE
				| IResourceChangeEvent.PRE_DELETE);
	}

	private void processCurrentDeltas(IProgressMonitor monitor)
			throws InterruptedException {
		final int maxAttempts = 3;
		final Iterator<IIndexDelta> iter = currentDeltas.iterator();
		while (iter.hasNext()) {
			final IIndexDelta delta = iter.next();

			int attempts = 0;
			boolean success = false;
			do {
				try {
					processDelta(delta, monitor);
					success = true;
				} catch (CancellationException e) {
					attempts++;
				}
			} while (!success && attempts < maxAttempts);
			queue.deltaProcessed();
			iter.remove();
		}
	}

	private void processDelta(IIndexDelta delta, IProgressMonitor monitor)
			throws InterruptedException {
		if (delta.getKind() == Kind.FILE_CHANGED) {
			final IRodinFile file = (IRodinFile) delta.getElement();

			processFileChanged(file, monitor);
		} else {
			processProjectChanged(delta, monitor);
		}
	}

	private void processProjectChanged(IIndexDelta delta,
			IProgressMonitor monitor) {
		final IRodinProject project = (IRodinProject) delta.getElement();
		final PersistenceManager persistenceManager = PersistenceManager
				.getDefault();
		switch (delta.getKind()) {
		case PROJECT_OPENED:
			synchronized (this) { // ¿ TODO synchronize the whole method ?
				final boolean success = persistenceManager.restoreProject(
						project, pppim);
				if (!success) {
					indexProject(project, monitor);
				}
				persistenceManager.deleteProject(project);
			}
			break;
		case PROJECT_CLOSED:
			// already saved by persistence manager (PROJECT_SAVE)
			pppim.remove(project);
			break;
		case PROJECT_CREATED:
		case PROJECT_CLEANED:
			indexProject(project, monitor);
			break;
		case PROJECT_DELETED:
			pppim.remove(project);
			persistenceManager.deleteProject(project);
			break;
		default:
			break;
		}
	}

	private void processFileChanged(final IRodinFile file, IProgressMonitor monitor)
			throws InterruptedException {
		indexing.setFile(file);
		indexing.schedule();
		indexing.join();
		if (Status.CANCEL_STATUS.equals(indexing.getResult())) {
			throw new CancellationException();
		}
	}

	public synchronized PersistentIndexManager getPersistentData() {
		// TODO move side effects to stop() ?
		final Set<IIndexDelta> deltaSet = new HashSet<IIndexDelta>();
		deltaSet.addAll(currentDeltas);
		queue.drainTo(deltaSet);
		return new PersistentIndexManager(pppim, deltaSet, indexerRegistry
				.getPersistentData());
	}

	public PersistentPIM getPersistentPIM(IRodinProject project) {
		final ProjectIndexManager pim = pppim.get(project);
		if (pim == null) {
			return null;
		}
		return pim.getPersistentData();
	}

	private synchronized void restore(ISavedState savedState,
			IProgressMonitor monitor) {
		if (IndexManager.VERBOSE)
			printVerbose("Restoring IndexManager");
		final PersistentIndexManager persistIM = new PersistentIndexManager(
				pppim, currentDeltas, new Registry<String, String>());
		final boolean success = PersistenceManager.getDefault().restore(
				savedState, persistIM, listener);
		if (!success || !indexerRegistry.sameAs(persistIM.getIndexerRegistry())) {
			indexAll(monitor);
			// FIXME run at startup even if we want indexing disabled
		}
	}

	/*
	 * Blocks until the indexing system becomes up to date, propagating
	 * interruption.
	 */
	public void waitUpToDate() throws InterruptedException {
		queue.awaitEmptyQueue();
	}

	/*
	 * To allow for cancellation, we need to start a new thread that will do the
	 * blocking wait, while the current thread wakes up regularly to check the
	 * cancellation status of the given monitor and interrupts the child thread
	 * in case of cancellation. Alternatively, if the current thread is itself
	 * interrupted, it propagates the interruption to the child thread, and
	 * rethrows the exception once the child has terminated.
	 */
	public void waitUpToDate(IProgressMonitor monitor)
			throws InterruptedException {
		final Thread childThread = new Thread() {

			@Override
			public void run() {
				if (VERBOSE)
					printVerbose("Child waiting thread has just started");
				try {
					waitUpToDate();
					if (VERBOSE)
						printVerbose("Child waiting thread has finished normally");
				} catch (InterruptedException consumed) {
					// We have been canceled, just finish the thread
					if (VERBOSE)
						printVerbose("Child waiting thread has been interrupted");
				}
			}

		};
		childThread.start();
		InterruptedException interruption = null;
		while (childThread.isAlive()) {
			try {
				childThread.join(BLOCKING_DELAY);
			} catch (InterruptedException exc) {
				if (VERBOSE)
					printVerbose("Parent thread has been interrupted while"
							+ " waiting for the child thread");
				interruption = exc;
			}
			if (interruption != null || monitor.isCanceled()) {
				if (VERBOSE)
					printVerbose("Parent thread cancels the child waiting thread");
				// Ask the child thread to stop
				childThread.interrupt();
			}
		}
		if (interruption != null) {
			throw interruption; // Propagate the parent thread interruption
		}
	}

	/**
	 * Clears the indexes, tables and indexers.
	 */
	public synchronized void clear() {
		clearIndexers();
		pppim.clear();
	}

	// For testing purpose only, do not call in operational code
	public void enableIndexing() {
		RodinCore.addElementChangedListener(listener, eventMask);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
		indexingEnabled = true;
		indexing.schedule();
	}

	// For testing purpose only, do not call in operational code
	public void disableIndexing() {
		RodinCore.removeElementChangedListener(listener);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		indexingEnabled = false;
		indexing.cancel();
	}

	static void printVerbose(String message) {
		System.out.println(message);
	}

	private void indexAll(IProgressMonitor monitor) {
		try {
			final IRodinDB rodinDB = RodinCore.getRodinDB();
			final IRodinProject[] allProjects = rodinDB.getRodinProjects();
			indexAll(monitor, allProjects);
		} catch (RodinDBException e) {
			// could not get projects: cannot do anymore
			if (DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private void indexProject(IRodinProject project, IProgressMonitor monitor) {
		indexAll(monitor, project);
	}

	private void indexAll(IProgressMonitor monitor, IRodinProject... projects) {
		if (VERBOSE)
			printVerbose("project indexing: " + Arrays.asList(projects));
		try {
			for (IRodinProject project : projects) {
				fetchPIM(project).indexAll(monitor);
			}
		} catch (CancellationException e) {
			if (VERBOSE)
				printVerbose("indexing Cancelled");
		} finally {
			if (VERBOSE)
				printVerbose("...end project indexing");
		}
	}

	public void stop() {
		// avoid processing future deltas
		indexingEnabled = false;
		// make pims reject (but record) future indexing requests
		// (even if indexing is disabled, deltas might be under process)
		for (ProjectIndexManager pim : pppim.pims()) {
			pim.setProjectVanishing();
		}
		// stop current indexing
		indexing.cancel();
	}
	
	public void projectVanishing(IRodinProject project) {
		if (VERBOSE)
			printVerbose("INDEXER: Project " + project + " vanishes");
		final ProjectIndexManager pim = this.pppim.get(project);
		if (pim == null) {
			return;
		}
		pim.setProjectVanishing();
		final IRodinFile file = indexing.getFile();
		if (file == null) {
			return;
		}
		if (project.isAncestorOf(file)) {
			indexing.cancel();
		}
	}
	
	// needed when a project is open with unprocessed files
	public void enqueueUnprocessedFile(IRodinFile file) {
		listener.enqueueDelta(new IndexDelta(file, Kind.FILE_CHANGED), false);
	}
	
	public synchronized IDeclaration getDeclaration(IInternalElement element) {
		final ProjectIndexManager pim = pppim.get(element.getRodinProject());
		if (pim == null) {
			return null;
		}
		return pim.getDeclaration(element);
	}

	public synchronized Set<IDeclaration> getDeclarations(IRodinFile file) {
		final ProjectIndexManager pim = pppim.get(file.getRodinProject());
		if (pim == null) {
			return Collections.emptySet();
		}
		return pim.getDeclarations(file);
	}

	public synchronized Set<IDeclaration> getVisibleDeclarations(IRodinFile file) {
		final ProjectIndexManager pim = pppim.get(file.getRodinProject());
		if (pim == null) {
			return Collections.emptySet();
		}
		return pim.getVisibleDeclarations(file);
	}

	public synchronized Set<IDeclaration> getDeclarations(
			IRodinProject project, String name) {
		final ProjectIndexManager pim = pppim.get(project);
		if (pim == null) {
			return Collections.emptySet();
		}
		return pim.getDeclarations(name);
	}

	public synchronized Set<IOccurrence> getOccurrences(IDeclaration declaration) {
		final ProjectIndexManager pim = pppim.get(declaration.getElement()
				.getRodinProject());
		if (pim == null) {
			return Collections.emptySet();
		}
		return pim.getOccurrences(declaration);
	}

	public synchronized Set<IDeclaration> getExports(IRodinFile file) {
		final ProjectIndexManager pim = pppim.get(file.getRodinProject());
		if (pim == null) {
			return Collections.emptySet();
		}
		return pim.getExports(file);
	}

}
