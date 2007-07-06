package org.eventb.pp.core.search;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cProp;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.datastructure.DataStructureWrapper;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.search.IterableHashSet;

public class TestBacktracking extends TestCase {

	
	private static Level L0 = Level.base;
	private static Level L1 = new Level(BigInteger.ONE);
	private static Level L2 = new Level(BigInteger.valueOf(2));
	private static Level L3 = new Level(BigInteger.valueOf(3));
	private static Level L4 = new Level(BigInteger.valueOf(4));
	private static Level L5 = new Level(BigInteger.valueOf(5));
	
	
	private Clause[] clauses1 = new Clause[]{
		cClause(L0,cProp(0)),
		cClause(L1,cProp(1)),
		cClause(L2,cProp(2)),
		cClause(L3,cProp(3)),
		cClause(L4,cProp(4)),
		cClause(L5,cProp(5)),
	};
	
//	private Clause[] clauses2 = new Clause[]{
//		cClause(L0,cProp(0)),
//		cClause(L1,cProp(1)),
//		cClause(L2,cProp(2)),
//		cClause(L3,cProp(3)),
//		cClause(L4,cProp(4)),
//		cClause(L5,cProp(5)),
//	};
		
		
	
	private DataStructureWrapper prepareProver(Clause[] clauses) {
		IterableHashSet<Clause> set = new IterableHashSet<Clause>();
		for (Clause clause : clauses) {
			set.appends(clause);
		}
		DataStructureWrapper prover = new DataStructureWrapper(set);
		return prover;
	}
	
	private void doTest(Clause[] clauses, Level level) {
		DataStructureWrapper prover;
		prover = prepareProver(clauses1);
		prover.backtrack(level);
		for (Clause clause : prover) {
			assertFalse(level.isAncestorOf(clause.getLevel()));
		}
	}
	
	public void testBacktracking() {
		doTest(clauses1, L0);
		doTest(clauses1, L1);
		doTest(clauses1, L2);
		doTest(clauses1, L3);
		doTest(clauses1, L4);
		doTest(clauses1, L5);
	}
	
}
