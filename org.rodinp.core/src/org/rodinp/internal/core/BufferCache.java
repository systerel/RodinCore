/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import org.rodinp.core.IRodinFile;
import org.rodinp.internal.core.util.SoftLRUCache;

/**
 * Implements a cache of Rodin file buffers.
 * 
 * @author Laurent Voisin
 */
public class BufferCache extends SoftLRUCache<IRodinFile, Buffer> {

	public BufferCache(int size) {
		super(size);
	}

	@Override
	protected boolean close(LRUCacheEntry<IRodinFile, Buffer> entry) {
		Buffer buffer = entry._fValue;
		return !buffer.hasUnsavedChanges();
	}

	@Override
	protected SoftLRUCache<IRodinFile, Buffer> newSoftLRUInstance(int size) {
		// FIXME is it really the intended behaviour ?
		return null;
	}

}
