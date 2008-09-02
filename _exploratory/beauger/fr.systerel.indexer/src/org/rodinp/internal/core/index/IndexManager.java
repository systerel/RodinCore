package org.rodinp.internal.core.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.internal.core.index.tables.FileIndexTable;

public final class IndexManager {

	// TODO should automatically remove projects when they get deleted.

	private static IndexManager instance;
	private Map<IRodinProject, RodinIndex> indexes;
	private Map<IRodinProject, FileIndexTable> fileTables;
	private Set<IIndexer> indexers;

	// protected IElementChangedListener listener = new
	// IElementChangedListener() {
	// rETURNS
	// public void elementChanged(ElementChangedEvent event) {
	// IndexManager.this.elementChanged(event);
	// }
	//
	// };

	private IndexManager() {
		indexes = new HashMap<IRodinProject, RodinIndex>();
		indexers = new HashSet<IIndexer>();
		fileTables = new HashMap<IRodinProject, FileIndexTable>();
		// TODO: register listener
	}

	public static IndexManager getDefault() {
		// TODO: examine multithreading issues
		if (instance == null) {
			instance = new IndexManager();
		}
		return instance;
	}

	// void elementChanged(ElementChangedEvent event) {
	// scheduleIndexing(event.getDelta().getElement().getRodinProject());
	// // TODO: figure out a better use of the delta
	// }

	public void addIndexer(IIndexer indexer) {
		indexers.add(indexer);
	}

	public void removeIndexer(IIndexer indexer) {
		indexers.remove(indexer);
	}

	public void scheduleIndexing(IRodinElement rodinElement) {
		// TODO don't launch indexing immediately (define scheduling options)
		index(rodinElement);
	}

	private IIndexer findIndexerFor(IRodinFile file) {
		for (IIndexer indexer : indexers) {
			if (indexer.canIndex(file)) {
				return indexer;
			}
		}
		return null;
	}

	private void index(IRodinElement rodinElement) {
		if (rodinElement instanceof IRodinFile) {
			IRodinFile file = (IRodinFile) rodinElement;
			indexFile(file);
		} else if (rodinElement instanceof IRodinProject) {
			IRodinProject project = (IRodinProject) rodinElement;
			indexProject(project);
		}
	}

	private void indexFile(IRodinFile file) {
		IIndexer indexer = findIndexerFor(file);

		if (indexer == null) {
			// TODO: throw an exception or return an error code
			// or try to decompose a possibly complex element (RodinDB,
			// RodinProject)
			// into more simple ones and retry to find an indexer.
			return;
		}

		final IRodinProject project = file.getRodinProject();
		if (project == null || !project.exists()) {
			// TODO: throw an exception or return an error code
			return;
		}
		cleanIndex(file);
		indexer.index(file, new IndexingFacade(getIndex(project),
				getFileTable(project)));
	}

	private void cleanIndex(IRodinFile file) {
		final IRodinProject project = file.getRodinProject();
		final IRodinIndex index = getIndex(project);
		final FileIndexTable fileTable = getFileTable(project);
	
		for (IInternalElement element : fileTable.getElements(file)) {
			index.removeDescriptor(element);
		}
	}

	private void indexProject(IRodinProject project) {
		// TODO
	}

	/**
	 * Creates a new index associated to the given project. Does nothing if an
	 * index already exists for the project.
	 * 
	 * @param project
	 *            the project for which to create an index.
	 * @param overwrite
	 *            overwrites any existing mapping to that project.
	 */
	private void createIndex(IRodinProject project, boolean overwrite) {
		if (overwrite || !indexes.containsKey(project)) {
			indexes.put(project, new RodinIndex());
		}
	}

	/**
	 * Returns an IRodinIndex corresponding to the given project. If no index
	 * already exists, a new empty one is created.
	 * 
	 * @param project
	 * @return a non null IRodinIndex.
	 */
	public IRodinIndex getIndex(IRodinProject project) {
		createIndex(project, false); // creates only if not already present
		return indexes.get(project);
	}

	private void createFileTable(IRodinProject project, boolean overwrite) {
		if (overwrite || !fileTables.containsKey(project)) {
			fileTables.put(project, new FileIndexTable());
		}
	}

	public FileIndexTable getFileTable(IRodinProject project) {
		createFileTable(project, false); // creates only if not already
											// present
		return fileTables.get(project);
	}

	public void saveAll() {
		// TODO
	}

	public void load() {
		// TODO
	}

}
