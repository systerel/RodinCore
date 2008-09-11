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
import org.rodinp.core.index.IIndexingFacade;
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

		Map<IRodinProject, Set<IRodinFile>> toIndexSplit = splitIntoProjects(files);

		for (IRodinProject project : toIndexSplit.keySet()) {

			Set<IRodinFile> toIndex = toIndexSplit.get(project);

			final RodinIndex index = indexes.getMapping(project);
			final FileTable fileTable = fileTables.getMapping(project);
			final NameTable nameTable = nameTables.getMapping(project);
			final ExportTable exportTable = exportTables.getMapping(project);
			DependenceTable dependencies = dependenceTables.getMapping(project);

			update(toIndex, dependencies, exportTable);
			
			IRodinFile[] indexingOrder = orderFilesToIndex(toIndex,
					dependencies);

			for (IRodinFile file : indexingOrder) {
				assertFileExists(file);

				IIndexer indexer = getIndexerFor(file.getElementType());

				clean(file, index, fileTable, nameTable);

				final IIndexingFacade indexingFacade = new IndexingFacade(file,
						indexer, index, fileTable, nameTable, exportTable);

				indexer.index(file, indexingFacade);
			}
		}
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

	private void update(Set<IRodinFile> toIndex,
			DependenceTable dependencies, ExportTable exports) {

		final Set<IRodinFile> dependents = new HashSet<IRodinFile>();
		
		for (IRodinFile file : toIndex) {
			final IIndexer indexer = getIndexerFor(file.getElementType());
			final IRodinFile[] dependFiles = indexer.getDependencies(file);
			final Map<IInternalElement, String> newExports = indexer
			.getExports(file);
			// FIXME something to do according to exports change
			if (!newExports.isEmpty() && !newExports.equals(exports.get(file))) {
				final IRodinFile[] fileDeps = dependencies.getDependents(file);
				dependents.addAll(Arrays.asList(fileDeps));
			}
			dependencies.put(file, dependFiles);
			exports.put(file, newExports);
		}
		toIndex.addAll(dependents);
	}
	

	private Integer max(Integer i, Integer j) {
		return i > j ? i : j;
	}

	private IRodinFile[] orderFilesToIndex(Set<IRodinFile> files,
			DependenceTable dependencies) {
		Map<IRodinFile, Integer> degree = new HashMap<IRodinFile, Integer>();
		Map<IRodinFile, Integer> level = new HashMap<IRodinFile, Integer>();

		for (IRodinFile file : files) {
			degree.put(file, degree(file, dependencies, files));
			level.put(file, 0);
		}

		final int size = files.size();
		for (int i = 0; i < size; i++) {
			IRodinFile zeroDegree = getZeroDegree(degree);
			if (zeroDegree == null) {
				throw new IllegalStateException(
						"File dependencies contain one or more cycles");
			} else {
				degree.put(zeroDegree, -1);
				updateLevels(dependencies, degree, level, zeroDegree);
			}
		}
		return flattenLevels(level);
	}

	private int degree(IRodinFile file, DependenceTable dependencies,
			Set<IRodinFile> valuable) {
		final Set<IRodinFile> dependents = new HashSet<IRodinFile>();

		dependents.addAll(Arrays.asList(dependencies.get(file)));
		dependents.retainAll(valuable);

		return dependents.size();
	}

	private IRodinFile getZeroDegree(Map<IRodinFile, Integer> degree) {
		IRodinFile zeroDegree = null;
		final Integer ZERO = new Integer(0);
		for (IRodinFile file : degree.keySet()) {
			if (degree.get(file).equals(ZERO)) {
				zeroDegree = file;
				break;
			}
		}
		return zeroDegree;
	}

	private IRodinFile[] flattenLevels(Map<IRodinFile, Integer> level) {
		Map<Integer, Set<IRodinFile>> sortedLevels = new HashMap<Integer, Set<IRodinFile>>();
		Integer maxLevel = new Integer(0);

		for (IRodinFile file : level.keySet()) {
			final Integer lvl = level.get(file);
			maxLevel = max(maxLevel, lvl);
			Set<IRodinFile> set = sortedLevels.get(lvl);
			if (set == null) {
				set = new HashSet<IRodinFile>();
				sortedLevels.put(lvl, set);
			}
			set.add(file);
		}

		List<IRodinFile> result = new ArrayList<IRodinFile>();
		for (Integer l = 0; l <= maxLevel; l++) {
			final Set<IRodinFile> set = sortedLevels.get(l);
			result.addAll(set);
		}

		return result.toArray(new IRodinFile[result.size()]);
	}

	private void updateLevels(DependenceTable dependencies,
			Map<IRodinFile, Integer> degree, Map<IRodinFile, Integer> level,
			IRodinFile zeroDegree) {
		for (IRodinFile file : level.keySet()) {
			final List<IRodinFile> list = Arrays.asList(dependencies.get(file));
			if (list.contains(zeroDegree)) {
				degree.put(file, degree.get(file) - 1);
				level
						.put(file, max(level.get(file),
								level.get(zeroDegree) + 1));
			}
		}
	}

	private void assertFileExists(IRodinFile file) {
		if (!file.exists()) {
			toBeIndexed.remove(file);
			throw new IllegalArgumentException("cannot perform indexing: file "
					+ file.getBareName() + " does not exist");
		}
	}

	private IIndexer getIndexerFor(IFileElementType<? extends IRodinFile> fileType) {
		final List<IIndexer> list = indexers.get(fileType);
		if (list == null || list.isEmpty()) {
			throw new IllegalStateException("unable to find an indexer for "
					+ fileType);
		}
		// TODO manage indexers priorities
		return list.get(0);
	}

	private void clean(IRodinFile file, final RodinIndex index,
			final FileTable fileTable, final NameTable nameTable) {

		for (IInternalElement element : fileTable.getElements(file)) {
			final Descriptor descriptor = (Descriptor) index
					.getDescriptor(element);
			final String name = descriptor.getName();
			nameTable.remove(name, element);

			descriptor.removeOccurrences(file);

			if (descriptor.getOccurrences().length == 0) {
				index.removeDescriptor(element);
			}
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
