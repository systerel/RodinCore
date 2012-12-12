/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.eventb.core.ast.tests.ExtendedFormulas.EFF;
import static org.eventb.core.ast.tests.ExtensionHelper.getGenericOperatorExtension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.internal.core.ast.extension.datatype.DatatypeTranslation;
import org.junit.Test;

/**
 * Unit tests for the common datatype rewriter class. Verifies that the rewriter
 * correctly retrieves the datatype instance of a given parametric type, and all
 * overriden rewrite methods are working as expected.
 * 
 * @author "Thomas Muller"
 */
public class TestDatatypeRewriter extends AbstractTranslatorTests {

	/**
	 * Checks that a parametric type is well retrieved from a type constructor.
	 */
	@Test 
	public void testTypeConstructorRewriting() {
		assertCorrectParametricType("List(1‥3)");
	}

	/**
	 * Checks that a parametric type is well retrieved from a value constructor.
	 */
	@Test 
	public void testConstructorRewriting() {
		assertCorrectParametricType("cons(1, nil)");
	}

	/**
	 * Checks that a parametric type is well retrieved from a destructor.
	 */
	@Test 
	public void testDestructorRewriting() {
		assertCorrectParametricType("tail(cons(1, nil))");
	}

	private void assertCorrectParametricType(String expression) {
		final TestTranslationSupport s = mSupport(LIST__DT);
		final IDatatype listDatatype = s.getDatatypes().get(0);
		final DatatypeTranslation trans = s.getTranslation();
		final Expression parsedExpr = s.parseSourceExpression(expression);
		assertTrue(parsedExpr instanceof ExtendedExpression);
		final ExtendedExpression extendedExpr = (ExtendedExpression) parsedExpr;
		final Type dtInstance = trans.getDatatypeInstance(extendedExpr);
		assertTrue(dtInstance instanceof ParametricType);
		final FormulaFactory listFactory = trans.getSourceFormulaFactory();
		final ParametricType parametric = listFactory.makeParametricType(
				new Type[] { listFactory.makeIntegerType() },
				listDatatype.getTypeConstructor());
		assertEquals(parametric, dtInstance);
	}

	/**
	 * Checks that the rewrite method correctly applies for a BoudIdentDecl
	 * (i.e. it rewrites the type of the BoundIdentDecl).
	 */
	@Test 
	public void testBoundIdentDeclRewrite() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type); List_Type0=ℙ(List_Type0)");
		s.assertPredTranslation("∀ x ⦂ List(ℤ) · ⊤", "∀ x ⦂ List_Type · ⊤");
		s.assertPredTranslation("∀ x ⦂ List(BOOL) · ⊤", "∀ x ⦂ List_Type0 · ⊤");
	}

	/**
	 * Checks that the rewrite method correctly applies for an AtomicExpression
	 * (i.e. it rewrites the type of the AtomicExpression).
	 */
	@Test 
	public void testAtomicExpressionRewrite() {
		final TestTranslationSupport s = mSupport(MESSAGE__DT);
		s.addGivenTypes("Agent", "Identifier");
		s.setExpectedTypeEnvironment("Message_Type=ℙ(Message_Type)");
		s.assertExprTranslation("(prj1 ⦂ ℤ×Message(Agent,Identifier)↔ℤ)",
				"(prj1 ⦂ ℤ×Message_Type↔ℤ)");
	}

	/**
	 * Checks that the rewrite method correctly applies for a BoundIdendifier
	 * (i.e. it rewrites the type of the BoundIdendifier).
	 */
	@Test 
	public void testBoundIdentifierRewrite() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type); List_Type0=ℙ(List_Type0)");
		s.assertPredTranslation("∀ x ⦂ List(ℤ) · x = x",
				"∀ x ⦂ List_Type · x = x");
		s.assertPredTranslation("∀ x ⦂ List(BOOL) · x = x",
				"∀ x ⦂ List_Type0 · x = x");
	}

	/**
	 * Checks that the rewrite method correctly applies for a FreeIdentifier
	 * (i.e. it rewrites the type of the FreeIdentifier).
	 */
	@Test 
	public void testFreeIdentifierRewrite() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addToSourceEnvironment("x=List(ℤ)");
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type); x=List_Type");
		s.assertExprTranslation("x", "x");
	}

	/**
	 * Checks that the rewrite method correctly applies for a
	 * QuantifiedExpression (i.e. it rewrites the type of the quantified
	 * identifiers [bound identifier declarations] that occur in the
	 * QuantifiedExpression).
	 */
	@Test 
	public void testQuantifiedExpressionRewrite() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type)");
		s.assertExprTranslation("⋃ x⦂List(ℤ) · ⊤ ∣ {x}",
				"⋃ x⦂List_Type · ⊤ ∣ {x}");
	}

	/**
	 * Checks that the rewrite method correctly applies for a
	 * QuantifiedPredicate (i.e. it rewrites the type of the quantified
	 * identifiers [bound identifier declarations] that occur in the
	 * QuantifiedPredicate).
	 */
	@Test 
	public void testQuantifiedPredicateRewrite() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type)");
		s.assertPredTranslation("∀ x⦂List(ℤ) · ⊤", "∀ x⦂List_Type · ⊤");
	}

	/**
	 * Checks that the rewrite method correctly applies for a SetExtension which
	 * denotes a typed empty set.
	 */
	@Test 
	public void testSetExtensionRewrite() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.setExpectedTypeEnvironment("List=ℙ(List_Type)");
		s.assertExprTranslation("∅⦂ℙ(List(ℤ))", "∅⦂ℙ(List_Type)");
	}

	/**
	 * Checks that the rewrite method correctly applies for a SetExtension which
	 * denotes a typed empty set.
	 */
	@Test 
	public void testSetExtensionChildrenRewrite() {
		final TestTranslationSupport s = mSupport(LIST__DT);
		s.addToSourceEnvironment("x=List(ℤ)");
		s.setExpectedTypeEnvironment("List=ℙ(List);x=List_Type");
		s.assertPredTranslation("{} = ∅⦂ℙ(List(ℤ))", "{} = ∅⦂ℙ(List_Type)");
		s.assertExprTranslation("{x}", "{x}");
	}

	/**
	 * Checks that the rewrite method correctly translates datatypes in the
	 * expressions of an ExpressionExtension.
	 */
	@Test 
	public void testExtendedExpressionWithChildPredRewrite() {
		final TestTranslationSupport s = mSupport(EFF, LIST__DT);
		s.addToSourceEnvironment("x=List(ℤ);y=List(ℤ)");
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type);x=List_Type;y=List_Type");
		s.assertExprTranslation("barL(⊤, x, ⊥, y)", "barL(⊤, x, ⊥, y)");
	}

	/**
	 * Checks that the rewrite method correctly translates datatypes in the
	 * expressions of an ExtendedPredicate.
	 */
	@Test 
	public void testExtendedPredicateWithChildPredRewrite() {
		final TestTranslationSupport s = mSupport(EFF, LIST__DT);
		s.addToSourceEnvironment("x=List(ℤ);y=List(ℤ)");
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type);x=List_Type;y=List_Type");
		s.assertPredTranslation("fooL(⊤, x, ⊥, y)", "fooL(⊤, x, ⊥, y)");
	}

	/**
	 * Checks that the rewrite method correctly translates the type of a generic
	 * operator.
	 */
	@Test 
	public void testGenericOperatorRewrite() {
		final FormulaFactory genericOpFac = FormulaFactory
				.getInstance(getGenericOperatorExtension());
		final TestTranslationSupport s = mSupport(genericOpFac, LIST__DT);
		s.setExpectedTypeEnvironment("List_Type=ℙ(List_Type)");
		s.assertExprTranslation("(List(ℤ)×ℤ) ◁ ▲", "(List_Type×ℤ) ◁ ▲");
	}

}