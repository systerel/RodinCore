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
package org.rodinp.internal.core.index.persistence;

import java.util.Collection;

import org.rodinp.internal.core.index.IIndexDelta;
import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.Registry;

/**
 * @author Nicolas Beauger
 * 
 */
public class PersistentIndexManager {

	private final PerProjectPIM pppim;
	private final Collection<IIndexDelta> deltas;
	private final Registry<String, String> indexerIdRegistry;

	public PersistentIndexManager(PerProjectPIM pppim,
			Collection<IIndexDelta> deltas,
			Registry<String, String> indexerIdRegistry) {
		this.pppim = pppim;
		this.deltas = deltas;
		this.indexerIdRegistry = indexerIdRegistry;
	}

	public PerProjectPIM getPPPIM() {
		return pppim;
	}

	public Collection<IIndexDelta> getDeltas() {
		return deltas;
	}

	public Registry<String, String> getIndexerRegistry() {
		return indexerIdRegistry;
	}
	
	public void clear() {
		pppim.clear();
		deltas.clear();
		indexerIdRegistry.clear();
	}

}
