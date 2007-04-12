package org.eventb.internal.pp.core.provers.equality;

import java.util.Set;

import org.eventb.internal.pp.core.Level;

public interface IEquivalenceManager {

	public boolean contradictsEquality(INode c1, INode c2);

	public boolean contradictsInequality(INode c1, INode c2);

//	public void addEquality(INode c1, INode c2, IOrigin origin);
//
//	public void addInequality(INode c1, INode c2, IOrigin origin);

	public void backtrack(Level level);
	
//	public IOrigin getOrigin(INode c1, INode c2);
	
	
	public Set<String> dump();
}