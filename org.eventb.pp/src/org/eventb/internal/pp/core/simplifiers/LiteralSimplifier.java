package org.eventb.internal.pp.core.simplifiers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.IArithmetic;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;

public class LiteralSimplifier implements ISimplifier {
	
	private IVariableContext context;
	
	public LiteralSimplifier(IVariableContext context) {
		this.context = context;
	}
	
	public boolean canSimplify(IClause clause) {
		if (!clause.isUnit()) return true;
		return false;
	}

	public IClause simplifyDisjunctiveClause(PPDisjClause clause) {
		List<IPredicate> predicateLiterals = getDisjSimplifiedList(clause.getPredicateLiterals());
		if (predicateLiterals == null) return null;
		List<IEquality> equalityLiterals = getDisjSimplifiedList(clause.getEqualityLiterals());
		if (equalityLiterals == null) return null;
		List<IArithmetic> arithmeticLiterals = getDisjSimplifiedList(clause.getArithmeticLiterals());
		if (arithmeticLiterals == null) return null;
		
		IClause result = new PPDisjClause(clause.getLevel(),predicateLiterals,equalityLiterals,arithmeticLiterals);
		result.setOrigin(clause.getOrigin());
		return result;
	}

	public IClause simplifyEquivalenceClause(PPEqClause clause) {
		List<IEquality> conditions = getDisjSimplifiedList(clause.getConditions());
		
		List<IPredicate> predicateLiterals = getEqSimplifiedList(clause.getPredicateLiterals());
		List<IEquality> equalityLiterals = getEqSimplifiedList(clause.getEqualityLiterals());
		List<IArithmetic> arithmeticLiterals = getEqSimplifiedList(clause.getArithmeticLiterals());
		
		int numberOfFalse = 0;
		if (predicateLiterals == null) {
			numberOfFalse++;
			predicateLiterals = new ArrayList<IPredicate>();
		}
		if (equalityLiterals == null) {
			numberOfFalse++;
			equalityLiterals = new ArrayList<IEquality>();
		}
		if (arithmeticLiterals == null) {
			numberOfFalse++;
			arithmeticLiterals = new ArrayList<IArithmetic>();
		}
		
		if ((numberOfFalse == 0 || numberOfFalse == 2) && predicateLiterals.isEmpty() && equalityLiterals.isEmpty() && arithmeticLiterals.isEmpty()) {
			return null;
		}
		
		if (numberOfFalse == 1) {
			// we inverse one
			if (predicateLiterals.size() > 0) {
				IPredicate literal = predicateLiterals.remove(0);
				predicateLiterals.add(0,literal.getInverse());
			}
			else if (equalityLiterals.size() > 0) {
				IEquality literal = equalityLiterals.remove(0);
				equalityLiterals.add(0,literal.getInverse());
			}
			else if (arithmeticLiterals.size() > 0) {
				IArithmetic literal = arithmeticLiterals.remove(0);
				arithmeticLiterals.add(0,literal.getInverse());
			}
		}
		IClause result = PPEqClause.newClause(clause.getLevel(), predicateLiterals, equalityLiterals, arithmeticLiterals,
					conditions, context);
		result.setOrigin(clause.getOrigin());
		return result;
	}
	
	private <T extends ILiteral> List<T> getDisjSimplifiedList(List<T> literals) {
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
	private <T extends ILiteral<T>> List<T> getEqSimplifiedList(List<T> literals) {
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
