/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
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

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B (unchecked) machines.
 * <p>
 * A machine has a name that is returned by
 * {@link IRodinElement#getElementName()}.
 * </p>
 * <p>
 * The elements contained in an event-B machine are:
 * <ul>
 * <li>at most one refines clause (<code>IRefinesMachine</code>)</li>
 * <li>sees clauses (<code>ISeesContext</code>)</li>
 * <li>variables (<code>IVariable</code>)</li>
 * <li>invariants (<code>IInvariant</code>)</li>
 * <li>theorems (<code>ITheorem</code>)</li>
 * <li>events (<code>IEvent</code>)</li>
 * <li>at most one variant (<code>IVariant</code>)</li>
 * </ul>
 * </p>
 * <p>
 * In addition to access methods for children elements, also access methods for
 * related file handles are provided.
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
public interface IMachineRoot extends IEventBRoot, ICommentedElement, IConfigurationElement {

	IInternalElementType<IMachineRoot> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".machineFile"); //$NON-NLS-1$

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
	IRefinesMachine getRefinesClause(String elementName);

	/**
	 * Returns the refines clause of this machine or <code>null</code> if this
	 * machine does not have an abstraction.
	 * 
	 * @return the refines clause of this machine or <code>null</code>
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getRefinesClauses(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IRefinesMachine getRefinesClause() throws RodinDBException;

	/**
	 * Returns an array of all refines clauses of this machine.
	 * @return an array of refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IRefinesMachine[] getRefinesClauses() throws RodinDBException;

	/**
	 * Returns a handle to a child sees clause with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the sees clause
	 * @return a handle to a child sees clause with the given element name
	 */
	ISeesContext getSeesClause(String elementName);

	/**
	 * Returns an array of all sees clauses of this machine.
	 * @return an array of sees clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISeesContext[] getSeesClauses() throws RodinDBException;

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
	 * Returns an array containing all (global) variables of this machine.
	 * @return an array of variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IVariable[] getVariables() throws RodinDBException;

	/**
	 * Returns a handle to a child invariant with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the invariant
	 * @return a handle to a child invariant with the given element name
	 */
	IInvariant getInvariant(String elementName);

	/**
	 * Returns an array containing all invariants of this machine.
	 * @return an array of invariants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IInvariant[] getInvariants() throws RodinDBException;

	/**
	 * Returns a handle to a child theorem with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the theorem
	 * @return a handle to a child theorem with the given element name
	 */
	@Deprecated
	ITheorem getTheorem(String elementName);

	/**
	 * Returns an array containing all theorems of this machine.
	 * 
	 * @return an array of theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	@Deprecated
	ITheorem[] getTheorems() throws RodinDBException;

	/**
	 * Returns a handle to a child event with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the event
	 * @return a handle to a child event with the given element name
	 */
	IEvent getEvent(String elementName);

	/**
	 * Returns an array containing all events of this machine.
	 * 
	 * @return an array of events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IEvent[] getEvents() throws RodinDBException;

	/**
	 * Returns a handle to a child variant with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the variant
	 * @return a handle to a child variant with the given element name
	 */
	IVariant getVariant(String elementName);

	/**
	 * Returns a handle to the variant of this machine.
	 * 
	 * @return a handle to the variant
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getVariants(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IVariant getVariant() throws RodinDBException;

	/**
	 * Returns an array containing all variants of this machine.
	 * 
	 * @return an array of variants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IVariant[] getVariants() throws RodinDBException;

}
