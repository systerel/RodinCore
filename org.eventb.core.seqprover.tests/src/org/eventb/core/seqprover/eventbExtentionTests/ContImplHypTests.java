/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ContImplHypRewrites;

/**
 * Unit tests for the Contraposition of Implication in Hypotheses reasoner
 * {@link ContImplHypRewrites}
 * 
 * @author Nicolas Beauger
 */
public class ContImplHypTests extends AbstractManualReasonerTests {

	private static final String P1 = "x = 1 ⇒ x = 2";

	private static final String resultP1 = "¬ x = 2 ⇒ ¬ x = 1";

	private static final String P2 = "∀x·x=0 ⇒ (x = 1 ⇒ x = 2)";

	private static final String resultP21 = "∀x· ¬(x = 1 ⇒ x = 2) ⇒ ¬x=0";

	private static final String resultP22 = "∀x· x=0 ⇒ (¬ x = 2 ⇒ ¬ x = 1)";

	@Override
	public String getReasonerID() {
		return new ContImplHypRewrites().getReasonerID();
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		// TODO make a method in Tactics
		return predicate.getPositions(new DefaultFilter() {
			@Override
			public boolean select(BinaryPredicate predicate) {
				return predicate.getTag() == Formula.LIMP;
			}
		});
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] { P1, "ROOT", P2, "1\n" + "1.1", };
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				makeSuccess(P1, "", resultP1),
				makeSuccess(P2, "1", resultP21),
				makeSuccess(P2, "1.1", resultP22),
		};
	}

	private static SuccessfullReasonerApplication makeSuccess(String hyp,
			String position, String result) {
		return new SuccessfullReasonerApplication(
				makeHypSeq(hyp), makeInput(hyp, position),
				makeResultSeq(hyp, result));
	}

	private static IReasonerInput makeInput(String hyp, String position) {
		final Predicate pHyp = genPred(hyp);
		final IPosition pPosition = makePosition(position);
		return new AbstractManualRewrites.Input(pHyp, pPosition);
	}

	private static IProverSequent makeHypSeq(String hyp) {
		return TestLib.genSeq(hyp + " |- ⊤");
	}

	private static IProverSequent makeResultSeq(String hyp, String result) {
		return TestLib.genFullSeq(hyp + ";;" + result + ";H;" + hyp + ";S;"
				+ result + " |- ⊤");
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				makeFailure(P1, "0"),
				makeFailure(P2, "1.0"),
		};
	}

	private static UnsuccessfullReasonerApplication makeFailure(String hyp,
			String position) {
		return new UnsuccessfullReasonerApplication(
				makeHypSeq(hyp),
				makeInput(hyp, position));
	}

}
