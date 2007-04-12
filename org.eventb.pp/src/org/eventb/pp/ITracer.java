package org.eventb.pp;

import java.util.List;

import org.eventb.core.ast.Predicate;

public interface ITracer {

//	public abstract List<IClause> getClauses();

	public List<Predicate> getOriginalPredicates();

	public boolean isGoalNeeded();
	
}