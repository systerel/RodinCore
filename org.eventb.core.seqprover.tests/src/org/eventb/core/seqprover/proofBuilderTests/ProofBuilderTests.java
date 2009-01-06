package org.eventb.core.seqprover.proofBuilderTests;

import static org.junit.Assert.*;

import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

public class ProofBuilderTests {

	private static IProverSequent makeSequent(String sequent) {
		return TestLib.genSeq(sequent);
	}

	private static IProofSkeleton makeSkeleton(IProverSequent sequent,
			boolean assertClosed, ITactic... tactics) {
		final IProofTree t = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode node = t.getRoot();

		BasicTactics.loopOnAllPending(tactics).apply(node, null);
		if (assertClosed) {
			assertTrue(t.isClosed());
		}
		return node.copyProofSkeleton();
	}

	private static IProverSequent makeTrueSequent() {
		return makeSequent("|- ⊤");
	}

	private static IProverSequent makeTrivialSequent() {
		return makeSequent("|- 0 = 0");
	}

	private static IProofSkeleton makeTrivialSkeleton() {
		final IProverSequent trivialSequent = makeTrivialSequent();
		return makeSkeleton(trivialSequent, true,
				new AutoTactics.AutoRewriteTac(), //
				new AutoTactics.TrueGoalTac());
	}

	private static IProverSequent makeBranchSequent() {
		return makeSequent("c1 = 0 ;; 0 < c2 |- c1 = 0 ∧ c1 < c2");
	}

	private static IProofSkeleton makeBranchSkeleton() {
		final IProverSequent branchSequent = makeBranchSequent();
		return makeSkeleton(branchSequent, true, //
				new AutoTactics.ConjGoalTac(), //
				new AutoTactics.GoalInHypTac(), //
				new AutoTactics.EqHypTac());
	}

	private static IProverSequent makeBranchSequent2() {
		return makeSequent("c1 = 0 ;; c1 < c2 |- c1 = 0 ∧ c1 < c2");
	}

	private static IProofSkeleton makeBranchSkeleton2() {
		final IProverSequent branchSequent = makeBranchSequent2();
		return makeSkeleton(branchSequent, false, //
				new AutoTactics.ConjGoalTac(), //
				new AutoTactics.GoalInHypTac());
	}

	@Test
	public void reuseCannotApplyRules() {
		final IProverSequent trueSequent = makeTrueSequent();
		final IProofSkeleton trivialSkeleton = makeTrivialSkeleton();

		IProofTree prTree = ProverFactory.makeProofTree(trueSequent, null);
		IProofTreeNode node = prTree.getRoot();

		assertFalse("should not have been able to reuse", ProofBuilder.reuse(
				node, trivialSkeleton, null));
	}

	@Test
	public void reuseTrivial() throws Exception {
		final IProverSequent trivialSequent = makeTrivialSequent();
		final IProofSkeleton trivialSkeleton = makeTrivialSkeleton();

		IProofTree prTree = ProverFactory.makeProofTree(trivialSequent, null);
		IProofTreeNode node = prTree.getRoot();

		assertTrue("could not reuse", ProofBuilder.reuse(node, trivialSkeleton,
				null));
	}

	@Test
	public void reuseBranch() throws Exception {
		final IProverSequent branchSequent = makeBranchSequent();
		final IProofSkeleton branchSkeleton = makeBranchSkeleton();

		final IProofTree t = ProverFactory.makeProofTree(branchSequent, null);
		final IProofTreeNode node = t.getRoot();

		assertTrue("could not reuse", ProofBuilder.reuse(node, branchSkeleton,
				null));
		assertTrue(t.isClosed());
	}

	@Test
	public void reuseCannotApplyRuleOneBranch() throws Exception {
		final IProverSequent branchSequent = makeBranchSequent();
		final IProofSkeleton branchSkeleton = makeBranchSkeleton2();

		final IProofTree t = ProverFactory.makeProofTree(branchSequent, null);
		final IProofTreeNode node = t.getRoot();

		assertFalse("should not be able to reuse", ProofBuilder.reuse(node,
				branchSkeleton, null));
	}

}
