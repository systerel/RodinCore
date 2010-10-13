package org.eventb.pp.core.inferrers;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.EqualityInferrer;
import org.junit.Test;

public class TestEqualityInferrer extends AbstractInferrerTests {

    @Test
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
	
    @Test
	public void testDisappearingDisjunctiveClauses() {
		doTest(
				cClause(ab), mList(ab), EMPTY, mList(cClause(ab)),
				TRUE
		);
		doTest(
				cClause(ab), EMPTY, mList(ab), mList(cClause(ab)),
				FALSE
		);
		doTest(
				cClause(mList(ab), bc), EMPTY, mList(ab), mList(cClause(ab)),
				cClause(EMPTY, bc)
		);
    }

    @Test
	public void testSimpleDisjunctiveClausesWithVariables() {
		doTest(
				cClause(cPred(d0A,x),ab), mList(ab), EMPTY, mList(cClause(ab)), 
				TRUE
		);
		doTest(
				cClause(cPred(d0A,x),ab), EMPTY, mList(ab), mList(cClause(ab)), 
				cClause(cPred(d0A,x))
		);
		doTest(
				cClause(cPred(d0A,x),nab), mList(nab), EMPTY, mList(cClause(nab)),
				TRUE
		);
		doTest(
				cClause(cPred(d0A,x),nab), EMPTY, mList(nab), mList(cClause(nab)), 
				cClause(cPred(d0A,x))
		);		
	}

    @Test
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

    @Test
	public void testDisappearingEquivalenceClauses() {
		doTest(
				cEqClause(ab, cd), mList(ab, cd), EMPTY, mList(cClause(nab)),
				TRUE
		);
		doTest(
				cEqClause(ab, cd), EMPTY, mList(ab, cd), mList(cClause(nab)),
				TRUE
		);
		doTest(
				cEqClause(ab, cd), mList(ab), mList(cd), mList(cClause(nab)),
				FALSE
		);
		doTest(
				cEqClause(ab, cd), mList(cd), mList(ab), mList(cClause(nab)),
				FALSE
		);
    }

    @Test
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

    @Test
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
		doTest(
				cEqClause(mList(cProp(0),bc),ab), mList(bc), EMPTY, mList(cClause(ab)),
				cClause(mList(cProp(0)), ab)
		);
		doTest(
				cEqClause(mList(cProp(0),bc),ab), EMPTY, mList(bc), mList(cClause(ab)),
				cClause(mList(cNotProp(0)), ab)
		);
	}

    @Test
	public void testDisappearingEquivalenceClausesWithConditions() {
		doTest(
				cEqClause(mList(ab, cd), bc), mList(ab, cd), mList(bc), mList(cClause(ab)),
				TRUE
		);
		doTest(
				cEqClause(mList(ab, cd), bc), mList(ab, cd, bc), EMPTY, mList(cClause(ab)),
				TRUE
		);
		doTest(
				cEqClause(mList(ab, cd), bc), EMPTY, mList(ab, cd, bc), mList(cClause(ab)),
				TRUE
		);
		doTest(
				cEqClause(mList(ab, cd), bc), mList(ab, cd), EMPTY, mList(cClause(ab)),
				TRUE
		);
		doTest(
				cEqClause(mList(ab, cd), bc), EMPTY, mList(ab, cd), mList(cClause(ab)),
				TRUE
		);
		doTest(
				cEqClause(mList(ab, cd), bc), mList(ab), mList(cd), mList(cClause(ab)),
				cClause(EMPTY, bc)
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
