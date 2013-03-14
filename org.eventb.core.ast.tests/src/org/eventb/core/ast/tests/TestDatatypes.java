/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
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
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.UPTO;
import static org.eventb.core.ast.ProblemKind.InvalidGenericType;
import static org.eventb.core.ast.ProblemKind.InvalidTypeExpression;
import static org.eventb.core.ast.ProblemSeverities.Error;
import static org.eventb.core.ast.tests.FastFactory.mInferredTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype2.IConstructorBuilder;
import org.eventb.core.ast.extension.datatype2.IConstructorExtension;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.eventb.core.ast.extension.datatype2.IDatatypeBuilder;
import org.eventb.core.ast.extension.datatype2.IDestructorExtension;
import org.junit.Test;

public class TestDatatypes extends AbstractTests {

	/* Copy equivalent of AbstractTests List definition */
	protected static final IDatatype2 NLIST_DT;
	protected static final FormulaFactory NLIST_FF;

	static {
		final GivenType tyS = ff.makeGivenType("S");
		final GivenType tyList = ff.makeGivenType("List");
		final IDatatypeBuilder builder = ff.makeDatatypeBuilder("List", tyS);
		builder.addConstructor("nil");
		final IConstructorBuilder cons = builder.addConstructor("cons");
		cons.addArgument(tyS, "head");
		cons.addArgument(tyList, "tail");
		NLIST_DT = builder.finalizeDatatype();
		NLIST_FF = FormulaFactory.getInstance(NLIST_DT.getExtensions());
	}


	protected static final IExpressionExtension NEXT_LIST = NLIST_DT
			.getTypeConstructor();

	protected static final ParametricType NLIST_INT_TYPE = NLIST_FF
			.makeParametricType(Collections.<Type> singletonList(NLIST_FF
					.makeIntegerType()), NEXT_LIST);
	protected static final PowerSetType NPOW_LIST_INT_TYPE = NLIST_FF
			.makePowerSetType(NLIST_INT_TYPE);
	protected static final IConstructorExtension NEXT_NIL = NLIST_DT
			.getConstructor("nil");
	protected static final IConstructorExtension NEXT_CONS = NLIST_DT
			.getConstructor("cons");
	protected static final IDestructorExtension NEXT_HEAD = NEXT_CONS
			.getDestructor("head");
	protected static final IDestructorExtension NEXT_TAIL = NEXT_CONS
			.getDestructor("tail");
	/* End of copy */

	private static final Predicate[] NO_PRED = new Predicate[0];
	private static final Expression[] NO_EXPR = new Expression[0];

	protected static final AtomicExpression INT_ffLIST = NLIST_FF
			.makeAtomicExpression(Formula.INTEGER, null);

	protected static final BooleanType BOOL_TYPE_ffLIST = NLIST_FF
			.makeBooleanType();

	protected static final IntegerLiteral ONE_ffLIST = NLIST_FF
			.makeIntegerLiteral(BigInteger.ONE, null);
	protected static final IntegerLiteral ZERO_ffLIST = NLIST_FF
			.makeIntegerLiteral(BigInteger.ZERO, null);
	protected static final FreeIdentifier FRID_x_ffLIST = NLIST_FF
			.makeFreeIdentifier("x", null);

	protected static final SourceLocationChecker slChecker = new SourceLocationChecker();

	private static void assertFailure(IParseResult result,
			ASTProblem... expected) {
		assertTrue("expected parsing to fail", result.hasProblem());
		final List<ASTProblem> problems = result.getProblems();
		assertEquals("wrong problem", asList(expected), problems);
	}

	private static IParseResult parseTypeRes(String image,
			FormulaFactory factory) {
		return factory.parseType(image);
	}

	private static Type doTypeTest(String formula, Type expected,
			FormulaFactory factory) {
		final IParseResult result = parseTypeRes(formula, factory);
		assertFalse("unexpected problems " + result.getProblems(),
				result.hasProblem());
		final Type actual = result.getParsedType();
		assertEquals(expected, actual);
		return actual;
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

	private static <T extends Formula<T>> void checkParsedFormula(
			String formula, T expected, T actual) {
		assertEquals(expected, actual);

		actual.accept(slChecker);
		checkSourceLocation(actual, formula.length());
	}

	private static Expression parseExpr(String formula, FormulaFactory factory) {
		final IParseResult result = factory.parseExpression(formula, null);
		assertFalse("unexpected problem(s): " + result.getProblems(),
				result.hasProblem());
		final Expression actual = result.getParsedExpression();
		return actual;
	}

	private static Expression parseAndCheck(String formula,
			Expression expected, FormulaFactory factory) {
		final Expression actual = parseExpr(formula, factory);
		checkParsedFormula(formula, expected, actual);

		return actual;
	}

	private static Expression doExpressionTest(String formula,
			Expression expected, FormulaFactory factory) {
		return parseAndCheck(formula, expected, factory);
	}

	private static Expression doExpressionTest(String formula,
			Expression expected, Type expectedType, FormulaFactory factory,
			boolean typeCheck) {
		final Expression actual = doExpressionTest(formula, expected, factory);
		if (typeCheck) {
			final ITypeCheckResult result = actual.typeCheck(factory
					.makeTypeEnvironment());
			assertFalse(
					"unexpected type check problems " + result.getProblems(),
					result.hasProblem());
		}
		assertEquals(expectedType, actual.getType());
		return actual;
	}

	private static Predicate doPredicateTest(String formula,
			Predicate expected, FormulaFactory factory) {
		final IParseResult result = factory.parsePredicate(formula, null);
		assertFalse("unexpected problem(s): " + result.getProblems(),
				result.hasProblem());
		final Predicate actual = result.getParsedPredicate();
		checkParsedFormula(formula, expected, actual);
		return actual;
	}

	@Test
	public void testDatatypeType() throws Exception {

		final ExtendedExpression list = NLIST_FF.makeExtendedExpression(
				NEXT_LIST, Collections.<Expression> singleton(INT_ffLIST),
				Collections.<Predicate> emptyList(), null);

		final Expression expr = doExpressionTest("List(ℤ)", list,
				NPOW_LIST_INT_TYPE, NLIST_FF, false);

		assertTrue("expected a type expression", expr.isATypeExpression());
		assertEquals("unexpected toType", NLIST_INT_TYPE, expr.toType());

		doTypeTest("List(ℤ)", NLIST_INT_TYPE, NLIST_FF);

		final ParametricType listBoolType = NLIST_FF.makeParametricType(
				Collections.<Type> singletonList(BOOL_TYPE_ffLIST), NEXT_LIST);
		assertFalse(listBoolType.equals(NLIST_INT_TYPE));
	}

	@Test
	public void testDatatypeExpr() throws Exception {
		final Expression upTo = NLIST_FF.makeBinaryExpression(UPTO,
				NLIST_FF.makeIntegerLiteral(BigInteger.ZERO, null),
				NLIST_FF.makeIntegerLiteral(BigInteger.ONE, null), null);

		final ExtendedExpression list0upTo1 = NLIST_FF.makeExtendedExpression(
				NEXT_LIST, Collections.<Expression> singleton(upTo),
				Collections.<Predicate> emptyList(), null);

		final Expression expr = doExpressionTest("List(0‥1)", list0upTo1,
				NPOW_LIST_INT_TYPE, NLIST_FF, false);
		assertFalse("unexpected type expression", expr.isATypeExpression());
		final IParseResult result = parseTypeRes("List(0‥1)", NLIST_FF);
		assertFailure(result, new ASTProblem(new SourceLocation(0, 8),
				InvalidTypeExpression, ProblemSeverities.Error));
	}

	@Test
	public void testDatatypeNil() throws Exception {

		final ExtendedExpression nil = NLIST_FF.makeExtendedExpression(
				NEXT_NIL, Collections.<Expression> emptyList(),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("nil", nil, NLIST_FF);

		final ExtendedExpression nilInt = NLIST_FF.makeExtendedExpression(
				NEXT_NIL, NO_EXPR, NO_PRED, null, NLIST_INT_TYPE);

		doExpressionTest("(nil ⦂ List(ℤ))", nilInt, NLIST_INT_TYPE, NLIST_FF,
				false);

		final ParametricType listBoolBoolType = NLIST_FF.makeParametricType(
				Collections.<Type> singletonList(NLIST_FF.makeProductType(
						BOOL_TYPE_ffLIST, BOOL_TYPE_ffLIST)), NEXT_LIST);
		final ExtendedExpression nilBoolBool = NLIST_FF.makeExtendedExpression(
				NEXT_NIL, NO_EXPR, NO_PRED, null, listBoolBoolType);

		doExpressionTest("(nil ⦂ List(BOOL×BOOL))", nilBoolBool,
				listBoolBoolType, NLIST_FF, false);

		assertFalse(nil.equals(nilInt));
		assertFalse(nil.equals(nilBoolBool));
		assertFalse(nilBoolBool.equals(nilInt));
	}

	@Test
	public void testDatatypeNilInvalidType() throws Exception {
		final IParseResult result = NLIST_FF.parseExpression("(nil ⦂ ℤ)", null);
		assertFailure(result, new ASTProblem(new SourceLocation(1, 7),
				InvalidGenericType, Error, "[see operator definition]"));
	}

	@Test
	public void testDatatypeConstructor() throws Exception {

		final ExtendedExpression nil = NLIST_FF.makeExtendedExpression(
				NEXT_NIL, NO_EXPR, NO_PRED, null);

		final ExtendedExpression list1 = NLIST_FF.makeExtendedExpression(
				NEXT_CONS, Arrays.asList(ONE_ffLIST, nil),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("cons(1, nil)", list1, NLIST_INT_TYPE, NLIST_FF, true);

		final ExtendedExpression list01 = NLIST_FF.makeExtendedExpression(
				NEXT_CONS,
				Arrays.asList(
						ZERO_ffLIST,
						NLIST_FF.makeExtendedExpression(NEXT_CONS,
								Arrays.asList(ONE_ffLIST, nil),
								Collections.<Predicate> emptyList(), null)),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("cons(0, cons(1, nil))", list01, NLIST_INT_TYPE,
				NLIST_FF, true);

		assertFalse(list1.equals(list01));
	}

	@Test
	public void testDatatypeDestructors() throws Exception {
		assertNotNull("head destructor not found", NEXT_HEAD);

		assertNotNull("tail destructor not found", NEXT_TAIL);

		final ExtendedExpression head = NLIST_FF.makeExtendedExpression(
				NEXT_HEAD, Arrays.<Expression> asList(FRID_x_ffLIST),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("head(x)", head, NLIST_FF);

		final ExtendedExpression tail = NLIST_FF.makeExtendedExpression(
				NEXT_TAIL, Arrays.<Expression> asList(FRID_x_ffLIST),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("tail(x)", tail, NLIST_FF);
	}

	@Test
	public void testTypeConstrTypeCheck() throws Exception {
		final Expression listIntExpr = NLIST_FF.makeExtendedExpression(
				NEXT_LIST, Collections.<Expression> singleton(INT_ffLIST),
				Collections.<Predicate> emptySet(), null);
		final Predicate expected = NLIST_FF.makeRelationalPredicate(IN,
				NLIST_FF.makeFreeIdentifier("x", null), listIntExpr, null);

		final Predicate pred = doPredicateTest("x ∈ List(ℤ)", expected,
				NLIST_FF);
		final ITypeCheckResult tcResult = pred.typeCheck(NLIST_FF
				.makeTypeEnvironment());
		assertFalse(tcResult.hasProblem());
		assertTrue(pred.isTypeChecked());
		final FreeIdentifier[] freeIdentifiers = pred.getFreeIdentifiers();
		assertEquals(1, freeIdentifiers.length);
		final FreeIdentifier x = freeIdentifiers[0];
		assertEquals(NLIST_INT_TYPE, x.getType());
	}

	@Test
	public void testTypeCheckError() throws Exception {
		// problem raised by Issam, produced a StackOverflowError
		final Expression A_Id = NLIST_FF.makeFreeIdentifier("A", null);

		final Expression List_A = NLIST_FF.makeExtendedExpression(NEXT_LIST,
				asList(A_Id), Collections.<Predicate> emptySet(), null);
		final Expression List_List_A = NLIST_FF.makeExtendedExpression(
				NEXT_LIST, asList(List_A), Collections.<Predicate> emptySet(),
				null);

		final BoundIdentDecl bid_x = NLIST_FF.makeBoundIdentDecl("x", null);
		final BoundIdentDecl bid_y = NLIST_FF.makeBoundIdentDecl("y", null);
		final BoundIdentifier bi_x = NLIST_FF.makeBoundIdentifier(1, null);
		final BoundIdentifier bi_y = NLIST_FF.makeBoundIdentifier(0, null);

		final Predicate x_In_A = NLIST_FF.makeRelationalPredicate(IN, bi_x,
				A_Id, null);

		final Predicate y_In_ListListA = NLIST_FF.makeRelationalPredicate(IN,
				bi_y, List_List_A, null);

		final ExtendedExpression cons_x_y = NLIST_FF.makeExtendedExpression(
				NEXT_CONS, new Expression[] { bi_x, bi_y }, NO_PRED, null);
		final Predicate cons_In_ListA = NLIST_FF.makeRelationalPredicate(IN,
				cons_x_y, List_A, null);

		final Predicate expected = NLIST_FF.makeQuantifiedPredicate(FORALL,
				asList(bid_x, bid_y), NLIST_FF.makeBinaryPredicate(
						LIMP,
						NLIST_FF.makeAssociativePredicate(LAND,
								asList(x_In_A, y_In_ListListA), null),
						cons_In_ListA, null), null);
		final Predicate pred = doPredicateTest(
				"∀ x,y· (x ∈A ∧ y ∈List(List(A))) ⇒ cons(x,y)∈ List(A)",
				expected, NLIST_FF);
		final ITypeCheckResult tcRes = pred.typeCheck(NLIST_FF
				.makeTypeEnvironment());
		assertTrue(tcRes.hasProblem());

		final List<ASTProblem> problems = tcRes.getProblems();
		for (ASTProblem problem : problems) {
			assertEquals(ProblemKind.Circularity, problem.getMessage());
		}
	}

	@Test
	public void testDatatypeDestructorsTyping() throws Exception {

		final ExtendedExpression nil = NLIST_FF.makeExtendedExpression(
				NEXT_NIL, Collections.<Expression> emptyList(),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression list1 = NLIST_FF.makeExtendedExpression(
				NEXT_CONS, Arrays.asList(ONE_ffLIST, nil),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression headList1 = NLIST_FF.makeExtendedExpression(
				NEXT_HEAD, Arrays.<Expression> asList(list1),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("head(cons(1, nil))", headList1, INT_TYPE, NLIST_FF,
				true);

		final ExtendedExpression tail = NLIST_FF.makeExtendedExpression(
				NEXT_TAIL, Arrays.<Expression> asList(list1),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("tail(cons(1, nil))", tail, NLIST_INT_TYPE, NLIST_FF,
				true);
	}

	@Test
	public void testListOfLists() throws Exception {
		final ExtendedExpression nil = NLIST_FF.makeExtendedExpression(
				NEXT_NIL, Collections.<Expression> emptyList(),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression nilInt = NLIST_FF.makeExtendedExpression(
				NEXT_NIL, NO_EXPR, NO_PRED, null, NLIST_INT_TYPE);

		final ExtendedExpression listNilNil = NLIST_FF.makeExtendedExpression(
				NEXT_CONS, Arrays.<Expression> asList(nilInt, nil),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression headListNil = NLIST_FF.makeExtendedExpression(
				NEXT_HEAD, Arrays.<Expression> asList(listNilNil),
				Collections.<Predicate> emptyList(), null);

		doExpressionTest("head(cons((nil ⦂ List(ℤ)), nil))", headListNil,
				NLIST_INT_TYPE, NLIST_FF, true);

		final ExtendedExpression cons1 = NLIST_FF.makeExtendedExpression(
				NEXT_CONS, Arrays.asList(ONE_ffLIST, nil),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression consCons1 = NLIST_FF.makeExtendedExpression(
				NEXT_CONS, Arrays.<Expression> asList(cons1, nil),
				Collections.<Predicate> emptyList(), null);

		final ExtendedExpression tailConsCons1 = NLIST_FF
				.makeExtendedExpression(NEXT_TAIL,
						Arrays.<Expression> asList(consCons1),
						Collections.<Predicate> emptyList(), null);

		final ParametricType NLIST_LIST_INT_TYPE = NLIST_FF.makeParametricType(
				Arrays.<Type> asList(NLIST_INT_TYPE), NEXT_LIST);

		doExpressionTest("tail(cons(cons(1, nil), nil))", tailConsCons1,
				NLIST_LIST_INT_TYPE, NLIST_FF, true);
	}

	@Test
	public void testDatatypeOrigins() throws Exception {
		for (IFormulaExtension extension : NLIST_DT.getExtensions()) {
			final Object origin = extension.getOrigin();
			assertSame("wrong origin for " + extension.getId(), NLIST_DT,
					origin);
		}
	}

	@Test
	public void testDTbuilder() {
		final GivenType tyList3 = ff.makeGivenType("List3");
		final GivenType tyS = ff.makeGivenType("S");
		final GivenType tyT = ff.makeGivenType("T");
		final GivenType tyU = ff.makeGivenType("U");
		final IDatatypeBuilder dtBuilder = ff.makeDatatypeBuilder("List3", tyS,
				tyT, tyU);
		dtBuilder.addConstructor("nil");
		final IConstructorBuilder cons = dtBuilder.addConstructor("cons");
		cons.addArgument(tyS, "head1");
		cons.addArgument(tyT, "head2");
		cons.addArgument(tyU, "head3");
		cons.addArgument(tyList3, "tail");
		dtBuilder.finalizeDatatype();
	}

	/* Copy of TestTypeChecker tests begin */

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
		final IDatatype2 datatype = DatatypeParser
				.parse(initial, datatypeImage);
		final Set<IFormulaExtension> exts = initial.getExtensions();
		exts.addAll(datatype.getExtensions());
		return FormulaFactory.getInstance(exts);
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
		IdentsChecker.check(formula);
	}

	private Predicate testPredicate(String image, ITypeEnvironment initialEnv,
			ITypeEnvironment finalEnv) {
		final FormulaFactory factory = initialEnv.getFormulaFactory();
		final Predicate formula = parsePredicate(image, factory);
		doTest(formula, initialEnv, finalEnv, image);
		return formula;
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

	/* Copy of TestTypeChecker end */

}
