/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.seqprover.eventbExtensions.Lib.ff;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimplifies;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimplifies.Input;
import org.junit.Test;

public class FunImgSimpRewriterTests {

	private static final IPosition FIRST_CHILD = IPosition.ROOT.getFirstChild();
	private static final IReasoner rewriter = new FunImgSimplifies();
	private static final IReasonerInput input = new FunImgSimplifies.Input(
			FIRST_CHILD);

	private static <T> void assertSingleton(T expectedSingle, Collection<T> coll) {
		assertEquals(Collections.singleton(expectedSingle), coll);
	}

	/**
	 * Test valid functional symbols.
	 */
	@Test
	public void testValids() {
		// partial function
		doValidTest("⇸");

		// total function
		doValidTest("→");

		// total injection
		doValidTest("↣");

		// total surjection
		doValidTest("↠");

		// total bijection
		doValidTest("⤖");
	}

	private static void doValidTest(String arrow) {
		final IProverSequent seq = TestLib.genSeq("g={1↦3} ;; f∈ℕ" + arrow
				+ "ℕ |- (dom(g)⩤f)(5)=6");
		assertValid(seq);

		final IProverSequent seq2 = TestLib.genSeq("f∈ℕ" + arrow
				+ "ℕ |- ({1}⩤f)(5)=6");
		assertValid(seq2);
	}

	private static void assertValid(IProverSequent seq) {
		Predicate neededHyp = null;
		for (Predicate pred : seq.hypIterable()) {
			neededHyp = pred;
		}
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IProofRule);
		final IProofRule rule = (IProofRule) output;
		final Set<Predicate> actualNeededHyps = rule.getNeededHyps();
		assertSingleton(neededHyp, actualNeededHyps);

		final IAntecedent[] antecedents = rule.getAntecedents();
		assertEquals(1, antecedents.length);
		final Predicate pred = antecedents[0].getGoal();
		assertEquals(TestLib.genPred("f(5)=6"), pred);
	}

	/**
	 * Testing all invalid relational symbols.
	 */
	@Test
	public void testInvalid() {
		// relation
		doInvalidTest("↔");

		// total surjective relation
		doInvalidTest("");

		// total relation
		doInvalidTest("");

		// surjective relation
		doInvalidTest("");

	}

	private static void doInvalidTest(String arrow) {
		final IProverSequent seq = TestLib.genSeq("f∈ℕ" + arrow
				+ "ℕ |- ({1}⩤f)(5)=6");
		assertInvalid(seq);
		final IProverSequent seq2 = TestLib.genSeq("g={1↦3} ;; f∈ℕ" + arrow
				+ "ℕ |- (dom(g)⩤f)(5)=6");
		assertInvalid(seq2);
	}

	private static void assertInvalid(IProverSequent seq) {
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * The missing hypothesis f∈ℕ op ℕ results in a reasoner failure.
	 */
	@Test
	public void testMissingHypothesis() {
		final IProverSequent seq = TestLib.genSeq("⊤ |- ({1}⩤f)(5)=6");
		assertInvalid(seq);
	}

	/**
	 * Checking that the reasoner doesn't apply with wrong positions.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWrongPosition() throws Exception {
		final IProverSequent seq = TestLib.genSeq("f∈ℕ→ℕ |- ({1}⩤f)(5)=6");

		// position out of the goal
		final IPosition posOut = ff.makePosition("3");
		doTestWrongPosition(seq, new Input(posOut));

		// position of ℕ
		final IPosition posN = ff.makePosition("1");
		doTestWrongPosition(seq, new Input(posN));

		// position of (5)
		final IPosition posDom01 = ff.makePosition("0.1");
		doTestWrongPosition(seq, new Input(posDom01));
	}

	private static void doTestWrongPosition(IProverSequent seq, Input input) {
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Try to apply the FunImgSimp rule on a relation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotApplicable() throws Exception {
		final IProverSequent seq = TestLib
				.genSeq("⊤ |- ({4} ⩤ {1 ↦ 2,1 ↦ 3,4 ↦ 5})(4)=5");
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Testing with a wrong hypothesis
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotConcerned() throws Exception {
		final IProverSequent seq = TestLib
				.genSeq("f∈ℕ→ℕ  |- ({4} ⩤ {1 ↦ 2,1 ↦ 3,4 ↦ 5})(1)=2");
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

}
