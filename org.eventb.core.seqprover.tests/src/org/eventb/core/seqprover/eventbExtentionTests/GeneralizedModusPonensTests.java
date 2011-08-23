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

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstractGenMP;

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
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; 1∈P⇒2∈P |- ⊤ "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒2∈P] |- ⊤"),
				// Apply once in the hypothesis 2/2 (TRUE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; ¬1∈P⇒2∈P |- ⊤ "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][1∈P ;; ¬⊤⇒2∈P] |- ⊤"),
				// Apply once in goal 1/2 (TRUE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P |- 1∈P⇒2∈P "), new EmptyInput(),
						"{P=ℙ(ℤ)}[][][1∈P] |- ⊤⇒2∈P"),
				// Apply once in goal 2/2 (TRUE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq("  1∈P |- ¬1∈P⇒2∈P "), new EmptyInput(),
						"{P=ℙ(ℤ)}[][][1∈P] |- ¬⊤⇒2∈P"),
				// Apply once in the hypothesis 1/2 (FALSE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" ¬1∈P ;; 1∈P⇒2∈P |- ⊤ "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[(1∈P⇒2∈P)][][¬1∈P ;; ⊥⇒2∈P] |- ⊤"),
				// Apply once in the hypothesis 2/2 (FALSE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" ¬1∈P ;; ¬1∈P⇒2∈P |- ⊤ "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[(¬1∈P⇒2∈P)][][¬1∈P ;; ¬⊥⇒2∈P] |- ⊤"),
				// Apply once in goal 1/2 (FALSE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" ¬1∈P |- 1∈P⇒2∈P "), new EmptyInput(),
						"{P=ℙ(ℤ)}[][][¬1∈P] |- ⊥⇒2∈P"),
				// Apply once in goal 2/2 (FALSE)
				new SuccessfullReasonerApplication(
						TestLib.genSeq("  ¬1∈P |- ¬1∈P⇒2∈P "),
						new EmptyInput(), "{P=ℙ(ℤ)}[][][¬1∈P] |- ¬⊥⇒2∈P"),
				// Apply in both hypothesis and goal
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; (1∈P⇒2∈P)⇒3∈P |- 2∈P⇒1∈P "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[(1∈P⇒2∈P)⇒3∈P][][1∈P ;; (⊤⇒2∈P)⇒3∈P] |- 2∈P⇒⊤"),
				// Apply in many hypothesis
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; ¬(1∈P⇒2∈P) ;; (¬1∈P⇒3∈P) |- ⊤ "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[¬(1∈P⇒2∈P) ;; (¬1∈P⇒3∈P)][][1∈P ;; ¬(⊤⇒2∈P) ;; (¬⊤⇒3∈P)] |- ⊤"),
				// Apply many times in many hypothesis
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; (1∈P⇒2∈P) ;; 1∈P∧(1∈P⇒2∈P) |- ⊤ "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[(1∈P⇒2∈P) ;; 1∈P∧(1∈P⇒2∈P)][][1∈P ;; (⊤⇒2∈P) ;; ⊤∧⊤] |- ⊤"),
				// Apply many times in hypothesis
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;;  1∈P∧(¬1∈P⇒(3∈P∧1∈P)) |- ⊤ "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[1∈P∧(¬1∈P⇒(3∈P∧1∈P))][][1∈P ;; ⊤∧(¬⊤⇒(3∈P∧⊤))] |- ⊤"),
				// Apply many times in goal 1/2
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P |- 2∈P⇒1∈P ∧ (1∈P ∨ (¬1∈P)⇒2∈P)"),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[][][1∈P] |- 2∈P⇒⊤ ∧ (⊤ ∨ (¬⊤)⇒2∈P)"),
				// Apply many times in goal 2/2
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; (2∈P⇒3∈P) |- 1∈P∧(2∈P⇒3∈P) "),
						new EmptyInput(),
						"{P=ℙ(ℤ)}[][][1∈P ;; (2∈P⇒3∈P)] |- ⊤∧⊤"),
				// With associative predicates exactly equal (∧)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P∧2∈P |- 1∈P∧2∈P ⇒ 3∈P "),
						new EmptyInput(), "{P=ℙ(ℤ)}[][][1∈P∧2∈P] |- ⊤⇒3∈P "),
				// With associative predicates exactly equal (∨)
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P∨2∈P |- 1∈P∨2∈P ⇒ 3∈P "),
						new EmptyInput(), "{P=ℙ(ℤ)}[][][1∈P∨2∈P] |- ⊤⇒3∈P "), };
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Two hypothesis equal
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; 1∈P|- ⊤ "), new EmptyInput()),
				// An hypothesis and its negation
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P ;; ¬1∈P|- ⊤ "), new EmptyInput()),
				// An hypothesis and its negation in goal
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P |- ¬1∈P "), new EmptyInput()),
				// An goal and its negation in hypothesis
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ¬1∈P |- 1∈P "), new EmptyInput()),
				// Two associative predicates equivalent but not exactly equal
				// (∨)
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P∨2∈P ;; 2∈P∨1∈P |- ⊤ "),
						new EmptyInput()),
				// Two associative predicates equivalent but not exactly equal
				// (∧)
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P∧2∈P ;; 2∈P∧1∈P |- ⊤ "),
						new EmptyInput()),
				// Two associative predicates : one containing the other one (∨)
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P∨2∈P ;; 3∈P∨1∈P∨2∈P |- ⊤ "),
						new EmptyInput()),
				// Two associative predicates : one containing the other one (∧)
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P∧2∈P ;; 3∈P∧1∈P∧2∈P |- ⊤ "),
						new EmptyInput()),
				// Predicate ⊤ and ⊥ are not replaced
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊥ ;; ⊤ ;; (⊤∨⊥) |- ⊤ "),
						new EmptyInput()),
				// Avoid infinite loop
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊥ ;; ¬⊤ |- ¬⊤ "), new EmptyInput()), };
	}

}
