/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IIdentifierElement extends IInternalElement {

	/**
	 * This method is deprecated; use <code>getIdentifierString(IProgressMonitor)</code> instead.
	 * 
	 * Returns the string representation of the identifier contained in this
	 * element.
	 * 
	 * @return the identifier of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	@Deprecated
	String getIdentifierString() throws RodinDBException;

	/**
	 * This method is deprecated; use <code>setIdentifierString(String,IProgressMonitor)</code> instead.
	 * 
	 * Sets the string representation of the identifier contained in this
	 * element.
	 * 
	 * @param identifier
	 *            the string representation of the identifier
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	@Deprecated
	void setIdentifierString(String identifier) throws RodinDBException;

	/**
	 * Returns the string representation of the identifier contained in this
	 * element.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the identifier of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getIdentifierString(IProgressMonitor monitor) throws RodinDBException;

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
