package org.eventb.pp.core.inferrers;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.EqualityInstantiationInferrer;

public class TestEqualityInstantiationInferrer extends AbstractInferrerTests {

	
	public void testSimple() {
		doTest(
				cClause(cPred(0, x),cEqual(x, a)), 
				M(xa, b),
				mList(cClause(nab)),
				cClause(cPred(0, b))
		);
		doTest(
				cClause(cPred(0, x, y),cEqual(x, a)), 
				M(xa, b),
				mList(cClause(nab)),
				cClause(cPred(0, b, y))
		);
	}
	
	public void testEquivalenceTransformation() {
		doTest(
				cEqClause(cPred(0, x),cEqual(x, a)), 
				M(xa, b),
				mList(cClause(nab)),
				cClause(cNotPred(0, b))
		);
		doTest(
				cEqClause(cPred(0, x),cNEqual(x, a)), 
				M(nxa, b),
				mList(cClause(nab)),
				cClause(cPred(0, b))
		);
		doTest(
				cEqClause(cPred(0, x),cProp(1),cEqual(x, a)), 
				M(xa, b),
				mList(cClause(nab)),
				cEqClause(cNotPred(0, b),cProp(1))
		);
	}
	
	public void testSeveralEquivalenceTransformation() {
		doTest(
				cEqClause(cPred(0, x, y),cEqual(y, b),cEqual(x, a)), 
				M(	xa, b,
					yb, a),
				mList(cClause(nab)),
				cClause(cPred(0, b, a))
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
