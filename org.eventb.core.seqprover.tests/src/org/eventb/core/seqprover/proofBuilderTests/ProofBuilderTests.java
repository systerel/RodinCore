package org.eventb.core.seqprover.proofBuilderTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.ReasonerRegistry;
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

	private static IProofSkeleton makeVersionSkeleton(
			IVersionedReasoner reasoner) {
		final IProverSequent trueSequent = makeTrueSequent();
		final IProofTree t = ProverFactory.makeProofTree(trueSequent, null);
		final IProofTreeNode node = t.getRoot();
		final IReasonerRegistry registry = ReasonerRegistry
				.getReasonerRegistry();
		final IReasonerDesc desc = registry.getReasonerDesc(reasoner
				.getReasonerID()
				+ ":" + reasoner.getVersion());
		final Set<Predicate> noHyps = Collections.emptySet();
		final IProofRule rule = ProverFactory.makeProofRule(desc,
				new EmptyInput(), trueSequent.goal(), noHyps,
				IConfidence.DISCHARGED_MAX, desc.getName());
		final boolean success = node.applyRule(rule);
		assertTrue(success);
		assertTrue(t.isClosed());
		return node;
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

	private static String makeMessage(boolean successExpected) {
		return "should " + (successExpected ? "" : "not ")
				+ "have been able to reuse";
	}

	private static void doVersionTest(IVersionedReasoner reasoner,
			boolean successExpected) {
		final IProverSequent sequent = makeTrueSequent();
		final IProofSkeleton skeleton = makeVersionSkeleton(reasoner);

		final IProofTree t = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode node = t.getRoot();

		final boolean success = ProofBuilder.reuse(node, skeleton, null);
		assertEquals(makeMessage(successExpected), successExpected, success);
	}

	// no replay
	@Test
	public void reuseSuccessNoReplay() throws Exception {
		final SuccessReasoner reasoner = new SuccessReasoner(2, true);
		doVersionTest(reasoner, true);
	}

	// replay is called and succeeds
	@Test
	public void reuseSuccessWithReplay() throws Exception {
		final SuccessReasoner reasoner = new SuccessReasoner(1, true);
		doVersionTest(reasoner, true);
	}

	// no replay
	@Test
	public void reuseFailureNoReplay() throws Exception {
		final FailureReasoner reasoner = new FailureReasoner(2, true);
		doVersionTest(reasoner, true);
	}

	// replay is called and fails
	@Test
	public void reuseFailureWithReplay() throws Exception {
		final FailureReasoner reasoner = new FailureReasoner(1, true);
		doVersionTest(reasoner, false);
	}

}
