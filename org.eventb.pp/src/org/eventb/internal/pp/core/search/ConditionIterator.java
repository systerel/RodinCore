/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.search;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator that wraps around any {@link Iterator} and returns only elements
 * that have certain properties, defined in {@link #isSelected(Object)}. It is
 * as if the iterator was an iterator over the subset of this list that have
 * those properties. Objects for which {@link #isSelected(Object)} returns <code>
 * false</code> are not returned by the iterator.
 * <p>
 * When used with a {@link ResetIterator}, a call to {@link #hasNext()} must be 
 * immediately followed by a call to {@link #next()}. Otherwise, if the underlying
 * {@link IRandomAccessList} is modified in between, the result of a call to
 * {@link #next()} might be inconsistent (because of the fact that a {@link ResetIterator}
 * allows the list to be modified).
 * 
 * @author François Terrier
 *
 * @param <T>
 */
public abstract class ConditionIterator<T extends Object> implements Iterator<T> {

	private final Iterator<T> iterator;
	private T cache = null;
	
	/**
	 * Creates a new {@link ConditionIterator} with the specified underlying
	 * iterator.
	 * 
	 * @param iterable
	 */
	public ConditionIterator(Iterator<T> iterable) {
		this.iterator = iterable;
	}
	
	/**
	 * Returns <code>true</code> if the element should occur
	 * in this iterator and <code>false</code> otherwise.
	 * 
	 * @param element the element for which we want to test the presence 
	 * in the iterator
	 * @return <code>true</code> if the element should occur in this iterator
	 *  and <code>false</code> otherwise
	 */
	public abstract boolean isSelected(T element);

	@Override
	public boolean hasNext() {
		if (cache == null) {
			nextObject();
		}
		return cache != null;
	}
	
	@Override
	public T next() {
		T result;
		if (cache == null) {
			nextObject();
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

	private void nextObject() {
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
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
