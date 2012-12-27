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
package org.eventb.pp.core.simplifiers;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AA;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.junit.Assert.assertEquals;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.junit.Test;

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
					cClause(cPred(d0A,var0)),
					cClause(cPred(d0A,var0))
			),
			new TestPair(
					cClause(cPred(d0A,evar0)),
					cClause(cPred(d0A,newCons0))
			),
			new TestPair(
					cClause(cPred(d0AA,evar0,var0)),
					cClause(cPred(d0AA,evar0,var0))
			),
			new TestPair(
					cClause(cPred(d0AA,evar0,var0)),
					cClause(cPred(d0AA,evar0,var0))
			),
			new TestPair(
					cClause(cPred(d0AA,evar0,a)),
					cClause(cPred(d0AA,newCons0,a))
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
					cClause(cPred(d0A,evar0),cPred(d1A,evar1)),
					cClause(cPred(d0A,newCons0),cPred(d1A,newCons1))
			),
			
			// FORALL in equivalence clauses
			new TestPair(
					cEqClause(cPred(d0A,evar0),cPred(d1A,evar1)),
					cEqClause(cPred(d0A,evar0),cPred(d1A,evar1))
			),
			//simple
			new TestPair(
					cEqClause(cPred(d0A,fvar0),cPred(d1A,var0)),
					cEqClause(cPred(d0A,fvar0),cPred(d1A,var0))
			),
			//2 foralls
			new TestPair(
					cEqClause(cPred(d0A,fvar0),cPred(d1A,fvar0)),
					cEqClause(cPred(d0A,fvar0),cPred(d1A,fvar0))
			),
			// mixed
			new TestPair(
					cEqClause(cPred(d0A,fvar0),cPred(d1A,evar0)),
					cEqClause(cPred(d0A,fvar0),cPred(d1A,evar0))
			),
			
	};
	
    @Test
	public void testExistential() {
		for (TestPair test : tests) {
			ExistentialSimplifier rule = new ExistentialSimplifier(new MyVariableContext());
			Clause actual = test.input.simplify(rule);
			assertEquals(test.output,actual);
		}
	}
	
	static class MyVariableContext extends VariableContext {
		int i=0;
		
		@Override
		public Constant getNextFreshConstant(Sort sort) {
			if (i==0) {i++; return newCons0;}
			if (i==1) {i++; return newCons1;}
			return null;
		}

		@Override
		public LocalVariable getNextLocalVariable(boolean isForall, Sort sort) {
			return null;
		}

		@Override
		public Variable getNextVariable(Sort sort) {
			return null;
		}
	}
	
	
//	public void testExistentialBlocking() {
//		Clause clause = cClause(cPred(d0AA, var0, evar0));
//	}
}
