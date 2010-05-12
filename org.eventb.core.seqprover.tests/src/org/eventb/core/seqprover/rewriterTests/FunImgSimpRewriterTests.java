/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - refactored using AbstractReasonerTests
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.seqprover.eventbExtensions.Lib.ff;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimplifies;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimplifies.Input;
import org.junit.Test;

public class FunImgSimpRewriterTests extends AbstractReasonerTests {

	private static final IPosition FIRST_CHILD = IPosition.ROOT.getFirstChild();
	private static final IReasoner rewriter = new FunImgSimplifies();
	private static final IReasonerInput input = new FunImgSimplifies.Input(
			FIRST_CHILD);
	private static final ITypeEnvironment typeEnv = TestLib
			.genTypeEnv("g=ℤ↔ℤ,f=ℤ↔ℤ");

	private static final String valids[] = { 
			"⇸", // partial function
			"→", // total function
			"↣", // total injection
			"↠", // total surjection
			"⤖", // total bijection
	};

	private static final String invalids[] = { 
			"↔", // relation
			"", // total surjective relation
			"", // total relation
			"", // surjective relation
	};

	/**
	 * Testing all valid relational symbols.
	 */
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final Input input = new FunImgSimplifies.Input(ff.makePosition("0"));
		final List<SuccessfullReasonerApplication> result = new ArrayList<SuccessfullReasonerApplication>();
		for (String arrow : valids) {
			final String resultString1 = "{g=ℤ↔ℤ,f=ℤ↔ℤ}[][][g={1↦3} ;; f∈ℕ"
					+ arrow + "ℕ] |- f(5)=6";
			final String resultString2 = "{g=ℤ↔ℤ,f=ℤ↔ℤ}[][][f∈ℕ" + arrow
					+ "ℕ] |- f(5)=6";
			final IProverSequent seq = TestLib.genFullSeq(typeEnv, "", "",
					"g={1↦3} ;; f∈ℕ" + arrow + "ℕ", "(dom(g)⩤f)(5)=6");
			final IProverSequent seq2 = TestLib.genFullSeq(typeEnv, "", "",
					"f∈ℕ" + arrow + "ℕ", "({1}⩤f)(5)=6");
			result.add(new SuccessfullReasonerApplication(seq, input,
					resultString1));
			result.add(new SuccessfullReasonerApplication(seq2, input,
					resultString2));
		}
		return result
				.toArray(new SuccessfullReasonerApplication[result.size()]);
	}

	/**
	 * Testing all invalid relational symbols.
	 */
	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		final Input input = new FunImgSimplifies.Input(ff.makePosition("0"));
		final List<UnsuccessfullReasonerApplication> result = new ArrayList<UnsuccessfullReasonerApplication>();
		for (String arrow : invalids) {
			final String goal1 = "(dom(g) ⩤ f)(5)=6";
			final String resultString1 = "Rewriter org.eventb.core.seqprover."
					+ "funImgSimplifies is inapplicable for goal " + goal1
					+ " at position 0";
			final String goal2 = "({1} ⩤ f)(5)=6";
			final String resultString2 = "Rewriter org.eventb.core.seqprover."
					+ "funImgSimplifies is inapplicable for goal " + goal2
					+ " at position 0";
			final IProverSequent seq = TestLib.genFullSeq(typeEnv, "", "",
					"g={1↦3} ;; f∈ℕ" + arrow + "ℕ", goal1);
			final IProverSequent seq2 = TestLib.genFullSeq(typeEnv, "", "",
					"f∈ℕ" + arrow + "ℕ", "({1}⩤f)(5)=6");
			result.add(new UnsuccessfullReasonerApplication(seq, input,
					resultString1));
			result.add(new UnsuccessfullReasonerApplication(seq2, input,
					resultString2));
		}
		return result.toArray(new UnsuccessfullReasonerApplication[result
				.size()]);
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

	@Override
	public String getReasonerID() {
		return (new FunImgSimplifies()).getReasonerID();
	}

}
