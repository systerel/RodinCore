package org.eventb.internal.pp.core.provers.seedsearch;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.Constant;

public final class SeedSearchResult {

	private final Constant constant;
	private final int predicatePosition;
	private final Clause clause;
	private final int position;
	
	public SeedSearchResult(Constant constant, int position,
			int predicatePosition, Clause clause) {
		this.constant = constant;
		this.predicatePosition = predicatePosition;
		this.clause = clause;
		this.position = position;
	}
	
	public Constant getConstant() {
		return constant;
	}

	public Clause getInstantiableClause() {
		return clause;
	}

	public int getPredicatePosition() {
		return predicatePosition;
	}

	public int getPosition() {
		return position;
	}

}
