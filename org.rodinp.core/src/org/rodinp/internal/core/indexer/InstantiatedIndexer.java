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

import org.rodinp.core.indexer.IIndexer;

/**
 * @author Nicolas Beauger
 *
 */
public class InstantiatedIndexer extends IndexerElement {

	private final IIndexer instance;
	
	
	public InstantiatedIndexer(IIndexer instance) {
		super(instance.getId());
		this.instance = instance;
	}


	@Override
	public IIndexer getIndexer() {
		return instance;
	}

}
