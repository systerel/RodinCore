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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;

public class IndexerRegistry {

	private static IndexerRegistry instance;
	private final Map<IInternalElementType<?>, List<IIndexer>> indexers;

	private IndexerRegistry() {
		this.indexers = new HashMap<IInternalElementType<?>, List<IIndexer>>();
	}

	public static IndexerRegistry getDefault() {
		if (instance == null) {
			instance = new IndexerRegistry();
		}
		return instance;
	}

	public void addIndexer(IIndexer indexer, IInternalElementType<?> fileType) {
		List<IIndexer> list = indexers.get(fileType);
		if (list == null) {
			list = new ArrayList<IIndexer>();
			indexers.put(fileType, list);
		}
		list.add(indexer);
	}

	public List<IIndexer> getIndexersFor(IRodinFile file) {
		final IInternalElementType<? extends IInternalElement> fileType =
				file.getRoot().getElementType();
		final List<IIndexer> list = indexers.get(fileType);

		if (list == null || list.isEmpty()) {
			throw new IllegalArgumentException(
					"No known indexers for file type: " + fileType);
		}
		// TODO manage indexers priorities
		return list;
	}

	public boolean isIndexable(IRodinFile file) {
		return indexers.containsKey(file.getRoot().getElementType());
	}

	public void clear() {
		indexers.clear();
	}

}