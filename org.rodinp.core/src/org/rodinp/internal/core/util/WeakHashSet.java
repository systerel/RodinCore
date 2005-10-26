/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.compiler.util.WeakHashSet.java
 * 
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

// TODO Use Java 5 genericity here. 
// TODO Rewrite completely with HashSet, the algo used here is known to be very inefficient!


/**
 * A hashset whose values can be garbage collected.
 */
public class WeakHashSet<T> {
	
	public class HashableWeakReference<U> extends WeakReference<U> {
		public int hashCode;
		public HashableWeakReference(U referent, ReferenceQueue<? super U> queue) {
			super(referent, queue);
			this.hashCode = referent.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashableWeakReference)) return false;
			Object referent = get();
			Object other = ((HashableWeakReference) obj).get();
			if (referent == null) return other == null;
			return referent.equals(other);
		}
		@Override
		public int hashCode() {
			return this.hashCode;
		}
		@Override
		public String toString() {
			Object referent = get();
			if (referent == null) return "[hashCode=" + this.hashCode + "] <referent was garbage collected>"; //$NON-NLS-1$  //$NON-NLS-2$
			return "[hashCode=" + this.hashCode + "] " + referent.toString(); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	HashableWeakReference<T>[] values;
	public int elementSize; // number of elements in the table
	int threshold;
	ReferenceQueue<T> referenceQueue = new ReferenceQueue<T>();	
	
	public WeakHashSet() {
		this(5);
	}
	
	@SuppressWarnings("unchecked")	// For array creation.
	public WeakHashSet(int size) {
		this.elementSize = 0;
		this.threshold = size; // size represents the expected number of elements
		int extraRoom = (int) (size * 1.75f);
		if (this.threshold == extraRoom)
			extraRoom++;
		this.values = new HashableWeakReference[extraRoom];
	}
	
	/*
	 * Adds the given object to this set.
	 * If an object that is equals to the given object already exists, do nothing.
	 * Returns the existing object or the new object if not found.
	 */
	public T add(T obj) {
		cleanupGarbageCollectedValues();
		int index = (obj.hashCode() & 0x7FFFFFFF) % this.values.length;
		HashableWeakReference<T> currentValue;
		while ((currentValue = this.values[index]) != null) {
			T referent;
			if (obj.equals(referent = currentValue.get())) {
				return referent;
			}
			index = (index + 1) % this.values.length;
		}
		this.values[index] = new HashableWeakReference<T>(obj, this.referenceQueue);

		// assumes the threshold is never equal to the size of the table
		if (++this.elementSize > this.threshold)
			rehash();
		
		return obj;
	}
		
	private void addValue(HashableWeakReference<T> value) {
		Object obj = value.get();
		if (obj == null) return;
		int valuesLength = this.values.length;
		int index = (value.hashCode & 0x7FFFFFFF) % valuesLength;
		HashableWeakReference currentValue;
		while ((currentValue = this.values[index]) != null) {
			if (obj.equals(currentValue.get())) {
				return;
			}
			index = (index + 1) % valuesLength;
		}
		this.values[index] = value;

		// assumes the threshold is never equal to the size of the table
		if (++this.elementSize > this.threshold)
			rehash();
	}
	
	private void cleanupGarbageCollectedValues() {
		HashableWeakReference<T> toBeRemoved;
		while ((toBeRemoved = poll()) != null) {
			int hashCode = toBeRemoved.hashCode;
			int valuesLength = this.values.length;
			int index = (hashCode & 0x7FFFFFFF) % valuesLength;
			HashableWeakReference<T> currentValue;
			while ((currentValue = this.values[index]) != null) {
				if (currentValue == toBeRemoved) {
					// replace the value at index with the last value with the same hash
					int sameHash = index;
					int current;
					while ((currentValue = this.values[current = (sameHash + 1) % valuesLength]) != null && currentValue.hashCode == hashCode)
						sameHash = current;
					this.values[index] = this.values[sameHash];
					this.values[sameHash] = null;
					this.elementSize--;
					break;
				}
				index = (index + 1) % valuesLength;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private final HashableWeakReference<T> poll() {
		return (HashableWeakReference<T>) this.referenceQueue.poll();
	}
	
	public boolean contains(T obj) {
		return get(obj) != null;
	}
	
	/*
	 * Return the object that is in this set and that is equals to the given object.
	 * Return null if not found.
	 */
	public T get(T obj) {
		cleanupGarbageCollectedValues();
		int valuesLength = this.values.length;
		int index = (obj.hashCode() & 0x7FFFFFFF) % valuesLength;
		HashableWeakReference<T> currentValue;
		while ((currentValue = this.values[index]) != null) {
			T referent;
			if (obj.equals(referent = currentValue.get())) {
				return referent;
			}
			index = (index + 1) % valuesLength;
		}
		return null;
	}
		
	private void rehash() {
		WeakHashSet<T> newHashSet = new WeakHashSet<T>(this.elementSize * 2);		// double the number of expected elements
		newHashSet.referenceQueue = this.referenceQueue;
		HashableWeakReference<T> currentValue;
		for (int i = 0, length = this.values.length; i < length; i++)
			if ((currentValue = this.values[i]) != null)
				newHashSet.addValue(currentValue);

		this.values = newHashSet.values;
		this.threshold = newHashSet.threshold;
		this.elementSize = newHashSet.elementSize;
	}

	/*
	 * Removes the object that is in this set and that is equals to the given object.
	 * Return the object that was in the set, or null if not found.
	 */
	public T remove(T obj) {
		cleanupGarbageCollectedValues();
		int valuesLength = this.values.length;
		int index = (obj.hashCode() & 0x7FFFFFFF) % valuesLength;
		HashableWeakReference<T> currentValue;
		while ((currentValue = this.values[index]) != null) {
			T referent;
			if (obj.equals(referent = currentValue.get())) {
				this.elementSize--;
				this.values[index] = null;
				rehash();
				return referent;
			}
			index = (index + 1) % valuesLength;
		}
		return null;
	}

	public int size() {
		return this.elementSize;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("{"); //$NON-NLS-1$
		for (int i = 0, length = this.values.length; i < length; i++) {
			HashableWeakReference<T> value = this.values[i];
			if (value != null) {
				T ref = value.get();
				if (ref != null) {
					buffer.append(ref.toString());
					buffer.append(", "); //$NON-NLS-1$
				}
			}
		}
		buffer.append("}"); //$NON-NLS-1$
		return buffer.toString();
	}
}
