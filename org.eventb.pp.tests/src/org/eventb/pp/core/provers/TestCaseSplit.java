//package org.eventb.pp.core.provers;
//
//import static org.eventb.pp.Util.cClause;
//import static org.eventb.pp.Util.cProp;
//
//import java.math.BigInteger;
//
//import junit.framework.TestCase;
//
//import org.eventb.internal.pp.core.IDispatcher;
//import org.eventb.internal.pp.core.IVariableContext;
//import org.eventb.internal.pp.core.Level;
//import org.eventb.internal.pp.core.VariableContext;
//import org.eventb.internal.pp.core.datastructure.DataStructureWrapper;
//import org.eventb.internal.pp.core.elements.IClause;
//import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
//import org.eventb.internal.pp.core.search.IterableHashSet;
//
//public class TestCaseSplit extends TestCase {
//
//	IClause[] clauses = new IClause[]{
//		cClause(cProp(0),cProp(1)),
//		cClause(cProp(2),cProp(3)),
//		cClause(cProp(4),cProp(5)),
//		cClause(cProp(6),cProp(7)),
//		cClause(cProp(8),cProp(9)),
//		cClause(cProp(10),cProp(11)),
//		cClause(cProp(12),cProp(13)),
//	};
//	
//	private static Level BASE = Level.base;
//	private static Level ONE = new Level(BigInteger.ONE);
//	private static Level TWO = new Level(BigInteger.valueOf(2));
//	private static Level THREE = new Level(BigInteger.valueOf(3));
//	private static Level FOUR = new Level(BigInteger.valueOf(4));
//	private static Level FIVE = new Level(BigInteger.valueOf(5));
//	private static Level SIX = new Level(BigInteger.valueOf(6));
//	private static Level SEVEN = new Level(BigInteger.valueOf(7));
//	private static Level EIGHT = new Level(BigInteger.valueOf(8));
//
//	
//	public void testCaseSplit1() {
//		IVariableContext context = new VariableContext();
//		CaseSplitter casesplitter = new CaseSplitter(context);
//		IterableHashSet<IClause> it = new IterableHashSet<IClause>();
//		for (IClause clause : clauses) {
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
//		assertEquals(casesplitter.next(), cClause(cProp(4)));
//	
//		// backtrack to 1
//		dispatcher.setLevel(ONE);
//		casesplitter.contradiction(SEVEN, ONE, false);
//		
//		// level 4
//		dispatcher.setLevel(FOUR);
//		assertEquals(casesplitter.next(), cClause(cProp(3)));
//	}
//	
//	public void testCaseSplit2() {
//		IVariableContext context = new VariableContext();
//		CaseSplitter casesplitter = new CaseSplitter(context);
//		IterableHashSet<IClause> it = new IterableHashSet<IClause>();
//		for (IClause clause : clauses) {
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
//		IterableHashSet<IClause> it = new IterableHashSet<IClause>();
//		for (IClause clause : clauses) {
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
//		IterableHashSet<IClause> it = new IterableHashSet<IClause>();
//		for (IClause clause : clauses) {
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
//		IterableHashSet<IClause> it = new IterableHashSet<IClause>();
//		for (IClause clause : clauses) {
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
//	
//	
//	private static class Dispatcher implements IDispatcher {
//		private Level level;
//		
//		public Level getLevel() {
//			return level;
//		}
//		
//		private void setLevel(Level level) {
//			this.level = level;
//		}
//
//		public boolean hasStopped() {
//			return false;
//		}
//
//		public void newClause(IClause clause) {
//			// do nothing
//		}
//	}
//	
//}
