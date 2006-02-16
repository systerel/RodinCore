package org.eventb.core.prover.tests;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class CommonProverSequents {
	
	FormulaFactory ff = FormulaFactory.getDefault();
	
	public IProverSequent hypSeq;
	public IProverSequent hypSeqFail;
	public IProverSequent conjSeq2;
	public Predicate[] conjSeq2conjs;
	public IProverSequent conjSeq3;
	public Predicate[] conjSeq3conjs;
	public IProverSequent impSeq;
	public Predicate impSeqLeft, impSeqRight;
	public IProverSequent quantSeq;
	public Predicate quantSeqFree;
	public IProverSequent quantSeqClash;
	
	public CommonProverSequents(){
		Predicate P1 = ff.parsePredicate("1=1").getParsedPredicate();
		Predicate P2 = ff.parsePredicate("2=2").getParsedPredicate();
		Predicate P3 = ff.parsePredicate("3=3").getParsedPredicate();
		Predicate P4 = ff.parsePredicate("x=4").getParsedPredicate();
		Predicate P5 = ff.parsePredicate("5=5 ∧4=4").getParsedPredicate();
		Predicate P6 = ff.parsePredicate("6=6 ∧5=5 ∧4=4").getParsedPredicate();
		Predicate P7 = ff.parsePredicate("7=7 ⇒ 6=6").getParsedPredicate();
		Predicate P8 = ff.parsePredicate("∀y·y=8").getParsedPredicate();
		Predicate P9 = ff.parsePredicate("∀x·x=9").getParsedPredicate();
			
//			new Predicate[] {
//				ff.parsePredicate("5=5").getParsedPredicate(),
//				ff.parsePredicate("4=4").getParsedPredicate()};
//		conjSeq3conjs = new Predicate[] {
//				ff.parsePredicate("6=6").getParsedPredicate(),
//				ff.parsePredicate("5=5").getParsedPredicate(),
//				ff.parsePredicate("4=4").getParsedPredicate()};
//		impSeqLeft = ff.parsePredicate("7=7").getParsedPredicate();
//		impSeqRight = ff.parsePredicate("6=6").getParsedPredicate();
//		quantSeqFree = ff.parsePredicate("y=8").getParsedPredicate();
		
		Predicate[] predicates = new Predicate[]{P1,P2,P3,P4,P5,P6,P7,P8,P9};
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		for (int i=0;i<predicates.length;i++){
			ITypeCheckResult tcResult =  predicates[i].typeCheck(typeEnvironment);
			assert tcResult.isSuccess();
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
			
			// boolean typed = (predicates[i].isCorrectlyTyped(typeEnvironment,ff)).isCorrectlyTyped();
			// assert typed;
		}
		
		conjSeq2conjs = Lib.conjuncts(P5);
		conjSeq3conjs = Lib.conjuncts(P6);
		impSeqLeft = Lib.impLeft(P7);
		impSeqRight = Lib.impRight(P7);
		quantSeqFree = Lib.parsePredicate("y=8");
		quantSeqFree.typeCheck(ff.makeTypeEnvironment());
		assert quantSeqFree.isTypeChecked();
		
		Set<Hypothesis> Hyps = Hypothesis.Hypotheses(P1,P2,P3,P4);
		hypSeq = new SimpleProverSequent(typeEnvironment,Hyps,P1);
		hypSeqFail = new SimpleProverSequent(typeEnvironment,Hyps,P9);
		conjSeq2 = new SimpleProverSequent(typeEnvironment,Hyps,P5);
		conjSeq3 = new SimpleProverSequent(typeEnvironment,Hyps,P6);
		impSeq = new SimpleProverSequent(typeEnvironment,Hyps,P7);
		quantSeq = new SimpleProverSequent(typeEnvironment,Hyps,P8);
		quantSeqClash = new SimpleProverSequent(typeEnvironment,Hyps,P9);
	}

}
