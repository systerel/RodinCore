package org.eventb.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.ProofStrategy;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;
import org.eventb.pptrans.Translator;

public class RodinTests extends AbstractPPTest {
	
	private static class TestPair {
		Set<Predicate> hypotheses;
		Predicate goal;
		boolean result;
		
		public TestPair(Set<Predicate> hypotheses, Predicate goal, boolean result) {
			this.hypotheses = hypotheses;
			this.goal = goal;
			this.result = result;
		}
		
		public TestPair(Set<String> hypotheses, String goal, boolean result) {
			this.hypotheses = new HashSet<Predicate>();
			for (String string : hypotheses) {
				this.hypotheses.add(ff.parsePredicate(string).getParsedPredicate());
			}
			this.goal = ff.parsePredicate(goal).getParsedPredicate();
			this.result = result;
		}
	}
	
	private static FormulaFactory ff = FormulaFactory.getDefault();
	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addName("f", REL(ty_S, ty_T));
		env.addName("g", REL(ty_T, ty_U));
		
		env.addName("a", ty_U);
//		env.addName("SIG", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("B"), ff.makeGivenType("S"))));
//		env.addName("fst", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("S"), ff.makeGivenType("B"))));
		
		env.addName("A", POW(ty_S));
		env.addName("B", POW(ty_S));
//		env.addName("r", REL(ty_S, ty_S));
		env.addName("R", POW(ty_T));
		env.addName("rtbl", REL(ty_S,ty_T));
	}
	
	
	public void testAll() {
		ProofStrategy.DEBUG = true;
		PPProof.DEBUG = true;
		PredicateProver.DEBUG = true;
		for (TestPair test : tests) {
			doTest(test);
		}
	}
	
	TestPair[] tests = new TestPair[]{
			new TestPair(mSet("∀r·r∈R⇒nxt(r)∈rtbl∼[{r}] ∖ {lst(r)} ⤖ rtbl∼[{r}] ∖ {fst(r)}","nxt∈R → (B ⤔ B)"),
					"∀r·r∈R⇒r∈dom(nxt)∧nxt∼;({r} ◁ nxt)⊆id(ℙ(B × B))∧r∈dom(nxt)∧nxt∼;({r} ◁ nxt)⊆id(ℙ(B × B))",true),
//			new TestPair(mSet("A ⊆ B"),"r[A] ⊆ r[B]",true),
//			new TestPair(mSet("a = c"),"a ∈ {c,d}",true),
//			new TestPair(mSet("(∃x,y·f(x)=y ∧ g(y)=a)"),"(∃x·(g∘f)(x)=a)",true),
//			new TestPair(mSet("(∀x·(∃x0·x ↦ x0∈SIG)⇒(∃x0·x0 ↦ x∈fst))" +
//					"∧" +
//					"(∀x,x0,x1·x ↦ x0∈SIG∧x ↦ x1∈SIG⇒x0=x1)" +
//					"∧" +
//					"(∀x·(∃x0·x0 ↦ x∈fst)⇒(∃x0·x ↦ x0∈SIG))" +
//					"∧" +
//					"(∀x·∃x0·x0 ↦ x∈SIG)" +
//					"∧" +
//					"(∀x,x0,x1·x0 ↦ x∈SIG∧x1 ↦ x∈SIG⇒x0=x1)"),"⊥",false)
	};
	
	private void doTest(TestPair test) {
//		ITypeEnvironment env = ff.makeTypeEnvironment();

		
		List<Predicate> translatedHypotheses = new ArrayList<Predicate>();
		for (Predicate pred : test.hypotheses) {
			typeCheck(pred,env);
			translatedHypotheses.add(translate(pred));
		}
		typeCheck(test.goal, env);
		Predicate translatedGoal = translate(test.goal);
		
		System.out.println("Translated hypotheses: "+translatedHypotheses);
		System.out.println("Translated goal: "+translatedGoal);
		
		PPProof prover = new PPProof(translatedHypotheses,translatedGoal);
		prover.prove(200);
		PPResult result = prover.getResult();
		assertEquals(result.getResult()==Result.valid, test.result);
	}
	
	private Predicate translate(Predicate predicate) {
		predicate = Translator.decomposeIdentifiers(predicate, ff);
		predicate = Translator.reduceToPredicateCalulus(predicate, ff);
		predicate = Translator.simplifyPredicate(predicate, ff);
		return predicate;
	}
	
	private void typeCheck(Predicate predicate, ITypeEnvironment environment) {
		ITypeCheckResult result = predicate.typeCheck(environment);
		assertTrue(result.toString(),result.isSuccess());
		environment.addAll(result.getInferredEnvironment());
	}
	
}
