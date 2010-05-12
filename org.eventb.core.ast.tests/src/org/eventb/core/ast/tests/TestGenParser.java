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
import org.eventb.core.ast.Assignment;
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
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.extension.CycleError;
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

	private static final BoundIdentifier BI_0 = ff.makeBoundIdentifier(0, null);
	private static final BoundIdentifier BI_1 = ff.makeBoundIdentifier(1, null);
	private static final BoundIdentifier BI_2 = ff.makeBoundIdentifier(2, null);
	private static final BoundIdentifier BI_3 = ff.makeBoundIdentifier(3, null);
	private static final BoundIdentDecl BID_u = ff.makeBoundIdentDecl("u", null);
	private static final BoundIdentDecl BID_x = ff.makeBoundIdentDecl("x", null);
	private static final BoundIdentDecl BID_y = ff.makeBoundIdentDecl("y", null);
	private static final BoundIdentDecl BID_z = ff.makeBoundIdentDecl("z", null);
	private static final LiteralPredicate LIT_BFALSE = ff.makeLiteralPredicate(
							Formula.BFALSE, null);
	private static final LiteralPredicate LIT_BTRUE = ff.makeLiteralPredicate(
							Formula.BTRUE, null);
	private static final AtomicExpression ATOM_TRUE = ff.makeAtomicExpression(TRUE, null);
	private static final IntegerLiteral ZERO = ff.makeIntegerLiteral(BigInteger.ZERO, null);
	private static final IntegerLiteral ONE = ff.makeIntegerLiteral(BigInteger.ONE, null);
	private static final AtomicExpression EMPTY = ff.makeEmptySet(null, null);
	private static final FreeIdentifier FRID_S = ff.makeFreeIdentifier("S", null);
	private static final GivenType S_TYPE = ff.makeGivenType("S");
	private static final PowerSetType POW_S_TYPE = ff.makePowerSetType(S_TYPE);
	private static final FreeIdentifier FRID_a = ff.makeFreeIdentifier("a", null);
	private static final FreeIdentifier FRID_b = ff.makeFreeIdentifier("b", null);
	private static final FreeIdentifier FRID_c = ff.makeFreeIdentifier("c", null);
	private static final AtomicExpression INT = ff.makeAtomicExpression(Formula.INTEGER, null);
	private static final UnaryExpression POW_INT = ff.makeUnaryExpression(POW, INT, null);
	private static final IntegerType INT_TYPE = ff.makeIntegerType();
	private static final PowerSetType POW_INT_TYPE = ff.makePowerSetType(INT_TYPE);
	private static final SourceLocationChecker slChecker = new SourceLocationChecker();

	private static void assertFailure(IParseResult result, ProblemKind problemKind) {
		assertTrue(result.hasProblem());
		final List<ASTProblem> problems = result.getProblems();
		System.out.println(problems);
		assertEquals(1, problems.size());
		final ASTProblem problem = problems.get(0);
		assertTrue(problem.isError());
		assertEquals(problemKind, problem.getMessage());
	}

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
	
	private void doAssignmentTest(String formula, Assignment expected) {
		final IParseResult result = ff.parseAssignment(formula,
				LanguageVersion.V2, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Assignment actual = result.getParsedAssignment();
		System.out.println(actual);
		assertEquals(expected, actual);
	
		actual.accept(slChecker);
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
		assertFailure(result, ProblemKind.SyntaxError);
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
	
	public void testSourceLocation2() throws Exception {
		final IParseResult result = ff.parsePredicate("⊤∧(⊥∨⊥        )",
				LanguageVersion.V2, null);
		final Predicate pred = result.getParsedPredicate();
		assertNotNull(pred.getSourceLocation());
		final Predicate childFalseOrFalse = ((AssociativePredicate) pred)
				.getChildren()[1];
		assertEquals(new SourceLocation(3, 5), childFalseOrFalse.getSourceLocation());
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
			return ExtensionKind.BINARY_INFIX_EXPRESSION;
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

	private static final IExpressionExtension MONEY = new IExpressionExtension() {
		private static final String SYNTAX_SYMBOL = "€";
		private static final String OPERATOR_ID = "Money";
		
		public Type getType(ITypeMediator mediator,
				ExtendedExpression expression) {
			final Expression[] children = expression.getChildExpressions();
			final Type resultType = children[0].getType();
			for (Expression child: children) {
				final Type childType = child.getType();
				if (! (childType instanceof IntegerType)) {
					return null;
				}
			}
			return resultType;
		}

		public Type typeCheck(ITypeCheckMediator tcMediator,
				ExtendedExpression expression) {
			final Expression[] children = expression.getChildExpressions();
			final Type resultType = tcMediator.makeIntegerType();
			for (int i = 0; i < children.length; i++) {
				tcMediator.sameType(children[i].getType(), resultType);
			}
			return resultType;
		}

		public void addCompatibilities(ICompatibilityMediator mediator) {
			mediator.addCompatibility(getId(), getId());
		}

		public void addPriorities(IPriorityMediator mediator) {
			try {
				mediator.addPriority(getId(), "plus");
			} catch (CycleError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void checkPreconditions(Expression[] expressions,
				Predicate[] predicates) {
			assertTrue(expressions.length >= 2);
			assertTrue(predicates.length == 0);
		}

		public Associativity getAssociativity() {
			return Associativity.LEFT;
		}

		public String getGroupId() {
			return "Arithmetic";
		}

		public String getId() {
			return OPERATOR_ID;
		}

		public ExtensionKind getKind() {
			return ExtensionKind.ASSOCIATIVE_INFIX_EXPRESSION;
		}

		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
		}

		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeChildWDConjunction(formula);
		}

		public boolean isFlattenable() {
			return true;
		}

		public void toString(IToStringMediator mediator,
				IExtendedFormula formula) {
			final Expression[] childExpressions = formula.getChildExpressions();
			mediator.append(childExpressions[0], false);
			for (int i = 1; i < childExpressions.length; i++) {
				mediator.append(getSyntaxSymbol());
				mediator.append(childExpressions[i], true);
			}
		}
	};

	// verify that the newly introduced symbol cannot be part of an identifier
	public void testExtensionSymbol() throws Exception {
		final String strAEuroB = "A€B";
		
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(MONEY));
		
		assertTrue(
				"€ symbol should be a valid part of identifier for default factory",
				ff.isValidIdentifierName(strAEuroB));
		assertTrue(
				"€ symbol should not be a valid part of identifier for extended factory",
				extFac.isValidIdentifierName(strAEuroB));
		
		final FreeIdentifier expectedDefault = ff.makeFreeIdentifier(strAEuroB, null);
		doExpressionTest(strAEuroB, expectedDefault, ff);
	
		final FreeIdentifier expectedExtended = extFac.makeFreeIdentifier(strAEuroB, null);
		doExpressionTest(strAEuroB, expectedExtended, extFac);

		final Expression expectedExtendedSpaced = extFac.makeExtendedExpression(MONEY,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeFreeIdentifier("B", null)),
				Collections.<Predicate> emptySet(), null);
		doExpressionTest("A € B", expectedExtendedSpaced, extFac);
	}
	
	public void testAssociativeExtension() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(MONEY));
		final Expression expected = extFac.makeExtendedExpression(MONEY,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeFreeIdentifier("B", null),
						extFac.makeFreeIdentifier("C", null)),
				Collections.<Predicate> emptySet(), null);
		doExpressionTest("A € B € C", expected, extFac);
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
								BI_1,
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
		doExpressionTest("λx·⊤∣ x", expected);
	}
	
	public void testLambdaMaplet() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_x, BID_y),
				ff.makeRelationalPredicate(GT, BI_1, BI_0, null),
				ff.makeBinaryExpression(MAPSTO,
						ff.makeBinaryExpression(MAPSTO,	BI_1, BI_0, null),
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_1, BI_0), null),
						null),
				null, Form.Lambda);
		doExpressionTest("λx↦y·x>y∣ x+y", expected);
	}

	public void testLambdaMaplet2() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_x, BID_y, BID_z),
				ff.makeRelationalPredicate(GT,
						BI_2,
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_1, BI_0), null),
						null),
				ff.makeBinaryExpression(MAPSTO,
						ff.makeBinaryExpression(MAPSTO,
								ff.makeBinaryExpression(MAPSTO, BI_2, BI_1, null),
								BI_0,
								null),
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_2, BI_1, BI_0), null),
						null),
				null, Form.Lambda);
		doExpressionTest("λx↦y↦z·x>y+z∣ x+y+z", expected);
	}

	public void testLambdaMapletParentheses() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_x, BID_y, BID_z),
				ff.makeRelationalPredicate(GT,
						BI_2,
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_1, BI_0), null),
						null),
				ff.makeBinaryExpression(MAPSTO,
						ff.makeBinaryExpression(MAPSTO,
								BI_2, 
								ff.makeBinaryExpression(MAPSTO, BI_1, BI_0, null), null),
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_2, BI_1, BI_0), null),
						null),
				null, Form.Lambda);
		doExpressionTest("λx↦(y↦z)·x>y+z∣ x+y+z", expected);
	}
	
	public void testLambdaMapletParentheses2() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_u, BID_x, BID_y, BID_z),
				ff.makeRelationalPredicate(GT,
						BI_3,
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_2, BI_1, BI_0), null),
						null),
				ff.makeBinaryExpression(MAPSTO,
						ff.makeBinaryExpression(MAPSTO,
								ff.makeBinaryExpression(MAPSTO, BI_3, BI_2, null),
								ff.makeBinaryExpression(MAPSTO, BI_1, BI_0, null), null),
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_3, BI_2, BI_1, BI_0), null),
						null),
				null, Form.Lambda);
		doExpressionTest("λ(u↦x)↦(y↦z)·u>x+y+z∣ u+x+y+z", expected);
	}
	
	public void testLambdaMapletParentheses3() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_u, BID_x, BID_y, BID_z),
				ff.makeRelationalPredicate(GT,
						BI_3,
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_2, BI_1, BI_0), null),
						null),
				ff.makeBinaryExpression(MAPSTO,
						ff.makeBinaryExpression(MAPSTO,
								BI_3,
								ff.makeBinaryExpression(MAPSTO, 
										BI_2,
										ff.makeBinaryExpression(MAPSTO, BI_1, BI_0, null), null), null),
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_3, BI_2, BI_1, BI_0), null),
						null),
				null, Form.Lambda);
		doExpressionTest("λu↦(x↦(y↦z))·u>x+y+z∣ u+x+y+z", expected);
	}
	
	public void testLambdaDuplicateIdents() throws Exception {
		final IParseResult result = ff.parseExpression("λx↦(y↦x)·x>y∣ x+y",
				LanguageVersion.V2, null);
		assertFailure(result, ProblemKind.SyntaxError);
	}

	public void testInnerBoundIdentsForall() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL,
				new BoundIdentDecl[] { BID_x },
				ff.makeQuantifiedPredicate(EXISTS,
						new BoundIdentDecl[] { BID_y },
						ff.makeRelationalPredicate(GT,
								BI_1,
								BI_0, null), null), null);
		doPredicateTest("∀x·∃y·x>y", expected);
	}
	
	public void testInnerBoundIdentsCSet() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_x),
				ff.makeQuantifiedPredicate(EXISTS,
						new BoundIdentDecl[] { BID_y },
						ff.makeRelationalPredicate(GT,
								BI_1,
								BI_0, null),
						null),
				BI_0, null, Form.Implicit);
		doExpressionTest("{x∣ ∃y·x>y}", expected);		
	}
	
	public void testLIMP() throws Exception {
		final Predicate expected = 
				ff.makeBinaryPredicate(LIMP,
						LIT_BFALSE, LIT_BFALSE, null);
		doPredicateTest("⊥⇒⊥", expected);
	}
	
	// verify that a bound identifier reference after an inner bound predicate
	// is parsed properly (involves boundStack.pop())
	public void testBoundAfterInnerBound() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL,
				new BoundIdentDecl[] { BID_x },
				ff.makeBinaryPredicate(LIMP,
						ff.makeQuantifiedPredicate(EXISTS,
								new BoundIdentDecl[] { BID_y },
							ff.makeRelationalPredicate(GT,
									BI_1,
									BI_0, null), null),
						ff.makeRelationalPredicate(GT,
								BI_0,
								ZERO, null),
						null), null);
		doPredicateTest("∀x·(∃y·x>y)⇒x>0", expected);
	}

	public void testTrue() throws Exception {
		final Expression expected = ff.makeAtomicExpression(TRUE, null);
		doExpressionTest("TRUE", expected);
	}
	
	public void testAssignment() throws Exception {
		final Assignment expected = ff.makeBecomesEqualTo(FRID_a, ZERO, null);
		doAssignmentTest("a ≔ 0", expected);
	}

	public void testAssignmentList() throws Exception {
		final Assignment expected = ff.makeBecomesEqualTo(
				asList(FRID_a, FRID_b, FRID_c),
				asList(ZERO, EMPTY, ATOM_TRUE), null);
		doAssignmentTest("a,b,c ≔ 0,∅,TRUE", expected);
	}
	
	public void testBecomesMemberOf() throws Exception {
		final Assignment expected = ff.makeBecomesMemberOf(FRID_a, FRID_S, null);
		doAssignmentTest("a :∈ S", expected);
	}

	public void testBecomesMemberOfList() throws Exception {
		assertFailure(ff.parseAssignment("a,b :∈ S", LanguageVersion.V2, null),
				ProblemKind.SyntaxError);
		assertFailure(ff.parseAssignment("a,b :∈ S,S", LanguageVersion.V2, null),
				ProblemKind.SyntaxError);
	}

	public void testBecomesSuchThat() throws Exception {
		final Assignment expected = ff.makeBecomesSuchThat(FRID_a,
				FRID_a.asPrimedDecl(ff), LIT_BTRUE, null);
		doAssignmentTest("a :∣  ⊤", expected);
	}

	public void testBecomesSuchThatList() throws Exception {
		final List<FreeIdentifier> idents = asList(FRID_a, FRID_b);
		final List<BoundIdentDecl> primed = asList(FRID_a.asPrimedDecl(ff),
				FRID_b.asPrimedDecl(ff));
		final Assignment expected = ff.makeBecomesSuchThat(idents, primed,
				LIT_BTRUE, null);
		doAssignmentTest("a,b :∣  ⊤", expected);
	}

	public void testBecomesSuchThatPrimed() throws Exception {
		final List<FreeIdentifier> idents = asList(FRID_a, FRID_b);
		final List<BoundIdentDecl> primed = asList(FRID_a.asPrimedDecl(ff),
				FRID_b.asPrimedDecl(ff));
		final Predicate condition = ff.makeAssociativePredicate(LAND, Arrays.<Predicate>asList(
				ff.makeRelationalPredicate(EQUAL, BI_1, FRID_b, null),
				ff.makeRelationalPredicate(EQUAL, BI_0, FRID_a, null)), null);
		final Assignment expected = ff.makeBecomesSuchThat(idents, primed,
				condition, null);
		doAssignmentTest("a,b :∣  a'=b ∧ b'=a  ", expected);
	}

}
