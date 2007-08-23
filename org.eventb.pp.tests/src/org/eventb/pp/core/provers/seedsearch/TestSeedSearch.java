package org.eventb.pp.core.provers.seedsearch;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;

import java.util.List;

import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.search.RandomAccessList;

public class TestSeedSearch extends AbstractPPTest {

	
	private static class TestPair {
		List<Clause> originalClauses;
		Clause unitClause;
		List<Clause> result;
		
		TestPair(List<Clause> originalClauses, Clause unitClause, List<Clause> result) {
			this.originalClauses = originalClauses;
			this.unitClause = unitClause;
			this.result = result;
		}
	}
	
	TestPair[] tests = new TestPair[]{
			new TestPair(
					mList(cClause(cNotPred(0,x,a),cPred(2,x))),
					cClause(cPred(0,evar0,x)),
					mList(cClause(cPred(0,evar0,a)))
			),
			new TestPair(
					mList(cClause(cNotPred(0,x,b),cPred(2,x))),
					cClause(cPred(0,evar0,x)),
					mList(cClause(cPred(0,evar0,b)))
			),
			new TestPair(
					mList(	cClause(cNotPred(0,x,y),cPred(1,x,y)),
							cClause(cNotPred(1,x,a),cPred(2,x))),
					cClause(cPred(0,evar0,x)),
					mList(cClause(cPred(0,evar0,a)))
			),
			
			new TestPair(
					mList(	cClause(cNotPred(0,x,b),cPred(2,x)),
							cClause(cNotPred(0,x,c),cPred(2,x))
					),
					cClause(cPred(0,evar0,x)),
					mList(	cClause(cPred(0,evar0,c)),
							cClause(cPred(0,evar0,b)))					
			),
			
			new TestPair(
					mList(cClause(cNotPred(0,x,a),cPred(2,x))),
					cClause(cPred(0,evar0,x)),
					mList(cClause(cPred(0,evar0,a)))
			),
			
			new TestPair(
					mList(cEqClause(cPred(0,x,a),cPred(2,x))),
					cClause(cPred(0,evar0,x)),
					mList(cClause(cPred(0,evar0,a)))
			),
			
			new TestPair(
					mList(	cClause(cNotPred(0,x,y,a),cPred(2,y)),
							cClause(cNotPred(2,b),cPred(1,a,b))),
					cClause(cPred(0,evar0,x,y)),
					mList(cClause(cPred(0,evar0,b,x)))
			),
			
	};
	
	public void doTest(TestPair pair) {
		SeedSearchProver.DEBUG = true;
		
		SeedSearchProver prover = getProver();
		
		RandomAccessList<Clause> clauses = new RandomAccessList<Clause>();
		for (Clause clause : pair.originalClauses) {
			clauses.add(clause);
		}

		prover.addClauseAndDetectContradiction(pair.unitClause);
		ProverResult result = prover.next(false);
		int i = 0;
		while (!result.equals(ProverResult.EMPTY_RESULT)) {
			for (Clause clause : result.getGeneratedClauses()) {
				assertEquals(pair.result.get(i),clause);
				i++;
			}
			result = prover.next(false);
		}
	}

	private SeedSearchProver getProver() {
		VariableContext context = new VariableContext();
		SeedSearchProver prover = new SeedSearchProver(context);
		return prover;
	}
	
	public void testEmptyResult() {
		SeedSearchProver prover = getProver();
		assertEquals(prover.next(false), ProverResult.EMPTY_RESULT);
		assertEquals(prover.addClauseAndDetectContradiction(cClause(cProp(0))), ProverResult.EMPTY_RESULT);
	}
		
	
	public void test() {
		for (TestPair test : tests) {
			doTest(test);
		}
	}
	
}
