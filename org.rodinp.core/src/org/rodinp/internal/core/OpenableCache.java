/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * Strongly inspired by org.eclipse.jdt.internal.core.ElementCache.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.RodinDBException;

/**
 * An LRU cache of <code>Openable</code>s.
 */
public class OpenableCache extends
		OverflowingLRUCache<Openable, OpenableElementInfo> {

	/**
	 * Constructs a new element cache of the given size.
	 */
	public OpenableCache(int size) {
		super(size);
	}

	/**
	 * Constructs a new element cache of the given size.
	 */
	public OpenableCache(int size, int overflow) {
		super(size, overflow);
	}

	/**
	 * Returns true if the element is successfully closed and removed from the
	 * cache, otherwise false.
	 * 
	 * <p>
	 * NOTE: this triggers an external removal of this element by closing the
	 * element.
	 */
	@Override
	protected boolean doClose(LRUCacheEntry<Openable, OpenableElementInfo> entry) {
		final Openable element = entry._fKey;
		try {
			element.close();
			return true;
		} catch (RodinDBException e) {
			return false;
		}
	}

	/**
	 * Returns a new instance of the receiver.
	 */
	@Override
	protected OpenableCache newInstance(int newSize, int overflow) {
		return new OpenableCache(newSize, overflow);
	}

	@Override
	protected boolean canClose(
			LRUCacheEntry<Openable, OpenableElementInfo> entry) {
		final Openable element = entry._fKey;
		return element.canBeRemovedFromCache();
	}

}
