/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * Strongly inspired by org.eclipse.jdt.internal.core.OverflowingLRUCache.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.text.NumberFormat;

import org.rodinp.internal.core.util.LRUCache;
import org.rodinp.internal.core.util.Messages;

/**
 * The <code>OverflowingLRUCache</code> is an LRUCache which attempts to
 * maintain a size equal or less than its <code>fSpaceLimit</code> by removing
 * the least recently used elements.
 * 
 * <p>
 * The cache will remove elements which successfully close and all elements
 * which are explicitly removed.
 * </p>
 * <p>
 * If the cache cannot remove enough old elements to add new elements it will
 * grow beyond <code>fSpaceLimit</code>. Later, it will attempt to shrink back
 * to the maximum space limit.
 * </p>
 * <p>
 * The method <code>close</code> should attempt to close the element. If the
 * element is successfully closed it will return true and the element will be
 * removed from the cache. Otherwise the element will remain in the cache.
 * </p>
 * <p>
 * The cache implicitly attempts shrinks on calls to <code>put</code>and
 * <code>setSpaceLimit</code>. Explicitly calling the <code>shrink</code> method
 * will also cause the cache to attempt to shrink.
 * </p>
 * <p>
 * The used space of every element is one. Hence, the notion of used space is
 * actually a number of hardly cached elements.
 * </p>
 * <p>
 * Use the <code>#peek(Object)</code> and <code>#disableTimestamps()</code>
 * method to circumvent the timestamp feature of the cache. This feature is
 * intended to be used only when the <code>#close(LRUCacheEntry)</code> method
 * causes changes to the cache. For example, if a parent closes its children
 * when </code>#close(LRUCacheEntry)</code> is called, it should be careful not
 * to change the LRU linked list. It can be sure it is not causing problems by
 * calling <code>#peek(Object)</code> instead of <code>#get(Object)</code>
 * method.
 * </p>
 * 
 * @see LRUCache
 */
public abstract class OverflowingLRUCache<K, V> extends LRUCache<K, V> {
	/**
	 * Indicates if the cache has been over filled and by how much.
	 */
	protected int fOverflow = 0;

	/**
	 * Indicates whether or not timestamps should be updated
	 */
	protected boolean fTimestampsOn = true;

	/**
	 * Indicates how much space should be reclaimed when the cache overflows.
	 * Inital load factor of one third.
	 */
	protected double fLoadFactor = 0.333;

	/**
	 * Creates a OverflowingLRUCache.
	 * 
	 * @param size
	 *            Size limit of cache.
	 */
	public OverflowingLRUCache(int size) {
		this(size, 0);
	}

	/**
	 * Creates a OverflowingLRUCache.
	 * 
	 * @param size
	 *            Size limit of cache.
	 * @param overflow
	 *            Size of the overflow.
	 */
	public OverflowingLRUCache(int size, int overflow) {
		super(size);
		fOverflow = overflow;
	}

	/**
	 * Returns a new cache containing the same contents.
	 * 
	 * @return New copy of this object.
	 */
	@Override
	public OverflowingLRUCache<K,V> clone() {

		OverflowingLRUCache<K,V> newCache = newInstance(getSpaceLimit(), fOverflow);
		LRUCacheEntry<K,V> qEntry;

		/* Preserve order of entries by copying from oldest to newest */
		qEntry = this.fEntryQueueTail;
		while (qEntry != null) {
			newCache.privateAdd(qEntry._fKey, qEntry._fValue);
			qEntry = qEntry._fPrevious;
		}
		return newCache;
	}

	/**
	 * Returns true if the element is successfully closed and removed from the
	 * cache, otherwise false.
	 * 
	 * <p>
	 * NOTE: this triggers an external remove from the cache by closing the
	 * object.
	 * 
	 */
	protected abstract boolean close(LRUCacheEntry<K,V> entry);

	/**
	 * Returns an enumerator of the values in the cache with the most recently
	 * used first.
	 */
	public LRUCacheEnumerator<V> elements() {
		if (fEntryQueue == null)
			return new LRUCacheEnumerator<V>(null);
		LRUCacheEnumerator.LRUEnumeratorElement<V> head = new LRUCacheEnumerator.LRUEnumeratorElement<V>(
				fEntryQueue._fValue);
		LRUCacheEntry<K,V> currentEntry = fEntryQueue._fNext;
		LRUCacheEnumerator.LRUEnumeratorElement<V> currentElement = head;
		while (currentEntry != null) {
			currentElement.fNext = new LRUCacheEnumerator.LRUEnumeratorElement<V>(
					currentEntry._fValue);
			currentElement = currentElement.fNext;

			currentEntry = currentEntry._fNext;
		}
		return new LRUCacheEnumerator<V>(head);
	}

	public double fillingRatio() {
		return (getCurrentSpace() + fOverflow) * 100.0 / getSpaceLimit();
	}

	/**
	 * Returns the load factor for the cache. The load factor determines how
	 * much space is reclaimed when the cache exceeds its space limit.
	 * 
	 * @return double
	 */
	public double getLoadFactor() {
		return fLoadFactor;
	}

	/**
	 * @return The space by which the cache has overflown.
	 */
	public int getOverflow() {
		return fOverflow;
	}

	/**
	 * Ensures there is the specified amount of free space in the receiver, by
	 * removing old entries if necessary. Returns true if the requested space
	 * was made available, false otherwise. May not be able to free enough space
	 * since some elements cannot be removed until they are saved.
	 * 
	 * @param space
	 *            Amount of space to free up
	 */
	@Override
	protected boolean makeSpace(int space) {

		final int limit = getSpaceLimit();
		if (fOverflow == 0) {
			/* if space is already available */
			if (getCurrentSpace() + space <= limit) {
				return true;
			}
		}

		/* Free up space by removing oldest entries */
		int spaceNeeded = (int) ((1 - fLoadFactor) * limit);
		spaceNeeded = (spaceNeeded > space) ? spaceNeeded : space;
		LRUCacheEntry<K,V> entry = fEntryQueueTail;

		try {
			// disable timestamps update while making space so that the previous
			// and next links are not changed
			// (by a call to get(Object) for example)
			fTimestampsOn = false;

			while (getCurrentSpace() + spaceNeeded > limit && entry != null) {
				// FIXME don't remove: move to soft
				this.privateRemoveEntry(entry, false, false);
				entry = entry._fPrevious;
			}
		} finally {
			fTimestampsOn = true;
		}

		/* check again, since we may have aquired enough space */
		if (getCurrentSpace() + space <= limit) {
			fOverflow = 0;
			return true;
		}

		/* update fOverflow */
		fOverflow = getCurrentSpace() + space - limit;
		return false;
	}

	/**
	 * Returns a new instance of the receiver.
	 */
	protected abstract OverflowingLRUCache<K,V> newInstance(int newSize, int overflow);

	/**
	 * Answers the value in the cache at the given key. If the value is not in
	 * the cache, returns null
	 * 
	 * This function does not modify timestamps.
	 */
	public V peek(K key) {

		LRUCacheEntry<K,V> entry = cache.getEntry(key);
		if (entry == null) {
			return null;
		}
		return entry._fValue;
	}

	/**
	 * Removes the entry from the entry queue. Calls
	 * <code>privateRemoveEntry</code> with the external functionality
	 * enabled.
	 * 
	 * @param shuffle
	 *            indicates whether we are just shuffling the queue (in which
	 *            case, the entry table is not modified).
	 */
	@Override
	protected void privateRemoveEntry(LRUCacheEntry<K,V> entry, boolean shuffle) {
		privateRemoveEntry(entry, shuffle, true);
	}

	/**
	 * Removes the entry from the entry queue. If <i>external</i> is true, the
	 * entry is removed without checking if it can be removed. It is assumed
	 * that the client has already closed the element it is trying to remove (or
	 * will close it promptly).
	 * 
	 * If <i>external</i> is false, and the entry could not be closed, it is
	 * not removed and the pointers are not changed.
	 * 
	 * @param shuffle
	 *            indicates whether we are just shuffling the queue (in which
	 *            case, the entry table is not modified).
	 */
	protected void privateRemoveEntry(LRUCacheEntry<K,V> entry, boolean shuffle,
			boolean external) {

		if (!shuffle) {
			if (external) {
				cache.removeEntry(entry._fKey);
			} else {
				if (!close(entry))
					return;
				// buffer close will recursively call #privateRemoveEntry with
				// external==true
				// thus entry will already be removed if reaching this point.
				if (cache.getEntry(entry._fKey) == null) {
					return;
				} else {
					// basic removal
					cache.removeEntry(entry._fKey);
				}
			}
		}
		queueRemove(entry);
	}

	/**
	 * Sets the value in the cache at the given key. Returns the value.
	 * 
	 * @param key
	 *            Key of object to add.
	 * @param value
	 *            Value of object to add.
	 * @return added value.
	 */
	@Override
	public V put(K key, V value) {
		/* attempt to rid ourselves of the overflow, if there is any */
		if (fOverflow > 0)
			shrink();

		/* Check whether there's an entry in the cache */
		LRUCacheEntry<K,V> entry = cache.getEntry(key);
		// FIXME assumes hard entry
		if (entry != null) {

			if (getCurrentSpace() <= getSpaceLimit()) {
				updateTimestamp(entry);
				entry._fValue = value;
				fOverflow = 0;
				return value;
			} else {
				privateRemoveEntry(entry, false, false);
			}
		}

		// attempt to make new space
		makeSpace(1);

		// add without worring about space, it will
		// be handled later in a makeSpace call
		privateAdd(key, value);

		return value;
	}

	/**
	 * Removes and returns the value in the cache for the given key. If the key
	 * is not in the cache, returns null.
	 * 
	 * @param key
	 *            Key of object to remove from cache.
	 * @return Value removed from cache.
	 */
	public V remove(K key) {
		return removeKey(key);
	}

	/**
	 * Sets the load factor for the cache. The load factor determines how much
	 * space is reclaimed when the cache exceeds its space limit.
	 * 
	 * @param newLoadFactor
	 *            double
	 * @throws IllegalArgumentException
	 *             when the new load factor is not in (0.0, 1.0]
	 */
	public void setLoadFactor(double newLoadFactor)
			throws IllegalArgumentException {
		if (newLoadFactor <= 1.0 && newLoadFactor > 0.0)
			fLoadFactor = newLoadFactor;
		else
			throw new IllegalArgumentException(Messages.cache_invalidLoadFactor);
	}

	/**
	 * Attempts to shrink the cache if it has overflown. Returns true if the
	 * cache shrinks to less than or equal to <code>fSpaceLimit</code>.
	 */
	public boolean shrink() {
		if (fOverflow > 0)
			return makeSpace(0);
		return true;
	}

	/**
	 * Returns a String that represents the value of this object. This method is
	 * for debugging purposes only.
	 */
	@Override
	public String toString() {
		return "OverflowingLRUCache " + NumberFormat.getInstance().format(this.fillingRatio()) + "% full\n" + //$NON-NLS-1$ //$NON-NLS-2$
				this.toStringContents();
	}

	/**
	 * Updates the timestamp for the given entry, ensuring that the queue is
	 * kept in correct order. The entry must exist.
	 * 
	 * <p>
	 * This method will do nothing if timestamps have been disabled.
	 */
	@Override
	protected void updateTimestamp(LRUCacheEntry<K,V> entry) {
		if (fTimestampsOn) {
			entry._fTimestamp = fTimestampCounter++;
			if (fEntryQueue != entry) {
				this.privateRemoveEntry(entry, true);
				this.privateAddEntry(entry, true);
			}
		}
	}
}
