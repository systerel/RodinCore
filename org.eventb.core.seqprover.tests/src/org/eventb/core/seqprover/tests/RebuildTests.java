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
package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.SequentProver.getReasonerRegistry;
import static org.eventb.core.seqprover.tactics.BasicTactics.composeOnAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;
import static org.eventb.core.seqprover.tactics.BasicTactics.rebuildTac;
import static org.eventb.core.seqprover.tactics.BasicTactics.replayTac;
import static org.eventb.core.seqprover.tactics.BasicTactics.reuseTac;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.TrueGoal;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewrites;
import org.junit.Test;

/**
 * Acceptance tests for checking proof rebuild in the presence of useless rules.
 * 
 * @author Laurent Voisin
 */
public class RebuildTests extends AbstractProofTreeTests {

	/**
	 * Ensures that proof reuse and rebuild are resilient to proof rules that
	 * are no-op on the proof tree. This is based on an example where a
	 * simplification of typing hypotheses that was once performed (on an old
	 * sequent) is no longer applicable to a new sequent.
	 */
	@Test
	public void bug3027365() {
		final IProverSequent oldSeq = genSeq("x ∈ ℤ |- ⊤");
		final IProverSequent newSeq = genSeq("    ⊤ |- ⊤");

		final IProofTree oldTree = makeOriginalProofTree(oldSeq);
		final IProofSkeleton skel = oldTree.getRoot().copyProofSkeleton();

		final IProofTree newTree = makeProofTree(newSeq, null);
		final IProofTreeNode newRoot = newTree.getRoot();

		// Reuse should succeed
		reuseTac(skel).apply(newRoot, null);
		assertTreeDischarged(newTree);

		// Rebuild should succeed
		newRoot.pruneChildren();
		rebuildTac(skel).apply(newRoot, null);
		assertTreeDischarged(newTree);

		// Replay should fail (the typeRewrites reasoner has nothing to do)
		newRoot.pruneChildren();
		replayTac(skel).apply(newRoot, null);
		assertNodeOpen(newRoot);

		// Rebuild with a patched skeleton should succeed
		final IProofSkeleton patchedSkel = new UnreusableSkeleton(skel);
		rebuildTac(patchedSkel).apply(newRoot, null);
		assertTreeDischarged(newTree);
	}

	/*
	 * Creates a proof of the original sequent containing a redundant node.
	 */
	private IProofTree makeOriginalProofTree(IProverSequent oldSeq) {
		final IProofTree tree = makeProofTree(oldSeq, null);
		final EmptyInput input = new EmptyInput();
		composeOnAllPending(reasonerTac(new TypeRewrites(), input),
				reasonerTac(new TrueGoal(), input)).apply(tree.getRoot(), null);
		assertTreeDischarged(tree);
		return tree;
	}

	/**
	 * Wrapper class for a proof skeleton, that returns exactly the same
	 * skeleton, except for the reasoner versions which are systematically
	 * changed to prevent reuse.
	 */
	private static class UnreusableSkeleton implements IProofSkeleton {

		private final IProofSkeleton original;

		public UnreusableSkeleton(IProofSkeleton original) {
			this.original = original;
		}

		public IProofSkeleton[] getChildNodes() {
			final IProofSkeleton[] origChildren = original.getChildNodes();
			final int length = origChildren.length;
			final IProofSkeleton[] result = new IProofSkeleton[length];
			for (int i = 0; i < length; i++) {
				result[i] = new UnreusableSkeleton(origChildren[i]);
			}
			return result;
		}

		/*
		 * Returns a patched proof rule where the reasoner version has been
		 * changed to prevent its reuse.
		 */
		public IProofRule getRule() {
			final IProofRule origRule = original.getRule();

			final IReasonerDesc generatedBy = patchedReasoner(origRule
					.getReasonerDesc());
			final IReasonerInput generatedUsing = origRule.generatedUsing();
			final Predicate goal = origRule.getGoal();
			final Set<Predicate> neededHyps = origRule.getNeededHyps();
			final Integer confidence = origRule.getConfidence();
			final String display = origRule.getDisplayName();
			final IAntecedent[] antecedents = origRule.getAntecedents();
			return makeProofRule(generatedBy, generatedUsing, goal, neededHyps,
					confidence, display, antecedents);
		}

		private IReasonerDesc patchedReasoner(IReasonerDesc reasonerDesc) {
			final int patchedVersion = reasonerDesc.getVersion() + 1;
			final String id = reasonerDesc.getId();
			return getReasonerRegistry().getReasonerDesc(id + ":"+ patchedVersion);
		}

		public String getComment() {
			return original.getComment();
		}

	}

}
