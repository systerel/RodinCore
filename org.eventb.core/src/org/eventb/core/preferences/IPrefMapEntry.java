/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences;

/**
 * Interface describing an entry of a preference map.
 * 
 * @param <T>
 *            the type of the entry
 * @since 2.1
 */
public interface IPrefMapEntry<T> {

	/**
	 * Returns the entry key.
	 * 
	 * @return the entry key
	 */
	public String getKey();

	/**
	 * Returns the value of the entry.
	 * 
	 * @return a value
	 */
	public T getValue();

	/**
	 * Set the value associated to the entry.
	 * 
	 * @param value
	 *            the new value of the entry
	 */
	public void setValue(T value);

}
