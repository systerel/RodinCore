/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
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
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;

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
	 * @throws CoreException
	 */
	private static IProverSequent buildRootSequent(IPRProof pr,
			FormulaFactory ff, IProgressMonitor pm)
			throws CoreException {
		final LiteralPredicate bfalseGoal = ff.makeLiteralPredicate(
				Formula.BFALSE, null);

		final ITypeEnvironment env;
		final Collection<Predicate> hyps;
		final Predicate goal;
		final IProofDependencies deps = pr.getProofDependencies(ff, pm);

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
	 * @param pm
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return the computed IProofTree, or null if the proof tree could not be
	 *         rebuilt.
	 * @throws CoreException 
	 */
	public static IProofTree buildProofTree(IPRProof pr, IProgressMonitor pm)
			throws CoreException {
		final SubMonitor sm = SubMonitor.convert(pm, 110);
		final IProofComponent pc = getProofComponent(pr);
		FormulaFactory ff;
		boolean isBroken = false;
		try {
			ff = pr.getFormulaFactory(sm.newChild(10));
		} catch (CoreException e) {
			// exception already logged
			isBroken = true;
			ff = pc.getFormulaFactory();
		}

		if (sm.isCanceled()) {
			return null;
		}

		final IProverSequent rootSequent = buildRootSequent(pr, ff, sm.newChild(40));
		if (rootSequent == null) {
			logIllFormedProof(pr);
			return null;
		}

		if (sm.isCanceled()) {
			return null;
		}
		
		final IProofSkeleton skel = pc.getProofSkeleton(pr.getElementName(),
				ff, sm.newChild(40));
		if (sm.isCanceled()) {
			return null;
		}

		final IProofTree prTree = ProverFactory.makeProofTree(rootSequent, pr);
		if (isBroken) {
			if (!ProofBuilder.rebuild(prTree.getRoot(), skel, new ProofMonitor(sm))) {
				return null;
			}
		} else if (!ProofBuilder.reuse(prTree.getRoot(), skel, new ProofMonitor(sm))) {
			logIllFormedProof(pr);
			return null;
		}
		return prTree;
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
