/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.datatype.IDestructorExtension;
import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Base abstract class for AST tests.
 *
 * @author Laurent Voisin
 */
public abstract class AbstractTests {

	// Default formula factory for the last language version, without extension
	public static final FormulaFactory ff = FormulaFactory.getDefault();

	// Formula factory for the old V1 language
	public static final FormulaFactory ffV1 = FormulaFactory.getV1Default();

	// Default formula factories for V1 and V2 languages versions
	protected static final FormulaFactory[] ALL_VERSION_FACTORIES = { ffV1, ff };

	// Utility arrays for building extended formulas
	public static final Expression[] NO_EXPRS = new Expression[0];
	public static final Predicate[] NO_PREDS = new Predicate[0];

	public static final FreeIdentifier[] NO_IDS = new FreeIdentifier[0];
	public static final BoundIdentDecl[] NO_BIDS = new BoundIdentDecl[0];

	protected static final BooleanType BOOL_TYPE = ff.makeBooleanType();
	protected static final IntegerType INT_TYPE = ff.makeIntegerType();

	protected static final IDatatype LIST_DT;
	public static final FormulaFactory LIST_FAC;

	static {
		final GivenType tyList = ff.makeGivenType("List");
		final GivenType tyS = ff.makeGivenType("S");
		final IDatatypeBuilder dtBuilder = ff.makeDatatypeBuilder("List", tyS);
		dtBuilder.addConstructor("nil");
		final IConstructorBuilder cons = dtBuilder.addConstructor("cons");
		cons.addArgument("head", tyS);
		cons.addArgument("tail", tyList);
		LIST_DT = dtBuilder.finalizeDatatype();
		LIST_FAC = FormulaFactory.getInstance(LIST_DT.getExtensions());
	}


	protected static final IExpressionExtension EXT_LIST = LIST_DT
			.getTypeConstructor();

	protected static final ParametricType LIST_INT_TYPE = LIST_FAC
			.makeParametricType(Collections.<Type> singletonList(LIST_FAC
					.makeIntegerType()), EXT_LIST);
	protected static final PowerSetType POW_LIST_INT_TYPE = LIST_FAC
			.makePowerSetType(LIST_INT_TYPE);
	protected static final IConstructorExtension EXT_NIL = LIST_DT
			.getConstructor("nil");
	protected static final IConstructorExtension EXT_CONS = LIST_DT
			.getConstructor("cons");
	protected static final IDestructorExtension EXT_HEAD = EXT_CONS
			.getDestructor("head");
	protected static final IDestructorExtension EXT_TAIL = EXT_CONS
			.getDestructor("tail");

	
	public static void assertSuccess(String message, IResult result) {
		if (!result.isSuccess() || result.hasProblem()) {
			fail(message + result.getProblems());
		}
	}

	public static void assertFailure(String message, IResult result) {
		assertFalse(message, result.isSuccess());
		assertTrue(message, result.hasProblem());
	}

	private static String makeFailMessage(String image, IParseResult result) {
		return "Parse failed for " + image + " (parser "
				+ result.getFormulaFactory() + "): " + result.getProblems();
	}

	public static Expression parseExpression(String image) {
		return parseExpression(image, ff);
	}
	
	public static Expression parseExpression(String image,
			ITypeEnvironment typenv) {
		final FormulaFactory fac = typenv.getFormulaFactory();
		final Expression result = parseExpression(image, fac);
		typeCheck(result, typenv);
		return result;
	}

	public static Expression parseExpression(String image, FormulaFactory factory) {
		final IParseResult result;
		if (image.contains(PredicateVariable.LEADING_SYMBOL)) {
			result = factory.parseExpressionPattern(image, null);
		} else {
			result = factory.parseExpression(image, null);
		}
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedExpression();
	}

	public static Predicate parsePredicate(String image) {
		return parsePredicate(image, ff);
	}

	public static Predicate parsePredicate(String image,
			ITypeEnvironment typenv) {
		final FormulaFactory fac = typenv.getFormulaFactory();
		final Predicate result = parsePredicate(image, fac);
		typeCheck(result, typenv);
		return result;
	}
	
	public static Predicate parsePredicate(String image, FormulaFactory factory) {
		final IParseResult result;
		if (image.contains(PredicateVariable.LEADING_SYMBOL)) {
			result = factory.parsePredicatePattern(image, null);
		} else {
			result = factory.parsePredicate(image, null);
		}
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedPredicate();
	}
	
	public static Assignment parseAssignment(String image) {
		return parseAssignment(image, ff);
	}
		
	public static Assignment parseAssignment(String image,
			ITypeEnvironment typenv) {
		final FormulaFactory fac = typenv.getFormulaFactory();
		final Assignment result = parseAssignment(image, fac);
		typeCheck(result, typenv);
		return result;
	}

	public static Assignment parseAssignment(String image,
			FormulaFactory factory) {
		final IParseResult result = factory.parseAssignment(image, null);
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedAssignment();
	}

	public static Type parseType(String image) {
		return parseType(image, ff);
	}

	public static Type parseType(String image, FormulaFactory factory) {
		final IParseResult result = factory.parseType(image);
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedType();
	}

	/**
	 * Type-checks the given formula and returns the type environment made of the
	 * given type environment and the type environment inferred during
	 * type-check.
	 * 
	 * @param formula
	 *            a formula to type-check
	 * @param tenv
	 *            initial type environment
	 * @return augmented type environment
	 */
	public static ITypeEnvironmentBuilder typeCheck(Formula<?> formula,
			ITypeEnvironment tenv) {
		final ITypeCheckResult result = formula.typeCheck(tenv);
		assertSuccess(formula.toString(), result);
		assertTrue(formula.isTypeChecked());

		final ITypeEnvironmentBuilder newEnv = tenv.makeBuilder();
		newEnv.addAll(result.getInferredEnvironment());
		return newEnv;
	}
	
	public static ITypeEnvironment typeCheck(Formula<?> formula) {
		return typeCheck(formula, formula.getFactory().makeTypeEnvironment());
	}

	public static Type POW(Type base) {
		return base.getFactory().makePowerSetType(base);
	}

	public static Type CPROD(Type left, Type right) {
		return left.getFactory().makeProductType(left, right);
	}

	public static Type REL(Type left, Type right) {
		return left.getFactory().makeRelationalType(left, right);
	}

}
