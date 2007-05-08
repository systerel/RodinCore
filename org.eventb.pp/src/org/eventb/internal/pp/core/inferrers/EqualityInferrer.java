package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.PPFalseClause;
import org.eventb.internal.pp.core.elements.PPTrueClause;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class EqualityInferrer extends AbstractInferrer {

	private HashMap<IEquality, Boolean> equalityMap = new HashMap<IEquality, Boolean>();
	private List<IClause> parents = new ArrayList<IClause>();
	
	// opitmization for disjunctive clauses only
	private boolean hasTrueEquality = false;
	
	private IClause result;
	
	public EqualityInferrer(IVariableContext context) {
		super(context);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(IClause clause) {
		if (hasTrueEquality) {
			// we have a true equality -> result = TRUE
			result = new PPTrueClause(getOrigin(clause));
			return;
		}
		for (IEquality equality : equalityMap.keySet()) {
			// TODO optimize by using a hashset ?
			equalities.remove(equality);
			conditions.remove(equality);
		}
		
		if (isEmpty()) result = new PPFalseClause(getOrigin(clause));
		else result = new PPDisjClause(getOrigin(clause), predicates, equalities, arithmetic, conditions);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(IClause clause) {
		boolean inverse = false;
		for (Entry<IEquality, Boolean> entry : equalityMap.entrySet()) {
			if (conditions.contains(entry.getKey())) {
				if (entry.getValue()) { 
					// one of the conditions is false -> result = TRUE
					result = new PPTrueClause(getOrigin(clause));
					return;
				}
				else conditions.remove(entry.getKey());					
			}
			if (equalities.contains(entry.getKey())) {
				if (!entry.getValue()) inverse = !inverse;
				equalities.remove(entry.getKey());
			}
		}
		if (inverse) PPEqClause.inverseOneliteral(predicates, equalities, arithmetic);
		
		if (isEmpty()) result = new PPFalseClause(getOrigin(clause));
		else result = PPEqClause.newClause(getOrigin(clause), predicates, equalities, arithmetic, conditions, context);
	}

	public void addParentClauses(List<IClause> clauses) {
		// these are the unit equality clauses
		parents.addAll(clauses);
	}
	
	public void addEquality(IEquality equality, boolean value) {
		if (value) hasTrueEquality = true;
		equalityMap.put(equality, value);
	}
	
	public IClause getResult() {
		return result;
	}
	
	@Override
	protected void initialize(IClause clause) throws IllegalStateException {
		if (parents.isEmpty() || equalityMap.isEmpty()) throw new IllegalStateException();
	}

	@Override
	protected void reset() {
		equalityMap.clear();
		parents.clear();
		hasTrueEquality = false;
	}

	protected IOrigin getOrigin(IClause clause) {
		List<IClause> clauseParents = new ArrayList<IClause>();
		clauseParents.addAll(parents);
		clauseParents.add(clause);
		return new ClauseOrigin(clauseParents);
	}

	public boolean canInfer(IClause clause) {
		return true;
	}

}
