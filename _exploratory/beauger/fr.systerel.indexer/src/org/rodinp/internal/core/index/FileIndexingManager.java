/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
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

	private static final IRodinFile[] EMPTY_DEPS = new IRodinFile[0];
	
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
		if (IndexManager.VERBOSE) {
			System.out.println("INDEXER: Extracting dependencies for file "
					+ file.getPath() + " with indexer " + indexer.getId());
		}
		final IRodinFile[] result = indexer.getDependencies(file);
		if (IndexManager.DEBUG) {
			System.out.println("INDEXER: Dependencies for file "
					+ file.getPath() + " are:");
			for (IRodinFile dep: result) {
				System.out.println("\t" + dep.getPath());
			}
		}
		return result;
	}
	
	public void doIndexing(IRodinFile file, IIndexingToolkit indexingToolkit) {
		final IIndexer indexer = getIndexerFor(file.getElementType());
		if (indexer == null) {
			return;
		}
		if (IndexManager.VERBOSE) {
			System.out.println("INDEXER: Indexing file "
					+ file.getPath() + " with indexer " + indexer.getId());
		}
		indexer.index(file, indexingToolkit);
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
