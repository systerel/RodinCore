/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an extends clause of a statically checked context.
 * <p>
 * An extends element has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface describes a statically checked version of
 * {@link org.eventb.core.IExtendsContext}. The value stored in an
 * <code>ISCExtendsContext</code> is a handle of the abstract SC context.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface ISCExtendsContext extends ITraceableElement {

	IInternalElementType<ISCExtendsContext> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID
					+ ".scExtendsContext"); //$NON-NLS-1$

	/**
	 * Returns the abstract SC context file introduced by this extends clause.
	 * 
	 * @return the abstract SC context file
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCContextRoot getAbstractSCContext() throws RodinDBException;

	/**
	 * Sets the abstract SC context file introduced by this extends clause.
	 * 
	 * @param abstractSCContext
	 *            the abstract context file
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractSCContext(ISCContextRoot abstractSCContext,
			IProgressMonitor monitor) throws RodinDBException;

}
