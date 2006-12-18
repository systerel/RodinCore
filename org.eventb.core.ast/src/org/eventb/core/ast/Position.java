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

	// Must be package restricted, so that the array is not modified by clients
	final int[] indexes;
	
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

	@Override
	public int hashCode() {
		return Arrays.hashCode(indexes);
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
