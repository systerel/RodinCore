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
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.eventb.internal.pp.core.elements.terms.Util.d1AA;
import static org.eventb.internal.pp.core.elements.terms.Util.mSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.CaseSplitInferrer;
import org.junit.Test;

/**
 * This class tests the case split inferrer. Case split inferrer behaves
 * correctly if a clause is split in two clauses that contain two disjoint
 * sets of variables. 
 * <p>
 * For now, the casesplit inferrer only splits on constant only clauses.
 *
 * @author François Terrier
 *
 */
public class TestCaseSplitInferrer extends AbstractPPTest {

    @Test
	public void testCaseSplitInferrer () {
			doTest(
					cClause(BASE, cProp(0),cProp(1)),
					mSet(cClause(ONE, cProp(0))),
					mSet(cClause(TWO,cProp(1)))
			);
			doTest(
					cEqClause(BASE, cProp(0),cProp(1)),
					mSet(cClause(ONE, cProp(0)),cClause(ONE,cProp(1))),
					mSet(cClause(TWO, cNotProp(0)),cClause(TWO, cNotProp(1)))
			);
			doTest(
					cClause(BASE, cEqual(a,b),cPred(d0A,a)),
					mSet(cClause(ONE, cPred(d0A,a))),
					mSet(cClause(TWO, cEqual(a,b)))
			);
			doTest(
					cClause(BASE, cEqual(a,b),cEqual(b,c)),
					mSet(cClause(ONE, cEqual(a,b))),
					mSet(cClause(TWO,cEqual(b,c)))
			);
			doTest(
					cEqClause(BASE, cPred(d0A,fvar0), cPred(d0A,a)),
					mSet(cClause(ONE, cPred(d0A,x)), cClause(ONE, cPred(d0A,a))),
					mSet(cClause(TWO, cNotPred(d0A,evar0)), cClause(cNotPred(d0A,a)))
			);
			doTest(
					cClause(BASE, cPred(d0A,evar0), cPred(d0A,a)),
					mSet(cClause(ONE, cPred(d0A,evar0))),
					mSet(cClause(TWO, cPred(d0A,a)))
			);
			doTest(
					cEqClause(BASE, cPred(d0A,evar0), cPred(d0A,a)),
					mSet(cClause(ONE, cPred(d0A,evar0)),cClause(ONE, cPred(d0A,a))),
					mSet(cClause(TWO, cNotPred(d0A,x)),cClause(TWO, cNotPred(d0A,a)))
			);
			doTest(
					cEqClause(BASE, cProp(0), cProp(1), cProp(2)),
					mSet(cClause(ONE, cProp(0)), cEqClause(cProp(1),cProp(2))),
					mSet(cClause(TWO, cNotProp(0)),cEqClause(cNotProp(1),cProp(2)))
			);
			
	}

	public void doTest(Clause original, Set<Clause> left, Set<Clause> right) {
		CaseSplitInferrer inferrer = new CaseSplitInferrer(new VariableContext());

		inferrer.setLevel(original.getLevel());
		original.infer(inferrer);
		assertTrue("Expected: "+left+", was: "+inferrer.getLeftCase()+", from :"+original,inferrer.getLeftCase().equals(left));
		assertTrue("Expected: "+right+", was: "+inferrer.getRightCase()+", from :"+original,inferrer.getRightCase().equals(right));
	}
	
    @Test
	public void testIllegal() {
		CaseSplitInferrer inferrer = new CaseSplitInferrer(new VariableContext());

		Clause clause = cClause(cProp(0),cProp(1));
		try {
			clause.infer(inferrer);
			fail();
		}
		catch (IllegalStateException e) {
			// normal case
		}
	}	
	
    @Test
	public void testCanInfer() {
		CaseSplitInferrer inferrer = new CaseSplitInferrer(new VariableContext());
		
		Clause[] canInfer = new Clause[]{
				cClause(cProp(0),cProp(1)),
				cClause(cPred(d0A,a),cPred(d1A,b)),
				cClause(cPred(d0A,evar0),cPred(d1A,evar1)),
				cClause(cEqual(a,b),cPred(d0A,a)),
				cClause(cPred(d0A,evar0),cPred(d1A,var1)),
				cEqClause(cProp(0),cProp(1)),
				cEqClause(cPred(d0A,a),cPred(d1A,b)),
				cEqClause(cPred(d0A,evar0),cPred(d1A,evar1)),
				cEqClause(cEqual(a,b),cPred(d0A,a)),
				cEqClause(cPred(d0A,evar0),cPred(d1A,var1)),
		};

		for (Clause clause : canInfer) {
			assertTrue(inferrer.canInfer(clause));
		}
	}
	
    @Test
	public void testCannotInfer() {
		CaseSplitInferrer inferrer = new CaseSplitInferrer(new VariableContext());
		
		Clause[] cannotInfer = new Clause[]{
				cClause(cProp(0)),
				cClause(cPred(d0A,x),cPred(d0A,x)),
				cClause(cPred(d0A,x),cPred(d1AA,x,y)),
				cClause(cEqual(x,b),cPred(d0A,x)),
				cEqClause(cPred(d0A,x),cPred(d0A,x)),
				cEqClause(cPred(d0A,x),cPred(d1AA,x,y)),
		};

		for (Clause clause : cannotInfer) {
			assertFalse(inferrer.canInfer(clause));
		}
	}
	
}
