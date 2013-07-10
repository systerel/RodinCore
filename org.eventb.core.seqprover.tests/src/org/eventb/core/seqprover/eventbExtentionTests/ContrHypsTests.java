/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner.Input;

public class ContrHypsTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.contrHyps";
	}
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// Simple contradiction
				makeSuccess(" 1>x ;; ¬ 1>x |- 2>x ", //
							"¬ 1>x"),
				// Conjunctive negation and separate conjunct contradictions
				makeSuccess(" 1>x ;; 2>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
							"¬ (1>x ∧ 2>x)"),
		};
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage,
			String inputImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		final Input input = new Input(genPred(inputImage));
		return new SuccessfullReasonerApplication(sequent, input);
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Input is not a negation
				makeFailure("1>x ;; ¬ 1>x |- 2>x ", //
							"1>x", //
							"Predicate 1>x is not a negation"),
				// No negation of simple contradiction
				makeFailure("¬ 1>x |- 2>x ", //
							"¬ 1>x", //
							"Predicate ¬1>x is not contradicted by hypotheses"),
				// No negation of conjunctive contradiction
				makeFailure(" 1>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
							"¬ (1>x ∧ 2>x)", //
							"Predicate ¬(1>x∧2>x) is not contradicted by hypotheses"),
				// Conjunctive negation and conjunctive contradiction
				// (conjuncts will have to be split so that the rule applies)
				makeFailure(" 1>x ∧ 2>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
							"¬ (1>x ∧ 2>x)", //
							"Predicate ¬(1>x∧2>x) is not contradicted by hypotheses"),
				};
	}

	private UnsuccessfullReasonerApplication makeFailure(String sequentImage,
			String inputImage, String expected) {
		final IProverSequent sequent = genSeq(sequentImage);
		final Input input = new Input(genPred(inputImage));
		return new UnsuccessfullReasonerApplication(sequent, input, expected);
	}

}
