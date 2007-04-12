package org.eventb.pp.core.inferrers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cPlus;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;

import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.InstantiationInferrer;
import org.eventb.pp.AbstractPPTest;

public class TestInstantiationInferrer extends AbstractPPTest {

	private static class TestPair {
		IClause input;
		Variable var;
		Term term;
		IClause output;
		
		TestPair(IClause input, Variable var, Term term, IClause output) {
			this.input = input;
			this.var = var;
			this.term = term;
			this.output = output;
		}
	}
	
	TestPair[] tests = new TestPair[]{
			new TestPair(
					cClause(cProp(0)),
					x,
					a,
					cClause(cProp(0))
			),
			new TestPair(
					cClause(cPred(1,x)),
					x,
					a,
					cClause(cPred(1,a))
			),
			new TestPair(
					cClause(cPred(1,x)),
					x,
					cPlus(a,b,c),
					cClause(cPred(1,cPlus(a,b,c)))
			),
			new TestPair(
					cClause(cPred(1,x)),
					x,
					cPlus(a,y,c),
					cClause(cPred(1,cPlus(a,y,c)))
			),
			new TestPair(
					cClause(cPred(1,cPlus(x,y))),
					x,
					a,
					cClause(cPred(1,cPlus(a,y)))
			),
			new TestPair(
					cClause(cPred(1,cPlus(x,y))),
					x,
					cPlus(a,z),
					cClause(cPred(1,cPlus(cPlus(a,z),y)))
			),
			new TestPair(
					cClause(cPred(1,x),cPred(2,x,y)),
					x,
					a,
					cClause(cPred(1,a),cPred(2,a,y))
			),
			new TestPair(
					cEqClause(cPred(1,x),cPred(2,x,y)),
					x,
					a,
					cEqClause(cPred(1,a),cPred(2,a,y))
			),
			
	};
	
	public void testInstantiationInferrer() {
		InstantiationInferrer inferrer = new InstantiationInferrer(new VariableContext());
		for (TestPair test : tests) {
			assertTrue(inferrer.canInfer(test.input));
			inferrer.setTerm(test.term);
			inferrer.setVariable(test.var);
			test.input.infer(inferrer);
			assertEquals(test.output, inferrer.getResult());
		}
	}
	
}
