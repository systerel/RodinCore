package org.rodinp.internal.core.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.internal.core.index.tables.DependenceTable;
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

	private final ProjectMapping<DependenceTable> dependenceTables;

	private Map<IFileElementType<?>, List<IIndexer>> indexers;

	private Set<IRodinFile> toBeIndexed;

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

		indexers = new HashMap<IFileElementType<?>, List<IIndexer>>();

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

		dependenceTables = new ProjectMapping<DependenceTable>() {
			@Override
			protected DependenceTable createT() {
				return new DependenceTable();
			}
		};

		toBeIndexed = new HashSet<IRodinFile>();
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

	public void addIndexer(IIndexer indexer, IFileElementType<?> fileType) {
		List<IIndexer> list = indexers.get(fileType);
		if (list == null) {
			list = new ArrayList<IIndexer>();
			indexers.put(fileType, list);
		}
		list.add(indexer);
	}

	public void clearIndexers() {
		indexers.clear();
	}

	public void scheduleIndexing(IRodinFile file) {

		toBeIndexed.add(file);

		launchIndexing(toBeIndexed);
		toBeIndexed.clear();
		// TODO don't launch indexing immediately (define scheduling options)
		// NOTE : that method will be replaced when implementing listeners
	}

	public void scheduleIndexing(IRodinFile[] files) {
		toBeIndexed.addAll(Arrays.asList(files));

		launchIndexing(toBeIndexed);
		toBeIndexed.clear();
	}

	private void launchIndexing(Set<IRodinFile> files) {

		final Map<IRodinProject, Set<IRodinFile>> toIndexSplit = splitIntoProjects(files);

		for (IRodinProject project : toIndexSplit.keySet()) {

			final Set<IRodinFile> toIndex = toIndexSplit.get(project);

			final RodinIndex index = indexes.getMapping(project);
			final FileTable fileTable = fileTables.getMapping(project);
			final NameTable nameTable = nameTables.getMapping(project);
			final ExportTable exportTable = exportTables.getMapping(project);
			final DependenceTable dependTable = dependenceTables
					.getMapping(project);

			removeInexistent(toIndex, exportTable, dependTable);
			if (toIndex.isEmpty()) {
				return;
			}

			updateDependencies(toIndex, dependTable);

			while (!toIndex.isEmpty()) {
				// TODO can be quite long, possibly infinite if some problem are
				// not detected => implement a timer

				IRodinFile file = getNextFile(toIndex, dependTable);

				final IIndexer indexer = getIndexerFor(file.getElementType());

				clean(file, index, fileTable, nameTable);

				final IndexingFacade indexingFacade = new IndexingFacade(file,
						index, fileTable, nameTable, exportTable, dependTable);

				indexer.index(file, indexingFacade);

				toIndex.remove(file);

				if (indexingFacade.mustReindexDependents()) {
					toIndex.addAll(Arrays.asList(dependTable
							.getDependents(file)));
				}
			}
		}
	}

	private void updateDependencies(Set<IRodinFile> toIndex,
			DependenceTable dependTable) {
	
		for (IRodinFile file : toIndex) {
			final IIndexer indexer = getIndexerFor(file.getElementType());
			final IRodinFile[] dependFiles = indexer.getDependencies(file);
			dependTable.put(file, dependFiles);
		}
	}

	private IRodinFile getNextFile(Set<IRodinFile> files,
			DependenceTable dependTable) {
		
		IRodinFile zeroDegree = null;
		for (IRodinFile file : files) {
			if (degree(file, dependTable, files) == 0) {
				zeroDegree = file;
			}
		}
		if (zeroDegree == null) {
			throw new IllegalStateException(
					"File dependencies contain one or more cycles");
		}
		return zeroDegree;
	}

	private void clean(IRodinFile file, final RodinIndex index,
			final FileTable fileTable, final NameTable nameTable) {

		for (IInternalElement element : fileTable.getElements(file)) {
			final Descriptor descriptor = index.getDescriptor(element);
			final String name = descriptor.getName();
			nameTable.remove(name, element);

			descriptor.removeOccurrences(file);

			if (descriptor.getOccurrences().length == 0) {
				index.removeDescriptor(element);
			}
		}
		fileTable.removeElements(file);
	}

	private Map<IRodinProject, Set<IRodinFile>> splitIntoProjects(
			Set<IRodinFile> files) {
		Map<IRodinProject, Set<IRodinFile>> result = new HashMap<IRodinProject, Set<IRodinFile>>();
		for (IRodinFile file : files) {
			IRodinProject project = file.getRodinProject();
			Set<IRodinFile> set = result.get(project);
			if (set == null) {
				set = new HashSet<IRodinFile>();
				result.put(project, set);
			}
			set.add(file);
		}
		return result;
	}


	private int degree(IRodinFile file, DependenceTable dependencies,
			Set<IRodinFile> valuable) {
		final Set<IRodinFile> dependents = new HashSet<IRodinFile>();

		dependents.addAll(Arrays.asList(dependencies.get(file)));
		dependents.retainAll(valuable);

		return dependents.size();
	}

	private void removeInexistent(Set<IRodinFile> files,
			ExportTable exportTable, DependenceTable dependenceTable) {
		List<IRodinFile> toRemove = new ArrayList<IRodinFile>();
		for (IRodinFile f : files) {
			if (!f.exists()) {
				toRemove.add(f);
			}
		}
		cleanInexistent(toRemove, exportTable, dependenceTable);
		files.removeAll(toRemove);
	}

	private void cleanInexistent(List<IRodinFile> toRemove,
			ExportTable exportTable, DependenceTable dependenceTable) {
		for (IRodinFile file : toRemove) {
			exportTable.remove(file);
			dependenceTable.remove(file);
		}
	}

	private IIndexer getIndexerFor(
			IFileElementType<? extends IRodinFile> fileType) {
		final List<IIndexer> list = indexers.get(fileType);
		if (list == null || list.isEmpty()) {
			throw new IllegalStateException("unable to find an indexer for "
					+ fileType);
		}
		// TODO manage indexers priorities
		return list.get(0);
	}

	private static abstract class ProjectMapping<T> {

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

		public void clear() {
			map.clear();
		}
	}

	public RodinIndex getIndex(IRodinProject project) {
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

	public DependenceTable getDependenceTable(IRodinProject project) {
		return dependenceTables.getMapping(project);
	}

	public void saveAll() {
		// TODO
	}

	public void load() {
		// TODO
	}

	public void clear() {
		indexes.clear();
		fileTables.clear();
		nameTables.clear();
		exportTables.clear();
		dependenceTables.clear();
		indexers.clear();
		toBeIndexed.clear();
	}
}
