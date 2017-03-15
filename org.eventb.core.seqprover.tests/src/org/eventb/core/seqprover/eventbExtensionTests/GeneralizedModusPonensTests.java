/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP.Level;
import org.junit.Test;

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

	@Test
	public void success() throws Exception {

		// Apply once in the hypothesis 1/2 (TRUE)
		assertReasonerSuccess(" 1∈P ;; 1∈P⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒2∈P] |- ⊤");
		// Apply once in the hypothesis 2/2 (TRUE)
		assertReasonerSuccess(" 1∈P ;; ¬1∈P⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][1∈P ;; ¬⊤⇒2∈P] |- ⊤");
		// Apply once in goal 1/2 (TRUE)
		assertReasonerSuccess(" 1∈P |- 1∈P⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1∈P] |- ⊤⇒2∈P");
		// Apply once in goal 2/2 (TRUE)
		assertReasonerSuccess("  1∈P |- ¬1∈P⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1∈P] |- ¬⊤⇒2∈P");
		// Apply once in the hypothesis 1/2 (FALSE)
		assertReasonerSuccess(" ¬1∈P ;; 1∈P⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[(1∈P⇒2∈P)][][¬1∈P ;; ⊥⇒2∈P] |- ⊤");
		// Apply once in the hypothesis 2/2 (FALSE)
		assertReasonerSuccess(" ¬1∈P ;; ¬1∈P⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[(¬1∈P⇒2∈P)][][¬1∈P ;; ¬⊥⇒2∈P] |- ⊤");
		// Apply once in goal 1/2 (FALSE)
		assertReasonerSuccess(" ¬1∈P |- 1∈P⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][¬1∈P] |- ⊥⇒2∈P");
		// Apply once in goal 2/2 (FALSE)
		assertReasonerSuccess("  ¬1∈P |- ¬1∈P⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][¬1∈P] |- ¬⊥⇒2∈P");
		// Apply in both hypothesis and goal
		assertReasonerSuccess(" 1∈P ;; (1∈P⇒2∈P)⇒3∈P |- 2∈P⇒1∈P ", //
				"{P=ℙ(ℤ)}[(1∈P⇒2∈P)⇒3∈P][][1∈P ;; (⊤⇒2∈P)⇒3∈P] |- 2∈P⇒⊤");
		// Apply in many hypothesis
		assertReasonerSuccess(" 1∈P ;; ¬(1∈P⇒2∈P) ;; (¬1∈P⇒3∈P) |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬(1∈P⇒2∈P) ;; (¬1∈P⇒3∈P)][][1∈P ;; ¬(⊤⇒2∈P) ;; (¬⊤⇒3∈P)] |- ⊤");
		// Apply many times in many hypothesis
		assertReasonerSuccess(" 1∈P ;; (1∈P⇒2∈P) ;; 1∈P∧(1∈P⇒2∈P) |- ⊤ ", //
				"{P=ℙ(ℤ)}[(1∈P⇒2∈P) ;; 1∈P∧(1∈P⇒2∈P)][][1∈P ;; (⊤⇒2∈P) ;; ⊤∧⊤] |- ⊤");
		// Apply many times in hypothesis
		assertReasonerSuccess(" 1∈P ;;  1∈P∧(¬1∈P⇒(3∈P∧1∈P)) |- ⊤ ", //
				"{P=ℙ(ℤ)}[1∈P∧(¬1∈P⇒(3∈P∧1∈P))][][1∈P ;; ⊤∧(¬⊤⇒(3∈P∧⊤))] |- ⊤");
		// Apply many times in goal 1/2
		assertReasonerSuccess(" 1∈P |- 2∈P⇒1∈P ∧ (1∈P ∨ (¬1∈P)⇒2∈P)", //
				"{P=ℙ(ℤ)}[][][1∈P] |- 2∈P⇒⊤ ∧ (⊤ ∨ (¬⊤)⇒2∈P)");
		// Apply many times in goal 2/2
		assertReasonerSuccess(" 1∈P ;; (2∈P⇒3∈P) |- 1∈P∧(2∈P⇒3∈P) ", //
				"{P=ℙ(ℤ)}[][][1∈P ;; (2∈P⇒3∈P)] |- ⊤∧⊤");
		// With associative predicates exactly equal (∧)
		assertReasonerSuccess(" 1∈P∧2∈P |- 1∈P∧2∈P ⇒ 3∈P ", //
				"{P=ℙ(ℤ)}[][][1∈P∧2∈P] |- ⊤⇒3∈P ");
		// With associative predicates exactly equal (∨)
		assertReasonerSuccess(" 1∈P∨2∈P |- 1∈P∨2∈P ⇒ 3∈P ", //
				"{P=ℙ(ℤ)}[][][1∈P∨2∈P] |- ⊤⇒3∈P ");
		// Rewrites deeply in expressions
		assertReasonerSuccess(" 1∈P |- bool(1∈P) = TRUE ", //
				"{P=ℙ(ℤ)}[][][1∈P] |- bool(⊤) = TRUE ");
		assertReasonerSuccess(" 1∈P |- {x ∣ 1∈P ∧ x∈P} = P ", //
				"{P=ℙ(ℤ)}[][][1∈P] |- {x ∣ ⊤ ∧ x∈P} = P ");
		if (fromLevel2) {
			// Two equivalent hypothesis
			assertReasonerSuccess(" x=1 ;; 1=x|- ⊤ ",
					"{x=ℤ}[1=x][][x=1 ;; ⊤] |- ⊤ ");
			// A hypothesis and its negation
			assertReasonerSuccess(" 1∈P ;; ¬1∈P|- ⊤ ",
					"{P=ℙ(ℤ)}[¬1∈P][][1∈P ;; ¬⊤] |- ⊤");
			// A hypothesis and its negation in goal
			assertReasonerSuccess(" 1∈P |- ¬1∈P ", //
					"{P=ℙ(ℤ)}[][][1∈P] |- ¬⊤ ");
			// A goal and its negation in hypothesis
			assertReasonerSuccess(" ¬1∈P |- 1∈P ", //
					"{P=ℙ(ℤ)}[][][¬1∈P] |- ⊥ ");
		}
		// Bug #713: Apply in two hypotheses giving the same result
		assertReasonerSuccess(" 1∈P ;; 2∈P ;; 3∈P ⇒ 1∈P ;; 3∈P ⇒ 2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[3∈P ⇒ 1∈P ;; 3∈P ⇒ 2∈P][][1∈P ;; 2∈P ;; 3∈P ⇒ ⊤] |- ⊤");
		// Bug #713: Apply in a hypotheses giving an already existing hypothesis
		assertReasonerSuccess(" 1∈P ;; 2∈P ⇒ 1∈P ;; 2∈P ⇒ ⊤ |- ⊤ ", //
				"{P=ℙ(ℤ)}[2∈P ⇒ 1∈P][][1∈P ;; 2∈P ⇒ ⊤] |- ⊤");
	}

	@Test
	public void failure() throws Exception {
		// Two associative predicates equivalent but not exactly
		// equal
		// (∨)
		assertReasonerFailure(" 1∈P∨2∈P ;; 2∈P∨1∈P |- ⊤ ");
		// Two associative predicates equivalent but not exactly
		// equal
		// (∧)
		assertReasonerFailure(" 1∈P∧2∈P ;; 2∈P∧1∈P |- ⊤ ");
		// Two associative predicates : one containing the other
		// one (∨)
		assertReasonerFailure(" 1∈P∨2∈P ;; 3∈P∨1∈P∨2∈P |- ⊤ ");
		// Two associative predicates : one containing the other
		// one (∧)
		assertReasonerFailure(" 1∈P∧2∈P ;; 3∈P∧1∈P∧2∈P |- ⊤ ");
		// Predicate ⊤ and ⊥ are not replaced
		assertReasonerFailure(" ⊥ ;; ⊤ ;; (⊤∨⊥) |- ⊤ ");
		// Avoid infinite loop
		assertReasonerFailure(" ⊥ ;; ¬⊤ |- ¬⊤ ");
		// Fails because genSeq removes duplicates hypotheses
		// rewrites the sequent to "1∈P|- ⊤"
		assertReasonerFailure(" 1∈P ;; 1∈P|- ⊤ ");
		// Hidden hypotheses are not considered anymore
		assertReasonerFailure(seq("1∈P", "1∈P⇒2∈P", "", "⊥"));
		// Hidden hypothesis cannot rewrite the goal.
		assertReasonerFailure(seq("1∈P", "2∈P", "", "1∈P"));
		// From the level 2, works as HYP, CNTR
		if (!fromLevel2) {
			// Two hypothesis equal
			assertReasonerFailure(" 1=x ;; x=1|- ⊤ ");
			// A hypothesis and its negation
			assertReasonerFailure(" 1∈P ;; ¬1∈P|- ⊤ ");
			// A hypothesis and its negation in goal
			assertReasonerFailure(" 1∈P |- ¬1∈P ");
			// A goal and its negation in hypothesis
			assertReasonerFailure(" ¬1∈P |- 1∈P ");
		}

		// Regression test for bug #764
		assertReasonerFailure(seq("¬x<2", "", "x≥2", "x=2"));
	}

	protected void assertReasonerSuccess(IProverSequent sequent,
			IProverSequent newSequent) throws Exception {
		assertReasonerSuccess(sequent, new EmptyInput(), newSequent);
	}

	protected void assertReasonerSuccess(String sequent, String newSequent)
			throws Exception {
		assertReasonerSuccess(sequent, new EmptyInput(), newSequent);
	}

	private static final String REASON = "generalized MP no more applicable";
	
	protected void assertReasonerFailure(String sequent) throws Exception {
		assertReasonerFailure(sequent, new EmptyInput(), REASON);
	}

	protected void assertReasonerFailure(IProverSequent sequent)
			throws Exception {
		assertReasonerFailure(sequent, new EmptyInput(), REASON);
	}

	protected IProverSequent seq(String hiddenHypsImage,
			String defaultHypsImage, String selHypsImage, String goalImage) {
		return genFullSeq("P=ℙ(ℤ); a=S; b=S", hiddenHypsImage, defaultHypsImage,
				selHypsImage, goalImage);
	}

}
