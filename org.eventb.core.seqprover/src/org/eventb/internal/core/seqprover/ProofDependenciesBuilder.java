/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added used reasoners to proof dependencies
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.ProverFactory;

/**
 * This is a mutable implementation of {@link IProofDependencies} to be used with
 * the intention of collecting dependencies from the internal proof data structures
 * <p>
 * The goal my be modified using the setGoal() method. All other data structures are
 * mutable and are intended to be modified in-place.
 * </p>
 * 
 * @author Farhad Mehta
 */
public class ProofDependenciesBuilder {

	private Predicate goal = null;
	private final Set<Predicate> usedHypotheses = new HashSet<Predicate>();
	private final Set<FreeIdentifier> usedFreeIdents = new HashSet<FreeIdentifier>();
	private final Set<String> introducedFreeIdents = new HashSet<String>();
	private final Set<IReasonerDesc> usedReasoners = new HashSet<IReasonerDesc>();

	/**
	 * @return the goal
	 */
	public final Predicate getGoal() {
		return goal;
	}

	/**
	 * @param goal the goal to set
	 */
	public final void setGoal(Predicate goal) {
		this.goal = goal;
	}

	/**
	 * @return the introducedFreeIdents
	 */
	public final Set<String> getIntroducedFreeIdents() {
		return introducedFreeIdents;
	}

	/**
	 * @return the usedFreeIdents
	 */
	public final Set<FreeIdentifier> getUsedFreeIdents() {
		return usedFreeIdents;
	}

	/**
	 * @return the usedHypotheses
	 */
	public final Set<Predicate> getUsedHypotheses() {
		return usedHypotheses;
	}

	/**
	 * @return the used reasoners
	 */
	public Set<IReasonerDesc> getUsedReasoners() {
		return usedReasoners;
	}
	
	/**
	 * The last method to call after all dependencies have been calculated.
	 * 
	 * This should be the last method called on an instance of this class (i.e. the calling instance should
	 * be discarded after this method call)
	 * 
	 * @return The {@link IProofDependencies} collected by this {@link ProofDependenciesBuilder}.
	 */
	public final IProofDependencies finished(FormulaFactory ff){
		final boolean hasDeps = (goal != null ||
				! usedHypotheses.isEmpty() ||
				! usedFreeIdents.isEmpty() ||
				! introducedFreeIdents.isEmpty() ||
				! usedReasoners.isEmpty());
		final ITypeEnvironmentBuilder usedTypEnv = ff.makeTypeEnvironment();
		for (FreeIdentifier freeIdent : usedFreeIdents) {
			usedTypEnv.add(freeIdent);
		}
		
		final IProofDependencies finishedProofDeps = ProverFactory
				.makeProofDependencies(hasDeps, goal, usedHypotheses,
						usedTypEnv.makeSnapshot(), introducedFreeIdents,
						usedReasoners);
		return finishedProofDeps;
	}
	
}
