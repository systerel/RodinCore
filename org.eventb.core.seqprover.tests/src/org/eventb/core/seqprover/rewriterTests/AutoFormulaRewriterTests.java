/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - SIMP_IN_COMPSET, SIMP_SPECIAL_OVERL, SIMP_FUNIMAGE_LAMBDA
 *     Systerel - added tests for SIMP_FUNIMAGE_LAMBDA
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
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
	
	private static final IDatatypeExtension DATATYPE = new IDatatypeExtension() {

		private static final String TYPE_NAME = "List";
		private static final String TYPE_IDENTIFIER = "List Id";
		
		
		@Override
		public String getTypeName() {
			return TYPE_NAME;
		}

		@Override
		public String getId() {
			return TYPE_IDENTIFIER;
		}
		
		@Override
		public void addTypeParameters(ITypeConstructorMediator mediator) {
			// no type parameter			
		}

		@Override
		public void addConstructors(IConstructorMediator mediator) {
			mediator.addConstructor("void", "VOID");
			
			final IntegerType intType = mediator.makeIntegerType();
			final IArgumentType intArgType = mediator.newArgumentType(intType);
			final IArgument destr1 = mediator.newArgument("destr1", intArgType);
			final IArgument destr2_0 = mediator.newArgument("destr2_0", intArgType);
			final IArgument destr2_1 = mediator.newArgument("destr2_1", intArgType);
			
			mediator.addConstructor("cons1", "CONS_1", asList(destr1));
			mediator.addConstructor("cons2", "CONS_2",
					asList(destr2_0, destr2_1));
		}

	};

	private static final IDatatype DT = ff.makeDatatype(DATATYPE);

	private static final Set<IFormulaExtension> EXTENSIONS = new HashSet<IFormulaExtension>();
	static {
		EXTENSIONS.addAll(DT.getExtensions());
		EXTENSIONS.add(FormulaFactory.getCond());
	}
	private static final FormulaFactory DT_FAC = FormulaFactory.getInstance(EXTENSIONS);

	// The automatic rewriter for testing.
	private static final IFormulaRewriter rewriter = new AutoRewriterImpl(DT_FAC);
	
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

		// cons(a1, b1) = cons(a2, b2)  ==  a1 = a2 & b1 = b2
		predicateTest("⊤", "void = void");
		predicateTest("a1 = a2", "cons1(a1) = cons1(a2)");
		predicateTest("a1 = a2 ∧ b1 = b2", "cons2(a1, b1) = cons2(a2, b2)");
		
		// cons1(...) = cons2(...)  ==  false
		predicateTest("⊥", "void = cons1(a1)");
		predicateTest("⊥", "cons1(a1) = cons2(a2, b2)");

	}
	
	/**
	 * Tests for rewriting set theoretical statements.
	 */
	@Test
	public void testSetTheory() {
		// S /\ ... /\ {} /\ ... /\ T == {}
		expressionTest("(∅ ⦂ ℙ(ℤ))", "{x ∣ x > 0} ∩ ∅");
		expressionTest("(∅ ⦂ ℙ(ℤ))", "∅ ∩ {x ∣ x > 0}");
		expressionTest("(∅ ⦂ ℙ(ℤ))", "∅ ∩ {x ∣ x > 0} ∩ S ∩ T");
		expressionTest("(∅ ⦂ ℙ(ℤ))", "{x ∣ x > 0} ∩ S ∩ ∅ ∩ T");
		expressionTest("(∅ ⦂ ℙ(ℤ))", "{x ∣ x > 0} ∩ S ∩ T ∩ ∅");
		expressionTest("(∅ ⦂ ℙ(ℤ))", "∅ ∩ {x ∣ x > 0} ∩ ∅ ∩ S ∩ T");
		expressionTest("(∅ ⦂ ℙ(ℤ))", "∅ ∩ {x ∣ x > 0} ∩ S ∩ T ∩ ∅");
		expressionTest("(∅ ⦂ ℙ(ℤ))", "{x ∣ x > 0} ∩ ∅ ∩ S ∩ T ∩ ∅");
		expressionTest("(∅ ⦂ ℙ(ℤ))", "∅ ∩ {x ∣ x > 0} ∩ ∅ ∩ S ∩ T ∩ ∅");
		
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
		predicateTest("∀n·n≥0 ⇒ (∃x,y· (x≥ 0 ∧ y≥ 0) ∧ x+y = n)",
				"∀n·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥0∣x+y}");
		// One Point Rule applies
		predicateTest("∀n·n≥0 ⇒ (∃y· (n ≥ 0 ∧ y≥ 0))",
				"∀n·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥0∣x}");
		predicateTest("∀n,m·n≥0 ⇒ (∃y· (n ≥ 0 ∧ y≥ m))",
				"∀n,m·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥m∣x}");
		// One Point Rule applies replacement on expression ('x=n' here)
		predicateTest("n=0", "n ∈ {x·x=0∣x}");
		// One Point Rule does not apply replacement on guard ('x=0' here)
		predicateTest("∃x· x=0 ∧ x+1 = n", "n ∈ {x·x=0∣x+1}");
		// Jean-Raymond Abrial's bug
		predicateTest("∃z·(∃x,y·(x>0∧y>0)∧g(x+y)−g(x)−g(y)=l)∧l=z",
				"∃z·(l∈ {x,y·x>0 ∧ y>0 ∣ g(x+y)−g(x)−g(y)})∧l=z");
		
		// S \ S == {}
		expressionTest("(∅ ⦂ ℙ(ℤ))", "{y ∣ y > 0} ∖ {y ∣ y > 0}");
		

		// {} \ S == {}
		expressionTest("(∅ ⦂ ℙ(ℤ))", "∅ ∖ {y ∣ y > 0}");


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
		expressionTest("(∅⦂S↔U)", "f;(∅⦂T↔U)", "f", "S↔T");
		expressionTest("(∅⦂S↔U)", "(∅⦂S↔T);f", "f", "T↔U");
		expressionTest("(∅⦂S↔W)", "(∅⦂S↔T);f;g;h",//
				"f", "T↔U", "g", "U↔V", "h", "V↔W");
		expressionTest("(∅⦂S↔W)", "f;(∅⦂T↔U);g;h",//
				"f", "S↔T", "g", "U↔V", "h", "V↔W");
		expressionTest("(∅⦂S↔W)", "f;g;h;(∅⦂V↔W)",//
				"f", "S↔T", "g", "T↔U", "h", "U↔V");
		expressionTest("(∅⦂S↔X)", "(∅⦂S↔T);f;(∅⦂U↔V);g;h",//
				"f", "T↔U", "g", "V↔W", "h", "W↔X");
		expressionTest("(∅⦂S↔X)", "(∅⦂S↔T);f;g;h;(∅⦂W↔X)",//
				"f", "T↔U", "g", "U↔V", "h", "V↔W");
		expressionTest("(∅⦂S↔X)", "f;(∅⦂T↔U);g;h;(∅⦂W↔X)",//
				"f", "S↔T", "g", "U↔V", "h", "V↔W");
		expressionTest("(∅⦂S↔Y)", "(∅⦂S↔T);f;(∅⦂U↔V);g;h;(∅⦂X↔Y)",//
				"f", "T↔U", "g", "V↔W", "h", "W↔X");


		// p circ ... circ {} circ ... circ q == {}
		expressionTest("(∅⦂S↔U)", "f∘(∅⦂S↔T)", "f", "T↔U");
		expressionTest("(∅⦂S↔U)", "(∅⦂T↔U)∘f", "f", "S↔T");
		expressionTest("(∅⦂S↔W)", "(∅⦂V↔W)∘h∘g∘f",//
				"f", "S↔T", "g", "T↔U", "h", "U↔V");
		expressionTest("(∅⦂S↔W)", "h∘g∘(∅⦂T↔U)∘f",//
				"f", "S↔T", "g", "U↔V", "h", "V↔W");
		expressionTest("(∅⦂S↔W)", "h∘g∘f∘(∅⦂S↔T)",//
				"f", "T↔U", "g", "U↔V", "h", "V↔W");
		expressionTest("(∅⦂S↔X)", "(∅⦂W↔X)∘h∘g∘(∅⦂T↔U)∘f",//
				"f", "S↔T", "g", "U↔V", "h", "V↔W");
		expressionTest("(∅⦂S↔X)", "h∘g∘(∅⦂U↔V)∘f∘(∅⦂S↔T)",//
				"f", "T↔U", "g", "V↔W", "h", "W↔X");
		expressionTest("(∅⦂S↔X)", "(∅⦂W↔X)∘h∘g∘f∘(∅⦂S↔T)",//
				"f", "T↔U", "g", "U↔V", "h", "V↔W");
		expressionTest("(∅⦂S↔Y)", "(∅⦂X↔Y)∘h∘g∘(∅⦂U↔V)∘f∘(∅⦂S↔T)",//
				"f", "T↔U", "g", "V↔W", "h", "W↔X");


		// U \ (U \ S) == S
		expressionTest("{x ∣ x > 0}", "ℤ ∖ (ℤ ∖ {x ∣ x > 0})");
		expressionTest("ℙ({x ∣ x > 0})", "ℙ(ℤ) ∖ (ℙ(ℤ) ∖ ℙ({x ∣ x > 0}))");
		
		
		// S \ U == {}
		expressionTest("(∅ ⦂ ℙ(ℤ))", "S ∖ ℤ");
		expressionTest("(∅ ⦂ ℙ(ℤ×ℤ))", "S ∖ (ℤ × ℤ)");

		
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
		expressionTest("(∅ ⦂ ℙ(S))", "dom((∅ ⦂ ℙ(S×T)))");
		
		// ran({}) == {}
		expressionTest("(∅ ⦂ ℙ(T))", "ran((∅ ⦂ ℙ(S×T)))");

		// (S ** {E})(x) == E
		expressionTest("TRUE", "(ℕ × {TRUE})(1)");
		expressionTest("1", "(BOOL × {1})(TRUE)");
	
		// r <+ ... <+ {} <+ ... <+ s = r <+ ... <+ s
		expressionTest("{1 ↦ 2}  {3 ↦ 4}", "{1 ↦ 2}  ∅  {3 ↦ 4}");
		
		// (%x . P | E)(y) and similar
		expressionTest("0", "(λx·x∈ℤ∣x)(0)");
		expressionTest("1", "{x·x∈ℤ∣x↦x}(1)");
		expressionTest("1+2", "(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(1↦2)");
		expressionTest("prj1(1↦2)", "(λx·x∈ℤ×ℤ∣prj1(x))(1↦2)");
		expressionTest(//
				"{m↦n∣m>5−3 ∧ n> (8−4)∗2}",//
				"(λ(x↦y)↦((a↦b)↦(c ⦂ ℤ ))·"//
						+ "x∈ℤ∧y∈ℤ∧a∈ℤ∧b∈ℤ ∣"//
						+ "{m↦n∣m>y−x ∧ n>(b−a)∗c})((3↦5)↦((4↦8)↦2))");
		predicateTest("∀x·x=ℕ⇒x={m∣m>1+2}",
				"∀x·x=ℕ⇒x=(λa↦b·a∈ℕ∧b∈ℕ∣{m∣m>a+b})(1↦2)");
		predicateTest("∀x·x=ℕ⇒x={m∣m>0}",
				"∀x·x=ℕ⇒x=(λa↦b·a∈ℕ∧b∈ℕ∣{m∣m>a+b})(0↦0)");
		// verify that no exception is thrown when no rewrite occurs
		expressionTest("(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(w)", "(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(w)",//
				"w", "ℤ×ℤ");
		// Rewriting fails as "x" is not a maplet
		expressionTest("{x·x∈ℤ×ℤ∣x}(1)", "{x·x∈ℤ×ℤ∣x}(1)");
		// Rewriting fails as "pair" is not an explicit maplet
		expressionTest("(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(pair)", "(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(pair)");
		
		// destr(cons(a_1, ..., a_n))  ==  a_i   [i is the param index of the destructor]
		expressionTest("1", "destr1(cons1(1))");
		expressionTest("1", "destr2_0(cons2(1, 2))");
		expressionTest("2", "destr2_1(cons2(1, 2))");
		expressionTest("destr2_0(cons1(1))", "destr2_0(cons1(1))");

	}
	
	@Test
	public void testBug2995930() {
		// Checks that internal lambda is conserved, and De Bruijn index are correct
		expressionTest("(λx↦p·x∈s∧p⊆s∣p)", "(λs·s⊆S∣(λx↦p·x∈s∧p⊆s∣p))(s)", "s", "ℙ(S)");
		// Checks that external lambda disappear and x is instantiated
		expressionTest("(λz·z∈ℕ ∣ z+z)[{1,2,3}]", "(λx·x∈ℙ(ℕ) ∣ (λz·z∈ℕ ∣ z+z)[x])({1,2,3})");
		// Real example from Bug 2995930 with an argument containing a bound identifier.
		predicateTest("∀t⦂ℙ(S)·(λx↦p·x∈t∧p⊆t∣p) = ∅",
				"∀t⦂ℙ(S)·(λs·s⊆S∣(λx↦p·x∈s∧p⊆s∣p))(t) = ∅", "S", "ℙ(S)");
	}

	/**
	 * Ensures that bug 3025836: Rodin 1.3.1 prover is still unsound is fixed.
	 * Also adds similar test cases for completeness.
	 */
	@Test
	public void testBug3025836() {
		predicateTest("∀x,y,z·x∈ℤ ∧ y∈BOOL ∧ z∈BOOL ⇒ x=0",
				"∀x,y,z·x∈ℤ ∧ y∈BOOL ∧ z∈BOOL ⇒ (λa·a∈ℤ ∣ a)(x)=0");
		predicateTest("∀x⦂ℤ,y⦂ℙ(ℤ)·y∪{x}=∅",
				"∀x⦂ℤ,y⦂ℙ(ℤ)·(λa·a∈ℤ∣y∪{a})(x)=∅");
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
		predicateTest("⊤", "finite((∅ ⦂ ℙ(ℤ)))");

		
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
		expressionTest("0", "card((∅ ⦂ ℙ(S)))");

		
		// card({E}) == 1
		expressionTest("1", "card({x + 1})");
		
		
		// card(POW(S)) == 2^card(S)
		expressionTest("2^(card({x ∣ x >0}))", "card(ℙ({x ∣ x > 0}))");
		
		
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

	@Test
	public void testCond() throws Exception {
		
		// COND(true, E_1, E_2) == E_1
		expressionTest("1", "COND(⊤,1,2)");

		// COND(false, E_1, E_2) == E_2
		expressionTest("2", "COND(⊥,1,2)");
		
	}
}
