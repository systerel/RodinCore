/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a sees clause in a machine.
 * <p>
 * A sees element has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface provides methods for accessing and manipulating the name of
 * the seen context.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 */
public interface ISeesContext extends IInternalElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".seesContext"; //$NON-NLS-1$

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
	 * Sets the name of the context that is seen by the machine which contains
	 * this element.
	 * 
	 * @param name
	 *            the name of the seen context
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setSeenContextName(String name) throws RodinDBException;

	/**
	 * Returns a handle to the checked version of the seen context, that is the
	 * file produced when statically checking that context.
	 * 
	 * @return a handle to the checked version of the seen context
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCContextFile getSeenSCContext() throws RodinDBException;

}
