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
 * Common protocol for Event-B events.
 * <p>
 * An event has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * The elements contained in an event are:
 * <ul>
 * <li>refines clauses (<code>IRefinesEvent</code>)</li>
 * <li>local variables (<code>IVariable</code>)</li>
 * <li>witnesses (<code>IWitness</code>)</li>
 * <li>guards (<code>IGuard</code>)</li>
 * <li>actions (<code>IAction</code>)</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Laurent Voisin
 */
public interface IEvent extends ILabeledElement, IInternalElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".event"; //$NON-NLS-1$

	/**
	 * Returns an array of all refines clauses of this event.
	 * 
	 * @return an array of all refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IRefinesEvent[] getRefinesClauses() throws RodinDBException;

	/**
	 * Returns an array containing all (local) variables of this event.
	 * 
	 * @return an array of all variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IVariable[] getVariables() throws RodinDBException;

	/**
	 * Returns an array of all witnesses of this event.
	 * 
	 * @return an array of all witnesses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IWitness[] getWitnesses() throws RodinDBException;

	/**
	 * Returns an array containing all guards of this event.
	 * 
	 * @return an array of all guards
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IGuard[] getGuards() throws RodinDBException;

	/**
	 * Returns an array containing all actions of this event.
	 * 
	 * @return an array of all actions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IAction[] getActions() throws RodinDBException;

}
