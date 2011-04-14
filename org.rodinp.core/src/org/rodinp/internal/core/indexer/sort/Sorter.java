/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
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

public class Sorter<T, N extends INode<T, N>> {

	private static class Degrees<T, N extends INode<T, N>> {

		private final Map<INode<T, N>, Integer> degrees;

		public Degrees() {
			degrees = new HashMap<INode<T, N>, Integer>();
		}

		public void set(INode<T, N> node, int degree) {
			degrees.put(node, degree);
		}

		public int decr(N node) {
			Integer degree = degrees.get(node);
			if (degree == null) {
				return -1;
			}
			degree--;
			degrees.put(node, degree);
			return degree;
		}
	}

	private final Collection<N> nodes;

	public Sorter(Collection<N> nodes) {
		this.nodes = nodes;
	}

	public List<N> sort() {
		final Degrees<T, N> degrees = new Degrees<T, N>();
		final List<N> zeroDegrees = new ArrayList<N>();
		final List<N> remaining = new ArrayList<N>();
		final List<N> order = new ArrayList<N>();

		initDegrees(degrees, zeroDegrees, remaining);
		while (!remaining.isEmpty()) {

			order.addAll(topoSort(degrees, zeroDegrees, remaining));

			if (!remaining.isEmpty()) { // there are cycles => break them
				final N minDegree = findMinDegree(remaining);
				zeroDegrees.add(minDegree);
				// The cycle is only virtually broken, the graph is not modified
				// because the client may later break it himself from elsewhere.
			}
		}
		setOrderPos(order);

		return order;
	}

	private void initDegrees(Degrees<T, N> degrees, List<N> zeroDegrees,
			List<N> remaining) {
		for (N node : nodes) {
			remaining.add(node);
			final int degree = node.degree();
			degrees.set(node, degree);
			if (degree == 0) {
				zeroDegrees.add(node);
			}
		}
	}

	private List<N> topoSort(Degrees<T, N> degrees,
			List<N> zeroDegrees, List<N> remaining) {

		final List<N> order = new ArrayList<N>();

		while (!zeroDegrees.isEmpty()) {
			final N node = zeroDegrees.get(0);
			order.add(node);
			node.setOrderPos(order.size());
			zeroDegrees.remove(0);
			remaining.remove(node);
			for (N succ : node.getSuccessors()) {
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

	private N findMinDegree(List<N> remaining) {
		N minDegNode = null;
		int minDegree = Integer.MAX_VALUE;
		for (N node : remaining) {
			final int degree = node.degree();
			if (degree < minDegree) {
				minDegree = degree;
				minDegNode = node;
			}
		}
		return minDegNode;
	}

	private void setOrderPos(List<N> order) {
		int pos = 0;

		for (N node : order) {
			node.setOrderPos(pos);
			pos++;
		}
	}

}
