/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.rodinp.internal.core.OverflowingLRUCache;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class SoftLRUCache<K, V> extends OverflowingLRUCache<K, V> {

	private final Map<K, SoftReference<V>> softReferences = new HashMap<K, SoftReference<V>>();

	public SoftLRUCache(int minHardRefs) {
		super(minHardRefs);
	}

	private void reintegrateSoftRef(K key) {
		final SoftReference<V> softRef = softReferences.get(key);
		if (softRef == null) {
			return;
		}
		final V value = softRef.get();
		if (value == null) {
			return;
		}
		put(key, value);
	}

	@Override
	protected V internalGet(K key, boolean updateTimestamps) {
//		reintegrateSoftRef(key);
		return super.internalGet(key, updateTimestamps);
	}

	protected abstract SoftLRUCache<K, V> newSoftLRUInstance(int size);
	
	@Override
	protected OverflowingLRUCache<K, V> newInstance(int size, int overflow) {
		final SoftLRUCache<K, V> newInstance = newSoftLRUInstance(size);
		newInstance.softReferences.putAll(this.softReferences);
		return newInstance;
	}
	
	@Override
	protected void privateNotifyDeletionFromCache(LRUCacheEntry<K, V> entry) {
		softReferences.put(entry._fKey, new SoftReference<V>(entry._fValue));
	}

}
