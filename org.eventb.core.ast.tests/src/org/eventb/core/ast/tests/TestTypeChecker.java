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
 *     Systerel - test for bug #3574565
 *     Systerel - new implementation of inferred environment
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mInferredTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.InjectedDatatypeExtension.injectExtension;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.junit.Test;

/**
 * Unit test of the mathematical formula Type-Checker.
 *
 * @author franz
 */
public class TestTypeChecker extends AbstractTests {

	/**
	 * Main test routine for predicates.
	 */
	@Test 
	public void testTypeChecker() {
		testPredicate("x∈ℤ∧1≤x",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testPredicate("x⊆S∧∅⊂x",
				mTypeEnvironment("S=ℙ(S)", ff),
				mTypeEnvironment("x=ℙ(S)", ff)
		);
		testPredicate("∅=∅",
				mTypeEnvironment(),
				null
		);
		testPredicate("x=TRUE",
				mTypeEnvironment("x=ℤ", ff),
				null
		);
		testPredicate("x=TRUE",
				mTypeEnvironment("x=BOOL", ff),
				mTypeEnvironment()
		);
		testPredicate("x=TRUE",
				mTypeEnvironment(),
				mTypeEnvironment("x=BOOL", ff)
		);
		testPredicate("M = {A ∣ A ∉ A}",
				mTypeEnvironment(),
				null
		);
		testPredicate("x>x",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testPredicate("x∈y∧y∈x",
				mTypeEnvironment(),
				null
		);
		testPredicate("x∈ℙ(y)∧y∈ℙ(x)",
				mTypeEnvironment("x=ℙ(BOOL)", ff),
				mTypeEnvironment("y=ℙ(BOOL)", ff)
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
				mTypeEnvironment("x=ℙ(ℤ)", ff),
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
				mTypeEnvironment("x=ℤ", ff)
		);
		testPredicate("x≤x",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testPredicate("x>x",
				mTypeEnvironment("x=BOOL", ff),
				null
		);
		testPredicate("x≥x",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testPredicate("x∈S",
				mTypeEnvironment(),
				null
		);
		testPredicate("x∈S",
				mTypeEnvironment("x=ℤ", ff),
				mTypeEnvironment("S=ℙ(ℤ)", ff)
		);
		testPredicate("x∈S", 
				mTypeEnvironment("x=S",ff),
				mTypeEnvironment()
		);
		testPredicate("x∉S",
				mTypeEnvironment("x=S",ff),
				mTypeEnvironment()
		);
		testPredicate("x⊂S",
				mTypeEnvironment(),
				null
		);
		testPredicate("x⊂S",
				mTypeEnvironment("x=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("x⊄S",
				mTypeEnvironment("x=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("x⊆S",
				mTypeEnvironment("x=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("x⊈S",
				mTypeEnvironment("x=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("partition(S, {x},{y})",
				mTypeEnvironment("x=S", ff),
				mTypeEnvironment("S=ℙ(S); y=S", ff)
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
				mTypeEnvironment("x=BOOL", ff),
				mTypeEnvironment()
		);
		testPredicate("∃ x · x ∈ ℤ",
				mTypeEnvironment("x=BOOL", ff),
				mTypeEnvironment()
		);
		testPredicate("∀ x,y,z · ⊥",
				mTypeEnvironment("x=BOOL; y=BOOL; z=BOOL", ff),
				null
		);
		testPredicate("∀ x,y · x ∈ y ∧ y ⊆ ℤ",
				mTypeEnvironment("x=BOOL", ff),  // Not used.
				mTypeEnvironment()
		);
		testPredicate("∃ x,y,z · x ∈ y ∧ x ∈ z ∧ z ⊆ S",
				mTypeEnvironment("S=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("∀ x,y · ∀ s,t · x ∈ s ∧ y ∈ t ∧ s ∩ t ⊆ S",
				mTypeEnvironment("S=ℙ(S)", ff),
				mTypeEnvironment()
		);
		// SimpleExpression
		testPredicate("bool(⊥)=y",
				mTypeEnvironment(),
				mTypeEnvironment("y=BOOL", ff)
		);
		testPredicate("card(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("card(x)=y",
				mTypeEnvironment("x=S", ff),
				null
		);
		testPredicate("card(x)=y",
				mTypeEnvironment("x=ℙ(S)", ff),
				mTypeEnvironment("y=ℤ", ff)
		);
		testPredicate("ℙ(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("ℙ(x)=y",
				mTypeEnvironment("y=ℙ(ℙ(ℤ))", ff),
				mTypeEnvironment("x=ℙ(ℤ)", ff)
		);
		testPredicate("ℙ1(x)=y",
				mTypeEnvironment("y=ℙ(ℙ(ℤ))", ff),
				mTypeEnvironment("x=ℙ(ℤ)", ff)
		);
		testPredicate("union(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("union(x)=y",
				mTypeEnvironment("y=ℙ(S)", ff),
				mTypeEnvironment("x=ℙ(ℙ(S))", ff)
		);
		testPredicate("inter(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("inter(x)=y",
				mTypeEnvironment("y=ℙ(S)", ff),
				mTypeEnvironment("x=ℙ(ℙ(S))", ff)
		);
		testPredicate("dom(x)=y",
				mTypeEnvironment(),
				null
		);
		testPredicate("dom(x)=y",
				mTypeEnvironment("x=ℤ↔S", ff),
				mTypeEnvironment("y=ℙ(ℤ)", ff)
		);
		testPredicate("ran(x)=y",
				mTypeEnvironment("x=ℤ↔S", ff),
				mTypeEnvironment("y=ℙ(S)", ff)
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
				mTypeEnvironment("x=S↔T", ff),
				mTypeEnvironment("y=S×T↔S", ff)
		);
		testPredicate("prj2(x)=y",
				mTypeEnvironment("x=ℤ↔BOOL", ffV1),
				mTypeEnvironment("y=ℤ×BOOL↔BOOL", ffV1)
		);
		testPredicate("x◁prj2=y",
				mTypeEnvironment("x=S↔T",ff),
				mTypeEnvironment("y=S×T↔T", ff)
		);
		testPredicate("id(x)=y",
				mTypeEnvironment("x=ℙ(S)", ffV1),
				mTypeEnvironment("y=S↔S", ffV1)
		);
		testPredicate("x◁id=y",
				mTypeEnvironment("x=ℙ(S)", ff),
				mTypeEnvironment("y=S↔S", ff)
		);
		testPredicate("id(x)=y",
				mTypeEnvironment("x=S", ff),
				mTypeEnvironment("y=S", ff)
		);
		testPredicate("{x,y·⊥∣z}=a",
				mTypeEnvironment(),
				null
		);
		testPredicate("{x,y·⊥∣z}=a",
				mTypeEnvironment("z=ℤ", ff),
				null
		);
		testPredicate("{x · x ∈ z ∣ z}=a",
				mTypeEnvironment("a=ℙ(ℙ(BOOL))", ff),
				mTypeEnvironment("z=ℙ(BOOL)", ff)
		);
		testPredicate("{x · ⊥ ∣ x}=a",
				mTypeEnvironment("a=ℙ(ℤ)", ff),
				mTypeEnvironment()
		);
		testPredicate("{x+y∣⊥}=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℙ(ℤ)", ff)
		);
		testPredicate("{}={}",
				mTypeEnvironment(),
				null
		);
		testPredicate("a=∅",
				mTypeEnvironment("a=ℙ(N)", ff),
				mTypeEnvironment()
		);
		testPredicate("a=∅",
				mTypeEnvironment("a=N↔N", ff),
				mTypeEnvironment()
		);
		testPredicate("∅=a",
				mTypeEnvironment("a=ℙ(N)", ff),
				mTypeEnvironment()
		);
		testPredicate("∅=a",
				mTypeEnvironment("a=ℙ(N)", ff),
				mTypeEnvironment()
		);
		testPredicate("{x}=a",
				mTypeEnvironment("x=ℤ", ff),
				mTypeEnvironment("a=ℙ(ℤ)", ff)
		);
		testPredicate("{x,y,z}=a",
				mTypeEnvironment("x=ℤ", ff),
				mTypeEnvironment("y=ℤ; z=ℤ; a=ℙ(ℤ)", ff)
		);
		testPredicate("x∈ℤ",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testPredicate("x∈ℕ",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testPredicate("x∈ℕ1",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testPredicate("x∈BOOL",
				mTypeEnvironment(),
				mTypeEnvironment("x=BOOL", ff)
		);
		testPredicate("x=FALSE",
				mTypeEnvironment(),
				mTypeEnvironment("x=BOOL", ff)
		);
		testPredicate("x=pred",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ↔ℤ", ff)
		);
		testPredicate("x=succ",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ↔ℤ", ff)
		);
		testPredicate("x=2",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		// Primary
		testPredicate("x∼=y",
				mTypeEnvironment("x=ℤ↔BOOL", ff),
				mTypeEnvironment("y=BOOL↔ℤ", ff)
		);
		// Image
		testPredicate("f(x)=a",
				mTypeEnvironment("f=ℤ↔BOOL", ff),				
				mTypeEnvironment("x=ℤ; a=BOOL", ff)
		);
		testPredicate("f[x]=a",
				mTypeEnvironment("f=ℤ↔BOOL", ff),				
				mTypeEnvironment("x=ℙ(ℤ); a=ℙ(BOOL)", ff)
		);
		testPredicate("f[x](y)=a",
				mTypeEnvironment("f=S↔T×U", ff),				
				mTypeEnvironment("x=ℙ(S); y=T; a=U", ff)
		);
		testPredicate("f(x)[y]=a",
				mTypeEnvironment("f=S↔(T↔U)", ff),				
				mTypeEnvironment("x=S; y=ℙ(T); a=ℙ(U)", ff)
		);
		testPredicate("f(x)(y)=a",
				mTypeEnvironment("f=S↔(T↔U)", ff),				
				mTypeEnvironment("x=S; y=T; a=U", ff)
		);
		testPredicate("f[x][y]=a",
				mTypeEnvironment("f=S↔T×U", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); a=ℙ(U)", ff)
		);

		// Factor
		testPredicate("x^y=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ", ff)
		);

		// Term
		testPredicate("x∗x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ", ff)				
		);
		testPredicate("x∗x∗x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ", ff)
		);
		testPredicate("x÷x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ", ff)
		);
		testPredicate("x mod x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ", ff)
		);
		// ArithmeticExpr
		testPredicate("x+y=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ", ff)
		);
		testPredicate("x+y+x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ", ff)
		);
		testPredicate("−x+y+z=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ; z=ℤ", ff)
		);
		testPredicate("x−y=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ", ff)
		);
		testPredicate("x−y−z=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ; z=ℤ", ff)
		);
		testPredicate("−x−y=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ", ff)
		);
		testPredicate("x−y+z−x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ; z=ℤ", ff)
		);
		testPredicate("−x−y+z−x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ; z=ℤ", ff)
		);
		testPredicate("x+y−z+x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ; z=ℤ", ff)
		);
		testPredicate("−x+y−z+x=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℤ; x=ℤ; y=ℤ; z=ℤ", ff)
		);
		// IntervalExpr
		testPredicate("x‥y=a",
				mTypeEnvironment(),
				mTypeEnvironment("a=ℙ(ℤ); x=ℤ; y=ℤ", ff)
		);
		// RelationExpr
		testPredicate("x⊗y=a",
				mTypeEnvironment("x=S↔T; y=S↔U", ff),
				mTypeEnvironment("a=S↔T×U; y=S↔U", ff)
		);
		testPredicate("x;y=a",
				mTypeEnvironment("a=S↔T; x=S↔U", ff),
				mTypeEnvironment("y=U↔T", ff)
		);
		testPredicate("x;y;z=a",
				mTypeEnvironment("a=S↔T; x=S↔U; z=V↔T", ff),
				mTypeEnvironment("y=U↔V", ff)
		);
		testPredicate("x▷y=a",
				mTypeEnvironment("x=S↔T", ff),
				mTypeEnvironment("y=ℙ(T); a=S↔T", ff)
		);
		testPredicate("x⩥y=a",
				mTypeEnvironment("x=S↔T", ff),
				mTypeEnvironment("y=ℙ(T); a=S↔T", ff)
		);
		testPredicate("x∩y=a",
				mTypeEnvironment("x=ℙ(T)", ff),
				mTypeEnvironment("y=ℙ(T); a=ℙ(T)", ff)
		);
		testPredicate("x∩y∩z=a",
				mTypeEnvironment("x=ℙ(T)", ff),
				mTypeEnvironment("y=ℙ(T); z=ℙ(T); a=ℙ(T)", ff)
		);
		testPredicate("x∖y=a",
				mTypeEnvironment("x=ℙ(T)", ff),
				mTypeEnvironment("y=ℙ(T); a=ℙ(T)", ff)
		);
		testPredicate("x;y⩥z=a",
				mTypeEnvironment("x=S↔T; z=ℙ(U)", ff),
				mTypeEnvironment("y=T↔U; a=S↔U", ff)
		);
		testPredicate("x∩y⩥z=a",
				mTypeEnvironment("x=S↔T", ff),
				mTypeEnvironment("y=S↔T; z=ℙ(T); a=S↔T", ff)
		);
		testPredicate("x∩y∖z=a",
				mTypeEnvironment("x=S↔T", ff),
				mTypeEnvironment("y=S↔T; z=S↔T; a=S↔T", ff)
		);

		// SetExpr
		testPredicate("x∪y=a",
				mTypeEnvironment("x=ℙ(T)", ff),
				mTypeEnvironment("y=ℙ(T); a=ℙ(T)", ff)
		);
		testPredicate("x∪y∪z=a",
				mTypeEnvironment("x=ℙ(T)", ff),
				mTypeEnvironment("y=ℙ(T); z=ℙ(T); a=ℙ(T)", ff)
		);
		testPredicate("x×y=a",
				mTypeEnvironment("a=S↔T", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("x×y×z=a",
				mTypeEnvironment("a=S×T↔U", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("xy=a",
				mTypeEnvironment("a=S↔T", ff),
				mTypeEnvironment("x=S↔T; y=S↔T", ff)
		);
		testPredicate("xyz=a",
				mTypeEnvironment("a=S↔T", ff),
				mTypeEnvironment("x=S↔T; y=S↔T; z=S↔T", ff)
		);
		testPredicate("f ∘ g = a",
				mTypeEnvironment("f=T↔U; a=S↔U", ff),
				mTypeEnvironment("g=S↔T", ff)
		);
		testPredicate("f ∘ g ∘ h = a",
				mTypeEnvironment("f=U↔V; h=S↔T", ff),
				mTypeEnvironment("a=S↔V; g=T↔U", ff)
		);
		testPredicate("x∥y=a",
				mTypeEnvironment(),
				null
		);
		testPredicate("x∥y=a",
				mTypeEnvironment("x=S↔U; y=T↔V", ff),
				mTypeEnvironment("a=S×T↔U×V", ff)
		);
		testPredicate("x◁y=a",
				mTypeEnvironment("y=S↔T", ff),
				mTypeEnvironment("x=ℙ(S); a=S↔T", ff)
		);
		testPredicate("x⩤y=a",
				mTypeEnvironment("y=S↔T", ff),
				mTypeEnvironment("x=ℙ(S); a=S↔T", ff)
		);
		// RelationalSetExpr
		testPredicate("xy=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(xy)z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("xy=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(xy)z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("xy=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(xy)z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("x⤀y=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(x⤀y)⤀z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("x⤔y=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(x⤔y)⤔z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("x⤖y=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(x⤖y)⤖z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("x→y=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(x→y)→z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("x↔y=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(x↔y)↔z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("x↠y=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(x↠y)↠z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("x↣y=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(x↣y)↣z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		testPredicate("x⇸y=a",
				mTypeEnvironment("a=ℙ(S↔T)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T)", ff)
		);
		testPredicate("(x⇸y)⇸z=a",
				mTypeEnvironment("a=ℙ((S↔T)↔U)", ff),
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff)
		);
		// PairExpr
		testPredicate("x↦y=a",
				mTypeEnvironment("a=S×T", ff),
				mTypeEnvironment("x=S; y=T", ff)
		);
		testPredicate("a=x↦y",
				mTypeEnvironment("a=S×T", ff),
				mTypeEnvironment("x=S; y=T", ff)
		);
		// QuantifiedExpr & IdentPattern
		// UnBound
		testPredicate("finite(λ x·⊥∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				null
		);
		testPredicate("finite(λ x· x∈ℤ ∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("finite(λ x↦y·⊥∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				null
		);
		testPredicate("finite(λ x↦y·x↦y∈ℤ×ℤ ∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("finite(λ x↦y↦s·⊥∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				null
		);
		testPredicate("finite(λ x↦y↦s · x↦y↦s∈ℤ×ℤ×ℤ ∣ z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("finite(λ x↦(y↦s)·⊥∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				null
		);
		testPredicate("finite(λ x↦(y↦s) · x↦y↦s∈ℤ×ℤ×ℤ ∣ z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				mTypeEnvironment()
		);

		// Bound
		testPredicate("a = (λ x·⊥∣x)",
				mTypeEnvironment("a=S↔S", ff),
				mTypeEnvironment()
		);
		testPredicate("a = (λ x↦y·⊥∣y)",
				mTypeEnvironment("a=S×T↔T", ff),
				mTypeEnvironment()
		);
		testPredicate("a = (λ x↦y↦s·⊥∣s)",
				mTypeEnvironment("a=S×T×U↔U", ff),
				mTypeEnvironment()
		);
		testPredicate("a = (λ x↦(y↦s)·⊥∣s)",
				mTypeEnvironment("a=S×(T×U)↔U", ff),
				mTypeEnvironment()
		);

		// UnBound
		testPredicate("finite(⋃x·⊥∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				null
		);
		testPredicate("finite(⋃x· x∈ℤ ∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("finite(⋃y,x·⊥∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				null
		);
		testPredicate("finite(⋃y,x · x↦y∈ℤ×ℤ ∣ z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("finite(⋃s,y,x·⊥∣z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				null
		);
		testPredicate("finite(⋃s,y,x · x↦y↦s∈ℤ×ℤ×ℤ ∣ z)",
				mTypeEnvironment("z=ℙ(S)", ff),
				mTypeEnvironment()
		);

		// Bound
		testPredicate("(⋃ x · ⊥ ∣ x) = a",
				mTypeEnvironment("a=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("(⋃y,x·⊥∣y ▷ x) = a",
				mTypeEnvironment("a=S↔T", ff),
				mTypeEnvironment()
		);
		testPredicate("(⋃s,y,x·⊥∣ (s▷y)▷x) = a",
				mTypeEnvironment("a=S↔T", ff),
				mTypeEnvironment()
		);

		// Implicitly Bound
		testPredicate("(⋃x∣⊥) = a",
				mTypeEnvironment("a=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testPredicate("(⋃y∩x∣⊥) = a",
				mTypeEnvironment("a=ℙ(S)", ff),
				mTypeEnvironment()
		);

		// Special formulas
		testPredicate("∀ s · N◁id ⊆ s ∧ s ; r ⊆ s ⇒ c ⊆ s",
				mTypeEnvironment("N=ℙ(N)", ff),
				mTypeEnvironment("r=N↔N; c=N↔N", ff)
		);
		testPredicate("(λ x ↦ y ↦ z · x < y ∧ z ∈ ℤ∣ H ) ( f ( 1 ) ) ∈ ℙ ( ℤ )",
				mTypeEnvironment(),
				mTypeEnvironment("H=ℙ(ℤ); f=ℤ↔ℤ×ℤ×ℤ", ff)
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
				mTypeEnvironment("S=ℙ(S)", ff),
				mTypeEnvironment("filter=ℙ(ℙ(ℙ(S))); ultraf=ℙ(ℙ(ℙ(S)))", ff)
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
				mTypeEnvironment("S=ℙ(S)", ff),
				mTypeEnvironment("filter=ℙ(ℙ(ℙ(S))); ultraf=ℙ(ℙ(ℙ(S)))", ff)
		);
		testPredicate("N◁id ∩ g = ∅",
				mTypeEnvironment("N=ℙ(N)", ff),
				mTypeEnvironment("g=N↔N", ff)
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
				mTypeEnvironment("N=ℙ(N)", ff),
				mTypeEnvironment("g=N↔N; h=N↔(N↔N)", ff)
		);
		testPredicate(
                " com ∩ id = ∅ ∧ " +
                " exit ∈ L ∖ {outside} ↠ L ∧ " +
                " exit ⊆ com ∧ " +
                " ( ∀ s · s ⊆ exit∼[s] ⇒ s = ∅ ) ∧ " +
                " aut ⩥ {outside} ⊆ (aut ; exit∼) ∧ " +
                " ( ∃ l · l ∈ L ∖ {outside} ∧ outside ↦ l ∈ com ∧ L×{l} ⊆ aut )",
                mTypeEnvironment("L=ℙ(L)", ff),
                mTypeEnvironment("aut=L↔L; com=L↔L; outside=L; exit=L↔L", ff)
		);
		testPredicate(
                " f ∈ ℙ(S) ↠ ℙ(S) ∧ " +
                " (∀ a, b · a ⊆ b ⇒ f(a) ⊆ f(b)) ∧ " +
                " fix = inter({s ∣ f(s) ⊆ s}) ∧ " +
                " (∀ s · f(s) ⊆ s ⇒ fix ⊆ s) ∧ " +
                " (∀ v · (∀ w · f(w) ⊆ w ⇒ v ⊆ w) ⇒ v ⊆ fix) ∧ " +
                " f(fix) = fix ",
				mTypeEnvironment("S=ℙ(S)", ff),
				mTypeEnvironment("fix=ℙ(S); f=ℙ(S)↔ℙ(S)", ff)				
		);
		testPredicate(
                "  x ∈ S " +
                "∧ (∀x·x ∈ T) " +
                "∧ (∀x·x ∈ U) ",
				mTypeEnvironment("S=ℙ(S); T=ℙ(T); U=ℙ(U)", ff),
				mTypeEnvironment("x=S", ff)
		);
		testPredicate(
                "  x ∈ S " +
                "∧ (∀x·x ∈ T ∧ (∀x·x ∈ U)) ",
                mTypeEnvironment("S=ℙ(S); T=ℙ(T); U=ℙ(U)", ff),
				mTypeEnvironment("x=S", ff)
		);

		// Test with typed empty set
		testPredicate("(∅⦂S↔ℤ) ∈ (∅⦂ℙ(S)) → ℤ",
				mTypeEnvironment(),
				mTypeEnvironment("S=ℙ(S)", ff)
		);

		// Nested quantified expressions
		testPredicate("ℤ = {x∣x∈{y∣y∈ℤ ∧ y≤x}}",
				mTypeEnvironment(),
				mTypeEnvironment()
		);
	}

	@Test 
	public void testAssignmentTypeChecker() {
		testAssignment("A ≔ (∅⦂ℙ(S))", //
				mTypeEnvironment(), //
				mTypeEnvironment("S=ℙ(S); A=ℙ(S)", ff)
		);
		testAssignment("x ≔ E",
				mTypeEnvironment("x=S", ff),
				mTypeEnvironment("E=S", ff)
		);
		testAssignment("x ≔ E",
				mTypeEnvironment("x=S", ff),
				mTypeEnvironment("E=S", ff)
		);
		testAssignment("x ≔ 2",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testAssignment("x ≔ 2",
				mTypeEnvironment("x=S", ff),
				null
		);
		testAssignment("x,y ≔ E,F",
				mTypeEnvironment("x=S; F=T", ff),
				mTypeEnvironment("E=S; y=T", ff)
		);
		testAssignment("x,y ≔ E,F",
				mTypeEnvironment("x=S; y=T; E=T", ff),
				null
		);
		testAssignment("x,y ≔ E,F",
				mTypeEnvironment("x=S; y=T; F=S", ff),
				null
		);
		testAssignment("x,y,z ≔ ∅,∅,∅",
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U)", ff),
				mTypeEnvironment()
		);
		testAssignment("x,y,z ≔ E,F,G",
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U); E=ℙ(U)", ff),
				null
		);
		testAssignment("x,y,z ≔ E,F,G",
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U); F=ℙ(U)", ff),
				null
		);
		testAssignment("x,y,z ≔ E,F,G",
				mTypeEnvironment("x=ℙ(S); y=ℙ(T); z=ℙ(U); G=ℙ(S)", ff),
				null
		);
		testAssignment("x :∈ S",
				mTypeEnvironment("S=ℙ(S)", ff),
				mTypeEnvironment("x=S", ff)
		);
		testAssignment("x :∈ ∅",
				mTypeEnvironment("x=ℙ(S)", ff),
				mTypeEnvironment()
		);
		testAssignment("x :∈ 1",
				mTypeEnvironment("x=S", ff),
				null
		);
		testAssignment("x :∈ 1",
				mTypeEnvironment("x=ℤ", ff),
				null
		);
		testAssignment("x :∣ x' < 0",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ", ff)
		);
		testAssignment("x,y :∣ x' < 0 ∧ y' = bool(x' = 5)",
				mTypeEnvironment(),
				mTypeEnvironment("x=ℤ; y=BOOL", ff)
		);
	}

	/**
	 * Regression test for rejecting incompatible types when introducing
	 * implicitly given sets.
	 */
	@Test
	public void testStrengtheningTypeChecker() {
		testAssignment("f(S) ≔ (∅⦂ℙ(S)↔T)(∅⦂ℙ(S))", //
				mTypeEnvironment("S=BOOL", ff), //
				null //
		);
		testPredicate("f(S) = (∅⦂ℙ(S)↔U)(∅⦂ℙ(S))", //
				mTypeEnvironment("f=T↔U", ff), //
				null //
		);
	}

	/**
	 * Regression test for bug #3574565: Inconsistent result of formula
	 * type-checking
	 */
	@Test 
	public void testBug3574565() {
		final FormulaFactory fac = makeDatatypeFactory(ff,//
				"A[T] ::= a; d[T]",//
				"B[U] ::= b; e[U]");
		testPredicate("b(1) ∈ A(ℤ)", mTypeEnvironment("", fac), null);
	}

	/**
	 * Regression test for an extended operator that requires that its child
	 * expression bears some fixed type. This used to wreak havoc in the
	 * type-checker, because it can infer a type for the node, but the node can
	 * never be type-checked, because its child has the wrong type.
	 */
	@Test
	public void strangeTypeCheck() {
		final FormulaFactory fac = FormulaFactory.getInstance(new Strange());
		testPredicate("strange(1) ∈ S", mTypeEnvironment("S=ℙ(S)", fac), null);
	}

	/**
	 * Ensures that type-check throws an exception on an ill-formed formulas.
	 */
	@Test(expected = IllegalStateException.class)
	public void illFormedPredicate() {
		final Predicate pred = mRelationalPredicate(
				mFreeIdentifier("x", INT_TYPE),//
				mBoundIdentifier(0, INT_TYPE));
		assertTrue(pred.isTypeChecked());
		assertFalse(pred.isWellFormed());
		pred.typeCheck(mTypeEnvironment());
	}

	/**
	 * Ensures that the type-checker returns a failure if the given
	 * type-environment is not compatible with the formula, but the formula
	 * itself remains type-checked if it was.
	 */
	@Test
	public void incompatibleTypeEnvironment() {
		final ITypeEnvironment empty = mTypeEnvironment();
		final ITypeEnvironment goodTypenv = mTypeEnvironment("x=ℤ", ff);
		final ITypeEnvironment badTypenv = mTypeEnvironment("x=ℙ(S)", ff);
		final Predicate pred = testPredicate("1≤x", empty, goodTypenv);
		final ITypeCheckResult result = pred.typeCheck(badTypenv);
		assertFalse(result.isSuccess());
		assertTrue(pred.isTypeChecked());
	}

	private FormulaFactory makeDatatypeFactory(FormulaFactory initial,
			String... datatypeImages) {
		FormulaFactory fac = initial;
		for (final String datatypeImage : datatypeImages) {
			fac = makeDatatypeFactory(fac, datatypeImage);
		}
		return fac;
	}

	private FormulaFactory makeDatatypeFactory(FormulaFactory initial,
			String datatypeImage) {
		final IDatatypeExtension dtExt = injectExtension(datatypeImage);
		final IDatatype datatype = initial.makeDatatype(dtExt);
		final Set<IFormulaExtension> exts = initial.getExtensions();
		exts.addAll(datatype.getExtensions());
		return FormulaFactory.getInstance(exts);
	}

	private Predicate testPredicate(String image, ITypeEnvironment initialEnv,
			ITypeEnvironment finalEnv) {
		final FormulaFactory factory = initialEnv.getFormulaFactory();
		final Predicate formula = parsePredicate(image, factory);
		doTest(formula, initialEnv, finalEnv, image);
		return formula;
	}

	private void testAssignment(String image, ITypeEnvironment initialEnv,
			ITypeEnvironment finalEnv) {
		final FormulaFactory factory = initialEnv.getFormulaFactory();
		final Assignment formula = parseAssignment(image, factory);
		doTest(formula, initialEnv, finalEnv, image);
	}

	private void doTest(Formula<?> formula, ITypeEnvironment initialEnv,
			ITypeEnvironment finalEnv, String image) {
		final boolean expectSuccess = finalEnv != null;
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
		if (finalEnv != null) {
			// Create an inferred environment from the final environment
			inferredTypEnv = mInferredTypeEnvironment(initialEnv);
			inferredTypEnv.addAll(finalEnv);
		}
		assertEquals("Inferred typenv differ", inferredTypEnv,
				result.getInferredEnvironment());
		assertEquals("Incompatible result for isTypeChecked()", expectSuccess,
				formula.isTypeChecked());
		IdentsChecker.check(formula, ff);
	}

	/**
	 * This is a strange operator. It has the same shape as a unary expression.
	 * But it insists that its child is of Boolean type, while the result can be
	 * of any type. Its type-checking algorithm is thus quite peculiar, as there
	 * is no relation between the child type and the result type. Also, method
	 * <code>verifyType</code> can fail although the proposed type is perfectly
	 * valid, which makes auto-verification of the type-checker difficult.
	 */
	private static class Strange implements IExpressionExtension {

		public Strange() {
			// Do nothing, but is publicly visible
		}

		@Override
		public String getSyntaxSymbol() {
			return "strange";
		}

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public String getId() {
			return "STRANGE";
		}

		@Override
		public String getGroupId() {
			return StandardGroup.CLOSED.getId();
		}

		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_UNARY_EXPRESSION;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// None to add
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// None to add
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return null;
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			return childExprs[0].getType() instanceof BooleanType;
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final Expression[] childExprs = expression.getChildExpressions();
			final Type childType = childExprs[0].getType();
			tcMediator.sameType(childType, tcMediator.makeBooleanType());
			return tcMediator.newTypeVariable();
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

}
