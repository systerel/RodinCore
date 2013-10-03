/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - SIMP_IN_COMPSET, SIMP_SPECIAL_OVERL, SIMP_FUNIMAGE_LAMBDA
 *     Systerel - added tests for SIMP_FUNIMAGE_LAMBDA
 *     Systerel - added tests for SIMP_FORALL and SIMP_EXISTS
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.ast.FormulaFactory.getCond;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         This is the class for testing automatic rewriter {@link AutoRewriterImpl}
 *         using the abstract formula rewriter tests
 *         {@link AbstractFormulaRewriterTests}.
 */
public abstract class AutoFormulaRewriterTests extends PredicateSimplifierTests {

	public static final IDatatype DT;
	static {
		final FormulaFactory ff = FormulaFactory.getDefault();
		final Type integerType = ff.makeIntegerType();
		final IDatatypeBuilder dtBuilder = ff.makeDatatypeBuilder("List");
		dtBuilder.addConstructor("void");
		final IConstructorBuilder cons1 = dtBuilder.addConstructor("cons1");
		cons1.addArgument("destr1", integerType);
		final IConstructorBuilder cons2 = dtBuilder.addConstructor("cons2");
		cons2.addArgument("destr2_0", integerType);
		cons2.addArgument("destr2_1", integerType);
		DT = dtBuilder.finalizeDatatype();
	}

	protected static final FormulaFactory DT_FAC = FormulaFactory.getInstance(
			getCond()).withExtensions(DT.getExtensions());
	
	protected final boolean level2AndHigher;
	protected final boolean level3AndHigher;
	protected final boolean level4AndHigher;

	/**
	 * Constructor.
	 * <p>
	 * Create an abstract formula rewriter test with the input is the automatic
	 * rewriter.
	 */
	public AutoFormulaRewriterTests(AutoRewriterImpl rewriter) {
		super(DT_FAC, rewriter);
		this.level2AndHigher = rewriter.getLevel().from(Level.L2);
		this.level3AndHigher = rewriter.getLevel().from(Level.L3);
		this.level4AndHigher = rewriter.getLevel().from(Level.L4);
	}

	/**
	 * Tests for rewriting negations. 
	 */
	@Test
	public void testNegation() {
		super.testNegation();

		// not(x /: S))  ==  x : S
		rewritePred("¬2 ∉ S", "2 ∈ S");
		rewritePred("¬x ∉ {x ∣ x > 0}", "x ∈ {x ∣ x > 0}");

		
		// E /= F  ==  not (E = F)
		rewritePred("x + 2 ≠ y", "¬x + 2 = y");
		

		// E /: F  ==  not (E : F)
		rewritePred("2 ∉ S", "¬2 ∈ S");
		rewritePred("x ∉ {x ∣ x > 0}", "¬x ∈ {x ∣ x > 0}");

		
		// E /<<: F  ==  not (E <<: F)
		rewritePred("S ⊄ {x ∣ x > 0}", "¬ S ⊂ {x ∣ x > 0}");
		rewritePred("{x ∣ x > 0} ⊄ S", "¬ {x ∣ x > 0} ⊂ S");
		
		
		// E /<: F  ==  not (E <: F)
		rewritePred("S ⊈ {x ∣ x > 0}", "¬ S ⊆ {x ∣ x > 0}");
		rewritePred("{x ∣ x > 0} ⊈ S", "¬ {x ∣ x > 0} ⊆ S");

		
		// not(a <= b) == a > b
		rewritePred("¬ x + 2 ≤ y ∗ 2", "x + 2 > y ∗ 2");

		
		// not(a >= b) == a < b
		rewritePred("¬ x + 2 ≥ y ∗ 2", "x + 2 < y ∗ 2");

		
    	// not(a > b) == a <= b
		rewritePred("¬ x + 2 > y ∗ 2", "x + 2 ≤ y ∗ 2");

		
	   	// not(a < b) == a >= b
		rewritePred("¬ x + 2 < y ∗ 2", "x + 2 ≥ y ∗ 2");


	   	// not(E = FALSE) == E = TRUE
		rewritePred("¬ E = FALSE", "E = TRUE");
		

	   	// not(E = TRUE) == E = FALSE
		rewritePred("¬ E = TRUE", "E = FALSE");
		

	   	// not(FALSE = E) == TRUE = E
		rewritePred("¬ FALSE = E", "TRUE = E");

	   	// not(TRUE = E) == FALSE = E
		rewritePred("¬ TRUE = E", "FALSE = E");
		
	}

	/**
	 * Tests for rewriting equalities.
	 */
	@Test
	public void testEquality() {
		// E = E == true
		rewritePred("x + 2 ∗ y = x + 2 ∗ y", "⊤");


		// E /= E == false
		rewritePred("x + 2 ∗ y ≠ x + 2 ∗ y", "⊥");

		
		// E |-> F = G |-> H == E = G & F = H
		rewritePred("x + 2 ∗ y ↦ 3 = 2 ↦ y + 2 ∗ x", "x + 2 ∗ y = 2 ∧ 3 = y + 2 ∗ x");
		
		// TRUE = FALSE  ==  false
		rewritePred("TRUE = FALSE", "⊥");


		// FALSE = TRUE  ==  false
		rewritePred("FALSE = TRUE", "⊥");

		// cons(a1, b1) = cons(a2, b2)  ==  a1 = a2 & b1 = b2
		rewritePred("void = void", "⊤");
		rewritePred("cons1(a1) = cons1(a2)", "a1 = a2");
		rewritePred("cons2(a1, b1) = cons2(a2, b2)", "a1 = a2 ∧ b1 = b2");
		
		// cons1(...) = cons2(...)  ==  false
		rewritePred("void = cons1(a1)", "⊥");
		rewritePred("cons1(a1) = cons2(a2, b2)", "⊥");

	}
	
	/**
	 * Tests for rewriting set theoretical statements.
	 */
	@Test
	public void testSetTheory() {
		// S /\ ... /\ {} /\ ... /\ T == {}
		rewriteExpr("{x ∣ x > 0} ∩ ∅", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("∅ ∩ {x ∣ x > 0}", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("∅ ∩ {x ∣ x > 0} ∩ S ∩ T", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("{x ∣ x > 0} ∩ S ∩ ∅ ∩ T", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("{x ∣ x > 0} ∩ S ∩ T ∩ ∅", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("∅ ∩ {x ∣ x > 0} ∩ ∅ ∩ S ∩ T", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("∅ ∩ {x ∣ x > 0} ∩ S ∩ T ∩ ∅", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("{x ∣ x > 0} ∩ ∅ ∩ S ∩ T ∩ ∅", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("∅ ∩ {x ∣ x > 0} ∩ ∅ ∩ S ∩ T ∩ ∅", "(∅ ⦂ ℙ(ℤ))");
		
		// Test with empty and type
		rewriteExpr("(∅ ⦂ ℙ(S)) ∩ ∅", "(∅ ⦂ ℙ(S))");
		rewriteExpr("(∅ ⦂ ℙ(S)) ∩ S", "(∅ ⦂ ℙ(S))");
		rewriteExpr("S ∩ (∅ ⦂ ℙ(S))", "(∅ ⦂ ℙ(S))");


		// S /\ ... /\ T /\ ... /\ T /\ ... /\ U == S /\ ... /\ T /\ ... /\ ... /\ U
		rewriteExpr("{x ∣ x > 0} ∩ {x ∣ x > 0}", "{x ∣ x > 0}");
		rewriteExpr("S ∩ S ∩ T ∩ {x ∣ x > 0}", "S ∩ T ∩ {x ∣ x > 0}");
		rewriteExpr("S ∩ T ∩ S ∩ {x ∣ x > 0}", "S ∩ T ∩ {x ∣ x > 0}");
		rewriteExpr("S ∩ T ∩ {x ∣ x > 0} ∩ S", "S ∩ T ∩ {x ∣ x > 0}");
		rewriteExpr("S ∩ T ∩ S ∩ {x ∣ x > 0} ∩ S", "S ∩ T ∩ {x ∣ x > 0}");
		rewriteExpr("S ∩ T ∩ S ∩ T ∩ {x ∣ x > 0} ∩ S ∩ T", "S ∩ T ∩ {x ∣ x > 0}");

		rewriteExpr("S ∩ S", "S", "S=ℙ(S)");
		rewriteExpr("S ∩ S ∩ S", "S", "S=ℙ(S)");
		rewriteExpr("t ∩ t", "t", "t=ℙ(S)");
		rewriteExpr("t ∩ t ∩ t", "t", "t=ℙ(S)");


		// S \/ ... \/ {} \/ ... \/ T == S ... \/ ... \/ T
		rewriteExpr("{x ∣ x > 0} ∪ ∅", "{x ∣ x > 0}");
		rewriteExpr("∅ ∪ {x ∣ x > 0}", "{x ∣ x > 0}");
		rewriteExpr("∅ ∪ {x ∣ x > 0} ∪ S ∪ T", "{x ∣ x > 0} ∪ S ∪ T");
		rewriteExpr("{x ∣ x > 0} ∪ S ∪ ∅ ∪ T", "{x ∣ x > 0} ∪ S ∪ T");
		rewriteExpr("{x ∣ x > 0} ∪ S ∪ T ∪ ∅", "{x ∣ x > 0} ∪ S ∪ T");
		rewriteExpr("∅ ∪ {x ∣ x > 0} ∪ ∅ ∪ S ∪ T", "{x ∣ x > 0} ∪ S ∪ T");
		rewriteExpr("∅ ∪ {x ∣ x > 0} ∪ S ∪ T ∪ ∅", "{x ∣ x > 0} ∪ S ∪ T");
		rewriteExpr("{x ∣ x > 0} ∪ ∅ ∪ S ∪ T ∪ ∅", "{x ∣ x > 0} ∪ S ∪ T");
		rewriteExpr("∅ ∪ {x ∣ x > 0} ∪ ∅ ∪ S ∪ T ∪ ∅", "{x ∣ x > 0} ∪ S ∪ T");

		rewriteExpr("S ∪ S", "S", "S=ℙ(S)");
		rewriteExpr("S ∪ S ∪ S", "S", "S=ℙ(S)");
		rewriteExpr("t ∪ t", "t", "t=ℙ(S)");
		rewriteExpr("t ∪ t ∪ t", "t", "t=ℙ(S)");

		
		// S \/ ... \/ T \/ ... \/ T \/ ... \/ U == S \/ ... \/ T \/ ... \/ ... \/ U
		rewriteExpr("{x ∣ x > 0} ∪ {x ∣ x > 0}", "{x ∣ x > 0}");
		rewriteExpr("S ∪ S ∪ T ∪ {x ∣ x > 0}", "S ∪ T ∪ {x ∣ x > 0}");
		rewriteExpr("S ∪ T ∪ S ∪ {x ∣ x > 0}", "S ∪ T ∪ {x ∣ x > 0}");
		rewriteExpr("S ∪ T ∪ {x ∣ x > 0} ∪ S", "S ∪ T ∪ {x ∣ x > 0}");
		rewriteExpr("S ∪ T ∪ S ∪ {x ∣ x > 0} ∪ S", "S ∪ T ∪ {x ∣ x > 0}");
		rewriteExpr("S ∪ T ∪ S ∪ T ∪ {x ∣ x > 0} ∪ S ∪ T", "S ∪ T ∪ {x ∣ x > 0}");


		// {} <: S == true
		rewritePred("∅ ⊆ {x ∣ x > 0}", "⊤");
		

		// S <: S == true
		rewritePred("{x ∣ x > 0} ⊆ {x ∣ x > 0}", "⊤");
		

		// S <: A \/ ... \/ S \/ ... \/ B == true
		rewritePred("S ⊆ S ∪ T ∪ {x ∣ x > 0}", "⊤");
		rewritePred("S ⊆ T ∪ S ∪ {x ∣ x > 0}", "⊤");
		rewritePred("S ⊆ T ∪ {x ∣ x > 0} ∪ S", "⊤");

		
		// A /\ ... /\ S /\ ... /\ B <: S == true
		rewritePred("S ∩ T ∩ {x ∣ x > 0} ⊆ S", "⊤");
		rewritePred("T ∩ S ∩ {x ∣ x > 0} ⊆ S", "⊤");
		rewritePred("T ∩ {x ∣ x > 0} ∩ S ⊆ S", "⊤");
		

		// A \/ ... \/ B <: S == A <: S & ... & B <: S
		rewritePred("A ∪ B ⊆ {x ∣ x > 0}", "A ⊆ {x ∣ x > 0} ∧ B ⊆ {x ∣ x > 0}");
		rewritePred("A ∪ B ∪ C ⊆ {x ∣ x > 0}",
				"A ⊆ {x ∣ x > 0} ∧ B ⊆ {x ∣ x > 0} ∧ C ⊆ {x ∣ x > 0}");

		
		// S <: A /\ ... /\ B == S <: A & ... & S <: B
		rewritePred("{x ∣ x > 0} ⊆ A ∩ B", "{x ∣ x > 0} ⊆ A ∧ {x ∣ x > 0} ⊆ B");
		rewritePred("{x ∣ x > 0} ⊆ A ∩ B ∩ C",
				"{x ∣ x > 0} ⊆ A ∧ {x ∣ x > 0} ⊆ B ∧ {x ∣ x > 0} ⊆ C");
		
		
		// A \/ ... \/ B <<: S == A <<: S & ... & B <<: S
		// This rule is wrong and has been removed, no rewriting should occur.
		noRewritePred("A ∪ B ⊂ {x ∣ x > 0}");
		noRewritePred("A ∪ B ∪ C ⊂ {x ∣ x > 0}");

		
		// S <<: A /\ ... /\ B == S <<: A & ... & S <<: B
		// This rule is wrong and has been removed, no rewriting should occur.
		noRewritePred("{x ∣ x > 0} ⊂ A ∩ B");
		noRewritePred("{x ∣ x > 0} ⊂ A ∩ B ∩ C");

		
		// E : {} == false
		rewritePred("2 ∈ ∅", "⊥");
		rewritePred("FALSE ∈ ∅", "⊥");
		rewritePred("x + 2 ∈ ∅", "⊥");

		
		// A : {A} == true
		rewritePred("2 ∈ {2}", "⊤");
		rewritePred("x + 2 ∈ {x + 2}", "⊤");
		rewritePred("FALSE ∈ {FALSE}", "⊤");

		
		// B : {A, ..., B, ..., C} == true
		rewritePred("B ∈ {B, x + 2, C}", "⊤");
		rewritePred("B ∈ {x + 2, B, C}", "⊤");
		rewritePred("B ∈ {x + 2, C, B}", "⊤");
		rewritePred("B ∈ {B, x + 2, B, C}", "⊤");
		rewritePred("B ∈ {B, x + 2, C, B}", "⊤");
		rewritePred("B ∈ {x + 2, B, C, B}", "⊤");
		rewritePred("B ∈ {B, x + 2, B, C, B}", "⊤");


		// {A, ..., B, ..., B, ..., C} == {A, ..., B, ..., C}
		rewriteExpr("{x + 2 ∗ y, x + 2 ∗ y}", "{x + 2 ∗ y}");
		rewriteExpr("{x + 2 ∗ y, x + 2 ∗ y, E, F}", "{x + 2 ∗ y, E, F}");
		rewriteExpr("{x + 2 ∗ y, E, x + 2 ∗ y, F}", "{x + 2 ∗ y, E, F}");
		rewriteExpr("{x + 2 ∗ y, E, F, x + 2 ∗ y}", "{x + 2 ∗ y, E, F}");
		rewriteExpr("{E, x + 2 ∗ y, F, x + 2 ∗ y}", "{E, x + 2 ∗ y, F}");
		rewriteExpr("{E, F, x + 2 ∗ y, x + 2 ∗ y}", "{E, F, x + 2 ∗ y}");
		rewriteExpr("{E, x + 2 ∗ y, E, F, x + 2 ∗ y, F}", "{E, x + 2 ∗ y, F}");

		
		// E : {x | P(x)} == P(E)
		rewritePred("x ∈ {y ∣ y > 0 ∧ y < 2}", "x > 0 ∧ x < 2");

		// E : {x . P(x) | x} == P(E)
		rewritePred("n ∈ {x·x≥0∣x}", "n ≥ 0");
		rewritePred("∀n·n≥1 ⇒ n ∈ {x·x≥0∣x}", "∀n·n≥1 ⇒ n ≥ 0");
		
		// F : {x,y . P(x,y) | E(x,y) == #x,y . P(x,y) & E(x,y) = F
		rewritePred("n ∈ {x,y·x≥0∧y≥0∣x+y}", "∃x,y· (x≥ 0 ∧ y≥ 0) ∧ x+y = n");
		rewritePred("∀n·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥0∣x+y}",
				"∀n·n≥0 ⇒ (∃x,y· (x≥ 0 ∧ y≥ 0) ∧ x+y = n)");
		// One Point Rule applies
		rewritePred("∀n·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥0∣x}",
				"∀n·n≥0 ⇒ (∃y· (n ≥ 0 ∧ y≥ 0))");
		rewritePred("∀n,m·n≥0 ⇒ n ∈ {x,y·x≥0∧y≥m∣x}",
				"∀n,m·n≥0 ⇒ (∃y· (n ≥ 0 ∧ y≥ m))");
		// One Point Rule applies replacement on expression ('x=n' here)
		rewritePred("n ∈ {x·x=0∣x}", "n=0");
		// One Point Rule does not apply replacement on guard ('x=0' here)
		rewritePred("n ∈ {x·x=0∣x+1}", "∃x· x=0 ∧ x+1 = n");

		// Jean-Raymond Abrial's bug
		rewritePred("∃z·(l∈ {x,y·x>0 ∧ y>0 ∣ g(x+y)−g(x)−g(y)})∧l=z",
				"∃z·(∃x,y·(x>0∧y>0)∧g(x+y)−g(x)−g(y)=l)∧l=z");
		
		// S \ S == {}
		rewriteExpr("{y ∣ y > 0} ∖ {y ∣ y > 0}", "(∅ ⦂ ℙ(ℤ))");
		

		// {} \ S == {}
		rewriteExpr("∅ ∖ {y ∣ y > 0}", "(∅ ⦂ ℙ(ℤ))");


		// S \ {} == S
		rewriteExpr("{y ∣ y > 0} ∖ ∅", "{y ∣ y > 0}");

		
		// r~~ == r
		rewriteExpr("{x ↦ y ∣ x > 0 ∧ y < 2}∼∼", "{x ↦ y ∣ x > 0 ∧ y < 2}");
		

		// dom({x |-> a, ..., y |-> b}) == {x, ..., y}
		rewriteExpr("dom({x + 2 ↦ 3})", "{x + 2}");
		rewriteExpr("dom({x + 2 ↦ 3, 2 ↦ y})", "{x + 2, 2}");
		rewriteExpr("dom({x + 2 ↦ 3, 2 ↦ y, a ↦ b})", "{x + 2, 2, a}");

		
		// ran({x |-> a, ..., y |-> b}) == {a, ..., b}
		rewriteExpr("ran({x + 2 ↦ 3})", "{3}");
		rewriteExpr("ran({x + 2 ↦ 3, 2 ↦ y})", "{3, y}");
		rewriteExpr("ran({x + 2 ↦ 3, 2 ↦ y, a ↦ b})", "{3, y, b}");

		
		// (f <+ {E |-> F})(E) = F
		rewriteExpr("(f  {x + 2 ↦ 3})(x + 2)", "3");
		noRewriteExpr("(f  {2 ↦ 3}  g)(2)");
		if (level2AndHigher) {
			rewriteExpr("(f  {2 ↦ 3, 4 ↦ 5})(2)", "3");
		} else {
			noRewriteExpr("(f  {2 ↦ 3, 4 ↦ 5})(2)");
		}
 
		// E : {F} == E = F (if F is a single expression)
		rewritePred("x + 2 ∗ y ∈ {y + 2 ∗ x}", "x + 2 ∗ y = y + 2 ∗ x");

		
		// not(E : {F}) == not(E = F) (if F is a single expression)
		rewritePred("¬x + 2 ∗ y ∈ {y + 2 ∗ x}", "¬x + 2 ∗ y = y + 2 ∗ x");

		
		// {E} = {F} == E = F if E, F is a single expression
		rewritePred("{x + 2 ∗ y} = {y + 2 ∗ x}", "x + 2 ∗ y = y + 2 ∗ x");
		
		
		// not({E} = {F}) == not(E = F) if E, F is a single expression
		rewritePred("¬{x + 2 ∗ y} = {y + 2 ∗ x}", "¬x + 2 ∗ y = y + 2 ∗ x");

		
		// {x |-> a, ..., y |-> b}~  ==  {a |-> x, ..., b |-> y}
		rewriteExpr("{x + 2 ↦ 3}∼", "{3 ↦ x + 2}");
		rewriteExpr("{x + 2 ↦ 3, 2 ↦ y}∼", "{3 ↦ x + 2, y ↦ 2}");
		rewriteExpr("{x + 2 ↦ 3, 2 ↦ y, a ↦ b}∼", "{3 ↦ x + 2, y ↦ 2, b ↦ a}");
		

		// Typ = {} == false (where Typ is a type expression) is NOT done here
		noRewritePred("ℤ = ∅");
		if (level4AndHigher) {
			// However powerset rewriting has been added at level 4
			rewritePred("ℙ(ℤ) = ∅", "⊥");
		} else {
			noRewritePred("ℙ(ℤ) = ∅");
		}

		
		// {} = Typ == false (where Typ is a type expression) is NOT done here
		noRewritePred("∅ = ℤ");
		if (level4AndHigher) {
			// However powerset rewriting has been added at level 4
			rewritePred("∅ = ℙ(ℤ)", "⊥");
		} else {
			noRewritePred("∅ = ℙ(ℤ)");
		}
		

		// E : Typ == true (where Typ is a type expression) is NOT done here
		noRewritePred("E ∈ ℤ");

		
		// f(f~(E)) == E
		rewriteExpr("f(f∼(E))", "E", "f=S↔T");
		if (level2AndHigher) {
			rewriteExpr("{x + 2 ↦ 3}(({x + 2 ↦ 3}∼)(y + 2))", "3");
		} else {
			rewriteExpr("{x + 2 ↦ 3}(({x + 2 ↦ 3}∼)(y + 2))", "y + 2");
		}

		
		// f~(f(E)) == E
		rewriteExpr("f∼(f(E))", "E", "f=S↔T; E=S");
		if (level2AndHigher) {
			rewriteExpr("({x + 2 ↦ 3}∼)({x + 2 ↦ 3}(y + 2))", "x + 2");
		} else {
			rewriteExpr("({x + 2 ↦ 3}∼)({x + 2 ↦ 3}(y + 2))", "y + 2");
		}

		
		// {x |-> a, ..., y |-> b}({a |-> x, ..., b |-> y}(E)) = E
		rewriteExpr("{x ↦ a, y ↦ b}({a ↦ x, b ↦ y}(E))", "E", //
				"E=S; a=S; x=T");
		if (level2AndHigher) {
			rewriteExpr("{x + 2 ↦ 3}({3 ↦ x + 2}(y + 2))", "3");
			rewriteExpr("{x + 2 ↦ 3, y ↦ 2}({3 ↦ x + 2, 2 ↦ y}(y + 2))",
					"y + 2");
			rewriteExpr("{x + 2 ↦ 3, y ↦ 2, a ↦ b}({3 ↦ x + 2, 2 ↦ y, b ↦ a}(y + 2))",
					"y + 2");
		} else {
			rewriteExpr("{x + 2 ↦ 3}({3 ↦ x + 2}(y + 2))", "y + 2");
			rewriteExpr("{x + 2 ↦ 3, y ↦ 2}({3 ↦ x + 2, 2 ↦ y}(y + 2))",
					"y + 2");
			rewriteExpr("{x + 2 ↦ 3, y ↦ 2, a ↦ b}({3 ↦ x + 2, 2 ↦ y, b ↦ a}(y + 2))",
					"y + 2");
		}

		// p;...;{};...;q == {}
		rewriteExpr("f;(∅⦂T↔U)", "(∅⦂S↔U)", "f=S↔T");
		rewriteExpr("(∅⦂S↔T);f", "(∅⦂S↔U)", "f=T↔U");
		rewriteExpr("(∅⦂S↔T);f;g;h", "(∅⦂S↔W)",//
				"f=T↔U; g=U↔V; h=V↔W");
		rewriteExpr("f;(∅⦂T↔U);g;h", "(∅⦂S↔W)",//
				"f=S↔T; g=U↔V; h=V↔W");
		rewriteExpr("f;g;h;(∅⦂V↔W)", "(∅⦂S↔W)",//
				"f=S↔T; g=T↔U; h=U↔V");
		rewriteExpr("(∅⦂S↔T);f;(∅⦂U↔V);g;h", "(∅⦂S↔X)",//
				"f=T↔U; g=V↔W; h=W↔X");
		rewriteExpr("(∅⦂S↔T);f;g;h;(∅⦂W↔X)", "(∅⦂S↔X)",//
				"f=T↔U; g=U↔V; h=V↔W");
		rewriteExpr("f;(∅⦂T↔U);g;h;(∅⦂W↔X)", "(∅⦂S↔X)",//
				"f=S↔T; g=U↔V; h=V↔W");
		rewriteExpr("(∅⦂S↔T);f;(∅⦂U↔V);g;h;(∅⦂X↔Y)", "(∅⦂S↔Y)",//
				"f=T↔U; g=V↔W; h=W↔X");


		// p circ ... circ {} circ ... circ q == {}
		rewriteExpr("f∘(∅⦂S↔T)", "(∅⦂S↔U)", "f=T↔U");
		rewriteExpr("(∅⦂T↔U)∘f", "(∅⦂S↔U)", "f=S↔T");
		rewriteExpr("(∅⦂V↔W)∘h∘g∘f", "(∅⦂S↔W)",//
				"f=S↔T; g=T↔U; h=U↔V");
		rewriteExpr("h∘g∘(∅⦂T↔U)∘f", "(∅⦂S↔W)",//
				"f=S↔T; g=U↔V; h=V↔W");
		rewriteExpr("h∘g∘f∘(∅⦂S↔T)", "(∅⦂S↔W)",//
				"f=T↔U; g=U↔V; h=V↔W");
		rewriteExpr("(∅⦂W↔X)∘h∘g∘(∅⦂T↔U)∘f", "(∅⦂S↔X)",//
				"f=S↔T; g=U↔V; h=V↔W");
		rewriteExpr("h∘g∘(∅⦂U↔V)∘f∘(∅⦂S↔T)", "(∅⦂S↔X)",//
				"f=T↔U; g=V↔W; h=W↔X");
		rewriteExpr("(∅⦂W↔X)∘h∘g∘f∘(∅⦂S↔T)", "(∅⦂S↔X)",//
				"f=T↔U; g=U↔V; h=V↔W");
		rewriteExpr("(∅⦂X↔Y)∘h∘g∘(∅⦂U↔V)∘f∘(∅⦂S↔T)", "(∅⦂S↔Y)",//
				"f=T↔U; g=V↔W; h=W↔X");


		// U \ (U \ S) == S
		rewriteExpr("ℤ ∖ (ℤ ∖ {x ∣ x > 0})", "{x ∣ x > 0}");
		rewriteExpr("ℙ(ℤ) ∖ (ℙ(ℤ) ∖ ℙ({x ∣ x > 0}))", "ℙ({x ∣ x > 0})");
		
		
		// S \ U == {}
		rewriteExpr("S ∖ ℤ", "(∅ ⦂ ℙ(ℤ))");
		rewriteExpr("S ∖ (ℤ × ℤ)", "(∅ ⦂ ℙ(ℤ×ℤ))");

		
		// S \/ ... \/ U \/ ... \/ T == U
		rewriteExpr("ℤ ∪ S", "ℤ");
		rewriteExpr("S ∪ ℤ", "ℤ");
		rewriteExpr("ℤ ∪ S ∪ T ∪ U", "ℤ");
		rewriteExpr("S ∪ ℤ ∪ T ∪ U", "ℤ");
		rewriteExpr("S ∪ T ∪ U ∪ ℤ ", "ℤ");
		rewriteExpr("ℤ ∪ S ∪ ℤ ∪ T ∪ U", "ℤ");
		rewriteExpr("ℤ ∪ S ∪ T ∪ U ∪ ℤ", "ℤ");
		rewriteExpr("S ∪ ℤ ∪ T ∪ U ∪ ℤ", "ℤ");
		rewriteExpr("ℤ ∪ S ∪ ℤ ∪ T ∪ U ∪ ℤ", "ℤ");

		
		// S /\ ... /\ U /\ ... /\ T == S /\ ... /\ ... /\ T
		rewriteExpr("ℤ ∩ {x ∣ x > 0}", "{x ∣ x > 0}");
		rewriteExpr("{x ∣ x > 0} ∩ ℤ", "{x ∣ x > 0}");
		rewriteExpr("ℤ ∩ {x ∣ x > 0} ∩ S ∩ T", "{x ∣ x > 0} ∩ S ∩ T");
		rewriteExpr("{x ∣ x > 0} ∩ ℤ ∩ S ∩ T", "{x ∣ x > 0} ∩ S ∩ T");
		rewriteExpr("{x ∣ x > 0} ∩ S ∩ T ∩ ℤ", "{x ∣ x > 0} ∩ S ∩ T");
		rewriteExpr("ℤ ∩ {x ∣ x > 0} ∩ ℤ ∩ S ∩ T", "{x ∣ x > 0} ∩ S ∩ T");
		rewriteExpr("ℤ ∩ {x ∣ x > 0} ∩ S ∩ T ∩ ℤ", "{x ∣ x > 0} ∩ S ∩ T");
		rewriteExpr("{x ∣ x > 0} ∩ ℤ ∩ S ∩ T ∩ ℤ", "{x ∣ x > 0} ∩ S ∩ T");
		rewriteExpr("ℤ ∩ {x ∣ x > 0} ∩ ℤ ∩ S ∩ T ∩ ℤ ", "{x ∣ x > 0} ∩ S ∩ T");

		// r[∅] == ∅
		rewriteExpr("r[(∅ ⦂ ℙ(S))]", "(∅ ⦂ ℙ(T))", "r=ℙ(S×T)");
		rewriteExpr("(∅ ⦂ ℙ(S×T))[(∅ ⦂ ℙ(S))]", "(∅ ⦂ ℙ(T))");

		// ∅[A] == ∅
		rewriteExpr("(∅ ⦂ ℙ(S×T))[A]", "(∅ ⦂ ℙ(T))", "A=ℙ(S)");
		
		// dom({}) == {}
		rewriteExpr("dom((∅ ⦂ ℙ(S×T)))", "(∅ ⦂ ℙ(S))");
		
		// ran({}) == {}
		rewriteExpr("ran((∅ ⦂ ℙ(S×T)))", "(∅ ⦂ ℙ(T))");

		// (S ** {E})(x) == E
		rewriteExpr("(ℕ × {TRUE})(1)", "TRUE");
		rewriteExpr("(BOOL × {1})(TRUE)", "1");
	
		// r <+ ... <+ {} <+ ... <+ s = r <+ ... <+ s
		rewriteExpr("{1 ↦ 2}  ∅  {3 ↦ 4}", "{1 ↦ 2}  {3 ↦ 4}");
		
		// (%x . P | E)(y) and similar
		rewriteExpr("(λx·x∈ℤ∣x)(0)", "0");
		rewriteExpr("{x·x∈ℤ∣x↦x}(1)", "1");
		rewriteExpr("(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(1↦2)", "1+2");
		rewriteExpr("(λx·x∈ℤ×ℤ∣prj1(x))(1↦2)", "prj1(1↦2)");
		rewriteExpr(//
				"(λ(x↦y)↦((a↦b)↦(c ⦂ ℤ ))·"//
						+ "x∈ℤ∧y∈ℤ∧a∈ℤ∧b∈ℤ ∣"//
						+ "{m↦n∣m>y−x ∧ n>(b−a)∗c})((3↦5)↦((4↦8)↦2))",//
				"{m↦n∣m>5−3 ∧ n> (8−4)∗2}");
		rewritePred("∀x·x=ℕ⇒x=(λa↦b·a∈ℕ∧b∈ℕ∣{m∣m>a+b})(1↦2)",
				"∀x·x=ℕ⇒x={m∣m>1+2}");
		rewritePred("∀x·x=ℕ⇒x=(λa↦b·a∈ℕ∧b∈ℕ∣{m∣m>a+b})(0↦0)",
				"∀x·x=ℕ⇒x={m∣m>0}");
		// verify that no exception is thrown when no rewrite occurs
		noRewriteExpr("(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(w)", "w=ℤ×ℤ");
		// Rewriting fails as "x" is not a maplet
		if (level2AndHigher) {
			rewriteExpr("{x·x∈ℤ×ℤ∣x}(1)", "(ℤ×ℤ)(1)");
		} else {
			noRewriteExpr("{x·x∈ℤ×ℤ∣x}(1)");
		}
		// Rewriting fails as "pair" is not an explicit maplet
		noRewriteExpr("(λx↦y·x∈ℤ∧y∈ℤ∣x+y)(pair)");
		
		// destr(cons(a_1, ..., a_n))  ==  a_i   [i is the param index of the destructor]
		rewriteExpr("destr1(cons1(1))", "1");
		rewriteExpr("destr2_0(cons2(1, 2))", "1");
		rewriteExpr("destr2_1(cons2(1, 2))", "2");
		noRewriteExpr("destr2_0(cons1(1))");

	}
	
	@Test
	public void testBug2995930() {
		// Checks that internal lambda is conserved, and De Bruijn index are correct
		rewriteExpr("(λs·s⊆S∣(λx↦p·x∈s∧p⊆s∣p))(s)", "(λx↦p·x∈s∧p⊆s∣p)", "s=ℙ(S)");
		// Checks that external lambda disappear and x is instantiated
		rewriteExpr("(λx·x∈ℙ(ℕ) ∣ (λz·z∈ℕ ∣ z+z)[x])({1,2,3})", "(λz·z∈ℕ ∣ z+z)[{1,2,3}]");
		// Real example from Bug 2995930 with an argument containing a bound identifier.
		rewritePred("∀t⦂ℙ(S)·(λs⦂ℙ(S)·s⊆S∣(λx↦p·x∈s∧p⊆s∣p))(t) = a",
				"∀t⦂ℙ(S)·(λx↦p·x∈t∧p⊆t∣p) = a");
	}

	/**
	 * Ensures that bug 3025836: Rodin 1.3.1 prover is still unsound is fixed.
	 * Also adds similar test cases for completeness.
	 */
	@Test
	public void testBug3025836() {
		rewritePred("∀x,y,z·x∈ℤ ∧ y∈BOOL ∧ z∈BOOL ⇒ (λa·a∈ℤ ∣ a)(x)=0",
				"∀x,y,z·x∈ℤ ∧ y∈BOOL ∧ z∈BOOL ⇒ x=0");
		rewritePred("∀x⦂ℤ,y⦂ℙ(ℤ)·(λa·a∈ℤ∣y∪{a})(x)=A",
				"∀x⦂ℤ,y⦂ℙ(ℤ)·y∪{x}=A");
	}

	/**
	 * Tests for rewriting arithmetic formulas. 
	 */
	@Test
	public void testArithmetic() {
		// E + ... + 0 + ... + F == E + ... + ... + F
		rewriteExpr("0 + 0", "0");
		rewriteExpr("0 + (x + 2 ∗ y)", "x + 2 ∗ y");
		rewriteExpr("(x + 2 ∗ y) + 0", "x + 2 ∗ y");
		rewriteExpr("0 + (x + 2 ∗ y) + y", "x + 2 ∗ y + y");
		rewriteExpr("(x + 2 ∗ y) + 0 + y", "x + 2 ∗ y + y");
		rewriteExpr("(x + 2 ∗ y) + y + 0", "x + 2 ∗ y + y");
		rewriteExpr("0 + (x + 2 ∗ y) + 0 + y", "x + 2 ∗ y + y");
		rewriteExpr("0 + (x + 2 ∗ y) + y + 0", "x + 2 ∗ y + y");
		rewriteExpr("(x + 2 ∗ y) + 0 + y + 0", "x + 2 ∗ y + y");
		rewriteExpr("0 + (x + 2 ∗ y) + 0 + y + 0", "x + 2 ∗ y + y");
		
		
		// E - 0 == E
		rewriteExpr("(x + 2 ∗ y) − 0", "(x + 2 ∗ y)");

		
		// 0 - E == -E
		rewriteExpr("0 − (x + 2 ∗ y)", "−(x + 2 ∗ y)");
		rewriteExpr("0 − 1", "−(1)");
		rewriteExpr("−(1)", "−1");


		// -(-E) == E
		rewriteExpr("−(−(x + 2 ∗ y))", "x + 2 ∗ y");
		rewriteExpr("−(−1)", "1");
		rewriteExpr("−(−(1))", "1");
		
		
		// E - E == 0
		rewriteExpr("1 − 1", "0");
		rewriteExpr("(x + 2 ∗ y) − (x + 2 ∗ y)", "0");
		
		
		// E * ... * 1 * ... * F == E * ... * ... * F
		rewriteExpr("1 ∗ 1", "1");
		rewriteExpr("(x + 2 ∗ y) ∗ 1", "x + 2 ∗ y");
		rewriteExpr("1 ∗ (x + 2 ∗ y)", "x + 2 ∗ y");
		rewriteExpr("1 ∗ (x + 2 ∗ y) ∗ y", "(x + 2 ∗ y) ∗ y");
		rewriteExpr("(x + 2 ∗ y) ∗ 1 ∗ y", "(x + 2 ∗ y) ∗ y");
		rewriteExpr("(x + 2 ∗ y) ∗ y ∗ 1", "(x + 2 ∗ y) ∗ y");
		rewriteExpr("1 ∗ (x + 2 ∗ y) ∗ 1 ∗ y", "(x + 2 ∗ y) ∗ y");
		rewriteExpr("1 ∗ (x + 2 ∗ y) ∗ y ∗ 1", "(x + 2 ∗ y) ∗ y");
		rewriteExpr("(x + 2 ∗ y) ∗ 1 ∗ y ∗ 1", "(x + 2 ∗ y) ∗ y");
		rewriteExpr("1 ∗ (x + 2 ∗ y) ∗ 1 ∗ y ∗ 1", "(x + 2 ∗ y) ∗ y");

		
		// E * ... * 0 * ... * F == 0
		rewriteExpr("0 ∗ 0", "0");
		rewriteExpr("0 ∗ 1", "0");
		rewriteExpr("1 ∗ 0", "0");
		rewriteExpr("(x + 2 ∗ y) ∗ 0", "0");
		rewriteExpr("0 ∗ (x + 2 ∗ y)", "0");
		rewriteExpr("0 ∗ (x + 2 ∗ y) ∗ y", "0");
		rewriteExpr("(x + 2 ∗ y) ∗ 0 ∗ y", "0");
		rewriteExpr("(x + 2 ∗ y) ∗ y ∗ 0", "0");
		rewriteExpr("0 ∗ (x + 2 ∗ y) ∗ 0 ∗ y", "0");
		rewriteExpr("0 ∗ (x + 2 ∗ y) ∗ y ∗ 0", "0");
		rewriteExpr("(x + 2 ∗ y) ∗ 0 ∗ y ∗ 0", "0");
		rewriteExpr("0 ∗ (x + 2 ∗ y) ∗ 0 ∗ y ∗ 0", "0");


		// (-E) * (-F) == E * F
		rewriteExpr("(−(x + 2 ∗ y)) ∗ (−y)", "(x + 2 ∗ y) ∗ y");
		rewriteExpr("(−(x + 2 ∗ y)) ∗ (−2)", "(x + 2 ∗ y) ∗ 2");
		rewriteExpr("(−(x + 2 ∗ y)) ∗ (−(2))", "(x + 2 ∗ y) ∗ 2");
		rewriteExpr("(−2) ∗ (−(x + 2 ∗ y))", "2 ∗ (x + 2 ∗ y)");
		rewriteExpr("(−(2)) ∗ (−(x + 2 ∗ y))", "2 ∗ (x + 2 ∗ y)");
		rewriteExpr("(−(x + 2 ∗ y)) ∗ (−(2)) ∗ (−1)",
				"−((x + 2 ∗ y) ∗ 2)");
		rewriteExpr("−((x + 2 ∗ y) ∗ 2 ∗ 1)", "−((x + 2 ∗ y) ∗ 2)");
		

		// E / E == 1
		rewriteExpr("2 ÷ 2", "1");
		rewriteExpr("(x + 2 ∗ y) ÷ (x + 2 ∗ y)", "1");

		
		// E / 1 == E
		rewriteExpr("2 ÷ 1", "2");
		rewriteExpr("(x + 2 ∗ y) ÷ 1", "x + 2 ∗ y");

		
		// 0 / E == 0
		rewriteExpr("0 ÷ 2", "0");
		rewriteExpr("0 ÷ (x + 2 ∗ y)", "0");

		
		// (-E) /(-F) == E / F
		rewriteExpr("(−3) ÷ (−2)", "3 ÷ 2");
		rewriteExpr("(−x) ÷(−(x + 2 ∗ y))", "x ÷ (x + 2 ∗ y)");

		
		// (X * ... * E * ... * Y)/E == X * ... * Y
		rewriteExpr("((x + 2 ∗ y) ∗ 2) ÷  (x + 2 ∗ y)", "2");
		rewriteExpr("(2 ∗ (x + 2 ∗ y)) ÷  (x + 2 ∗ y)", "2");
		rewriteExpr("(2 ∗ (x + 2 ∗ y)) ÷  2", "x + 2 ∗ y");
		rewriteExpr("((x + 2 ∗ y) ∗ 2) ÷  2", "x + 2 ∗ y");
		rewriteExpr("(2 ∗ (x + 2 ∗ y) ∗ 2) ÷  2", "(x + 2 ∗ y) ∗ 2");
		rewriteExpr("(2 ∗ (x + 2 ∗ y) ∗ (x + 2 ∗ y)) ÷  (x + 2 ∗ y)", "2 ∗ (x + 2 ∗ y)");

		
		// E^1 == E
		rewriteExpr("2^1", "2");
		rewriteExpr("(−2)^1", "−2");
		rewriteExpr("(x + 2 ∗ y)^1", "x + 2 ∗ y");
		rewriteExpr("(−(x + 2 ∗ y))^1", "−(x + 2 ∗ y)");
		
		
		// E^0 == 1
		rewriteExpr("2^0", "1");
		rewriteExpr("(−2)^0", "1");
		rewriteExpr("(x + 2 ∗ y)^0", "1");
		rewriteExpr("(−(x + 2 ∗ y))^0", "1");


		// 1^E == 1
		rewriteExpr("1^2", "1");
		rewriteExpr("1^(−2)", "1");
		rewriteExpr("1^(x + 2 ∗ y)", "1");
		rewriteExpr("1^(−(x + 2 ∗ y))", "1");

		
		// -(i) == (-i) where i is a literal
		rewriteExpr("−(1)", "(−1)");
		
		
		// -(-i) == i where i is a literal
		rewriteExpr("−(−1)", "1");

		
		// i = j == true   or   i = j == false  (by computation)
		rewritePred("1 = 1", "⊤");
		rewritePred("1 = 2", "⊥");
		rewritePred("1 = −1", "⊥");
		rewritePred("−1 = −1", "⊤");
		rewritePred("−1 = −2", "⊥");
		rewritePred("−1 = 1", "⊥");
		

		// i <= j == true   or   i <= j == false  (by computation)
		rewritePred("1 ≤ 1", "⊤");
		rewritePred("1 ≤ 2", "⊤");
		rewritePred("1 ≤ −1", "⊥");
		rewritePred("−1 ≤ −1", "⊤");
		rewritePred("−1 ≤ −2", "⊥");
		rewritePred("−1 ≤ 1", "⊤");

		// i < j == true   or   i < j == false  (by computation)
		rewritePred("1 < 1", "⊥");
		rewritePred("1 < 2", "⊤");
		rewritePred("1 < −1", "⊥");
		rewritePred("−1 < −1", "⊥");
		rewritePred("−1 < −2", "⊥");
		rewritePred("−1 < 1", "⊤");

		// i >= j == true   or   i >= j == false  (by computation)
		rewritePred("1 ≥ 1", "⊤");
		rewritePred("1 ≥ 2", "⊥");
		rewritePred("1 ≥ −1", "⊤");
		rewritePred("−1 ≥ −1", "⊤");
		rewritePred("−1 ≥ −2", "⊤");
		rewritePred("−1 ≥ 1", "⊥");

		// i > j == true   or   i > j == false  (by computation)
		rewritePred("1 > 1", "⊥");
		rewritePred("1 > 2", "⊥");
		rewritePred("1 > −1", "⊤");
		rewritePred("−1 > −1", "⊥");
		rewritePred("−1 > −2", "⊤");
		rewritePred("−1 > 1", "⊥");
		
		// E <= E = true
		rewritePred("x + 2 ∗ y ≤ x + 2 ∗ y", "⊤");


		// E >= E = true
		rewritePred("x + 2 ∗ y ≥ x + 2 ∗ y", "⊤");

		// E < E = false
		rewritePred("x + 2 ∗ y < x + 2 ∗ y", "⊥");

		// E > E = false
		rewritePred("x + 2 ∗ y > x + 2 ∗ y", "⊥");

	}

	/**
	 * Non-regression test for multiplication and division by a negative
	 * literal.
	 */
	@Test
	public void testBug2706216() {
		rewriteExpr("d ∗ (−2)", "− (d ∗ 2)");
		rewriteExpr("d ∗ 2 ∗ (−2)", "− (d ∗ 2 ∗ 2)");
		// original problem
		rewriteExpr("v + d ∗ (−1)", "v + (− d)");
	}	

	/**
	 * Tests for rewriting finiteness predicates.
	 */
	@Test
	public void testFinite() {
		// finite({}) == true
		rewritePred("finite((∅ ⦂ ℙ(ℤ)))", "⊤");

		
		// finite({a, ..., b}) == true
		rewritePred("finite({TRUE})", "⊤");
		rewritePred("finite({TRUE, FALSE})", "⊤");
		rewritePred("finite({1, 2})", "⊤");
		rewritePred("finite({2})", "⊤");
		
		// finite(S \/ ... \/ T) == finite(S) & ... & finite(T)
		rewritePred("finite({x ∣ x > 0} ∪ {y ∣ y < 0})",
				"finite({x ∣ x > 0}) ∧ finite({y ∣ y < 0})");
		rewritePred(
				"finite({x ∣ x > 0} ∪ {y ∣ y < 0} ∪ {x ∣ x =  0})",
				"finite({x ∣ x > 0}) ∧ finite({y ∣ y < 0}) ∧ finite({x ∣ x = 0})");

		
		// finite(POW(S)) == finite(S)
		rewritePred("finite(ℙ({x ∣ x > 0}))", "finite({x ∣ x > 0})");

		
		// finite(S ** T) == S = {} or T = {} or (finite(S) & finite(T))
		rewritePred(
				"finite({x ∣ x > 0} × {x ∣ x < 0})",
				"{x ∣ x > 0} = ∅ ∨ {x ∣ x < 0} = ∅ ∨ (finite({x ∣ x > 0}) ∧ finite({x ∣ x < 0}))");
		
		
		// finite(r~) == finite(r)
		rewritePred("finite(r∼)", "finite(r)", "r=S↔T");
		// In level 2, expression "r~" can be rewritten earlier
		if (level2AndHigher) {
			rewritePred("finite((ℤ × BOOL)∼)",
					"BOOL = ∅ ∨ ℤ = ∅ ∨ (finite(BOOL) ∧ finite(ℤ))");
			rewritePred("finite({x ↦ y ∣ x > 0 ∧ y < 2}∼)",
					"finite({x,y · x>0 ∧ y<2 ∣ y↦x})");
		} else {
			rewritePred("finite((ℤ × BOOL)∼)", "finite(ℤ × BOOL)");
			rewritePred("finite({x ↦ y ∣ x > 0 ∧ y < 2}∼)",
					"finite({x ↦ y ∣ x > 0 ∧ y < 2})");
		}

		// finite(a..b) == true
		rewritePred("finite(a‥b)", "⊤");

	}


	/**
	 * Tests for rewriting cardinality expressions.
	 */
	@Test
	public void testCardinality() {
		// card({}) == 0
		rewriteExpr("card((∅ ⦂ ℙ(S)))", "0");

		
		// card({E}) == 1
		rewriteExpr("card({x + 1})", "1");
		
		
		// card(POW(S)) == 2^card(S)
		rewriteExpr("card(ℙ({x ∣ x > 0}))", "2^(card({x ∣ x >0}))");
		
		
		// card(S) = 0  ==  S = {}
		rewritePred("card({x ∣ x > 0}) = 0", "{x ∣ x > 0} = ∅");

		
		// 0 = card(S)  ==  S = {}
		rewritePred("0 = card({x ∣ x > 0})", "{x ∣ x > 0} = ∅");

		
		// not(card(S) = 0)  ==  not(S = {})
		rewritePred("¬card({x ∣ x > 0}) = 0", "¬{x ∣ x > 0} = ∅");

		
		// not(0 = card(S))  ==  not(S = {})
		rewritePred("¬0 = card({x ∣ x > 0})", "¬{x ∣ x > 0} = ∅");

		
		// card(S) > 0  ==  not(S = {})
		rewritePred("card({x ∣ x > 0}) > 0", "¬{x ∣ x > 0} = ∅");

		
		// 0 < card(S)  ==  not(S = {})
		rewritePred("0 < card({x ∣ x > 0})", "¬{x ∣ x > 0} = ∅");
		

		// card(S) = 1 == #x.S = {x}
		rewritePred("card({x ∣ x > 0}) = 1", "∃y·{x ∣ x > 0} = {y}");

		
		// 1 = card(S) == #x.S = {x}
		rewritePred("1 = card({x ∣ x > 0})", "∃y·{x ∣ x > 0} = {y}");


		// card(S(1) \/ ... \/ S(n)) == card(S(1)) + ... card(S(2)) -
		//	                            - ... 
		//                              + (-1)^(n-1)card(S(1) /\ ... card(S(n)))
		if (level2AndHigher) {
			rewriteExpr("card(A ∪ B)", "card(A) + card(B) − card(A ∩ B)", //
					"A=ℙ(S); B=ℙ(S)");
			rewriteExpr(
					"card(A ∪ B ∪ C)", //
					"card(A) + card(B)  + card(C) − (card(A ∩ B) + card(A ∩ C) + card(B ∩ C)) + card(A ∩ B ∩ C)", //
					"A=ℙ(S)");
			rewriteExpr(
					"card(A ∪ B ∪ C ∪ D)",//
					"card(A) + card(B) + card(C) + card(D) − "
							+ "(card(A ∩ B) + card(A ∩ C) + card(A ∩ D) + card(B ∩ C) + card(B ∩ D) + card(C ∩ D)) + "
							+ "(card(A ∩ B ∩ C) + card(A ∩ B ∩ D) + card(A ∩ C ∩ D) + card(B ∩ C ∩ D)) − "
							+ "card(A ∩ B ∩ C ∩ D)", //
					"A=ℙ(S)");
		} else {
			rewriteExpr(
					"card({x ∣ x ∈ BOOL} ∪ S)",
					"card({x ∣ x ∈ BOOL}) + card(S) − card({x ∣ x ∈ BOOL} ∩ S)");
			rewriteExpr(
					"card({x ∣ x ∈ BOOL} ∪ S ∪ T)",
					"card({x ∣ x ∈ BOOL}) + card(S) + card(T) − "
							+ "(card({x ∣ x ∈ BOOL} ∩ S) + card({x ∣ x ∈ BOOL} ∩ T) + card(S ∩ T)) + "
							+ "card({x ∣ x ∈ BOOL} ∩ S ∩ T)");
			rewriteExpr(
					"card({x ∣ x ∈ BOOL} ∪ S ∪ T ∪ R)",
					"card({x ∣ x ∈ BOOL}) + card(S) + card(T) + card(R) − "
							+ "(card({x ∣ x ∈ BOOL} ∩ S) + card({x ∣ x ∈ BOOL} ∩ T) + card({x ∣ x ∈ BOOL} ∩ R) + card(S ∩ T) + card(S ∩ R) + card(T ∩ R)) + "
							+ "(card({x ∣ x ∈ BOOL} ∩ S ∩ T) + card({x ∣ x ∈ BOOL} ∩ S ∩ R) + card({x ∣ x ∈ BOOL} ∩ T ∩ R) + card(S ∩ T ∩ R)) − "
							+ "card({x ∣ x ∈ BOOL} ∩ S ∩ T ∩ R)");
		}
	}

	/**
	 * Tests for rewriting boolean predicates.
	 */
	@Test
	public void testBoolean() {
		// bool(false) == FALSE
		rewriteExpr("bool(⊥)", "FALSE");
		
		// bool(true) == TRUE
		rewriteExpr("bool(⊤)", "TRUE");
		
		// TRUE = bool(P) == P
		rewritePred("TRUE = bool(x = 1)", "x = 1");
		
		// bool(P) = TRUE == P
		rewritePred("bool(x = 1) = TRUE", "x = 1");
		
		// FALSE = bool(P) == not(P)
		rewritePred("FALSE = bool(x = 1)", "¬x = 1");
		
		// bool(P) = FALSE == not(P)
		rewritePred("bool(x = 1) = FALSE", "¬x = 1");
	}

	@Test
	public void testCond() throws Exception {
		
		// COND(true, E_1, E_2) == E_1
		rewriteExpr("COND(⊤,1,2)", "1");

		// COND(false, E_1, E_2) == E_2
		rewriteExpr("COND(⊥,1,2)", "2");

		// COND(C, E, E) == E
		rewriteExpr("COND(x=1,2,2)", "2");
		
	}

	@Test
	public void bug3158594() throws Exception {
		rewritePred("0 ↦ 0 ∈ {x ∣ ∃ y· y∗y < 0 ∧ y = 1 ÷ 0}",
				"∃y·y∗y<0∧y=1 ÷ 0");
		noRewritePred("∃y·y∗y<0∧y=1 ÷ 0");
	}

	/**
	 * Ensures that rules SIMP_EMPTY_PARTITION and SIMP_SINGLE_PARTITION are
	 * implemented correctly.
	 */
	@Test
	public void fr294() {
		rewritePred("partition(S)", "S = ∅", "S=ℙ(T)", level4AndHigher);
		rewritePred("partition(S1, S2)", "S1 = S2", "S1=ℙ(T)", level4AndHigher);
		noRewritePred("partition(S1, S2, S3)", "S1=ℙ(T)");
	}

}
