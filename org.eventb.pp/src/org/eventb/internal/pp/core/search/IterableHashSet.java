package org.eventb.internal.pp.core.search;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This set allows multiple iterator to be defined on it and to be modified without throwing a
 * {@link ConcurrentModificationException}.
 *
 * @author François Terrier
 *
 * @param <T>
 */
public class IterableHashSet<T extends Object> implements IRandomAccessIterable<T> {

	private HashMap<T,LinkableClause> list = new HashMap<T,LinkableClause>();
	LinkableClause first,last;
	List<NiceIterator> iterators = new ArrayList<NiceIterator>();
	
//	private boolean initialized = false;
	
//	public IterableHashSet(List<T> list) {
//		for (T t : list) {
//			appends(t);
//		}
//	}
	
	public IterableHashSet() {
		// nothing
	}
	
	public T remove(T clause) {
		updateIterators(clause);
		return internalRemove(clause);
	}

	public void appends(T clause) {
		if (list.containsKey(clause)) return;
		
		LinkableClause link = new LinkableClause(clause);
		
		if (!list.isEmpty()) {
			link.previous = last;
			last.next = link;
			last = link;
		}
		else {
			first = link;
			last = link;
		}
		list.put(clause, link);
	}
	
	public boolean contains(T clause) {
		return list.containsKey(clause);
	}
	
	public T get(T clause) {
		return list.get(clause).clause;
	}
	
//	public void resetIterator() {
//		current = null;
//		canRemove = false;
//		initialized = true;
//	}
	
//	public void remove() {
//		if (!canRemove) throw new IllegalStateException();
//		canRemove = false;
//		internalRemove(current.clause);
//		if (current == first) current = null;
//	}
//	
//	
//	public boolean hasNext() {
//		if (!initialized) throw new IllegalStateException();
//		return current != last;
//	}
//
//	public T next() {
//		if (!initialized) throw new IllegalStateException();
//		if (current == last) throw new NoSuchElementException();
//		canRemove = true;
//		if (current == null) current = first;
//		else current = current.next;
//		return current.clause;
//	}
	
	public ResetIterator<T> iterator() {
		NiceIterator it = new NiceIterator();
		iterators.add(it);
		return it;
	}
	
	private void updateIterators(T clause) {
		for (NiceIterator it : iterators) {
			it.remove(clause);
		}
	}
	
	private T internalRemove(T clause) {
		LinkableClause r = list.remove(clause);
		if (r == null) return null;
		if (r.previous != null)
			r.previous.next = r.next;
		else 
			// r is the first node
			first = r.next;
		if (r.next != null)
			r.next.previous = r.previous;
		else
			// r is the last node
			last = r.previous;
		
		return r.clause;
	}
	
	private class NiceIterator implements ResetIterator<T> {
		private LinkableClause current;
		NiceIterator(){
			// nothing
		}
		
		public void delete() {
			iterators.remove(this);
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public boolean hasNext() {
			boolean hasNext = current != last;
			return hasNext;
		}

		public T next() {
			if (current == last) throw new NoSuchElementException();
			if (current == null) current = first;
			else current = current.next;
			return current.clause;
		}
		
		void remove(T clause) {
			if (current != null && current.clause.equals(clause)) {
				if (current.previous != null) current = current.previous;
				else current = null;
			}
		}

		public void reset() {
			current = null;
		}
		
	}
	
	private class LinkableClause  {
		T clause;
		LinkableClause next;
		LinkableClause previous;
		
		LinkableClause(T clause) {
			assert clause != null;
			
			this.clause = clause;
		}
		
		@Override
		public String toString() {
			return clause.toString();
		}
	}


	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		LinkableClause tmp = first;
		while (tmp != null) {
			builder.append(tmp.toString()+", ");
			tmp = tmp.next;
		}
		builder.append("]");
		return builder.toString();
	}


}
