package org.eventb.internal.pp.core.elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public final class EquivalenceClause extends Clause {
	
	private static final int BASE_HASHCODE = 5;
	
	public EquivalenceClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic) {
		super(origin, predicates, equalities, arithmetic, BASE_HASHCODE);
		
		// not a unit clause. unit clauses are disjunctive clauses
		assert !isUnit();
		assert predicates != null && equalities != null && arithmetic != null;
		assert !isEmpty();
	}

	public EquivalenceClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, List<EqualityLiteral> conditions) {
		super(origin, predicates, equalities, arithmetic, conditions, BASE_HASHCODE);
		
		// not a unit clause. unit clauses are disjunctive clauses
		assert !isUnit();
		assert predicates != null && equalities != null && arithmetic != null && conditions != null;
		assert !isEmpty();
	}
	
	
	@Override
	protected void computeBitSets() {
		for (PredicateLiteral literal : predicates) {
			literal.setBit(positiveLiterals);
			literal.setBit(negativeLiterals);
		}
	}
	
	@Override
	public void infer(IInferrer inferrer) {
		inferrer.inferFromEquivalenceClause(this);
	}
	
	@Override
	public Clause simplify(ISimplifier simplifier) {
		Clause result = simplifier.simplifyEquivalenceClause(this);
		assert result != null;
		return result;
	}
	
	@Override
	public String toString() {
		return "E"+super.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EquivalenceClause) {
			return super.equals(obj);
		}
		return false;
	}
	
	// form a new clause from an equivalence clause
	public static Clause newClause(IOrigin origin, List<PredicateLiteral> predicate, 
			List<EqualityLiteral> equality, List<ArithmeticLiteral> arithmetic, 
			List<EqualityLiteral> conditions, IVariableContext context) {
		assert predicate.size() + equality.size() + arithmetic.size() + conditions.size() > 0;
		
		// we have a disjunctive clause
		if (predicate.size() + equality.size() + arithmetic.size() <= 1) {
			Literal<?,?> literal = null;
			if (predicate.size() == 1) {
				literal = predicate.remove(0);
			}
			else if (equality.size() == 1) {
				literal = equality.remove(0);
			}
			else if (arithmetic.size() == 1) {
				literal = arithmetic.remove(0);
			}
			if (literal != null) {
				Set<LocalVariable> constants = new HashSet<LocalVariable>();
				for (Term term : literal.getTerms()) {
					term.collectLocalVariables(constants);
				}
				if (!constants.isEmpty() && constants.iterator().next().isForall()) {
					Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
					for (LocalVariable variable : constants) {
						map.put(variable, variable.getVariable(context));
					}
					literal = literal.substitute(map);
				}
				if (literal instanceof EqualityLiteral) equality.add((EqualityLiteral)literal);
				if (literal instanceof PredicateLiteral) predicate.add((PredicateLiteral)literal);
				if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
			}
			
			return new DisjunctiveClause(origin, predicate, equality, arithmetic, conditions);
		}
		////////////////////////////////
		return new EquivalenceClause(origin, predicate, equality, arithmetic, conditions);
	}
	
	public static void inverseOneliteral(List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic) {
		// we must inverse one sign
		if (predicates.size() > 0) {
			// we inverse a predicate literal
			PredicateLiteral toInverse = predicates.remove(0);
			predicates.add(0, toInverse.getInverse());
		}
		else if (equalities.size() > 0) {
			// we inverse another literal
			EqualityLiteral toInverse = equalities.remove(0);
			equalities.add(0, toInverse.getInverse());
		}
		else if (arithmetic.size() > 0) {
			ArithmeticLiteral toInverse = arithmetic.remove(0);
			arithmetic.add(0, toInverse.getInverse());
		}
	}
	
//	public boolean contains(PredicateDescriptor predicate) {
//		return hasPredicateOfSign(predicate, false);
//	}

	@Override
	public boolean matches(PredicateDescriptor predicate) {
		return hasPredicateOfSign(predicate, true) || hasPredicateOfSign(predicate, false);
	}
	
	@Override
	public boolean matchesAtPosition(PredicateDescriptor predicate, int position) {
		PredicateDescriptor matched = predicates.get(position).getDescriptor();
		return predicate.getIndex() == matched.getIndex();
	}

	@Override
	public boolean isFalse() {
		return false;
	}

	@Override
	public boolean isTrue() {
		return false;
	}

	@Override
	public boolean isEquivalence() {
		return true;
	}

}
