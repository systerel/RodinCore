/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.prover.classicBtests;
import junit.framework.TestCase;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.externalReasoners.classicB.SyntaxVisitor;

public class TestOldSyntax extends TestCase {
	
	private ITypeEnvironment defaultTEnv = SyntaxUtil.mTypeEnvironment(
			SyntaxUtil.mList(
					"x",
					"y",
					"z",
					"A",
					"B",
					"f",
					"g",
					"h"
			),
			SyntaxUtil.mList(
					SyntaxUtil.INTEGER,
					SyntaxUtil.INTEGER,
					SyntaxUtil.INTEGER,
					SyntaxUtil.POW(SyntaxUtil.INTEGER),
					SyntaxUtil.POW(SyntaxUtil.INTEGER),
					SyntaxUtil.POW(SyntaxUtil.CPROD(SyntaxUtil.INTEGER,SyntaxUtil.INTEGER)),
					SyntaxUtil.POW(SyntaxUtil.CPROD(SyntaxUtil.INTEGER,SyntaxUtil.INTEGER)),
					SyntaxUtil.POW(SyntaxUtil.CPROD(SyntaxUtil.INTEGER,SyntaxUtil.INTEGER))
			)
	);
	
	abstract class TestItem {
		public abstract void test();
	};
	
	class TestOKE extends TestItem {
		public final String input;
		public final String result;
		public TestOKE(String input, String result) {
			this.input = input;
			this.result = result;
		}
		public void test() {
			IParseResult presult = SyntaxUtil.factory.parseExpression(input);
			assertTrue(input + " parsed", presult.isSuccess());
			ITypeCheckResult tresult = presult.getParsedExpression().typeCheck(defaultTEnv);
			assertTrue(input + " type checked", tresult.isSuccess());
			SyntaxVisitor visitor = new SyntaxVisitor();
			presult.getParsedExpression().accept(visitor);
			assertEquals(input, result, visitor.getString());
		}
	}

	class TestOKP extends TestItem {
		public final String input;
		public final String result;
		public TestOKP(String input, String result) {
			this.input = input;
			this.result = result;
		}
		public void test() {
			IParseResult presult = SyntaxUtil.factory.parsePredicate(input);
			assertTrue(input + " parsed", presult.isSuccess());
			ITypeCheckResult tresult = presult.getParsedPredicate().typeCheck(defaultTEnv);
			assertTrue(input + " type checked", tresult.isSuccess());
			SyntaxVisitor visitor = new SyntaxVisitor();
			presult.getParsedPredicate().accept(visitor);
			assertEquals(input, result, visitor.getString());
		}
	}

	class TestKOE extends TestItem {
		public final String input;
		public TestKOE(String input) {
			this.input = input;
		}
		public void test() {
			IParseResult presult = SyntaxUtil.factory.parsePredicate(input);
			assertTrue(input + " parsed", presult.isSuccess());
			ITypeCheckResult tresult = presult.getParsedPredicate().typeCheck(defaultTEnv);
			assertTrue(input + " type checked", tresult.isSuccess());
			SyntaxVisitor visitor = new SyntaxVisitor();
			presult.getParsedPredicate().accept(visitor);
			try {
				visitor.getString();
			} catch (RuntimeException e) {
				return; // ok: this test is supposed to fail!
			}
			assertTrue("should fail", false);
		}
	}

	class TestSPP extends TestItem {
		public final Predicate input;
		public final String result;
		public TestSPP(Predicate input, String result) {
			this.input = input;
			this.result = result;
		}
		public void test() {
			ITypeCheckResult tresult = input.typeCheck(defaultTEnv);
			assertTrue(input + " type checked", tresult.isSuccess());
			SyntaxVisitor visitor = new SyntaxVisitor();
			input.accept(visitor);
			assertEquals(input.toString(), result, visitor.getString());
		}
	}

	class TestSPE extends TestItem {
		public final Expression input;
		public final String result;
		public TestSPE(Expression input, String result) {
			this.input = input;
			this.result = result;
		}
		public void test() {
			ITypeCheckResult tresult = input.typeCheck(defaultTEnv);
			assertTrue(input + " type checked", tresult.isSuccess());
			SyntaxVisitor visitor = new SyntaxVisitor();
			input.accept(visitor);
			assertEquals(input.toString(), result, visitor.getString());
		}
	}

	TestItem[] testItems = new TestItem[] {
			new TestOKP("⊤", "btrue"),
			new TestOKP("⊥", "bfalse"),
			new TestOKP("⊤ ∧ ⊤", "(btrue & btrue)"),
			new TestOKP("⊤ ∨ ⊤", "(btrue or btrue)"),
			new TestOKP("⊤ ⇒ ⊤", "(btrue => btrue)"),
			new TestOKP("¬⊤", "not(btrue)"),
			new TestOKP("(∃x·x∈ℕ)", "# x.((x : INTEGER) & (x : NATURAL))"),
			new TestOKP("(∃x,y·x∈ℕ∧y∈ℕ)", "# (x,y).((x : INTEGER & y : INTEGER) & ((x : NATURAL) & (y : NATURAL)))"),
			new TestOKP("(∀x·x∈ℕ)", "! x.((x : INTEGER) => (x : NATURAL))"),
			new TestOKP("(∀x,y·x∈ℕ∧y∈ℕ)", "! (x,y).((x : INTEGER & y : INTEGER) => ((x : NATURAL) & (y : NATURAL)))"),
			new TestOKE("x", "x"),
			new TestOKE("x+y", "(x + y)"),
			new TestOKE("x+y+z", "(x + y + z)"),
			new TestOKE("x−y", "(x - y)"),
			new TestOKE("x∗y", "(x * y)"),
			new TestOKE("x∗y∗z", "(x * y * z)"),
			new TestOKE("x÷y", "(x / y)"),
			new TestOKE("x mod y", "(x mod y)"),
//			new TestOKE("A*B", "(A * B)"),
//			new TestOKE("A-B", "(A - B)"),
			new TestOKE("A∖B", "(A _moinsE B)"),
			new TestOKE("A×B", "(A _multE B)"),
			new TestOKP("x<y", "(x < y)"),
			new TestOKP("x≤y", "(x <= y)"),
			new TestOKP("x>y", "(x > y)"),
			new TestOKP("x≥y", "(x >= y)"),
			new TestOKP("A⊆B", "(A <: B)"),
			new TestOKP("A⊂B", "(A <<: B)"),
			new TestOKP("A⊈B", "(A /<: B)"),
			new TestOKP("A⊄B", "(A /<<: B)"),
			new TestOKE("A→B", "(A --> B)"),
			new TestOKE("A⇸B", "(A +-> B)"),
			new TestOKE("A↣B", "(A >-> B)"),
			new TestOKE("A⤔B", "(A >+> B)"),
			new TestOKE("A⤖B", "(A >->> B)"),
			new TestOKE("A↠B", "(A -->> B)"),
			new TestOKE("A⤀B", "(A +->> B)"),
			new TestOKE("A↔B", "(A <-> B)"),
//			new TestKOE("AB"),
//			new TestKOE("AB"),
//			new TestKOE("AB"),
			new TestOKE("x↦y", "(x |-> y)"),
			new TestOKE("{x}", "{x}"),
			new TestOKE("{x,y,z}", "{x,y,z}"),
			new TestOKE("A◁f", "(A <| f)"),
			new TestOKE("f▷A", "(f |> A)"),
			new TestOKE("A⩤f", "(A <<| f)"),
			new TestOKE("f⩥A", "(f |>> A)"),
			new TestOKP("x=y", "(x = y)"),
			new TestOKP("x≠y", "(x /= y)"),
			new TestOKP("x∈A", "(x : A)"),
			new TestOKP("x∉A", "(x /: A)"),
			new TestOKE("A∪B", "(A \\/ B)"),
			new TestOKE("A∩B", "(A /\\ B)"),
			new TestOKE("f{x↦y}", "(f <+ {(x |-> y)})"),
			new TestOKE("x‥y", "(x .. y)"),
			new TestOKE("f;g;h", "(f ; g ; h)"),
			new TestOKE("h∘g∘f", "(f ; g ; h)"),
			new TestOKE("f⊗g", "(f >< g)"),
			new TestOKE("f∥g", "(f || g)"),
			new TestOKP("finite(B)", "(B : FIN(POW(INTEGER)))"),
			new TestOKE("TRUE", "TRUE"),
			new TestOKE("FALSE", "FALSE"),
			new TestOKE("BOOL", "BOOL"),
			new TestOKE("bool(⊤)", "bool(btrue)"),
			new TestOKE("f(x)", "f(x)"),
			new TestOKE("prj1(f)", "prj1(f)"),
			new TestOKE("prj2(f)", "prj2(f)"),
			new TestOKE("f∼", "(f)~"),
			new TestOKE("(⋃ x· x∈ℕ ∣ {x})", "UNION x.((x : INTEGER) & (x : NATURAL) | {x})"),
			new TestOKE("(⋃ x,y· x∈ℕ ∧ y∈ℕ ∣ {x,y})", "UNION (x,y).((x : INTEGER & y : INTEGER) & ((x : NATURAL) & (y : NATURAL)) | {x,y})"),
			new TestOKE("(⋂ x· x∈ℕ ∣ {x})", "INTER x.((x : INTEGER) & (x : NATURAL) | {x})"),
			new TestOKE("(⋂ x,y· x∈ℕ ∧ y∈ℕ ∣ {x,y})", "INTER (x,y).((x : INTEGER & y : INTEGER) & ((x : NATURAL) & (y : NATURAL)) | {x,y})"),
			new TestOKE("inter({A,B})", "inter({A,B})"),
			new TestOKE("union({A,B})", "union({A,B})"),
			new TestSPP(SyntaxUtil.forall(SyntaxUtil.BD("m", "n"), SyntaxUtil.exists(SyntaxUtil.BD("n"), SyntaxUtil.eq(SyntaxUtil.plus(SyntaxUtil.bd(0),SyntaxUtil.bd(1)),SyntaxUtil.bd(2)))), 
					"! (m,n).((m : INTEGER & n : INTEGER) => (# n0.((n0 : INTEGER) & ((n0 + n) = m))))"),
//			new TestSPE(set(BD("m", "x"),exists(BD("m"),lt(bd(0),bd(2))),plus(bd(0),bd(1),id_x)), 
//					"{yy | (yy : INTEGER) &  # (m,x0).((m : INTEGER & x0 : INTEGER) & # m0.((m0 : INTEGER) & (m0 < m)) & (yy = (x0 + m + x)))}"),
			new TestSPE(SyntaxUtil.set(SyntaxUtil.BD("m", "x"),SyntaxUtil.exists(SyntaxUtil.BD("m"),SyntaxUtil.lt(SyntaxUtil.bd(0),SyntaxUtil.bd(2))),SyntaxUtil.plus(SyntaxUtil.bd(0),SyntaxUtil.bd(1),SyntaxUtil.id_x)), 
					"SET(yy).((yy : INTEGER) &  # (m,x0).((m : INTEGER & x0 : INTEGER) & # m0.((m0 : INTEGER) & (m0 < m)) & (yy = (x0 + m + x))))")
	};
	
	public void testTranslation() throws Exception {
		for(TestItem item : testItems) {
			item.test();
		}
	}
}
