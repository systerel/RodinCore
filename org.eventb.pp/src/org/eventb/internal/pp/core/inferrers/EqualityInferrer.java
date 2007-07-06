package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.FalseClause;
import org.eventb.internal.pp.core.elements.TrueClause;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class EqualityInferrer extends AbstractInferrer {

	private HashMap<EqualityLiteral, Boolean> equalityMap = new HashMap<EqualityLiteral, Boolean>();
	private List<Clause> parents = new ArrayList<Clause>();
	
	// opitmization for disjunctive clauses only
	private boolean hasTrueEquality = false;
	
	private Clause result;
	
	public EqualityInferrer(IVariableContext context) {
		super(context);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(Clause clause) {
		if (hasTrueEquality) {
			// we have a true equality -> result = TRUE
			result = new TrueClause(getOrigin(clause));
			return;
		}
		for (EqualityLiteral equality : equalityMap.keySet()) {
			// TODO optimize by using a hashset ?
			equalities.remove(equality);
			conditions.remove(equality);
		}
		
		if (isEmpty()) result = new FalseClause(getOrigin(clause));
		else result = new DisjunctiveClause(getOrigin(clause), predicates, equalities, arithmetic, conditions);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(Clause clause) {
		boolean inverse = false;
		for (Entry<EqualityLiteral, Boolean> entry : equalityMap.entrySet()) {
			if (conditions.contains(entry.getKey())) {
				if (entry.getValue()) { 
					// one of the conditions is false -> result = TRUE
					result = new TrueClause(getOrigin(clause));
					return;
				}
				else conditions.remove(entry.getKey());					
			}
			if (equalities.contains(entry.getKey())) {
				if (!entry.getValue()) inverse = !inverse;
				equalities.remove(entry.getKey());
			}
		}
		if (inverse) EquivalenceClause.inverseOneliteral(predicates, equalities, arithmetic);
		
		if (isEmpty()) result = new FalseClause(getOrigin(clause));
		else result = EquivalenceClause.newClause(getOrigin(clause), predicates, equalities, arithmetic, conditions, context);
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
	}

	protected IOrigin getOrigin(Clause clause) {
		List<Clause> clauseParents = new ArrayList<Clause>();
		clauseParents.addAll(parents);
		clauseParents.add(clause);
		return new ClauseOrigin(clauseParents);
	}

}
