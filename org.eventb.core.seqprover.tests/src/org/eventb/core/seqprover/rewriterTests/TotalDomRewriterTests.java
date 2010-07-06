/*******************************************************************************
 * Copyright (c) 2009, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored and used tactic applications
 *     Systerel - added tests about needed hyp (ver 1)
 *     Systerel - added tests about hyp rewriting (ver 2)
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.seqprover.eventbExtensions.Lib.ff;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomSubstitutions;
import org.junit.Test;

public class TotalDomRewriterTests {

	private static final IntegerLiteral ZERO = ff.makeIntegerLiteral(
			BigInteger.ZERO, null);
	private static final IntegerLiteral ONE = ff.makeIntegerLiteral(
			BigInteger.ONE, null);
	private static final IPosition FIRST_CHILD = IPosition.ROOT.getFirstChild();
	private static final Expression NAT = ff.makeAtomicExpression(
			Formula.NATURAL, null);

	private static final IReasoner rewriter = new TotalDomRewrites();
	private static final IReasonerInput input = new TotalDomRewrites.Input(
			null, FIRST_CHILD, NAT);

	private static final Type INT = ff.makeIntegerType();
	private static final Type TYPE_F = ff.makeRelationalType(INT, INT);
	private static final Expression ID_F = ff.makeFreeIdentifier("f", null,
			TYPE_F);

	private static <T> List<T> mList(T... args) {
		return Arrays.asList(args);
	}

	private static <T> void assertSameCollections(Collection<T> coll1,
			Collection<T> coll2) {
		assertEquals("bad length", coll1.size(), coll2.size());
		assertTrue("unexpected elements", coll1.containsAll(coll2));
	}

	private static <T> void assertSingleton(T expectedSingle, Collection<T> coll) {
		assertEquals(Collections.singleton(expectedSingle), coll);
	}

	private static void assertSubstitutions(IProverSequent sequent,
			Expression expression, List<Expression> expectedSubstitutes,
			List<Predicate> expectedNeededHyps) {
		assertEquals(expectedSubstitutes.size(), expectedNeededHyps.size());

		final TotalDomSubstitutions substitutions = new TotalDomSubstitutions(
				sequent);
		substitutions.computeSubstitutions();
		final Set<Expression> actualSubstitutes = substitutions.get(expression);

		assertSameCollections(expectedSubstitutes, actualSubstitutes);
		for (int i = 0; i < expectedSubstitutes.size(); i++) {
			final Predicate expectedNeededHyp = expectedNeededHyps.get(i);
			final Predicate actualNeededHyp = substitutions.getNeededHyp(
					expression, expectedSubstitutes.get(i));
			assertEquals("Unexpected needed hypothesis", expectedNeededHyp,
					actualNeededHyp);
		}
	}

	@Test
	public void testValids() {
		// total function
		doValidTest("→");

		// total injection
		doValidTest("↣");

		// total surjection
		doValidTest("↠");
		
		// total bijection
		doValidTest("⤖");

		// total relation
		doValidTest("");

		// total surjective relation
		doValidTest("");
	}

	private static void doValidTest(String arrow) {
		final IProverSequent seq = TestLib.genSeq("f∈ℕ" + arrow
				+ "ℕ |- dom(f)=ℕ");
		final Predicate neededHyp = seq.hypIterable().iterator().next();
		
		assertSubstitutions(seq, ID_F, mList(NAT), mList(neededHyp));
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IProofRule);
		final IProofRule rule = (IProofRule) output;
		final Set<Predicate> actualNeededHyps = rule.getNeededHyps();
		assertSingleton(neededHyp, actualNeededHyps);

		final IAntecedent[] antecedents = rule.getAntecedents();
		assertEquals(1, antecedents.length);
		final Predicate pred = antecedents[0].getGoal();
		assertEquals(TestLib.genPred("ℕ=ℕ"), pred);
	}

	@Test
	public void testInHypothesis() throws Exception {
		final IProverSequent seq = TestLib.genSeq("f∈ℕ→ℕ ;; dom(f)=ℕ |- ⊥");
		final ITypeEnvironment typeEnv = seq.typeEnvironment();
		final Predicate neededHyp = TestLib.genPred(typeEnv, "f∈ℕ→ℕ");
		final Predicate toRewrite = TestLib.genPred(typeEnv, "dom(f)=ℕ");

		assertSubstitutions(seq, ID_F, mList(NAT), mList(neededHyp));

		final IReasonerInput input = new TotalDomRewrites.Input(toRewrite,
				FIRST_CHILD, NAT);

		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IProofRule);
		final IProofRule rule = (IProofRule) output;
		final Set<Predicate> actualNeededHyps = rule.getNeededHyps();
		assertSingleton(neededHyp, actualNeededHyps);
		final IAntecedent[] antecedents = rule.getAntecedents();
		assertEquals(1, antecedents.length);
		final Predicate goal = antecedents[0].getGoal();
		assertNull(goal);
		for (IHypAction hypAction : antecedents[0].getHypActions()) {
			if (hypAction instanceof IHypAction.IForwardInfHypAction) {
				final IForwardInfHypAction fwdInf = (IForwardInfHypAction) hypAction;
				final Collection<Predicate> hyps = fwdInf.getHyps();
				assertSingleton(toRewrite, hyps);
				final Collection<Predicate> inferredHyps = fwdInf
						.getInferredHyps();
				assertSingleton(TestLib.genPred("ℕ=ℕ"), inferredHyps);
			}
		}
	}

	@Test
	public void testSeveralSubstitutes() throws Exception {
		final IProverSequent seq = TestLib
				.genSeq("f∈{1,0}→ℕ ;; f∈{0,1}→ℕ |- dom(f)⊂ℕ");
		final Predicate neededHyp01 = TestLib.genPred("f∈{0,1}→ℕ");
		final Predicate neededHyp10 = TestLib.genPred("f∈{1,0}→ℕ");

		final List<Expression> list01 = new ArrayList<Expression>();
		list01.addAll(Arrays.asList(ZERO, ONE));
		final Expression setExt01 = ff.makeSetExtension(list01, null);
		final List<Expression> list10 = new ArrayList<Expression>();
		list10.addAll(Arrays.asList(ONE, ZERO));
		final Expression setExt10 = ff.makeSetExtension(list10, null);
		assertSubstitutions(seq, ID_F, mList(setExt01, setExt10), mList(
				neededHyp01, neededHyp10));
	}

	@Test
	public void testInvalid() {
		// relation
		doInvalidTest("↔");

		// surjective relation
		doInvalidTest("");

		// partial function
		doInvalidTest("⇸");

		// partial injection
		doInvalidTest("⤔");

		// partial surjection
		doInvalidTest("⤀");
	}

	private static void doInvalidTest(String arrow) {
		final IProverSequent seq = TestLib.genSeq("f∈ℕ" + arrow
				+ "ℕ |- dom(f)=ℕ");
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testWrongPosition() throws Exception {
		final IProverSequent seq = TestLib
				.genSeq("f∈ℕ→ℕ |- dom(f)∪dom({0↦0})=ℕ");

		// position out of the goal
		final IPosition posOut = ff.makePosition("3");
		doTestWrongPosition(seq, posOut);

		// position of ℕ
		final IPosition posN = ff.makePosition("1");
		doTestWrongPosition(seq, posN);

		// position of dom({0↦0}) (no corresponding substitution)
		final IPosition posDom00 = ff.makePosition("0.1");
		doTestWrongPosition(seq, posDom00);
	}

	private static void doTestWrongPosition(IProverSequent seq, IPosition pos) {
		final IReasonerInput input = new TotalDomRewrites.Input(null, pos, NAT);
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}
}