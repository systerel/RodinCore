/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added genPred() and genPreds() with a type environment
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;

/**
 * This is a collection of static methods for conveniently constructing objects used for
 * testing using their string counterparts. 
 * 
 * @author Farhad Mehta
 *
 * TODO : At the moment there are two copies of this file (in org.eventb.core.tests(.pom), and
 *  org.eventb.core.seqprover.tests). Find a way to use ony one copy.
 *
 */
public class TestLib {
	
	public static final FormulaFactory ff = FormulaFactory.getDefault();
	

	/**
	 * Constructs a simple sequent (only with selected hypotheses and a goal) from
	 * a string of the form "shyp1 ;; shyp2 ;; .. ;; shypn |- goal"
	 * 
	 * <p>
	 * The type environment of the sequent should be inferrable from the predicates in
	 * the order in which they appear (eg. "x+1=y ;; x=y |- x/=0" is fine, but
	 * "x=y ;; x+1=y |- x/=0" is not since "x=y" cannot be typechecked alone)
	 * </p>
	 * 
	 * This method is used to easily construct sequents for test cases.
	 * 
	 * @param sequentAsString
	 * 			The sequent as a string
	 * @return
	 * 			The resulting sequent
	 * @throws IllegalArgumentException
	 * 		 in case the sequent could not be constructed due to a parsing or typechecking error.
	 */
	public static IProverSequent genSeq(String sequentAsString, FormulaFactory factory) {
		final String[] split = sequentAsString.split("\\|-");
		return genFullSeq("", "", "", split[0], split[1], factory);
	}
	
	public static IProverSequent genSeq(String sequentAsString) {
		return genSeq(sequentAsString, ff);
	}
	
	private static final Pattern fullSequentPattern = Pattern
			.compile("^(.*);H;(.*);S;(.*)\\s*\\|-\\s*(.*)$");	
	
	public static IProverSequent genFullSeq(String sequentAsString) {
		return genFullSeq(sequentAsString, ff);
	}
	
	/**
	 * Constructs a sequent from a string of the form " Hyps ;H; Hyps ;S; Hyps |-
	 * goal" where the hypothesis list Hyps is of the form " hyp ;; hyp ;; ... ;;
	 * hyp ". (If the list is empty, Hyps should be at least as contain one blank space)
	 * <p>
	 * The first list of hypotheses is the list of all hypotheses. The second
	 * list (after ;H;) is the list of hidden hypotheses. The third list (after
	 * ;S;) is the list of selected hypotheses. The order of hypotheses in the
	 * list are kept. Ignoring order, the set of hidden hypotheses and selected
	 * hypotheses should be disjoint and both are subset of the set of all
	 * hypotheses.
	 * </p>
	 * <p>
	 * The type environment of the sequent should be inferrable from the
	 * predicates in the order in which they appear in the global set of
	 * hypotheses (e.g. "x+1=y ;; x=y " is fine, but "x=y ;; x+1=y " is not
	 * since "x=y" cannot be type-checked alone)
	 * </p>
	 * 
	 * This method is used to easily construct sequents for test cases.
	 * 
	 * @param sequentAsString
	 *            the sequent as a string
	 * @return the resulting sequent
	 * @throws IllegalArgumentException
	 *             in case the sequent could not be constructed due to a parsing
	 *             or type-checking error.
	 * @author htson
	 */
	public static IProverSequent genFullSeq(String sequentStr,
			FormulaFactory factory) {
		return genFullSeq(sequentStr, factory.makeTypeEnvironment());
	}

	public static IProverSequent genFullSeq(String sequentAsString,
			ITypeEnvironmentBuilder typenv) {
		final Matcher m = fullSequentPattern.matcher(sequentAsString);
		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid sequent: "
					+ sequentAsString);
		}
		final Set<Predicate> globalHyps = genPredSet(typenv, m.group(1));
		final Set<Predicate> hiddenHyps = genPredSet(typenv, m.group(2));
		final Set<Predicate> selectHyps = genPredSet(typenv, m.group(3));
		final Predicate goal = genPred(typenv, m.group(4));
		
		globalHyps.addAll(hiddenHyps);
		globalHyps.addAll(selectHyps);

		return ProverFactory.makeSequent(typenv, globalHyps, hiddenHyps,
				selectHyps, goal);
	}
	
	public static IProofTreeNode genProofTreeNode(String str){
		return ProverFactory.makeProofTree(genSeq(str), null).getRoot();
	}
		
	/**
	 * Builds a sequent from several strings detailing its type environment,
	 * hypotheses and goal. Sets of hypotheses are given as one string
	 * concatenating all hypotheses of the set separated by <code>";;"</code>.
	 * <p>
	 * This method is used to easily construct sequents for test cases.
	 * </p>
	 * 
	 * @param typenv
	 *            type environment
	 * @param hiddenHypsImage
	 *            hidden hypothesis
	 * @param defaultHypsImage
	 *            visible but not selected hypotheses
	 * @param selHypsImage
	 *            selected hypotheses
	 * @param goalImage
	 *            goal
	 * @return the sequent build from the given parameters
	 * @author Thomas Muller
	 */
	public static IProverSequent genFullSeq(ITypeEnvironment typenv,
			String hiddenHypsImage, String defaultHypsImage,
			String selHypsImage, String goalImage) {
		final ITypeEnvironmentBuilder te = typenv.makeBuilder();
		final Set<Predicate> hiddenHyps = genPredSet(te, hiddenHypsImage);
		final Set<Predicate> defaultHyps = genPredSet(te, defaultHypsImage);
		final Set<Predicate> selectedHyps = genPredSet(te, selHypsImage);
		final Predicate goal = genPred(te, goalImage);
		return genFullSeq(te, hiddenHyps, defaultHyps, selectedHyps, goal);
	}

	public static IProverSequent genFullSeq(String typeEnvImage,
			String hiddenHypsImage, String defaultHypsImage,
			String selHypsImage, String goalImage) {
		return genFullSeq(typeEnvImage, hiddenHypsImage, defaultHypsImage,
				selHypsImage, goalImage, ff);
	}
	
	/**
	 * Builds a sequent from several strings detailing its type environment,
	 * hypotheses and goal. Sets of hypotheses are given as one string
	 * concatenating all hypotheses of the set separated by <code>";;"</code>.
	 * <p>
	 * This method is used to easily construct sequents for test cases.
	 * </p>
	 * 
	 * @param typeEnvImage
	 *            type environment in string form, should look like "x=S,y=ℙ(ℤ)"
	 * @param hiddenHypsImage
	 *            hidden hypothesis
	 * @param defaultHypsImage
	 *            visible but not selected hypotheses
	 * @param selHypsImage
	 *            selected hypotheses
	 * @param goalImage
	 *            goal
	 * @return the sequent build from the given parameters
	 * @author Thomas Muller
	 */
	public static IProverSequent genFullSeq(String typeEnvImage,
			String hiddenHypsImage, String defaultHypsImage,
			String selHypsImage, String goalImage, FormulaFactory factory) {

		final ITypeEnvironmentBuilder typenv = mTypeEnvironment(typeEnvImage, factory);
		return genFullSeq(typenv, hiddenHypsImage, defaultHypsImage, selHypsImage, goalImage);
	}

	/**
	 * Creates a new proof sequent.
	 * 
	 * @param typenv
	 *            type environment
	 * @param hiddenHyps
	 *            hidden hypotheses
	 * @param defaultHyps
	 *            visible hypotheses that are not selected
	 * @param selectedHyps
	 *            selected hypotheses
	 * @param goal
	 *            goal
	 * @return a new proof sequent
	 */
	public static IProverSequent genFullSeq(final ITypeEnvironment typenv,
			final Set<Predicate> hiddenHyps, final Set<Predicate> defaultHyps,
			final Set<Predicate> selectedHyps, final Predicate goal) {
		final Set<Predicate> globalHyps = new LinkedHashSet<Predicate>();
		globalHyps.addAll(hiddenHyps);
		globalHyps.addAll(defaultHyps);
		globalHyps.addAll(selectedHyps);
		return ProverFactory.makeSequent(typenv, globalHyps, hiddenHyps,
				selectedHyps, goal);
	}

	/**
	 * Creates a new proof sequent. The type environment is inferred from the
	 * given predicates.
	 * 
	 * @param factory
	 *            formula factory used to create the predicates
	 * @param predicates
	 *            selected hypotheses and goal in this order
	 * @return a new proof sequent
	 */
	public static IProverSequent genSeq(final FormulaFactory factory,
			final Predicate... predicates) {
		final ITypeEnvironmentBuilder typenv = factory.makeTypeEnvironment();
		for (final Predicate pred : predicates) {
			typenv.addAll(pred.getFreeIdentifiers());
		}
		final int nbHyps = predicates.length - 1;
		final Set<Predicate> hyps = new LinkedHashSet<Predicate>(Arrays.asList(
				predicates).subList(0, nbHyps));
		final Predicate goal = predicates[nbHyps];
		return ProverFactory.makeSequent(typenv, hyps, null, hyps, goal);
	}

	/**
	 * Creates a new proof sequent. The type environment is inferred from the
	 * given predicates. It is assumed that the given predicates do not use any
	 * mathematical extension.
	 * 
	 * @param predicates
	 *            selected hypotheses and goal in this order
	 * @return a new proof sequent
	 */
	public static IProverSequent genSeq(final Predicate... predicates) {
		return genSeq(ff, predicates);
	}

	private static final Pattern typenvPairSeparator = Pattern.compile(";");
	private static final Pattern typEnvPairPattern = Pattern
			.compile("^([^=]*)=([^=]*)$");

	public static ITypeEnvironmentBuilder mTypeEnvironment() {
		return ff.makeTypeEnvironment();
	}
	
	public static ITypeEnvironmentBuilder mTypeEnvironment(String typeEnvImage) {
		return mTypeEnvironment(typeEnvImage, ff);		
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
	public static ITypeEnvironmentBuilder mTypeEnvironment(String typeEnvImage, FormulaFactory factory) {
		final ITypeEnvironmentBuilder result = factory.makeTypeEnvironment();
		if (typeEnvImage.length() == 0) {
			return result;
		}
		for (final String pairImage : typenvPairSeparator.split(typeEnvImage)) {
			final Matcher m = typEnvPairPattern.matcher(pairImage);
			if (!m.matches()) {
				throw new IllegalArgumentException(
						"Invalid type environment pair: " + pairImage);
			}
			final Expression expr = parseExpr(m.group(1), factory);
			if (!(expr instanceof FreeIdentifier)) {
				throw new IllegalArgumentException(
						"Invalid type environment pair: " + pairImage);
			}
			final Type type = factory.parseType(m.group(2)).getParsedType();
			result.addName(expr.toString(), type);
		}
		return result;
	}

	private static LinkedHashSet<Predicate> genPredSet(ITypeEnvironmentBuilder typenv,
			String predList) {
		if (predList.trim().length() == 0) {
			return new LinkedHashSet<Predicate>();
		}
		return genPreds(typenv, predList.split(";;"));
	}

	/**
	 * Generates a type checked predicate from a string.
	 * 
	 * The type environment must be completely inferrable from the given predicate.
	 * (e.g., "x=x" will not work since the type of x is unknown)
	 * 
	 * @param str
	 * 		The string version of the predicate
	 * @return
	 * 		The type checked predicate, or <code>null</code> if there was a parsing
	 * 		of type checking error. 
	 */
	public static Predicate genPred(String str){
		return genPred(str, ff);
	}

	public static Predicate genPred(String str, FormulaFactory factory){
		return genPred(factory.makeTypeEnvironment(), str);
	}

	/**
	 * Generates a type checked predicate from a string and a type environment.
	 * As a side-effect the given type environment gets completed with the types
	 * inferred during type check.
	 * 
	 * @param str
	 *            The string version of the predicate
	 * @param typeEnv
	 *            The type environment to check the predicate with
	 * @return the type checked predicate
	 */
	public static Predicate genPred(ITypeEnvironmentBuilder typeEnv, String str) {
		final Predicate result = typeEnv.getFormulaFactory()
				.parsePredicate(str, null).getParsedPredicate();
		if (result == null)
			throw new IllegalArgumentException("Invalid predicate: " + str);
		typeCheck(typeEnv, result);
		return result;
	}
	
	/**
	 * Generates a type checked predicate from a string and a type environment.
	 * This version does not modify the given type environment.
	 * 
	 * @param str
	 *            The string version of the predicate
	 * @param typeEnv
	 *            The type environment to check the predicate with
	 * @return the type checked predicate
	 */
	public static Predicate genPred(ISealedTypeEnvironment typeEnv, String str) {
		final Predicate result = typeEnv.getFormulaFactory()
				.parsePredicate(str, null).getParsedPredicate();
		if (result == null)
			throw new IllegalArgumentException("Invalid predicate: " + str);
		typeCheck(typeEnv, result);
		return result;
	}

	private static void typeCheck(ITypeEnvironmentBuilder typeEnv,
			Formula<?> formula) {
		final ITypeCheckResult tcResult = doTypeCheck(typeEnv, formula);
		typeEnv.addAll(tcResult.getInferredEnvironment());
	}

	private static void typeCheck(ISealedTypeEnvironment typeEnv,
			Formula<?> formula) {
		final ITypeCheckResult tcResult = doTypeCheck(typeEnv, formula);
		final ITypeEnvironment inferred = tcResult.getInferredEnvironment();
		if (!inferred.isEmpty())
			throw new IllegalArgumentException("Formula " + formula
					+ " generates inferred identifiers " + inferred);
	}

	private static ITypeCheckResult doTypeCheck(ITypeEnvironment typeEnv,
			Formula<?> formula) {
		final ITypeCheckResult tcResult = formula.typeCheck(typeEnv);
		assertNoProblem(tcResult, formula, "does not typecheck in environment "
				+ typeEnv);
		return tcResult;
	}
	
	/**
	 * A Set version of {@link #genPred(String)}
	 * 
	 * @param strs
	 * @return
	 */
	public static LinkedHashSet<Predicate> genPreds(String... strs) {
		return genPreds(ff.makeTypeEnvironment(), strs);
	}

	
	/**
	 * A Set version of {@link #genPred(ITypeEnvironmentBuilder, String)}
	 * 
	 * @param strs
	 * @return
	 */
	public static LinkedHashSet<Predicate> genPreds(ITypeEnvironmentBuilder typeEnv,
			String... strs) {
		final LinkedHashSet<Predicate> hyps = new LinkedHashSet<Predicate>(
				strs.length * 4 / 3);
		for (String s : strs)
			hyps.add(genPred(typeEnv, s));
		return hyps;
	}

	/**
	 * A Set version of {@link #genPred(ISealedTypeEnvironment, String)}
	 * 
	 * @param strs
	 * @return
	 */
	public static LinkedHashSet<Predicate> genPreds(
			ISealedTypeEnvironment typeEnv, String... strs) {
		final LinkedHashSet<Predicate> hyps = new LinkedHashSet<Predicate>(
				strs.length * 4 / 3);
		for (String s : strs)
			hyps.add(genPred(typeEnv, s));
		return hyps;
	}

	/**
	 * A List version of {@link #genPred(ITypeEnvironmentBuilder, String)}
	 * 
	 * @param strs
	 * @return
	 */
	public static List<Predicate> genPredList(ITypeEnvironmentBuilder typeEnv,
			String... strs) {
		final List<Predicate> hyps = new ArrayList<Predicate>(
				strs.length * 4 / 3);
		for (String s : strs)
			hyps.add(genPred(typeEnv, s));
		return hyps;
	}

	/**
	 * Generates an expression from a string without type-checking it. The
	 * string is parsed with the default formula factory.
	 * 
	 * @param str
	 *            the string version of the expression
	 * @return the parsed expression
	 */
	public static Expression parseExpr(String str) {
		return parseExpr(str, ff);
	}
	
	/**
	 * Generates an expression from a string without type-checking it. The
	 * string is parsed with the given formula factory.
	 * 
	 * @param str
	 *            the string version of the expression
	 * @param factory
	 *            formula factory to use for parsing the expression
	 * @return the parsed expression
	 */
	public static Expression parseExpr(String str, FormulaFactory factory) {
		final IParseResult result = factory.parseExpression(str, null);
		assertNoProblem(result, str, "does not parse");
		return result.getParsedExpression();
	}
	
	/**
	 * Generates a type checked expression from a string and a type environment.
	 * As a side-effect the given type environment gets completed with the types
	 * inferred during type check.
	 * 
	 * @param str
	 *            The string version of the expression
	 * @param typeEnv
	 *            The type environment to check the expression with
	 * @return the type checked expression
	 */
	public static Expression genExpr(ITypeEnvironmentBuilder typeEnv, String str) {
		final Expression result = parseExpr(str, typeEnv.getFormulaFactory());
		typeCheck(typeEnv, result);
		return result;
	}
	
	/**
	 * Generates a type checked expression from a string and a type environment.
	 * This version does not modify the given type environment.
	 * 
	 * @param str
	 *            The string version of the expression
	 * @param typeEnv
	 *            The type environment to check the expression with
	 * @return the type checked expression
	 */
	public static Expression genExpr(ISealedTypeEnvironment typeEnv, String str) {
		final Expression result = parseExpr(str, typeEnv.getFormulaFactory());
		typeCheck(typeEnv, result);
		return result;
	}
	
	/**
	 * Searches the set of hypotheses in the given sequent for the given
	 * hypotheses and returns the hypothesis found as it occurs in the
	 * sequent.
	 * 
	 * This is useful for extracting the reference of a hypothesis inside
	 * a sequent in the case where this is needed.
	 * 
	 * @param seq
	 * 		The sequent in whose hyoptheses to search
	 * @param hyp
	 * 		The hypothesis to search for
	 * @return
	 * 		The sequent copy of the hypothesis in case it is found, or
	 * 		<code>null</code> otherwise.
	 */
	public static Predicate getHypRef(IProverSequent seq, Predicate hyp) {
		for (Predicate pred : seq.hypIterable()) {
			if (hyp.equals(pred)) return pred;
		}
		return null;
	}
	
	/**
	 * Returns the first hypothesis in the given sequent as returned by the
	 * iterator in {@link IProverSequent#hypIterable()}.
	 * 
	 * <p>
	 * This is useful in test cases where generating the hypothesis from a string is 
	 * difficult because the typing information needed to typecheck it is present in the 
	 * sequent, but cannot be inferred from the predicate itself.
	 * </p>
	 * 
	 * <p>
	 * It is recommended to use this method only for sequents with exactly one hypothesis 
	 * since the order of the hypotheses does not matter and this would lead to more resilient
	 * test case code.
	 * </p>
	 * 
	 * @param seq
	 * 		The sequent in whose hyoptheses to search
	 * @return
	 * 		The reference to the first hypothesis in the sequent.
	 * @throws IllegalArgumentException
	 * 		in case the sequent has no hypotheses
	 */
	public static Predicate getFirstHyp(IProverSequent seq) {
		for (Predicate pred : seq.hypIterable()) {
			return pred;
		}
		throw new IllegalArgumentException("Sequent " + seq +" contains no hypotheses.");
	}

	public static void assertNoProblem(IResult result, Object object,
			String message) {
		if (result.hasProblem()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("Formula '");
			sb.append(object.toString());
			sb.append("' ");
			sb.append(message);
			sb.append(":\n");
			for (final ASTProblem pb : result.getProblems()) {
				sb.append('\t');
				sb.append(pb);
				sb.append('\n');
			}
			fail(sb.toString());
		}
	}

}
