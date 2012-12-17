/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
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
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a refines clause of a statically checked machine.
 * <p>
 * A refines element has a name that is returned by
 * {@link IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface describes a statically checked version of
 * {@link IRefinesMachine}. The value stored in an
 * <code>ISCRefinesMachine</code> is a handle of the abstract SC machine.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface ISCRefinesMachine extends ITraceableElement {
	
	IInternalElementType<ISCRefinesMachine> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scRefinesMachine"); //$NON-NLS-1$

	/**
	 * Returns the abstract SC machine file introduced by this refines clause.
	 * 
	 * @return the abstract SC machine file ( <code>IRodinFile<ISCMachineRoot></code> )
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IRodinFile getAbstractSCMachine() throws RodinDBException;

	/**
	 * Sets the abstract SC machine file introduced by this refines clause.
	 * 
	 * @param abstractSCMachine
	 *            the abstract machine file
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractSCMachine(IRodinFile abstractSCMachine, IProgressMonitor monitor) throws RodinDBException;

}
