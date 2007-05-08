package org.eventb.pp.core.inferrers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.mList;

import java.util.List;

import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.inferrers.EqualityInferrer;
import org.eventb.pp.AbstractPPTest;

public class TestEqualityInferrer extends AbstractPPTest {
	
	private static class TestPair {
		IClause original;
		List<IEquality> trueEqualities, falseEqualities;
		List<IClause> parents;
		IClause expected;
		
		TestPair(IClause original, List<IEquality> trueEqualities,
				List<IEquality> falseEqualities, List<IClause> parents, IClause expected) {
			this.original = original;
			this.trueEqualities = trueEqualities;
			this.falseEqualities = falseEqualities;
			this.parents = parents;
			this.expected = expected;
		}
	}
	
	TestPair[] tests = new TestPair[] {
			new TestPair(
					cClause(cProp(0),ab), mList(ab), EMPTY, mList(cClause(ab)), 
					FALSE
			),
			new TestPair(
					cClause(cProp(0),ab), EMPTY, mList(ab), mList(cClause(ab)), 
					cClause(cProp(0))
			),
			new TestPair(
					cClause(cProp(0),nab), mList(nab), EMPTY, mList(cClause(nab)),
					FALSE
			),
			new TestPair(
					cClause(cProp(0),nab), EMPTY, mList(nab), mList(cClause(nab)), 
					cClause(cProp(0))
			)			
	};

	TestPair[] testsEquivalence = new TestPair[] {
			new TestPair(
					cEqClause(cProp(0),ab), mList(ab), EMPTY, mList(cClause(ab)), 
					cClause(cProp(0))
			),
			new TestPair(
					cEqClause(cProp(0),ab), EMPTY, mList(ab), mList(cClause(ab)), 
					cClause(cNotProp(0))
			),
			new TestPair(
					cEqClause(cProp(0),nab), mList(nab), EMPTY, mList(cClause(nab)),
					cClause(cProp(0))
			),
			new TestPair(
					cEqClause(cProp(0),nab), EMPTY, mList(nab), mList(cClause(nab)), 
					cClause(cNotProp(0))
			),
			
			// conditions
			new TestPair(
					cEqClause(mList(cProp(0),cProp(1)),ab), mList(ab), EMPTY, mList(cClause(ab)), 
					FALSE
			),
			new TestPair(
					cEqClause(mList(cProp(0),cProp(1)),ab), EMPTY, mList(ab), mList(cClause(ab)), 
					cEqClause(cProp(0),cProp(1))
			),
			new TestPair(
					cEqClause(mList(cProp(0),cProp(1)),nab), mList(nab), EMPTY, mList(cClause(nab)),
					FALSE
			),
			new TestPair(
					cEqClause(mList(cProp(0),cProp(1)),nab), EMPTY, mList(nab), mList(cClause(nab)), 
					cEqClause(cProp(0),cProp(1))
			),
		
			// mixed
			// conditions
			new TestPair(
					cEqClause(mList(cProp(0),cProp(1),bc),ab), EMPTY, mList(ab,bc), mList(cClause(ab)), 
					cEqClause(cNotProp(0),cProp(1))
			),
			new TestPair(
					cEqClause(mList(cProp(0),cProp(1),bc),ab), mList(bc), mList(ab), mList(cClause(ab)), 
					cEqClause(cProp(0),cProp(1))
			),
			new TestPair(
					cEqClause(mList(cProp(0),bc),ab), EMPTY, mList(ab,bc), mList(cClause(ab)),
					cClause(cNotProp(0))
			),
			new TestPair(
					cEqClause(mList(cProp(0),bc),ab), mList(bc), mList(ab), mList(cClause(ab)),
					cClause(cProp(0))
			),
	};
	

	
	public void doTest(TestPair[] tests) {
		for (TestPair pair : tests) {
			EqualityInferrer inferrer = new EqualityInferrer(new VariableContext());
			for (IEquality equality : pair.trueEqualities) {
				inferrer.addEquality(equality, true);
			}
			for (IEquality equality : pair.falseEqualities) {
				inferrer.addEquality(equality, false);
			}
			inferrer.addParentClauses(pair.parents);
			pair.original.infer(inferrer);
			IClause actual = inferrer.getResult();
			assertEquals(pair.expected, actual);
		}
	}
	
	public void testEqualityInferrer() {
		doTest(tests);
	}

	public void testEqualityInferrerEquivalence() {
		doTest(testsEquivalence);
	}

	
}
