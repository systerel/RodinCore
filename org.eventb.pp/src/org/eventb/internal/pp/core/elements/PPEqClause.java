package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public final class PPEqClause extends AbstractPPClause {
	
	public PPEqClause(IOrigin origin, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		super(origin, predicates, equalities, arithmetic);
		
		// not a unit clause. unit clauses are disjunctive clauses
		assert !isUnit();
		assert predicates != null && equalities != null && arithmetic != null;
		assert !isEmpty();
	}

	public PPEqClause(IOrigin origin, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic, List<IEquality> conditions) {
		super(origin, predicates, equalities, arithmetic, conditions);
		
		// not a unit clause. unit clauses are disjunctive clauses
		assert !isUnit();
		assert predicates != null && equalities != null && arithmetic != null && conditions != null;
		assert !isEmpty();
	}
	
	
	@Override
	protected void computeBitSets() {
		for (IPredicate literal : predicates) {
			literal.setBit(positiveLiterals);
			literal.setBit(negativeLiterals);
		}
	}
	
	public void infer(IInferrer inferrer) {
		inferrer.inferFromEquivalenceClause(this);
	}
	
	public IClause simplify(ISimplifier simplifier) {
		IClause result = simplifier.simplifyEquivalenceClause(this);
		assert result != null;
		return result;
	}
	
	@Override
	public String toString() {
		return "E"+super.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PPEqClause) {
			return super.equals(obj);
		}
		return false;
	}
	
	// form a new clause from an equivalence clause
	public static IClause newClause(IOrigin origin, List<IPredicate> predicate, 
			List<IEquality> equality, List<IArithmetic> arithmetic, 
			List<IEquality> conditions, IVariableContext context) {
		assert predicate.size() + equality.size() + arithmetic.size() + conditions.size() > 0;
		
		// we have a disjunctive clause
		if (predicate.size() + equality.size() + arithmetic.size() <= 1) {
			ILiteral<?> literal = null;
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
				List<LocalVariable> constants = new ArrayList<LocalVariable>();
				for (Term term : literal.getTerms()) {
					term.collectLocalVariables(constants);
				}
				if (!constants.isEmpty() && constants.get(0).isForall()) {
					Map<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
					for (LocalVariable variable : constants) {
						map.put(variable, variable.getVariable(context));
					}
					literal = literal.substitute(map);
				}
				if (literal instanceof IEquality) equality.add((IEquality)literal);
				if (literal instanceof IPredicate) predicate.add((IPredicate)literal);
				if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
			}
			
			return new PPDisjClause(origin, predicate, equality, arithmetic, conditions);
		}
		////////////////////////////////
		return new PPEqClause(origin, predicate, equality, arithmetic, conditions);
	}
	
	public static void inverseOneliteral(List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		// we must inverse one sign
		if (predicates.size() > 0) {
			// we inverse a predicate literal
			IPredicate toInverse = predicates.remove(0);
			predicates.add(0, toInverse.getInverse());
		}
		else if (equalities.size() > 0) {
			// we inverse another literal
			IEquality toInverse = equalities.remove(0);
			equalities.add(0, toInverse.getInverse());
		}
		else if (arithmetic.size() > 0) {
			IArithmetic toInverse = arithmetic.remove(0);
			arithmetic.add(0, toInverse.getInverse());
		}
	}

	public boolean isFalse() {
		return false;
	}

	public boolean isTrue() {
		return false;
	}

}
