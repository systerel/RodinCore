package org.eventb.pp.core.inferrers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.mList;

import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.inferrers.EqualityInferrer;
import org.eventb.internal.pp.loader.clause.VariableContext;

public class TestEqualityInferrer extends AbstractInferrerTests {

	public void testSimpleDisjunctiveClauses() {
		doTest(
				cClause(cProp(0),ab), mList(ab), EMPTY, mList(cClause(ab)), 
				TRUE
		);
		doTest(
				cClause(cProp(0),ab), EMPTY, mList(ab), mList(cClause(ab)), 
				cClause(cProp(0))
		);
		doTest(
				cClause(cProp(0),nab), mList(nab), EMPTY, mList(cClause(nab)),
				TRUE
		);
		doTest(
				cClause(cProp(0),nab), EMPTY, mList(nab), mList(cClause(nab)), 
				cClause(cProp(0))
		);		
		doTest(
				cClause(ab,bc), EMPTY, mList(ab, bc), mList(cClause(ab)),
				FALSE
		);
	}
	
	public void testSimpleDisjunctiveClausesWithVariables() {
		doTest(
				cClause(cPred(0,x),ab), mList(ab), EMPTY, mList(cClause(ab)), 
				TRUE
		);
		doTest(
				cClause(cPred(0,x),ab), EMPTY, mList(ab), mList(cClause(ab)), 
				cClause(cPred(0,x))
		);
		doTest(
				cClause(cPred(0,x),nab), mList(nab), EMPTY, mList(cClause(nab)),
				TRUE
		);
		doTest(
				cClause(cPred(0,x),nab), EMPTY, mList(nab), mList(cClause(nab)), 
				cClause(cPred(0,x))
		);		
	}

	public void testSimpleEquivalenceClauses () {
		doTest(
				cEqClause(cProp(0),ab), mList(ab), EMPTY, mList(cClause(ab)), 
				cClause(cProp(0))
		);
		doTest(
				cEqClause(cProp(0),ab), EMPTY, mList(ab), mList(cClause(ab)), 
				cClause(cNotProp(0))
		);
		doTest(
				cEqClause(cProp(0),nab), mList(nab), EMPTY, mList(cClause(nab)),
				cClause(cProp(0))
		);
		doTest(
				cEqClause(cProp(0),nab), EMPTY, mList(nab), mList(cClause(nab)), 
				cClause(cNotProp(0))
		);
	}

	public void testSimpleEquivalenceClausesWithConditions() {
		// conditions
		doTest(
				cEqClause(mList(cProp(0),cProp(1)),ab), mList(ab), EMPTY, mList(cClause(ab)), 
				TRUE
		);
		doTest(
				cEqClause(mList(cProp(0),cProp(1)),ab), EMPTY, mList(ab), mList(cClause(ab)), 
				cEqClause(cProp(0),cProp(1))
		);
		doTest(
				cEqClause(mList(cProp(0),cProp(1)),nab), mList(nab), EMPTY, mList(cClause(nab)),
				TRUE
		);
		doTest(
				cEqClause(mList(cProp(0),cProp(1)),nab), EMPTY, mList(nab), mList(cClause(nab)), 
				cEqClause(cProp(0),cProp(1))
		);
	}

	@SuppressWarnings("unchecked")
	public void testSimpleEquivalenceClausesWithConditionsAndEqualities() {
		// mixed
		// conditions
		doTest(
				cEqClause(mList(cProp(0),cProp(1),bc),ab), EMPTY, mList(ab,bc), mList(cClause(ab)), 
				cEqClause(cNotProp(0),cProp(1))
		);
		doTest(
				cEqClause(mList(cProp(0),cProp(1),bc),ab), mList(bc), mList(ab), mList(cClause(ab)), 
				cEqClause(cProp(0),cProp(1))
		);
		doTest(
				cEqClause(mList(cProp(0),bc),ab), EMPTY, mList(ab,bc), mList(cClause(ab)),
				cClause(cNotProp(0))
		);
		doTest(
				cEqClause(mList(cProp(0),bc),ab), mList(bc), mList(ab), mList(cClause(ab)),
				cClause(cProp(0))
		);
	}


	
	public void doTest(Clause original, List<EqualityLiteral> trueEqualities,
			List<EqualityLiteral> falseEqualities, List<Clause> parents, Clause expected) {
		EqualityInferrer inferrer = new EqualityInferrer(new VariableContext());
		for (EqualityLiteral equality : trueEqualities) {
			inferrer.addEquality(equality, true);
		}
		for (EqualityLiteral equality : falseEqualities) {
			inferrer.addEquality(equality, false);
		}
		inferrer.addParentClauses(parents);
		original.infer(inferrer);
		Clause actual = inferrer.getResult();
		if (actual.isTrue()) assertTrue(expected.isTrue());
		else if (actual.isFalse()) assertTrue(expected.isFalse());
		else assertEquals(expected, actual);

		disjointVariables(original, actual);
	}
	
	
}
