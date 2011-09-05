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
				// Basic test : REL -- REL
				createSuccessfullReasonerApplication("↔", "↔"),
				// Basic test : REL -- SREL
				createSuccessfullReasonerApplication("↔", ""),
				// Basic test : REL -- TREL
				createSuccessfullReasonerApplication("↔", ""),
				// Basic test : REL -- STREL
				createSuccessfullReasonerApplication("↔", ""),
				// Basic test : REL -- PFUN
				createSuccessfullReasonerApplication("↔", "⇸"),
				// Basic test : REL -- TFUN
				createSuccessfullReasonerApplication("↔", "→"),
				// Basic test : REL -- PINJ
				createSuccessfullReasonerApplication("↔", "⤔"),
				// Basic test : REL -- TINJ
				createSuccessfullReasonerApplication("↔", "↣"),
				// Basic test : REL -- PSUR
				createSuccessfullReasonerApplication("↔", "⤀"),
				// Basic test : REL -- TSUR
				createSuccessfullReasonerApplication("↔", "↠"),
				// Basic test : REL -- TBIJ
				createSuccessfullReasonerApplication("↔", "⤖"),
				// Basic test : TREL -- TREL
				createSuccessfullReasonerApplication("", ""),
				// Basic test : TREL -- STREL
				createSuccessfullReasonerApplication("", ""),
				// Basic test : TREL -- TFUN
				createSuccessfullReasonerApplication("", "→"),
				// Basic test : TREL -- TINJ
				createSuccessfullReasonerApplication("", "↣"),
				// Basic test : TREL -- TSUR
				createSuccessfullReasonerApplication("", "↠"),
				// Basic test : TREL -- TBIJ
				createSuccessfullReasonerApplication("", "⤖"),
				// Basic test : PFUN -- PFUN
				createSuccessfullReasonerApplication("⇸", "⇸"),
				// Basic test : PFUN -- TFUN
				createSuccessfullReasonerApplication("⇸", "→"),
				// Basic test : PFUN -- PINJ
				createSuccessfullReasonerApplication("⇸", "⤔"),
				// Basic test : PFUN -- TINJ
				createSuccessfullReasonerApplication("⇸", "↣"),
				// Basic test : PFUN -- PSUR
				createSuccessfullReasonerApplication("⇸", "⤀"),
				// Basic test : PFUN -- TSUR
				createSuccessfullReasonerApplication("⇸", "↠"),
				// Basic test : PFUN -- TBIJ
				createSuccessfullReasonerApplication("⇸", "↠"),
				// Basic test : TFUN -- TFUN
				createSuccessfullReasonerApplication("→", "→"),
				// Basic test : TFUN -- TINJ
				createSuccessfullReasonerApplication("→", "↣"),
				// Basic test : TFUN -- TSUR
				createSuccessfullReasonerApplication("→", "↠"),
				// Basic test : TFUN -- TBIJ
				createSuccessfullReasonerApplication("→", "↠"), };
	}

	private SuccessfullReasonerApplication createSuccessfullReasonerApplication(
			String opGoal, String opHyp) {
		return new SuccessfullReasonerApplication(
				TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A " + opHyp
						+ " B |- f{x ↦ y}∈A " + opGoal + " B "),
				new HypothesisReasoner.Input(TestLib.genPred(
						TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈A " + opHyp + " B")),
				"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A "
						+ opHyp + " B] |- x∈A",
				"{f=ℙ(ℤ×ℤ), A=ℙ(ℤ), B=ℙ(ℤ), y=ℤ, x=ℤ}[][][A=ℤ ;; B=ℤ ;; f∈A "
						+ opHyp + " B] |- y∈B");
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
								TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈C"))),
				// Fails on type relation : SREL
				createUnsuccessfullApplication("", ""),
				// Fails on type relation : STREL
				createUnsuccessfullApplication("", ""),
				// Fails on type relation : PINJ
				createUnsuccessfullApplication("⤔", "⤔"),
				// Fails on type relation : TINJ
				createUnsuccessfullApplication("↣", "↣"),
				// Fails on type relation : PSUR
				createUnsuccessfullApplication("⤀", "⤀"),
				// Fails on type relation : TSUR
				createUnsuccessfullApplication("↠", "↠"),
				// Fails on type relation : TBIJ
				createUnsuccessfullApplication("⤖", "⤖"),
				// Fails on type relation : TREL -- REL
				createUnsuccessfullApplication("", "↔"),
				// Fails on type relation : TREL -- SREL
				createUnsuccessfullApplication("", ""),
				// Fails on type relation : TREL -- PFUN
				createUnsuccessfullApplication("", "⇸"),
				// Fails on type relation : TREL -- PINJ
				createUnsuccessfullApplication("", "⤔"),
				// Fails on type relation : TREL -- PSUR
				createUnsuccessfullApplication("", "⤀"),
				// Fails on type relation : PFUN -- REL
				createUnsuccessfullApplication("⇸", "↔"),
				// Fails on type relation : PFUN -- TREL
				createUnsuccessfullApplication("⇸", ""),
				// Fails on type relation : PFUN -- SREL
				createUnsuccessfullApplication("⇸", ""),
				// Fails on type relation : PFUN -- STREL
				createUnsuccessfullApplication("⇸", ""),
				// Fails on type relation : TFUN -- REL
				createUnsuccessfullApplication("→", "↔"),
				// Fails on type relation : TFUN -- TREL
				createUnsuccessfullApplication("→", ""),
				// Fails on type relation : TFUN -- SREL
				createUnsuccessfullApplication("→", ""),
				// Fails on type relation : TFUN -- STREL
				createUnsuccessfullApplication("→", ""),
				// Fails on type relation : TFUN -- PFUN
				createUnsuccessfullApplication("→", "⇸"),
				// Fails on type relation : TFUN -- PINJ
				createUnsuccessfullApplication("→", "⤔"),
				// Fails on type relation : TFUN -- PSUR
				createUnsuccessfullApplication("→", "⤀"),};
	}

	private UnsuccessfullReasonerApplication createUnsuccessfullApplication(
			String opGoal, String opHyp) {
		return new UnsuccessfullReasonerApplication(
				TestLib.genSeq(" A=ℤ ;; B=ℤ ;; f∈A " + opHyp
						+ " B|- f{x ↦ y}∈A " + opGoal + " B "),
				new HypothesisReasoner.Input(TestLib.genPred(
						TestLib.genTypeEnv("f=ℙ(ℤ×ℤ)"), "f∈A " + opHyp + " B")));
	}

}
