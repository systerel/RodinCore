package org.eventb.pp.core.simplifiers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

public class TestExistentialSimplification extends AbstractPPTest {
	
	private class TestPair {
		Clause input, output;
		
		TestPair(Clause input, Clause output) {
			this.input = input;
			this.output = output;
		}
	}
	
	
	static Constant newCons0 = Util.cCons("0",A);
	static Constant newCons1 = Util.cCons("1",A);
	
	
	
	TestPair[] tests = new TestPair[] {
			// UNIT clauses
			new TestPair(
					cClause(cProp(0)),
					cClause(cProp(0))
			),
			new TestPair(
					cClause(cPred(0,var0)),
					cClause(cPred(0,var0))
			),
			new TestPair(
					cClause(cPred(0,evar0)),
					cClause(cPred(0,newCons0))
			),
			new TestPair(
					cClause(cPred(0,evar0,var0)),
					cClause(cPred(0,evar0,var0))
			),
			new TestPair(
					cClause(cPred(0,evar0,var0)),
					cClause(cPred(0,evar0,var0))
			),
			new TestPair(
					cClause(cPred(0,evar0,a)),
					cClause(cPred(0,newCons0,a))
			),
			new TestPair(
					cClause(cEqual(var0,evar0)),
					cClause(cEqual(var0,evar0))
			),
			new TestPair(
					cClause(cEqual(a,evar0)),
					cClause(cEqual(a,newCons0))
			),
			
			// non unit disjunctive
			new TestPair(
					cClause(cPred(0,evar0),cPred(1,evar1)),
					cClause(cPred(0,newCons0),cPred(1,newCons1))
			),
			
			// FORALL in equivalence clauses
			new TestPair(
					cEqClause(cPred(0,evar0),cPred(1,evar1)),
					cEqClause(cPred(0,evar0),cPred(1,evar1))
			),
			//simple
			new TestPair(
					cEqClause(cPred(0,fvar0),cPred(1,var0)),
					cEqClause(cPred(0,fvar0),cPred(1,var0))
			),
			//2 foralls
			new TestPair(
					cEqClause(cPred(0,fvar0),cPred(1,fvar0)),
					cEqClause(cPred(0,fvar0),cPred(1,fvar0))
			),
			// mixed
			new TestPair(
					cEqClause(cPred(0,fvar0),cPred(1,evar0)),
					cEqClause(cPred(0,fvar0),cPred(1,evar0))
			),
			
	};
	
	public void testExistential() {
		for (TestPair test : tests) {
			ExistentialSimplifier rule = new ExistentialSimplifier(new MyVariableContext());
			Clause actual = test.input.simplify(rule);
			assertEquals(test.output,actual);
		}
	}
	
	static class MyVariableContext implements IVariableContext {
		int i=0;
		
		public Constant getNextFreshConstant(Sort sort) {
			if (i==0) {i++; return newCons0;}
			if (i==1) {i++; return newCons1;}
			return null;
		}

		public LocalVariable getNextLocalVariable(boolean isForall, Sort sort) {
			return null;
		}

		public Variable getNextVariable(Sort sort) {
			return null;
		}
	}
	
	
//	public void testExistentialBlocking() {
//		Clause clause = cClause(cPred(0, var0, evar0));
//	}
}
