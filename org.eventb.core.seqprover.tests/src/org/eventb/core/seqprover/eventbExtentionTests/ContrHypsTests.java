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


import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;

public class ContrHypsTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.contrHyps";
	}
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// Simple contradiction
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1>x ;; ¬ 1>x |- 2>x "),
						new HypothesisReasoner.Input(TestLib.genPred("¬ 1>x"))),
				// Conjunctive negation and separate conjunct contradictions
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1>x ;; 2>x ;; ¬ (1>x ∧ 2>x) |- 3>x "),
						new HypothesisReasoner.Input(TestLib.genPred("¬ (1>x ∧ 2>x)"))),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Input is not a negation
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq("1>x ;; ¬ 1>x |- 2>x "), 
						new HypothesisReasoner.Input(TestLib.genPred("1>x")),
						"Predicate 1>x is not a negation"),
				// No negation of simple contradiction
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq("¬ 1>x |- 2>x "), 
						new HypothesisReasoner.Input(TestLib.genPred("¬ 1>x")),
						"Predicate ¬1>x is not contradicted by hypotheses"),
				// No negation of conjunctive contradiction
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" 1>x ;; ¬ (1>x ∧ 2>x) |- 3>x "), 
						new HypothesisReasoner.Input(TestLib.genPred("¬ (1>x ∧ 2>x)")),
						"Predicate ¬(1>x∧2>x) is not contradicted by hypotheses"),
				// Conjunctive negation and conjunctive contradiction
				// (conjuncts will have to be split so that the rule applies)
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" 1>x ∧ 2>x ;; ¬ (1>x ∧ 2>x) |- 3>x "),
						new HypothesisReasoner.Input(TestLib.genPred("¬ (1>x ∧ 2>x)")),
						"Predicate ¬(1>x∧2>x) is not contradicted by hypotheses"),
				};
	}

}
