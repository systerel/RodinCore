package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.PPEquality;
import org.eventb.internal.pp.core.elements.PPFalseClause;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class ResolutionInferrer extends AbstractInferrer {

	private IClause unitClause;
	
	private IPredicate predicate;
	private int position;
	
	private IClause result;
	private boolean blocked;
	
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
		
//		// we do not accept existential unit clause
//		assert !predicate.isQuantified();
	}
	
	@Override
	protected void initialize(IClause clause) throws IllegalStateException {
		assert clause.getPredicateLiterals().size() > 0;
		
		if (position<0 || unitClause==null || predicate==null || !canInfer(clause)) {
			throw new IllegalStateException();
		}
		blocked = clause.getPredicateLiterals().get(position).updateInstantiationCount(predicate);
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
		IPredicate matcher = predicate;
		IPredicate matched = clause.getPredicateLiterals().get(position);
		// 1 test same index
		if (matcher.getIndex() != matched.getIndex()) return false;
		// 2 test matching signs
		if (matcher.isPositive() == matched.isPositive() && !clause.isEquivalence()) return false;
		// 3 test compatible terms
		// we reject constant term in unit clause matching pseudo constant term in non-unit clause
		for (int i=0;i<matcher.getTerms().size();i++) {
			Term matcherTerm = matcher.getTerms().get(i);
			Term matchedTerm = matched.getTerms().get(i);
			if ((matcherTerm.isQuantified() || matcherTerm.isConstant()) && (matchedTerm.isQuantified() || matchedTerm.isConstant())) {
				// we do not match on a locally quantified variable
				if (matcherTerm.isQuantified()) return false;
				if (matchedTerm.isQuantified() && !clause.isEquivalence()) return false;
				if (matchedTerm.isQuantified() && clause.isEquivalence()) {
					boolean forall = matched.isForall();
					boolean sameSign = matcher.isPositive() == matched.isPositive();
					if (forall == sameSign) return false;
				}
			}
		}
		return true;
	}
	
	public InferrenceResult getResult() {
		return new InferrenceResult(result,blocked);
	}
	
	private void preparePredicate(IPredicate matchingPredicate) {
		for (int i = 0; i < matchingPredicate.getTerms().size(); i++) {
			Term matchingTerm = matchingPredicate.getTerms().get(i);
			if (matchingTerm.isQuantified()) {
				Term matcherTerm = predicate.getTerms().get(i);
				// for now only variables
				if (matcherTerm instanceof Variable) {
					HashMap<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
					map.put((Variable)matcherTerm, matchingTerm);
					predicate = predicate.substitute(map);
				}
			}
		}
	}
	
	// MUST BE called on a unit-clause
	private List<IEquality> getConditions(IPredicate matchingPredicate) {
		// We first do a copy of the predicate, in which we replace all
		// local variables by a fresh existential variable and all
		// variables by a fresh variable. This ensures invariant of variables
		// and local variables which states that there must not be equal variables
		// in 2 different clauses and no equal local variables in 2 different
		// literals.
		List<IEquality> result = new ArrayList<IEquality>();
		for (int i = 0; i < matchingPredicate.getTerms().size(); i++) {
			Term term1 = matchingPredicate.getTerms().get(i);
			Term term2 = predicate.getTerms().get(i);
			result.add(new PPEquality(term1,term2,false));
		}
		return result;
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(IClause clause) {
		IPredicate matchingPredicate = predicates.remove(position);
		
		// TODO check
		preparePredicate(matchingPredicate);
		
		conditions.addAll(getConditions(matchingPredicate));
		
		if (isEmpty()) result = new PPFalseClause(getOrigin(clause));
		else result = new PPDisjClause(getOrigin(clause),predicates,equalities,arithmetic,conditions); 
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(IClause clause) {
		IPredicate matchingPredicate = predicates.remove(position);
		boolean sameSign = matchingPredicate.isPositive() == predicate.isPositive();
		if (!sameSign) PPEqClause.inverseOneliteral(predicates, equalities, arithmetic);
		
		if (matchingPredicate.isQuantified()) matchingPredicate = transformVariables(matchingPredicate);
		
		// TODO check
		preparePredicate(matchingPredicate);
		
		conditions.addAll(getConditions(matchingPredicate));
		
		if (isEmpty()) result = new PPFalseClause(getOrigin(clause));
		else result = PPEqClause.newClause(getOrigin(clause), predicates, equalities, arithmetic, conditions, context);
	}
	
	///////////transforms the variable in the inequality//////////////
	private IPredicate transformVariables(IPredicate matchingPredicate) {
		assert matchingPredicate.isQuantified();
		
		IPredicate result;
		List<LocalVariable> pseudoConstants = new ArrayList<LocalVariable>();
		for (Term term : matchingPredicate.getTerms()) {
			term.collectLocalVariables(pseudoConstants);
		}
		if (pseudoConstants.isEmpty()) result = matchingPredicate;
		else {
			boolean forall = pseudoConstants.get(0).isForall();
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
		}
		return result;
	}

	protected IOrigin getOrigin(IClause clause) {
		List<IClause> parents = new ArrayList<IClause>();
		parents.add(clause);
		parents.add(unitClause);
		return new ClauseOrigin(parents);
	}

}
