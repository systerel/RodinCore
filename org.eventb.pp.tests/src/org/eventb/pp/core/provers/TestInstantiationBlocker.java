package org.eventb.pp.core.provers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cVar;
import junit.framework.TestCase;

import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;
import org.eventb.pp.Util;

@SuppressWarnings("unused")
public class TestInstantiationBlocker extends TestCase {

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
		x1 = cVar();
		x2 = cVar();
		x3 = cVar();
		y1 = cVar();
		y2 = cVar();
		y3 = cVar();
		z1 = cVar();
		z2 = cVar();
		z3 = cVar();
	}
	
	IClause[] inputClauses1() {
		return new IClause[] {
			cClause(cPred(0,x1),cNotPred(1,x1,ey)),
			cClause(cNotPred(0,a)),
			cClause(cPred(1,y1,y2),cNotPred(0,y2))
		};
	}
	
	IClause[] inputClauses2() {
		return new IClause[] {
			cEqClause(cNotPred(0,x1),cNotPred(1,x1,ey)),
			cClause(cNotPred(0,a)),
			cEqClause(cNotPred(1,y1,y2),cNotPred(0,y2))
		};
	}
	
	// this is not permitted any more
//	IClause[] inputClauses3() {
//		return new IClause[] {
//			cClause(cPred(0,x1,x2),cNotPred(1,x1,ey)),
//			cClause(cNotPred(0,ez,y1)),
//			cClause(cPred(1,z1,z2),cNotPred(0,z2,z3))
//		};
//	}
		
	public void testDisjunctive() {
		initVars();
		doTest(inputClauses1());
	}
	
	public void testEquivalence() {
		initVars();
		doTest(inputClauses2());
	}
	
//	public void testWithExistential() {
//		initVars();
//		doTest(inputClauses3());
//	}
	
	public void doTest(IClause[] clauses) {
		PredicateProver prover = new PredicateProver(new VariableContext());
		
		for (IClause clause : clauses) {
			prover.newClause(clause);
		}
		OnePointRule simp1 = new OnePointRule();
		ExistentialSimplifier simp2 = new ExistentialSimplifier();
		
		boolean stop = false;
		while (!stop) {
			IClause clause = prover.next();
			if (clause == null) {
				stop = true;
			}
			else {
				clause = clause.simplify(simp1);
				clause = clause.simplify(simp2);
				prover.newClause(clause);
			}
		}
		assertTrue(prover.isBlocked());
		
	}
	
}
