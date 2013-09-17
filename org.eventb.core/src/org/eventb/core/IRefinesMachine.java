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
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a refines clause in a machine.
 * <p>
 * An refines element has a name that is returned by
 * {@link IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface provides methods for accessing and manipulating the name of
 * the abstract machine. We call the machine that is refined by the machine
 * containing this element, the <em>abstract machine</em>. This is to avoid
 * confusion by using the term <em>refined machine</em> which could be either
 * machine in a refinement relationship.
 * </p>
 * <p>
 * The attribute storing the abstract machine name is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
 * </p>
 *
 * @see IRodinElement#getElementName()
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRefinesMachine extends IInternalElement {

	IInternalElementType<IRefinesMachine> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".refinesMachine"); //$NON-NLS-1$

	/**
	 * Tests whether the abstract machine name is defined or not.
	 * 
	 * @return whether the abstract machine name is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasAbstractMachineName() throws RodinDBException;
	
	/**
	 * Returns the name of the machine that is refined by the machine that
	 * contains this element.
	 * 
	 * @return the name of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getAbstractMachineName() throws RodinDBException;

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
	 */
	IRodinFile getAbstractMachine() throws RodinDBException;

	/**
	 * Returns a handle to the root of the unchecked version of the abstract
	 * machine.
	 * 
	 * @return a handle to the root of the unchecked version of the abstract
	 *         machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IMachineRoot getAbstractMachineRoot() throws RodinDBException;

	/**
	 * Returns a handle to the checked version of the abstract machine, that is
	 * the file produced when statically checking that machine.
	 * 
	 * @return a handle to the checked version of the abstract machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IRodinFile getAbstractSCMachine() throws RodinDBException;

	/**
	 * Returns a handle to the root of the checked version of the abstract
	 * machine, that is the root produced when statically checking that machine.
	 * 
	 * @return a handle to the root of the checked version of the abstract
	 *         machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCMachineRoot getAbstractSCMachineRoot() throws RodinDBException;

}
