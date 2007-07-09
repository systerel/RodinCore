package org.eventb.pp.core.provers.seedsearch;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.mList;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.loader.clause.VariableContext;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

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
					cClause(cPred(0,cELocVar(0),x)),
					mList(cClause(cPred(0,cELocVar(0),a)))
			),
			new TestPair(
					mList(cClause(cNotPred(0,x,b),cPred(2,x))),
					cClause(cPred(0,cELocVar(0),x)),
					mList(cClause(cPred(0,cELocVar(0),b)))
			),
			new TestPair(
					mList(	cClause(cNotPred(0,x,y),cPred(1,x,y)),
							cClause(cNotPred(1,x,a),cPred(2,x))),
					cClause(cPred(0,cELocVar(0),x)),
					mList(cClause(cPred(0,cELocVar(0),a)))
			),
			
			new TestPair(
					mList(	cClause(cNotPred(0,x,b),cPred(2,x)),
							cClause(cNotPred(0,x,c),cPred(2,x))
					),
					cClause(cPred(0,cELocVar(0),x)),
					mList(	cClause(cPred(0,cELocVar(0),c)),
							cClause(cPred(0,cELocVar(0),b)))					
			),
			
			new TestPair(
					mList(cClause(cNotPred(0,x,a),cPred(2,x))),
					cClause(cPred(0,cELocVar(0),x)),
					mList(cClause(cPred(0,cELocVar(0),a)))
			),
			
			new TestPair(
					mList(cEqClause(cPred(0,x,a),cPred(2,x))),
					cClause(cPred(0,cELocVar(0),x)),
					mList(cClause(cPred(0,cELocVar(0),a)))
			),
			
			new TestPair(
					mList(	cClause(cNotPred(0,x,y,a),cPred(2,y)),
							cClause(cNotPred(2,b),cPred(1,a,b))),
					cClause(cPred(0,cELocVar(0),x,y)),
					mList(cClause(cPred(0,cELocVar(0),b,x)))
			),
			
	};
	
	public void doTest(TestPair pair) {
		SeedSearchProver.DEBUG = true;
		
		IVariableContext context = new VariableContext();
		SeedSearchProver prover = new SeedSearchProver(context);
		IterableHashSet<Clause> clauses = new IterableHashSet<Clause>();
		for (Clause clause : pair.originalClauses) {
			clauses.appends(clause);
		}
		ClauseSimplifier simplifier = new ClauseSimplifier();
		
		prover.initialize(simplifier);
		prover.addClauseAndDetectContradiction(pair.unitClause);
		ProverResult result = prover.next();
		int i = 0;
		while (result != null) {
			for (Clause clause : result.getGeneratedClauses()) {
				assertEquals(pair.result.get(i),clause);
				i++;
			}
			result = prover.next();
		}
		assertNull(prover.next());
	}
	
	public void test() {
		for (TestPair test : tests) {
			doTest(test);
		}
	}
	
}
