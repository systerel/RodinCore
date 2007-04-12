package org.eventb.pp.core.simplifiers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.mList;
import junit.framework.TestCase;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.simplifiers.EqualitySimplifier;
import org.eventb.pp.Util;

public class TestEqualitySimplifier extends TestCase {

	
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
			// simple inequality
			new TestPair(
					cClause(cNEqual(a,a)),
					cClause()
			),
			// simple equality
			new TestPair(
					cClause(cEqual(a,a)),
					null
			),
			// simple inequality with variables
			new TestPair(
					cClause(cNEqual(var0,var0)),
					cClause()
			),
			// simple equality with variables
			new TestPair(
					cClause(cEqual(var0,var0)),
					null
			),
			// simple inequality with variables
			new TestPair(
					cClause(cNEqual(evar0,evar0)),
					cClause()
			),
			// do nothing
			new TestPair(
					cClause(cNEqual(a,b)),
					cClause(cNEqual(a,b))
			),
			// do nothing
			new TestPair(
					cClause(cNEqual(var0,var1)),
					cClause(cNEqual(var0,var1))
			),
			// do nothing
			new TestPair(
					cClause(cNEqual(evar0,evar1)),
					cClause(cNEqual(evar0,evar1))
			),
			
			// more than one literal
			new TestPair(
					cClause(cNEqual(a,a),cPred(0,a)),
					cClause(cPred(0,a))
			),
			new TestPair(
					cClause(cNEqual(a,a),cNEqual(var0,var0),cPred(0,a)),
					cClause(cPred(0,a))
			),
			new TestPair(
					cClause(cNEqual(a,a),cEqual(a,a)),
					null
			),
			new TestPair(
					cClause(cPred(0,a),cEqual(a,a)),
					null
			),
			
			// EQUIVALENCE
			new TestPair(
					cEqClause(cNEqual(a, a),cPred(0,a)),
					cClause(cNotPred(0,a))
			),
			new TestPair(
					cEqClause(cNEqual(a,a),cEqual(a,a)),
					cClause()
			),
			new TestPair(
					cEqClause(cNEqual(a,a),cEqual(a,a),cPred(0,a)),
					cClause(cNotPred(0,a))
			),
			new TestPair(
					cEqClause(cNEqual(a,a),cNEqual(a,a),cPred(0,a)),
					cClause(cPred(0,a))
			),
			new TestPair(
					cEqClause(cEqual(a,a),cEqual(a,a),cPred(0,a)),
					cClause(cPred(0,a))
			),
			new TestPair(
					cEqClause(cEqual(a,a),cEqual(a,a)),
					null
			),
			
			// EQUIVALENCE and conditions
			new TestPair(
					cEqClause(mList(cPred(0,a),cPred(1,a)),cNEqual(a,a)),
					cEqClause(cPred(0,a),cPred(1,a))
			),
			new TestPair(
					cEqClause(mList(cPred(0,a),cPred(1,a)),cNEqual(a,a),cNEqual(b,b)),
					cEqClause(cPred(0,a),cPred(1,a))
			),
			new TestPair(
					cEqClause(mList(cPred(0,a),cNEqual(a,a)),cNEqual(a,a)),
					cClause(cNotPred(0,a))
			),
			new TestPair(
					cEqClause(mList(cPred(0,a),cNEqual(a,a)),cEqual(a,a)),
					null
			),
			
	};
	
	private IVariableContext variableContext() {
		VariableContext context = new VariableContext();
		context.putInCache(var00);
		return context;
	}
	
	public void testEquality() {
		for (TestPair test : tests) {
			EqualitySimplifier rule = new EqualitySimplifier(variableContext(),null);
			
			Constant.uniqueIdentifier = 0;
			IClause actual = test.input.simplify(rule);
			assertEquals(test.input.toString(),test.output,actual);
		}
	}
}
