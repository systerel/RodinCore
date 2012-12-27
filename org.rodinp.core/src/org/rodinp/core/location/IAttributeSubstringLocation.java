/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.location;

/**
 * Common protocol for specifying a location which is a substring of a string
 * attribute of an internal element in the Rodin database.
 * 
 * @see IRodinLocation
 * @see IInternalLocation
 * @see IAttributeLocation
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface IAttributeSubstringLocation extends IAttributeLocation {

	/**
	 * Returns the start position of the substring of this location.
	 * <p>
	 * The value returned is the index of the first character of the substring
	 * in the attribute value. It is guaranteed to be non-negative and less than
	 * the value returned by {@link #getCharEnd()}.
	 * </p>
	 * 
	 * @return the start position of this location
	 * 
	 * @see String#substring(int, int)
	 */
	int getCharStart();

	/**
	 * Returns the end position of the substring of this location.
	 * <p>
	 * The value returned is the index plus one of the last character of the
	 * substring in the attribute value. It is guaranteed to be greater than the
	 * value returned by {@link #getCharStart()}.
	 * </p>
	 * 
	 * @return the end position of the location.
	 * 
	 * @see String#substring(int, int)
	 */
	int getCharEnd();

}
