/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.ArrayList;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;

/**
 * Stream of identifiers. Common protocol for merging sorted arrays of free
 * identifiers.
 * 
 * @author Laurent Voisin
 */
public abstract class IdentListMerger {
	
	protected final Class<?> itemClass;

	protected boolean errorFound = false;
	
	/**
	 * A string which is known to be greater than any identifier name.
	 */
	protected static final String infinity = "\uffff";

	protected IdentListMerger(Class<?> itemClass) {
		this.itemClass = itemClass;
	}
	
	/**
	 * Creates a merging stream for the given sorted arrays whose index lay
	 * between first (inclusive) and last (exclusive).
	 * 
	 * @param lists
	 * @param first
	 * @param last
	 */
	protected static IdentListMerger buildMerger(ArrayList<? extends Identifier[]> lists,
			int first, int last) {
		
		assert first < last;
		switch (last - first) {
		case 1:
			return new ArrayStream(lists.get(first));
		case 2:
			return new MergingStream(new ArrayStream(lists.get(first)),
					new ArrayStream(lists.get(first + 1)));
		default:
			final int mid = (first + last) / 2;
			return new MergingStream(buildMerger(lists, first, mid),
					buildMerger(lists, mid, last));
		}
	}

	/**
	 * Creates a merger for the given sorted arrays.
	 * 
	 * @param lists
	 *            non empty list of lists of free identifiers to merge
	 */
	public static IdentListMerger makeMerger(ArrayList<? extends Identifier[]> lists) {
		assert ! lists.isEmpty();
		return buildMerger(lists, 0, lists.size());
	}
	
	/**
	 * Creates a merger for the given sorted arrays.
	 * 
	 * @param list1
	 *            first list to merge
	 * @param list2
	 *            second list to merge
	 * @return a merger for the given lists of free identifiers
	 */
	public static <S extends Identifier> IdentListMerger makeMerger(S[] list1,
			S[] list2) {
	
		final int length1 = list1.length;
		if (length1 == 0) {
			return new ArrayStream(list2);
		}
		
		final int length2 = list2.length;
		if (length2 == 0) {
			return new ArrayStream(list1);
		}
		
		return new MergingStream(
				new ArrayStream(list1),
				new ArrayStream(list2)
		);
	}

	/**
	 * Reports whether an error has been encountered while merging.
	 * <p>
	 * An error is raised in one of the following cases:
	 * <ul>
	 * <li>An identifier is not typed.</li>
	 * <li>Two identifiers, bearing the same name, have different types.</li>
	 * </ul></p>
	 * 
	 * @return <code>true</code> if an error has been encountered while merging.
	 */
	public abstract boolean containsError();
	
	/**
	 * Returns the origin (an array) of one of the longest substream of this
	 * stream.
	 */
	protected abstract Identifier[] getMaximalArray();

	// Runs this merger and builds the resulting merged array
	public final FreeIdentifier[] getFreeMergedArray() {
		final int maxLength = this.getPotentialLength();
		FreeIdentifier[] result = new FreeIdentifier[maxLength];
		int length = buildMergedArray(result);
		
		// Check if we can reuse an already existing array
		FreeIdentifier[] maxArray = (FreeIdentifier[]) this.getMaximalArray();
		if (length == maxArray.length) {
			return maxArray;
		}
		
		// Adapt the size of the result
		if (length != maxLength) {
			FreeIdentifier[] temp = new FreeIdentifier[length];
			System.arraycopy(result, 0, temp, 0, length);
			result = temp;
		}
		return result;
	}

	public final BoundIdentifier[] getBoundMergedArray() {
		final int maxLength = this.getPotentialLength();
		BoundIdentifier[] result = new BoundIdentifier[maxLength];
		int length = buildMergedArray(result);
		
		// Check if we can reuse an already existing array
		BoundIdentifier[] maxArray = (BoundIdentifier[]) this.getMaximalArray();
		if (length == maxArray.length) {
			return maxArray;
		}
		
		// Adapt the size of the result
		if (length != maxLength) {
			BoundIdentifier[] temp = new BoundIdentifier[length];
			System.arraycopy(result, 0, temp, 0, length);
			result = temp;
		}
		return result;
	}

	private int buildMergedArray(Identifier[] result) {
		int index = 0;
		Identifier ident = this.getNext();
		while (ident != null) {
			result[index++] = ident;
			ident = this.getNext();
		}
		return index;
	}
	
	/**
	 * Returns the next identifier (advance the stream by one).
	 * <p>
	 * Returns <code>null</code> is this stream is exhausted.
	 * </p>
	 * 
	 * @return the name of the next identifier or <code>null</code>
	 */
	protected abstract Identifier getNext();

	/**
	 * Returns the maximal possible length of this stream, that is the size of
	 * the array to allocate to store the results of reading fully this stream.
	 * 
	 * @return the maximal possible length of this stream
	 */
	protected abstract int getPotentialLength();

}
