package org.eventb.internal.pp.core.provers.equality;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;

public interface IFactResult {

	public boolean hasContradiction();
	
	public List<Clause> getContradictionOrigin();
	
	public Level getContradictionLevel();
	
	// returns a list of queries if some queries are solved or null if not
	public List<? extends IQueryResult> getSolvedQueries();
	
	public List<? extends IInstantiationResult> getSolvedInstantiations();
	
}
