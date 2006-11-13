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
 * Common protocol for Event-B elements that contain a predicate.
 * <p>
 * The predicate is manipulated as a bare string of characters, as there is no
 * guarantee that it is parseable.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface IPredicateElement extends IInternalElement {

	/**
	 * Returns the string representation of the predicate contained in this
	 * element.
	 * 
	 * @return the predicate of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getPredicateString(IProgressMonitor)</code> instead
	 */
	@Deprecated
	String getPredicateString() throws RodinDBException;

	/**
	 * Returns the string representation of the predicate contained in this
	 * element.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the predicate of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getPredicateString(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the string representation of the predicate contained in this
	 * element.
	 * 
	 * @param predicate
	 *            the string representation of the predicate
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>setPredicateString(String,IProgressMonitor)</code> instead
	 */
	@Deprecated
	void setPredicateString(String predicate) throws RodinDBException;

	/**
	 * Sets the string representation of the predicate contained in this
	 * element.
	 * 
	 * @param predicate
	 *            the string representation of the predicate
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setPredicateString(String predicate, IProgressMonitor monitor) throws RodinDBException;

}
