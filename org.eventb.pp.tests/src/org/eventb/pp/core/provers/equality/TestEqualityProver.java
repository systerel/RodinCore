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
package org.eventb.pp.core.provers.equality;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.mSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.equality.EqualityProver;
import org.junit.Before;
import org.junit.Test;

public class TestEqualityProver extends AbstractPPTest {

	private EqualityProver prover;
	
    @Before
	public void setUp() {
		prover = new EqualityProver(new VariableContext());
	}
	
    @Test
	public void testProverInstantiation() {
		prover.addClauseAndDetectContradiction(cClause(cEqual(a, x), cProp(0)));
		prover.addClauseAndDetectContradiction(cClause(cNEqual(a, b)));
		
		ProverResult result = prover.next(true);
		assertEquals(result.getGeneratedClauses(), mSet(cClause(cProp(0))));
	}
	
    @Test
	public void testProverInstantiation2() {
		Clause c1 = cClause(cEqual(a, x), cProp(0));
		Clause u1 = cClause(cEqual(a, b));
		prover.addClauseAndDetectContradiction(c1);
		prover.addClauseAndDetectContradiction(u1);
		
		ProverResult result = prover.next(false);
		assertEquals(result, ProverResult.EMPTY_RESULT);
	}
	
    @Test
	public void testEmptyResult() {
		ProverResult result = prover.addClauseAndDetectContradiction(cClause(cProp(0)));
		assertTrue(result.isEmpty());
	}
	
    @Test
	public void testContradiction() {
		prover.addClauseAndDetectContradiction(cClause(cEqual(a, b)));
		ProverResult result = prover.addClauseAndDetectContradiction(cClause(cNEqual(a, b)));
		
		assertFalseClause(result);
	}
	
    @Test
	public void testSubsumption() {
		Clause c1 = cClause(cEqual(a,b),cProp(0));
		prover.addClauseAndDetectContradiction(c1);
		ProverResult result = prover.addClauseAndDetectContradiction(cClause(cEqual(a,b)));
		
		assertTrueClause(result);
		assertEquals(result.getSubsumedClauses(),mSet(c1));
	}
	
    @Test
	public void testSubsumptionWithDifferentLevels() {
		Clause c1 = cClause(ONE,cEqual(a,b),cProp(0));
		prover.addClauseAndDetectContradiction(c1);
		ProverResult result = prover.addClauseAndDetectContradiction(cClause(cEqual(a,b)));
		
		assertTrueClause(result);
		assertEquals(result.getSubsumedClauses(),mSet(c1));
	}
	
    @Test
	public void testNoSubsumption() {
		Clause c1 = cClause(cEqual(a,b),cProp(0));
		prover.addClauseAndDetectContradiction(c1);
		ProverResult result = prover.addClauseAndDetectContradiction(cClause(ONE,cEqual(a,b)));
		
		assertTrueClause(result);
		assertTrue(result.getSubsumedClauses().isEmpty());
	}
}
