package org.eventb.pp.core.provers;

import org.eventb.pp.AbstractPPTest;

public class TestEqualityManager extends AbstractPPTest {

//	private static INode a;
//	private static INode b;
//	private static INode c;
//	private static INode d;
//	private static INode e;
//	private static INode f;
//
//	
//	
//	private IEquivalenceManager manager;
//	
//	@Override
//	protected void setUp() throws Exception {
//		// init manager
//	}
//
//	private static IOrigin o(String rule, Level level) {
//		return new Origin(rule,level);
//	}
//	
//	private static <T> Set<T> mSet(T... elements) {
//		return new HashSet<T>(Arrays.asList(elements));
//	}
//	
//	// deductions
//	public void testEmptyManager() {
//		assertFalse(manager.contradictsInequality(a, b));
//		assertFalse(manager.contradictsEquality(a, b));
//	}
//	
//	public void testSimpleEquality() {
//		manager.addEquality(a, b, o("R1",BASE));
//		assertEquals(mSet(
//				"b->a[R1/0]"
//		),manager.dump());
//		
//		manager.addEquality(b, c, o("R2",BASE));
//		assertEquals(mSet(
//				"b->a[R1/0]",
//				"c->b[R2/0]"
//		),manager.dump());
//		
//		manager.addEquality(c, d, o("R3",BASE));
//		assertEquals(mSet(
//				"b->a[R1/0]",
//				"c->b[R2/0]",
//				"d->c[R3/0]"
//		),manager.dump());
//	}
//	
//	public void testMergingTree() {
//		manager.addEquality(a, b, o("R1",BASE));
//		manager.addEquality(b, c, o("R2",BASE));
//		manager.addEquality(d, e, o("R3",BASE));
//		manager.addEquality(e, f, o("R4",BASE));
//		assertEquals(mSet(
//				"b->a[R1/0]",
//				"c->b[R2/0]",
//				"e->d[R3/0]",
//				"f->e[R4/0]"		
//		),manager.dump());
//		
//		manager.addEquality(e, b, o("R5",BASE));
//		assertEquals(mSet(
//				"b->a[R1/0]",
//				"c->b[R2/0]",
//				"d->e[R3/0]",
//				"f->e[R4/0]",
//				"e->b[R5/0]"
//		),manager.dump());
//	}
//	
//	public void testAlreadyEqual() {
//		
//	}
//	
//	
////	public void testSimpleInequality() {
////		manager.addInequality(a, b);
////		assertTrue(manager.contradictsEquality(a, b));
////		assertFalse(manager.contradictsInequality(a, b));
////		assertTrue(manager.getEqualityClasses().size() == 0);
////		assertTrue(manager.getInequalityClasses().size() == 2);
////		
////		manager.addInequality(a, c);
////		assertTrue(manager.contradictsEquality(a, b));
////		assertTrue(manager.contradictsEquality(a, c));
////		assertFalse(manager.contradictsEquality(b, c));
////		assertTrue(manager.contradictsEquality(b, a));
////		assertTrue(manager.contradictsEquality(c, a));
////		assertFalse(manager.contradictsEquality(c, b));
////		
////		assertTrue(manager.getEqualityClasses().size() == 0);
////		assertTrue(manager.getInequalityClasses().size() == 3);
////	}
////	
////	public void testTwoDifferentEqualities() {
////		manager.addEquality(a, b);
////		manager.addEquality(c, d);
////		assertTrue(manager.contradictsInequality(a, b));
////		assertTrue(manager.contradictsInequality(b, a));
////		assertTrue(manager.contradictsInequality(c, d));
////		assertTrue(manager.contradictsInequality(d, c));
////		
////		assertFalse(manager.contradictsInequality(a, c));
////		assertFalse(manager.contradictsInequality(a, d));
////		assertFalse(manager.contradictsInequality(b, c));
////		assertFalse(manager.contradictsInequality(b, d));
////		
////		assertTrue(manager.getInequalityClasses().size()==0);
////		assertTrue(manager.getEqualityClasses().size()==2);
////	}
////	
////	public void testTwoDifferentInequalities() {
////		manager.addInequality(a, b);
////		manager.addInequality(c, d);
////		assertTrue(manager.contradictsEquality(a, b));
////		assertTrue(manager.contradictsEquality(c, d));
////		assertTrue(manager.contradictsEquality(b, a));
////		assertTrue(manager.contradictsEquality(d, c));
////		
////		assertFalse(manager.contradictsEquality(a, c));
////		assertFalse(manager.contradictsEquality(a, d));
////		assertFalse(manager.contradictsEquality(b, c));
////		assertFalse(manager.contradictsEquality(b, d));
////		
////		assertTrue(manager.getInequalityClasses().size() == 4);
////		assertTrue(manager.getEqualityClasses().size() == 0);
////	}
////	
////	public void testEqualitiesThenInequalities() {
////		manager.addEquality(a, b);
////		manager.addEquality(c, d);
////		manager.addInequality(a, c);
////		assertTrue(manager.contradictsEquality(a, d));
////		assertTrue(manager.contradictsEquality(a, c));
////		assertTrue(manager.contradictsEquality(b, c));
////		assertTrue(manager.contradictsEquality(d, a));
////		assertTrue(manager.contradictsEquality(c, a));
////		assertTrue(manager.contradictsEquality(c, b));
////		
////		assertTrue(manager.contradictsEquality(b, d));
////		assertTrue(manager.contradictsEquality(d, b));
////		
////		assertFalse(manager.contradictsEquality(a, b));
////		assertFalse(manager.contradictsEquality(c, d));
////		assertFalse(manager.contradictsInequality(a, c));
////		
////		assertTrue(manager.getInequalityClasses().size()==4);
////		assertTrue(manager.getEqualityClasses().size()==2);
////	}
////
////	public void testInequalitiesThenEqualities() {
////		manager.addInequality(a, b);
////		manager.addInequality(c, d);
////		manager.addEquality(a, c);
////		
////		assertTrue(manager.contradictsEquality(a, d));
////		assertTrue(manager.contradictsEquality(c, b));
////		assertTrue(manager.contradictsEquality(a, b));
////		assertTrue(manager.contradictsEquality(c, d));
////
////		assertTrue(manager.contradictsEquality(d, a));
////		assertTrue(manager.contradictsEquality(b, c));
////		assertTrue(manager.contradictsEquality(b, a));
////		assertTrue(manager.contradictsEquality(d, c));
////		
////		assertTrue(manager.contradictsEquality(a, b));
////		assertTrue(manager.contradictsEquality(c, d));
////		
////		assertFalse(manager.contradictsEquality(b, d));
////		assertFalse(manager.contradictsEquality(d, b));
////		
////		assertTrue(manager.getInequalityClasses().size()==4);
////		assertTrue(manager.getEqualityClasses().size()==1);
////	}
////	
////	// origin
////	public void testEqualityOrigin() {
////		manager.addEquality(a, b);
////		manager.addEquality(b, c);
////		
////		assertEquals(mSet(ab),manager.getOrigins(a, b));
////		assertEquals(mSet(bc),manager.getOrigins(b, c));
////		assertEquals(mSet(abc),manager.getOrigins(a, c));
////		
////		manager.addEquality(c, d);
////		assertEquals(mSet(ab),manager.getOrigins(a, b));
////		assertEquals(mSet(bc),manager.getOrigins(b, c));
////		assertEquals(mSet(abc),manager.getOrigins(a, c));
////
////		assertEquals(mSet(abcd),manager.getOrigins(a, d));
////		assertEquals(mSet(bcd),manager.getOrigins(b, d));
////
////		manager.addEquality(a, d);
////		assertEquals(mSet(ab),manager.getOrigins(a, b));
////		assertEquals(mSet(bc),manager.getOrigins(b, c));
////		assertEquals(mSet(abc,adc),manager.getOrigins(a, c));
////
////		assertEquals(mSet(abcd,ad),manager.getOrigins(a, d));
////		assertEquals(mSet(bad),manager.getOrigins(b, d));
////		
////	}
//	
//	// origin with different levels
//	
//	
//	// backtracking
//	
//	
//	private static class Origin implements IOrigin {
//		String rule;
//		Level level;
//		Origin(String rule,Level level) {
//			this.rule = rule;
//		}
//		
//		@Override
//		public String toString() {
//			return "["+rule+"/"+level+"]";
//		}
//
//		public Level getLevel() {
//			return level;
//		}
//	}
	
}
