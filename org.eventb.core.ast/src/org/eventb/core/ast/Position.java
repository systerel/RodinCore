/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Arrays;

import org.eventb.internal.core.ast.IntStack;

/**
 * Implementation of a position in a formula. A position denotes a node in a
 * formula tree. By extension, this node is also sometimes considered as a
 * sub-tree, that sub-tree which is rooted at this node.
 * 
 * @author Laurent Voisin
 */
public final class Position implements Comparable<Position> {
	
	private static int[] NO_INTS = new int[0]; 
	
	/**
	 * The position of the root node of a formula.
	 */
	public static final Position ROOT = new Position(NO_INTS);

	// Package restricted, so that the array is not modified by clients
	final int[] indexes;
	
	private Position(int[] indexes) {
		this.indexes = indexes;
	}
	
	Position(IntStack stack) {
		this.indexes = stack.toArray();
	}

	/**
	 * Compares this position with the given position, using the lexicographical
	 * order.
	 * 
	 * @return a negative integer, zero, or a positive integer as this position
	 *         is less than, equal to, or greater than the specified position.
	 */
	public int compareTo(Position other) {
		final int leftLen = this.indexes.length;
		final int rightLen = other.indexes.length;
		final int minLen = Math.min(leftLen, rightLen);
		for (int i = 0; i < minLen; i++) {
			final int diff = this.indexes[i] - other.indexes[i];
			if (diff != 0) {
				return diff;
			}
		}
		return leftLen - rightLen;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Position other = (Position) obj;
		return Arrays.equals(indexes, other.indexes);
	}

	/**
	 * Returns the position of the parent node of the node designated by this
	 * position.
	 * <p>
	 * This position must not be a root position.
	 * </p>
	 * 
	 * @return the position of the parent
	 * @see #isRoot()
	 */
	public Position getParent() {
		final int parentLength = indexes.length - 1;
		final int[] parentIndexes = new int[parentLength];
		System.arraycopy(indexes, 0, parentIndexes, 0, parentLength);
		return new Position(parentIndexes);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(indexes);
	}
	
	/**
	 * Tells whether this position denotes the root of a formula.
	 * 
	 * @return <code>true</code> iff this position denotes the root of a
	 *         formula
	 */
	public boolean isRoot() {
		return indexes.length == 0;
	}

	@Override
	public String toString() {
		final int length = indexes.length;
		if (length == 0) {
			return "";
		}
		if (length == 1) {
			return Integer.toString(indexes[0]);
		}
		StringBuilder result = new StringBuilder();
		String sep = "";
		for (int index : indexes) {
			result.append(sep);
			sep = ".";
			result.append(index);
		}
		return result.toString();
	}

}
