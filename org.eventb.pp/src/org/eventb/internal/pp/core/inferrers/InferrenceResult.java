package org.eventb.internal.pp.core.inferrers;

import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;

public class InferrenceResult {

	public static final InferrenceResult BLOCKED_RESULT = new InferrenceResult(true);
	
	private Clause clause;
	private boolean isBlocked;
	private Set<Clause> subsumedClauses = new HashSet<Clause>();
	
	public InferrenceResult(Clause clause /*, boolean isBlocked */) {
		this.clause = clause;
		this.isBlocked = false;
//		this.isBlocked = isBlocked;
	}
	
	private InferrenceResult(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
	
	public Clause getClause() {
		return clause;
	}
	
	public boolean isBlocked() {
		return isBlocked;
	}
	
//	public boolean isBlockedOnInferrence() {
//		return isBlocked;
//	}

	public Set<Clause> getSubsumedClauses() {
		return subsumedClauses;
	}	
	
	public void addSubsumedClause(Clause clause) {
		subsumedClauses.add(clause);
	}
	
}
