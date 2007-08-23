package org.eventb.pp.core.provers.casesplit;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;

import org.eventb.internal.pp.core.ILevelController;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.Tracer;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;

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
	
	private CaseSplitter getCaseSplitter(Tracer tracer) {
		VariableContext context = new VariableContext();
		CaseSplitter casesplitter = new CaseSplitter(context, tracer);
		return casesplitter;
	}
	
	private Clause getClause(ProverResult result) {
		return result.getGeneratedClauses().iterator().next();
	}
	
	public void testCaseSplit1() {
		Tracer tracer = new Tracer();
		CaseSplitter casesplitter = getCaseSplitter(tracer);
		for (Clause clause : clauses) {
			casesplitter.addClauseAndDetectContradiction(clause);
		}
		
		// level 1
		ProverResult result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(ONE,cProp(0))));
		assertEquals(tracer.getCurrentLevel(), ONE);
		
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(THREE,cProp(2))));
		assertEquals(tracer.getCurrentLevel(), THREE);
		
		result = casesplitter.next(true);
		assertTrue(getClause(result).equalsWithLevel(cClause(SEVEN,cProp(4))));
		assertEquals(tracer.getCurrentLevel(), SEVEN);
		

		
//		// backtrack to 1
//		dispatcher.setLevel(ONE);
//		casesplitter.contradiction(SEVEN, ONE, false);
//		
//		// level 4
//		dispatcher.setLevel(FOUR);
//		assertEquals(casesplitter.next(), cClause(cProp(3)));
	}
	
	
	
//	public void testCaseSplit2() {
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
//		// backtrack to 1
//		dispatcher.setLevel(BASE);
//		casesplitter.contradiction(ONE, BASE, false);
//		
//		// level 2
//		dispatcher.setLevel(TWO);
//		assertEquals(casesplitter.next(), cClause(cProp(1)));
//	}
//	
//	public void testCaseSplit3() {	
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
//		// backtrack to base
//		dispatcher.setLevel(BASE);
//		casesplitter.contradiction(SEVEN, BASE, false);
//		
//		// level 2
//		dispatcher.setLevel(TWO);
//		assertEquals(casesplitter.next(), cClause(cProp(1)));
//	}
//	
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
	
	
	private static class MyDispatcher implements ILevelController {
		private Level level;

		public Level getCurrentLevel() {
			return level;
		}

		public void nextLevel() {
			
		}
		
	}
	
}
