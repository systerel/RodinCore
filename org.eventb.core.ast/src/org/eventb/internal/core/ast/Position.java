/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Arrays;

import org.eventb.core.ast.IPosition;

/**
 * Implementation of a position in a formula. A position denotes a node in a
 * formula tree. By extension, this node is also sometimes considered as a
 * sub-tree, that sub-tree which is rooted at this node.
 * 
 * @author Laurent Voisin
 */
public final class Position implements IPosition {
	
	public static IPosition getRoot() {
		return new Position();
	}

	public final int[] indexes;
	
	private Position() {
		this.indexes = new int[0];
	}
	
	private Position(int[] indexes) {
		this.indexes = indexes;
	}

	public Position(IntStack stack) {
		this.indexes = stack.toArray();
	}

	public int compareTo(IPosition position) {
		final Position other = (Position) position;
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
	
	public IPosition getParent() {
		final int parentLength = indexes.length - 1;
		final int[] parentIndexes = new int[parentLength];
		System.arraycopy(indexes, 0, parentIndexes, 0, parentLength);
		return new Position(parentIndexes);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(indexes);
	}

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
