package org.eventb.pp.core.provers.predicate;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.cVar;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.loader.clause.VariableContext;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

@SuppressWarnings("unused")
public class TestInstantiationBlocker extends AbstractPPTest {

	private static Variable x1;
	private static Variable x2;
	private static Variable x3;
	private static Variable y1;
	private static Variable y2;
	private static Variable y3;
	private static Variable z1;
	private static Variable z2;
	private static Variable z3;
	
	
	private static LocalVariable ey = Util.cELocVar(0);
	private static LocalVariable ez = Util.cELocVar(1);
	
	private static Constant a = Util.cCons("a");
	
	private static void initVars() {
		x1 = cVar(1);
		x2 = cVar(2);
		x3 = cVar(3);
		y1 = cVar(4);
		y2 = cVar(5);
		y3 = cVar(6);
		z1 = cVar(7);
		z2 = cVar(8);
		z3 = cVar(9);
	}
	
	// this is not permitted any more
//	Clause[] inputClauses3() {
//		return new Clause[] {
//			cClause(cPred(0,x1,x2),cNotPred(1,x1,ey)),
//			cClause(cNotPred(0,ez,y1)),
//			cClause(cPred(1,z1,z2),cNotPred(0,z2,z3))
//		};
//	}
		
	public void testDisjunctive() {
		initVars();
		doTest(	cClause(cPred(0,x1),cNotPred(1,x1,b)), 0,
				cClause(cNotPred(0,a)));
	}
	
	public void testEquivalence() {
		initVars();
		doTest(	cEqClause(cNotPred(0,x1),cNotPred(1,x1,b)), 0,
				cClause(cNotPred(0,a)));
	}
	
	public void doTest(Clause clause, int position, Clause unitClause) {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		
		int i = 0;
		while (!clause.checkIsBlockedOnInstantiationsAndUnblock()) {
			inferrer.setUnitClause(unitClause);
			inferrer.setPosition(position);
			clause.infer(inferrer);
			i++;
		}
		
		assertTrue(i>0);
		inferrer.setUnitClause(unitClause);
		inferrer.setPosition(position);
		clause.infer(inferrer);
		assertFalse(clause.checkIsBlockedOnInstantiationsAndUnblock());
	}
	
	
//	public void testWithPredicateProver() {
//		PredicateProver prover = new PredicateProver(new VariableContext());
//		prover.initialize(new ClauseSimplifier());
//		Clause unitClause = cClause(cNotPred(0,a));
//		
//		prover.addClauseAndDetectContradiction(cClause(cPred(0,x1),cProp(1)));
//		prover.addClauseAndDetectContradiction(unitClause);
//		
//		int i = 0;
//		ProverResult newClause = prover.next(false);
//		while (!newClause.equals(ProverResult.EMPTY_RESULT)) {
//			prover.addClauseAndDetectContradiction(unitClause);
//			newClause = prover.next(false);
//			i++;
//		}
//		assertTrue(i > 0);
//	}
	
}
