package org.eventb.pp.core.provers.predicate;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.junit.Test;

/**
 * TODO Comment
 *
 * @author François Terrier
 *
 */
public class TestPredicateProver extends AbstractPPTest {

	private class TestPair {
		List<Clause> unit, nonUnit;
		Clause[] result;
		
		TestPair(List<Clause> nonUnit, List<Clause> unit, Clause... result) {
			this.unit = unit;
			this.nonUnit = nonUnit;
			this.result = result;
		}
		
//		TestPair(String input, Clause output) {
//			LoaderResult result = Util.doPhaseOneAndTwo(input);		
//			assert result.getClauses().size() == 1;
//			assert result.getLiterals().size() == 0;
//			this.input = result.getClauses().iterator().next();
//			this.output = output;
//		}
		
	}
	
	
	TestPair[] tests = new TestPair[]{
			// normal case
			new TestPair(
					mList(cClause(cProp(0),cProp(1))),
					mList(cClause(cNotProp(0))),
					cClause(cProp(1))
			),
			// several match case
			new TestPair(
					mList(cClause(cProp(0),cProp(0))),
					mList(cClause(cNotProp(0))),
					cClause(cProp(0)),
					cClause(cProp(0))
			),
			// no match
			new TestPair(
					mList(cClause(cProp(0),cProp(1))),
					mList(cClause(cProp(2)))
			),
			// no match
			new TestPair(
					mList(cClause(cProp(0),cProp(1))),
					mList(cClause(cProp(0)))
			),
			//
			new TestPair(
					mList(cClause(cNotProp(0),cProp(0))),
					mList(cClause(cProp(0))),
					cClause(cProp(0))
			),
			new TestPair(
					mList(cClause(cProp(0),cProp(0),cProp(1))),
					mList(cClause(cNotProp(0))),
					cClause(cProp(0),cProp(1)),
					cClause(cProp(0),cProp(1))
			),
			
			new TestPair(
					mList(	cClause(cProp(0),cProp(1),cProp(2)),
							cClause(cNotProp(0),cProp(1),cProp(2)),
							cClause(cProp(0),cNotProp(1),cProp(2)),
							cClause(cProp(0),cProp(1),cNotProp(2)),
							cClause(cNotProp(0),cNotProp(1),cProp(2)),
							cClause(cNotProp(0),cProp(1),cNotProp(2)),
							cClause(cProp(0),cNotProp(1),cNotProp(2)),
							cClause(cNotProp(0),cNotProp(1),cNotProp(2))
					),
					mList(
							cClause(cProp(0)),
							cClause(cNotProp(0)),
							cClause(cProp(1)),
							cClause(cNotProp(1)),
							cClause(cProp(2)),
							cClause(cNotProp(2))
					),
					cClause(cProp(1),cProp(2)),
					cClause(cNotProp(1),cProp(2)),
					cClause(cProp(1),cNotProp(2)),
					cClause(cNotProp(1),cNotProp(2)),
					cClause(cProp(1),cProp(2)),
					cClause(cNotProp(1),cProp(2)),
					cClause(cProp(1),cNotProp(2)),
					cClause(cNotProp(1),cNotProp(2)),
					
					cClause(cProp(0),cProp(2)),
					cClause(cNotProp(0),cProp(2)),
					cClause(cProp(0),cNotProp(2)),
					cClause(cNotProp(0),cNotProp(2)),
					cClause(cProp(0),cProp(2)),
					cClause(cNotProp(0),cProp(2)),
					cClause(cProp(0),cNotProp(2)),
					cClause(cNotProp(0),cNotProp(2)),
					
					cClause(cProp(0),cProp(1)),
					cClause(cNotProp(0),cProp(1)),
					cClause(cProp(0),cNotProp(1)),
					cClause(cNotProp(0),cNotProp(1)),
					cClause(cProp(0),cProp(1)),
					cClause(cNotProp(0),cProp(1)),
					cClause(cProp(0),cNotProp(1)),
					cClause(cNotProp(0),cNotProp(1))
			),
//			
			new TestPair(
					new ArrayList<Clause>(),
					mList(cClause(cNotProp(0)),cClause(cProp(0)))
			),
			
			new TestPair(
					new ArrayList<Clause>(),
					mList(cClause(cProp(0)),cClause(cProp(0)))
			),
			
			new TestPair(
					new ArrayList<Clause>(),
					mList(cClause(cProp(0)),cClause(cProp(0)))
			),
			
	};

	TestPair[] testEq = new TestPair[]{
			// normal case
			new TestPair(
					mList(cEqClause(cProp(0),cProp(1))),
					mList(cClause(cProp(0))),
					cClause(cProp(1))
			),
			new TestPair(
					mList(cEqClause(cProp(0),cProp(1))),
					mList(cClause(cNotProp(0))),
					cClause(cNotProp(1))
			),
			// several match case
			new TestPair(
					mList(cEqClause(cProp(0),cProp(0))),
					mList(cClause(cProp(0))),
					cClause(cProp(0)),
					cClause(cProp(0))
			),
			new TestPair(
					mList(cEqClause(cProp(0),cProp(0))),
					mList(cClause(cNotProp(0))),
					cClause(cNotProp(0)),
					cClause(cNotProp(0))
			),
			// no match
			new TestPair(
					mList(cEqClause(cProp(0),cProp(1))),
					mList(cClause(cProp(2)))
			),
			//
			new TestPair(
					mList(cEqClause(cNotProp(0),cProp(0))),
					mList(cClause(cProp(0))),
					cClause(cNotProp(0)),
					cClause(cNotProp(0))
			),
			new TestPair(
					mList(cEqClause(cProp(0),cProp(0))),
					mList(cClause(cProp(0))),
					cClause(cProp(0)),
					cClause(cProp(0))
			),
			new TestPair(
					mList(cClause(cProp(0),cProp(0),cProp(1))),
					mList(cClause(cNotProp(0))),
					cClause(cProp(0),cProp(1)),
					cClause(cProp(0),cProp(1))
			),
			
			new TestPair(
					mList(	cClause(cProp(0),cProp(1),cProp(2)),
							cClause(cNotProp(0),cProp(1),cProp(2)),
							cClause(cProp(0),cNotProp(1),cProp(2)),
							cClause(cProp(0),cProp(1),cNotProp(2)),
							cClause(cNotProp(0),cNotProp(1),cProp(2)),
							cClause(cNotProp(0),cProp(1),cNotProp(2)),
							cClause(cProp(0),cNotProp(1),cNotProp(2)),
							cClause(cNotProp(0),cNotProp(1),cNotProp(2))
					),
					mList(
							cClause(cProp(0)),
							cClause(cNotProp(0)),
							cClause(cProp(1)),
							cClause(cNotProp(1)),
							cClause(cProp(2)),
							cClause(cNotProp(2))
					),
					cClause(cProp(1),cProp(2)),
					cClause(cNotProp(1),cProp(2)),
					cClause(cProp(1),cNotProp(2)),
					cClause(cNotProp(1),cNotProp(2)),
					cClause(cProp(1),cProp(2)),
					cClause(cNotProp(1),cProp(2)),
					cClause(cProp(1),cNotProp(2)),
					cClause(cNotProp(1),cNotProp(2)),
					
					cClause(cProp(0),cProp(2)),
					cClause(cNotProp(0),cProp(2)),
					cClause(cProp(0),cNotProp(2)),
					cClause(cNotProp(0),cNotProp(2)),
					cClause(cProp(0),cProp(2)),
					cClause(cNotProp(0),cProp(2)),
					cClause(cProp(0),cNotProp(2)),
					cClause(cNotProp(0),cNotProp(2)),
					
					cClause(cProp(0),cProp(1)),
					cClause(cNotProp(0),cProp(1)),
					cClause(cProp(0),cNotProp(1)),
					cClause(cNotProp(0),cNotProp(1)),
					cClause(cProp(0),cProp(1)),
					cClause(cNotProp(0),cProp(1)),
					cClause(cProp(0),cNotProp(1)),
					cClause(cNotProp(0),cNotProp(1))
			),

			new TestPair(
					new ArrayList<Clause>(),
					mList(cClause(cNotProp(0)),cClause(cProp(0)))
			),
			
			new TestPair(
					new ArrayList<Clause>(),
					mList(cClause(cProp(0)),cClause(cProp(0)))
			),
			
			new TestPair(
					new ArrayList<Clause>(),
					mList(cClause(cProp(0)),cClause(cProp(0)))
			),
			
	};

    @Test
	public void testDisj() {
		for (TestPair test : tests) {
			doTest(test);
		}
	}
	
    @Test
	public void testEq() {
		for (TestPair test : testEq) {
			doTest(test);
		}
	}
	
	
    @Test
	public void testHiddenInferrence() {
		doTest(new TestPair(mList(cClause(cPred(d0A,evar1),cProp(1)),cClause(cPred(d0A,a),cProp(1))),
				mList(cClause(cNotPred(d0A,a))),
				cClause(mList(cProp(1)),cNEqual(evar1, a)),
				cClause(mList(cProp(1)),cNEqual(a, a))));
	}
	
	public void doTest(TestPair test) {
			IProverModule prover = getProver();
			
			for (Clause clause : test.nonUnit) {
				prover.addClauseAndDetectContradiction(clause);
			}
			for (Clause clause : test.unit) {
				prover.addClauseAndDetectContradiction(clause);
			}
			
			int i=0;
			for (Clause clause : test.result) {
				ProverResult result = prover.next(true);
				assertEquals(1, result.getGeneratedClauses().size());
				assertEquals(clause, result.getGeneratedClauses().iterator().next());
				i++;
			}
			assertEquals("\nUnit: " + test.unit + "NonUnit: " + test.nonUnit, prover.next(false), ProverResult.EMPTY_RESULT);
			assertEquals(test.result.length, i);
	}
	
    @Test
	public void testEmptyResult() {
		IProverModule predicateProver = getProver();
		
		assertEquals(predicateProver.next(false), ProverResult.EMPTY_RESULT);
	}
	
    @Test
	public void testEmptyResultWithClauses() {
		IProverModule predicateProver = getProver();
		
		assertEquals(predicateProver.addClauseAndDetectContradiction(cClause(cProp(0))),ProverResult.EMPTY_RESULT);
		assertEquals(predicateProver.next(false), ProverResult.EMPTY_RESULT);
	}
	
    @Test
	public void testContradictionResult() {
		IProverModule prover = getProver();
		
		prover.addClauseAndDetectContradiction(cClause(cProp(0)));
		ProverResult result = prover.addClauseAndDetectContradiction(cClause(cNotProp(0)));
		assertEquals(result.getGeneratedClauses().size(), 1);
		assertTrue(result.getGeneratedClauses().iterator().next().isFalse());
	}

	
	private IProverModule getProver() {
		IProverModule predicateProver = new PredicateProver(new VariableContext());
		return predicateProver;
	}
	
	
}
