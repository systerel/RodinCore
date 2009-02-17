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
package org.rodinp.internal.core.indexer.persistence;

import java.util.Collection;

import org.rodinp.internal.core.indexer.sort.Node;

/**
 * @author Nicolas Beauger
 * 
 */
public class PersistentTotalOrder<T> {

	private final boolean isSorted;
	// if isSorted then nodes are listed in total order
	private final Collection<Node<T>> nodes;
	private final Collection<T> iterated;

	public PersistentTotalOrder(boolean isSorted, Collection<Node<T>> nodes,
			Collection<T> iterated) {
		this.isSorted = isSorted;
		this.nodes = nodes;
		this.iterated = iterated;
	}

	public boolean isSorted() {
		return isSorted;
	}

	public Collection<Node<T>> getNodes() {
		return nodes;
	}

	public Collection<T> getIterated() {
		return iterated;
	}

}
