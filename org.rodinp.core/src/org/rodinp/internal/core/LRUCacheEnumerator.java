/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.LRUCacheEnumerator.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.Enumeration;

/**
 * The <code>LRUCacheEnumerator</code> returns its elements in the order they
 * are found in the <code>LRUCache</code>, with the most recent elements
 * first.
 * 
 * Once the enumerator is created, elements which are later added to the cache
 * are not returned by the enumerator. However, elements returned from the
 * enumerator could have been closed by the cache.
 */
public class LRUCacheEnumerator<V> implements Enumeration<V> {
	/**
	 * Current element;
	 */
	protected LRUEnumeratorElement<V> fElementQueue;

	protected static class LRUEnumeratorElement<VI> {
		/**
		 * Value returned by <code>nextElement()</code>;
		 */
		public VI fValue;

		/**
		 * Next element
		 */
		public LRUEnumeratorElement<VI> fNext;

		/**
		 * Constructor
		 */
		public LRUEnumeratorElement(VI value) {
			fValue = value;
		}
	}

	/**
	 * Creates a CacheEnumerator on the list of
	 * <code>LRUEnumeratorElements</code>.
	 */
	public LRUCacheEnumerator(LRUEnumeratorElement<V> firstElement) {
		fElementQueue = firstElement;
	}

	/**
	 * Returns true if more elements exist.
	 */
	@Override
	public boolean hasMoreElements() {
		return fElementQueue != null;
	}

	/**
	 * Returns the next element.
	 */
	@Override
	public V nextElement() {
		V temp = fElementQueue.fValue;
		fElementQueue = fElementQueue.fNext;
		return temp;
	}
}
