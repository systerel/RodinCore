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

import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.GeneralizedModusPonensL2;
import org.junit.Test;

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

	@Test
	public void successL2() throws Exception {
		// Apply once in the hypothesis 1/2 (TRUE)
		// Integer equivalences
		assertReasonerSuccess(" 1<x ;; x>1⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[x>1⇒2∈P][][1<x ;; ⊤⇒2∈P] |- ⊤");
		// Set equivalences
		assertReasonerSuccess("{1}⊂{2} ;; {1}⊆{2}⇒2∈P |- ⊤ ",
				"{P=ℙ(ℤ)}[{1}⊆{2}⇒2∈P][][{1}⊂{2} ;; ⊤⇒2∈P] |- ⊤");
		// Scalar equality
		assertReasonerSuccess(seq("", "a=b ;; b=a⇒2∈P", "", "1∈P"), //
				seq("b=a⇒2∈P", "a=b ;; ⊤⇒2∈P" ,"" ,"1∈P"));
		// Apply once in the hypothesis 2/2 (TRUE)
		assertReasonerSuccess(" 1=x ;; ¬x=1⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬x=1⇒2∈P][][1=x ;; ¬⊤⇒2∈P] |- ⊤");
		assertReasonerSuccess(" 1=x ;; 1<x⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[1<x⇒2∈P][][1=x ;; ⊥⇒2∈P] |- ⊤");
		assertReasonerSuccess(" 1<x ;; ¬x=1⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬x=1⇒2∈P][][1<x ;; ¬⊥⇒2∈P] |- ⊤");
		// Apply once in goal 1/2 (TRUE)
		assertReasonerSuccess(" 1=x |- x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1=x] |- ⊤⇒2∈P");
		assertReasonerSuccess(" 1=x |- ¬x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1=x] |- ¬⊤⇒2∈P");
		// Apply once in goal 2/2 (TRUE)
		assertReasonerSuccess(" 1=x |- x>1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1=x] |- ⊥⇒2∈P");
		assertReasonerSuccess(" 1<x |- ¬x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][1<x] |- ¬⊥⇒2∈P");
		// Apply once in the hypothesis 1/2 (FALSE)
		assertReasonerSuccess(" 1>x ;; x=1⇒2∈P |- ⊤ ", //
				"{P=ℙ(ℤ)}[(x=1⇒2∈P)][][1>x ;; ⊥⇒2∈P] |- ⊤");
		// Apply once in the hypothesis 2/2 (FALSE)
		assertReasonerSuccess(" ¬x=1⇒2∈P |- 1=x ", //
				"{P=ℙ(ℤ)}[¬x=1⇒2∈P][][¬⊥⇒2∈P] |- 1=x");
		// Apply once in goal 1/2 (FALSE)
		assertReasonerSuccess(" ¬1=x |- x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][¬1=x] |- ⊥⇒2∈P");
		// Apply once in goal 2/2 (FALSE)
		assertReasonerSuccess("  ¬1=x |- ¬x=1⇒2∈P ", //
				"{P=ℙ(ℤ)}[][][¬1=x] |- ¬⊥⇒2∈P");
		// Apply in both hypothesis and goal
		assertReasonerSuccess(" 1=x ;; (x=1⇒2∈P)⇒3∈P |- 2∈P⇒x=1 ", //
				"{P=ℙ(ℤ)}[(x=1⇒2∈P)⇒3∈P][][1=x ;; (⊤⇒2∈P)⇒3∈P] |- 2∈P⇒⊤");
		// Apply in several hypothesis
		assertReasonerSuccess(" 1=x ;; ¬(x=1⇒2∈P) ;; (¬x=1⇒3∈P) |- ⊤ ", //
				"{P=ℙ(ℤ)}[¬(x=1⇒2∈P) ;; (¬x=1⇒3∈P)][][1=x ;; ¬(⊤⇒2∈P) ;; (¬⊤⇒3∈P)] |- ⊤");
		// Apply several times in several hypothesis
		assertReasonerSuccess(" 1=x ;; (x=1⇒2∈P) ;; x=1∧(x=1⇒2∈P) |- ⊤ ", //
				"{P=ℙ(ℤ)}[(x=1⇒2∈P) ;; x=1∧(x=1⇒2∈P)][][1=x ;; (⊤⇒2∈P) ;; ⊤∧⊤] |- ⊤");
		// Apply several times in hypothesis
		assertReasonerSuccess(" 1=x ;;  x=1∧(¬x=1⇒(3∈P∧x=1)) |- ⊤ ", //
				"{P=ℙ(ℤ)}[x=1∧(¬x=1⇒(3∈P∧x=1))][][1=x ;; ⊤∧(¬⊤⇒(3∈P∧⊤))] |- ⊤");
		// Apply many times in goal 1/2
		assertReasonerSuccess(" 1=x |- 2∈P⇒x=1 ∧ (x=1 ∨ (¬x=1)⇒2∈P)", //
				"{P=ℙ(ℤ)}[][][1=x] |- 2∈P⇒⊤ ∧ (⊤ ∨ (¬⊤)⇒2∈P)");
		// Apply many times in goal 2/2
		assertReasonerSuccess(" 1=x ;; (2∈P⇒3∈P) |- x=1∧(2∈P⇒3∈P) ", //
				"{P=ℙ(ℤ)}[][][1=x ;; (2∈P⇒3∈P)] |- ⊤∧⊤");
		// Regression test for a case similar to bug #764:
		// Two equivalent hypotheses do not rewrite each other.
		assertReasonerSuccess(" a<b ;; b>a |- ⊤ ", //
				"{a=ℤ; b=ℤ}[b>a][][a<b ;; ⊤] |- ⊤");
	}

	@Test
	public void failureL2() throws Exception {
		// Two associative predicates equivalent but not exactly equal (∨)
		assertReasonerFailure(" 1=x∨2=y ;; y=2∨x=1 |- ⊤ ");
		// Two associative predicates equivalent but not exactly equal (∧)
		assertReasonerFailure(" 1=x∧2=y ;; y=2∧x=1 |- ⊤ ");
		// Two associative predicates : one containing the other one (∨)
		assertReasonerFailure(" 1=x∨2=y ;; 3∈P∨x=1∨y=2 |- ⊤ ");
		// Two associative predicates : one containing the other one (∧)
		assertReasonerFailure(" 1=x∧2=y ;; 3∈P∧x=1∧y=2 |- ⊤ ");
		// With associative predicates equivalent (∧)
		assertReasonerFailure(" 1=x∧2∈P |- x=1∧2∈P ⇒ 3∈P ");
		// With associative predicates equivalent (∨)
		assertReasonerFailure(" 1=x∨2∈P |- x=1∨2∈P ⇒ 3∈P ");
	}

}
