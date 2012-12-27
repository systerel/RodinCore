/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.provers.equality.IFactResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;

public final class FactResult implements IFactResult {

	private Set<FactSource> source;
	private List<QueryResult> queries;
	private List<InstantiationResult> instantiations;
	
	public FactResult(Set<FactSource> source) {
		this.source = source;
	}
	
	public FactResult(List<QueryResult> queries, boolean dummy) {
		this.queries = queries;
	}
	
	public FactResult(List<InstantiationResult> instantiations) {
		this.instantiations = instantiations;
	}
	
	public FactResult(List<QueryResult> queries, List<InstantiationResult> instantiations) {
		this.instantiations = instantiations;
		this.queries = queries;
	}
	
	@Override
	public List<Clause> getContradictionOrigin() {
		List<Clause> result = new ArrayList<Clause>();
		for (FactSource s : source) {
			result.add(s.getClause());
		}
		return result;
	}

	@Override
	public List<QueryResult> getSolvedQueries() {
		return queries;
	}

	@Override
	public boolean hasContradiction() {
		return source != null;
	}

	public Set<FactSource> getContradictionSource() {
		return source;
	}

	@Override
	public Level getContradictionLevel() {
		return Source.getLevel(source);
	}

	@Override
	public List<InstantiationResult> getSolvedInstantiations() {
		return instantiations;
	}
	
}
