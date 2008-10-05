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
import org.rodinp.core.index.IIndexer;

public class IndexersRegistry {

	private final Map<IFileElementType<?>, List<IIndexer>> indexers;

	public IndexersRegistry() {
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

	public IIndexer getIndexerFor(IFileElementType<?> fileType) {
		final List<IIndexer> list = indexers.get(fileType);

		if (list == null || list.isEmpty()) {
			throw new IllegalArgumentException("No known indexers for file type: "
					+ fileType);
		}
		// TODO manage indexers priorities
		return list.get(0);
	}

	public boolean isIndexable( IFileElementType<?> fileType) {
		return indexers.containsKey(fileType);
	}
	
	public void clear() {
		indexers.clear();
	}

}