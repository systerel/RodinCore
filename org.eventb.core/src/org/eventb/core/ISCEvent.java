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
 * Common protocol for events in Event-B statically checked (SC) files.
 * <p>
 * An SC event has a label that is accessed and manipulated via
 * {@link ILabeledElement}.
 * </p>
 * <p>
 * SC events are similar to events ({@link org.eventb.core.IEvent}) except
 * that they contain statically checked elements:
 * <ul>
 * <li>refines clauses (<code>ISCRefinesEvent</code>)</li>
 * <li>local variables (<code>ISCVariable</code>)</li>
 * <li>witnesses (<code>ISCWitness</code>)</li>
 * <li>guards (<code>ISCGuard</code>)</li>
 * <li>actions (<code>ISCAction</code>)</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel(IProgressMonitor)
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 */
public interface ISCEvent extends ITraceableElement, ILabeledElement, IConvergenceElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scEvent"); //$NON-NLS-1$

	/**
	 * Returns an array of all SC refines clauses of this SC event.
	 * 
	 * @return an array of all SC refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCRefinesClauses(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCRefinesEvent[] getSCRefinesClauses() throws RodinDBException;

	/**
	 * Returns an array of all SC refines clauses of this SC event.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCRefinesEvent[] getSCRefinesClauses(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array of all SC events refined by this SC event.
	 * <p>
	 * This is a convenience method. It fetches all refines clauses of this
	 * events and gets the handles of the abstract events from there.
	 * </p>
	 * 
	 * @return an array of all abstract SC events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAbstractSCEvents(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCEvent[] getAbstractSCEvents() throws RodinDBException;

	/**
	 * Returns an array of all SC events refined by this SC event.
	 * <p>
	 * This is a convenience method. It fetches all refines clauses of this
	 * events and gets the handles of the abstract events from there.
	 * </p>
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all abstract SC events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCEvent[] getAbstractSCEvents(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all SC (local) variables of this SC event.
	 * 
	 * @return an array of all SC variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCVariables(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCVariable[] getSCVariables() throws RodinDBException;

	/**
	 * Returns an array containing all SC (local) variables of this SC event.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCVariable[] getSCVariables(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array of all SC witnesses of this SC event.
	 * 
	 * @return an array of all SC witnesses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCWitnesses(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCWitness[] getSCWitnesses() throws RodinDBException;

	/**
	 * Returns an array of all SC witnesses of this SC event.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC witnesses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCWitness[] getSCWitnesses(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all SC guards of this SC event.
	 * 
	 * @return an array of all SC guards
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCGuards(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCGuard[] getSCGuards() throws RodinDBException;

	/**
	 * Returns an array containing all SC guards of this SC event.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC guards
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCGuard[] getSCGuards(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all SC actions of this SC event.
	 * 
	 * @return an array of all SC actions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCActions(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCAction[] getSCActions() throws RodinDBException;

	/**
	 * Returns an array containing all SC actions of this SC event.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC actions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCAction[] getSCActions(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * An event label that has been used in an abstraction but not in some refinement
	 * cannot be used again. It is "forbidden".
	 * 
	 * @param value the "forbidden" status of the event label
	 * @deprecated use <code>setForbidden(boolean,IProgressMonitor)</code> instead
	 */
	@Deprecated
	void setForbidden(boolean value) throws RodinDBException;
	
	/**
	 * Returns whether the event label is forbidden or not.
	 * 
	 * @return whether the event label is forbidden or not
	 * @deprecated use <code>isForbidden(IProgressMonitor)</code> instead
	 */
	@Deprecated
	boolean isForbidden() throws RodinDBException;
	
	/**
	 * An event label that has been used in an abstraction but not in some refinement
	 * cannot be used again. It is "forbidden".
	 * 
	 * @param value the "forbidden" status of the event label
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setForbidden(boolean value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether the event label is forbidden or not.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return whether the event label is forbidden or not
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean isForbidden(IProgressMonitor monitor) throws RodinDBException;

}
