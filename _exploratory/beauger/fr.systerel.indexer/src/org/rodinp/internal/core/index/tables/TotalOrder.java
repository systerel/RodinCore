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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Nicolas Beauger
 * 
 */
public class TotalOrder<T> implements Iterator<T> {

	// TODO provide a markSuccessors() method to reindex dependents

	private final Map<T, Node<T>> graph;
	private final List<Node<T>> order;
	private Iterator<Node<T>> iter;
	private Node<T> currentNode;
	private boolean isSorted;

	/**
	 * 
	 */
	public TotalOrder() {
		this.graph = new HashMap<T, Node<T>>();
		this.order = new ArrayList<Node<T>>();
		this.iter = null;
		this.currentNode = null;
		this.isSorted = false;
	}

	public void setPredecessors(T label, T[] predecessors) {
		final Node<T> node = fetchNode(label);

		final List<Node<T>> currentPred = node.getPredecessors();
		for (T pred : predecessors) {
			final Node<T> n = fetchNode(pred);
			if (currentPred.contains(n)) {
				currentPred.remove(n);
			} else {
				node.addPredecessor(n);
				isSorted = false; // FIXME still true if not already iterated
			}
		}

		if (!currentPred.isEmpty()) {
			isSorted = false;
			for (Node<T> n : currentPred) {
				node.removePredecessor(n);
			}
		}
	}

	public boolean contains(T label) {
		return graph.containsKey(label);
	}

	private void remove(Node<T> node) {
		node.clear();
		graph.remove(node.getLabel());
		isSorted = false;
	}

	public void remove(T label) {
		final Node<T> node = graph.get(label);
		if (node == null) {
			return;
		}
		remove(node);
	}

	public void clear() {
		graph.clear();
		order.clear();
		this.iter = null;
		this.currentNode = null;
		isSorted = false;
	}

	public boolean hasNext() {
		if (!isSorted) {
			sort();
		}
		return iter.hasNext();
	}

	public T next() {
		if (!isSorted) {
			sort();
		}
		currentNode = iter.next();
		return currentNode.getLabel();
	}

	public void remove() {
		iter.remove();
		if (currentNode != null) {
			remove(currentNode);
		}
	}

	private static class Degrees<T> {

		private final Map<Node<T>, Integer> degrees;

		public Degrees() {
			degrees = new HashMap<Node<T>, Integer>();
		}

		public void set(Node<T> node, int degree) {
			degrees.put(node, degree);
		}

		public int decr(Node<T> node) {
			Integer degree = degrees.get(node);
			if (degree == null) {
				return -1;
			}
			degree--;
			degrees.put(node, degree);
			return degree;
		}
	}

	private void sort() {
		order.clear();
		final Degrees<T> degrees = new Degrees<T>();
		final List<Node<T>> zeroDegrees = new ArrayList<Node<T>>();
		final Set<Node<T>> remaining = new HashSet<Node<T>>();

		for (T label : graph.keySet()) {
			final Node<T> node = graph.get(label);
			remaining.add(node);
			final int degree = node.degree();
			degrees.set(node, degree);
			if (degree == 0) {
				zeroDegrees.add(node);
			}
		}
		while (remaining.size() > 0) {
			while (!zeroDegrees.isEmpty()) {
				final Node<T> node = zeroDegrees.get(0);
				order.add(node);
				zeroDegrees.remove(0);
				remaining.remove(node);
				for (Node<T> succ : node.getSuccessors()) {
					final int degree = degrees.decr(succ);
					if (degree == 0) {
						zeroDegrees.add(succ);
					}
				}
			}
			if (remaining.size() > 0) { // there are cycles => break them
				// FIXME temporary solution
				order.addAll(remaining);
				remaining.clear();
			}
		}
		iter = order.iterator();
		isSorted = true;
	}

	private Node<T> fetchNode(T label) {
		Node<T> node = graph.get(label);

		if (node == null) {
			node = new Node<T>(label);
			graph.put(label, node);
		}
		return node;
	}

}
