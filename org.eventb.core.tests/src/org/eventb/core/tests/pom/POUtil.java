/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.rodinp.core.RodinDBException;

/**
 * Utility methods for writing a PO file.
 * 
 * @author halstefa
 * @author Laurent Voisin
 */
public class POUtil {
	
	// Default formula factory for the last language version, without extension
	public static final FormulaFactory ff = FormulaFactory.getDefault();

	// TODO: code between START COPY and END COPY was copied from
	// org.eventb.core.ast.tests: FastFactory and AbstractTests. We must isolate
	// part useful for all core tests projects and create a dedicated class.
	
	// START COPY

	// Formula factory for the old V1 language
	public static final FormulaFactory ffV1 = FormulaFactory.getV1Default();


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

	public static Expression parseExpression(String image,
			FormulaFactory factory) {
		final IParseResult result;
		if (image.contains(PredicateVariable.LEADING_SYMBOL)) {
			result = factory.parseExpressionPattern(image, null);
		} else {
			result = factory.parseExpression(image, null);
		}
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedExpression();
	}

	public static Type parseType(String image, FormulaFactory factory) {
		final IParseResult result = factory.parseType(image);
		assertSuccess(makeFailMessage(image, result), result);
		return result.getParsedType();
	}

	private static final Pattern typenvPairSeparator = Pattern.compile(";");
	private static final Pattern typenvPairPattern = Pattern
			.compile("^([^=]*)=([^=]*)$");
	
	
	public static ITypeEnvironmentBuilder addToTypeEnvironment(
			ITypeEnvironmentBuilder typeEnv, String typeEnvImage){
		if (typeEnvImage.length() == 0) {
			return typeEnv;
		}
		FormulaFactory factory = typeEnv.getFormulaFactory();
		for (final String pairImage : typenvPairSeparator.split(typeEnvImage)) {
			final Matcher m = typenvPairPattern.matcher(pairImage);
			if (!m.matches()) {
				throw new IllegalArgumentException(
						"Invalid type environment pair: " + pairImage);
			}
			final Expression expr = parseExpression(m.group(1), factory);
			if (expr.getTag() != FREE_IDENT) {
				throw new IllegalArgumentException(
						"Invalid type environment pair: " + pairImage);
			}
			final Type type = parseType(m.group(2), factory);
			typeEnv.addName(expr.toString(), type);
		}
		return typeEnv;
	}
	
	/**
	 * Generates the type environment specified by the given string. The string
	 * contains pairs of form <code>ident=type</code> separated by semicolons.
	 * <p>
	 * Example of valid parameters are:
	 * <ul>
	 * <li><code>""</code></li>
	 * <li><code>"x=S"</code></li>
	 * <li><code>"x=S; y=T"</code></li>
	 * <li><code>"x=S; r=S↔S"</code></li>
	 * </ul>
	 * </p>
	 * 
	 * @param typeEnvImage
	 *            image of the type environment to generate
	 * @param factory
	 *            the formula factory to use for building the result
	 * @return the type environment described by the given string
	 */
	public static ITypeEnvironmentBuilder mTypeEnvironment(String typeEnvImage,
			FormulaFactory factory) {
		final ITypeEnvironmentBuilder result = factory.makeTypeEnvironment();
		addToTypeEnvironment(result, typeEnvImage);
		return result;
	}
	
	public static ITypeEnvironmentBuilder mTypeEnvironment(String typeEnvImage) {
		final ITypeEnvironmentBuilder result = ff.makeTypeEnvironment();
		addToTypeEnvironment(result, typeEnvImage);
		return result;
	}
	
	public static ITypeEnvironmentBuilder mTypeEnvironment() {
		return ff.makeTypeEnvironment();
	}

	// END COPY
	
	/**
	 * Adds a predicate set to the given PO file, using the given contents.
	 * 
	 * @param poRoot
	 *            file in which to add the predicate set
	 * @param setName
	 *            name of the set
	 * @param parentSet
	 *            parent set (may be <code>null</code>)
	 * @param typEnv
	 *            type environment for the set
	 * @param predStrings
	 *            predicates of the set as strings
	 * @return a handle to the created set
	 * @throws RodinDBException
	 */
	public static IPOPredicateSet addPredicateSet(IPORoot poRoot, String setName,
			IPOPredicateSet parentSet, ITypeEnvironment typEnv,
			String... predStrings) throws RodinDBException {

		IPOPredicateSet poSet = poRoot.getPredicateSet(setName);
		createPredicateSet(poSet, parentSet, typEnv, predStrings);
		return poSet;
	}

	/**
	 * Adds a PO to the given PO file with the supplied information.
	 * 
	 * @param poRoot
	 *            file where to create the PO
	 * @param poName
	 *            name of the PO
	 * @param goalString
	 *            goal of the PO
	 * @param globalSet
	 *            handle to the set of global hypotheses
	 * @param typEnv
	 *            local type environment
	 * @param localHypStrings
	 *            local hypotheses as strings
	 * @throws RodinDBException
	 */
	public static void addSequent(IPORoot poRoot, String poName, long poStamp,
			String goalString, IPOPredicateSet globalSet, ITypeEnvironment typEnv,
			String... localHypStrings) throws RodinDBException {
		
		IPOSequent poSeq = poRoot.getSequent(poName);
		poSeq.create(null, null);
		poSeq.setPOStamp(poStamp, null);
		IPOPredicate poGoal = poSeq.getGoal("goal");
		poGoal.create(null, null);
		poGoal.setPredicateString(goalString, null);
		
		IPOPredicateSet poSet = poSeq.getHypothesis("local");
		createPredicateSet(poSet, globalSet, typEnv, localHypStrings);
	}
	
	/**
	 * Adds a PO to the given PO file with the supplied information.
	 * 
	 * @param poRoot
	 *            file where to create the PO
	 * @param poName
	 *            name of the PO
	 * @param goalString
	 *            goal of the PO
	 * @param globalSet
	 *            handle to the set of global hypotheses
	 * @param typEnv
	 *            local type environment
	 * @param localHypStrings
	 *            local hypotheses as strings
	 * @throws RodinDBException
	 */
	public static void addSequent(IPORoot poRoot, String poName,
			String goalString, IPOPredicateSet globalSet, ITypeEnvironment typEnv,
			String... localHypStrings) throws RodinDBException {
		addSequent(poRoot, poName, IPOStampedElement.INIT_STAMP, goalString, globalSet, typEnv, localHypStrings);
	}


	/**
	 * Creates and populates the given predicate set with the supplied information.
	 * 
	 * @param poSet
	 *            predicate set to create and populate
	 * @param parentSet
	 *            parent set (may be <code>null</code>)
	 * @param typEnv
	 *            type environment for the set
	 * @param predStrings
	 *            predicates of the set as strings
	 * @throws RodinDBException
	 */
	private static void createPredicateSet(IPOPredicateSet poSet,
			IPOPredicateSet parentSet, ITypeEnvironment typEnv,
			String... predStrings) throws RodinDBException,
			NoSuchElementException {

		poSet.create(null, null);
		if (parentSet != null) {
			poSet.setParentPredicateSet(parentSet, null);
		}
		ITypeEnvironment.IIterator iter = typEnv.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			IPOIdentifier poIdent = poSet.getIdentifier(iter.getName());
			poIdent.create(null, null);
			poIdent.setType(iter.getType(), null);
		}
		int idx = 1;
		for (String predString: predStrings) {
			IPOPredicate poPred = poSet.getPredicate("p" + idx++);
			poPred.create(null, null);
			poPred.setPredicateString(predString, null);
		}
	}
	
}
