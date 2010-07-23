/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.provers.equality.IQueryResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

public class QueryResult implements IQueryResult {

	private final QuerySource querySource;
	private final Set<FactSource> factSource;
	private final boolean value;
	
	public QueryResult(QuerySource querySource, Set<FactSource> factSource, boolean value) {
		this.querySource = querySource;
		this.factSource = factSource;
		this.value = value;
	}
	
	@Override
	public List<Clause> getSolvedValueOrigin() {
		List<Clause> result = new ArrayList<Clause>();
		for (FactSource source : factSource) {
			result.add(source.getClause());
		}
		return result;
	}

	@Override
	public Set<Clause> getSolvedClauses() {
		return querySource.getClauses();
	}
	
	@Override
	public boolean getValue() {
		return value;
	}

	public Set<FactSource> getSolvedValueSource() {
		return factSource;
	}
	
	public QuerySource getQuerySource() {
		return querySource;
	}

	@Override
	public EqualityLiteral getEquality() {
		return querySource.getEquality();
	}

}
