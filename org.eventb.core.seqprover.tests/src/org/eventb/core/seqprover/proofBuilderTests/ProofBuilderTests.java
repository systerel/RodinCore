/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
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
import org.eventb.core.seqprover.ProverLib;
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

	private static IProofTree makeProofTree(IProverSequent sequent,
			boolean assertClosed, ITactic... tactics) {
		final IProofTree t = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode node = t.getRoot();

		BasicTactics.loopOnAllPending(tactics).apply(node, null);
		if (assertClosed) {
			assertTrue(t.isClosed());
		}
		return t;
	}

	private static IProverSequent makeTrueSequent() {
		return makeSequent("|- ⊤");
	}

	private static IProverSequent makeTrivialSequent() {
		return makeSequent("|- 0 = 0");
	}

	private static IProofTree makeTrivialProofTree() {
		final IProverSequent trivialSequent = makeTrivialSequent();
		return makeProofTree(trivialSequent, true,
				new AutoTactics.AutoRewriteTac(), //
				new AutoTactics.TrueGoalTac());
	}

	private static IProverSequent makeBranchSequent() {
		return makeSequent("c1 = 0 ;; 0 < c2 |- c1 = 0 ∧ c1 < c2");
	}

	private static IProofTree makeBranchProofTree() {
		final IProverSequent branchSequent = makeBranchSequent();
		return makeProofTree(branchSequent, true, //
				new AutoTactics.ConjGoalTac(), //
				new AutoTactics.GoalInHypTac(), //
				new AutoTactics.EqHypTac());
	}

	private static IProverSequent makeBranchSequent2() {
		return makeSequent("c1 = 0 ;; c1 < c2 |- c1 = 0 ∧ c1 < c2");
	}

	private static IProofTree makeBranchProofTree2() {
		final IProverSequent branchSequent = makeBranchSequent2();
		return makeProofTree(branchSequent, false, //
				new AutoTactics.ConjGoalTac(), //
				new AutoTactics.GoalInHypTac());
	}

	private static IProofTree makeVersionProofTree(
			IVersionedReasoner reasoner) {
		final IProverSequent trueSequent = makeTrueSequent();
		final IProofTree t = ProverFactory.makeProofTree(trueSequent, null);
		final IProofTreeNode node = t.getRoot();
		final IReasonerRegistry registry = ReasonerRegistry
				.getReasonerRegistry();
		final int storedVersion = reasoner.getVersion();
		final String versionedId;
		if (storedVersion >= 0) {
			versionedId = reasoner.getReasonerID() + ":" + storedVersion;
		} else {
			versionedId = reasoner.getReasonerID();
		}
		final IReasonerDesc desc = registry.getReasonerDesc(versionedId);
		final Set<Predicate> noHyps = Collections.emptySet();
		final IProofRule rule = ProverFactory.makeProofRule(desc,
				new EmptyInput(), trueSequent.goal(), noHyps,
				IConfidence.DISCHARGED_MAX, desc.getName());
		final boolean success = node.applyRule(rule);
		assertTrue(success);
		assertTrue(t.isClosed());
		return t;
	}
	
	@Test
	public void reuseCannotApplyRules() {
		final IProverSequent trueSequent = makeTrueSequent();
		final IProofTree trivialTree = makeTrivialProofTree();

		IProofTree prTree = ProverFactory.makeProofTree(trueSequent, null);
		IProofTreeNode node = prTree.getRoot();

		assertFalse("should not have been able to reuse", ProofBuilder.reuse(
				node, trivialTree.getRoot(), null));

		assertFalse(
				"[ProverLib] should not claim a proof is reusable when it is not",
				ProverLib.isProofReusable(trivialTree.getProofDependencies(),
						trueSequent, null));
	}

	@Test
	public void reuseTrivial() throws Exception {
		final IProverSequent trivialSequent = makeTrivialSequent();
		final IProofTree trivialTree = makeTrivialProofTree();

		IProofTree prTree = ProverFactory.makeProofTree(trivialSequent, null);
		IProofTreeNode node = prTree.getRoot();

		assertTrue("could not reuse", ProofBuilder.reuse(node, trivialTree
				.getRoot(), null));

		assertTrue("[ProverLib] should be reusable", ProverLib.isProofReusable(
				trivialTree.getProofDependencies(), trivialSequent, null));
	}

	@Test
	public void reuseBranch() throws Exception {
		final IProverSequent branchSequent = makeBranchSequent();
		final IProofTree branchTree = makeBranchProofTree();

		final IProofTree t = ProverFactory.makeProofTree(branchSequent, null);
		final IProofTreeNode node = t.getRoot();

		assertTrue("could not reuse", ProofBuilder.reuse(node, branchTree
				.getRoot(), null));
		assertTrue(t.isClosed());

		assertTrue("[ProverLib] should be reusable", ProverLib.isProofReusable(
				branchTree.getProofDependencies(), branchSequent, null));
	}

	@Test
	public void reuseCannotApplyRuleOneBranch() throws Exception {
		final IProverSequent branchSequent = makeBranchSequent();
		final IProofTree branchTree2 = makeBranchProofTree2();

		final IProofTree t = ProverFactory.makeProofTree(branchSequent, null);
		final IProofTreeNode node = t.getRoot();

		assertFalse("should not be able to reuse", ProofBuilder.reuse(node,
				branchTree2.getRoot(), null));

		assertFalse(
				"[ProverLib] should not claim a proof is reusable when it is not",
				ProverLib.isProofReusable(branchTree2.getProofDependencies(),
						branchSequent, null));
	}

	@Test
	public void reuseUnknownReasoner() throws Exception {
		final AbstractFakeReasoner unknown = new AbstractFakeReasoner(0, true) {
			public String getReasonerID() {
				return "org.eventb.core.seqprover.tests.unknown";
			}
		};
		final IProverSequent trivialSequent = makeTrivialSequent();
		final IProofTree trivialTree = makeProofTree(trivialSequent, true,
				BasicTactics.reasonerTac(unknown, null));

		final IProofTree prTree = ProverFactory.makeProofTree(trivialSequent, null);
		final IProofTreeNode node = prTree.getRoot();
		
		assertFalse(makeMessage("ProofBuilder.reuse()", false), ProofBuilder
				.reuse(node, trivialTree.getRoot(), null));
		assertFalse(makeMessage("ProverLib.proofReusable()", false),
				ProverLib.isProofReusable(trivialTree.getProofDependencies(),
						trivialSequent, null));
	}
	
	private static String makeMessage(String reuser, boolean successExpected) {
		return reuser + " should " + (successExpected ? "" : "not ")
				+ "have been able to reuse";
	}

	// makes a proof discharged by the given reasoner in its inner version
	// tries to reuse the proof (registered reasoner version = 2)
	// verifies that:
	// ProverLib.proofReusable()   == successExpected
	// ProofBuilder.reuse()        == successExpected
	// treeAfterReuse.isClosed()   == successExpected
	private static void doVersionTest(AbstractFakeReasoner reasoner,
			boolean successExpected) {
		final IProverSequent sequent = makeTrueSequent();
		final IProofTree reused = makeVersionProofTree(reasoner);
		final IProofSkeleton reusedSkel = reused.getRoot();

		final IProofTree treeAfterReuse = ProverFactory.makeProofTree(sequent,
				null);
		final IProofTreeNode node = treeAfterReuse.getRoot();

		final boolean successReusable = ProverLib.isProofReusable(
				reused.getProofDependencies(), sequent, null);
		assertEquals(
				makeMessage("ProverLib.proofReusable()", successExpected),
				successExpected, successReusable);

		final boolean success = ProofBuilder.reuse(node, reusedSkel, null);
		assertEquals(makeMessage("ProofBuilder.reuse()", successExpected),
				successExpected, success);
		assertEquals("Proof tree closed", successExpected, treeAfterReuse
				.isClosed());
	}

	// Common case: the reasoner is the same as the one used in the proof
	// no version conflict => success expected
	@Test
	public void reuseSuccessSameVersion() throws Exception {
		final SuccessReasoner reasoner = new SuccessReasoner(2, true);
		doVersionTest(reasoner, true);
	}

	// Evolution case: the reasoner version changed since the proof was made
	// with version conflict => failure expected
	// Also checks that the reasoner is not called when in other version:
	// calling the reasoner again would succeed and close the tree
	@Test
	public void reuseSuccessOtherVersion() throws Exception {
		final SuccessReasoner reasoner = new SuccessReasoner(1, true);
		doVersionTest(reasoner, false);
	}

	// Evolution case: the reasoner version changed since the proof was made
	// from NO_VERSION to another version
	// with version conflict => failure expected
	// Also checks that the reasoner is not called when in other version:
	// calling the reasoner again would succeed and close the tree
	@Test
	public void reuseSuccessUnversioned() throws Exception {
		final SuccessReasoner reasoner = new SuccessReasoner(IReasonerDesc.NO_VERSION, true);
		doVersionTest(reasoner, false);
	}

	// Verify that the reasoner is not called when in same version:
	// calling the reasoner again would fail and the tree would not be closed
	// success expected
	@Test
	public void reuseFailureSameVersion() throws Exception {
		final FailureReasoner reasoner = new FailureReasoner(2, true);
		doVersionTest(reasoner, true);
	}

	// TODO test reuse context dependent proof success + failure
}
