/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences;

/**
 * Interface for listening to changes in a preference map cache.
 * <p>
 * This interface may be implemented by clients.
 * </p>
 * 
 * @see CachedPreferenceMap#addListener(ICacheListener)
 * @since 2.1
 */
public interface ICacheListener<T> {

	/**
	 * Notifies this listener that the cache contents changed.
	 * 
	 * @param map
	 *            the cache where changes occur
	 */
	public void cacheChanged(CachedPreferenceMap<T> map);

}
