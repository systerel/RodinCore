/*******************************************************************************
 * Copyright (c) 2009, 2023 Systerel and others.
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
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.junit.Assert.assertNull;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner.Input;
import org.eventb.internal.core.seqprover.eventbExtensions.ContradictionFinder.ContradictionInSetFinder;
import org.junit.Assert;
import org.junit.Test;

public class ContrHypsTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.contrHyps";
	}
	
	@Test
	public void testSuccessfulContrHyps() throws UntranslatableException {
		final String typEnv = "A=ℙ(ℤ); B=ℙ(ℤ)";

		// H, nP† ;; P |- G

		// Simple contradiction
		assertReasonerSuccess(" 1>x ;; ¬ 1>x |- 2>x ", //
				makeInput("¬ 1>x"));
		// Contradict by a negation of an equivalent predicate
		assertReasonerSuccess(" 1=x ;; ¬ x=1 |- 2>x ", //
				makeInput("¬ x=1"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A⊂B ;; ¬ A⊂B |- 2>x ", //
				makeInput(typEnv, "¬ A⊂B"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A⊆B ;; ¬ A⊆B |- 2>x ", //
				makeInput(typEnv, "¬ A⊆B"));
		// Contradict a positive predicate by a positive predicate
		assertReasonerSuccess(" 1<x ;; 1=x |- 2>x ", //
				makeInput("1=x"));
		assertReasonerSuccess(" 1>x ;; 1=x |- 2>x ", //
				makeInput("1=x"));
		assertReasonerSuccess(" x<1 ;; 1=x |- 2>x ", //
				makeInput("1=x"));
		assertReasonerSuccess(" x>1 ;; 1=x |- 2>x ", //
				makeInput("1=x"));
		assertReasonerSuccess(" x≥1 ;; 1>x |- 2>x ", //
				makeInput("1>x"));
		assertReasonerSuccess(" x>1 ;; 1>x |- 2>x ", //
				makeInput("1>x"));
		assertReasonerSuccess(" x=1 ;; 1>x |- 2>x ", //
				makeInput("1>x"));
		assertReasonerSuccess(" x<1 ;; 1<x |- 2>x ", //
				makeInput("1<x"));
		assertReasonerSuccess(" 1=x ;; 1<x |- 2>x ", //
				makeInput("1<x"));
		assertReasonerSuccess(" x≤1 ;; 1<x |- 2>x ", //
				makeInput("1<x"));
		assertReasonerSuccess(" x∈ℕ ;; x<0 |- 2>x ", //
				makeInput("x<0"));
		assertReasonerSuccess(" x≤1 ;; 2≤x |- 2>x ", //
				makeInput("2≤x"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A⊂B ;; A=B |- 2>x ", //
				makeInput(typEnv, "A=B"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B⊂A ;; A=B |- 2>x ", //
				makeInput(typEnv, "A=B"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B⊂A ;; A⊆B |- 2>x ", //
				makeInput(typEnv, "A⊆B"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B⊂A ;; A⊂B |- 2>x ", //
				makeInput(typEnv, "A⊂B"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B⊆A ;; A⊂B |- 2>x ", //
				makeInput(typEnv, "A⊂B"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A=B ;; A⊂B |- 2>x ", //
				makeInput(typEnv, "A⊂B"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B=A ;; A⊂B |- 2>x ", //
				makeInput(typEnv, "A⊂B"));
		// Conjunctive negation and separate conjunct contradictions
		assertReasonerSuccess(" 1>x ;; 2>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
				makeInput("¬ (1>x ∧ 2>x)"));
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A=B ;; x=2 ;; ¬ (A⊆B ∧ 2=x) |- 3>x ", //
				makeInput(typEnv, "¬ (A⊆B ∧ 2=x)"));
		// Conjunctive negation (equivalent predicate)
		assertReasonerSuccess(" 1=x ;; 2>x ;; 3≥x ;; ¬ (x=1 ∧ x<2 ∧ x≤3) |- 3>x ", //
				makeInput("¬ (x=1 ∧ x<2 ∧ x≤3)"));
	}

	private Input makeInput(String inputImage) {
		return new Input(genPred(inputImage));
	}

	private Input makeInput(String typeEnv, String inputImage) {
		return new Input(genPred(mTypeEnvironment(typeEnv), inputImage));
	}

	@Test
	public void testUnsuccessfulContrHyps() throws UntranslatableException {
		// No negation of simple contradiction
		assertReasonerFailure("¬ 1>x |- 2>x ", //
				makeInput("¬ 1>x"), //
				"Predicate ¬1>x is not contradicted by hypotheses");
		// No negation of conjunctive contradiction
		assertReasonerFailure(" 1>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
				makeInput("¬ (1>x ∧ 2>x)"), //
				"Predicate ¬(1>x∧2>x) is not contradicted by hypotheses");
		// Conjunctive negation and conjunctive contradiction
		// (conjuncts will have to be split so that the rule
		// applies)
		assertReasonerFailure(" 1>x ∧ 2>x ;; ¬ (1>x ∧ 2>x) |- 3>x ", //
				makeInput("¬ (1>x ∧ 2>x)"), //
				"Predicate ¬(1>x∧2>x) is not contradicted by hypotheses");
	}

	public Predicate DeserializeInput(String[] neededHyps) {
		Set<Predicate> hyps = new LinkedHashSet<Predicate>();
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
		successfullDeserialization(new String[]{"1≥x","1<x"}, "1≥x");
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
