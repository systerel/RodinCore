/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - SIMP_IN_COMPSET, SIMP_SPECIAL_OVERL, SIMP_FUNIMAGE_LAMBDA
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         This is the class for testing automatic rewriter {@link AutoRewriterImpl}
 *         using the abstract formula rewriter tests
 *         {@link AbstractFormulaRewriterTests}.
 */
public class AutoFormulaRewriterTests extends AbstractFormulaRewriterTests {
	
	// The automatic rewriter for testing.
	private static final IFormulaRewriter rewriter = new AutoRewriterImpl();
	
	/**
	 * Constructor.
	 * <p>
	 * Create an abstract formula rewriter test with the input is the automatic
	 * rewriter.
	 */
	public AutoFormulaRewriterTests() {
		super(rewriter);
	}

	/**
	 * Tests for rewriting conjunctions.
	 */
	@Test
	public void testConjunction() {
		// P & ... & true & ... & Q == P & ... & ... & Q
		predicateTest("x = 1", "x = 1 ∧ ⊤");
		predicateTest("x = 1", "⊤ ∧ x = 1");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "⊤ ∧ x = 1 ∧ y = 2 ∧ z = 3");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ ⊤ ∧ y = 2 ∧ z = 3");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ ⊤ ∧ z = 3");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3 ∧ ⊤");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "⊤ ∧ x = 1 ∧ ⊤ ∧ y = 2 ∧ z = 3");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "⊤ ∧ x = 1 ∧ y = 2 ∧ z = 3 ∧ ⊤");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ ⊤ ∧ y = 2 ∧ z = 3 ∧ ⊤");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3",
				"⊤ ∧ x = 1 ∧ ⊤ ∧ y = 2 ∧ z = 3 ∧ ⊤");


		// P & ... & false & ... & Q == false
		predicateTest("⊥", "x = 1 ∧ ⊥");
		predicateTest("⊥", "⊥ ∧ x = 1");
		predicateTest("⊥", "⊥ ∧ x = 1 ∧ y = 2 ∧ z = 3");
		predicateTest("⊥", "x = 1 ∧ ⊥ ∧ y = 2 ∧ z = 3");
		predicateTest("⊥", "x = 1 ∧ y = 2 ∧ ⊥ ∧ z = 3");
		predicateTest("⊥", "x = 1 ∧ y = 2 ∧ z = 3 ∧ ⊥");
		predicateTest("⊥", "⊥ ∧ x = 1 ∧ ⊥ ∧ y = 2 ∧ z = 3");
		predicateTest("⊥", "⊥ ∧ x = 1 ∧ y = 2 ∧ z = 3 ∧ ⊥");
		predicateTest("⊥", "x = 1 ∧ ⊥ ∧ y = 2 ∧ z = 3 ∧ ⊥");
		predicateTest("⊥", "⊥ ∧ x = 1 ∧ ⊥ ∧ y = 2 ∧ z = 3 ∧ ⊥");


		// P & ... & Q & ... & Q & ... & R == P & ... & Q & ... & ... & R
		predicateTest("x = 1", "x = 1 ∧ x = 1");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ x = 1 ∧ y = 2 ∧ z = 3");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ x = 1 ∧ z = 3");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3 ∧ x = 1");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3 ∧ y = 2");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3", "x = 1 ∧ y = 2 ∧ z = 3 ∧ z = 3");
		predicateTest("x = 1 ∧ y = 2 ∧ z = 3",
				"x = 1 ∧ y = 2 ∧ z = 3 ∧ z = 3 ∧ y = 2");
				

		// P & ... & Q & ... & not(Q) & ... & R == false
		predicateTest("⊥", "x = 1 ∧ ¬x = 1");
		predicateTest("⊥", "¬x = 1 ∧ x = 1 ∧ y = 2 ∧ z = 3");
		predicateTest("⊥", "x = 1 ∧ ¬x = 1 ∧ y = 2 ∧ z = 3");
		predicateTest("⊥", "x = 1 ∧ y = 2 ∧ z = 3 ∧ ¬x = 1");
		predicateTest("⊥", "x = 1 ∧ y = 2 ∧ z = 3 ∧ ¬x = 1");
		predicateTest("⊥", "x = 1 ∧ ¬y = 2 ∧ y = 2 ∧ z = 3 ∧ ¬x = 1");
		predicateTest("⊥", "x = 1 ∧ ¬y = 2 ∧ y = 2 ∧ ¬x = 1 ∧ z = 3 ∧ ¬x = 1");
		predicateTest("⊥", "y = 2 ∧ ¬x = 1 ∧ z = 3 ∧ ¬x = 1 ∧ x = 1 ∧ ¬y = 2");
	}

	/**
	 * Tests for rewriting disjunctions.
	 */
	@Test
	public void testDisjunction() {
		// P or ... or true or ... or Q == true
		predicateTest("⊤", "x = 1 ∨ ⊤");
		predicateTest("⊤", "⊤ ∨ x = 1");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ y = 2 ∨ z = 3");
		predicateTest("⊤", "⊤ ∨ x = 1 ∨ y = 2 ∨ z = 3");
		predicateTest("⊤", "x = 1 ∨ ⊤ ∨ y = 2 ∨ z = 3");
		predicateTest("⊤", "x = 1 ∨ y = 2 ∨ z = 3 ∨ ⊤");
		predicateTest("⊤", "⊤ ∨ x = 1 ∨ ⊤ ∨ y = 2 ∨ z = 3");
		predicateTest("⊤", "⊤ ∨ x = 1 ∨ y = 2 ∨ z = 3 ∨ ⊤");
		predicateTest("⊤", "x = 1 ∨ ⊤ ∨ y = 2 ∨ z = 3 ∨ ⊤");
		predicateTest("⊤", "⊤ ∨ x = 1 ∨ ⊤ ∨ y = 2 ∨ z = 3 ∨ ⊤");
		
		
		// P or ... or false or ... or Q == P or ... or ... or Q
		predicateTest("x = 1", "x = 1 ∨ ⊥");
		predicateTest("x = 1", "⊥ ∨ x = 1");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "⊥ ∨ x = 1 ∨ y = 2 ∨ z = 3");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ ⊥ ∨ y = 2 ∨ z = 3");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ y = 2 ∨ z = 3 ∨ ⊥");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "⊥ ∨ x = 1 ∨ ⊥ ∨ y = 2 ∨ z = 3");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "⊥ ∨ x = 1 ∨ y = 2 ∨ z = 3 ∨ ⊥");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ ⊥ ∨ y = 2 ∨ z = 3 ∨ ⊥");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3",
				"⊥ ∨ x = 1 ∨ ⊥ ∨ y = 2 ∨ z = 3 ∨ ⊥");

		
		// P or ... or Q or ... or Q or ... or R == P or ... or Q or ... or ... or R
		predicateTest("x = 1", "x = 1 ∨ x = 1");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ x = 1 ∨ y = 2 ∨ z = 3");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ y = 2 ∨ x = 1 ∨ z = 3");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ y = 2 ∨ z = 3 ∨ x = 1");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ y = 2 ∨ z = 3 ∨ y = 2");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3", "x = 1 ∨ y = 2 ∨ z = 3 ∨ z = 3");
		predicateTest("x = 1 ∨ y = 2 ∨ z = 3",
				"x = 1 ∨ y = 2 ∨ x = 1 ∨ z = 3 ∨ z = 3");
		
		
		// P or ... or Q or ... or not(Q) or ... or R == true
		predicateTest("⊤", "x = 1 ∨ ¬x = 1");
		predicateTest("⊤", "¬x = 1 ∨ x = 1 ∨ y = 2 ∨ z = 3");
		predicateTest("⊤", "x = 1 ∨ ¬x = 1 ∨ y = 2 ∨ z = 3");
		predicateTest("⊤", "x = 1 ∨ y = 2 ∨ ¬x = 1 ∨ z = 3");
		predicateTest("⊤", "x = 1 ∨ y = 2 ∨ z = 3 ∨ ¬x = 1");
		predicateTest("⊤", "x = 1 ∨ y = 2 ∨ z = 3 ∨ y = 2 ∨ ¬y = 2 ∨ ¬x = 1");
	}

	/**
	 * Tests for rewriting implications.
	 */
	@Test
	public void testImplication() {
		// true => P == P
		predicateTest("x = 2", "⊤ ⇒ x = 2");
		predicateTest("⊤", "⊤ ⇒ ⊤");
		predicateTest("⊥", "⊤ ⇒ ⊥");
		
		
		// false => P == true
		predicateTest("⊤", "⊥ ⇒ x = 2");
		predicateTest("⊤", "⊥ ⇒ ⊤");
		predicateTest("⊤", "⊥ ⇒ ⊥");

		
		// P => true == true
		predicateTest("⊤", "x = 2 ⇒ ⊤");


		// P => false == not(P)
		predicateTest("¬x = 2", "x = 2 ⇒ ⊥");


		// P => P == true
		predicateTest("⊤", "x = 2 ⇒ x = 2");
		
	}

	/**
	 * Tests for rewriting equivalents.
	 */
	@Test
	public void testEquivalent() {
		// P <=> true == P
		predicateTest("x = 2", "x = 2 ⇔ ⊤");
		predicateTest("⊤", "⊤ ⇔ ⊤");
		predicateTest("⊥", "⊥ ⇔ ⊤");

		
		// true <=> P == P
		predicateTest("x = 2", "⊤ ⇔ x = 2");
		predicateTest("⊥", "⊤ ⇔ ⊥");

		
		// P <=> false == not(P)
		predicateTest("¬x = 2", "x = 2 ⇔ ⊥");
		predicateTest("⊤", "⊥ ⇔ ⊥");

		// false <=> P == not(P)
		predicateTest("¬x = 2", "⊥ ⇔ x = 2");

		// P <=> P == true
		predicateTest("⊤", "x = 2 ⇔ x = 2");

	}

	/**
	 * Tests for rewriting negations. 
	 */
	@Test
	public void testNegation() {
		// not(true)  ==  false
		predicateTest("⊥", "¬⊤");


		// not(false)  ==  true
		predicateTest("⊤", "¬⊥");
		

		// not(not(P))  ==  not(P)
		predicateTest("x = 2", "¬¬x = 2");
		predicateTest("⊤", "¬¬⊤");
		predicateTest("⊥", "¬¬⊥");
		

		// not(x /: S))  ==  x : S
		predicateTest("2 ∈ S", "¬2 ∉ S");
		predicateTest("x ∈ {x ∣ x > 0}", "¬x ∉ {x ∣ x > 0}");

		
		// E /= F  ==  not (E = F)
		predicateTest("¬x + 2 = y", "x + 2 ≠ y");
		

		// E /: F  ==  not (E : F)
		predicateTest("¬2 ∈ S", "2 ∉ S");
		predicateTest("¬x ∈ {x ∣ x > 0}", "x ∉ {x ∣ x > 0}");

		
		// E /<<: F  ==  not (E <<: F)
		predicateTest("¬ S ⊂ {x ∣ x > 0}", "S ⊄ {x ∣ x > 0}");
		predicateTest("¬ {x ∣ x > 0} ⊂ S", "{x ∣ x > 0} ⊄ S");
		
		
		// E /<: F  ==  not (E <: F)
		predicateTest("¬ S ⊆ {x ∣ x > 0}", "S ⊈ {x ∣ x > 0}");
		predicateTest("¬ {x ∣ x > 0} ⊆ S", "{x ∣ x > 0} ⊈ S");

		
		// not(a <= b) == a > b
		predicateTest("x + 2 > y ∗ 2", "¬ x + 2 ≤ y ∗ 2");

		
		// not(a >= b) == a < b
		predicateTest("x + 2 < y ∗ 2", "¬ x + 2 ≥ y ∗ 2");

		
    	// not(a > b) == a <= b
		predicateTest("x + 2 ≤ y ∗ 2", "¬ x + 2 > y ∗ 2");

		
	   	// not(a < b) == a >= b
		predicateTest("x + 2 ≥ y ∗ 2", "¬ x + 2 < y ∗ 2");


	   	// not(E = FALSE) == E = TRUE
		predicateTest("E = TRUE", "¬ E = FALSE");
		

	   	// not(E = TRUE) == E = FALSE
		predicateTest("E = FALSE", "¬ E = TRUE");
		

	   	// not(FALSE = E) == TRUE = E
		predicateTest("TRUE = E", "¬ FALSE = E");

	   	// not(TRUE = E) == FALSE = E
		predicateTest("FALSE = E", "¬ TRUE = E");
		
	}

	/**
	 * Tests for rewriting quantifications.
	 */
	@Test
	public void testQuantification() {
		// !x.(P & Q) == (!x.P) & (!x.Q)
		predicateTest("(∀x·x > 0) ∧ (∀x·x < 2)", "∀x·x > 0 ∧ x < 2");
		predicateTest("(∀x·x > 0) ∧ (∀x·x < 2) ∧ (∀x·x < 1)", "∀x·x > 0 ∧ x < 2 ∧ x < 1");
		predicateTest("(∀x, y·x > 0 ∨ y > 0) ∧ (∀x, y·y < 2 ∨ x < 2)",
				"∀x, y·(x > 0 ∨ y > 0) ∧ (y < 2 ∨ x < 2)");
		predicateTest(
				"(∀x, y·x > 0 ∨ y > 0 ∨ z > 0) ∧ (∀x, y·y < 2 ∨ x < 2 ∨ z < 2) ∧ (∀x, y·y < 1 ∨ x < 1 ∨ z < 1)",
				"∀x, y·(x > 0 ∨ y > 0 ∨ z > 0) ∧ (y < 2 ∨ x < 2 ∨ z < 2) ∧ (y < 1 ∨ x < 1 ∨ z < 1)");

		// #x.(P or Q) == (#x.P) or (#x.Q)
		predicateTest("(∃x·x > 0) ∨ (∃x·x < 2)", "∃x·x > 0 ∨ x < 2");
		predicateTest("(∃x·x > 0) ∨ (∃x·x < 2) ∨ (∃x·x < 1)", "∃x·x > 0 ∨ x < 2 ∨ x < 1");
		predicateTest("(∃x, y·x > 0 ∧ y > 0) ∨ (∃x, y·y < 2 ∧ x < 2)",
				"∃x, y·(x > 0 ∧ y > 0) ∨ (y < 2 ∧ x < 2)");
		predicateTest(
				"(∃x, y·x > 0 ∧ y > 0 ∧ z > 0) ∨ (∃x, y·y < 2 ∧ x < 2 ∧ z < 2) ∨ (∃x, y·y < 1 ∧ x < 1 ∧ z < 1)",
				"∃x, y·(x > 0 ∧ y > 0 ∧ z > 0) ∨ (y < 2 ∧ x < 2 ∧ z < 2) ∨ (y < 1 ∧ x < 1 ∧ z < 1)");

	}

	/**
	 * Tests for rewriting equalities.
	 */
	@Test
	public void testEquality() {
		// E = E == true
		predicateTest("⊤", "x + 2 ∗ y = x + 2 ∗ y");


		// E /= E == false
		predicateTest("⊥", "x + 2 ∗ y ≠ x + 2 ∗ y");

		
		// E |-> F = G |-> H == E = G & F = H
		predicateTest("x + 2 ∗ y = 2 ∧ 3 = y + 2 ∗ x", "x + 2 ∗ y ↦ 3 = 2 ↦ y + 2 ∗ x");
		
		// TRUE = FALSE  ==  false
		predicateTest("⊥", "TRUE = FALSE");


		// FALSE = TRUE  ==  false
		predicateTest("⊥", "FALSE = TRUE");

	}
	
	/**
	 * Tests for rewriting set theoretical statements.
	 */
	@Test
	public void testSetTheory() {
		IntegerType intType = ff.makeIntegerType();
		Expression emptySetInteger = ff.makeEmptySet(ff
				.makePowerSetType(intType), null);
		ITypeCheckResult typeCheck = emptySetInteger.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Expected expression: \n\t"
				+ emptySetInteger + "\n\tcannot be type checked");
		
		// S /\ ... /\ {} /\ ... /\ T == {}
		Expression inputExp = makeInputExpression("{x ∣ x > 0} ∩ ∅");
		expressionTest(emptySetInteger, inputExp);
		
		inputExp = makeInputExpression("∅ ∩ {x ∣ x > 0}");
		expressionTest(emptySetInteger, inputExp);
		
		inputExp = makeInputExpression("∅ ∩ {x ∣ x > 0} ∩ S ∩ T");
		expressionTest(emptySetInteger, inputExp);

		inputExp = makeInputExpression("{x ∣ x > 0} ∩ S ∩ ∅ ∩ T");
		expressionTest(emptySetInteger, inputExp);
		
		inputExp = makeInputExpression("{x ∣ x > 0} ∩ S ∩ T ∩ ∅");
		expressionTest(emptySetInteger, inputExp);

		inputExp = makeInputExpression("∅ ∩ {x ∣ x > 0} ∩ ∅ ∩ S ∩ T");
		expressionTest(emptySetInteger, inputExp);
				
		inputExp = makeInputExpression("∅ ∩ {x ∣ x > 0} ∩ S ∩ T ∩ ∅");
		expressionTest(emptySetInteger, inputExp);
		
		inputExp = makeInputExpression("{x ∣ x > 0} ∩ ∅ ∩ S ∩ T ∩ ∅");
		expressionTest(emptySetInteger, inputExp);
		
		inputExp = makeInputExpression("∅ ∩ {x ∣ x > 0} ∩ ∅ ∩ S ∩ T ∩ ∅");
		expressionTest(emptySetInteger, inputExp);
		
		// Test with empty and type
		expressionTest("(∅ ⦂ ℙ(S))", "(∅ ⦂ ℙ(S)) ∩ ∅");
		expressionTest("(∅ ⦂ ℙ(S))", "(∅ ⦂ ℙ(S)) ∩ S");
		expressionTest("(∅ ⦂ ℙ(S))", "S ∩ (∅ ⦂ ℙ(S))");


		// S /\ ... /\ T /\ ... /\ T /\ ... /\ U == S /\ ... /\ T /\ ... /\ ... /\ U
		expressionTest("{x ∣ x > 0}", "{x ∣ x > 0} ∩ {x ∣ x > 0}");
		expressionTest("S ∩ T ∩ {x ∣ x > 0}", "S ∩ S ∩ T ∩ {x ∣ x > 0}");
		expressionTest("S ∩ T ∩ {x ∣ x > 0}", "S ∩ T ∩ S ∩ {x ∣ x > 0}");
		expressionTest("S ∩ T ∩ {x ∣ x > 0}", "S ∩ T ∩ {x ∣ x > 0} ∩ S");
		expressionTest("S ∩ T ∩ {x ∣ x > 0}", "S ∩ T ∩ S ∩ {x ∣ x > 0} ∩ S");
		expressionTest("S ∩ T ∩ {x ∣ x > 0}", "S ∩ T ∩ S ∩ T ∩ {x ∣ x > 0} ∩ S ∩ T");

		expressionTest("S", "S ∩ S", "S", "ℙ(S)");
		expressionTest("S", "S ∩ S ∩ S", "S", "ℙ(S)");
		expressionTest("t", "t ∩ t", "t", "ℙ(S)");
		expressionTest("t", "t ∩ t ∩ t", "t", "ℙ(S)");


		// S \/ ... \/ {} \/ ... \/ T == S ... \/ ... \/ T
		expressionTest("{x ∣ x > 0}", "{x ∣ x > 0} ∪ ∅");
		expressionTest("{x ∣ x > 0}", "∅ ∪ {x ∣ x > 0}");
		expressionTest("{x ∣ x > 0} ∪ S ∪ T", "∅ ∪ {x ∣ x > 0} ∪ S ∪ T");
		expressionTest("{x ∣ x > 0} ∪ S ∪ T", "{x ∣ x > 0} ∪ S ∪ ∅ ∪ T");
		expressionTest("{x ∣ x > 0} ∪ S ∪ T", "{x ∣ x > 0} ∪ S ∪ T ∪ ∅");
		expressionTest("{x ∣ x > 0} ∪ S ∪ T", "∅ ∪ {x ∣ x > 0} ∪ ∅ ∪ S ∪ T");
		expressionTest("{x ∣ x > 0} ∪ S ∪ T", "∅ ∪ {x ∣ x > 0} ∪ S ∪ T ∪ ∅");
		expressionTest("{x ∣ x > 0} ∪ S ∪ T", "{x ∣ x > 0} ∪ ∅ ∪ S ∪ T ∪ ∅");
		expressionTest("{x ∣ x > 0} ∪ S ∪ T", "∅ ∪ {x ∣ x > 0} ∪ ∅ ∪ S ∪ T ∪ ∅");

		expressionTest("S", "S ∪ S", "S", "ℙ(S)");
		expressionTest("S", "S ∪ S ∪ S", "S", "ℙ(S)");
		expressionTest("t", "t ∪ t", "t", "ℙ(S)");
		expressionTest("t", "t ∪ t ∪ t", "t", "ℙ(S)");

		
		// S \/ ... \/ T \/ ... \/ T \/ ... \/ U == S \/ ... \/ T \/ ... \/ ... \/ U
		expressionTest("{x ∣ x > 0}", "{x ∣ x > 0} ∪ {x ∣ x > 0}");
		expressionTest("S ∪ T ∪ {x ∣ x > 0}", "S ∪ S ∪ T ∪ {x ∣ x > 0}");
		expressionTest("S ∪ T ∪ {x ∣ x > 0}", "S ∪ T ∪ S ∪ {x ∣ x > 0}");
		expressionTest("S ∪ T ∪ {x ∣ x > 0}", "S ∪ T ∪ {x ∣ x > 0} ∪ S");
		expressionTest("S ∪ T ∪ {x ∣ x > 0}", "S ∪ T ∪ S ∪ {x ∣ x > 0} ∪ S");
		expressionTest("S ∪ T ∪ {x ∣ x > 0}", "S ∪ T ∪ S ∪ T ∪ {x ∣ x > 0} ∪ S ∪ T");


		// {} <: S == true
		predicateTest("⊤", "∅ ⊆ {x ∣ x > 0}");
		

		// S <: S == true
		predicateTest("⊤", "{x ∣ x > 0} ⊆ {x ∣ x > 0}");
		

		// S <: A \/ ... \/ S \/ ... \/ B == true
		predicateTest("⊤", "S ⊆ S ∪ T ∪ {x ∣ x > 0}");
		predicateTest("⊤", "S ⊆ T ∪ S ∪ {x ∣ x > 0}");
		predicateTest("⊤", "S ⊆ T ∪ {x ∣ x > 0} ∪ S");

		
		// A /\ ... /\ S /\ ... /\ B <: S == true
		predicateTest("⊤", "S ∩ T ∩ {x ∣ x > 0} ⊆ S");
		predicateTest("⊤", "T ∩ S ∩ {x ∣ x > 0} ⊆ S");
		predicateTest("⊤", "T ∩ {x ∣ x > 0} ∩ S ⊆ S");
		

		// A \/ ... \/ B <: S == A <: S & ... & B <: S
		predicateTest("A ⊆ {x ∣ x > 0} ∧ B ⊆ {x ∣ x > 0}", "A ∪ B ⊆ {x ∣ x > 0}");
		predicateTest("A ⊆ {x ∣ x > 0} ∧ B ⊆ {x ∣ x > 0} ∧ C ⊆ {x ∣ x > 0}",
				"A ∪ B ∪ C ⊆ {x ∣ x > 0}");

		
		// S <: A /\ ... /\ B == S <: A & ... & S <: B
		predicateTest("{x ∣ x > 0} ⊆ A ∧ {x ∣ x > 0} ⊆ B", "{x ∣ x > 0} ⊆ A ∩ B");
		predicateTest("{x ∣ x > 0} ⊆ A ∧ {x ∣ x > 0} ⊆ B ∧ {x ∣ x > 0} ⊆ C",
				"{x ∣ x > 0} ⊆ A ∩ B ∩ C");
		
		
		// A \/ ... \/ B <<: S == A <<: S & ... & B <<: S
		// This rule is wrong and has been removed, no rewriting should occur.
		predicateTest("A ∪ B ⊂ {x ∣ x > 0}", "A ∪ B ⊂ {x ∣ x > 0}");
		predicateTest("A ∪ B ∪ C ⊂ {x ∣ x > 0}",	"A ∪ B ∪ C ⊂ {x ∣ x > 0}");

		
		// S <<: A /\ ... /\ B == S <<: A & ... & S <<: B
		// This rule is wrong and has been removed, no rewriting should occur.
		predicateTest("{x ∣ x > 0} ⊂ A ∩ B", "{x ∣ x > 0} ⊂ A ∩ B");
		predicateTest("{x ∣ x > 0} ⊂ A ∩ B ∩ C",	"{x ∣ x > 0} ⊂ A ∩ B ∩ C");

		
		// E : {} == false
		predicateTest("⊥", "2 ∈ ∅");
		predicateTest("⊥", "FALSE ∈ ∅");
		predicateTest("⊥", "x + 2 ∈ ∅");

		
		// A : {A} == true
		predicateTest("⊤", "2 ∈ {2}");
		predicateTest("⊤", "x + 2 ∈ {x + 2}");
		predicateTest("⊤", "FALSE ∈ {FALSE}");

		
		// B : {A, ..., B, ..., C} == true
		predicateTest("⊤", "B ∈ {B, x + 2, C}");
		predicateTest("⊤", "B ∈ {x + 2, B, C}");
		predicateTest("⊤", "B ∈ {x + 2, C, B}");
		predicateTest("⊤", "B ∈ {B, x + 2, B, C}");
		predicateTest("⊤", "B ∈ {B, x + 2, C, B}");
		predicateTest("⊤", "B ∈ {x + 2, B, C, B}");
		predicateTest("⊤", "B ∈ {B, x + 2, B, C, B}");


		// {A, ..., B, ..., B, ..., C} == {A, ..., B, ..., C}
		expressionTest("{x + 2 ∗ y}", "{x + 2 ∗ y, x + 2 ∗ y}");
		expressionTest("{x + 2 ∗ y, E, F}", "{x + 2 ∗ y, x + 2 ∗ y, E, F}");
		expressionTest("{x + 2 ∗ y, E, F}", "{x + 2 ∗ y, E, x + 2 ∗ y, F}");
		expressionTest("{x + 2 ∗ y, E, F}", "{x + 2 ∗ y, E, F, x + 2 ∗ y}");
		expressionTest("{E, x + 2 ∗ y, F}", "{E, x + 2 ∗ y, F, x + 2 ∗ y}");
		expressionTest("{E, F, x + 2 ∗ y}", "{E, F, x + 2 ∗ y, x + 2 ∗ y}");
		expressionTest("{E, x + 2 ∗ y, F}", "{E, x + 2 ∗ y, E, F, x + 2 ∗ y, F}");

		
		// E : {x | P(x)} == P(E)
		predicateTest("x > 0 ∧ x < 2", "x ∈ {y ∣ y > 0 ∧ y < 2}");

		// E : {x . P(x) | x} == P(E)
		predicateTest("n ≥ 0", "n ∈ {x·x≥0∣x}");
		predicateTest("∀n·n≥1 ⇒ n ≥ 0", "∀n·n≥1 ⇒ n ∈ {x·x≥0∣x}");
		
		// F : {x,y . P(x,y) | E(x,y) == #x,y . P(x,y) & E(x,y) = F
		predicateTest("∃x,y· (x≥ 0 ∧ y≥ 0) ∧ x+y = n", "n ∈ {x,y·x≥0∧y≥0∣x+y}");
		predicateTest("∀n·n≥0 ⇒ (∃x,y· (x≥ 0 ∧ y≥ 0) ∧ x+y = n)", "∀n·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥0∣x+y}");
		// One Point Rule applies
		predicateTest("∀n·n≥0 ⇒ (∃y· (n ≥ 0 ∧ y≥ 0))", "∀n·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥0∣x}");
		predicateTest("∀n,m·n≥0 ⇒ (∃y· (n ≥ 0 ∧ y≥ m))", "∀n,m·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥m∣x}");
		// One Point Rule applies replacement on expression ('x=n' here)
		predicateTest("n=0", "n ∈ {x·x=0∣x}");
		// One Point Rule does not apply replacement on guard ('x=0' here)
		predicateTest("∃x· x=0 ∧ x+1 = n", "n ∈ {x·x=0∣x+1}");
		// Jean-Raymond Abrial's bug
		predicateTest("∃z·(∃x,y·(x>0∧y>0)∧g(x+y)−g(x)−g(y)=l)∧l=z", "∃z·(l∈ {x,y·x>0 ∧ y>0 ∣ g(x+y)−g(x)−g(y)})∧l=z");
		
		// S \ S == {}
		inputExp = makeInputExpression("{y ∣ y > 0} ∖ {y ∣ y > 0}");
		expressionTest(emptySetInteger, inputExp);
		

		// {} \ S == {}
		inputExp = makeInputExpression("∅ ∖ {y ∣ y > 0}");
		expressionTest(emptySetInteger, inputExp);


		// S \ {} == S
		expressionTest("{y ∣ y > 0}", "{y ∣ y > 0} ∖ ∅");

		
		// r~~ == r
		expressionTest("{x ↦ y ∣ x > 0 ∧ y < 2}", "{x ↦ y ∣ x > 0 ∧ y < 2}∼∼");
		

		// dom({x |-> a, ..., y |-> b}) == {x, ..., y}
		expressionTest("{x + 2}", "dom({x + 2 ↦ 3})");
		expressionTest("{x + 2, 2}", "dom({x + 2 ↦ 3, 2 ↦ y})");
		expressionTest("{x + 2, 2, a}", "dom({x + 2 ↦ 3, 2 ↦ y, a ↦ b})");

		
		// ran({x |-> a, ..., y |-> b}) == {a, ..., b}
		expressionTest("{3}", "ran({x + 2 ↦ 3})");
		expressionTest("{3, y}", "ran({x + 2 ↦ 3, 2 ↦ y})");
		expressionTest("{3, y, b}", "ran({x + 2 ↦ 3, 2 ↦ y, a ↦ b})");

		
		// (f <+ {E |-> F})(E) = F
		expressionTest("3", "(f  {x + 2 ↦ 3})(x + 2)");

 
		// E : {F} == E = F (if F is a single expression)
		predicateTest("x + 2 ∗ y = y + 2 ∗ x", "x + 2 ∗ y ∈ {y + 2 ∗ x}");

		
		// not(E : {F}) == not(E = F) (if F is a single expression)
		predicateTest("¬x + 2 ∗ y = y + 2 ∗ x", "¬x + 2 ∗ y ∈ {y + 2 ∗ x}");

		
		// {E} = {F} == E = F if E, F is a single expression
		predicateTest("x + 2 ∗ y = y + 2 ∗ x", "{x + 2 ∗ y} = {y + 2 ∗ x}");
		
		
		// not({E} = {F}) == not(E = F) if E, F is a single expression
		predicateTest("¬x + 2 ∗ y = y + 2 ∗ x", "¬{x + 2 ∗ y} = {y + 2 ∗ x}");

		
		// {x |-> a, ..., y |-> b}~  ==  {a |-> x, ..., b |-> y}
		expressionTest("{3 ↦ x + 2}", "{x + 2 ↦ 3}∼");
		expressionTest("{3 ↦ x + 2, y ↦ 2}", "{x + 2 ↦ 3, 2 ↦ y}∼");
		expressionTest("{3 ↦ x + 2, y ↦ 2, b ↦ a}", "{x + 2 ↦ 3, 2 ↦ y, a ↦ b}∼");
		

		// Typ = {} == false (where Typ is a type expression) is NOT done here
		predicateTest("ℤ = ∅", "ℤ = ∅");
		predicateTest("ℙ(ℤ) = ∅", "ℙ(ℤ) = ∅");

		
		// {} = Typ == false (where Typ is a type expression) is NOT done here
		predicateTest("∅ = ℤ", "∅ = ℤ");
		predicateTest("∅ = ℙ(ℤ)", "∅ = ℙ(ℤ)");
		

		// E : Typ == true (where Typ is a type expression) is NOT done here
		predicateTest("E ∈ ℤ", "E ∈ ℤ");

		
		// f(f~(E)) == E
		expressionTest("y + 2", "{x + 2 ↦ 3}(({x + 2 ↦ 3}∼)(y + 2))");
		

		// f~(f(E)) == E
		expressionTest("y + 2", "({x + 2 ↦ 3}∼)({x + 2 ↦ 3}(y + 2))");

		
		// {x |-> a, ..., y |-> b}({a |-> x, ..., b |-> y}(E)) = E
		expressionTest("y + 2", "{x + 2 ↦ 3}({3 ↦ x + 2}(y + 2))");
		expressionTest("y + 2", "{x + 2 ↦ 3, y ↦ 2}({3 ↦ x + 2, 2 ↦ y}(y + 2))");
		expressionTest("y + 2", "{x + 2 ↦ 3, y ↦ 2, a ↦ b}({3 ↦ x + 2, 2 ↦ y, b ↦ a}(y + 2))");
		

		// p;...;{};...;q == {}
		Expression emptySetFunction = ff.makeEmptySet(ff.makePowerSetType(ff
				.makeProductType(intType, intType)), null);
		typeCheck = emptySetFunction.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Expected expression: \n\t"
				+ emptySetFunction + "\n\tcannot be type checked");
		inputExp = makeInputExpression("{x + 2 ↦ 3}");
		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { inputExp, emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);
		
		inputExp = makeInputExpression("{x + 2 ↦ 3}");
		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { emptySetFunction, inputExp }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);
		
		Expression inputExp1 = makeInputExpression("{x + 2 ↦ 3}");
		Expression inputExp2 = makeInputExpression("{x + 4 ↦ FALSE}");
		Expression inputExp3 = makeInputExpression("{FALSE ↦ y+ 2}");
		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { emptySetFunction, inputExp1, inputExp2,
						inputExp3 }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { inputExp1, emptySetFunction, inputExp2,
						inputExp3 }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { inputExp1, inputExp2, inputExp3,
						emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { emptySetFunction, inputExp1,
						emptySetFunction, inputExp2, inputExp3 }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { emptySetFunction, inputExp1, inputExp2,
						inputExp3, emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { inputExp1, emptySetFunction, inputExp2,
						inputExp3, emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.FCOMP,
				new Expression[] { emptySetFunction, inputExp1,
						emptySetFunction, inputExp2, inputExp3,
						emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);


		// p circ ... circ {} circ ... circ q == {}
		inputExp = makeInputExpression("{x + 2 ↦ 3}");
		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { inputExp, emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);
		
		inputExp = makeInputExpression("{x + 2 ↦ 3}");
		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { emptySetFunction, inputExp }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);
		
		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { emptySetFunction, inputExp3, inputExp2,
						inputExp1 }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { inputExp3, inputExp2, emptySetFunction,
						inputExp1 }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { inputExp3, inputExp2, inputExp1,
						emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { emptySetFunction, inputExp3, inputExp2,
						emptySetFunction, inputExp1 }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { emptySetFunction, inputExp3, inputExp2,
						inputExp1, emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { inputExp3, inputExp2, emptySetFunction,
						inputExp1, emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);

		inputExp = ff.makeAssociativeExpression(Expression.BCOMP,
				new Expression[] { emptySetFunction, inputExp3, inputExp2,
						emptySetFunction, inputExp1, emptySetFunction }, null);
		typeCheck = inputExp.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t"
				+ inputExp + "\n\tcannot be type checked");
		expressionTest(emptySetFunction, inputExp);


		// U \ (U \ S) == S
		expressionTest("{x ∣ x > 0}", "ℤ ∖ (ℤ ∖ {x ∣ x > 0})");
		expressionTest("ℙ({x ∣ x > 0})", "ℙ(ℤ) ∖ (ℙ(ℤ) ∖ ℙ({x ∣ x > 0}))");
		
		
		// S \ U == {}
		inputExp = makeInputExpression("S ∖ ℤ");
		expressionTest(emptySetInteger, inputExp);
		inputExp = makeInputExpression("S ∖ (ℤ × ℤ)");
		expressionTest(emptySetFunction, inputExp);

		
		// S \/ ... \/ U \/ ... \/ T == U
		expressionTest("ℤ", "ℤ ∪ S");
		expressionTest("ℤ", "S ∪ ℤ");
		expressionTest("ℤ", "ℤ ∪ S ∪ T ∪ U");
		expressionTest("ℤ", "S ∪ ℤ ∪ T ∪ U");
		expressionTest("ℤ", "S ∪ T ∪ U ∪ ℤ ");
		expressionTest("ℤ", "ℤ ∪ S ∪ ℤ ∪ T ∪ U");
		expressionTest("ℤ", "ℤ ∪ S ∪ T ∪ U ∪ ℤ");
		expressionTest("ℤ", "S ∪ ℤ ∪ T ∪ U ∪ ℤ");
		expressionTest("ℤ", "ℤ ∪ S ∪ ℤ ∪ T ∪ U ∪ ℤ");

		
		// S /\ ... /\ U /\ ... /\ T == S /\ ... /\ ... /\ T
		expressionTest("{x ∣ x > 0}", "ℤ ∩ {x ∣ x > 0}");
		expressionTest("{x ∣ x > 0}", "{x ∣ x > 0} ∩ ℤ");
		expressionTest("{x ∣ x > 0} ∩ S ∩ T", "ℤ ∩ {x ∣ x > 0} ∩ S ∩ T");
		expressionTest("{x ∣ x > 0} ∩ S ∩ T", "{x ∣ x > 0} ∩ ℤ ∩ S ∩ T");
		expressionTest("{x ∣ x > 0} ∩ S ∩ T", "{x ∣ x > 0} ∩ S ∩ T ∩ ℤ");
		expressionTest("{x ∣ x > 0} ∩ S ∩ T", "ℤ ∩ {x ∣ x > 0} ∩ ℤ ∩ S ∩ T");
		expressionTest("{x ∣ x > 0} ∩ S ∩ T", "ℤ ∩ {x ∣ x > 0} ∩ S ∩ T ∩ ℤ");
		expressionTest("{x ∣ x > 0} ∩ S ∩ T", "{x ∣ x > 0} ∩ ℤ ∩ S ∩ T ∩ ℤ");
		expressionTest("{x ∣ x > 0} ∩ S ∩ T", "ℤ ∩ {x ∣ x > 0} ∩ ℤ ∩ S ∩ T ∩ ℤ ");

		
		// A \ B <: S == A <: S \/ B
		predicateTest("A ⊆ {x ∣ x > 0} ∪ B", "A ∖ B ⊆ {x ∣ x > 0} ");

		
		// r[∅] == ∅
		expressionTest("(∅ ⦂ ℙ(T))", "r[(∅ ⦂ ℙ(S))]", "r", "ℙ(S×T)");
		expressionTest("(∅ ⦂ ℙ(T))", "(∅ ⦂ ℙ(S×T))[(∅ ⦂ ℙ(S))]");

		// ∅[A] == ∅
		expressionTest("(∅ ⦂ ℙ(T))", "(∅ ⦂ ℙ(S×T))[A]", "A", "ℙ(S)");
		
		// dom({}) == {}
		Type domainType = intType;
		Type rangeType = ff.makeBooleanType();
		Type relType = ff.makeRelationalType(domainType, rangeType);
		AtomicExpression r = ff.makeEmptySet(relType, null);
		UnaryExpression domExp = ff.makeUnaryExpression(Expression.KDOM, r, null);
		expressionTest(emptySetInteger, domExp);

		
		// ran({}) == {}
		UnaryExpression ranExp = ff.makeUnaryExpression(Expression.KRAN, r,
				null);
		AtomicExpression emptySetBoolean = ff.makeEmptySet(ff
				.makePowerSetType(rangeType), null);
		expressionTest(emptySetBoolean, ranExp);
		

		// (S ** {E})(x) == E
		expressionTest("TRUE", "(ℕ × {TRUE})(1)");
		expressionTest("1", "(BOOL × {1})(TRUE)");
	
		// r <+ ... <+ {} <+ ... <+ s = r <+ ... <+ s
		expressionTest("{1 ↦ 2}  {3 ↦ 4}", "{1 ↦ 2}  ∅  {3 ↦ 4}");
		
		// (%x . P | E)(y)
		expressionTest("0", "(λx·x∈ℤ∣x)(0)");
		expressionTest("1+2", "(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(1↦2)");
		expressionTest("prj1(1↦2)", "(λx·x∈ℤ×ℤ∣prj1(x))(1↦2)");
		expressionTest(
				"{m↦n∣m>5−3 ∧ n> (8−4)∗2}",
				"(λ(x↦y)↦((a↦b)↦(c ⦂ ℤ ))·x∈ℤ∧y∈ℤ∧a∈ℤ∧b∈ℤ ∣{m↦n∣m>y−x ∧ n>(b−a)∗c})((3↦5)↦((4↦8)↦2))");
		predicateTest("∀x·x=ℕ⇒x={m∣m>0+0}", "∀x·x=ℕ⇒x=(λa↦b·a∈ℕ∧b∈ℕ∣{m∣m>a+b})(0↦0)");
		// verify that no exception is thrown when no rewrite occurs
		expressionTest("(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(w)", "(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(w)", "w", "ℤ×ℤ");
	}


	/**
	 * Tests for rewriting arithmetic formulas. 
	 */
	@Test
	public void testArithmetic() {
		// E + ... + 0 + ... + F == E + ... + ... + F
		expressionTest("0", "0 + 0");
		expressionTest("x + 2 ∗ y", "0 + (x + 2 ∗ y)");
		expressionTest("x + 2 ∗ y", "(x + 2 ∗ y) + 0");
		expressionTest("x + 2 ∗ y + y", "0 + (x + 2 ∗ y) + y");
		expressionTest("x + 2 ∗ y + y", "(x + 2 ∗ y) + 0 + y");
		expressionTest("x + 2 ∗ y + y", "(x + 2 ∗ y) + y + 0");
		expressionTest("x + 2 ∗ y + y", "0 + (x + 2 ∗ y) + 0 + y");
		expressionTest("x + 2 ∗ y + y", "0 + (x + 2 ∗ y) + y + 0");
		expressionTest("x + 2 ∗ y + y", "(x + 2 ∗ y) + 0 + y + 0");
		expressionTest("x + 2 ∗ y + y", "0 + (x + 2 ∗ y) + 0 + y + 0");
		
		
		// E - 0 == E
		expressionTest("(x + 2 ∗ y)", "(x + 2 ∗ y) − 0");

		
		// 0 - E == -E
		expressionTest("−(x + 2 ∗ y)", "0 − (x + 2 ∗ y)");
		expressionTest("−(1)", "0 − 1");
		expressionTest("−1", "−(1)");


		// -(-E) == E
		expressionTest("x + 2 ∗ y", "−(−(x + 2 ∗ y))");
		expressionTest("1", "−(−1)");
		expressionTest("1", "−(−(1))");
		
		
		// E - E == 0
		expressionTest("0", "1 − 1");
		expressionTest("0", "(x + 2 ∗ y) − (x + 2 ∗ y)");
		
		
		// E * ... * 1 * ... * F == E * ... * ... * F
		expressionTest("1", "1 ∗ 1");
		expressionTest("x + 2 ∗ y", "(x + 2 ∗ y) ∗ 1");
		expressionTest("x + 2 ∗ y", "1 ∗ (x + 2 ∗ y)");
		expressionTest("(x + 2 ∗ y) ∗ y", "1 ∗ (x + 2 ∗ y) ∗ y");
		expressionTest("(x + 2 ∗ y) ∗ y", "(x + 2 ∗ y) ∗ 1 ∗ y");
		expressionTest("(x + 2 ∗ y) ∗ y", "(x + 2 ∗ y) ∗ y ∗ 1");
		expressionTest("(x + 2 ∗ y) ∗ y", "1 ∗ (x + 2 ∗ y) ∗ 1 ∗ y");
		expressionTest("(x + 2 ∗ y) ∗ y", "1 ∗ (x + 2 ∗ y) ∗ y ∗ 1");
		expressionTest("(x + 2 ∗ y) ∗ y", "(x + 2 ∗ y) ∗ 1 ∗ y ∗ 1");
		expressionTest("(x + 2 ∗ y) ∗ y", "1 ∗ (x + 2 ∗ y) ∗ 1 ∗ y ∗ 1");

		
		// E * ... * 0 * ... * F == 0
		expressionTest("0", "0 ∗ 0");
		expressionTest("0", "0 ∗ 1");
		expressionTest("0", "1 ∗ 0");
		expressionTest("0", "(x + 2 ∗ y) ∗ 0");
		expressionTest("0", "0 ∗ (x + 2 ∗ y)");
		expressionTest("0", "0 ∗ (x + 2 ∗ y) ∗ y");
		expressionTest("0", "(x + 2 ∗ y) ∗ 0 ∗ y");
		expressionTest("0", "(x + 2 ∗ y) ∗ y ∗ 0");
		expressionTest("0", "0 ∗ (x + 2 ∗ y) ∗ 0 ∗ y");
		expressionTest("0", "0 ∗ (x + 2 ∗ y) ∗ y ∗ 0");
		expressionTest("0", "(x + 2 ∗ y) ∗ 0 ∗ y ∗ 0");
		expressionTest("0", "0 ∗ (x + 2 ∗ y) ∗ 0 ∗ y ∗ 0");


		// (-E) * (-F) == E * F
		expressionTest("(x + 2 ∗ y) ∗ y", "(−(x + 2 ∗ y)) ∗ (−y)");
		expressionTest("(x + 2 ∗ y) ∗ 2", "(−(x + 2 ∗ y)) ∗ (−2)");
		expressionTest("(x + 2 ∗ y) ∗ 2", "(−(x + 2 ∗ y)) ∗ (−(2))");
		expressionTest("2 ∗ (x + 2 ∗ y)", "(−2) ∗ (−(x + 2 ∗ y))");
		expressionTest("2 ∗ (x + 2 ∗ y)", "(−(2)) ∗ (−(x + 2 ∗ y))");
		expressionTest("−((x + 2 ∗ y) ∗ 2)",
				"(−(x + 2 ∗ y)) ∗ (−(2)) ∗ (−1)");
		expressionTest("−((x + 2 ∗ y) ∗ 2)", "−((x + 2 ∗ y) ∗ 2 ∗ 1)");
		

		// E / E == 1
		expressionTest("1", "2 ÷ 2");
		expressionTest("1", "(x + 2 ∗ y) ÷ (x + 2 ∗ y)");

		
		// E / 1 == E
		expressionTest("2", "2 ÷ 1");
		expressionTest("x + 2 ∗ y", "(x + 2 ∗ y) ÷ 1");

		
		// 0 / E == 0
		expressionTest("0", "0 ÷ 2");
		expressionTest("0", "0 ÷ (x + 2 ∗ y)");

		
		// (-E) /(-F) == E / F
		expressionTest("3 ÷ 2", "(−3) ÷ (−2)");
		expressionTest("x ÷ (x + 2 ∗ y)", "(−x) ÷(−(x + 2 ∗ y))");

		
		// (X * ... * E * ... * Y)/E == X * ... * Y
		expressionTest("2", "((x + 2 ∗ y) ∗ 2) ÷  (x + 2 ∗ y)");
		expressionTest("2", "(2 ∗ (x + 2 ∗ y)) ÷  (x + 2 ∗ y)");
		expressionTest("x + 2 ∗ y", "(2 ∗ (x + 2 ∗ y)) ÷  2");
		expressionTest("x + 2 ∗ y", "((x + 2 ∗ y) ∗ 2) ÷  2");
		expressionTest("(x + 2 ∗ y) ∗ 2", "(2 ∗ (x + 2 ∗ y) ∗ 2) ÷  2");
		expressionTest("2 ∗ (x + 2 ∗ y)", "(2 ∗ (x + 2 ∗ y) ∗ (x + 2 ∗ y)) ÷  (x + 2 ∗ y)");

		
		// E^1 == E
		expressionTest("2", "2^1");
		expressionTest("−2", "(−2)^1");
		expressionTest("x + 2 ∗ y", "(x + 2 ∗ y)^1");
		expressionTest("−(x + 2 ∗ y)", "(−(x + 2 ∗ y))^1");
		
		
		// E^0 == 1
		expressionTest("1", "2^0");
		expressionTest("1", "(−2)^0");
		expressionTest("1", "(x + 2 ∗ y)^0");
		expressionTest("1", "(−(x + 2 ∗ y))^0");


		// 1^E == 1
		expressionTest("1", "1^2");
		expressionTest("1", "1^(−2)");
		expressionTest("1", "1^(x + 2 ∗ y)");
		expressionTest("1", "1^(−(x + 2 ∗ y))");

		
		// -(i) == (-i) where i is a literal
		expressionTest("(−1)", "−(1)");
		
		
		// -(-i) == i where i is a literal
		expressionTest("1", "−(−1)");

		
		// i = j == true   or   i = j == false  (by computation)
		predicateTest("⊤", "1 = 1");
		predicateTest("⊥", "1 = 2");
		predicateTest("⊥", "1 = −1");
		predicateTest("⊤", "−1 = −1");
		predicateTest("⊥", "−1 = −2");
		predicateTest("⊥", "−1 = 1");
		

		// i <= j == true   or   i <= j == false  (by computation)
		predicateTest("⊤", "1 ≤ 1");
		predicateTest("⊤", "1 ≤ 2");
		predicateTest("⊥", "1 ≤ −1");
		predicateTest("⊤", "−1 ≤ −1");
		predicateTest("⊥", "−1 ≤ −2");
		predicateTest("⊤", "−1 ≤ 1");

		// i < j == true   or   i < j == false  (by computation)
		predicateTest("⊥", "1 < 1");
		predicateTest("⊤", "1 < 2");
		predicateTest("⊥", "1 < −1");
		predicateTest("⊥", "−1 < −1");
		predicateTest("⊥", "−1 < −2");
		predicateTest("⊤", "−1 < 1");

		// i >= j == true   or   i >= j == false  (by computation)
		predicateTest("⊤", "1 ≥ 1");
		predicateTest("⊥", "1 ≥ 2");
		predicateTest("⊤", "1 ≥ −1");
		predicateTest("⊤", "−1 ≥ −1");
		predicateTest("⊤", "−1 ≥ −2");
		predicateTest("⊥", "−1 ≥ 1");

		// i > j == true   or   i > j == false  (by computation)
		predicateTest("⊥", "1 > 1");
		predicateTest("⊥", "1 > 2");
		predicateTest("⊤", "1 > −1");
		predicateTest("⊥", "−1 > −1");
		predicateTest("⊤", "−1 > −2");
		predicateTest("⊥", "−1 > 1");
		
		// E <= E = true
		predicateTest("⊤", "x + 2 ∗ y ≤ x + 2 ∗ y");


		// E >= E = true
		predicateTest("⊤", "x + 2 ∗ y ≥ x + 2 ∗ y");

		// E < E = false
		predicateTest("⊥", "x + 2 ∗ y < x + 2 ∗ y");

		// E > E = false
		predicateTest("⊥", "x + 2 ∗ y > x + 2 ∗ y");

	}

	/**
	 * Non-regression test for multiplication and division by a negative
	 * literal.
	 */
	@Test
	public void testBug2706216() {
		expressionTest("− (d ∗ 2)", "d ∗ (−2)");
		expressionTest("− (d ∗ 2 ∗ 2)", "d ∗ 2 ∗ (−2)");
		// original problem
		expressionTest("v + (− d)", "v + d ∗ (−1)");
	}	

	/**
	 * Tests for rewriting finiteness predicates.
	 */
	@Test
	public void testFinite() {
		// finite({}) == true
		Expression emptySet = ff.makeEmptySet(ff.makePowerSetType(ff
				.makeIntegerType()), null);
		Predicate finite = ff.makeSimplePredicate(Expression.KFINITE, emptySet,
				null);
		ITypeCheckResult typeCheck = finite.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t" + finite
				+ "\n\tcannot be type checked");
		Predicate truePred = makeExpectedPredicate("⊤");
		predicateTest(truePred, finite);

		
		// finite({a, ..., b}) == true
		predicateTest("⊤", "finite({TRUE})");
		predicateTest("⊤", "finite({TRUE, FALSE})");
		predicateTest("⊤", "finite({1, 2})");
		predicateTest("⊤", "finite({2})");
		
		// finite(S \/ ... \/ T) == finite(S) & ... & finite(T)
		predicateTest("finite({x ∣ x > 0}) ∧ finite({y ∣ y < 0})",
				"finite({x ∣ x > 0} ∪ {y ∣ y < 0})");
		predicateTest(
				"finite({x ∣ x > 0}) ∧ finite({y ∣ y < 0}) ∧ finite({x ∣ x = 0})",
				"finite({x ∣ x > 0} ∪ {y ∣ y < 0} ∪ {x ∣ x =  0})");

		
		// finite(POW(S)) == finite(S)
		predicateTest("finite({x ∣ x > 0})", "finite(ℙ({x ∣ x > 0}))");

		
		// finite(S ** T) == S = {} or T = {} or (finite(S) & finite(T))
		predicateTest(
				"{x ∣ x > 0} = ∅ ∨ {x ∣ x < 0} = ∅ ∨ (finite({x ∣ x > 0}) ∧ finite({x ∣ x < 0}))",
				"finite({x ∣ x > 0} × {x ∣ x < 0})");
		
		
		// finite(r~) == finite(r)
		predicateTest("finite({x ↦ y ∣ x > 0 ∧ y < 2})",
				"finite({x ↦ y ∣ x > 0 ∧ y < 2}∼)");

		
		// finite(a..b) == true
		predicateTest("⊤", "finite(a‥b)");

	}


	/**
	 * Tests for rewriting cardinality expressions.
	 */
	@Test
	public void testCardinality() {
		// card({}) == 0
		Expression emptySet = ff.makeEmptySet(ff.makePowerSetType(ff
				.makeIntegerType()), null);
		Expression card = ff.makeUnaryExpression(Expression.KCARD, emptySet,
				null);
		ITypeCheckResult typeCheck = card.typeCheck(ff.makeTypeEnvironment());
		Assert.isTrue(typeCheck.isSuccess(), "Input expression: \n\t" + card
				+ "\n\tcannot be type checked");
		Expression number0 = makeExpectedExpression("0");
		expressionTest(number0, card);

		
		// card({E}) == 1
		expressionTest("1", "card({x + 1})");
		
		
		// card(POW(S)) == 2^card(S)
		expressionTest("2^(card({x ∣ x >0}))", "card(ℙ({x ∣ x > 0}))");
		
		
		// card(S ** T) == card(S) * card(T)
		expressionTest("card({x ∣ x > 0}) ∗ card({y ∣ y < 0})",
				"card({x ∣ x > 0} × {y ∣ y < 0})");
		
		
		// card(S \ T) == card(S) - card(S /\ T)
		expressionTest("card({x ∣ x > 0}) − card({x ∣ x > 0} ∩ {y ∣ y < 0})",
				"card({x ∣ x > 0} ∖ {y ∣ y < 0})");
		
		
		// card(S) = 0  ==  S = {}
		predicateTest("{x ∣ x > 0} = ∅", "card({x ∣ x > 0}) = 0");

		
		// 0 = card(S)  ==  S = {}
		predicateTest("{x ∣ x > 0} = ∅", "0 = card({x ∣ x > 0})");

		
		// not(card(S) = 0)  ==  not(S = {})
		predicateTest("¬{x ∣ x > 0} = ∅", "¬card({x ∣ x > 0}) = 0");

		
		// not(0 = card(S))  ==  not(S = {})
		predicateTest("¬{x ∣ x > 0} = ∅", "¬0 = card({x ∣ x > 0})");

		
		// card(S) > 0  ==  not(S = {})
		predicateTest("¬{x ∣ x > 0} = ∅", "card({x ∣ x > 0}) > 0");

		
		// 0 < card(S)  ==  not(S = {})
		predicateTest("¬{x ∣ x > 0} = ∅", "0 < card({x ∣ x > 0})");
		

		// card(S) = 1 == #x.S = {x}
		predicateTest("∃y·{x ∣ x > 0} = {y}", "card({x ∣ x > 0}) = 1");

		
		// 1 = card(S) == #x.S = {x}
		predicateTest("∃y·{x ∣ x > 0} = {y}", "1 = card({x ∣ x > 0})");


		// card(S(1) \/ ... \/ S(n)) == card(S(1)) + ... card(S(2)) -
		//	                            - ... 
		//                              + (-1)^(n-1)card(S(1) /\ ... card(S(n)))
		expressionTest(
				"card({x ∣ x ∈ BOOL}) + card(S) − card({x ∣ x ∈ BOOL} ∩ S)",
				"card({x ∣ x ∈ BOOL} ∪ S)");
		expressionTest(
				"card({x ∣ x ∈ BOOL}) + card(S) + card(T) − "
						+ "(card({x ∣ x ∈ BOOL} ∩ S) + card({x ∣ x ∈ BOOL} ∩ T) + card(S ∩ T)) + "
						+ "card({x ∣ x ∈ BOOL} ∩ S ∩ T)",
				"card({x ∣ x ∈ BOOL} ∪ S ∪ T)");
		expressionTest(
				"card({x ∣ x ∈ BOOL}) + card(S) + card(T) + card(R) − "
						+ "(card({x ∣ x ∈ BOOL} ∩ S) + card({x ∣ x ∈ BOOL} ∩ T) + card({x ∣ x ∈ BOOL} ∩ R) + card(S ∩ T) + card(S ∩ R) + card(T ∩ R)) + "
						+ "(card({x ∣ x ∈ BOOL} ∩ S ∩ T) + card({x ∣ x ∈ BOOL} ∩ S ∩ R) + card({x ∣ x ∈ BOOL} ∩ T ∩ R) + card(S ∩ T ∩ R)) − "
						+ "card({x ∣ x ∈ BOOL} ∩ S ∩ T ∩ R)",
				"card({x ∣ x ∈ BOOL} ∪ S ∪ T ∪ R)");
	}

	/**
	 * Tests for rewriting boolean predicates.
	 */
	@Test
	public void testBoolean() {
		// bool(false) == FALSE
		expressionTest("FALSE", "bool(⊥)");
		
		// bool(true) == TRUE
		expressionTest("TRUE", "bool(⊤)");
		
		// TRUE = bool(P) == P
		predicateTest("x = 1", "TRUE = bool(x = 1)");
		
		// bool(P) = TRUE == P
		predicateTest("x = 1", "bool(x = 1) = TRUE");
		
		// FALSE = bool(P) == not(P)
		predicateTest("¬x = 1", "FALSE = bool(x = 1)");
		
		// bool(P) = FALSE == not(P)
		predicateTest("¬x = 1", "bool(x = 1) = FALSE");
	}

}
