/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - added additional acceptance tests
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;

/**
 * Unit and acceptance tests for the computation of WD lemmas.
 * 
 * @author Stefan Hallerstede
 */
public class TestWD extends AbstractTests {

	private static IntegerType INTEGER = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();
	private static GivenType S = ff.makeGivenType("S");

	static ITypeEnvironment defaultTEnv = mTypeEnvironment(//
			"x", INTEGER,//
			"y", INTEGER,//
			"A", POW(INTEGER),//
			"B", POW(INTEGER),//
			"f", POW(CPROD(INTEGER, INTEGER)),//
			"Y", POW(BOOL),//
			"S", POW(S)//
	);

	private static abstract class TestFormula<T extends Formula<T>> {

		final T input;
		final Predicate expected;

		// base class of various tests
		TestFormula(ITypeEnvironment env, String in, String exp) {
			this.input = parse(in);
			this.expected = parsePredicate(exp).flatten(ff);
			typeCheck(input, env);
			typeCheck(expected, env);
		}

		// private static final WDComputer computer = new WDComputer(ff);

		public void test() {
			// final Predicate actual = computer.getWDLemma(input);
			final Predicate actual = input.getWDPredicate(ff);
			assertTrue("Ill-formed WD predicate", actual.isWellFormed());
			assertTrue("Untyped WD predicate", actual.isTypeChecked());
			assertEquals(expected, actual);
		}

		public abstract T parse(String image);
	}

	private static class TestPredicate extends TestFormula<Predicate> {

		TestPredicate(ITypeEnvironment env, String in, String exp) {
			super(env, in, exp);
		}

		@Override
		public Predicate parse(String image) {
			return parsePredicate(image);
		}

	}

	private static class TestAssignment extends TestFormula<Assignment> {

		TestAssignment(String in, String exp) {
			super(defaultTEnv, in, exp);
		}

		@Override
		public Assignment parse(String image) {
			return parseAssignment(image);
		}

	}

	private static void assertWDLemma(String in, String expected) {
		assertWDLemma(defaultTEnv, in, expected);
	}

	// private static final List<TestFormula<?>> tests = new
	// ArrayList<TestFormula<?>>();

	private static void assertWDLemma(ITypeEnvironment env, String in,
			String expected) {
		final TestPredicate test = new TestPredicate(env, in, expected);
		// tests.add(test);
		test.test();
	}

	private static void assertWDLemmaAssignment(String in, String expected) {
		final TestAssignment test = new TestAssignment(in, expected);
		// tests.add(test);
		test.test();
	}

	public void testWD() {
		assertWDLemma("x≠y ∧ y=1", "⊤");
		assertWDLemma("x+y+x+1=0 ⇒ y<x", "⊤");
		assertWDLemma("x+1=0 ∨ x<y", "⊤");
		assertWDLemma("(∃x \u00b7 0<x ⇒ (∀y \u00b7 y+x=0))", "⊤");
		assertWDLemma("(B×Y)(x) ∈ Y", "x∈dom(B × Y) ∧ B × Y ∈ ℤ ⇸ BOOL");
		assertWDLemma("x=f(f(y))", //
				"((y∈dom(f) ∧ f ∈ ℤ ⇸ ℤ) ∧ f(y)∈dom(f)) ∧ f ∈ ℤ ⇸ ℤ");
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

		// Ensure that a type name doesn't get captured
		// when computing a WD lemma
		assertWDLemma("T ⊆ S ∧ g ∈ ℤ → T ⇒ (∃S·g(S) ∈ T)",
				"T ⊆ S ∧ g ∈ ℤ → T ⇒ (∀S0·S0 ∈ dom(g) ∧ g ∈ ℤ ⇸ S)");

		// Example from the Mobile model
		assertWDLemma("   a ∈ S ↔ S" + " ∧ b ∈ S ↔ (ℤ ↔ S)"
				+ " ∧ (∀s·s ∈ dom(a) ⇒ a(s) = b(s)(max(dom(b(s)))))",
		// ---------------------------
				"  a∈S ↔ S ∧ b∈S ↔ (ℤ ↔ S)" + "⇒ (∀s·s∈dom(a)"
						+ "    ⇒ s∈dom(a) ∧ a ∈ S ⇸ S"
						+ "    ∧ s∈dom(b) ∧ b ∈ S ⇸ ℙ(ℤ × S)"
						+ "    ∧ s∈dom(b) ∧ b ∈ S ⇸ ℙ(ℤ × S)"
						+ "    ∧ dom(b(s))≠∅ ∧ (∃b0·∀x·x∈dom(b(s))⇒b0≥x)"
						+ "    ∧ max(dom(b(s)))∈dom(b(s))"
						+ "    ∧ b(s) ∈ ℤ ⇸ S)");

		// Reduced example extracted from the preceding one
		assertWDLemma("∀s·max(s) ∈ s", "∀s·s≠∅ ∧ (∃b·∀x·x ∈ s  ⇒  b ≥ x)");

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

	public void testRedundant() {
		assertWDLemma("3÷P = 4÷P", "P≠0 ∧ P≠0");
		assertWDLemma("∃x·x=a÷b", "∀x·b≠0");
		assertWDLemma("3÷P=0 ∧ 2=5 ∧ 6÷P=0", "P≠0 ∧ (3÷P=0 ∧ 2=5 ⇒ P≠0)");
		assertWDLemma("f(x)=f(y)", "x∈dom(f) ∧ f∈ℤ⇸ℤ ∧ y∈dom(f) ∧ f∈ℤ⇸ℤ");
	}

	/**
	 * Tests coming from model "routing_new" from Jean-Raymond Abrial.
	 */
	public void testRouting() {
		final GivenType L = ff.makeGivenType("L");
		final GivenType N = ff.makeGivenType("N");
		final ITypeEnvironment env = mTypeEnvironment(//
				"N", POW(N), //
				"L", POW(L), //
				"age", REL(L, INTEGER), //
				"l_net", REL(INTEGER, L),//
				"parity", REL(INTEGER, INTEGER));

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
						+ "x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ)");

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
						+ "      parity∈ℤ ⇸ ℤ)");//
	}

	/**
	 * Tests coming from model "DIR41.4 "
	 */
	public void testDIR() {
		final GivenType T = ff.makeGivenType("T");
		final GivenType C = ff.makeGivenType("C");
		final GivenType CO = ff.makeGivenType("CO");
		final GivenType SI = ff.makeGivenType("SI");
		final GivenType CH = ff.makeGivenType("CH");
		final ITypeEnvironment env = mTypeEnvironment(//
				"T", POW(T), //
				"C", POW(C),//
				"SI", POW(SI),//
				"CH", POW(CH),//
				"CO", POW(CO),//
				"pcoc", REL(CO, C),//
				"p_at", REL(CO, CO),//
				"p_c_a", REL(CO, CO),//
				"p_c_inv", REL(CO, CO),//
				"c_chemin_signal", REL(CH, SI),//
				"c_chemin_cellule_accès", REL(CH, CO),//
				"c_signal_cellule_arrêt", REL(SI, CO),//
				"closure1", REL(REL(CO, CO), REL(CO, CO))//
		);

		// thm1/WD in Atteignable
		assertWDLemma(env, "∀x,y · x↦y ∈ p_at ⇒ p_c_inv(y)↦p_c_inv(x) ∈ p_at",
				"∀ x, y · "//
						+ "    x ↦ y∈p_at"//
						+ "  ⇒"//
						+ "    y∈dom(p_c_inv) ∧"//
						+ "    p_c_inv∈ CO ⇸ CO ∧"//
						+ "    x∈dom(p_c_inv) ∧"//
						+ "    p_c_inv∈ CO ⇸ CO");

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
						+ "     c_chemin_signal∈CH ⇸ SI)");

		// axm2/WD in Closure1
		assertWDLemma(env, "∀R · closure1(R);R ⊆ closure1(R)",
				"∀R·R∈dom(closure1)∧closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)∧"
						+ "R∈dom(closure1)∧closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");

		// thm2/WD in Closure1
		assertWDLemma(env, "∀R1,R2 · R1⊆R2 ⇒ closure1(R1) ⊆ closure1(R2)",//
				"∀ R1, R2 · R1⊆R2 ⇒"//
						+ "  R1∈dom(closure1) ∧ "
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  R2∈dom(closure1) ∧ "
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");

		// thm7/WD in Closure1
		assertWDLemma(env, "∀R · closure1(R);closure1(R) ⊆ closure1(R)	",//
				"∀ R ·" //
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");

		// thm7/WD in closure 1 modified
		assertWDLemma(env, "∀R · closure1(R) ⊆ closure1(R)",
				"∀R·R∈dom(closure1) ∧ closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "R∈dom(closure1) ∧ closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)");

		// thm18/WD in Closure1
		assertWDLemma(env,
				"∀R · R∈ CO ⇸ CO ⇒ closure1(R) ⊆ {x↦y ∣ x↦y ∈ closure1(R) ∧"
						+ "(∀z · "
						+ "  x↦z ∈ closure1(R) ∧ y≠z ∧ z↦y ∉ closure1(R)"//
						+ "  ⇒"//
						+ "  y↦z ∈ closure1(R))}",

				"∀ R · R∈CO ⇸ CO ⇒"//
						+ "  R∈dom(closure1) ∧"
						+ "  closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "  (∀ x, y ·"
						+ "    R∈dom(closure1) ∧"
						+ "    closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "    (x ↦ y∈closure1(R)"
						+ "      ⇒"
						+ "      (∀ z · "
						+ "        R∈dom(closure1) ∧"
						+ "        closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "        (x ↦ z∈closure1(R) ∧ y≠z"
						+ "         ⇒  R∈dom(closure1) ∧"
						+ "            closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)) ∧"
						+ "            (x ↦ z∈closure1(R) ∧ y≠z ∧"
						+ "              z ↦ y∉closure1(R)"
						+ "              ⇒"
						+ "              R∈dom(closure1) ∧"
						+ "              closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)))))");

		// thm13/WD in Closure1
		assertWDLemma(env, "∀R,x,y ·"
				+ "		R ∈ CO ⇸ CO ∧ y ∈ closure1(R)[{x}] ⇒"
				+ "		((closure1(R)[{x}])∖(closure1(R)[{y}])) ⊆"
				+ "		((closure1(R))∼)[{y}] ∪ {y}",//
				"∀R,x,y·(R∈CO ⇸ CO"//
						+ " ⇒ R∈dom(closure1) ∧"
						+ "   closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO)) ∧"
						+ "   (R∈CO ⇸ CO ∧ y∈(closure1(R))[{x}]"
						+ "    ⇒ R∈dom(closure1) ∧ "
						+ "      closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "      R∈dom(closure1) ∧"
						+ "      closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO) ∧"
						+ "      R∈dom(closure1) ∧"
						+ "      closure1∈ℙ(CO × CO) ⇸ ℙ(CO × CO))");
	}

	// public void testPerformance() throws Exception {
	// final int LOOP = 10000;
	// for (int i = 0; i < LOOP; ++ i) {
	// for (TestFormula<?> t: tests) {
	// t.test();
	// }
	// }
	// }

}
