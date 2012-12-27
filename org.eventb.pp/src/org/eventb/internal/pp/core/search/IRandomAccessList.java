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

import java.util.ConcurrentModificationException;

/**
 * An ordered list of objects. This list can be iterated using a special iterator,
 * called a {@link ResetIterator}. This iterator allows the list to be modified 
 * through this interface while being iterated. This iterator will never throw a
 * {@link ConcurrentModificationException}.
 *
 * This list does not allow duplicates. This list does not accept <code>null</code>
 * references.
 * 
 * @author François Terrier
 *
 * @param <T>
 */
public interface IRandomAccessList<T extends Object> extends Iterable<T> {

	/**
	 * Removes the corresponding object from the list. Returns the
	 * object that was removed or <code>null</code> if the list does
	 * not contain the object.
	 * <p>
	 * More specifically, this removes and returns the object o such that <code>
	 * o.equals(object) == true</code>.
	 * 
	 * @param object the object to remove
	 * @return the object removed or <code>null</code>
	 */
	T remove(T object);
	
	/**
	 * Retrieves the given object from the list. Returns the object
	 * that is in the list or <code>null</code> if the object is not in
	 * the list.
	 * <p>
	 * More specifically, this returns the object o such that <code>
	 * o.equals(object) == true</code>.
	 * 
	 * @param object the object to retrieve
	 * @return the retrieved object or <code>null</code>
	 */
	T get(T object);
	
	/**
	 * Adds the given object to the end of this list. If this list
	 * already contains the object, the list remains unchanged.
	 * 
	 * @param object the object to add
	 */
	void add(T object);
	
	/**
	 * Return <code>true</code> if this list contains the specified
	 * object or <code>false</code> otherwise.
	 * <p>
	 * More formally, returns <tt>true</tt> if and only if this list contains
     * the object <tt>o</tt> such that
     * <tt>o.equals(object)</tt>.
     * 
	 * @param object object whose presence in the list is to be tested
	 * @return <code>true</code> if this list contains the object, <code>false</code> otherwise
	 */
	boolean contains(T object);
	
	/**
     * Returns a reset iterator of the elements in this list (in proper
     * sequence).
     * <p>
     * This iterator does not throw a {@link ConcurrentModificationException} 
     * when the list is modified while iterating. 
	 * 
	 * @return a reset iterator of the elements in this list (in proper
     * sequence)
	 */
	@Override
	ResetIterator<T> iterator();
	
    /**
     * Returns the number of elements in this list.  If this list contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list.
     */
    int size();
    
    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements.
     */
    boolean isEmpty();
}
