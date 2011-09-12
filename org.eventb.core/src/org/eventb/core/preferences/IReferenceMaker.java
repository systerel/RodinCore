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
package org.eventb.core.preferences;

/**
 * Common protocol for reference makers.
 * <p>
 * References makers make a reference of type T from an entry of T.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @see CachedPreferenceMap
 */
public interface IReferenceMaker<T> {

	/**
	 * Returns a reference to the value of the given entry.
	 * 
	 * @param prefEntry
	 *            a preference entry
	 * @return a T reference
	 */
	T makeReference(IPrefMapEntry<T> prefEntry);

	/**
	 * Returns an array of preference keys that are referenced by the given
	 * preference;
	 * 
	 * @param pref
	 *            a preference
	 * @return an array of keys with no duplicate
	 */
	String[] getReferencedKeys(T pref);
}
