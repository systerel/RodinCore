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
import org.eventb.internal.core.seqprover.eventbExtensions.ExE;

/**
 * Acceptance tests for deprecated reasoner ExE.
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("deprecation")
public class ExETests extends AbstractReasonerTests {
	
	private static final Predicate HYP = genPred("∃x·x=1");
	private static final Predicate HYP2PREDS = genPred("∃x·x=1∧x=2");
	private static final Predicate HYP2VARS = genPred("∃x,y·x↦y=1↦2");

	@Override
	public String getReasonerID() {
		return ExE.REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				// one bound variable
				new SuccessfullReasonerApplication(genSeq(HYP + " |- ⊥ "), 
						new ExE.Input(HYP),
						"{}[][" + HYP + "][x=1] |- ⊥"
				),
				// two predicates generated
				new SuccessfullReasonerApplication(genSeq(HYP2PREDS + " |- ⊥ "), 
						new ExE.Input(HYP2PREDS),
						"{}[][" + HYP2PREDS + "][x=1;; x=2] |- ⊥"
				),
				// two bound variables
				new SuccessfullReasonerApplication(genSeq(HYP2VARS + " |- ⊥ "), 
						new ExE.Input(HYP2VARS),
						"{}[][" + HYP2VARS + "][x↦y=1↦2] |- ⊥"
				),
				// name collision
				new SuccessfullReasonerApplication(genSeq(HYP + ";; x=3 |- ⊥ "), 
						new ExE.Input(HYP),
						"{}[][" + HYP + "][x0=1;; x=3] |- ⊥"
				),
				// name collision, different type
				new SuccessfullReasonerApplication(genSeq(HYP + ";; x=TRUE |- ⊥ "), 
						new ExE.Input(HYP),
						"{}[][" + HYP + "][x0=1;; x=TRUE] |- ⊥"
				),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// hyp null
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- ⊥ "),
						new ExE.Input(null),
						"Null hypothesis"
				),
				// hyp not present
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- ⊥ "),
						new ExE.Input(HYP),
						"Nonexistent hypothesis: " + HYP
				),
				// hyp not existentially quantified
				new UnsuccessfullReasonerApplication(
						genSeq(" ∀x·x = 1 |- ⊥ "),
						new ExE.Input(genPred("∀x·x=1")),
						"Hypothesis is not existentially quantified: ∀x·x=1"
				),	
		};
	}

}
