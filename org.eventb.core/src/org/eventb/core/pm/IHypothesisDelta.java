package org.eventb.core.pm;

import java.util.Collection;

import org.eventb.core.prover.sequent.Hypothesis;

public interface IHypothesisDelta {
	
	public static final int SELECTED = 1;
	public static final int CACHED = 2;
	public static final int SEARCHED = 3;
	
	public static final int ADDED = 1;
	public static final int REMOVED = 2;
	
	public Collection<Hypothesis> getHypotheses(int place, int type);
}
