/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.RodinDBException;


/**
 * Common protocol for Event-B statically checked (SC) contexts.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A statically checked context is similar to a context but contains
 * some additional types of elements:
 * <ul>
 * <li>it contains one axiom set that conatins all axioms of all abstractions of the context
 *   (ISCAxiomSet)</li>
 * <li>it contains one theorem set that conatins all theorems of all abstractions of the context
 *   (ISCTheoremSet)</li>
 * <li>it cointains type informations for all constants of the context and all its abstractions.
 *   (IPOIdentifier)</li>
 * <li>it conatins constants and sets of all abstraction with an attribute marking their origin.
 *   (IConstant and ICarrierSet)</li>
 * </ul>
 * The rest of the elements are the same as for contexts.
 * Theorems and Axioms of abstractions of a context are refered to as "old".
 * </p>
 * @author Stefan Hallerstede
 * 
 */
public interface ISCContext extends IContext {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scContext"; //$NON-NLS-1$
	
	/**
	 * Returns a handle to the unchecked version of this context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of this context
	 */
	public IContext getContext();

	ISCCarrierSet[] getSCCarrierSets() throws RodinDBException;
	ISCConstant[] getSCConstants() throws RodinDBException;
	IAxiom[] getOldAxioms() throws RodinDBException;
	ITheorem[] getOldTheorems() throws RodinDBException;
	
}
