/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a refines clause in an event.
 * <p>
 * A refines element has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface provides methods for accessing and manipulating the name of
 * the abstract event. We call an event that is refined by the event containing
 * this clause, the <em>abstract event</em>. This is to avoid confusion by
 * using the term <em>refined event</em> which could be either event in a
 * refinement relationship.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 */
public interface IRefinesEvent extends IInternalElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".refinesEvent"; //$NON-NLS-1$

	/**
	 * Returns the name of the event that is refined by the event that contains
	 * this element.
	 * 
	 * @return the name of the abstract event
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getAbstractEventName() throws RodinDBException;

	/**
	 * Sets the name of the event that is refined by the event that contains
	 * this element.
	 * 
	 * @param name
	 *            the name of the abstract event
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractEventName(String name) throws RodinDBException;

	// No method getAbstractSCEvent(), as its implementation would involve
	// non-local operations on the database (essentially, getting to the
	// parent machine and finding the refines clause for the machine).

}
