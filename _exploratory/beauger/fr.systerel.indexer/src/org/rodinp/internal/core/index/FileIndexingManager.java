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
	
	public IRodinFile[] getDependencies(IRodinFile file) {
		final IIndexer indexer = getIndexerFor(file.getElementType());

		final IRodinFile[] result = indexer.getDependencies(file);

		return result;
	}
	
	public void launchIndexing(IRodinFile file, IIndexingToolkit indexingToolkit) {
		final IIndexer indexer = getIndexerFor(file.getElementType());

		indexer.index(file, indexingToolkit);
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

	public void clear() {
		indexers.clear();
	}
}
