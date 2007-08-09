/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;

public abstract class Source {
	
	private final EqualityLiteral equality;
	
	protected Source(){
		// for subclasses
		equality = null;
	}
	
	public Source(EqualityLiteral equality) {
		this.equality = equality;
	}
	
	// common
	public EqualityLiteral getEquality() {
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

		private Clause clause;
		
		protected FactSource() {
			// for subclasses
		}
		
		public FactSource(EqualityLiteral equality) {
			super(equality);
		}

		public void setClause(Clause clause) {
			this.clause = clause;
		}
		
		public Clause getClause() {
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
		
		@Override
		public String toString() {
			return clause.toString();
		}
		
	}	
	
	public static class QuerySource extends Source {
		
		private Map<Clause, Level> clauses = new HashMap<Clause, Level>();
		
		protected QuerySource() {
			// for subclasses
		}
		
		public QuerySource(EqualityLiteral equality) {
			super(equality);
		}

		public Set<Clause> getClauses() {
			return new HashSet<Clause>(clauses.keySet());
		}
		
		@Override
		public boolean isValid() {
			return !clauses.isEmpty();
		}
		
		public void addClause(Clause clause) {
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
		
		public void removeClause(Clause clause) {
			// if the clause exists, it should not be with a different level
			// assumption done by proofstrategy, which removes clauses of a higher
			// level before adding clauses of a lower level
			for (Iterator<Entry<Clause,Level>> iter = clauses.entrySet().iterator(); iter.hasNext();) {
				Entry<Clause,Level> entry = iter.next();
				if (entry.getKey().equalsWithLevel(clause)) {
					iter.remove();
					return;
				}
			}
		}
		
		@Override
		public void backtrack(Level level) {
			for (Iterator<Entry<Clause,Level>> iter = clauses.entrySet().iterator(); iter.hasNext();) {
				Entry<Clause,Level> clause = iter.next();
				if (level.isAncestorOf(clause.getValue())) iter.remove();
			}
		}
		
		@Override
		public String toString() {
			return clauses.keySet().toString();
		}
	}	
}
