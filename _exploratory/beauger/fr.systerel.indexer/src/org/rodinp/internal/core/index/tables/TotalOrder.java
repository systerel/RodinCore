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

import static java.lang.Math.min;

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
public class TotalOrder<T> implements Iterator<T> {

	// FIXME impossible to make a graph with only one node
	// consider using setToIter(T) for this purpose
	
	private final Map<T, Node<T>> graph;
	private final List<Node<T>> order;
	private Iterator<Node<T>> iter;
	private Node<T> currentNode;
	private boolean isSorted;
	private boolean restartIter;
	private int restartPos;
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
		this.restartIter = true;
		this.restartPos = -1;
		this.numberToIter = 0;
	}

	public void setPredecessors(T label, T[] predecessors) {
		final Node<T> node = fetchNode(label);

		final List<Node<T>> prevPreds = node.getPredecessors();
		final List<Node<T>> newPreds = new ArrayList<Node<T>>();
		for (T pred : predecessors) {
			newPreds.add(fetchNode(pred));
		}

		final List<Node<T>> toRemove = new ArrayList<Node<T>>(prevPreds);
		toRemove.removeAll(newPreds);
		final List<Node<T>> toAdd = new ArrayList<Node<T>>(newPreds);
		toAdd.removeAll(prevPreds);

		if (!(toRemove.isEmpty() && toAdd.isEmpty())) { // there are changes

			processPredChanges(node, toRemove, toAdd);
		}
	}

	private void processPredChanges(final Node<T> node,
			final List<Node<T>> toRemove, final List<Node<T>> toAdd) {
		int minPos = node.getOrderPos();
		for (Node<T> addNode : toAdd) {
			node.addPredecessor(addNode);

			minPos = getMinRestartPos(minPos, addNode);
		}
		for (Node<T> remNode : toRemove) {
			node.removePredecessor(remNode);
			minPos = getMinRestartPos(minPos, remNode);
		}

		if (currentNode != null) { // iterating
			isSorted = false;
			restartIter = true;
		}
	}

	private int getMinRestartPos(int pos, Node<T> node) {
		if (alreadyIterated(node) && node.isMarked()) {
			pos = min(pos, node.getOrderPos());
		}
		return pos;
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
		restartIter = true;
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
		restartIter = true;
		restartPos = -1;
		numberToIter = 0;
	}

	public boolean hasNext() {
		updateIter();

		return numberToIter > 0;
	}

	public T next() {
		updateIter();
		if (numberToIter <= 0) {
			throw new NoSuchElementException("No more elements to iter.");
		}

		currentNode = nextMarked(iter);
		numberToIter--;
		return currentNode.getLabel();
	}

	private void updateIter() {
		if (!isSorted) {
			sort();
		}
		if (restartIter) {
			initIter();
		}
	}

	public void remove() {
		iter.remove();
		if (currentNode != null) {
			remove(currentNode);
		}
	}

	public void setToIter(T label) {
		final Node<T> node = graph.get(label);
		if (node == null) {
			return;
		}
		setToIter(node);
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

	private void setToIter(Node<T> node) {
		if (!node.isMarked()) {
			node.setMark(true);
			if (alreadyIterated(node)) {
				restartIter = true;
			} else {
				numberToIter++;
			}
		}
	}

	public void setToIterNone() {
		if (currentNode != null) { // iterating => restart
			restartIter = true;
		}
		for (T label : graph.keySet()) {
			final Node<T> node = graph.get(label);
			node.setMark(false);
		}
		numberToIter = 0;
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

		initDegrees(degrees, zeroDegrees, remaining);
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

		isSorted = true;
	}

	// restartIter == true
	// restartPos is set
	private void initIter() {
		currentNode = null;
		iter = getIterator(restartPos);
		numberToIter = markedCount(restartPos);
		restartIter = false;
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

	private void initDegrees(Degrees<T> degrees, List<Node<T>> zeroDegrees,
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

	private void setOrderPos() {
		int pos = 0;

		for (Node<T> node : order) {
			node.setOrderPos(pos);
			pos++;
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
}
