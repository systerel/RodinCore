/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a sees clause of a statically checked machine.
 * <p>
 * A sees element has a name that is returned by
 * {@link IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface describes a statically checked version of
 * {@link ISeesContext}. The value stored in an
 * <code>ISCSeesContext</code> is a handle of the seen SC context.
 * </p>
 *
 * @see IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISCSeesContext extends ITraceableElement {

	IInternalElementType<ISCSeesContext> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID
					+ ".scSeesContext"); //$NON-NLS-1$

	/**
	 * Returns the seen SC context file introduced by this sees clause.
	 * 
	 * @return the seen SC context file
	 * @throws CoreException
	 *             if there was a problem accessing the database, or the seen
	 *             context is invalid
	 */
	ISCContextRoot getSeenSCContext() throws CoreException;

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
	void setSeenSCContext(IRodinFile seenSCContext,
			IProgressMonitor monitor) throws RodinDBException;

}
