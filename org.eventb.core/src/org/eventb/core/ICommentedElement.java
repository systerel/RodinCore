/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for elements that carry a comment. A comment is a text string
 * that the user associates with the element and give more information about the
 * element.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author htson
 * 
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ICommentedElement extends IInternalElement {

	/**
	 * Sets the comment contained in this element.
	 * 
	 * @param comment
	 *            the comment for the element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setComment(String comment, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Tells whether this element carries a comment.
	 * 
	 * @return <code>true</code> iff this element has a comment attached to it
	 * 
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean hasComment() throws RodinDBException;

	/**
	 * Returns the comment contained in this element.
	 * 
	 * @return the comment attached to this element
	 * 
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getComment() throws RodinDBException;

}
