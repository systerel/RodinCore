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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.rodinp.internal.core.indexer.persistence.PersistentSortedNodes;

/**
 * @author Nicolas Beauger
 * 
 */
public class SortedNodes<T> implements Iterator<T> {

	private final List<Node<T>> order;
	private final List<T> iterated;
	private Iterator<Node<T>> iter;
	private boolean startIter;
	private int restartPos;
	private Node<T> currentNode;
	private int numberToIter;

	public SortedNodes() {
		this.order = new ArrayList<Node<T>>();
		this.iterated = new ArrayList<T>();
		this.iter = null;
		this.startIter = true;
		this.restartPos = 0;
		this.currentNode = null;
		this.numberToIter = 0;
	}

	public void clear() {
		order.clear();
		iterated.clear();
		iter = null;
		startIter = true;
		restartPos = 0;
		currentNode = null;
		numberToIter = 0;
	}

	public void sort(Collection<Node<T>> nodes) {

		final boolean iterating = (currentNode != null);

		order.clear();
		final Sorter<T> sorter = new Sorter<T>(nodes);
		order.addAll(sorter.sort());

		if (iterating) {
			restartPos = findRestartPos();
		} else {
			restartPos = 0;
		}
	}

	private int findRestartPos() {

		Iterator<T> iterPrev = iterated.listIterator();
		Iterator<Node<T>> iterNew = order.listIterator();
		int pos = 0;

		while (iterNew.hasNext() && iterPrev.hasNext()) {
			final T labelPrev = iterPrev.next();
			final Node<T> nodeOrder = nextMarked(iterNew);

			if (nodeOrder == null) {
				break;
			}
			if (!nodeOrder.getLabel().equals(labelPrev)) {
				break;
			}
			pos++;
		}
		return pos;
	}

	public void start() {
		startIter = true;
		iterated.clear();
	}

	public boolean hasNext() {
		updateIter();

		return moreToIter();
	}

	public T next() {
		updateIter();

		if (!moreToIter()) {
			throw new NoSuchElementException("No more elements to iter.");
		}

		numberToIter--;
		currentNode = nextMarked(iter);
		final T label = currentNode.getLabel();
		iterated.add(label);
		return label;
	}

	public void remove() {
		iter.remove();
	}

	// successors of the current node will be iterated
	public void setToIterSuccessors() {
		if (currentNode == null) {
			throw new IllegalStateException("not iterating");
		}
		for (Node<T> node : currentNode.getSuccessors()) {
			if (node.isAfter(currentNode)) { // false if a cycle was broken
				setToIter(node);
			} // else the successor is ignored
		}
	}

	public void setToIter(Node<T> node) {
		if (!node.isMarked()) {
			if (currentNode != null && currentNode.isAfter(node)) {
				// iterating and the node was skipped (not marked)
				startIter = true;
			}
			node.setMark(true);
			numberToIter++;
		}
	}

	public Node<T> getCurrentNode() {
		return currentNode;
	}

	private boolean moreToIter() {
		return numberToIter > 0;
	}

	private void updateIter() {
		if (startIter) {
			currentNode = null;
			iter = order.listIterator(restartPos);
			numberToIter = markedCount(restartPos);
			startIter = false;
		}
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

	/**
	 * Use only for persistence purposes.
	 * 
	 * @return data to save.
	 */
	protected PersistentSortedNodes<T> getPersistentData() {
		final List<T> psnIterated = new ArrayList<T>(iterated);
		final List<Node<T>> psnNodes = new ArrayList<Node<T>>(order);

		return new PersistentSortedNodes<T>(psnNodes, psnIterated);
	}

	protected void setPersistentData(PersistentSortedNodes<T> psn) {
		order.clear();
		order.addAll(psn.getNodes());

		iterated.clear();
		iterated.addAll(psn.getIterated());

		restartPos = findRestartPos();

		startIter = true;
		updateIter();
	}
}
