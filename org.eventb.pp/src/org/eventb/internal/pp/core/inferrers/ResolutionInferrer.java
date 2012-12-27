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
package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class ResolutionInferrer extends AbstractInferrer {

	private Clause unitClause;
	
	private PredicateLiteral predicate;
	private int position;
	
	private Clause result;
	private Clause subsumedClause;
	private boolean hasInstantiations;
	
	public ResolutionInferrer(VariableContext context) {
		super(context);
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void setUnitClause(Clause clause) {
		assert clause.isUnit() && clause.getPredicateLiteralsSize() == 1;
		
		// we keep the original unit clause
		unitClause = clause;
		// we save a copy of the original predicate
		predicate = clause.getPredicateLiteral(0).getCopyWithNewVariables(context, new HashMap<SimpleTerm, SimpleTerm>());
	}
	
	@Override
	protected void initialize(Clause clause) throws IllegalStateException {
		assert predicates.size() > 0;
		
		if (position<0 || unitClause==null || predicate==null 
				|| !clause.matchesAtPosition(predicate.getDescriptor(), predicate.isPositive(), position)) {
			throw new IllegalStateException();
		}
		result = null;
		subsumedClause = null;
		hasInstantiations = false;
	}
	
	@Override
	protected void reset() {
		position = -1;
		unitClause = null;
		predicate = null;
		super.reset();
	}
	
	public Clause getDerivedClause() {
		return result;
	}
	
	public Clause getSubsumedClause() {
		return subsumedClause;
	}
	
	private boolean isSubsumed(Clause clause) {
		assert result != null;

		if (hasInstantiations) return false;
		if (clause.isEquivalence()) return false;
		if (clause.getLevel().isAncestorOf(result.getLevel())) return false;
//		if (clause.getPredicateLiterals().size() == 0) return false;
		return true;
	}
	
	private void preparePredicate(PredicateLiteral matchingPredicate) {
		for (int i = 0; i < matchingPredicate.getTermsSize(); i++) {
			SimpleTerm matchingTerm = matchingPredicate.getTerm(i);
			SimpleTerm matcherTerm = predicate.getTerm(i);
			
			if (!matcherTerm.isConstant()) {
				// for now only variables
				HashMap<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
				map.put(matcherTerm, matchingTerm);
				predicate = predicate.substitute(map);
			}
		}
	}
	
	// MUST BE called on a unit-clause
	private List<EqualityLiteral> getConditions(PredicateLiteral matchingPredicate) {
		// We first do a copy of the predicate, in which we replace all
		// local variables by a fresh existential variable and all
		// variables by a fresh variable. This ensures invariant of variables
		// and local variables which states that there must not be equal variables
		// in 2 different clauses and no equal local variables in 2 different
		// literals.
		List<EqualityLiteral> result = new ArrayList<EqualityLiteral>();
		for (int i = 0; i < matchingPredicate.getTermsSize(); i++) {
			SimpleTerm term1 = matchingPredicate.getTerm(i);
			SimpleTerm term2 = predicate.getTerm(i);
			// we set the instantiation flag for subsumption checking
			if (!term1.equals(term2)) hasInstantiations = true;
			result.add(new EqualityLiteral(term1,term2,false));
		}
		return result;
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(Clause clause) {
		PredicateLiteral matchingPredicate = predicates.remove(position);
		
		// TODO check
//		updateInstantiationCount(matchingPredicate);
		preparePredicate(matchingPredicate);
		
		conditions.addAll(getConditions(matchingPredicate));
		
		if (isEmptyWithConditions()) result = cf.makeFALSE(getOrigin(clause));
		else result = cf.makeDisjunctiveClause(getOrigin(clause),predicates,equalities,arithmetic,conditions);
		
		if (isSubsumed(clause)) subsumedClause = clause;
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(Clause clause) {
		PredicateLiteral matchingPredicate = predicates.remove(position);
		boolean sameSign = sameSign(matchingPredicate, predicate);
		if (!sameSign) EquivalenceClause.inverseOneliteral(predicates, equalities, arithmetic);
		
		if (matchingPredicate.isQuantified()) matchingPredicate = transformVariables(matchingPredicate);
		
		// TODO check
		preparePredicate(matchingPredicate);
		
		conditions.addAll(getConditions(matchingPredicate));
		
		if (isEmptyWithConditions()) result = cf.makeFALSE(getOrigin(clause));
		else result = cf.makeClauseFromEquivalenceClause(getOrigin(clause), predicates, equalities, arithmetic, conditions, context);
		
		if (isSubsumed(clause)) subsumedClause = clause;
	}
	
	///////////transforms the variable in the inequality//////////////
	private PredicateLiteral transformVariables(PredicateLiteral matchingPredicate) {
		assert matchingPredicate.isQuantified();
		
		PredicateLiteral result;
		Set<LocalVariable> pseudoConstants = new HashSet<LocalVariable>();
		matchingPredicate.collectLocalVariables(pseudoConstants);
		if (pseudoConstants.isEmpty()) result = matchingPredicate;
		else {
			boolean forall = pseudoConstants.iterator().next().isForall();
			boolean sameSign = sameSign(matchingPredicate, predicate);
			if (sameSign && forall) {
				// replace forall by exist
				Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
				for (LocalVariable variable : pseudoConstants) {
					map.put(variable, variable.getInverseVariable());
				}
				result = matchingPredicate.substitute(map);
			}
			else if ((sameSign && !forall) || (!sameSign && forall)) {
				// replace local variable by variable
				Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
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
	
	private boolean sameSign(PredicateLiteral literal1, PredicateLiteral literal2) {
		return literal1.isPositive() == literal2.isPositive();
	}

	protected IOrigin getOrigin(Clause clause) {
		List<Clause> parents = new ArrayList<Clause>();
		parents.add(clause);
		parents.add(unitClause);
		return new ClauseOrigin(parents);
	}

}
