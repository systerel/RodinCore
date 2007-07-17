package org.eventb.internal.pp.loader.formula;


public class TermVisitorContext {
	// TODO remove this class
	
	// set before starting visiting
	public boolean isEquivalence;
	
	TermVisitorContext(boolean isEquivalence) {
		this.isEquivalence = isEquivalence;
	}
	
	// set before and changed during visiting
	public boolean isPositive = true;
	
	// set during visit, info about quantification
	public boolean isQuantified = false;
	public boolean isForall = true;
	public int startOffset;
	public int endOffset;
}
