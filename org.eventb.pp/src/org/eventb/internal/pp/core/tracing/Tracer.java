package org.eventb.internal.pp.core.tracing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.pp.ITracer;

public class Tracer implements ITracer {

	private List<IClause> clauses = new ArrayList<IClause>();
	
	public void addClosingClause(IClause clause) {
		for (Iterator<IClause> iter = clauses.iterator(); iter.hasNext();) {
			IClause element = iter.next();
			if (clause.getLevel().isAncestorOf(element.getLevel())) 
				iter.remove();
		}
		clauses.add(clause);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.tracing.ITracer#getClauses()
	 */
	public List<IClause> getClauses() {
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
		for (IClause clause : clauses) {
			clause.getOrigin().trace(this);	
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
