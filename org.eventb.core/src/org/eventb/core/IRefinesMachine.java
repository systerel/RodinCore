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
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a refines clause in a machine.
 * <p>
 * An refines element has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface provides methods for accessing and manipulating the name of
 * the abstract machine. We call the machine that is refined by the machine
 * containing this element, the <em>abstract machine</em>. This is to avoid
 * confusion by using the term <em>refined machine</em> which could be either
 * machine in a refinement relationship.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Laurent Voisin
 */
public interface IRefinesMachine extends IInternalElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".refinesMachine"); //$NON-NLS-1$

	/**
	 * Returns the name of the machine that is refined by the machine that
	 * contains this element.
	 * 
	 * @return the name of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAbstractMachineName(IProgressMonitor)</code> instead
	 */
	@Deprecated
	String getAbstractMachineName() throws RodinDBException;

	/**
	 * Returns the name of the machine that is refined by the machine that
	 * contains this element.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the name of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getAbstractMachineName(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the name of the machine that is refined by the machine that contains
	 * this element.
	 * 
	 * @param name
	 *            the name of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>setAbstractMachineName(String,IProgressMonitor)</code> instead
	 */
	@Deprecated
	void setAbstractMachineName(String name) throws RodinDBException;

	/**
	 * Sets the name of the machine that is refined by the machine that contains
	 * this element.
	 * 
	 * @param name
	 *            the name of the abstract machine
	 * @param monitor a progress monitor
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractMachineName(String name, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to the unchecked version of the abstract machine.
	 * 
	 * @return a handle to the unchecked version of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAbstractMachine(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IMachineFile getAbstractMachine() throws RodinDBException;

	/**
	 * Returns a handle to the unchecked version of the abstract machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return a handle to the unchecked version of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IMachineFile getAbstractMachine(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to the checked version of the abstract machine, that is
	 * the file produced when statically checking that machine.
	 * 
	 * @return a handle to the checked version of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAbstractSCMachine(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCMachineFile getAbstractSCMachine() throws RodinDBException;

	/**
	 * Returns a handle to the checked version of the abstract machine, that is
	 * the file produced when statically checking that machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return a handle to the checked version of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCMachineFile getAbstractSCMachine(IProgressMonitor monitor) throws RodinDBException;

}
