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
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * <p>
 * Common interface for all traceable elements.
 * </p>
 * Some Elements in derived resources correspond exactly to to certain elements
 * entered by the user. These elements are called "traceable".
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface ITraceableElement extends IInternalElement {

	/**
	 * Sets the source element of this traceable element.
	 * @param source the source element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setSource(IRodinElement source, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the source element of this traceable element, or <code>null</code> 
	 * if there is no source element associated with this traceable element.
	 * 
	 * @return the source element
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IRodinElement getSource() throws RodinDBException;
}
