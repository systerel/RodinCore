/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.location;

import org.rodinp.core.IRodinElement;

/**
 * Common protocol for specifying a location in the Rodin database. A location
 * can be
 * <ul>
 * <li>either a Rodin element ({@link IRodinLocation}),</li>
 * <li>or an internal element ({@link IInternalLocation}),</li>
 * <li>or an attribute of an internal element ({@link IAttributeLocation}),</li>
 * <li>or a substring of a string attribute of an internal element (
 * {@link IAttributeSubstringLocation}).</li>
 * </ul>
 * <p>
 * Locations are handle-only. The items referenced by a location may exist or
 * not exist. The Rodin database may hand out any number of instances for each
 * location. Instances that refer to the same location are guaranteed to be
 * equal, but not necessarily identical.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IInternalLocation
 * @see IAttributeLocation
 * @see IAttributeSubstringLocation
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public interface IRodinLocation {

	/**
	 * Returns the element containing this location.
	 * 
	 * @return the element containing this location
	 */
	IRodinElement getElement();

	/**
	 * Returns whether this location is included in the given one. A location A
	 * is considered as included in location B if and only if
	 * <ul>
	 * <li>either the element contained in B is an ancestor of the element
	 * contained in A;</li>
	 * <li>or A and B contain the same element and B does not contain an
	 * attribute type;</li>
	 * <li>or A and B contain the same element and attribute type, and B does
	 * not contain a substring specification;</li>
	 * <li>or A and B contain the same element and attribute type, both A and B
	 * contain a substring specification, and the substring of A is included
	 * non-strictly in the substring of B.</li>
	 * </ul>
	 * 
	 * @param other
	 *            a location to compare to for inclusion
	 * @return <code>true</code> iff this location is included in the given one
	 */
	boolean isIncludedIn(IRodinLocation other);

}