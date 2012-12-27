/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.core.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eventb.internal.pp.core.search.IRandomAccessList;
import org.eventb.internal.pp.core.search.RandomAccessList;
import org.eventb.internal.pp.core.search.ResetIterator;
import org.junit.Test;

public class TestRandom {

	private Object a = new Object();
	private Object b = new Object();
	private Object c = new Object();
	private Object d = new Object();
	private Object e = new Object();
	private Object f = new Object();
	private Object g = new Object();
//	private Object h = new Object();
//	private Object i = new Object();
//	private Object j = new Object();
//	private Object k = new Object();
	
	
    @Test
	public void testConstruction() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		set.add(e);
		set.add(f);
		set.add(g);
		
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
	
    @Test
	public void testNoSuchElement() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		Iterator<Object> it = set.iterator();
		it.next();
		it.next();
		it.next();
		it.next();
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
			// nothing
		}
	}
	
    @Test
	public void testHasNext() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
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
	

    @Test
	public void testRemoveFirstNoIt() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		set.remove(a);
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
	
	
    @Test
	public void testRemoveLastNoIt() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		set.remove(d);
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
	}
	
    @Test
	public void testRemoveMidNoIt() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		set.remove(b);
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
    @Test
	public void testRemoveInexistantNoIt() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		assertNull(set.remove(e));
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
    @Test
	public void testRemoveFirstIt() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		Iterator<Object> it = set.iterator();
		set.remove(a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
    @Test
	public void testRemoveFirstItOneElement() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		set.remove(a);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException	e) {
			// nothing
		}
	}
	
    @Test
	public void testAddAfterEnd1() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		
		Iterator<Object> it = set.iterator();
		assertFalse(it.hasNext());
		set.add(a);
		assertTrue(it.hasNext());
		assertEquals(it.next(), a);
	}
	

    @Test
	public void testAddAfterEnd2() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertFalse(it.hasNext());
		set.remove(a);
		set.add(b);
		assertTrue(it.hasNext());
		assertEquals(it.next(), b);
	}
	
    @Test
	public void testAddAfterEnd3() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		
		Iterator<Object> it = set.iterator();
		assertFalse(it.hasNext());
		set.add(a);
		set.add(b);
		
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		set.remove(b);
		set.add(d);
		assertEquals(it.next(), d);
	}
	
    @Test
	public void testAddAfterEnd4() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		Iterator<Object> it = set.iterator();
		assertFalse(it.hasNext());
		
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		set.remove(b);
		set.remove(c);
		
		assertEquals(it.next(), d);
	}
	
    @Test
	public void testRemoveFirstItAfter() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
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
	
    @Test
	public void testRemoveLastIt() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		set.remove(d);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException	e) {
			// nothing
		}
	}
	
    @Test
	public void testRemoveIterator() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		ResetIterator<Object> it = set.iterator();
		try {
			it.remove();
			fail();
		}
		catch (UnsupportedOperationException e) {
			// nothing
		}
	}
	
    @Test
	public void testRemoveMidIt1() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
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
	
    @Test
	public void testRemoveMidIt2() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
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
	
    @Test
	public void testRemoveMidIt3() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
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
	
    @Test
	public void testRemoveMidIt4() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
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
	
    @Test
	public void testRemoveMidIt5() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		Iterator<Object> it = set.iterator();
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		set.remove(b);
		set.remove(c);
		
		assertEquals(it.next(), d);
	}
	
    @Test
	public void testRemoveInexistantIt() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
		Iterator<Object> it = set.iterator();
		assertNull(set.remove(e));
		assertEquals(it.next(), a);
		assertEquals(it.next(), b);
		assertEquals(it.next(), c);
		assertEquals(it.next(), d);
	}
	
    @Test
	public void testAppendsSame() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(a);
		
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
	
    @Test
	public void testremoveLast() {
		IRandomAccessList<Object> set = new RandomAccessList<Object>();
		set.add(a);
		set.add(b);
		set.add(c);
		set.add(d);
		
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
			// nothing
		}
	}
	
    @Test
	public void testRemoveSameObject() {
		IRandomAccessList<MyObject> set = new RandomAccessList<MyObject>();
		MyObject a1 = new MyObject("a");
		MyObject a2 = new MyObject("a");
		
		set.add(a1);
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
			// nothing
		}
	}
	
    @Test
	public void testRemoveSameObjectWithIterator() {
		IRandomAccessList<MyObject> set = new RandomAccessList<MyObject>();
		MyObject b = new MyObject("b");
		MyObject a1 = new MyObject("a");
		MyObject a2 = new MyObject("a");
		
		set.add(b);
		set.add(a1);
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
			// nothing
		}
	}
	
    @Test
	public void testInvalidateNext() {
		IRandomAccessList<MyObject> set = new RandomAccessList<MyObject>();
		ResetIterator<MyObject> it = set.iterator();
		it.invalidate();
		try {
			it.next();
			fail();
		}
		catch (IllegalStateException e) {
			// nothing
		}
	}
	
    @Test
	public void testInvalidateHasNext() {
		IRandomAccessList<MyObject> set = new RandomAccessList<MyObject>();
		ResetIterator<MyObject> it = set.iterator();
		it.invalidate();
		try {
			it.hasNext();
			fail();
		}
		catch (IllegalStateException e) {
			// nothing
		}
	}
	
    @Test
	public void testInvalidateReset() {
		IRandomAccessList<MyObject> set = new RandomAccessList<MyObject>();
		ResetIterator<MyObject> it = set.iterator();
		it.invalidate();
		try {
			it.reset();
			fail();
		}
		catch (IllegalStateException e) {
			// nothing
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
