/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.index;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * Common protocol for specifying a location in the Rodin database. A location
 * can be
 * <ul>
 * <li>either a Rodin element ({@link IRodinLocation}),</li>
 * <li>or an internal element ({@link IInternalLocation}),</li>
 * <li>or an attribute of an internal element ({@link IAttributeLocation}),</li>
 * <li>or a substring of a string attribute of an internal element ({@link IAttributeSubstringLocation}).</li>
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
 */
public interface IRodinLocation {

    /**
     * Returns the file containing the location, if any. If the location is a
     * Rodin file or occurs within a Rodin file, then this Rodin file is
     * returned. Otherwise, <code>null</code> is returned.
     * 
     * @return the file containing the location or <code>null</code>
     */
    // TODO move this method to IRodinElement
    IRodinFile getRodinFile();

    /**
     * Returns the element containing this location.
     * 
     * @return the element containing this location
     */
    IRodinElement getElement();

    /**
     * Returns true iff this location is included in the given one.
     * 
     * @param other
     *                the checked location.
     * @return whether this location is included in the given one.
     */
    boolean isIncludedIn(IRodinLocation other);

}