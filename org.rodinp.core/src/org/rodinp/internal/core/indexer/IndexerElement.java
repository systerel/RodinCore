/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
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