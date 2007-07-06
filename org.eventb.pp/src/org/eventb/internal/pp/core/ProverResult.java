package org.eventb.internal.pp.core;

import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class ProverResult {

	private Set<Clause> generatedClauses;
	private Set<Clause> subsumedClauses;
	private IOrigin origin = null;
	
	public ProverResult(IOrigin origin) {
		this.origin = origin;
		this.subsumedClauses = new HashSet<Clause>();
	}
	
	public ProverResult(IOrigin origin, Set<Clause> subsumedClauses) {
		this.origin = origin;
		this.subsumedClauses = subsumedClauses;
	}
	
	public ProverResult(Set<Clause> generatedClauses, Set<Clause> subsumedClauses) {
		this.generatedClauses = generatedClauses;
		this.subsumedClauses = subsumedClauses;
	}
	
	public ProverResult(Clause clause, Set<Clause> subsumedClauses) {
		this.generatedClauses = new HashSet<Clause>();
		this.subsumedClauses = subsumedClauses;
		generatedClauses.add(clause);
	}
	
	public ProverResult(Clause clause) {
		this.generatedClauses = new HashSet<Clause>();
		this.subsumedClauses = new HashSet<Clause>();
		generatedClauses.add(clause);
	}
	
	public Set<Clause> getGeneratedClauses() {
		return generatedClauses;
	}
	
	public Set<Clause> getSubsumedClauses() {
		return subsumedClauses;
	}
	
	public boolean isContradiction() {
		return origin != null;
	}
	
	public IOrigin getContradictionOrigin() {
		return origin;
	}
	
	@Override
	public String toString() {
		if (isContradiction()) return "contradiction on: "+origin;
		return "new clauses: "+generatedClauses+", subsumed clauses: "+subsumedClauses;
	}
}
