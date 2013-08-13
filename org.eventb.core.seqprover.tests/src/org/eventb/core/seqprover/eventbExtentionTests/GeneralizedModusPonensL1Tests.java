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

import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import java.util.List;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.GeneralizedModusPonensL1;

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

	@Override
	protected void addSuccessfullReasonerApplications(
			List<SuccessfullReasonerApplication> tests) {
		// Rewrite goal in hypothesis 1/2 (FALSE)
		tests.add(makeSuccess(" 1∈P⇒2∈P |- 1∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒2∈P] |- 1∈P"));
		// Rewrite goal in hypothesis 2/2 (FALSE)
		tests.add(makeSuccess(" ¬1∈P⇒2∈P |- 1∈P ",
				"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][¬⊥⇒2∈P] |- 1∈P"));
		// Rewrite goal in hypothesis 1/2 (TRUE)
		tests.add(makeSuccess(" 1∈P⇒2∈P |- ¬1∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊤⇒2∈P] |- ¬1∈P"));
		// Rewrite goal in hypothesis 2/2 (TRUE)
		tests.add(makeSuccess(" ¬1∈P⇒2∈P |- ¬1∈P ",
				"{P=ℙ(ℤ)}[¬1∈P⇒2∈P][][¬⊤⇒2∈P] |- ¬1∈P"));
		// Rewrite goal disjunct in hypothesis 1/2
		tests.add(makeSuccess(" 1∈P⇒2∈P |- 1∈P∨2∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒⊥] |- 1∈P∨2∈P"));
		// Rewrite goal disjunct in hypothesis 2/2
		tests.add(makeSuccess(" 1∈P⇒2∈P |- 1∈P∨¬2∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][⊥⇒⊤] |- 1∈P∨¬2∈P"));
		// Hypothesis takes precedence other goal for rewriting
		tests.add(makeSuccess(" 1∈P ;; 1∈P⇒2∈P |- 1∈P∨2∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒⊥] |- ⊤∨2∈P"));
		if (!fromLevel2) {
			// Sequent (P⊢P) is not re-written (⊥⊢P) or (P⊢⊤), even when
			// the goal denotes a disjunction.
			tests.add(makeSuccess(" 1∈P∨2∈P |- 1∈P∨2∈P ",
					"{P=ℙ(ℤ)}[1∈P∨2∈P][][⊥∨⊥] |- 1∈P∨2∈P"));
		} else {
			// Sequent (P⊢P) is now systematically re-written
			tests.add(makeSuccess(" 1∈P∨2∈P |- 1∈P∨2∈P ",
					"{P=ℙ(ℤ)}[1∈P∨2∈P][][⊥∨⊥] |- ⊤"));
		}
		// Double-rewrite, larger formula takes precedence
		tests.add(makeSuccess(" 3∈P⇒(1∈P⇒2∈P) |- 1∈P ∨ (1∈P⇒2∈P) ",
				"{P=ℙ(ℤ)}[3∈P⇒(1∈P⇒2∈P)][][3∈P⇒⊥] |- 1∈P ∨ (1∈P⇒2∈P)"));
		tests.add(makeSuccess(" 1∈P ;; 1∈P⇒2∈P |- 1∈P ∨ 3∈P⇒(1∈P⇒2∈P) ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P][][1∈P ;; ⊤⇒2∈P] |- ⊤ ∨ 3∈P⇒⊤"));
		// Ensure that the order of predicates is not significant
		tests.add(makeSuccess(" 3∈P⇒(1∈P⇒2∈P) |- (1∈P⇒2∈P) ∨ 1∈P ",
				"{P=ℙ(ℤ)}[3∈P⇒(1∈P⇒2∈P)][][3∈P⇒⊥] |- (1∈P⇒2∈P) ∨ 1∈P"));
		// Ensure that goal dependence is correctly computed
		tests.add(makeSuccess(" 1∈P⇒2∈P ;; 3∈P ;; 3∈P⇒2∈P |- 1∈P ",
				"{P=ℙ(ℤ)}[1∈P⇒2∈P ;; 3∈P⇒2∈P][][⊥⇒2∈P ;; 3∈P ;; ⊤⇒2∈P] |- 1∈P"));

		super.addSuccessfullReasonerApplications(tests);
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage,
			String newSequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new SuccessfullReasonerApplication(sequent, new EmptyInput(),
				newSequentImage);
	}

}
