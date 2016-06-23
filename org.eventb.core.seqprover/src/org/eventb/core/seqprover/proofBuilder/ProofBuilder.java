/*******************************************************************************
 * Copyright (c) 2006, 2016 ETH Zurich and others.
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

import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverLib.isRuleReusable;
import static org.eventb.internal.core.seqprover.proofBuilder.ProofSkeletonWithDependencies.withDependencies;

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

	private static IReasonerOutput getReplayOutput(IProofRule rule, IProverSequent sequent,
			IProofMonitor proofMonitor) {
		final IReasoner reasoner = rule.generatedBy();

		final IReasonerInput reasonerInput = rule.generatedUsing();

		return reasoner.apply(sequent, reasonerInput, proofMonitor);
	}

	private static IProofSkeleton getTranslatedSkeleton(IProofTreeNode node, IProofSkeleton skeleton) {
		final IProofRule skelRule = skeleton.getRule();
		if (skelRule == null || node.getFormulaFactory() == skelRule.getFormulaFactory()) {
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
	public static boolean reuse(IProofTreeNode node, IProofSkeleton skeleton, IProofMonitor proofMonitor) {
		skeleton = getTranslatedSkeleton(node, skeleton);
		return recReuse(node, skeleton, proofMonitor);
	}

	private static boolean recReuse(IProofTreeNode node, IProofSkeleton skeleton, IProofMonitor proofMonitor) {

		node.setComment(skeleton.getComment());

		IProofRule reuseProofRule = skeleton.getRule();
		Object origin = node.getSequent().getOrigin();

		// if this is an open proof skeleton node, we are done
		if (reuseProofRule == null) {
			return true;
		}

		// Check if reuse was cancelled.
		if (proofMonitor != null && proofMonitor.isCanceled())
			return false;

		if (!isRuleReusable(reuseProofRule, origin)) {
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
			combinedResult &= recReuse(nodeChildren[i], skelChildren[i], proofMonitor);
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
	public static boolean replay(IProofTreeNode node, IProofSkeleton skeleton, IProofMonitor proofMonitor) {
		skeleton = getTranslatedSkeleton(node, skeleton);
		return recReplay(node, skeleton, proofMonitor);
	}

	private static boolean recReplay(IProofTreeNode node, IProofSkeleton skeleton, IProofMonitor proofMonitor) {

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
		final IReasonerOutput replayReasonerOutput = getReplayOutput(reuseProofRule, node.getSequent(), proofMonitor);

		// Check if reasoner successfully generated a rule.
		if (!(replayReasonerOutput instanceof IProofRule))
			return false;

		// Check if the generated rule is applicable
		boolean replaySuccessfull = node.applyRule((IProofRule) replayReasonerOutput);
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
			combinedResult &= recReplay(nodeChildren[i], skelChildren[i], proofMonitor);
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
	public static boolean rebuild(IProofTreeNode node, IProofSkeleton skeleton, ReplayHints replayHints,
			IProofMonitor proofMonitor) {
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
	public static boolean rebuild(IProofTreeNode node, IProofSkeleton skeleton, ReplayHints replayHints,
			boolean tryReplayUncertain, IProofMonitor proofMonitor) {
		if (replayHints == null) {
			replayHints = new ReplayHints(node.getFormulaFactory());
		}
		skeleton = getTranslatedSkeleton(node, skeleton);
		return recRebuild(node, skeleton, replayHints, tryReplayUncertain, proofMonitor);
	}

	private static boolean recRebuild(IProofTreeNode node, IProofSkeleton skeleton, ReplayHints replayHints,
			boolean tryReplayUncertain, IProofMonitor proofMonitor) {

		node.setComment(skeleton.getComment());

		final IProofRule reuseProofRule = skeleton.getRule();

		// if this is an open proof skeleton node, we are done
		if (reuseProofRule == null) {
			return true;
		}

		// Check if replay was canceled.
		if (proofMonitor != null && proofMonitor.isCanceled()) {
			return false;
		}

		boolean reuseSuccessfull = false;
		boolean replaySuccessfull = false;

		final boolean certain = reuseProofRule.getConfidence() > IConfidence.UNCERTAIN_MAX;
		if (certain) {
			reuseSuccessfull = tryReuse(reuseProofRule, node, replayHints);
		}

		if (certain ? !reuseSuccessfull : tryReplayUncertain) {
			replaySuccessfull = tryReplay(reuseProofRule, node, replayHints, proofMonitor);
		}

		final IProofSkeleton[] skelChildren = skeleton.getChildNodes();

		if (!(reuseSuccessfull || replaySuccessfull)) {
			if (ruleIsSkip(node, reuseProofRule)) {
				// Actually the rule was doing nothing, can be by-passed.
				return recRebuild(node, skelChildren[0], replayHints, tryReplayUncertain, proofMonitor);
			}
			final boolean appliedUncertain = tryUncertainRule(node, reuseProofRule);
			if (!appliedUncertain) {
				return tryCompatibleSubtree(node, skeleton, proofMonitor);
			}
			// rule applied: proceed below
		}

		final IProofTreeNode[] nodeChildren = node.getChildNodes();
		if (nodeChildren.length != skelChildren.length) {
			return tryRebuildUnsorted(nodeChildren, skeleton, proofMonitor);
		}

		// run recursively for each child
		boolean combinedResult = true;
		for (int i = 0; i < nodeChildren.length; i++) {
			ReplayHints newReplayHints = replayHints;
			if (replaySuccessfull) {
				// generate hints for continuing the proof
				newReplayHints = replayHints.clone();
				newReplayHints.addHints(reuseProofRule.getAntecedents()[i], node.getRule().getAntecedents()[i]);
			}
			combinedResult &= recRebuild(nodeChildren[i], skelChildren[i], newReplayHints, tryReplayUncertain,
					proofMonitor);
		}
		return combinedResult;
	}

	private static boolean tryReuse(IProofRule reuseProofRule, IProofTreeNode node, ReplayHints replayHints) {
		Object origin = node.getSequent().getOrigin();
		// If there are replay hints or version conflict do not try a reuse
		if (replayHints.isEmpty() && isRuleReusable(reuseProofRule, origin)) {
			// see if reuse works
			return node.applyRule(reuseProofRule);
		}
		return false;
	}

	private static boolean tryReplay(IProofRule reuseProofRule, IProofTreeNode node, ReplayHints replayHints,
			IProofMonitor proofMonitor) {
		IReasoner reasoner = reuseProofRule.generatedBy();
		// Check if the reasoner is installed
		if (reasoner == null)
			return false;
		// Get the reasoner input and apply replay hints to it.
		IReasonerInput reasonerInput = reuseProofRule.generatedUsing();
		IProofRule replayProofRule = null;
		replayHints.applyHints(reasonerInput);
		final IReasonerOutput replayReasonerOutput = reasoner.apply(node.getSequent(), reasonerInput, proofMonitor);

		// Check if the reasoner successfully generated a proof rule.
		if (replayReasonerOutput instanceof IProofRule) {
			// Try to apply the generated proof rule.
			replayProofRule = (IProofRule) replayReasonerOutput;
			return node.applyRule(replayProofRule);
		}
		return false;
	}

	/**
	 * Try to apply the given uncertain rule.
	 * <p>
	 * Lowers its confidence to uncertain if needed.
	 * </p>
	 * 
	 * @param node
	 *            an open node
	 * @param reuseProofRule
	 *            an uncertain rule to apply
	 * @return <code>true</code> if rule application succeeded,
	 *         <code>false</code> otherwise
	 */
	private static boolean tryUncertainRule(IProofTreeNode node, IProofRule reuseProofRule) {
		final IProofRule uncertainRule;
		if (reuseProofRule.getConfidence() <= IConfidence.UNCERTAIN_MAX) {
			uncertainRule = reuseProofRule;
		} else {
			uncertainRule = makeProofRule(reuseProofRule.getReasonerDesc(), reuseProofRule.generatedUsing(),
					reuseProofRule.getGoal(), reuseProofRule.getNeededHyps(), IConfidence.UNCERTAIN_MAX,
					reuseProofRule.getDisplayName(), reuseProofRule.getAntecedents());
		}
		return node.applyRule(uncertainRule);
	}

	/**
	 * Tells whether a rule would really modify the proof tree, rather than just
	 * producing exactly the same sequent as a subgoal. If the test succeeds,
	 * then the rule has exactly one antecedent and can safely be skipped.
	 */
	private static boolean ruleIsSkip(IProofTreeNode node, IProofRule rule) {
		final IProverSequent sequent = node.getSequent();
		final IProverSequent[] newSequents = rule.apply(sequent);
		return newSequents != null && newSequents.length == 1 && newSequents[0] == sequent;
	}

	/**
	 * Try to apply a child of the given skeleton to the given node.
	 * <p>
	 * Returns <code>true</code> in case of success, which means that the given
	 * node has been completely rebuilt from a subtree of the skeleton.
	 * </p>
	 * <p>
	 * In case of failure, the given node remains unchanged.
	 * </p>
	 * 
	 * @param node
	 *            an open node
	 * @param skeleton
	 *            a skeleton to search for compatible subtrees
	 * @param proofMonitor
	 *            the proof monitor
	 * @return <code>true</code> if a compatible subtree has been found and
	 *         applied, <code>false</code> otherwise
	 */
	private static boolean tryCompatibleSubtree(IProofTreeNode node, IProofSkeleton skeleton,
			IProofMonitor proofMonitor) {
		final ProofSkeletonWithDependencies skelDeps = withDependencies(skeleton);

		if (skelDeps.applyTo(node, true, proofMonitor)) {
			return true;
		}
		if (skelDeps.applyTo(node, false, proofMonitor)) {
			return true;
		}
		return false;
	}

	/**
	 * Try to rebuild the given unsorted children from those of the given
	 * skeleton.
	 * <p>
	 * Returns <code>true</code> in case of success, which means that the given
	 * nodes have been completely rebuilt from the skeleton.
	 * </p>
	 * 
	 * @param nodeChildren
	 *            unsorted nodes
	 * @param skeleton
	 *            a skeleton
	 * @param proofMonitor
	 *            the proof monitor
	 * @return <code>true</code> if rebuild succeeds, <code>false</code>
	 *         otherwise
	 */
	private static boolean tryRebuildUnsorted(IProofTreeNode[] nodeChildren, IProofSkeleton skeleton,
			IProofMonitor proofMonitor) {
		final ProofSkeletonWithDependencies skelDeps = withDependencies(skeleton);
		if (skelDeps.rebuildUnsortedChildren(nodeChildren, proofMonitor, true)) {
			return true;
		}
		return skelDeps.rebuildUnsortedChildren(nodeChildren, proofMonitor, false);
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
	public static boolean rebuild(IProofTreeNode node, IProofSkeleton skeleton, IProofMonitor proofMonitor) {
		return rebuild(node, skeleton, null, false, proofMonitor);
	}

}
