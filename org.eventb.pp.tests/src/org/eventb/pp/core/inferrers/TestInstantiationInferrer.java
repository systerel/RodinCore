/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.core.inferrers;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.eventb.internal.pp.core.elements.terms.Util.d2AA;
import static org.junit.Assert.assertEquals;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.InstantiationInferrer;
import org.junit.Test;

public class TestInstantiationInferrer extends AbstractInferrerTests {

	private static class TestPair {
		Clause input;
		Variable var;
		SimpleTerm term;
		Clause output;
		
		TestPair(Clause input, Variable var, SimpleTerm term, Clause output) {
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
					cClause(cPred(d1A,x)),
					x,
					a,
					cClause(cPred(d1A,a))
			),
//			new TestPair(
//					cClause(cPred(d1A,x)),
//					x,
//					cPlus(a,b,c),
//					cClause(cPred(1,cPlus(a,b,c)))
//			),
//			new TestPair(
//					cClause(cPred(1,x)),
//					x,
//					cPlus(a,y,c),
//					cClause(cPred(1,cPlus(a,y,c)))
//			),
//			new TestPair(
//					cClause(cPred(1,cPlus(x,y))),
//					x,
//					a,
//					cClause(cPred(1,cPlus(a,y)))
//			),
//			new TestPair(
//					cClause(cPred(1,cPlus(x,y))),
//					x,
//					cPlus(a,z),
//					cClause(cPred(1,cPlus(cPlus(a,z),y)))
//			),
			new TestPair(
					cClause(cPred(d1A,x),cPred(d2AA,x,y)),
					x,
					a,
					cClause(cPred(d1A,a),cPred(d2AA,a,y))
			),
			new TestPair(
					cEqClause(cPred(d1A,x),cPred(d2AA,x,y)),
					x,
					a,
					cEqClause(cPred(d1A,a),cPred(d2AA,a,y))
			),
			
	};
	
	
	
    @Test
	public void testInstantiationInferrer() {
		InstantiationInferrer inferrer = new InstantiationInferrer(new VariableContext());
		for (TestPair test : tests) {
//			assertTrue(inferrer.canInfer(test.input));
			inferrer.addInstantiation(test.var, test.term);
			test.input.infer(inferrer);
			assertEquals(test.output, inferrer.getResult());
			
			disjointVariables(test.input, inferrer.getResult());
		}
		
	}
	
}
