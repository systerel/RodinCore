/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
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
 *
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISCEvent 
extends ITraceableElement, ILabeledElement, IConvergenceElement, IAccuracyElement {

	IInternalElementType<ISCEvent> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scEvent"); //$NON-NLS-1$

	/**
	 * Returns a handle to a child SC refines clause with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC refines clause
	 * @return a handle to a child SC refines clause with the given element name
	 */
	ISCRefinesEvent getSCRefinesClause(String elementName);

	/**
	 * Returns an array of all SC refines clauses of this SC event.
	 * 
	 * @return an array of all SC refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCRefinesEvent[] getSCRefinesClauses() throws RodinDBException;

	/**
	 * Returns an array of all SC events refined by this SC event.
	 * <p>
	 * This is a convenience method. It fetches all refines clauses of this
	 * events and gets the handles of the abstract events from there.
	 * </p>
	 * 
	 * @return an array of all abstract SC events
	 * @throws CoreException
	 *             if there was a problem accessing the database, or one
	 *             abstract SC event is invalid
	 */
	ISCEvent[] getAbstractSCEvents() throws CoreException;

	/**
	 * Returns a handle to a child SC parameter with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC parameter
	 * @return a handle to a child SC parameter with the given element name
	 */
	ISCParameter getSCParameter(String elementName);

	/**
	 * Returns an array containing all SC parameters of this SC event.
	 * 
	 * @return an array of all SC parameters
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCParameter[] getSCParameters() throws RodinDBException;

	/**
	 * Returns a handle to a child SC witness with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC witness
	 * @return a handle to a child SC witness with the given element name
	 */
	ISCWitness getSCWitness(String elementName);

	/**
	 * Returns an array of all SC witnesses of this SC event.
	 * 
	 * @return an array of all SC witnesses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCWitness[] getSCWitnesses() throws RodinDBException;

	/**
	 * Returns a handle to a child SC guard with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC guard
	 * @return a handle to a child SC guard with the given element name
	 */
	ISCGuard getSCGuard(String elementName);

	/**
	 * Returns an array containing all SC guards of this SC event.
	 * 
	 * @return an array of all SC guards
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCGuard[] getSCGuards() throws RodinDBException;

	/**
	 * Returns a handle to a child SC action with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC action
	 * @return a handle to a child SC action with the given element name
	 */
	ISCAction getSCAction(String elementName);

	/**
	 * Returns an array containing all SC actions of this SC event.
	 * 
	 * @return an array of all SC actions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCAction[] getSCActions() throws RodinDBException;
	
	/**
	 * Returns the type environment of this event, given the type environment of
	 * its parent machine. The returned type environment is made of a copy of
	 * the given machine type environment together with all the local variables
	 * defined in this event. The given type environment is not modified by this
	 * method.
	 * <p>
	 * The returned type environment can be used subsequently to type-check the
	 * guards and actions of this event. It might not be appropriate for
	 * type-checking the witnesses of this event.
	 * </p>
	 * 
	 * @param mchTypenv
	 *            type environment of the parent machine
	 * @return the type environment of this event
	 * @throws CoreException
	 *             if there was a problem accessing the database, or computing
	 *             the type environment
	 * @see ISCMachineRoot#getTypeEnvironment()
	 * @since 3.0
	 */
	ITypeEnvironmentBuilder getTypeEnvironment(ITypeEnvironment mchTypenv)
			throws CoreException;

}
