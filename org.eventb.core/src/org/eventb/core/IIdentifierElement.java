/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for elements that contain an Event-B identifier (carrier set,
 * constant or variable).
 * <p>
 * The identifier is manipulated as a bare string of characters, as there is no
 * guarantee that it is parseable (i.e., a syntactically correct identifier).
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IIdentifierElement extends IInternalElement {

	/**
	 * Returns the string representation of the identifier contained in this
	 * element.
	 * 
	 * @return the identifier of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getIdentifierString() throws RodinDBException;

	/**
	 * Sets the string representation of the identifier contained in this
	 * element.
	 * 
	 * @param identifier
	 *            the string representation of the identifier
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setIdentifierString(String identifier) throws RodinDBException;

}
