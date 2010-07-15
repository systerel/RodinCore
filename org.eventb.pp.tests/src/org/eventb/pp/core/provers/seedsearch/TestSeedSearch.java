package org.eventb.pp.core.provers.seedsearch;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AA;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AAA;
import static org.eventb.internal.pp.core.elements.terms.Util.d1AA;
import static org.eventb.internal.pp.core.elements.terms.Util.d2A;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eventb.internal.pp.core.ILevelController;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.search.RandomAccessList;
import org.junit.Test;

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
					mList(cClause(cNotPred(d0AA,x,a),cPred(d2A,x))),
					cClause(cPred(d0AA,evar0,x)),
					mList(cClause(cPred(d0AA,evar0,a)))
			),
			new TestPair(
					mList(cClause(cNotPred(d0AA,x,b),cPred(d2A,x))),
					cClause(cPred(d0AA,evar0,x)),
					mList(cClause(cPred(d0AA,evar0,b)))
			),
			new TestPair(
					mList(	cClause(cNotPred(d0AA,x,y),cPred(d1AA,x,y)),
							cClause(cNotPred(d1AA,x,a),cPred(d2A,x))),
					cClause(cPred(d0AA,evar0,x)),
					mList(cClause(cPred(d0AA,evar0,a)))
			),
			
			new TestPair(
					mList(	cClause(cNotPred(d0AA,x,b),cPred(d2A,x)),
							cClause(cNotPred(d0AA,x,c),cPred(d2A,x))
					),
					cClause(cPred(d0AA,evar0,x)),
					mList(	cClause(cPred(d0AA,evar0,c)),
							cClause(cPred(d0AA,evar0,b)))					
			),
			
			new TestPair(
					mList(cClause(cNotPred(d0AA,x,a),cPred(d2A,x))),
					cClause(cPred(d0AA,evar0,x)),
					mList(cClause(cPred(d0AA,evar0,a)))
			),
			
			new TestPair(
					mList(cEqClause(cPred(d0AA,x,a),cPred(d2A,x))),
					cClause(cPred(d0AA,evar0,x)),
					mList(cClause(cPred(d0AA,evar0,a)))
			),
			
			new TestPair(
					mList(	cClause(cNotPred(d0AAA,x,y,a),cPred(d2A,y)),
							cClause(cNotPred(d2A,b),cPred(d1AA,a,b))),
					cClause(cPred(d0AAA,evar0,x,y)),
					mList(cClause(cPred(d0AAA,evar0,b,x)))
			),
			
	};
	
	public void doTest(TestPair pair) {
		final SeedSearchProver prover = getProver();
		
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
		final VariableContext context = new VariableContext();
		final SeedSearchProver prover = new SeedSearchProver(context, new ILevelController(){
			public Level getCurrentLevel() {
				return BASE;
			}

			public void nextLevel() {
				// nothing
			}
		});
		return prover;
	}
	
    @Test
	public void testEmptyResult() {
		final SeedSearchProver prover = getProver();
		assertEquals(prover.next(false), ProverResult.EMPTY_RESULT);
		assertEquals(prover.addClauseAndDetectContradiction(cClause(cProp(0))), ProverResult.EMPTY_RESULT);
	}
		
	
    @Test
	public void test() {
		for (TestPair test : tests) {
			doTest(test);
		}
	}
	
}
