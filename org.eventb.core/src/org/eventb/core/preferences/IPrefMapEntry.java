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
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IPrefMapEntry<T> {

	/**
	 * Returns the entry key.
	 * 
	 * @return the entry key
	 */
	public String getKey();

	/**
	 * Returns the current value of this entry.
	 * 
	 * @return a value
	 */
	public T getValue();

	/**
	 * Sets the key associated to this entry.
	 * 
	 * @param key
	 *            the new value of the key
	 * @since 2.3
	 */
	public void setKey(String key);

	/**
	 * Set the value associated to this entry.
	 * 
	 * @param value
	 *            the new value of the entry
	 */
	public void setValue(T value);

	/**
	 * Returns an object that references the value of this entry.
	 * <p>
	 * Returned object always has the same behaviour as the value of this entry,
	 * even if the value changes.
	 * </p>
	 * 
	 * @return a reference
	 * @since 2.3
	 */
	T getReference();

}
