package org.eventb.internal.pp.core.simplifiers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.FalseClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.TrueClause;

/**
 * This simplifier removes all duplicate predicate, arithmetic or equality literals in a clause.
 * If the clause is disjunctive, conditions that are duplicate of existing inequalities are removed.
 *
 * @author François Terrier
 *
 */
public class LiteralSimplifier implements ISimplifier {
	
	private IVariableContext context;
	
	public LiteralSimplifier(IVariableContext context) {
		this.context = context;
	}
	
	public boolean canSimplify(Clause clause) {
		if (!clause.isUnit()) return true;
		return false;
	}

	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause) {
		List<PredicateLiteral> predicateLiterals = getDisjSimplifiedList(clause.getPredicateLiterals());
		if (predicateLiterals == null) return new TrueClause(clause.getOrigin());
		List<EqualityLiteral> equalityLiterals = getDisjSimplifiedList(clause.getEqualityLiterals());
		if (equalityLiterals == null) return new TrueClause(clause.getOrigin());
		List<ArithmeticLiteral> arithmeticLiterals = getDisjSimplifiedList(clause.getArithmeticLiterals());
		if (arithmeticLiterals == null) return new TrueClause(clause.getOrigin());
		List<EqualityLiteral> conditions = getDisjSimplifiedList(clause.getConditions());
		
		// we look for conditions that are in the equalities
		conditions = getDisjSimplifiedConditions(equalityLiterals, conditions);
		if (conditions == null) return new TrueClause(clause.getOrigin());
		
		if (predicateLiterals.size() + equalityLiterals.size() + arithmeticLiterals.size() + conditions.size() == 0) return new FalseClause(clause.getOrigin());
		else return new DisjunctiveClause(clause.getOrigin(),predicateLiterals,equalityLiterals,arithmeticLiterals,conditions);
	}

	public Clause simplifyEquivalenceClause(EquivalenceClause clause) {
		List<EqualityLiteral> conditions = getDisjSimplifiedList(clause.getConditions());
		
		List<PredicateLiteral> predicateLiterals = getEqSimplifiedList(clause.getPredicateLiterals());
		List<EqualityLiteral> equalityLiterals = getEqSimplifiedList(clause.getEqualityLiterals());
		List<ArithmeticLiteral> arithmeticLiterals = getEqSimplifiedList(clause.getArithmeticLiterals());
		
		int numberOfFalse = 0;
		if (predicateLiterals == null) {
			numberOfFalse++;
			predicateLiterals = new ArrayList<PredicateLiteral>();
		}
		if (equalityLiterals == null) {
			numberOfFalse++;
			equalityLiterals = new ArrayList<EqualityLiteral>();
		}
		if (arithmeticLiterals == null) {
			numberOfFalse++;
			arithmeticLiterals = new ArrayList<ArithmeticLiteral>();
		}
		
		if ((numberOfFalse == 0 || numberOfFalse == 2) && predicateLiterals.isEmpty() && equalityLiterals.isEmpty() && arithmeticLiterals.isEmpty()) {
			return new TrueClause(clause.getOrigin());
		}
		
		if (numberOfFalse == 1) {
			// we inverse one
			if (predicateLiterals.size() > 0) {
				PredicateLiteral literal = predicateLiterals.remove(0);
				predicateLiterals.add(0,literal.getInverse());
			}
			else if (equalityLiterals.size() > 0) {
				EqualityLiteral literal = equalityLiterals.remove(0);
				equalityLiterals.add(0,literal.getInverse());
			}
			else if (arithmeticLiterals.size() > 0) {
				ArithmeticLiteral literal = arithmeticLiterals.remove(0);
				arithmeticLiterals.add(0,literal.getInverse());
			}
		}
		
		if (predicateLiterals.size() + equalityLiterals.size() + arithmeticLiterals.size() + conditions.size() == 0) return new FalseClause(clause.getOrigin());
		return EquivalenceClause.newClause(clause.getOrigin(), predicateLiterals, equalityLiterals, arithmeticLiterals,
					conditions, context);
	}
	
	private List<EqualityLiteral> getDisjSimplifiedConditions(List<EqualityLiteral> equalities, List<EqualityLiteral> conditions) {
		List<EqualityLiteral> result = new ArrayList<EqualityLiteral>();
		condloop: for (EqualityLiteral condition : conditions) {
			for (EqualityLiteral equality : equalities) {
				if (condition.getInverse().equals(equality)) {
					return null;
				}
				if (condition.equals(equality)) continue condloop;
			}
			result.add(condition);
		}
		return result;
	}
	
	private <T extends Literal<?,?>> List<T> getDisjSimplifiedList(List<T> literals) {
		LinkedHashSet<T> set = new LinkedHashSet<T>();
		for (int i = 0; i < literals.size(); i++) {
			T literal1 = literals.get(i);
			for (int j = i+1; j < literals.size(); j++) {
				T literal2 = literals.get(j);
				if (literal1.getInverse().equals(literal2)) {
					return null;
				}
			}
			set.add(literal1);
		}
		List<T> result = new ArrayList<T>();
		result.addAll(set);
		return result;
	}
	
	// a return value of null, means there was a contradiction
	private <T extends Literal<T,?>> List<T> getEqSimplifiedList(List<T> literals) {
		for (int i = 0; i < literals.size(); i++) {
			T literal1 = literals.get(i);
			for (int j = i+1; j < literals.size(); j++) {
				T literal2 = literals.get(j);
				if (literal1.getInverse().equals(literal2)) {
					// take the inverse of a literal
					List<T> list = new ArrayList<T>();
					list.addAll(literals);
					list.remove(literal1);
					list.remove(literal2);
					if (list.size() == 0) return null;
					else {
						T toInverse = list.remove(0);
						list.add(0, toInverse.getInverse());
						return getEqSimplifiedList(list);
					}
				}
				else if (literal1.equals(literal2)) {
					List<T> list = new ArrayList<T>();
					list.addAll(literals);
					list.remove(literal1);
					list.remove(literal2);
					return getEqSimplifiedList(list);
				}
			}
		}
		return literals;
	}
	
}
