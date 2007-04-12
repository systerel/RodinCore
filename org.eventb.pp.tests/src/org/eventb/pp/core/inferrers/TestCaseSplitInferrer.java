package org.eventb.pp.core.inferrers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;

import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.inferrers.CaseSplitInferrer;
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

	private static class TestPair {
		IClause original;
		IClause left,right;
		
		TestPair(IClause original, IClause left, IClause right) {
			this.original = original;
			this.left = left;
			this.right = right;
		}
	}
	

	TestPair[] pairs = new TestPair[]{
			new TestPair(
					cClause(BASE, cProp(0),cProp(1)),
					cClause(ONE, cProp(0)),
					cClause(TWO, cProp(1))
			),
			new TestPair(
					cEqClause(BASE, cProp(0),cProp(1)),
					cClause(ONE, cProp(0)),
					cClause(TWO, cNotProp(1))
			),
			new TestPair(
					cClause(BASE, cEqual(a,b),cPred(0,a)),
					cClause(ONE, cPred(0,a)),
					cClause(TWO, cEqual(a,b))
			),
			new TestPair(
					cClause(BASE, cEqual(a,b),cEqual(b,c)),
					cClause(ONE, cEqual(a,b)),
					cClause(TWO, cEqual(b,c))
			),
			new TestPair(
					cEqClause(BASE, cPred(0,fvar0), cPred(0,a)),
					cClause(ONE, cPred(0,x)),
					cClause(TWO, cNotPred(0,a))
			),
			new TestPair(
					cClause(BASE, cPred(0,evar0), cPred(0,a)),
					cClause(ONE, cPred(0,evar0)),
					cClause(TWO, cPred(0,a))
			),
			new TestPair(
					cEqClause(BASE, cPred(0,evar0), cPred(0,a)),
					cClause(ONE, cPred(0,evar0)),
					cClause(TWO, cNotPred(0,a))
			),
			new TestPair(
					cEqClause(BASE, cProp(0), cProp(1), cProp(2)),
					cClause(ONE, cProp(0)),
					cEqClause(TWO, cNotPred(1), cProp(2))
			),
			
	};

	public void testCaseSplitInferrer() {
		CaseSplitInferrer inferrer = new CaseSplitInferrer(new VariableContext());
		
		for (TestPair test : pairs) {
			inferrer.setLevel(test.original.getLevel());
			test.original.infer(inferrer);
			assertTrue("Expected: "+test.left+", was: "+inferrer.getLeftCase()+", from :"+test.original,inferrer.getLeftCase().equalsWithLevel(test.left));
			assertTrue("Expected: "+test.right+", was: "+inferrer.getRightCase()+", from :"+test.original,inferrer.getRightCase().equalsWithLevel(test.right));
		}
		
	}
	
	public void testIllegal() {
		CaseSplitInferrer inferrer = new CaseSplitInferrer(new VariableContext());

		IClause clause = cClause(cProp(0),cProp(1));
		try {
			clause.infer(inferrer);
			fail();
		}
		catch (IllegalStateException e) {
		}
	}	
	
	public void testCanInfer() {
		CaseSplitInferrer inferrer = new CaseSplitInferrer(new VariableContext());
		
		IClause[] canInfer = new IClause[]{
				cClause(cProp(0),cProp(1)),
				cClause(cPred(0,a),cPred(1,b)),
				cClause(cPred(0,evar0),cPred(1,evar1)),
				cClause(cEqual(a,b),cPred(0,a)),
				cEqClause(cProp(0),cProp(1)),
				cEqClause(cPred(0,a),cPred(1,b)),
				cEqClause(cPred(0,evar0),cPred(1,evar1)),
				cEqClause(cEqual(a,b),cPred(0,a)),
		};

		for (IClause clause : canInfer) {
			assertTrue(inferrer.canInfer(clause));
		}
	}
	
	public void testCannotInfer() {
		CaseSplitInferrer inferrer = new CaseSplitInferrer(new VariableContext());
		
		IClause[] cannotInfer = new IClause[]{
				cClause(cProp(0)),
				cClause(cPred(0,x),cPred(0,x)),
				cClause(cPred(0,x),cPred(1,x,y)),
				cClause(cEqual(x,b),cPred(0,x)),
				cEqClause(cPred(0,x),cPred(0,x)),
				cEqClause(cPred(0,x),cPred(1,x,y)),
		};

		for (IClause clause : cannotInfer) {
			assertFalse(inferrer.canInfer(clause));
		}
	}
	
}
