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

import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.GeneralizedModusPonensL1;
import org.junit.Test;

/**
 * Unit tests for the reasoner GeneralizedModusPonensL1.
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedModusPonensL1Tests extends GeneralizedModusPonensL0Tests {

	// The reasoner for testing.
	private static final AbstractGenMP GenMP_L1 = new GeneralizedModusPonensL1();

	public GeneralizedModusPonensL1Tests() {
		this(GenMP_L1);
	}

	protected GeneralizedModusPonensL1Tests(AbstractGenMP rewriter) {
		super(rewriter);
	}

	@Test
	public void successL1() throws Exception {
		// Rewrite goal in hypothesis 1/2 (FALSE)
		assertReasonerSuccess(" 1∈P⇒2∈P |- 1∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒2∈P] |- 1∈P");
		// Rewrite goal in hypothesis 2/2 (FALSE)
		assertReasonerSuccess(" ¬1∈P⇒2∈P |- 1∈P ",
				"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][¬⊥⇒2∈P] |- 1∈P");
		// Rewrite goal in hypothesis 1/2 (TRUE)
		assertReasonerSuccess(" 1∈P⇒2∈P |- ¬1∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊤⇒2∈P] |- ¬1∈P");
		// Rewrite goal in hypothesis 2/2 (TRUE)
		assertReasonerSuccess(" ¬1∈P⇒2∈P |- ¬1∈P ",
				"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][¬⊤⇒2∈P] |- ¬1∈P");
		// Rewrite goal disjunct in hypothesis 1/2
		assertReasonerSuccess(" 1∈P⇒2∈P |- 1∈P∨2∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒⊥] |- 1∈P∨2∈P");
		// Rewrite goal disjunct in hypothesis 2/2
		assertReasonerSuccess(" 1∈P⇒2∈P |- 1∈P∨¬2∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒⊤] |- 1∈P∨¬2∈P");
		// Hypothesis takes precedence other goal for rewriting
		assertReasonerSuccess(" 1∈P ;; 1∈P⇒2∈P |- 1∈P∨2∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒⊥] |- ⊤∨2∈P");
		if (!fromLevel2) {
			// Sequent (P⊢P) is not re-written (⊥⊢P) or (P⊢⊤), even when
			// the goal denotes a disjunction.
			assertReasonerSuccess(" 1∈P∨2∈P |- 1∈P∨2∈P ",
					"{P=ℙ(ℤ)}[1∈P∨2∈P][][⊥∨⊥] |- 1∈P∨2∈P");
		} else {
			// Sequent (P⊢P) is now systematically re-written
			assertReasonerSuccess(" 1∈P∨2∈P |- 1∈P∨2∈P ",
					"{P=ℙ(ℤ)}[1∈P∨2∈P][][⊥∨⊥] |- ⊤");
		}
		// Double-rewrite, larger formula takes precedence
		assertReasonerSuccess(" 3∈P⇒(1∈P⇒2∈P) |- 1∈P ∨ (1∈P⇒2∈P) ",
				"{P=ℙ(ℤ)}[3∈P⇒(1∈P⇒2∈P)][][3∈P⇒⊥] |- 1∈P ∨ (1∈P⇒2∈P)");
		assertReasonerSuccess(" 1∈P ;; 1∈P⇒2∈P |- 1∈P ∨ 3∈P⇒(1∈P⇒2∈P) ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒2∈P] |- ⊤ ∨ 3∈P⇒⊤");
		// Ensure that the order of predicates is not significant
		assertReasonerSuccess(" 3∈P⇒(1∈P⇒2∈P) |- (1∈P⇒2∈P) ∨ 1∈P ",
				"{P=ℙ(ℤ)}[3∈P⇒(1∈P⇒2∈P)][][3∈P⇒⊥] |- (1∈P⇒2∈P) ∨ 1∈P");
		// Ensure that goal dependence is correctly computed
		assertReasonerSuccess(" 1∈P⇒2∈P ;; 3∈P ;; 3∈P⇒2∈P |- 1∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P ;; 3∈P⇒2∈P][][⊥⇒2∈P ;; 3∈P ;; ⊤⇒2∈P] |- 1∈P");
	}

}
