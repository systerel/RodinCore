package org.eventb.pp.core.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eventb.internal.pp.core.search.ConditionIterator;

import junit.framework.TestCase;

public class TestIterator extends TestCase {

	private class NiceIterator extends ConditionIterator<Object> {

		public NiceIterator(Iterator<Object> iterables) {
			super(iterables);
		}

		@Override
		public boolean isSelected(Object element) {
			return true;
		}
		
	}
	
	private Iterator<Object> createEmptyIterator() {
		List<Object> list = new ArrayList<Object>();
		return new NiceIterator(list.iterator());
	}
	
	private Iterator<Object> createOneIterableIteratorTwoElements() {
		List<Object> objects = new ArrayList<Object>();
		objects.add(new Object());
		objects.add(new Object());
		return new NiceIterator(objects.iterator());
	}
	
//	private Iterator<Object> createTwoIterableIteratorTwoElements() {
//		List<Iterator<Object>> list = new ArrayList<Iterator<Object>>();
//		List<Object> objects1 = new ArrayList<Object>();
//		objects1.add(new Object());
//		List<Object> objects2 = new ArrayList<Object>();
//		objects2.add(new Object());
//		list.add(objects1.iterator());
//		list.add(objects2.iterator());
//		return new NiceIterator(list.iterator());
//	}
	
	public void testEmptyIterator1() {
		Iterator<Object> it = createEmptyIterator();
		assertFalse(it.hasNext());
		try {
			it.next();
		}
		catch (NoSuchElementException e) {
			assertTrue(true);
		}
	}
	
	public void testEmptyIterator2() {
		Iterator<Object> it = createEmptyIterator();
		try {
			it.next();
		}
		catch (NoSuchElementException e) {
			assertTrue(true);
		}
		assertFalse(it.hasNext());
	}
	
	public void testEmptyIteratorSeveral1() {
		Iterator<Object> it = createEmptyIterator();
		assertFalse(it.hasNext());
		assertFalse(it.hasNext());
		try {
			it.next();
		}
		catch (NoSuchElementException e) {
			assertTrue(true);
		}
		try {
			it.next();
		}
		catch (NoSuchElementException e) {
			assertTrue(true);
		}
		assertFalse(it.hasNext());
	}
	
	public void testEmptyIteratorSeveral2() {
		Iterator<Object> it = createEmptyIterator();
		try {
			it.next();
		}
		catch (NoSuchElementException e) {
			assertTrue(true);
		}
		assertFalse(it.hasNext());
		assertFalse(it.hasNext());
		try {
			it.next();
		}
		catch (NoSuchElementException e) {
			assertTrue(true);
		}
		assertFalse(it.hasNext());
	}
	
	public void testHasNext() {
		testHasNext(createOneIterableIteratorTwoElements());
//		testHasNext(createTwoIterableIteratorTwoElements());
	}
	
	private void testHasNext(Iterator<Object> it) {
		assertTrue(it.hasNext());
		assertNotNull(it.next());
		assertTrue(it.hasNext());
		assertNotNull(it.next());
		assertFalse(it.hasNext());
	}
	
	
}
