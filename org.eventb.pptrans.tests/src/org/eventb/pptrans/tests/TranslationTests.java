/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2 (BR8, IR39, IR43, IR44)
 *     Systerel - added pred and succ (IR47, IR48)
 *     Systerel - added sequent translation
 *     Systerel - added test for IR3' and bug #3489973
 *     Systerel - adapted tests after ER10 rule fixing (see bug #3495675)
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.pptrans.Translator.isInGoal;
import static org.eventb.pptrans.Translator.reduceToPredicateCalulus;
import static org.eventb.pptrans.Translator.Option.expandSetEquality;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.pptrans.Translator.Option;
import org.junit.Test;


/**
 * Ensures that the translator from set-theory to predicate calculus works
 * correctly.
 * 
 * 
 * @author Matthias Konrad
 */

public class TranslationTests extends AbstractTranslationTests {

	protected static final Type S = ff.makeGivenType("S");
	protected static final Type T = ff.makeGivenType("T");
	protected static final Type U = ff.makeGivenType("U");
	protected static final Type V = ff.makeGivenType("V");
	protected static final Type X = ff.makeGivenType("X");
	protected static final Type Y = ff.makeGivenType("Y");
	protected static final ITypeEnvironmentBuilder defaultTe;
	static {
		defaultTe = ff.makeTypeEnvironment();
		defaultTe.addGivenSet("S");
		defaultTe.addGivenSet("T");
		defaultTe.addGivenSet("U");
		defaultTe.addGivenSet("V");
	}

	private static void doTest(String input, String expected, boolean transformExpected) {
		doTest(input, expected, transformExpected, defaultTe);
	}

	private static Predicate translate(Predicate goal, FormulaFactory factory,
			Option[] options) {
		final ISimpleSequent sequent = SimpleSequents.make(NONE, goal, factory);
		final ISimpleSequent result = reduceToPredicateCalulus(sequent, options);
		final ITrackedPredicate[] tpreds = result.getPredicates();
		if (tpreds.length > 0) {
			return tpreds[0].getPredicate();
		} else {
			return factory.makeLiteralPredicate(BFALSE, null);
		}
	}

	private static void doTest(String input, String expected,
			boolean transformExpected, ITypeEnvironment inputTypenv,
			Option... options) {
		final ITypeEnvironmentBuilder te = inputTypenv.makeBuilder();
		final Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		if (transformExpected) {
			pexpected = translate(pexpected, ff, options);
		}
		doTest(pinput, pexpected, options);
	}
	
	@SuppressWarnings("deprecation")
	private static void doTest(Predicate input, Predicate expected,
			Option... options) {
		assertTypeChecked(input);
		assertTypeChecked(expected);

		final Predicate actual = translate(input, ff, options);

		assertTypeChecked(actual);
		assertTrue("Result not in goal: " + actual, isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	

	static ITypeEnvironment br_te = mTypeEnvironment(
			"s=ℙ(S); t=ℙ(S); v=ℙ(S); w=ℙ(S); p=S↔T; q=S↔T; e1=S; e2=S; e3=S",
			ff);

	/**
	 *  Tests for BR1
	 */
	@Test
	public void testBR1_simple() {
		
		doTest( "s⊆t",
				"s ∈ ℙ(t)", true, br_te);
	}
	
	@Test
	public void testBR1_recursion() {

		doTest( "s∪v ⊆ t∪w",
				"s∪v ∈ ℙ(t∪w)", true, br_te);
	}
	
	/**
	 *  Tests for BR2
	 */
	@Test
	public void testBR2_simple() {

		doTest( "s ⊈ t",
				"¬(s ∈ ℙ(t))", true, br_te);
	}
	
	@Test
	public void testBR2_recursion() {
		
		doTest( "s∪v ⊈ t∪w",
				"¬(s∪v ∈ ℙ(t∪w))", true, br_te);
	}
	
	/**
	 *  Tests for BR3
	 */
	@Test
	public void testBR3_simple() {

		doTest( "s⊂t",
				"s ∈ ℙ(t) ∧ ¬(t ∈ ℙ(s))", true, br_te);
	}

	@Test
	public void testBR3_recursion() {

		doTest( "s∪v ⊂ t∪w",
				"s∪v ∈ ℙ(t∪w) ∧ ¬(t∪w ∈ ℙ(s∪v))", true, br_te);
	}
	
	/**
	 *  Tests for BR4
	 */
	@Test
	public void testBR4_simple() {

		doTest( "s ⊄ t",
				"¬(s ∈ ℙ(t)) ∨ t ∈ ℙ(s)", true, br_te);
	}

	@Test
	public void testBR4_recursion() {

		doTest( "s∪v ⊄ t∪w",
				"¬(s∪v ∈ ℙ(t∪w)) ∨ t∪w ∈ ℙ(s∪v)", true, br_te);
	}
	
	/**
	 *  Tests for BR5
	 */
	@Test
	public void testBR5_simple() {

		doTest( "s ≠ t",
				"¬(s = t)", false, br_te);
		doTest( "x ≠ 0",
				"¬(x = 0)", false, br_te);
		doTest( "x ≠ TRUE",
				"¬(x = TRUE)", false, br_te);
	}

	@Test
	public void testBR5_recursion() {

		doTest( "s∪v ≠ t∪w",
				"¬(s∪v = t∪w)", true, br_te);
	}
	
	/**
	 *  Tests for BR6
	 */
	@Test
	public void testBR6_simple() {

		doTest( "x ∉ s",
				"¬(x ∈ s)", false, br_te);
	}

	@Test
	public void testBR6_recursion() {

		doTest( "s∪v ∉ ℙ(t∪w)",
				"¬(s∪v ∈  ℙ(t∪w))", true, br_te);
	}

	/**
	 * Tests for BR7
	 */
	@Test
	public void testBR7_simple() {

		doTest( "finite(s)",
				"∀a·∃b,f·f∈(s↣a‥b)", true, br_te);
	}
	
	@Test
	public void testBR7_recursive() {

		doTest( "finite(ℙ(s∪t))",
				"∀a·∃b,f·f∈(ℙ(s∪t)↣a‥b)", true, br_te);
	}
	
	@Test
	public void testBR7_complex() {
		
		doTest( "∀x·∃y·y=t∨finite({s∪x∪y})",
				"∀x·∃y·y=t∨(∀a·∃b,f·f∈({s∪x∪y}↣a‥b))", true, br_te);
	}
	
	@Test
	public void testBR8_simple() throws Exception {
		doTest( "partition(s, t, v)",
				"(s=t∪v)∧(t∩v=∅)", true, br_te);
		doTest( "partition(s, {e1}, {e2}, {e3})",
				"(s={e1,e2,e3})∧e1≠e2∧e1≠e3∧e2≠e3", true, br_te);
	}

	@Test
	public void testBR8_recursive() throws Exception {
		doTest( "partition(ℙ(s∪t))",
				"ℙ(s∪t) = ∅", true, br_te);
	}

	static ITypeEnvironment er_te = mTypeEnvironment(
			"f=S↔T; s=ℙ(S); t=ℙ(S); v=ℙ(S); w=ℙ(S); x=S; y=T; a=S; b=T; is=ℙ(ℤ); it=ℙ(ℤ);",
			ff);

	/**
	 * Tests for ER1
	 */
	@Test
	public void testER1_simple() {
		
		doTest( "f(a) = f(a)", 
				"⊤", false, er_te);
	}
	
	/**
	 * Tests for ER2
	 */
	@Test
	public void testER2_simple() {
		
		doTest( "x↦y = a↦b", 
				"x=a ∧ y=b", false, er_te);
	}
	
	@Test
	public void testER2_recursive() {
		doTest( "s∪v↦v∪t = t∪s↦v∪w", 
				"s∪v=t∪s ∧ v∪t=v∪w", true, er_te);
	}
	
	/**
	 * Tests for ER3
	 */
	@Test
	public void testER3_simple() {
		
		doTest( "bool(n>0) = bool(n>2)", 
				"n>0 ⇔ n>2", true, er_te);
	}
	
	@Test
	public void testER3_recursive() {

		doTest( "bool(1∈{1}) = bool(1∈{1,2})", 
				"1∈{1} ⇔ 1∈{1,2}", true, er_te);
	}
	
	/**
	 * Tests for ER4
	 */
	@Test
	public void testER4_simple() {
		
		doTest( "bool(n>0) = TRUE", 
				"n>0", true, er_te);
	}
	
	@Test
	public void testER4_recursive() {

		doTest( "bool(1∈{1}) = TRUE", 
				"1∈{1}", true, er_te);
	}

	/**
	 * Tests for ER5
	 */
	@Test
	public void testER5_simple() {
		
		doTest( "bool(n>0) = FALSE", 
				"¬(n>0)", true, er_te);
	}
	
	@Test
	public void testER5_recursive() {

		doTest( "bool(1∈{1}) = FALSE", 
				"¬(1∈{1})", true, er_te);
	}
	
	/**
	 * Tests for ER6
	 */
	@Test
	public void testER6_simple() {
		
		doTest( "x = FALSE", 
				"¬(x=TRUE)", true);
	}
	
	@Test
	public void testER6_recursive() {

		doTest( "x = bool(1∈{1})", 
				"x = TRUE ⇔ 1∈{1}", true);
	}
	
	/**
	 * Tests for ER7
	 */
	@Test
	public void testER7_simple() {
		
		doTest( "x = bool(n>0)", 
				"x = TRUE ⇔ n>0", true);
	}
	
	@Test
	public void testER7_recursive() {

		doTest( "x = bool(1∈{1})", 
				"x = TRUE ⇔ 1∈{1}", true);
	}

	/**
	 * Tests for ER8
	 */
	@Test
	public void testER8_simple() {
		
		doTest( "y = f(x)", 
				"x↦y ∈ f", true, er_te);
	}
	
	@Test
	public void testER8_recursive() {
		ITypeEnvironment te = mTypeEnvironment(
				"f=ℙ(S)↔ℙ(T); s=ℙ(S); t=ℙ(T); v=ℙ(S); w=ℙ(T)", ff);

		doTest( "t∪w = f(s∪v)", 
				"s∪v↦t∪w ∈ f", true, te);
	}

	/**
	 * Tests for ER9
	 */
	@Test
	public void testER9_simple_inGoal() {
		
		doTest( "s = t", 
				"s = t", false, er_te);
		doTest( "s = t",
				"∀x·x∈s ⇔ x∈t", false, er_te, expandSetEquality);
	}
	
	@Test
	public void testER9_simple() {
		
		doTest( "is = ℕ", 
				"∀x·x∈is ⇔ x∈ℕ", true, er_te);
	}
	
	@Test
	public void testER9_recursive() {
		doTest( "s∪v = t∪w", 
				"∀x·x∈s∪v ⇔ x∈t∪w", true, er_te);
	}
	
	/**
	 * Tests for ER10
	 */
	@Test
	public void testER10_simple() {
		
		doTest( "n = card(s)", 
				"0≤n∧(∃f·f ∈ s⤖1‥n)", true, er_te);
	}
	
	@Test
	public void testER10_recursive() {

		doTest( "n = card(s∪t)", 
				"0≤n∧(∃f·f ∈ s∪t⤖1‥n)", true, er_te);
	}
	
	@Test
	public void testER10_complex() {
		doTest( "∀m,d·m = card(s∪d)", 
				"∀m,d· 0≤m∧(∃f·f ∈ s∪d⤖1‥m)", true, er_te);
	}

	/**
	 * Tests for ER11
	 */
	@Test
	public void testER11_simple() {
		
		doTest( "n = min(is)", 
				"n∈is ∧ n≤min(is)", true, er_te);
	}
	
	@Test
	public void testER11_recursive() {

		doTest( "n = min(is∪it)", 
				"n∈is∪it ∧ n≤min(is∪it)", true, er_te);
	}

	/**
	 * Tests for ER12
	 */
	@Test
	public void testER12_simple() {
		
		doTest( "n = max(is)", 
				"n∈is ∧ max(is)≤n", true, er_te);
	}
	
	@Test
	public void testER12_recursive() {

		doTest( "n = max(is∪it)", 
				"n∈is∪it ∧ max(is∪it)≤n", true, er_te);
	}
	
	private static ITypeEnvironment cr_te = mTypeEnvironment("s=ℙ(ℤ); t=ℙ(ℤ)",
			ff);

	private static ITypeEnvironment cr_ste = mTypeEnvironment("s=ℙ(BOOL); t=ℙ(ℤ)",ff);

	/**
	 * Tests for CR1
	 */
	@Test
	public void testCR1_simple() {
		
		doTest( "a < min(s)", 
				"∀x·x∈s ⇒ a < x", true, cr_te);
	}
	
	@Test
	public void testCR1_recursive() {

		doTest( "min(t) < min(s∪t)", 
				"∀x·x∈s∪t ⇒ min(t) < x", true, cr_te);
	}	
	
	@Test
	public void testCR1_complex() {
		
		doTest( "∀s·∃t·min(t) < min(s∪t)",
				"∀s·∃t·∀x·x∈s∪t ⇒ min(t) < x", true, cr_te);
	}
	
	/**
	 * Tests for CR2
	 */
	@Test
	public void testCR2_simple() {
		
		doTest( "max(s) < a", 
				"∀x·x∈s ⇒ x < a", true, cr_te);
	}
	
	@Test
	public void testCR2_recursive() {

		doTest( "max(s∪t) < max(t)", 
				"∀x·x∈s∪t ⇒ x < max(t)", true, cr_te);
	}	
	
	@Test
	public void testCR2_complex() {
		
		doTest( "∀s·∃t·max(s∪t) < max(t)",
				"∀s·∃t·∀x·x∈s∪t ⇒ x < max(t)", true, cr_te);
	}

	/**
	 * Tests for CR3
	 */
	@Test
	public void testCR3_simple() {
		
		doTest( "min(s) < a", 
				"∃x·x∈s ∧ x < a", true, cr_te);
	}
	
	@Test
	public void testCR3_recursive() {

		doTest( "min(s∪t) < min(t)", 
				"∀x·x∈t ⇒ (∃y·y∈s∪t ∧ y < x)", true, cr_te);
	}	
	
	@Test
	public void testCR3_complex() {
		
		doTest( "∀s·∃t·min(s∪t) < min(t)",
				"∀s·∃t·∀x·x∈t ⇒ (∃y·y∈s∪t ∧ y < x)", true, cr_te);
	}

	/**
	 * Tests for CR4
	 */
	@Test
	public void testCR4_simple() {
		
		doTest( "a < max(s)", 
				"∃x·x∈s ∧ a < x", true, cr_te);
	}
	
	@Test
	public void testCR4_recursive() {

		doTest( "max(t) < max(s∪t)", 
				"∀x·x∈t ⇒ (∃y·y∈s∪t ∧ x<y)", true, cr_te);
	}	
	
	@Test
	public void testCR4_complex() {
		
		doTest( "∀s·∃t·max(t) < max(s∪t)",
				"∀s·∃t·∀x·x∈t ⇒ (∃y·y∈s∪t ∧ x<y)", true, cr_te);
	}
	
	/**
	 * Tests for CR5
	 */
	@Test
	public void testCR5_simple() {
		
		doTest( "a > b", 
				"b < a", true, cr_te);
	}
	
	@Test
	public void testCR5_recursive() {

		doTest( "min(t) > max(s)", 
				"∀x·x∈t ⇒ (∀y·y∈s ⇒ y < x)", true, cr_te);
	}	
	
	/**
	 * Tests for IR1
	 */
	@Test
	public void testIR1_simple1() {
		ITypeEnvironment te = mTypeEnvironment("e=S", ff);

		doTest( "e ∈ S", 
				"⊤", false, te);
	}
	
	/**
	 * Tests for IR2
	 */
	@Test
	public void testIR2_simple() {
		
		doTest( "e∈ℙ(t)", 
				"∀x·x∈e⇒x∈t", true, cr_te);
	}
	
	@Test
	public void testIR2_complex() {

		doTest( "∀f,t·e∪f∈ℙ(s∪t)", 
				"∀f,t·∀x·x∈e∪f ⇒ x∈s∪t", true, cr_te);
	}	
	
	/**
	 * Tests for IR2'
	 */
	@Test
	public void testIR2prime_simple() {
		
		doTest( "e∈s↔t", 
				"∀xs,xt·xs↦xt∈e ⇒ xs∈s ∧ xt∈t", true, cr_ste);
	}
	
	@Test
	public void testIR2prime_complex() {

		doTest( "∀f⦂ℤ↔ℤ,t·e;f ∈ s↔t", 
				"∀f⦂ℤ↔ℤ,t·∀xs,xt·xs↦xt∈e;f ⇒ xs∈s ∧ xt∈t", true, cr_ste);
	}	
	
	/**
	 * Tests for IR3
	 */
	@Test
	public void testIR3_simple() {
		
		doTest( "1 ∈ s",
				"∃x·x=1 ∧ x∈s", false);
	}
	
	@Test
	public void testIR3_recursive() {

		doTest( "s∪t ∈ v",
				"∃x·x=s∪t ∧ x∈v", 
				true, 
				mTypeEnvironment("s=ℙ(S)", ff));
		
	}

	@Test
	public void testIR3_complex() {

		doTest( "∀t,w·s∪t ∈ w",
				"∀t,w·∃x·x=s∪t ∧ x∈w", 
				true, 
				mTypeEnvironment("s=ℙ(S)", ff));
		
	}

	@Test
	public void testIR3_additional_1() {
		
		doTest( "a↦1 ∈ S",
				"∃x·x=1 ∧ a↦x∈S", 
				false, 
				mTypeEnvironment("S=BOOL↔ℤ", ff));
	}
	
	@Test
	public void testIR3_additional_2() {
		
		doTest( "a↦1↦2 ∈ S",
				"∃x1,x2·x1=1∧x2=2 ∧ a↦x1↦x2∈S", 
				false, 
				mTypeEnvironment("S=BOOL×ℤ↔ℤ", ff));
	}
	
	@Test
	public void testIR3_additional_3() {
		
		doTest( "a↦b↦f(10)∈S",
				"∃x·x=f(10) ∧ a↦b↦x∈S", 
				true, 
				mTypeEnvironment("S=BOOL×ℤ↔ℤ", ff));
	}

	@Test
	public void testIR3_additional_4() {
		
		doTest( "f(a)  ∈ S",
				"∃x·x=f(a) ∧ x∈S", 
				true, 
				mTypeEnvironment("f=T↔U", ff));
	}
	
	@Test
	public void testIR3_additional_5() {
		
		doTest( "f(a)  ∈ S",
				"∃x1,x2·x1↦x2=f(a) ∧ x1↦x2∈S ", 
				true,
				 mTypeEnvironment("f=T↔U×V", ff));
	}

	/**
	 * Tests for IR3'
	 */
	@Test
	public void testIR3p_simple() {
		doTest("r(c) ∈ A×B", //
				"∀a,b· c ↦ (a ↦ b) ∈ r ⇒ a ∈ A ∧ b ∈ B", //
				false, //
				mTypeEnvironment("r=S↔T×U",ff));
	}
	
	/**
	 * Tests for IR4
	 */
	@Test
	public void testIR4_simple() {
		
		doTest( "e∈ℕ", 
				"0≤e", 
				false, 
				mTypeEnvironment("e=ℤ", ff));
	}
	
	/**
	 * Tests for IR5
	 */
	@Test
	public void testIR5_simple() {
		
		doTest( "e∈ℕ1", 
				"0<e", 
				false, 
				mTypeEnvironment("e=ℤ", ff));
	}
	
	/**
	 * Tests for IR6
	 */
	@Test
	public void testIR6_simple() {

		doTest( "e ∈ {x·a<x∣f}",
				"∃x·a<x ∧ e=f", 
				false, 
				mTypeEnvironment("e=ℤ", ff));
	}
	
	@Test
	public void testIR6_recursive() {

		doTest( "e∈{x·x∈{1}∣f∪g}",
				"∃x·x∈{1}∧e=f∪g", 
				true, 
				mTypeEnvironment("e=ℙ(S)", ff));
	}

	@Test
	public void testIR6_complex() {

		doTest( "∀f,b·e∈{x·x∈{1, b}∣f∪g}",
				"∀f,b·∃x·x∈{1, b}∧e=f∪g", 
				true, 
				mTypeEnvironment("e=ℙ(S)", ff));
	}

	/**
	 * Tests for IR7
	 */
	@Test
	public void testIR7_simple() {

		doTest( "e ∈ (⋂x·a<x∣f)",
				"∀x·a<x ⇒ e∈f ", 
				false, 
				mTypeEnvironment("e=ℤ", ff));
	}
	
	@Test
	public void testIR7_recursive() {

		doTest( "e ∈ (⋂x·x∈{1}∣f∪g)",
				"∀x·x∈{1} ⇒ e∈f∪g", 
				true, 
				mTypeEnvironment("e=S", ff));
	}

	@Test
	public void testIR7_complex() {

		doTest( "∀f,b·e ∈ (⋂x·x∈{1, b}∣f∪g)",
				"∀f,b·∀x·x∈{1, b} ⇒ e∈f∪g", 
				true, 
				mTypeEnvironment("e=S", ff));
	}
	
	/**
	 * Tests for IR8
	 */
	@Test
	public void testIR8_simple() {

		doTest( "e ∈ (⋃x·a<x∣f)",
				"∃x·a<x ∧ e∈f ", 
				false, 
				mTypeEnvironment("e=ℤ", ff));
	}
	
	@Test
	public void testIR8_recursive() {

		doTest( "e ∈ (⋃x·x∈{1}∣f∪g)",
				"∃x·x∈{1} ∧ e∈f∪g", 
				true, 
				mTypeEnvironment("e=S", ff));
	}

	@Test
	public void testIR8_complex() {

		doTest( "∀f,b·e ∈ (⋃x·x∈{1, b}∣f∪g)",
				"∀f,b·∃x·x∈{1, b} ∧ e∈f∪g", 
				true, 
				mTypeEnvironment("e=S", ff));
	}
	
	/**
	 * Tests for IR9
	 */
	@Test
	public void testIR9_simple() {

		doTest( "e ∈ union(s)",
				"∃x·x∈s ∧ e∈x", 
				false, 
				mTypeEnvironment("e=S", ff));
	}
	
	@Test
	public void testIR9_recursive() {

		doTest( "e ∈ union(s∪t)",
				"∃x·x∈s∪t ∧ e∈x", 
				true, 
				mTypeEnvironment("e=S", ff));
	}

	@Test
	public void testIR9_complex() {

		doTest( "∀t·e ∈ union(s∪t)",
				"∀t·∃x·x∈s∪t ∧ e∈x", 
				true, 
				mTypeEnvironment("e=S", ff));
	}
	
	/**
	 * Tests for IR10
	 */
	@Test
	public void testIR10_simple() {

		doTest( "e ∈ inter(s)",
				"∀x·x∈s ⇒ e∈x", 
				false, 
				mTypeEnvironment("e=S", ff));
	}
	
	@Test
	public void testIR10_recursive() {

		doTest( "e ∈ inter(s∪t)",
				"∀x·x∈s∪t ⇒ e∈x", 
				true, 
				mTypeEnvironment("e=S", ff));
	}

	@Test
	public void testIR10_complex() {

		doTest( "∀t·e ∈ inter(s∪t)",
				"∀t·∀x·x∈s∪t ⇒ e∈x", 
				true, 
				mTypeEnvironment("e=S", ff));
	}
	
	/**
	 * Tests for IR11
	 */
	@Test
	public void testIR11_simple1() {

		doTest( "e∈∅",
				"⊥", 
				false,
				 mTypeEnvironment("e=S", ff));
	}
	
	@Test
	public void testIR11_simple2() {

		doTest( "e∈{}",
				"⊥", 
				false,
				 mTypeEnvironment("e=S", ff));
	}
	
	/**
	 * Tests for IR12
	 */
	
	ITypeEnvironment te_ir12 = mTypeEnvironment("e=T; r=S↔T ; w=ℙ(S)", ff);
	
	@Test
	public void testIR12_simple() {

		doTest( "e ∈ r[w]", 
				"∃x·x∈w∧x↦e∈r", 
				false, 
				te_ir12);
	}
	
	@Test
	public void testIR12_recursive() {

		doTest( "e ∈ r[w∪t]", 
				"∃x·x∈w∪t∧x↦e∈r", 
				true, 
				te_ir12);
	}

	@Test
	public void testIR12_complex() {

		doTest( "∀t·e ∈ r[w∪t]", 
				"∀t·∃x·x∈w∪t∧x↦e∈r", 
				true, 
				te_ir12);
	}
	
	/**
	 * Tests for IR13
	 */
	
	ITypeEnvironment te_ir13 = mTypeEnvironment("e=T; f=ℙ(S)↔ℙ(T); w=ℙ(S)", ff);

	@Test
	public void testIR13_simple() {

		doTest( "e ∈ f(w)", 
				"∃x·w↦x∈f ∧ e∈x", 
				false, 
				te_ir13);
	}
	
	@Test
	public void testIR13_recursive() {

		doTest( "e ∈ f(w∪t)", 
				"∃x·w∪t↦x∈f ∧ e∈x", 
				true, 
				te_ir13);
	}

	@Test
	public void testIR13_complex() {

		doTest( "∀t·e ∈ f(w∪t)", 
				"∀t·∃x·w∪t↦x∈f ∧ e∈x", 
				true, 
				te_ir13);
	}
	
	/**
	 * Tests for IR14
	 */
	ITypeEnvironment te_ir14 = mTypeEnvironment("r=S↔T; e=T", ff);

	@Test
	public void testIR14_simple() {

		doTest( "e ∈ ran(r)", 
				"∃x·x↦e ∈ r", 
				false, 
				te_ir14);
	}
	
	@Test
	public void testIR14_recursive() {

		doTest( "e ∈ ran(r∪t)", 
				"∃x·x↦e ∈ r∪t", 
				true, 
				te_ir14);
	}

	@Test
	public void testIR14_complex() {

		doTest( "∀t·e ∈ ran(r∪t)", 
				"∀t·∃x·x↦e ∈ r∪t", 
				true, 
				te_ir14);
	}

	/**
	 * Tests for IR15
	 */
	ITypeEnvironment te_ir15 = mTypeEnvironment("r=S↔T; e=S", ff);

	@Test
	public void testIR15_simple() {

		doTest( "e ∈ dom(r)", 
				"∃x·e↦x ∈ r", 
				false, 
				te_ir15);
	}
	
	@Test
	public void testIR15_recursive() {

		doTest( "e ∈ dom(r∪t)", 
				"∃x·e↦x ∈ r∪t", 
				true, 
				te_ir15);
	}

	@Test
	public void testIR15_complex() {

		doTest( "∀t·e ∈ dom(r∪t)", 
				"∀t·∃x·e↦x ∈ r∪t", 
				true, 
				te_ir15);
	}
	
	/**
	 * Tests for IR16
	 */
	ITypeEnvironment te_ir16 = mTypeEnvironment("a=S; s=ℙ(S);", ff);

	@Test
	public void testIR16_simple() {

		doTest( "e ∈ {a}", 
				"e = a", 
				false, 
				te_ir16);
	}
	
	@Test
	public void testIR16_recursive() {

		doTest( "e∈{s,s∪t,t}", 
				"e=s ∨ e=s∪t ∨ e = t", 
				true, 
				te_ir16);
	}
	
	/**
	 * Tests for IR17
	 */
	ITypeEnvironment te_ir17 = mTypeEnvironment("s=ℙ(S); e=ℙ(S)", ff);

	@Test
	public void testIR17_simple() {

		doTest( "e ∈ ℙ1(s)", 
				"e ∈ ℙ(s) ∧ (∃x·x ∈ e)", 
				true, 
				te_ir17);
	}
	
	@Test
	public void testIR17_recursive() {

		doTest( "e ∈ ℙ1(r∪t)", 
				"e ∈ ℙ(r∪t) ∧ (∃x·x ∈ e)", 
				true, 
				te_ir17);
	}

	@Test
	public void testIR17_complex() {

		doTest( "∀t·e ∈ ℙ1(r∪t)", 
				"∀t·e ∈ ℙ(r∪t) ∧ (∃x·x ∈ e)", 
				true, 
				te_ir17);
	}

	/**
	 * Tests for IR18
	 */
	@Test
	public void testIR18_simple() {

		doTest( "e ∈ a‥b", 
				"a ≤ e ∧ e ≤ b", 
				false, 
				mTypeEnvironment());
	}
	
	/**
	 * Tests for IR19
	 */
	ITypeEnvironment te_ir19 = mTypeEnvironment("s=ℙ(S); t=ℙ(S)", ff);

	@Test
	public void testIR19_simple() {

		doTest( "e ∈ s ∖ t", 
				"e∈s ∧ ¬(e∈t)", 
				false, 
				te_ir19);
	}
	
	@Test
	public void testIR19_recursive() {

		doTest( "e ∈ (s∪v) ∖ (t∪w)", 
				"e∈s∪v ∧ ¬(e∈t∪w)", 
				true, 
				te_ir19);
	}
	
	/**
	 * Tests for IR20
	 */
	ITypeEnvironment te_ir20 = mTypeEnvironment("s1=ℙ(S); s2=ℙ(S); s3=ℙ(S)", ff);

	@Test
	public void testIR20_simple() {

		doTest( "e ∈ s1 ∩ s2 ∩ s3", 
				"e∈s1 ∧ e∈s2 ∧ e∈s3", 
				false, 
				te_ir20);
	}
	
	@Test
	public void testIR20_recursive() {

		doTest( "e ∈ (s1∪t1) ∩ (s2∪t2) ∩ (s3∪t3)", 
				"e∈s1∪t1 ∧ e∈s2∪t2 ∧ e∈s3∪t3", 
				true, 
				te_ir20);
	}
		
	/**
	 * Tests for IR21
	 */
	ITypeEnvironment te_ir21 = mTypeEnvironment("s1=ℙ(S); s2=ℙ(S); s3=ℙ(S)", ff);

	@Test
	public void testIR21_simple() {

		doTest( "e ∈ s1 ∪ s2 ∪ s3", 
				"e∈s1 ∨ e∈s2 ∨ e∈s3", 
				false, 
				te_ir21);
	}
	
	@Test
	public void testIR21_recursive() {

		doTest( "e ∈ s1∪t1 ∪ s2∪t2 ∪ s3∪t3", 
				"e∈s1∪t1 ∨ e∈s2∪t2 ∨ e∈s3∪t3", 
				true, 
				te_ir21);
	}

	ITypeEnvironment te_irRels = mTypeEnvironment("s=ℙ(S); t=ℙ(S)", ff);

	/**
	 * Tests for IR23
	 */
	@Test
	public void testIR23_simple() {

		doTest( "e ∈ st", 
				"e∈s↔t ∧ s⊆dom(e)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR23_recursive() {

		doTest( "e ∈ s∪vt∪w", 
				"e∈s∪v↔t∪w ∧ s∪v⊆dom(e)", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR24
	 */
	@Test
	public void testIR24_simple() {

		doTest( "e ∈ st", 
				"e∈s↔t ∧ t⊆ran(e)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR24_recursive() {

		doTest( "e ∈ s∪vt∪w", 
				"e∈s∪v↔t∪w ∧ t∪w⊆ran(e)", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR25
	 */
	@Test
	public void testIR25_simple() {

		doTest( "e ∈ st", 
				"e∈st ∧ t⊆ran(e)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR25_recursive() {

		doTest( "e ∈ s∪vt∪w", 
				"e∈s∪vt∪w ∧ t∪w⊆ran(e)", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR26
	 */
	@Test
	public void testIR26_simple() {

		doTest( "e ∈ s⤖t", 
				"e∈s↠t ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR26_recursive() {

		doTest( "e ∈ s∪v⤖t∪w", 
				"e∈s∪v↠t∪w ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c)", 
				true, 
				te_irRels);
	}

	@Test
	public void testIR26_complex() {

		doTest( "∀v,w·e ∈ s∪v⤖t∪w", 
				"∀v,w·(e∈s∪v↠t∪w ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c))", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR27
	 */
	@Test
	public void testIR27_simple() {

		doTest( "e ∈ s↠t", 
				"e∈s→t ∧ t⊆ran(e)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR27_recursive() {

		doTest( "e ∈ s∪v↠t∪w", 
				"e∈s∪v→t∪w ∧ t∪w⊆ran(e)", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR28
	 */
	@Test
	public void testIR28_simple() {

		doTest( "e ∈ s⤀t", 
				"e∈s⇸t ∧ t⊆ran(e)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR28_recursive() {

		doTest( "e ∈ s∪v⤀t∪w", 
				"e∈s∪v⇸t∪w ∧ t∪w⊆ran(e)", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR29
	 */
	@Test
	public void testIR29_simple() {

		doTest( "e ∈ s↣t", 
				"e∈s→t ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR29_recursive() {

		doTest( "e ∈ s∪v↣t∪w", 
				"e∈s∪v→t∪w ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c)", 
				true, 
				te_irRels);
	}

	@Test
	public void testIR29_complex() {

		doTest( "∀v,w·e ∈ s∪v↣t∪w", 
				"∀v,w·(e∈s∪v→t∪w ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c))", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR30
	 */
	@Test
	public void testIR30_simple() {

		doTest( "e ∈ s⤔t", 
				"e∈s⇸t ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR30_recursive() {

		doTest( "e ∈ s∪v⤔t∪w", 
				"e∈s∪v⇸t∪w ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR30_complex() {

		doTest( "∀v,w·e ∈ s∪v⤔t∪w", 
				"∀v,w·(e∈s∪v⇸t∪w ∧ (∀a,b,c·(b↦a∈e∧c↦a∈e)⇒b=c))", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR31
	 */
	@Test
	public void testIR31_simple() {

		doTest( "e ∈ s→t", 
				"e∈s⇸t ∧ s⊆dom(e)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR31_recursive() {

		doTest( "e ∈ s∪v→t∪w", 
				"e∈s∪v⇸t∪w ∧ s∪v⊆dom(e)", 
				true, 
				te_irRels);
	}

	/**
	 * Tests for IR32
	 */
	@Test
	public void testIR32_simple() {

		doTest( "e ∈ s⇸t", 
				"e∈s↔t ∧(∀a,b,c·(a↦b∈e∧a↦c∈e)⇒b=c)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR32_recursive() {

		doTest( "e ∈ s∪v⇸t∪w", 
				"e∈s∪v↔t∪w ∧ (∀a,b,c·(a↦b∈e∧a↦c∈e)⇒b=c)", 
				true, 
				te_irRels);
	}
	
	@Test
	public void testIR32_complex() {

		doTest( "∀v,w·e ∈ s∪v⇸t∪w", 
				"∀v,w·(e∈s∪v↔t∪w ∧ (∀a,b,c·(a↦b∈e∧a↦c∈e)⇒b=c))", 
				true, 
				te_irRels);
	}

	ITypeEnvironment te_irEF = mTypeEnvironment(
			"e=S; f=T; s=ℙ(S); t=ℙ(T); r=S↔T; q=S↔T", ff);

	/**
	 * Tests for IR33
	 */
	@Test
	public void testIR33_simple() {

		doTest( "e↦f ∈ s×t", 
				"e∈s ∧ f∈t", 
				true, 
				te_irEF);
	}
	
	@Test
	public void testIR33_complex() {

		doTest( "e↦f ∈ (s∪v)×(t∪w)", 
				"e∈s∪v ∧ f∈t∪w", 
				true, 
				te_irEF);
	}
	
	/**
	 * Tests for IR34
	 */
	@Test
	public void testIR34_simple() {
		doTest( "e↦f ∈ qr", 
				"e↦f∈dom(r)⩤q ∨ e↦f∈r", 
				true, 
				te_irEF);
	}

	@Test
	public void testIR34_recursive() {
		doTest( "e↦f ∈ (q∪v)(r∪w)", 
				"e↦f∈dom(r∪w)⩤(q∪v) ∨ e↦f∈r∪w", 
				true, 
				te_irEF);
	}

	@Test
	public void testIR34_additional() {
		doTest("e↦f∈r1r2r3",
				"e↦f∈(dom(r2)∪dom(r3))⩤r1 ∨ e↦f∈dom(r3)⩤r2 ∨ e↦f∈r3", true,
				mTypeEnvironment("E=S; F=T; r1=S↔T; r2=S↔T; r3=S↔T", ff));
 	}
	
	/**
	 * Tests for IR35
	 */
	@Test
	public void testIR35_simple() {
		doTest ("e↦f ∈ r⩥t", 
				"e↦f∈r ∧ ¬(f∈t)", 
				true, 
				te_irEF);
	}

	@Test
	public void testIR35_recursive() {
		doTest ("e↦f ∈ (r∪r2)⩥(t∪t2)", 
				"e↦f∈r∪r2 ∧ ¬(f∈t∪t2)", 
				true, 
				te_irEF);
	}
	
	/**
	 * Tests for IR36
	 */
	@Test
	public void testIR36_simple() {

		doTest( "e↦f ∈ s⩤r", 
				"e↦f∈ r ∧¬(e∈s)", 
				true, 
				te_irEF);
	}
	
	@Test
	public void testIR36_recursive() {

		doTest( "e↦f ∈ (s∪s2)⩤(r∪r2)", 
				"e↦f∈ r∪r2 ∧¬(e∈s∪s2)", 
				true, 
				te_irEF);
	}
	
	/**
	 * Tests for IR37
	 */
	@Test
	public void testIR37_simple() {

		doTest( "e↦f∈r▷t", 
				"e↦f∈r∧f∈t", 
				true, 
				te_irEF);
	}
	
	@Test
	public void testIR37_recursive() {

		doTest( "e↦f∈(r∪r2)▷(t∪t2)", 
				"e↦f∈r∪r2∧f∈t∪t2", 
				true, 
				te_irEF);
	}

	/**
	 * Tests for IR38
	 */
	@Test
	public void testIR38_simple() {

		doTest( "e↦f∈s◁r", 
				"e↦f∈r∧e∈s", true, 
				te_irEF);
	}
	
	@Test
	public void testIR38_recursive() {

		doTest( "e↦f∈(s∪s2)◁(r∪r2)", 
				"e↦f∈r∪r2∧e∈s∪s2", true, 
				te_irEF);
	}

	/**
	 * Tests for IR39
	 */
	ITypeEnvironment te_ir39 = mTypeEnvironment("e=S; f=S; s=ℙ(S)", ff);
			
	@Test
	public void testIR39_simple() {

		doTest( "e↦f∈id",
				"e=f", 
				true, 
				te_ir39);
	}

	@Test
	public void testIR39_recursive() {
		doTest( "e↦f∈(sus2)◁id",
				"e=f∧e∈(sus2)", 
				true, 
				te_ir39);
	}

	/**
	 * Tests for IR40
	 */
	ITypeEnvironment te_ir40 = mTypeEnvironment(
			"e=S; f=T; p=S↔U; q=U↔T; r1=S↔U; r2=U↔V; r3=V↔T", ff);

	@Test
	public void testIR40_simple() {

		doTest( "e↦f ∈ p;q",
				"∃x·e↦x∈p ∧ x↦f∈q", 
				true, 
				te_ir40);
	}

	@Test
	public void testIR40_recursive() {

		doTest( "e↦f ∈ (p∪p1);(q∪q1)",
				"∃x·e↦x∈p∪p1 ∧ x↦f∈q∪q1", 
				true, 
				te_ir40);
	}
	
	@Test
	public void testIR40_complex() {

		doTest( "∀p1,q1·e↦f ∈ (p∪p1);(q∪q1)",
				"∀p1,q1·∃x·e↦x∈p∪p1 ∧ x↦f∈q∪q1", 
				true, 
				te_ir40);
	}

	@Test
	public void testIR40_additional() {

		doTest( "e↦f∈r1;r2;r3",
				"∃x1,x2·e↦x1∈r1 ∧ x1↦x2∈r2 ∧ x2↦f∈r3", 
				true, 
				te_ir40);
	}
	
	/**
	 * Tests for IR41
	 */
	ITypeEnvironment te_ir41 = mTypeEnvironment("e=S; f=T; p=U↔T; q=S↔U", ff);
	
	@Test
	public void testIR41_simple() {
		
		doTest( "e↦f∈p∘q", 
				"e↦f∈q;p", 
				true, 
				te_ir41);
	}
	
	@Test
	public void testIR41_recursive() {
		
		doTest( "e↦f∈(p∪p1)∘(q∪q1)", 
				"e↦f∈(q∪q1);(p∪p1)", 
				true, 
				te_ir41);
	}
	
	/**
	 * Tests for IR42
	 */
	@Test
	public void testIR42_simple() {

		doTest( "f↦e∈(r∼)", 
				"e↦f∈r", 
				true, 
				te_irEF);
	}
	
	@Test
	public void testIR42_recursive() {

		doTest( "f↦e∈((r∪r1)∼)", 
				"e↦f∈r∪r1", 
				true, 
				te_irEF);
	}
	
	/**
	 * Tests for IR43
	 */
	ITypeEnvironment te_ir43 = mTypeEnvironment("e=S; f=T; g=S; r=S↔T", ff);
	
	@Test
	public void testIR43_simple() {

		doTest( "(e↦f)↦g ∈ prj1", 
				"e=g", 
				true, 
				te_ir43);
	}
	
	@Test
	public void testIR43_recursive() {
		doTest( "(e↦f)↦g ∈ (r∪r1)◁prj1", 
				"e=g∧(e ↦ f∈r∨e ↦ f∈r1)", 
				true, 
				te_ir43);
	}
	
	/**
	 * Tests for IR44
	 */
	ITypeEnvironment te_ir44 = mTypeEnvironment("e=S; f=T; g=T; r=S↔T", ff);
	
	@Test
	public void testIR44_simple() {

		doTest( "(e↦f)↦g ∈ prj2", 
				"f=g",  
				true, 
				te_ir44);
	}
	
	@Test
	public void testIR44_recursive() {
		doTest( "(e↦f)↦g ∈ (r∪r1)◁prj2", 
				"f=g∧(e ↦ f∈r∨e ↦ f∈r1)", 
				true, 
				te_ir44);
	}
	
	/**
	 * Tests for IR45
	 */
	ITypeEnvironment te_ir45 = mTypeEnvironment("e=S; f=T; g=ℙ(T); r=S↔T", ff);
	
	@Test
	public void testIR45_simple() {

		doTest( "e↦(f↦g) ∈ p⊗q", 
				"e↦f∈p ∧ e↦g∈q", 
				true, 
				te_ir45);
	}
	
	@Test
	public void testIR45_recursive() {

		doTest( "e↦(f↦g) ∈ (p∪p1)⊗(q∪q1)", 
				"e↦f∈p∪p1 ∧ e↦g∈q∪q1", 
				true, 
				te_ir45);
	}
	
	/**
	 * Tests for IR46
	 */
	ITypeEnvironment te_ir46 = mTypeEnvironment(
			"e=S; f=T; g=ℙ(S); h=ℙ(T); p=S↔ℙ(S); q=T↔ℙ(T)", ff);
	
	@Test
	public void testIR46_simple() {

		doTest( "(e↦f)↦(g↦h) ∈ p∥q", 
				"e↦g∈p ∧ f↦h∈q", 
				true, 
				te_ir46);
	}
	
	@Test
	public void testIR46_recursive() {

		doTest( "(e↦f)↦(g↦h) ∈ (p∪p1)∥(q∪q1)", 
				"e↦g∈p∪p1 ∧ f↦h∈q∪q1", 
				true, 
				te_ir46);
	}
	
	/**
	 * Tests for IR47 and IR48
	 */
	ITypeEnvironment te_ir47_48 = mTypeEnvironment("e=ℤ; f=ℤ; foo=ℤ↔ℤ", ff);

	@Test
	public void testIR47_simple() {
		doTest( "e↦f ∈ pred", 
				"e = f + 1", 
				true, 
				te_ir47_48);
	}

	@Test
	public void testIR47_recursive() {
		doTest( "e↦f ∈ ℕ ◁ pred", 
				"e = f + 1 ∧ 0 ≤ e", 
				true, 
				te_ir47_48);
	}

	@Test
	public void testIR47_complex() {
		doTest( "e+1↦f+2 ∈ pred", 
				"e + 1 = (f + 2) + 1", 
				true, 
				te_ir47_48);
		doTest( "foo(e)↦foo(f) ∈ pred", 
				"foo(e) = foo(f) + 1", 
				true, 
				te_ir47_48);
	}

	@Test
	public void testIR48_simple() {
		doTest( "e↦f ∈ succ", 
				"f = e + 1", 
				true, 
				te_ir47_48);
	}
	
	@Test
	public void testIR48_recursive() {
		doTest( "e↦f ∈ ℕ ◁ succ", 
				"f = e + 1 ∧ 0 ≤ e", 
				true, 
				te_ir47_48);
	}

	@Test
	public void testIR48_complex() {
		doTest( "e+1↦f−2 ∈ succ", 
				"f − 2 = (e + 1) + 1", 
				true, 
				te_ir47_48);
		doTest( "foo(e)↦foo(f) ∈ succ", 
				"foo(f) = foo(e) + 1", 
				true, 
				te_ir47_48);
	}

	@Test
	public void testFALSE_1() {
		doTest( "b = FALSE", 
				"¬(b = TRUE)", 
				false, 
				mTypeEnvironment());
	}
	
	@Test
	public void testFALSE_2() {
		doTest( "FALSE = b", 
				"¬(b = TRUE)", 
				false, 
				mTypeEnvironment());
	}
	
	@Test
	public void testFALSE_3() {
		doTest( "TRUE = FALSE", 
				"¬⊤", 
				false, 
				mTypeEnvironment());
	}
	
	@Test
	public void testFALSE_4() {
		doTest( "FALSE = TRUE", 
				"¬⊤", 
				false, 
				mTypeEnvironment());
	}
	
	@Test
	public void testFALSE_5() {
		doTest( "FALSE = FALSE", 
				"⊤", 
				false, 
				mTypeEnvironment());
	}
	
	@Test
	public void testFALSE_6() {
		doTest( "FALSE ∈ S", 
				"∃x· ¬ x = TRUE ∧ x ∈ S", 
				false, 
				mTypeEnvironment());
	}
	
	@Test
	public void testFALSE_7() {
		doTest("FALSE ↦ a ∈ S", "∃x· ¬ x = TRUE ∧ x ↦ a ∈ S", false,
				mTypeEnvironment("a=T", ff));
	}
	
	@Test
	public void testFALSE_8() {
		doTest("a ↦ FALSE ∈ S", "∃x· ¬ x = TRUE ∧ a ↦ x ∈ S", false,
				mTypeEnvironment("a=T", ff));
	}
	
	@Test
	public void testFALSE_9() {
		doTest("a ↦ (FALSE ↦ b) ∈ S", "∃x· ¬ x = TRUE ∧ a ↦ (x ↦ b) ∈ S",
				false, mTypeEnvironment("a=T; b=U", ff));
	}
	
	/**
	 * Ensures that bug #3489973: "NPE in pp trans" is fixed (original predicate
	 * from the bug report).
	 */
	@Test
	public void testBug3489973() {
		doTest("prj2(r(a ↦ b)) ∈ T", //
				"∃aa·(∀a1,b1·a ↦ b ↦ (a1 ↦ b1) ∈ r  ⇒  b1 = aa) ∧ aa ∈ T", //
				true, //
				mTypeEnvironment("r=ℤ×ℤ ↔ ℤ×ℤ", ff));
	}

	/**
	 * Ensures that bug #3489973: "NPE in pp trans" is fixed (reduced predicate
	 * found while investigating).
	 */
	@Test
	public void testBug3489973Reduced() {
		doTest("r(a)↦b ∈ prj2", //
				"∀r1,r2· a ↦ (r1 ↦ r2) ∈ r ⇒ r2 = b", //
				true, //
				mTypeEnvironment("r=S ↔ T×U", ff));
	}

	/**
	 * Ensures that a functional image that hides a maplet is properly
	 * translated when occurring in an equality predicate.
	 */
	@Test
	public void testBug3489973Equal() {
		doTest("a ↦ b = r(c)", //
				"c ↦ (a ↦ b) ∈ r", //
				false, //
				mTypeEnvironment("r=S ↔ T×U", ff));
	}

	/**
	 * Ensure that mathematical extensions get discarded by the translation.
	 */
	@Test
	public void testMathExtension() {
		final ISimpleSequent sequent = make(DT_FF, "p = dt");
		final ISimpleSequent expected = make(DT_FF, null);
		assertEquals(expected, reduceToPredicateCalulus(sequent));
	}

	/**
	 * Ensures that all predicates of a sequent get translated.
	 */
	@Test
	public void testSequent() {
		final ISimpleSequent sequent = make("1↦2 ∈ succ", "1↦3 ∈ succ");
		final ISimpleSequent expected = make("2 = 1 + 1", "3 = 1 + 1");
		assertEquals(expected, reduceToPredicateCalulus(sequent));
	}
	
}
