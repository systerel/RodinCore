package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.provers.equality.IQueryResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

public class QueryResult implements IQueryResult {

	private QuerySource querySource;
	private Set<FactSource> factSource;
	private boolean value;
	
	public QueryResult(QuerySource querySource, Set<FactSource> factSource, boolean value) {
		this.querySource = querySource;
		this.factSource = factSource;
		this.value = value;
	}
	
//	public IEquality getEquality() {
//		return querySource.getEquality();
//	}

	public List<IClause> getSolvedValueOrigin() {
		List<IClause> result = new ArrayList<IClause>();
		for (FactSource source : factSource) {
			result.add(source.getClause());
		}
		return result;
	}

	public List<IClause> getSolvedClauses() {
		return querySource.getClauses();
	}
	
	public boolean getValue() {
		return value;
	}

	public Set<FactSource> getSolvedValueSource() {
		return factSource;
	}
	
	public QuerySource getQuerySource() {
		return querySource;
	}

	public IEquality getEquality() {
		return querySource.getEquality();
	}

}
