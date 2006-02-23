package org.eventb.core.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.Contr;
import org.eventb.core.prover.externalReasoners.Contr.Input;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class ContrTest extends TestCase {

	FormulaFactory ff = Lib.ff;
	IExternalReasoner contr = new Contr();

	IProverSequent contrSeq;
	Predicate goal,newgoal,newgoalFh;
	Predicate contrHypPred;
	Hypothesis contrHyp;
	
	@Override
	public void setUp(){
		contrHypPred = ff.parsePredicate("x=1").getParsedPredicate();
		goal = ff.parsePredicate("x ∈ℕ").getParsedPredicate();
		newgoalFh = ff.parsePredicate("¬x∈ℕ⇒¬x=1").getParsedPredicate();
		newgoal = ff.parsePredicate("¬x∈ℕ⇒⊥").getParsedPredicate();
		
		ITypeEnvironment typeEnvironment = Lib.typeCheck(contrHypPred,goal,newgoalFh,newgoal);
		assertNotNull(typeEnvironment);
		contrHyp = new Hypothesis(contrHypPred);
		contrSeq = new SimpleProverSequent(typeEnvironment,Hypothesis.Hypotheses(contrHyp),goal);
	}
	
	public void testApply() {	
		Input I;
		Predicate newGoalPredicate;
		I = new Input(contrHyp);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(contrSeq,contr,I);
		assertTrue(newGoalPredicate.equals(newgoalFh));
		
		I = new Input();
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(contrSeq,contr,I);
		assertTrue(newGoalPredicate.equals(newgoal));
		//System.out.println(newGoalPredicate);
		
	}
	
}
