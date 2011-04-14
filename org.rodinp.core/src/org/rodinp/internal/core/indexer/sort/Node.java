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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Node<T> extends DefaultNode<T, Node<T>> {

	private boolean mark;
	private int orderPos;

	public Node(T label) {
		super(label);
		this.mark = false;
		this.orderPos = -1;
	}

	public List<Node<T>> getPredecessors() {
		return Collections.unmodifiableList(predecessors);
	}

	public void changePredecessors(List<Node<T>> newPredecessors) {
		clearPredecessors();
		for (Node<T> pred : newPredecessors) {
			addPredecessor(pred);
		}
	}

	public void addPredecessor(Node<T> tail) {
		if (this.equals(tail)) {
			throw new IllegalArgumentException(
					"Setting a node as self-predecessor: " + this);
		}
		if (!predecessors.contains(tail)) {
			this.predecessors.add(tail);
			tail.successors.add(this);
		}
	}

	public void removePredecessor(Node<T> tail) {
		this.predecessors.remove(tail);
		tail.successors.remove(this);
	}

	public void clear() {
		clearPredecessors();
		clearSuccessors();
		this.mark = false;
		this.orderPos = -1;
	}

	private void clearPredecessors() {
		final Iterator<Node<T>> iterPred = predecessors.iterator();
		while (iterPred.hasNext()) {
			final Node<T> pred = iterPred.next();
			pred.successors.remove(this);
			iterPred.remove();
		}
	}

	private void clearSuccessors() {
		final Iterator<Node<T>> iterSucc = successors.iterator();
		while (iterSucc.hasNext()) {
			final Node<T> succ = iterSucc.next();
			succ.predecessors.remove(this);
			iterSucc.remove();
		}
	}

	public void setMark(boolean value) {
		mark = value;
	}

	public boolean isMarked() {
		return mark;
	}

	public int getOrderPos() {
		return orderPos;
	}

	public void setOrderPos(int orderPos) {
		if (orderPos < 0) {
			this.orderPos = -1;
		} else {
			this.orderPos = orderPos;
		}
	}

	// false if both nodes have -1 as orderPos
	public boolean isAfter(Node<T> node) {
		return this.orderPos > node.orderPos;
	}

	@Override
	public String toString() {
		// return label.toString();
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(label);
		sb.append("; preds: ");
		for (Node<T> n : predecessors) {
			sb.append(n.label + " ");
		}
		sb.append("; succs: ");
		for (Node<T> n : successors) {
			sb.append(n.label + " ");
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return 31 + label.hashCode();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Node))
			return false;
		final Node<T> other = (Node<T>) obj;
		if (!label.equals(other.label))
			return false;
		return true;
	}

	public List<T> getPredecessorLabels() {
		final List<T> result = new ArrayList<T>();
		for (Node<T> pred : predecessors) {
			result.add(pred.getLabel());
		}
		return result;
	}
}
