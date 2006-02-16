package org.eventb.core.prover.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.ExI;
import org.eventb.core.prover.externalReasoners.ExI.Input;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class ExITest extends TestCase {

	IExternalReasoner exI = new ExI();
	
	IProverSequent Ex1seq,Ex2seq,nonExSeq;
	Predicate goal1,goal2,goal1inst,goal2inst,goal2_1inst;
	
	@Override
	public void setUp(){
		goal1 = Lib.parsePredicate("∃x· x ∈ℕ");
		goal1inst = Lib.parsePredicate("⊤∧a ∈ℕ");
		goal2 = Lib.parsePredicate("∃x,y· x ∈ℕ∧y = x÷a");
		goal2_1inst = Lib.parsePredicate("⊤∧(∃y· a ∈ℕ∧y = a÷a)");
		goal2inst = Lib.parsePredicate("⊤∧(a ∈ℕ∧1 = a÷a)");
		Predicate hyp = Lib.parsePredicate("a≠0");
		ITypeEnvironment typeEnvironment = Lib.typeCheck(goal1,goal1inst,goal2,goal2_1inst,goal2inst,hyp);
		assertNotNull(typeEnvironment);	
		Set<Hypothesis> Hyps = Hypothesis.Hypotheses(hyp);
		Ex1seq = new SimpleProverSequent(typeEnvironment,Hyps,goal1);
		Ex2seq = new SimpleProverSequent(typeEnvironment,Hyps,goal2);
		nonExSeq = new SimpleProverSequent(typeEnvironment,Hyps,hyp);
	}
	
	/*
	 * Test method for 'org.eventb.core.prover.plugins.exI_WD.apply(ProverSequent, PluginInput)'
	 */
	public final void testApply() {
		Input I;
		Predicate newGoalPredicate;

		String[] witnesses1 = {"a"};
		I = new Input(witnesses1);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(Ex1seq,exI,I);
		assertTrue(newGoalPredicate.equals(goal1inst));

		String[] witnesses2_1 = {"a",null};
		I = new Input(witnesses2_1);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(Ex2seq,exI,I);
		assertTrue(newGoalPredicate.equals(goal2_1inst));
		
		String[] witnesses2 = {"a","1"};
		I = new Input(witnesses2);
		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(Ex2seq,exI,I);
		assertTrue(newGoalPredicate.equals(goal2inst));

	}


}
