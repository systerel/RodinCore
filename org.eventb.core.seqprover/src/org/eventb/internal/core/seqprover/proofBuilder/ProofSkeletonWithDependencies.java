/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.core.seqprover.proofBuilder;

import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.internal.core.seqprover.ProofDependenciesBuilder;
import org.eventb.internal.core.seqprover.ProofRule;

/**
 * A data structure used to store a skeleton with his dependencies associated. *
 */
public class ProofSkeletonWithDependencies implements IProofSkeleton {

	private final IProofRule skeletonRule;
	private final String skeletonComment;
	private final ProofDependenciesBuilder dependencies;
	private final ProofSkeletonWithDependencies[] children;
	private final boolean trivialCase;

	private ProofSkeletonWithDependencies(String comment, IProofRule rule,
			ProofSkeletonWithDependencies[] children,
			ProofDependenciesBuilder dependencies) {
		this.skeletonRule = rule;
		this.skeletonComment = comment;
		this.dependencies = dependencies;
		this.trivialCase = isTrivialCase(rule);
		this.children = children;

	}

	/**
	 * Determinate if the rule of the skeleton is a trivial case (hyp, true goal
	 * or contradictory hypothesis)
	 */
	private static boolean isTrivialCase(IProofRule rule) {
		if (rule == null) {
			return false;
		}
		final String reasonerID = rule.getReasonerDesc().getId();
		return reasonerID.equals(SequentProver.PLUGIN_ID + ".hyp")
				|| reasonerID.equals(SequentProver.PLUGIN_ID + ".trueGoal")
				|| reasonerID.equals(SequentProver.PLUGIN_ID + ".contrHyps");
	}

	/**
	 * Static method that allows to associate his dependencies to a proof
	 * skeleton.
	 * 
	 * @param skeleton
	 *            The proof skeleton
	 * @return The proof skeleton with his dependencies associated.
	 */
	public static ProofSkeletonWithDependencies withDependencies(
			IProofSkeleton skel) {
		if (skel instanceof ProofSkeletonWithDependencies) {
			return (ProofSkeletonWithDependencies) skel;
		}
		final IProofSkeleton[] skelChildren = skel.getChildNodes();
		final ProofSkeletonWithDependencies[] children = new ProofSkeletonWithDependencies[skelChildren.length];
		for (int i = 0; i < skelChildren.length; i++) {
			children[i] = withDependencies(skelChildren[i]);
		}
		final IProofRule rule = skel.getRule();
		final ProofDependenciesBuilder deps = computeDependencies(rule,
				children);
		return new ProofSkeletonWithDependencies(skel.getComment(), rule,
				children, deps);
	}

	/**
	 * Compute the dependencies for this node and his children.
	 * 
	 * @return dependencies of this node.
	 */
	private static ProofDependenciesBuilder computeDependencies(
			IProofRule rule, ProofSkeletonWithDependencies[] children) {
		if (rule == null) {
			return new ProofDependenciesBuilder();
		}
		final ProofDependenciesBuilder[] childProofDeps = new ProofDependenciesBuilder[children.length];
		for (int i = 0; i < children.length; i++) {
			childProofDeps[i] = children[i].dependencies;
		}
		return ((ProofRule) rule).processDeps(childProofDeps);

	}

	public ProofSkeletonWithDependencies[] getChildNodes() {
		return children;
	}

	public String getComment() {
		return skeletonComment;
	}

	public IProofRule getRule() {
		return skeletonRule;
	}

	/**
	 * Traverses the given skeleton tree in search of a skeleton compatible with
	 * the given proof tree node. Tries to discharge the trivial cases first
	 * (true goal,...) and then tries to discharge the other cases.
	 * 
	 * 
	 * @param node
	 *            The open proof tree node where rebuilding should start
	 * @param dischargeTrivialCase
	 *            if true, apply all the trivial rules of the skeleton to the
	 *            node. If false, try to find a compatible node in the other
	 *            rules from the skeleton.
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the rebuild
	 *            activity
	 * @return true iff the proof has been correctly rebuilt.
	 */
	public boolean applyTo(IProofTreeNode node, boolean dischargeTrivialCase,
			IProofMonitor proofMonitor) {

		return rebuildFromChildren(node, 0, children.length,
				dischargeTrivialCase, proofMonitor);
	}

	/**
	 * For each node child, tries to find a compatible skeleton node to make a
	 * rebuild. Tries to discharge the trivial cases first (true goal,...) and
	 * then tries to discharge the other cases.
	 * 
	 * @param nodeChildren
	 *            children of the open proof tree node where rebuilding should
	 *            start
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the rebuild
	 *            activity
	 * @param dischargeTrivialCase
	 *            if true, apply all the trivial rules of the skeleton to the
	 *            node. If false, try to find a compatible node in the other
	 *            rules from the skeleton.
	 * @return true iff the proof has been correctly rebuilt.
	 */
	public boolean rebuildUnsortedChildren(IProofTreeNode[] nodeChildren,
			IProofMonitor proofMonitor, boolean dischargeTrivialCase) {

		boolean combinedSuccess = true;
		boolean success = false;

		for (int i = 0; i < nodeChildren.length; i++) {

			success = rebuildFromChildren(nodeChildren[i], i, children.length,
					dischargeTrivialCase, proofMonitor);

			if (!success) {

				success = rebuildFromChildren(nodeChildren[i], 0, i,
						dischargeTrivialCase, proofMonitor);
			}

			combinedSuccess &= success;
		}
		return combinedSuccess;
	}

	/**
	 * Allows to traverse the children of a given skeleton more efficiently in
	 * search for an compatible skeleton to a given node.
	 * 
	 * @param node
	 *            open proof tree node where rebuilding should start
	 * @param begin
	 *            Initial index of the loop on skeleton children.
	 * @param end
	 *            Final index of the loop on skeleton children.
	 * @param dischargeTrivialCase
	 *            If true try a rebuild if the skeleton is a trivial case. If
	 *            false, checks if the skeleton is not a trivial and is
	 *            compatible with the node.
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the rebuild
	 *            activity
	 * @return true iff the proof has been correctly rebuilt.
	 * 
	 */
	private boolean rebuildFromChildren(IProofTreeNode node, int begin,
			int end, boolean dischargeTrivialCase, IProofMonitor proofMonitor) {

		for (int i = begin; i < end; i++) {

			if (dischargeTrivialCase && children[i].trivialCase) {
				if (doRebuild(children[i], node, proofMonitor)) {
					return true;
				}
			}

			if (!dischargeTrivialCase && !children[i].trivialCase) {
				if (children[i].hasCompatibleDependencies(node, proofMonitor)) {
					if (doRebuild(children[i], node, proofMonitor)) {
						return true;
					}
				}
			}

		}
		return false;
	}

	/**
	 * Checks if the node of the proof and the node of the proof skeleton has
	 * the same goal and hypothesis and try a rebuild when these conditions are
	 * fulfilled. Also try to rebuild the proof when the rule of the skeleton is
	 * applicable to a sequent with any goal(ie goal is null).
	 * 
	 * @param node
	 *            The open proof tree node where rebuilding should start.
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the rebuild
	 *            activity
	 * @return true iff the proof has been correctly rebuilt.
	 */
	private boolean hasCompatibleDependencies(IProofTreeNode node,
			IProofMonitor proofMonitor) {

		if (isLeafNode()) {
			return false;
		}

		final Predicate childSkelGoal = dependencies.getGoal();

		if (childSkelGoal != null) {
			final IProverSequent sequent = node.getSequent();
			final Predicate nodeGoal = sequent.goal();
			final ITypeEnvironment env = sequent.typeEnvironment();

			final PredicateDecomposer decomposer = new PredicateDecomposer(env);
			final List<Predicate> subgoalsSequent = decomposer
					.decompose(nodeGoal);
			final List<Predicate> subgoalsSkeleton = decomposer
					.decompose(childSkelGoal);

			if (subgoalsSkeleton.containsAll(subgoalsSequent)
					&& sequent.containsHypotheses(dependencies
							.getUsedHypotheses())) {
				return true;
			}
		}

		return false;
	}

	private static boolean doRebuild(ProofSkeletonWithDependencies skeleton,
			IProofTreeNode node, IProofMonitor proofMonitor) {
		final boolean success = ProofBuilder.rebuild(node, skeleton,
				proofMonitor);
		if (!success) {
			node.pruneChildren();
		}
		return success;
	}

	/**
	 * Checks if the node a leaf node.
	 * 
	 * @return true iff this node is a leaf node.
	 */
	private boolean isLeafNode() {
		return this.skeletonRule == null;
	}

}
