/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

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

public final class InstantiationResult implements IInstantiationResult {

	private final QuerySource source;
	private final Node proposedValue;
	private final Set<FactSource> solvedSource;
	
	public InstantiationResult(Node proposedValue, QuerySource source, Set<FactSource> solvedSource) {
		this.source = source;
		this.proposedValue = proposedValue;
		this.solvedSource = solvedSource;
	}

	public Level getLevel() {
		return Source.getLevel(solvedSource);
	}

	@Override
	public EqualityLiteral getEquality() {
		return source.getEquality();
	}

	@Override
	public Constant getInstantiationValue() {
		return proposedValue.getConstant();
	}

	@Override
	public Set<Clause> getSolvedClauses() {
		return source.getClauses();
	}

	@Override
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


