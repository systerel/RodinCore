/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core;

import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.FalseClause;

/**
 * Base interface for a prover module. 
 * <p>
 * Methods in this interface are called by the {@link ClauseDispatcher} in a
 * certain order. Clauses are added to the module using {@link #addClauseAndDetectContradiction(Clause)}.
 * This method should return clauses that are derived when trying to detect
 * a contradiction. Method {@link #next(boolean)} is then called on each prover
 * module until one of them returns a result different from {@link ProverResult#EMPTY_RESULT}.
 * <p>
 * When a contradiction is detected, the level is first adjusted by the {@link ClauseDispatcher}
 * and the proof is terminated if the base level is closed. If not, {@link #contradiction(Level, Level, Set)}
 * is first called on each prover module. Then, the {@link ClauseDispatcher} backtracks
 * its data structure and calls {@link #removeClause(Clause)} on each prover module
 * for all backtracked clauses.
 * <p>
 * 
 *
 * @author François Terrier
 *
 */
public interface IProverModule {
	
	/**
	 * Returns the next prover result or {@link ProverResult#EMPTY_RESULT}
	 * if nothing can be inferred.
	 * <p>
	 * A prover module can return {@link ProverResult#EMPTY_RESULT} even if
	 * something can be inferred. It happens when the prover module blocks 
	 * itself, to allow other modules to be called. However, if <code>force</code>
	 * is <code>true</code> the module must return a result if it has one.
	 * <p>
	 * TODO the module blocking mechanism should better go outside each module
	 * in the {@link ClauseDispatcher}, since it can control better what module
	 * should be called next.
	 * 
	 * @param force <code>true</code> to enforce this module to return a non-empty result
	 * @return the result, and never <code>null</code>
	 */
	public ProverResult next(boolean force);

	/**
	 * Adds the specified clause to this module and tries to derive a contradiction
	 * from it. All clauses inferred while trying to derive a contradiction are 
	 * usually returned by this method. If a contradiction is found, the {@link FalseClause}
	 * corresponding to the contradiction must be contained in the result.
	 * 
	 * @param clause the clause to be added
	 * @return the clauses derived when trying to detect a contradiction, never <code>null</code>
	 */
	public ProverResult addClauseAndDetectContradiction(Clause clause);

	/**
	 * Removes the clause from this module.
	 * <p>
	 * TODO perhaps let this method return a {@link ProverResult}
	 * 
	 * @param clause the clause to remove
	 */
	public void removeClause(Clause clause);
	
	/**
	 * Lets this prover module update its data structure from the old to the new
	 * level. The result contains clauses derived by this prover module and by 
	 * construction it cannot contain any TRUE or FALSE clauses.
	 * <p>
	 * When this method is called, {@link ILevelController#getCurrentLevel()} returns
	 * the same value as the one in newLevel, i.e. 
	 * <code>newLevel.equals(levelController.getCurrentLevel()) == true</code>. This
	 * holds until {@link ILevelController#nextLevel()} is called.
	 * 
	 * @param oldLevel the level before the contradiction has been derived
	 * @param newLevel the level after the contradiction has been derived
	 * @param dependencies the level dependencies of the contradiction
	 * @return the result, can never be <code>null</code>
	 */
	public ProverResult contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies);
	
	/**
	 * Lets this prover module register the data structure it wishes be dumped.
	 * 
	 * @param dumper the dumper on which the data structures can be registered
	 */
	public void registerDumper(Dumper dumper);
	
}
