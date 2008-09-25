package org.rodinp.internal.core.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IIndexer;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public final class IndexManager extends Thread {

	// TODO should automatically remove projects mappings when a project gets
	// deleted.
	// TODO implement an overall consistency check method

	// Must be accessed only by synchronized methods
	private static IndexManager instance;

	private final Map<IRodinProject, ProjectIndexManager> pims;
	
	private final FileIndexingManager fim;
	
	protected IElementChangedListener listener = new IElementChangedListener() {
		public void elementChanged(ElementChangedEvent event) {
			IndexManager.this.elementChanged(event);
		}

	};

	private static final int eventMask = ElementChangedEvent.POST_CHANGE;

	private final List<IRodinElementDelta> deltas = new ArrayList<IRodinElementDelta>();

	private IndexManager() {
		pims = new HashMap<IRodinProject, ProjectIndexManager>();
		
		fim = new FileIndexingManager();
		
		RodinCore.addElementChangedListener(listener, eventMask);
	}

	public static synchronized IndexManager getDefault() {
		if (instance == null) {
			instance = new IndexManager();
		}
		return instance;
	}

	void elementChanged(ElementChangedEvent event) {
		deltas.add(event.getDelta());
//		System.out.println(event);
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

		launchIndexing();
		// TODO don't launch indexing immediately (define scheduling options)
		// NOTE : that method will be replaced when implementing listeners
	}
	
	private void launchIndexing() {
		for (IRodinProject project : pims.keySet()) {
			fetchPIM(project).launchIndexing();
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
	
	public void saveAll() {
		// TODO
	}

	public void load() {
		// TODO
	}

	public void clear() {
		pims.clear();
		fim.clear();
	}
}
