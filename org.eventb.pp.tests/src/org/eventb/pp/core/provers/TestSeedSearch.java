package org.eventb.pp.core.provers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.mList;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.datastructure.DataStructureWrapper;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.pp.Util;

public class TestSeedSearch extends TestCase {

	private static Variable x = Util.cVar();
	private static Variable y = Util.cVar();
	private static Constant a = Util.cCons("a");
	private static Constant b = Util.cCons("b");
	private static Constant c = Util.cCons("c");
	
	private static class TestPair {
		List<IClause> originalClauses;
		IClause unitClause;
		List<IClause> result;
		
		private TestPair(List<IClause> originalClauses, IClause unitClause, List<IClause> result) {
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
		IterableHashSet<IClause> clauses = new IterableHashSet<IClause>();
		for (IClause clause : pair.originalClauses) {
			clauses.appends(clause);
		}
		DataStructureWrapper dsWrapper = new DataStructureWrapper(clauses);
		
		prover.initialize(null, dsWrapper, null);
		prover.newClause(pair.unitClause);
		for (IClause clause : pair.result) {
			assertEquals(clause,prover.next());
		}
		assertNull(prover.next());
	}
	
	public void test() {
		for (TestPair test : tests) {
			doTest(test);
		}
	}
	
}
