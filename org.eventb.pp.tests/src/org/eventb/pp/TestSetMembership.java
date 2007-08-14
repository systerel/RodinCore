package org.eventb.pp;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cCons;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.Clause;

public class TestSetMembership extends AbstractPPTest {

	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	
	static {
		env.addName("C", POW(ff.makeGivenType("A")));
	}
	
	private static class TestPair {
		Set<Predicate> hypotheses;
		Predicate goal;
		Collection<Clause> result;
		
		public TestPair(Set<String> hypotheses, String goal, Clause... result) {
			this.hypotheses = new HashSet<Predicate>();
			for (String string : hypotheses) {
				this.hypotheses.add(ff.parsePredicate(string).getParsedPredicate());
			}
			this.goal = ff.parsePredicate(goal).getParsedPredicate();
			this.result = new HashSet<Clause>();
			this.result.addAll(Arrays.asList(result));
		}
		
		void typeCheck(ITypeEnvironment tenv) {
			for (Predicate pred : hypotheses) {
				typeCheck(pred,tenv);
			}
			typeCheck(goal,tenv);	
		}
		
		private void typeCheck(Predicate predicate, ITypeEnvironment environment) {
			ITypeCheckResult result = predicate.typeCheck(environment);
			assertTrue(result.toString(),result.isSuccess());
			environment.addAll(result.getInferredEnvironment());
		}
	}
	
	public void testEqualityInGoal() {
		doTest(new TestPair(new HashSet<String>(),
				"C = B",
				cClause(cNotPred(1,cELocVar(1))),
				cClause(cNEqual(cCons("B"),cCons("C"))),
				cEqClause(cPred(1,x), cPred(0, x, cCons("C")), cPred(0, x, cCons("B"))),
				cClause(cPred(0,x,cCons("A")))
		));
	}
	
	
	public void doTest(TestPair test) {
		ITypeEnvironment tenv = env.clone();
		test.typeCheck(tenv);
		
		
		PPProof proof = new PPProof(test.hypotheses, test.goal);
		proof.translate();
		proof.load();
		
		assertTrue(test.result.containsAll(proof.getClauses()));
		assertTrue(proof.getClauses().containsAll(test.result));
		
	}
	
}
