/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.core;

/**
 * Common protocol for Rodin problems. A problem is characterized by
 * <ul>
 * <li>a severity
 * <li>an error code (for plug-ins)
 * <li>a localized error message (for the end-user)
 * </ul>
 * Instances of this class group all this information. By contrast, all
 * information dependent on the place where a problem actually occurs, which is
 * stored in a Rodin problem marker, is not part of this interface.
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @author Laurent Voisin
 * @see IRodinElement#createProblemMarker(IRodinProblem, Object[])
 * @see IInternalElement#createProblemMarker(IAttributeType.String, int, int,
 *      IRodinProblem, Object[])
 * @see IInternalElement#createProblemMarker(IAttributeType, IRodinProblem,
 *      Object[])
 * @since 1.0
 */
public interface IRodinProblem {

	/**
	 * Returns the severity of this problem.
	 * 
	 * @return the severity of this problem (one of error, warning, info).
	 * @see org.eclipse.core.resources.IMarker#SEVERITY
	 */
	int getSeverity();

	/**
	 * Returns the error code of this problem.
	 * 
	 * @return the error code of this problem
	 * @see RodinMarkerUtil#ERROR_CODE
	 */
	String getErrorCode();

	/**
	 * Returns a localized message describing this problem for the end-user. The
	 * returned message is built with the given arguments.
	 * 
	 * @param args
	 *            arguments for the message
	 * @return a message describing the problem for the end-user
	 * @see org.eclipse.core.resources.IMarker#MESSAGE
	 */
	String getLocalizedMessage(Object[] args);

}
