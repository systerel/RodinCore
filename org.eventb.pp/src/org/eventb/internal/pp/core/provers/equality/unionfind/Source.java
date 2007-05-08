package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
		
		private List<IClause> clauses = new ArrayList<IClause>();
		
		protected QuerySource() {
			// for subclasses
		}
		
		public QuerySource(IEquality equality) {
			super(equality);
		}

		public List<IClause> getClauses() {
			return clauses;
		}
		
		@Override
		public boolean isValid() {
			return !clauses.isEmpty();
		}
		
		public void addClause(IClause clause) {
			this.clauses.add(clause);
		}
		
		public IClause removeClause(IClause clause) {
			// if the clause exists, it should not be with a different level
			// assumption done by proofstrategy, which removes clauses of a higher
			// level before adding clauses of a lower level
			for (Iterator<IClause> iter = clauses.iterator(); iter.hasNext();) {
				IClause oldClause = iter.next();
				if (oldClause.equalsWithLevel(clause)) {
					iter.remove();
					return oldClause;
				}
//				assert false;
			}
			return null;
		}
		
		@Override
		public void backtrack(Level level) {
			for (Iterator<IClause> iter = clauses.iterator(); iter.hasNext();) {
				IClause clause = iter.next();
				if (level.isAncestorOf(clause.getLevel())) iter.remove();
			}
		}
	}	
}
