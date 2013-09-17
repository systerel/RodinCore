/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
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
 * The attribute storing the identifier string is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IIdentifierElement extends IInternalElement {
	
	/**
	 * Tests whether the identifier string is defined or not.
	 * 
	 * @return whether the identifier string is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasIdentifierString() throws RodinDBException;

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
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setIdentifierString(String identifier, IProgressMonitor monitor) throws RodinDBException;

}
