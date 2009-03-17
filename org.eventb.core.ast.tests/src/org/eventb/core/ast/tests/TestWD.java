/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * @author halstefa
 *
 */
public class TestWD extends AbstractTests {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	private static IntegerType INTEGER = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();
	private static GivenType S = ff.makeGivenType("S");

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	ITypeEnvironment defaultTEnv = mTypeEnvironment(
			"x", INTEGER,
			"y", INTEGER,
			"A", POW(INTEGER),
			"B", POW(INTEGER),
			"f", POW(CPROD(INTEGER,INTEGER)),
			"Y", POW(BOOL),
			"S", POW(S)
	);
	
	private abstract class TestFormula {
		// base class of various tests
		TestFormula() { super(); }
		public abstract void test();
	}
	
	private class TestPredicate extends TestFormula {
		String input;
		String expected;
		ITypeEnvironment env;
		TestPredicate(String in, String exp) {
			input = in;
			expected = exp;
			env = defaultTEnv;
		}
		
		@Override
		public void test() {
			Predicate inP = parsePredicate(input);
			ITypeEnvironment newEnv = typeCheck(inP, env);
			
			Predicate inWD = inP.getWDPredicate(ff);
			assertTrue("Ill-formed WD predicate: " + inWD, inWD.isWellFormed());
			assertTrue(input + "\n"
					+ inWD.toString() + "\n"
					+ inWD.getSyntaxTree() + "\n",
					inWD.isTypeChecked());
			
			Predicate exP = parsePredicate(expected).flatten(ff);
			typeCheck(exP, newEnv);
			
			assertEquals(input, exP, inWD);
		}
	}
	
	private class TestAssignment extends TestFormula {
		String input;
		String expected;
		ITypeEnvironment env;
		TestAssignment(String in, String exp) {
			input = in;
			expected = exp;
			env = defaultTEnv;
		}
		
		@Override
		public void test() {
			Assignment inA = parseAssignment(input);
			ITypeEnvironment newEnv = typeCheck(inA, env);
						
			Predicate inWD = inA.getWDPredicate(ff);
			assertTrue("Ill-formed WD predicate: " + inWD, inWD.isWellFormed());
			assertTrue("Untyped WD predicate: " + inWD, inWD.isTypeChecked());
			
			Predicate exP = parsePredicate(expected).flatten(ff);
			typeCheck(exP, newEnv);
			
			assertEquals(input, exP, inWD);
		}
	}
	
	private TestFormula[] formulas = new TestFormula[] {
			new TestPredicate(
					"x≠y ∧ y=1",
					"⊤"
			), new TestPredicate(
					"x+y+x+1=0 ⇒ y<x",
					"⊤"
			), new TestPredicate(
					"x+1=0 ∨ x<y",
					"⊤"
			), new TestPredicate(
					"(∃x \u00b7 0<x ⇒ (∀y \u00b7 y+x=0))",
					"⊤"
			), new TestPredicate(
					"(B×Y)(x) ∈ Y",
					"x∈dom(B × Y) ∧ B × Y ∈ ℤ ⇸ BOOL"
			), new TestPredicate(
					"x=f(f(y))",
					"((y∈dom(f) ∧ f ∈ ℤ ⇸ ℤ)" +
					"∧ f(y)∈dom(f)) ∧ f ∈ ℤ ⇸ ℤ"
			), new TestPredicate(
					"(x÷y=y) ⇔ (y mod x=0)",
					"y≠0 ∧ x≠0"
			), new TestPredicate(
					"∀z \u00b7 x^z>y",
					"∀z \u00b7 0≤x ∧ 0≤z"
			), new TestPredicate(
					"card(A)>x",
					"finite(A)"
			), new TestPredicate(
					"inter({A,B}) ⊆ A∩B",
					"{A,B}≠∅"
			), new TestPredicate(
					"(λ m↦n \u00b7 m>n \u2223 y)(1↦x) = y",
					"1 ↦ x∈dom(λm ↦ n\u00b7m>n ∣ y) " +
					"∧ (λm ↦ n\u00b7m>n ∣ y) ∈ (ℤ×ℤ) ⇸ ℤ"
			), new TestPredicate(
					"{m,n \u00b7 m=f(n) \u2223 m↦n}[A] ⊂ B",
					"∀n \u00b7 n∈dom(f) ∧ f ∈ ℤ ⇸ ℤ"
			), new TestPredicate(
					"{f(n)↦m \u2223 x=n ∧ y+x=m ∧ f ∈ ℤ→A} = A×B",
					"∀f,n,m \u00b7 x=n ∧ y+x=m ∧ f ∈ ℤ→A ⇒ n∈dom(f) ∧ f ∈ ℤ ⇸ ℤ"
			), new TestPredicate(
					"{1, 2, x, x+y, 4, 6} = B",
					"⊤"
			), new TestPredicate(
					"(⋂ m,n \u00b7 m∈A ∧ n∈B \u2223 {m÷n}) = B",
					"(∀m,n \u00b7 (m∈A ∧ n∈B) ⇒ n≠0) ∧ (∃m,n \u00b7 (m∈A ∧ n∈B))"
			), new TestPredicate(
					"(⋂{m+n} \u2223 m+n∈A)=B",
					"∃m,n\u00b7m+n∈A"
			), new TestPredicate(
					"bool(⊤)=bool(⊥)",
					"⊤"
			), new TestPredicate(
					"x+y+(x mod y)\u2217x+1=0 ⇒ y<x",
					"y≠0"
			), new TestAssignment(
					"x≔y",
					"⊤"
			), new TestAssignment(
					"x :\u2223 x'>x",
					"⊤"
			), new TestAssignment(
					"x :∈ {x,y}",
					"⊤"
			), new TestAssignment(
					"x :∈ {x÷y, y}",
					"y≠0"
			), new TestAssignment(
					"f(x)≔f(x)",
					"x∈dom(f)∧f ∈ ℤ ⇸ ℤ"
			), new TestAssignment(
					"x :\u2223 x'=card(A∪{x'})",
					"∀x' \u00b7 finite(A∪{x'})"
			), new TestPredicate(
					"a = {x∣x≤card(A)}",
					"finite(A)"
			), new TestPredicate(
					"a = min(A)",
					"A ≠ ∅ ∧ (∃b·∀x·x∈A ⇒ b≤x)"
			), new TestPredicate(
					"a = max(A)",
					"A ≠ ∅ ∧ (∃b·∀x·x∈A ⇒ b≥x)"
			// Ensure that a type name doesn't get captured
			// when computing a WD lemma
			), new TestPredicate(
					"T ⊆ S ∧ g ∈ ℤ → T ⇒ (∃S·g(S) ∈ T)",
					"T ⊆ S ∧ g ∈ ℤ → T ⇒ (∀S0·S0 ∈ dom(g) ∧ g ∈ ℤ ⇸ S)"
			// Example from the Mobile model
			), new TestPredicate(
					"   a ∈ S ↔ S" +
					" ∧ b ∈ S ↔ (ℤ ↔ S)" +
					" ∧ (∀s·s ∈ dom(a) ⇒ a(s) = b(s)(max(dom(b(s)))))",
					//---------------------------
					"  a∈S ↔ S ∧ b∈S ↔ (ℤ ↔ S)" +
					"⇒ (∀s·s∈dom(a)" +
					"    ⇒ s∈dom(a) ∧ a ∈ S ⇸ S" +
					"    ∧ s∈dom(b) ∧ b ∈ S ⇸ ℙ(ℤ × S)" +
					"    ∧ s∈dom(b) ∧ b ∈ S ⇸ ℙ(ℤ × S)" +
					"    ∧ dom(b(s))≠∅ ∧ (∃b0·∀x·x∈dom(b(s))⇒b0≥x)" +
					"    ∧ max(dom(b(s)))∈dom(b(s))" +
					"    ∧ b(s) ∈ ℤ ⇸ S)"
			// Reduced example extracted from the preceding one
			), new TestPredicate(
					"∀s·max(s) ∈ s",
					"∀s·s≠∅ ∧ (∃b·∀x·x ∈ s  ⇒  b ≥ x)"
			// Case where a bound variable disappears
			), new TestPredicate(
					"∀y·∃x·x = f(y)",
					"∀y·y∈dom(f) ∧ f∈ℤ ⇸ ℤ"
			), new TestPredicate(
					"∀f,y·f∈ℤ → ℤ ⇒ (∃x·x = f(y))",
					"∀f,y·f∈ℤ → ℤ ⇒ y∈dom(f) ∧ f∈ℤ ⇸ ℤ"
			),
	};
	
	public void testWD() {
		for (int i=0; i<formulas.length; i++) {
			formulas[i].test();
		}
	}
	
}
