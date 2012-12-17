/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added isInitialisation() method
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B events.
 * <p>
 * An event has a name that is returned by
 * {@link IRodinElement#getElementName()}.
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
 * The attribute storing whether teh event is inherited is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IRodinElement#getElementName()
 * 
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface IEvent extends ICommentedElement, ILabeledElement, IConvergenceElement {

	IInternalElementType<IEvent> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".event"); //$NON-NLS-1$
	
	/**
	 * The label of an initialisation event.
	 */
	String INITIALISATION = "INITIALISATION";

	/**
	 * Tests whether the extended attribute value is defined or not.
	 * 
	 * @return whether the extended attribute value is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasExtended() throws RodinDBException;
	
	/**
	 * Returns whether the event is extended, that is whether the abstract children
	 * are copied (in particular, parameters, guards, and actions) except refines 
	 * clauses and witnesses.
	 * <p>
	 * The concrete event has to set all attributes of the event and may add additional
	 * children to an extended event.
	 * </p>
	 * 
	 * @return <code>true</code> if the event is extended.
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean isExtended() throws RodinDBException;

	/**
	 * Returns whether this event is an initialisation event. An event is an
	 * initialisation event iff its label is <code>INITIALISATION</code>.
	 * 
	 * @return <code>true</code> if the event is an initialisation event
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean isInitialisation() throws RodinDBException;
	
	/**
	 * Marks the event as extended.
	 * 
	 * @param extended the new value specifying whether this event id
	 * extended or not.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database.
	 */
	void setExtended(boolean extended, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to a child refines clause with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the refines clause
	 * @return a handle to a child refines clause with the given element name
	 */
	IRefinesEvent getRefinesClause(String elementName);
	
	/**
	 * Returns an array of all refines clauses of this event.
	 * @return an array of all refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IRefinesEvent[] getRefinesClauses() throws RodinDBException;

	/**
	 * Returns a handle to a child parameter with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the parameter
	 * @return a handle to a child parameter with the given element name
	 */
	IParameter getParameter(String elementName);

	/**
	 * Returns an array containing all parameters of this event.
	 * @return an array of all parameters
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IParameter[] getParameters() throws RodinDBException;

	/**
	 * Returns a handle to a child witness with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the witness
	 * @return a handle to a child witness with the given element name
	 */
	IWitness getWitness(String elementName);

	/**
	 * Returns an array of all witnesses of this event.
	 * @return an array of all witnesses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IWitness[] getWitnesses() throws RodinDBException;

	/**
	 * Returns a handle to a child guard with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the guard
	 * @return a handle to a child guard with the given element name
	 */
	IGuard getGuard(String elementName);

	/**
	 * Returns an array containing all guards of this event.
	 * @return an array of all guards
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IGuard[] getGuards() throws RodinDBException;

	/**
	 * Returns a handle to a child action with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the action
	 * @return a handle to a child action with the given element name
	 */
	IAction getAction(String elementName);

	/**
	 * Returns an array containing all actions of this event.
	 * @return an array of all actions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IAction[] getActions() throws RodinDBException;

}
