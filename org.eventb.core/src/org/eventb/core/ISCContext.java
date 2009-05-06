/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B statically checked (SC) context files.
 * <p>
 * There are two kinds of such contexts:
 * <ul>
 * <li>{@link org.eventb.core.ISCContextRoot} is a statically checked context
 * that corresponds directly to a context file
 * {@link org.eventb.core.IContextRoot}</li>
 * <li>{@link org.eventb.core.ISCInternalContext} is a statically checked
 * context that is stored inside another statically checked context or
 * statically checked machine. It is usually a copy of an
 * {@link org.eventb.core.ISCContextRoot}.</li>
 * <ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface ISCContext extends IRodinElement {

	/**
	 * Returns the name of the event-B component associated context.
	 * As an <code>ISCContext</code> can be backed by an <code>ISCInternalContext</code>
	 * or an <code>ISCContextFile</code> it returns in the former case the element name
	 * (i.e. the component name of the context from which it was copied), and in the latter
	 * case the proper component name using <code>IEventBFile.getComponentName()</code>.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @see IRodinElement#getElementName()
	 * 
	 * @return the name of the event-B component
	 */
	String getComponentName();

	/**
	 * Returns a handle to a child SC carrier set with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC carrier set
	 * @return a handle to a child SC carrier set with the given element name
	 */
	ISCCarrierSet getSCCarrierSet(String elementName);

	/**
	 * Returns an array containing all SC carrier sets of this SC context.
	 * @return an array of all SC carrier sets
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCCarrierSet[] getSCCarrierSets() throws RodinDBException;

	/**
	 * Returns a handle to a child SC constant with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC constant
	 * @return a handle to a child SC constant with the given element name
	 */
	ISCConstant getSCConstant(String elementName);

	/**
	 * Returns an array containing all SC constants of this SC context.
	 * 
	 * @return an array of all SC constants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCConstant[] getSCConstants() throws RodinDBException;

	/**
	 * Returns a handle to a child SC axiom with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC axiom
	 * @return a handle to a child SC axiom with the given element name
	 */
	ISCAxiom getSCAxiom(String elementName);

	/**
	 * Returns an array containing all SC axioms of this SC context.
	 * 
	 * @return an array of all SC axioms
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCAxiom[] getSCAxioms() throws RodinDBException;

	/**
	 * Returns a handle to a child SC theorem with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC theorem
	 * @return a handle to a child SC theorem with the given element name
	 */
	@Deprecated
	ISCTheorem getSCTheorem(String elementName);

	/**
	 * Returns an array containing all SC theorems of this SC context.
	 * 
	 * @return an array of all SC theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	@Deprecated
	ISCTheorem[] getSCTheorems() throws RodinDBException;
	
}
