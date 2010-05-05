/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.IToStringMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * This test class aims at supporting generic parser development. It is not part
 * of the AST project test and is intended to be removed when the development is
 * complete.
 * 
 * @author Nicolas Beauger
 * FIXME remove this class (DO NOT COMMIT TO TRUNK !)
 */
public class TestGenParser extends AbstractTests {

	private static final BoundIdentifier BI_0 = ff
					.makeBoundIdentifier(0, null);
	private static final BoundIdentDecl BID_x = ff.makeBoundIdentDecl("x", null);
	private static final LiteralPredicate LIT_BFALSE = ff.makeLiteralPredicate(
							Formula.BFALSE, null);
	private static final LiteralPredicate LIT_BTRUE = ff.makeLiteralPredicate(
							Formula.BTRUE, null);
	private static final IntegerLiteral ZERO = ff.makeIntegerLiteral(BigInteger.ZERO, null);
	private static final IntegerLiteral ONE = ff.makeIntegerLiteral(BigInteger.ONE, null);
	private static final AtomicExpression EMPTY = ff.makeEmptySet(null, null);
	private static final FreeIdentifier FRID_S = ff.makeFreeIdentifier("S", null);
	private static final GivenType S_TYPE = ff.makeGivenType("S");
	private static final PowerSetType POW_S_TYPE = ff.makePowerSetType(S_TYPE);
	private static final AtomicExpression INT = ff.makeAtomicExpression(Formula.INTEGER, null);
	private static final UnaryExpression POW_INT = ff.makeUnaryExpression(POW, INT, null);
	private static final IntegerType INT_TYPE = ff.makeIntegerType();
	private static final PowerSetType POW_INT_TYPE = ff.makePowerSetType(INT_TYPE);
	private static final SourceLocationChecker slChecker = new SourceLocationChecker();

	private void doExpressionTest(String formula, Formula<?> expected, FormulaFactory factory) {
		final IParseResult result = factory.parseExpression(formula,
				LanguageVersion.V2, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Expression actual = result.getParsedExpression();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		actual.accept(slChecker);
	}
	
	private void doExpressionTest(String formula, Formula<?> expected) {
		doExpressionTest(formula, expected, ff);
	}
	
	private void doPredicateTest(String formula, Predicate expected) {
		final IParseResult result = ff.parsePredicate(formula,
				LanguageVersion.V2, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Predicate actual = result.getParsedPredicate();
		System.out.println(actual);
		assertEquals(expected, actual);
	
		actual.accept(slChecker);
	}

	private void doTypeTest(String formula, Type expected) {
		final IParseResult result = ff.parseType(formula,
				LanguageVersion.V2);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Type actual = result.getParsedType();
		System.out.println(actual);
		assertEquals(expected, actual);
	}
	
	public void testIntegerLiteral() throws Exception {
		final Expression expected = ff.makeIntegerLiteral(BigInteger.ONE, null);
		doExpressionTest("1", expected);
	}

	public void testPlus() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(PLUS,
				Arrays.<Expression> asList(ff.makeIntegerLiteral(BigInteger
						.valueOf(2), null), ff.makeIntegerLiteral(BigInteger
						.valueOf(3), null)), null);
		doExpressionTest("2+3", expected);
	}

	public void testPlusAsso() throws Exception {
		final Expression expected = ff
				.makeAssociativeExpression(PLUS, Arrays
						.<Expression> asList(
								ff.makeIntegerLiteral(BigInteger.valueOf(1), null),
								ff.makeIntegerLiteral(BigInteger.valueOf(2), null),
								ff.makeIntegerLiteral(BigInteger.valueOf(3), null)), null);
		doExpressionTest("1+2+3", expected);
	}

	public void testPlusMult() throws Exception {
		final Expression expected = ff
				.makeAssociativeExpression(PLUS, Arrays
						.<Expression> asList(
								ff.makeIntegerLiteral(BigInteger.valueOf(1), null),
								ff.makeAssociativeExpression(MUL, Arrays.<Expression> asList(ff
										.makeIntegerLiteral(BigInteger
												.valueOf(2), null), ff
										.makeIntegerLiteral(BigInteger
												.valueOf(3), null)), null)
						), null);
		doExpressionTest("1+2∗3", expected);
	}
	
	public void testIdentDoubleParen() throws Exception {
		final Expression expected = ff.makeFreeIdentifier("A", null);
		doExpressionTest("((A))", expected);
	}

	public void testUnion() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BUNION,
				Arrays.<Expression> asList(ff.makeFreeIdentifier("A", null)
						, ff.makeFreeIdentifier("B", null)), null);
		doExpressionTest("A∪B", expected);
	}

	public void testInter() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BINTER,
				Arrays.<Expression> asList(ff.makeFreeIdentifier("A", null)
						, ff.makeFreeIdentifier("B", null)), null);
		doExpressionTest("A∩B", expected);
	}

	public void testUnionInter() throws Exception {
		final Expression expected = ff
				.makeAssociativeExpression(BINTER, Arrays.<Expression> asList(
						ff.makeFreeIdentifier("A", null),
						ff.makeAssociativeExpression(BUNION, Arrays.<Expression> asList(
								ff.makeFreeIdentifier("B", null),
								ff.makeFreeIdentifier("C", null)),
										null)), null);
		doExpressionTest("A∩(B∪C)", expected);
	}
	
	public void testUnionInterNoParen() throws Exception {
		final IParseResult result = ff.parseExpression("A∩B∪C",
				LanguageVersion.V2, null);
		assertTrue(result.hasProblem());
		final List<ASTProblem> problems = result.getProblems();
		System.out.println(problems);
	}

	public void testAnd() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(Formula.LAND,
				Arrays.<Predicate> asList(LIT_BTRUE, LIT_BFALSE), null);
		doPredicateTest("⊤∧⊥", expected);
	}
	
	public void testOrAnd() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(LOR,
				Arrays.<Predicate> asList(ff.makeAssociativePredicate(
						LAND, Arrays.<Predicate> asList(LIT_BTRUE, LIT_BFALSE),
						null), LIT_BFALSE),
				null);
		doPredicateTest("(⊤∧⊥)∨⊥", expected);
	}	
	
	public void testSourceLocation() throws Exception {
		final IParseResult result = ff.parsePredicate("(⊤∧⊥)∨⊥",
				LanguageVersion.V2, null);
		final Predicate pred = result.getParsedPredicate();
		assertNotNull(pred.getSourceLocation());
		final Predicate childFalse = ((AssociativePredicate) pred)
				.getChildren()[1];
		assertEquals(new SourceLocation(6, 6), childFalse.getSourceLocation());
	}
	
	private static final IExpressionExtension DIRECT_PRODUCT = new IExpressionExtension() {

		public void toString(IToStringMediator mediator,
				IExtendedFormula formula) {
			final Expression[] childExpressions = formula.getChildExpressions();
			mediator.append(childExpressions[0], false);
			mediator.append(getSyntaxSymbol());
			mediator.append(childExpressions[1], true);
		}

		public boolean isFlattenable() {
			return false;
		}

		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeChildWDConjunction(formula);
		}

		public String getSyntaxSymbol() {
			return "§";
		}

		public void checkPreconditions(Expression[] expressions,
				Predicate[] predicates) {
			assertTrue(expressions.length == 2);
			assertTrue(predicates.length == 0);
		}

		public Type typeCheck(ITypeCheckMediator tcMediator,
				ExtendedExpression expression) {
			final Type alpha = tcMediator.newTypeVariable();
			final Type beta = tcMediator.newTypeVariable();
			final Type gamma = tcMediator.newTypeVariable();
			final Type leftType = tcMediator.makeRelationalType(alpha, beta);
			final Type rightType = tcMediator.makeRelationalType(alpha, gamma);

			final Expression[] children = expression.getChildExpressions();
			tcMediator.sameType(children[0].getType(), leftType);
			tcMediator.sameType(children[1].getType(), rightType);

			final Type resultType = tcMediator.makeRelationalType(alpha,
					tcMediator.makeProductType(beta, gamma));
			return resultType;
		}

		public Type getType(ITypeMediator mediator,
				ExtendedExpression expression) {

			final Expression[] children = expression.getChildExpressions();
			Type leftType = children[0].getType();
			Type rightType = children[1].getType();

			final Type alpha = leftType.getSource();
			final Type beta = leftType.getTarget();
			final Type gamma = rightType.getTarget();
			if (alpha != null && beta != null && gamma != null
					&& alpha.equals(rightType.getSource())) {
				return ff.makeRelationalType(alpha, ff.makeProductType(beta,
						gamma));
			} else {
				return null;
			}
		}

		public String getGroupId() {
			return "My own group";
		}

		public String getId() {
			return "direct product extension";
		}

		public ExtensionKind getKind() {
			return ExtensionKind.ASSOCIATIVE_INFIX_EXPRESSION;
		}

		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility
		}

		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}

		public Associativity getAssociativity() {
			return Associativity.LEFT;
		}
	};

	public void testExtensionDirectProduct() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(DIRECT_PRODUCT));
		final Expression expected = extFac.makeExtendedExpression(DIRECT_PRODUCT,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeFreeIdentifier("B", null)),
				Collections.<Predicate> emptySet(), null);
		doExpressionTest("A§B", expected, extFac);
	}

	public void testEqual() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(EQUAL, ff
				.makeFreeIdentifier("A", null), ff
				.makeFreeIdentifier("B", null), null);
		doPredicateTest("A=B", expected);
	}
	
	public void testForall() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL,
				new BoundIdentDecl[] { BID_x }, LIT_BFALSE, null);
		doPredicateTest("∀x·⊥", expected);
	}

	public void testForallList() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL,
				new BoundIdentDecl[] { BID_x,
						ff.makeBoundIdentDecl("y", null),
						ff.makeBoundIdentDecl("z", null) },
						LIT_BFALSE, null);
		doPredicateTest("∀x,y,z·⊥", expected);
	}
	
	public void testForallRefs() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL,
				new BoundIdentDecl[] { BID_x,
						ff.makeBoundIdentDecl("y", null) },
						ff.makeRelationalPredicate(GT,
								ff.makeBoundIdentifier(1, null),
								BI_0, null), null);
		doPredicateTest("∀x,y·x>y", expected);
	}
	
	public void testExists() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(EXISTS,
				new BoundIdentDecl[] { BID_x }, LIT_BFALSE, null);
		doPredicateTest("∃x·⊥", expected);
	}

	public void testExistsList() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(EXISTS,
				new BoundIdentDecl[] { BID_x,
						ff.makeBoundIdentDecl("y", null),
						ff.makeBoundIdentDecl("z", null) }, LIT_BFALSE, null);
		doPredicateTest("∃x,y,z·⊥", expected);
	}
	
	public void testGT() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(GT,
				ff.makeFreeIdentifier("x", null),
				ZERO, null);
		doPredicateTest("x>0", expected);
	}

	public void testLE() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(LE,
				ff.makeFreeIdentifier("x", null),
				ZERO, null);
		doPredicateTest("x≤0", expected);
	}

	public void testFunImage() throws Exception {
		final Expression expected = ff.makeBinaryExpression(FUNIMAGE,
				ff.makeFreeIdentifier("f", null),
				ZERO, null);
		doExpressionTest("f(0)", expected);
	}

	public void testCard() throws Exception {
		final Expression expected = ff.makeUnaryExpression(KCARD,
				FRID_S, null);
		doExpressionTest("card(S)", expected);
	}
	
	public void testIn() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(IN, ZERO, FRID_S, null);
		doPredicateTest("0 ∈ S", expected);		
	}
	
	public void testInt() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(IN, ZERO, INT, null);
		doPredicateTest("0 ∈ ℤ", expected);
	}
	
	public void testPowerSet() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(IN, FRID_S, POW_INT, null);
		doPredicateTest("S ∈ ℙ(ℤ)", expected);
	}
	
	public void testCartProd() throws Exception {
		final Expression expected = ff.makeBinaryExpression(CPROD, FRID_S, FRID_S, null);
		doExpressionTest("S × S", expected);
	}
	
	public void testSingleton() throws Exception {
		final Expression expected = ff.makeSetExtension(ZERO, null);
		doExpressionTest("{0}", expected);		
	}
	
	public void testSetExtension() throws Exception {
		final Expression expected = ff.makeSetExtension(Arrays
				.<Expression> asList(ZERO, ONE), null);
		doExpressionTest("{0,1}", expected);		
	}
	
	public void testEmptySet() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(IN,
				ZERO,
				EMPTY, null);
		doPredicateTest("0 ∈ ∅", expected);		
	}
	
	public void testParseTypeInt() throws Exception {
		final Type expected = INT_TYPE;
		doTypeTest("ℤ", expected);
	}
	
	public void testParseTypeRelational() throws Exception {
		final Type expected = ff.makeRelationalType(INT_TYPE, INT_TYPE);
		doTypeTest("ℙ(ℤ×ℤ)", expected);
	}

	// TODO parsing a given type requires to introduce either a notion of
	// 'parsing a type' in parser context, or backtracking
	public void testParseTypeGivenType() throws Exception {
		final Type expected = S_TYPE;
		doTypeTest("S", expected);
	}
	
	public void testEmptySetOfType() throws Exception {
		final Expression expected = ff.makeEmptySet(POW_INT_TYPE, null);
		doExpressionTest("∅ ⦂ ℙ(ℤ)", expected);		
	}
	
	public void testCSetExplicit() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_x), LIT_BTRUE, BI_0, null, Form.Explicit);
		doExpressionTest("{x · ⊤ ∣ x}", expected);		
	}
	
	public void testCSetImplicit() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_x), LIT_BTRUE, BI_0, null, Form.Implicit);
	doExpressionTest("{x∣ ⊤}", expected);		

	}
	
	public void testMapsto() throws Exception {
		final Expression expected = ff.makeBinaryExpression(MAPSTO, ZERO,
				FRID_S, null);
		doExpressionTest("0 ↦ S", expected);		
	}
	
	public void testLambda() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_x), LIT_BTRUE,
				ff.makeBinaryExpression(MAPSTO, BI_0, BI_0, null), null,
				Form.Lambda);
		doExpressionTest("(λx·⊤∣ x)", expected);
	}
}
