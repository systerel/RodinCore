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

	public static synchronized IndexManager getDefault() {
		if (instance == null) {
			instance = new IndexManager();
		}
		return instance;
	}

	public void addIndexer(IIndexer indexer, IFileElementType<?> fileType) {
		fim.addIndexer(indexer, fileType);
	}

	public void clearIndexers() {
		fim.clear();
	}

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

	void launchIndexing(IProgressMonitor monitor) {
		for (IRodinProject project : pims.keySet()) {
			fetchPIM(project).launchIndexing();
			if (monitor != null && monitor.isCanceled()) {
				return;
			}
		}
	}

	public RodinIndex getIndex(IRodinProject project) {
		return fetchPIM(project).getIndex();
	}

	public FileTable getFileTable(IRodinProject project) {
		return fetchPIM(project).getFileTable();
	}

	public NameTable getNameTable(IRodinProject project) {
		return fetchPIM(project).getNameTable();
	}

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
				indexing.schedule(TIME_BEFORE_INDEXING);
			}
		}
	}

	private void load() {
		// TODO recover from previous save
		System.out.println("Loading IndexManager");

		RodinCore.addElementChangedListener(listener, eventMask);
	}

	public boolean isBusy() {
		return indexing.getState() != Job.NONE; // TODO maybe == Job.RUNNING
	}
	
	public void clear() {
		pims.clear();
		fim.clear();
	}

}
