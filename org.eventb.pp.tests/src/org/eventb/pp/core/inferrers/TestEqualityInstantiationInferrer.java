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
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AA;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.EqualityInstantiationInferrer;
import org.junit.Test;

public class TestEqualityInstantiationInferrer extends AbstractInferrerTests {

	
    @Test
	public void testSimple() {
		doTest(
				cClause(cPred(d0A, x),cEqual(x, a)), 
				M(xa, b),
				mList(cClause(nab)),
				cClause(cPred(d0A, b))
		);
		doTest(
				cClause(cPred(d0AA, x, y),cEqual(x, a)), 
				M(xa, b),
				mList(cClause(nab)),
				cClause(cPred(d0AA, b, y))
		);
	}
	
    @Test
	public void testEquivalenceTransformation() {
		doTest(
				cEqClause(cPred(d0A, x),cEqual(x, a)), 
				M(xa, b),
				mList(cClause(nab)),
				cClause(cNotPred(d0A, b))
		);
		doTest(
				cEqClause(cPred(d0A, x),cNEqual(x, a)), 
				M(nxa, b),
				mList(cClause(nab)),
				cClause(cPred(d0A, b))
		);
		doTest(
				cEqClause(cPred(d0A, x),cProp(1),cEqual(x, a)), 
				M(xa, b),
				mList(cClause(nab)),
				cEqClause(cNotPred(d0A, b),cProp(1))
		);
	}
	
    @Test
	public void testSeveralEquivalenceTransformation() {
		doTest(
				cEqClause(cPred(d0AA, x, y),cEqual(y, b),cEqual(x, a)), 
				M(	xa, b,
					yb, a),
				mList(cClause(nab)),
				cClause(cPred(d0AA, b, a))
		);
		
	}
	
	
	private Map<EqualityLiteral, Constant> M(EqualityLiteral equality, Constant constant) {
		Map<EqualityLiteral, Constant> map = new HashMap<EqualityLiteral, Constant>();
		map.put(equality, constant);
		return map;
	}
	
	private Map<EqualityLiteral, Constant> M(EqualityLiteral equality1, Constant constant1,
			EqualityLiteral equality2, Constant constant2) {
		Map<EqualityLiteral, Constant> map = new HashMap<EqualityLiteral, Constant>();
		map.put(equality1, constant1);
		map.put(equality2, constant2);
		return map;
	}
	
	
	public void doTest(Clause original, Map<EqualityLiteral, Constant> map, List<Clause> parents, Clause expected) {
		EqualityInstantiationInferrer inferrer = new EqualityInstantiationInferrer(new VariableContext());
		for (Entry<EqualityLiteral, Constant> entry : map.entrySet()) {
			inferrer.addEqualityUnequal(entry.getKey(), entry.getValue());
		}
		
		inferrer.addParentClauses(parents);
		original.infer(inferrer);
		Clause actual = inferrer.getResult();
		assertEquals(expected, actual);

		disjointVariables(original, actual);
	}
	
}
