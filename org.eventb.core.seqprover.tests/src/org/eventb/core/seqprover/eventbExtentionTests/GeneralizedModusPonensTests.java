/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;

/**
 * Units tests for reasoners GeneralizedModusPonens.
 * 
 * @author Emmanuel Billaud
 */
public abstract class GeneralizedModusPonensTests extends AbstractReasonerTests {
	private final String REASONER_ID;

	public GeneralizedModusPonensTests(AbstractGenMP reasoner) {
		REASONER_ID = reasoner.getReasonerID();
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// Apply once in the hypothesis 1/2 (TRUE)
				makeSuccess(" 1∈P ;; 1∈P⇒2∈P |- ⊤ ", //
						"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒2∈P] |- ⊤"),
				// Apply once in the hypothesis 2/2 (TRUE)
				makeSuccess(" 1∈P ;; ¬1∈P⇒2∈P |- ⊤ ", //
						"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][1∈P ;; ¬⊤⇒2∈P] |- ⊤"),
				// Apply once in goal 1/2 (TRUE)
				makeSuccess(" 1∈P |- 1∈P⇒2∈P ", //
						"{P=ℙ(ℤ)}[][][1∈P] |- ⊤⇒2∈P"),
				// Apply once in goal 2/2 (TRUE)
				makeSuccess("  1∈P |- ¬1∈P⇒2∈P ", //
						"{P=ℙ(ℤ)}[][][1∈P] |- ¬⊤⇒2∈P"),
				// Apply once in the hypothesis 1/2 (FALSE)
				makeSuccess(" ¬1∈P ;; 1∈P⇒2∈P |- ⊤ ", //
						"{P=ℙ(ℤ)}[(1∈P⇒2∈P)][][¬1∈P ;; ⊥⇒2∈P] |- ⊤"),
				// Apply once in the hypothesis 2/2 (FALSE)
				makeSuccess(" ¬1∈P ;; ¬1∈P⇒2∈P |- ⊤ ", //
						"{P=ℙ(ℤ)}[(¬1∈P⇒2∈P)][][¬1∈P ;; ¬⊥⇒2∈P] |- ⊤"),
				// Apply once in goal 1/2 (FALSE)
				makeSuccess(" ¬1∈P |- 1∈P⇒2∈P ", //
						"{P=ℙ(ℤ)}[][][¬1∈P] |- ⊥⇒2∈P"),
				// Apply once in goal 2/2 (FALSE)
				makeSuccess("  ¬1∈P |- ¬1∈P⇒2∈P ", //
						"{P=ℙ(ℤ)}[][][¬1∈P] |- ¬⊥⇒2∈P"),
				// Apply in both hypothesis and goal
				makeSuccess(" 1∈P ;; (1∈P⇒2∈P)⇒3∈P |- 2∈P⇒1∈P ", //
						"{P=ℙ(ℤ)}[(1∈P⇒2∈P)⇒3∈P][][1∈P ;; (⊤⇒2∈P)⇒3∈P] |- 2∈P⇒⊤"),
				// Apply in many hypothesis
				makeSuccess(" 1∈P ;; ¬(1∈P⇒2∈P) ;; (¬1∈P⇒3∈P) |- ⊤ ", //
						"{P=ℙ(ℤ)}[¬(1∈P⇒2∈P) ;; (¬1∈P⇒3∈P)][][1∈P ;; ¬(⊤⇒2∈P) ;; (¬⊤⇒3∈P)] |- ⊤"),
				// Apply many times in many hypothesis
				makeSuccess(" 1∈P ;; (1∈P⇒2∈P) ;; 1∈P∧(1∈P⇒2∈P) |- ⊤ ", //
						"{P=ℙ(ℤ)}[(1∈P⇒2∈P) ;; 1∈P∧(1∈P⇒2∈P)][][1∈P ;; (⊤⇒2∈P) ;; ⊤∧⊤] |- ⊤"),
				// Apply many times in hypothesis
				makeSuccess(" 1∈P ;;  1∈P∧(¬1∈P⇒(3∈P∧1∈P)) |- ⊤ ", //
						"{P=ℙ(ℤ)}[1∈P∧(¬1∈P⇒(3∈P∧1∈P))][][1∈P ;; ⊤∧(¬⊤⇒(3∈P∧⊤))] |- ⊤"),
				// Apply many times in goal 1/2
				makeSuccess(" 1∈P |- 2∈P⇒1∈P ∧ (1∈P ∨ (¬1∈P)⇒2∈P)", //
						"{P=ℙ(ℤ)}[][][1∈P] |- 2∈P⇒⊤ ∧ (⊤ ∨ (¬⊤)⇒2∈P)"),
				// Apply many times in goal 2/2
				makeSuccess(" 1∈P ;; (2∈P⇒3∈P) |- 1∈P∧(2∈P⇒3∈P) ", //
						"{P=ℙ(ℤ)}[][][1∈P ;; (2∈P⇒3∈P)] |- ⊤∧⊤"),
				// With associative predicates exactly equal (∧)
				makeSuccess(" 1∈P∧2∈P |- 1∈P∧2∈P ⇒ 3∈P ", //
						"{P=ℙ(ℤ)}[][][1∈P∧2∈P] |- ⊤⇒3∈P "),
				// With associative predicates exactly equal (∨)
				makeSuccess(" 1∈P∨2∈P |- 1∈P∨2∈P ⇒ 3∈P ", //
						"{P=ℙ(ℤ)}[][][1∈P∨2∈P] |- ⊤⇒3∈P "),
				// Rewrites deeply in expressions
				makeSuccess(" 1∈P |- bool(1∈P) = TRUE ", //
						"{P=ℙ(ℤ)}[][][1∈P] |- bool(⊤) = TRUE "),
				makeSuccess(" 1∈P |- {x ∣ 1∈P ∧ x∈P} = P ", //
						"{P=ℙ(ℤ)}[][][1∈P] |- {x ∣ ⊤ ∧ x∈P} = P "),
				// Hidden hypotheses are considered.
				makeSuccess(seq("1∈P", "1∈P⇒2∈P", "", "⊥"),
						seq("1∈P ;; 1∈P⇒2∈P", "⊤⇒2∈P", "", "⊥")),
				// Hidden hypothesis takes precedence over goal.
				makeSuccess(seq("1∈P", "1∈P⇒2∈P", "", "1∈P"),
						seq("1∈P ;; 1∈P⇒2∈P", "⊤⇒2∈P", "", "1∈P")),

		};
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage,
			String newSequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new SuccessfullReasonerApplication(sequent, new EmptyInput(),
				newSequentImage);
	}

	private SuccessfullReasonerApplication makeSuccess(IProverSequent sequent,
			IProverSequent newSequent) {
		return new SuccessfullReasonerApplication(sequent, new EmptyInput(),
				newSequent);
	}

	private IProverSequent seq(String hiddenHypsImage, String defaultHypsImage,
			String selHypsImage, String goalImage) {
		return genFullSeq("P=ℙ(ℤ)", hiddenHypsImage, defaultHypsImage,
				selHypsImage, goalImage);
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Two hypothesis equal
				makeFailure(" 1∈P ;; 1∈P|- ⊤ "),
				// An hypothesis and its negation
				makeFailure(" 1∈P ;; ¬1∈P|- ⊤ "),
				// An hypothesis and its negation in goal
				makeFailure(" 1∈P |- ¬1∈P "),
				// An goal and its negation in hypothesis
				makeFailure(" ¬1∈P |- 1∈P "),
				// Two associative predicates equivalent but not exactly equal
				// (∨)
				makeFailure(" 1∈P∨2∈P ;; 2∈P∨1∈P |- ⊤ "),
				// Two associative predicates equivalent but not exactly equal
				// (∧)
				makeFailure(" 1∈P∧2∈P ;; 2∈P∧1∈P |- ⊤ "),
				// Two associative predicates : one containing the other one (∨)
				makeFailure(" 1∈P∨2∈P ;; 3∈P∨1∈P∨2∈P |- ⊤ "),
				// Two associative predicates : one containing the other one (∧)
				makeFailure(" 1∈P∧2∈P ;; 3∈P∧1∈P∧2∈P |- ⊤ "),
				// Predicate ⊤ and ⊥ are not replaced
				makeFailure(" ⊥ ;; ⊤ ;; (⊤∨⊥) |- ⊤ "),
				// Avoid infinite loop
				makeFailure(" ⊥ ;; ¬⊤ |- ¬⊤ "), };
	}

	private UnsuccessfullReasonerApplication makeFailure(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new UnsuccessfullReasonerApplication(sequent, new EmptyInput());
	}
}
