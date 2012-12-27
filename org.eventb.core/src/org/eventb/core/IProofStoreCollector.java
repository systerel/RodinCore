/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved used reasoners to proof root
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerDesc;
import org.rodinp.core.RodinDBException;

/**
 * Collects formulae for the proof store
 * 
 * @author Farhad Mehta
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 1.0
 */
public interface IProofStoreCollector {

	/**
	 * Puts the given predicate in the store and returns a reference to it.
	 * 
	 * @param pred
	 *            a predicate
	 * @return a string reference
	 * @throws RodinDBException
	 */
	String putPredicate(Predicate pred) throws RodinDBException;

	/**
	 * Puts the given expression in the store and returns a reference to it.
	 * 
	 * @param expr
	 *            an expression
	 * @return a string reference
	 * @throws RodinDBException
	 */
	String putExpression(Expression expr) throws RodinDBException;
	
	/**
	 * Puts the given reasoner in the store and returns a reference to it.
	 * 
	 * @param reasoner
	 *            a reasoner descriptor
	 * @return a string reference
	 * @since 2.2
	 */
	String putReasoner(IReasonerDesc reasoner);

	/**
	 * Writes out collected predicates, expressions and reasoners to the given
	 * proof.
	 * 
	 * @param prProof
	 *            a proof
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 */
	void writeOut(IPRProof prProof, IProgressMonitor monitor) throws RodinDBException;

	
	
}
