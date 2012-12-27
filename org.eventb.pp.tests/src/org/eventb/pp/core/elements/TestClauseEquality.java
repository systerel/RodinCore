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
import static org.eventb.internal.pp.core.elements.terms.Util.d0AAA;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.eventb.internal.pp.core.elements.terms.Util.d1AA;
import static org.eventb.internal.pp.core.elements.terms.Util.d2A;
import static org.eventb.internal.pp.core.elements.terms.Util.d3A;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.junit.Test;

public class TestClauseEquality extends AbstractPPTest {
	
	
    @Test
	public void testPropositionalEqual() {
		testEqual(
				cClause(cProp(0)),cClause(cProp(0))	
		);
	}

    @Test
	public void testPredicateEqual() {
		testEqual(
				cClause(cPred(d0A,x)),cClause(cPred(d0A,y))	
		);
		testEqual(
				cClause(cPred(d0AA,x,y)),cClause(cPred(d0AA,y,x))	
		);
		testEqual(
				cClause(cPred(d0AAA,x,y,z)),cClause(cPred(d0AAA,y,x,z))	
		);
		testEqual(
				cClause(cPred(d0AAA,x,y,z)),cClause(cPred(d0AAA,z,y,x))	
		);
		testEqual(
				cClause(cPred(d0A,x),cPred(d1A,y)),cClause(cPred(d0A,y),cPred(d1A,x))
		);
		testEqual(
				cClause(cPred(d0A,x),cPred(d1A,y),cPred(d2A,z)),cClause(cPred(d0A,y),cPred(d1A,x),cPred(d2A,z))
		);
		testEqual(
				cClause(cPred(d0A,x),cPred(d1A,x),cPred(d2A,x)),cClause(cPred(d0A,y),cPred(d1A,y),cPred(d2A,y))
		);
		testEqual(
				cClause(cPred(d0A,x),cPred(d1A,y),cPred(d2A,x)),cClause(cPred(d0A,z),cPred(d1A,x),cPred(d2A,z))
		);

		testEqual(
				cClause(cPred(d0A,x),cEqual(x,y)),cClause(cPred(d0A,x),cEqual(y,x))
		);
		testEqual(
				cClause(cPred(d0A,y),cEqual(a,y)),cClause(cPred(d0A,x),cEqual(a,x))
		);
		testEqual(
				cClause(cPred(d0A,y),cEqual(a,y)),cClause(cPred(d0A,x),cEqual(x,a))
		);

		testEqual(
				cClause(cPred(d0A,a)),cClause(cPred(d0A,a))
		);
		testEqual(
				cClause(cPred(d0A,evar0)),cClause(cPred(d0A,evar1))
		);
		testEqual(
				cClause(cPred(d0AA,x,evar0)),cClause(cPred(d0AA,y,evar1))
		);
		testEqual(
				cEqClause(cProp(0),cPred(d0A,fvar0)),cEqClause(cProp(0),cPred(d0A,fvar1))
		);

//		testEqual(
//				cClause(cPred(d1A,cPlus(cPlus(a,y),y))),cClause(cPred(d1A,cPlus(cPlus(a,x),x)))
//		);

		testEqual(
				cEqClause(cPred(d3A,x),cPred(d0A,x),cNotPred(d1AA,x,evar0)),cEqClause(cPred(d3A,x),cPred(d0A,x),cNotPred(d1AA,x,evar1))
		);
	}


    @Test
	public void testUnequalClauses() {
		testUnequal(
				cClause(cProp(0)),cClause(cNotProp(0))
		);
		testUnequal(
				cClause(cPred(d1A,a)),cClause(cNotPred(d1A,a))
		);
		testUnequal(
				cClause(cEqual(a,a)),cClause(cNEqual(a,a))
		);

		testUnequal(
				cClause(cProp(0)),cClause(cProp(1))	
		);
		testUnequal(
				cClause(cPred(d0A,x)),cClause(cPred(d0AA,x,x))	
		);
		testUnequal(
				cClause(cPred(d0A,x)),cClause(cPred(d1A,x))	
		);

		testUnequal(
				cClause(cPred(d0AA,x,y)),cClause(cPred(d0AA,y,y))	
		);

		testUnequal(
				cClause(cPred(d0AAA,x,y,z)),cClause(cPred(d0AAA,y,x,x))	
		);
		testUnequal(
				cClause(cPred(d0AAA,x,y,z)),cClause(cPred(d0AAA,x,x,x))	
		);
		testUnequal(
				cClause(cPred(d0AAA,x,y,z)),cClause(cPred(d0AAA,x,x,y))	
		);

		testUnequal(
				cClause(cPred(d0A,x),cPred(d1A,y)),cClause(cPred(d0A,x),cPred(d1A,x))
		);
		testUnequal(
				cClause(cPred(d0A,x),cPred(d1A,y),cPred(d2A,z)),cClause(cPred(d0A,x),cPred(d1A,x),cPred(d2A,z))
		);
		testUnequal(
				cClause(cPred(d0A,x),cPred(d1A,x),cPred(d2A,x)),cClause(cPred(d0A,x),cPred(d1A,y),cPred(d2A,y))
		);
		testUnequal(
				cClause(cPred(d0A,x),cPred(d1A,y),cPred(d2A,x)),cClause(cPred(d0A,z),cPred(d1A,x),cPred(d2A,x))
		);

		testUnequal(
				cClause(cPred(d0A,a)),cClause(cPred(d0A,b))
		);


		testUnequal(
				cEqClause(cProp(1),cPred(d0A,evar0)),cEqClause(cProp(1),cPred(d0A,fvar1))
		);
		testUnequal(
				cEqClause(cProp(1),cPred(d0A,fvar0)),cEqClause(cProp(1),cPred(d0A,evar1))
		);

		testUnequal(
				cEqClause(cProp(0),cProp(0)),cClause(cProp(0),cProp(0))	
		);

		testUnequal(
				cEqClause(cProp(0),cPred(d1A,a)),cClause(cProp(0),cPred(d1A,b))	
		);

		testUnequal(
				cClause(cPred(d0A,x),cEqual(a,y)),cClause(cPred(d0A,x),cEqual(a,x))
		);			
		testUnequal(
				cClause(cPred(d0A,x),cEqual(a,y)),cClause(cPred(d0A,x),cEqual(a,a))
		);
	}
	
    @Test
	public void testUnequalDisjAndEqClauses() {
		testUnequal(cClause(cProp(0),cProp(0)),cEqClause(cProp(0),cProp(0)));
	}
	
    @Test
	public void testEqualClauseWithEquality() {
		// this is unequal
		testUnequal(cClause(cPred(d0A,x),cEqual(x,y)),cClause(cPred(d0A,y),cEqual(x,y)));
	}

	public void testEqual(Clause... clauses) {
		for (int i = 0; i < clauses.length-1; i++) {
			Clause clause1 = clauses[i];
			Clause clause2 = clauses[i+1];
			assertTrue(clause1.equals(clause2));
			assertTrue(clause2.equals(clause1));

			assertEquals(""+clause1+","+clause2,clause1.hashCode(),clause2.hashCode());
		}
	}
	
	public void testUnequal(Clause... clauses) {
		for (int i = 0; i < clauses.length-1; i++) {
			Clause clause1 = clauses[i];
			Clause clause2 = clauses[i+1];
			assertFalse(clause1.equals(clause2));
			assertFalse(clause2.equals(clause1));
		}
	}
}
