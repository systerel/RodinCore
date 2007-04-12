package org.eventb.pp.core.simplifiers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import junit.framework.TestCase;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.eventb.pp.Util;

public class TestExistentialSimplification extends TestCase {
	
	private class TestPair {
		IClause input, output;
		
		TestPair(IClause input, IClause output) {
			this.input = input;
			this.output = output;
		}
	}
	
	private static Constant a = Util.cCons("a");
	private static Constant b = Util.cCons("b");
	private static Constant c = Util.cCons("c");
	
	private static Constant newCons0 = Util.cCons("0");
	private static Constant newCons1 = Util.cCons("1");
	private static Constant newCons2 = Util.cCons("2");
	
	private static Variable var0 = Util.cVar();
	private static Variable var00 = Util.cVar();
	private static Variable var1 = Util.cVar();
	private static Variable var2 = Util.cVar();
	
	private static LocalVariable fvar0 = Util.cFLocVar(0);
	private static LocalVariable fvar1 = Util.cFLocVar(1);
	private static LocalVariable fvar2 = Util.cFLocVar(2);
	private static LocalVariable evar0 = Util.cELocVar(0);
	private static LocalVariable evar1 = Util.cELocVar(1);
	private static LocalVariable evar2 = Util.cELocVar(2);
	
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
	
	private ExistentialSimplifier rule = new ExistentialSimplifier();
	
	public void testExistential() {
		for (TestPair test : tests) {
			Constant.uniqueIdentifier = 0;
			IClause actual = test.input.simplify(rule);
			assertEquals(test.output,actual);
		}
	}
}
