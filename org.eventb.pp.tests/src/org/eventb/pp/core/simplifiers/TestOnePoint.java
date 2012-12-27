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
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AA;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;
import org.junit.Test;

/**
 * This class tests the one point rule. There are several tests :
 * <ul>
 * 	<li>one-point rule on non-arithmetic</li>
 * 	<li>one-point rule on arithmetic with single expressions</li>
 * 	<li>one-point rule on arithmetic with complex expressions</li>
 * </ul>
 *
 * @author François Terrier
 *
 */
public class TestOnePoint extends AbstractPPTest {

	
    @Test
	public void testOnePoint() {
		// standard test
			testOnePoint(
					cClause(cNEqual(var0, a), cPred(d0A, var0)),
					cClause(cPred(d0A, a))
			);
			// no rule applies
			testOnePoint(
					cClause(cNEqual(a, b), cPred(d0A, a)),
					cClause(cNEqual(a, b), cPred(d0A, a))
			);
			// no rule applies either, variable is in expression
			// border case, see what to do with it
			// TODO this test will change with arithmetic
//			testOnePoint(
//					cClause(cNEqual(var0, cPlus(var1,var0)), cPred(d0A, var0)),
//					cClause(cNEqual(var0, cPlus(var1,var0)), cPred(d0A, var0))
//			);
			testOnePoint(
					cClause(cNEqual(var0, var00), cPred(d0A, var0)),
					cClause(cPred(d0A, var00))
			);
			// more variables
			testOnePoint(
					cClause(cNEqual(var0, a), cNEqual(var1, b), cPred(d0AA, var0, var1)),
					cClause(cPred(d0AA, a, b))
			);
			// more literals
			testOnePoint(
					cClause(cNEqual(var0, a), cPred(d0A, var0), cPred(d1A, var0)),
					cClause(cPred(d0A, a), cPred(d1A, a))
			);
			// different literals
			testOnePoint(
					cClause(cNEqual(var0, a), cEqual(var0, b)),
					cClause(cEqual(a, b))
			);
			// 2 inequalities
			testOnePoint(
					cClause(cNEqual(var0, a), cNEqual(var0, b)),
					cClause(cNEqual(a, b))
			);
			
			// EQUIVALENCE
			// standard test
			testOnePoint(
					cEqClause(cNEqual(var0, a), cPred(d0A, var0)),
					cEqClause(cNEqual(var0, a), cPred(d0A, var0))
			);
			testOnePoint(
					cEqClause(mList(cPred(d0A, var0),cPred(d1A,var0)), cNEqual(var0, a)),
					cEqClause(cPred(d0A, a),cPred(d1A, a))
			);
			testOnePoint(
					cEqClause(mList(cNEqual(var0,a),cNEqual(a,var0)),cNEqual(var0,a)),
					cEqClause(cNEqual(a,a),cNEqual(a,a))
			);
			
			//
			testOnePoint(
					cEqClause(mList(cPred(d0A,var0),cPred(d1A,var0)),cNEqual(var0,a),cNEqual(b,b)),
					cEqClause(mList(cPred(d0A,a),cPred(d1A,a)),cNEqual(b,b))
			);
			
			// Disjunctive with conditions
			testOnePoint(
					cClause(mList(cPred(d0A,var0)),cNEqual(var0,a)),
					cClause(cPred(d0A,a))
			);
			
			// EQUALITY alone
			testOnePoint(
					cClause(cNEqual(var0, var1)),
					FALSE
			);
			testOnePoint(
					cClause(cNEqual(var0, var1),cNEqual(var2, var3)),
					FALSE
			);
			testOnePoint(
					cClause(cNEqual(var0, a)),
					FALSE
			);
			
			
			// simple equality with a variable and a local variable
			testOnePoint(
					cClause(cEqual(evar0, var0)),
					TRUE
			);		
			testOnePoint(
					cClause(cEqual(evar0, evar1)),
					TRUE
			);		
	}
	
    @Test
	public void testNotOnePoint () {
			// standard test
			testOnePoint(
					cClause(cEqual(var0, a), cPred(d0A, var0)),
					cClause(cEqual(var0, a), cPred(d0A, var0))
			);
			
			
			testOnePoint(
					cClause(cNEqual(evar0, var1)),
					cClause(cNEqual(evar0, var1))
			);
			
			// EXISTENTIAL
			testOnePoint(
					cClause(cPred(d0A,var0),cNEqual(evar0,var0)),
					cClause(cPred(d0A,var0),cNEqual(evar0,var0))
			);
			testOnePoint(
					cClause(cPred(d0A,var0),cNEqual(var0,evar0)),
					cClause(cPred(d0A,var0),cNEqual(var0,evar0))
			);
			testOnePoint(
					cClause(cPred(d0A,var0),cPred(d1A,var0),cNEqual(evar0,var0)),
					cClause(cPred(d0A,var0),cPred(d1A,var0),cNEqual(evar0,var0))
			);
			
			testOnePoint(
					cClause(cPred(d0A, var0),cEqual(evar0, evar0)),
					cClause(cPred(d0A, var0),cEqual(evar0, evar0))
			);
	}
	
	private OnePointRule rule = new OnePointRule();
	
	public void testOnePoint(Clause input, Clause output) {
		Clause actual = input.simplify(rule);
		assertTrue(rule.canSimplify(input));
		
		if (actual.isFalse()) assertTrue(output.isFalse());
		else if (actual.isTrue()) assertTrue(output.isTrue());
		else assertEquals(output,actual);
	}
	
	public void testNotOnePoint(Clause input, Clause output) {
		Clause actual = input.simplify(rule);
		assertTrue(rule.canSimplify(input));
		assertEquals(output, actual);
	}
	
}
