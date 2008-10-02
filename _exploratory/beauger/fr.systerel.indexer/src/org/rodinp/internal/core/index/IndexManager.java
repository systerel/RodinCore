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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IIndexer;
import org.rodinp.internal.core.RodinDB;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

public final class IndexManager {

	// For debugging and tracing purposes
	public static boolean DEBUG;
	public static boolean VERBOSE;

	// TODO should automatically remove projects mappings when a project gets
	// deleted.
	// TODO implement an overall consistency check method

	// Must be accessed only by synchronized methods
	private static IndexManager instance;

	private final Map<IRodinProject, ProjectIndexManager> pims;

	private final IndexersManager indexersManager;
	
	private final FileIndexingManager fim;

	private static final int eventMask = ElementChangedEvent.POST_CHANGE;

	private static final int QUEUE_CAPACITY = 10;
	private final BlockingQueue<IRodinFile> queue;
	private final RodinDBChangeListener listener;
	private static final int TIME_BEFORE_INDEXING = 10000;

	private volatile boolean ENABLE_INDEXING = true;
    /** Lock guarding table access during indexing */
//    private final Lock indexingLock;
    
	private IndexManager() {
		pims = new HashMap<IRodinProject, ProjectIndexManager>();
		indexersManager = new IndexersManager();
		fim = new FileIndexingManager(indexersManager);
		queue = new ArrayBlockingQueue<IRodinFile>(QUEUE_CAPACITY);
		listener = new RodinDBChangeListener(queue);
//		indexingLock = new ReentrantLock(true); // TODO decide which type of Lock
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
	 * <p>
	 * Conversely, several indexers may be added for the same file type. They
	 * will then all be called each time a file of the given file type has to be
	 * indexed, according to the order they were added in.
	 * 
	 * @param indexer
	 *            the indexer to add.
	 * @param fileType
	 *            the associated file type.
	 */
	public void addIndexer(IIndexer indexer, IFileElementType<?> fileType) {
		indexersManager.addIndexer(indexer, fileType);
	}

	/**
	 * Clears all associations between indexers and file types. Indexers will
	 * have to be added again if indexing is to be performed anew.
	 */
	public void clearIndexers() {
		indexersManager.clear();
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
			pim.setToIndex(file);
		}

		launchIndexing(null);
		// TODO don't launch indexing immediately (define scheduling options)
		// NOTE : that method will be replaced when implementing listeners
	}

	/**
	 * Performs immediately the actual indexing of all files currently set to
	 * index. Files are indexed project per project. If cancellation is
	 * requested on the given progress monitor, the method returns when the
	 * indexing of the current project has completed.
	 * 
	 * @param monitor
	 *            the monitor by which cancel requests can be performed, or
	 *            <code>null</code> if monitoring is not required.
	 */
	void launchIndexing(IProgressMonitor monitor) {
		// TODO use indexingLock
		for (IRodinProject project : pims.keySet()) {
			fetchPIM(project).launchIndexing();
			if (monitor != null && monitor.isCanceled()) {
				return;
			}
		}
	}

	/**
	 * Returns the current index of the given project.
	 * <p>
	 * Note that the result may be erroneous if the project is being indexed.
	 * 
	 * @param project
	 *            the project of the requested index.
	 * @return the current index of the given project.
	 * @see #isUpToDate()
	 */
	public RodinIndex getIndex(IRodinProject project) {
		return fetchPIM(project).getIndex();
	}

	/**
	 * Returns the current file table of the given project.
	 * <p>
	 * Note that the result may be erroneous if the project is being indexed.
	 * 
	 * @param project
	 *            the project of the requested file table.
	 * @return the current file table of the given project.
	 * @see #isUpToDate()
	 */
	public FileTable getFileTable(IRodinProject project) {
		return fetchPIM(project).getFileTable();
	}

	/**
	 * Returns the current name table of the given project.
	 * <p>
	 * Note that the result may be erroneous if the project is being indexed.
	 * 
	 * @param project
	 *            the project of the requested name table.
	 * @return the current name table of the given project.
	 * @see #isUpToDate()
	 */
	public NameTable getNameTable(IRodinProject project) {
		return fetchPIM(project).getNameTable();
	}

	/**
	 * Returns the current export table of the given project.
	 * <p>
	 * Note that the result may be erroneous if the project is being indexed.
	 * 
	 * @param project
	 *            the project of the requested export table.
	 * @return the current export table of the given project.
	 * @see #isUpToDate()
	 */
	public ExportTable getExportTable(IRodinProject project) {
		return fetchPIM(project).getExportTable();
	}

	private ProjectIndexManager fetchPIM(IRodinProject project) {
		ProjectIndexManager pim = pims.get(project);
		if (pim == null) {
			pim = new ProjectIndexManager(project, fim, indexersManager);
			pims.put(project, pim);
		}
		return pim;
	}

	public void save() {
		// TODO
	}

	private final Job indexing = new Job("indexing") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (VERBOSE) {
				System.out.println("indexing...");
			}
			launchIndexing(monitor);
			if (monitor != null && monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}
	};

	/**
	 * Starts the indexing system. It will run until the given progress monitor
	 * is canceled.
	 * 
	 * @param startMonitor
	 *            the progress monitor that handles the indexing system
	 *            cancellation.
	 */
	public void start(IProgressMonitor startMonitor) {
		load();

		final RodinDB rodinDB = RodinDBManager.getRodinDBManager().getRodinDB();
		indexing.setRule(rodinDB.getSchedulingRule());
		// indexing.setUser(true);

		while (!startMonitor.isCanceled()) {
			IRodinFile file = null;
			try {
				file = queue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final IRodinProject project = file.getRodinProject();
			final ProjectIndexManager pim = fetchPIM(project);
			final boolean isSet = pim.setToIndex(file);

			if (isSet) {
				if (ENABLE_INDEXING) {

					indexing.schedule(TIME_BEFORE_INDEXING);
					// TODO define scheduling policies
				}
			}
		}
	}

	private void load() {
		if (VERBOSE) {
			System.out.println("Loading IndexManager");
		}
		// TODO recover from previous save

		RodinCore.addElementChangedListener(listener, eventMask);
	}

	/**
	 * Returns <code>true</code> when the indexing system is up to date, else
	 * blocks until it becomes up to date.
	 * 
	 * @return whether the indexing system is currently busy.
	 */
	public boolean isUpToDate() {
		// TODO use indexingLock
		return indexing.getState() == Job.NONE; // TODO maybe != Job.RUNNING
	}

	/**
	 * Clears the indexes, tables and indexers.
	 */
	public void clear() {
		pims.clear();
		clearIndexers();
	}

	public synchronized void enableIndexing() {
		ENABLE_INDEXING = true;
	}

	public synchronized void disableIndexing() {
		ENABLE_INDEXING = false;
	}

}
