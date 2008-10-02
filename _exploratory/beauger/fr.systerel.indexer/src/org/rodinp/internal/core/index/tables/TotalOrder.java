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
package org.rodinp.internal.core.index.tables;

import java.util.Iterator;
import java.util.List;

/**
 * Stores and maintains a total order in a set of T objects.
 * <p>
 * Those objects are added to the order via {@link #setToIter(Object)} and
 * {@link #setPredecessors(Object, Object[])} methods, there is no specific
 * adding method.
 * <p>
 * The order implements Iterator, thus allowing the user to scan it in a
 * sequential manner in the computed total order.
 * <p>
 * Note that only nodes explicitly set to iter will be iterated. After an
 * iteration has finished, the user is required to call {@link #end()}, which
 * resets the nodes set to iter and allows for a future iteration. This requires
 * to set the nodes to iter before each new iteration.
 * <p>
 * This implementation allows for modifications during iteration. Modifications
 * will make the iteration restart to the first order difference.
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
	
	public void setPredecessors(T label, T[] predecessors) {
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

	// successors of the current node will be iterated
	// FIXME what is the meaning just after remove() ? decide and test
	public void setToIterSuccessors() { 
		sortedNodes.setToIterSuccessors();
	}

	/**
	 * Resets iteration. Resets nodes set to iter. Must be called at the end of
	 * each iteration. Thus, each iteration must be preceded with the setting of
	 * nodes to iter.
	 */
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

}
