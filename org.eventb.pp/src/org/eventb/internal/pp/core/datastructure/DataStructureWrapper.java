package org.eventb.internal.pp.core.datastructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.search.ConditionIterator;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;

public class DataStructureWrapper implements IObservable, Iterable<Clause> {
	// TODO delete this class
	
	private IterableHashSet<Clause> clauses;
	private ResetIterator<Clause> clausesIterator;
	
	public DataStructureWrapper(IterableHashSet<Clause> clauses) {
		this.clauses = clauses;
		this.clausesIterator = clauses.iterator();
	}
	
	public ResetIterator<Clause> iterator() {
		return clauses.iterator();
	}
	
	public boolean contains(Clause clause) {
		return clauses.contains(clause);
	}
	
	public Clause get(Clause clause) {
		return clauses.get(clause);
	}
	
	public void backtrack(Level level) {
//		debug("PredicateProver: Backtracking datastructures");
		clausesIterator.reset();
		BacktrackIterator it = new BacktrackIterator(clausesIterator, level);
		
		while (it.hasNext()) {
			Clause toRemove = it.next();
		
			remove(toRemove);
		}
	}
	
	private List<IChangeListener> listeners = new ArrayList<IChangeListener>();
	
	public void addChangeListener(IChangeListener listener) {
		listeners.add(listener);
	}
	
	private void fireNew(Clause clause) {
		for (IChangeListener listener : listeners) {
			listener.newClause(clause);
		}
	}
	private void fireRemove(Clause clause) {
		for (IChangeListener listener : listeners) {
			listener.removeClause(clause);
		}
	}
	
	public void add(Clause clause) {
		clauses.appends(clause);
		fireNew(clause);
	}

	public void remove(Clause clause) {
		clauses.remove(clause);
		fireRemove(clause);
	}
	
	private class BacktrackIterator extends ConditionIterator<Clause> {
		private Level level;
		public BacktrackIterator(Iterator<Clause> clauses,
				Level level) {
			super(clauses);
			
			this.level = level;
		}
		@Override
		public boolean isSelected(Clause element) {
			return level.isAncestorOf(element.getLevel());
		}
	}

}
