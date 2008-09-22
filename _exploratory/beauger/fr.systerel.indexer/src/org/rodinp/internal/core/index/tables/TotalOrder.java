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
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Nicolas Beauger
 * 
 */
public class TotalOrder<T> implements Iterator<T> {

	private final Map<T, Node<T>> graph;
	private final List<Node<T>> order;
	private Iterator<Node<T>> iter;
	private Node<T> currentNode;
	private boolean isSorted;
	private int numberToIter;

	/**
	 * 
	 */
	public TotalOrder() {
		this.graph = new HashMap<T, Node<T>>();
		this.order = new ArrayList<Node<T>>();
		this.iter = null;
		this.currentNode = null;
		this.isSorted = false;
		this.numberToIter = 0;
	}

	public void setPredecessors(T label, T[] predecessors) {
		final Node<T> node = fetchNode(label);

		if (alreadyIterated(node)) {
			isSorted = false;
		}

		final List<Node<T>> currentPred = node.getPredecessors();
		for (T pred : predecessors) {
			final Node<T> n = fetchNode(pred);
			if (currentPred.contains(n)) {
				currentPred.remove(n);
			} else {
				node.addPredecessor(n);
				if (alreadyIterated(n)) {
					isSorted = false;
				}
			}
		}

		if (!currentPred.isEmpty()) {
			for (Node<T> n : currentPred) {
				if (alreadyIterated(n)) {
					isSorted = false;
				}
				node.removePredecessor(n);
			}
		}
	}

	public boolean contains(T label) {
		return graph.containsKey(label);
	}

	private void remove(Node<T> node) {
		if (node.isMarked() && !alreadyIterated(node)) {
			numberToIter--;
		}
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
		iter = null;
		currentNode = null;
		isSorted = false;
		numberToIter = 0;
	}

	public boolean hasNext() {
		if (!isSorted) {
			sort();
		}
		return numberToIter > 0;
	}

	public T next() {
		if (!isSorted) {
			sort();
		}
		if (!this.hasNext()) {
			throw new NoSuchElementException("No more elements to iter.");
		}
		currentNode = nextMarked();
		numberToIter--;
		return currentNode.getLabel();
	}

	public void remove() {
		iter.remove();
		if (currentNode != null) {
			remove(currentNode);
		}
	}

	public void setToIter(T label) {
		Node<T> node = graph.get(label);
		if (node == null) {
			return;
		}
		if (!node.isMarked()) {
			node.setMark(true);
			numberToIter++;
		}
	}

	// successors of the current node will be iterated
	public void setToIterSuccessors() {
		if (currentNode == null) {
			return;
		}
		for (Node<T> node : currentNode.getSuccessors()) {
			if (node.isAfter(currentNode)) {
				// The edge to the successor hay have been virtually removed
				// to break a cycle
				node.setMark(true);
				numberToIter++;
			}
		}
	}

	// assumes hasNext
	private Node<T> nextMarked() {
		Node<T> node = iter.next();
		while (!node.isMarked() && iter.hasNext()) {
			node = iter.next();
		}
		return node;
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

		initDegrees(degrees, zeroDegrees, remaining);
		while (!remaining.isEmpty()) {

			sort(degrees, zeroDegrees, remaining);

			if (!remaining.isEmpty()) { // there are cycles => break them
				final Node<T> minDegree = findMinDegree(remaining);
				zeroDegrees.add(minDegree);
				// The cycle is only virtually broken, the graph is not modified
				// because the user may later break it himself from elsewhere.
			}
		}
		setOrderPos();
		iter = order.iterator();
		isSorted = true;
	}

	private void sort(Degrees<T> degrees, List<Node<T>> zeroDegrees,
			Set<Node<T>> remaining) {
		while (!zeroDegrees.isEmpty()) {
			final Node<T> node = zeroDegrees.get(0);
			order.add(node);
			node.setOrderPos(order.size());
			zeroDegrees.remove(0);
			remaining.remove(node);
			for (Node<T> succ : node.getSuccessors()) {
				if (remaining.contains(succ)) {
					final int degree = degrees.decr(succ);
					if (degree == 0) {
						zeroDegrees.add(succ);
					}
				}
			}
		}
	}

	private void initDegrees(Degrees<T> degrees, List<Node<T>> zeroDegrees,
			Set<Node<T>> remaining) {
		for (T label : graph.keySet()) {
			final Node<T> node = graph.get(label);
			remaining.add(node);
			final int degree = node.degree();
			degrees.set(node, degree);
			if (degree == 0) {
				zeroDegrees.add(node);
			}
		}
	}

	private void setOrderPos() {
		int pos = 0;
		
		for (Node<T> node : order) {
			node.setOrderPos(pos);
			pos++;
		}
	}

	private Node<T> findMinDegree(Set<Node<T>> remaining) {
		Node<T> minDegNode = null;
		int minDegree = Integer.MAX_VALUE;
		for (Node<T> node : remaining) {
			final int degree = node.degree();
			if (degree < minDegree) {
				minDegree = degree;
				minDegNode = node;
			}
		}
		return minDegNode;
	}

	private Node<T> fetchNode(T label) {
		Node<T> node = graph.get(label);

		if (node == null) {
			node = new Node<T>(label);
			graph.put(label, node);
		}
		return node;
	}

	// true iff already iterated or being iterated
	private boolean alreadyIterated(Node<T> node) {
		return !(currentNode == null || node.isAfter(currentNode));
	}
}
