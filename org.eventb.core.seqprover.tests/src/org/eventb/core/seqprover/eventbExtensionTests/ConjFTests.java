/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.internal.core.seqprover.eventbExtensions.ConjF;

/**
 * Acceptance tests for reasoner ConjF.
 * 
 * @author Laurent Voisin
 */
public class ConjFTests extends AbstractReasonerTests {
	
	private static Predicate HYP = genPred("x=1 ∧ y=2");
	private static Predicate HYP3 = genPred("x=1 ∧ y=2 ∧ z=3");
	private static Predicate HYP_NESTED = genPred("x=1 ∧ (y=2 ∧ z=3)");
	private static Predicate HYP_BAD = genPred("x=1 ∨ y=2");

	@Override
	public String getReasonerID() {
		return ConjF.REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				// hyp not present: success but no effect
				new SuccessfullReasonerApplication(
						genSeq(" |- ⊥ "),
						new ConjF.Input(HYP),
						"{}[][][] |- ⊥"
				),
				// two predicates
				new SuccessfullReasonerApplication(
						genSeq(HYP + " |- ⊥ "), 
						new ConjF.Input(HYP),
						"{}[" + HYP + "][][x=1;; y=2] |- ⊥"
				),
				// three predicates
				new SuccessfullReasonerApplication(
						genSeq(HYP3 + " |- ⊥ "), 
						new ConjF.Input(HYP3),
						"{}[" + HYP3 + "][][x=1;; y=2;; z=3] |- ⊥"
				),
				// nested conjunction is not split
				new SuccessfullReasonerApplication(
						genSeq(HYP_NESTED + " |- ⊥ "), 
						new ConjF.Input(HYP_NESTED),
						"{}[" + HYP_NESTED + "][][x=1;; y=2 ∧ z=3] |- ⊥"
				),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// hyp null
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- ⊥ "),
						new ConjF.Input(null),
						"Null hypothesis"
				),
				// hyp not a conjunction
				new UnsuccessfullReasonerApplication(
						genSeq(HYP_BAD + " |- ⊥ "),
						new ConjF.Input(HYP_BAD),
						"Predicate is not a conjunction: " + HYP_BAD
				),	
		};
	}

}
