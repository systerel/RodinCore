/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index;

import java.util.concurrent.CancellationException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
import org.rodinp.core.index.IIndexer;
import org.rodinp.internal.core.index.persistence.PersistenceManager;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

public final class IndexManager {

	// For debugging and tracing purposes
	public static boolean DEBUG;
	public static boolean VERBOSE;
	public static boolean SAVE_RESTORE;

	// TODO should automatically remove projects mappings when a project gets
	// deleted.
	// TODO implement an overall consistency check method

	// Must be accessed only by synchronized methods
	private static IndexManager instance;

	private final PerProjectPIM pppim;

	private final IndexerRegistry indexerRegistry;

	private static final int eventMask = ElementChangedEvent.POST_CHANGE;

	private final FileQueue queue;

	private final DeltaQueuer listener;
	// private static final int TIME_BEFORE_INDEXING = 2000;

	private volatile boolean ENABLE_INDEXING = true;

	private final ReentrantReadWriteLock initSaveLock =
			new ReentrantReadWriteLock();

	/** Lock guarding table access during indexing */

	private IndexManager() {
		pppim = new PerProjectPIM();
		indexerRegistry = IndexerRegistry.getDefault();
		queue = new FileQueue();
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

	/**
	 * Schedules the indexing of the given files.
	 * 
	 * @param files
	 *            the files to index.
	 */
	// @Deprecated
	public void scheduleIndexing(IRodinFile... files) {
		for (IRodinFile file : files) {
			final IRodinProject project = file.getRodinProject();
			final ProjectIndexManager pim = fetchPIM(project);
			pim.fileChanged(file);
		}

		doIndexing(null);
		// TODO don't launch indexing immediately (define scheduling options)
		// NOTE : that method will be replaced when implementing listeners
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
	 * @param daemonMonitor
	 *            the progress monitor that handles the indexing system
	 *            cancellation.
	 */
	public void start(IProgressMonitor daemonMonitor) {
		if (SAVE_RESTORE) {
			restore();
		}
		RodinCore.addElementChangedListener(listener, eventMask);

		final IRodinDB rodinDB = RodinCore.getRodinDB();
		indexing.setRule(rodinDB.getSchedulingRule());
		// indexing.setUser(true);

		boolean stop = false;
		do {
			try {
				final IRodinFile file = queue.take();
				// TODO drain to collection ?

				final IRodinProject project = file.getRodinProject();
				final ProjectIndexManager pim = fetchPIM(project);

				pim.fileChanged(file);
				if (ENABLE_INDEXING) {

					// TODO define scheduling policies ?
					indexing.schedule();
					indexing.join();
					queue.fileProcessed();
				}
				stop =
						daemonMonitor.isCanceled()
								|| Status.CANCEL_STATUS.equals(indexing
										.getResult());
				// FIXME treat separately :
				// if indexing cancelled, retry 3 times
				// then pass to next file after remembering this one

			} catch (InterruptedException e) {
				stop = true;
				// TODO save
				// - the queue
				// - the remaining work => done in PIM when cancel
			}
		} while (!stop);

		// save();
	}

	private void unlockWriteInitSave() {
		initSaveLock.writeLock().unlock();
	}

	private void lockWriteInitSave() {
		initSaveLock.writeLock().lock();
	}

	public PerProjectPIM getPerProjectPIM() {
		return pppim;
	}

	// public void save(File file) {
	// lockWriteInitSave();
	// PersistenceManager.getDefault().save(pppim);
	// unlockWriteInitSave();
	// }

	private void restore() {
		lockWriteInitSave();
		if (VERBOSE) {
			System.out.println("Restoring IndexManager");
		}
		PersistenceManager.getDefault().restore();

		unlockWriteInitSave();
	}

	/**
	 * Returns <code>true</code> when the indexing system is up to date, else
	 * blocks until it becomes up to date.
	 * 
	 * @throws InterruptedException
	 * 
	 */
	public void waitUpToDate() throws InterruptedException {
		// TODO use 1 lock per project ?
		// FIXME sometimes blocks (running testIntegration)
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
		ENABLE_INDEXING = true;
		indexing.schedule();
	}

	// For testing purpose only, do not call in operational code
	public void disableIndexing() {
		RodinCore.removeElementChangedListener(listener);
		ENABLE_INDEXING = false;

	}

	void printVerbose(String message) {
		if (VERBOSE) {
			System.out.println(message);
		}
	}

	private final Job overallIndexing = new Job("overallIndexing") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			printVerbose("overall indexing...");
			final IRodinDB rodinDB = RodinCore.getRodinDB();
			try {
				final IRodinProject[] allProjects =
					rodinDB.getRodinProjects();
				for (IRodinProject project : allProjects) {
					fetchPIM(project).indexAll(monitor);
				}
				doIndexing(monitor);
			} catch (CancellationException e) {
				printVerbose("indexing Cancelled");

				return Status.CANCEL_STATUS;
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			printVerbose("...end overall indexing");

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
	 * 
	 */
	public void indexAll() {
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		overallIndexing.setRule(rodinDB.getSchedulingRule());
		overallIndexing.schedule();
		try {
			overallIndexing.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
