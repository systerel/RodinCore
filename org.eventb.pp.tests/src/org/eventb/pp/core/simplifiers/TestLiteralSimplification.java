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

import static org.eventb.internal.pp.core.elements.terms.Util.cAEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cANEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AA;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.simplifiers.LiteralSimplifier;
import org.junit.Test;

public class TestLiteralSimplification extends AbstractPPTest {
	private class TestPair {
		Clause input, output;
		
		TestPair(Clause input, Clause output) {
			this.input = input;
			this.output = output;
		}
	}
	
	
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
				cClause(cProp(0),cPred(d1A,a)),
				cClause(cProp(0),cPred(d1A,a))
		),
		new TestPair(
				cClause(cProp(0),cProp(0),cPred(d1A,a)),
				cClause(cProp(0),cPred(d1A,a))
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
				cClause(cNotProp(0),cPred(d1A,a)),
				cClause(cNotProp(0),cPred(d1A,a))
		),
		new TestPair(
				cClause(cNotProp(0),cNotProp(0),cPred(d1A,a)),
				cClause(cNotProp(0),cPred(d1A,a))
		),
		
		
		new TestPair(
				cClause(cPred(d0A,a),cPred(d0A,a)),
				cClause(cPred(d0A,a))
		),
		new TestPair(
				cClause(cPred(d0A,var0),cPred(d0A,var0)),
				cClause(cPred(d0A,var0))
		),
		new TestPair(
				cClause(cPred(d0A,var0),cPred(d0A,var1)),
				cClause(cPred(d0A,var0),cPred(d0A,var1))
		),
		new TestPair(
				cClause(cPred(d0AA,var0,a),cPred(d0AA,var0,a)),
				cClause(cPred(d0AA,var0,a))
		),
		new TestPair(
				cClause(cPred(d0A,evar0),cPred(d0A,fvar0)),
				cClause(cPred(d0A,evar0),cPred(d0A,fvar0))
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
				cClause(cNEqual(a,b),cNEqual(b,a)),
				cClause(cNEqual(a,b))
		),
		
		
		// discard
		new TestPair(
				cClause(cProp(0),cNotProp(0)),
				TRUE
		),
		new TestPair(
				cClause(cPred(d0A,a),cNotPred(d0A,a)),
				TRUE
		),
		new TestPair(
				cClause(cPred(d0A,var0),cNotPred(d0A,var0)),
				TRUE
		),
		new TestPair(
				cClause(cPred(d0A,evar0),cNotPred(d0A,evar0)),
				cClause(cPred(d0A,evar0),cNotPred(d0A,evar0))
		),
		new TestPair(
				cClause(cPred(d0A,evar0),cNotPred(d0A,var0)),
				cClause(cPred(d0A,evar0),cNotPred(d0A,var0))
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
				cEqClause(cPred(d0A,evar0),cPred(d0A,evar1)),
				cEqClause(cPred(d0A,evar0),cPred(d0A,evar1))
		),
		new TestPair(
				cEqClause(cPred(d0A,fvar0),cPred(d0A,fvar1)),
				cEqClause(cPred(d0A,fvar0),cPred(d0A,fvar1))
		),
//		new TestPair(
//				cEqClause(cPred(d0A,evar0),cNotPred(d0A,fvar0)),
//				FALSE
//		),
		new TestPair(
				cEqClause(cPred(d0A,evar0),cPred(d0A,var0)),
				cEqClause(cPred(d0A,evar0),cPred(d0A,var0))
		),
		new TestPair(
				cEqClause(cPred(d0A,fvar0),cPred(d0A,var0)),
				cEqClause(cPred(d0A,fvar0),cPred(d0A,var0))
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
				cClause(new ArrayList<Literal<?, ?>>(),cNEqual(a, b))
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
		
		// other
		new TestPair(
				cEqClause(cProp(0),cProp(0),cProp(0)),
				cClause(cProp(0))
		),
		new TestPair(
				cEqClause(cProp(0),cProp(0),cNotProp(0)),
				cClause(cNotProp(0))
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cNotProp(0)),
				cClause(cProp(0))
		),
		new TestPair(
				cEqClause(cProp(0),cProp(0),cProp(0),cProp(0)),
				TRUE
		),
		new TestPair(
				cEqClause(cProp(0),cProp(0),cNotProp(0),cProp(0)),
				FALSE
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cNotProp(0),cProp(0)),
				TRUE
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cEqual(a,b),cNEqual(a,b)),
				TRUE
		),
		new TestPair(
				cEqClause(cProp(0),cProp(0),cNotProp(0),cEqual(a,b),cNEqual(a,b)),
				cClause(cProp(0))
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cEqual(a,b),cNEqual(a,b),cAEqual(zero, one),cANEqual(zero, one)),
				FALSE
		),
		new TestPair(
				cEqClause(cProp(0),cNotProp(0),cProp(0),cEqual(a,b),cNEqual(a,b),cAEqual(zero, one),cANEqual(zero, one)),
				cClause(cNotProp(0))
		),
	};

	private VariableContext variableContext() {
		return new VariableContext();
	}
	
    @Test
	public void testSimplifier() {
		for (TestPair test : tests) {
			LiteralSimplifier rule = new LiteralSimplifier(variableContext());
			
			Clause actual = test.input.simplify(rule);
			if (actual.isTrue()) assertTrue(test.output.isTrue());
			else if (actual.isFalse()) assertTrue(test.output.isFalse());
			else assertEquals(test.input.toString(),test.output,actual);
		}
	}
}
