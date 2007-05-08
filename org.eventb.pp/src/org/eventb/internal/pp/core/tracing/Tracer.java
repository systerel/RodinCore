package org.eventb.internal.pp.core.tracing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.pp.ITracer;

public class Tracer implements ITracer {

	private List<IOrigin> clauses = new ArrayList<IOrigin>();
	
	public void addClosingClause(IOrigin clause) {
		for (Iterator<IOrigin> iter = clauses.iterator(); iter.hasNext();) {
			IOrigin element = iter.next();
			if (clause.getLevel().isAncestorOf(element.getLevel())) 
				iter.remove();
		}
		clauses.add(clause);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.tracing.ITracer#getClauses()
	 */
	public List<IOrigin> getClauses() {
		return clauses;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.tracing.ITracer#getOriginalPredicates()
	 */
	public List<Predicate> getOriginalPredicates() {
		if (originalPredicates==null) calculateOriginalPredicates();
		return new ArrayList<Predicate>(originalPredicates);
	}
	
	private void calculateOriginalPredicates() {
		originalPredicates = new HashSet<Predicate>();
		for (IOrigin clause : clauses) {
			clause.trace(this);	
		}
	}
	
	private HashSet<Predicate> originalPredicates;
	protected void addNeededHypothesis(Predicate predicate) {
		originalPredicates.add(predicate);
	}

	private boolean goalNeeded = false;
	public boolean isGoalNeeded() {
		if (originalPredicates == null) calculateOriginalPredicates();
		return goalNeeded;
	}
	
	protected void setGoalNeeded(boolean goalNeeded) {
		this.goalNeeded = goalNeeded;
	}
}
