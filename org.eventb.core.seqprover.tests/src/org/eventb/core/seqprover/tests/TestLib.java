package org.eventb.core.seqprover.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class TestLib {

	public final static FormulaFactory ff = Lib.ff;
	
	public static IProverSequent genSeq(String s){
		String[] hypsStr = (s.split("[|]-")[0]).split(";;");
		if ((hypsStr.length == 1) && (hypsStr[0].matches("^[ ]*$")))
			hypsStr = new String[0];
		
		String goalStr = s.split("[|]-")[1];
		
		// Parsing
		Predicate[] hyps = new Predicate[hypsStr.length];
		Predicate goal = Lib.parsePredicate(goalStr);
		for (int i=0;i<hypsStr.length;i++){
			hyps[i] = Lib.parsePredicate(hypsStr[i]);
		}
		
		// Type check
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		
		for (int i=0;i<hyps.length;i++){
			ITypeCheckResult tcResult =  hyps[i].typeCheck(typeEnvironment);
			Assert.assertTrue(tcResult.isSuccess());
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
		}
		{
			ITypeCheckResult tcResult =  goal.typeCheck(typeEnvironment);
			Assert.assertTrue(tcResult.isSuccess());
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
		}
		
		// constructing sequent
		Set<Predicate> Hyps = new LinkedHashSet<Predicate>(Arrays.asList(hyps));

		IProverSequent seq = ProverFactory.makeSequent(typeEnvironment,Hyps,Hyps,goal);
		return seq;
	}
	
	public static IProofTreeNode genProofTreeNode(String str){
		return ProverFactory.makeProofTree(genSeq(str), null).getRoot();
		// return (new ProofTree(genSeq(str))).getRoot();
	}
	
	public static Predicate genHyp(String s){
		Predicate hypPred = Lib.parsePredicate(s);
		Lib.typeCheck(hypPred);
		return hypPred;
	}
	
	public static Set<Predicate> genHyps(String... strs){
		Set<Predicate> hyps = new HashSet<Predicate>(strs.length);
		for (String s : strs) 
			hyps.add(genHyp(s));
		return hyps;
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
	
	public static Predicate genPredicate(String str){
		Predicate result = Lib.parsePredicate(str);
		Lib.typeCheck(result);
		return result;
	}
	
}
