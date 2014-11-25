/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replay when reasoner version has changed
 *     Systerel - in reuse(), no more try to replay; check reasoners registered
 *     Systerel - improved rebuild()
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilder;

import static org.eventb.core.seqprover.ProverLib.isRuleReusable;

import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.ProofTreeNode;
import org.eventb.internal.core.seqprover.proofBuilder.ProofSkeletonWithDependencies;

/**
 * This class contains static methods that can be used to reconstruct proof tree
 * nodes from proof skeleton nodes.
 * 
 * <p>
 * There are currently three ways in which this is supported:
 * <ul>
 * <li>Reuse : The simplest and most efficient, but also the most quick to fail
 * way to reconstruct a proof tree node from a proof skeleton node. Only the
 * rule stored in the proof skeleton node is tried for reuse. No reasoner is
 * called. This way is guaranteed to succeed if the sequent of the open proof
 * tree node satisfies the dependencies of the proof skeleton. The only
 * exception to the above statements is when a reasoner version has changed: in
 * this case, the reuse fails.
 * <li>Replay : Each reasoner that is mentioned in the proof skeleton in
 * replayed to generate a rule to use. The rules present in the proof skeleton
 * nodes are ignored. This method can be used to re-validate proof skeletons
 * since it completely ignores stored rules.
 * <li>Rebuild : A mixture between reuse and replay. Reuse is performed when
 * possible, and if not, replay is tried. In addition, replay hints are taken
 * into account and generated to be used in subsequent replays in order to
 * support refactoring.
 * </ul>
 * </p>
 * 
 * <p>
 * The methods in this class should not be used directly, but should be used in
 * their tactic-wrapped form.
 * </p>
 * 
 * @see IProofSkeleton
 * @see ProofTreeNode
 * @see BasicTactics
 * 
 * @author Farhad Mehta
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ProofBuilder {

	private static IReasonerOutput getReplayOutput(IProofRule rule,
			IProverSequent sequent, IProofMonitor proofMonitor) {
		final IReasoner reasoner = rule.generatedBy();

		final IReasonerInput reasonerInput = rule.generatedUsing();

		return reasoner.apply(sequent, reasonerInput, proofMonitor);
	}

	private static IProofSkeleton getTranslatedSkeleton(IProofTreeNode node,
			IProofSkeleton skeleton) {
		final IProofRule skelRule = skeleton.getRule();
		if (skelRule == null
				|| node.getFormulaFactory() == skelRule.getFormulaFactory()) {
			return skeleton;
		}
		return ProverLib.translate(skeleton, node.getFormulaFactory());
	}

	/**
	 * Singleton class; Should not be instantiated.
	 */
	private ProofBuilder() {
		// no instance
	}

	/**
	 * A method that recursively constructs proof tree nodes only using the
	 * rules from a proof skeleton.
	 * 
	 * <p>
	 * No reasoners are called for this proof reconstruction method
	 * </p>
	 * <p>
	 * This method may be used for a quick way to reconstruct a proof from a
	 * proof skeleton when its proof dependencies are satisfied.
	 * </p>
	 * 
	 * @param node
	 *            The open proof tree node where reuse should start. This node
	 *            MUST be checked to be open before calling this method.
	 * @param skeleton
	 *            The proof skeleton to use
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the replay
	 *            activity
	 * @return <code>true</code> iff the all proof tree nodes could be
	 *         completely rebuilt from the rules present in the proof skeleton
	 *         nodes. If this method returns <code>false</code>, it (possibly)
	 *         means that the proof skeleton of the constructed tree is not
	 *         identical to the one provided since the proof skeleton could not
	 *         be completely reused for this node.
	 * 
	 *         TODO : Test this code & wrap using appropriate tactic.
	 */
	public static boolean reuse(IProofTreeNode node, IProofSkeleton skeleton,
			IProofMonitor proofMonitor) {
		skeleton = getTranslatedSkeleton(node, skeleton);
		return recReuse(node, skeleton, proofMonitor);
	}

	private static boolean recReuse(IProofTreeNode node,
			IProofSkeleton skeleton, IProofMonitor proofMonitor) {

		node.setComment(skeleton.getComment());

		IProofRule reuseProofRule = skeleton.getRule();

		// if this is an open proof skeleton node, we are done
		if (reuseProofRule == null) {
			return true;
		}

		// Check if reuse was cancelled.
		if (proofMonitor != null && proofMonitor.isCanceled())
			return false;

		if (!isRuleReusable(reuseProofRule)) {
			return false;
		}

		// Try to reuse the rule
		boolean reuseSuccessfull = node.applyRule(reuseProofRule);
		if (!reuseSuccessfull)
			return false;

		// Do this recursively on children.
		IProofSkeleton[] skelChildren = skeleton.getChildNodes();
		IProofTreeNode[] nodeChildren = node.getChildNodes();

		if (nodeChildren.length != skelChildren.length)
			return false;

		// run recursively for each child
		boolean combinedResult = true;
		for (int i = 0; i < nodeChildren.length; i++) {
			combinedResult &= recReuse(nodeChildren[i], skelChildren[i],
					proofMonitor);
		}
		return combinedResult;
	}

	/**
	 * A method that recursively constructs proof tree nodes using a proof
	 * skeleton and replay hints using only reasoner replay.
	 * 
	 * <p>
	 * This method can be used to re-validate proof skeletons since it
	 * completely ignores stored rules.
	 * </p>
	 * 
	 * @param node
	 *            The open proof tree node where rebuilding should start. This
	 *            node MUST be checked to be open before calling this method.
	 * @param skeleton
	 *            The proof skeleton to use
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the rebuild
	 *            activity
	 * @return <code>true</code> iff the all proof tree nodes could be
	 *         completely rebuilt from the proof skeleton nodes using reasoner
	 *         replay. If this method returns <code>false</code>, it (possibly)
	 *         means that the proof skeleton of the constructed tree is not
	 *         identical to the one provided since a reasoner either failed or
	 *         was not installed.
	 * 
	 *         TODO : Test this code & wrap using appropriate tactic.
	 */
	public static boolean replay(IProofTreeNode node, IProofSkeleton skeleton,
			IProofMonitor proofMonitor) {
		skeleton = getTranslatedSkeleton(node, skeleton);
		return recReplay(node, skeleton, proofMonitor);
	}

	private static boolean recReplay(IProofTreeNode node,
			IProofSkeleton skeleton, IProofMonitor proofMonitor) {

		node.setComment(skeleton.getComment());

		IProofRule reuseProofRule = skeleton.getRule();

		// if this is an open proof skeleton node, we are done
		if (reuseProofRule == null) {
			return true;
		}

		// Check if replay was canceled.
		if (proofMonitor != null && proofMonitor.isCanceled())
			return false;

		// Try to replay the rule
		final IReasonerOutput replayReasonerOutput = getReplayOutput(
				reuseProofRule, node.getSequent(), proofMonitor);

		// Check if reasoner successfully generated a rule.
		if (!(replayReasonerOutput instanceof IProofRule))
			return false;

		// Check if the generated rule is applicable
		boolean replaySuccessfull = node
				.applyRule((IProofRule) replayReasonerOutput);
		if (!replaySuccessfull)
			return false;

		// Do this recursively on children.
		IProofSkeleton[] skelChildren = skeleton.getChildNodes();
		IProofTreeNode[] nodeChildren = node.getChildNodes();

		if (nodeChildren.length != skelChildren.length)
			return false;

		// run recursively for each child
		boolean combinedResult = true;
		for (int i = 0; i < nodeChildren.length; i++) {
			combinedResult &= recReplay(nodeChildren[i], skelChildren[i],
					proofMonitor);
		}
		return combinedResult;
	}

	/**
	 * A method that recursively rebuilds proof tree nodes using a proof
	 * skeleton and replay hints.
	 * 
	 * <p>
	 * If no replay hints are present, this method first tries to reuse the
	 * proof rules in the proof skeleton to rebuild proof nodes. If this fails,
	 * it resorts to replaying the reasoner to generate a new proof rule that to
	 * use.
	 * </p>
	 * <p>
	 * In case there are some replay hints, these hints are applied to the
	 * reasoner input and the reasoners are replayed.
	 * </p>
	 * <p>
	 * If this proof tree node was successfully rebuilt, new replay hints are
	 * generated and this method is recursively called.
	 * </p>
	 * 
	 * @param node
	 *            The open proof tree node where rebuilding should start. This
	 *            node MUST be checked to be open before calling this method.
	 * @param skeleton
	 *            The proof skeleton to use
	 * @param replayHints
	 *            The replay hints to use
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the rebuild
	 *            activity
	 * @return <code>true</code> iff the all proof tree nodes could be
	 *         completely rebuilt from the proof skeleton nodes WITHOUT
	 *         recalling any of the reasoners. If this method returns
	 *         <code>false</code>, it (possibly) means that the proof skeleton
	 *         of the constructed tree is not identical to the one provided
	 *         since either:
	 *         <ul>
	 *         <li>The proof skeleton could not be completely rebuilt for this
	 *         node.
	 *         <li>Reasoners needed to be recalled, changing the rules present
	 *         in the proof skeleton.
	 *         </ul>
	 * @deprecated use
	 *             {@link #rebuild(IProofTreeNode, IProofSkeleton, ReplayHints, boolean, IProofMonitor)}
	 */
	@Deprecated
	public static boolean rebuild(IProofTreeNode node, IProofSkeleton skeleton,
			ReplayHints replayHints, IProofMonitor proofMonitor) {
		return rebuild(node, skeleton, replayHints, true, proofMonitor);
	}

	/**
	 * A method that recursively rebuilds proof tree nodes using a proof
	 * skeleton and replay hints.
	 * 
	 * <p>
	 * If no replay hints are present, this method first tries to reuse the
	 * proof rules in the proof skeleton to rebuild proof nodes. If this fails,
	 * it resorts to replaying the reasoner to generate a new proof rule that to
	 * use.
	 * </p>
	 * <p>
	 * In case there are some replay hints, these hints are applied to the
	 * reasoner input and the reasoners are replayed.
	 * </p>
	 * <p>
	 * If rules have a {@link IConfidence#UNCERTAIN_MAX} confidence, the
	 * tryReplayUncertain parameter controls what this function does. If
	 * tryReplayUncertain is <code>false</code>, uncertain rules are reused as
	 * is; tryReplayUncertain is <code>true</code>, a replay is attempted. If it
	 * succeeds, then the replayed rule is applied. If it fails, then the
	 * uncertain rule is kept in the resulting proof tree (as if replay had not
	 * been attempted). In all cases, the rebuild goes on recursively with the
	 * subtree of the uncertain rule.
	 * </p>
	 * <p>
	 * If this proof tree node was successfully rebuilt, new replay hints are
	 * generated and this method is recursively called.
	 * </p>
	 * 
	 * @param node
	 *            The open proof tree node where rebuilding should start. This
	 *            node MUST be checked to be open before calling this method.
	 * @param skeleton
	 *            The proof skeleton to use
	 * @param replayHints
	 *            The replay hints to use, or <code>null</code> if none
	 * @param tryReplayUncertain
	 *            <code>true</code> to try to replay uncertain rules,
	 *            <code>false</code> to reuse them as is
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the rebuild
	 *            activity
	 * @return <code>true</code> iff the all proof tree nodes could be
	 *         completely rebuilt from the proof skeleton nodes WITHOUT
	 *         recalling any of the reasoners. If this method returns
	 *         <code>false</code>, it (possibly) means that the proof skeleton
	 *         of the constructed tree is not identical to the one provided
	 *         since either:
	 *         <ul>
	 *         <li>The proof skeleton could not be completely rebuilt for this
	 *         node.
	 *         <li>Reasoners needed to be recalled, changing the rules present
	 *         in the proof skeleton.
	 *         </ul>
	 * @since 3.1
	 */
	public static boolean rebuild(IProofTreeNode node, IProofSkeleton skeleton,
			ReplayHints replayHints, boolean tryReplayUncertain,
			IProofMonitor proofMonitor) {
		if (replayHints == null) {
			replayHints = new ReplayHints(node.getFormulaFactory());
		}
		skeleton = getTranslatedSkeleton(node, skeleton);
		return recRebuild(node, skeleton, replayHints, tryReplayUncertain,
				proofMonitor);
	}

	private static boolean recRebuild(IProofTreeNode node,
			IProofSkeleton skeleton, ReplayHints replayHints,
			boolean tryReplayUncertain, IProofMonitor proofMonitor) {

		node.setComment(skeleton.getComment());

		IProofRule reuseProofRule = skeleton.getRule();

		// if this is an open proof skeleton node, we are done
		if (reuseProofRule == null) {
			return true;
		}

		// Check if replay was canceled.
		if (proofMonitor != null && proofMonitor.isCanceled())
			return false;

		boolean reuseSuccessfull = false;
		boolean replaySuccessfull = false;

		if (reuseProofRule.getConfidence() <= IConfidence.UNCERTAIN_MAX) {
			if (tryReplayUncertain) {
				replaySuccessfull = tryReplay(reuseProofRule, node,
						replayHints, proofMonitor);
			}
			if (!replaySuccessfull) {
				// force reuse
				reuseSuccessfull = node.applyRule(reuseProofRule);
			}
		} else {
			reuseSuccessfull = tryReuse(reuseProofRule, node, replayHints);

			if (!reuseSuccessfull) { // reuse failed; try replay
				replaySuccessfull = tryReplay(reuseProofRule, node,
						replayHints, proofMonitor);
			}
		}

		IProofSkeleton[] skelChildren = skeleton.getChildNodes();
		IProofTreeNode[] nodeChildren = node.getChildNodes();

		// Check if rebuild for this node was successful
		if (!(reuseSuccessfull || replaySuccessfull)) {
			if (ruleIsSkip(node, reuseProofRule)) {
				// Actually the rule was doing nothing, can be by-passed.
				return recRebuild(node, skelChildren[0], replayHints,
						tryReplayUncertain, proofMonitor);
			} else {
				ProofSkeletonWithDependencies skelDeps = ProofSkeletonWithDependencies
						.withDependencies(skeleton);
				if (skelDeps.applyTo(node, true, proofMonitor)) {
					return true;
				}
				return skelDeps.applyTo(node, false, proofMonitor);
			}
		}

		// Maybe check if the node has the same number of children as the prNode
		// it may be smart to replay anyway, but generate a warning.
		if (nodeChildren.length != skelChildren.length) {
			// Create a skeleton with dependencies
			ProofSkeletonWithDependencies skelDeps = ProofSkeletonWithDependencies
					.withDependencies(skeleton);
			if (skelDeps.rebuildUnsortedChildren(nodeChildren, proofMonitor,
					true)) {
				return true;
			}
			return skelDeps.rebuildUnsortedChildren(nodeChildren, proofMonitor,
					false);
		}
		// run recursively for each child
		boolean combinedResult = true;
		for (int i = 0; i < nodeChildren.length; i++) {
			ReplayHints newReplayHints = replayHints;
			if (replaySuccessfull) {
				// generate hints for continuing the proof
				newReplayHints = replayHints.clone();
				newReplayHints.addHints(reuseProofRule.getAntecedents()[i],
						node.getRule().getAntecedents()[i]);
			}
			combinedResult &= recRebuild(nodeChildren[i], skelChildren[i],
					newReplayHints, tryReplayUncertain, proofMonitor);
		}
		return combinedResult;
	}

	private static boolean tryReuse(IProofRule reuseProofRule,
			IProofTreeNode node, ReplayHints replayHints) {
		// If there are replay hints or version conflict do not try a reuse
		if (replayHints.isEmpty() && isRuleReusable(reuseProofRule)) {
			// see if reuse works
			return node.applyRule(reuseProofRule);
		}
		return false;
	}

	private static boolean tryReplay(IProofRule reuseProofRule,
			IProofTreeNode node, ReplayHints replayHints,
			IProofMonitor proofMonitor) {
		IReasoner reasoner = reuseProofRule.generatedBy();
		// Check if the reasoner is installed
		if (reasoner == null)
			return false;
		// Get the reasoner input and apply replay hints to it.
		IReasonerInput reasonerInput = reuseProofRule.generatedUsing();
		IProofRule replayProofRule = null;
		replayHints.applyHints(reasonerInput);
		final IReasonerOutput replayReasonerOutput = reasoner.apply(
				node.getSequent(), reasonerInput, proofMonitor);

		// Check if the reasoner successfully generated a proof rule.
		if (replayReasonerOutput instanceof IProofRule) {
			// Try to apply the generated proof rule.
			replayProofRule = (IProofRule) replayReasonerOutput;
			return node.applyRule(replayProofRule);
		}
		return false;
	}

	/**
	 * Tells whether a rule would really modify the proof tree, rather than just
	 * producing exactly the same sequent as a subgoal. If the test succeeds,
	 * then the rule has exactly one antecedent and can safely be skipped.
	 */
	private static boolean ruleIsSkip(IProofTreeNode node, IProofRule rule) {
		final IProverSequent sequent = node.getSequent();
		final IProverSequent[] newSequents = rule.apply(sequent);
		return newSequents != null && newSequents.length == 1
				&& newSequents[0] == sequent;
	}

	/**
	 * A method that recursively rebuilds proof tree nodes using a proof
	 * skeleton and no replay hints.
	 * 
	 * <p>
	 * This method calls the more general <code>rebuild()</code> method with
	 * initially empty replay hints.
	 * </p>
	 * 
	 * @param node
	 *            The open proof tree node where rebuilding should start
	 * @param skeleton
	 *            The proof skeleton to use
	 * @param proofMonitor
	 *            The proof monitor that monitors the progress of the rebuild
	 *            activity
	 * @return <code>true</code> iff the all proof tree nodes could be
	 *         completely rebuilt from the proof skeleton nodes WITHOUT
	 *         recalling any of the reasoners. If this method returns
	 *         <code>false</code>, it (possibly) means that the proof skeleton
	 *         of the constructed tree is not identical to the one provided
	 *         since either:
	 *         <ul>
	 *         <li>The proof skeleton could not be completely rebuilt for this
	 *         node.
	 *         <li>Reasoners needed to be recalled, changing the rules present
	 *         in the proof skeleton.
	 *         </ul>
	 * @deprecated use
	 *             {@link #rebuild(IProofTreeNode, IProofSkeleton, ReplayHints, boolean, IProofMonitor)}
	 */
	@Deprecated
	public static boolean rebuild(IProofTreeNode node, IProofSkeleton skeleton,
			IProofMonitor proofMonitor) {
		return rebuild(node, skeleton, null, false, proofMonitor);
	}

}
