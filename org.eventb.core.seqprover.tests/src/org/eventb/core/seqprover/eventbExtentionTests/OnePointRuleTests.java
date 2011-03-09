/*******************************************************************************
 * Copyright (c) 2009, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointRule;

/**
 * @author "Nicolas Beauger"
 */
public class OnePointRuleTests extends AbstractReasonerTests {

	private static final IReasoner onePoint = new OnePointRule();
	private static final IReasonerInput goalInput = new OnePointRule.Input(null);

	private static SuccessfullReasonerApplication newSuccessGoal(String goal,
			String simplifiedGoal, String WDgoal) {
		return new SuccessfullReasonerApplication(
				TestLib.genSeq(" |- " + goal), goalInput, "{}[][][] |- "
						+ simplifiedGoal, "{}[][][] |- " + WDgoal);
	}

	private static UnsuccessfullReasonerApplication newFailureGoal(String goal) {
		return new UnsuccessfullReasonerApplication(TestLib.genSeq(" |- "
				+ goal), goalInput);
	}

	@Override
	public String getReasonerID() {
		return onePoint.getReasonerID();
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {

				// One quantified identifier => result not quantified
				newSuccessGoal("∀x· x=0 ∧ x+1=0 ⇒ x+1=2", "0+1=0⇒0+1=2", "⊤"),

				// Two quantified identifiers => result quantified
				newSuccessGoal("∀x,y· x=0 ∧ x+1=y ⇒ y=1", "∀y·0+1=y⇒y=1", "⊤"),

				// 'One conjunct' => no more implication
				newSuccessGoal("∀x· x=0 ⇒ x+x=0", "0+0=0", "⊤"),

				// 'One conjunct' and Two quantified identifiers
				newSuccessGoal("∀x,y· x=y ⇒ x+y=2∗x", "∀x·x+x=2∗x", "⊤"),

				// Replacement expression is not trivial => more complex WD
				newSuccessGoal("∀x,y· x = prj1(0↦1) ∧ x+1=y ⇒ y=1",
						"∀y·prj1(0 ↦ 1)+1=y⇒y=1",
						"0 ↦ 1∈dom(prj1)∧prj1∈ℤ × ℤ ⇸ ℤ"),

				// One Point Rule in hyp => forward inference hyp action
				new SuccessfullReasonerApplication(
						TestLib.genSeq("∀x· x=0 ∧ x+1=0 ⇒ x+1=2 |- ⊥"),
						new OnePointRule.Input(TestLib
								.genPred("∀x· x=0 ∧ x+1=0 ⇒ x+1=2")),
						"{}[∀x·x=0∧x+1=0⇒x+1=2][][0+1=0⇒0+1=2] |- ⊥",
						"{}[∀x·x=0∧x+1=0⇒x+1=2][][] |- ⊤"),

				// ∃ quantifier conjunctive form
				newSuccessGoal("∃x,y· x=0 ∧ x+1=y ∧ y=1", "∃y·0+1=y∧y=1", "⊤"),

				// Non conjunctive ∃ predicate
				newSuccessGoal("∃y· y=0", "⊤", "⊤"),
				// simple conjunction with ∃ quantifier
				newSuccessGoal("∃x,y· x=0 ∧ x+1=y", "∃y·0+1=y", "⊤"),

				// Also works with bound identifier on the rhs of the equality
				newSuccessGoal("∃y· 0=y", "⊤", "⊤"),
				newSuccessGoal("∃x,y· 0=x ∧ x+1=y", "∃y·0+1=y", "⊤"),
				newSuccessGoal("∀x· 0=x ∧ x+1=0 ⇒ x+1=2", "0+1=0⇒0+1=2", "⊤"),

				// Will work in chain
				newSuccessGoal("∀x,y,z· x=0 ∧ y=x ∧ z=y ⇒ ⊥", "∀y,z·y=0∧z=y⇒⊥",
						"⊤"),
				newSuccessGoal("∀y,z·y=0∧z=y⇒⊥", "∀z·z=0⇒⊥", "⊤"),
				newSuccessGoal("∀z·z=0⇒⊥", "⊥", "⊤"),

				// No replacement to perform
				newSuccessGoal("∀x,y·x=0∧y=0⇒⊥", "∀y·y=0⇒⊥", "⊤"),
				newSuccessGoal("∀x·x=0∧0=0⇒⊥", "0=0⇒⊥", "⊤"), //
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		final Predicate pred = TestLib
				.genPred("∀x·x ∈ ℕ ⇒ (∀y·y ∈ ℕ ∧ x = −1 ⇒ y ∈ ℕ1)");
		final Predicate boundOutside = (Predicate) pred
				.getSubFormula(IPosition.ROOT.getFirstChild().getNextSibling()
						.getFirstChild().getNextSibling());

		return new UnsuccessfullReasonerApplication[] {

				// Matching predicate for one point rule is not root
				newFailureGoal("∀x·x ∈ ℕ ⇒ (∀y·y = 1 ∧ y ∈ ℕ ⇒ y = y∗y)"),

				// Exists with implication
				newFailureGoal("∃x· x=0 ⇒ x=1"),

				// Forall with conjunction
				newFailureGoal("∀x· x=0 ∧ x=1"),

				// Matching substitution bound identifier is declared outside
				new UnsuccessfullReasonerApplication(ProverFactory.makeSequent(
						TestLib.genTypeEnv(""), null, boundOutside), goalInput),

				// Self-referring expressions (x=x+x, x=x*x) are not applicable
				newFailureGoal("∀x· x=x+x ∧ x=x∗x ⇒ x=0"),

				// Failure goal with x=x replacement
				newFailureGoal("∀x·x=x∧x>0⇒⊥"), //
		};
	}
}
