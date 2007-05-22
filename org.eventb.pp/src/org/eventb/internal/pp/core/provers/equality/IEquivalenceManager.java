package org.eventb.internal.pp.core.provers.equality;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;

public interface IEquivalenceManager {
	
	// here is the mapping Constant -> Node
	// mapping Equality -> Source / one source per equality
	
	// nodes must be ordered
	public void removeQueryEquality(IEquality equality, IClause clause);
	
	// returns contradiction + source and solved queries
	// or null if nothing happens
	public IFactResult addFactEquality(IEquality equality, IClause clause);
	
	// returns solved query
	// or null if nothing happens
	public IQueryResult addQueryEquality(IEquality equality, IClause clause);
	
	// backtrack up to/exclusive level
	public void backtrack(Level level);

	
	public List<? extends IInstantiationResult> addInstantiationEquality(IEquality equality, IClause clause);
	
	public void removeInstantiation(IEquality equality, IClause clause);
}