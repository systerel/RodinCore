/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.ff;
import static org.eventb.core.seqprover.tests.TestLib.genExpr;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointProcessorInference;
import org.junit.Test;

/**
 * Unit tests for the automated application of one-point rule in quantified
 * predicates.
 * 
 * @author Laurent Voisin
 */
public class OnePointProcessorInferenceTest {

	private static void assertSuccess(String input, String result,
			String replacementStr) {
		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		final Predicate pred = genPred(typenv, input);
		final Expression replacement = genExpr(typenv, replacementStr);
		final Predicate expected = genPred(typenv, result);

		assertSuccess((QuantifiedPredicate) pred, expected, replacement);
	}

	private static void assertSuccess(String input, String result,
			Expression replacement) {
		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		final Predicate pred = genPred(typenv, input);
		final Predicate expected = genPred(typenv, result);

		assertSuccess((QuantifiedPredicate) pred, expected, replacement);
	}

	private static void assertSuccess(QuantifiedPredicate qPred,
			Predicate expected, Expression replacement) {
		final OnePointProcessorInference processor = new OnePointProcessorInference(
				qPred, ff);
		processor.matchAndInstantiate();
		assertTrue(processor.wasSuccessfullyApplied());
		assertEquals(replacement, processor.getReplacement());
		assertEquals(expected, processor.getProcessedResult());
	}

	private static void assertFailure(String input) {
		final Predicate pred = genPred(input);
		assertFailure((QuantifiedPredicate) pred);
	}

	private static void assertFailure(QuantifiedPredicate qPred) {
		final OnePointProcessorInference processor = new OnePointProcessorInference(
				qPred, ff);
		processor.matchAndInstantiate();
		assertFalse(processor.wasSuccessfullyApplied());
	}

	private static BoundIdentifier bi(int index) {
		return ff.makeBoundIdentifier(index, null, ff.makeIntegerType());
	}

	/**
	 * Ensures that the processor works appropriately in simplistic cases.
	 */
	@Test
	public void simple() {
		assertSuccess("∃x⦂S·  x=a", "⊤", "a");
		assertFailure("∃x⦂S·¬ x=a");
		assertFailure("∀x⦂S·  x=a");
		assertSuccess("∀x⦂S·¬ x=a", "⊥", "a");
	}

	/**
	 * Ensures that the processor works appropriately with top-level operators.
	 */
	@Test
	public void toplevel() {
		assertSuccess("∃x⦂S· x=a ∧  x∈B", "a∈B", "a");
		assertSuccess("∃x⦂S· x∈B ∧  x=a", "a∈B", "a");
		assertFailure("∃x⦂S·¬x=a ∧  x∈B");
		assertFailure("∃x⦂S· x∈B ∧ ¬x=a");
		assertFailure("∃x⦂S· x=a ∨  x∈B");
		assertFailure("∃x⦂S· x∈B ∨  x=a");
		assertFailure("∃x⦂S· x=a ⇒  x∈B");
		assertFailure("∃x⦂S· x∈B ⇒  x=a");
		assertFailure("∃x⦂S· x=a ⇔  x∈B");
		assertFailure("∃x⦂S· x∈B ⇔  x=a");

		assertSuccess("∀x⦂S·¬x=a ∨  x∈B", "a∈B", "a");
		assertSuccess("∀x⦂S· x∈B ∨ ¬x=a", "a∈B", "a");
		assertSuccess("∀x⦂S· x=a ⇒  x∈B", "a∈B", "a");
		assertSuccess("∀x⦂S· x∈B ⇒ ¬x=a", "¬a∈B", "a");
		assertFailure("∀x⦂S· x=a ∨  x∈B");
		assertFailure("∀x⦂S· x∈B ∨  x=a");
		assertFailure("∀x⦂S·¬x=a ⇒  x∈B");
		assertFailure("∀x⦂S· x∈B ⇒  x=a");
		assertFailure("∀x⦂S·¬x=a ∧  x∈B");
		assertFailure("∀x⦂S· x∈B ∧ ¬x=a");
		assertFailure("∀x⦂S·¬x=a ⇔  x∈B");
		assertFailure("∀x⦂S· x∈B ⇔ ¬x=a");
	}

	/**
	 * Ensures that the processor works appropriately with top-level operators.
	 */
	@Test
	public void not() {
		assertFailure("∃x⦂S·¬( x=a ∧  x∈B)");
		assertSuccess("∃x⦂S·¬(¬x=a ∨  x∈B)", "¬a∈B", "a");
		assertSuccess("∃x⦂S·¬( x=a ⇒  x∈B)", "¬a∈B", "a");
		assertSuccess("∃x⦂S·¬( x∈B ⇒ ¬x=a)", "a∈B", "a");

		assertFailure("∀x⦂S·¬( x=a ∨  x∈B)");
		assertFailure("∀x⦂S·¬(¬x=a ⇒  x∈B)");
		assertFailure("∀x⦂S·¬( x∈B ⇒  x=a)");
		assertSuccess("∀x⦂S·¬( x=a ∧  x∈B)", "¬a∈B", "a");
		assertSuccess("∀x⦂S·¬( x∈B ∧  x=a)", "¬a∈B", "a");
	}

	/**
	 * Ensures that the processor works appropriately with complex mixing of
	 * operators.
	 */
	@Test
	public void complex() {
		assertSuccess("∃x · x = 1 ∧ x ≥ 0", "1 ≥ 0", "1");
		assertSuccess("∃x · ¬(¬(x = 1) ∨ x ≥ 0)", "¬(1 ≥ 0)", "1");

		assertSuccess("∀x · x ≥ 0 ⇒ ¬(x = 1)", "¬(1 ≥ 0)", "1");
		assertSuccess("∀x · x ≥ 0 ⇒ ¬(x = 1 ∧ x ≤ 5)", "1 ≥ 0 ⇒ ¬(1 ≤ 5)", "1");

		assertSuccess("∀x,y,z,t·x↦y=z↦t∧x≥z⇒y≥t", "∀x,y,t·y=t∧x≥x⇒y≥t", bi(3));
		assertSuccess("∀x,y,t·y=t∧x≥x⇒y≥t", "∀x,y·x≥x⇒y≥y", bi(1));

		// replacements inside an implication
		assertSuccess("∀x,y· x+y≥1 ⇒ (x=1 ⇒ x≥0)", "∀y · 1+y ≥ 1 ⇒ 1≥0", "1");
		assertSuccess("∀x,y· x+y≥1 ⇒ (x≥0 ⇒ ¬ x=1)", "∀y· 1+y≥1 ⇒ ¬ 1≥0", "1");

		assertFailure("∀x · x ≥ 0 ⇒ x = 1 ∧ x ≤ 5");
		assertFailure("∀x · x ≥ 0 ⇒ ¬(x = 1) ∧ x ≤ 5");
	}

}
