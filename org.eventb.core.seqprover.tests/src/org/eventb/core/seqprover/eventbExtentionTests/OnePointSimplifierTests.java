/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.eventbExtensions.Lib.ff;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointSimplifier;
import org.junit.Test;

/**
 * Ensures that class {@link OnePointSimplifier} works appropriately.
 * 
 * @author "Thomas Muller"
 */
public class OnePointSimplifierTests {

	private static void assertFailure(String input, String eqPosStr) {
		final Predicate pred = genPred(input);
		final Predicate eq = eqPosStr == null ? null : //
				(Predicate) pred.getSubFormula(ff.makePosition(eqPosStr));
		final OnePointSimplifier ops = new OnePointSimplifier(pred, eq, ff);
		ops.matchAndApply();
		assertFalse(ops.wasSuccessfullyApplied());
	}

	private static void assertSuccess(String input, String eqPosStr,
			String replPosStr, String expected) {
		assertSuccess(input, "", eqPosStr, replPosStr, expected);
	}

	private static void assertSuccess(String input, String appPosStr,
			String eqPosStr, String replPosStr, String expected) {
		final Predicate pred = genPred(input);
		final IPosition appPos = ff.makePosition(appPosStr);
		final Predicate subPred = (Predicate) pred.getSubFormula(appPos);
		final IPosition eqPos = ff.makePosition(eqPosStr);
		final IPosition replPos = ff.makePosition(replPosStr);
		final Predicate eq = (Predicate) pred.getSubFormula(eqPos);
		final OnePointSimplifier ops = new OnePointSimplifier(subPred, eq, ff);
		ops.matchAndApply();
		assertTrue(ops.wasSuccessfullyApplied());
		assertEquals(pred.getSubFormula(replPos), ops.getReplacement());
		final Predicate subAct = ops.getProcessedPredicate();
		final Predicate actual = pred.rewriteSubFormula(appPos, subAct, ff);
		assertEquals(genPred(expected), actual);
	}

	/**
	 * Ensures that the one point simplifier fails on predicate <code>⊤</code>.
	 */
	@Test
	public void simpleFailure() throws Exception {
		assertFailure("⊤", null);
	}

	/**
	 * Ensures that the one point simplifier fails when given a non-equality
	 * predicate.
	 */
	@Test
	public void notEquality() throws Exception {
		assertFailure("∃x·x∈ℤ", "1");
	}

	/**
	 * Ensures that the one point simplifier fails when given an equality
	 * predicate where the replacement contains the bound identifier to replace.
	 */
	@Test
	public void loopSelf() throws Exception {
		assertFailure("∃x·x=x+1", "1");
	}

	/**
	 * Ensures that the one point simplifier fails when given an equality
	 * predicate that does not occur within a conjunction inside an existential
	 * quantification.
	 */
	@Test
	public void existsNotConj() throws Exception {
		assertFailure("∃x·x=1 ∨ x=2", "1.0");
		assertFailure("∃x·x=1 ∨ x=2", "1.1");
		assertFailure("∃x·x=1 ⇒ x=2", "1.0");
		assertFailure("∃x·x=1 ⇒ x=2", "1.1");
		assertFailure("∃x·x=1 ⇔ x=2", "1.1");
		assertFailure("∃x·x=1 ⇔ x=2", "1.1");
	}

	/**
	 * Ensures that the one point simplifier fails when given an equality
	 * predicate that does not occur on the left-hand side of an implication
	 * inside a universal quantification.
	 */
	@Test
	public void forallNotImpl() throws Exception {
		assertFailure("∀x·x=1 ∧ x=2", "1.0");
		assertFailure("∀x·x=1 ∧ x=2", "1.1");
		assertFailure("∀x·x=1 ∨ x=2", "1.0");
		assertFailure("∀x·x=1 ∨ x=2", "1.1");
		assertFailure("∀x·x=1 ⇒ x=2", "1.1");
		assertFailure("∀x·x=1 ⇔ x=2", "1.1");
		assertFailure("∀x·x=1 ⇔ x=2", "1.1");
	}

	/**
	 * Ensures that the one point simplifier fails when given an equality
	 * predicate where the replacement contains a bound identifier which is
	 * declared inward.
	 */
	@Test
	public void loopInward() throws Exception {
		assertFailure("∃x,y·x=y+1", "2");
	}

	/**
	 * Ensures that a bound identifier can be replaced by a simple expression
	 * when there is only one predicate.
	 */
	@Test
	public void justOneEquality() throws Exception {
		assertSuccess("∃x·x = 1", "1", "1.1", "⊤");
	}

	/**
	 * Ensures that a bound identifier can be replaced by a free identifier.
	 */
	@Test
	public void freeIdent() throws Exception {
		assertSuccess("∃x·x∈ℤ ∧ x=y", "1.1", "1.1.1", "y∈ℤ");
	}

	/**
	 * Ensures that a bound identifier can be replaced by another bound
	 * identifier declared outward.
	 */
	@Test
	public void threeBoundedIdents() throws Exception {
		assertSuccess("∃x,y,z·x∈ℤ ∧ y∈ℤ ∧ z=y", "3.2", "3.2.1", "∃x,y·x∈ℤ∧y∈ℤ");
	}

	/**
	 * Ensures that a bound identifier can be replaced by another bound
	 * identifier declared in another quantifier.
	 */
	@Test
	public void complicatedSuccess() throws Exception {
		assertSuccess("∃x·∃y·y∈ℤ ∧ y=x", "1", "1.1.1", "1.1.1.1", "∃x·x∈ℤ");
	}

	/**
	 * Ensures that a bound identifier can be replaced by another bound
	 * identifier declared in another quantifier. Variant with several bound
	 * identifier in the inner quantification.
	 */
	@Test
	public void quantifInnerPredSuccess() throws Exception {
		assertSuccess("∃x·∃y,t,z·y∈ℤ ∧ t∈ℤ ∧ z∈ℤ ∧ y=x", "1", "1.3.3",
				"1.3.3.1", "∃x·∃t,z·x∈ℤ ∧ t∈ℤ ∧ z∈ℤ");
	}

	/**
	 * Ensures that a bound identifier can be replaced by another bound
	 * identifier even in a complex situation.
	 */
	@Test
	public void quantifPredWithPrioritySuccess() throws Exception {
		assertSuccess("∃x,y,t,z·x∈ℤ ∧ y∈ℤ ∧ t∈ℤ ∧ z∈ℤ ∧ y=t ∧ t=z", "4.4",
				"4.4.0", "∃x,y,z·x∈ℤ ∧ y∈ℤ ∧ y∈ℤ ∧ z∈ℤ ∧ y=z");
	}

	/**
	 * Ensures that a bound identifier can be replaced by a simple expression
	 * when there are several predicates.
	 */
	@Test
	public void expressionReplacementSuccess() throws Exception {
		assertSuccess("∃x,y·x∈ℤ ∧ y∈ℤ ∧ x=2", "2.2", "2.2.1", "∃y·2∈ℤ ∧ y∈ℤ");
	}

}
