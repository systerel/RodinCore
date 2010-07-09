/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - in reuse() and rebuild(), try to replay if the reasoner
 *                version has changed
 *     Systerel - in reuse(), no more try to replay; check reasoners registered
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilder;

import static org.eventb.core.seqprover.ProverLib.isRuleReusable;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.ProofTreeNode;

/**
 * This class contains static methods that can be used to reconstruct proof tree nodes from
 * proof skeleton nodes.
 *
 * <p>
 * There are currently three ways in which this is supported:
 * <ul>
 * <li> Reuse : The simplest and most efficient, but also the most quick to fail way to reconstruct
 * 				a proof tree node from a proof skeleton node. Only the rule stored in the proof skeleton
 * 				node is tried for reuse. No reasoner is called. This way is guaranteed to succeed if the sequent
 * 				of the open proof tree node satisfies the dependencies of the proof skeleton.
 *              The only exception to the above statements is when a reasoner version has changed: in this case,
 *              the reuse fails.
 * <li> Replay : Each reasoner that is mentioned in the proof skeleton in replayed to generate a rule to use. The rules 
 * 				present in the proof skeleton nodes are ignored. This method can be used to re-validate proof skeletons since 
 * 				it completely ignores stored rules.
 * <li> Rebuild : A mixture between reuse and replay. Reuse is performed when possible, and if not, replay is tried. In addition,
 * 				replay hints are taken into account and generated to be used in subsequent replays in order to support refactoring.
 * </ul>
 * </p>
 * 
 * <p>
 * The methods in this class should not be used directly, but should be used in their tactic-wrapped form.
 * </p>
 * 
 * @see IProofSkeleton
 * @see ProofTreeNode
 * @see BasicTactics
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public class ProofBuilder {

	private static IReasonerOutput getReplayOutput(IProofRule rule,
			IProverSequent sequent, IProofMonitor proofMonitor) {
		final IReasoner reasoner = rule.generatedBy();

		final IReasonerInput reasonerInput = rule.generatedUsing();

		return reasoner.apply(sequent, reasonerInput, proofMonitor);
	}

	/**
	 *  Singleton class; Should not be instantiated.
	 */
	private ProofBuilder() {
	}
	
	/**
	 * A method that recursively constructs proof tree nodes only using the rules from a proof skeleton.
	 * 
	 * <p>
	 * No reasoners are called for this proof reconstruction method
	 * </p>
	 * <p>
	 * This method may be used for a quick way to reconstruct a proof from a proof skeleton when its proof dependencies
	 * are satisfied.  
	 * </p>
	 * 
	 * @param node
	 * 			The open proof tree node where reuse should start. This node MUST be checked to be open before calling this method.
	 * @param skeleton
	 * 			The proof skeleton to use
	 * @param proofMonitor
	 * 			The proof monitor that monitors the progress of the replay activity
	 * @return
	 * 			<code>true</code> iff the all proof tree nodes could be completely rebuilt from the rules present in the 
	 * 			proof skeleton nodes. If this method returns <code>false</code>, it (possibly) means that the
	 * 			proof skeleton of the constructed tree is not identical to the one provided since the proof skeleton could not be
	 * 			completely reused for this node.
	 * 
	 * TODO : Test this code & wrap using appropriate tactic.
	 */
	public static boolean reuse(IProofTreeNode node,IProofSkeleton skeleton, IProofMonitor proofMonitor) {

		node.setComment(skeleton.getComment());

		IProofRule reuseProofRule = skeleton.getRule();

		// if this is an open proof skeleton node, we are done
		if (reuseProofRule == null){
			return true;
		}

		// Check if reuse was cancelled. 
		if (proofMonitor!= null && proofMonitor.isCanceled()) return false;

		if (!isRuleReusable(reuseProofRule)) {
			return false;
		}
		
		// Try to reuse the rule
		boolean reuseSuccessfull = node.applyRule(reuseProofRule);
		if (! reuseSuccessfull) return false;

		// Do this recursively on children.
		IProofSkeleton[] skelChildren = skeleton.getChildNodes();
		IProofTreeNode[] nodeChildren = node.getChildNodes();

		if (nodeChildren.length != skelChildren.length) return false;

		// run recursively for each child
		boolean combinedResult = true;
		for (int i = 0; i < nodeChildren.length; i++) {
				combinedResult &= reuse(nodeChildren[i],skelChildren[i], proofMonitor);
		}
		return combinedResult;		
	}

	
	/**
	 * A method that recursively constructs proof tree nodes using a proof skeleton and replay hints using only reasoner replay.
	 * 
	 * <p>
	 * This method can be used to re-validate proof skeletons since it completely ignores stored rules.
	 * </p>
	 * 
	 * @param node
	 * 			The open proof tree node where rebuilding should start. This node MUST be checked to be open before calling this method.
	 * @param skeleton
	 * 			The proof skeleton to use
	 * @param proofMonitor
	 * 			The proof monitor that monitors the progress of the rebuild activity
	 * @return
	 * 			<code>true</code> iff the all proof tree nodes could be completely rebuilt from the proof skeleton nodes 
	 * 			using reasoner replay. If this method returns <code>false</code>, it (possibly) means that the
	 * 			proof skeleton of the constructed tree is not identical to the one provided since a reasoner either failed 
	 * 			or was not installed.
	 * 
	 * TODO : Test this code & wrap using appropriate tactic.
	 */
	public static boolean replay(IProofTreeNode node,IProofSkeleton skeleton, IProofMonitor proofMonitor) {

		node.setComment(skeleton.getComment());
		
		IProofRule reuseProofRule = skeleton.getRule();

		// if this is an open proof skeleton node, we are done
		if (reuseProofRule == null){
			return true;
		}
		
		// Check if replay was canceled. 
		if (proofMonitor!= null && proofMonitor.isCanceled()) return false;
		
		// Try to replay the rule
		final IReasonerOutput replayReasonerOutput = getReplayOutput(
				reuseProofRule, node.getSequent(), proofMonitor);

		// Check if reasoner successfully generated a rule.
		if (!(replayReasonerOutput instanceof IProofRule)) return false;

		// Check if the generated rule is applicable
		boolean replaySuccessfull =  node.applyRule((IProofRule) replayReasonerOutput);
		if (! replaySuccessfull) return false;
				
		// Do this recursively on children.
		IProofSkeleton[] skelChildren = skeleton.getChildNodes();
		IProofTreeNode[] nodeChildren = node.getChildNodes();

		if (nodeChildren.length != skelChildren.length) return false;

		// run recursively for each child
		boolean combinedResult = true;
		for (int i = 0; i < nodeChildren.length; i++) {
				combinedResult &= replay(nodeChildren[i],skelChildren[i], proofMonitor);
		}
		return combinedResult;
	}
	
	/**
	 * A method that recursively rebuilds proof tree nodes using a proof skeleton and replay hints.
	 * 
	 * <p>
	 * If no replay hints are present, this method first tries to reuse the proof rules in the proof
	 * skeleton to rebuild proof nodes. If this fails, it resorts to replaying the reasoner to generate a new proof rule that to use.
	 * </p>
	 * <p>
	 * In case there are some replay hints, these hints are applied to the reasoner input and the reasoners are replayed.  
	 * </p>
	 * <p>
	 * If this proof tree node was successfully rebuilt, new replay hints are generated and this method is recursively called.
	 * </p>
	 * 
	 * @param node
	 * 			The open proof tree node where rebuilding should start. This node MUST be checked to be open before calling this method.
	 * @param skeleton
	 * 			The proof skeleton to use
	 * @param replayHints
	 * 			The replay hints to use
	 * @param proofMonitor
	 * 			The proof monitor that monitors the progress of the rebuild activity
	 * @return
	 * 			<code>true</code> iff the all proof tree nodes could be completely rebuilt from the proof skeleton nodes WITHOUT
	 * 			recalling any of the reasoners. If this method returns <code>false</code>, it (possibly) means that the
	 * 			proof skeleton of the constructed tree is not identical to the one provided since either:
	 * 			<ul>
	 * 			<li> The proof skeleton could not be completely rebuilt for this node.
	 * 			<li> Reasoners needed to be recalled, changing the rules present in the proof skeleton.
	 * 			</ul>
	 */
	public static boolean rebuild(IProofTreeNode node,IProofSkeleton skeleton, ReplayHints replayHints, IProofMonitor proofMonitor) {


		node.setComment(skeleton.getComment());

		IProofRule reuseProofRule = skeleton.getRule();

		// if this is an open proof skeleton node, we are done
		if (reuseProofRule == null){
			return true;
		}
		
		// Check if replay was canceled. 
		if (proofMonitor!= null && proofMonitor.isCanceled()) return false;

		boolean reuseSuccessfull = false;
		boolean replaySuccessfull = false;

		// If there are replay hints or version conflict do not try a reuse
		if (replayHints.isEmpty() && isRuleReusable(reuseProofRule))
		{
			// see if reuse works
			reuseSuccessfull = node.applyRule(reuseProofRule);
		}

		if (! reuseSuccessfull)
		{	// reuse failed; try replay
			IReasoner reasoner = reuseProofRule.generatedBy();
			// Check if the reasoner is installed
			if (reasoner == null) return false;
			// Get the reasoner input and apply replay hints to it.
			IReasonerInput reasonerInput = reuseProofRule.generatedUsing();
			IProofRule replayProofRule = null;
			replayHints.applyHints(reasonerInput);
			IReasonerOutput replayReasonerOutput = reasoner.apply(node.getSequent(),reasonerInput, proofMonitor);
			// Check if the reasoner successfully generated a proof rule.
			if ((replayReasonerOutput != null) && 
					((replayReasonerOutput instanceof IProofRule))){
				// Try to apply the generated proof rule.
				replayProofRule = (IProofRule) replayReasonerOutput;
				replaySuccessfull = node.applyRule(replayProofRule);
			}
		}	

		IProofSkeleton[] skelChildren = skeleton.getChildNodes();
		IProofTreeNode[] nodeChildren = node.getChildNodes();

		// Check if rebuild for this node was successful
		if (!(reuseSuccessfull || replaySuccessfull)) {
			if (ruleIsSkip(node, reuseProofRule)) {
				// Actually the rule was doing nothing, can be by-passed.
				return rebuild(node, skelChildren[0], replayHints, proofMonitor);
			}
			// We don't know what to do for this node.
			return false;
		}

		// Maybe check if the node has the same number of children as the prNode
		// it may be smart to replay anyway, but generate a warning.
		if (nodeChildren.length != skelChildren.length) return false;

		// run recursively for each child
		boolean combinedResult = true;
		for (int i = 0; i < nodeChildren.length; i++) {
			ReplayHints newReplayHints = replayHints;
			if (replaySuccessfull)
			{
				// generate hints for continuing the proof
				newReplayHints = replayHints.clone();
				newReplayHints.addHints(reuseProofRule.getAntecedents()[i],node.getRule().getAntecedents()[i]);
			}
			combinedResult &= rebuild(nodeChildren[i],skelChildren[i],newReplayHints, proofMonitor);
		}
		return combinedResult;
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
	 * A method that recursively rebuilds proof tree nodes using a proof skeleton and no replay hints.
	 *
	 * <p>
	 * This method calls the more general <code>rebuild()</code> method with initially empty replay hints.
	 * </p>
	 * 
	 * @param node
	 * 			The open proof tree node where rebuilding should start
	 * @param skeleton
	 * 			The proof skeleton to use
	 * @param proofMonitor
	 * 			The proof monitor that monitors the progress of the rebuild activity
	 * @return
	 * 			<code>true</code> iff the all proof tree nodes could be completely rebuilt from the proof skeleton nodes WITHOUT
	 * 			recalling any of the reasoners. If this method returns <code>false</code>, it (possibly) means that the
	 * 			proof skeleton of the constructed tree is not identical to the one provided since either:
	 * 			<ul>
	 * 			<li> The proof skeleton could not be completely rebuilt for this node.
	 * 			<li> Reasoners needed to be recalled, changing the rules present in the proof skeleton.
	 * 			</ul>
	 */
	public static boolean rebuild(IProofTreeNode node, IProofSkeleton skeleton, IProofMonitor proofMonitor) {
		return rebuild(node,skeleton,new ReplayHints(), proofMonitor);
	}

}
