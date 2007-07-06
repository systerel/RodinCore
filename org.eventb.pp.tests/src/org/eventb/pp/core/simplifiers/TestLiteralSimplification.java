package org.eventb.pp.core.simplifiers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cCons;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.mList;

import java.util.ArrayList;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.simplifiers.LiteralSimplifier;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

public class TestLiteralSimplification extends AbstractPPTest {
	private class TestPair {
		Clause input, output;
		
		TestPair(Clause input, Clause output) {
			this.input = input;
			this.output = output;
		}
	}
	
	
	private static Variable var0 = Util.cVar();
	private static Variable var00 = Util.cVar();
	private static Variable var1 = Util.cVar();
	
	
	TestPair[] tests = new TestPair[] {
		new TestPair(
				cClause(cProp(0),cProp(0)),
				cClause(cProp(0))
		),
		new TestPair(
				cClause(cProp(0),cProp(1)),
				cClause(cProp(0),cProp(1))
		),
		new TestPair(
				cClause(cProp(0),cProp(0),cProp(0)),
				cClause(cProp(0))
		),
		new TestPair(
				cClause(cProp(0),cProp(0),cProp(1)),
				cClause(cProp(0),cProp(1))
		),
		new TestPair(
				cClause(cProp(0),cProp(0),cProp(1),cProp(1)),
				cClause(cProp(0),cProp(1))
		),
		
		new TestPair(
				cClause(cNotProp(0),cNotProp(0)),
				cClause(cNotProp(0))
		),
		new TestPair(
				cClause(cNotProp(0),cProp(1)),
				cClause(cNotProp(0),cProp(1))
		),
		new TestPair(
				cClause(cNotProp(0),cNotProp(0),cNotProp(0)),
				cClause(cNotProp(0))
		),
		new TestPair(
				cClause(cNotProp(0),cNotProp(0),cProp(1)),
				cClause(cNotProp(0),cProp(1))
		),
		new TestPair(
				cClause(cNotProp(0),cNotProp(0),cNotProp(1),cNotProp(1)),
				cClause(cNotProp(0),cNotProp(1))
		),
		new TestPair(
				cClause(cNotProp(0),cNotProp(0),cProp(1),cProp(1)),
				cClause(cNotProp(0),cProp(1))
		),
		
		
		new TestPair(
				cClause(cProp(0),cEqual(a,b)),
				cClause(cProp(0),cEqual(a,b))
		),
		new TestPair(
				cClause(cProp(0),cProp(0),cEqual(a,b)),
				cClause(cProp(0),cEqual(a,b))
		),
		
		new TestPair(
				cClause(cProp(0),cPred(1,a)),
				cClause(cProp(0),cPred(1,a))
		),
		new TestPair(
				cClause(cProp(0),cProp(0),cPred(1,a)),
				cClause(cProp(0),cPred(1,a))
		),
		
		
		new TestPair(
				cClause(cNotProp(0),cEqual(a,b)),
				cClause(cNotProp(0),cEqual(a,b))
		),
		new TestPair(
				cClause(cNotProp(0),cNotProp(0),cEqual(a,b)),
				cClause(cNotProp(0),cEqual(a,b))
		),
		
		new TestPair(
				cClause(cNotProp(0),cPred(1,a)),
				cClause(cNotProp(0),cPred(1,a))
		),
		new TestPair(
				cClause(cNotProp(0),cNotProp(0),cPred(1,a)),
				cClause(cNotProp(0),cPred(1,a))
		),
		
		
		new TestPair(
				cClause(cPred(0,a),cPred(0,a)),
				cClause(cPred(0,a))
		),
		new TestPair(
				cClause(cPred(0,var0),cPred(0,var0)),
				cClause(cPred(0,var0))
		),
		new TestPair(
				cClause(cPred(0,var0),cPred(0,var1)),
				cClause(cPred(0,var0),cPred(0,var1))
		),
		new TestPair(
				cClause(cPred(0,var0,a),cPred(0,var0,a)),
				cClause(cPred(0,var0,a))
		),
		new TestPair(
				cClause(cPred(0,evar0),cPred(0,fvar0)),
				cClause(cPred(0,evar0),cPred(0,fvar0))
		),
		
		new TestPair(
				cClause(cEqual(a,b),cEqual(a,b)),
				cClause(cEqual(a,b))
		),
		new TestPair(
				cClause(cEqual(a,b),cEqual(a,c)),
				cClause(cEqual(a,b),cEqual(a,c))
		),
		new TestPair(
				cClause(cEqual(a,b),cEqual(a,b),cProp(0)),
				cClause(cEqual(a,b),cProp(0))
		),
		new TestPair(
				cClause(cEqual(a,b),cEqual(a,b),cProp(0),cProp(0)),
				cClause(cEqual(a,b),cProp(0))
		),
		new TestPair(
				cClause(cEqual(a,b),cEqual(b,a)),
				cClause(cEqual(a,b))
		),
		
		new TestPair(
				cClause(cNEqual(a,b),cNEqual(a,b)),
				cClause(cNEqual(a,b))
		),
		new TestPair(
				cClause(cNEqual(a,b),cNEqual(a,c)),
				cClause(cNEqual(a,b),cNEqual(a,c))
		),
		new TestPair(
				cClause(cNEqual(a,b),cNEqual(a,b),cProp(0)),
				cClause(cNEqual(a,b),cProp(0))
		),
		new TestPair(
				cClause(cNEqual(a,b),cNEqual(a,b),cNotProp(0),cNotProp(0)),
				cClause(cNEqual(a,b),cNotProp(0))
		),
		new TestPair(
				cClause(cNEqual(a,b),cNEqual(b,a)),
				cClause(cNEqual(a,b))
		),
		new TestPair(
				cClause(cNEqual(a,b),cNEqual(cCons("b"),cCons("a"))),
				cClause(cNEqual(a,b))
		),
		
		
		// discard
		new TestPair(
				cClause(cProp(0),cNotProp(0)),
				TRUE
		),
		new TestPair(
				cClause(cPred(0,a),cNotPred(0,a)),
				TRUE
		),
		new TestPair(
				cClause(cPred(0,var0),cNotPred(0,var0)),
				TRUE
		),
		new TestPair(
				cClause(cPred(0,evar0),cNotPred(0,evar0)),
				cClause(cPred(0,evar0),cNotPred(0,evar0))
		),
		new TestPair(
				cClause(cPred(0,evar0),cNotPred(0,var0)),
				cClause(cPred(0,evar0),cNotPred(0,var0))
		),
		
		new TestPair(
				cClause(cEqual(a,b),cNEqual(a,b)),
				TRUE
		),
		new TestPair(
				cClause(cEqual(a,b),cNEqual(b,a)),
				TRUE
		),
		new TestPair(
				cClause(cEqual(evar0,var0),cNEqual(var00,var0)),
				cClause(cEqual(evar0,var0),cNEqual(var00,var0))
		),

		// EQUIVALENCE Clauses
		new TestPair(
				cEqClause(cProp(0),cProp(0)),
				TRUE
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0)),
				FALSE
		),
		new TestPair(
				cEqClause(cProp(0),cProp(0),cProp(1)),
				cClause(cProp(1))
		),
		new TestPair(
				cEqClause(cProp(0),cProp(0),cProp(1),cProp(2)),
				cEqClause(cProp(1),cProp(2))
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cProp(1)),
				cClause(cNotProp(1))
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cProp(1),cProp(2)),
				cEqClause(cNotProp(1),cProp(2))
		),
		new TestPair(
				cEqClause(cProp(0),cProp(0),cProp(1),cNotProp(1)),
				FALSE
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cProp(1),cNotProp(1)),
				TRUE
		),
		new TestPair(
				cEqClause(cNotProp(0),cNotProp(0),cProp(0)),
				cClause(cProp(0))
		),
		
		new TestPair(
				cEqClause(cPred(0,evar0),cPred(0,evar1)),
				cEqClause(cPred(0,evar0),cPred(0,evar1))
		),
		new TestPair(
				cEqClause(cPred(0,fvar0),cPred(0,fvar1)),
				cEqClause(cPred(0,fvar0),cPred(0,fvar1))
		),
		new TestPair(
				cEqClause(cPred(0,evar0),cNotPred(0,fvar0)),
				FALSE
		),
		new TestPair(
				cEqClause(cPred(0,evar0),cPred(0,var0)),
				cEqClause(cPred(0,evar0),cPred(0,var0))
		),
		new TestPair(
				cEqClause(cPred(0,fvar0),cPred(0,var0)),
				cEqClause(cPred(0,fvar0),cPred(0,var0))
		),
		
		new TestPair(
				cEqClause(cProp(0),cProp(0),cEqual(a,b),cEqual(a,b)),
				TRUE
		),
		new TestPair(
				cEqClause(cProp(0),cProp(0),cEqual(a,b),cEqual(a,b),cProp(0)),
				cClause(cProp(0))
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cEqual(a,b),cNEqual(a,b)),
				TRUE
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cEqual(a,b),cNEqual(a,b),cProp(0)),
				cClause(cProp(0))
		),
		
		// Equivalence with conditions
		new TestPair(
				cEqClause(mList(cProp(0),cNotProp(0),cNotProp(0)),cNEqual(a,b),cNEqual(a,b)),
				cClause(mList(cProp(0)),cNEqual(a,b))
		),
		new TestPair(
				cEqClause(mList(cNotProp(0),cNotProp(0)),cNEqual(a,b),cNEqual(a,b)),
				TRUE
		),
		new TestPair(
				cEqClause(mList(cProp(0),cProp(0)),cNEqual(a,b),cNEqual(a,b)),
				TRUE
		),
		new TestPair(
				cEqClause(mList(cProp(0),cNotProp(0)),cNEqual(a,b),cNEqual(a,b)),
				cClause(new ArrayList<Literal>(),cNEqual(a, b))
		),
		
		// Disjunctive with conditions
		new TestPair(
				cClause(mList(cProp(0),cNotProp(0)),cNEqual(a,b),cNEqual(a,b)),
				TRUE
		),
		new TestPair(
				cClause(mList(cNotProp(0),cNotProp(0)),cNEqual(a,b),cNEqual(a,b)),
				cClause(mList(cNotProp(0)),cNEqual(a,b))
		),
		new TestPair(
				cClause(mList(cProp(0),cProp(0)),cNEqual(a,b),cNEqual(a,b)),
				cClause(mList(cProp(0)),cNEqual(a,b))
		),
		new TestPair(
				cClause(mList(cNEqual(a,b)),cNEqual(a,b)),
				cClause(cNEqual(a,b))
		),
		new TestPair(
				cClause(mList(cEqual(a,b)),cNEqual(a,b)),
				TRUE
		),
		
	};

	private IVariableContext variableContext() {
		VariableContext context = new VariableContext();
		context.putInCache(var00);
		return context;
	}
	
	public void testSimplifier() {
		for (TestPair test : tests) {
			LiteralSimplifier rule = new LiteralSimplifier(variableContext());
			
			Clause actual = test.input.simplify(rule);
			assertEquals(test.input.toString(),test.output,actual);
		}
	}
}
