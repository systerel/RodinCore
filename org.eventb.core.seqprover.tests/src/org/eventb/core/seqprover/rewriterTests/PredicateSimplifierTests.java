/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added tests for SIMP_FORALL and SIMP_EXISTS
 *     Systerel - extracted this class from AutoFormulaRewriterTests
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier;
import org.junit.Test;

/**
 * Unit tests for class {@link PredicateSimplifier}.
 * 
 * @author htson
 * @author Laurent Voisin
 */
public abstract class PredicateSimplifierTests extends
		AbstractFormulaRewriterTests {

	private final boolean withMultiImp;
	private final boolean withMultiImpNot;
	private final boolean withMultiEqvNot;
	private final boolean withMultiImpOrAnd;
	private final boolean withQuantDistr;
	private final boolean withExistsImp;
	private final boolean withMultiAndOr;

	// Each inner class allows to run tests with a different set of options.
	public static class MinimalTests extends PredicateSimplifierTests {
		public MinimalTests() {
			super(new PredicateSimplifier(DLib.mDLib(ff), 0, false,
					"PredicateSimplifier"));
		}
	}

	public static class FullTests extends PredicateSimplifierTests {
		public FullTests() {
			super(new PredicateSimplifier(DLib.mDLib(ff), 0xffffffff, false,
					"PredicateSimplifier"));
		}
	}

	public PredicateSimplifierTests(PredicateSimplifier rewriter) {
		super(rewriter);
		this.withMultiImp = rewriter.withMultiImp;
		this.withMultiImpNot = rewriter.withMultiImpNot;
		this.withMultiEqvNot = rewriter.withMultiEqvNot;
		this.withMultiImpOrAnd = rewriter.withMultiImpOrAnd;
		this.withQuantDistr = rewriter.withQuantDistr;
		this.withExistsImp = rewriter.withExistsImp;
		this.withMultiAndOr = rewriter.withMultiAndOr;
	}

	protected void rewriteCond(boolean condition, String inputImage,
			String expectedImage, String... env) {
		if (condition) {
			rewritePred(inputImage, expectedImage, env);
		} else {
			noRewritePred(inputImage, env);
		}
	}

	protected void rewriteMultiImp(String inputImage, String expectedImage,
			String... env) {
		rewriteCond(withMultiImp, inputImage, expectedImage, env);
	}

	protected void rewriteMultiEqvNot(String inputImage, String expectedImage,
			String... env) {
		rewriteCond(withMultiEqvNot, inputImage, expectedImage, env);
	}

	protected void rewriteMultiImpNot(String inputImage, String expectedImage,
			String... env) {
		rewriteCond(withMultiImpNot, inputImage, expectedImage, env);
	}

	protected void rewriteMultiImpOrAnd(String inputImage,
			String expectedImage, String... env) {
		rewriteCond(withMultiImpOrAnd, inputImage, expectedImage, env);
	}

	protected void rewriteQuantDistr(String inputImage, String expectedImage,
			String... env) {
		rewriteCond(withQuantDistr, inputImage, expectedImage, env);
	}

	protected void rewriteExistsImp(String inputImage, String expectedImage,
			String... env) {
		rewriteCond(withExistsImp, inputImage, expectedImage, env);
	}

	protected void rewriteMultiAndOr(String inputImage, String expectedImage,
			String... env) {
		rewriteCond(withMultiAndOr, inputImage, expectedImage, env);
	}

	/**
	 * Tests for rewriting conjunctions.
	 */
	@Test
	public void testConjunction() {
		// SIMP_SPECIAL_AND_BTRUE:
		// P & ... & true & ... & Q == P & ... & ... & Q
		rewritePred("x = 1 ∧ ⊤", "x = 1");
		rewritePred("⊤ ∧ x = 1", "x = 1");
		noRewritePred("x = 1 ∧ y = 2 ∧ z = 3");
		rewritePred("⊤ ∧ x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3");
		rewritePred("x = 1 ∧ ⊤ ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3");
		rewritePred("x = 1 ∧ y = 2 ∧ ⊤ ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3");
		rewritePred("x = 1 ∧ y = 2 ∧ z = 3 ∧ ⊤", "x = 1 ∧ y = 2 ∧ z = 3");
		rewritePred("⊤ ∧ x = 1 ∧ ⊤ ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3");
		rewritePred("⊤ ∧ x = 1 ∧ y = 2 ∧ z = 3 ∧ ⊤", "x = 1 ∧ y = 2 ∧ z = 3");
		rewritePred("x = 1 ∧ ⊤ ∧ y = 2 ∧ z = 3 ∧ ⊤", "x = 1 ∧ y = 2 ∧ z = 3");
		rewritePred("⊤ ∧ x = 1 ∧ ⊤ ∧ y = 2 ∧ z = 3 ∧ ⊤",
				"x = 1 ∧ y = 2 ∧ z = 3");

		// SIMP_SPECIAL_AND_BFALSE:
		// P & ... & false & ... & Q == false
		rewritePred("x = 1 ∧ ⊥", "⊥");
		rewritePred("⊥ ∧ x = 1", "⊥");
		rewritePred("⊥ ∧ x = 1 ∧ y = 2 ∧ z = 3", "⊥");
		rewritePred("x = 1 ∧ ⊥ ∧ y = 2 ∧ z = 3", "⊥");
		rewritePred("x = 1 ∧ y = 2 ∧ ⊥ ∧ z = 3", "⊥");
		rewritePred("x = 1 ∧ y = 2 ∧ z = 3 ∧ ⊥", "⊥");
		rewritePred("⊥ ∧ x = 1 ∧ ⊥ ∧ y = 2 ∧ z = 3", "⊥");
		rewritePred("⊥ ∧ x = 1 ∧ y = 2 ∧ z = 3 ∧ ⊥", "⊥");
		rewritePred("x = 1 ∧ ⊥ ∧ y = 2 ∧ z = 3 ∧ ⊥", "⊥");
		rewritePred("⊥ ∧ x = 1 ∧ ⊥ ∧ y = 2 ∧ z = 3 ∧ ⊥", "⊥");

		// SIMP_MULTI_AND:
		// P & ... & Q & ... & Q & ... & R == P & ... & Q & ... & ... & R
		rewriteMultiAndOr("x = 1 ∧ x = 1", "x = 1");
		rewriteMultiAndOr("x = 1 ∧ x = 1 ∧ y = 2 ∧ z = 3",
				"x = 1 ∧ y = 2 ∧ z = 3");
		rewriteMultiAndOr("x = 1 ∧ y = 2 ∧ x = 1 ∧ z = 3",
				"x = 1 ∧ y = 2 ∧ z = 3");
		rewriteMultiAndOr("x = 1 ∧ y = 2 ∧ z = 3 ∧ x = 1",
				"x = 1 ∧ y = 2 ∧ z = 3");
		rewriteMultiAndOr("x = 1 ∧ y = 2 ∧ z = 3 ∧ y = 2",
				"x = 1 ∧ y = 2 ∧ z = 3");
		rewriteMultiAndOr("x = 1 ∧ y = 2 ∧ z = 3 ∧ z = 3",
				"x = 1 ∧ y = 2 ∧ z = 3");
		rewriteMultiAndOr("x = 1 ∧ y = 2 ∧ z = 3 ∧ z = 3 ∧ y = 2",
				"x = 1 ∧ y = 2 ∧ z = 3");

		// SIMP_MULTI_AND_NOT:
		// P & ... & Q & ... & not(Q) & ... & R == false
		rewriteMultiAndOr("x = 1 ∧ ¬x = 1", "⊥");
		rewriteMultiAndOr("¬x = 1 ∧ x = 1 ∧ y = 2 ∧ z = 3", "⊥");
		rewriteMultiAndOr("x = 1 ∧ ¬x = 1 ∧ y = 2 ∧ z = 3", "⊥");
		rewriteMultiAndOr("x = 1 ∧ y = 2 ∧ z = 3 ∧ ¬x = 1", "⊥");
		rewriteMultiAndOr("x = 1 ∧ y = 2 ∧ z = 3 ∧ ¬x = 1", "⊥");
		rewriteMultiAndOr("x = 1 ∧ ¬y = 2 ∧ y = 2 ∧ z = 3 ∧ ¬x = 1", "⊥");
		rewriteMultiAndOr("x = 1 ∧ ¬y = 2 ∧ y = 2 ∧ ¬x = 1 ∧ z = 3 ∧ ¬x = 1",
				"⊥");
		rewriteMultiAndOr("y = 2 ∧ ¬x = 1 ∧ z = 3 ∧ ¬x = 1 ∧ x = 1 ∧ ¬y = 2",
				"⊥");
	}

	/**
	 * Tests for rewriting disjunctions.
	 */
	@Test
	public void testDisjunction() {
		// SIMP_SPECIAL_OR_BTRUE:
		// P or ... or true or ... or Q == true
		rewritePred("x = 1 ∨ ⊤", "⊤");
		rewritePred("⊤ ∨ x = 1", "⊤");
		noRewritePred("x = 1 ∨ y = 2 ∨ z = 3");
		rewritePred("⊤ ∨ x = 1 ∨ y = 2 ∨ z = 3", "⊤");
		rewritePred("x = 1 ∨ ⊤ ∨ y = 2 ∨ z = 3", "⊤");
		rewritePred("x = 1 ∨ y = 2 ∨ z = 3 ∨ ⊤", "⊤");
		rewritePred("⊤ ∨ x = 1 ∨ ⊤ ∨ y = 2 ∨ z = 3", "⊤");
		rewritePred("⊤ ∨ x = 1 ∨ y = 2 ∨ z = 3 ∨ ⊤", "⊤");
		rewritePred("x = 1 ∨ ⊤ ∨ y = 2 ∨ z = 3 ∨ ⊤", "⊤");
		rewritePred("⊤ ∨ x = 1 ∨ ⊤ ∨ y = 2 ∨ z = 3 ∨ ⊤", "⊤");

		// SIMP_SPECIAL_OR_BFALSE:
		// P or ... or false or ... or Q == P or ... or ... or Q
		rewritePred("x = 1 ∨ ⊥", "x = 1");
		rewritePred("⊥ ∨ x = 1", "x = 1");
		rewritePred("⊥ ∨ x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ y = 2 ∨ z = 3");
		noRewritePred("x = 1 ∨ y = 2 ∨ z = 3");
		rewritePred("x = 1 ∨ y = 2 ∨ z = 3 ∨ ⊥", "x = 1 ∨ y = 2 ∨ z = 3");
		rewritePred("⊥ ∨ x = 1 ∨ ⊥ ∨ y = 2 ∨ z = 3", "x = 1 ∨ y = 2 ∨ z = 3");
		rewritePred("⊥ ∨ x = 1 ∨ y = 2 ∨ z = 3 ∨ ⊥", "x = 1 ∨ y = 2 ∨ z = 3");
		rewritePred("x = 1 ∨ ⊥ ∨ y = 2 ∨ z = 3 ∨ ⊥", "x = 1 ∨ y = 2 ∨ z = 3");
		rewritePred("⊥ ∨ x = 1 ∨ ⊥ ∨ y = 2 ∨ z = 3 ∨ ⊥",
				"x = 1 ∨ y = 2 ∨ z = 3");

		// SIMP_MULTI_OR:
		// P or ... or Q or ... or Q or ... or R == P or ... or Q or ... or ...
		// or R
		rewriteMultiAndOr("x = 1 ∨ x = 1", "x = 1");
		rewriteMultiAndOr("x = 1 ∨ x = 1 ∨ y = 2 ∨ z = 3",
				"x = 1 ∨ y = 2 ∨ z = 3");
		rewriteMultiAndOr("x = 1 ∨ y = 2 ∨ x = 1 ∨ z = 3",
				"x = 1 ∨ y = 2 ∨ z = 3");
		rewriteMultiAndOr("x = 1 ∨ y = 2 ∨ z = 3 ∨ x = 1",
				"x = 1 ∨ y = 2 ∨ z = 3");
		rewriteMultiAndOr("x = 1 ∨ y = 2 ∨ z = 3 ∨ y = 2",
				"x = 1 ∨ y = 2 ∨ z = 3");
		rewriteMultiAndOr("x = 1 ∨ y = 2 ∨ z = 3 ∨ z = 3",
				"x = 1 ∨ y = 2 ∨ z = 3");
		rewriteMultiAndOr("x = 1 ∨ y = 2 ∨ x = 1 ∨ z = 3 ∨ z = 3",
				"x = 1 ∨ y = 2 ∨ z = 3");

		// SIMP_MULTI_OR_NOT:
		// P or ... or Q or ... or not(Q) or ... or R == true
		rewriteMultiAndOr("x = 1 ∨ ¬x = 1", "⊤");
		rewriteMultiAndOr("¬x = 1 ∨ x = 1 ∨ y = 2 ∨ z = 3", "⊤");
		rewriteMultiAndOr("x = 1 ∨ ¬x = 1 ∨ y = 2 ∨ z = 3", "⊤");
		rewriteMultiAndOr("x = 1 ∨ y = 2 ∨ ¬x = 1 ∨ z = 3", "⊤");
		rewriteMultiAndOr("x = 1 ∨ y = 2 ∨ z = 3 ∨ ¬x = 1", "⊤");
		rewriteMultiAndOr("x = 1 ∨ y = 2 ∨ z = 3 ∨ y = 2 ∨ ¬y = 2 ∨ ¬x = 1",
				"⊤");
	}

	/**
	 * Tests for rewriting implications.
	 */
	@Test
	public void testImplication() {
		// SIMP_SPECIAL_IMP_BTRUE_L:
		// true => P == P
		rewritePred("⊤ ⇒ x = 2", "x = 2");
		rewritePred("⊤ ⇒ ⊤", "⊤");
		rewritePred("⊤ ⇒ ⊥", "⊥");

		// SIMP_SPECIAL_IMP_BFALSE_L:
		// false => P == true
		rewritePred("⊥ ⇒ x = 2", "⊤");
		rewritePred("⊥ ⇒ ⊤", "⊤");
		rewritePred("⊥ ⇒ ⊥", "⊤");

		// SIMP_SPECIAL_IMP_BTRUE_R:
		// P => true == true
		rewritePred("x = 2 ⇒ ⊤", "⊤");

		// SIMP_SPECIAL_IMP_BFALSE_R:
		// P => false == not(P)
		rewritePred("x = 2 ⇒ ⊥", "¬x = 2");

		// SIMP_MULTI_IMP:
		// P => P == true
		rewriteMultiImp("x = 2 ⇒ x = 2", "⊤");
	}

	/**
	 * Tests for rewriting equivalences.
	 */
	@Test
	public void testEquivalence() {
		// SIMP_SPECIAL_EQV_BTRUE:
		// P <=> true == P
		rewritePred("x = 2 ⇔ ⊤", "x = 2");
		rewritePred("⊤ ⇔ ⊤", "⊤");
		rewritePred("⊥ ⇔ ⊤", "⊥");

		// SIMP_SPECIAL_EQV_BTRUE:
		// true <=> P == P
		rewritePred("⊤ ⇔ x = 2", "x = 2");
		rewritePred("⊤ ⇔ ⊥", "⊥");

		// SIMP_SPECIAL_EQV_BFALSE:
		// P <=> false == not(P)
		rewritePred("x = 2 ⇔ ⊥", "¬x = 2");
		rewritePred("⊥ ⇔ ⊥", "⊤");

		// SIMP_SPECIAL_EQV_BFALSE:
		// false <=> P == not(P)
		rewritePred("⊥ ⇔ x = 2", "¬x = 2");

		// SIMP_MULTI_EQV:
		// P <=> P == true
		rewritePred("x = 2 ⇔ x = 2", "⊤");
	}

	/**
	 * Tests for rewriting negations.
	 */
	@Test
	public void testNegation() {
		// org.junit.Assert.fail();

		// SIMP_SPECIAL_NOT_BTRUE:
		// not(true) == false
		rewritePred("¬⊤", "⊥");

		// SIMP_SPECIAL_NOT_BFALSE:
		// not(false) == true
		rewritePred("¬⊥", "⊤");

		// SIMP_NOT_NOT:
		// not(not(P)) == P
		rewritePred("¬¬x = 2", "x = 2");
		rewritePred("¬¬⊤", "⊤");
		rewritePred("¬¬⊥", "⊥");
	}

	/**
	 * Tests for rewriting quantifications.
	 */
	@Test
	public void testQuantification() {
		// SIMP_FORALL_AND:
		// !x.(P & Q) == (!x.P) & (!x.Q)
		rewriteQuantDistr("∀x·x > 0 ∧ x < 2", "(∀x·x > 0) ∧ (∀x·x < 2)");
		rewriteQuantDistr("∀x·x > 0 ∧ x < 2 ∧ x < 1",
				"(∀x·x > 0) ∧ (∀x·x < 2) ∧ (∀x·x < 1)");
		rewriteQuantDistr("∀x, y·(x > 0 ∨ y > 0) ∧ (y < 2 ∨ x < 2)",
				"(∀x, y·x > 0 ∨ y > 0) ∧ (∀x, y·y < 2 ∨ x < 2)");
		rewriteQuantDistr(
				"∀x, y·(x > 0 ∨ y > 0 ∨ z > 0) ∧ (y < 2 ∨ x < 2 ∨ z < 2)"
						+ " ∧ (y < 1 ∨ x < 1 ∨ z < 1)",
				"(∀x, y·x > 0 ∨ y > 0 ∨ z > 0) ∧ (∀x, y·y < 2 ∨ x < 2 ∨ z < 2)"
						+ " ∧ (∀x, y·y < 1 ∨ x < 1 ∨ z < 1)");

		// SIMP_EXISTS_OR:
		// #x.(P or Q) == (#x.P) or (#x.Q)
		rewriteQuantDistr("∃x·x > 0 ∨ x < 2", "(∃x·x > 0) ∨ (∃x·x < 2)");
		rewriteQuantDistr("∃x·x > 0 ∨ x < 2 ∨ x < 1",
				"(∃x·x > 0) ∨ (∃x·x < 2) ∨ (∃x·x < 1)");
		rewriteQuantDistr("∃x, y·(x > 0 ∧ y > 0) ∨ (y < 2 ∧ x < 2)",
				"(∃x, y·x > 0 ∧ y > 0) ∨ (∃x, y·y < 2 ∧ x < 2)");
		rewriteQuantDistr(
				"∃x, y·(x > 0 ∧ y > 0 ∧ z > 0) ∨ (y < 2 ∧ x < 2 ∧ z < 2)"
						+ " ∨ (y < 1 ∧ x < 1 ∧ z < 1)",
				"(∃x, y·x > 0 ∧ y > 0 ∧ z > 0) ∨ (∃x, y·y < 2 ∧ x < 2 ∧ z < 2)"
						+ " ∨ (∃x, y·y < 1 ∧ x < 1 ∧ z < 1)");

		// SIMP_EXISTS_IMP
		// ∃x·P ⇒ Q == (∃x·P) ⇒ (∃x·Q)
		rewriteExistsImp("∃x·x > 0 ⇒ x < 2", "(∀x·x > 0) ⇒ (∃x·x < 2)");
		noRewritePred("∀x·x > 0 ⇒ x < 2");

		// SIMP_FORALL: !x,y,z.P(y) == !y.P(y)
		rewritePred("∀x,y⦂ℤ·x>0", "∀x·x>0");
		rewritePred("∀y⦂ℤ,x·x>0", "∀x·x>0");
		rewritePred("∀x,y⦂ℤ,z⦂ℤ·x>0", "∀x·x>0");
		rewritePred("∀y⦂ℤ,x,z⦂ℤ·x>0", "∀x·x>0");
		rewritePred("∀y⦂ℤ,z⦂ℤ,x·x>0", "∀x·x>0");
		rewritePred("∀x,y⦂ℤ,t,z⦂ℤ·x>0 ∨ t>0", "∀x,t·x>0 ∨ t>0");

		// SIMP_EXISTS: #x,y,z.P(y) == #y.P(y)
		rewritePred("∃x,y⦂ℤ·x>0", "∃x·x>0");
		rewritePred("∃y⦂ℤ,x·x>0", "∃x·x>0");
		rewritePred("∃x,y⦂ℤ,z⦂ℤ·x>0", "∃x·x>0");
		rewritePred("∃y⦂ℤ,x,z⦂ℤ·x>0", "∃x·x>0");
		rewritePred("∃y⦂ℤ,z⦂ℤ,x·x>0", "∃x·x>0");
		rewritePred("∃x,y⦂ℤ,t,z⦂ℤ·x>0 ∧ t>0", "∃x,t·x>0 ∧ t>0");
	}

	/**
	 * Ensures that rule SIMP_MULTI_IMP_OR is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_IMP_OR() {
		rewriteMultiImpOrAnd("a=0 ∧ b=1 ⇒ a=0", "⊤");
		rewriteMultiImpOrAnd("a=0 ∧ b=1 ⇒ b=1", "⊤");

		rewriteMultiImpOrAnd("a=0 ∧ b=1 ∧ c=2 ⇒ a=0", "⊤");
		rewriteMultiImpOrAnd("a=0 ∧ b=1 ∧ c=2 ⇒ b=1", "⊤");
		rewriteMultiImpOrAnd("a=0 ∧ b=1 ∧ c=2 ⇒ c=2", "⊤");

		noRewritePred("a=0 ∧ b=1 ⇒ a=1");
	}

	/**
	 * Ensures that rule SIMP_MULTI_IMP_AND_NOT_R is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_IMP_AND_NOT_R() {
		rewriteMultiImpOrAnd("a=0 ∧ b=1 ⇒ ¬a=0", "¬(a=0 ∧ b=1)");
		rewriteMultiImpOrAnd("a=0 ∧ b=1 ⇒ ¬b=1", "¬(a=0 ∧ b=1)");

		rewriteMultiImpOrAnd("a=0 ∧ b=1 ∧ c=2⇒ ¬a=0", "¬(a=0 ∧ b=1 ∧ c=2)");
		rewriteMultiImpOrAnd("a=0 ∧ b=1 ∧ c=2⇒ ¬b=1", "¬(a=0 ∧ b=1 ∧ c=2)");
		rewriteMultiImpOrAnd("a=0 ∧ b=1 ∧ c=2⇒ ¬c=2", "¬(a=0 ∧ b=1 ∧ c=2)");

		noRewritePred("a=0 ∧ b=1 ⇒ ¬a=1");
	}

	/**
	 * Ensures that rule SIMP_MULTI_IMP_AND_NOT_L is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_IMP_AND_NOT_L() {
		rewriteMultiImpOrAnd("¬a=0 ∧  b=1 ⇒ a=0", "¬(¬a=0 ∧  b=1)");
		rewriteMultiImpOrAnd(" a=0 ∧ ¬b=1 ⇒ b=1", "¬( a=0 ∧ ¬b=1)");

		rewriteMultiImpOrAnd("¬a=0 ∧  b=1 ∧  c=2 ⇒ a=0",
				"¬(¬a=0 ∧  b=1 ∧  c=2)");
		rewriteMultiImpOrAnd(" a=0 ∧ ¬b=1 ∧  c=2 ⇒ b=1",
				"¬( a=0 ∧ ¬b=1 ∧  c=2)");
		rewriteMultiImpOrAnd(" a=0 ∧  b=1 ∧ ¬c=2 ⇒ c=2",
				"¬( a=0 ∧  b=1 ∧ ¬c=2)");

		noRewritePred("¬a=0 ∧ b=1 ⇒ a=1");
	}

	/**
	 * Ensures that rule SIMP_MULTI_EQV_NOT is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_EQV_NOT() {
		rewriteMultiEqvNot(" a=0 ⇔ ¬a=0", "⊥");
		rewriteMultiEqvNot("¬b=1 ⇔  b=1", "⊥");

		noRewritePred(" a=0 ⇔ ¬a=1");
		noRewritePred("¬a=0 ⇔  b=1");
	}

	/**
	 * Ensures that rule SIMP_MULTI_IMP_NOT_L is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_IMP_NOT_L() {
		rewriteMultiImpNot("¬a=1 ⇒ a=1", "a=1");
		noRewritePred("¬a=0 ⇒ a=1");
	}

	/**
	 * Ensures that rule SIMP_MULTI_IMP_NOT_R is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_IMP_NOT_R() {
		rewriteMultiImpNot("a=0 ⇒ ¬a=0", "¬a=0");
		noRewritePred("a=0 ⇒ ¬a=1");
	}

}
