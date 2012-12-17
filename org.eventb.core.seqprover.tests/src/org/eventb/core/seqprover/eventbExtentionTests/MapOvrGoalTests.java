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

import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.pfun;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.pinj;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.psur;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.rel;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.srel;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.strel;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.tbij;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.tfun;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.tinj;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.trel;
import static org.eventb.core.seqprover.eventbExtentionTests.OperatorString.tsur;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Unit tests for the MapOvrGoal reasoner.
 * 
 * @author Emmanuel Billaud
 */
public class MapOvrGoalTests extends AbstractReasonerTests {

	private static final String notInfering = "Given predicate does not infer a part of the goal.";
	private static final String goalShape = "Goal does not possessed the correct form.";

	private final String hypsStr = " A⊆ℤ ;; B⊆ℤ ;; f∈A ";
	private final String typenvStr = "{f=ℤ↔ℤ; A=ℙ(ℤ); B=ℙ(ℤ); y=ℤ; x=ℤ}[][][";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.mapOvrG";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				createSuccessfullReasonerApplication(rel, rel),
				createSuccessfullReasonerApplication(rel, srel),
				createSuccessfullReasonerApplication(rel, trel),
				createSuccessfullReasonerApplication(rel, strel),
				createSuccessfullReasonerApplication(rel, pfun),
				createSuccessfullReasonerApplication(rel, tfun),
				createSuccessfullReasonerApplication(rel, pinj),
				createSuccessfullReasonerApplication(rel, tinj),
				createSuccessfullReasonerApplication(rel, psur),
				createSuccessfullReasonerApplication(rel, tsur),
				createSuccessfullReasonerApplication(rel, tbij),
				createSuccessfullReasonerApplication(trel, trel),
				createSuccessfullReasonerApplication(trel, strel),
				createSuccessfullReasonerApplication(trel, tfun),
				createSuccessfullReasonerApplication(trel, tinj),
				createSuccessfullReasonerApplication(trel, tsur),
				createSuccessfullReasonerApplication(trel, tbij),
				createSuccessfullReasonerApplication(pfun, pfun),
				createSuccessfullReasonerApplication(pfun, tfun),
				createSuccessfullReasonerApplication(pfun, pinj),
				createSuccessfullReasonerApplication(pfun, tinj),
				createSuccessfullReasonerApplication(pfun, psur),
				createSuccessfullReasonerApplication(pfun, tsur),
				createSuccessfullReasonerApplication(pfun, tbij),
				createSuccessfullReasonerApplication(tfun, tfun),
				createSuccessfullReasonerApplication(tfun, tinj),
				createSuccessfullReasonerApplication(tfun, tsur),
				createSuccessfullReasonerApplication(tfun, tbij), };
	}

	private SuccessfullReasonerApplication createSuccessfullReasonerApplication(
			String opGoal, String opHyp) {
		return new SuccessfullReasonerApplication(genSeq(hypsStr + opHyp
				+ " B |- f{x ↦ y}∈A " + opGoal + " B "),
				new HypothesisReasoner.Input(genPred(mTypeEnvironment("f=ℤ↔ℤ"),
						"f∈A " + opHyp + " B")), typenvStr + hypsStr + opHyp
						+ " B] |- x∈A", typenvStr + hypsStr + opHyp
						+ " B] |- y∈B");
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Goal is not an Inclusion
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- f{x ↦ y}∉ℙ(ℤ) → ℙ(ℤ) "),
						new HypothesisReasoner.Input(null), goalShape),
				// Left member is not an Overriding
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- f∖{x ↦ y}∈ℙ(ℤ) → ℙ(ℤ) "),
						new HypothesisReasoner.Input(null), goalShape),
				// The function is not override by a singleton
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- fg∈ℙ(ℤ) → ℙ(ℤ) "),
						new HypothesisReasoner.Input(null), goalShape),
				// The singleton is not a mapplet
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ |- f{a}∈ℙ(ℤ) → ℙ(ℤ) "),
						new HypothesisReasoner.Input(null), goalShape),
				// Input is not contained in hypothesis
				new UnsuccessfullReasonerApplication(
						genSeq(" A=ℤ ;; B=ℤ ;; f∈A  B|- f{x ↦ y}∈A → B "),
						new HypothesisReasoner.Input(genPred(
								mTypeEnvironment("f=ℤ↔ℤ"), "f∈A → B")),
						"Nonexistent hypothesis: "
								+ genPred(mTypeEnvironment("f=ℤ↔ℤ"), "f∈A → B")),
				// Input is incorrect
				new UnsuccessfullReasonerApplication(
						genSeq(" A=ℤ ;; B=ℤ ;; f∈A → B ;; f∉A → B |- f{x ↦ y}∈A → B "),
						new HypothesisReasoner.Input(genPred(
								mTypeEnvironment("f=ℤ↔ℤ"), "f∉A → B")),
						notInfering),
				// Input is incorrect
				new UnsuccessfullReasonerApplication(
						genSeq(" A=ℤ ;; B=ℤ ;; f∈A → B ;; f∈C |- f{x ↦ y}∈A → B "),
						new HypothesisReasoner.Input(genPred(
								mTypeEnvironment("f=ℤ↔ℤ"), "f∈C")), notInfering),
				// Fails on type relation
				createUnsuccessfullApplication(srel, srel, goalShape),
				createUnsuccessfullApplication(strel, strel, goalShape),
				createUnsuccessfullApplication(pinj, pinj, goalShape),
				createUnsuccessfullApplication(tinj, tinj, goalShape),
				createUnsuccessfullApplication(psur, psur, goalShape),
				createUnsuccessfullApplication(tsur, tsur, goalShape),
				createUnsuccessfullApplication(tbij, tbij, goalShape),
				createUnsuccessfullApplication(trel, rel, notInfering),
				createUnsuccessfullApplication(trel, srel, notInfering),
				createUnsuccessfullApplication(trel, pfun, notInfering),
				createUnsuccessfullApplication(trel, pinj, notInfering),
				createUnsuccessfullApplication(trel, psur, notInfering),
				createUnsuccessfullApplication(pfun, rel, notInfering),
				createUnsuccessfullApplication(pfun, trel, notInfering),
				createUnsuccessfullApplication(pfun, srel, notInfering),
				createUnsuccessfullApplication(pfun, strel, notInfering),
				createUnsuccessfullApplication(tfun, rel, notInfering),
				createUnsuccessfullApplication(tfun, trel, notInfering),
				createUnsuccessfullApplication(tfun, srel, notInfering),
				createUnsuccessfullApplication(tfun, strel, notInfering),
				createUnsuccessfullApplication(tfun, pfun, notInfering),
				createUnsuccessfullApplication(tfun, pinj, notInfering),
				createUnsuccessfullApplication(tfun, psur, notInfering), };
	}

	private UnsuccessfullReasonerApplication createUnsuccessfullApplication(
			String opGoal, String opHyp, String reason) {
		return new UnsuccessfullReasonerApplication(genSeq(hypsStr + opHyp
				+ " B|- f{x ↦ y}∈A " + opGoal + " B "),
				new HypothesisReasoner.Input(genPred(mTypeEnvironment("f=ℤ↔ℤ"),
						"f∈A " + opHyp + " B")), reason);
	}

}
