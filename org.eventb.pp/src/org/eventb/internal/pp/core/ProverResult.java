/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;

public class ProverResult {

	public static ProverResult EMPTY_RESULT = new ProverResult();
	
	private Set<Clause> generatedClauses;
	private Set<Clause> subsumedClauses;
	
	public ProverResult(Set<Clause> generatedClauses, Set<Clause> subsumedClauses) {
		this.generatedClauses = generatedClauses;
		this.subsumedClauses = subsumedClauses;
	}
	
	public ProverResult(Clause clause, Set<Clause> subsumedClauses) {
		this.generatedClauses = new HashSet<Clause>();
		this.subsumedClauses = subsumedClauses;
		generatedClauses.add(clause);
	}
	
	public ProverResult(Clause clause, Clause subsumedClause) {
		this.generatedClauses = new HashSet<Clause>();
		this.subsumedClauses = new HashSet<Clause>();
		generatedClauses.add(clause);
		subsumedClauses.add(subsumedClause);
	}
	
	public ProverResult(Clause clause) {
		this.generatedClauses = new HashSet<Clause>();
		this.subsumedClauses = new HashSet<Clause>();
		generatedClauses.add(clause);
	}
	
	private ProverResult() {
		this.generatedClauses = new HashSet<Clause>();
		this.subsumedClauses = new HashSet<Clause>();
	}

	public Set<Clause> getGeneratedClauses() {
		return generatedClauses;
	}
	
	public Set<Clause> getSubsumedClauses() {
		return subsumedClauses;
	}
	
	public boolean isEmpty() {
		return generatedClauses.isEmpty() && subsumedClauses.isEmpty();
	}
	
	@Override
	public String toString() {
		if (isEmpty()) return "EMPTY RESULT";
		return "new clauses: "+generatedClauses+", subsumed clauses: "+subsumedClauses;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProverResult) {
			ProverResult tmp = (ProverResult) obj;
			return generatedClauses.equals(tmp.generatedClauses) && subsumedClauses.equals(tmp.subsumedClauses);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return generatedClauses.hashCode() * 37 + subsumedClauses.hashCode();
	}

}

