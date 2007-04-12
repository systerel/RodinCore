package org.eventb.internal.pp.loader.clause;

import java.util.HashMap;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class BooleanEqualityTable extends HashMap<TermSignature, Integer> {
	private static final long serialVersionUID = 2139618668777947328L;
	
	private int nextIdentifier;
	
	public BooleanEqualityTable(int nextIdentifier) {
		// do nothing
		this.nextIdentifier = nextIdentifier;
	}
	
	public int getNextLiteralIdentifier() {
		return nextIdentifier++;
	}
	
}
