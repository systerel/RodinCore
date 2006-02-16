package org.eventb.core.prover.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.AllF;
import org.eventb.core.prover.externalReasoners.AllF.Input;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;


public class AllFTest extends TestCase {

	FormulaFactory ff = Lib.ff;
	IExternalReasoner allF = new AllF();

	IProverSequent univSeq;
	Predicate goal,newgoal;
	Hypothesis univHyp;
	Predicate univHypPred;
	
	@Override
	public void setUp(){
		univHypPred = Lib.parsePredicate("∀x·x∈ℕ⇒ x+1∈ℕ");
		goal = Lib.parsePredicate("1 ∈ℕ");
		newgoal = Lib.parsePredicate("2≠0∧((4 ÷ 2∈ℕ⇒4 ÷ 2+1∈ℕ)⇒1∈ℕ)");
		
		ITypeEnvironment typeEnvironment = Lib.typeCheck(univHypPred,goal,newgoal);
		assertNotNull(typeEnvironment);
		
		univHyp = new Hypothesis(univHypPred);
		Set<Hypothesis> Hyps = Hypothesis.Hypotheses(univHyp);
		univSeq = new SimpleProverSequent(typeEnvironment,Hyps,goal);
	}

	/*
	 * Test method for 'org.eventb.core.prover.plugins.allF_WD.apply(ProverSequent, PluginInput)'
	 */
	public void testApply() {
		
		Input I;
		Predicate newGoalPredicate;
		String[] instantiations = {"4÷2"};
		I = new Input(instantiations,univHyp);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(univSeq,allF,I);
		assertTrue(newGoalPredicate.isTypeChecked());
		assertTrue(newGoalPredicate.equals(newgoal));
		// System.out.println(newGoalPredicate);

	}

}
