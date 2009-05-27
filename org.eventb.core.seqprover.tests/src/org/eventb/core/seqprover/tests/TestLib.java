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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
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

	public final static FormulaFactory ff = Lib.ff;
	
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
	public static IProverSequent genSeq(String sequentAsString){
		String[] hypsStr = (sequentAsString.split("\\|-")[0]).split(";;");
		if ((hypsStr.length == 1) && (hypsStr[0].matches("^[ ]*$")))
			hypsStr = new String[0];
		
		String goalStr = sequentAsString.split("\\|-")[1];
		
		// Parsing
		Predicate[] hyps = new Predicate[hypsStr.length];
		for (int i=0;i<hypsStr.length;i++){
			hyps[i] = Lib.parsePredicate(hypsStr[i]);
			if (hyps[i] == null) throw new IllegalArgumentException();
		}
		Predicate goal = Lib.parsePredicate(goalStr);
		if (goal == null) throw new IllegalArgumentException();
		
		// Type check
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		ITypeCheckResult tcResult;
		
		for (int i=0;i<hyps.length;i++){
			tcResult =  hyps[i].typeCheck(typeEnvironment);
			if (! tcResult.isSuccess()) throw new IllegalArgumentException();
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
		}

		tcResult =  goal.typeCheck(typeEnvironment);
		if (! tcResult.isSuccess()) throw new IllegalArgumentException();
		typeEnvironment.addAll(tcResult.getInferredEnvironment());
				
		// constructing sequent
		Set<Predicate> Hyps = new LinkedHashSet<Predicate>(Arrays.asList(hyps));
		IProverSequent seq = ProverFactory.makeSequent(typeEnvironment,Hyps,Hyps,goal);
		return seq;
	}
	
	/**
	 * <p>
	 * Constructs a sequent from a string of the form " Hyps ;H; Hyps ;S; Hyps |-
	 * goal" where the hypothesis list Hyps is of the form " hyp ;; hyp ;; ... ;;
	 * hyp ". (If the list is empty, Hyps should be at least as contain one blank space)
	 * </p>
	 * <p>
	 * The first list of hypotheses is the list of all hypotheses. The second
	 * list (after ;H;) is the list of hidden hypotheses. The third list (after
	 * ;S;) is the list of selected hypotheses. The order of hypotheses in the
	 * list are kept. Ignoring order, the set of hidden hypotheses and selected
	 * hypotheses should be disjoint and both are subset of the set of all
	 * hypotheses.
	 * </p>
	 * <p>
	 * 
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
	public static IProverSequent genFullSeq(String sequentAsString){
		String[] goalSplit = sequentAsString.split("\\|-");
		if (goalSplit.length != 2)
			throw new IllegalArgumentException();
		
		String hypsImage = goalSplit[0];
		String goalImage = goalSplit[1];
		
		String[] hiddenHypSplit = hypsImage.split(";H;");
		if (hiddenHypSplit.length != 2)
			throw new IllegalArgumentException();
		String globalHypLists = hiddenHypSplit[0];
		String[] selectedHypSplit = hiddenHypSplit[1].split(";S;");
		if (selectedHypSplit.length != 2)
			throw new IllegalArgumentException();
		
		String hiddenHypLists = selectedHypSplit[0];
		String selectedHypLists = selectedHypSplit[1];
		
		// Parsing hyps
		Predicate[] globalHyps = parsePredicate(globalHypLists);
		Predicate[] hiddenHyps = parsePredicate(hiddenHypLists);
		Predicate[] selectedHyps = parsePredicate(selectedHypLists);
		Predicate goal = Lib.parsePredicate(goalImage);
		if (goal == null)
			throw new IllegalArgumentException();
		
		// Type check global hyps
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		ITypeCheckResult tcResult;
		for (int i = 0; i < globalHyps.length; i++) {
			tcResult = globalHyps[i].typeCheck(typeEnvironment);
			if (!tcResult.isSuccess())
				throw new IllegalArgumentException();
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
		}

		// Type check hidden hyps and check to see if it is contains in the set
		// of global hypotheses.
		for (Predicate hiddenHyp : hiddenHyps) {
			tcResult = hiddenHyp.typeCheck(typeEnvironment);
			if (!tcResult.isSuccess())
				throw new IllegalArgumentException();
			boolean found = false;
			for (Predicate globalHyp : globalHyps) {
				if (hiddenHyp.equals(globalHyp)) {
					found = true;
					break;
				}
			}
			if (!found)
				throw new IllegalArgumentException();
		}
		
		// Type check selected hyps if it is contains in the set
		// of global hypotheses but not the hidden hypotheses.
		for (Predicate selectedHyp : selectedHyps) {
			tcResult = selectedHyp.typeCheck(typeEnvironment);
			if (!tcResult.isSuccess())
				throw new IllegalArgumentException();
			boolean found = false;
			for (Predicate globalHyp : globalHyps) {
				if (selectedHyp.equals(globalHyp)) {
					found = true;
					break;
				}
			}
			if (!found)
				throw new IllegalArgumentException();
			
			found = false;
			for (Predicate hiddenHyp : hiddenHyps) {
				if (selectedHyp.equals(hiddenHyp)) {
					found = true;
					break;
				}
			}
			if (found)
				throw new IllegalArgumentException();
		}

		tcResult = goal.typeCheck(typeEnvironment);
		if (!tcResult.isSuccess())
			throw new IllegalArgumentException();
		typeEnvironment.addAll(tcResult.getInferredEnvironment());
				
		// constructing sequent
		Set<Predicate> globalHypSet = new LinkedHashSet<Predicate>(Arrays
				.asList(globalHyps));
		Set<Predicate> hiddenHypSet = new LinkedHashSet<Predicate>(Arrays
				.asList(hiddenHyps));
		Set<Predicate> selectedHypSet = new LinkedHashSet<Predicate>(Arrays
				.asList(selectedHyps));
		IProverSequent seq = ProverFactory.makeSequent(typeEnvironment,
				globalHypSet, hiddenHypSet, selectedHypSet, goal);
		
		return seq;
	}
	
	private static Predicate[] parsePredicate(String predicateList)
			throws IllegalArgumentException {
		String[] hypsStr = predicateList.split(";;");
		if ((hypsStr.length == 1) && (hypsStr[0].matches("^[ ]*$")))
			hypsStr = new String[0];
		Predicate[] Hyps = new Predicate[hypsStr.length];
		for (int i = 0; i < hypsStr.length; i++) {
			Hyps[i] = Lib.parsePredicate(hypsStr[i]);
			if (Hyps[i] == null)
				throw new IllegalArgumentException();
		}
		return Hyps;
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
	 * Generates a type checked predicate from a string.
	 * 
	 * The type environment must be completely inferrable from the given predicate.
	 * (eg. "x=x" will not work since the type of x is unknown)
	 * 
	 * @param str
	 * 		The string version of the predicate
	 * @return
	 * 		The type checked predicate, or <code>null</code> if there was a parsing
	 * 		of type checking error. 
	 */
	public static Predicate genPred(String str){
		return genPred(ff.makeTypeEnvironment(), str);
	}
	
	/**
	 * Generates a type checked predicate from a string and a type environment.
	 * 
	 * @param str
	 *            The string version of the predicate
	 * @param typeEnv
	 *            The type environment to check the predicate with
	 * @return The type checked predicate, or <code>null</code> if there was a
	 *         parsing of type checking error.
	 */
	public static Predicate genPred(ITypeEnvironment typeEnv, String str){
		Predicate result = Lib.parsePredicate(str);
		if (result == null) return null;
		ITypeCheckResult tcResult = result.typeCheck(typeEnv);
		if (! tcResult.isSuccess()) return null;
		return result;
	}
	
	/**
	 * A Set version of {@link #genPred(String)}
	 * 
	 * @param strs
	 * @return
	 */
	public static Set<Predicate> genPreds(String... strs){
		return genPreds(ff.makeTypeEnvironment(), strs);
	}

	
	/**
	 * A Set version of {@link #genPred(ITypeEnvironment, String)}
	 * 
	 * @param strs
	 * @return
	 */
	public static Set<Predicate> genPreds(ITypeEnvironment typeEnv, String... strs){
		Set<Predicate> hyps = new HashSet<Predicate>(strs.length);
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
