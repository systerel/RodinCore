/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - tests with already existing hypothesis
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.seqprover.SequentProver;

public class AutoRewriterReasonerTests extends AbstractAutomaticReasonerTests {

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {

		return new SuccessfulTest [] {
				
				// Rule SIMP_IN_COMPSET_ONEPOINT in binding context
				new SuccessfulTest(
						";H; ;S; |- ∀b·union(b)∈{x·x∈ℙ(ℤ)∧ℙ(ℤ)⊆{y·y∈ℙ(ℤ) ∣ y} ∣ x}",
						"{}[][][] |- (∀b·union(b)∈ℙ(ℤ))∧ℙ(ℤ)⊆{y·y∈ℙ(ℤ) ∣ y}"),
				new SuccessfulTest(
						";H; ;S; |- ∀x·bool(x = 5) ∈ {b·¬(FALSE=b) ∣ b}",
						"{}[][][] |- ∀x·x=5"),
				// Hide trivial hypothesis
				new SuccessfulTest(
						";H; ;S; ⊤ |- ⊥",
						"{}[⊤][][] |- ⊥"),
				// Select hypothesis as a result of simplification
				new SuccessfulTest(
						"a=1 ;H; ;S; ⊤ ⇒ a=1 |- ⊥",
						"{}[⊤ ⇒ a=1][][a=1] |- ⊥"),
				// Just hide the rewritten hypothesis if inferred already selected
				new SuccessfulTest(
						";H; ;S; a=1 ;; ⊤ ⇒ a=1 |- ⊥",
						"{}[⊤ ⇒ a=1][][a=1] |- ⊥"),
				// Do not select if neither was selected
				new SuccessfulTest(
						"⊤ ⇒ a=1 ;; a=1 ;H; ;S; |- ⊥",
						"{}[⊤ ⇒ a=1][a=1][] |- ⊥"),
				// Do not resurrect a hidden hypothesis (from normal)
				new SuccessfulTest(
						"⊤ ⇒ a=1 ;H; a=1 ;S; |- ⊥",
						"{}[a=1 ;; ⊤ ⇒ a=1][][] |- ⊥"),
				// Do not resurrect a hidden hypothesis (from selected)
				new SuccessfulTest(
						";H; a=1 ;S; ⊤ ⇒ a=1 |- ⊥",
						"{}[a=1 ;; ⊤ ⇒ a=1][][] |- ⊥"),

		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		// No need to test this. This should be guaranteed by testing the
		// abstract automatic rewrite reasoner and the formula rewriter itself.
		return new String [] {
		};	
	}

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".autoRewrites";
	}

}
