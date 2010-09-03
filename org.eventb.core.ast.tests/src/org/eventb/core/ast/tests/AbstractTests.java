/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.LanguageVersion.LATEST;
import junit.framework.TestCase;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;

/**
 * Base abstract class for AST tests.
 *
 * @author Laurent Voisin
 */
public abstract class AbstractTests extends TestCase {

	public static final FormulaFactory ff = FormulaFactory.getDefault();

	public static void assertSuccess(String message, IResult result) {
		assertTrue(message, result.isSuccess());
		assertFalse(message, result.hasProblem());
	}

	public static void assertFailure(String message, IResult result) {
		assertFalse(message, result.isSuccess());
		assertTrue(message, result.hasProblem());
	}

	private static String makeFailMessage(String image, IParseResult result) {
		return "Parse failed for " + image + " (parser "
				+ result.getLanguageVersion() + "): " + result.getProblems();
	}

	public static Expression parseExpression(String image) {
		return parseExpression(image, LATEST);
	}
	
	public static Expression parseExpression(String image,
			FormulaFactory factory) {
		return parseExpression(image, LATEST, factory);
	}
	
	public static Expression parseExpression(String image,
			LanguageVersion version) {
		return parseExpression(image, version, ff);
	}
	
	public static Expression parseExpression(String image,
			LanguageVersion version, FormulaFactory factory) {
		final IParseResult result;
		if (image.contains(PredicateVariable.LEADING_SYMBOL)) {
			result = factory.parseExpressionPattern(image, version, null);
		} else {
			result = factory.parseExpression(image, version, null);
		}
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedExpression();
	}

	public static Predicate parsePredicate(String image) {
		return parsePredicate(image, LATEST);
	}

	public static Predicate parsePredicate(String image, FormulaFactory factory) {
		return parsePredicate(image, LATEST, factory);
	}
	
	public static Predicate parsePredicate(String image,
			LanguageVersion version) {
		return parsePredicate(image, version, ff);
	}
	
	public static Predicate parsePredicate(String image,
			LanguageVersion version, FormulaFactory factory) {
		final IParseResult result;
		if (image.contains(PredicateVariable.LEADING_SYMBOL)) {
			result = factory.parsePredicatePattern(image, version, null);
		} else {
			result = factory.parsePredicate(image, version, null);
		}
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedPredicate();
	}
	
	public static Assignment parseAssignment(String image) {
		return parseAssignment(image, LATEST);
	}
		
	public static Assignment parseAssignment(String image,
			FormulaFactory factory) {
		return parseAssignment(image, LATEST, factory);
	}
	
	public static Assignment parseAssignment(String image,
			LanguageVersion version) {
		return parseAssignment(image, version, ff);
	}
	
	public static Assignment parseAssignment(String image,
			LanguageVersion version, FormulaFactory factory) {
		final IParseResult result = factory.parseAssignment(image, version,
				null);
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedAssignment();
	}

	public static Type parseType(String image) {
		final IParseResult result = ff.parseType(image, LATEST);
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
	public static ITypeEnvironment typeCheck(Formula<?> formula,
			ITypeEnvironment tenv) {
		if (tenv == null) {
			tenv = ff.makeTypeEnvironment();
		}
		final ITypeCheckResult result = formula.typeCheck(tenv);
		assertSuccess(formula.toString(), result);
		assertTrue(formula.isTypeChecked());

		final ITypeEnvironment newEnv = tenv.clone();
		newEnv.addAll(result.getInferredEnvironment());
		return newEnv;
	}
	
	public static ITypeEnvironment typeCheck(Formula<?> formula) {
		return typeCheck(formula, ff.makeTypeEnvironment());
	}

	protected static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	protected static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}

	protected static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}

}
