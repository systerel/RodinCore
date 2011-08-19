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

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.MapOvrGoal;

/**
 * Unit tests for the MapOvrGoal reasoner.
 * 
 * @author Emmanuel Billaud
 */
public class MapOvrGoalTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new MapOvrGoal()).getReasonerID();
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// Basic test : REL
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A ↔ B |- f{x ↦ y}∈A ↔ B "),
						new HypothesisReasoner.Input(TestLib.genPred(
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈A ↔ B")),
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A ↔ B] |- f∈A ↔ B",
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A ↔ B] |- x∈A",
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A ↔ B] |- y∈B"),
				// Basic test : TREL
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A  B |- f{x ↦ y}∈A  B "),
						new HypothesisReasoner.Input(TestLib.genPred(
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈A  B")),
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A  B] |- f∈A  B",
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A  B] |- x∈A",
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A  B] |- y∈B"),
				// Basic test : PFUN
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A ⇸ B |- f{x ↦ y}∈A ⇸ B "),
						new HypothesisReasoner.Input(TestLib.genPred(
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈A ⇸ B")),
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A ⇸ B] |- f∈A ⇸ B",
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A ⇸ B] |- x∈A",
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A ⇸ B] |- y∈B"),
				// Basic test : TFUN
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A → B |- f{x ↦ y}∈A → B "),
						new HypothesisReasoner.Input(TestLib.genPred(
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈A → B")),
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A → B] |- f∈A → B",
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A → B] |- x∈A",
						"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A → B] |- y∈B"), };
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Goal is not an Inclusion
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- f{x ↦ y}∉ℙ(ℤ) → ℙ(ℤ) "),
						new HypothesisReasoner.Input(null)),
				// Left member is not an Overriding
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- f∖{x ↦ y}∈ℙ(ℤ) → ℙ(ℤ) "),
						new HypothesisReasoner.Input(null)),
				// Right Member is not a Relation, or a Total Relation, or a
				// Partial Function or a Total Function.
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- f{x ↦ y}∈ℙ(ℤ) ⤖ ℙ(ℤ) "),
						new HypothesisReasoner.Input(null)),
				// The function is not override by a singleton
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- fg∈ℙ(ℤ) → ℙ(ℤ) "),
						new HypothesisReasoner.Input(null)),
				// The singleton is not a mapplet
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- f{a}∈ℙ(ℤ) → ℙ(ℤ) "),
						new HypothesisReasoner.Input(null)),
				// Input is not contained in hypothesis
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A  B|- f{x ↦ y}∈A → B "),
						new HypothesisReasoner.Input(TestLib.genPred(
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈A → B"))),
				// Input is not contained in hypothesis
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A → B|- f{x ↦ y}∈A → B "),
						new HypothesisReasoner.Input(TestLib.genPred(
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈A  B"))),
				// Input is incorrect
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A → B|- f{x ↦ y}∈A → B "),
						new HypothesisReasoner.Input(TestLib.genPred(
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∉A → B"))),
				// Input is incorrect
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A → B|- f{x ↦ y}∈A → B "),
						new HypothesisReasoner.Input(TestLib.genPred(
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈C"))), };
	}

}
