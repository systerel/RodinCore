/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.ElementCache.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.Openable;

/**
 * An LRU cache of <code>RodinElements</code>.
 */
public class OpenableCache extends OverflowingLRUCache<Openable, RodinElementInfo> {

	IRodinElement spaceLimitParent = null;

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
	protected boolean close(LRUCacheEntry<Openable, RodinElementInfo> entry) {
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

	/*
	 * Ensures that there is enough room for adding the given number of
	 * children. If the space limit must be increased, record the parent that
	 * needed this space limit.
	 */
	protected void ensureSpaceLimit(int childrenSize, IRodinElement parent) {
		// ensure the children can be put without closing other elements
		int spaceNeeded = 1 + (int) ((1 + fLoadFactor) * (childrenSize + fOverflow));
		if (fSpaceLimit < spaceNeeded) {
			// parent is being opened with more children than the space limit
			shrink(); // remove overflow
			setSpaceLimit(spaceNeeded);
			this.spaceLimitParent = parent;
		}
	}

	/*
	 * If the given parent was the one that increased the space limit, reset the
	 * space limit to the given default value.
	 */
	protected void resetSpaceLimit(int defaultLimit, IRodinElement parent) {
		if (parent.equals(this.spaceLimitParent)) {
			setSpaceLimit(defaultLimit);
			this.spaceLimitParent = null;
		}
	}

	/**
	 * Returns a new instance of the receiver.
	 */
	@Override
	protected OpenableCache newInstance(int size, int overflow) {
		return new OpenableCache(size, overflow);
	}

}
