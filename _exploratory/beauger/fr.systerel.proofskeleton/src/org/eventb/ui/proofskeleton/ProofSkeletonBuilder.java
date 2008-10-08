/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.proofskeleton;

import java.util.Collection;

import org.eventb.core.IPRProof;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
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
	public static IProverSequent buildRootSequent(IPRProof pr)
			throws RodinDBException {
		final FormulaFactory ff = FormulaFactory.getDefault();
		final LiteralPredicate bfalseGoal = ff.makeLiteralPredicate(
				Formula.BFALSE, null);
		final ITypeEnvironment env;
		final Collection<Predicate> hyps;
		final Predicate goal;
		final IProofDependencies deps = pr.getProofDependencies(ff, null);

		if (deps == null) {
			env = null;
			hyps = null;
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
		// TODO checks (see makeSequent)
		return ProverFactory.makeSequent(env, hyps, goal);
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
//		if (pr.getElementName().equals("thm0/THM"))
//			for (int i = 0; i < 500000000; i++) {
//				Integer g = new Integer(i);
//			}

		IProverSequent rootSequent = buildRootSequent(pr);
		IProofTree prTree = ProverFactory.makeProofTree(rootSequent, null);
		// Reuse method (see below) requires to check that its node is open.
		// By construction, a newly created root node is open (has no children).
		assert prTree.getRoot().isOpen();

		IProofSkeleton skel = pr.getSkeleton(FormulaFactory.getDefault(), null);

		 if (ProofBuilder.reuse(prTree.getRoot(), skel, monitor)) {
			 return prTree;
		 }

		return null;
	}

}
