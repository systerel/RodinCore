/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP.Level;

/**
 * Units tests for reasoners GeneralizedModusPonens.
 * 
 * @author Emmanuel Billaud
 */
public abstract class GeneralizedModusPonensTests extends AbstractReasonerTests {
	private final String REASONER_ID;
	protected final boolean fromLevel2;

	public GeneralizedModusPonensTests(AbstractGenMP reasoner) {
		this.fromLevel2 = reasoner.level().from(Level.L2);
		REASONER_ID = reasoner.getReasonerID();
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public final SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final List<SuccessfullReasonerApplication> tests;
		tests = new ArrayList<SuccessfullReasonerApplication>();
		addSuccessfullReasonerApplications(tests);
		return tests
				.toArray(new SuccessfullReasonerApplication[tests.size()]);
	}

	protected void addSuccessfullReasonerApplications(
			List<SuccessfullReasonerApplication> tests) {
		// Apply once in the hypothesis 1/2 (TRUE)
		tests.add(makeSuccess(" 1∈P ;; 1∈P⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒2∈P] |- ⊤"));
		// Apply once in the hypothesis 2/2 (TRUE)
		tests.add(makeSuccess(" 1∈P ;; ¬1∈P⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][1∈P ;; ¬⊤⇒2∈P] |- ⊤"));
		// Apply once in goal 1/2 (TRUE)
		tests.add(makeSuccess(" 1∈P |- 1∈P⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1∈P] |- ⊤⇒2∈P"));
		// Apply once in goal 2/2 (TRUE)
		tests.add(makeSuccess("  1∈P |- ¬1∈P⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1∈P] |- ¬⊤⇒2∈P"));
		// Apply once in the hypothesis 1/2 (FALSE)
		tests.add(makeSuccess(" ¬1∈P ;; 1∈P⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[(1∈P⇒2∈P)][][¬1∈P ;; ⊥⇒2∈P] |- ⊤"));
		// Apply once in the hypothesis 2/2 (FALSE)
		tests.add(makeSuccess(" ¬1∈P ;; ¬1∈P⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[(¬1∈P⇒2∈P)][][¬1∈P ;; ¬⊥⇒2∈P] |- ⊤"));
		// Apply once in goal 1/2 (FALSE)
		tests.add(makeSuccess(" ¬1∈P |- 1∈P⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][¬1∈P] |- ⊥⇒2∈P"));
		// Apply once in goal 2/2 (FALSE)
		tests.add(makeSuccess("  ¬1∈P |- ¬1∈P⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][¬1∈P] |- ¬⊥⇒2∈P"));
		// Apply in both hypothesis and goal
		tests.add(makeSuccess(" 1∈P ;; (1∈P⇒2∈P)⇒3∈P |- 2∈P⇒1∈P ", //
				"{P=ℙ(ℤ)}[(1∈P⇒2∈P)⇒3∈P][][1∈P ;; (⊤⇒2∈P)⇒3∈P] |- 2∈P⇒⊤"));
		// Apply in many hypothesis
		tests.add(makeSuccess(" 1∈P ;; ¬(1∈P⇒2∈P) ;; (¬1∈P⇒3∈P) |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬(1∈P⇒2∈P) ;; (¬1∈P⇒3∈P)][][1∈P ;; ¬(⊤⇒2∈P) ;; (¬⊤⇒3∈P)] |- ⊤"));
		// Apply many times in many hypothesis
		tests.add(makeSuccess(" 1∈P ;; (1∈P⇒2∈P) ;; 1∈P∧(1∈P⇒2∈P) |- ⊤ ", //
				"{P=ℙ(ℤ)}[(1∈P⇒2∈P) ;; 1∈P∧(1∈P⇒2∈P)][][1∈P ;; (⊤⇒2∈P) ;; ⊤∧⊤] |- ⊤"));
		// Apply many times in hypothesis
		tests.add(makeSuccess(" 1∈P ;;  1∈P∧(¬1∈P⇒(3∈P∧1∈P)) |- ⊤ ", //
				"{P=ℙ(ℤ)}[1∈P∧(¬1∈P⇒(3∈P∧1∈P))][][1∈P ;; ⊤∧(¬⊤⇒(3∈P∧⊤))] |- ⊤"));
		// Apply many times in goal 1/2
		tests.add(makeSuccess(" 1∈P |- 2∈P⇒1∈P ∧ (1∈P ∨ (¬1∈P)⇒2∈P)", //
				"{P=ℙ(ℤ)}[][][1∈P] |- 2∈P⇒⊤ ∧ (⊤ ∨ (¬⊤)⇒2∈P)"));
		// Apply many times in goal 2/2
		tests.add(makeSuccess(" 1∈P ;; (2∈P⇒3∈P) |- 1∈P∧(2∈P⇒3∈P) ", //
				"{P=ℙ(ℤ)}[][][1∈P ;; (2∈P⇒3∈P)] |- ⊤∧⊤"));
		// With associative predicates exactly equal (∧)
		tests.add(makeSuccess(" 1∈P∧2∈P |- 1∈P∧2∈P ⇒ 3∈P ", //
				"{P=ℙ(ℤ)}[][][1∈P∧2∈P] |- ⊤⇒3∈P "));
		// With associative predicates exactly equal (∨)
		tests.add(makeSuccess(" 1∈P∨2∈P |- 1∈P∨2∈P ⇒ 3∈P ", //
				"{P=ℙ(ℤ)}[][][1∈P∨2∈P] |- ⊤⇒3∈P "));
		// Rewrites deeply in expressions
		tests.add(makeSuccess(" 1∈P |- bool(1∈P) = TRUE ", //
				"{P=ℙ(ℤ)}[][][1∈P] |- bool(⊤) = TRUE "));
		tests.add(makeSuccess(" 1∈P |- {x ∣ 1∈P ∧ x∈P} = P ", //
				"{P=ℙ(ℤ)}[][][1∈P] |- {x ∣ ⊤ ∧ x∈P} = P "));
		// Hidden hypotheses are considered.
		tests.add(makeSuccess(seq("1∈P", "1∈P⇒2∈P", "", "⊥"),
				seq("1∈P ;; 1∈P⇒2∈P", "⊤⇒2∈P", "", "⊥")));
		// Hidden hypothesis takes precedence over goal. The behavior
		// in level 0 and 1 is incorrect and has been fixed in level 2 where
		// the goal gets rewritten by a hidden hypothesis
		tests.add(makeSuccess(seq("1∈P", "1∈P⇒2∈P", "", "1∈P"), //
				fromLevel2 //
				? seq("1∈P ;; 1∈P⇒2∈P", "⊤⇒2∈P", "", "⊤") //
						: seq("1∈P ;; 1∈P⇒2∈P", "⊤⇒2∈P", "", "1∈P")));
		if (fromLevel2) {
			// Two equivalent hypothesis
			tests.add(makeSuccess(" x=1 ;; 1=x|- ⊤ ",
					"{x=ℤ}[1=x][][x=1 ;; ⊤] |- ⊤ "));
			// A hypothesis and its negation
			tests.add(makeSuccess(" 1∈P ;; ¬1∈P|- ⊤ ",
					"{P=ℙ(ℤ)}[¬1∈P][][1∈P ;; ¬⊤] |- ⊤"));
			// A hypothesis and its negation in goal
			tests.add(makeSuccess(" 1∈P |- ¬1∈P ",
					"{P=ℙ(ℤ)}[][][1∈P] |- ¬⊤ "));
			// A goal and its negation in hypothesis
			tests.add(makeSuccess(" ¬1∈P |- 1∈P ",
					"{P=ℙ(ℤ)}[][][¬1∈P] |- ⊥ "));
		}
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage,
			String newSequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new SuccessfullReasonerApplication(sequent, new EmptyInput(),
				newSequentImage);
	}

	protected SuccessfullReasonerApplication makeSuccess(IProverSequent sequent,
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
	public final UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		final List<UnsuccessfullReasonerApplication> tests;
		tests = new ArrayList<UnsuccessfullReasonerApplication>();
		addUnsuccessfullReasonerApplications(tests);
		return tests
				.toArray(new UnsuccessfullReasonerApplication[tests.size()]);
	}

	protected void addUnsuccessfullReasonerApplications(
			List<UnsuccessfullReasonerApplication> tests) {
		// Two associative predicates equivalent but not exactly
		// equal
		// (∨)
		tests.add(makeFailure(" 1∈P∨2∈P ;; 2∈P∨1∈P |- ⊤ "));
		// Two associative predicates equivalent but not exactly
		// equal
		// (∧)
		tests.add(makeFailure(" 1∈P∧2∈P ;; 2∈P∧1∈P |- ⊤ "));
		// Two associative predicates : one containing the other
		// one (∨)
		tests.add(makeFailure(" 1∈P∨2∈P ;; 3∈P∨1∈P∨2∈P |- ⊤ "));
		// Two associative predicates : one containing the other
		// one (∧)
		tests.add(makeFailure(" 1∈P∧2∈P ;; 3∈P∧1∈P∧2∈P |- ⊤ "));
		// Predicate ⊤ and ⊥ are not replaced
		tests.add(makeFailure(" ⊥ ;; ⊤ ;; (⊤∨⊥) |- ⊤ "));
		// Avoid infinite loop
		tests.add(makeFailure(" ⊥ ;; ¬⊤ |- ¬⊤ "));
		// Fails because genSeq removes duplicates hypotheses
		// rewrites the sequent to "1∈P|- ⊤"
		tests.add(makeFailure(" 1∈P ;; 1∈P|- ⊤ "));
		// From the level 2, works as HYP, CNTR
		if (!fromLevel2) {
			// Two hypothesis equal
			tests.add(makeFailure(" 1=x ;; x=1|- ⊤ "));
			// A hypothesis and its negation
			tests.add(makeFailure(" 1∈P ;; ¬1∈P|- ⊤ "));
			// A hypothesis and its negation in goal
			tests.add(makeFailure(" 1∈P |- ¬1∈P "));
			// A goal and its negation in hypothesis
			tests.add(makeFailure(" ¬1∈P |- 1∈P "));
		}
	}

	private UnsuccessfullReasonerApplication makeFailure(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new UnsuccessfullReasonerApplication(sequent, new EmptyInput());
	}

}
