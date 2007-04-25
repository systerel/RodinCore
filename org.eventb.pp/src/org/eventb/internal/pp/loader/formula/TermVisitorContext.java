package org.eventb.internal.pp.loader.formula;


public class TermVisitorContext {

//	public boolean freshContext = true;
	
	public TermVisitorContext(boolean equivalence) {
		this.isEquivalence = equivalence;
	}
	
	// set before starting visiting
	public final boolean isEquivalence;
	
	// set before and changed during visiting
	public boolean isPositive = true;
	
	// set during visit, info about quantification
	public boolean isQuantified = false;
	public boolean isForall = true;
	public int startOffset;
	public int endOffset;
	
}
