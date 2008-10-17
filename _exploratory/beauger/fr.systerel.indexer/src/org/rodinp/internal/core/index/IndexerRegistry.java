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

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.index.IIndexer;

public class IndexerRegistry {

	private final Map<IInternalElementType<?>, List<IIndexer>> indexers;

	public IndexerRegistry() {
		this.indexers = new HashMap<IInternalElementType<?>, List<IIndexer>>();
	}

	public void addIndexer(IIndexer indexer, IInternalElementType<?> fileType) {
		List<IIndexer> list = indexers.get(fileType);
		if (list == null) {
			list = new ArrayList<IIndexer>();
			indexers.put(fileType, list);
		}
		list.add(indexer);
	}

	public IIndexer getIndexerFor(IInternalElementType<?> fileType) {
		final List<IIndexer> list = indexers.get(fileType);

		if (list == null || list.isEmpty()) {
			throw new IllegalArgumentException("No known indexers for file type: "
					+ fileType);
		}
		// TODO manage indexers priorities
		return list.get(0);
	}

	public boolean isIndexable(IInternalElementType<?> fileType) {
		return indexers.containsKey(fileType);
	}
	
	public void clear() {
		indexers.clear();
	}

}