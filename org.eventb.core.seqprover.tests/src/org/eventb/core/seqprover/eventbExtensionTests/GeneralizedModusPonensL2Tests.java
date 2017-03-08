/*******************************************************************************
 * Copyright (c) 2013, 2017 Systerel and others.
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
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import java.util.List;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.GeneralizedModusPonensL2;

/**
 * Unit tests for the reasoner GeneralizedModusPonensL2.
 * 
 * @author Josselin Dolhen
 */
public class GeneralizedModusPonensL2Tests extends GeneralizedModusPonensL1Tests {

	// The reasoner for testing.
	private static final AbstractGenMP GenMP_L2 = new GeneralizedModusPonensL2();

	public GeneralizedModusPonensL2Tests() {
		this(GenMP_L2);
	}

	protected GeneralizedModusPonensL2Tests(AbstractGenMP rewriter) {
		super(rewriter);
	}

	@Override
	protected void addSuccessfullReasonerApplications(
			List<SuccessfullReasonerApplication> tests) {
		// Apply once in the hypothesis 1/2 (TRUE)
		// Integer equivalences
		tests.add(makeSuccess(" 1<x ;; x>1⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[x>1⇒2∈P][][1<x ;; ⊤⇒2∈P] |- ⊤"));
		// Set equivalences
		tests.add(makeSuccess("{1}⊂{2} ;; {1}⊆{2}⇒2∈P |- ⊤ ",
				"{P=ℙ(ℤ)}[{1}⊆{2}⇒2∈P][][{1}⊂{2} ;; ⊤⇒2∈P] |- ⊤"));
		// Scalar equality
		tests.add(makeSuccess(seq("", "a=b ;; b=a⇒2∈P", "", "1∈P"), //
				seq("b=a⇒2∈P", "a=b ;; ⊤⇒2∈P" ,"" ,"1∈P")));
		// Apply once in the hypothesis 2/2 (TRUE)
		tests.add(makeSuccess(" 1=x ;; ¬x=1⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬x=1⇒2∈P][][1=x ;; ¬⊤⇒2∈P] |- ⊤"));
		tests.add(makeSuccess(" 1=x ;; 1<x⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[1<x⇒2∈P][][1=x ;; ⊥⇒2∈P] |- ⊤"));
		tests.add(makeSuccess(" 1<x ;; ¬x=1⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬x=1⇒2∈P][][1<x ;; ¬⊥⇒2∈P] |- ⊤"));
		// Apply once in goal 1/2 (TRUE)
		tests.add(makeSuccess(" 1=x |- x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1=x] |- ⊤⇒2∈P"));
		tests.add(makeSuccess(" 1=x |- ¬x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1=x] |- ¬⊤⇒2∈P"));
		// Apply once in goal 2/2 (TRUE)
		tests.add(makeSuccess(" 1=x |- x>1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1=x] |- ⊥⇒2∈P"));
		tests.add(makeSuccess(" 1<x |- ¬x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1<x] |- ¬⊥⇒2∈P"));
		// Apply once in the hypothesis 1/2 (FALSE)
		tests.add(makeSuccess(" 1>x ;; x=1⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[(x=1⇒2∈P)][][1>x ;; ⊥⇒2∈P] |- ⊤"));
		// Apply once in the hypothesis 2/2 (FALSE)
		tests.add(makeSuccess(" ¬x=1⇒2∈P |- 1=x ", //
				"{P=ℙ(ℤ)}[¬x=1⇒2∈P][][¬⊥⇒2∈P] |- 1=x"));
		// Apply once in goal 1/2 (FALSE)
		tests.add(makeSuccess(" ¬1=x |- x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][¬1=x] |- ⊥⇒2∈P"));
		// Apply once in goal 2/2 (FALSE)
		tests.add(makeSuccess("  ¬1=x |- ¬x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][¬1=x] |- ¬⊥⇒2∈P"));
		// Apply in both hypothesis and goal
		tests.add(makeSuccess(" 1=x ;; (x=1⇒2∈P)⇒3∈P |- 2∈P⇒x=1 ", //
				"{P=ℙ(ℤ)}[(x=1⇒2∈P)⇒3∈P][][1=x ;; (⊤⇒2∈P)⇒3∈P] |- 2∈P⇒⊤"));
		// Apply in several hypothesis
		tests.add(makeSuccess(" 1=x ;; ¬(x=1⇒2∈P) ;; (¬x=1⇒3∈P) |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬(x=1⇒2∈P) ;; (¬x=1⇒3∈P)][][1=x ;; ¬(⊤⇒2∈P) ;; (¬⊤⇒3∈P)] |- ⊤"));
		// Apply several times in several hypothesis
		tests.add(makeSuccess(" 1=x ;; (x=1⇒2∈P) ;; x=1∧(x=1⇒2∈P) |- ⊤ ", //
				"{P=ℙ(ℤ)}[(x=1⇒2∈P) ;; x=1∧(x=1⇒2∈P)][][1=x ;; (⊤⇒2∈P) ;; ⊤∧⊤] |- ⊤"));
		// Apply several times in hypothesis
		tests.add(makeSuccess(" 1=x ;;  x=1∧(¬x=1⇒(3∈P∧x=1)) |- ⊤ ", //
				"{P=ℙ(ℤ)}[x=1∧(¬x=1⇒(3∈P∧x=1))][][1=x ;; ⊤∧(¬⊤⇒(3∈P∧⊤))] |- ⊤"));
		// Apply many times in goal 1/2
		tests.add(makeSuccess(" 1=x |- 2∈P⇒x=1 ∧ (x=1 ∨ (¬x=1)⇒2∈P)", //
				"{P=ℙ(ℤ)}[][][1=x] |- 2∈P⇒⊤ ∧ (⊤ ∨ (¬⊤)⇒2∈P)"));
		// Apply many times in goal 2/2
		tests.add(makeSuccess(" 1=x ;; (2∈P⇒3∈P) |- x=1∧(2∈P⇒3∈P) ", //
				"{P=ℙ(ℤ)}[][][1=x ;; (2∈P⇒3∈P)] |- ⊤∧⊤")); //
		// Regression test for a case similar to bug #764:
		// Two equivalent hypotheses do not rewrite each other.
		tests.add(makeSuccess(" a<b ;; b>a |- ⊤ ", //
				"{a=ℤ; b=ℤ}[b>a][][a<b ;; ⊤] |- ⊤"));
		super.addSuccessfullReasonerApplications(tests);
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage,
			String newSequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new SuccessfullReasonerApplication(sequent, new EmptyInput(),
				newSequentImage);
	}

	private IProverSequent seq(String hiddenHypsImage, String defaultHypsImage,
			String selHypsImage, String goalImage) {
		return genFullSeq("a=S; b=S", hiddenHypsImage, defaultHypsImage,
				selHypsImage, goalImage);
	}

	@Override
	protected void addUnsuccessfullReasonerApplications(
			List<UnsuccessfullReasonerApplication> tests) {
		// Two associative predicates equivalent but not exactly equal
		// (∨)
		tests.add(makeFailure(" 1=x∨2=y ;; y=2∨x=1 |- ⊤ "));
		// Two associative predicates equivalent but not exactly equal
		// (∧)
		tests.add(makeFailure(" 1=x∧2=y ;; y=2∧x=1 |- ⊤ "));
		// Two associative predicates : one containing the other one (∨)
		tests.add(makeFailure(" 1=x∨2=y ;; 3∈P∨x=1∨y=2 |- ⊤ "));
		// Two associative predicates : one containing the other one (∧)
		tests.add(makeFailure(" 1=x∧2=y ;; 3∈P∧x=1∧y=2 |- ⊤ "));
		// With associative predicates equivalent (∧)
		tests.add(makeFailure(" 1=x∧2∈P |- x=1∧2∈P ⇒ 3∈P "));
		// With associative predicates equivalent (∨)
		tests.add(makeFailure(" 1=x∨2∈P |- x=1∨2∈P ⇒ 3∈P ")); //

		super.addUnsuccessfullReasonerApplications(tests);
	}

	private UnsuccessfullReasonerApplication makeFailure(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new UnsuccessfullReasonerApplication(sequent, new EmptyInput());
	}
}
