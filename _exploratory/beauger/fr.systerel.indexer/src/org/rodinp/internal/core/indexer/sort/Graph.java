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
package org.rodinp.internal.core.indexer.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.rodinp.internal.core.indexer.persistence.PersistentTotalOrder;

public class Graph<T> {

	private final Map<T, Node<T>> nodes;
	private final List<IGraphChangedListener> listeners;

	private static <T> boolean sameList(List<T> left, T[] right) {
		if (left.size() != right.length)
			return false;
		if (!left.containsAll(Arrays.asList(right)))
			return false;
		return true;
	}

	public Graph() {
		this.nodes = new HashMap<T, Node<T>>();
		this.listeners = new CopyOnWriteArrayList<IGraphChangedListener>();
	}

	public void addElementChangedListener(IGraphChangedListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeElementChangedListener(IGraphChangedListener listener) {
		listeners.remove(listener);
	}

	public List<T> getPredecessors(T label) {
		final Node<T> node = nodes.get(label);
		if (node == null) {
			throw new IllegalArgumentException("no such label in graph: "
					+ label);
		}
		return node.getPredecessorLabels();
	}

	public void setPredecessors(T label, T[] predecessors) {
		final Node<T> node = getOrCreateNode(label);
		final List<T> oldPreds = node.getPredecessorLabels();
		if (sameList(oldPreds, predecessors)) {
			return;
		}
		final List<Node<T>> predNodes = getOrCreateNodes(predecessors);
		node.changePredecessors(predNodes);
		fireGraphChanged();
	}

	private List<Node<T>> getOrCreateNodes(T[] labels) {
		final List<Node<T>> result = new ArrayList<Node<T>>();
		for (T label : labels) {
			result.add(getOrCreateNode(label));
		}
		return result;
	}

	public void clear() {
		nodes.clear();
		fireGraphChanged();
	}

	public void remove(Node<T> node) {
		node.clear();
		nodes.remove(node.getLabel());
		fireGraphChanged();
	}

	public List<Node<T>> getNodes() {
		return new ArrayList<Node<T>>(nodes.values());
	}

	public Node<T> getOrCreateNode(T label) {
		Node<T> node = nodes.get(label);

		if (node == null) {
			node = new Node<T>(label);
			nodes.put(label, node);
			fireGraphChanged();
		}
		return node;
	}

	private void fireGraphChanged() {
		for (IGraphChangedListener listener : listeners) {
			listener.graphChanged();
		}
	}

	protected void setPersistentData(PersistentTotalOrder<T> pto) {
		final Map<T, List<T>> predMap = pto.getPredMap();
		nodes.clear();
		for (Node<T> n : pto.getNodes()) {
			final T label = n.getLabel();
			nodes.put(label, n);
			for (T pred : predMap.get(label)) {
				final Node<T> predNode = getOrCreateNode(pred);
				n.addPredecessor(predNode);
			}
		}
	}

}