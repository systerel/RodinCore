package org.rodinp.internal.core.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public final class IndexManager {

	// TODO should automatically remove projects mappings when a project gets
	// deleted.

	private static IndexManager instance;
	private Map<IRodinProject, RodinIndex> indexes;
	private Map<IRodinProject, FileTable> fileTables;
	private Map<IRodinProject, NameTable> nameTables;
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
		fileTables = new HashMap<IRodinProject, FileTable>();
		nameTables = new HashMap<IRodinProject, NameTable>();
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

	public void scheduleIndexing(IRodinFile file) {
		// TODO don't launch indexing immediately (define scheduling options)

		if (!file.exists()) {
			throw new IllegalArgumentException(
					"trying to index an inexistent file: " + file.getBareName());
		}
		indexFile(file);
	}

	private IIndexer findIndexerFor(IRodinFile file) {
		for (IIndexer indexer : indexers) {
			if (indexer.canIndex(file)) {
				return indexer;
			}
		}
		return null;
	}

	private void indexFile(IRodinFile file) {

		IIndexer indexer = findIndexerFor(file);

		if (indexer == null) {
			throw new IllegalStateException("unable to find an indexer for "
					+ file.getElementName());
		}

		final IRodinProject project = file.getRodinProject();

		final IRodinIndex index = getIndex(project);
		final FileTable fileTable = getFileTable(project);
		final NameTable nameTable = getNameTable(project);

		clean(file, index, fileTable, nameTable);

		indexer.index(file, new IndexingFacade(index, fileTable, nameTable));
	}

	private void clean(IRodinFile file, final IRodinIndex index,
			final FileTable fileTable, final NameTable nameTable) {

		for (IInternalElement element : fileTable.getElements(file)) {
			final String name = index.getDescriptor(element).getName();
			nameTable.remove(name, element);
			index.removeDescriptor(element);
		}
		fileTable.removeElements(file);
	}

	// TODO: find out whether there is a way to factorize the various create and
	// get methods

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
			fileTables.put(project, new FileTable());
		}
	}

	public FileTable getFileTable(IRodinProject project) {
		createFileTable(project, false);
		return fileTables.get(project);
	}

	private void createNameTable(IRodinProject project, boolean overwrite) {
		if (overwrite || !nameTables.containsKey(project)) {
			nameTables.put(project, new NameTable());
		}
	}

	public NameTable getNameTable(IRodinProject project) {
		createNameTable(project, false);
		return nameTables.get(project);
	}

	public void saveAll() {
		// TODO
	}

	public void load() {
		// TODO
	}

}
