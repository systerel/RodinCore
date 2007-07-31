package org.eventb.internal.pp.core;

import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;

public interface IProver {
	
	// can return true/false or a clause or null
	// if it cannot infer a new clause
	// never calls IDispatcher.contradiction
	// those clauses are not simplified
	
	public ProverResult next(boolean force);

//	public boolean isSubsumed(Clause clause);

	public ProverResult addClauseAndDetectContradiction(Clause clause);

	public void removeClause(Clause clause);
	
	public void initialize(ClauseSimplifier simplifier);
	
	public void contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies);
	
	public void registerDumper(Dumper dumper);
	
}
