/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.search;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class ConditionIterator<T extends Object> implements Iterator<T> {

//	private Iterator<Iterator<T>> iterables;
	
	private Iterator<T> iterator;
	
//	// assumes that the Iterables are not modified during 
//	// the iteration. If it is the case, a ConcurrentModificationException
//	// should be thrown -> we assume that the iterables are not modified
//	// during the iteration
//	// TODO add a mechanism that throws a ConcurrentModificationException
//	// in case an iterable (which are IterableHashSet) is modified
//	public ConditionIterator(Iterator<Iterator<T>> iterables) {
//		assert iterables.hasNext();
//		
//		this.iterables = iterables;
//
//		this.current = this.iterables.next();
//	}
//	
//	public ConditionIterator(Iterator<T>... iterables) {
//		List<Iterator<T>> list = new ArrayList<Iterator<T>>();
//		for (Iterator<T> iterator : iterables) {
//			list.add(iterator);
//		}
// 		
//		this.iterables = list.iterator();
//
//		this.current = this.iterables.next();
//	}
	
	public ConditionIterator(Iterator<T> iterable) {
//		List<Iterator<T>> list = new ArrayList<Iterator<T>>();
//		list.add(iterable);
//		
//		this.iterables = list.iterator();
//
//		this.current = this.iterables.next();
		this.iterator = iterable;
	}
	
	public abstract boolean isSelected(T element);
	
	private T cache = null;
	
	public boolean hasNext() {
		if (cache == null) {
			nextClause();
		}
		return cache != null;
	}
	
	public T next() {
		T result;
		if (cache == null) {
			nextClause();
		}
		if (cache == null) {
			throw new NoSuchElementException();
		}
		else {
			result = cache;
			cache = null;
			return result;
		}
	}

//	private Iterator<T> current;
	
	public void nextClause() {
		assert cache == null;
		
		if (!iterator.hasNext()) cache = null;
		while (iterator.hasNext()) {
			T next = iterator.next();
			if (isSelected(next)) {
				cache = next;
				break;
			}
		}
	}
	
//	private void nextIterator() {
//		if (iterables.hasNext()) {
//			current = iterables.next();
//		}
//		else current = null;
//	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
