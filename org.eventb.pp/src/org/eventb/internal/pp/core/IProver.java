package org.eventb.internal.pp.core;

import java.util.Set;
import java.util.Stack;

import org.eventb.internal.pp.core.datastructure.IObservable;
import org.eventb.internal.pp.core.elements.IClause;

public interface IProver {
	
	// can return true/false or a clause or null
	// if it cannot infer a new clause
	// never calls IDispatcher.contradiction
	// those clauses are not simplified
	public IClause next();

	// the set does not contain any true nor false clauses
	public Set<IClause> getGeneratedClauses(); 
	
	public void initialize(IDispatcher dispatcher, IObservable clauses, ClauseSimplifier simplifier);
	
	public void contradiction(Level oldLevel, Level newLevel, boolean proofFound, Stack<Level> dependencies);
	
	public void registerDumper(Dumper dumper);
	
}
