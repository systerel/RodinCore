package org.eventb.internal.pp.core.inferrers;

import org.eventb.internal.pp.core.elements.IClause;

public class InferrenceResult {

	private IClause clause;
	private boolean isBlocked;
	
	public InferrenceResult(IClause clause, boolean isBlocked) {
		this.clause = clause;
		this.isBlocked = isBlocked;
	}
	
	public IClause getClause() {
		return clause;
	}
	
	public boolean isBlocked() {
		return isBlocked;
	}
	
}
