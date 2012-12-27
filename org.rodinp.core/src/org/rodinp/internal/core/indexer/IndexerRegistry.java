/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IIndexer;

public class IndexerRegistry {

	private static IndexerRegistry instance;
	private final Registry<IInternalElementType<?>, IndexerElement> indexers;

	private IndexerRegistry() {
		this.indexers = new Registry<IInternalElementType<?>, IndexerElement>();
	}

	public static IndexerRegistry getDefault() {
		if (instance == null) {
			instance = new IndexerRegistry();
		}
		return instance;
	}

	public void addIndexer(IConfigurationElement element, String indexerId,
			IInternalElementType<?> fileType) {
		final IndexerElement ind = new ContributedIndexer(element, indexerId);
		addIndexerElement(ind, fileType);
	}

	public void addIndexer(IIndexer indexer, IInternalElementType<?> fileType) {
		final IndexerElement ind = new InstantiatedIndexer(indexer);
		addIndexerElement(ind, fileType);
	}

	private void addIndexerElement(IndexerElement element,
			IInternalElementType<?> fileType) {
		indexers.add(fileType, element);
	}

	public List<IIndexer> getIndexersFor(IRodinFile file) {
		final IInternalElementType<? extends IInternalElement> fileType = file
				.getRootElementType();
		final List<IndexerElement> list = indexers.get(fileType);

		if (list == null || list.isEmpty()) {
			throw new IllegalArgumentException(
					"No known indexers for file type: " + fileType);
		}

		return makeIndexerList(list);
	}

	private List<IIndexer> makeIndexerList(List<IndexerElement> list) {
		final List<IIndexer> result = new ArrayList<IIndexer>();
		for (IndexerElement element : list) {
			final IIndexer indexer = element.getIndexer();
			if (indexer != null) {
				result.add(indexer);
			}
		}
		return result;
	}

	public boolean isIndexable(IRodinFile file) {
		final List<IndexerElement> list = indexers.get(file
				.getRootElementType());
		return list != null && !list.isEmpty();
	}

	public void clear() {
		indexers.clear();
	}

	public Registry<String, String> getPersistentData() {
		final Registry<String, String> result = new Registry<String, String>();
		for (Entry<IInternalElementType<?>, List<IndexerElement>> entry : indexers.entrySet()) {
			final IInternalElementType<?> rootType = entry.getKey();
			final String rootTypeId = rootType.getId();
			for (IndexerElement indexer : entry.getValue()) {
				result.add(rootTypeId, indexer.getIndexerId());
			}
		}
		return result;
	}
	
	public boolean sameAs(
			Registry<String, String> registry) {
		return getPersistentData().equals(registry);
	}
}