/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a sees clause of a statically checked machine.
 * <p>
 * A sees element has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface describes a statically checked version of
 * {@link org.eventb.core.ISeesContext}. The value stored in an
 * <code>ISCSeesContext</code> is a handle of the seen SC context.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 */
public interface ISCSeesContext extends ITraceableElement {

	IInternalElementType<ISCSeesContext> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID
					+ ".scSeesContext"); //$NON-NLS-1$

	/**
	 * Returns the seen SC context file introduced by this sees clause.
	 * 
	 * @return the seen SC context file
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCContextFile getSeenSCContext() throws RodinDBException;

	/**
	 * Sets the seen SC context file introduced by this sees clause.
	 * 
	 * @param seenSCContext
	 *            the seen SC context file
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setSeenSCContext(ISCContextFile seenSCContext,
			IProgressMonitor monitor) throws RodinDBException;

}
