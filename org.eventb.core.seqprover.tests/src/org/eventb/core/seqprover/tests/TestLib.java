/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added genPred() and genPreds() with a type environment
 ******************************************************************************/
package org.eventb.core.seqprover.tests;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Lib;

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
	public static IProverSequent genSeq(String sequentAsString) {
		final String[] split = sequentAsString.split("\\|-");
		return genFullSeq("", "", "", split[0], split[1]);
	}
	
	private static final Pattern fullSequentPattern = Pattern
			.compile("^(.*);H;(.*);S;(.*)\\s*\\|-\\s*(.*)$");	
	
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
	 *            The sequent as a string
	 * @return The resulting sequent
	 * @throws IllegalArgumentException
	 *             in case the sequent could not be constructed due to a parsing
	 *             or type-checking error.
	 * @author htson
	 */
	public static IProverSequent genFullSeq(String sequentAsString) {
		final ITypeEnvironment typenv = Lib.makeTypeEnvironment();
		final Matcher m = fullSequentPattern.matcher(sequentAsString);
		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid sequent: "
					+ sequentAsString);
		}
		final Set<Predicate> globalHyps = genPredSet(typenv, m.group(1));
		final Set<Predicate> hiddenHyps = genPredSet(typenv, m.group(2));
		final Set<Predicate> selectHyps = genPredSet(typenv, m.group(3));
		final Predicate goal = genPred(typenv, m.group(4));

		if (!globalHyps.containsAll(hiddenHyps)) {
			throw new IllegalArgumentException(
					"Global hypotheses do not contain hidden hypotheses");
		}
		if (!globalHyps.containsAll(selectHyps)) {
			throw new IllegalArgumentException(
					"Global hypotheses do not contain selected hypotheses");
		}

		return ProverFactory.makeSequent(typenv, globalHyps, hiddenHyps,
				selectHyps, goal);
	}
	
	public static IProofTreeNode genProofTreeNode(String str){
		return ProverFactory.makeProofTree(genSeq(str), null).getRoot();
	}
		
	public static ITypeEnvironment genTypeEnv(String... strs){
		ITypeEnvironment typeEnv = Lib.makeTypeEnvironment();
		assert strs.length % 2 == 0;
		for (int i = 0; i+1 < strs.length; i=i+2) {
			Type type = Lib.parseType(strs[i+1]);
			assert type != null;
			typeEnv.addName(strs[i],type);
		}
		return typeEnv;
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
		final ITypeEnvironment te = typenv.clone();
		final Set<Predicate> hiddenHyps = genPredSet(te, hiddenHypsImage);
		final Set<Predicate> defaultHyps = genPredSet(te, defaultHypsImage);
		final Set<Predicate> selectedHyps = genPredSet(te, selHypsImage);
		final Predicate goal = genPred(te, goalImage);

		final Set<Predicate> globalHyps = new LinkedHashSet<Predicate>();
		globalHyps.addAll(hiddenHyps);
		globalHyps.addAll(defaultHyps);
		globalHyps.addAll(selectedHyps);

		return ProverFactory.makeSequent(te, globalHyps, hiddenHyps,
				selectedHyps, goal);

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
			String selHypsImage, String goalImage) {

		final ITypeEnvironment typenv = parseTypeEnv(typeEnvImage);
		return genFullSeq(typenv, hiddenHypsImage, defaultHypsImage, selHypsImage, goalImage);
	}

	private static final Pattern typEnvPairPattern = Pattern
			.compile("^([^=]*)=([^=]*)$");
	
	private static ITypeEnvironment parseTypeEnv(String typeEnvImage) {
		final ITypeEnvironment result = Lib.makeTypeEnvironment();
		if (typeEnvImage.length() == 0) {
			return result;
		}
		for (final String pairImage : typeEnvImage.split(",")) {
			final Matcher m = typEnvPairPattern.matcher(pairImage);
			if (!m.matches()) {
				throw new IllegalArgumentException(
						"Invalid type environment pair: " + pairImage);
			}
			final Expression expr = Lib.parseExpression(m.group(1));
			if (!(expr instanceof FreeIdentifier)) {
				throw new IllegalArgumentException(
						"Invalid type environment pair: " + pairImage);
			}
			final Type type = Lib.parseType(m.group(2));
			result.addName(expr.toString(), type);
		}
		return result;
	}

	private static Set<Predicate> genPredSet(ITypeEnvironment typenv,
			String predList) {
		if (predList.trim().length() == 0) {
			return Collections.emptySet();
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
		return genPred(Lib.makeTypeEnvironment(), str);
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
	 * @return The type checked predicate, or <code>null</code> if there was a
	 *         parsing or type checking error.
	 */
	public static Predicate genPred(ITypeEnvironment typeEnv, String str){
		final Predicate result = Lib.parsePredicate(str);
		if (result == null) return null;
		final ITypeCheckResult tcResult = result.typeCheck(typeEnv);
		if (! tcResult.isSuccess()) return null;
		typeEnv.addAll(tcResult.getInferredEnvironment());
		return result;
	}
	
	/**
	 * A Set version of {@link #genPred(String)}
	 * 
	 * @param strs
	 * @return
	 */
	public static Set<Predicate> genPreds(String... strs){
		return genPreds(Lib.makeTypeEnvironment(), strs);
	}

	
	/**
	 * A Set version of {@link #genPred(ITypeEnvironment, String)}
	 * 
	 * @param strs
	 * @return
	 */
	public static Set<Predicate> genPreds(ITypeEnvironment typeEnv, String... strs){
		final Set<Predicate> hyps = new LinkedHashSet<Predicate>(
				strs.length * 4 / 3);
		for (String s : strs)
			hyps.add(genPred(typeEnv, s));
		return hyps;
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
	public static Predicate getHypRef(IProverSequent seq, Predicate hyp)
	{
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
	public static Predicate getFirstHyp(IProverSequent seq)
	{
		for (Predicate pred : seq.hypIterable()) {
			return pred;
		}
		throw new IllegalArgumentException("Sequent " + seq +" contains no hypotheses.");
	}
	
}
