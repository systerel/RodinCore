/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added mathematical extension related tests
 *     Systerel - port to AbstractReasonerTests
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerExtentionTests.ExtendedOperators.AssocExt;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;

/**
 * Unit tests for the Eh reasoner
 * 
 * @author htson
 */
public class EhTests extends AbstractReasonerTests {

	private static final FormulaFactory FF_WITH_ASSOC = FormulaFactory
			.getInstance(AssocExt.getInstance());

	public EhTests() {
		super(FF_WITH_ASSOC);
	}

	@Override
	public String getReasonerID() {
		return Eq.REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				new SuccessfullReasonerApplication(
						genSeq("0 = 1 ;; 0+1 = 2 |- 1+0+1 = 3"),
						makeInput("0 = 1"),
						"{}[][0+1 = 2][0 = 1 ;; 1+1 = 2] |- 1+1+1 = 3"),
				new SuccessfullReasonerApplication(
						genSeq("0 + 1 = 1 ;; 0+1 = 2 |- 2+0+1 = 3"),
						makeInput("0 + 1 = 1"),
						"{}[][0+1=2][0+1=1 ;; 1=2] |- 2+1=3"),
				new SuccessfullReasonerApplication(
						genSeq("0 + 1 + 2 = 2 + 1 ;; 0+1 = 0+1+2 |- 2+0+1 = 0+1+2+3 "),
						makeInput("0 + 1 + 2 = 2 + 1"),
						"{}[][0+1=0+1+2][0+1+2=2+1 ;; 0+1=2+1] |- 2+0+1 = 2+1+3"),
				new SuccessfullReasonerApplication(
						genSeq("1 = 2 ;; 1+1 = 2 |- 1+1+1 = 3"),
						makeInput("1 = 2"),//
						"{}[][1+1=2][1 = 2;; 2+2=2] |- 2+2+2=3"),
				new SuccessfullReasonerApplication(
						genSeq("1+2 = 12 |- 0+1+2+3 = 123"),
						makeInput("1+2 = 12"),//
						"{}[][][1+2=12] |- 0+12+3=123"),
				// bug 3389537
				new SuccessfullReasonerApplication(
						genSeq("1●2 = 12 ;; 1●2 + 1●2 = 2 |- 1 + 1●2 + 1 = 1●2"),//
						makeInput("1●2 = 12"),
						"{}[][1●2 + 1●2 = 2][1●2 = 12;; 12+12=2] |- 1+12+1=12"),
				// bug 3389537
				new SuccessfullReasonerApplication(
						genSeq("1●2 = 12 ;; 0●1●2●3 = 123 |- 0 = 1 + 1●2●3 + 1"),//
						makeInput("1●2 = 12"),
						"{}[][0●1●2●3 = 123][1●2=12;; 0●12●3 = 123] |- 0 = 1 + 12●3 + 1"),

		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// eqHyp not present
				new UnsuccessfullReasonerApplication(//
						genSeq("⊤ |- ⊤"), makeInput("1=2")),
				// eqHyp not an equality
				new UnsuccessfullReasonerApplication(//
						genSeq("⊤ |- ⊥"), makeInput("⊤")),
				// nothing to do
				new UnsuccessfullReasonerApplication(//
						genSeq("1=2 ;; ⊤ |- ⊤"), makeInput("1=2")),
				// nothing to do
				new UnsuccessfullReasonerApplication(
						genSeq("1=2 ;; 1=1 ;; 2=2 |- ⊤"), makeInput("1=2")),

		};
	}

	private IProverSequent genSeq(String seqImage) {
		return TestLib.genSeq(seqImage, ff);
	}

	private HypothesisReasoner.Input makeInput(String predImage) {
		final Predicate pred = genPred(predImage, ff);
		return new HypothesisReasoner.Input(pred);
	}

}
