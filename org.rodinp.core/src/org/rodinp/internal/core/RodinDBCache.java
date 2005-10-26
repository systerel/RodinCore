/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.JavaModelCache.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.Openable;
import org.rodinp.core.RodinFile;

/**
 * The cache of Rodin elements to their respective info.
 */
public class RodinDBCache {

	public static final int BASE_VALUE = 20;

	// average 25552 bytes per project.
	public static final int DEFAULT_PROJECT_SIZE = 5;

	// average 6629 bytes per openable (includes members)
	// -> maximum size : 662900*BASE_VALUE bytes
	public static final int DEFAULT_OPENABLE_SIZE = BASE_VALUE * 100;

	// average 20 members per openable
	public static final int DEFAULT_MEMBER_SIZE = BASE_VALUE * 100 * 20;
	
	/**
	 * Active Rodin Model Info
	 */
	protected RodinDBInfo modelInfo;

	/**
	 * Cache of open projects.
	 */
	protected HashMap<IRodinProject, RodinElementInfo> projectCache;

	/**
	 * Cache of open Rodin files
	 */
	protected ElementCache openableCache;

	/**
	 * Cache of open members of openable Rodin elements
	 */
	protected Map<IRodinElement, RodinElementInfo> memberCache;

	public RodinDBCache() {
		// NB: Don't use a LRUCache for projects as they are constantly reopened
		// (e.g. during delta processing)
		this.projectCache = new HashMap<IRodinProject, RodinElementInfo>(DEFAULT_PROJECT_SIZE);
		this.openableCache = new ElementCache(DEFAULT_OPENABLE_SIZE);
		this.memberCache = new HashMap<IRodinElement, RodinElementInfo>(DEFAULT_MEMBER_SIZE);
	}

	/**
	 * Returns the info for the element.
	 */
	public RodinElementInfo getInfo(IRodinElement element) {
		String elementType = element.getElementType();
		if (elementType == IRodinElement.RODIN_DATABASE) {
			return this.modelInfo;
		} else if (elementType == IRodinElement.RODIN_PROJECT) {
			return this.projectCache.get(element);
		} else if (element instanceof Openable) {
			return this.openableCache.get((Openable) element);
		} else {
			return this.memberCache.get(element);
		}
	}

	/**
	 * Returns the info for this element without disturbing the cache ordering.
	 */
	protected RodinElementInfo peekAtInfo(IRodinElement element) {
		String elementType = element.getElementType();
		if (elementType == IRodinElement.RODIN_DATABASE) {
			return this.modelInfo;
		} else if (elementType == IRodinElement.RODIN_PROJECT) {
			return this.projectCache.get(element);
		} else if (element instanceof Openable) {
			return this.openableCache.peek((Openable) element);
		} else {
			return this.memberCache.get(element);
		}
	}

	/**
	 * Remember the info for the element.
	 */
	protected void putInfo(IRodinElement element, RodinElementInfo info) {
		String elementType = element.getElementType();
		if (elementType == IRodinElement.RODIN_DATABASE) {
			this.modelInfo = (RodinDBInfo) info;
		} else if (elementType == IRodinElement.RODIN_PROJECT) {
			this.projectCache.put((IRodinProject) element, info);
		} else if (element instanceof Openable) {
			this.openableCache.put((Openable) element, info);
		} else {
			this.memberCache.put(element, info);
		}
	}

	/**
	 * Removes the info of the element from the cache.
	 */
	protected void removeInfo(IRodinElement element) {
		String elementType = element.getElementType();
		if (elementType == IRodinElement.RODIN_DATABASE) {
			this.modelInfo = null;
		} else if (elementType == IRodinElement.RODIN_PROJECT) {
			this.projectCache.remove(element);
		} else if (element instanceof RodinFile) {
			this.openableCache.remove((Openable) element);
		} else {
			this.memberCache.remove(element);
		}
	}

	public String toStringFillingRation(String prefix) {
		StringBuffer buffer = new StringBuffer();
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
		return buffer.toString();
	}
}
