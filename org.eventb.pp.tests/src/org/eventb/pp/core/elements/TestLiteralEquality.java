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
package org.eventb.pp.core.elements;

import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AA;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AAA;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.junit.Test;

public class TestLiteralEquality extends AbstractPPTest {

    @Test
	public void testProposition() {
		doEqualTests(cProp(0), cProp(0));
		doEqualTests(cNotProp(0), cNotProp(0));
		doUnequalTests(cProp(0), cProp(1), true);
		doUnequalTests(cNotProp(0), cNotProp(1), true);
		doUnequalTests(cNotProp(0), cProp(0), true);
		doUnequalTests(cProp(0), cPred(d0A,a), true);
		doUnequalTests(cProp(0), cNotPred(d0A,a), true);
//		doUnequalTests(cProp(0), cEqual(a, b), true);
//		doUnequalTests(cProp(0), cNEqual(a, b), true);
		doUnequalTests(cNotProp(0), cPred(d0A,a), true);
		doUnequalTests(cNotProp(0), cNotPred(d0A,a), true);
//		doUnequalTests(cNotProp(0), cEqual(a, b), true);
//		doUnequalTests(cNotProp(0), cNEqual(a, b), true);
	}
	
    @Test
	public void testPredicate() {
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(d0A, a), cPred(d0A, a));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(d0A, a), cNotPred(d0A, a));
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0A, a), cPred(d0A, b), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0A, a), cNotPred(d0A, b), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0A, a), cNotPred(d0A, a), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0A, a), cPred(d1A, a), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0A, a), cNotPred(d1A, a), true);
		
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(d0A, x), cPred(d0A, x));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(d0A, x), cNotPred(d0A, x));
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0A, x), cPred(d0A, y), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0A, x), cNotPred(d0A, y), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0A, x), cNotPred(d0A, x), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0A, x), cPred(d1A, x), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0A, x), cNotPred(d1A, x), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0A,x), cPred(d0A,a), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0A,x), cNotPred(d0A,a), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA, x, y), cPred(d0AA, y, x), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA, x, x), cPred(d0AA, y, x), true);
		
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(d0AA, x, evar0), cPred(d0AA, x, evar0));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(d0AA, x, evar0), cNotPred(d0AA, x, evar0));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(d0AAA, x, evar0, evar0), cPred(d0AAA, x, evar0, evar0));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(d0AAA, x, evar0, evar0), cNotPred(d0AAA, x, evar0, evar0));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(d0AA, x, fvar0), cPred(d0AA, x, fvar0));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(d0AA, x, fvar0), cNotPred(d0AA, x, fvar0));
		
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA, x, evar0), cPred(d0AA, x, evar1), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0AA, x, evar0), cNotPred(d0AA, x, evar1), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA, x, evar0), cPred(d0AA, x, fvar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0AA, x, evar0), cNotPred(d0AA, x, fvar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AAA, x, evar0, evar1), cPred(d0AAA, x, evar0, evar0), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0AAA, x, evar0, evar1), cNotPred(d0AAA, x, evar0, evar0), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AAA, x, evar0, evar0), cPred(d0AAA, x, evar0, fvar0), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0AAA, x, evar0, evar0), cNotPred(d0AAA, x, evar0, fvar0), false);
		
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA, x, x), cPred(d0AA, x, evar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0AA, x, x), cNotPred(d0AA, x, evar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA, x, x), cPred(d0AA, x, fvar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0AA, x, x), cNotPred(d0AA, x, fvar0), true);
		
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA, x, a), cPred(d0AA, x, evar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0AA, x, a), cNotPred(d0AA, x, evar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA, x, a), cPred(d0AA, x, fvar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(d0AA, x, a), cNotPred(d0AA, x, fvar0), true);
		
//		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(d0AA,a,b), cEqual(a, b), true);
	}
	
    @Test
	public void testEquality() {
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, a),cEqual(a, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, a),cNEqual(a, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, b),cEqual(a, b));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, b),cNEqual(a, b));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, b),cEqual(b, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, b),cNEqual(b, a));
		
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, x),cEqual(x, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, x),cNEqual(x, a));
		
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, evar0),cEqual(evar0, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, evar0),cNEqual(evar0, a));
		
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(x, y),cEqual(y, x));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(x, y),cNEqual(y, x));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(x, y),cEqual(x, y));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(x, y),cNEqual(x, y));
		
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(x, evar0),cEqual(evar0, x));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(x, evar0),cNEqual(evar0, x));
		
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cEqual(a, a),cEqual(b, b), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cEqual(a, b),cEqual(a, c), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cNEqual(a, a),cNEqual(b, b), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cNEqual(a, b),cNEqual(a, c), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cEqual(x, y), cEqual(x, x), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cEqual(x, evar0), cEqual(x, x), true);
	}
	
	public static <T extends Literal<T,?>>  void doEqualTests(T... literals) {
		Literal<T,?> base = literals[0];
		for (int i = 1; i < literals.length; i++) {
			assertEquals(""+base, base, literals[i]);
			// if equal, then it is also equal with different variables
			assertTrue(base.equalsWithDifferentVariables(literals[i], new HashMap<SimpleTerm,SimpleTerm>()));
			assertEquals(base.hashCode(), literals[i].hashCode());
			assertEquals(base.hashCodeWithDifferentVariables(),literals[i].hashCodeWithDifferentVariables());
		}
		
	}
	
	public static <T extends Literal<T,?>> void doUnequalTests(T literal1, T literal2, boolean diffVars) {
		assertFalse(literal1.equals(literal2));
		if (diffVars) assertFalse(literal1.equalsWithDifferentVariables(literal2, new HashMap<SimpleTerm, SimpleTerm>()));
	}
	
}
