/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
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
import static org.eventb.core.ast.AssociativeExpression.BCOMP_ID;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DIV;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.GT;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KID;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KPRJ1;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.NOTEQUAL;
import static org.eventb.core.ast.Formula.OVR;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.POW;
import static org.eventb.core.ast.Formula.QUNION;
import static org.eventb.core.ast.Formula.RELIMAGE;
import static org.eventb.core.ast.Formula.SETMINUS;
import static org.eventb.core.ast.Formula.TFUN;
import static org.eventb.core.ast.Formula.TRUE;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.core.ast.Formula.UPTO;
import static org.eventb.core.ast.LanguageVersion.LATEST;
import static org.eventb.core.ast.ProblemKind.InvalidGenericType;
import static org.eventb.core.ast.ProblemKind.InvalidTypeExpression;
import static org.eventb.core.ast.ProblemKind.PrematureEOF;
import static org.eventb.core.ast.ProblemKind.UnknownOperator;
import static org.eventb.core.ast.ProblemKind.UnmatchedTokens;
import static org.eventb.core.ast.ProblemSeverities.Error;
import static org.eventb.core.ast.extension.ExtensionFactory.NO_CHILD;
import static org.eventb.core.ast.extension.ExtensionFactory.makeAllExpr;
import static org.eventb.core.ast.extension.ExtensionFactory.makeFixedArity;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;
import static org.eventb.core.ast.tests.ExtendedFormulas.EFF;
import static org.eventb.core.ast.tests.ExtendedFormulas.barS;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.internal.core.parser.BMath.StandardGroup.ARITHMETIC;
import static org.eventb.internal.core.parser.BMath.StandardGroup.ATOMIC_PRED;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
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
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.operators.OperatorRelationship;

/**
 * This test class aims at supporting generic parser development. It is not part
 * of the AST project test and is intended to be removed when the development is
 * complete.
 * 
 * @author Nicolas Beauger
 * TODO merge these tests with other tests (remove duplicates, add original)
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
	protected static final AtomicExpression BOOL = ff.makeAtomicExpression(Formula.BOOL, null);
	protected static final UnaryExpression POW_INT = ff.makeUnaryExpression(POW, INT, null);
	protected static final BooleanType BOOL_TYPE = ff.makeBooleanType();
	protected static final PowerSetType POW_INT_TYPE = ff.makePowerSetType(INT_TYPE);
	protected static final PowerSetType REL_INT_INT = ff.makeRelationalType(INT_TYPE, INT_TYPE);
	protected static final SourceLocationChecker slChecker = new SourceLocationChecker();

	private static void assertFailure(IParseResult result, ASTProblem... expected) {
		assertTrue("expected parsing to fail", result.hasProblem());
		final List<ASTProblem> problems = result.getProblems();
		assertEquals("wrong problem", asList(expected), problems);
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
		assertFalse(result.hasProblem());
		final Predicate actual = result.getParsedPredicate();
		checkParsedFormula(formula, expected, actual);
	}

	private static IParseResult parseTypeRes(String image, FormulaFactory factory) {
		return factory.parseType(image, LATEST);
	}

	private static Type doTypeTest(String formula, Type expected) {
		return doTypeTest(formula, expected, ff);
	}
	
	private static Type doTypeTest(String formula, Type expected, FormulaFactory factory) {
		final IParseResult result = parseTypeRes(formula, factory);
		assertFalse("unexpected problems " + result.getProblems(),
				result.hasProblem());
		final Type actual = result.getParsedType();
		assertEquals(expected, actual);
		return actual;
	}
	
	private static Assignment doAssignmentTest(String formula, Assignment expected) {
		final IParseResult result = ff.parseAssignment(formula,
				LanguageVersion.V2, null);
		assertFalse("parse failed for " + formula + ", problems: "
				+ result.getProblems(), result.hasProblem());
		final Assignment actual = result.getParsedAssignment();
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
	
	public static final IExpressionExtension DIRECT_PRODUCT = new IExpressionExtension() {

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public String getSyntaxSymbol() {
			return "§";
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {

			Type leftType = childExprs[0].getType();
			Type rightType = childExprs[1].getType();

			final Type alpha = leftType.getSource();
			final Type beta = leftType.getTarget();
			final Type gamma = rightType.getTarget();
			if (alpha != null && beta != null && gamma != null) {
				return ff.makeRelationalType(alpha, ff.makeProductType(beta,
						gamma));
			} else {
				return null;
			}
		}

		@Override
		public boolean verifyType(Type proposedType,
				Expression[] childExprs, Predicate[] childPreds) {
			final Type alphaP = proposedType.getSource();
			if (alphaP == null) {
				return false;
			}
			final Type target = proposedType.getTarget();
			if (!(target instanceof ProductType)) {
				return false;
			}
			return true;
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
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

		@Override
		public Object getOrigin() {
			return null;
		}

	};

	public void testExtensionDirectProduct() throws Exception {
		final FormulaFactory extFac = FormulaFactory
				.getInstance(DIRECT_PRODUCT);
		final Expression expected = extFac.makeExtendedExpression(DIRECT_PRODUCT,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeFreeIdentifier("B", null)),
				Collections.<Predicate> emptySet(), null);
		doExpressionTest("A§B", expected, extFac);
	}

	public void testBinaryWithClosedOperands() throws Exception {
		final FormulaFactory extFac = FormulaFactory
				.getInstance(DIRECT_PRODUCT);
		final Expression expected = extFac.makeExtendedExpression(
				DIRECT_PRODUCT, Arrays.<Expression> asList(
						extFac.makeUnaryExpression(KDOM,
								extFac.makeFreeIdentifier("A", null), null),
						extFac.makeUnaryExpression(KDOM,
								extFac.makeFreeIdentifier("B", null), null)),
				Collections.<Predicate> emptySet(), null);
		doExpressionTest("dom(A)§dom(B)", expected, extFac);
	}

	public void testExtensionInFormula() throws Exception {
		final FormulaFactory extFac = FormulaFactory
				.getInstance(DIRECT_PRODUCT);
		final Expression prodAB = extFac.makeExtendedExpression(DIRECT_PRODUCT,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeFreeIdentifier("B", null)),
				Collections.<Predicate> emptySet(), null);

		final Expression expected = ff.makeAssociativeExpression(BUNION,
				Arrays.<Expression> asList(prodAB, prodAB), null);
		doExpressionTest("(A§B) ∪ (A§B)", expected, extFac);
	}

	private static class Money implements IExpressionExtension {
		private static final String SYNTAX_SYMBOL = "€";
		private static final String OPERATOR_ID = "Money";
		
		private final boolean arithmetic;

		public Money(boolean arithmetic) {
			this.arithmetic = arithmetic;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return childExprs[0].getType();
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			for (Expression child : childExprs) {
				final Type childType = child.getType();
				if (!(childType instanceof IntegerType)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
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
			if (! arithmetic) {
				return;
			}
			try {
				mediator.addPriority(getId(), "plus");
			} catch (CycleError e) {
				fail("A cycle error was detected"
						+ " when adding priorities for plus " + e);
			}
		}

		@Override
		public String getGroupId() {
			return arithmetic ? ARITHMETIC.getId() : OPERATOR_ID;
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
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
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

		@Override
		public Object getOrigin() {
			return null;
		}

	}

	public static final IExpressionExtension MONEY = new Money(true);

	// verify that the newly introduced symbol cannot be an identifier
	public void testExtensionSymbol() throws Exception {
		final String strAEuroB = "A€B";
		
		final FormulaFactory extFac = FormulaFactory.getInstance(MONEY);
		
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
		final FormulaFactory extFac = FormulaFactory.getInstance(MONEY);
		final Expression expected = extFac.makeExtendedExpression(MONEY,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeFreeIdentifier("B", null),
						extFac.makeFreeIdentifier("C", null)),
				Collections.<Predicate> emptySet(), null);
		doParseUnparseTest("A € B € C", expected, extFac);
	}
	
	public void testAssociativeExtensionUnparseL() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(MONEY);
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
		final FormulaFactory extFac = FormulaFactory.getInstance(MONEY);
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
	
	public void testAssociativeWithClosedOperands() throws Exception {
		final Money money = new Money(false);
		final FormulaFactory extFac = FormulaFactory.getInstance(money);
		final Expression expected = extFac.makeExtendedExpression(money, Arrays
				.<Expression> asList(
						extFac.makeUnaryExpression(KDOM,
								extFac.makeFreeIdentifier("A", null), null),
						extFac.makeUnaryExpression(KDOM,
								extFac.makeFreeIdentifier("B", null), null),
						extFac.makeUnaryExpression(KDOM,
								extFac.makeFreeIdentifier("C", null), null)),
				Collections.<Predicate> emptySet(), null);
		doExpressionTest("dom(A) € dom(B) € dom(C)", expected, extFac);
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
						// Note : these bound identifiers (which occur on the
						// left-hand side of the top-level maplet) must be typed
						// as they are typed from the bound identifier
						// declarations when parsing the lambda construct.
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
	
	public static final IExpressionExtension EMAX = new IExpressionExtension() {
		private static final String SYNTAX_SYMBOL = "emax";
		private static final String OPERATOR_ID = "Extension Maximum";
		
		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return childExprs[0].getType();
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			for (Expression child : childExprs) {
				final Type childType = child.getType();
				if (!(childType instanceof IntegerType)) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
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
			return ARITHMETIC.getId();
		}

		@Override
		public String getId() {
			return OPERATOR_ID;
		}

		@Override
		public IExtensionKind getKind() {
			return makePrefixKind(EXPRESSION,
					makeAllExpr(makeFixedArity(3)));
		}

		@Override
		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
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
		public boolean isATypeConstructor() {
			return false;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

	};

	// verify that the newly introduced symbol cannot be part of an identifier
	public void testExtensionSymbolEMax() throws Exception {
		final String emax = "emax";
		
		final FormulaFactory extFac = FormulaFactory.getInstance(EMAX);
		
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
				ProblemKind.UnexpectedSymbol, ProblemSeverities.Error, "(", "End of Formula"));
	}
	
	public void testEMax() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(EMAX);
		final Expression expected = extFac.makeExtendedExpression(EMAX,
				Arrays.<Expression> asList(
						extFac.makeFreeIdentifier("A", null),
						extFac.makeFreeIdentifier("B", null),
						extFac.makeFreeIdentifier("C", null)),
				Collections.<Predicate> emptySet(), null);
		doExpressionTest("emax(A, B, C)", expected, extFac);
	}
	
	public void testEMaxInvalidNumberOfChildren() throws Exception {
		final FormulaFactory extFac = FormulaFactory.getInstance(EMAX);
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

	public void testDatatypeType() throws Exception {

		final ExtendedExpression list = LIST_FAC.makeExtendedExpression(
				EXT_LIST, Collections.<Expression> singleton(INT),
				Collections.<Predicate> emptyList(), null);

		final Expression expr = doExpressionTest("List(ℤ)", list,
				POW_LIST_INT_TYPE, LIST_FAC, false);

		assertTrue("expected a type expression", expr.isATypeExpression());
		assertEquals("unexpected toType", LIST_INT_TYPE, expr.toType());

		doTypeTest("List(ℤ)", LIST_INT_TYPE, LIST_FAC);

		final ParametricType listBoolType = LIST_FAC.makeParametricType(
				Collections.<Type> singletonList(BOOL_TYPE), EXT_LIST);
		assertFalse(listBoolType.equals(LIST_INT_TYPE));
	}

	public void testDatatypeExpr() throws Exception {
		final Expression upTo = LIST_FAC.makeBinaryExpression(UPTO, ZERO, ONE,
				null);

		final ExtendedExpression list0upTo1 = LIST_FAC.makeExtendedExpression(
				EXT_LIST, Collections.<Expression> singleton(upTo),
				Collections.<Predicate> emptyList(), null);

		final Expression expr = doExpressionTest("List(0‥1)", list0upTo1,
				POW_LIST_INT_TYPE, LIST_FAC, false);
		assertFalse("unexpected type expression", expr.isATypeExpression());
		final IParseResult result = parseTypeRes("List(0‥1)", LIST_FAC);
		assertFailure(result, new ASTProblem(new SourceLocation(0, 8),
				InvalidTypeExpression, ProblemSeverities.Error));
	}
	
	public void testDatatypeNil() throws Exception {

		final ExtendedExpression nil = LIST_FAC.makeExtendedExpression(EXT_NIL,
				Collections.<Expression> emptyList(),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("nil", nil, LIST_FAC);

		final ExtendedExpression nilInt = LIST_FAC.makeExtendedExpression(
				EXT_NIL, NO_EXPR, NO_PRED, null, LIST_INT_TYPE);

		doExpressionTest("(nil ⦂ List(ℤ))", nilInt, LIST_INT_TYPE, LIST_FAC,
				false);

		final ParametricType listBoolBoolType = LIST_FAC.makeParametricType(
				Collections.<Type> singletonList(LIST_FAC.makeProductType(
						BOOL_TYPE, BOOL_TYPE)), EXT_LIST);
		final ExtendedExpression nilBoolBool = LIST_FAC.makeExtendedExpression(
				EXT_NIL, NO_EXPR, NO_PRED, null, listBoolBoolType);

		doExpressionTest("(nil ⦂ List(BOOL×BOOL))", nilBoolBool,
				listBoolBoolType, LIST_FAC, false);

		assertFalse(nil.equals(nilInt));
		assertFalse(nil.equals(nilBoolBool));
		assertFalse(nilBoolBool.equals(nilInt));
	}
	
	public void testDatatypeNilInvalidType() throws Exception {
		final IParseResult result = parseExprRes("(nil ⦂ ℤ)", LIST_FAC,
				LanguageVersion.LATEST);
		assertFailure(result, new ASTProblem(new SourceLocation(1, 7),
				InvalidGenericType, Error, "[see operator definition]"));
	}
	
	public void testDatatypeConstructor() throws Exception {

		final ExtendedExpression nil = LIST_FAC.makeExtendedExpression(EXT_NIL,
				NO_EXPR, NO_PRED, null);

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
		assertNotNull("head destructor not found", EXT_HEAD);

		assertNotNull("tail destructor not found", EXT_TAIL);
		
		final ExtendedExpression head = LIST_FAC.makeExtendedExpression(
				EXT_HEAD, Arrays.<Expression> asList(FRID_x),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("head(x)", head, LIST_FAC);

		final ExtendedExpression tail = LIST_FAC.makeExtendedExpression(
				EXT_TAIL, Arrays.<Expression> asList(FRID_x),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("tail(x)", tail, LIST_FAC);
	}
	
	public void testTypeConstrTypeCheck() throws Exception {
		final Expression listIntExpr = LIST_FAC.makeExtendedExpression(
				EXT_LIST, Collections.<Expression> singleton(INT),
				Collections.<Predicate> emptySet(), null);
		final Predicate expected = LIST_FAC.makeRelationalPredicate(IN, FRID_x,
				listIntExpr, null);
		
		final Predicate pred = doPredicateTest("x ∈ List(ℤ)", expected,
				LIST_FAC);
		final ITypeCheckResult tcResult = pred.typeCheck(LIST_FAC.makeTypeEnvironment());
		assertFalse(tcResult.hasProblem());
		assertTrue(pred.isTypeChecked());
		final FreeIdentifier[] freeIdentifiers = pred.getFreeIdentifiers();
		assertEquals(1, freeIdentifiers.length);
		final FreeIdentifier x = freeIdentifiers[0];
		assertEquals(LIST_INT_TYPE, x.getType());
	}
	
	public void testTypeCheckError() throws Exception {
		// problem raised by Issam, produced a StackOverflowError
		final Expression A_Id = LIST_FAC.makeFreeIdentifier("A", null);
		
		final Expression List_A = LIST_FAC.makeExtendedExpression(
				EXT_LIST, asList(A_Id), Collections.<Predicate> emptySet(),
				null);
		final Expression List_List_A = LIST_FAC.makeExtendedExpression(
				EXT_LIST, asList(List_A), Collections.<Predicate> emptySet(),
				null);
		
		final BoundIdentDecl bid_x = LIST_FAC.makeBoundIdentDecl("x", null);
		final BoundIdentDecl bid_y = LIST_FAC.makeBoundIdentDecl("y", null);
		final BoundIdentifier bi_x = LIST_FAC.makeBoundIdentifier(1, null);
		final BoundIdentifier bi_y = LIST_FAC.makeBoundIdentifier(0, null);

		final Predicate x_In_A = LIST_FAC.makeRelationalPredicate(IN, bi_x,
				A_Id, null);

		final Predicate y_In_ListListA = LIST_FAC.makeRelationalPredicate(IN,
				bi_y, List_List_A, null);
		
		final ExtendedExpression cons_x_y = LIST_FAC
				.makeExtendedExpression(EXT_CONS, new Expression[]{bi_x, bi_y}, NO_PRED, null);
		final Predicate cons_In_ListA = LIST_FAC
		.makeRelationalPredicate(IN, cons_x_y,
				List_A, null);
		
		final Predicate expected = LIST_FAC.makeQuantifiedPredicate(
				FORALL,
				asList(bid_x,
						bid_y), LIST_FAC
						.makeBinaryPredicate(LIMP, LIST_FAC
								.makeAssociativePredicate(LAND, asList(x_In_A, y_In_ListListA), null), 
								cons_In_ListA
								, null), null);
		final Predicate pred = doPredicateTest("∀ x,y· (x ∈A ∧ y ∈List(List(A))) ⇒ cons(x,y)∈ List(A)", expected,
				LIST_FAC);
		final ITypeCheckResult tcRes = pred.typeCheck(LIST_FAC.makeTypeEnvironment());
		assertTrue(tcRes.hasProblem());
		
		final List<ASTProblem> problems = tcRes.getProblems();
		for (ASTProblem problem : problems) {
			assertEquals(ProblemKind.Circularity, problem.getMessage());
		}
	}
	
	public void testDatatypeDestructorsTyping() throws Exception {
		
		final ExtendedExpression nil = LIST_FAC.makeExtendedExpression(EXT_NIL,
				Collections.<Expression> emptyList(), Collections
						.<Predicate> emptyList(), null);

		final ExtendedExpression list1 = LIST_FAC.makeExtendedExpression(EXT_CONS,
				Arrays.asList(ONE, nil), Collections
						.<Predicate> emptyList(), null);

		final ExtendedExpression headList1 = LIST_FAC.makeExtendedExpression(
				EXT_HEAD, Arrays.<Expression> asList(list1),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("head(cons(1, nil))", headList1, INT_TYPE, LIST_FAC, true);

		final ExtendedExpression tail = LIST_FAC.makeExtendedExpression(
				EXT_TAIL, Arrays.<Expression> asList(list1),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("tail(cons(1, nil))", tail, LIST_INT_TYPE, LIST_FAC, true);
	}
	
	public void testListOfLists() throws Exception {
		final ExtendedExpression nil = LIST_FAC.makeExtendedExpression(EXT_NIL,
				Collections.<Expression> emptyList(),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression nilInt = LIST_FAC.makeExtendedExpression(
				EXT_NIL, NO_EXPR, NO_PRED, null, LIST_INT_TYPE);

		final ExtendedExpression listNilNil = LIST_FAC.makeExtendedExpression(
				EXT_CONS, Arrays.<Expression> asList(nilInt, nil),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression headListNil = LIST_FAC.makeExtendedExpression(
				EXT_HEAD, Arrays.<Expression> asList(listNilNil),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("head(cons((nil ⦂ List(ℤ)), nil))", headListNil,
				LIST_INT_TYPE, LIST_FAC, true);

		final ExtendedExpression cons1 = LIST_FAC.makeExtendedExpression(
				EXT_CONS, Arrays.asList(ONE, nil),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression consCons1 = LIST_FAC.makeExtendedExpression(
				EXT_CONS, Arrays.<Expression> asList(cons1, nil),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression tailConsCons1 = LIST_FAC
				.makeExtendedExpression(EXT_TAIL,
						Arrays.<Expression> asList(consCons1),
						Collections.<Predicate> emptyList(), null);

		final ParametricType LIST_LIST_INT_TYPE = LIST_FAC.makeParametricType(
				Arrays.<Type> asList(LIST_INT_TYPE), EXT_LIST);

		doExpressionTest("tail(cons(cons(1, nil), nil))", tailConsCons1,
				LIST_LIST_INT_TYPE, LIST_FAC, true);
	}
	
	public void testDatatypeOrigins() throws Exception {
		for (IFormulaExtension extension : LIST_DT.getExtensions()) {
			final Object origin = extension.getOrigin();
			assertSame("wrong origin for " + extension.getId(), LIST_DT, origin);
		}
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
	
	public static final IPredicateExtension EXT_PRIME = new IPredicateExtension() {
		private static final String SYMBOL = "prime";
		private static final String ID = "Ext Prime";
		
		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
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
			return ATOMIC_PRED.getId();
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
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			final Expression child = predicate.getChildExpressions()[0];
			final Type childType = tcMediator.makePowerSetType(tcMediator.makeIntegerType());
			tcMediator.sameType(child.getType(), childType);
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public Object getOrigin() {
			return null;
		}
	};

	private static final FormulaFactory PRIME_FAC = FormulaFactory
			.getInstance(EXT_PRIME);

	public void testPredicateExtension() throws Exception {
		final ExtendedPredicate expected = PRIME_FAC.makeExtendedPredicate(
				EXT_PRIME, Arrays.<Expression> asList(ONE),
				Collections.<Predicate> emptySet(), null);
		doPredicateTest("prime(1)", expected, PRIME_FAC);
	}
	
	public void testPredicateExtensionInFormula() throws Exception {
		final Predicate primeOne = PRIME_FAC.makeExtendedPredicate(EXT_PRIME,
				Arrays.<Expression> asList(ONE),
				Collections.<Predicate> emptySet(), null);
		final Predicate expected = PRIME_FAC.makeAssociativePredicate(LAND,
				asList(primeOne, primeOne), null);
		
		doPredicateTest("prime(1) ∧ prime(1)", expected, PRIME_FAC);
	}
	
	private static final IDatatypeExtension MOULT_TYPE = new IDatatypeExtension() {

		private static final String TYPE_NAME = "Moult";
		private static final String TYPE_IDENTIFIER = "Moult Id";

		@Override
		public String getTypeName() {
			return TYPE_NAME;
		}

		@Override
		public String getId() {
			return TYPE_IDENTIFIER;
		}
		
		@Override
		public void addTypeParameters(ITypeConstructorMediator mediator) {
			mediator.addTypeParam("S");			
			mediator.addTypeParam("T");			
		}

		@Override
		public void addConstructors(IConstructorMediator mediator) {
			final ITypeParameter typeS = mediator.getTypeParameter("S");
			final ITypeParameter typeT = mediator.getTypeParameter("T");
			
			final IArgumentType refS = mediator.newArgumentType(typeS);
			final IArgument argS = mediator.newArgument(refS);
			final IArgumentType refT = mediator.newArgumentType(typeT);
			final IArgument argT = mediator.newArgument(refT);
			mediator.addConstructor("makeMoult", "MAKE MOULT", Arrays.asList(argS, argT));
		}

	};
	
	private static final IDatatype MOULT_DT = ff.makeDatatype(MOULT_TYPE);
	private static final FormulaFactory MOULT_FAC = FormulaFactory
			.getInstance(MOULT_DT.getExtensions());
	private static final IExpressionExtension EXT_MOULT = MOULT_DT
			.getTypeConstructor();
	private static final ParametricType MOULT_INT_BOOL_TYPE = MOULT_FAC
			.makeParametricType(Arrays.<Type> asList(INT_TYPE, BOOL_TYPE),
					EXT_MOULT);
	private static final IExpressionExtension EXT_MAKE_MOULT = MOULT_DT
			.getConstructor("MAKE MOULT");

	public void testMoult() throws Exception {

		doTypeTest("Moult(ℤ, BOOL)", MOULT_INT_BOOL_TYPE, MOULT_FAC);

		final ExtendedExpression moult1True = MOULT_FAC.makeExtendedExpression(
				EXT_MAKE_MOULT, Arrays.asList(ONE, ATOM_TRUE),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("makeMoult(1, TRUE)", moult1True, MOULT_INT_BOOL_TYPE,
				MOULT_FAC, true);
	}

	static class NoInducType implements IDatatypeExtension {

		public static final String CONS1 = "CONS1 ID";
		public static final String CONS2 = "CONS2 ID";
		public static final String CONS3 = "CONS3 ID";
		
		private static final String TYPE_NAME = "NoInduc";
		private static final String TYPE_IDENTIFIER = "NoInduc Id";

		@Override
		public String getTypeName() {
			return TYPE_NAME;
		}

		@Override
		public String getId() {
			return TYPE_IDENTIFIER;
		}
		
		@Override
		public void addTypeParameters(ITypeConstructorMediator mediator) {
			mediator.addTypeParam("S");			
			mediator.addTypeParam("T");			
		}

		@Override
		public void addConstructors(IConstructorMediator mediator) {
			final ITypeParameter typeS = mediator.getTypeParameter("S");
			final IArgumentType refS = mediator.newArgumentType(typeS);
			final IArgument argS = mediator.newArgument(refS);
			
			final ITypeParameter typeT = mediator.getTypeParameter("T");
			final IArgumentType refT = mediator.newArgumentType(typeT);
			final IArgument argT = mediator.newArgument(refT);
			
			final IntegerType intType = mediator.makeIntegerType();
			final PowerSetType powInt = mediator.makePowerSetType(intType);
			final IArgumentType powIntType = mediator.newArgumentType(powInt);
			final IArgument argPowInt = mediator.newArgument(powIntType);

			mediator.addConstructor("cons1", CONS1,
					asList(argS, argPowInt, argT));

			final IArgumentType powS = mediator.makePowerSetType(refS);
			final IArgument argPowS = mediator.newArgument(powS);
			
			final IArgumentType prodPowIntT = mediator.makeProductType(
					powIntType, refT);
			final IArgument argProdPowIntT = mediator.newArgument(prodPowIntT);
			mediator.addConstructor("cons2", CONS2,
					asList(argPowS, argProdPowIntT));
			
			final IArgumentType relST = mediator.makeRelationalType(refS, refT);
			final IArgument argRelST = mediator.newArgument(relST);
			
			mediator.addConstructor("cons3", CONS3, asList(argRelST));
		}

	}

	private static final IDatatypeExtension NO_INDUC_TYPE = new NoInducType();

	private static final IDatatype NO_INDUC_EXTNS = ff
			.makeDatatype(NO_INDUC_TYPE);
	private static final FormulaFactory NO_INDUC_FAC = FormulaFactory
			.getInstance(NO_INDUC_EXTNS.getExtensions());
	private static final IExpressionExtension EXT_NO_INDUC = NO_INDUC_EXTNS
			.getTypeConstructor();
	private static final ParametricType NO_INDUC_INT_BOOL_TYPE = NO_INDUC_FAC
			.makeParametricType(Arrays.<Type> asList(INT_TYPE, BOOL_TYPE),
					EXT_NO_INDUC);

	public void testNoInducType() throws Exception {
		doTypeTest("NoInduc(ℤ, BOOL)", NO_INDUC_INT_BOOL_TYPE, NO_INDUC_FAC);
	}

	public void testArgSimpleType() throws Exception {
		final IExpressionExtension extCons1 = NO_INDUC_EXTNS
				.getConstructor(NoInducType.CONS1);

		final ExtendedExpression c1Sing0True = NO_INDUC_FAC
				.makeExtendedExpression(extCons1, Arrays.asList(ONE,
						NO_INDUC_FAC.makeSetExtension(ZERO, null), ATOM_TRUE),
						Collections.<Predicate> emptyList(), null);

		doExpressionTest("cons1(1, {0}, TRUE)", c1Sing0True,
				NO_INDUC_INT_BOOL_TYPE, NO_INDUC_FAC, true);
	}

	public void testArgPowerSetType() throws Exception {
		final IExpressionExtension extCons2 = NO_INDUC_EXTNS
				.getConstructor(NoInducType.CONS2);

		final ExtendedExpression c2Sing2MapSing0True = NO_INDUC_FAC
				.makeExtendedExpression(extCons2, Arrays.asList(
						NO_INDUC_FAC.makeSetExtension(ONE, null),
						NO_INDUC_FAC.makeBinaryExpression(MAPSTO,
								NO_INDUC_FAC.makeSetExtension(ZERO, null),
								ATOM_TRUE, null)), Collections
						.<Predicate> emptyList(), null);

		doExpressionTest("cons2({1}, {0} ↦ TRUE)", c2Sing2MapSing0True,
				NO_INDUC_INT_BOOL_TYPE, NO_INDUC_FAC, true);
	}

	public void testArgRelationalType() throws Exception {
		final IExpressionExtension extCons3 = NO_INDUC_EXTNS
				.getConstructor(NoInducType.CONS3);

		final ExtendedExpression c3SingMaps0True = NO_INDUC_FAC
				.makeExtendedExpression(extCons3, Arrays.<Expression>asList(
						NO_INDUC_FAC.makeSetExtension(Arrays.<Expression>asList(
								NO_INDUC_FAC.makeBinaryExpression(MAPSTO,
								ZERO, ATOM_TRUE, null)), null)), Collections
						.<Predicate> emptyList(), null);

		doExpressionTest("cons3({0 ↦ TRUE})", c3SingMaps0True,
				NO_INDUC_INT_BOOL_TYPE, NO_INDUC_FAC, true);
	}

	public void testDatatypeSameExtensions() throws Exception {
		final IDatatype extns1 = ff.makeDatatype(NO_INDUC_TYPE);
		final IDatatype extns2 = ff.makeDatatype(NO_INDUC_TYPE);
		final IExpressionExtension typeExt1 = extns1.getTypeConstructor();
		final IExpressionExtension typeExt2 = extns2.getTypeConstructor();
		assertSame("expected same extensions", typeExt1, typeExt2);

		final IExpressionExtension cons1Ext1 = extns1
				.getConstructor(NoInducType.CONS1);
		final IExpressionExtension cons1Ext2 = extns2
				.getConstructor(NoInducType.CONS1);
		assertSame("expected same extensions", cons1Ext1, cons1Ext2);
	}
	
	public void testAddingExtensions() throws Exception {
		final Predicate primeOne = PRIME_FAC.makeExtendedPredicate(EXT_PRIME,
				Arrays.<Expression> asList(ONE),
				Collections.<Predicate> emptySet(), null);

		final Expression nil = LIST_FAC.makeExtendedExpression(EXT_NIL,
				Collections.<Expression> emptyList(),
				Collections.<Predicate> emptyList(), null);

		final Expression listInt = LIST_FAC.makeExtendedExpression(EXT_LIST,
				Collections.<Expression> singleton(INT),
				Collections.<Predicate> emptyList(), null);

		final Expression powListInt = LIST_FAC.makeUnaryExpression(POW,
				listInt, null);

		final Predicate nilInListInt = LIST_FAC.makeRelationalPredicate(IN,
				nil, powListInt, null);

		final FormulaFactory facListPrime = LIST_FAC.withExtensions(Collections
				.<IFormulaExtension> singleton(EXT_PRIME));

		final Predicate separate = facListPrime.makeAssociativePredicate(LAND,
				asList(primeOne, nilInListInt), null);

		doPredicateTest("prime(1) ∧ nil ∈ ℙ(List(ℤ))", separate, facListPrime);
		
		final Predicate primeNil = facListPrime.makeExtendedPredicate(EXT_PRIME,
				Arrays.<Expression> asList(nil),
				Collections.<Predicate> emptySet(), null);
		
		doPredicateTest("prime(nil)", primeNil, facListPrime);
	}
	
	public void testMixedTypesToType() throws Exception {
		final Expression moultIntBool = MOULT_FAC.makeExtendedExpression(
				EXT_MOULT, Arrays.<Expression> asList(INT, BOOL),
				Collections.<Predicate> emptySet(), null);

		final FormulaFactory listMoultFac = LIST_FAC.withExtensions(MOULT_DT
				.getExtensions());
		
		final Expression listMoult = listMoultFac.makeExtendedExpression(
				EXT_LIST, Collections.<Expression> singleton(moultIntBool),
				Collections.<Predicate> emptyList(), null);
		
		final ParametricType listMoultType = listMoultFac.makeParametricType(
				Collections.<Type> singletonList(MOULT_INT_BOOL_TYPE), EXT_LIST);
		
		final Type powListMoultType = listMoultFac.makePowerSetType(listMoultType);
		
		doExpressionTest("List(Moult(ℤ, BOOL))", listMoult, powListMoultType,
				listMoultFac, false);
		
		assertTrue("expected a type expression", listMoult.isATypeExpression());
		assertEquals("unexpected type", listMoultType, listMoult.toType());
		
		doTypeTest("List(Moult(ℤ, BOOL))", listMoultType, listMoultFac);
		doTypeTest("ℙ(List(Moult(ℤ, BOOL)))", powListMoultType, listMoultFac);
	}
	
	private static final IExpressionExtension COND = FormulaFactory.getCond();

	private static FormulaFactory FAC_COND = FormulaFactory.getInstance(COND);

	private static ExtendedExpression makeCond(Predicate condition, Expression expr1,
			Expression expr2, SourceLocation location) {
		return FAC_COND.makeExtendedExpression(COND, asList(expr1, expr2),
				asList(condition), location);
	}

	public void testCond() throws Exception {
		final Expression expectedInt = makeCond(LIT_BTRUE, ZERO, ONE, null);
		doExpressionTest("COND(⊤, 0, 1)", expectedInt, INT_TYPE, FAC_COND, false);
		
		final Expression expected = makeCond(LIT_BFALSE, FRID_a, ATOM_TRUE, null);
		doExpressionTest("COND(⊥, a, TRUE)", expected, BOOL_TYPE, FAC_COND, true);
	}
	
	private static Expression makeMaxCond(Expression a, Expression b) {
		return makeCond(ff.makeRelationalPredicate(Formula.LE, a, b, null), b, a, null);
	}
	
	public void testCondWD() throws Exception {
		final Expression max_a_b = makeMaxCond(FRID_a, FRID_b);
		// WD is 'true & a<b=>true & not(a<b)=>true', but after WD simplifier:
		final Predicate expectedWD = LIT_BTRUE;
		
		max_a_b.typeCheck(FAC_COND.makeTypeEnvironment());
		final Predicate actualWD = max_a_b.getWDPredicate(FAC_COND);
		
		assertEquals(expectedWD, actualWD);
	}
	
	
	public void testCondWDnotTrivial() throws Exception {
		final Expression expr = parseExpr(
				"COND(a÷b=1,card({a,b}),card({0,1,2}))", FAC_COND,
				LanguageVersion.LATEST);
		expr.typeCheck(FAC_COND.makeTypeEnvironment());

		final Predicate expectedWD = parsePredicate(
				"b≠0 ∧ (a÷b=1⇒finite({a,b})) ∧ (¬ a÷b=1⇒finite({0,1,2}))",
				FAC_COND);
		expectedWD.typeCheck(FAC_COND.makeTypeEnvironment());
	
		final Predicate actualWD = expr.getWDPredicate(FAC_COND);
		
		assertEquals(expectedWD, actualWD);
	}
	
	public void testExtraParentheses() throws Exception {
		assertFailure(ff.parseExpression(")", LATEST, null),
				makeError(0, 0, UnmatchedTokens), makeError(0, 0, PrematureEOF));

		assertFailure(ff.parseExpression("f(x))", LATEST, null),
				makeError(4, 4, UnmatchedTokens));

		assertFailure(ff.parseExpression("(", LATEST, null),
				makeError(0, 0, PrematureEOF));

		assertFailure(ff.parseExpression("f(x)(", LATEST, null),
				makeError(4, 4, PrematureEOF));
		
		assertFailure(ff.parseExpression("(]", LATEST, null),
				makeError(1, 1, UnknownOperator, "]"));
		
		assertFailure(ff.parseExpression("(0]", LATEST, null),
				makeError(2, 2, ProblemKind.UnexpectedSymbol, ")", "]"));
	}

	private static ASTProblem makeError(int start, int end, ProblemKind problem, Object... args) {
		return new ASTProblem(new SourceLocation(start, end),
				problem, Error, args);
	}

	public void testEqualInAssign() throws Exception {
		final IParseResult res = ff.parseAssignment("x = 0", LATEST, null);
		assertFailure(
				res,
				makeError(0, 4, UnknownOperator,
						" (expected to find an assignment operator)"));
	}
	
	public void testFactoryCache() throws Exception {
		final Set<IFormulaExtension> extPrimeList1 = new HashSet<IFormulaExtension>();
		final Set<IFormulaExtension> extPrimeList2 = new LinkedHashSet<IFormulaExtension>();
		final Set<IFormulaExtension> listExtns = LIST_DT.getExtensions();
		extPrimeList1.addAll(listExtns);
		extPrimeList1.add(EXT_PRIME);
		extPrimeList2.addAll(extPrimeList1);
		
		final Set<IFormulaExtension> extList = new HashSet<IFormulaExtension>();
		extList.addAll(listExtns);

		final FormulaFactory ffPrimeList1 = FormulaFactory.getInstance(extPrimeList1);
		final FormulaFactory ffPrimeList2 = FormulaFactory.getInstance(extPrimeList2);
		assertSame("expected same ff instances", ffPrimeList1, ffPrimeList2);
		
		assertSame("expected same ff instances", ffPrimeList1,
				ffPrimeList1.withExtensions(extList));
		
		final FormulaFactory ffList = FormulaFactory.getInstance(extList);
		assertNotSame("expected different ff", ffPrimeList1, ffList);
	}

	private static class DummyExtn implements IPredicateExtension {

		private String symbol;
		private String id;

		public DummyExtn(String symbol, String id) {
			this.symbol = symbol;
			this.id = id;
		}

		@Override
		public String getSyntaxSymbol() {
			return symbol;
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
			return id;
		}

		@Override
		public String getGroupId() {
			return "org.eventb.core.ast.dummy";
		}

		@Override
		public IExtensionKind getKind() {
			return makePrefixKind(PREDICATE, NO_CHILD);
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// None			
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// None
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			// nothing
		}
		
	}
	
	public void testIdUnicityGiven() throws Exception {
		final String id = "unic_id0";
		final DummyExtn ext_s1_id0 = new DummyExtn("unic_s1", id);
		final DummyExtn ext_s2_id0 = new DummyExtn("unic_s2", id);
		
		try {
			FormulaFactory.getInstance(ext_s1_id0, ext_s2_id0);
			fail("duplicate id in given extensions");
		} catch (IllegalArgumentException e) {
			// as expected
		}
		
	}
	
	public void testSymbolUnicityGiven() throws Exception {
		final String symbol = "unic_s5";
		final DummyExtn ext_s5_id2 = new DummyExtn(symbol, "unic_id2");
		final DummyExtn ext_s5_id3 = new DummyExtn(symbol, "unic_id3");
		
		try {
			FormulaFactory.getInstance(ext_s5_id2, ext_s5_id3);
			fail("duplicate symbol in given extensions");
		} catch (IllegalArgumentException e) {
			// as expected
		}
		
	}
	
	public void testSymbolNonGlobalUnicity() throws Exception {
		final String symbol = "unic_s6";
		final DummyExtn ext_s6_id4 = new DummyExtn(symbol, "unic_id4");
		final DummyExtn ext_s6_id5 = new DummyExtn(symbol, "unic_id5");
		
		FormulaFactory.getInstance(ext_s6_id4);
		
		// same symbol with different ids is authorized
		FormulaFactory.getInstance(ext_s6_id5);
	}
	
	public void testOverridingStandardId() throws Exception {
		final DummyExtn ext_s7_idLand = new DummyExtn("unic_s7", BCOMP_ID);
		
		try {
			FormulaFactory.getInstance(ext_s7_idLand);
			fail("overriding standard id");
		} catch (IllegalArgumentException e) {
			// as expected
		}

	}
	
	public void testOverridingStandardSymbol() throws Exception {
		final DummyExtn ext_partition_id6 = new DummyExtn("partition", "unic_id6");
		
		try {
			FormulaFactory.getInstance(ext_partition_id6);
			fail("overriding standard symbol");
		} catch (IllegalArgumentException e) {
			// as expected
		}

	}

	public void testGrammarViewBug() throws Exception {
		// the following throws IndexOutOfBoundsException when bug is present
		ff.getGrammarView();
	}

	/**
	 * Ensures that a prefix operator contributed by an extension is compatible
	 * with equality.
	 */
	public void testExprExtWithEquals() {
		final Expression extended = EFF.makeExtendedExpression(barS,
				mList(ZERO, ONE), mList(LIT_BTRUE, LIT_BTRUE), null);
		doPredicateTest("barS(⊤, 0, ⊤, 1) = 0",
				EFF.makeRelationalPredicate(EQUAL, extended, ZERO, null), EFF);
		doPredicateTest("0 = barS(⊤, 0, ⊤, 1)",
				EFF.makeRelationalPredicate(EQUAL, ZERO, extended, null), EFF);
	}

}
