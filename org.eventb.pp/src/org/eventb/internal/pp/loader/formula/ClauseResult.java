package org.eventb.internal.pp.loader.formula;

import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;

public class ClauseResult {

	private List<List<Literal<?, ?>>> literalLists;
	private boolean isEquivalence;
	
	ClauseResult(boolean isEquivalence, List<List<Literal<?, ?>>> literalLists) {
		this.literalLists = literalLists;
		this.isEquivalence = isEquivalence;
	}
	
	public List<List<Literal<?, ?>>> getLiteralLists() {
		return literalLists;
	}

	public boolean isEquivalence() {
		return isEquivalence;
	}
}
