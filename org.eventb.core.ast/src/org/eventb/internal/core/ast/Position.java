/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added child indexes
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Arrays;
import java.util.regex.Pattern;

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

	private static final int[] NO_INTS = new int[0];
	
	private static final Pattern DOT_PATTERN = Pattern.compile("\\.");
	
	public final int[] indexes;
	
	private Position() {
		this.indexes = NO_INTS;
	}
	
	private Position(int[] indexes) {
		this.indexes = indexes;
	}

	public Position(IntStack stack) {
		this.indexes = stack.toArray();
	}

	public Position(String image) {
		if (image.length() == 0) {
			this.indexes = NO_INTS;
		} else {
			final String[] components = DOT_PATTERN.split(image, -1);
			final int length = components.length;
			this.indexes = new int[length];
			for (int i = 0; i < length; i++) {
				try {
					final int idx = Integer.parseInt(components[i]);
					if (idx < 0) {
						throw new IllegalArgumentException(
								"Negative index in position: " + image);
					}
					this.indexes[i] = idx;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid position: "
							+ image);
				}
			}
		}
	}

	@Override
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
	
	@Override
	public IPosition getParent() {
		final int parentLength = indexes.length - 1;
		if (parentLength < 0)
			throw new IllegalStateException("Root position has no parent");
		final int[] parentIndexes = new int[parentLength];
		System.arraycopy(indexes, 0, parentIndexes, 0, parentLength);
		return new Position(parentIndexes);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(indexes);
	}

	@Override
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

	@Override
	public Position getFirstChild() {
		return getChildAtIndex(0);
	}

	@Override
	public Position getNextSibling() {
		final int lastIdx = indexes.length - 1;
		if (lastIdx < 0)
			throw new IllegalStateException("Root position is not a sibling");
		final int[] newIndexes = indexes.clone();
		++ newIndexes[lastIdx];
		return new Position(newIndexes);
	}

	@Override
	public Position getPreviousSibling() {
		final int lastIdx = indexes.length - 1;
		if (lastIdx < 0)
			throw new IllegalStateException("Root position is not a sibling");
		final int[] newIndexes = indexes.clone();
		-- newIndexes[lastIdx];
		if (newIndexes[lastIdx] < 0)
			throw new IllegalStateException(
					"First child position has no previous sibling");
		return new Position(newIndexes);
	}

	@Override
	public boolean isFirstChild() {
		final int lastIdx = indexes.length - 1;
		if (lastIdx < 0)
			return false;
		return indexes[lastIdx] == 0;
	}

	@Override
	public Position getChildAtIndex(int n) {
		if (n < 0)
			throw new IllegalStateException("Negative child index " + n);
		final int length = indexes.length;
		final int[] childIndexes = new int[length + 1];
		System.arraycopy(indexes, 0, childIndexes, 0, length);
		childIndexes[length] = n;
		return new Position(childIndexes);
	}

	@Override
	public int getChildIndex() {
		final int lastIdx = indexes.length - 1;
		if (lastIdx < 0)
			throw new IllegalStateException("Root position has no child index");
		return indexes[lastIdx];
	}

}
