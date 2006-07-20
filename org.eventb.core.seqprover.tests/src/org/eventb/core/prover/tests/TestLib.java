package org.eventb.core.prover.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ProverSequent;

public class TestLib {

	public final static FormulaFactory ff = Lib.ff;
	
	public static IProverSequent genSeq(String s){
		String[] hypsStr = (s.split("[|]-")[0]).split(";;");
		if ((hypsStr.length == 1) && (hypsStr[0].matches("^[ ]*$")))
			hypsStr = new String[0];
		
		String goalStr = s.split("[|]-")[1];
		
		// Parsing
		Predicate[] hyps = new Predicate[hypsStr.length];
		// Predicate goal = ff.parsePredicate(goalStr).getParsedPredicate();
		Predicate goal = Lib.parsePredicate(goalStr);
		for (int i=0;i<hypsStr.length;i++){
			// hyps[i] = ff.parsePredicate(hypsStr[i]).getParsedPredicate();
			hyps[i] = Lib.parsePredicate(hypsStr[i]);
		}
		
		// Type check
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		
		for (int i=0;i<hyps.length;i++){
			ITypeCheckResult tcResult =  hyps[i].typeCheck(typeEnvironment);
			Assert.assertTrue(tcResult.isSuccess());
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
			// boolean typed = (hyps[i].isCorrectlyTyped(typeEnvironment,ff)).isCorrectlyTyped();
			// assert typed;
		}
		{
			ITypeCheckResult tcResult =  goal.typeCheck(typeEnvironment);
			Assert.assertTrue(tcResult.isSuccess());
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
			
			// boolean typed = (goal.isCorrectlyTyped(typeEnvironment,ff)).isCorrectlyTyped();
			// assert typed;
		}
		
		// constructing sequent
		Set<Hypothesis> Hyps = Hypothesis.Hypotheses(hyps);
			// new HashSet<Predicate>(Arrays.asList(hyps));
		
		// return new SimpleProverSequent(typeEnvironment,Hyps,goal);
		IProverSequent Seq = new ProverSequent(typeEnvironment,Hyps,goal);
		return Seq.selectHypotheses(Hyps);
	}
	
	public static IProofTreeNode genProofTreeNode(String str){
		return (new ProofTree(genSeq(str))).getRoot();
	}
	
	public static Hypothesis genHyp(String s){
		Predicate hypPred = Lib.parsePredicate(s);
		Lib.typeCheck(hypPred);
		return new Hypothesis(hypPred);
	}
	
	public static Set<Hypothesis> genHyps(String... strs){
		Set<Hypothesis> hyps = new HashSet<Hypothesis>(strs.length);
		for (String s : strs) 
			hyps.add(genHyp(s));
		return hyps;
	}
	
	public static ITypeEnvironment genTypeEnv(String... strs){
		ITypeEnvironment typeEnv = Lib.makeTypeEnvironment();
		assert strs.length % 2 == 1;
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
