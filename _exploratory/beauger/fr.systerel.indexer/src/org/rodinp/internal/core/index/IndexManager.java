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

	private final FileIndexingManager fim;

	private static final int eventMask = ElementChangedEvent.POST_CHANGE;

	private static final int QUEUE_CAPACITY = 10;
	private final BlockingQueue<IRodinFile> queue;
	private final RodinDBChangeListener listener;
	private static final int TIME_BEFORE_INDEXING = 10000;

	private IndexManager() {
		pims = new HashMap<IRodinProject, ProjectIndexManager>();

		fim = new FileIndexingManager();
		queue = new ArrayBlockingQueue<IRodinFile>(QUEUE_CAPACITY);
		listener = new RodinDBChangeListener(queue);
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
		fim.addIndexer(indexer, fileType);
	}

	/**
	 * Clears all associations between indexers and file types. Indexers will
	 * have to be added again if indexing is to be performed anew.
	 */
	public void clearIndexers() {
		fim.clear();
	}

	/**
	 * Schedules the indexing of the given files.
	 * 
	 * @param files
	 *            the files to index.
//	 * @deprecated this method is no longer needed as the indexing system
//	 *             mechanism is now based on RodinDB change events listening.
	 */
//	@Deprecated
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
	 * @see #isBusy()
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
	 * @see #isBusy()
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
	 * @see #isBusy()
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
	 * @see #isBusy()
	 */
	public ExportTable getExportTable(IRodinProject project) {
		return fetchPIM(project).getExportTable();
	}

	private ProjectIndexManager fetchPIM(IRodinProject project) {
		ProjectIndexManager pim = pims.get(project);
		if (pim == null) {
			pim = new ProjectIndexManager(project, fim);
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
			System.out.println("indexing...");
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
				indexing.schedule(TIME_BEFORE_INDEXING); // TODO define scheduling policies
			}
		}
	}

	private void load() {
		// TODO recover from previous save
		System.out.println("Loading IndexManager");

		RodinCore.addElementChangedListener(listener, eventMask);
	}

	/**
	 * Returns whether the indexing system is currently busy. This method should
	 * be called before any other request, as a <code>true</code> result
	 * indicates that the index database is being modified and that the current
	 * result of the requests may soon become obsolete.
	 * <p>
	 * Note that the busy state is inherently volatile, and in most cases
	 * clients cannot rely on the result of this method being valid by the time
	 * the result is obtained. For example, if isBusy returns <code>true</code>,
	 * the indexing may have actually completed by the time the method returns.
	 * All clients can infer from invoking this method is that the indexing
	 * system was recently in the returned state.
	 * 
	 * @return whether the indexing system is currently busy.
	 */
	public boolean isBusy() {
		return indexing.getState() != Job.NONE; // TODO maybe == Job.RUNNING
	}

	/**
	 * Clears the indexes, tables and indexers.
	 */
	public void clear() {
		pims.clear();
		fim.clear();
	}

}
