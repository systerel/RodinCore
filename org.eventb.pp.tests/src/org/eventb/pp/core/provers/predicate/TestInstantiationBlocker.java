package org.eventb.pp.core.provers.predicate;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cVar;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;
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
		doTest(new Clause[] {
				cClause(cPred(0,x1),cNotPred(1,x1,b)),
				cClause(cNotPred(0,a)),
				cClause(cPred(1,y1,y2),cPred(0,y2))
			},cClause(cNotPred(1,a,b)));
	}
	
	public void testEquivalence() {
		initVars();
		doTest(new Clause[] {
				cEqClause(cNotPred(0,x1),cNotPred(1,x1,b)),
				cClause(cNotPred(0,a)),
				cEqClause(cNotPred(1,y1,y2),cNotPred(0,y2))
			},cClause(cNotPred(1,a,b)), cClause(cPred(1,a,b)), cClause(cNotPred(1,y1,a)));
	}
	
//	public void testWithExistential() {
//		initVars();
//		doTest(inputClauses3());
//	}
	
	public void doTest(Clause[] clauses, Clause... nextClauses) {
		PredicateProver prover = new PredicateProver(new VariableContext());
		OnePointRule simp1 = new OnePointRule();
		ExistentialSimplifier simp2 = new ExistentialSimplifier();
		ClauseSimplifier simplifier = new ClauseSimplifier();
		simplifier.addSimplifier(simp1);
		simplifier.addSimplifier(simp2);
		prover.initialize(simplifier);
		
		for (Clause clause : clauses) {
			prover.addClauseAndDetectContradiction(clause);
		}
		
		boolean stop = false;
		while (!stop) {
			ProverResult result = prover.next();
			if (result == null) {
				stop = true;
			}
			else {
				Clause clause = result.getGeneratedClauses().iterator().next();
				prover.addClauseAndDetectContradiction(clause);
			}
		}
		assertTrue(prover.isBlocked());
		for (Clause clause : nextClauses) {
			assertEquals(prover.next().getGeneratedClauses().iterator().next(), clause);
			assertNull(prover.next());
			assertTrue(prover.isBlocked());
		}
		
	}
	
}
