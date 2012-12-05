/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - test for bug #3574565
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mInferredTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.InjectedDatatypeExtension.injectExtension;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;

/**
 * Unit test of the mathematical formula Type-Checker.
 *
 * @author franz
 */
public class TestTypeChecker extends AbstractTests {

	private static IntegerType INTEGER = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();

	private static GivenType ty_L = ff.makeGivenType("L");
	private static GivenType ty_N = ff.makeGivenType("N");
	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");
	private static GivenType ty_U = ff.makeGivenType("U");
	private static GivenType ty_V = ff.makeGivenType("V");

	// Construction of a given type with a name "S" that is not canonic (not
	// gone through intern())
	private static GivenType ty_S2 = ff.makeGivenType(new String(
			new char[] { 'S' }));

	/**
	 * Main test routine for predicates.
	 */
	public void testTypeChecker() {
		testPredicate(
				"x∈ℤ∧1≤x",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testPredicate("x⊆S∧∅⊂x",
				mTypeEnvironment("S", POW(ty_S)),
				mTypeEnvironment("x", POW(ty_S))
		);
		testPredicate("∅=∅",
				mTypeEnvironment(),
				null
		);
		testPredicate("x=TRUE",
				mTypeEnvironment("x", INTEGER),
				null
		);
		testPredicate("x=TRUE",
				mTypeEnvironment("x", BOOL),
				mTypeEnvironment()
		);
		testPredicate("x=TRUE",
				mTypeEnvironment(),
				mTypeEnvironment("x", BOOL)
		);
		testPredicate("M = {A ∣ A ∉ A}",
				mTypeEnvironment(),
				null
		);
		testPredicate("x>x",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testPredicate("x∈y∧y∈x",
				mTypeEnvironment(),
				null
		);
		testPredicate("x∈ℙ(y)∧y∈ℙ(x)",
				mTypeEnvironment("x", POW(BOOL)),
				mTypeEnvironment("y", POW(BOOL))
		);
		testPredicate("⊥",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		testPredicate("⊤",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		testPredicate("finite(x)",
				mTypeEnvironment(),
				null
		);
		testPredicate("finite(x)",
				mTypeEnvironment("x", POW(INTEGER)),
				mTypeEnvironment()
		);
		testPredicate("x=x",
				mTypeEnvironment(),
				null
		);
		testPredicate("x≠x",
				mTypeEnvironment(),
				null
		);
		testPredicate("x<x",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testPredicate("x≤x",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testPredicate("x>x",
				mTypeEnvironment("x", BOOL),
				null
		);
		testPredicate("x≥x",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testPredicate("x∈S",
				mTypeEnvironment(),
				null
		);
		testPredicate("x∈S",
				mTypeEnvironment("x", INTEGER),
				mTypeEnvironment("S", POW(INTEGER))
		);
		testPredicate("x∈S", mTypeEnvironment("x", ty_S),
				mTypeEnvironment()
		);
		testPredicate("x∉S",
				mTypeEnvironment("x", ty_S),
				mTypeEnvironment()
		);
		testPredicate("x⊂S",
				mTypeEnvironment(),
				null
		);
		testPredicate("x⊂S",
				mTypeEnvironment("x", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("x⊄S",
				mTypeEnvironment("x", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("x⊆S",
				mTypeEnvironment("x", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("x⊈S",
				mTypeEnvironment("x", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("partition(S, {x},{y})",
				mTypeEnvironment("x", ty_S),
				mTypeEnvironment("S", POW(ty_S), "y", ty_S)
		);
		// LiteralPredicate
		testPredicate("¬⊥",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		// SimplePredicate
		testPredicate("⊥∧⊥",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		testPredicate("⊥∨⊥",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		testPredicate("⊥∧⊥∧⊥",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		testPredicate("⊥∨⊥∨⊥",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		// UnquantifiedPredicate
		testPredicate("⊥⇒⊥",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		testPredicate("⊥⇔⊥",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
		// Predicate + IdentList + Quantifier
		testPredicate("∀x·⊥",
				mTypeEnvironment(),
				null
		);
		// Bound variable "x" has a different type from free variable "x"
		testPredicate("∀ x · x ∈ ℤ",
				mTypeEnvironment("x", BOOL),
				mTypeEnvironment()
		);
		testPredicate("∃ x · x ∈ ℤ",
				mTypeEnvironment("x", BOOL),
				mTypeEnvironment()
		);
		testPredicate("∀ x,y,z · ⊥",
				mTypeEnvironment("x", BOOL, "y", BOOL, "z", BOOL),
				null
		);
		testPredicate("∀ x,y · x ∈ y ∧ y ⊆ ℤ",
				mTypeEnvironment("x", BOOL),  // Not used.
				mTypeEnvironment()
		);
		testPredicate("∃ x,y,z · x ∈ y ∧ x ∈ z ∧ z ⊆ S",
				mTypeEnvironment("S", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("∀ x,y · ∀ s,t · x ∈ s ∧ y ∈ t ∧ s ∩ t ⊆ S",
				mTypeEnvironment("S", POW(ty_S)),
				mTypeEnvironment()
		);
		// SimpleExpression
		testPredicate("bool(⊥)=y",
				mTypeEnvironment(),
				mTypeEnvironment("y", BOOL)
		);
		testPredicate("card(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("card(x)=y",
				mTypeEnvironment("x", ty_S),
				null
		);
		testPredicate("card(x)=y",
				mTypeEnvironment("x", POW(ty_S)),
				mTypeEnvironment("y", INTEGER)
		);
		testPredicate("ℙ(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("ℙ(x)=y",
				mTypeEnvironment("y", POW(POW(INTEGER))),
				mTypeEnvironment("x", POW(INTEGER))
		);
		testPredicate("ℙ1(x)=y",
				mTypeEnvironment("y", POW(POW(INTEGER))),
				mTypeEnvironment("x", POW(INTEGER))
		);
		testPredicate("union(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("union(x)=y",
				mTypeEnvironment("y", POW(ty_S)),
				mTypeEnvironment("x", POW(POW(ty_S)))
		);
		testPredicate("inter(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("inter(x)=y",
				mTypeEnvironment("y", POW(ty_S)),
				mTypeEnvironment("x", POW(POW(ty_S)))
		);
		testPredicate("dom(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("dom(x)=y",
				mTypeEnvironment("x", POW(CPROD(INTEGER,ty_S))),
				mTypeEnvironment("y", POW(INTEGER))
		);
		testPredicate("ran(x)=y",
				mTypeEnvironment("x", POW(CPROD(INTEGER,ty_S))),
				mTypeEnvironment("y", POW(ty_S))
		);
		testPredicate("prj1(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("prj1(x)=y",
				mTypeEnvironment("x=ℤ↔BOOL", ffV1),
				mTypeEnvironment("y=ℤ×BOOL↔ℤ", ffV1)
		);
		testPredicate("x◁prj1=y",
				mTypeEnvironment("x", REL(ty_S, ty_T)),
				mTypeEnvironment("y", REL(CPROD(ty_S, ty_T), ty_S))
		);
		testPredicate("prj2(x)=y",
				mTypeEnvironment("x=ℤ↔BOOL", ffV1),
				mTypeEnvironment("y=ℤ×BOOL↔BOOL", ffV1)
		);
		testPredicate("x◁prj2=y",
				mTypeEnvironment("x", REL(ty_S, ty_T)),
				mTypeEnvironment("y", REL(CPROD(ty_S, ty_T), ty_T))
		);
		testPredicate("id(x)=y",
				mTypeEnvironment("x=ℙ(S)", ffV1),
				mTypeEnvironment("y=S↔S", ffV1)
		);
		testPredicate("x◁id=y",
				mTypeEnvironment("x", POW(ty_S)),
				mTypeEnvironment("y", REL(ty_S,ty_S))
		);
		testPredicate("id(x)=y",
				mTypeEnvironment("x", ty_S),
				mTypeEnvironment("y", ty_S)
		);
		testPredicate("{x,y·⊥∣z}=a",
				mTypeEnvironment(),
				null
		);
		testPredicate("{x,y·⊥∣z}=a",
				mTypeEnvironment("z", INTEGER),
				null
		);
		testPredicate("{x · x ∈ z ∣ z}=a",
				mTypeEnvironment("a", POW(POW(BOOL))),
				mTypeEnvironment("z", POW(BOOL))
		);
		testPredicate("{x · ⊥ ∣ x}=a",
				mTypeEnvironment("a", POW(INTEGER)),
				mTypeEnvironment()
		);
		testPredicate("{x+y∣⊥}=a",
				mTypeEnvironment(),
				mTypeEnvironment("a", POW(INTEGER))
		);
		testPredicate("{}={}",
				mTypeEnvironment(),
				null
		);
		testPredicate("a=∅",
				mTypeEnvironment("a", POW(ty_N)),
				mTypeEnvironment()
		);
		testPredicate("a=∅",
				mTypeEnvironment("a", POW(CPROD(ty_N,ty_N))),
				mTypeEnvironment()
		);
		testPredicate("∅=a",
				mTypeEnvironment("a", POW(ty_N)),
				mTypeEnvironment()
		);
		testPredicate("∅=a",
				mTypeEnvironment("a", POW(CPROD(ty_N,ty_N))),
				mTypeEnvironment()
		);
		testPredicate("{x}=a",
				mTypeEnvironment("x", INTEGER),
				mTypeEnvironment("a", POW(INTEGER))
		);
		testPredicate("{x,y,z}=a",
				mTypeEnvironment("x", INTEGER),
				mTypeEnvironment("y", INTEGER, "z", INTEGER, "a", POW(INTEGER))
		);
		testPredicate("x∈ℤ",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testPredicate("x∈ℕ",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testPredicate("x∈ℕ1",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testPredicate("x∈BOOL",
				mTypeEnvironment(),
				mTypeEnvironment("x", BOOL)
		);
		testPredicate("x=FALSE",
				mTypeEnvironment(),
				mTypeEnvironment("x", BOOL)
		);
		testPredicate("x=pred",
				mTypeEnvironment(),
				mTypeEnvironment("x", REL(INTEGER, INTEGER))
		);
		testPredicate("x=succ",
				mTypeEnvironment(),
				mTypeEnvironment("x", REL(INTEGER, INTEGER))
		);
		testPredicate("x=2",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		// Primary
		testPredicate("x∼=y",
				mTypeEnvironment("x", POW(CPROD(INTEGER,BOOL))),
				mTypeEnvironment("y", POW(CPROD(BOOL,INTEGER)))
		);
		// Image
		testPredicate("f(x)=a",
				mTypeEnvironment("f", POW(CPROD(INTEGER,BOOL))),
				mTypeEnvironment("x", INTEGER, "a", BOOL)
		);
		testPredicate("f[x]=a",
				mTypeEnvironment("f", POW(CPROD(INTEGER,BOOL))),
				mTypeEnvironment("x", POW(INTEGER), "a", POW(BOOL))
		);
		testPredicate("f[x](y)=a",
				mTypeEnvironment("f", REL(ty_S, CPROD(ty_T, ty_U))),
				mTypeEnvironment("x", POW(ty_S), "y", ty_T, "a", ty_U)
		);
		testPredicate("f(x)[y]=a",
				mTypeEnvironment("f", REL(ty_S, REL(ty_T, ty_U))),
				mTypeEnvironment("x", ty_S, "y", POW(ty_T), "a", POW(ty_U))
		);
		testPredicate("f(x)(y)=a",
				mTypeEnvironment("f", REL(ty_S, REL(ty_T, ty_U))),
				mTypeEnvironment("x", ty_S, "y", ty_T, "a", ty_U)
		);
		testPredicate("f[x][y]=a",
				mTypeEnvironment("f", REL(ty_S, CPROD(ty_T, ty_U))),
				mTypeEnvironment("x", POW(ty_S), "y", POW(ty_T), "a", POW(ty_U))
		);

		// Factor
		testPredicate("x^y=a",
				mTypeEnvironment(),
				mTypeEnvironment("a", INTEGER, "x", INTEGER, "y", INTEGER)
		);

		// Term
		testPredicate("x∗x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER
				)
		);
		testPredicate("x∗x∗x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER
				)
		);
		testPredicate("x÷x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER
				)
		);
		testPredicate("x mod x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER
				)
		);
		// ArithmeticExpr
		testPredicate("x+y=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER
				)
		);
		testPredicate("x+y+x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER
				)
		);
		testPredicate("−x+y+z=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER,
						"z", INTEGER
				)
		);
		testPredicate("x−y=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER
				)
		);
		testPredicate("x−y−z=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER,
						"z", INTEGER
				)
		);
		testPredicate("−x−y=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER
				)
		);
		testPredicate("x−y+z−x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER,
						"z", INTEGER
				)
		);
		testPredicate("−x−y+z−x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER,
						"z", INTEGER
				)
		);
		testPredicate("x+y−z+x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER,
						"z", INTEGER
				)
		);
		testPredicate("−x+y−z+x=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", INTEGER,
						"x", INTEGER,
						"y", INTEGER,
						"z", INTEGER
				)
		);
		// IntervalExpr
		testPredicate("x‥y=a",
				mTypeEnvironment(),
				mTypeEnvironment(
						"a", POW(INTEGER),
						"x", INTEGER,
						"y", INTEGER
				)
		);
		// RelationExpr
		testPredicate("x⊗y=a",
				mTypeEnvironment(
						"x", POW(CPROD(ty_S,ty_T)),
						"y", POW(CPROD(ty_S,ty_U))
				),
				mTypeEnvironment("a", POW(CPROD(ty_S,CPROD(ty_T,ty_U))))
		);
		testPredicate("x;y=a",
				mTypeEnvironment(
						"a", POW(CPROD(ty_S,ty_T)),
						"x", POW(CPROD(ty_S,ty_U))
				),
				mTypeEnvironment("y", POW(CPROD(ty_U,ty_T)))
		);
		testPredicate("x;y;z=a",
				mTypeEnvironment(
						"a", REL(ty_S,ty_T),
						"x", REL(ty_S,ty_U),
						"z", REL(ty_V, ty_T)
				),
				mTypeEnvironment("y", POW(CPROD(ty_U,ty_V)))
		);
		testPredicate("x▷y=a",
				mTypeEnvironment(
						"x", POW(CPROD(ty_S,ty_T))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"a", POW(CPROD(ty_S,ty_T))
				)
		);
		testPredicate("x⩥y=a",
				mTypeEnvironment(
						"x", POW(CPROD(ty_S,ty_T))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"a", POW(CPROD(ty_S,ty_T))
				)
		);
		testPredicate("x∩y=a",
				mTypeEnvironment(
						"x", POW(ty_T)
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"a", POW(ty_T)
				)
		);
		testPredicate("x∩y∩z=a",
				mTypeEnvironment(
						"x", POW(ty_T)
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"z", POW(ty_T),
						"a", POW(ty_T)
				)
		);
		testPredicate("x∖y=a",
				mTypeEnvironment(
						"x", POW(ty_T)
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"a", POW(ty_T)
				)
		);
		testPredicate("x;y⩥z=a",
				mTypeEnvironment("x", REL(ty_S, ty_T), "z", POW(ty_U)),
				mTypeEnvironment("y", REL(ty_T, ty_U), "a", REL(ty_S, ty_U))
		);
		testPredicate("x∩y⩥z=a",
				mTypeEnvironment("x", REL(ty_S, ty_T)),
				mTypeEnvironment(
						"y", REL(ty_S, ty_T),
						"z", POW(ty_T),
						"a", REL(ty_S, ty_T)
				)
		);
		testPredicate("x∩y∖z=a",
				mTypeEnvironment("x", REL(ty_S, ty_T)),
				mTypeEnvironment(
						"y", REL(ty_S, ty_T),
						"z", REL(ty_S, ty_T),
						"a", REL(ty_S, ty_T)
				)
		);

		// SetExpr
		testPredicate("x∪y=a",
				mTypeEnvironment(
						"x", POW(ty_T)
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"a", POW(ty_T)
				)
		);
		testPredicate("x∪y∪z=a",
				mTypeEnvironment(
						"x", POW(ty_T)
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"z", POW(ty_T),
						"a", POW(ty_T)
				)
		);
		testPredicate("x×y=a",
				mTypeEnvironment(
						"a", POW(CPROD(ty_S,ty_T))
				),
				mTypeEnvironment(
						"x", POW(ty_S),
						"y", POW(ty_T)
				)
		);
		testPredicate("x×y×z=a",
				mTypeEnvironment(
						"a", POW(CPROD(CPROD(ty_S,ty_T),ty_U))
				),
				mTypeEnvironment(
						"x", POW(ty_S),
						"y", POW(ty_T),
						"z", POW(ty_U)
				)
		);
		testPredicate("xy=a",
				mTypeEnvironment(
						"a", POW(CPROD(ty_S,ty_T))
				),
				mTypeEnvironment(
						"x", POW(CPROD(ty_S,ty_T)),
						"y", POW(CPROD(ty_S,ty_T))
				)
		);
		testPredicate("xyz=a",
				mTypeEnvironment(
						"a", POW(CPROD(ty_S,ty_T))
				),
				mTypeEnvironment(
						"x", POW(CPROD(ty_S,ty_T)),
						"y", POW(CPROD(ty_S,ty_T)),
						"z", POW(CPROD(ty_S,ty_T))
				)
		);
		testPredicate("f ∘ g = a",
				mTypeEnvironment(
						"f", POW(CPROD(ty_T,ty_U)),
						"a", POW(CPROD(ty_S,ty_U))
				),
				mTypeEnvironment(
						"g", POW(CPROD(ty_S,ty_T))
				)
		);
		testPredicate("f ∘ g ∘ h = a",
				mTypeEnvironment(
						"f", POW(CPROD(ty_U,ty_V)),
						"h", POW(CPROD(ty_S,ty_T))
				),
				mTypeEnvironment(
						"a", POW(CPROD(ty_S,ty_V)),
						"g", POW(CPROD(ty_T,ty_U))
				)
		);
		testPredicate("x∥y=a",
				mTypeEnvironment(),
				null
		);
		testPredicate("x∥y=a",
				mTypeEnvironment(
						"x", POW(CPROD(ty_S,ty_U)),
						"y", POW(CPROD(ty_T,ty_V))
				),
				mTypeEnvironment(
						"a", POW(CPROD(CPROD(ty_S,ty_T),CPROD(ty_U,ty_V)))
				)
		);
		testPredicate("x◁y=a",
				mTypeEnvironment(
						"y", POW(CPROD(ty_S,ty_T))
				),
				mTypeEnvironment(
						"x", POW(ty_S),
						"a", POW(CPROD(ty_S,ty_T))
				)
		);
		testPredicate("x⩤y=a",
				mTypeEnvironment(
						"y", POW(CPROD(ty_S,ty_T))
				),
				mTypeEnvironment(
						"x", POW(ty_S),
						"a", POW(CPROD(ty_S,ty_T))
				)
		);
		// RelationalSetExpr
		testPredicate("xy=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(xy)z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("xy=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(xy)z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("xy=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(xy)z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("x⤀y=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(x⤀y)⤀z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("x⤔y=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(x⤔y)⤔z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("x⤖y=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(x⤖y)⤖z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("x→y=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(x→y)→z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("x↔y=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(x↔y)↔z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("x↠y=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(x↠y)↠z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("x↣y=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(x↣y)↣z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("x⇸y=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(ty_S,ty_T)))
				),
				mTypeEnvironment(
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		testPredicate("(x⇸y)⇸z=a",
				mTypeEnvironment(
						"a", POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U)))
				),
				mTypeEnvironment(
						"z", POW(ty_U),
						"y", POW(ty_T),
						"x", POW(ty_S)
				)
		);
		// PairExpr
		testPredicate("x↦y=a",
				mTypeEnvironment(
						"a", CPROD(ty_S,ty_T)
				),
				mTypeEnvironment(
						"x", ty_S,
						"y", ty_T
				)
		);
		testPredicate("a=x↦y",
				mTypeEnvironment(
						"a", CPROD(ty_S,ty_T)
				),
				mTypeEnvironment(
						"x", ty_S,
						"y", ty_T
				)
		);
		// QuantifiedExpr & IdentPattern
		// UnBound
		testPredicate("finite(λ x·⊥∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				null
		);
		testPredicate("finite(λ x· x∈ℤ ∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("finite(λ x↦y·⊥∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				null
		);
		testPredicate("finite(λ x↦y· " +
				"x↦y∈ℤ×ℤ ∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("finite(λ x↦y↦s·⊥∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				null
		);
		testPredicate("finite(λ x↦y↦s·" +
				"x↦y↦s∈ℤ×ℤ×ℤ" +
				"∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("finite(λ x↦(y↦s)·⊥∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				null
		);
		testPredicate("finite(λ x↦(y↦s)·" +
				"x↦y↦s∈ℤ×ℤ×ℤ" +
				"∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				mTypeEnvironment()
		);

		// Bound
		testPredicate("a = (λ x·⊥∣x)",
				mTypeEnvironment("a", REL(ty_S, ty_S)),
				mTypeEnvironment()
		);
		testPredicate("a = (λ x↦y·⊥∣y)",
				mTypeEnvironment("a", REL(CPROD(ty_S, ty_T), ty_T)),
				mTypeEnvironment()
		);
		testPredicate("a = (λ x↦y↦s·⊥∣s)",
				mTypeEnvironment("a", REL(CPROD(CPROD(ty_S, ty_T), ty_U), ty_U)),
				mTypeEnvironment()
		);
		testPredicate("a = (λ x↦(y↦s)·⊥∣s)",
				mTypeEnvironment("a", REL(CPROD(ty_S, CPROD(ty_T, ty_U)), ty_U)),
				mTypeEnvironment()
		);

		// UnBound
		testPredicate("finite(⋃x·⊥∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				null
		);
		testPredicate("finite(⋃x· x∈ℤ ∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("finite(⋃y,x·⊥∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				null
		);
		testPredicate("finite(⋃y,x·" +
				"x↦y∈ℤ×ℤ ∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("finite(⋃s,y,x·⊥∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				null
		);
		testPredicate("finite(⋃s,y,x·" +
				"x↦y↦s∈ℤ×ℤ×ℤ" +
				"∣z)",
				mTypeEnvironment("z", POW(ty_S)),
				mTypeEnvironment()
		);

		// Bound
		testPredicate("(⋃ x · ⊥ ∣ x) = a",
				mTypeEnvironment("a", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("(⋃y,x·⊥∣y ▷ x) = a",
				mTypeEnvironment("a", REL(ty_S, ty_T)),
				mTypeEnvironment()
		);
		testPredicate("(⋃s,y,x·⊥∣ (s▷y)▷x) = a",
				mTypeEnvironment("a", REL(ty_S, ty_T)),
				mTypeEnvironment()
		);

		// Implicitly Bound
		testPredicate("(⋃x∣⊥) = a",
				mTypeEnvironment("a", POW(ty_S)),
				mTypeEnvironment()
		);
		testPredicate("(⋃y∩x∣⊥) = a",
				mTypeEnvironment("a", POW(ty_S)),
				mTypeEnvironment()
		);

		// Special formulas
		testPredicate("∀ s · N◁id ⊆ s ∧ s ; r ⊆ s ⇒ c ⊆ s",
				mTypeEnvironment("N", POW(ty_N)),
				mTypeEnvironment(
						"r", POW(CPROD(ty_N,ty_N)),
						"c", POW(CPROD(ty_N,ty_N))
				)
		);
		testPredicate("(λ x ↦ y ↦ z · x < y ∧ z ∈ ℤ∣ H ) ( f ( 1 ) ) ∈ ℙ ( ℤ )",
				mTypeEnvironment(),
				mTypeEnvironment(
						"H", POW(INTEGER),
						"f", POW(CPROD(INTEGER,CPROD(CPROD(INTEGER,INTEGER),INTEGER)))
				)
		);
		testPredicate(
				" ultraf = { " +
				" f ∣ f ∈ filter ∧ " +
				" (∀ g · g ∈ filter ∧ f ⊆ g ⇒ f = g) " +
				" } " +
				" ∧ filter = { " +
				" h ∣ h ∈ ℙ ( ℙ ( S ) ) ∧ " +
				" S ∈ h ∧" +
				" ∅ ∉ h ∧" +
				" ( ∀ a, b · a ∈ h ∧ a ⊆ b ⇒ b ∈ h ) ∧ " +
				" ( ∀ c, d · c ∈ h ∧ d ∈ h ⇒ c ∩ d ∈ h )" +
				" } ",
				mTypeEnvironment("S", POW(ty_S)),
				mTypeEnvironment(
						"filter", POW(POW(POW(ty_S))),
						"ultraf", POW(POW(POW(ty_S)))
				)
		);
		testPredicate(
                " filter = { " +
                " h ∣ h ∈ ℙ ( ℙ ( S ) ) ∧ " +
                " S ∈ h ∧" +
                " ∅ ∉ h ∧" +
                " ( ∀ a, b · a ∈ h ∧ a ⊆ b ⇒ b ∈ h ) ∧ " +
                " ( ∀ c, d · c ∈ h ∧ d ∈ h ⇒ c ∩ d ∈ h )" +
                " } ∧ " +
                " ultraf = { " +
                " f ∣ f ∈ filter ∧ " +
                " (∀ g · g ∈ filter ∧ f ⊆ g ⇒ f = g) " +
                " } ",
				mTypeEnvironment("S", POW(ty_S)),
				mTypeEnvironment(
						"filter", POW(POW(POW(ty_S))),
						"ultraf", POW(POW(POW(ty_S)))
				)
		);
		testPredicate("N◁id ∩ g = ∅",
				mTypeEnvironment("N", POW(ty_N)),
				mTypeEnvironment(
						"g", POW(CPROD(ty_N,ty_N))
				)
		);
		testPredicate(
                " g = g∼ ∧ " +
                " id ∩ g = ∅ ∧ " +
                " dom(g) = N ∧ " +
                " h ∈ N ↔ ( N ⤀ N ) ∧ " +
                " (∀n,f·" +
                "    n ∈ N ∧ " +
                "    f ∈ N ⤀ N" +
                "    ⇒" +
                "    (n ↦ f ∈ h" +
                "     ⇔" +
                "     (f ∈ N ∖ {n} ↠ N ∧ " +
                "      f ⊆ g ∧ " +
                "      (∀ S · n ∈ S ∧ f∼[S] ⊆ S ⇒ N ⊆ S)" +
                "     )" +
                "    )" +
                " )",
				mTypeEnvironment("N", POW(ty_N)),
				mTypeEnvironment(
						"g", POW(CPROD(ty_N,ty_N)),
						"h", POW(CPROD(ty_N,POW(CPROD(ty_N,ty_N))))
				)
		);
		testPredicate(
                " com ∩ id = ∅ ∧ " +
                " exit ∈ L ∖ {outside} ↠ L ∧ " +
                " exit ⊆ com ∧ " +
                " ( ∀ s · s ⊆ exit∼[s] ⇒ s = ∅ ) ∧ " +
                " aut ⩥ {outside} ⊆ (aut ; exit∼) ∧ " +
                " ( ∃ l · l ∈ L ∖ {outside} ∧ outside ↦ l ∈ com ∧ L×{l} ⊆ aut )",
				mTypeEnvironment("L", POW(ty_L)),
				mTypeEnvironment(
						"aut", POW(CPROD(ty_L,ty_L)),
						"com", POW(CPROD(ty_L,ty_L)),
						"outside", ty_L,
						"exit", POW(CPROD(ty_L,ty_L))
				)
		);
		testPredicate(
                " f ∈ ℙ(S) ↠ ℙ(S) ∧ " +
                " (∀ a, b · a ⊆ b ⇒ f(a) ⊆ f(b)) ∧ " +
                " fix = inter({s ∣ f(s) ⊆ s}) ∧ " +
                " (∀ s · f(s) ⊆ s ⇒ fix ⊆ s) ∧ " +
                " (∀ v · (∀ w · f(w) ⊆ w ⇒ v ⊆ w) ⇒ v ⊆ fix) ∧ " +
                " f(fix) = fix ",
				mTypeEnvironment(
						"S", POW(ty_S)
				),
				mTypeEnvironment(
						"fix", POW(ty_S),
						"f", POW(CPROD(POW(ty_S),POW(ty_S)))
				)
		);
		testPredicate(
                "  x ∈ S " +
                "∧ (∀x·x ∈ T) " +
                "∧ (∀x·x ∈ U) ",
				mTypeEnvironment(
						"S", POW(ty_S), "T", POW(ty_T), "U", POW(ty_U)
				),
				mTypeEnvironment(
						"x", ty_S
				)
		);
		testPredicate(
                "  x ∈ S " +
                "∧ (∀x·x ∈ T ∧ (∀x·x ∈ U)) ",
				mTypeEnvironment(
						"S", POW(ty_S), "T", POW(ty_T), "U", POW(ty_U)
				),
				mTypeEnvironment(
						"x", ty_S
				)
		);

		// Example from Christophe.
		testPredicate(
                "x ∈ y",
				mTypeEnvironment(
						"x", ty_S,
						"y", POW(ty_S2)
				),
				mTypeEnvironment()
		);

		// Test with typed empty set
		testPredicate(
                "(∅⦂ℙ(S×ℤ)) ∈ (∅⦂ℙ(S)) → ℤ",
				mTypeEnvironment(),
				mTypeEnvironment()
		);

		// Nested quantified expressions
		testPredicate(
                "ℤ = {x∣x∈{y∣y∈ℤ ∧ y≤x}}",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
	}

	public void testAssignmentTypeChecker() {
		testAssignment("A ≔ (∅⦂ℙ(S))", //
				mTypeEnvironment(), //
				mTypeEnvironment("S", POW(ty_S), "A", POW(ty_S))
		);
		testAssignment("x ≔ E", mTypeEnvironment("x", ty_S),
				mTypeEnvironment("E", ty_S)
		);
		testAssignment("x ≔ E", mTypeEnvironment("x", ty_S),
				mTypeEnvironment("E", ty_S)
		);
		testAssignment(
				"x ≔ 2",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testAssignment(
				"x ≔ 2",
				mTypeEnvironment("x", ty_S),
				null
		);
		testAssignment(
				"x,y ≔ E,F",
				mTypeEnvironment("x", ty_S,     "F", ty_T),
				mTypeEnvironment("E", ty_S, "y", ty_T)
		);
		testAssignment(
				"x,y ≔ E,F",
				mTypeEnvironment("x", ty_S, "y", ty_T, "E", ty_T),
				null
		);
		testAssignment(
				"x,y ≔ E,F",
				mTypeEnvironment("x", ty_S, "y", ty_T, "F", ty_S),
				null
		);
		testAssignment(
				"x,y,z ≔ ∅,∅,∅",
				mTypeEnvironment(
						"x", POW(ty_S),
						"y", POW(ty_T),
						"z", POW(ty_U)
				),
				mTypeEnvironment()
		);
		testAssignment(
				"x,y,z ≔ E,F,G",
				mTypeEnvironment(
						"x", POW(ty_S),
						"y", POW(ty_T),
						"z", POW(ty_U),
						"E", POW(ty_T)
				),
				null
		);
		testAssignment(
				"x,y,z ≔ E,F,G",
				mTypeEnvironment(
						"x", POW(ty_S),
						"y", POW(ty_T),
						"z", POW(ty_U),
						"F", POW(ty_U)
				),
				null
		);
		testAssignment(
				"x,y,z ≔ E,F,G",
				mTypeEnvironment(
						"x", POW(ty_S),
						"y", POW(ty_T),
						"z", POW(ty_U),
						"G", POW(ty_S)
				),
				null
		);
		testAssignment(
				"x :∈ S",
				mTypeEnvironment("S", POW(ty_S)),
				mTypeEnvironment("x", ty_S)
		);
		testAssignment(
				"x :∈ ∅",
				mTypeEnvironment("x", POW(ty_S)),
				mTypeEnvironment()
		);
		testAssignment(
				"x :∈ 1",
				mTypeEnvironment("x", ty_S),
				null
		);
		testAssignment(
				"x :∈ 1",
				mTypeEnvironment("x", INTEGER),
				null
		);
		testAssignment(
				"x :∣ x' < 0",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER)
		);
		testAssignment(
				"x,y :∣ x' < 0 ∧ y' = bool(x' = 5)",
				mTypeEnvironment(),
				mTypeEnvironment("x", INTEGER, "y", BOOL)
		);
		// FIXME: synthesizeType: fix code to reject incompatible types in this
		// assignement
		// testAssignment("f(S) ≔ (∅⦂ℙ(S)↔T)(∅⦂ℙ(S))", //
		// mTypeEnvironment("S", BOOL), //
		// null //
		//	);
	}

	/**
	 * Regression test for bug #3574565: Inconsistent result of formula
	 * type-checking
	 */
	public void testBug3574565() {
		final FormulaFactory fac = makeDatatypeFactory(ff,
				"A[T] ::= a; d[T] || B[U] ::= b; e[U]");
		testPredicate("b(1) ∈ A(ℤ)", mTypeEnvironment("", fac), null);
	}

	private FormulaFactory makeDatatypeFactory(FormulaFactory initial,
			String datatypeImage) {
		final IDatatypeExtension dtExt = injectExtension(datatypeImage, initial);
		final IDatatype datatype = initial.makeDatatype(dtExt);
		final Set<IFormulaExtension> exts = initial.getExtensions();
		exts.addAll(datatype.getExtensions());
		return FormulaFactory.getInstance(exts);
	}

	private void testPredicate(String image, ITypeEnvironment initialEnv,
			ITypeEnvironment inferredEnv) {
		final FormulaFactory factory = initialEnv.getFormulaFactory();
		final Predicate formula = parsePredicate(image, factory);
		doTest(formula, initialEnv, inferredEnv, image);
	}

	private void testAssignment(String image, ITypeEnvironment initialEnv,
			ITypeEnvironment inferredEnv) {
		final FormulaFactory factory = initialEnv.getFormulaFactory();
		final Assignment formula = parseAssignment(image, factory);
		doTest(formula, initialEnv, inferredEnv, image);
	}

	private void doTest(Formula<?> formula, ITypeEnvironment initialEnv,
			ITypeEnvironment inferredEnv, String image) {
		final boolean expectSuccess = inferredEnv != null;
		final ITypeCheckResult result = formula.typeCheck(initialEnv);
		if (expectSuccess && !result.isSuccess()) {
			StringBuilder builder = new StringBuilder(
					"Type-checker unexpectedly failed for " + formula
							+ "\nInitial type environment:\n"
							+ result.getInitialTypeEnvironment() + "\n");
			final List<ASTProblem> problems = result.getProblems();
			for (ASTProblem problem : problems) {
				builder.append(problem);
				final SourceLocation loc = problem.getSourceLocation();
				if (loc != null) {
					builder.append(", where location is: ");
					builder.append(image.substring(loc.getStart(),
							loc.getEnd() + 1));
				}
				builder.append("\n");
			}
			fail(builder.toString());
		}
		if (!expectSuccess && result.isSuccess()) {
			fail("Type checking should have failed for: " + formula
					+ "\nParser result: " + formula.toString()
					+ "\nType check results:\n" + result.toString()
					+ "\nInitial type environment:\n"
					+ result.getInitialTypeEnvironment() + "\n");
		}
		IInferredTypeEnvironment inferredTypEnv = null;
		if (inferredEnv != null) {
			inferredTypEnv = mInferredTypeEnvironment(initialEnv);
			ITypeEnvironment.IIterator iter = inferredEnv.getIterator();
			while (iter.hasNext()) {
				iter.advance();
				inferredTypEnv.addName(iter.getName(), iter.getType());
			}
		}
		assertEquals("Inferred typenv differ", inferredTypEnv,
				result.getInferredEnvironment());
		assertEquals("Incompatible result for isTypeChecked()", expectSuccess,
				formula.isTypeChecked());
		assertTrue("Problem with identifier caches",
				IdentsChecker.check(formula, ff));
	}
}
