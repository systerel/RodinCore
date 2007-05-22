package org.eventb.pp.core.simplifiers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.mList;

import java.util.ArrayList;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.simplifiers.EqualitySimplifier;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

@SuppressWarnings("unchecked")
public class TestEqualitySimplifier extends AbstractPPTest {

	
	private class TestPair {
		IClause input, output;
		
		TestPair(IClause input, IClause output) {
			this.input = input;
			this.output = output;
		}
	}
	
	
	private static Variable var0 = Util.cVar();
	private static Variable var00 = Util.cVar();
	private static Variable var1 = Util.cVar();
	
	
	TestPair[] tests = new TestPair[] {
			new TestPair(
					cClause(cNEqual(a,a)),
					FALSE
			),
			// simple equality
			new TestPair(
					cClause(cEqual(a,a)),
					TRUE
			),
			// simple inequality with variables
			new TestPair(
					cClause(cNEqual(var0,var0)),
					FALSE
			),
			// simple equality with variables
			new TestPair(
					cClause(cEqual(var0,var0)),
					TRUE
			),
			// simple inequality with variables
			new TestPair(
					cClause(cNEqual(evar0,evar0)),
					FALSE
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
					TRUE
			),
			new TestPair(
					cClause(cPred(0,a),cEqual(a,a)),
					TRUE
			),
			
			// EQUIVALENCE
			new TestPair(
					cEqClause(cNEqual(a, a),cPred(0,a)),
					cClause(cNotPred(0,a))
			),
			new TestPair(
					cEqClause(cNEqual(a,a),cEqual(a,a)),
					FALSE
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
					TRUE
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
			
			// DISJUNCTIVE with conditions
			new TestPair(
					cClause(mList(cPred(0,a),cPred(1,a)),cNEqual(a,a)),
					cClause(cPred(0,a),cPred(1,a))
			),
			new TestPair(
					cClause(mList(cPred(0,a),cPred(1,a)),cNEqual(a,a),cNEqual(b,b)),
					cClause(cPred(0,a),cPred(1,a))
			),
			new TestPair(
					cClause(mList(cPred(0,a),cNEqual(a,a))),
					cClause(cPred(0,a))
			),
			new TestPair(
					cClause(new ArrayList<ILiteral>(),cNEqual(a,a)),
					FALSE
			),
			
	};
	
	private IVariableContext variableContext() {
		VariableContext context = new VariableContext();
		context.putInCache(var00);
		return context;
	}
	
	public void testEquality() {
		for (TestPair test : tests) {
			EqualitySimplifier rule = new EqualitySimplifier(variableContext());
			
			
			assertTrue(rule.canSimplify(test.input));
			IClause actual = test.input.simplify(rule);
			
			assertEquals(test.input.toString(),test.output,actual);
		}
	}
}
