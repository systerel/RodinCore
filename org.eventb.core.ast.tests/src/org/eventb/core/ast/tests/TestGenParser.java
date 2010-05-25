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
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
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
	private static final FreeIdentifier FRID_A = ff.makeFreeIdentifier("A", null);
	private static final FreeIdentifier FRID_B = ff.makeFreeIdentifier("B", null);
	private static final FreeIdentifier FRID_C = ff.makeFreeIdentifier("C", null);
	private static final FreeIdentifier FRID_f = ff.makeFreeIdentifier("f", null);
	private static final PredicateVariable PV_P = ff.makePredicateVariable("$P", null);
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
	
	private static void checkSourceLocation(Formula<?> formula, int length) {
		for (int i = 0; i < length; i++) {
			for (int j = i; j < length; j++) {
				final SourceLocation sloc = new SourceLocation(i, j);
				final IPosition pos = formula.getPosition(sloc);
				if (!formula.contains(sloc)) {
					assertNull(pos);
					break;
				}
				assertNotNull("null position for location " + sloc
						+ " in formula " + formula + " with location: "
						+ formula.getSourceLocation(), pos);
				final Formula<?> actual = formula.getSubFormula(pos);
				assertTrue(actual.getSourceLocation().contains(sloc));
			}
		}
	}
	
	private static <T extends Formula<T>> void checkParsedFormula(String formula,
			T expected, T actual) {
		System.out.println(actual);
		assertEquals(expected, actual);
		
		actual.accept(slChecker);
		checkSourceLocation(actual, formula.length());
	}
	
	private static Expression doExpressionTest(String formula, Expression expected,
			FormulaFactory factory, LanguageVersion version) {
		final IParseResult result = factory.parseExpression(formula,
				version, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Expression actual = result.getParsedExpression();
		checkParsedFormula(formula, expected, actual);
		return actual;
	}

	private static Expression doExpressionTest(String formula, Expression expected, FormulaFactory factory) {
		return doExpressionTest(formula, expected, factory, LanguageVersion.V2);
	}
	
	private static Expression doExpressionTest(String formula, Expression expected, Type expectedType) {
		final Expression actual = doExpressionTest(formula, expected);
		assertEquals(expectedType, actual.getType());
		return actual;
	}
	
	private static Expression doExpressionTest(String formula, Expression expected) {
		return doExpressionTest(formula, expected, ff);
	}
	
	private static Predicate doPredicateTest(String formula, Predicate expected) {
		return doPredicateTest(formula, expected, LanguageVersion.V2);
	}
	
	private static Predicate doPredicateTest(String formula, Predicate expected, LanguageVersion version) {
		final IParseResult result = ff.parsePredicate(formula,
				version, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Predicate actual = result.getParsedPredicate();
		checkParsedFormula(formula, expected, actual);
		return actual;
	}
	
	private static void doQuantPredicateTest(String formula, QuantifiedPredicate expected, Type...types) {
		final Predicate actual = doPredicateTest(formula, expected);
		final QuantifiedPredicate quant = (QuantifiedPredicate) actual;
		final BoundIdentDecl[] boundIdents = quant.getBoundIdentDecls();
		assertBoundTypes(boundIdents, types);
	}
	
	private static void doQuantExpressionTest(String formula, QuantifiedExpression expected, Type...types) {
		final Expression actual = doExpressionTest(formula, expected);
		final QuantifiedExpression quant = (QuantifiedExpression) actual;
		final BoundIdentDecl[] boundIdents = quant.getBoundIdentDecls();
		assertBoundTypes(boundIdents, types);
	}
	
	private static void assertBoundTypes(BoundIdentDecl[] boundIdents, Type...types) {
		assertEquals(types.length, boundIdents.length);
		for (int i = 0; i < types.length; i++) {
			assertEquals(types[i], boundIdents[i].getType());
		}
	}

	private static void doPredicatePatternTest(String formula, Predicate expected) {
		final IParseResult result = ff.parsePredicatePattern(formula,
				LanguageVersion.V2, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Predicate actual = result.getParsedPredicate();
		checkParsedFormula(formula, expected, actual);
	}

	private static void doTypeTest(String formula, Type expected) {
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
	
	private static void doAssignmentTest(String formula, Assignment expected) {
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

	private static void doTest(String formula, Formula<?> expected, LanguageVersion version) {
		if (expected instanceof Expression) {
			doExpressionTest(formula, (Expression) expected,ff, version);
		} else if (expected instanceof Predicate) {
			doPredicateTest(formula, (Predicate) expected, version);
		}
	}
	
	private static void doVersionTest(String formula, Formula<?> expectedV1, Formula<?> expectedV2) {
		doTest(formula, expectedV1, LanguageVersion.V1);
		doTest(formula, expectedV2, LanguageVersion.V2);
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
				.makeAssociativeExpression(PLUS, Arrays.<Expression> asList(
								ff.makeIntegerLiteral(BigInteger.valueOf(1), null),
								ff.makeIntegerLiteral(BigInteger.valueOf(2), null),
								ff.makeIntegerLiteral(BigInteger.valueOf(3), null)), null);
		doExpressionTest("1+2+3", expected);
	}

	public void testPlusAssoWithParenLeft() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(PLUS, Arrays.<Expression> asList(
								ff.makeAssociativeExpression(PLUS, Arrays.<Expression> asList(
										ff.makeIntegerLiteral(BigInteger.valueOf(1), null),
										ff.makeIntegerLiteral(BigInteger.valueOf(2), null)), null),
								ff.makeIntegerLiteral(BigInteger.valueOf(3), null)), null);
		doExpressionTest("(1+2)+3", expected);
	}

	public void testPlusAssoWithParenRight() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(PLUS, Arrays.<Expression> asList(
								ff.makeIntegerLiteral(BigInteger.valueOf(1), null),
								ff.makeAssociativeExpression(PLUS, Arrays.<Expression> asList(
										ff.makeIntegerLiteral(BigInteger.valueOf(2), null),
										ff.makeIntegerLiteral(BigInteger.valueOf(3), null)), null)), null);
		doExpressionTest("1+(2+3)", expected);
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
	
	// verifies that parentheses do correctly restore priorities when closed
	// when it fails, the result is 1∗(2+3)
	public void testPlusMultParen() throws Exception {
		final Expression expected = ff
				.makeAssociativeExpression(PLUS, Arrays
						.<Expression> asList(
								ff.makeAssociativeExpression(MUL, Arrays.<Expression> asList(ff
										.makeIntegerLiteral(BigInteger
												.valueOf(1), null), ff
										.makeIntegerLiteral(BigInteger
												.valueOf(2), null)), null),
												ff.makeIntegerLiteral(BigInteger.valueOf(3), null)
						), null);
		doExpressionTest("1∗(2)+3", expected);
	}
	
	public void testIdentDoubleParen() throws Exception {
		final Expression expected = FRID_A;
		doExpressionTest("((A))", expected);
	}

	public void testUnion() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BUNION,
				Arrays.<Expression> asList(FRID_A, FRID_B), null);
		doExpressionTest("A∪B", expected);
	}

	public void testInter() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BINTER,
				Arrays.<Expression> asList(FRID_A, FRID_B), null);
		doExpressionTest("A∩B", expected);
	}

	public void testUnionInter() throws Exception {
		final Expression expected = ff
				.makeAssociativeExpression(BINTER, Arrays.<Expression> asList(
						FRID_A,
						ff.makeAssociativeExpression(BUNION, Arrays.<Expression> asList(
								FRID_B,
								FRID_C),
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
	
	public void testAndAsso() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(Formula.LAND,
				Arrays.<Predicate> asList(LIT_BTRUE, LIT_BTRUE, LIT_BTRUE), null);
		doPredicateTest("⊤∧⊤∧⊤", expected);
	}
	
	public void testAndAssoWithParenLeft() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(Formula.LAND, 
				Arrays.<Predicate> asList(
						ff.makeAssociativePredicate(Formula.LAND,
								Arrays.<Predicate> asList(LIT_BTRUE, LIT_BTRUE), null),
						LIT_BTRUE), null);
		doPredicateTest("(⊤∧⊤)∧⊤", expected);
	}
	
	public void testAndAssoWithParenRight() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(Formula.LAND, 
				Arrays.<Predicate> asList(
						LIT_BTRUE,
						ff.makeAssociativePredicate(Formula.LAND,
								Arrays.<Predicate> asList(LIT_BTRUE, LIT_BTRUE), null)
						), null);
		doPredicateTest("⊤∧(⊤∧⊤)", expected);
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

		public boolean checkPreconditions(Expression[] expressions,
				Predicate[] predicates) {
			return expressions.length == 2 && predicates.length == 0;
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

		public boolean checkPreconditions(Expression[] expressions,
				Predicate[] predicates) {
			return expressions.length >= 2 && predicates.length == 0;
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

	// verify that the newly introduced symbol cannot be an identifier
	public void testExtensionSymbol() throws Exception {
		final String strAEuroB = "A€B";
		
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(MONEY));
		
		assertTrue(
				"€ symbol should be a valid part of identifier for default factory",
				ff.isValidIdentifierName(strAEuroB));
		assertTrue(
				"€ symbol should be a valid part of identifier for extended factory",
				extFac.isValidIdentifierName(strAEuroB));
		assertFalse(
				"€ symbol should not be a valid identifier for extended factory",
				extFac.isValidIdentifierName("€"));
		
		final FreeIdentifier expectedDefault = ff.makeFreeIdentifier(strAEuroB, null);
		doExpressionTest(strAEuroB, expectedDefault, ff);
	
		final FreeIdentifier expectedExtended = extFac.makeFreeIdentifier(strAEuroB, null);
		doExpressionTest(strAEuroB, expectedExtended, extFac);

		// considered an operator only if surrounded with spaces
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
		final Predicate expected = ff.makeRelationalPredicate(EQUAL,
				FRID_A,
				FRID_B, null);
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
				FRID_a,
				ZERO, null);
		doPredicateTest("a>0", expected);
	}

	public void testLE() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(LE,
				FRID_a,
				ZERO, null);
		doPredicateTest("a≤0", expected);
	}

	public void testFunImage() throws Exception {
		final Expression expected = ff.makeBinaryExpression(FUNIMAGE,
				FRID_f,
				ZERO, null);
		doExpressionTest("f(0)", expected);
	}

	public void testFunImageLeftAssociativity() throws Exception {
		final Expression expected = ff.makeBinaryExpression(FUNIMAGE,
				ff.makeBinaryExpression(FUNIMAGE,
						FRID_f,
						ZERO, null),
					ONE, null);
		doExpressionTest("f(0)(1)", expected);
	}

	public void testFunImageInner() throws Exception {
		final Expression expected = ff.makeBinaryExpression(FUNIMAGE,
				FRID_f,
				ff.makeBinaryExpression(FUNIMAGE,
						FRID_f,
						ZERO, null), null);
		doExpressionTest("f(f(0))", expected);
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
	
	public void testSetExtensionEmpty() throws Exception {
		final Expression expected = ff.makeSetExtension(Arrays
				.<Expression> asList(), null);
		doExpressionTest("{}", expected);
	}
	
	// verifies that priority between Maplet and Ovr is not taken into account
	// inside braces
	public void testSetExtensionPriority() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(OVR, Arrays.<Expression>asList(
			FRID_f,
			ff.makeSetExtension(
				ff.makeBinaryExpression(MAPSTO, ZERO, ONE, null), null)), null);
		doExpressionTest("f{0↦1}", expected);		
	}

	public void testSetExtensionEqual() throws Exception {
		final Predicate expected = ff.makeRelationalPredicate(EQUAL,
				ff.makeSetExtension(Arrays.<Expression> asList(ZERO, ONE), null),
				FRID_f, null);
		doPredicateTest("{0,1}=f", expected);
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
		doExpressionTest("∅ ⦂ ℙ(ℤ)", expected, POW_INT_TYPE);
	}
	
	public void testIdOfType() throws Exception {
		final Expression expected = ff.makeAtomicExpression(KID_GEN, null, ff
				.makeRelationalType(INT_TYPE, INT_TYPE));
		doExpressionTest("id ⦂ ℙ(ℤ×ℤ)", expected, ff.makePowerSetType(ff
				.makeProductType(INT_TYPE, INT_TYPE)));
	}
	
	public void testPrj1OfType() throws Exception {
		final PowerSetType expectedType = ff
				.makeRelationalType(ff.makeProductType(S_TYPE, INT_TYPE),
						S_TYPE);
		final Expression expected = ff.makeAtomicExpression(KPRJ1_GEN, null, expectedType);
		doExpressionTest("prj1 ⦂ ℙ(S×ℤ×S)", expected, expectedType);		
	}
	
	public void testPrj2OfType() throws Exception {
		final PowerSetType expectedType = ff
				.makeRelationalType(ff.makeProductType(INT_TYPE, S_TYPE),
						S_TYPE);
		final Expression expected = ff.makeAtomicExpression(KPRJ2_GEN, null, expectedType);
		doExpressionTest("prj2 ⦂ ℙ(ℤ×S×S)", expected, expectedType);		
	}
	
	public void testBoundIdentDeclOfType() throws Exception {
		final QuantifiedPredicate expected = ff.makeQuantifiedPredicate(FORALL,
				asList( ff.makeBoundIdentDecl("x", null, INT_TYPE) ),
				LIT_BFALSE, null);
		doQuantPredicateTest("∀x⦂ℤ·⊥", expected, INT_TYPE);
	}
	
	public void testBoundIdentDeclSeveralOfType() throws Exception {
		final QuantifiedPredicate expected = ff.makeQuantifiedPredicate(FORALL,
				asList(ff.makeBoundIdentDecl("x", null, INT_TYPE),
						ff.makeBoundIdentDecl("y", null),
						ff.makeBoundIdentDecl("z", null, POW_S_TYPE)),
				LIT_BFALSE, null);
		doQuantPredicateTest("∀x⦂ℤ,y,z⦂ℙ(S)·⊥", expected, INT_TYPE, null, POW_S_TYPE);
	}

	public void testBoundIdentDeclExprOfType() throws Exception {
		final QuantifiedExpression expected = ff.makeQuantifiedExpression(QUNION,
				asList(BID_x),
				ff.makeRelationalPredicate(GT,
						BI_0,
						ZERO, null),
				ff.makeAssociativeExpression(MUL,
						asList(ONE, BI_0), null),
				null, Form.Explicit);
		doQuantExpressionTest("⋃x⦂ℤ·x>0∣1∗x", expected, INT_TYPE);
	}
	
	public void testBoundIdentDeclLambdaOfType() throws Exception {
		final BoundIdentDecl bid_x_INT = ff.makeBoundIdentDecl("x", null, INT_TYPE);
		final BoundIdentDecl bid_y_S = ff.makeBoundIdentDecl("y", null, S_TYPE);
		final BoundIdentifier bi1_INT = ff.makeBoundIdentifier(1, null, INT_TYPE);
		final BoundIdentifier bi0_S = ff.makeBoundIdentifier(0, null, S_TYPE);
		final QuantifiedExpression expected = ff.makeQuantifiedExpression(CSET,
				asList(bid_x_INT,
						bid_y_S),
				ff.makeRelationalPredicate(GT,
						BI_1,
						BI_0, null),
				ff.makeBinaryExpression(MAPSTO,
						// FIXME why are bound identifiers typed here and not elsewhere ?
						ff.makeBinaryExpression(MAPSTO,	bi1_INT, bi0_S, null),
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(BI_1, BI_0), null),
						null),
				null, Form.Lambda);
		doQuantExpressionTest("λx⦂ℤ↦y⦂S·x>y∣ x+y", expected, INT_TYPE, S_TYPE);
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
	
	// verifies that priority between Maplet and Ovr is not taken into account
	// inside braces
	public void testCSetPriority() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(OVR, Arrays.<Expression>asList(
				FRID_f,
				ff.makeQuantifiedExpression(CSET,
						asList(BID_x),
						LIT_BTRUE,
						ff.makeBinaryExpression(MAPSTO, BI_0, BI_0, null),
						null, Form.Implicit)), null);
		doExpressionTest("f{x↦x ∣ ⊤}", expected);		
	}
	
	public void testForallCSetPriority() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL,
				new BoundIdentDecl[] { BID_x },
				ff.makeRelationalPredicate(NOTEQUAL,
				ff.makeQuantifiedExpression(CSET,
						asList(BID_y),
						ff.makeRelationalPredicate(GT,
								BI_1,
								BI_0, null),
								ff.makeBinaryExpression(MINUS, BI_1, BI_0, null),
								null, Form.Explicit), EMPTY, null),
				 null);

		doPredicateTest("∀x·{y·x>y∣x−y}≠∅", expected);		
	}
	
	public void testCSetForallPriority() throws Exception {
						
		final Expression expected =			
				ff.makeQuantifiedExpression(CSET,
						asList(BID_y),
						ff.makeQuantifiedPredicate(FORALL,
								new BoundIdentDecl[] { BID_x },
								ff.makeRelationalPredicate(GT,
										BI_0,
										BI_1, null), null),
						BI_0, null, Form.Implicit);

		doExpressionTest("{y∣∀x·x>y}", expected);		
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
	
	public void testBecomesEqualTo() throws Exception {
		final Assignment expected = ff.makeBecomesEqualTo(FRID_a, ZERO, null);
		doAssignmentTest("a ≔ 0", expected);
	}

	public void testBecomesEqualToList() throws Exception {
		final Assignment expected = ff.makeBecomesEqualTo(
				asList(FRID_a, FRID_b, FRID_c),
				asList(ZERO, EMPTY, ATOM_TRUE), null);
		doAssignmentTest("a,b,c ≔ 0,∅,TRUE", expected);
	}
	
	public void testFunImageBecomesEqualTo() throws Exception {
		final Expression overriding = makeFunctionOverriding(FRID_f, FRID_a, ZERO);
		final Assignment expected = ff.makeBecomesEqualTo(FRID_f, overriding, null);
		doAssignmentTest("f(a) ≔ 0", expected);
	}
	
	private static Expression makeFunctionOverriding(FreeIdentifier ident,
			Expression index, Expression value) {
		Expression pair = ff.makeBinaryExpression(Formula.MAPSTO, index, value,
				null);
		Expression singletonSet = ff.makeSetExtension(pair, null);
		return ff.makeAssociativeExpression(Formula.OVR, new Expression[] {
				ident, singletonSet }, null);
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

	public void testNot() throws Exception {
		final Predicate expected = ff.makeUnaryPredicate(NOT,
				LIT_BTRUE, null);
		doPredicateTest("¬⊤", expected);
	}
	
	public void testTotalFunction() throws Exception {
		final Expression expected = ff.makeBinaryExpression(TFUN, FRID_S,
				FRID_S, null);
		doExpressionTest("S → S", expected);
	}
	
	public void testUpTo() throws Exception {
		final Expression expected = ff.makeBinaryExpression(UPTO, ZERO,
				ONE, null);
		doExpressionTest("0‥1", expected);
	}
	
	public void testConverse() throws Exception {
		final Expression expected = ff.makeUnaryExpression(CONVERSE, 
				FRID_a, null);
		doExpressionTest("a∼", expected);
	}
	
	public void testKBool() throws Exception {
		final Expression expected = ff.makeBoolExpression(LIT_BTRUE, null);
		doExpressionTest("bool(⊤)", expected);
	}
	
	public void testPartitionEmpty() throws Exception {
		final Predicate expected = ff.makeMultiplePredicate(KPARTITION,
				Arrays.<Expression>asList(FRID_S), null);
		doPredicateTest("partition(S)", expected);
	}
	
	public void testPartitionSingleton() throws Exception {
		final Predicate expected = ff.makeMultiplePredicate(KPARTITION,
				Arrays.<Expression>asList(
						FRID_S,
						ff.makeSetExtension(FRID_a, null)), null);
		doPredicateTest("partition(S, {a})", expected);
	}
	
	public void testPartitionSeveral() throws Exception {
		final Predicate expected = ff.makeMultiplePredicate(KPARTITION,
				Arrays.<Expression>asList(
						FRID_S,	FRID_A, FRID_B), null);
		doPredicateTest("partition(S, A, B)", expected);
	}
	
	public void testFinite() throws Exception {
		final Predicate expected = ff
				.makeSimplePredicate(KFINITE, FRID_S, null);
		doPredicateTest("finite(S)", expected);
	}
	
	public void testPredicateVariable() throws Exception {
		final Predicate expected = PV_P;
		doPredicatePatternTest("$P", expected);
		
	}
	
	public void testPredVarInner() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(LAND, Arrays.<Predicate>asList(
				LIT_BTRUE,
				PV_P), null);
		doPredicatePatternTest("⊤∧$P", expected);
		doPredicatePatternTest("⊤∧($P)", expected);
	}
	
	public void testPredVarRefused() throws Exception {
		assertFailure(ff.parsePredicate("$P", LanguageVersion.V2, null),
				ProblemKind.SyntaxError);
	}
	
	@SuppressWarnings("deprecation")
	public void testIdV1V2() throws Exception {
		final Expression expectedV1 = ff.makeUnaryExpression(KID, FRID_S, null);
		final Expression expectedV2 = ff.makeBinaryExpression(FUNIMAGE,
				ff.makeAtomicExpression(KID_GEN, null),
				FRID_S, null);
		doVersionTest("id(S)", expectedV1, expectedV2);
	}
	
	@SuppressWarnings("deprecation")
	public void testPrj1V1V2() throws Exception {
		final Expression expectedV1 = ff.makeUnaryExpression(KPRJ1, FRID_f, null);
		final Expression expectedV2 = ff.makeBinaryExpression(FUNIMAGE,
				ff.makeAtomicExpression(KPRJ1_GEN, null),
				FRID_f, null);
		doVersionTest("prj1(f)", expectedV1, expectedV2);
	}
	
	@SuppressWarnings("deprecation")
	public void testPrj2V1V2() throws Exception {
		final Expression expectedV1 = ff.makeUnaryExpression(KPRJ2, FRID_f, null);
		final Expression expectedV2 = ff.makeBinaryExpression(FUNIMAGE,
				ff.makeAtomicExpression(KPRJ2_GEN, null),
				FRID_f, null);
		doVersionTest("prj2(f)", expectedV1, expectedV2);
	}
	
	public void testPartitionV1V2() throws Exception {
		final Expression expectedV1 = ff.makeBinaryExpression(FUNIMAGE,
				ff.makeFreeIdentifier("partition", null),
				FRID_S, null);
		final Predicate expectedV2 = ff.makeMultiplePredicate(KPARTITION,
				Arrays.<Expression>asList(FRID_S), null);
		doVersionTest("partition(S)", expectedV1, expectedV2);
	}
	
	public void testUnMinus() throws Exception {
		final Expression detached = ff.makeUnaryExpression(UNMINUS, ONE, null);
		doExpressionTest("− 1", detached);
		
		final Expression attached = ff.makeIntegerLiteral(ONE.getValue().negate(), null);
		doExpressionTest("−1", attached);
	}
	
	public void testBinMinus() throws Exception {
		final Expression expected = ff.makeBinaryExpression(MINUS, ZERO,
				ONE, null);
		doExpressionTest("0−1", expected);
	}
	
	public void testQUnion() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(QUNION,
				asList(BID_x),
				ff.makeRelationalPredicate(GT,
						BI_0,
						ZERO, null),
				ff.makeAssociativeExpression(MUL,
						asList(ONE, BI_0), null),
				null, Form.Explicit);
		doExpressionTest("⋃x·x>0∣1∗x", expected);
	}
	
	public void testQUnionSeveral() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(QUNION,
				asList(BID_x, BID_y),
				ff.makeRelationalPredicate(GT,
						BI_1,
						BI_0, null),
				ff.makeAssociativeExpression(MUL,
						Arrays.<Expression>asList(BI_0, BI_1), null),
				null, Form.Explicit);
		doExpressionTest("⋃x,y·x>y∣y∗x", expected);
	}
	
	public void testQUnionImplicit() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(QUNION,
				asList(BID_x),
				ff.makeRelationalPredicate(GT,
						BI_0,
						ZERO, null),
				ff.makeAssociativeExpression(MUL,
						asList(ONE, BI_0), null),
				null, Form.Implicit);
		doExpressionTest("⋃ 1∗x∣x>0", expected);
	}
	
	public void testRelImage() throws Exception {
		final Expression expected = 
				ff.makeBinaryExpression(RELIMAGE,
						FRID_f,
						FRID_S,
						null);
		doExpressionTest("f[S]", expected);
	}

	// verifies that priority between Maplet and Ovr is not taken into account
	// inside square brackets
	public void testRelImagePriority() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(OVR, Arrays.<Expression>asList(
				FRID_f,
				ff.makeBinaryExpression(RELIMAGE,
						FRID_a,
						ff.makeBinaryExpression(MAPSTO, FRID_b, FRID_c, null),
						null)), null);
		doExpressionTest("fa[b↦c]", expected);		
	}
	
	public void testLedBacktrack() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(Formula.LAND,
				Arrays.<Predicate> asList(
						ff.makeRelationalPredicate(EQUAL, FRID_S, ff
				.makeSetExtension(ZERO, null), null),
				LIT_BTRUE), null);
		doPredicateTest("S={0}∧⊤", expected );
	}
	
	public void testCProdCProdCompatibility() throws Exception {
		final Type expected = ff.makeProductType(
				ff.makeProductType(S_TYPE, S_TYPE),
				S_TYPE);
		doTypeTest("S×S×S", expected);
	}
	
	public void testDoubleBoundIdentifiers() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL, asList(BID_x), ff.makeAssociativePredicate(Formula.LAND,
				Arrays.<Predicate> asList(
						ff.makeRelationalPredicate(IN, BI_0, FRID_A, null),
						ff.makeQuantifiedPredicate(FORALL, asList(BID_x), 
								ff.makeRelationalPredicate(IN, BI_0, FRID_B, null), null)
						), null), null);
		final Predicate actual = doPredicateTest("∀x·x ∈ A ∧ (∀x·x ∈ B)", expected);
		final SourceLocation loc = actual.getSourceLocation();
		assertNotNull(loc);
	}

	// Some sub-parsers are triggered manually, ensure that it does not allow unacceptable formulae
	public void testManualSubParsers() throws Exception {
		// bound identifier name is an operator !
		final IParseResult result = ff.parsePredicate("∀+·⊤", LanguageVersion.V2, null);
		System.out.println(result.getParsedPredicate());
		assertFailure(result, ProblemKind.SyntaxError);
	}
	
	public void testMinusPriority() throws Exception {
		final Expression plusMinus =
			ff.makeBinaryExpression(MINUS,
					ff.makeAssociativeExpression(PLUS, Arrays.<Expression>asList(
							ONE, ONE), null),
							ONE,  null);
		doExpressionTest("1+1−1", plusMinus);
		
		final Expression minusPlus =
			ff.makeAssociativeExpression(PLUS, Arrays.<Expression>asList(
					ff.makeBinaryExpression(MINUS,
							ONE, ONE, null),
					ONE), null);
		doExpressionTest("1−1+1", minusPlus);
		
		final Expression expected = ff.makeAssociativeExpression(PLUS, asList(
						ff.makeUnaryExpression(UNMINUS, ONE, null),
						ONE), null);
		doExpressionTest("− 1+1", expected);
		
	}
	
	private static final IExpressionExtension EMAX = new IExpressionExtension() {
		private static final String SYNTAX_SYMBOL = "emax";
		private static final String OPERATOR_ID = "Extension Maximum";
		
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
			// no priority to add
		}

		public boolean checkPreconditions(Expression[] expressions,
				Predicate[] predicates) {
			return expressions.length >= 2 && predicates.length == 0;
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
			return ExtensionKind.PARENTHESIZED_PREFIX_EXPRESSION;
		}

		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
		}

		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeChildWDConjunction(formula);
		}

		public boolean isFlattenable() {
			return false;
		}

		public void toString(IToStringMediator mediator,
				IExtendedFormula formula) {
			mediator.append(getSyntaxSymbol());
			mediator.append("(");
			final Expression[] childExpressions = formula.getChildExpressions();
			mediator.append(childExpressions[0], false);
			for (int i = 1; i < childExpressions.length; i++) {
				mediator.append(",");
				mediator.append(childExpressions[i], true);
			}
			mediator.append(")");
		}
	};

	// verify that the newly introduced symbol cannot be part of an identifier
	public void testExtensionSymbolEMax() throws Exception {
		final String emax = "emax";
		
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(EMAX));
		
		assertTrue(
				"emax symbol should be a valid part of identifier for default factory",
				ff.isValidIdentifierName(emax));
		assertFalse(
				"emax symbol should not be a valid part of identifier for extended factory",
				extFac.isValidIdentifierName(emax));
		
		final FreeIdentifier expectedDefault = ff.makeFreeIdentifier(emax, null);
		doExpressionTest(emax, expectedDefault, ff);
	
		final IParseResult result = extFac.parseExpression(emax,
				LanguageVersion.V2, null);
		assertFailure(result, ProblemKind.SyntaxError);
	}
	
	public void testEMax() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(EMAX));
		final Expression expected = extFac.makeExtendedExpression(EMAX,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeFreeIdentifier("B", null),
						extFac.makeFreeIdentifier("C", null)),
				Collections.<Predicate> emptySet(), null);
		doExpressionTest("emax(A, B, C)", expected, extFac);
	}
	
	public void testEMaxInvalidNumberOfChildren() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(EMAX));
		final IParseResult result = extFac.parseExpression("emax(a)", LanguageVersion.V2, null);
		assertFailure(result, ProblemKind.SyntaxError);
	}
	
}
