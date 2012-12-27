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

import org.rodinp.core.IAttributeType;

/**
 * Common protocol for specifying a location which is an attribute of an
 * internal element in the Rodin database.
 * 
 * @see IRodinLocation
 * @see IInternalLocation
 * @see IAttributeSubstringLocation
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface IAttributeLocation extends IInternalLocation {

	/**
	 * Returns the type of the attribute containing this location.
	 * 
	 * @return the type of the attribute of this location
	 */
	IAttributeType getAttributeType();

}
