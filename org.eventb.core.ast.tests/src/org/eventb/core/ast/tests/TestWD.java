/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - added additional acceptance tests + improved result
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.ExtendedFormulas.EFF;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.internal.core.ast.wd.WDComputer;
import org.junit.Test;

/**
 * Unit and acceptance tests for the computation of WD lemmas.
 * 
 * @author Stefan Hallerstede
 */
public class TestWD extends AbstractTests {


	static ITypeEnvironment defaultTEnv = mTypeEnvironment(
			"x=ℤ; y=ℤ; A=ℙ(ℤ); B=ℙ(ℤ); f=ℤ↔ℤ; Y=ℙ(BOOL); S=ℙ(S)", ff);

	private static abstract class TestFormula<T extends Formula<T>> {

		final ITypeEnvironment typenv;
		final FormulaFactory factory;
		final T input;
		final Predicate originalPredicate;
		final Predicate simplifiedPredicate;

		TestFormula(ITypeEnvironment env, String in, String exp, String imp) {
			this.typenv = env;
			this.factory = env.getFormulaFactory();
			this.input = parse(in);
			this.originalPredicate = parsePredicate(exp, env);
			this.simplifiedPredicate = parsePredicate(imp, env);
		}

		@Test 
		public void test() {
			assertCorrect(originalPredicate, getNonSimplifiedWD());
			assertCorrect(simplifiedPredicate, getSimplifiedWD());
		}

		private Predicate getNonSimplifiedWD() {
			final WDComputer wdComputer = new WDComputer(factory);
			return wdComputer.getWDLemma(input);
		}

		private Predicate getSimplifiedWD() {
			return input.getWDPredicate();
		}

		private void assertCorrect(Predicate expected, Predicate actual) {
			assertTrue("Ill-formed WD predicate", actual.isWellFormed());
			assertTrue("Untyped WD predicate", actual.isTypeChecked());
			assertEquals(expected, actual);
		}

		public abstract T parse(String image);
	}

	private static class TestPredicate extends TestFormula<Predicate> {

		TestPredicate(ITypeEnvironment env, String in, String exp, String imp) {
			super(env, in, exp, imp);
		}

		@Override
		public Predicate parse(String image) {
			return parsePredicate(image, typenv);
		}

	}

	private static class TestAssignment extends TestFormula<Assignment> {

		TestAssignment(String in, String exp, String imp, FormulaFactory factory) {
			super(defaultTEnv, in, exp, imp);
		}

		@Override
		public Assignment parse(String image) {
			return parseAssignment(image, typenv);
		}

	}

	private static void assertWDLemma(String in, String expected) {
		assertWDLemma(defaultTEnv, in, expected);
	}

	private static void assertWDLemma(String in, String expected,
			String improvedExpected) {
		assertWDLemma(defaultTEnv, in, expected, improvedExpected);
	}

	private static void assertWDLemma(ITypeEnvironment env, String in,
			String expected) {
		assertWDLemma(env, in, expected, expected);
	}

	private static void assertWDLemma(ITypeEnvironment env, String in,
			String expected, String improvedExpected) {
		final TestPredicate test = new TestPredicate(env, in, expected,
				improvedExpected);
		test.test();
	}

	private static void assertWDLemmaAssignment(String in, String expected) {
		assertWDLemmaAssignment(in, expected, expected);
	}

	private static void assertWDLemmaAssignment(String in, String expected,
			String improvedExpected) {
		final TestAssignment test = new TestAssignment(in, expected,
				improvedExpected, ff);
		test.test();
	}

	@Test 
	public void testWD() {
		assertWDLemma("x≠y ∧ y=1", "⊤");
		assertWDLemma("x+y+x+1=0 ⇒ y<x", "⊤");
		assertWDLemma("x+1=0 ∨ x<y", "⊤");
		assertWDLemma("(∃x \u00b7 0<x ⇒ (∀y \u00b7 y+x=0))", "⊤");
		assertWDLemma("(B×Y)(x) ∈ Y", "x∈dom(B × Y) ∧ B × Y ∈ ℤ ⇸ BOOL");
		assertWDLemma(
				"x=f(f(y))", //
				"y∈dom(f) ∧ f ∈ ℤ ⇸ ℤ ∧ f(y)∈dom(f) ∧ f ∈ ℤ ⇸ ℤ",
				"y∈dom(f) ∧ f ∈ ℤ ⇸ ℤ ∧ f(y)∈dom(f)");
		assertWDLemma("(x÷y=y) ⇔ (y mod x=0)", "y≠0 ∧ 0 ≤ y ∧ 0 < x");
		assertWDLemma("∀z \u00b7 x^z>y", "∀z \u00b7 0≤x ∧ 0≤z");
		assertWDLemma("card(A)>x", "finite(A)");
		assertWDLemma("inter({A,B}) ⊆ A∩B", "{A,B}≠∅");
		assertWDLemma("(λ m↦n \u00b7 m>n \u2223 y)(1↦x) = y",
				"1 ↦ x∈dom(λm ↦ n\u00b7m>n ∣ y) "
						+ "∧ (λm ↦ n\u00b7m>n ∣ y) ∈ (ℤ×ℤ) ⇸ ℤ");
		assertWDLemma("{m,n \u00b7 m=f(n) \u2223 m↦n}[A] ⊂ B",
				"∀n \u00b7 n∈dom(f) ∧ f ∈ ℤ ⇸ ℤ");
		assertWDLemma("{f(n)↦m \u2223 x=n ∧ y+x=m ∧ f ∈ ℤ→A} = A×B",
				"∀f,n,m \u00b7 x=n ∧ y+x=m ∧ f ∈ ℤ→A ⇒ n∈dom(f) ∧ f ∈ ℤ ⇸ ℤ");
		assertWDLemma("{1, 2, x, x+y, 4, 6} = B", "⊤");
		assertWDLemma("(⋂ m,n \u00b7 m∈A ∧ n∈B \u2223 {m÷n}) = B",
				"(∀m,n \u00b7 (m∈A ∧ n∈B) ⇒ n≠0) ∧ (∃m,n \u00b7 (m∈A ∧ n∈B))");
		assertWDLemma("(⋂{m+n} \u2223 m+n∈A)=B", "∃m,n\u00b7m+n∈A");
		assertWDLemma("bool(⊤)=bool(⊥)", "⊤");
		assertWDLemma("x+y+(x mod y)\u2217x+1=0 ⇒ y<x", "0 ≤ x ∧ 0 < y");
		assertWDLemmaAssignment("x≔y", "⊤");
		assertWDLemmaAssignment("x :\u2223 x'>x", "⊤");
		assertWDLemmaAssignment("x :∈ {x,y}", "⊤");
		assertWDLemmaAssignment("x :∈ {x÷y, y}", "y≠0");
		assertWDLemmaAssignment("f(x)≔f(x)", "x∈dom(f)∧f ∈ ℤ ⇸ ℤ");
		assertWDLemmaAssignment("x :\u2223 x'=card(A∪{x'})",
				"∀x' \u00b7 finite(A∪{x'})");
		assertWDLemma("a = {x∣x≤card(A)}", "finite(A)");
		assertWDLemma("a = min(A)", "A ≠ ∅ ∧ (∃b·∀x·x∈A ⇒ b≤x)");
		assertWDLemma("a = max(A)", "A ≠ ∅ ∧ (∃b·∀x·x∈A ⇒ b≥x)");
		assertWDLemma("a = max(A)", "A ≠ ∅ ∧ (∃b·∀x·x∈A ⇒ b≥x)");

		assertWDLemma("T ⊆ S ∧ g ∈ ℤ → T ⇒ (∃S·g(S) ∈ T)",
				"T ⊆ S ∧ g ∈ ℤ → T ⇒ (∀S0·S0 ∈ dom(g) ∧ g ∈ ℤ ⇸ S)");

		assertWDLemma("∀f,y·f∈ℤ → ℤ ⇒ (∃x·x = f(y))",
				"∀f,y·f∈ℤ → ℤ ⇒ y∈dom(f) ∧ f∈ℤ ⇸ ℤ");

		assertWDLemma("∀y·∃x·x = f(y)", "∀y·y∈dom(f) ∧ f∈ℤ ⇸ ℤ");

		assertWDLemma("f(x)=f(y)", "x∈dom(f) ∧ f∈ℤ ⇸ ℤ ∧ y∈dom(f) ∧ f∈ℤ ⇸ ℤ",
				"x∈dom(f) ∧ f∈ℤ ⇸ ℤ ∧ y∈dom(f)");

		// Ensure that a type name doesn't get captured
		// when computing a WD lemma
		assertWDLemma("T ⊆ S ∧ g ∈ ℤ → T ⇒ (∃S·g(S) ∈ T)",
				"T ⊆ S ∧ g ∈ ℤ → T ⇒ (∀S0·S0 ∈ dom(g) ∧ g ∈ ℤ ⇸ S)");

		// Example from the Mobile model
		assertWDLemma("a ∈ S ↔ S ∧ b ∈ S ↔ (ℤ ↔ S) ∧"
				+ "(∀s·s ∈ dom(a) ⇒ a(s) = b(s)(max(dom(b(s)))))"//
		,//
				"a∈S ↔ S ∧ b∈S ↔ (ℤ ↔ S) ⇒"
						+ "  (∀s·s∈dom(a) ⇒ s∈dom(a) ∧ a∈S ⇸ S ∧"
						+ "   s∈dom(b) ∧ b∈S ⇸ ℙ(ℤ × S) ∧"
						+ "   s∈dom(b) ∧ b∈S ⇸ ℙ(ℤ × S) ∧"
						+ "   dom(b(s))≠∅ ∧ (∃b0·∀x·x∈dom(b(s))⇒b0≥x) ∧"
						+ "   max(dom(b(s)))∈dom(b(s)) ∧ b(s)∈ℤ ⇸ S)"//
				,//
				"a∈S ↔ S ∧ b∈S ↔ (ℤ ↔ S) ⇒" + "  (∀s·s∈dom(a) ⇒ a∈S ⇸ S ∧"
						+ "   s∈dom(b) ∧ b∈S ⇸ ℙ(ℤ × S) ∧"
						+ "   dom(b(s))≠∅ ∧ (∃b0·∀x·x∈dom(b(s))⇒b0≥x) ∧"
						+ "   max(dom(b(s)))∈dom(b(s)) ∧ b(s)∈ℤ ⇸ S)"//
		);

		// Reduced example extracted from the preceding one
		assertWDLemma("∀s·max(s) ∈ s", "∀s·s≠∅ ∧ (∃b·∀x·x ∈ s  ⇒ b ≥ x)");

		// Case where a bound variable disappears
		assertWDLemma("∀y·∃x·x = f(y)", "∀y·y∈dom(f) ∧ f∈ℤ ⇸ ℤ");
		assertWDLemma("∀f,y·f∈ℤ → ℤ ⇒ (∃x·x = f(y))",
				"∀f,y·f∈ℤ → ℤ ⇒ y∈dom(f) ∧ f∈ℤ ⇸ ℤ");

		// Disjunctions
		assertWDLemma("a÷1=b ∨ a÷2=b",//
				"1≠0 ∧ (a÷1=b ∨ 2≠0)");
		assertWDLemma("a÷1=b ∨ a÷2=b ∨ a÷3=b",
				"1≠0 ∧ (a÷1=b ∨ (2≠0 ∧ (a÷2=b ∨ 3≠0)))");
		assertWDLemma("a÷1=b ∨ a=b ∨ a÷3=b",//
				"1≠0 ∧ (a÷1=b ∨ a=b ∨ 3≠0)");
	}

	@Test 
	public void testRedundant() {
		assertWDLemma("3÷P=0 ∧ 2=5 ∧ 6÷P=0", "P≠0 ∧ (3÷P=0 ∧ 2=5 ⇒ P≠0)", "P≠0");
		assertWDLemma("f(x)=f(y)", "x∈dom(f) ∧ f∈ℤ⇸ℤ ∧ y∈dom(f) ∧ f∈ℤ⇸ℤ",
				"x∈dom(f) ∧ f∈ℤ⇸ℤ ∧ y∈dom(f)");
		assertWDLemma("x≠0 ∨ 3 = 4÷x", "x≠0 ∨ x≠0", "x≠0 ∨ x≠0");
	}

	/**
	 * Ensures that documented simplification rules are indeed implemented.
	 */
	@Test 
	public void testSimplified() {
		// (⊤ ∧ A) ⇔ A
		assertWDLemma("1 = 2÷x", "x≠0");
		// (A ∧ ⊤) ⇔ A
		assertWDLemma("1÷x = 2", "x≠0");
		// (A ∧ A) ⇔ A
		assertWDLemma("3÷x = 4÷x", "x≠0 ∧ x≠0", "x≠0");
		// (A ∨ ⊤) ⇔ ⊤
		assertWDLemma("1 = 2÷x ∨ 3 = 4", "x≠0");
		// (⊤ ∨ A) ⇔ ⊤
		assertWDLemma("⊤ ∨ 3 = 4÷x", "⊤");
		// (A ⇒ (B ⇒ C)) ⇔ (A ∧ B ⇒ C)
		assertWDLemma("1 = 2 ⇒ (3 = 4 ⇒ 5 = 6÷x)", "1 = 2 ∧ 3 = 4 ⇒ x≠0");
		// (A ⇒ ⊤) ⇔ ⊤
		assertWDLemma("1 = 2 ⇒ 3 = 4", "⊤");
		// (⊤ ⇒ A) ⇔ A
		assertWDLemma("⊤ ⇒ 3 = 4÷x", "x≠0");
		// (A ⇒ A) ⇔ ⊤
		assertWDLemma("x≠0 ⇒ 3 = 4÷x", "⊤");
		// (∀x·⊤) ⇔ ⊤
		assertWDLemma("∀x·x=1", "⊤");
		// (∃x·⊤) ⇔ ⊤
		assertWDLemma("a=(⋂x⦂ℙ(ℙ(ℤ))·⊤∣x)", "⊤");
		// (∀x·A) ⇔ A provided x nfin A
		assertWDLemma("∀x·x=a÷b", "b≠0");
		// (∃x·A) ⇔ A provided x nfin A
		assertWDLemma("a=(⋂x⦂ℙ(ℙ(ℤ))·1=2∣x)", "1=2");
	}

	/**
	 * Shows that some WD predicates are not simplified, although they could be.
	 */
	@Test 
	public void testNotSimplified() {
		// (A ∨ A) ⇔ A
		assertWDLemma("x≠0 ∨ 3 = 4÷x", "x≠0 ∨ x≠0", "x≠0 ∨ x≠0");
	}

	/**
	 * Tests coming from model "routing_new" from Jean-Raymond Abrial.
	 */
	@Test 
	public void testRouting() {
		final ITypeEnvironment env = mTypeEnvironment(//
				"N=ℙ(N); age=L ↔ ℤ; l_net=ℤ ↔ L; parity=ℤ ↔ ℤ", ff);

		// inv11/WD in rm_3
		assertWDLemma(env, //
				"∀n,l· n∈N ∧ l∈L ⇒ (n↦l∈m_net_up ⇔ n↦l↦age(l)∈n_net"
						+ " ∧ parity(age(l))=1)", //
				"∀ n, l · n∈N ∧ l∈L" //
						+ "  ⇒"//
						+ "    l∈dom(age) ∧ age∈L ⇸ ℤ ∧"//
						+ "(      n ↦ l ↦ age(l)∈n_net" //
						+ "    ⇒"//
						+ "      l∈dom(age) ∧ age∈L ⇸ ℤ ∧"//
						+ "      age(l)∈dom(parity) ∧" //
						+ "      parity∈ℤ ⇸ ℤ)",

				"∀ n, l · n∈N ∧ l∈L" //
						+ "  ⇒"
						+ "    l∈dom(age) ∧ age∈L ⇸ ℤ ∧"
						+ "(      n ↦ l ↦ age(l)∈n_net" //
						+ "    ⇒" + "      age(l)∈dom(parity) ∧" //
						+ "      parity∈ℤ ⇸ ℤ)");

		// inv3/WD in rm_3
		assertWDLemma(env, "∀l·l∈L ⇒ (l∈net ⇔ parity(age(l))=1)",//
				"∀ l · l∈L" //
						+ "⇒"//
						+ "l∈dom(age) ∧ age∈L ⇸ ℤ ∧"
						+ "age(l)∈dom(parity) ∧"
						+ "parity∈ℤ ⇸ ℤ");

		// change_link2/ln/WD
		assertWDLemma(env, "(parity(x)=0 ⇒ ln = l_net∖{n↦l}) ∧"//
				+ "(parity(x)=1 ⇒ ln = l_net∪{n↦l})",
				"x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ ∧"//
						+ "((parity(x)=0 ⇒ ln=l_net ∖ {n ↦ l})"//
						+ "⇒"//
						+ "x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ)",
				"x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ");

		// inv5/WD in rm_3
		assertWDLemma(env, "∀l·l∈L ⇒ n_net[N×{l}] ⊆ 0‥age(l)",
				"∀l·l∈L ⇒ l∈dom(age) ∧ age∈L ⇸ ℤ");

		// inv7/WD in rm_7
		assertWDLemma(env,
				"∀n,l·n∈N ∧ l∈L ⇒ age(l)=l_age(n↦l) ∨ n↦l↦age(l)∈n_net ",
				"∀ n, l · "//
						+ "    n∈N ∧ l∈L"//
						+ "  ⇒"//
						+ "    l∈dom(age) ∧ age∈L ⇸ ℤ ∧"//
						+ "    n ↦ l∈dom(l_age) ∧"//
						+ "    l_age∈N × L ⇸ ℤ ∧"//
						+ "    (age(l)=l_age(n ↦ l) ∨"//
						+ "     (l∈dom(age) ∧ age∈L ⇸ ℤ))");//

		// inv 10 WD in rm_3
		assertWDLemma(
				env,
				"∀n,l· n∈N ∧ l∈L ⇒ (n↦l∈m_net_up ⇔ n↦l↦age(l)∈n_net ∧ parity(age(l))=1)",
				"∀ n, l ·"//
						+ "    n∈N ∧ l∈L"//
						+ "  ⇒"//
						+ "    l∈dom(age) ∧ age∈L ⇸ ℤ ∧"//
						+ "    (n ↦ l ↦ age(l)∈n_net"//
						+ "    ⇒"//
						+ "      l∈dom(age) ∧ age∈L ⇸ ℤ ∧"//
						+ "      age(l)∈dom(parity) ∧"//
						+ "      parity∈ℤ ⇸ ℤ)",//
				"∀ n, l ·"//
						+ "    n∈N ∧ l∈L"//
						+ "  ⇒"//
						+ "    l∈dom(age) ∧ age∈L ⇸ ℤ ∧"//
						+ "    (n ↦ l ↦ age(l)∈n_net"//
						+ "    ⇒"//
						+ "      age(l)∈dom(parity) ∧"//
						+ "      parity∈ℤ ⇸ ℤ)");//
	}

	/**
	 * Tests coming from model "DIR41.4 "
	 */
	@Test 
	public void testDIR() {
		final ITypeEnvironment env = mTypeEnvironment(//
				"T=ℙ(T); " //
				+"C=ℙ(C); " //
				+"SI=ℙ(SI); " //
				+"CH=ℙ(CH); " //
				+"CO=ℙ(CO); " //
				+"pcoc=CO ↔ C; " //
				+"p_at=CO ↔ CO; " //
				+"p_c_a=CO ↔ CO; " //
				+"p_c_inv=CO ↔ CO; " //
				+"c_chemin_signal=CH ↔ SI; " //
				+"c_chemin_cellule_accès=CH ↔ CO; " //
				+"c_signal_cellule_arrêt=SI ↔ CO; " //
				+"closure1=(CO ↔ CO) ↔ (CO ↔ CO)"//
				, ff);

		// thm1/WD in Atteignable
		assertWDLemma(env, "∀x,y · x↦y ∈ p_at ⇒ p_c_inv(y)↦p_c_inv(x) ∈ p_at",
				"∀ x, y · "//
						+ "    x ↦ y∈p_at"//
						+ "  ⇒"//
						+ "    y∈dom(p_c_inv) ∧"//
						+ "    p_c_inv∈ CO ⇸ CO ∧"//
						+ "    x∈dom(p_c_inv) ∧"//
						+ "    p_c_inv∈ CO ⇸ CO",//
				"∀ x, y · "//
						+ "    x ↦ y∈p_at"//
						+ "  ⇒"//
						+ "    y∈dom(p_c_inv) ∧"//
						+ "    p_c_inv∈ CO ⇸ CO ∧"//
						+ "    x∈dom(p_c_inv)");

		// thm2/WD in C_SIGNAUX
		assertWDLemma(
				env,
				" ∀ ch, s ·"//
						+ "		ch ∈ dom(c_chemin_signal) ∧"//
						+ "		c_chemin_cellule_accès(ch)=c_signal_cellule_arrêt(s)"//
						+ "		 ⇒"//
						+ "		 	c_chemin_signal(ch) = s"//
				,//
				"∀ ch, s ·"//
						+ "(    ch∈dom(c_chemin_signal)"//
						+ "  ⇒"//
						+ "    ch∈dom(c_chemin_cellule_accès)    ∧"//
						+ "    c_chemin_cellule_accès∈CH ⇸ CO    ∧"//
						+ "    s∈dom(c_signal_cellule_arrêt)    ∧"//
						+ "    c_signal_cellule_arrêt∈SI ⇸ CO)  ∧"//
						+ "    (ch∈dom(c_chemin_signal) ∧"//
						+ "     c_chemin_cellule_accès(ch)=c_signal_cellule_arrêt(s)"//
						+ "     ⇒"//
						+ "     ch∈dom(c_chemin_signal) ∧"
						+ "     c_chemin_signal∈CH ⇸ SI)",//
				"∀ ch, s ·"//
						+ "(    ch∈dom(c_chemin_signal)"//
						+ "  ⇒"//
						+ "    ch∈dom(c_chemin_cellule_accès)    ∧"//
						+ "    c_chemin_cellule_accès∈CH ⇸ CO    ∧"//
						+ "    s∈dom(c_signal_cellule_arrêt)    ∧"//
						+ "    c_signal_cellule_arrêt∈SI ⇸ CO)  ∧"//
						+ "    (ch∈dom(c_chemin_signal) ∧"//
						+ "     c_chemin_cellule_accès(ch)=c_signal_cellule_arrêt(s)"//
						+ "     ⇒"//
						+ "     c_chemin_signal∈CH ⇸ SI)");

		// axm2/WD in Closure1
		assertWDLemma(env, "∀R · closure1(R);R ⊆ closure1(R)",
				"∀R·R∈dom(closure1)∧closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)∧"
						+ "R∈dom(closure1)∧closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)",
				"∀R·R∈dom(closure1)∧closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");

		// thm2/WD in Closure1
		assertWDLemma(env,
				"∀R1,R2 · R1⊆R2 ⇒ closure1(R1) ⊆ closure1(R2)",//
				"∀ R1, R2 · R1⊆R2 ⇒"//
						+ "  R1∈dom(closure1) ∧ "
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  R2∈dom(closure1) ∧ "
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)",
				"∀ R1, R2 · R1⊆R2 ⇒"//
						+ "  R1∈dom(closure1) ∧ "
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  R2∈dom(closure1)");

		// thm7/WD in Closure1
		assertWDLemma(env, "∀R · closure1(R);closure1(R) ⊆ closure1(R)	",//
				"∀ R ·" //
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)",//
				"∀ R ·" //
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");

		// thm7/WD in closure 1 modified
		assertWDLemma(env, "∀R · closure1(R) ⊆ closure1(R)",//
				"∀ R ·" //
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)",//
				"∀ R ·" //
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");

		// thm18/WD in Closure1
		assertWDLemma(
				env,
				"∀R · R∈ CO ⇸ CO ⇒ closure1(R) ⊆ {x↦y ∣ x↦y ∈ closure1(R) ∧"
						+ "(∀z · "
						+ "  x↦z ∈ closure1(R) ∧ y≠z ∧ z↦y ∉ closure1(R)"//
						+ "  ⇒"//
						+ "  y↦z ∈ closure1(R))}",//
				"∀ R · R∈CO ⇸ CO ⇒"//
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  (∀ x, y ·"
						+ "    R∈dom(closure1) ∧"
						+ "    closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "    (x ↦ y∈closure1(R)"
						+ "     ⇒"
						+ "     (∀ z · "
						+ "      R∈dom(closure1) ∧"
						+ "      closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "      (x ↦ z∈closure1(R) ∧ y≠z"
						+ "       ⇒"
						+ "       R∈dom(closure1) ∧"
						+ "       closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)) ∧"
						+ "       (x ↦ z∈closure1(R) ∧ y≠z ∧"
						+ "        z ↦ y∉closure1(R)"
						+ "        ⇒"
						+ "        R∈dom(closure1) ∧"
						+ "        closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)))))",
				"∀ R · R∈CO ⇸ CO ⇒"//
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");

		// thm13/WD in Closure1
		assertWDLemma(env,//
				"∀R,x,y ·"//
						+ "R ∈ CO ⇸ CO ∧ y ∈ closure1(R)[{x}] ⇒"
						+ "((closure1(R)[{x}])∖(closure1(R)[{y}])) ⊆"
						+ "((closure1(R))∼)[{y}] ∪ {y}",//
				"∀R,x,y·"
						+ "(R∈CO ⇸ CO"//
						+ " ⇒ R∈dom(closure1) ∧"
						+ "   closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)) ∧"
						+ "(R∈CO ⇸ CO ∧ y∈(closure1(R))[{x}]"
						+ " ⇒ R∈dom(closure1) ∧ "
						+ "   closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "   R∈dom(closure1) ∧"
						+ "   closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "   R∈dom(closure1) ∧"
						+ "   closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO))",//
				"∀R·R∈CO ⇸ CO ⇒" //
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");
	}

	/**
	 * Ensures that WD simplification does not mess up bound identifiers
	 */
	@Test 
	public void testQuantifiers() {
		final ITypeEnvironment env = mTypeEnvironment("f=S↔S", ff);
		assertWDLemma(env,//
				"∀x·x ∈ dom(f) ⇒ (∃y · f(x) = f(y)) ",//
				"∀x·x∈dom(f) ⇒" //
						+ "(∀y·x∈dom(f) ∧ f∈S ⇸ S ∧ y∈dom(f) ∧ f∈S ⇸ S)",//
				"∀x·x∈dom(f) ⇒ (∀y·f∈S ⇸ S∧y∈dom(f))");
	}

	/**
	 * Ensures that WD simplification does not mess up bound identifiers, even
	 * in the presence of multiple quantifiers.
	 */
	@Test 
	public void testQuantifiedMany() {
		final ITypeEnvironment env = mTypeEnvironment("f=S↔S; S=ℙ(S)", ff);
		assertWDLemma(env,//
				"∀x·x∈dom(f) ⇒ (∃y,z·f(y) = f(z)) ",//
				"∀x·x∈dom(f) ⇒"//
						+ "(∀y,z·y∈dom(f) ∧ f∈S⇸S ∧ z∈dom(f) ∧ f∈S⇸S)",//
				"∀x·x∈dom(f) ⇒"//
						+ "(∀y,z·y∈dom(f) ∧ f∈S⇸S ∧ z∈dom(f))");

		assertWDLemma(env,//
				"(∃x·x⊆S) ⇒ (⋂x∣x⊆S) = S",//
				"⊤");
		assertWDLemma(env,//
				"(∃x·x⊆S) ⇒ (∀y·y=0 ⇒ (⋂x∣x⊆S) = S)",//
				"(∃x·x⊆S) ⇒ (∀y·y=0 ⇒ (∃x·x⊆S))",//
				"⊤");
		assertWDLemma(env,//
				"(∀y·y=0 ∧ (∃x·x⊆S)) ⇒ (⋂x∣x⊆S) = S",//
				"(∀y·y=0 ∧ (∃x·x⊆S)) ⇒ (∃x·x⊆S)",//
				"⊤");
	}

	/**
	 * Ensures that WD simplification does not mess up bound identifiers, even
	 * after a quantifier.
	 */
	@Test 
	public void testQuantifiedAfter() {
		final ITypeEnvironment env = mTypeEnvironment("f=S↔S", ff);
		assertWDLemma(env,//
				"∀x·x∈dom(f) ⇒ (∃y·f(x)=f(y)) ∧ f(x)=f(x)",//
				"∀x·x∈dom(f) ⇒"//
						+ "(∀y·x∈dom(f) ∧ f∈S⇸S ∧ y∈dom(f) ∧ f∈S⇸S) ∧"//
						+ "((∃y·f(x)=f(y)) ⇒"//
						+ "   x∈dom(f) ∧ f∈S⇸S ∧"//
						+ "   x∈dom(f) ∧ f∈S⇸S)",//
				"∀x·x∈dom(f) ⇒ (∀y·f∈S⇸S ∧ y∈dom(f))");
	}

	/**
	 * Ensures that WD simplification does not mess up bound identifiers, even
	 * in the presence of deep nesting.
	 */
	@Test 
	public void testQuantifiedDeep() {
		assertWDLemma(
				mTypeEnvironment("S=ℙ(S)", ff),//
				"∀f·f∈S ⇸ S ⇒ (∀x·f(x) = f(x) ⇒ (∃y·f(x) = f(y)))", //
				"∀f·f∈S ⇸ S ⇒ "//
						+ "  (∀x·x∈dom(f) ∧ f∈S⇸S ∧ x∈dom(f) ∧ f∈S⇸S ∧"//
						+ "    (f(x)=f(x) ⇒"
						+ "      (∀y·x∈dom(f) ∧ f∈S⇸S ∧ y∈dom(f) ∧ f∈S⇸S)))",
				"∀f·f∈S ⇸ S ⇒ "//
						+ "  (∀x·x∈dom(f) ∧"//
						+ "    (f(x)=f(x) ⇒ (∀y·y∈dom(f))))");
	}

	/**
	 * Ensures that WD conditions produced by two copies of the same predicate
	 * are properly simplified. Also checks, that if the duplication is not
	 * exact, only sound simplifications are carried.
	 */
	@Test 
	public void testQuantifierDeepDuplicate() {
		final ITypeEnvironment env = mTypeEnvironment("f=S↔S", ff);
		assertWDLemma(
				env,//
				"f∈S ⇸ S ⇒ (∀x·f(x) = f(x) ⇒"//
						+ "   (∃y·f(x) = f(y)) ∧ (∃z·f(x) = f(z)))", //
				"f∈S ⇸ S ⇒ "//
						+ "  (∀x·x∈dom(f) ∧ f∈S⇸S ∧ x∈dom(f) ∧ f∈S⇸S ∧"//
						+ "    (f(x)=f(x) ⇒"
						+ "      (∀y·x∈dom(f) ∧ f∈S⇸S ∧ y∈dom(f) ∧ f∈S⇸S) ∧"
						+ "      ((∃y·f(x) = f(y)) ⇒"
						+ "        (∀z·x∈dom(f) ∧ f∈S⇸S ∧ z∈dom(f) ∧ f∈S⇸S))))",
				"f∈S ⇸ S ⇒ "//
						+ "  (∀x·x∈dom(f) ∧"//
						+ "    (f(x)=f(x) ⇒ (∀y·y∈dom(f))))");

		// With differing condition for nested quantified identifier "y"
		assertWDLemma(
				env,//
				"f∈S ⇸ S ⇒ (∀x·f(x) = f(x) ⇒"//
						+ "   (∃y·y∈dom(f) ⇒ f(x) = f(y)) ∧"
						+ "   (∃z·f(x) = f(z)))", //
				"f∈S ⇸ S ⇒ "//
						+ "  (∀x·x∈dom(f) ∧ f∈S⇸S ∧ x∈dom(f) ∧ f∈S⇸S ∧"//
						+ "    (f(x)=f(x) ⇒"
						+ "      (∀y·y∈dom(f) ⇒ x∈dom(f) ∧ f∈S⇸S ∧ y∈dom(f) ∧ f∈S⇸S) ∧"
						+ "      ((∃y·y∈dom(f) ⇒ f(x) = f(y)) ⇒"
						+ "        (∀z·x∈dom(f) ∧ f∈S⇸S ∧ z∈dom(f) ∧ f∈S⇸S))))",
				"f∈S ⇸ S ⇒ "//
						+ "  (∀x·x∈dom(f) ∧"//
						+ "    (f(x)=f(x) ∧ (∃y·y∈dom(f) ⇒ f(x) = f(y)) ⇒"
						+ "      (∀z·z∈dom(f))))");
	}

	/**
	 * Acceptance test for CDIS model
	 */
	@Test 
	public void testCDIS() {
		final ITypeEnvironment env = mTypeEnvironment(//
				"Attr_Id=ℙ(Attr_id); "//
						+ "Attrs=ℙ(Attrs); "//
						+ "Attr_value=ℙ(Attr_value); "//
						+ "value=Attrs ↔ Attr_value; "//
						+ "db0=Attr_id ↔ Attrs; "//
						+ "contents=Page ↔ Page_contents; "//
						+ "private_pages=Page_number ↔ Page; "//
						+ "previous_pages=Page_number ↔ Page; "//
						+ "last_update=Attrs ↔ Date_time; "//
						+ "creation_date=Page ↔ Date_time; "//
						+ "release_date=Page ↔ Date_time; "//
						+ "leq=Date_time ↔ Date_time; "//
						+ "dp_time=Disp_params ↔ Date_time; "//
						+ "conform=Attr_id ↔ Attr_value", ff);

		assertWDLemma(env,//
				"∀ai·ai∈Attr_id ⇒ ai ↦ value(db0(ai)) ∈ conform", //
				"∀ ai·ai∈Attr_id ⇒"//
						+ "ai∈dom(db0) ∧"
						+ "db0∈Attr_id ⇸ Attrs ∧"
						+ "db0(ai)∈dom(value) ∧" + "value∈Attrs ⇸ Attr_value");
		assertWDLemma(env,//
				"value(a)=av", //
				"a∈dom(value) ∧ value∈Attrs ⇸ Attr_value");
		assertWDLemma(env,//
				"contents(p) = pc", //
				"p∈dom(contents) ∧ contents∈Page ⇸ Page_contents");

		assertWDLemma(env,//
				"last_update(a) = time_now", //
				"a∈dom(last_update) ∧ last_update∈Attrs ⇸ Date_time");

		assertWDLemma(env,//
				"creation_date(p) = time_now", //
				"p∈dom(creation_date) ∧ creation_date∈Page ⇸ Date_time");
		assertWDLemma(
				env,//
				"pp = {rp ∣ rp∈Rel_Page ∧ (release_date(rp) ↦ time_now) ∈ leq}",
				"∀rp·rp∈Rel_Page ⇒"//
						+ "rp∈dom(release_date) ∧"
						+ "release_date∈Page ⇸ Date_time");
		assertWDLemma(env,//
				"(time_now ↦ release_date(p)) ∈ leq", //
				"p∈dom(release_date) ∧ release_date∈Page ⇸ Date_time");
		assertWDLemma(env,//
				"dp_time(dp) = time_now", //
				"dp∈dom(dp_time) ∧ dp_time∈Disp_params ⇸ Date_time");
	}

	/**
	 * Ensures that the predicates which are not part of an implication are not
	 * simplified by predicates which belong to an implication. Also checks,
	 * that all the isolated predicates are used to simplify implications.
	 */
	@Test 
	public void testTraversal() {
		
		final ITypeEnvironment env = mTypeEnvironment(
				"f=S↔ℙ(S); g=S↔ℙ(S)", ff);
		assertWDLemma(
				env,//
				"g(x)∪{y ∣ (b<a⇒a=0) ∧ f(x)=∅ ∧ g(x)=∅}=f(x)",
				"x∈dom(g) ∧ g∈S ⇸ ℙ(S) ∧"
						+ "((b<a ⇒ a=0) ⇒ x∈dom(f) ∧ f∈S ⇸ ℙ(S) ∧"//
						+ "  (f(x)=∅ ⇒ x∈dom(g) ∧ g∈S ⇸ ℙ(S))) ∧"
						+ "x∈dom(f) ∧ f∈S ⇸ ℙ(S)",
				"x∈dom(g) ∧ g∈S ⇸ ℙ(S) ∧ x∈dom(f) ∧ f∈S ⇸ ℙ(S)");
		assertWDLemma(
				env,//
				"f(x)∪{y ∣ (b<a⇒a=0) ∧ f(x)=∅ ∧ g(x)=∅}=g(x)",
				"x∈dom(f) ∧ f∈S ⇸ ℙ(S) ∧"
						+ "((b<a ⇒ a=0) ⇒ x∈dom(f) ∧ f∈S ⇸ ℙ(S) ∧"
						+ "  (f(x)=∅ ⇒ x∈dom(g) ∧ g∈S ⇸ ℙ(S))) ∧"
						+ "x∈dom(g) ∧ g∈S ⇸ ℙ(S)",
				"x∈dom(f) ∧ f∈S ⇸ ℙ(S) ∧ x∈dom(g) ∧ g∈S ⇸ ℙ(S)");
		assertWDLemma(
				env,//
				"(b<c⇒c=0) ⇒ (f(x)∪{y ∣ (b<a⇒a=0) ∧ f(x)=∅ ∧ g(x)=∅}=g(x))",
				"(b<c⇒c=0) ⇒"//
						+ "x∈dom(f) ∧ f∈S ⇸ ℙ(S) ∧"
						+ "((b<a⇒a=0) ⇒ x∈dom(f) ∧ f∈S ⇸ ℙ(S) ∧"
						+ "  (f(x)=∅ ⇒ x∈dom(g) ∧ g∈S ⇸ ℙ(S))) ∧"
						+ "x∈dom(g) ∧ g∈S ⇸ ℙ(S)",
				"(b<c⇒c=0) ⇒ x∈dom(f) ∧ f∈S ⇸ ℙ(S) ∧ x∈dom(g) ∧ g∈S ⇸ ℙ(S)");
		assertWDLemma(
				env,//
				"(b<c⇒c=0) ⇒ (g(x)∪{y ∣ (b<a⇒a=0) ∧ f(x)=∅ ∧ g(x)=∅}=f(x))",
				"(b<c⇒c=0) ⇒"//
						+ "x∈dom(g) ∧ g∈S ⇸ ℙ(S) ∧"
						+ "((b<a⇒a=0) ⇒ x∈dom(f) ∧ f∈S ⇸ ℙ(S) ∧"
						+ "  (f(x)=∅ ⇒ x∈dom(g) ∧ g∈S ⇸ ℙ(S))) ∧"
						+ "x∈dom(f) ∧ f∈S ⇸ ℙ(S)",
				"(b<c⇒c=0) ⇒ x∈dom(g) ∧ g∈S ⇸ ℙ(S) ∧ x∈dom(f) ∧ f∈S ⇸ ℙ(S)");
	}

	/**
	 * Unit test for mathematical extensions
	 */
	@Test 
	public void testExtensions() {
		final ITypeEnvironment env = EFF.makeTypeEnvironment();
		// WD strict predicate
		assertWDLemma(env, "fooS(1=1÷x, 1÷y, 1=1÷z, 1÷t)",
				"finite({1}) ∧ y≠0 ∧ t≠0 ∧ x≠0 ∧ z≠0");
		// non WD strict predicate
		assertWDLemma(env, "fooL(1=1÷x, 1÷y, 1=1÷z, 1÷t)", "finite({0})");
		// WD strict expression
		assertWDLemma(env, "1=barS(1=1÷x, 1÷y, 1=1÷z, 1÷t)",
				"finite({1}) ∧ y≠0 ∧ t≠0 ∧ x≠0 ∧ z≠0");
		// non WD strict expression
		assertWDLemma(env, "1=barL(1=1÷x, 1÷y, 1=1÷z, 1÷t)", "finite({0})");
	}

	/**
	 * Unit test for COND operator
	 */
	@Test 
	public void testCond() {
		final IExpressionExtension cond = FormulaFactory.getCond();
		final FormulaFactory factory = FormulaFactory.getInstance(cond);
		final ITypeEnvironment env = factory.makeTypeEnvironment();
		assertWDLemma(env, "COND(a<b, b, a) = a", "⊤ ∧ (a<b ⇒ ⊤) ∧ (¬a<b ⇒ ⊤)",
				"⊤");
		assertWDLemma(env, "COND(a÷b=1, card({a,b}), card({0,1,2})) = a",
				"b≠0 ∧ (a÷b=1⇒finite({a,b})) ∧ (¬ a÷b=1⇒finite({0,1,2}))");
	}

	/**
	 * Unit test for data type extensions
	 */
	@Test 
	public void testDatatype() throws Exception {
		final ITypeEnvironmentBuilder env = LIST_FAC.makeTypeEnvironment();
		env.addName("l", LIST_INT_TYPE);

		// Type constructor
		assertWDLemma(env, "l ∈ List({1÷x})", "x≠0");

		// Value constructors
		assertWDLemma(env, "l = nil", "⊤");
		assertWDLemma(env, "l = cons(1÷x, nil)", "x≠0");
		assertWDLemma(env, "l = cons(1÷x, tail(l))",
				"x≠0  ∧ (∃h,t·l=cons(h,t))");

		// Destructors
		assertWDLemma(env, "x = head(l)", "∃h,t· l = cons(h, t)");
		assertWDLemma(env, "x = tail(l)", "∃h,t· l = cons(h, t)");

		// Mixed cases
		assertWDLemma(env, "x = head(cons(1÷x, l))",
				"(∃h,t· cons(1÷x, l) = cons(h, t)) ∧ x≠0");
		assertWDLemma(env, "l = tail(cons(1÷x, l))",
				"(∃h,t· cons(1÷x, l) = cons(h, t)) ∧ x≠0");
	}

	/**
	 * Unit test to check the simplification of the WD of a destructor when
	 * there is only one datatype constructor.
	 */
	@Test 
	public void testDatatypeOneConstructorOnly() {
		final IDatatype dt = ff.makeDatatype(ExtensionHelper.FOOBARTYPE);
		final FormulaFactory fac = FormulaFactory.getInstance(dt
				.getExtensions());
		final ITypeEnvironmentBuilder env = fac.makeTypeEnvironment();
		env.addName("l", fac.makeIntegerType());

		// Value constructor
		assertWDLemma(env, "x = foo(l)", "⊤");
		// Destructor (common case)
		assertWDLemma(env, "l = bar(f)", "⊤");
		// Destructor (explicit constructor)
		assertWDLemma(env, "l = bar(foo(l))", "⊤");
		// Destructor (with child WD)
		assertWDLemma(env, "l = bar(foo(1÷x))", "x≠0");
	}

}
