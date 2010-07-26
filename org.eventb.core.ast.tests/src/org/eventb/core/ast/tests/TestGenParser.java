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
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GenericType;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.extension.CycleError;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.IDestructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship;

/**
 * This test class aims at supporting generic parser development. It is not part
 * of the AST project test and is intended to be removed when the development is
 * complete.
 * 
 * @author Nicolas Beauger
 * FIXME remove this class (DO NOT COMMIT TO TRUNK !)
 */
public class TestGenParser extends AbstractTests {

	private static final Predicate[] NO_PRED = new Predicate[0];
	private static final Expression[] NO_EXPR = new Expression[0];
	protected static final BoundIdentifier BI_0 = ff.makeBoundIdentifier(0, null);
	protected static final BoundIdentifier BI_1 = ff.makeBoundIdentifier(1, null);
	protected static final BoundIdentifier BI_2 = ff.makeBoundIdentifier(2, null);
	protected static final BoundIdentifier BI_3 = ff.makeBoundIdentifier(3, null);
	protected static final BoundIdentDecl BID_u = ff.makeBoundIdentDecl("u", null);
	protected static final BoundIdentDecl BID_x = ff.makeBoundIdentDecl("x", null);
	protected static final BoundIdentDecl BID_y = ff.makeBoundIdentDecl("y", null);
	protected static final BoundIdentDecl BID_z = ff.makeBoundIdentDecl("z", null);
	protected static final LiteralPredicate LIT_BFALSE = ff.makeLiteralPredicate(
							Formula.BFALSE, null);
	protected static final LiteralPredicate LIT_BTRUE = ff.makeLiteralPredicate(
							Formula.BTRUE, null);
	protected static final AtomicExpression ATOM_TRUE = ff.makeAtomicExpression(TRUE, null);
	protected static final IntegerLiteral ZERO = ff.makeIntegerLiteral(BigInteger.ZERO, null);
	protected static final IntegerLiteral ONE = ff.makeIntegerLiteral(BigInteger.ONE, null);
	protected static final AtomicExpression EMPTY = ff.makeEmptySet(null, null);
	protected static final FreeIdentifier FRID_S = ff.makeFreeIdentifier("S", null);
	protected static final GivenType S_TYPE = ff.makeGivenType("S");
	protected static final PowerSetType POW_S_TYPE = ff.makePowerSetType(S_TYPE);
	protected static final FreeIdentifier FRID_x = ff.makeFreeIdentifier("x", null);
	protected static final FreeIdentifier FRID_y = ff.makeFreeIdentifier("y", null);
	protected static final FreeIdentifier FRID_a = ff.makeFreeIdentifier("a", null);
	protected static final FreeIdentifier FRID_b = ff.makeFreeIdentifier("b", null);
	protected static final FreeIdentifier FRID_c = ff.makeFreeIdentifier("c", null);
	protected static final FreeIdentifier FRID_A = ff.makeFreeIdentifier("A", null);
	protected static final FreeIdentifier FRID_B = ff.makeFreeIdentifier("B", null);
	protected static final FreeIdentifier FRID_C = ff.makeFreeIdentifier("C", null);
	protected static final FreeIdentifier FRID_f = ff.makeFreeIdentifier("f", null);
	protected static final PredicateVariable PV_P = ff.makePredicateVariable("$P", null);
	protected static final AtomicExpression INT = ff.makeAtomicExpression(Formula.INTEGER, null);
	protected static final UnaryExpression POW_INT = ff.makeUnaryExpression(POW, INT, null);
	protected static final IntegerType INT_TYPE = ff.makeIntegerType();
	protected static final BooleanType BOOL_TYPE = ff.makeBooleanType();
	protected static final PowerSetType POW_INT_TYPE = ff.makePowerSetType(INT_TYPE);
	protected static final PowerSetType REL_INT_INT = ff.makeRelationalType(INT_TYPE, INT_TYPE);
	protected static final SourceLocationChecker slChecker = new SourceLocationChecker();

	private static void assertFailure(IParseResult result, ASTProblem expected) {
		assertTrue("expected parsing to fail", result.hasProblem());
		final List<ASTProblem> problems = result.getProblems();
		System.out.println(problems);
		assertEquals(1, problems.size());
		final ASTProblem actual = problems.get(0);
		assertEquals("wrong problem", expected, actual);
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
	
	private static Expression parseAndCheck(String formula, Expression expected,
			FormulaFactory factory, LanguageVersion version) {
		final Expression actual = parseExpr(formula, factory, version);
		checkParsedFormula(formula, expected, actual);
		
		return actual;
	}

	private static Expression doParseUnparseTest(String formula, Expression expected) {
		final Expression actual = parseExpr(formula);
		checkParsedFormula(formula, expected, actual);
		
		final String actToStr = actual.toString();
		final Expression reparsed = parseExpr(actToStr);
		assertEquals("bad reparsed", expected, reparsed);
	
		return actual;
	}

	private static Expression doParseUnparseTest(String formula, Expression expected, FormulaFactory factory) {
		final Expression actual = parseExpr(formula, factory, LanguageVersion.LATEST);
		checkParsedFormula(formula, expected, actual);
		
		final String actToStr = actual.toString();
		final Expression reparsed = parseExpr(actToStr, factory, LanguageVersion.LATEST);
		assertEquals("bad reparsed", expected, reparsed);
	
		return actual;
	}

	private static Predicate doParseUnparseTest(String formula, Predicate expected) {
		final Predicate actual = parsePred(formula);
		checkParsedFormula(formula, expected, actual);
		
		final String actToStr = actual.toString();
		final Predicate reparsed = parsePred(actToStr);
		assertEquals("bad reparsed, unparser produced " + actToStr + "\n",
				expected, reparsed);
	
		return actual;
	}

	private static Assignment doParseUnparseTest(String formula, Assignment expected) {
		final Assignment actual = doAssignmentTest(formula, expected);
		final String actToStr = actual.toString();
		doAssignmentTest(actToStr, expected);
		return actual;
	}

	private static Expression parseExpr(String formula, FormulaFactory factory,
			LanguageVersion version) {
		final IParseResult result = parseExprRes(formula, factory, version);
		assertFalse("unexpected problem(s): " + result.getProblems(), result
				.hasProblem());
		final Expression actual = result.getParsedExpression();
		return actual;
	}

	private static Predicate parsePred(String formula,
			LanguageVersion version) {
		final IParseResult result = parsePredRes(formula, version);
		assertFalse("unexpected problem(s) for " + formula + ": "
				+ result.getProblems(), result.hasProblem());
		final Predicate actual = result.getParsedPredicate();
		return actual;
	}

	private static Expression parseExpr(String formula) {
		return parseExpr(formula, ff, LanguageVersion.LATEST);
	}

	private static Predicate parsePred(String formula) {
		return parsePred(formula, LanguageVersion.LATEST);
	}

	private static IParseResult parseExprRes(String formula,
			FormulaFactory factory, LanguageVersion version) {
		return factory.parseExpression(formula,
				version, null);
	}
	
	private static IParseResult parseExprRes(String formula) {
		return parseExprRes(formula, ff, LanguageVersion.LATEST);
	}
	
	private static Expression doExpressionTest(String formula, Expression expected, FormulaFactory factory) {
		return parseAndCheck(formula, expected, factory, LanguageVersion.V2);
	}
	
	private static Expression doExpressionTest(String formula, Expression expected, Type expectedType, boolean typeCheck) {
		return doExpressionTest(formula, expected, expectedType, ff, typeCheck);
	}	

	private static Expression doExpressionTest(String formula, Expression expected, Type expectedType, FormulaFactory factory, boolean typeCheck) {
		final Expression actual = doExpressionTest(formula, expected, factory);
		if (typeCheck) {
			final ITypeCheckResult result = actual.typeCheck(factory.makeTypeEnvironment());
			assertFalse(
					"unexpected type check problems " + result.getProblems(),
					result.hasProblem());
		}
		assertEquals(expectedType, actual.getType());
		return actual;
	}
	
	private static Expression doExpressionTest(String formula, Expression expected) {
		return doExpressionTest(formula, expected, ff);
	}
	
	private static Predicate doPredicateTest(String formula, Predicate expected) {
		return doPredicateTest(formula, expected, LanguageVersion.V2);
	}
	
	private static Predicate doPredicateTest(String formula, Predicate expected, LanguageVersion version, FormulaFactory factory) {
		final IParseResult result = parsePredRes(formula, version, factory);
		assertFalse("unexpected problem(s): " + result.getProblems(), result.hasProblem());
		final Predicate actual = result.getParsedPredicate();
		checkParsedFormula(formula, expected, actual);
		return actual;
	}
	
	private static Predicate doPredicateTest(String formula, Predicate expected, FormulaFactory factory) {
		return doPredicateTest(formula, expected, LanguageVersion.LATEST, factory);
	}
	
	private static Predicate doPredicateTest(String formula, Predicate expected, LanguageVersion version) {
		return doPredicateTest(formula, expected, version, ff);
		
//		final String actToStr = actual.toStringWithTypes();
//		final IParseResult resToStr = parsePredRes(actToStr, version);
//		assertFalse(result.hasProblem());
//		final Predicate reparsed = resToStr.getParsedPredicate();
//		checkParsedFormula(actToStr, expected, reparsed);
		
	}

	private static IParseResult parsePredRes(String formula,
			LanguageVersion version, FormulaFactory factory) {
		return factory.parsePredicate(formula, version, null);
	}
	
	private static IParseResult parsePredRes(String formula,
			LanguageVersion version) {
		return parsePredRes(formula, version, ff);
	}
	
	private static IParseResult parsePredRes(String formula) {
		return parsePredRes(formula, LanguageVersion.LATEST);
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

	private static Type doTypeTest(String formula, Type expected) {
		return doTypeTest(formula, expected, ff);
	}
	
	private static Type doTypeTest(String formula, Type expected, FormulaFactory factory) {
		final IParseResult result = factory.parseType(formula,
				LanguageVersion.V2);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse("unexpected problems " + result.getProblems(),
				result.hasProblem());
		final Type actual = result.getParsedType();
		System.out.println(actual);
		assertEquals(expected, actual);
		return actual;
	}
	
	private static Assignment doAssignmentTest(String formula, Assignment expected) {
		final IParseResult result = ff.parseAssignment(formula,
				LanguageVersion.V2, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse("parse failed for " + formula + ", problems: "
				+ result.getProblems(), result.hasProblem());
		final Assignment actual = result.getParsedAssignment();
		System.out.println(actual);
		assertEquals(expected, actual);
	
		actual.accept(slChecker);
		return actual;
	}

	private static void doTest(String formula, Formula<?> expected, LanguageVersion version) {
		if (expected instanceof Expression) {
			parseAndCheck(formula, (Expression) expected,ff, version);
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
		final IParseResult result = parseExprRes("A∩B∪C");
		assertFailure(result, new ASTProblem(new SourceLocation(3, 3),
				ProblemKind.IncompatibleOperators, ProblemSeverities.Error, "∩",
				"∪"));
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
		final Predicate pred = parsePred("(⊤∧⊥)∨⊥");
		assertNotNull(pred.getSourceLocation());
		final Predicate childFalse = ((AssociativePredicate) pred)
				.getChildren()[1];
		assertEquals(new SourceLocation(6, 6), childFalse.getSourceLocation());
	}
	
	public void testSourceLocation2() throws Exception {
		final Predicate pred = parsePred("⊤∧(⊥∨⊥        )");
		assertNotNull(pred.getSourceLocation());
		final Predicate childFalseOrFalse = ((AssociativePredicate) pred)
				.getChildren()[1];
		assertEquals(new SourceLocation(3, 5), childFalseOrFalse.getSourceLocation());
	}
	
	private static final IExpressionExtension DIRECT_PRODUCT = new IExpressionExtension() {

		@Override
		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public String getSyntaxSymbol() {
			return "§";
		}

		@Override
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

		@Override
		public Type getType(ExtendedExpression expression,
				Type proposedType, ITypeMediator mediator) {

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

		@Override
		public String getGroupId() {
			return "My own group";
		}

		@Override
		public String getId() {
			return "direct product extension";
		}

		@Override
		public IExtensionKind getKind() {
			return BINARY_INFIX_EXPRESSION;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
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
		
		@Override
		public Type getType(ExtendedExpression expression,
				Type proposedType, ITypeMediator mediator) {
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

		@Override
		public Type typeCheck(ITypeCheckMediator tcMediator,
				ExtendedExpression expression) {
			final Expression[] children = expression.getChildExpressions();
			final Type resultType = tcMediator.makeIntegerType();
			for (int i = 0; i < children.length; i++) {
				tcMediator.sameType(children[i].getType(), resultType);
			}
			return resultType;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			mediator.addAssociativity(getId());
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			try {
				mediator.addPriority(getId(), "plus");
			} catch (CycleError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public String getGroupId() {
			return "Arithmetic";
		}

		@Override
		public String getId() {
			return OPERATOR_ID;
		}

		@Override
		public IExtensionKind getKind() {
			return ASSOCIATIVE_INFIX_EXPRESSION;
		}

		@Override
		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
		}

		@Override
		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
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
		doParseUnparseTest("A € B € C", expected, extFac);
	}
	
	public void testAssociativeExtensionUnparseL() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(MONEY));
		final Expression expected = extFac.makeExtendedExpression(MONEY,
				Arrays.<Expression> asList(
						extFac.makeExtendedExpression(MONEY,
								Arrays.<Expression> asList(
										extFac.makeFreeIdentifier("A", null),
										extFac.makeFreeIdentifier("B", null)), Collections.<Predicate> emptySet(), null),
										extFac.makeFreeIdentifier("C", null)),
				Collections.<Predicate> emptySet(), null);
		doParseUnparseTest("(A € B) € C", expected, extFac);
	}
	
	public void testAssociativeExtensionUnparseR() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(Collections
				.<IFormulaExtension> singleton(MONEY));
		final Expression expected = extFac.makeExtendedExpression(MONEY,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeExtendedExpression(MONEY,
								Arrays.<Expression> asList(
										extFac.makeFreeIdentifier("B", null),
										extFac.makeFreeIdentifier("C", null)), Collections.<Predicate> emptySet(), null)),
				Collections.<Predicate> emptySet(), null);
		doParseUnparseTest("A € (B € C)", expected, extFac);
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
	
	public void testNudNoLed() throws Exception {
		assertFailure(
				parseExprRes("0 card(x)"),
				new ASTProblem(new SourceLocation(2, 5),
						ProblemKind.MisplacedNudOperator,
						ProblemSeverities.Error, "card"));
	}
	
	public void testLedNoNud() throws Exception {
		assertFailure(
				parseExprRes("x += 2"),
				new ASTProblem(new SourceLocation(3, 3),
						ProblemKind.MisplacedLedOperator,
						ProblemSeverities.Error, "="));
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
		final Type expected = REL_INT_INT;
		doTypeTest("ℙ(ℤ×ℤ)", expected);
	}

	public void testParseTypeGivenType() throws Exception {
		final Type expected = S_TYPE;
		doTypeTest("S", expected);
	}
	
	public void testParseRelationalType() throws Exception {
		final Type expected = REL_INT_INT;
		doTypeTest("ℤ↔ℤ", expected);
	}

	
	public void testEmptySetOfType() throws Exception {
		final Expression expected = ff.makeEmptySet(POW_INT_TYPE, null);
		doExpressionTest("(∅ ⦂ ℙ(ℤ))", expected, POW_INT_TYPE, false);
	}
	
	public void testIdOfType() throws Exception {
		final Expression expected = ff.makeAtomicExpression(KID_GEN, null, ff
				.makeRelationalType(INT_TYPE, INT_TYPE));
		doExpressionTest("(id ⦂ ℙ(ℤ×ℤ))", expected, REL_INT_INT, false);
	}
	
	public void testIdOfTypeRel() throws Exception {
		final Expression expected = ff.makeAtomicExpression(KID_GEN, null, ff
				.makeRelationalType(INT_TYPE, INT_TYPE));
		doExpressionTest("(id ⦂ ℤ↔ℤ)", expected, REL_INT_INT, false);
	}
	
	public void testPrj1OfType() throws Exception {
		final PowerSetType expectedType = ff
				.makeRelationalType(ff.makeProductType(S_TYPE, INT_TYPE),
						S_TYPE);
		final Expression expected = ff.makeAtomicExpression(KPRJ1_GEN, null, expectedType);
		doExpressionTest("(prj1 ⦂ ℙ(S×ℤ×S))", expected, expectedType, false);		
	}
	
	public void testPrj2OfType() throws Exception {
		final PowerSetType expectedType = ff
				.makeRelationalType(ff.makeProductType(INT_TYPE, S_TYPE),
						S_TYPE);
		final Expression expected = ff.makeAtomicExpression(KPRJ2_GEN, null, expectedType);
		doExpressionTest("(prj2 ⦂ ℙ(ℤ×S×S))", expected, expectedType, false);		
	}
	
	public void testIdentOfType() throws Exception {
		final IParseResult result = parseExprRes("(x ⦂ ℙ(ℤ))");
		assertFailure(result, new ASTProblem(new SourceLocation(3, 3),
				ProblemKind.UnexpectedOftype, ProblemSeverities.Error));
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
	
	public void testCSetImplicitOftype() throws Exception {
		final ASTProblem identExpected = new ASTProblem(new SourceLocation(1, 1),
				ProblemKind.UnexpectedSymbol, ProblemSeverities.Error, "an identifier", "(");
		
		final ASTProblem errorOftype = new ASTProblem(new SourceLocation(2, 2),
				ProblemKind.UnexpectedOftype, ProblemSeverities.Error);

		final ASTProblem dotExpected = new ASTProblem(new SourceLocation(4, 4),
				ProblemKind.UnexpectedSymbol, ProblemSeverities.Error, "·", "∣");

		final ASTProblem identOrOftype = new ASTProblem(
				new SourceLocation(0, 0), ProblemKind.VariousPossibleErrors,
				ProblemSeverities.Error,
				ProblemKind.makeCompoundMessage(asList(identExpected,
						errorOftype)));
		
		final ASTProblem dotOrOftype = new ASTProblem(
				new SourceLocation(0, 0), ProblemKind.VariousPossibleErrors,
				ProblemSeverities.Error,
				ProblemKind.makeCompoundMessage(asList(dotExpected,
						errorOftype)));
		assertFailure(parseExprRes("{(x⦂ℤ)∣ ⊤}"), identOrOftype);
		assertFailure(parseExprRes("{x⦂ℤ∣ ⊤}"), dotOrOftype);
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
	
	public void testForallCSetExplicitBoundTwice() throws Exception {
		final Predicate expected = 
			ff.makeQuantifiedPredicate(FORALL,
					new BoundIdentDecl[] { BID_x },
					ff.makeSimplePredicate(KFINITE,
							ff.makeAssociativeExpression(BUNION,
									asList(BI_0,
											ff.makeQuantifiedExpression(CSET,
													asList(BID_x),
													LIT_BTRUE, BI_0, null,
													Form.Explicit)), null), null), null);
		doPredicateTest("∀x·finite(x ∪ {x · ⊤ ∣ x})", expected);		
	}
	
	public void testForallCSetImplicitBoundTwice() throws Exception {
		final Predicate expected =
			ff.makeQuantifiedPredicate(FORALL,
					new BoundIdentDecl[] { BID_x },
					ff.makeSimplePredicate(KFINITE,
							ff.makeAssociativeExpression(BUNION,
									asList(BI_0,			
											ff.makeQuantifiedExpression(CSET,
													asList(BID_x),
													LIT_BTRUE, BI_0, null,
													Form.Implicit)), null), null), null);
		doPredicateTest("∀x·finite(x ∪ {x∣ ⊤})", expected);
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
		final IParseResult result = parseExprRes("λx↦(y↦x)·x>y∣ x+y");
		assertFailure(result, new ASTProblem(new SourceLocation(6, 6),
				ProblemKind.DuplicateIdentifierInPattern, ProblemSeverities.Error, "x"));
	}

	public void testForallLambdaBoundTwice() throws Exception {
		final Predicate expected =
			ff.makeQuantifiedPredicate(FORALL,
				new BoundIdentDecl[] { BID_x },
				ff.makeSimplePredicate(KFINITE,
						ff.makeAssociativeExpression(BUNION,
								asList(BI_0,			
										ff.makeQuantifiedExpression(CSET,
												asList(BID_x, BID_y),
												ff.makeRelationalPredicate(GT, BI_1, BI_0, null),
												ff.makeBinaryExpression(MAPSTO,
														ff.makeBinaryExpression(MAPSTO,	BI_1, BI_0, null),
														ff.makeAssociativeExpression(PLUS, 
																Arrays.<Expression> asList(BI_1, BI_0), null),
																null),
																null, Form.Lambda)), null), null), null);
		doPredicateTest("∀x·finite(x ∪ (λx↦y·x>y∣ x+y))",expected);
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
		final ASTProblem becmoError = new ASTProblem(new SourceLocation(1, 1),
				ProblemKind.BECMOAppliesToOneIdent, ProblemSeverities.Error);
		assertFailure(ff.parseAssignment("a,b :∈ S", LanguageVersion.V2, null),
				becmoError);
		assertFailure(ff.parseAssignment("a,b :∈ S,S", LanguageVersion.V2, null),
				becmoError);
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
		doParseUnparseTest("a,b :∣  a'=b ∧ b'=a  ", expected);
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
		assertFailure(parsePredRes("$P"),
				new ASTProblem(new SourceLocation(0, 1),
						ProblemKind.PredicateVariableNotAllowed, ProblemSeverities.Error, "$P"));
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
		final IParseResult result = parsePredRes("∀+·⊤");
		System.out.println(result.getParsedPredicate());
		assertFailure(result, new ASTProblem(new SourceLocation(1, 1),
				ProblemKind.UnexpectedSymbol, ProblemSeverities.Error, "an identifier", "+"));
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
		
		@Override
		public Type getType(ExtendedExpression expression,
				Type proposedType, ITypeMediator mediator) {
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

		@Override
		public Type typeCheck(ITypeCheckMediator tcMediator,
				ExtendedExpression expression) {
			final Expression[] children = expression.getChildExpressions();
			final Type resultType = tcMediator.makeIntegerType();
			for (int i = 0; i < children.length; i++) {
				tcMediator.sameType(children[i].getType(), resultType);
			}
			return resultType;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			mediator.addCompatibility(getId(), getId());
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority to add
		}

		@Override
		public String getGroupId() {
			return "Arithmetic";
		}

		@Override
		public String getId() {
			return OPERATOR_ID;
		}

		@Override
		public IExtensionKind getKind() {
			return new IFormulaExtension.PrefixKind(EXPRESSION, 3, EXPRESSION);
		}

		@Override
		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
		}

		@Override
		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
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
	
		final IParseResult result = parseExprRes(emax, extFac,
				LanguageVersion.LATEST);
		assertFailure(result, new ASTProblem(new SourceLocation(3, 3),
				ProblemKind.UnexpectedSymbol, ProblemSeverities.Error, "(", "End Of Formula"));
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
		final IParseResult result = parseExprRes("emax(a)", extFac,
				LanguageVersion.LATEST);
		assertFailure(result, new ASTProblem(new SourceLocation(0, 6),
				ProblemKind.ExtensionPreconditionError, ProblemSeverities.Error));
	}
	
	public void testFunImageConverse() throws Exception {
		final Expression expected = ff.makeBinaryExpression(FUNIMAGE,
				ff.makeUnaryExpression(CONVERSE, FRID_f, null), FRID_a, null);
		doExpressionTest("f∼(a)", expected);
	}

	public void testConverseFunImage() throws Exception {
		final Expression expected = ff.makeUnaryExpression(CONVERSE,
				ff.makeBinaryExpression(FUNIMAGE, FRID_f, FRID_a, null), null);
		doExpressionTest("(f(a))∼", expected); // parentheses are mandatory for non generic parser
		doExpressionTest("f(a)∼", expected); // parentheses are not required for generic parser
	}

	public void testConverseRelImage() throws Exception {
		final Expression expected = ff.makeUnaryExpression(CONVERSE,
				ff.makeBinaryExpression(RELIMAGE, FRID_f, FRID_a, null), null);
		doExpressionTest("(f[a])∼", expected); // parentheses are mandatory for non generic parser
		doExpressionTest("f[a]∼", expected); // parentheses are not required for generic parser
	}
	
	public void testMapstoConverseRelImage() throws Exception {
		final Expression expected = ff.makeBinaryExpression(MAPSTO,
				ONE,
				ff.makeUnaryExpression(CONVERSE,
						ff.makeBinaryExpression(RELIMAGE, FRID_f, FRID_a, null),
						null), null);
		doExpressionTest("1↦(f[a])∼", expected);
	}
	
	public void testMapstoConverseFunImage() throws Exception {
		final Expression expected = ff.makeBinaryExpression(MAPSTO,
				ONE,
				ff.makeUnaryExpression(CONVERSE,
						ff.makeBinaryExpression(FUNIMAGE, FRID_f, FRID_a, null), null), null);
		doExpressionTest("1↦(f(a))∼", expected);
		
	}
	
	public void testConverseMapsto() throws Exception {
		final Expression expected = 
			ff.makeUnaryExpression(CONVERSE,
					ff.makeBinaryExpression(MAPSTO,
							ONE,
							ff.makeBinaryExpression(FUNIMAGE, FRID_f, FRID_a, null), null), null);
		doExpressionTest("(1↦f(a))∼", expected);
	}
	
	public void testFunImageConverseMapsto() throws Exception {
		final Expression expected = ff.makeBinaryExpression(FUNIMAGE,
				ff.makeUnaryExpression(CONVERSE,
						ff.makeBinaryExpression(MAPSTO,
								ONE, ZERO, null), null),
				ZERO, null);
		doExpressionTest("(1↦0)∼(0)", expected);
	}
	
	public void testGroupCompatibility() throws Exception {
		final Expression expected = ff.makeBinaryExpression(SETMINUS, FRID_A,
				ff.makeBinaryExpression(DIV, FRID_B, FRID_c, null), null);
		doExpressionTest("A ∖ B ÷ c", expected);
	}
	
	public void testIncompatibleEXPN() throws Exception {
		final ASTProblem expected = new ASTProblem(new SourceLocation(3, 3),
				ProblemKind.IncompatibleOperators, ProblemSeverities.Error,
				"^", "^");
		final IParseResult result = parseExprRes("a^b^c");
		assertFailure(result, expected);
		
	}

	public void testUnexpectedSubFormula() throws Exception {
		final IParseResult result = parseExprRes("a − b ⇒ c");
		final ASTProblem expected = new ASTProblem(new SourceLocation(0, 4),
				ProblemKind.UnexpectedSubFormulaKind, ProblemSeverities.Error,
				"a predicate", "an expression");
		assertFailure(result, expected);
	}
	
	public void testEmptyFormula() throws Exception {
		final IParseResult result = parseExprRes("");
		final ASTProblem expected = new ASTProblem(new SourceLocation(0, 0),
				ProblemKind.PrematureEOF, ProblemSeverities.Error);
		assertFailure(result, expected);
	}
	
	public void testPrematureEOF() throws Exception {
		final IParseResult result = parseExprRes("1+");
		final ASTProblem expected = new ASTProblem(new SourceLocation(1, 1),
				ProblemKind.PrematureEOF, ProblemSeverities.Error);
		assertFailure(result, expected);
	}
	
	public void testUnmatchedTokens() throws Exception {
		final IParseResult result = parseExprRes("1+2 abc");
		final ASTProblem expected = new ASTProblem(new SourceLocation(4, 6),
				ProblemKind.UnmatchedTokens, ProblemSeverities.Error);
		assertFailure(result, expected);
	}
	
	public void testPrimedIdent() throws Exception {
		doExpressionTest("x'", ff.makeFreeIdentifier("x'", null));
		
		// start of partition, pred, prj1, prj2
		doExpressionTest("p", ff.makeFreeIdentifier("p", null));
		doExpressionTest("prj'", ff.makeFreeIdentifier("prj'", null));
		doExpressionTest("p'", ff.makeFreeIdentifier("p'", null));
		doExpressionTest("pp'", ff.makeFreeIdentifier("pp'", null));
		doExpressionTest("pa'", ff.makeFreeIdentifier("pa'", null));
		doExpressionTest("p'−1", ff.makeBinaryExpression(MINUS, ff.makeFreeIdentifier("p'", null), ONE, null));
		
		// start of mod, min, max
		doExpressionTest("m", ff.makeFreeIdentifier("m", null));
		doExpressionTest("m'", ff.makeFreeIdentifier("m'", null));
		doExpressionTest("ma'", ff.makeFreeIdentifier("ma'", null));
		
	}
	
	public void testCloseParenMatch() throws Exception {
		final IParseResult result = parseExprRes("(a}");
		final ASTProblem expected = new ASTProblem(new SourceLocation(2, 2),
				ProblemKind.UnexpectedSymbol, ProblemSeverities.Error, ")", "}");
		assertFailure(result, expected);
	}

	private static final IDatatypeExtension LIST_TYPE = new IDatatypeExtension() {

		private static final String TYPE_NAME = "List";
		private static final String TYPE_IDENTIFIER = "List Id";
		private static final String GROUP_IDENTIFIER = "List Group";
		
		
		@Override
		public String getTypeName() {
			return TYPE_NAME;
		}

		@Override
		public String getId() {
			return TYPE_IDENTIFIER;
		}
		
		@Override
		public String getGroupId() {
			return GROUP_IDENTIFIER;
		}

		@Override
		public void addTypeParameters(ITypeConstructorMediator mediator) {
			mediator.addTypeParam("S");			
		}

		@Override
		public void addConstructors(IConstructorMediator mediator) {
			mediator.addConstructor("nil", "NIL");
			final ITypeParameter typeS = mediator.getTypeParameter("S");
			
			final IArgumentType refS = mediator.newArgumentType(typeS);
			final IArgumentType listS = mediator.newArgumentTypeConstr(Collections.singletonList(refS));
			mediator.addConstructor("cons", "CONS", Arrays.asList(refS, listS));
		}

		@Override
		public void addDestructors(IDestructorMediator mediator) {
			final ITypeParameter typeS = mediator.getTypeParameter("S");
			final IArgumentType refS = mediator.newArgumentType(typeS);
			mediator.addDestructor("head", "HEAD", refS);
			final IArgumentType listS = mediator.newArgumentTypeConstr(Collections.singletonList(refS));
			mediator.addDestructor("tail", "TAIL", listS);
		}

	};

	private static final Map<String, IExpressionExtension> LIST_EXTNS = FormulaFactory
			.getExtensions(LIST_TYPE);
	private static final FormulaFactory LIST_FAC = FormulaFactory
			.getInstance(new HashSet<IFormulaExtension>(LIST_EXTNS.values()));	
	private static final IExpressionExtension EXT_LIST = LIST_EXTNS.get(LIST_TYPE.getId());
	private static final GenericType LIST_INT_TYPE = LIST_FAC.makeGenericType(
			Collections.<Type> singletonList(INT_TYPE), EXT_LIST);
	private static final IExpressionExtension EXT_NIL = LIST_EXTNS.get("NIL");
	private static final IExpressionExtension EXT_CONS = LIST_EXTNS.get("CONS");
	private static final IExpressionExtension extHead = LIST_EXTNS.get("HEAD");
	private static final IExpressionExtension extTail = LIST_EXTNS.get("TAIL");
	
	public void testDatatypeType() throws Exception {
		
		final ExtendedExpression list = LIST_FAC.makeExtendedExpression(EXT_LIST,
				Collections.<Expression> singleton(INT), Collections
						.<Predicate> emptyList(), null);

		final Expression expr = doExpressionTest("List(ℤ)", list, LIST_FAC);
		
		final PowerSetType powListIntType = LIST_FAC.makePowerSetType(LIST_INT_TYPE);
		assertEquals("unexpected type", powListIntType, expr.getType());
		
		assertTrue("expected a type expression", expr.isATypeExpression());
		assertEquals("unexpected toType", LIST_INT_TYPE, expr.toType());

		doTypeTest("List(ℤ)", LIST_INT_TYPE, LIST_FAC);
		
		final GenericType listBoolType = LIST_FAC.makeGenericType(
				Collections.<Type> singletonList(BOOL_TYPE), EXT_LIST);
		assertFalse(listBoolType.equals(LIST_INT_TYPE));
	}

	public void testDatatypeNil() throws Exception {
		assertNotNull("nil constructor not found", EXT_NIL);
		
		final ExtendedExpression nil = LIST_FAC.makeExtendedExpression(EXT_NIL,
				Collections.<Expression> emptyList(), Collections
						.<Predicate> emptyList(), null);

		doExpressionTest("nil", nil, LIST_FAC);
		
		final ExtendedExpression nilInt = LIST_FAC.makeExtendedExpression(EXT_NIL,
				NO_EXPR, NO_PRED, null, LIST_INT_TYPE);

		doExpressionTest("(nil ⦂ List(ℤ))", nilInt, LIST_FAC);

		final GenericType listBoolBoolType = LIST_FAC.makeGenericType(Collections
				.<Type> singletonList(LIST_FAC.makeProductType(BOOL_TYPE,
						BOOL_TYPE)), EXT_LIST);
		final ExtendedExpression nilBoolBool = LIST_FAC.makeExtendedExpression(
				EXT_NIL, NO_EXPR, NO_PRED, null, listBoolBoolType);

		doExpressionTest("(nil ⦂ List(BOOL×BOOL))", nilBoolBool, LIST_FAC);
		
		assertFalse(nil.equals(nilInt));
		assertFalse(nil.equals(nilBoolBool));
		assertFalse(nilBoolBool.equals(nilInt));
	}
	
	public void testDatatypeConstructor() throws Exception {

		final ExtendedExpression nil = LIST_FAC.makeExtendedExpression(EXT_NIL,
				NO_EXPR, NO_PRED, null, LIST_INT_TYPE);

		final ExtendedExpression list1 = LIST_FAC.makeExtendedExpression(EXT_CONS,
				Arrays.asList(ONE, nil), Collections
						.<Predicate> emptyList(), null);
		
		doExpressionTest("cons(1, nil)", list1, LIST_INT_TYPE, LIST_FAC, true);

		final ExtendedExpression list01 = LIST_FAC.makeExtendedExpression(
				EXT_CONS, Arrays.asList(ZERO,
						LIST_FAC.makeExtendedExpression(
								EXT_CONS, Arrays.asList(ONE,
										nil),
										Collections.<Predicate> emptyList(), null)),
										Collections.<Predicate> emptyList(), null);

		doExpressionTest("cons(0, cons(1, nil))", list01, LIST_INT_TYPE, LIST_FAC, true);
		
		assertFalse(list1.equals(list01));
	}
	
	public void testDatatypeDestructors() throws Exception {
		assertNotNull("head destructor not found", extHead);

		assertNotNull("tail destructor not found", extTail);
		
		final ExtendedExpression head = LIST_FAC.makeExtendedExpression(
				extHead, Arrays.<Expression> asList(FRID_x),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("head(x)", head, LIST_FAC);

		final ExtendedExpression tail = LIST_FAC.makeExtendedExpression(
				extTail, Arrays.<Expression> asList(FRID_x),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("tail(x)", tail, LIST_FAC);
	}
	
	
	public void testDatatypeDestructorsTyping() throws Exception {
		
		final ExtendedExpression nil = LIST_FAC.makeExtendedExpression(EXT_NIL,
				Collections.<Expression> emptyList(), Collections
						.<Predicate> emptyList(), null);

		final ExtendedExpression list1 = LIST_FAC.makeExtendedExpression(EXT_CONS,
				Arrays.asList(ONE, nil), Collections
						.<Predicate> emptyList(), null);

		final ExtendedExpression headList1 = LIST_FAC.makeExtendedExpression(
				extHead, Arrays.<Expression> asList(list1),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("head(cons(1, nil))", headList1, INT_TYPE, LIST_FAC, true);

		final ExtendedExpression tail = LIST_FAC.makeExtendedExpression(
				extTail, Arrays.<Expression> asList(list1),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("tail(cons(1, nil))", tail, LIST_INT_TYPE, LIST_FAC, true);
	}
	
	public void testMinusPU() throws Exception {
		final Expression expected = ff.makeBinaryExpression(MINUS, ONE, ZERO, null);
		doParseUnparseTest("1−0", expected);
	}
		
	public void testToStringAndExistsL() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(Formula.LAND,
				Arrays.<Predicate> asList(
						ff.makeQuantifiedPredicate(EXISTS, asList(BID_x), LIT_BFALSE, null),
				LIT_BTRUE), null);
		doParseUnparseTest("(∃x·⊥)∧⊤", expected);

	}
	
	public void testToStringAndExistsR() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(Formula.LAND,
				Arrays.<Predicate> asList(
						LIT_BTRUE,
						ff.makeQuantifiedPredicate(EXISTS, asList(BID_x), LIT_BFALSE, null)
				), null);
		doParseUnparseTest("⊤∧(∃x·⊥)", expected);
	}
	
	public void testToStringAndExistsNoPar() throws Exception {
		final IParseResult result = parsePredRes("⊤∧∃x·⊥");
		assertFailure(result, new ASTProblem(new SourceLocation(0, 5),
				ProblemKind.IncompatibleOperators, ProblemSeverities.Error,
				"∧", "∃"));
	}
	
	public void testToStringMaplet() throws Exception {
		final Expression expected = ff.makeBinaryExpression(MAPSTO, ff
				.makeBinaryExpression(MAPSTO, FRID_A, FRID_B, null), FRID_C,
				null);
		doParseUnparseTest("A↦B↦C", expected);
	}
	
	public void testToStringCProd() throws Exception {
		final Expression expected = ff.makeBinaryExpression(CPROD, ff
				.makeBinaryExpression(CPROD, FRID_A, FRID_B, null), FRID_C,
				null);
		doParseUnparseTest("A×B×C", expected);
	}
	
	public void testToStringInterSetMinusNoPar() throws Exception {
		final Expression expected =
			ff.makeBinaryExpression(SETMINUS,
					ff.makeAssociativeExpression(BINTER,
							Arrays.<Expression>asList(
							FRID_A,
							FRID_B), null),
						FRID_C, null);
		doParseUnparseTest("A∩B∖C", expected);
	}
	
	public void testToStringInterSetMinusWithParL() throws Exception {
		final Expression expected =
			ff.makeBinaryExpression(SETMINUS,
					ff.makeAssociativeExpression(BINTER,
							Arrays.<Expression>asList(
							FRID_A,
							FRID_B), null),
						FRID_C, null);
		doParseUnparseTest("(A∩B)∖C", expected);
	}
	
	public void testToStringInterSetMinusWithParR() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BINTER,
				asList(FRID_A, ff.makeBinaryExpression(SETMINUS, FRID_B,
						FRID_C, null)), null);
		doParseUnparseTest("A∩(B∖C)", expected);
	}
	
	public void testToStringSetMinusInterNoPar() throws Exception {
		final IParseResult result = parseExprRes("A∖B∩C");
		assertFailure(result, new ASTProblem(new SourceLocation(3, 3),
				ProblemKind.IncompatibleOperators, ProblemSeverities.Error,
				"∖", "∩"));
	}
	
	public void testToStringSetMinusInterWithParL() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BINTER,
				asList(ff.makeBinaryExpression(SETMINUS, FRID_A, FRID_B, null),
						FRID_C), null);
		doParseUnparseTest("(A∖B)∩C", expected);
	}
	
	public void testToStringSetMinusInterWithParR() throws Exception {
		final Expression expected = 
			ff.makeBinaryExpression(SETMINUS,
					FRID_A,
			ff.makeAssociativeExpression(BINTER, Arrays.<Expression>asList(
								FRID_B,
								FRID_C),
								null),
						null);
		doParseUnparseTest("A∖(B∩C)", expected);
	}
	
	public void testToStringPlusPlusL() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(PLUS,
				asList(
						ff.makeAssociativeExpression(PLUS,
								Arrays.<Expression>asList(FRID_A, FRID_B), null),
						FRID_C),
						null);
		doParseUnparseTest("(A+B)+C", expected);
	}
	
	public void testToStringPlusPlusR() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(PLUS,
				asList(FRID_A,
						ff.makeAssociativeExpression(PLUS,
								Arrays.<Expression>asList(FRID_B, FRID_C), null)),
						null);
		doParseUnparseTest("A+(B+C)", expected);
	}
	
	public void testToStringDivMinusL() throws Exception {
		final Expression expected = ff.makeBinaryExpression(MINUS,
				ff.makeBinaryExpression(DIV, 
						FRID_A, 
						FRID_B, null),
						FRID_C
						, null);
		doParseUnparseTest("(A÷B)−C", expected);
	}
	
	public void testToStringDivMinusR() throws Exception {
		final Expression expected = ff.makeBinaryExpression(DIV,
				FRID_A, 
				ff.makeBinaryExpression(MINUS, 
						FRID_B,
						FRID_C,	null), null);
		doParseUnparseTest("A÷(B−C)", expected);
	}
	
	public void testToStringMinusDivL() throws Exception {
		final Expression expected = ff.makeBinaryExpression(DIV,
				ff.makeBinaryExpression(MINUS, 
						FRID_A, 
						FRID_B,	null),
				FRID_C,	null);
		doParseUnparseTest("(A−B)÷C", expected);
	}
	
	public void testToStringMinusDivR() throws Exception {
		final Expression expected = ff.makeBinaryExpression(MINUS,
				FRID_A, 
				ff.makeBinaryExpression(DIV, 
						FRID_B,
						FRID_C,
						null), null);
		doParseUnparseTest("A−(B÷C)", expected);
	}

	public void testInterSetMinusCompatibility() throws Exception {
		
		final AbstractGrammar grammar = ff.getGrammar();
		final int setMinusKind = grammar.getKind("∖");
		final int interKind = grammar.getKind("∩");
		final OperatorRelationship relInterMinus = grammar.getOperatorRelationship(interKind, setMinusKind);
		assertEquals(OperatorRelationship.COMPATIBLE, relInterMinus);
		final OperatorRelationship relMinusInter = grammar.getOperatorRelationship(setMinusKind, interKind);
		assertEquals(OperatorRelationship.INCOMPATIBLE, relMinusInter);
	}
	
	public void testNotNot() throws Exception {
		final Predicate expected = ff.makeUnaryPredicate(NOT,
				ff.makeUnaryPredicate(NOT, LIT_BFALSE, null), null);
		doPredicateTest("\u00ac\u00ac\u22a5", expected);
	}
	
	public void testMinusConverse() throws Exception {
		final Expression expected = ff.makeUnaryExpression(CONVERSE,
				ff.makeIntegerLiteral(BigInteger.ONE.negate(), null), null);
		doParseUnparseTest("(−1)∼", expected);
	}
	
	public void testUnionSetExt() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BUNION,
				Arrays.<Expression> asList(
						ff.makeSetExtension(FRID_a, null),
						ff.makeSetExtension(Arrays.<Expression>asList(FRID_b, FRID_c), null)
						), null);

		doExpressionTest("{a} ∪ {b,c}", expected);
	}
	
	public void testBecMoSetExt() throws Exception {
		final BecomesMemberOf expected = ff.makeBecomesMemberOf(FRID_a, ff.makeSetExtension(Arrays.<Expression>asList(FRID_b, FRID_c), null), null);
		doAssignmentTest("a :∈ {b,c}", expected);
	}
	
	public void testBecEqSetExt() throws Exception {
		final BecomesEqualTo expected = ff.makeBecomesEqualTo(FRID_a, ff.makeSetExtension(Arrays.<Expression>asList(FRID_b, FRID_c), null), null);
		doAssignmentTest("a ≔ {b,c}", expected);
	}
	
	public void testBoundIdentRenamingPred() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL,
				asList(BID_x),
				ff.makeRelationalPredicate(EQUAL,
						FRID_x,
						BI_0, null), null);
		final String predStr = expected.toString();
		doPredicateTest(predStr, expected);
	}
	
	public void testBoundIdentRenamingExprExplicit() throws Exception {
		final QuantifiedExpression expected = ff.makeQuantifiedExpression(QUNION,
				asList(BID_x, BID_y),
				ff.makeRelationalPredicate(EQUAL,
						FRID_x,
						ZERO, null),
				ff.makeAssociativeExpression(MUL,
						Arrays.<Expression>asList(FRID_y, ONE), null),
				null, Form.Explicit);
		final String predStr = expected.toString();
		assertEquals("bad renaming", "⋃x0,y0·x=0 ∣ y∗1", predStr);
		doExpressionTest(predStr, expected);
	}
	
	public void testBoundIdentRenamingExprImplicit() throws Exception {
		final QuantifiedExpression expected = ff.makeQuantifiedExpression(QUNION,
				asList(BID_x, BID_y),
				ff.makeRelationalPredicate(LT,
						BI_1,
						BI_0, null),
				ff.makeAssociativeExpression(MUL,
						Arrays.<Expression>asList(BI_1, BI_0), null),
				null, Form.Implicit);
		final String implStr = expected.toString();
		assertEquals("bad toString", "⋃x∗y ∣ x<y", implStr);
		doExpressionTest(implStr, expected);
		
		final Expression exprFreeIdents = expected.rewrite(new DefaultRewriter(false, ff) {
			@Override
			public Expression rewrite(AssociativeExpression expression) {
				// rewrite x*y  with free x
				return ff.makeAssociativeExpression(MUL,
						Arrays.<Expression>asList(FRID_x, BI_0), null);
			}
			@Override
			public Predicate rewrite(RelationalPredicate predicate) {
				// rewrite x<y with free y
				return ff.makeRelationalPredicate(LT,
						BI_1,
						FRID_y, null);
			}
		});
		
		final String exprStr = exprFreeIdents.toString();
		// explicit form because of the presence of free identifiers
		assertEquals("bad renaming", "⋃x0,y0·x0<y ∣ x∗y0", exprStr);
		doExpressionTest(exprStr, exprFreeIdents);
	}
	
	public void testBoundIdentRenamingExprLambda() throws Exception {
		final Expression expected = ff.makeQuantifiedExpression(CSET,
				asList(BID_x, BID_y),
				ff.makeRelationalPredicate(GT, BI_1, FRID_y, null),
				ff.makeBinaryExpression(MAPSTO,
						ff.makeBinaryExpression(MAPSTO,	BI_1, BI_0, null),
						ff.makeAssociativeExpression(PLUS, 
								Arrays.<Expression> asList(FRID_x, BI_0), null),
						null),
				null, Form.Lambda);
		final String implStr = expected.toString();
		assertEquals("bad toString", "λx0 ↦ y0·x0>y ∣ x+y0", implStr);
		doExpressionTest(implStr, expected);
	}
	
	public void testTypedBoundDecl() throws Exception {
		final Predicate expected = ff.makeQuantifiedPredicate(FORALL,
				asList(ff.makeBoundIdentDecl("x", null, INT_TYPE)),
				LIT_BTRUE, null);
		final String predStr = expected.toStringWithTypes();
		assertEquals("∀x⦂ℤ·⊤", predStr);
	}
	
	private static final IPredicateExtension EXT_PRIME = new IPredicateExtension() {
		private static final String SYMBOL = "prime";
		private static final String ID = "Ext Prime";
		
		@Override
		public Predicate getWDPredicate(IWDMediator wdMediator,
				IExtendedFormula formula) {
			return wdMediator.makeTrueWD();
		}
		
		@Override
		public String getSyntaxSymbol() {
			return SYMBOL;
		}
		
		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_UNARY_PREDICATE;
		}
		
		@Override
		public String getId() {
			return ID;
		}
		
		@Override
		public String getGroupId() {
			return BMath.ATOMIC_PRED;
		}
		
		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}
		
		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility			
		}
		
		@Override
		public void typeCheck(ITypeCheckMediator tcMediator,
				ExtendedPredicate predicate) {
			final Expression child = predicate.getChildExpressions()[0];
			final Type childType = tcMediator.makePowerSetType(tcMediator.makeIntegerType());
			tcMediator.sameType(child.getType(), childType);
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}
	};

	private static final FormulaFactory PRIME_FAC = FormulaFactory
			.getInstance(Collections.<IFormulaExtension> singleton(EXT_PRIME));

	public void testPredicateExtension() throws Exception {
		final ExtendedPredicate expected = PRIME_FAC.makeExtendedPredicate(
				EXT_PRIME, Arrays.<Expression> asList(ONE),
				Collections.<Predicate> emptySet(), null);
		doPredicateTest("prime(1)", expected, PRIME_FAC);
	}
}
