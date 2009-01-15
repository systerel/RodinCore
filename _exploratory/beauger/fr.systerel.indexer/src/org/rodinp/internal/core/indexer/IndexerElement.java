package org.rodinp.internal.core.indexer;

import org.rodinp.core.indexer.IIndexer;

public abstract class IndexerElement {

	protected final String indexerId;

	public IndexerElement(String indexerId) {
		this.indexerId = indexerId;
	}

	public String getIndexerId() {
		return indexerId;
	}

	public abstract IIndexer getIndexer();

	@Override
	public int hashCode() {
		return 31 + indexerId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ContributedIndexer other = (ContributedIndexer) obj;
		if (!indexerId.equals(other.indexerId))
			return false;
		return true;
	}
}