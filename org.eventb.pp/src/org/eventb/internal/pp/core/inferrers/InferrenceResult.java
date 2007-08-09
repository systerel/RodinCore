/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.inferrers;

import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;

public class InferrenceResult {

	private Clause clause;
	private Set<Clause> subsumedClauses = new HashSet<Clause>();
	
	public InferrenceResult(Clause clause /*, boolean isBlocked */) {
		this.clause = clause;
	}
	
	public Clause getClause() {
		return clause;
	}
	
	public Set<Clause> getSubsumedClauses() {
		return subsumedClauses;
	}	
	
	public void addSubsumedClause(Clause clause) {
		subsumedClauses.add(clause);
	}
	
}
