package org.eventb.internal.pp.core.datastructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.search.ConditionIterator;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;

public class DataStructureWrapper implements IObservable, Iterable<IClause> {

	private IterableHashSet<IClause> clauses;
	private ResetIterator<IClause> clausesIterator;
	
	public DataStructureWrapper(IterableHashSet<IClause> clauses) {
		this.clauses = clauses;
		this.clausesIterator = clauses.iterator();
	}
	
	public ResetIterator<IClause> iterator() {
		return clauses.iterator();
	}
	
	public boolean contains(IClause clause) {
		return clauses.contains(clause);
	}
	
	public IClause get(IClause clause) {
		return clauses.get(clause);
	}
	
	public void backtrack(Level level) {
//		debug("PredicateProver: Backtracking datastructures");
		clausesIterator.reset();
		BacktrackIterator it = new BacktrackIterator(clausesIterator, level);
		
		while (it.hasNext()) {
			IClause toRemove = it.next();
		
			remove(toRemove);
		}
	}
	
	private List<IChangeListener> listeners = new ArrayList<IChangeListener>();
	
	public void addChangeListener(IChangeListener listener) {
		listeners.add(listener);
	}
	
	private void fireNew(IClause clause) {
		for (IChangeListener listener : listeners) {
			listener.newClause(clause);
		}
	}
	private void fireRemove(IClause clause) {
		for (IChangeListener listener : listeners) {
			listener.removeClause(clause);
		}
	}
	
	public void add(IClause clause) {
		clauses.appends(clause);
		fireNew(clause);
	}

	public void remove(IClause clause) {
		clauses.remove(clause);
		fireRemove(clause);
	}
	
	private class BacktrackIterator extends ConditionIterator<IClause> {
		private Level level;
		public BacktrackIterator(Iterator<IClause> clauses,
				Level level) {
			super(clauses);
			
			this.level = level;
		}
		@Override
		public boolean isSelected(IClause element) {
			return level.isAncestorOf(element.getLevel());
		}
	}

}
