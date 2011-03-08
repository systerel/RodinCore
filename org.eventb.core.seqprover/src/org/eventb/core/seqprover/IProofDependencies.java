/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

/**
 * Interface encapsulating information that can be used to decide if a proof
 * is reusable with another sequent.
 * 
 * A proof is reusable for another sequent if:
 * <ul>
 * <li> It has no dependencies OR</li>
 * <li> The goal is <code>null</code> or is identical to the goal of the sequent and</li>
 * <li> All used hypotheses are contained in the hypotheses of the sequent and</li>
 * <li> All used free identifiers (with identical types) are contained in the 
 * type environment of the sequent and</li>
 * <li> No introduced free identifiers (ignoring their types) are present in the
 * type environment of the sequent</li>
 * </ul>
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public interface IProofDependencies{
	
	/**
	 * Returns if the proof does indeed have dependencies.
	 * The rest of the methods in this interface are only valid if 
	 * <code>hasDeps()</code> returns <code>true</code>.
	 * 
	 * @return <code>false</code> iff the proof can be reused for any sequent.
	 */
	boolean hasDeps();
	
	/**
	 * Returns the goal predicate of the proof, or <code>null</code> in the case
	 * where any goal will do.
	 * 
	 * In case the proof has no dependencies the result is undefined.
	 * 
	 * @return the goal predicate of the proof.
	 */
	Predicate getGoal();
	
	/**
	 * Returns the hypotheses used in a proof.
	 * 
	 * In case the proof has no dependencies the result is undefined.
	 * 
	 * @return the hypotheses used in a proof.
	 * 
	 */
	Set<Predicate> getUsedHypotheses();
	
	/**
	 * Returns the type environment corresponding to the used free
	 * identifiers in a proof.
	 * 
	 * In case the proof has no dependencies the result is undefined.
	 * 
	 * @return the type environment corresponding to the used free
	 *  		identifiers in a proof.
	 */
	ITypeEnvironment getUsedFreeIdents();
	
	/**
	 * Returns the names of the free identifiers introduced in a proof.
	 * These identifiers must remain fresh for the proof to be reused.
	 * 
	 * In case the proof has no dependencies the result is undefined.
	 * 
	 * @return the type environment corresponding to the free
	 *  		identifiers introduced in a proof.
	 */
	Set<String> getIntroducedFreeIdents();

}