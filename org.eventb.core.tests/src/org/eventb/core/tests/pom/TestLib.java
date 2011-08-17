/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

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
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * This is a collection of static methods for conveniently constructing objects used for
 * testing using their string counterparts. 
 * 
 * @author Farhad Mehta
 *
 * TODO : At the moment there are two copies of this file (in org.eventb.core.tests(.pom), and
 *  org.eventb.core.seqprover.tests). Find a way to use ony one copy, without placing it in seqprover.
 *
 */public class TestLib {

	public final static FormulaFactory ff = FormulaFactory.getDefault();
	
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
	 * 			The resulting sequent, or <code>null</code> in case the sequent 
	 * 			could not be constructed due to a parsing or typechecking error.
	 */
	public static IProverSequent genSeq(String sequentAsString){
		String[] hypsStr = (sequentAsString.split("\\|-")[0]).split(";;");
		if ((hypsStr.length == 1) && (hypsStr[0].matches("^[ ]*$")))
			hypsStr = new String[0];
		
		String goalStr = sequentAsString.split("\\|-")[1];
		
		// Parsing
		final DLib lib = mDLib(ff);
		Predicate[] hyps = new Predicate[hypsStr.length];
		for (int i=0;i<hypsStr.length;i++){
			hyps[i] = lib.parsePredicate(hypsStr[i]);
			if (hyps[i] == null) return null;
		}
		Predicate goal = lib.parsePredicate(goalStr);
		if (goal == null) return null;
		
		// Type check
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		ITypeCheckResult tcResult;
		
		for (int i=0;i<hyps.length;i++){
			tcResult =  hyps[i].typeCheck(typeEnvironment);
			if (! tcResult.isSuccess()) return null;
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
		}

		tcResult =  goal.typeCheck(typeEnvironment);
		if (! tcResult.isSuccess()) return null;
		typeEnvironment.addAll(tcResult.getInferredEnvironment());
				
		// constructing sequent
		Set<Predicate> Hyps = new LinkedHashSet<Predicate>(Arrays.asList(hyps));
		IProverSequent seq = ProverFactory.makeSequent(typeEnvironment,Hyps,Hyps,goal);
		return seq;
	}
	
	public static IProofTreeNode genProofTreeNode(String str){
		return ProverFactory.makeProofTree(genSeq(str), null).getRoot();
	}
		
	public static ITypeEnvironment genTypeEnv(String... strs){
		final DLib lib = mDLib(ff);
		ITypeEnvironment typeEnv = lib.makeTypeEnvironment();
		assert strs.length % 2 == 0;
		for (int i = 0; i+1 < strs.length; i=i+2) {
			Type type = lib.parseType(strs[i+1]);
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
		Predicate result = mDLib(ff).parsePredicate(str);
		if (result == null) return null;
		ITypeCheckResult tcResult =  result.typeCheck(ff.makeTypeEnvironment());
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
		Set<Predicate> hyps = new HashSet<Predicate>(strs.length);
		for (String s : strs) 
			hyps.add(genPred(s));
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
	
}
