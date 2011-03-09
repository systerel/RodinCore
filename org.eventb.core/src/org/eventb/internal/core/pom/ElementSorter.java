/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Implements an algorithm for sorting Rodin internal elements so that they
 * occur in exactly the given order. The order to use is expressed by repetitive
 * calls to {@link #addItem(IInternalElement)}.
 * 
 * @author Laurent Voisin
 */
public class ElementSorter<T extends IInternalElement> {
	
	public static interface Mover<T> {
		public void move(T element, T nextSibling) throws RodinDBException;
	}
	
	private static class Item<T extends IInternalElement> implements
			Comparable<Item<T>> {
		
		final int key;
		final T element;

		public Item(int key, T element) {
			this.key = key;
			this.element = element;
		}

		@Override
		public int compareTo(Item<T> o) {
			return this.key - o.key;
		}

		@Override
		public String toString() {
			return element.toString();
		}

		// Default implementations of equals and hashCode are OK, as items are
		// unique.
		
//		@Override
//		public int hashCode() {
//			return key;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null || obj.getClass() != Item.class)
//				return false;
//			final Item<?> other = (Item<?>) obj;
//			return this.key == other.key;
//		}
		
	}

	private int count = 0;
	
	private final List<Item<T>> items = new ArrayList<Item<T>>();
	
	private final Map<T, Item<T>> itemMap = new HashMap<T, Item<T>>();
	
	public void addItem(T element) {
		final Item<T> item = new Item<T>(count++, element);
		items.add(item);
		itemMap.put(element, item);
	}
	
	public void sort(T[] arrayToSort, Mover<T> mover) throws RodinDBException {
		final int length = arrayToSort.length;
		final int[] positions = new int[length];
		for (int i = 0; i < length; i++) {
			positions[i] = itemMap.get(arrayToSort[i]).key;
		}
		final int[] backbone = new LongestIncrSubseq(positions).result();
		int curPos = 0;
		for (final int bbPos: backbone) {
			moveUpto(curPos, bbPos, getElement(bbPos), mover);
			curPos = bbPos + 1;
		}
		moveUpto(curPos, length, null, mover);
	}

	private void moveUpto(int from, final int until, final T nextSibling,
			Mover<T> mover) throws RodinDBException {
		for (int i = from; i < until; ++i) {
			mover.move(getElement(i), nextSibling);
		}
	}

	private T getElement(final int pos) {
		if (pos == Integer.MAX_VALUE) {
			return null;
		}
		return items.get(pos).element;
	}

}
