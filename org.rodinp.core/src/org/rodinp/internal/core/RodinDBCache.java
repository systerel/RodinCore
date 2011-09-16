/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.internal.core.JavaModelCache
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - added clear() method
 *     Systerel - removed deprecated methods and occurrence count
 *     Systerel - separation of file and root element
 *     Systerel - fixed invalid buffer removal during file conversion
 *******************************************************************************/
package org.rodinp.internal.core;

import java.text.NumberFormat;
import java.util.HashMap;

import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * The cache of Rodin elements to their respective info.
 */
public class RodinDBCache {

	// average 25552 bytes per project.
	private static final int DEFAULT_PROJECT_SIZE = 5;

	// number of files in hard cache (absolute minimum = 3)
	private static final int DEFAULT_OPENABLE_SIZE = 3;

	private static final int DEFAULT_BUFFER_SIZE = DEFAULT_OPENABLE_SIZE;
	
	/**
	 * Active Rodin Model Info
	 */
	private RodinDBInfo modelInfo;

	/**
	 * Cache of open projects.
	 */
	private HashMap<RodinProject, RodinProjectElementInfo> projectCache;

	/**
	 * Cache of open Rodin files
	 */
	private OpenableCache openableCache;

	/**
	 * Cache of Rodin file buffers
	 */
	private BufferCache bufferCache;

	public RodinDBCache() {
		// NB: Don't use a LRUCache for projects as they are constantly reopened
		// (e.g. during delta processing)
		this.projectCache = new HashMap<RodinProject, RodinProjectElementInfo>(
				DEFAULT_PROJECT_SIZE);
		this.openableCache = new OpenableCache(DEFAULT_OPENABLE_SIZE);
		this.bufferCache = new BufferCache(DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Returns the info for the element.
	 */
	public OpenableElementInfo getInfo(Openable element) {
		IElementType<?> elementType = element.getElementType();
		if (elementType == IRodinDB.ELEMENT_TYPE) {
			return this.modelInfo;
		}
		if (elementType == IRodinProject.ELEMENT_TYPE) {
			return this.projectCache.get(element);
		}
		return this.openableCache.get(element);
	}

	/**
	 * Returns the buffer for the given Rodin file.
	 */
	public Buffer getBuffer(RodinFile rodinFile) {
		return this.bufferCache.get(rodinFile);
	}

	public Buffer peekAtBuffer(RodinFile rodinFile) {
		return this.bufferCache.peek(rodinFile);
	}

	/**
	 * Returns the info for this element without disturbing the cache ordering.
	 */
	public OpenableElementInfo peekAtInfo(Openable element) {
		IElementType<?> elementType = element.getElementType();
		if (elementType == IRodinDB.ELEMENT_TYPE) {
			return this.modelInfo;
		}
		if (elementType == IRodinProject.ELEMENT_TYPE) {
			return this.projectCache.get(element);
		}
		return this.openableCache.peek(element);
	}

	/**
	 * Remembers the buffer for the given Rodin file.
	 */
	public void putBuffer(RodinFile rodinFile, Buffer buffer) {
		this.bufferCache.put(rodinFile, buffer);
	}

	/**
	 * Remember the info for the element.
	 */
	public void putInfo(Openable element, OpenableElementInfo info) {
		IElementType<?> elementType = element.getElementType();
		if (elementType == IRodinDB.ELEMENT_TYPE) {
			this.modelInfo = (RodinDBInfo) info;
		} else if (elementType == IRodinProject.ELEMENT_TYPE) {
			this.projectCache.put(
					(RodinProject) element,
					(RodinProjectElementInfo) info
			);
		} else {
			this.openableCache.put(element, info);
		}
	}

	/**
	 * Removes the info of the element from the cache.
	 */
	public void removeInfo(Openable element) {
		IElementType<?> elementType = element.getElementType();
		if (elementType == IRodinDB.ELEMENT_TYPE) {
			this.modelInfo = null;
		} else if (elementType == IRodinProject.ELEMENT_TYPE) {
			this.projectCache.remove(element);
		} else {
			this.openableCache.remove(element);
		}
	}

	/**
	 * Removes the buffer for the given Rodin file from the cache. If
	 * <code>force</code> is <code>true</code>, always remove the buffer,
	 * otherwise remove the buffer only if it has not been modified yet.
	 */
	public void removeBuffer(IRodinFile rodinFile, boolean force) {
		final Buffer buffer = this.bufferCache.peek(rodinFile);
		if (buffer == null)
			return;
		if ((force || !buffer.hasUnsavedChanges()) && buffer.hasBeenLoaded()) {
			this.bufferCache.remove(rodinFile);
		}
	}

	public String toStringFillingRation(String prefix) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(prefix);
		buffer.append("Project cache: "); //$NON-NLS-1$
		buffer.append(this.projectCache.size());
		buffer.append(" projects\n"); //$NON-NLS-1$
		buffer.append(prefix);
		buffer.append("Openable cache["); //$NON-NLS-1$
		buffer.append(this.openableCache.getSpaceLimit());
		buffer.append("]: "); //$NON-NLS-1$
		buffer.append(NumberFormat.getInstance().format(
				this.openableCache.fillingRatio()));
		buffer.append("%\n"); //$NON-NLS-1$
		buffer.append("Buffer cache["); //$NON-NLS-1$
		buffer.append(this.bufferCache.getSpaceLimit());
		buffer.append("]: "); //$NON-NLS-1$
		buffer.append(NumberFormat.getInstance().format(
				this.bufferCache.fillingRatio()));
		buffer.append("%\n"); //$NON-NLS-1$
		return buffer.toString();
	}
}
