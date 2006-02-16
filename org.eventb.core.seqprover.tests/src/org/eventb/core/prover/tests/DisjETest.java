package org.eventb.core.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.DisjE;
import org.eventb.core.prover.externalReasoners.DisjE.Input;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class DisjETest extends TestCase {

	FormulaFactory ff = Lib.ff;
	IExternalReasoner disjE = new DisjE();

	IProverSequent disjSeq;
	Predicate goal,newgoal;
	Predicate disjHypPred;
	Hypothesis disjHyp;
	
	@Override
	public void setUp(){
		disjHypPred = Lib.parsePredicate("x=1 ∨x=2 ∨x=3");
		goal = Lib.parsePredicate("x ∈ℕ");
		newgoal = Lib.parsePredicate("(x=1⇒x∈ℕ)∧(x=2⇒x∈ℕ)∧(x=3⇒x∈ℕ)");
		ITypeEnvironment typeEnvironment = Lib.typeCheck(disjHypPred,goal,newgoal);
		assertNotNull(typeEnvironment);
		disjHyp = new Hypothesis(disjHypPred);
		disjSeq = new SimpleProverSequent(typeEnvironment,Hypothesis.Hypotheses(disjHypPred),goal);
	}

	public void testApply() {	
		Input I;
		Predicate newGoalPredicate;
		I = new Input(disjHyp);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(disjSeq,disjE,I);
		
		assertTrue(newGoalPredicate.equals(newgoal));
		// System.out.println(newGoalPredicate);
		
	}
	
}
