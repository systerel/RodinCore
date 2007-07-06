package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.provers.equality.IInstantiationResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

public class InstantiationResult implements IInstantiationResult {

	private QuerySource source;
	private Node proposedValue;
	private Set<FactSource> solvedSource;
	
	public InstantiationResult(Node proposedValue, QuerySource source, Set<FactSource> solvedSource) {
		this.source = source;
		this.proposedValue = proposedValue;
		this.solvedSource = solvedSource;
	}

	public Level getLevel() {
		return Source.getLevel(solvedSource);
	}

	public EqualityLiteral getEquality() {
		return source.getEquality();
	}

	public Constant getInstantiationValue() {
		return proposedValue.getConstant();
	}

	public Set<Clause> getSolvedClauses() {
		return source.getClauses();
	}

	public Set<Clause> getSolvedValueOrigin() {
		Set<Clause> result = new HashSet<Clause>();
		for (FactSource source : solvedSource) {
			result.add(source.getClause());
		}
		return result;
	}
	
	
	public Node getProposedValue() {
		return proposedValue;
	}
	
	public QuerySource getSolvedSource() {
		return source;
	}
	
	public Set<FactSource> getSolvedValueSource() {
		return solvedSource;
	}

}


