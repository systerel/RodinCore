package org.eventb.pp.core.provers.predicate;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cCons;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.mList;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.loader.clause.VariableContext;

/**
 * TODO Comment
 *
 * @author François Terrier
 *
 */
public class TestPredicateProver extends TestCase {

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

	public void testDisj() {
		for (TestPair test : tests) {
			doTest(test);
		}
	}
	
	public void testEq() {
		for (TestPair test : testEq) {
			doTest(test);
		}
	}
	
	
	public void testHiddenInferrence() {
		doTest(new TestPair(mList(cClause(cPred(0,cELocVar(1)),cProp(1)),cClause(cPred(0,cCons("a")),cProp(1))),
				mList(cClause(cNotPred(0,cCons("a")))),
				cClause(mList(cProp(1)),cNEqual(cELocVar(1), cCons("a"))),
				cClause(mList(cProp(1)),cNEqual(cCons("a"), cCons("a")))));
	}
	
	public void doTest(TestPair test) {
			PredicateProver prover = new PredicateProver(new VariableContext());
			prover.initialize(new ClauseSimplifier());
			
			for (Clause clause : test.nonUnit) {
				prover.addClauseAndDetectContradiction(clause);
			}
			for (Clause clause : test.unit) {
				prover.addClauseAndDetectContradiction(clause);
			}
			
			int i=0;
			for (Clause clause : test.result) {
				ProverResult result = prover.next();
				assertEquals(1, result.getGeneratedClauses().size());
				assertEquals(clause, result.getGeneratedClauses().iterator().next());
				i++;
			}
			assertNull("\nUnit: " + test.unit + "NonUnit: " + test.nonUnit, prover.next());
			assertEquals(test.result.length, i);
	}
	
	public void testInitialization() {
		IProver predicateProver = new PredicateProver(new VariableContext());
		try {
			predicateProver.next();
			fail();
		}
		catch (IllegalStateException e) {
		}
		try {
			predicateProver.addClauseAndDetectContradiction(null);
			fail();
		}
		catch (IllegalStateException e) {
		}
	}
	
}
