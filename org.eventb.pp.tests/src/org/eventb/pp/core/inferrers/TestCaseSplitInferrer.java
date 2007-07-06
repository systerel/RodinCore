package org.eventb.pp.core.inferrers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;

import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.inferrers.CaseSplitNegationInferrer;
import org.eventb.pp.AbstractPPTest;

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

	public void testCaseSplitInferrer () {
			doTest(
					cClause(BASE, cProp(0),cProp(1)),
					cClause(ONE, cProp(0)),
					cClause(TWO, cNotProp(0))
			);
			doTest(
					cEqClause(BASE, cProp(0),cProp(1)),
					cClause(ONE, cProp(0)),
					cClause(TWO, cNotProp(0))
			);
			doTest(
					cClause(BASE, cEqual(a,b),cPred(0,a)),
					cClause(ONE, cPred(0,a)),
					cClause(TWO, cNotPred(0,a))
			);
			doTest(
					cClause(BASE, cEqual(a,b),cEqual(b,c)),
					cClause(ONE, cEqual(a,b)),
					cClause(TWO, cNEqual(a,b))
			);
			doTest(
					cEqClause(BASE, cPred(0,fvar0), cPred(0,a)),
					cClause(ONE, cPred(0,x)),
					cClause(TWO, cNotPred(0,evar0))
			);
			doTest(
					cClause(BASE, cPred(0,evar0), cPred(0,a)),
					cClause(ONE, cPred(0,evar0)),
					cClause(TWO, cNotPred(0,x))
			);
			doTest(
					cEqClause(BASE, cPred(0,evar0), cPred(0,a)),
					cClause(ONE, cPred(0,evar0)),
					cClause(TWO, cNotPred(0,x))
			);
			doTest(
					cEqClause(BASE, cProp(0), cProp(1), cProp(2)),
					cClause(ONE, cProp(0)),
					cClause(TWO, cNotProp(0))
			);
			
	}

	public void doTest(Clause original, Clause left, Clause right) {
		CaseSplitNegationInferrer inferrer = new CaseSplitNegationInferrer(new VariableContext());

		inferrer.setLevel(original.getLevel());
		original.infer(inferrer);
		assertTrue("Expected: "+left+", was: "+inferrer.getLeftCase()+", from :"+original,inferrer.getLeftCase().equalsWithLevel(left));
		assertTrue("Expected: "+right+", was: "+inferrer.getRightCase()+", from :"+original,inferrer.getRightCase().equalsWithLevel(right));
	}
	
	public void testIllegal() {
		CaseSplitNegationInferrer inferrer = new CaseSplitNegationInferrer(new VariableContext());

		Clause clause = cClause(cProp(0),cProp(1));
		try {
			clause.infer(inferrer);
			fail();
		}
		catch (IllegalStateException e) {
			// normal case
		}
	}	
	
	public void testCanInfer() {
		CaseSplitNegationInferrer inferrer = new CaseSplitNegationInferrer(new VariableContext());
		
		Clause[] canInfer = new Clause[]{
				cClause(cProp(0),cProp(1)),
				cClause(cPred(0,a),cPred(1,b)),
				cClause(cPred(0,evar0),cPred(1,evar1)),
				cClause(cEqual(a,b),cPred(0,a)),
				cEqClause(cProp(0),cProp(1)),
				cEqClause(cPred(0,a),cPred(1,b)),
				cEqClause(cPred(0,evar0),cPred(1,evar1)),
				cEqClause(cEqual(a,b),cPred(0,a)),
		};

		for (Clause clause : canInfer) {
			assertTrue(inferrer.canInfer(clause));
		}
	}
	
	public void testCannotInfer() {
		CaseSplitNegationInferrer inferrer = new CaseSplitNegationInferrer(new VariableContext());
		
		Clause[] cannotInfer = new Clause[]{
				cClause(cProp(0)),
				cClause(cPred(0,x),cPred(0,x)),
				cClause(cPred(0,x),cPred(1,x,y)),
				cClause(cEqual(x,b),cPred(0,x)),
				cEqClause(cPred(0,x),cPred(0,x)),
				cEqClause(cPred(0,x),cPred(1,x,y)),
		};

		for (Clause clause : cannotInfer) {
			assertFalse(inferrer.canInfer(clause));
		}
	}
	
}
