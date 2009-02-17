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
package org.rodinp.internal.core.indexer.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.rodinp.internal.core.indexer.persistence.PersistentSortedNodes;
import org.rodinp.internal.core.indexer.persistence.PersistentTotalOrder;

/**
 * Stores and maintains a total order in a set of T objects.
 * <p>
 * Those objects are added to the order via {@link #setToIter(Object)} and
 * {@link #setPredecessors(Object, Collection)} methods, there is no specific
 * adding method.
 * </p>
 * <p>
 * The order implements Iterator, thus allowing the user to scan it in a
 * sequential manner in the computed total order.
 * </p>
 * <p>
 * Note that only nodes explicitly set to iter will be iterated. After an
 * iteration has finished, the client is required to call {@link #end()}, which
 * resets the nodes set to iter and prepares for a future iteration. Thus, nodes
 * to iter must be set explicitly before each new iteration.
 * </p>
 * <p>
 * This implementation allows for modifications during iteration. Modifications
 * will make the iteration restart to the first order difference.
 * </p>
 * 
 * @author Nicolas Beauger
 * 
 */
public class TotalOrder<T> implements Iterator<T> {

	private final Graph<T> graph;
	private final SortedNodes<T> sortedNodes;
	boolean isSorted;

	private final IGraphChangedListener listener = new IGraphChangedListener() {
		public void graphChanged() {
			isSorted = false;
		}
	};

	public TotalOrder() {
		this.graph = new Graph<T>();
		this.sortedNodes = new SortedNodes<T>();
		this.isSorted = false;
		graph.addElementChangedListener(listener);
	}

	public void setToIter(T label) {
		final Node<T> node = graph.getOrCreateNode(label);
		sortedNodes.setToIter(node);
	}

	public List<T> getPredecessors(T label) {
		return graph.getPredecessors(label);
	}

	public void setPredecessors(T label, Collection<T> predecessors) {
		graph.setPredecessors(label, predecessors);
	}

	public void clear() {
		graph.clear();
		sortedNodes.clear();
		isSorted = false;
	}

	public boolean hasNext() {
		updateSort();

		return sortedNodes.hasNext();
	}

	public T next() {
		updateSort();

		return sortedNodes.next();
	}

	public void remove() {
		updateSort();

		graph.remove(sortedNodes.getCurrentNode());
		sortedNodes.remove();
	}

	// Sets successors of the current node to be iterated.
	// The result is unspecified if this method is called just after remove().
	public void setToIterSuccessors() {
		sortedNodes.setToIterSuccessors();
	}

	// Resets iteration. Resets nodes set to iter. Must be called at the end of
	// each iteration. Thus, each iteration must be preceded with the setting of
	// nodes to iter.
	public void end() {
		for (Node<T> node : graph.getNodes()) {
			node.setMark(false);
		}
		sortedNodes.start();
	}

	private void updateSort() {
		if (!isSorted) {
			sortedNodes.sort(graph.getNodes());
			isSorted = true;
			sortedNodes.start();
		}
	}

	// Use only for persistence purposes.
	public PersistentTotalOrder<T> getPersistentData() {
		if (isSorted) {
			final PersistentSortedNodes<T> sortData = sortedNodes
					.getPersistentData();
			return new PersistentTotalOrder<T>(true, sortData.getNodes(),
					sortData.getIterated());
		} else {
			final List<T> emptyList = Collections.emptyList();
			final List<Node<T>> nodes = new ArrayList<Node<T>>(graph.getNodes());

			return new PersistentTotalOrder<T>(false, nodes, emptyList);
		}
	}

	// Use only for persistence purposes.
	public void setPersistentData(PersistentTotalOrder<T> pto, Map<T, List<T>> predMap) {
		graph.setPersistentData(pto, predMap);
		if (pto.isSorted()) {
			final PersistentSortedNodes<T> psn =
					new PersistentSortedNodes<T>(pto.getNodes(), pto
							.getIterated());
			sortedNodes.setPersistentData(psn);
		} else {
			sortedNodes.clear();
		}
		isSorted = pto.isSorted();
	}
}
