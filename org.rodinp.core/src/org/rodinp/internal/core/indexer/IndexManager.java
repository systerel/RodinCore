/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.internal.core.indexer.IIndexDelta.Kind;
import org.rodinp.internal.core.indexer.persistence.PersistenceManager;
import org.rodinp.internal.core.indexer.persistence.PersistentIndexManager;
import org.rodinp.internal.core.indexer.tables.ExportTable;
import org.rodinp.internal.core.indexer.tables.FileTable;
import org.rodinp.internal.core.indexer.tables.NameTable;
import org.rodinp.internal.core.indexer.tables.RodinIndex;
import org.rodinp.internal.core.util.Util;

public final class IndexManager {

	// TODO change scheduling rules to the file being processed
	
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

	private final ReentrantReadWriteLock initSaveLock =
			new ReentrantReadWriteLock();

	/** Lock guarding table access during indexing */

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
	public void scheduleIndexing(IRodinFile... files)
			throws InterruptedException {
		assert !indexingEnabled;
		for (IRodinFile file : files) {
			final IRodinProject project = file.getRodinProject();
			final ProjectIndexManager pim = fetchPIM(project);
			pim.fileChanged(file);
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

	private void checkCancel(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			throw new CancellationException();
		}
	}

	private void lockReadInitAndPIM(ProjectIndexManager pim)
			throws InterruptedException {
		initSaveLock.readLock().lockInterruptibly();
		pim.lockRead();
	}

	private void unlockReadInitAndPIM(ProjectIndexManager pim) {
		pim.unlockRead();
		initSaveLock.readLock().unlock();
	}

	/**
	 * Returns the current index of the given project.
	 * <p>
	 * Note that the result may be erroneous if the project is being indexed.
	 * </p>
	 * 
	 * @param project
	 *            the project of the requested index.
	 * @return the current index of the given project.
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	public RodinIndex getIndex(IRodinProject project)
			throws InterruptedException {
		final ProjectIndexManager pim = fetchPIM(project);
		lockReadInitAndPIM(pim);
		final RodinIndex index = pim.getIndex();
		unlockReadInitAndPIM(pim);
		return index;
	}

	/**
	 * Returns the current file table of the given project.
	 * <p>
	 * Note that the result may be erroneous if the project is being indexed.
	 * </p>
	 * 
	 * @param project
	 *            the project of the requested file table.
	 * @return the current file table of the given project.
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	public FileTable getFileTable(IRodinProject project)
			throws InterruptedException {
		final ProjectIndexManager pim = fetchPIM(project);
		lockReadInitAndPIM(pim);
		final FileTable fileTable = pim.getFileTable();
		unlockReadInitAndPIM(pim);
		return fileTable;
	}

	/**
	 * Returns the current name table of the given project.
	 * <p>
	 * Note that the result may be erroneous if the project is being indexed.
	 * </p>
	 * 
	 * @param project
	 *            the project of the requested name table.
	 * @return the current name table of the given project.
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	public NameTable getNameTable(IRodinProject project)
			throws InterruptedException {
		final ProjectIndexManager pim = fetchPIM(project);
		lockReadInitAndPIM(pim);
		final NameTable nameTable = pim.getNameTable();
		unlockReadInitAndPIM(pim);
		return nameTable;
	}

	/**
	 * Returns the current export table of the given project.
	 * <p>
	 * Note that the result may be erroneous if the project is being indexed.
	 * </p>
	 * 
	 * @param project
	 *            the project of the requested export table.
	 * @return the current export table of the given project.
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	public ExportTable getExportTable(IRodinProject project)
			throws InterruptedException {
		final ProjectIndexManager pim = fetchPIM(project);
		lockReadInitAndPIM(pim);
		final ExportTable exportTable = pim.getExportTable();
		unlockReadInitAndPIM(pim);
		return exportTable;
	}

	ProjectIndexManager fetchPIM(IRodinProject project) {
		return pppim.getOrCreate(project);
	}

	private final Job indexing = new Job("indexing") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			printVerbose("indexing...");

			try {
				doIndexing(monitor);
			} catch (CancellationException e) {
				printVerbose("indexing Cancelled");

				return Status.CANCEL_STATUS;
			}
			printVerbose("...end indexing");

			if (monitor != null) {
				monitor.done();
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
			return Status.OK_STATUS;
		}
	};

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
		restore(savedState);
		
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		indexing.setRule(rodinDB.getSchedulingRule());
		indexing.setPriority(Job.DECORATE);

		boolean stop = false;
		do {
			try {
				stop = daemonMonitor.isCanceled();

				final IIndexDelta headDelta = queue.take();
				currentDeltas.add(headDelta);
				queue.drainTo(currentDeltas);
				if (indexingEnabled) {
					processCurrentDeltas();
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
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				listener,
				IResourceChangeEvent.POST_CHANGE
						| IResourceChangeEvent.POST_BUILD);
	}

	private void processCurrentDeltas() throws InterruptedException {
		final int maxAttempts = 3;
		final Iterator<IIndexDelta> iter = currentDeltas.iterator();
		while (iter.hasNext()) {
			final IIndexDelta delta = iter.next();

			int attempts = 0;
			boolean success = false;
			do {
				try {
					processDelta(delta);
					success = true;
				} catch (CancellationException e) {
					attempts++;
				}
			} while (!success && attempts < maxAttempts);
			queue.deltaProcessed();
			iter.remove();
		}
	}

	private void processDelta(IIndexDelta delta) throws InterruptedException {
		if (delta.getKind() == Kind.FILE_CHANGED) {
			final IRodinFile file = (IRodinFile) delta.getElement();

			processFileChanged(file);
		} else {
			processProjectChanged(delta);
		}
	}

	private void processProjectChanged(IIndexDelta delta) {
		final IRodinProject project = (IRodinProject) delta.getElement();
		final PersistenceManager persistenceManager =
				PersistenceManager.getDefault();
		final Kind kind = delta.getKind();

		if (kind == Kind.PROJECT_OPENED) {
			lockWriteInitSave();
			final boolean success =
					persistenceManager.restoreProject(project, pppim);
			if (!success) {
				indexProject(project);
			}
			persistenceManager.deleteProject(project);
			unlockWriteInitSave();
		} else if (kind == Kind.PROJECT_CLOSED) {
			// already saved by persistence manager (PROJECT_SAVE)
			pppim.remove(project);
		} else if (kind == Kind.PROJECT_CREATED || kind == Kind.PROJECT_CLEANED) {
			indexProject(project);
		} else if (kind == Kind.PROJECT_DELETED) {
			pppim.remove(project);
			persistenceManager.deleteProject(project);
		}
	}

	private void processFileChanged(final IRodinFile file)
			throws InterruptedException {
		final IRodinProject project = file.getRodinProject();
		final ProjectIndexManager pim = fetchPIM(project);

		pim.fileChanged(file);
		indexing.schedule();
		indexing.join();
		if (Status.CANCEL_STATUS.equals(indexing.getResult())) {
			throw new CancellationException();
		}
	}

	private void unlockWriteInitSave() {
		initSaveLock.writeLock().unlock();
	}

	private void lockWriteInitSave() {
		initSaveLock.writeLock().lock();
	}

	public PersistentIndexManager getPersistentData(boolean includeDeltas) {
		final Set<IIndexDelta> deltaSet = new HashSet<IIndexDelta>();
		if (includeDeltas) {
			deltaSet.addAll(currentDeltas);
			queue.drainTo(deltaSet);
		}
		return new PersistentIndexManager(pppim, deltaSet, indexerRegistry
				.getPersistentData());
	}

	private void restore(ISavedState savedState) {
		lockWriteInitSave();
		printVerbose("Restoring IndexManager");
		final PersistentIndexManager persistIM = new PersistentIndexManager(
				pppim, currentDeltas, new Registry<String, String>());
		final boolean success =
				PersistenceManager.getDefault().restore(savedState, persistIM, listener);
		if (!success || !indexerRegistry.sameAs(persistIM.getIndexerRegistry())) {
			indexAll();
			// FIXME run at startup even if we want indexing disabled
		}
		unlockWriteInitSave();
	}

	/**
	 * Blocks until the indexing system becomes up to date.
	 * 
	 * @throws InterruptedException
	 * 
	 */
	public void waitUpToDate() throws InterruptedException {
		queue.awaitEmptyQueue();
	}

	/**
	 * Clears the indexes, tables and indexers.
	 */
	public synchronized void clear() {
		lockWriteInitSave();
		clearIndexers();
		pppim.clear();
		unlockWriteInitSave();
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
	}

	void printVerbose(String message) {
		if (VERBOSE) {
			System.out.println(message);
		}
	}

	private class ProjectIndexing implements IWorkspaceRunnable {
		private final IRodinProject[] projects;

		public ProjectIndexing(IRodinProject... projects) {
			this.projects = projects;
		}

		
		public void run(IProgressMonitor monitor) {
			printVerbose("project indexing: " + Arrays.asList(projects));
			try {
				for (IRodinProject project : projects) {
					fetchPIM(project).indexAll(monitor);
				}
			} catch (CancellationException e) {
				printVerbose("indexing Cancelled");
			} finally {
				printVerbose("...end project indexing");
			}
		}
	}

	private void indexAll() {
		try {
			final IRodinDB rodinDB = RodinCore.getRodinDB();
			final IRodinProject[] allProjects = rodinDB.getRodinProjects();
			indexAll(allProjects);
		} catch (RodinDBException e) {
			// could not get projects: cannot do anymore
			if (DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private void indexProject(IRodinProject project) {
		indexAll(project);
	}

	private void indexAll(IRodinProject... projects) {

		final IRodinDB rodinDB = RodinCore.getRodinDB();
		final ProjectIndexing wholeIndexing = new ProjectIndexing(projects);

		try {
			RodinCore.run(wholeIndexing, rodinDB.getSchedulingRule(), null);
		} catch (RodinDBException e) {
			Util.log(e, "while indexing the whole database");
		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		listener.resourceChanged(event);		
	}

}
