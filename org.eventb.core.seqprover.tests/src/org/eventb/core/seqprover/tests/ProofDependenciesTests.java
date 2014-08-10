/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extracted from ProofTreeTests
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.ProverLib.isProofReusable;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerRegistry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.proofBuilderTests.ContextDependentReasoner;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.ReasonerRegistry;
import org.junit.Test;

/**
 * Unit tests for proof dependencies.
 * 
 * @author Laurent Voisin
 */
public class ProofDependenciesTests extends AbstractProofTreeTests {

	/**
	 * Ensures that dependencies are correctly computed for the hyp reasoner
	 * (used hypothesis and goal).
	 */
	@Test
	public void hypDependencies() throws Exception {
		final IProofTree proofTree = makeProofTree("y=2;; x=1 |- x=1");
		Tactics.hyp().apply(proofTree.getRoot(), null);
		assertProofReusable(proofTree);

		final DependencyMatcher expected = new DependencyMatcher();
		expected.setGoal("x=1");
		expected.addUsedHypotheses("x=1");
		expected.addUsedReasoners("org.eventb.core.seqprover.hyp");
		expected.matches(proofTree.getProofDependencies());
	}

	/**
	 * Ensures that dependencies are correctly computed for two cascaded
	 * reasoners: the hypothesis produced by one and used by the other is not in
	 * the dependencies.
	 */
	@Test
	public void twoReasoners() throws Exception {
		final IProofTree proofTree = makeProofTree("y=2 ;; x=1 |- x=1 ⇒ x=1");
		final IProofTreeNode root = proofTree.getRoot();
		Tactics.impI().apply(root, null);
		Tactics.hyp().apply(root.getFirstOpenDescendant(), null);
		assertProofReusable(proofTree);

		final DependencyMatcher expected = new DependencyMatcher();
		expected.setGoal("x=1 ⇒ x=1");
		expected.addUsedReasoners("org.eventb.core.seqprover.impI");
		expected.addUsedReasoners("org.eventb.core.seqprover.hyp");
		expected.matches(proofTree.getProofDependencies());
	}

	/**
	 * Ensures that dependencies are correctly computed for the lemma reasoner:
	 * only used identifiers are present.
	 */
	@Test
	public void lemmaDependencies() throws Exception {
		final IProofTree proofTree = makeProofTree("y=2;; 1=1 |- 1=1");
		Tactics.lemma("y=2").apply(proofTree.getRoot(), null);
		assertProofReusable(proofTree);

		final DependencyMatcher expected = new DependencyMatcher("y=ℤ");
		expected.addUsedReasoners("org.eventb.core.seqprover.cut");
		expected.matches(proofTree.getProofDependencies());
	}

	/**
	 * Ensures that dependencies are correctly computed for the allI reasoner:
	 * free identifiers have been introduced.
	 */
	@Test
	public void allIDependencies() throws Exception {
		final IProofTree proofTree = makeProofTree("y=2 |- ∀ x· x∈ℤ");
		Tactics.allI().apply(proofTree.getRoot(), null);
		assertProofReusable(proofTree);

		final DependencyMatcher expected = new DependencyMatcher();
		expected.setGoal("∀ x· x∈ℤ");
		expected.addIntroducedFreeIdents("x");
		expected.addUsedReasoners("org.eventb.core.seqprover.allI");
		expected.matches(proofTree.getProofDependencies());
	}

	/**
	 * Ensures that dependencies are correctly computed for the disjE reasoner:
	 * dependencies are correctly computed for a split node.
	 */
	@Test
	public void disjEDependencies() throws Exception {
		final IProofTree proofTree = makeProofTree("1=2 ;; 3=3 ∨ 4=4 |- 1=2");
		final IProofTreeNode root = proofTree.getRoot();
		Tactics.disjE(TestLib.genPred("3=3 ∨ 4=4")).apply(root, null);
		BasicTactics.onPending(0, Tactics.hyp()).apply(root, null);
		assertProofReusable(proofTree);

		final DependencyMatcher expected = new DependencyMatcher();
		expected.setGoal("1=2");
		expected.addUsedHypotheses("1=2", "3=3 ∨ 4=4");
		expected.addUsedReasoners("org.eventb.core.seqprover.disjE");
		expected.addUsedReasoners("org.eventb.core.seqprover.hyp");
		expected.matches(proofTree.getProofDependencies());
	}

	@Test
	public void contextDependencies() throws Exception {
		ContextDependentReasoner.setContextValidity(true);

		final IProofTree proofTree = makeProofTree(" |- 1=1");
		final IProofTreeNode root = proofTree.getRoot();
		final IReasoner reasoner = new ContextDependentReasoner();
		BasicTactics.reasonerTac(reasoner, new EmptyInput()).apply(root, null);

		final DependencyMatcher expected = new DependencyMatcher();
		expected.setGoal("1=1");
		expected.addUsedReasoners(reasoner.getReasonerID());
		expected.setContextDependent(true);
		expected.matches(proofTree.getProofDependencies());
	}

	private IProofTree makeProofTree(final String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return ProverFactory.makeProofTree(sequent, null);
	}

	// Ensures that a proof is reusable on its own sequent
	private void assertProofReusable(IProofTree proofTree) {
		final IProofDependencies deps = proofTree.getProofDependencies();
		final IProverSequent sequent = proofTree.getSequent();
		assertTrue(isProofReusable(deps, sequent, null));
	}

	/**
	 * Represents expected dependencies built one piece at a time with
	 * reasonable defaults.
	 */
	private static class DependencyMatcher {

		private ITypeEnvironmentBuilder typenv;
		private Predicate goal;
		private Set<Predicate> usedHypotheses = new HashSet<Predicate>();
		private Set<String> introFreeIdents = new HashSet<String>();
		private Set<IReasonerDesc> usedReasoners = new HashSet<IReasonerDesc>();
		private boolean isContextDependent;

		public DependencyMatcher() {
			this.typenv = mTypeEnvironment();
		}

		public DependencyMatcher(String typenvImage) {
			this.typenv = mTypeEnvironment(typenvImage);
		}

		public void setGoal(String goalImage) {
			this.goal = genPred(typenv, goalImage);
		}

		public void addUsedHypotheses(String... hypImages) {
			for (final String hypImage : hypImages) {
				usedHypotheses.add(genPred(typenv, hypImage));
			}
		}

		public void addIntroducedFreeIdents(String... idents) {
			introFreeIdents.addAll(Arrays.asList(idents));
		}

		public void addUsedReasoners(String... ids) {
			final ReasonerRegistry reg = getReasonerRegistry();
			for (final String id : ids) {
				final IReasonerDesc desc = reg.getLiveReasonerDesc(id);
				usedReasoners.add(desc);
			}
		}

		public void setContextDependent(boolean isContextDependent) {
			this.isContextDependent = isContextDependent;
		}

		public void matches(IProofDependencies actual) {
			final boolean hasDeps = goal != null || !usedHypotheses.isEmpty()
					|| !typenv.isEmpty() || !introFreeIdents.isEmpty()
					|| !usedReasoners.isEmpty();

			assertEquals(hasDeps, actual.hasDeps());
			assertEquals(goal, actual.getGoal());
			assertEquals(usedHypotheses, actual.getUsedHypotheses());
			assertEquals(typenv.makeSnapshot(), actual.getUsedFreeIdents());
			assertEquals(introFreeIdents, actual.getIntroducedFreeIdents());
			assertEquals(usedReasoners, actual.getUsedReasoners());
			assertEquals(isContextDependent, actual.isContextDependent());
		}

	}

}
