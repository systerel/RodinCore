package org.eventb.core.prover.tests;

import java.util.Set;

import junit.framework.Assert;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class TestLib {

	public final static FormulaFactory ff = Lib.ff;
	
	public static final Predicate chkProofFormat_getNewGoalPred(IProverSequent goalSeq,IExternalReasoner plugin, IExtReasonerInput I){
		IExtReasonerOutput O = plugin.apply(goalSeq,I);
		Assert.assertTrue (O.toString()+goalSeq.toString(),O instanceof SuccessfullExtReasonerOutput);
		SuccessfullExtReasonerOutput sO = (SuccessfullExtReasonerOutput) O;
		Predicate proofGoal = sO.proof().ofSequent().goal();
		Assert.assertTrue (goalSeq.hypotheses().containsAll(sO.proof().ofSequent().hypotheses()));
		Assert.assertTrue (goalSeq.typeEnvironment().containsAll(sO.proof().ofSequent().typeEnvironment()));
		// System.out.println(sO.proof());
		if (Lib.isImp(proofGoal)){
			Assert.assertTrue (((BinaryPredicate)proofGoal).getRight().equals(goalSeq.goal()));
			return ((BinaryPredicate)proofGoal).getLeft();
		}
		else
		{
			Assert.assertTrue (proofGoal.equals(goalSeq.goal()));
			return Lib.True;
		}
		
	}
	
	public static IProverSequent genSeq(String s){
		String[] hypsStr = (s.split("[|]-")[0]).split(";;");
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
		IProverSequent Seq = new SimpleProverSequent(typeEnvironment,Hyps,goal);
		return Seq.selectHypotheses(Hyps);
	}
	
}
