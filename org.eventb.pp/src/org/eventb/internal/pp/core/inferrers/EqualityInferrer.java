/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class EqualityInferrer extends AbstractInferrer {

	private HashMap<EqualityLiteral, Boolean> equalityMap = new HashMap<EqualityLiteral, Boolean>();
	private List<Clause> parents = new ArrayList<Clause>();
	
	// opitmization for disjunctive clauses only
	private boolean hasTrueEquality = false;
	private Clause result;
	
	public EqualityInferrer(VariableContext context) {
		super(context);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(Clause clause) {
		assertContainsEqualities(clause);
		
		if (hasTrueEquality) {
			// we have a true equality -> result = TRUE
			result = cf.makeTRUE(getOrigin(clause));
			return;
		}
		for (EqualityLiteral equality : equalityMap.keySet()) {
			// TODO optimize by using a hashset ?
			equalities.remove(equality);
			conditions.remove(equality);
		}
		
		if (isEmptyWithConditions()) result = cf.makeFALSE(getOrigin(clause));
		else result = cf.makeDisjunctiveClause(getOrigin(clause), predicates, equalities, arithmetic, conditions);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(Clause clause) {
		assertContainsEqualities(clause);
		
		boolean inverse = false;
		final IOrigin origin = getOrigin(clause);
		for (Entry<EqualityLiteral, Boolean> entry : equalityMap.entrySet()) {
			if (conditions.contains(entry.getKey())) {
				if (entry.getValue()) { 
					// one of the conditions is false -> result = TRUE
					result = cf.makeTRUE(origin);
					return;
				}
				else conditions.remove(entry.getKey());					
			}
			if (equalities.contains(entry.getKey())) {
				if (!entry.getValue()) inverse = !inverse;
				equalities.remove(entry.getKey());
			}
		}
		if (isEmptyWithConditions()) {
			result = inverse ? cf.makeFALSE(origin) : cf.makeTRUE(origin);
			return;
		}
		if (isEmptyWithoutConditions()) {
			// no literal left, but some conditions
			if (!inverse) {
				result = cf.makeTRUE(origin);
				return;
			}
			// else we will produce an empty disjunctive clause in the end
		} else if (inverse) {
			EquivalenceClause.inverseOneliteral(predicates, equalities,
					arithmetic);
		}
		result = cf.makeClauseFromEquivalenceClause(origin, predicates,
				equalities, arithmetic, conditions, context);
	}

	private void assertContainsEqualities(Clause clause) {
		for (EqualityLiteral equality : equalityMap.keySet()) {
			assert clause.getEqualityLiterals().contains(equality) || clause.getConditions().contains(equality);
		}
	}

	public void addParentClauses(List<Clause> clauses) {
		// these are the unit equality clauses
		parents.addAll(clauses);
	}
	
	public void addEquality(EqualityLiteral equality, boolean value) {
		if (value) hasTrueEquality = true;
		equalityMap.put(equality, value);
	}
	
	public Clause getResult() {
		return result;
	}
	
	@Override
	protected void initialize(Clause clause) throws IllegalStateException {
		if (parents.isEmpty() || equalityMap.isEmpty()) throw new IllegalStateException();
	}

	@Override
	protected void reset() {
		equalityMap.clear();
		parents.clear();
		hasTrueEquality = false;
		super.reset();
	}

	protected IOrigin getOrigin(Clause clause) {
		List<Clause> clauseParents = new ArrayList<Clause>();
		clauseParents.addAll(parents);
		clauseParents.add(clause);
		return new ClauseOrigin(clauseParents);
	}

}
