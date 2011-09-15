/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * Strongly inspired by org.eclipse.jdt.internal.core.util.LRUCache.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import org.rodinp.core.IRodinElement;

/**
 * The <code>LRUCache</code> is a hashtable that stores a finite number of
 * elements. When an attempt is made to add values to a full cache, the least
 * recently used values in the cache are discarded to make room for the new
 * values as necessary.
 * 
 * <p>
 * The data structure is based on the LRU virtual memory paging scheme.
 * </p>
 * <p>
 * Every object takes up an amount of cache space of one. Hence, the notion of
 * used space is actually a number of hardly cached elements.
 * </p>
 * <p>
 * This implementation is NOT thread-safe. Synchronization wrappers would have
 * to be added to ensure atomic insertions and deletions from the cache.
 * </p>
 * 
 */
public class LRUCache<K, V> implements Cloneable {

	/**
	 * This type is used internally by the LRUCache to represent entries stored
	 * in the cache. It is static because it does not require a pointer to the
	 * cache which contains it.
	 * 
	 * @see LRUCache
	 */
	protected static class LRUCacheEntry<KI, VI> {

		/**
		 * Hash table key
		 */
		public KI _fKey;

		/**
		 * Hash table value (an LRUCacheEntry object)
		 */
		public VI _fValue;

		/**
		 * Time value for queue sorting
		 */
		public int _fTimestamp;

		/**
		 * Cache footprint of this entry
		 */
		public int _fSpace;

		/**
		 * Previous entry in queue
		 */
		public LRUCacheEntry<KI, VI> _fPrevious;

		/**
		 * Next entry in queue
		 */
		public LRUCacheEntry<KI, VI> _fNext;

		/**
		 * Creates a new instance of the receiver with the provided values for
		 * key, value, and space.
		 */
		public LRUCacheEntry(KI key, VI value, int space) {
			_fKey = key;
			_fValue = value;
			_fSpace = space;
		}

		/**
		 * Returns a String that represents the value of this object.
		 */
		@Override
		public String toString() {
			return "LRUCacheEntry [" + _fKey + "-->" + _fValue + "]"; //$NON-NLS-3$ //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	protected static class HardCache<K, V> {
		
		/**
		 * Maximum space allowed in hard cache
		 */
		private final int fSpaceLimit;

		/**
		 * Hash table for fast random access to cache entries
		 */
		private final Hashtable<K, LRUCacheEntry<K, V>> fEntryTable;
		
		public HardCache(int hardSize) {
			fSpaceLimit = hardSize;
			fEntryTable = new Hashtable<K, LRUCacheEntry<K, V>>(hardSize);
		}
		
		public LRUCacheEntry<K, V> getEntry(K key) {
			return fEntryTable.get(key);
		}
		
		public void putEntry(LRUCacheEntry<K, V> entry) {
			fEntryTable.put(entry._fKey, entry);
		}
		
		public void removeEntry(K key) {
			fEntryTable.remove(key);
		}
		
		public void clear() {
			fEntryTable.clear();
		}
		
		public int size() {
			return fEntryTable.size();
		}

		public int getSpaceLimit() {
			return fSpaceLimit;
		}

		public Enumeration<LRUCacheEntry<K, V>> elements() {
			return fEntryTable.elements();
		}
	}
	
	/**
	 * Amount of cache space used so far
	 */
	protected int fCurrentSpace;

	/**
	 * Counter for handing out sequential timestamps
	 */
	protected int fTimestampCounter;

	protected final HardCache<K, V> cache;
	
	/**
	 * Start of queue (most recently used entry)
	 */
	protected LRUCacheEntry<K, V> fEntryQueue;

	/**
	 * End of queue (least recently used entry)
	 */
	protected LRUCacheEntry<K, V> fEntryQueueTail;

	/**
	 * Default amount of space in the cache
	 */
	protected static final int DEFAULT_SPACELIMIT = 100;

	/**
	 * Creates a new cache. Size of cache is defined by
	 * <code>DEFAULT_SPACELIMIT</code>.
	 */
	public LRUCache() {
		this(DEFAULT_SPACELIMIT);
	}

	/**
	 * Creates a new cache.
	 * 
	 * @param size
	 *            Size of Cache
	 */
	public LRUCache(int size) {
		fTimestampCounter = fCurrentSpace = 0;
		fEntryQueue = fEntryQueueTail = null;
		cache = new HardCache<K, V>(size);
	}

	/**
	 * Returns a new cache containing the same contents.
	 * 
	 * @return New copy of object.
	 */
	@Override
	public LRUCache<K, V> clone() {
		LRUCache<K, V> newCache = newInstance(getSpaceLimit());
		LRUCacheEntry<K, V> qEntry;

		/* Preserve order of entries by copying from oldest to newest */
		qEntry = this.fEntryQueueTail;
		while (qEntry != null) {
			newCache.privateAdd(qEntry._fKey, qEntry._fValue, qEntry._fSpace);
			qEntry = qEntry._fPrevious;
		}
		return newCache;
	}

	/**
	 * Flushes all entries from the cache.
	 */
	public void flush() {
		fCurrentSpace = 0;
		LRUCacheEntry<K, V> entry = fEntryQueueTail; // Remember last entry
		cache.clear(); // Clear it out
		fEntryQueue = fEntryQueueTail = null;
		while (entry != null) { // send deletion notifications in LRU order
			privateNotifyDeletionFromCache(entry);
			entry = entry._fPrevious;
		}
	}

	/**
	 * Flushes the given entry from the cache. Does nothing if entry does not
	 * exist in cache.
	 * 
	 * @param key
	 *            Key of object to flush
	 */
	public void flush(K key) {

		LRUCacheEntry<K, V> entry;

		entry = cache.getEntry(key);

		/* If entry does not exist, return */
		if (entry == null)
			return;

		this.privateRemoveEntry(entry, false);
	}

	/**
	 * Answers the value in the cache at the given key. If the value is not in
	 * the cache, returns null
	 * 
	 * @param key
	 *            Hash table key of object to retrieve
	 * @return Retreived object, or null if object does not exist
	 */
	public V get(K key) {

		LRUCacheEntry<K, V> entry = cache.getEntry(key);
		if (entry == null) {
			return null;
		}

		this.updateTimestamp(entry);
		return entry._fValue;
	}

	/**
	 * Returns the amount of space that is current used in the cache.
	 */
	public int getCurrentSpace() {
		return fCurrentSpace;
	}

	/**
	 * Returns the maximum amount of space available in the cache.
	 */
	public int getSpaceLimit() {
		return cache.getSpaceLimit();
	}

	/**
	 * Returns an enumeration that iterates over all the keys and values
	 * currently in the cache.
	 */
	public ICacheEnumeration<K, V> keysAndValues() {
		return new ICacheEnumeration<K, V>() {

			Enumeration<LRUCacheEntry<K, V>> fValues = cache.elements();

			LRUCacheEntry<K, V> fEntry;

			@Override
			public boolean hasMoreElements() {
				return fValues.hasMoreElements();
			}

			@Override
			public K nextElement() {
				fEntry = fValues.nextElement();
				return fEntry._fKey;
			}

			@Override
			public V getValue() {
				if (fEntry == null) {
					throw new java.util.NoSuchElementException();
				}
				return fEntry._fValue;
			}
		};
	}

	/**
	 * Ensures there is the specified amount of free space in the receiver, by
	 * removing old entries if necessary. Returns true if the requested space
	 * was made available, false otherwise.
	 * 
	 * @param space
	 *            Amount of space to free up
	 */
	protected boolean makeSpace(int space) {

		int limit;

		limit = this.getSpaceLimit();

		/* if space is already available */
		if (fCurrentSpace + space <= limit) {
			return true;
		}

		/* if entry is too big for cache */
		if (space > limit) {
			return false;
		}

		/* Free up space by removing oldest entries */
		while (fCurrentSpace + space > limit && fEntryQueueTail != null) {
			this.privateRemoveEntry(fEntryQueueTail, false);
		}
		return true;
	}

	/**
	 * Returns a new LRUCache instance
	 */
	protected LRUCache<K, V> newInstance(int newSize) {
		return new LRUCache<K, V>(newSize);
	}

	/**
	 * Adds an entry for the given key/value/space.
	 */
	protected void privateAdd(K key, V value, int space) {

		LRUCacheEntry<K, V> entry;

		entry = new LRUCacheEntry<K, V>(key, value, space);
		this.privateAddEntry(entry, false);
	}

	/**
	 * Adds the given entry from the receiver.
	 * 
	 * @param shuffle
	 *            Indicates whether we are just shuffling the queue (in which
	 *            case, the entry table is not modified).
	 */
	protected void privateAddEntry(LRUCacheEntry<K, V> entry, boolean shuffle) {

		if (!shuffle) {
			cache.putEntry(entry);
			fCurrentSpace += entry._fSpace;
		}

		entry._fTimestamp = fTimestampCounter++;
		entry._fNext = this.fEntryQueue;
		entry._fPrevious = null;

		if (fEntryQueue == null) {
			/* this is the first and last entry */
			fEntryQueueTail = entry;
		} else {
			fEntryQueue._fPrevious = entry;
		}

		fEntryQueue = entry;
	}

	/**
	 * An entry has been removed from the cache, for example because it has
	 * fallen off the bottom of the LRU queue. Subclasses could over-ride this
	 * to implement a persistent cache below the LRU cache.
	 */
	protected void privateNotifyDeletionFromCache(LRUCacheEntry<K, V> entry) {
		// Default is NOP.
	}

	/**
	 * Removes the entry from the entry queue.
	 * 
	 * @param shuffle
	 *            indicates whether we are just shuffling the queue (in which
	 *            case, the entry table is not modified).
	 */
	protected void privateRemoveEntry(LRUCacheEntry<K, V> entry, boolean shuffle) {

		LRUCacheEntry<K, V> previous, next;

		previous = entry._fPrevious;
		next = entry._fNext;

		if (!shuffle) {
			cache.removeEntry(entry._fKey);
			fCurrentSpace -= entry._fSpace;
			privateNotifyDeletionFromCache(entry);
		}

		/* if this was the first entry */
		if (previous == null) {
			fEntryQueue = next;
		} else {
			previous._fNext = next;
		}

		/* if this was the last entry */
		if (next == null) {
			fEntryQueueTail = previous;
		} else {
			next._fPrevious = previous;
		}
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
	public V put(K key, V value) {

		int newSpace, oldSpace, newTotal;
		LRUCacheEntry<K, V> entry;

		/* Check whether there's an entry in the cache */
		newSpace = spaceFor(value);
		entry = cache.getEntry(key);

		if (entry != null) {

			/**
			 * Replace the entry in the cache if it would not overflow the
			 * cache. Otherwise flush the entry and re-add it so as to keep
			 * cache within budget
			 */
			oldSpace = entry._fSpace;
			newTotal = getCurrentSpace() - oldSpace + newSpace;
			if (newTotal <= getSpaceLimit()) {
				updateTimestamp(entry);
				entry._fValue = value;
				entry._fSpace = newSpace;
				this.fCurrentSpace = newTotal;
				return value;
			} else {
				privateRemoveEntry(entry, false);
			}
		}
		if (makeSpace(newSpace)) {
			privateAdd(key, value, newSpace);
		}
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
	public V removeKey(K key) {

		LRUCacheEntry<K, V> entry = cache.getEntry(key);
		if (entry == null) {
			return null;
		}
		V value = entry._fValue;
		this.privateRemoveEntry(entry, false);
		return value;
	}

	/**
	 * Returns the space taken by the given value.
	 */
	protected int spaceFor(V value) {
		return 1;
	}

	/**
	 * Returns a String that represents the value of this object. This method is
	 * for debugging purposes only.
	 */
	@Override
	public String toString() {
		return "LRUCache " + (fCurrentSpace * 100.0 / cache.getSpaceLimit()) + "% full\n" + //$NON-NLS-1$ //$NON-NLS-2$
				this.toStringContents();
	}

	private static class ComparableEntry implements Comparable<ComparableEntry> {
		String name;

		Object value;

		ComparableEntry(Object key, Object value) {
			if (key instanceof IRodinElement) {
				this.name = ((IRodinElement) key).getElementName();
			} else {
				this.name = key.toString();
			}
			this.value = value;
		}

		@Override
		public final int compareTo(ComparableEntry other) {
			return name.compareTo(other.name);
		}
	}

	/**
	 * Returns a String that represents the contents of this object. This method
	 * is for debugging purposes only.
	 */
	protected String toStringContents() {
		StringBuffer result = new StringBuffer();
		ComparableEntry[] elements = new ComparableEntry[cache.size()];
		ICacheEnumeration<K, V> enumeration = keysAndValues();

		// Get the elements of the cache
		int index = 0;
		while (enumeration.hasMoreElements()) {
			elements[index] = new ComparableEntry(enumeration.nextElement(),
					enumeration.getValue());
		}

		// Sort them
		Arrays.sort(elements, null);

		// And turn them into a String
		for (ComparableEntry element : elements) {
			result.append(element.name);
			result.append(" -> "); //$NON-NLS-1$
			result.append(element.value);
			result.append("\n"); //$NON-NLS-1$
		}
		return result.toString();
	}

	/**
	 * Updates the timestamp for the given entry, ensuring that the queue is
	 * kept in correct order. The entry must exist
	 */
	protected void updateTimestamp(LRUCacheEntry<K, V> entry) {

		entry._fTimestamp = fTimestampCounter++;
		if (fEntryQueue != entry) {
			this.privateRemoveEntry(entry, true);
			this.privateAddEntry(entry, true);
		}
		return;
	}
}
