package org.eventb.internal.pp.core.simplifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.IArithmetic;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.PPFalseClause;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class OnePointRule implements ISimplifier {

	private List<IPredicate> predicates;
	private List<IEquality> equalities;
	private List<IArithmetic> arithmetic;
	private List<IEquality> conditions;
	
	private void init(IClause clause) {
		predicates = clause.getPredicateLiterals();
		equalities = clause.getEqualityLiterals();
		arithmetic = clause.getArithmeticLiterals();
		conditions = clause.getConditions();
	}
	
	private boolean isEmpty() {
		return conditions.size() + predicates.size() + arithmetic.size() + equalities.size() == 0;
	}
	
	public IClause simplifyDisjunctiveClause(PPDisjClause clause) {
		init(clause);
		onePointLoop(equalities);
		onePointLoop(conditions);
		if (isEmpty()) return new PPFalseClause(clause.getOrigin());
		else return new PPDisjClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
	}

	public IClause simplifyEquivalenceClause(PPEqClause clause) {
		init(clause);
		onePointLoop(conditions);
		
		// never empty, commented out
		// if (isEmpty()) return new PPFalseClause(clause.getOrigin());
		return new PPEqClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
	}
	
	private void onePointLoop(List<IEquality> candidateList) {
		// choose a candidate
		int i = 0;
		while (candidateList.size() > i) {
			IEquality equality = candidateList.get(i);
			if (isOnePointCandidate(equality)) {
				candidateList.remove(equality);
				doOnePoint(equality);
			}
			else {
				i++;
			}
		}
	}
	
	private void doOnePoint(IEquality equality) {
		assert isOnePointCandidate(equality);
		
		Map<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
		Variable variable = getOnePointVariable(equality);
		map.put(variable, getOnePointTerm(equality,variable));
		
		doOnePointHelper(predicates, map);
		doOnePointHelper(equalities, map);
		doOnePointHelper(arithmetic, map);
		doOnePointHelper(conditions, map);
	}
	
	protected <T extends ILiteral<T>> void doOnePointHelper(List<T> list, Map<AbstractVariable, Term> map) {
		ArrayList<T> tmp1 = new ArrayList<T>();
		for (ILiteral<T> literal : list) {
			tmp1.add(literal.substitute(map));
		}
		list.clear();
		list.addAll(tmp1);
	}



	private Term getOnePointTerm(IEquality equality, Variable variable) {
		assert isOnePointCandidate(equality);
		Term term1 = equality.getTerms().get(0);
		Term term2 = equality.getTerms().get(1);
		
		if (term1 == variable) return term2;
		if (term2 == variable) return term1;
		assert false;
		return null;
	}

	private Variable getOnePointVariable(IEquality equality) {
		assert isOnePointCandidate(equality);
		Term term1 = equality.getTerms().get(0);
		Term term2 = equality.getTerms().get(1);
		
		if (term1 instanceof Variable) return (Variable)term1;
		if (term2 instanceof Variable) return (Variable)term2;
		assert false;
		return null;
	}

	private boolean isOnePointCandidate(IEquality equality) {
		if (equality.isPositive()) return false;
		
		Term term1 = equality.getTerms().get(0);
		Term term2 = equality.getTerms().get(1);
		if (term1 instanceof Variable) {
			return !term2.contains((AbstractVariable)term1);
		}
		if (term2 instanceof Variable) {
			return !term1.contains((AbstractVariable)term2);
		}
		return false;
	}

	public boolean canSimplify(IClause clause) {
		return !clause.isFalse();
	}
}
