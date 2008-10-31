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

import java.util.List;
import java.util.Map;

import org.rodinp.internal.core.index.tables.Node;

/**
 * @author Nicolas Beauger
 *
 */
public class PersistentTotalOrder<T> {

	private final boolean isSorted;
	// if isSorted then nodes are listed in total order
	private final List<Node<T>> nodes;
	private final Map<T, List<T>> predMap;
	private final List<T> iterated;
	
	public PersistentTotalOrder(boolean isSorted, List<Node<T>> nodes,
			Map<T, List<T>> predMap, List<T> iterated) {
		this.isSorted = isSorted;
		this.nodes = nodes;
		this.predMap = predMap;
		this.iterated = iterated;
	}
	
	public boolean isSorted() {
		return isSorted;
	}
	public List<Node<T>> getNodes() {
		return nodes;
	}
	public List<T> getIterated() {
		return iterated;
	}

	public Map<T, List<T>> getPredMap() {
		return predMap;
	}
	
	
}
