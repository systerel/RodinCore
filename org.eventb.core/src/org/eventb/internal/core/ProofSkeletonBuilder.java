/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - got formula factory from proof component
 *     Systerel - set the origin of the built sequent
 *******************************************************************************/
package org.eventb.internal.core;

import java.util.Collection;
import java.util.Collections;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.rodinp.core.RodinDBException;

/**
 * Core methods for rebuilding a IProofTree from a IPRProof.
 * 
 * @author Nicolas Beauger
 *
 */
public class ProofSkeletonBuilder {

	/**
	 * Computes the root sequent of the given proof.
	 * 
	 * @param pr
	 *            the input proof.
	 * @return the IProverSequent that is the root node of the proof tree.
	 * @throws RodinDBException
	 */
	private static IProverSequent buildRootSequent(IPRProof pr, FormulaFactory ff)
			throws RodinDBException {
		final LiteralPredicate bfalseGoal = ff.makeLiteralPredicate(
				Formula.BFALSE, null);

		final ITypeEnvironment env;
		final Collection<Predicate> hyps;
		final Predicate goal;
		final IProofDependencies deps = pr.getProofDependencies(ff, null);

		if (!deps.hasDeps()) {
			env = ff.makeTypeEnvironment();
			hyps = Collections.emptyList();
			goal = bfalseGoal;
		} else {
			env = deps.getUsedFreeIdents();
			hyps = deps.getUsedHypotheses();
			if (deps.getGoal() == null) {
				goal = bfalseGoal;
			} else {
				goal = deps.getGoal();
			}
		}
		// making snapshot now to share it in all typechecks
		final ISealedTypeEnvironment tenv = env.makeSnapshot();
		
		if (!check(tenv, hyps, goal)) {
			return null;
		}
		return ProverFactory.makeSequent(tenv, hyps, null, goal, pr);
	}

	private static boolean check(ISealedTypeEnvironment env,
			Collection<Predicate> hyps, Predicate goal) {
		boolean hasProblem = goal.typeCheck(env).hasProblem();
		for (Predicate hyp : hyps) {
			hasProblem |= hyp.typeCheck(env).hasProblem();
		}
		return !hasProblem;
	}
	
	/**
	 * Builds the IProofTree corresponding to the given IPRProof.
	 * 
	 * @param pr
	 *            the IPRProof to build the IProofTree from.
	 * @param monitor
	 *            the IProofMonitor that manages the computation (can be
	 *            <code>null</code>).
	 * @return the computed IProofTree, or null if the proof tree could not be
	 *         rebuilt.
	 * @throws RodinDBException
	 */
	public static IProofTree buildProofTree(IPRProof pr, IProofMonitor monitor)
			throws RodinDBException {
		final IProofComponent pc = getProofComponent(pr);
		final FormulaFactory ff = pc.getFormulaFactory();
	
		final IProverSequent rootSequent = buildRootSequent(pr, ff);
		if (rootSequent == null) {
			logIllFormedProof(pr);
			return null;
		}

		if (monitor != null && monitor.isCanceled()) {
			return null;
		}
		
		final IProofSkeleton skel = pc.getProofSkeleton(pr.getElementName(),
				ff, null);
		final IProofTree prTree = ProverFactory.makeProofTree(rootSequent, null);
		if (ProofBuilder.reuse(prTree.getRoot(), skel, monitor)) {
			return prTree;
		} else {
			logIllFormedProof(pr);
			return null;
		}
	}

	private static void logIllFormedProof(IPRProof pr) {
		Util.log(null, "ill-formed proof: " + pr.getPath());
	}

	private static IProofComponent getProofComponent(IPRProof pr) {
		final IProofManager pm = EventBPlugin.getProofManager();
		final IEventBRoot prRoot = (IEventBRoot) pr.getRoot();
		return pm.getProofComponent(prRoot);
	}

}
