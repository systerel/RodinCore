/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
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
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner.Input;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.ContradictionFinder.ContradictionInSetFinder;
import org.junit.Assert;
import org.junit.Test;

public class ContrHypsTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.contrHyps";
	}
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final String typEnv = "A=ℙ(ℤ); B=ℙ(ℤ)";
		return new SuccessfullReasonerApplication[] {
				// H, nP† ;; P |- G

				// Simple contradiction
				makeSuccess(" 1>x ;; ¬ 1>x |- 2>x ", //
						"¬ 1>x"),
				// Contradict by a negation of an equivalent predicate
				makeSuccess(" 1=x ;; ¬ x=1 |- 2>x ", //
						"¬ x=1"),
				makeSuccess(typEnv, " A⊂B ;; ¬ A⊂B |- 2>x ", //
						"¬ A⊂B"),
				makeSuccess(typEnv, " A⊆B ;; ¬ A⊆B |- 2>x ", //
						"¬ A⊆B"),
				// Contradict a positive predicate by a positive predicate
				makeSuccess(" 1<x ;; 1=x |- 2>x ", //
						"1=x"),
				makeSuccess(" 1>x ;; 1=x |- 2>x ", //
						"1=x"),
				makeSuccess(" x<1 ;; 1=x |- 2>x ", //
						"1=x"),
				makeSuccess(" x>1 ;; 1=x |- 2>x ", //
						"1=x"),
				makeSuccess(" x≥1 ;; 1>x |- 2>x ", //
						"1>x"),
				makeSuccess(" x>1 ;; 1>x |- 2>x ", //
						"1>x"),
				makeSuccess(" x=1 ;; 1>x |- 2>x ", //
						"1>x"),
				makeSuccess(" x<1 ;; 1<x |- 2>x ", //
						"1<x"),
				makeSuccess(" 1=x ;; 1<x |- 2>x ", //
						"1<x"),
				makeSuccess(" x≤1 ;; 1<x |- 2>x ", //
						"1<x"),
				makeSuccess(typEnv, " A⊂B ;; A=B |- 2>x ", //
						"A=B"),
				makeSuccess(typEnv, " B⊂A ;; A=B |- 2>x ", //
						"A=B"),
				makeSuccess(typEnv, " B⊂A ;; A⊆B |- 2>x ", //
						"A⊆B"),
				makeSuccess(typEnv, " B⊂A ;; A⊂B |- 2>x ", //
						"A⊂B"),
				makeSuccess(typEnv, " B⊆A ;; A⊂B |- 2>x ", //
						"A⊂B"),
				makeSuccess(typEnv, " A=B ;; A⊂B |- 2>x ", //
						"A⊂B"),
				makeSuccess(typEnv, " B=A ;; A⊂B |- 2>x ", //
						"A⊂B"),
				// Conjunctive negation and separate conjunct contradictions
				makeSuccess(" 1>x ;; 2>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
						"¬ (1>x ∧ 2>x)"),
				makeSuccess(typEnv, " A=B ;; x=2 ;; ¬ (A⊆B ∧ 2=x) |- 3>x ", //
						"¬ (A⊆B ∧ 2=x)"),
				// Conjunctive negation (equivalent predicate)
				makeSuccess(" 1=x ;; 2>x ;; 3≥x ;; ¬ (x=1 ∧ x<2 ∧ x≤3) |- 3>x ", //
						"¬ (x=1 ∧ x<2 ∧ x≤3)"),
		};
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage,
			String inputImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		final Input input = new Input(genPred(inputImage));
		return new SuccessfullReasonerApplication(sequent, input);
	}

	private SuccessfullReasonerApplication makeSuccess(String typEnvStr, String sequentImage,
			String inputImage) {
		final String typEnv2 = "A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;;";
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment(typEnvStr);
		final IProverSequent sequent = genSeq(typEnv2 + sequentImage);
		final Input input = new Input(TestLib.genPred(typeEnv, inputImage));
		return new SuccessfullReasonerApplication(sequent, input);
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// No negation of simple contradiction
				makeFailure("¬ 1>x |- 2>x ", //
							"¬ 1>x", //
							"Predicate ¬1>x is not contradicted by hypotheses"),
				// No negation of conjunctive contradiction
				makeFailure(" 1>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
							"¬ (1>x ∧ 2>x)", //
							"Predicate ¬(1>x∧2>x) is not contradicted by hypotheses"),
				// Conjunctive negation and conjunctive contradiction
				// (conjuncts will have to be split so that the rule applies)
				makeFailure(" 1>x ∧ 2>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
							"¬ (1>x ∧ 2>x)", //
							"Predicate ¬(1>x∧2>x) is not contradicted by hypotheses"),
				};
	}

	private UnsuccessfullReasonerApplication makeFailure(String sequentImage,
			String inputImage, String expected) {
		final IProverSequent sequent = genSeq(sequentImage);
		final Input input = new Input(genPred(inputImage));
		return new UnsuccessfullReasonerApplication(sequent, input, expected);
	}

	public Predicate DeserializeInput(String[] neededHyps) {
		Set<Predicate> hyps = new HashSet<Predicate>();
		for (String neededHyp : neededHyps) {
			hyps.add(genPred(neededHyp));
		}
		final ContradictionInSetFinder finder;
		finder = new ContradictionInSetFinder(hyps);
		return finder.getContrHyp();
	}

	@Test
	public void testSuccessDeserializeInput() {
		// simple contradiction with P and ¬P
		successfullDeserialization(new String[]{"¬1>x","1>x"}, "¬1>x");
		// contradiction with P and ¬P' (where P' is equivalent to P)
		successfullDeserialization(new String[]{"¬1=x","x=1"}, "¬1=x");
		// contradiction with a strict relation
		successfullDeserialization(new String[]{"1≥x","1<x"}, "1<x");
		successfullDeserialization(new String[]{"1<x","1>x"}, "1<x");
		// before the normalization
		successfullDeserialization(new String[]{"1<x","¬1≤x"}, "¬1≤x");
	}

	public void successfullDeserialization(String[] neededHyps, String hyp){
		Predicate expected = genPred(hyp);
		Predicate actual = DeserializeInput(neededHyps);
		Assert.assertTrue("Incorrect predicate", expected.equals(actual));
	}

	@Test
	public void testUnsuccessDeserializeInput() {
		// one needed hypothesis
		unSuccessfullDeserialization(new String[]{"1<x"});
		// no contradiction
		unSuccessfullDeserialization(new String[]{"1<x","1≤x"});
	}

	public void unSuccessfullDeserialization(String[] neededHyps){
		assertNull(DeserializeInput(neededHyps));
	}
}
