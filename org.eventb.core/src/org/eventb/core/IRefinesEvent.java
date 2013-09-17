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

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a refines clause in an event.
 * <p>
 * A refines element has a name that is returned by
 * {@link IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface provides methods for accessing and manipulating the name of
 * the abstract event. We call an event that is refined by the event containing
 * this clause, the <em>abstract event</em>. This is to avoid confusion by
 * using the term <em>refined event</em> which could be either event in a
 * refinement relationship.
 * </p>
 * <p>
 * The attribute storing the abstract event label is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
 * </p>
 *
 * @see IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRefinesEvent extends IInternalElement {

	IInternalElementType<IRefinesEvent> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".refinesEvent"); //$NON-NLS-1$

	/**
	 * Tests whether the abstract event label is defined or not.
	 * 
	 * @return whether the abstract event label is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasAbstractEventLabel() throws RodinDBException;
	
	/**
	 * Returns the label of the event that is refined by the event that contains
	 * this element.
	 * 
	 * @return the label of the abstract event
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getAbstractEventLabel() throws RodinDBException;

	/**
	 * Sets the label of the event that is refined by the event that contains
	 * this element.
	 * 
	 * @param label
	 *            the label of the abstract event
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractEventLabel(String label, IProgressMonitor monitor) throws RodinDBException;

	// No method getAbstractSCEvent(), as its implementation would involve
	// non-local operations on the database (essentially, getting to the
	// parent machine and finding the refines clause for the machine).

}
