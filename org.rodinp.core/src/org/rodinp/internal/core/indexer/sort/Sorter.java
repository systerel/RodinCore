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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sorter<T> {

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

	private final Collection<Node<T>> nodes;

	public Sorter(Collection<Node<T>> nodes) {
		this.nodes = nodes;
	}

	public List<Node<T>> sort() {
		final Degrees<T> degrees = new Degrees<T>();
		final List<Node<T>> zeroDegrees = new ArrayList<Node<T>>();
		final List<Node<T>> remaining = new ArrayList<Node<T>>();
		final List<Node<T>> order = new ArrayList<Node<T>>();

		initDegrees(degrees, zeroDegrees, remaining);
		while (!remaining.isEmpty()) {

			order.addAll(topoSort(degrees, zeroDegrees, remaining));

			if (!remaining.isEmpty()) { // there are cycles => break them
				final Node<T> minDegree = findMinDegree(remaining);
				zeroDegrees.add(minDegree);
				// The cycle is only virtually broken, the graph is not modified
				// because the client may later break it himself from elsewhere.
			}
		}
		setOrderPos(order);

		return order;
	}

	private void initDegrees(Degrees<T> degrees, List<Node<T>> zeroDegrees,
			List<Node<T>> remaining) {
		for (Node<T> node : nodes) {
			remaining.add(node);
			final int degree = node.degree();
			degrees.set(node, degree);
			if (degree == 0) {
				zeroDegrees.add(node);
			}
		}
	}

	private List<Node<T>> topoSort(Degrees<T> degrees,
			List<Node<T>> zeroDegrees, List<Node<T>> remaining) {

		final List<Node<T>> order = new ArrayList<Node<T>>();

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
		return order;
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

	private void setOrderPos(List<Node<T>> order) {
		int pos = 0;

		for (Node<T> node : order) {
			node.setOrderPos(pos);
			pos++;
		}
	}

}
