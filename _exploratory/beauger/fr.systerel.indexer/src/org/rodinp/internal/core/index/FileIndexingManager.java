package org.rodinp.internal.core.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;

public class FileIndexingManager {

	private static final IRodinFile[] EMPTY_DEPS = new IRodinFile[] {};
	
	private Map<IFileElementType<?>, List<IIndexer>> indexers;

	public FileIndexingManager() {
		this.indexers = new HashMap<IFileElementType<?>, List<IIndexer>>();
	}

	public void addIndexer(IIndexer indexer, IFileElementType<?> fileType) {
		List<IIndexer> list = indexers.get(fileType);
		if (list == null) {
			list = new ArrayList<IIndexer>();
			indexers.put(fileType, list);
		}
		list.add(indexer);
	}
	
	public boolean isIndexable( IFileElementType<?> fileType) {
		return indexers.containsKey(fileType);
	}
	
	public IRodinFile[] getDependencies(IRodinFile file) {
		final IIndexer indexer = getIndexerFor(file.getElementType());

		if (indexer == null) {
			return EMPTY_DEPS;
		}
		final IRodinFile[] result = indexer.getDependencies(file);
		return result;
	}
	
	public void launchIndexing(IRodinFile file, IIndexingToolkit indexingToolkit) {
		final IIndexer indexer = getIndexerFor(file.getElementType());

		if (indexer != null) {
			indexer.index(file, indexingToolkit);
		}
	}
	
	private IIndexer getIndexerFor(
			IFileElementType<? extends IRodinFile> fileType) {
		final List<IIndexer> list = indexers.get(fileType);
		if (list == null || list.isEmpty()) {
			return null;
		}
		// TODO manage indexers priorities
		return list.get(0);
	}

	public void clear() {
		indexers.clear();
	}
}
