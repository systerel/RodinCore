/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of {@link RandomAccessList}
 *
 * @author François Terrier
 *
 * @param <T>
 */
public final class RandomAccessList<T extends Object> implements IRandomAccessList<T> {

	private final HashMap<T,LinkableObject> list = new HashMap<T,LinkableObject>();
	protected final List<NiceIterator> iterators = new ArrayList<NiceIterator>();
	protected LinkableObject first,last;
	
	public RandomAccessList() {
		// nothing
	}
	
	@Override
	public T remove(T object) {
		updateIterators(object);
		return internalRemove(object);
	}

	@Override
	public void add(T object) {
		if (list.containsKey(object)) return;
		
		LinkableObject link = new LinkableObject(object);
		
		if (list.isEmpty()) {
			first = link;
			last = link;
		} else {
			link.previous = last;
			last.next = link;
			last = link;
		}
		list.put(object, link);
	}
	
	@Override
	public boolean contains(T object) {
		return list.containsKey(object);
	}
	
	@Override
	public T get(T object) {
		return list.get(object).object;
	}
	
//	public void clear() {
//		for (NiceIterator iterator : iterators) {
//			iterator.reset();
//		}
//		list.clear();
//		first = null;
//		last = null;
//	}
	
	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public ResetIterator<T> iterator() {
		NiceIterator it = new NiceIterator();
		iterators.add(it);
		return it;
	}
	
	private void updateIterators(T object) {
		for (NiceIterator it : iterators) {
			it.remove(object);
		}
	}
	
	private T internalRemove(T object) {
		LinkableObject r = list.remove(object);
		if (r == null) return null;
		if (r.previous != null) {
			r.previous.next = r.next;
		}
		else { 
			// r is the first node
			first = r.next;
		}
		if (r.next != null) {
			r.next.previous = r.previous;
		}
		else {
			// r is the last node
			last = r.previous;
		}
		return r.object;
	}
	
	private class NiceIterator implements ResetIterator<T> {
		private LinkableObject current;
		private boolean invalid = false;
		
		NiceIterator(){
			// nothing
		}
		
		@Override
		public void invalidate() {
			iterators.remove(this);
			invalid = true;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean hasNext() {
			if (invalid) throw new IllegalStateException("Iterator has been invalidated");
			boolean hasNext = current != last;
			return hasNext;
		}

		@Override
		public T next() {
			if (invalid) throw new IllegalStateException("Iterator has been invalidated");
			if (current == last) throw new NoSuchElementException();
			if (current == null) current = first;
			else current = current.next;
			return current.object;
		}
		
		void remove(T object) {
			if (current != null && current.object.equals(object)) {
				if (current.previous != null) current = current.previous;
				else current = null;
			}
		}

		@Override
		public void reset() {
			if (invalid) throw new IllegalStateException("Iterator has been invalidated");
			current = null;
		}
	}
	
	private class LinkableObject  {
		T object;
		LinkableObject next;
		LinkableObject previous;
		
		LinkableObject(T object) {
			assert object != null;
			this.object = object;
		}
		
		@Override
		public String toString() {
			return object.toString();
		}
	}


	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		LinkableObject tmp = first;
		while (tmp != null) {
			builder.append(tmp.toString()+", ");
			tmp = tmp.next;
		}
		builder.append("]");
		return builder.toString();
	}

}
