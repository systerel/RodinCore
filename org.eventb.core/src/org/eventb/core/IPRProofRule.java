/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - updated Javadoc
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofSkeleton;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof rules.
 * <p>
 * Clients should use the Proof Manager API rather than direct access to this
 * Rodin database API.
 * </p>
 *
 * @see IProofManager
 * 
 * @author Farhad Mehta
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPRProofRule extends IInternalElement {

	IInternalElementType<IPRProofRule> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prRule"); //$NON-NLS-1$

	IProofSkeleton getProofSkeleton(IProofStoreReader store, String comment)
			throws RodinDBException;

	void setProofRule(IProofSkeleton rule, IProofStoreCollector store,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to the antecedent child with the given name.
	 * <p>
	 * This is a handle-only method. The antecedent element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            name of the child
	 * 
	 * @return a handle to the child antecedent with the given name
	 * @see #getAntecedents()
	 */
	IPRRuleAntecedent getAntecedent(String name);

	/**
	 * Returns all children antecedent elements.
	 * 
	 * @return an array of all chidren element of type antecedent
	 * @throws RodinDBException
	 * @see #getAntecedent(String)
	 */
	IPRRuleAntecedent[] getAntecedents() throws RodinDBException;

	/**
	 * Returns a handle to a child element that can be used to store a list of
	 * expressions used by a reasoner input. The key gets automatically prefixed
	 * with a dot to prevent name collision with proper rule children.
	 * <p>
	 * This is a handle-only method. The antecedent element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param key
	 *            key used to register the list of expressions
	 * @return a handle to a child element carrying a list of expressions
	 *         registered by the rule reasoner with the given key
	 */
	IPRExprRef getPRExprRef(String key);

	/**
	 * Returns a handle to a child element that can be used to store a list of
	 * predicates used by a reasoner input. The key gets automatically prefixed
	 * with a dot to prevent name collision with proper rule children.
	 * <p>
	 * This is a handle-only method. The antecedent element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param key
	 *            key used to register the list of predicates
	 * @return a handle to a child element carrying a list of predicates
	 *         registered by the rule reasoner with the given key
	 */
	IPRPredRef getPRPredRef(String key);

	/**
	 * Returns a handle to a child element that can be used to store a string
	 * used by a reasoner input. The key gets automatically prefixed with a dot
	 * to prevent name collision with proper rule children.
	 * <p>
	 * This is a handle-only method. The antecedent element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param key
	 *            key used to register the string
	 * @return a handle to a child element carrying a string registered by the
	 *         rule reasoner with the given key
	 */
	IPRStringInput getPRStringInput(String key);

}
