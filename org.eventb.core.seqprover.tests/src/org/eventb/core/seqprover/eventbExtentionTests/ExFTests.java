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
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.internal.core.seqprover.eventbExtensions.ExF;

/**
 * Acceptance tests for reasoner ExF.
 * 
 * @author Laurent Voisin
 */
public class ExFTests extends AbstractReasonerTests {
	
	private static Predicate HYP = genPred("∃x·x=1");
	private static Predicate HYP2PREDS = genPred("∃x·x=1∧x=2");
	private static Predicate HYP2VARS = genPred("∃x,y·x↦y=1↦2");

	@Override
	public String getReasonerID() {
		return ExF.REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				// hyp not present: success but no effect
				new SuccessfullReasonerApplication(genSeq(" |- ⊥ "),
						new ExF.Input(HYP),
						"{}[][][] |- ⊥"
				),
				// one bound variable
				new SuccessfullReasonerApplication(genSeq(HYP + " |- ⊥ "), 
						new ExF.Input(HYP),
						"{}[" + HYP + "][][x=1] |- ⊥"
				),
				// two predicates generated
				new SuccessfullReasonerApplication(genSeq(HYP2PREDS + " |- ⊥ "), 
						new ExF.Input(HYP2PREDS),
						"{}[" + HYP2PREDS + "][][x=1;; x=2] |- ⊥"
				),
				// two bound variables
				new SuccessfullReasonerApplication(genSeq(HYP2VARS + " |- ⊥ "), 
						new ExF.Input(HYP2VARS),
						"{}[" + HYP2VARS + "][][x↦y=1↦2] |- ⊥"
				),
				// name collision
				new SuccessfullReasonerApplication(genSeq(HYP + ";; x=3 |- ⊥ "), 
						new ExF.Input(HYP),
						"{}[" + HYP + "][][x0=1;; x=3] |- ⊥"
				),
				// name collision, different type
				new SuccessfullReasonerApplication(genSeq(HYP + ";; x=TRUE |- ⊥ "), 
						new ExF.Input(HYP),
						"{}[" + HYP + "][][x0=1;; x=TRUE] |- ⊥"
				),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// hyp null
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- ⊥ "),
						new ExF.Input(null),
						"Null hypothesis"
				),
				// hyp not existentially quantified
				new UnsuccessfullReasonerApplication(
						genSeq(" ∀x·x = 1 |- ⊥ "),
						new ExF.Input(genPred("∀x·x=1")),
						"Predicate is not existentially quantified: ∀x·x=1"
				),	
		};
	}

}
