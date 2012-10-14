/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
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
import static org.eventb.core.ast.LanguageVersion.V1;
import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * Base abstract class for AST tests.
 *
 * @author Laurent Voisin
 */
public abstract class AbstractTests extends TestCase {

	// Default formula factory for the last language version, without extension
	public static final FormulaFactory ff = FormulaFactory.getDefault();

	// Formula factory for the old V1 language
	public static final FormulaFactory ffV1 = FormulaFactory.getV1Default();

	protected static final IntegerType INT_TYPE = ff.makeIntegerType();
	private static final IDatatypeExtension LIST_TYPE = new IDatatypeExtension() {

		private static final String TYPE_NAME = "List";
		private static final String TYPE_IDENTIFIER = "org.eventb.core.ast.tests.list";

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
		}

		@Override
		public void addConstructors(IConstructorMediator mediator) {
			mediator.addConstructor("nil", "NIL");
			final ITypeParameter typeS = mediator.getTypeParameter("S");

			final IArgumentType refS = mediator.newArgumentType(typeS);
			final IArgument head = mediator.newArgument("head", refS);
			final IExpressionExtension typeCons = mediator.getTypeConstructor();
			final IArgumentType listS = mediator.makeParametricType(typeCons,
					asList(refS));
			final IArgument tail = mediator.newArgument("tail", listS);

			mediator.addConstructor("cons", "CONS", Arrays.asList(head, tail));
		}

	};
	protected static final IDatatype LIST_DT = ff.makeDatatype(LIST_TYPE);
	protected static final FormulaFactory LIST_FAC = FormulaFactory
				.getInstance(LIST_DT.getExtensions());
	protected static final IExpressionExtension EXT_LIST = LIST_DT
				.getTypeConstructor();
	protected static final ParametricType LIST_INT_TYPE = LIST_FAC
				.makeParametricType(Collections.<Type> singletonList(INT_TYPE),
						EXT_LIST);
	protected static final PowerSetType POW_LIST_INT_TYPE = LIST_FAC
				.makePowerSetType(LIST_INT_TYPE);
	protected static final IExpressionExtension EXT_NIL = LIST_DT
				.getConstructor("NIL");
	protected static final IExpressionExtension EXT_CONS = LIST_DT
				.getConstructor("CONS");
	protected static final IExpressionExtension EXT_HEAD = LIST_DT.getDestructor(
				"CONS", 0);
	protected static final IExpressionExtension EXT_TAIL = LIST_DT.getDestructor(
				"CONS", 1);
	
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
				+ result.getLanguageVersion() + "): " + result.getProblems();
	}

	public static Expression parseExpression(String image) {
		return parseExpression(image, ff);
	}
	
	public static Expression parseExpression(String image,
			FormulaFactory factory) {
		return parseExpression(image, getLanguageVersion(factory), factory);
	}
	
	public static Expression parseExpression(String image,
			ITypeEnvironment typenv) {
		final FormulaFactory fac = typenv.getFormulaFactory();
		final Expression result = parseExpression(image, fac);
		typeCheck(result, typenv);
		return result;
	}

	public static Expression parseExpression(String image,
			LanguageVersion version) {
		return parseExpression(image, version, getFormulaFactory(version));
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
		return parsePredicate(image, FormulaFactory.getDefault());
	}

	public static Predicate parsePredicate(String image, FormulaFactory factory) {
		return parsePredicate(image, getLanguageVersion(factory), factory);
	}
	
	public static Predicate parsePredicate(String image,
			ITypeEnvironment typenv) {
		final FormulaFactory fac = typenv.getFormulaFactory();
		final Predicate result = parsePredicate(image, fac);
		typeCheck(result, typenv);
		return result;
	}

	public static Predicate parsePredicate(String image,
			LanguageVersion version) {
		return parsePredicate(image, version, getFormulaFactory(version));
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
		return parseAssignment(image, ff);
	}
		
	public static Assignment parseAssignment(String image,
			FormulaFactory factory) {
		return parseAssignment(image, getLanguageVersion(factory), factory);
	}
	
	public static Assignment parseAssignment(String image,
			LanguageVersion version) {
		return parseAssignment(image, version, getFormulaFactory(version));
	}
	
	public static Assignment parseAssignment(String image,
			LanguageVersion version, FormulaFactory factory) {
		final IParseResult result = factory.parseAssignment(image, version,
				null);
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedAssignment();
	}

	public static Type parseType(String image) {
		return parseType(image, ff);
	}

	public static Type parseType(String image, FormulaFactory factory) {
		final LanguageVersion version = getLanguageVersion(factory);
		final IParseResult result = factory.parseType(image, version);
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedType();
	}

	private static LanguageVersion getLanguageVersion(FormulaFactory factory) {
		final LanguageVersion version = factory == ffV1 ? V1 : V2;
		return version;
	}
	
	private static FormulaFactory getFormulaFactory(LanguageVersion version) {
		switch (version) {
		case V1:
			return ffV1;
		case V2:
			return ff;
		default:
			assert false;
			return null;
		}
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
