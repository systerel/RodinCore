package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;

public abstract class Source {
	
	private IEquality equality;
	
	protected Source(){
		// for subclasses
	}
	
	public Source(IEquality equality) {
		this.equality = equality;
	}
	
	// common
	public IEquality getEquality() {
		return equality;
	}
	
	public abstract void backtrack(Level level);
	
	public abstract boolean isValid();
	
	protected static Level getLevel(Set<FactSource> source) {
		Level result = null;
		for (FactSource s : source) {
			if (result == null) result = s.getLevel();
			else if (!s.getLevel().isAncestorOf(result)) result = s.getLevel();
		}
		return result;
	}
	
	public static class FactSource extends Source {

		private IClause clause;
		
		protected FactSource() {
			// for subclasses
		}
		
		public FactSource(IEquality equality) {
			super(equality);
		}

		public void setClause(IClause clause) {
			this.clause = clause;
		}
		
		public IClause getClause() {
			return clause;
		}
		
		public Level getLevel() {
			return clause.getLevel();
		}

		@Override
		public void backtrack(Level level) {
			if (clause != null && level.isAncestorOf(clause.getLevel())) clause = null;
		}

		@Override
		public boolean isValid() {
			return clause != null;
		}
		
	}	
	
	public static class QuerySource extends Source {
		
		private Map<IClause, Level> clauses = new HashMap<IClause, Level>();
		
		protected QuerySource() {
			// for subclasses
		}
		
		public QuerySource(IEquality equality) {
			super(equality);
		}

		public Set<IClause> getClauses() {
			return new HashSet<IClause>(clauses.keySet());
		}
		
		@Override
		public boolean isValid() {
			return !clauses.isEmpty();
		}
		
		public void addClause(IClause clause) {
			if (clauses.containsKey(clause)) {
				Level oldLevel = clauses.get(clause);
				if (clause.getLevel().isAncestorOf(oldLevel)) {
					clauses.put(clause, clause.getLevel());
				}
			}
			else {
				clauses.put(clause, clause.getLevel());
			}
		}
		
		public void removeClause(IClause clause) {
			// if the clause exists, it should not be with a different level
			// assumption done by proofstrategy, which removes clauses of a higher
			// level before adding clauses of a lower level
			for (Iterator<Entry<IClause,Level>> iter = clauses.entrySet().iterator(); iter.hasNext();) {
				Entry<IClause,Level> entry = iter.next();
				if (entry.getKey().equalsWithLevel(clause)) {
					iter.remove();
					return;
				}
			}
		}
		
		@Override
		public void backtrack(Level level) {
			for (Iterator<Entry<IClause,Level>> iter = clauses.entrySet().iterator(); iter.hasNext();) {
				Entry<IClause,Level> clause = iter.next();
				if (level.isAncestorOf(clause.getValue())) iter.remove();
			}
		}
	}	
}
