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
package org.eventb.core.seqprover.eventbExtentionTests.mbGoal;

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoal;

/**
 * Unit tests for the reasoner MembershipGoal
 * 
 * @author Emmanuel Billaud
 */
public class MembershipGoalTests extends AbstractReasonerTests {

	private static final IReasonerInput EmptyInput = new EmptyInput();

	@Override
	public String getReasonerID() {
		return new MembershipGoal().getReasonerID();
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final ITypeEnvironmentBuilder typeEnv = TestLib
				.mTypeEnvironment("B=ℙ(ℤ); f=ℤ↔ℤ; m=ℙ(ℤ×ℤ×ℤ)");
		return new SuccessfullReasonerApplication[] {
				// Basic test
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; x∈B |- x∈B "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"))),
				// Basic test : works with SUBSETEQ
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊆C"))),
				// Basic test : works with SUBSETEQ
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; f∈ℙ(ℤ×ℤ) ;; B⊆C ;; f(x)∈B |- f(x)∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "f(x)∈B"),
								genPred(typeEnv, "B⊆C"))),
				// Basic test : works with SUBSET
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊂C ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊂C"))),
				// Basic test : works with EQUALITY
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B=C ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B=C"))),
				// Basic test : works with EQUALITY
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; C=B ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "C=B"))),
				// Basic test : works with SETEXT
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; {x,z}⊆B |- x∈C "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "{x,z}⊆B"), genPred(typeEnv,
										"B⊆C"))),
				// Basic test : works with SETEXT
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; {x,z}⊂B |- x∈C "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "{x,z}⊂B"), genPred(typeEnv,
										"B⊆C"))),
				// Basic test : works with SETEXT
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; {x,z}=B |- x∈C "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "{x,z}=B"), genPred(typeEnv,
										"B⊆C"))),
				// Basic test : works with SETEXT
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; B={x,z} |- x∈C "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "B={x,z}"), genPred(typeEnv,
										"B⊆C"))),
				// Basic test : works with MAPSTO
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x↦z∈f |- x∈dom(f) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x↦z∈f"))),
				// Basic test : works with MAPSTO
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x↦z∈f |- z∈ran(f) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x↦z∈f"))),
				// Basic test : works with MAPSTO
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x↦x∈f |- x∈ran(f) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x↦x∈f"))),
				// Basic test : works with MAPSTO
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆g ;; x↦z∈f |- x∈dom(g) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x↦z∈f"),
								genPred(typeEnv, "f⊆g"))),
				// Basic test : works with OVR
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f{x↦y}∈A↔B |- x∈A "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"f{x↦y}∈A↔B"))), };
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		final ITypeEnvironmentBuilder typeEnv = TestLib
				.mTypeEnvironment("B=ℙ(ℤ); f=ℤ↔ℤ; x=ℤ");
		return new UnsuccessfullReasonerApplication[] {
				//
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊤"),
						EmptyInput, "Goal must be a membership."),

				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) |- x∈B"), EmptyInput,
						"The input must be a HypothesesReasoner Input."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) |- x∈B"),
						new HypothesesReasoner.Input(genPred("⊤")),
						"Given predicates are not hypotheses of the sequent."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) |- x∈B"),
						new HypothesesReasoner.Input(null, genPred("⊤")),
						"Given predicates are not hypotheses of the sequent."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) |- x∈B"),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "x∈C")),
						"Given predicates are not hypotheses of the sequent."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊂C ;; C⊆D ;; D⊂E ;; E⊆F ;; x∈B |- x∈F "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊂C"),
								genPred(typeEnv, "C⊆D"), TestLib.genPred(
										TestLib.mTypeEnvironment("E=ℙ(ℤ)"), "E⊆F")),
						"Cannot discharge the goal."),

		};
	}

}