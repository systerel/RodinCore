/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rn;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.junit.Test;

/**
 * @author Emmanuel Billaud
 *
 * Unit tests for the auto tactic NnfrewritesAutoTac
 */
public class NNFRewritesAutoTacTests extends AbstractTacticTests {

	public NNFRewritesAutoTacTests() {
		super(new AutoTactics.NNFRewritesAutoTac(),
				"org.eventb.core.seqprover.NNFTac");
	}

	/**
	 * Ensures that the NnfRewrites auto-tactic succeeds once on both Hypothesis
	 * and Goal which contains :
	 * <ul>
	 *  <li>a disjunction</li>
	 *  <li>a conjunction</li>
	 *  <li>an implication</li>
	 *  <li>an existential quantifier</li>
	 *  <li>a universal quantifier</li>
	 *  <li>a double negation</li>
	 *  </ul>
	 */
	@Test
	public void applyOnce() {
		assertSuccessInHypAndGoal("¬(1=1 ∨ 2=2)");
		assertSuccessInHypAndGoal("¬(1=1 ∧ 2=2)");
		assertSuccessInHypAndGoal("¬(1=1 ⇒ 2=2)");
		assertSuccessInHypAndGoal("¬(∃x·x∈ℤ)");
		assertSuccessInHypAndGoal("¬(∀x·x∈ℤ)");
		assertSuccessInHypAndGoal("¬¬1=1");
	}
	
	/**
	 * Ensures that the NnfRewrites auto-tactic succeeds many times on both
	 * hypothesis and goal in those configuration :
	 * <ul>
	 * <li>recursively on one predicate</li>
	 * <li>on one predicate containing two sub-predicates on which the tactic
	 * can be applied</li>
	 * </ul>
	 */
	@Test
	public void applyMany() {
		final Predicate pRec = parsePredicate("¬(1=1 ∧ ¬2=2)");
		final Predicate pRecChild = parsePredicate("¬1=1 ∨ ¬¬2=2");
		assertSuccess(";H; ;S; ¬(1=1 ∧ ¬2=2) |- ⊥",
				rn(pRec, "", rn(pRecChild, "1", empty)));
		assertSuccess(";H; ;S; |- ¬(1=1 ∧ ¬2=2)", rn("", rn("1", empty)));

		final Predicate pMany = parsePredicate("¬(1=1 ∧ 2=2) ∨ ¬¬3=3");
		final Predicate pManyChild = parsePredicate("(¬1=1 ∨ ¬2=2) ∨ ¬¬3=3");
		assertSuccess(";H; ;S; ¬(1=1 ∧ 2=2) ∨ ¬¬3=3 |- ⊥",
				rn(pMany, "0", rn(pManyChild, "1", empty)));
		assertSuccess(";H; ;S; |- ¬(1=1 ∧ 2=2) ∨ ¬¬3=3",
				rn("0", rn("1", empty)));
	}

	/**
	 * Ensures that the NnfRewrites auto-tactic succeeds on hybrid
	 * configurations :
	 * <ul>
	 * <li>When there are two predicate in the hypothesis (impossible for the
	 * goal)</li>
	 * <li>When there are one predicate in the hypothesis and one in the goal</li>
	 * </ul>
	 */
	@Test
	public void applyHybrid() {
		final Predicate pred1 = parsePredicate("¬(1=1 ∧ 2=2)");
		final Predicate pred2 = parsePredicate("¬¬3=3");
		assertSuccess(";H; ;S; ¬(1=1 ∧ 2=2) ;; ¬¬3=3 |- ⊥",
				rn(pred1, "", rn(pred2, "", empty)));
		assertSuccess(";H; ;S; ¬(1=1 ∧ 2=2) |- ¬¬3=3",
				rn(pred1, "", rn("", empty)));
	}

	/**
	 * Ensures that the NnfRewrites auto-tactic fails on both Hypothesis and
	 * Goal which contains :
	 * <ul>
	 * <li>predicate which contains a rule that is implemented in the underlying
	 * reasoner but shall not be used (¬S=∅, 1=1⇔2=2)</li>
	 * <li>no ¬ (e.g. 1=1 ∧ 2=2)</li>
	 * <li>a ¬ that cannot be removed or pushed in its children (e.g. ¬1=1)</li>
	 * </ul>
	 */
	@Test
	public void failOnce() {
		assertFailureInHypAndGoal("¬ S=(∅⦂ℙ(ℤ))"); // DEF_SPECIAL_NOT_EQUAL
		assertFailureInHypAndGoal("¬ (∅⦂ℙ(ℤ))=S"); // Expressions' place switch
		assertFailureInHypAndGoal("¬(1=1 ⇔ 2=2)");
		assertFailureInHypAndGoal("(1=1 ∧ 2=2)");
		assertFailureInHypAndGoal("¬1=1");
	}

	public void assertSuccessInHypAndGoal(String predStr) {
		final Predicate hyp = parsePredicate(predStr);
		assertSuccess(";H; ;S;" + predStr + " |- ⊥", rn(hyp, "", empty));
		final Predicate pred = (Predicate) hyp.getChild(0);
		if (Lib.isDisj(pred) || Lib.isImp(pred)) {
			assertSuccess(";H; ;S; |- " + predStr, rn("", empty, empty));
		} else {
			assertSuccess(";H; ;S; |- " + predStr, rn("", empty));
		}
	}

	public void assertFailureInHypAndGoal(String predStr) {
		assertFailure(";H; ;S;" + predStr + " |- ⊥");
		assertFailure(";H; ;S; |- " + predStr);
	}

}
