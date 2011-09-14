/*******************************************************************************
 * Copyright (c) 2005-2011 ETH Zurich and others.
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
import org.rodinp.internal.core.util.SoftLRUCache;

/**
 * An LRU cache of <code>Openable</code>s.
 */
public class OpenableCache extends
		SoftLRUCache<Openable, OpenableElementInfo> {

	/**
	 * Constructs a new element cache of the given size.
	 */
	public OpenableCache(int size) {
		super(size);
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
	protected boolean close(LRUCacheEntry<Openable, OpenableElementInfo> entry) {
		Openable element = entry._fKey;
		try {
			if (!element.canBeRemovedFromCache()) {
				return false;
			} else {
				element.close();
				return true;
			}
		} catch (RodinDBException npe) {
			return false;
		}
	}

	@Override
	protected SoftLRUCache<Openable, OpenableElementInfo> newSoftLRUInstance(
			int size) {
		return new OpenableCache(size);
	}

}
