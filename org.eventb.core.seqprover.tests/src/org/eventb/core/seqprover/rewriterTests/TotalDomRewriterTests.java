/*******************************************************************************
 * Copyright (c) 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored and used tactic applications
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomSubstitutions;
import org.junit.Test;

public class TotalDomRewriterTests {

	private static final IntegerLiteral ZERO = TestLib.ff.makeIntegerLiteral(
			BigInteger.ZERO, null);
	private static final IntegerLiteral ONE = TestLib.ff.makeIntegerLiteral(
			BigInteger.ONE, null);
	private static final IPosition FIRST_CHILD = IPosition.ROOT.getFirstChild();
	private static final AtomicExpression NAT = TestLib.ff
			.makeAtomicExpression(Formula.NATURAL, null);

	private static final IReasoner rewriter = new TotalDomRewrites();
	private static final IReasonerInput input = new TotalDomRewrites.Input(
			null, FIRST_CHILD, NAT);

	private static void assertSubstitutions(IProverSequent sequent,
			Expression expression, Expression... expectedSubstitutes) {
		final TotalDomSubstitutions substitutions = new TotalDomSubstitutions(
				sequent);
		substitutions.computeSubstitutions();
		final Set<Expression> actualSubstitutes = substitutions.get(expression);
		assertEquals(expectedSubstitutes.length, actualSubstitutes.size());
		assertTrue(actualSubstitutes.containsAll(Arrays
				.asList(expectedSubstitutes)));
	}

	@Test
	public void testValids() {
		// total function
		doValidTest("→");

		// total injection
		doValidTest("↣");

		// total bijection
		doValidTest("⤖");

		// total relation
		doValidTest("");

		// total surjection
		doValidTest("↠");
	}

	private static void doValidTest(String arrow) {
		final IProverSequent seq = TestLib.genSeq("f∈ℕ" + arrow
				+ "ℕ |- dom(f)=ℕ");
		final Expression id_f = ((UnaryExpression) ((RelationalPredicate) seq
				.goal()).getLeft()).getChild();
		assertSubstitutions(seq, id_f, NAT);
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IProofRule);
		final IProofRule rule = (IProofRule) output;
		final IAntecedent[] antecedents = rule.getAntecedents();
		assertEquals(1, antecedents.length);
		final Predicate pred = antecedents[0].getGoal();
		assertTrue(pred.toString().equals("ℕ=ℕ"));
	}

	@Test
	public void testSeveralSubstitutes() throws Exception {
		final IProverSequent seq = TestLib
				.genSeq("f∈{1,0}→ℕ ;; f∈{0,1}→ℕ |- dom(f)⊂ℕ");
		final Expression id_f = ((UnaryExpression) ((RelationalPredicate) seq
				.goal()).getLeft()).getChild();
		final List<Expression> list01 = new ArrayList<Expression>();
		list01.addAll(Arrays.asList(ZERO, ONE));
		final Expression setExt01 = TestLib.ff.makeSetExtension(list01, null);
		final List<Expression> list10 = new ArrayList<Expression>();
		list10.addAll(Arrays.asList(ONE, ZERO));
		final Expression setExt10 = TestLib.ff.makeSetExtension(list10, null);
		assertSubstitutions(seq, id_f, setExt01, setExt10);
	}

	@Test
	public void testInvalid() {
		// relation
		doInvalidTest("↔");

		// surjective relation
		doInvalidTest("");

		// total surjective relation
		doInvalidTest("");

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
}