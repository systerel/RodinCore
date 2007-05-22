package org.eventb.pp.core.inferrers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.mList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.terms.Constant;
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
	
	
	private Map<IEquality, Constant> M(IEquality equality, Constant constant) {
		Map<IEquality, Constant> map = new HashMap<IEquality, Constant>();
		map.put(equality, constant);
		return map;
	}
	
	private Map<IEquality, Constant> M(IEquality equality1, Constant constant1,
			IEquality equality2, Constant constant2) {
		Map<IEquality, Constant> map = new HashMap<IEquality, Constant>();
		map.put(equality1, constant1);
		map.put(equality2, constant2);
		return map;
	}
	
	
	public void doTest(IClause original, Map<IEquality, Constant> map, List<IClause> parents, IClause expected) {
		EqualityInstantiationInferrer inferrer = new EqualityInstantiationInferrer(new VariableContext());
		for (Entry<IEquality, Constant> entry : map.entrySet()) {
			inferrer.addEqualityUnequal(entry.getKey(), entry.getValue());
		}
		
		inferrer.addParentClauses(parents);
		original.infer(inferrer);
		IClause actual = inferrer.getResult();
		assertEquals(expected, actual);

		disjointVariables(original, actual);
	}
	
}
