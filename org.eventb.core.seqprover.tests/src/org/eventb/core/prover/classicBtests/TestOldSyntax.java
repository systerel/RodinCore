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
import org.eventb.core.prover.reasoners.classicB.SyntaxVisitor;

/**
 * @author Stefan Hallerstede
 */

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
			new TestOKP("(∃x·x∈ℕ)", "# x0.((x0 : INTEGER) & (x0 : NATURAL))"),
			new TestOKP("(∃x,y·x∈ℕ∧y∈ℕ)", "# (x0,x1).((x0 : INTEGER & x1 : INTEGER) & ((x0 : NATURAL) & (x1 : NATURAL)))"),
			new TestOKP("(∀x·x∈ℕ)", "! x0.((x0 : INTEGER) => (x0 : NATURAL))"),
			new TestOKP("(∀x,y·x∈ℕ∧y∈ℕ)", "! (x0,x1).((x0 : INTEGER & x1 : INTEGER) => ((x0 : NATURAL) & (x1 : NATURAL)))"),
			new TestOKE("x", "x0"),
			new TestOKE("x+y", "(x0 + x1)"),
			new TestOKE("x+y+z", "(x0 + x1 + x2)"),
			new TestOKE("x−y", "(x0 - x1)"),
			new TestOKE("x∗y", "(x0 * x1)"),
			new TestOKE("x∗y∗z", "(x0 * x1 * x2)"),
			new TestOKE("x÷y", "(x0 / x1)"),
			new TestOKE("x mod y", "(x0 mod x1)"),
//			new TestOKE("A*B", "(A * B)"),
//			new TestOKE("A-B", "(A - B)"),
			new TestOKE("A∖B", "(x0 _moinsE x1)"),
			new TestOKE("A×B", "(x0 _multE x1)"),
			new TestOKP("x<y", "(x0 < x1)"),
			new TestOKP("x≤y", "(x0 <= x1)"),
			new TestOKP("x>y", "(x0 > x1)"),
			new TestOKP("x≥y", "(x0 >= x1)"),
			new TestOKP("A⊆B", "(x0 <: x1)"),
			new TestOKP("A⊂B", "(x0 <<: x1)"),
			new TestOKP("A⊈B", "(x0 /<: x1)"),
			new TestOKP("A⊄B", "(x0 /<<: x1)"),
			new TestOKE("A→B", "(x0 --> x1)"),
			new TestOKE("A⇸B", "(x0 +-> x1)"),
			new TestOKE("A↣B", "(x0 >-> x1)"),
			new TestOKE("A⤔B", "(x0 >+> x1)"),
			new TestOKE("A⤖B", "(x0 >->> x1)"),
			new TestOKE("A↠B", "(x0 -->> x1)"),
			new TestOKE("A⤀B", "(x0 +->> x1)"),
			new TestOKE("A↔B", "(x0 <-> x1)"),
//			new TestKOE("AB"),
//			new TestKOE("AB"),
//			new TestKOE("AB"),
			new TestOKE("x↦y", "(x0 |-> x1)"),
			new TestOKE("{x}", "{x0}"),
			new TestOKE("{x,y,z}", "{x0,x1,x2}"),
			new TestOKE("A◁f", "(x0 <| x1)"),
			new TestOKE("f▷A", "(x0 |> x1)"),
			new TestOKE("A⩤f", "(x0 <<| x1)"),
			new TestOKE("f⩥A", "(x0 |>> x1)"),
			new TestOKP("x=y", "(x0 = x1)"),
			new TestOKP("x≠y", "(x0 /= x1)"),
			new TestOKP("x∈A", "(x0 : x1)"),
			new TestOKP("x∉A", "(x0 /: x1)"),
			new TestOKE("A∪B", "(x0 \\/ x1)"),
			new TestOKE("A∩B", "(x0 /\\ x1)"),
			new TestOKE("f{x↦y}", "(x0 <+ {(x1 |-> x2)})"),
			new TestOKE("x‥y", "(x0 .. x1)"),
			new TestOKE("f;g;h", "(x0 ; x1 ; x2)"),
			new TestOKE("h∘g∘f", "(x2 ; x1 ; x0)"),
			new TestOKE("f⊗g", "(x0 >< x1)"),
			new TestOKE("f∥g", "(x0 || x1)"),
			new TestOKP("finite(B)", "(x0 : FIN(POW(INTEGER)))"),
			new TestOKE("TRUE", "TRUE"),
			new TestOKE("FALSE", "FALSE"),
			new TestOKE("BOOL", "BOOL"),
			new TestOKE("bool(⊤)", "bool(btrue)"),
			new TestOKE("f(x)", "x0(x1)"),
			new TestOKE("f[A]", "(x0[x1])"),
			new TestOKE("prj1(f)", "prj1(x0)"),
			new TestOKE("prj2(f)", "prj2(x0)"),
			new TestOKE("f∼", "(x0)~"),
			new TestOKE("(⋃ x· x∈ℕ ∣ {x})", "UNION x0.((x0 : INTEGER) & (x0 : NATURAL) | {x0})"),
			new TestOKE("(⋃ x,y· x∈ℕ ∧ y∈ℕ ∣ {x,y})", "UNION (x0,x1).((x0 : INTEGER & x1 : INTEGER) & ((x0 : NATURAL) & (x1 : NATURAL)) | {x0,x1})"),
			new TestOKE("(⋂ x· x∈ℕ ∣ {x})", "INTER x0.((x0 : INTEGER) & (x0 : NATURAL) | {x0})"),
			new TestOKE("(⋂ x,y· x∈ℕ ∧ y∈ℕ ∣ {x,y})", "INTER (x0,x1).((x0 : INTEGER & x1 : INTEGER) & ((x0 : NATURAL) & (x1 : NATURAL)) | {x0,x1})"),
			new TestOKE("inter({A,B})", "inter({x0,x1})"),
			new TestOKE("union({A,B})", "union({x0,x1})"),
			new TestSPP(SyntaxUtil.forall(SyntaxUtil.BD("m", "n"), SyntaxUtil.exists(SyntaxUtil.BD("n"), SyntaxUtil.eq(SyntaxUtil.plus(SyntaxUtil.bd(0),SyntaxUtil.bd(1)),SyntaxUtil.bd(2)))), 
					"! (x2,x1).((x2 : INTEGER & x1 : INTEGER) => (# x0.((x0 : INTEGER) & ((x0 + x1) = x2))))"),
//			new TestSPE(set(BD("m", "x"),exists(BD("m"),lt(bd(0),bd(2))),plus(bd(0),bd(1),id_x)), 
//					"{yy | (yy : INTEGER) &  # (m,x0).((m : INTEGER & x0 : INTEGER) & # m0.((m0 : INTEGER) & (m0 < m)) & (yy = (x0 + m + x)))}"),
			new TestSPE(SyntaxUtil.set(SyntaxUtil.BD("m", "x"),SyntaxUtil.exists(SyntaxUtil.BD("m"),SyntaxUtil.lt(SyntaxUtil.bd(0),SyntaxUtil.bd(2))),SyntaxUtil.plus(SyntaxUtil.bd(0),SyntaxUtil.bd(1),SyntaxUtil.id_x)), 
					"SET(x4).((x4 : INTEGER) &  # (x1,x2).((x1 : INTEGER & x2 : INTEGER) & # x0.((x0 : INTEGER) & (x0 < x1)) & (x4 = (x2 + x1 + x3))))")
	};
	
	public void testTranslation() throws Exception {
		for(TestItem item : testItems) {
			item.test();
		}
	}
}
