package org.eventb.pp.core.provers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;

import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult;
import org.eventb.pp.Util;
import org.eventb.pp.PPResult.Result;

public class TestConditions extends AbstractPPTest {

	public void testConditions() {
		initDebug();
		
		Set<Clause> clauses = mSet(
				cClause(cPred(0, a), cPred(1, a), cPred(2, a)),
				cClause(cProp(3), Util.cEqual(a, b)),
				cClause(cNotPred(0, b)),
				cClause(cNotPred(1, b)),
				cClause(cNotPred(2, b)),
				cClause(cNotProp(3))
		);
		
		PPProof proof = new PPProof(clauses);
		proof.load();
		proof.prove(-1);
		PPResult result =  proof.getResult();
		assertTrue(result.getResult() == Result.valid);
	}
}
