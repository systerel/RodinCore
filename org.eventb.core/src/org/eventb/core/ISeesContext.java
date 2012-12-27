/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
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
 * Common protocol for a sees clause in a machine.
 * <p>
 * A sees element has a name that is returned by
 * {@link IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface provides methods for accessing and manipulating the name of
 * the seen context.
 * </p>
 * <p>
 * The attribute storing the seen context name is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
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
public interface ISeesContext extends IInternalElement {

	IInternalElementType<ISeesContext> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".seesContext"); //$NON-NLS-1$

	/**
	 * Tests whether the seen context name is defined or not.
	 * 
	 * @return whether the seen context name is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasSeenContextName() throws RodinDBException;
	
	/**
	 * Returns the name of the context that is seen by the machine which
	 * contains this element.
	 * 
	 * @return the name of the seen context
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getSeenContextName() throws RodinDBException;

	/**
	 * Returns a handle to the root of the unchecked version of the context that
	 * is seen by the machine which contains this element.
	 * 
	 * @return the seen context root
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IContextRoot getSeenContextRoot() throws RodinDBException;

	/**
	 * Sets the name of the context that is seen by the machine which contains
	 * this element.
	 * 
	 * @param name
	 *            the name of the seen context
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setSeenContextName(String name, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to the checked version of the seen context, that is the
	 * file produced when statically checking that context.
	 * @return a handle to the checked version of the seen context
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IRodinFile getSeenSCContext() throws RodinDBException;

}
