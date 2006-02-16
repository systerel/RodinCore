package org.eventb.core.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.Eq;
import org.eventb.core.prover.externalReasoners.Eq.Input;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class EqTest extends TestCase {
	// FormulaFactory ff = new FormulaFactory();
	IExternalReasoner eq = new Eq();

	IProverSequent eqSeq;
	Predicate goal,newgoal,newGoalReflexive;
	Predicate eqHypPred;
	Hypothesis eqHyp;
	
	@Override
	public void setUp(){
		eqHypPred = Lib.parsePredicate("x=y");
		Predicate hyp = Lib.parsePredicate("y ∈ℤ");
		goal = Lib.parsePredicate("x ∈ℤ");
		newgoal = Lib.parsePredicate("y∈ℤ");
		newGoalReflexive =  Lib.parsePredicate("x∈ℤ⇒x∈ℤ");
		
		ITypeEnvironment typeEnvironment = Lib.typeCheck(hyp,eqHypPred,goal,newgoal,newGoalReflexive);
		assertNotNull(typeEnvironment);
		eqHyp = new Hypothesis(eqHypPred);
		eqSeq = new SimpleProverSequent(typeEnvironment,Hypothesis.Hypotheses(eqHypPred,hyp),goal);
		eqSeq = eqSeq.selectHypotheses(Hypothesis.Hypotheses(eqHypPred,hyp));
		// System.out.println(eqSeq);
	}
	
	public void testApply() {	
		Input I;
		Predicate newGoalPredicate;
		I = new Input(eqHyp);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(eqSeq,eq,I);
		
		// System.out.println(newGoalPredicate);
		assertTrue(newGoalPredicate.equals(newgoal));
		
		I = new Input(eqHyp,true);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(eqSeq,eq,I);
		
		assertTrue(newGoalPredicate.equals(newGoalReflexive));
		// System.out.println(newGoalPredicate);
		
	}
	
}
