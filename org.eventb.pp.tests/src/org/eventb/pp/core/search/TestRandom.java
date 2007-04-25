package org.eventb.pp.core.search;

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.eventb.internal.pp.core.search.IRandomAccessIterable;
import org.eventb.internal.pp.core.search.IterableHashSet;

public class TestRandom extends TestCase {

	private Object a = new Object();
	private Object b = new Object();
	private Object c = new Object();
	private Object d = new Object();
	private Object e = new Object();
	private Object f = new Object();
	private Object g = new Object();
	private Object h = new Object();
	private Object i = new Object();
	private Object j = new Object();
	private Object k = new Object();
	
	
	public void testConstruction() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		set.appends(e);
		set.appends(f);
		set.appends(g);
		
		for (int i=0;i<2;i++) {
			Iterator<Object> it = set.iterator();
			assertEquals(it.next(), a);
			assertEquals(it.next(), b);
			assertEquals(it.next(), c);
			assertEquals(it.next(), d);
			assertEquals(it.next(), e);
			assertEquals(it.next(), f);
			assertEquals(it.next(), g);
		}
	}
	
	public void testNoSuchElement() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		it.next();
		it.next();
		it.next();
		it.next();
		try {
			it.next();
		} catch (NoSuchElementException e) {
			assertTrue(true);
		}
	}
	
//	public void testNextNotInitialized() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		try {
//			it.next();
//		} catch (IllegalStateException e) {
//			assertTrue(true);
//		}
//	}
	
//	public void testHasNextNotInitialized() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		try {
//			set.hasNext();
//		} catch (IllegalStateException e) {
//			assertTrue(true);
//		}
//	}
	
	public void testHasNext() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.hasNext(), true);
		assertEquals(it.next(), a);
		assertEquals(it.hasNext(), true);
		assertEquals(it.next(), b);
		assertEquals(it.hasNext(), true);
		assertEquals(it.next(), c);
		assertEquals(it.hasNext(), true);
		assertEquals(it.next(), d);
		assertEquals(it.hasNext(), false);
	}
	

	public void testRemoveFirstNoIt() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		set.remove(a);
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	
	
	public void testRemoveLastNoIt() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		set.remove(d);
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
	}
	
	public void testRemoveMidNoIt() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		set.remove(b);
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	public void testRemoveInexistantNoIt() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		assertNull(set.remove(e));
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	public void testRemoveFirstIt() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		set.remove(a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	public void testRemoveFirstItOneElement() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		set.remove(a);
		try {
			it.next();
		} catch (NoSuchElementException	e) {
			assertTrue(true);
		}
	}
	
	public void testAddAfterEnd1() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		
		Iterator<Object> it = set.iterator();
		assertFalse(it.hasNext());
		set.appends(a);
		assertTrue(it.hasNext());
		assertEquals(it.next(), a);
	}
	

	public void testAddAfterEnd2() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertFalse(it.hasNext());
		set.remove(a);
		set.appends(b);
		assertTrue(it.hasNext());
		assertEquals(it.next(), b);
	}
	
	public void testAddAfterEnd3() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		
		Iterator<Object> it = set.iterator();
		assertFalse(it.hasNext());
		set.appends(a);
		set.appends(b);
		
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		set.remove(b);
		set.appends(d);
		assertEquals(it.next(), d);
	}
	
	public void testAddAfterEnd4() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		Iterator<Object> it = set.iterator();
		assertFalse(it.hasNext());
		
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		set.remove(b);
		set.remove(c);
		
		assertEquals(it.next(), d);
	}
	
	public void testRemoveFirstItAfter() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		set.remove(a);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
		it = set.iterator();
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
//	public void testRemoveException() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		set.appends(b);
//		set.appends(c);
//		set.appends(d);
//		
//		set.resetIterator();
//		assertEquals(it.next(), a);
//		try {
//			set.remove(a);
//		} catch (IllegalStateException e) {
//			assertTrue(true);
//		}
//	}
	
	public void testRemoveLastIt() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		set.remove(d);
		try {
			it.next();
		} catch (NoSuchElementException	e) {
			assertTrue(true);
		}
	}
	
	public void testRemoveMidIt1() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		set.remove(b);
		assertEquals(it.next(), a);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
		
		it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	public void testRemoveMidIt2() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		set.remove(b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
		
		it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	public void testRemoveMidIt3() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		set.remove(b);
		assertEquals(it.next(), d);
		
		it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	public void testRemoveMidIt4() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
		set.remove(b);
		
		it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	public void testRemoveMidIt5() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		set.remove(b);
		set.remove(c);
		
		assertEquals(it.next(), d);
	}
	
	public void testRemoveInexistantIt() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertNull(set.remove(e));
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	public void testAppendsSame() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(a);
		
		Iterator<Object> it = set.iterator();
		assertEquals(set.remove(a), a);
		assertFalse(it.hasNext());
	}
	
//	public void testremoveUninitialized() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		set.appends(b);
//		set.appends(c);
//		set.appends(d);
//		
//		try {
//			set.remove();
//		}
//		catch (IllegalStateException e) {
//			assertTrue(true);
//		}
//	}
	
//	public void testremoveNoNextException() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		set.appends(b);
//		set.appends(c);
//		set.appends(d);
//		
//		Iterator<Object> it = set.iterator();
//		try {
//			set.remove();
//		}
//		catch (IllegalStateException e) {
//			assertTrue(true);
//		}
//	}
	
//	public void testremoveTwiceException() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		set.appends(b);
//		set.appends(c);
//		set.appends(d);
//		
//		Iterator<Object> it = set.iterator();
//		it.next();
//		set.remove();
//		try {
//			set.remove();
//		}
//		catch (IllegalStateException e) {
//			assertTrue(true);
//		}
//	}
	
//	public void testremoveFirst() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		set.appends(b);
//		set.appends(c);
//		set.appends(d);
//		
//		Iterator<Object> it = set.iterator();
//		assertEquals(it.next(), a);
//		set.remove(a);
//		assertEquals(it.next(), b);
//		assertEquals(it.next(), c);
//		assertEquals(it.next(), d);
//		
//		it = set.iterator();
//		assertEquals(it.next(), b);
//		assertEquals(it.next(), c);
//		assertEquals(it.next(), d);
//	}
	
	public void testremoveLast() {
		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
		set.appends(a);
		set.appends(b);
		set.appends(c);
		set.appends(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		set.remove(d);
		
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		}
		catch(NoSuchElementException e) {
		}
	}
	
	public void testRemoveSameObject() {
		IRandomAccessIterable<MyObject> set = new IterableHashSet<MyObject>();
		MyObject a1 = new MyObject("a");
		MyObject a2 = new MyObject("a");
		
		set.appends(a1);
		assertTrue(set.contains(a2));
		set.remove(a2);
		
		assertFalse(set.contains(a1));
		assertFalse(set.contains(a2));
		
		Iterator<MyObject> it = set.iterator();
		assertFalse(it.hasNext());
		
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
		}
	}
	
	public void testRemoveSameObjectWithIterator() {
		IRandomAccessIterable<MyObject> set = new IterableHashSet<MyObject>();
		MyObject b = new MyObject("b");
		MyObject a1 = new MyObject("a");
		MyObject a2 = new MyObject("a");
		
		set.appends(b);
		set.appends(a1);
		assertTrue(set.contains(a2));
		
		Iterator<MyObject> it = set.iterator();
		assertEquals(it.next(),b);
		assertEquals(it.next(), a1);
		
		set.remove(a2);
		
		assertFalse(set.contains(a1));
		assertFalse(set.contains(a2));
		
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
		}
	}
	
	
	private static class MyObject {
		private String str;
		
		public MyObject(String str) {
			this.str = str;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MyObject) {
				MyObject temp = (MyObject) obj;
				return str.equals(temp.str);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return str.hashCode();
		}
		
	}
	
//	public void testremoveMid1() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		set.appends(b);
//		set.appends(c);
//		set.appends(d);
//		
//		Iterator<Object> it = set.iterator();
//		assertEquals(it.next(), a);
//		assertEquals(it.next(), b);
//		set.remove();
//		assertEquals(it.next(), c);
//		assertEquals(it.next(), d);
//		
//		Iterator<Object> it = set.iterator();
//		assertEquals(it.next(), a);
//		assertEquals(it.next(), c);
//		assertEquals(it.next(), d);
//	}
//	
//	public void testremoveMid2() {
//		IRandomAccessIterable<Object> set = new IterableHashSet<Object>();
//		set.appends(a);
//		set.appends(b);
//		set.appends(c);
//		set.appends(d);
//		
//		Iterator<Object> it = set.iterator();
//		assertEquals(it.next(), a);
//		assertEquals(it.next(), b);
//		assertEquals(set.next(), c);
//		set.remove();
//		assertEquals(it.next(), d);
//		
//		Iterator<Object> it = set.iterator();
//		assertEquals(set.next(), a);
//		assertEquals(set.next(), b);
//		assertEquals(set.next(), d);
//	}
	
}
