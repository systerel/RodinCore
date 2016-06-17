/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.SequentProver;

/**
 * Unit tests for the Functional image goal reasoner.
 */

public class FunImageGoalTests extends AbstractManualInferenceTests {

	public FunImageGoalTests() {
		super(false);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {

				new SuccessfulTest(" f ∈ ℕ → (ℕ → ℕ) |- 0∈dom(f(x))",
						"f ∈ ℕ → (ℕ → ℕ)", "1.0",
						"{f=ℤ↔(ℤ↔ℤ); x=ℤ}[][][f ∈ ℕ → (ℕ → ℕ) ;; f(x)∈ℕ → ℕ] |- 0 ∈ dom(f(x))"),

				new SuccessfulTest(" f ∈ ℤ ⇸ ℤ |- 0<f(x) ", "f ∈ ℤ ⇸ ℤ ", "1",
						"{f=ℤ↔ℤ; x=ℤ}[][][f∈ℤ ⇸  ℤ ;; f(x)∈ℤ] |- 0<f(x)"),

				new SuccessfulTest(" f ∈ ℕ → (ℕ → ℕ) |- f(x) ∈ ℤ ⇸ ℤ",
						"f ∈ ℕ → (ℕ → ℕ)", "0",
						"{f=ℤ↔(ℤ↔ℤ); x=ℤ}[][][f∈ℕ → (ℕ → ℕ) ;; f(x)∈ℕ → ℕ] |- f(x)∈ℤ ⇸ ℤ"),

				new SuccessfulTest(
						" f ∈ ℕ ↠ (ℕ → (ℕ → ℕ)) |- 0∈dom(f(x)(y))",
						"f ∈ ℕ ↠ (ℕ → (ℕ → ℕ))",
						"1.0.0",
						"{f=ℤ↔(ℤ↔(ℤ↔ℤ)); y=ℤ; x=ℤ}[][][f∈ℕ ↠ (ℕ → (ℕ → ℕ)) ;; f(x)∈ℕ → (ℕ → ℕ)] |- 0∈dom(f(x)(y))"),

				new SuccessfulTest(
						"f ∈ ℕ ↠ (ℕ → (ℕ → ℕ)) ;; f(x)∈ ℕ → (ℕ → ℕ) |- f(x)(y) ∈ ℤ ⇸ ℤ",
						"f ∈ ℕ ↠ (ℕ → (ℕ → ℕ))",
						"0.0",
						"{f=ℤ↔(ℤ↔(ℤ↔ℤ)); y=ℤ; x=ℤ}[][][f∈ℕ ↠ (ℕ → (ℕ → ℕ)) ;; f(x)∈ℕ → (ℕ → ℕ)] |- f(x)(y)∈ℤ ⇸ ℤ") };

	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {

				// Sequent does not contain the input hypothesis.
				" f ∈ ℕ → (ℕ → ℕ) |- f(x) ∈ ℤ ⇸ ℤ",
				"f ∈ ℕ ↠ (ℕ → (ℕ → ℕ))",
				"0",

				// The input hypothesis is not an inclusion.
				" y=0 ;; f ∈ ℕ → (ℕ → ℕ) |- f(x) = f(y)",
				"y=0",
				"0",

				// The right side of the inclusion is neither a relational
				// operator nor a functional operator.
				" y∈ℕ ;; f ∈ ℕ → (ℕ → ℕ) |- f(x) = f(y)",
				"y∈ℕ",
				"0",

				// The function in the hypothesis and the function in the goal
				// don't match.
				" g ∈ ℕ → (ℕ → ℕ) ;; f ∈ ℕ → (ℕ → ℕ) |- f(x) ∈ ℤ ⇸ ℤ",
				"g ∈ ℕ → (ℕ → ℕ)",
				"0",

				// The input position does not correspond to any
				// sub-formula of the goal.
				" g ∈ ℕ → (ℕ → ℕ) ;; f ∈ ℕ → (ℕ → ℕ) |- f(x) ∈ ℤ ⇸ ℤ",
				"g ∈ ℕ → (ℕ → ℕ)", "1.0",

				// The function application is not top level
				" f ∈ ℕ → (ℕ → ℕ) |- x=0 ⇒ 0∈dom(f(x))", "f ∈ ℕ → (ℕ → ℕ)", "0"

		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[0];
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return new ArrayList<IPosition>();
	}

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".funImgGoal";
	}

}
