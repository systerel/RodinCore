package org.eventb.core.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.ImpD;
import org.eventb.core.prover.externalReasoners.ImpD.Input;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class ImpDTest extends TestCase {
//	FormulaFactory ff = new FormulaFactory();
	IExternalReasoner impD = new ImpD();

	IProverSequent impDseq;
	Predicate goal,newgoal,newgoalContrap;
	Predicate impHypPred;
	Hypothesis impHyp;
	
	@Override
	public void setUp(){
		impHypPred = Lib.parsePredicate("x=1 ⇒ x∈ℕ");
		goal = Lib.parsePredicate("x ∈ℤ");
		newgoal = Lib.parsePredicate("x=1∧(x=1⇒(x∈ℕ⇒x∈ℤ))");
		newgoalContrap = Lib.parsePredicate("¬x∈ℕ∧(¬x∈ℕ⇒(¬x=1⇒x∈ℤ))");
		ITypeEnvironment typeEnvironment = Lib.typeCheck(impHypPred,goal,newgoal,newgoalContrap);
		assertNotNull(typeEnvironment);	
		impHyp = new Hypothesis(impHypPred);
		impDseq = new SimpleProverSequent(typeEnvironment,Hypothesis.Hypotheses(impHyp),goal);
	}
	
	public void testApply() {	
		Input I;
		Predicate newGoalPredicate;
		I = new Input(impHyp);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(impDseq,impD,I);
		
		assertTrue(newGoalPredicate.equals(newgoal));
		//System.out.println(newGoalPredicate);
		
		I = new Input(impHyp,true);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(impDseq,impD,I);
		
		assertTrue(newGoalPredicate.equals(newgoalContrap));
		//System.out.println(newGoalPredicate);
		
	}
	
}
