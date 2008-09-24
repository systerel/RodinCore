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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Nicolas Beauger
 *
 */
public class SortedNodes<T> implements Iterator<T> {

	private final List<Node<T>> order;
	private Iterator<Node<T>> iter;
	private boolean startIter;
	private int restartPos;
	private Node<T> currentNode;
	private int numberToIter;

	public SortedNodes() {
		this.order = new ArrayList<Node<T>>();
		this.iter = null;
		this.startIter = true;
		this.restartPos = 0;
		this.currentNode = null;
		this.numberToIter = 0;
	}
	
	public void clear() {
		order.clear();
		iter = null;
		startIter = true;
		restartPos = 0;
		currentNode = null;
		numberToIter = 0;
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

	public void sort(Map<T, Node<T>> graph) {

		final boolean iterating = (currentNode != null);
		final List<Node<T>> previousOrder;
		if (iterating) { // do not copy the whole list if not necessary
			previousOrder = new ArrayList<Node<T>>(order);
		} else {
			previousOrder = new ArrayList<Node<T>>();
		}

		order.clear();
		final Degrees<T> degrees = new Degrees<T>();
		final List<Node<T>> zeroDegrees = new ArrayList<Node<T>>();
		final List<Node<T>> remaining = new ArrayList<Node<T>>();

		initDegrees(graph, degrees, zeroDegrees, remaining);
		while (!remaining.isEmpty()) {

			sort(degrees, zeroDegrees, remaining);

			if (!remaining.isEmpty()) { // there are cycles => break them
				final Node<T> minDegree = findMinDegree(remaining);
				zeroDegrees.add(minDegree);
				// The cycle is only virtually broken, the graph is not modified
				// because the client may later break it himself from elsewhere.
			}
		}

		setOrderPos();

		if (iterating) {
			final int iterPos = currentNode.getOrderPos(); // pos in new order
			restartPos = findRestartPos(previousOrder, iterPos);
		} else {
			restartPos = 0;
		}
	}

	private void initDegrees(Map<T, Node<T>> graph, Degrees<T> degrees, List<Node<T>> zeroDegrees,
			List<Node<T>> remaining) {
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

	private void sort(Degrees<T> degrees, List<Node<T>> zeroDegrees,
			List<Node<T>> remaining) {
	
		while (!zeroDegrees.isEmpty()) {
			// TODO prefer following previous order in zeroDegrees choice
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

	private Node<T> findMinDegree(List<Node<T>> remaining) {
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

	private void setOrderPos() {
		int pos = 0;
	
		for (Node<T> node : order) {
			node.setOrderPos(pos);
			pos++;
		}
	}

	// only considers differences in the order of marked nodes
	private int findRestartPos(List<Node<T>> previousOrder, int iterPos) {
		
		Iterator<Node<T>> iterOrder = order.listIterator();
		Iterator<Node<T>> iterPrev = previousOrder.listIterator();
		int pos = 0;
		while (iterOrder.hasNext() && iterPrev.hasNext()) {
			final Node<T> nodeOrder = nextMarked(iterOrder);
			final Node<T> nodePrev = nextMarked(iterPrev);
	
			if (nodeOrder == null || nodePrev == null) {
				break;
			}
			pos = nodeOrder.getOrderPos();
			if (pos > iterPos) {
				break;
			}
			if (!nodeOrder.equals(nodePrev)) {
				break;
			}
		}
		return pos;
	}

	public void start() { // TODO consider getting rid of it
		this.startIter = true;
	}
	
	public boolean hasNext() {
		updateIter();
		
		return numberToIter > 0;
	}

	public T next() {
		updateIter();
		
		if (!hasNext()) {
			throw new NoSuchElementException("No more elements to iter.");
		}

		numberToIter--;
		currentNode = nextMarked(iter);
		return currentNode.getLabel();
	}

	public void remove() {
		updateIter();
		
		iter.remove();
	}

	private void updateIter() {
		if (startIter) {
			currentNode = null;
			iter = getIterator(restartPos);
			numberToIter = markedCount(restartPos);
			startIter = false;
		}
	}

	private Iterator<Node<T>> getIterator(int index) {
		if (index < 0) {
			index = 0;
		}
		return order.listIterator(index);
	}

	// assumes order has been filled
	private int markedCount(int beginIndex) {
		int count = 0;
		Iterator<Node<T>> iterOrder = order.listIterator(beginIndex);
		while (iterOrder.hasNext()) {
			final Node<T> node = nextMarked(iterOrder);
			if (node == null) {
				break;
			}
			count++;
		}
		return count;
	}

	private Node<T> nextMarked(Iterator<Node<T>> iterator) {
		Node<T> node;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			node = iterator.next();
		} while (!node.isMarked());
		return node;
	}

	public Node<T> getCurrentNode() {
		return currentNode;
	}
	
	public void setToIter(Node<T> node) {

		if (!node.isMarked()) {
			if (alreadyIterated(node)) {
				startIter = true;
			}
			node.setMark(true);
			numberToIter++;
		}
	}

	// true iff already iterated or being iterated
	private boolean alreadyIterated(Node<T> node) {
		return !(currentNode == null || node.isAfter(currentNode));
	}

	// successors of the current node will be iterated
	public void setToIterSuccessors() {
		if (currentNode == null) {
			return;
		}
		for (Node<T> node : currentNode.getSuccessors()) {
			if (node.isAfter(currentNode)) { // false if a cycle was broken
				setToIter(node);
			} // else the successor is ignored
		}
	}

}
