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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a refines clause of an event.
 * <p>
 * A refines element has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface describes a statically checked version of
 * {@link org.eventb.core.IRefinesEvent}. The value stored in an
 * <code>ISCRefinesEvent</code> is a handle of the abstract SC event.
 * </p>
 *
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISCRefinesEvent extends ITraceableElement, IInternalElement {

	IInternalElementType<ISCRefinesEvent> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scRefinesEvent"); //$NON-NLS-1$

	/**
	 * Returns the abstract event introduced by this refines clause.
	 * 
	 * @return the abstract SC event
	 * @throws CoreException
	 *             if there was a problem accessing the database, or if the
	 *             abstract SC event is invalid
	 */
	ISCEvent getAbstractSCEvent() throws CoreException;

	/**
	 * Sets the abstract event introduced by this refines clause.
	 * 
	 * @param abstractSCEvent
	 *            the abstract event
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractSCEvent(ISCEvent abstractSCEvent, IProgressMonitor monitor) throws RodinDBException;

}
