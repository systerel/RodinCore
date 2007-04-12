package org.eventb.internal.pp.core;

import org.eventb.internal.pp.core.datastructure.IObservable;
import org.eventb.internal.pp.core.elements.IClause;

public interface IProver {
	
	public IClause next();
	
	public void initialize(IDispatcher dispatcher, IObservable clauses);
	
//	public boolean accepts(IClause clause);
	
//	public void addOwnClause(IClause clause);
	
	public void contradiction(Level oldLevel, Level newLevel, boolean proofFound);
	
	public void registerDumper(Dumper dumper);
	
//	public void resetDataStructures();
	
}
