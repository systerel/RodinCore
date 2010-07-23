package org.eventb.pp.core.provers.casesplit;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.mSet;
import static org.junit.Assert.assertTrue;

import org.eventb.internal.pp.core.ILevelController;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.junit.Test;

public class TestCaseSplit extends AbstractPPTest {

	Clause[] clauses = new Clause[]{
		cClause(cProp(0),cProp(1)),
		cClause(cProp(2),cProp(3)),
		cClause(cProp(4),cProp(5)),
		cClause(cProp(6),cProp(7)),
		cClause(cProp(8),cProp(9)),
		cClause(cProp(10),cProp(11)),
		cClause(cProp(12),cProp(13)),
	};
	
	private CaseSplitter getCaseSplitter(ILevelController tracer) {
		VariableContext context = new VariableContext();
		CaseSplitter casesplitter = new CaseSplitter(context, tracer);
		return casesplitter;
	}
	
	private Clause getClause(ProverResult result) {
		return result.getGeneratedClauses().iterator().next();
	}
	
    @Test
	public void testCaseSplit1() {
		MyDispatcher dispatcher = new MyDispatcher();
		CaseSplitter casesplitter = getCaseSplitter(dispatcher);
		for (Clause clause : clauses) {
			casesplitter.addClauseAndDetectContradiction(clause);
		}
		ProverResult result;

		dispatcher.setNextLevel(ONE);
		// level 1
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(ONE,cProp(0))));
		
		dispatcher.setNextLevel(THREE);
		// level 3
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(THREE,cProp(2))));
		
		dispatcher.setNextLevel(SEVEN);
		// level 7
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(SEVEN,cProp(4))));
		
		dispatcher.setLevel(ONE);
		// backtrack to 1
		casesplitter.contradiction(SEVEN, ONE, mSet(SEVEN,ONE,BASE));
		
		// level 4
		dispatcher.setNextLevel(FOUR);
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(FOUR,cProp(3))));
	}
	
	
	
    @Test
	public void testCaseSplit2() {
		MyDispatcher dispatcher = new MyDispatcher();
		CaseSplitter casesplitter = getCaseSplitter(dispatcher);
		for (Clause clause : clauses) {
			casesplitter.addClauseAndDetectContradiction(clause);
		}
		ProverResult result;

		dispatcher.setNextLevel(ONE);
		// level 1
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(ONE,cProp(0))));
		
		// backtrack to 1
		dispatcher.setLevel(BASE);
		casesplitter.contradiction(ONE, BASE, mSet(ONE,BASE));
		
		// level 2
		dispatcher.setNextLevel(TWO);
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(TWO,cProp(1))));
	}
	
	
    @Test
	public void testCaseSplit3() {	
		MyDispatcher dispatcher = new MyDispatcher();
		CaseSplitter casesplitter = getCaseSplitter(dispatcher);
		for (Clause clause : clauses) {
			casesplitter.addClauseAndDetectContradiction(clause);
		}
		ProverResult result;
		
		dispatcher.setNextLevel(ONE);
		// level 1
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(ONE,cProp(0))));
		
		dispatcher.setNextLevel(THREE);
		// level 3
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(THREE,cProp(2))));
		
		dispatcher.setNextLevel(SEVEN);
		// level 7
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(SEVEN,cProp(4))));
	
		// backtrack to base
		dispatcher.setLevel(BASE);
		casesplitter.contradiction(SEVEN, BASE, mSet(SEVEN,BASE));
		
		// level 2
		dispatcher.setNextLevel(TWO);
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(TWO,cProp(1))));
	}
	
//	public void testCaseSplit5() {
//		IVariableContext context = new VariableContext();
//		CaseSplitter casesplitter = new CaseSplitter(context);
//		IterableHashSet<Clause> it = new IterableHashSet<Clause>();
//		for (Clause clause : clauses) {
//			it.appends(clause);
//		}
//		DataStructureWrapper wrapper = new DataStructureWrapper(it);
//		Dispatcher dispatcher = new Dispatcher();
//		casesplitter.initialize(dispatcher, wrapper);
//		
//		dispatcher.setLevel(ONE);
//		// level 1
//		assertEquals(casesplitter.next(),cClause(cProp(0)));
//		
//		dispatcher.setLevel(THREE);
//		// level 3
//		assertEquals(casesplitter.next(),cClause(cProp(2)));
//		
//		dispatcher.setLevel(SEVEN);
//		// level 7
//		assertEquals(casesplitter.next(),cClause(cProp(4)));
//	
//		// backtrack to 3
//		dispatcher.setLevel(THREE);
//		casesplitter.contradiction(SEVEN, THREE, false);
//		// level 3
//		// do nothing
//		// backtrack to 1
//		dispatcher.setLevel(ONE);
//		casesplitter.contradiction(THREE, ONE, false);
//		
//		dispatcher.setLevel(FOUR);
//		// level 4
//		assertEquals(casesplitter.next(),cClause(cProp(3)));
//	}
//	
//	
//	public void testCaseSplit4() {
//		IVariableContext context = new VariableContext();
//		CaseSplitter casesplitter = new CaseSplitter(context);
//		IterableHashSet<Clause> it = new IterableHashSet<Clause>();
//		for (Clause clause : clauses) {
//			it.appends(clause);
//		}
//		DataStructureWrapper wrapper = new DataStructureWrapper(it);
//		Dispatcher dispatcher = new Dispatcher();
//		casesplitter.initialize(dispatcher, wrapper);
//		
//		dispatcher.setLevel(ONE);
//		// level 1
//		assertEquals(casesplitter.next(),cClause(cProp(0)));
//		
//		dispatcher.setLevel(THREE);
//		// level 3
//		assertEquals(casesplitter.next(),cClause(cProp(2)));
//		
//		dispatcher.setLevel(SEVEN);
//		// level 7
//		assertEquals(casesplitter.next(),cClause(cProp(4)));
//	
//		// backtrack to 3
//		dispatcher.setLevel(THREE);
//		casesplitter.contradiction(SEVEN, ONE, false);
//		
//		// level 8
//		dispatcher.setLevel(EIGHT);
//		assertEquals(casesplitter.next(), cClause(cProp(5)));
//		
//		// level left to 8
//		dispatcher.setLevel(EIGHT.getLeftBranch());
//		assertEquals(casesplitter.next(), cClause(cProp(6)));
//		
//		// backtrack to 3
//		dispatcher.setLevel(THREE);
//		casesplitter.contradiction(EIGHT.getLeftBranch(), THREE, false);
//		
//		// level 4
//		dispatcher.setLevel(SEVEN);
//		assertEquals(casesplitter.next(), cClause(cProp(8)));
//	}
	
	
	static class MyDispatcher implements ILevelController {
		private Level level = BASE;

		@Override
		public Level getCurrentLevel() {
			return level;
		}

		@Override
		public void nextLevel() {
			this.level = nextLevel;
		}
		
		public void setLevel(Level level) {
			this.level = level;
		}
		
		private Level nextLevel;
		public void setNextLevel(Level nextLevel) {
			this.nextLevel = nextLevel;
		}
		
	}
	
}
