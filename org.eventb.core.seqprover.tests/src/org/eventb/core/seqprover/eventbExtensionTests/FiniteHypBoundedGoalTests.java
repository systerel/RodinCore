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
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.Collection;
import java.util.Collections;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.rewriterTests.AbstractAutomaticReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class FiniteHypBoundedGoalTests extends AbstractAutomaticReasonerTests {

	private static final String finite123 = "finite({1,2,3})";
	private static final String lowerBoundL = "∃n·(∀x·x ∈ {1,2,3} ⇒ n ≤ x)";
	private static final String lowerBoundR = "∃n·(∀x·x ∈ {1,2,3} ⇒ x ≥ n)";
	private static final String upperBoundL = "∃n·(∀x·x ∈ {1,2,3} ⇒ n ≥ x)";
	private static final String upperBoundR = "∃n·(∀x·x ∈ {1,2,3} ⇒ x ≤ n)";
	private static final String malformed1 = "∃n,m⦂ℤ·(∀x·x ∈ {1,2,3} ⇒ n ≤ x)";
	private static final String malformed2 = "∃n·(∀x,y⦂ℤ·x ∈ {1,2,3} ⇒ n ≤ x)";

	private static String makeSeq(String hyps, String goal) {
		return hyps + " ;H;   ;S; " + hyps + " |- " + goal;
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteHypBoundedGoal";
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// all configurations
				new SuccessfulTest(makeSeq(finite123, lowerBoundL)),
				new SuccessfulTest(makeSeq(finite123, lowerBoundR)),
				new SuccessfulTest(makeSeq(finite123, upperBoundL)),
				new SuccessfulTest(makeSeq(finite123, upperBoundR)),
				// additional hypothesis
				new SuccessfulTest(makeSeq("⊤;; " + finite123, lowerBoundR)),
				// comprehension set
				new SuccessfulTest(makeSeq("finite({y∣y=10})",
						"∃n·(∀x·x ∈ {y∣y=10} ⇒ n ≤ x)")),
				new SuccessfulTest(makeSeq("finite({y·0≤y∧y≤10∣2∗y})",
						"∃n·(∀x·x ∈ {y·0≤y∧y≤10∣2∗y} ⇒ n ≤ x)")),

		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// no finite hyp
				makeSeq("⊤;; ⊤", lowerBoundL),
				// finite hyp on a different set
				makeSeq("finite({1,2,3,4})", lowerBoundL),
				// comprehension set with bound identifiers references
				makeSeq("finite({y·n≤y∧y≤10∣2∗y})",
						"∃n·(∀x·x ∈ {y·n≤y∧y≤10∣2∗y} ⇒ n ≤ x)"),
				// malformed predicates
				makeSeq(finite123, malformed1),
				makeSeq(finite123, malformed2),
		};
	}

	@Override
	protected Collection<UnsuccessfullReasonerApplication> makeIncorrectPositionApplication(
			String sequentImage) {
		IReasonerInput input = new EmptyInput();
		IProverSequent sequent = TestLib.genFullSeq(sequentImage);

		return Collections.singleton(new UnsuccessfullReasonerApplication(
				sequent, input));
	}

}
