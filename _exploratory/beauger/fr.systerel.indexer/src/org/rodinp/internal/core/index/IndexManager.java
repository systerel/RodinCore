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
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public final class IndexManager {

	// TODO should automatically remove projects mappings when a project gets
	// deleted.

	private static IndexManager instance;

	private final ProjectMapping<RodinIndex> indexes;

	private final ProjectMapping<FileTable> fileTables;

	private final ProjectMapping<NameTable> nameTables;

	private final ProjectMapping<ExportTable> exportTables;

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
		indexes = new ProjectMapping<RodinIndex>() {
			@Override
			protected RodinIndex createT() {
				return new RodinIndex();
			}
		};

		indexers = new HashSet<IIndexer>();

		fileTables = new ProjectMapping<FileTable>() {
			@Override
			protected FileTable createT() {
				return new FileTable();
			}
		};

		nameTables = new ProjectMapping<NameTable>() {
			@Override
			protected NameTable createT() {
				return new NameTable();
			}
		};

		exportTables = new ProjectMapping<ExportTable>() {
			@Override
			protected ExportTable createT() {
				return new ExportTable();
			}
		};
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
		assertFileExists(file);
		
		IIndexer indexer = getIndexerFor(file);

		final IRodinProject project = file.getRodinProject();

		final IRodinIndex index = indexes.getMapping(project);
		final FileTable fileTable = fileTables.getMapping(project);
		final NameTable nameTable = nameTables.getMapping(project);
		final ExportTable exportTable = exportTables.getMapping(project);

		clean(file, index, fileTable, nameTable);

		final Map<IInternalElement, String> newExports = indexer.getExports(file);
		// TODO treat export tables differences
		exportTable.put(file, newExports);
		
		final IndexingFacade indexingFacade = new IndexingFacade(file, index,
				fileTable, nameTable, exportTable);
		indexer.index(file, indexingFacade);

	}

	private void assertFileExists(IRodinFile file) {
		if (!file.exists()) {
			throw new IllegalArgumentException("cannot perform index: file "
					+ file.getBareName() + " does not exist");
		}
	}

	private IIndexer getIndexerFor(IRodinFile file) {
		for (IIndexer indexer : indexers) {
			if (indexer.canIndex(file)) {
				return indexer;
			}
		}
		throw new IllegalStateException("unable to find an indexer for "
				+ file.getElementName());
	}

	private void clean(IRodinFile file, final IRodinIndex index,
			final FileTable fileTable, final NameTable nameTable) {

		for (IInternalElement element : fileTable.getElements(file)) {
			final String name = index.getDescriptor(element).getName();
			nameTable.remove(name, element);
			index.removeDescriptor(element); // FIXME new implementation => keep external occurrences
		}
		fileTable.removeElements(file);
	}

	private abstract class ProjectMapping<T> {

		private final Map<IRodinProject, T> map;

		public ProjectMapping() {
			map = new HashMap<IRodinProject, T>();
		}

		abstract protected T createT();

		/**
		 * Gets the existing mapping to given project, otherwise creates a new
		 * one and returns it.
		 * 
		 * @param project
		 * @return the non null T object mapped to the given project.
		 */
		public T getMapping(IRodinProject project) {
			T result = map.get(project);
			if (result == null) {
				result = createT();
				map.put(project, result);
			}
			return result;
		}
	}

	public IRodinIndex getIndex(IRodinProject project) {
		return indexes.getMapping(project);
	}

	public FileTable getFileTable(IRodinProject project) {
		return fileTables.getMapping(project);
	}

	public NameTable getNameTable(IRodinProject project) {
		return nameTables.getMapping(project);
	}

	public ExportTable getExportTable(IRodinProject project) {
		return exportTables.getMapping(project);
	}

	public void saveAll() {
		// TODO
	}

	public void load() {
		// TODO
	}

}
