/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.search;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that allows to traverse the list while allowing
 * the list to be modified without throwing a {@link ConcurrentModificationException}.
 * This iterator allows to traverse the list in only one direction, and does
 * not allow elements to be removed through it.
 * <p>
 * A call to {@link #next()} never returns an element that does not currently
 * exist in the list. A call to {@link #next()} never throws a {@link NoSuchElementException}. 
 * 
 * @author François Terrier
 *
 * @param <T>
 */
public interface ResetIterator<T> extends Iterator<T> {

	/**
	 * Resets this iterator.
	 * <p>
	 * Puts this iterator back at the beginning of the list.
	 * 
	 * @throws IllegalStateException if the iterator has been invalidated
	 */
	void reset();
	
	/**
	 * Invalidates this iterator.
	 * <p>
	 * An iterator that is not used any more will not be garbage-collected unless
	 * a call to {@link #invalidate()} is issued. Once this method is called, the
	 * iterator cannot be used any more. Once this method has been called, all other
	 * method calls on this interface will throw an {@link IllegalStateException}.
	 */
	void invalidate();
	
    /**
     * Returns <tt>true</tt> if the iteration has more elements. Returns 
     * <code>false</code> if the iteration has no more elements.
     * <p>
     * The result of this call is valid until the underlying list is modified. For
     * instance, a call to {@link #hasNext()} on an iterator that is at the end
     * of the list will return <code>false</code> until an element
     * is added again to the end of the list.
     *
     * @return <tt>true</tt> if the iterator has more elements, <tt>false</tt> otherwise
     * @throws IllegalStateException if the iterator has been invalidated
     */
    @Override
	boolean hasNext();

    /**
     * Returns the next element in the iteration. Returns <code>null</code> if
     * the iterator is at the end of the list (i.e. if {@link #hasNext()} returns 
     * <code>false</code>). 
     *
     * @return the next element in the iteration.
     * @throws IllegalStateException if the iterator has been invalidated
     */
    @Override
	T next();
}
