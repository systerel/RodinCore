/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
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
public interface IEvent extends ICommentedElement, ILabeledElement, IConvergenceElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".event"); //$NON-NLS-1$
	
	/**
	 * The label of the initialisation event.
	 */
	String INITIALISATION = "INITIALISATION";

	/**
	 * Returns whether the event is inherited, that is whether it is
	 * automatically generated and maintained.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return <code>true</code> if the event is inherited.
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean isInherited(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Sets the the event to inherited.
	 * <p>
	 * The event must not have any children (guards, actions, ...) if 
	 * inherited is set to <code>true</code>.
	 * </p>
	 * @param inherited the new value specifying whether this event id
	 * interited or not.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database, or
	 * if the event has already children and it is attempted to specify it as inherited.
	 */
	void setInherited(boolean inherited, IProgressMonitor monitor) throws RodinDBException;

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
	 * 
	 * @return an array of all refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getRefinesClauses(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IRefinesEvent[] getRefinesClauses() throws RodinDBException;

	/**
	 * Returns an array of all refines clauses of this event.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IRefinesEvent[] getRefinesClauses(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to a child variable with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the variable
	 * @return a handle to a child variable with the given element name
	 */
	IVariable getVariable(String elementName);

	/**
	 * Returns an array containing all (local) variables of this event.
	 * 
	 * @return an array of all variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getVariables(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IVariable[] getVariables() throws RodinDBException;

	/**
	 * Returns an array containing all (local) variables of this event.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IVariable[] getVariables(IProgressMonitor monitor) throws RodinDBException;

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
	 * 
	 * @return an array of all witnesses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getWitnesses(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IWitness[] getWitnesses() throws RodinDBException;

	/**
	 * Returns an array of all witnesses of this event.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all witnesses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IWitness[] getWitnesses(IProgressMonitor monitor) throws RodinDBException;

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
	 * 
	 * @return an array of all guards
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getGuards(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IGuard[] getGuards() throws RodinDBException;

	/**
	 * Returns an array containing all guards of this event.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return an array of all guards
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IGuard[] getGuards(IProgressMonitor monitor) throws RodinDBException;

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
	 * 
	 * @return an array of all actions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getActions(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IAction[] getActions() throws RodinDBException;

	/**
	 * Returns an array containing all actions of this event.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all actions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IAction[] getActions(IProgressMonitor monitor) throws RodinDBException;

}
