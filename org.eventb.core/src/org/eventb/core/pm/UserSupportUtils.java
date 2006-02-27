package org.eventb.core.pm;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class UserSupportUtils {
	
	public static final boolean debug = false;
	
	public static IProverSequent genSeq(String s){
		String[] hypsStr = (s.split("[|]-")[0]).split(";;");
		String goalStr = s.split("[|]-")[1];
		
		FormulaFactory ff = Lib.ff;
		
		// Parsing
		Predicate[] hyps = new Predicate[hypsStr.length];
		Predicate goal = ff.parsePredicate(goalStr).getParsedPredicate();
		for (int i=0;i<hypsStr.length;i++){
			hyps[i] = ff.parsePredicate(hypsStr[i]).getParsedPredicate();
		}
		
		// Type check
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		for (int i=0;i<hyps.length;i++){
			ITypeCheckResult tcResult =  hyps[i].typeCheck(typeEnvironment);
			assert tcResult.isSuccess();
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
		}
		ITypeCheckResult tcResult =  goal.typeCheck(typeEnvironment);
		assert tcResult.isSuccess();
		typeEnvironment.addAll(tcResult.getInferredEnvironment());
		
		// constructing sequent
		Set<Hypothesis> Hyps = Hypothesis.Hypotheses(hyps);
		
		IProverSequent Seq = new SimpleProverSequent(typeEnvironment,Hyps,goal);
		return Seq.selectHypotheses(Hyps);
	}
	

}
