/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.prover.classicBtests;
import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.prover.externalReasoners.classicB.SyntaxVisitor;

public class TestOldSyntax extends TestCase {
	
	private static FormulaFactory factory = FormulaFactory.getDefault();
	
	public static String[] mList(String... names) {
		return names;
	}

	public static Type[] mList(Type... types) {
		return types;
	}

	private static IntegerType INTEGER = factory.makeIntegerType();
	private static BooleanType BOOL = factory.makeBooleanType();

	private static Type POW(Type base) {
		return factory.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return factory.makeProductType(left, right);
	}
	
	public static ITypeEnvironment mTypeEnvironment(String[] names, Type[] types) {
		assert names.length == types.length;
		ITypeEnvironment result = factory.makeTypeEnvironment();
		for (int i = 0; i < names.length; i++) {
			result.addName(names[i], types[i]);
		}
		return result;
	}

	private ITypeEnvironment defaultTEnv = mTypeEnvironment(
			mList(
					"x",
					"y",
					"z",
					"A",
					"B",
					"f",
					"g",
					"h"
			),
			mList(
					INTEGER,
					INTEGER,
					INTEGER,
					POW(INTEGER),
					POW(INTEGER),
					POW(CPROD(INTEGER,INTEGER)),
					POW(CPROD(INTEGER,INTEGER)),
					POW(CPROD(INTEGER,INTEGER))
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
			IParseResult presult = factory.parseExpression(input);
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
			IParseResult presult = factory.parsePredicate(input);
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
			IParseResult presult = factory.parsePredicate(input);
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

	private static FreeIdentifier[] mi(FreeIdentifier...freeIdentifiers) {
		return freeIdentifiers;
	}
	
	private static Integer[] mj(Integer...integers) {
		return integers;
	}
	
	private static Expression[] me(Expression...expressions) {
		return expressions;
	}
	
	private static Predicate[] mp(Predicate...predicates) {
		return predicates;
	}
	
	private static Predicate eq(Expression l, Expression r) {
		return factory.makeRelationalPredicate(Formula.EQUAL, l, r, null);
	}
	
	private static Predicate lt(Expression l, Expression r) {
		return factory.makeRelationalPredicate(Formula.LT, l, r, null);
	}
	
	private static QuantifiedPredicate forall(BoundIdentDecl[] bd, Predicate pr) {
		return factory.makeQuantifiedPredicate(Formula.FORALL, bd, pr, null);
	}
	
	private static QuantifiedPredicate exists(BoundIdentDecl[] bd, Predicate pr) {
		return factory.makeQuantifiedPredicate(Formula.EXISTS, bd, pr, null);
	}
	
	private static BoundIdentDecl[] BD(String...strings) {
		BoundIdentDecl[] bd = new BoundIdentDecl[strings.length];
		for(int i=0; i<strings.length; i++)
			bd[i] = factory.makeBoundIdentDecl(strings[i], null);
		return bd;
	}
	
	private static BoundIdentifier bd(int i) {
		return factory.makeBoundIdentifier(i, null);
	}
	
	private static Expression apply(Expression l, Expression r) {
		return factory.makeBinaryExpression(Formula.FUNIMAGE, l, r, null);
	}
	
	private static Expression num(int i) {
		return factory.makeIntegerLiteral(BigInteger.valueOf(i), null);
	}
	
	private static Expression plus(Expression...expressions) {
		return factory.makeAssociativeExpression(Formula.PLUS, expressions, null);
	}
	
	private static Expression minus(Expression l, Expression r) {
		return factory.makeBinaryExpression(Formula.MINUS, l, r, null);
	}
	
	private static Predicate in(Expression l, Expression r) {
		return factory.makeRelationalPredicate(Formula.IN, l, r, null);
	}
	
	private static Expression fun(BoundIdentDecl[] d, Predicate p, Expression e) {
		return factory.makeQuantifiedExpression(Formula.CSET, d, p, e, null, QuantifiedExpression.Form.Lambda);
	}
	
	private static Expression set(BoundIdentDecl[] d, Predicate p, Expression e) {
		return factory.makeQuantifiedExpression(Formula.CSET, d, p, e, null, QuantifiedExpression.Form.Explicit);
	}
	
	private static Predicate limp(Predicate l, Predicate r) {
		return factory.makeBinaryPredicate(Formula.LIMP, l, r, null);
	}
	
	private static Expression maplet(Expression l, Expression r) {
		return factory.makeBinaryExpression(Formula.MAPSTO, l, r,null);
	}
	
	private static FreeIdentifier id_x = factory.makeFreeIdentifier("x", null);
	private static FreeIdentifier id_y = factory.makeFreeIdentifier("y", null);
	private static FreeIdentifier id_A = factory.makeFreeIdentifier("A", null);
	private static FreeIdentifier id_f = factory.makeFreeIdentifier("f", null);

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
			new TestSPP(forall(BD("m", "n"), exists(BD("n"), eq(plus(bd(0),bd(1)),bd(2)))), 
					"! (m,n).((m : INTEGER & n : INTEGER) => (# n0.((n0 : INTEGER) & ((n0 + n) = m))))"),
//			new TestSPE(set(BD("m", "x"),exists(BD("m"),lt(bd(0),bd(2))),plus(bd(0),bd(1),id_x)), 
//					"{yy | (yy : INTEGER) &  # (m,x0).((m : INTEGER & x0 : INTEGER) & # m0.((m0 : INTEGER) & (m0 < m)) & (yy = (x0 + m + x)))}"),
			new TestSPE(set(BD("m", "x"),exists(BD("m"),lt(bd(0),bd(2))),plus(bd(0),bd(1),id_x)), 
					"SET(yy).((yy : INTEGER) &  # (m,x0).((m : INTEGER & x0 : INTEGER) & # m0.((m0 : INTEGER) & (m0 < m)) & (yy = (x0 + m + x))))")
	};
	
	public void testTranslation() throws Exception {
		for(TestItem item : testItems) {
			item.test();
		}
	}
}
