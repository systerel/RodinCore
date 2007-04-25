package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.PPPredicate;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;

public class ResolutionInferrer extends AbstractInferrer {

	private IClause unitClause;
	
	private IPredicate predicate;
	private int position;
	
	private IClause result;
	private boolean blocked;
	
	private Level level;
	
	public ResolutionInferrer(IVariableContext context) {
		super(context);
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void setUnitClause(IClause clause) {
		assert clause.isUnit() && clause.getPredicateLiterals().size() == 1;
		
		// we keep the original unit clause
		unitClause = clause;
		// we save a copy of the original predicate
		predicate = clause.getPredicateLiterals().get(0).getCopyWithNewVariables(context, new HashMap<AbstractVariable, AbstractVariable>());
		
		// we do not accept existential unit clause
		assert !predicate.isQuantified();
	}
	
	@Override
	protected void initialize(IClause clause) throws IllegalStateException {
		if (position<0 || unitClause==null || predicate==null) {
			throw new IllegalStateException();
		}
		blocked = clause.getPredicateLiterals().get(position).updateInstantiationCount(predicate);
		level = Level.getHighest(unitClause.getLevel(), clause.getLevel());
	}
	
	@Override
	protected void reset() {
		position = -1;
		unitClause = null;
		predicate = null;
	}
	
	public boolean canInfer(IClause clause) {
		if (position<0 || unitClause==null || predicate==null /* || clause.isBlocked() */ ) {
			throw new IllegalStateException();
		}
		if (PPPredicate.match(predicate,clause.getPredicateLiterals().get(position),clause instanceof PPEqClause)) return true;
		return false;
	}
	
	public InferrenceResult getResult() {
		return new InferrenceResult(result,blocked);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper() {
		IPredicate matchingPredicate = predicates.remove(position);
		conditions.addAll(predicate.getConditions(matchingPredicate));
		
		result = new PPDisjClause(level,predicates,equalities,arithmetic,conditions); 
	}

	@Override
	protected void inferFromEquivalenceClauseHelper() {
		IPredicate matchingPredicate = predicates.remove(position);
		boolean sameSign = matchingPredicate.isPositive() == predicate.isPositive();
		if (!sameSign) PPEqClause.inverseOneliteral(predicates, equalities, arithmetic);
		
		if (matchingPredicate.isQuantified()) matchingPredicate = transformVariables(matchingPredicate);
		conditions.addAll(predicate.getConditions(matchingPredicate));
		
		result = PPEqClause.newClause(level, predicates, equalities, arithmetic, conditions, context);
	}
	
	///////////transforms the variable in the inequality//////////////
	private IPredicate transformVariables(IPredicate matchingPredicate) {
		assert matchingPredicate.isQuantified();
		
		IPredicate result;
		List<LocalVariable> pseudoConstants = new ArrayList<LocalVariable>();
		boolean forall = PPPredicate.isForall(matchingPredicate, pseudoConstants);
		boolean sameSign = matchingPredicate.isPositive() == predicate.isPositive();
		if (sameSign && forall) {
			// replace forall by exist
			Map<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
			for (LocalVariable variable : pseudoConstants) {
				map.put(variable, variable.getInverseVariable());
			}
			result = matchingPredicate.substitute(map);
		}
		else if ((sameSign && !forall) || (!sameSign && forall)) {
			// replace local variable by variable
			Map<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
			for (LocalVariable variable : pseudoConstants) {
				map.put(variable, variable.getVariable(context));
			}
			result = matchingPredicate.substitute(map);
		}
		else /*if (!sameSign && !forall)*/ {
			// do nothing
			result = matchingPredicate;
		}
		return result;
	}

	@Override
	protected void setParents(IClause clause) {
		List<IClause> parents = new ArrayList<IClause>();
		parents.add(clause);
		parents.add(unitClause);
		result.setOrigin(new ClauseOrigin(parents));
	}

}
