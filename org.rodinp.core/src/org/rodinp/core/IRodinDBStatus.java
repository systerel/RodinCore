/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.IJavaModelStatus.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

/**
 * Represents the outcome of a Rodin database operation. Status objects are used
 * inside <code>RodinDBException</code> objects to indicate what went wrong.
 * <p>
 * Rodin database status objects are distinguished by their plug-in id:
 * <code>getPlugin</code> returns <code>"org.rodinp.core"</code>.
 * <code>getCode</code> returns one of the status codes declared in
 * <code>IRodinDBStatusConstants</code>.
 * </p>
 * <p>
 * A Rodin database status may also carry additional information (that is, in
 * addition to the information defined in <code>IStatus</code>):
 * <ul>
 * <li>elements - optional handles to Rodin elements associated with the
 * failure</li>
 * <li>string - optional string associated with the failure</li>
 * </ul>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IStatus
 * @see IRodinDBStatusConstants
 * @since 1.0
 */
public interface IRodinDBStatus extends IStatus {

	/**
	 * Returns any Rodin elements associated with the failure (see specification
	 * of the status code), or an empty array if no elements are related to this
	 * particular status code.
	 * 
	 * @return the list of Rodin element culprits
	 * @see IRodinDBStatusConstants
	 */
	IRodinElement[] getElements();

	/**
	 * Returns the path associated with the failure (see specification of the
	 * status code), or <code>null</code> if the failure is not one of
	 * <code>DEVICE_PATH</code>, <code>INVALID_PATH</code>,
	 * <code>PATH_OUTSIDE_PROJECT</code>, or <code>RELATIVE_PATH</code>.
	 * 
	 * @return the path that caused the failure, or <code>null</code> if none
	 * @see IRodinDBStatusConstants#DEVICE_PATH
	 * @see IRodinDBStatusConstants#INVALID_PATH
	 * @see IRodinDBStatusConstants#PATH_OUTSIDE_PROJECT
	 * @see IRodinDBStatusConstants#RELATIVE_PATH
	 */
	IPath getPath();

	/**
	 * Returns whether this status indicates that a Rodin database element does
	 * not exist. This convenience method is equivalent to
	 * <code>getCode() == IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST</code>.
	 * 
	 * @return <code>true</code> if the status code indicates that a Rodin
	 *         database element does not exist
	 * @see IRodinDBStatusConstants#ELEMENT_DOES_NOT_EXIST
	 */
	boolean isDoesNotExist();

}
